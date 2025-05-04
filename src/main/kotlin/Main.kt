package moe.best.kimoneri

import moe.best.kimoneri.roulette.RouletteListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent

fun main() {
    JDABuilder.createLight(
        System.getenv("BOT_TOKEN"),
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_TYPING,
        GatewayIntent.MESSAGE_CONTENT,
    ).apply {
        addEventListeners(RouletteListener())
    }.build()
}