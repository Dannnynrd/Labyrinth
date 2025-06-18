package model;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;
import java.util.List;
import view.View;

/**
 * The world is our model. It saves the bare minimum of information required to
 * accurately reflect the state of the game. Note how this does not know
 * anything about graphics.
 */
public class World {

	/** The world's width. */
	private int width;
	/** The world's height. */
	private int height;
	/** The player's x position in the world. */
	private int playerX = 0;
	/** The player's y position in the world. */
	private int playerY = 0;
	/** walls */
	private boolean[][] walls;
	/** Enemies */
	private final List<Point> enemies;
	/** Game over (True / False) */
	private boolean gameOver;

	private Difficulty difficulty;

	private boolean isPaused = false;

	private int currentLevel; // Field to track the current level

	/** End Block */
	private int endX = 0;
	private int endY = 0;

	/** Set of views registered to be notified of world updates. */
	private final ArrayList<View> views = new ArrayList<>();

	/**
	 * Creates a new world with the given size.t
	 */
	public World(Difficulty difficulty) {
		this.difficulty = difficulty;
		this.enemies = new ArrayList<>();
		this.currentLevel = 1; // Initialize level to 1

		restart(difficulty); // Call restart with initial difficulty
	}

	// Modified restart method to accept a new difficulty
	public void restart(Difficulty newDifficulty) {
		this.difficulty = newDifficulty; // Update the world's difficulty
		// Reset game over status
		this.gameOver = false;
		// Reset paused status
		this.isPaused = false;

		// Use scaled dimensions based on current level
		this.width = difficulty.getScaledWorldSize(currentLevel);
		this.height = difficulty.getScaledWorldSize(currentLevel);
		this.walls = new boolean[width][height]; // Initialize the walls array here

		this.enemies.clear();

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				this.walls[j][i] = false;
			}
		}

		Random rand = new Random();
		this.playerX = rand.nextInt(width);
		this.playerY = rand.nextInt(height);

		do {
			this.endX = rand.nextInt(width);
			this.endY = rand.nextInt(height);
		} while (this.endX == this.playerX && this.endY == this.playerY);

		// Use the scaled wall percentage from the enum
		int numberOfWalls = (int) (width * height * difficulty.getScaledWallPercentage(currentLevel));
		for (int i = 0; i < numberOfWalls; i++) {
			int wallX = rand.nextInt(width);
			int wallY = rand.nextInt(height);
			boolean isPlayerPos = (wallX == this.playerX) && (wallY == this.playerY);
			boolean isEndPos = (wallX == this.endX) && (wallY == this.endY);
			if (!isPlayerPos && !isEndPos) {
				this.walls[wallX][wallY] = true;
			}
		}

		// Use the scaled enemy percentage from the enum
		int numberOfEnemies = (int) (width * height * difficulty.getScaledEnemyPercentage(currentLevel));
		for (int i = 0; i < numberOfEnemies; i++) {
			int enemyX, enemyY;
			boolean isWall, isPlayer, isEnd;
			do {
				enemyX = rand.nextInt(width);
				enemyY = rand.nextInt(height);
				isWall = isWall(enemyX, enemyY);
				isPlayer = (enemyX == this.playerX && enemyY == this.playerY);
				isEnd = (enemyX == this.endX && enemyY == this.endY);
			} while (isWall || isPlayer || isEnd);
			enemies.add(new Point(enemyX, enemyY));
		}

		// Checks if user is on an Enemy at start
		for (Point enemy : enemies) {
			if (enemy.x == playerX && enemy.y == playerY) {
				this.gameOver = true; // Player starts on an enemy, game over
				break;
			}
		}

		updateViews();
	}


	///////////////////////////////////////////////////////////////////////////
	// Getters and Setters
	public boolean isPaused() {
		return isPaused;
	}
	public void setPaused(boolean paused) {
		isPaused = paused;
		updateViews();
	}
	/**
	 * Returns the width of the world.
	 *
	 * @return the width of the world.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the world.
	 *
	 * @return the height of the world.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the player's x position.
	 *
	 * @return the player's x position.
	 */
	public int getPlayerX() {
		return playerX;
	}

	/**
	 * Sets the player's x position.
	 *
	 * @param playerX the player's x position.
	 */
	public void setPlayerX(int playerX) {
		playerX = Math.max(0, playerX);
		playerX = Math.min(getWidth() - 1, playerX);
		this.playerX = playerX;
	}

	/**
	 * Returns the player's y position.
	 *
	 * @return the player's y position.
	 */
	public int getPlayerY() {
		return playerY;
	}

	/**
	 * Sets the player's y position.
	 *
	 * @param playerY the player's y position.
	 */
	public void setPlayerY(int playerY) {
		playerY = Math.max(0, playerY);
		playerY = Math.min(getHeight() - 1, playerY);
		this.playerY = playerY;
	}

	public int getEndX() {
		return endX;
	}
	public int getEndY() {
		return endY;
	}

	public List<Point> getEnemies() {
		return enemies;
	}

	public boolean isGameOver() {return gameOver;}
	public boolean isEnemyAt(int x, int y) {
		for (Point enemy : enemies) {
			if(enemy.x == x && enemy.y == y){
				return true;
			}
		}
		return false;
	}

	// Getter for current level
	public int getCurrentLevel() {
		return currentLevel;
	}

	public long getEnemyMoveIntervalMillis() {
		return difficulty.getScaledEnemyMoveIntervalMillis(currentLevel);
	}

	// Getter for current difficulty
	public Difficulty getDifficulty() {
		return difficulty;
	}
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	///////////////////////////////////////////////////////////////////////////
	// Player Management

	/**
	 * Moves the player along the given direction.
	 *
	 * @param direction where to move.
	 */

	public void movePlayer(Direction direction) {
		if (isGameOver() || isPaused()) {
			return;
		}
		int newPlayerX = getPlayerX() + direction.deltaX;
		int newPlayerY = getPlayerY() + direction.deltaY;

		if (!isWall(newPlayerX, newPlayerY)) {
			setPlayerX(newPlayerX);
			setPlayerY(newPlayerY);

			// Check if player reached the end point
			if (playerX == endX && playerY == endY) {
				currentLevel++; // Increment level
				restart(this.difficulty); // Restart the level with current difficulty settings
				return; // Exit method, a new level has started
			}

			if(isEnemyAt(getPlayerX(),getPlayerY())){
				this.gameOver = true;
			}
		}
		updateViews(); // Update views after player move
	}

	// Methode zum Bewegen aller Gegner
	public void moveEnemies() {
		if (isPaused() || isGameOver()) {
			return;
		}

		Random rand = new Random();

		for (Point enemy : enemies) {
			int currentEnemyX = enemy.x;
			int currentEnemyY = enemy.y;

			int targetX = playerX;
			int targetY = playerY;

			int deltaX = Integer.compare(targetX, currentEnemyX);
			int deltaY = Integer.compare(targetY, currentEnemyY);

			boolean movedThisTurn = false;

			// Try to move in one direction, or randomly choose
			if (deltaX != 0 && deltaY != 0) {
				if (rand.nextBoolean()) { // Try to move horizontally
					int potentialNewX = currentEnemyX + deltaX;
					if (!isWall(potentialNewX, currentEnemyY)) {
						enemy.setLocation(potentialNewX, currentEnemyY);
						movedThisTurn = true;
					} else { // If horizontal is blocked, try vertically
						int potentialNewY = currentEnemyY + deltaY;
						if (!isWall(currentEnemyX, potentialNewY)) {
							enemy.setLocation(currentEnemyX, potentialNewY);
							movedThisTurn = true;
						}
					}
				} else { // Try to move vertically
					int potentialNewY = currentEnemyY + deltaY;
					if (!isWall(currentEnemyX, potentialNewY)) {
						enemy.setLocation(currentEnemyX, potentialNewY);
						movedThisTurn = true;
					} else { // If vertical is blocked, try horizontally
						int potentialNewX = currentEnemyX + deltaX;
						if (!isWall(potentialNewX, currentEnemyY)) {
							enemy.setLocation(potentialNewX, currentEnemyY);
							movedThisTurn = true;
						}
					}
				}
			} else if (deltaX != 0) { // Only horizontal movement needed
				int potentialNewX = currentEnemyX + deltaX;
				if (!isWall(potentialNewX, currentEnemyY)) {
					enemy.setLocation(potentialNewX, currentEnemyY);
					movedThisTurn = true;
				}
			} else if (deltaY != 0) { // Only vertical movement needed
				int potentialNewY = currentEnemyY + deltaY;
				if (!isWall(currentEnemyX, potentialNewY)) {
					enemy.setLocation(currentEnemyX, potentialNewY);
					movedThisTurn = true;
				}
			}

			// Collision check after enemy moves
			if (enemy.x == playerX && enemy.y == playerY) {
				this.gameOver = true;
				break; // Game is over, no need to move other enemies
			}
		}
		updateViews(); // Update views after all enemies moved
	}


	///////////////////////////////////////////////////////////////////////////
	// View Management

	/**
	 * Adds the given view of the world and updates it once. Once registered through
	 * this method, the view will receive updates whenever the world changes.
	 *
	 * @param view the view to be registered.
	 */
	public void registerView(View view) {
		views.add(view);
		view.update(this);
	}

	/**
	 * Updates all views by calling their {@link View#update(World)} methods.
	 */
	private void updateViews() {
		for (int i = 0; i < views.size(); i++) {
			views.get(i).update(this);
		}
	}

	// Checks if there is a Wall
	public boolean isWall(int x, int y) {
		if  (x >= 0 && x < width && y >= 0 && y < height) {
			return walls[x][y];
		}
		return true; //
	}
}