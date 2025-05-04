package moe.best.kimoneri.roulette.config.guilds

import moe.best.kimoneri.roulette.actions.Action
import moe.best.kimoneri.roulette.config.GuildConfiguration
import moe.best.kimoneri.roulette.config.GuildConfiguration.Activator
import moe.best.kimoneri.roulette.config.RollConfiguration
import moe.best.kimoneri.roulette.permissions.PermissionsGroup
import net.dv8tion.jda.api.entities.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/** Configuration for the Moe and Friends server. */
internal val MOE_AND_FRIENDS_GUILD_CONFIG = object : GuildConfiguration {

    private val channels = setOf(
        1200075107373170698, // # gamerwhen
        1078358866770792489, // # mad-science
    )

    override val id = 546420743710310433

    override val moderatorRoles = setOf(
        644057450743595010, // Sheriff
        986229557659045899 // Deputy
    )

    override val activators = listOf(
        // Activator for debug / test use.
        Activator { message: Message, permissions: PermissionsGroup ->
            val channelMatch = message.channel.idLong == 1078358866770792489 // channel=#mad-science
            val contentMatch = Regex("(<:nut:806018835919536128>)+").containsMatchIn(message.contentRaw)
            when {
                channelMatch && contentMatch -> true // All users can trigger the roll on specified channels.
                contentMatch && permissions == PermissionsGroup.MODERATOR -> true // Moderators can trigger anywhere (with content match).
                else -> false
            }
        }
    )

    override val responseDelay = UIntRange(1u, 5u)


    private val TIMEOUT_AFFECTED_STRINGS = setOf(
        { targetName: String, duration: Duration ->
            "What's the most you ever lost on a coin toss? $targetName wagered $duration."
        },
        { targetName: String, duration: Duration ->
            "Reverend Waluigi has deemed $targetName a sinner. Their penance: A vow of silence for $duration."
        }
    )

    private val DEFAULT_TIMEOUT_RESPONDER = Action.Timeout.Responder { target, outcome, _, duration ->
        when {
            outcome == Action.Outcome.AFFECTED ->
                TIMEOUT_AFFECTED_STRINGS.random().invoke(target.effectiveName, duration)

            else -> "${target.effectiveName} is protected from timeouts."
        }
    }

    override val rolls = listOf(
        RollConfiguration(
            weight = 5u,
            action = Action.Timeout(
                lowerBound = 1.minutes,
                upperBound = 2.minutes,
                responder = DEFAULT_TIMEOUT_RESPONDER
            )
        ),
    )
}
