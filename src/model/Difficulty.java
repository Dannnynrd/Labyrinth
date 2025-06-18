package model;

import java.util.Random;

public enum Difficulty {
    // baseSize, sizeVariance, wallPercentage, enemyPercentage, enemyMoveIntervalMillis
    EASY(25, 2, 0.20, 0.0, 1500),
    MEDIUM(35, 3, 0.30, 0.05, 1000),
    HARD(45, 4, 0.35, 0.08, 500);

    private final int baseSize;
    private final int sizeVariance;
    private final double wallPercentage;
    private final double enemyPercentage;
    private final long enemyMoveIntervalMillis;
    private static final Random rand = new Random();

    // Scaling Constants (you can adjust these values)
    private static final int LEVEL_SIZE_INCREMENT = 2; // World size increases by this much per level
    private static final double LEVEL_WALL_PERCENTAGE_INCREMENT = 0.005; // Wall % increases by 0.5% per level
    private static final double LEVEL_ENEMY_PERCENTAGE_INCREMENT = 0.003; // Enemy % increases by 0.3% per level
    private static final long LEVEL_SPEED_DECREMENT = 20; // Enemy move interval decreases by 20ms per level
    private static final long MIN_ENEMY_MOVE_INTERVAL = 150; // Minimum enemy move interval

    private static final double MAX_WALL_PERCENTAGE = 0.50; // Max 50% walls
    private static final double MAX_ENEMY_PERCENTAGE = 0.20; // Max 20% enemies


    Difficulty(int baseSize, int sizeVariance, double wallPercentage, double enemyPercentage, long enemyMoveIntervalMillis) {
        this.baseSize = baseSize;
        this.sizeVariance = sizeVariance;
        this.wallPercentage = wallPercentage;
        this.enemyPercentage = enemyPercentage;
        this.enemyMoveIntervalMillis = enemyMoveIntervalMillis;
    }

    public int generateRandomSize() {
        return baseSize + rand.nextInt(sizeVariance + 1) * 2;
    }

    public double getWallPercentage() {
        return wallPercentage;
    }

    public double getEnemyPercentage() {
        return enemyPercentage;
    }

    public long getEnemyMoveIntervalMillis() {
        return enemyMoveIntervalMillis;
    }

    // Scaled methods based on level
    public int getScaledWorldSize(int level) {
        // Size increases with level, ensuring it's always an even number for grid consistency (optional)
        int scaledSize = baseSize + (level - 1) * LEVEL_SIZE_INCREMENT;
        return scaledSize + rand.nextInt(sizeVariance + 1) * 2; // Still add random variance
    }

    public double getScaledWallPercentage(int level) {
        double scaled = wallPercentage + (level - 1) * LEVEL_WALL_PERCENTAGE_INCREMENT;
        return Math.min(scaled, MAX_WALL_PERCENTAGE); // Cap at max percentage
    }

    public double getScaledEnemyPercentage(int level) {
        double scaled = enemyPercentage + (level - 1) * LEVEL_ENEMY_PERCENTAGE_INCREMENT;
        return Math.min(scaled, MAX_ENEMY_PERCENTAGE); // Cap at max percentage
    }

    public long getScaledEnemyMoveIntervalMillis(int level) {
        long scaled = enemyMoveIntervalMillis - (level - 1) * LEVEL_SPEED_DECREMENT;
        return Math.max(scaled, MIN_ENEMY_MOVE_INTERVAL); // Ensure it doesn't go below minimum
    }
}