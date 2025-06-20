package model;

/**
 * Defines the different types of power-ups available in the Labyrinth game.
 * Each type corresponds to a specific beneficial effect for the player.
 */
public enum PowerupType {
    /** A power-up that increases the player's health by one. */
    HEALTH,
    /** A power-up that grants the player temporary invincibility to enemy damage. */
    INVINCIBILITY,
    /** A power-up that temporarily freezes all enemies, preventing their movement. */
    FREEZE_ENEMIES
}
