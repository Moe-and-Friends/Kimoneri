package moe.best.kimoneri.roulette.roll.executors

import kotlinx.coroutines.future.await
import moe.best.kimoneri.ext.toHumanString
import moe.best.kimoneri.roulette.actions.Action
import moe.best.kimoneri.roulette.actions.Target
import moe.best.kimoneri.roulette.permissions.PermissionsGroup
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration


object TimeoutExecutor {

    suspend fun apply(
        target: Target,
        timeout: Action.Timeout,
        message: Message,
    ): Boolean {
        val duration = timeout.generator.get(target, message)

        // Timeouts should only be applied if the target is a user.
        val outcome = when {
            target.permissions == PermissionsGroup.USER -> Action.Outcome.AFFECTED
            else -> Action.Outcome.UNAFFECTED
        }

        logger.debug(
            "Rolled timeout duration of {} minutes for {} with outcome {}.",
            duration,
            target.member.user,
            outcome
        )

        if (outcome == Action.Outcome.AFFECTED) {
            try {
                val reason = "[Roulette] Rolled timeout of duration ${duration.toHumanString(4)}"
                target.member.timeoutFor(duration.toJavaDuration()).reason(reason).submit().await()
            } catch (e: InsufficientPermissionException) {
                logger.error(e.toString())
                message.reply("Error: Insufficient permissions to timeout, please check your config!").submit().await()
                return false
            } catch (e: IllegalArgumentException) {
                logger.error(e.toString())
                message.reply("Error: Invalid Timeout configuration, please check your config").submit().await()
                return false
            }
        }

        val responseMessage =
            timeout.responder.get(target.member, outcome, checkNotNull(message.member), duration)
                .takeUnless { it.isEmpty() }
                ?: "Timed ${target.member.nickname ?: "user"} out for $duration."

        message.reply(responseMessage).submit().await()

        return true
    }

    private val logger = LoggerFactory.getLogger(TimeoutExecutor::class.java)
}
