package moe.best.kimoneri.roulette.config.guilds

import moe.best.kimoneri.ext.toHumanString
import moe.best.kimoneri.roulette.actions.Action
import moe.best.kimoneri.roulette.config.GuildConfiguration
import moe.best.kimoneri.roulette.config.GuildConfiguration.Activator
import moe.best.kimoneri.roulette.config.RollConfiguration
import moe.best.kimoneri.roulette.permissions.PermissionsGroup
import net.dv8tion.jda.api.entities.Message
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/** Configuration for the Moe and Friends server. */
internal val MOE_AND_FRIENDS_GUILD_CONFIG = object : GuildConfiguration {

    override val id = 546420743710310433

    override val moderatorRoles = setOf(
        644057450743595010, // Sheriff
        986229557659045899 // Deputy
    )

    override val protectedRoles = setOf(
        546422996810727434, // Best Friends
        1061611797133668423, // Ice Guy
        1157317681159098448, // Rakka
        889103199624118322, // Bakuhatsu-tai
        977591171079606322, // Server Mascot (Toshi)
    )

    override val activators = listOf(
        // #gamerwhen
        Activator { message: Message, permissions: PermissionsGroup ->
            val channelMatch = message.channel.idLong == 1200075107373170698 // channel=#gamerwhen
            val contentMatch = Regex("(<:gamerwhen:651367432967159808>)+").containsMatchIn(message.contentRaw)
            when {
                channelMatch && contentMatch -> true // All users can trigger the roll in #gamerwhen.
                contentMatch && permissions == PermissionsGroup.MODERATOR -> true // Moderators can trigger anywhere (with content match).
                else -> false
            }
        },
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

    // Strings for a timeout that should be applied onto a user by themselves.
    private val TIMEOUT_SELF_AFFECTED_STRINGS = setOf(
        { target: String, duration: String -> "Hitdad sees and Hitdad strikes $target with a flying briefcase. They will come to their senses in $duration." },
        { target: String, duration: String -> "What's the most you ever lost on a coin toss? $target wagered $duration." },
        { target: String, duration: String -> "Reverend Waluigi has deemed $target a sinner. Their penance: A vow of silence for $duration." },
        { target: String, duration: String -> "General Waluigi has sent $target's squad on a secret mission to spread the Word of Wah. They will be radio silent for $duration." },
        { target: String, duration: String -> "Eyeless dogs are prowling around $target's house. They'll go away after $duration." },
        { target: String, duration: String -> "Hitdad topdecks Exodia, the Forbidden One, sending $target to the Shadow Realm for $duration." },
        { target: String, duration: String -> "Hitdad does his best Luigi impression, shooting $target in the back and ending their reign of tyranny. They will respawn in $duration." },
        { target: String, duration: String -> "$target ate a burger and got food poisoning. They will recover in $duration." },
        { target: String, duration: String -> "Truck-kun blew through the intersection, yeeting $target into the metaverse. They will be able to return in $duration." },
        { target: String, duration: String -> "Hitdad throws $target into the nearest TV. They will be rescued in $duration." },
        { target: String, duration: String -> "The moon falls on $target, crushing them and the land around. Time will rewind in $duration." },
        { target: String, duration: String -> "Hitdad tiger drops $target, negating all damage and leaving them unconscious. They will wake up in $duration." },
    )

    // Strings for a timeout that should be applied onto a user by a moderator.
    private val TIMEOUT_OTHER_AFFECTED_STRINGS = setOf(
        { target: String, duration: String -> "Hitdad goes hunting for the Toshi ping. $target is hit in the crossfire and will return in $duration." },
        { target: String, duration: String -> "Hitdad puts his Sheriff hat on and snipes $target in the head. They will return in $duration." },
        { target: String, duration: String -> "The M&F Sheriff's Department has issued a warrant for $target's arrest. They will be released in $duration." },
        { target: String, duration: String -> "The moderation team has decided to mod-abuse $target for $duration." },
        { target: String, duration: String -> "The ghost of Chae whacks $target with the banhammer. Because she's a ghost, it only mutes them for $duration." },
    )

    // Strings for a timeout that should be not applied onto a user.
    private val TIMEOUT_UNAFFECTED_STRINGS = setOf { target: String, duration: String ->
        "The love of Moe's life, $target is not deserving of a $duration mute."
    }

    private val TIMEOUT_RESPONDER = Action.Timeout.Responder { target, outcome, initiator, duration ->
        when {
            // User self roll.
            outcome == Action.Outcome.AFFECTED && target == initiator ->
                TIMEOUT_SELF_AFFECTED_STRINGS.random().invoke(target.effectiveName, duration.toHumanString())

            // Moderator roll by mod/Anivanilla.
            outcome == Action.Outcome.AFFECTED && initiator.idLong == 229966380407980034 ->
                "Ani readies her gun and fires indiscriminately, hitting ${target.effectiveName} and knocking them out for ${duration.toHumanString()}."

            // Rolled by a moderator.
            outcome == Action.Outcome.AFFECTED && target != initiator ->
                TIMEOUT_OTHER_AFFECTED_STRINGS.random().invoke(target.effectiveName, duration.toHumanString())

            // Target is protected against rolls.
            else -> TIMEOUT_UNAFFECTED_STRINGS.random().invoke(target.effectiveName, duration.toHumanString())
        }
    }

    override val rolls = listOf(
        RollConfiguration(
            5u,
            Action.Timeout.createFromBounds(lowerBound = 1.minutes, upperBound = 1.minutes, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            15u,
            Action.Timeout.createFromBounds(lowerBound = 2.minutes, upperBound = 4.minutes, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            30u,
            Action.Timeout.createFromBounds(lowerBound = 5.minutes, upperBound = 10.minutes, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            50u,
            Action.Timeout.createFromBounds(lowerBound = 10.minutes, upperBound = 30.minutes, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            100u,
            Action.Timeout.createFromBounds(lowerBound = 30.minutes, upperBound = 1.hours, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            150u,
            Action.Timeout.createFromBounds(lowerBound = 1.hours, upperBound = 3.hours, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            150u,
            Action.Timeout.createFromBounds(lowerBound = 3.hours, upperBound = 6.hours, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            150u,
            Action.Timeout.createFromBounds(lowerBound = 6.hours, upperBound = 8.hours, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            100u,
            Action.Timeout.createFromBounds(lowerBound = 8.hours, upperBound = 12.hours, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            100u,
            Action.Timeout.createFromBounds(lowerBound = 12.hours, upperBound = 1.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            40u,
            Action.Timeout.createFromBounds(lowerBound = 1.days, upperBound = 2.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            40u,
            Action.Timeout.createFromBounds(lowerBound = 2.days, upperBound = 4.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            30u,
            Action.Timeout.createFromBounds(lowerBound = 4.days, upperBound = 7.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            20u,
            Action.Timeout.createFromBounds(lowerBound = 7.days, upperBound = 14.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            10u,
            Action.Timeout.createFromBounds(lowerBound = 14.days, upperBound = 21.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            6u,
            Action.Timeout.createFromBounds(lowerBound = 21.days, upperBound = 25.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            3u,
            Action.Timeout.createFromBounds(lowerBound = 25.days, upperBound = 27.days, TIMEOUT_RESPONDER)
        ),
        RollConfiguration(
            1u,
            Action.Timeout.createFromBounds(lowerBound = 28.days, upperBound = 28.days, TIMEOUT_RESPONDER)
        ),
    )
}
