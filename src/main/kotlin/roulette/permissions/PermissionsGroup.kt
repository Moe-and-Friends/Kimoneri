package moe.best.kimoneri.roulette.permissions

/** Roll-specific roles on a specific Member for controlling permissions behaviour. */
enum class PermissionsGroup {
    /** Standard user that can only roll for themselves. */
    USER,

    /** Users that are protected from negative roll effects. */
    PROTECTED,

    /** Users that can roll for other users, and are immune to rolls themselves. */
    MODERATOR;
}