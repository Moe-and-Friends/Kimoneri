package moe.best.kimoneri.ext

import kotlin.time.Duration

fun Duration.toHumanString(granularity: Int = 2): String {
    val components = buildList {
        this@toHumanString.toComponents { totalDays, hours, minutes, _, _ ->

            val weeks = totalDays.floorDiv(7).toInt()
            when {
                weeks == 1 -> add("1 week")
                weeks > 1 -> add("$weeks weeks")
            }

            val days = totalDays.mod(7)
            when {
                days == 1 -> add("1 day")
                days > 1 -> add("$days days")
            }

            when {
                hours == 1 -> add("1 hour")
                hours > 1 -> add("$hours hours")
            }

            when {
                minutes == 1 -> add("1 minute")
                minutes > 1 -> add("$minutes minutes")
            }
        }
    }.take(granularity)

    return when (components.size) {
        1 -> components.single()
        2 -> components.joinToString(" and ")
        else -> {
            buildString {
                append(components.dropLast(1).joinToString(", "))
                append(", and ${components.last()}")
            }
        }
    }
}