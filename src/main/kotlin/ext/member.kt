package moe.best.kimoneri.ext

import moe.best.kimoneri.roulette.config.GuildConfiguration
import moe.best.kimoneri.roulette.permissions.PermissionsGroup
import moe.best.kimoneri.roulette.permissions.PermissionsGroup.*
import net.dv8tion.jda.api.entities.Member

/** Returns the permissions for a user. */
fun Member.getPermissionsGroup(guildConfiguration: GuildConfiguration): PermissionsGroup {
    val memberRoles = this.roles.map { it.idLong }.toSet()

    val isModerator = guildConfiguration.moderatorRoles.intersect(memberRoles).isNotEmpty()
    if (isModerator) return MODERATOR

    val isProtected = guildConfiguration.protectedRoles.intersect(memberRoles).isNotEmpty()
    if (isProtected) return MODERATOR

    return USER
}

/** Convenience method to determine if a user is a moderator. */
fun Member.isModerator(guildConfiguration: GuildConfiguration): Boolean =
    this.getPermissionsGroup(guildConfiguration) == MODERATOR

fun Member.isProtected(guildConfiguration: GuildConfiguration): Boolean =
    this.getPermissionsGroup(guildConfiguration) == PROTECTED