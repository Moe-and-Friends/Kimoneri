package moe.best.kimoneri.roulette.config

import moe.best.kimoneri.roulette.actions.Action

/**
 * Configuration for a roll possibility a user could get in Roulette.
 *
 * @property weight Weight (probability) of this role, relative to the sum of all weights.
 * @property action Action to execute if this roll is rolled.
 */
data class RollConfiguration(val weight: UInt, val action: Action)