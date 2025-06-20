package model;

import java.util.Random;

/**
 * The Difficulty enum defines various difficulty settings for the Labyrinth game.
 * Each difficulty level specifies base parameters for world generation,
 * such as size, wall density, enemy density, and enemy movement speed.
 * It also provides methods to scale these parameters based on the current game level,
 * making the game progressively harder.
 */
public enum Difficulty {
    /** Easy difficulty setting. */
    EASY(25, 2, 0.6, 0.0, 1500),
    /** Medium difficulty setting. */
    MEDIUM(35, 3, 0.5, 0.02, 1000),
    /** Hard difficulty setting. */
    HARD(45, 4, 0.4, 0.03, 750);

    /** The base size (width/height) of the world for this difficulty. */
    private final int baseSize;
    /** The variance added to the base size to create slightly different maze dimensions. */
    private final int sizeVariance;
    /** The initial percentage of walls to remove during maze generation for more open paths. */
    private final double wallPercentage;
    /** The initial percentage of enemies to spawn relative to the world size. */
    private final double enemyPercentage;
    /** The initial interval (in milliseconds) at which enemies move. */
    private final long enemyMoveIntervalMillis;
    /** A static Random instance used for generating random values related to difficulty. */
    private static final Random rand = new Random();

    // Scaling Constants
    /** Amount by which world size increases per level. */
    private static final int LEVEL_SIZE_INCREMENT = 2;
    /** Amount by which wall removal percentage increases per level (makes mazes denser). */
    private static final double LEVEL_WALL_PERCENTAGE_INCREMENT = 0.005;
    /** Amount by which enemy spawn percentage increases per level. */
    private static final double LEVEL_ENEMY_PERCENTAGE_INCREMENT = 0.003;
    /** Amount by which enemy move interval decreases per level (makes enemies faster). */
    private static final long LEVEL_SPEED_DECREMENT = 20;
    /** The minimum allowed enemy move interval (prevents enemies from becoming impossibly fast). */
    private static final long MIN_ENEMY_MOVE_INTERVAL = 150;
    /** The maximum allowed wall percentage to prevent completely solid mazes. */
    private static final double MIN_WALL_PERCENTAGE = 0.20;
    /** The maximum allowed enemy percentage to prevent overly crowded mazes. */
    private static final double MAX_ENEMY_PERCENTAGE = 0.20;


    /**
     * Constructs a new Difficulty enum constant.
     *
     * @param baseSize The base dimension for the labyrinth.
     * @param sizeVariance The random variance to multiply to the basesize.
     * @param wallPercentage The percentage of walls to remove.
     * @param enemyPercentage The percentage of enemies to spawn.
     * @param enemyMoveIntervalMillis The delay between enemy moves in milliseconds.
     */
    Difficulty(int baseSize, int sizeVariance, double wallPercentage, double enemyPercentage, long enemyMoveIntervalMillis) {
        this.baseSize = baseSize;
        this.sizeVariance = sizeVariance;
        this.wallPercentage = wallPercentage;
        this.enemyPercentage = enemyPercentage;
        this.enemyMoveIntervalMillis = enemyMoveIntervalMillis;
    }

    /**
     * Generates a random world size based on the difficulty's base size and variance.
     * The size will be an odd number for better maze generation algo.
     *
     * @return A randomly generated world dimension based on difficulty.
     */
    public int generateRandomSize() {
        return baseSize + rand.nextInt(sizeVariance + 1) * 2;
    }

    /**
     * Returns the base wall removal percentage for this difficulty.
     * @return The initial wall removal percentage.
     */
    public double getWallPercentage() {
        return wallPercentage;
    }

    /**
     * Returns the base enemy spawn percentage for this difficulty.
     * @return The initial enemy spawn percentage.
     */
    public double getEnemyPercentage() {
        return enemyPercentage;
    }

    /**
     * Returns the base enemy movement interval in milliseconds for this difficulty.
     * @return The initial enemy movement interval.
     */
    public long getEnemyMoveIntervalMillis() {
        return enemyMoveIntervalMillis;
    }

    /**
     * Calculates the scaled world size for a given game level.
     * The size increases with each level and ensuring it remains an odd number.
     *
     * @param level The current game level
     * @return The calculated scaled world size.
     */
    public int getScaledWorldSize(int level) {
        // Size increasement with level, size will be also an odd number
        int scaledSize = baseSize + (level - 1) * LEVEL_SIZE_INCREMENT;
        // Still add random variance and after that it also the size should be odd.
        int finalSize = scaledSize + rand.nextInt(sizeVariance + 1) * 2;
        return (finalSize % 2 == 0) ? finalSize + 1 : finalSize; // make sure it's odd
    }

    /**
     * Calculates the scaled wall removal percentage for a given game level.
     * The percentage decreases with each level, making mazes denser (fewer removed walls).
     * The value is capped at 20%
     *
     * @param level The current game level.
     * @return The calculated scaled wall removal percentage.
     */
    public double getScaledWallPercentage(int level) {
        double scaled = wallPercentage - (level - 1) * LEVEL_WALL_PERCENTAGE_INCREMENT;
        return Math.max(scaled, MIN_WALL_PERCENTAGE);
    }

    /**
     * Calculates the scaled enemy spawn percentage for a given game level.
     * The percentage increases with each level, leading to more enemies.
     * The value is capped at 0.20.
     *
     * @param level The current game level.
     * @return The calculated scaled enemy spawn percentage.
     */
    public double getScaledEnemyPercentage(int level) {
        double scaled = enemyPercentage + (level - 1) * LEVEL_ENEMY_PERCENTAGE_INCREMENT;
        return Math.min(scaled, MAX_ENEMY_PERCENTAGE); // Cap at max percentage as ScaledWallPercantage
    }

    /**
     * Calculates the scaled enemy movement interval for a given game level.
     * The interval decreases with each level, making enemies faster.
     * The value is capped at 150ms.
     *
     * @param level The current game level.
     * @return The calculated scaled enemy movement interval.
     */
    public long getScaledEnemyMoveIntervalMillis(int level) {
        long scaled = enemyMoveIntervalMillis - (level - 1) * LEVEL_SPEED_DECREMENT;
        return Math.max(scaled, MIN_ENEMY_MOVE_INTERVAL); // Ensure it doesn't go below minimum
    }
}
