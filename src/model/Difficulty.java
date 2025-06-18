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
    private final long enemyMoveIntervalMillis; // NEU: Bewegungsintervall der Gegner
    private static final Random rand = new Random();

    Difficulty(int baseSize, int sizeVariance, double wallPercentage, double enemyPercentage, long enemyMoveIntervalMillis) {
        this.baseSize = baseSize;
        this.sizeVariance = sizeVariance;
        this.wallPercentage = wallPercentage;
        this.enemyPercentage = enemyPercentage;
        this.enemyMoveIntervalMillis = enemyMoveIntervalMillis; // NEU
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
}