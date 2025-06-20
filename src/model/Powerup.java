package model;

/**
 * Represents a power-up item in the game world.
 * A power-up has a specific location (x, y coordinates) and a type,
 * which determines its effect on the player.
 */
public class Powerup {
    /** The X coordinate of the power-up in the game world. */
    public int x;
    /** The Y coordinate of the power-up in the game world. */
    public int y;
    /** The type of the power-up, defining its effect (e.g., HEALTH, INVINCIBILITY). */
    public PowerupType type;

    /**
     * Constructs a new Powerup object.
     * @param x The initial X coordinate of the power-up.
     * @param y The initial Y coordinate of the power-up.
     * @param type The type of the power-up.
     */
    public Powerup(int x, int y, PowerupType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
