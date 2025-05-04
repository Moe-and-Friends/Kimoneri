package moe.best.kimoneri.roulette.config

import moe.best.kimoneri.roulette.permissions.PermissionsGroup
import net.dv8tion.jda.api.entities.Message


/** Configuration for a specific Discord Guild. */
interface GuildConfiguration {

    fun interface Activator {
        /**
         * Determines whether a message should start a Roulette event.
         *
         * @param message The Discord message to check against.
         * @param permissionsGroup The level of the user sending the message.
         * @return True to start a Roulette roll, False if not.
         */
        fun shouldActivate(message: Message, permissionsGroup: PermissionsGroup): Boolean
    }

    /** Snowflake of the Guild. */
    val id: Long

    /** Snowflakes of Roles that grant Moderator permissions. */
    val moderatorRoles: Collection<Long>
        get() = listOf()

    /** Snowflakes of Roles that grant protection from negative rolls. */
    val protectedRoles: Collection<Long>
        get() = listOf()

    /**
     * Additional bespoke configurations that determine whether a message should start a Roulette.
     *
     * This is a good location to include common rate-limiting checks, for example:
     * - A list of channels where the Roulette is active.
     * - Special rules to give Moderators broader activation rights.
     *
     * If any [Activator] returns true, a roulette event will start.
     */
    val activators: List<Activator>

    /**
     * Optional delay (in seconds) before responding to messages.
     *
     * Discord will show "<Bot> is typing..." during this time.
     */
    val responseDelay: UIntRange?

    /** Possible rolls that a user can get. */
    val rolls: List<RollConfiguration>
}




