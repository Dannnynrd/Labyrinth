package model;

import java.util.Random;

public enum Difficulty {
    // We now store a base size and a variance for the size.
    EASY(11, 2, 0.20, 0.0),      // Size can be 11, 13, 15
    MEDIUM(21, 3, 0.30, 0.05),   // Size can be 21, 23, 25, 27
    HARD(31, 4, 0.35, 0.08);     // Size can be 31, 33, 35, 37, 39

    private final int baseSize;
    private final int sizeVariance; // How many steps of 2 can be added to the base size
    private final double wallPercentage;
    private final double enemyPercentage;
    private static final Random rand = new Random();

    Difficulty(int baseSize, int sizeVariance, double wallPercentage, double enemyPercentage) {
        this.baseSize = baseSize;
        this.sizeVariance = sizeVariance;
        this.wallPercentage = wallPercentage;
        this.enemyPercentage = enemyPercentage;
    }

    // This new method generates a random odd size within the defined range.
    public int generateRandomSize() {
        // It takes the base size and adds a random even number (0, 2, 4, ...)
        return baseSize + rand.nextInt(sizeVariance + 1) * 2;
    }

    public double getWallPercentage() {
        return wallPercentage;
    }

    public double getEnemyPercentage() {
        return enemyPercentage;
    }
}