package moe.best.kimoneri.roulette.actions

import moe.best.kimoneri.roulette.permissions.PermissionsGroup
import net.dv8tion.jda.api.entities.Member

/**
 * The [Member] who is the receiver of a roulette roll.
 *
 * @property member The member to conditionally apply an [Action] onto.
 * @property permissions [member]'s [PermissionsGroup].
 * @property initiator The member who initiated the roll. This is often the same as [member].
 */
data class Target(
    val member: Member,
    val permissions: PermissionsGroup,
    val initiator: Member
)