package moe.best.kimoneri.roulette

import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.best.kimoneri.ext.getPermissionsGroup
import moe.best.kimoneri.ext.isModerator
import moe.best.kimoneri.roulette.actions.Action
import moe.best.kimoneri.roulette.config.GuildConfiguration
import moe.best.kimoneri.roulette.config.RollConfiguration
import moe.best.kimoneri.roulette.config.guilds.GuildConfigurationProvider
import moe.best.kimoneri.roulette.roll.Debouncer
import moe.best.kimoneri.roulette.permissions.PermissionsGroup
import moe.best.kimoneri.roulette.roll.executors.TimeoutExecutor
import moe.best.kimoneri.roulette.actions.Target
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class RouletteListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)

        // Ignore messages sent by bots (including self).
        if (event.author.isBot) return
        // Don't process direct messages.
        if (!event.isFromGuild) return

        // Fetch config for the guild, or end immediately.
        val guildConfig = GuildConfigurationProvider.getConfigForGuild(event.guild.idLong) ?: return
        val memberPermissions = event.member?.getPermissionsGroup(guildConfig) ?: PermissionsGroup.USER

        // Messages must match at least one Activator to trigger a roll.
        if (guildConfig.activators.none { it.shouldActivate(event.message, memberPermissions) }) return

        // Messages from regular users are subject to be being debounced (preventing spam rolls).
        if (Debouncer.shouldDebounce(event.message.author.idLong)
            && (memberPermissions != PermissionsGroup.MODERATOR)
        ) {
            logger.info("Debouncing latest message from {}.", event.author)
            return
        }

        logger.debug(
            "Starting roulette sequence with id ${event.messageId} from ${event.author.effectiveName} (${event.author.id}): (${event.message.contentRaw})"
        )

        val targets = determineTargets(event.message, guildConfig)
        for (target in targets) {
            runBlocking {
                launch {
                    // For suspense, there should be a "delay" before the bot responses.
                    // During this time, the "<bot_name> is typing..." message should appear.
                    event.message.channel.sendTyping().queue()
                    delay(guildConfig.responseDelay?.random()?.toLong()?.seconds ?: 0.seconds)

                    when (val action = getRandomAction(guildConfig)) {
                        is Action.Timeout -> {
                            logger.info("Rolled $action for ${target.member.user}")
                            TimeoutExecutor.apply(timeout = action, message = event.message, target = target)
                        }

                        else -> event.message.reply("Unimplemented functionality.").submit().await()
                    }
                }
            }
        }
    }

    private companion object {

        private val logger = LoggerFactory.getLogger(RouletteListener::class.java)

        private fun getRandomAction(guildConfiguration: GuildConfiguration): Action? {
            val rolls = guildConfiguration.rolls.shuffled() // Extra pseudo-randomness.
            if (rolls.isEmpty()) return null

            val cumWeights = rolls.sumOf { it.weight }.toInt()
            val rolledWeight = Random.nextInt(from = 0, until = cumWeights + 1)
            logger.debug("Rolled weight ($rolledWeight) from cumulative weight of $cumWeights.")

            val iterator = rolls.iterator().withIndex()
            var curr: IndexedValue<RollConfiguration>
            var weight = 0

            do {
                val next = iterator.next()
                weight += next.value.weight.toInt()
                curr = next
            } while (weight < rolledWeight)

            logger.debug("Selected action #${curr.index} of ${rolls.size} total actions.")
            return curr.value.action
        }

        private fun determineTargets(message: Message, guildConfiguration: GuildConfiguration): List<Target> {
            // Message author must be present.
            val messageAuthor = checkNotNull(message.member)
            val mentions = message.mentions.members

            // Only moderators can target someone not themselves.
            if (mentions.isNotEmpty() && messageAuthor.isModerator(guildConfiguration)) {
                val targets = mentions
                    .distinctBy { it.user.id }
                    .map { member -> Target(member, member.getPermissionsGroup(guildConfiguration), messageAuthor) }
                logger.debug(
                    "Roulette sequence {} is from a moderator targeting the following: {}",
                    message.id,
                    targets.map { it.member.user })
                return targets
            }

            // Default to message author.
            logger.debug("Roulette sequence {} is targeting message author {}.", message.id, message.author)
            return listOf(
                Target(
                    member = messageAuthor,
                    permissions = messageAuthor.getPermissionsGroup(guildConfiguration),
                    initiator = messageAuthor
                )
            )
        }
    }
}