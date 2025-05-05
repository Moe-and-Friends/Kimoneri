package moe.best.kimoneri.roulette.roll.executors

import kotlinx.coroutines.future.await
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
        val duration = when {
            timeout.lowerBound < timeout.upperBound -> {
                Random.nextInt(
                    from = timeout.lowerBound.inWholeMinutes.toInt(),
                    until = timeout.upperBound.inWholeMinutes.toInt()
                ).minutes
            }
            // Random.nextInt() can't be used on the same value.
            timeout.upperBound == timeout.lowerBound -> timeout.lowerBound.inWholeMinutes.minutes
            // Invalid configuration: The lower bound must be less than the upper bound.
            else -> {
                logger.warn(
                    "Timeout configuration's upperbound {} is greater than its lower bound {}.",
                    timeout.lowerBound,
                    timeout.upperBound
                )
                timeout.lowerBound
            }
        }

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
                target.member.timeoutFor(duration.toJavaDuration()).submit().await()
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
