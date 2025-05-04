package moe.best.kimoneri.roulette.actions

import net.dv8tion.jda.api.entities.Member
import kotlin.time.Duration


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
     * @property lowerBound An inclusive lower bound this timeout may have a duration of.
     * @property upperBound An inclusive upper bound this timeout may have a duration of.
     * @property responder A [Responder] object to use when this Timeout is rolled.
     */
    class Timeout(
        val lowerBound: Duration,
        val upperBound: Duration,
        val responder: Responder
    ) : Action {

        /** Callback defining how the bot should respond when this [Timeout] is rolled.*/
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

        override fun toString(): String = "Action:Timeout(lower=$lowerBound,upper=$upperBound)"
    }

    // TODO: Implement functionality related to this [Action].
    data class TemporaryRole(
        val lowerBound: Duration,
        val upperBound: Duration,
        val roleId: Long,
    ) : Action

}