package moe.best.kimoneri.ext

import kotlin.time.Duration

// TODO: This function needs to be implemented.
fun Duration.toHumanString() = buildString {
    this@toHumanString.toComponents { totalDays, hours, minutes, seconds, nanoseconds ->
        val weeks = totalDays.floorDiv(7).toInt()
        val days = totalDays.mod(7)
    }

    val totalDays = this@toHumanString.inWholeDays.toInt()

    val weeks = totalDays.floorDiv(7)
    val days = totalDays.mod(7)

    if (weeks > 0) {
        this.append("$weeks weeks")
    }
}