package moe.best.kimoneri.roulette.config.guilds

import moe.best.kimoneri.roulette.config.GuildConfiguration

/** Provider of per-Guild configurations. */
object GuildConfigurationProvider {

    private val configs = listOf(MOE_AND_FRIENDS_GUILD_CONFIG).associateBy { it.id }

    /** Returns the [GuildConfiguration] for a Guild, or null if it doesn't exist. */
    fun getConfigForGuild(id: Long): GuildConfiguration? = configs[id]

}