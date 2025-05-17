package moe.best.kimoneri.roulette.actions

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


/** A union of possible outcomes that a user can roll. */
sealed interface Action {

    /** The effect of an Action. */
    enum class Outcome {
        /** The associated [Action] should be applied to a [Target]. */
        AFFECTED,

        /** The associated [Action] should not be applied to the [Target]. */
        UNAFFECTED
    }

    /**
     * [Action] representing a temporary timeout using Discord's native timeout.
     *
     * A value between [lowerBound, upperBound] will be selected.
     * These can be set to equal to force a specific duration.
     *
     * @property generator A [Generator] impl to generate Timeout values.
     * @property responder A [Responder] object to use when this Timeout is rolled.
     * @property name A name identifier for this [Timeout].
     */
    class Timeout(
        val generator: Generator,
        val responder: Responder,
        private val name: String?,
    ) : Action {

        /** Callback returning a [Duration] when this [Timeout] is rolled. */
        fun interface Generator {
            /**
             * Generate a [Duration] that the bot will use as the duration of the [Timeout].
             *
             * @property target The [Target] of the roll.
             * @property message The message that activated this roll.
             */
            fun get(
                target: Target,
                message: Message
            ): Duration
        }

        /** Callback defining how the bot should respond when this [Timeout] is rolled. */
        fun interface Responder {
            /**
             * Generates a string that the bot will use to respond on a roll.
             *
             * @property target Discord [Member] that this [Timeout] will apply to.
             * @property outcome The [Outcome] of this [Timeout] roll.
             * @property initiator Discord [Member] that initiated the roll.
             * @property duration The duration of the mute.
             */
            fun get(
                target: Member,
                outcome: Outcome,
                initiator: Member,
                duration: Duration,
            ): String
        }

        override fun toString() = name.toString()

        companion object {

            /**
             * Create a [Timeout] with a duration randomly rolled between certain bounds.
             *
             * @property lowerBound An inclusive lower bound this timeout may have a duration of.
             * @property upperBound An inclusive upper bound this timeout may have a duration of.
             * @property responder A [Responder] object to use when this Timeout is rolled.
             * @property name A name identifier for this [Timeout].
             */
            fun createFromBounds(
                lowerBound: Duration,
                upperBound: Duration,
                responder: Responder,
                name: String? = "Action:Timeout(lowerBound=$lowerBound,upperBound=$upperBound)"
            ): Timeout {
                val generator = Generator { _, _ ->
                    when {
                        lowerBound < upperBound -> {
                            Random.nextInt(
                                from = lowerBound.inWholeMinutes.toInt(),
                                until = upperBound.inWholeMinutes.toInt() + 1 // Until is exclusive.
                            ).minutes
                        }
                        // Random.nextInt() can't be used on the same value.
                        upperBound == lowerBound -> lowerBound.inWholeMinutes.minutes
                        // Invalid configuration: The lower bound must be less than the upper bound.
                        else -> lowerBound
                    }
                }
                return Timeout(generator, responder, name)
            }
        }
    }

    // TODO: Implement functionality related to this [Action].
    data class TemporaryRole(
        val lowerBound: Duration,
        val upperBound: Duration,
        val roleId: Long,
    ) : Action

}