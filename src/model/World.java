package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import view.View;


/**
 * The World class represents the game model. It holds the complete state of the Labyrinth game,
 * including the maze structure, player position, enemy positions, power-up locations,
 * game over status, current level, player health, and active power-up effects.
 */
public class World {

	/** The width of the game world grid. */
	private int width;
	/** The height of the game world grid. */
	private int height;
	/** The player's current X position in the world. */
	private int playerX = 0;
	/** The player's current Y position in the world. */
	private int playerY = 0;
	/** A 2D array representing the maze walls. True if it's a wall, false if it's a path/floor. */
	private boolean[][] walls;
	/** A list of Point objects representing the current positions of all enemies. */
	private final List<Point> enemies;
	/** A boolean flag indicating if the game is over. */
	private boolean gameOver;
	/** The current difficulty setting of the game. */
	private Difficulty difficulty;
	/** A boolean flag indicating if the game is currently paused. */
	private boolean isPaused = false;
	/** The current level the player is on. Starts at 1. */
	private int currentLevel;
	/** The player's current health points. */
	private int playerHealth;
	/** The maximum possible health for the player. */
	private static final int MAX_PLAYER_HEALTH = 5;
	/** Flag indicating if the player is currently invincible. */
	private boolean isInvincible = false;
	/** Remaining time (in milliseconds) for the invincibility power-up. */
	private long invincibilityRemainingTime = 0;
	/** Maximum duration (in milliseconds) for the invincibility power-up. */
	private static final long MAX_INVINCIBILITY_DURATION_MILLIS = 2500; // 2.5 seconds
	/** Flag indicating if enemies are currently frozen. */
	private boolean areEnemiesFrozen = false;
	/** Remaining time (in milliseconds) for the enemy freeze power-up. */
	private long enemyFreezeRemainingTime = 0;
	/** Maximum duration (in milliseconds) for the enemy freeze power-up. */
	private static final long MAX_ENEMY_FREEZE_DURATION_MILLIS = 4000; // 4 seconds
	/** The X coordinate of the exit point of the labyrinth for the current level. */
	private int endX = 0;
	/** The Y coordinate of the exit point of the labyrinth for the current level. */
	private int endY = 0;
	/** A list of active power-up objects currently present in the world. */
	private final List<Powerup> powerups;
	/** The number of power-ups to spawn at the beginning of each level. */
	private static final int INITIAL_POWERUPS_PER_LEVEL = 5; // Number of power-ups to spawn per level
	/** A list of views registered to be notified of world updates. */
	private final ArrayList<View> views = new ArrayList<>();

	/**
	 * Constructs a new World with the given initial difficulty.
	 * Initializes game state, including level, player health, and power-up lists.
	 * It then immediately calls {@link #restart(Difficulty, boolean)} to generate the first maze.
	 *
	 * @param difficulty The initial difficulty setting for the world.
	 */
	public World(Difficulty difficulty) {
		this.difficulty = difficulty;
		this.enemies = new ArrayList<>();
		this.powerups = new ArrayList<>(); // Initialize power-ups list with custom Powerup objects
		this.currentLevel = 1; // Start at level to 1
		this.playerHealth = MAX_PLAYER_HEALTH; // initializes players health to the MAX_PLAYER_HEALTH

		restart(difficulty, true); // Call restart with initial difficulty, resetting health
	}

	/**
	 * Resets and restarts the game with a new difficulty or the current difficulty.
	 * This method initializes a new maze, places the player, end point, and enemies.
	 * It also resets power-up states and optionally player health.
	 *
	 * @param newDifficulty The current or selected difficulty setting to use for the new game.
	 * @param resetPlayerHealth True when player will reset the PlayerHealth(Restart game or Game over), False when next level,
	 */

	public void restart(Difficulty newDifficulty, boolean resetPlayerHealth) {
		this.difficulty = newDifficulty; // Update the world's difficulty for the new game
		// Reset game over status
		this.gameOver = false;
		// Reset paused status
		this.isPaused = false;
		if (resetPlayerHealth) { // Resets Players Health when resetPLayerHealth is true (for restart and gameover) not when new level
			this.playerHealth = MAX_PLAYER_HEALTH;
		}

		// Reset power-up states and their remaining times
		this.isInvincible = false;
		this.invincibilityRemainingTime = 0;
		this.areEnemiesFrozen = false;
		this.enemyFreezeRemainingTime = 0;

		// Use scaled dimensions based on the current level for difficulty.
		// Ensure dimensions are odd for better maze generation (algorithm).
		this.width = difficulty.getScaledWorldSize(currentLevel);
		this.height = difficulty.getScaledWorldSize(currentLevel);
		if (this.width % 2 == 0) this.width++; // Make width odd
		if (this.height % 2 == 0) this.height++; // Make height odd

		this.walls = new boolean[width][height]; // Initialize the walls array for the new maze

		this.enemies.clear(); // Clear existing enemies
		this.powerups.clear(); // Clear existing power-ups

		// Initialize all cells as walls before maze generation
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				this.walls[j][i] = true; // All cells are walls initially
			}
		}

		Random rand = new Random();

		// Set player and end positions on valid 'path' cells.
		// Randomly choose valid odd coordinates within the bounds.
		this.playerX = rand.nextInt((width - 1) / 2) * 2 + 1;
		this.playerY = rand.nextInt((height - 1) / 2) * 2 + 1;

		// Ensure the end point is different from the player's starting position
		do {
			this.endX = rand.nextInt((width - 1) / 2) * 2 + 1;
			this.endY = rand.nextInt((height - 1) / 2) * 2 + 1;
		} while (this.endX == this.playerX && this.endY == this.playerY);

		// Generate the maze using the Depth-First Search (Recursive Backtracker) algorithm,
		// starting from the player's initial position.
		generateMazeDFS(playerX, playerY);

		// Post-processing: Remove a percentage of walls to make the labyrinth more open
		// and prevent it from being too constricting. The percentage scales with level.
		removeRandomWalls(difficulty.getScaledWallPercentage(currentLevel));


		// Place enemies randomly throughout the maze.
		// Number of enemies scales with difficulty and current level.
		int numberOfEnemies = (int) (width * height * difficulty.getScaledEnemyPercentage(currentLevel));
		for (int i = 0; i < numberOfEnemies; i++) {
			int enemyX, enemyY;
			boolean isWall, isPlayer, isEnd, isPowerup;
			do {
				enemyX = rand.nextInt(width);
				enemyY = rand.nextInt(height);
				// Ensure enemy is not placed on a wall, player, end, or another power-up
				isWall = isWall(enemyX, enemyY);
				isPlayer = (enemyX == this.playerX && enemyY == this.playerY);
				isEnd = (enemyX == this.endX && enemyY == this.endY);
				isPowerup = isPowerupAt(enemyX, enemyY);
			} while (isWall || isPlayer || isEnd || isPowerup);
			enemies.add(new Point(enemyX, enemyY)); // Add the new enemy
		}

		// Place power-ups randomly throughout the maze.
		// The number of power-ups is fixed per level.
		for (int i = 0; i < INITIAL_POWERUPS_PER_LEVEL; i++) {
			int powerupX, powerupY;
			boolean isWall, isPlayer, isEnd, isEnemy, isAnotherPowerup;
			PowerupType type;
			// Assign specific types to power-ups for consistent distribution
			if (i == 0) {
				type = PowerupType.HEALTH;
			} else if (i == 1) {
				type = PowerupType.INVINCIBILITY;
			} else {
				type = PowerupType.FREEZE_ENEMIES;
			}

			do {
				powerupX = rand.nextInt(width);
				powerupY = rand.nextInt(height);
				// Ensure power-up is not placed on a wall, player, end, enemy, or another power-up
				isWall = isWall(powerupX, powerupY);
				isPlayer = (powerupX == this.playerX && powerupY == this.playerY);
				isEnd = (powerupX == this.endX && powerupY == this.endY);
				isEnemy = isEnemyAt(powerupX, powerupY);
				isAnotherPowerup = isPowerupAt(powerupX, powerupY);
			} while (isWall || isPlayer || isEnd || isEnemy || isAnotherPowerup);
			powerups.add(new Powerup(powerupX, powerupY, type)); // Add the new power-up
		}


		// if player is on an enemy and is not invincible, lose health.
		if (isEnemyAt(playerX, playerY) && !isInvincible) {
			playerHealth--;
			if (playerHealth <= 0) {
				this.gameOver = true; // Game over if health drops to 0 or below
			}
		}

		updateViews(); // UpdateView
	}

	/**
	 * Generates a maze using the Depth-First Search (Recursive Backtracker) algorithm.
	 * This algorithm carves paths by setting wall cells to false, starting from a given point.
	 * It ensures that a single, continuous path exists through the maze.
	 *
	 * @param startX The starting X coordinate for maze generation. This cell will be a path.
	 * @param startY The starting Y coordinate for maze generation. This cell will be a path.
	 */
	private void generateMazeDFS(int startX, int startY) {
		Stack<Point> stack = new Stack<>(); // Stack to keep track of visited cells for backtracking
		boolean[][] visited = new boolean[width][height]; // Tracks visited cells during DFS
		Random rand = new Random();

		stack.push(new Point(startX, startY));
		visited[startX][startY] = true;
		walls[startX][startY] = false; // Carve out the starting cell, making it a path

		while (!stack.isEmpty()) {
			Point current = stack.peek(); // Get current cell without removing it (for neighbor checking)

			// Get a list of unvisited neighbors (cells two steps away, as per maze generation rules)
			List<Direction> unvisitedNeighbors = getUnvisitedNeighbors(current.x, current.y, visited);

			if (!unvisitedNeighbors.isEmpty()) {
				// Choose a rndom unvisited neigahbor
				Direction chosenDirection = unvisitedNeighbors.get(rand.nextInt(unvisitedNeighbors.size()));
				int nextX = current.x + chosenDirection.deltaX * 2; // Calculate X of the next cell
				int nextY = current.y + chosenDirection.deltaY * 2; // Calculate Y of the next cell

				// Carve out the wall cell between the current cell and the next cell
				walls[current.x + chosenDirection.deltaX][current.y + chosenDirection.deltaY] = false;
				walls[nextX][nextY] = false; // Carve out the next cell itself

				visited[nextX][nextY] = true; // Mark the new cell as visited
				stack.push(new Point(nextX, nextY)); // Push the new cell onto the stack
			} else {
				stack.pop(); // If no unvisited neighbors, backtrack by popping from the stack
			}
		}
	}

	/**
	 * Gets a list of unvisited neighbor cells (cells two steps away) from the given coordinates.
	 * This method is used by the DFS maze generation algorithm to find new paths to carve.
	 * It checks all four cardinal directions.
	 *
	 * @param x The current X coordinate.
	 * @param y The current Y coordinate.
	 * @param visited A 2D boolean array indicating which cells have already been visited by DFS.
	 * @return A list of {@link Direction} enum constants, each representing a valid, unvisited neighbor.
	 */
	private List<Direction> getUnvisitedNeighbors(int x, int y, boolean[][] visited) {
		List<Direction> neighbors = new ArrayList<>();
		// Define all four cardinal directions
		Direction[] cardinalDirections = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

		for (Direction dir : cardinalDirections) {
			int neighborX = x + dir.deltaX * 2; // Calculate X of the potential new cell (2 steps away)
			int neighborY = y + dir.deltaY * 2; // Calculate Y of the potential new cell (2 steps away)

			// Check if the potential neighbor is within the world bounds and has not been visited yet
			if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height && !visited[neighborX][neighborY]) {
				neighbors.add(dir); // Add this direction as a valid unvisited neighbor
			}
		}
		Collections.shuffle(neighbors); // Shuffle the list to introduce randomness in maze generation
		return neighbors;
	}

	/**
	 * Removes a percentage of the existing walls to make the labyrinth more open and less constricting.
	 * @param percentageToOpen The percentage of existing interior walls to potentially remove.
	 */
	private void removeRandomWalls(double percentageToOpen) {
		Random rand = new Random();
		List<Point> potentialWallsToRemove = new ArrayList<>();

		// Iterate through the grid to identify all existing walls that are not critical (player, end)
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
					potentialWallsToRemove.add(new Point(x, y));
			}
		}

		// Calculate the number of walls to remove based on the given percentage
		int wallsToRemoveCount = (int) (potentialWallsToRemove.size() * percentageToOpen);

		// Shuffle the list of potential walls to randomize which ones are selected for removal
		Collections.shuffle(potentialWallsToRemove, rand);

		// Iterate through the shuffled list and remove the specified number of walls
		for (int i = 0; i < wallsToRemoveCount && i < potentialWallsToRemove.size(); i++) {
			Point wall = potentialWallsToRemove.get(i);
				walls[wall.x][wall.y] = false; // Set the cell to a path (remove the wall)

		}
	}


	// Getters and Setters

	/**
	 * Checks if the game is currently paused.
	 * @return true if the game is paused, false otherwise.
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Sets the paused state of the game.
	 * @param paused true to pause the game, false to unpause.
	 */
	public void setPaused(boolean paused) {
		isPaused = paused;
		updateViews(); // Notify views about the pause state change
	}

	/**
	 * Returns the width of the game world grid.
	 * @return The width of the world.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the game world grid.
	 * @return The height of the world.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the player's current X position.
	 * @return The player's X position.
	 */
	public int getPlayerX() {
		return playerX;
	}

	/**
	 * Sets the player's X position, ensuring it stays within world bounds.
	 * @param playerX The new X position for the player.
	 */
	public void setPlayerX(int playerX) {
		playerX = Math.max(0, playerX); // Ensure not less than 0
		playerX = Math.min(getWidth() - 1, playerX); // Ensure not greater than width - 1
		this.playerX = playerX;
	}

	/**
	 * Returns the player's current Y position.
	 * @return The player's Y position.
	 */
	public int getPlayerY() {
		return playerY;
	}

	/**
	 * Sets the player's Y position, ensuring it stays within world bounds.
	 * @param playerY The new Y position for the player.
	 */
	public void setPlayerY(int playerY) {
		playerY = Math.max(0, playerY); // Ensure not less than 0
		playerY = Math.min(getHeight() - 1, playerY); // Ensure not greater than height - 1
		this.playerY = playerY;
	}

	/**
	 * Returns the X coordinate of the end point (exit) of the labyrinth.
	 * @return The X coordinate of the end point.
	 */
	public int getEndX() {
		return endX;
	}

	/**
	 * Returns the Y coordinate of the end point (exit) of the labyrinth.
	 * @return The Y coordinate of the end point.
	 */
	public int getEndY() {
		return endY;
	}

	/**
	 * Returns a list of all current enemy positions in the world.
	 * @return A {@link List} of {@link Point} objects representing enemy coordinates.
	 */
	public List<Point> getEnemies() {
		return enemies;
	}

	/**
	 * Returns a list of all active power-ups currently present in the world.
	 * @return A {@link List} of {@link Powerup} objects.
	 */
	public List<Powerup> getPowerups() {
		return powerups;
	}

	/**
	 * Checks if the game is currently in a "game over" state.
	 * @return true if the game is over, false otherwise.
	 */
	public boolean isGameOver() {
		return gameOver;
	}

	/**
	 * Returns the player's current health.
	 * @return The current health points of the player.
	 */
	public int getPlayerHealth() {
		return playerHealth;
	}

	/**
	 * Returns the maximum possible health the player can have.
	 * @return The maximum player health.
	 */
	public int getMaxPlayerHealth() {
		return MAX_PLAYER_HEALTH;
	}

	/**
	 * Checks if the player is currently invincible.
	 * @return true if the player is invincible, false otherwise.
	 */
	public boolean isInvincible() {
		return isInvincible;
	}

	/**
	 * Checks if enemies are currently frozen.
	 * @return true if enemies are frozen, false otherwise.
	 */
	public boolean areEnemiesFrozen() {
		return areEnemiesFrozen;
	}

	/**
	 * Checks if an enemy is present at the given coordinates.
	 * @param x The X coordinate to check.
	 * @param y The Y coordinate to check.
	 * @return true if an enemy is at (x, y), false otherwise.
	 */
	public boolean isEnemyAt(int x, int y) {
		for (Point enemy : enemies) {
			if(enemy.x == x && enemy.y == y){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a power-up is present at the given coordinates.
	 * @param x The X coordinate to check.
	 * @param y The Y coordinate to check.
	 * @return true if a power-up is at (x, y), false otherwise.
	 */
	public boolean isPowerupAt(int x, int y) {
		for (Powerup powerup : powerups) {
			if(powerup.x == x && powerup.y == y){
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves the Powerup object at the given coordinates.
	 * @param x The X coordinate to check.
	 * @param y The Y coordinate to check.
	 * @return The Powerup object at (x, y), or null if no power-up is found there.
	 */
	public Powerup getPowerupAt(int x, int y) {
		for (Powerup powerup : powerups) {
			if(powerup.x == x && powerup.y == y){
				return powerup;
			}
		}
		return null;
	}

	/**
	 * Returns the current game level.
	 * @return The current level number.
	 */
	public int getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * Returns the calculated enemy movement interval (in milliseconds) for the current level and difficulty.
	 * This value scales based on the game level.
	 * @return The enemy movement interval in milliseconds.
	 */
	public long getEnemyMoveIntervalMillis() {
		return difficulty.getScaledEnemyMoveIntervalMillis(currentLevel);
	}

	/**
	 * Returns the current difficulty setting of the game.
	 * @return The current {@link Difficulty} enum constant.
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * Sets the current game level. This is primarily used internally when advancing levels.
	 * @param currentLevel The new current level number.
	 */
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	/**
	 * Decreases the remaining time for the invincibility power-up.
	 * If the time runs out, the player is no longer invincible.
	 * @param deltaTimeMillis The amount of time (in milliseconds) that has passed.
	 */
	public void decreaseInvincibilityTimer(long deltaTimeMillis) {
		if (isInvincible) {
			invincibilityRemainingTime -= deltaTimeMillis;
			if (invincibilityRemainingTime <= 0) {
				isInvincible = false;
				invincibilityRemainingTime = 0; // Ensure it's not negative
			}
			updateViews(); // Update views to reflect invincibility status change
		}
	}

	/**
	 * Decreases the remaining time for the enemy freeze power-up.
	 * If the time runs out, enemies are no longer frozen.
	 * @param deltaTimeMillis The amount of time (in milliseconds) that has passed.
	 */
	public void decreaseEnemyFreezeTimer(long deltaTimeMillis) {
		if (areEnemiesFrozen) {
			enemyFreezeRemainingTime -= deltaTimeMillis;
			if (enemyFreezeRemainingTime <= 0) {
				areEnemiesFrozen = false;
				enemyFreezeRemainingTime = 0; // Ensure it's not negative
			}
			updateViews(); // Update views to reflect enemy freeze status change
		}
	}


	// Player Management

	/**
	 * Moves the player in the specified direction.
	 * The player can only move into non-wall cells.
	 * This method handles power-up collection, level completion, and enemy collisions.
	 *
	 * @param direction The {@link Direction} in which to move the player.
	 */
	public void movePlayer(Direction direction) {
		// Player cannot move if the game is over or paused
		if (isGameOver() || isPaused()) {
			return;
		}

		// Calculate potential new player coordinates
		int newPlayerX = getPlayerX() + direction.deltaX;
		int newPlayerY = getPlayerY() + direction.deltaY;

		// Check if the new position is not a wall
		if (!isWall(newPlayerX, newPlayerY)) {
			setPlayerX(newPlayerX); // Update player's X position
			setPlayerY(newPlayerY); // Update player's Y position

			// Check if the player collected a power-up at the new position
			Powerup collectedPowerup = getPowerupAt(playerX, playerY);
			if (collectedPowerup != null) {
				switch (collectedPowerup.type) {
					case HEALTH:
						// Increase player health, capping at MAX_PLAYER_HEALTH
						playerHealth = Math.min(playerHealth + 1, MAX_PLAYER_HEALTH);
						break;
					case INVINCIBILITY:
						// Activate invincibility and set its duration
						isInvincible = true;
						invincibilityRemainingTime = MAX_INVINCIBILITY_DURATION_MILLIS;
						break;
					case FREEZE_ENEMIES:
						// Activate enemy freeze and set its duration
						areEnemiesFrozen = true;
						enemyFreezeRemainingTime = MAX_ENEMY_FREEZE_DURATION_MILLIS;
						break;
				}
				powerups.remove(collectedPowerup); // Remove the collected power-up from the world
			}

			// Check if the player reached the end point (level completion)
			if (playerX == endX && playerY == endY) {
				currentLevel++; // Increment the current level
				// Restart the level with the current difficulty, but do NOT reset player health
				restart(this.difficulty, false);
				return; // Exit method as a new level has started
			}

			// If player encounters an enemy at the new position and is not invincible, lose health
			if(isEnemyAt(getPlayerX(),getPlayerY()) && !isInvincible){
				playerHealth--;
				if (playerHealth <= 0) {
					this.gameOver = true; // Game over if health drops to 0 or below
				}
			}
		}
		updateViews(); // Notify all registered views about the world state change
	}

	/**
	 * Moves all enemies towards the player's current position.
	 * Enemies try to move horizontally or vertically towards the player,
	 * prioritizing one direction randomly if both are possible.
	 * They avoid moving into walls.
	 * also handles collisions between enemies and the player.
	 */
	public void moveEnemies() {
		// Enemies do not move if the game is paused, over, or if enemies are frozen by a power-up
		if (isPaused() || isGameOver() || areEnemiesFrozen) {
			return;
		}

		Random rand = new Random();

		// Iterate through each enemy
		for (Point enemy : enemies) {
			int currentEnemyX = enemy.x;
			int currentEnemyY = enemy.y;

			int targetX = playerX; // Player's X coordinate is the target
			int targetY = playerY; // Player's Y coordinate is the target

			// Calculate direction needed to move towards the player
			int deltaX = Integer.compare(targetX, currentEnemyX); // -1 (left), 0 (same), 1 (right)
			int deltaY = Integer.compare(targetY, currentEnemyY); // -1 (up), 0 (same), 1 (down)

			// Determine movement preference: try to move both horizontally and vertically if needed
			if (deltaX != 0 && deltaY != 0) {
				if (rand.nextBoolean()) { // Randomly choose to try horizontal or vertical first
					int potentialNewX = currentEnemyX + deltaX;
					if (!isWall(potentialNewX, currentEnemyY)) {
						enemy.setLocation(potentialNewX, currentEnemyY); // Move horizontally
					} else { // If horizontal path is blocked, try moving vertically
						int potentialNewY = currentEnemyY + deltaY;
						if (!isWall(currentEnemyX, potentialNewY)) {
							enemy.setLocation(currentEnemyX, potentialNewY); // Move vertically
						}
					}
				} else { // Try to move vertically first
					int potentialNewY = currentEnemyY + deltaY;
					if (!isWall(currentEnemyX, potentialNewY)) {
						enemy.setLocation(currentEnemyX, potentialNewY); // Move vertically
					} else { // If vertical path is blocked, try moving horizontally
						int potentialNewX = currentEnemyX + deltaX;
						if (!isWall(potentialNewX, currentEnemyY)) {
							enemy.setLocation(potentialNewX, currentEnemyY); // Move horizontally
						}
					}
				}
			} else if (deltaX != 0) { // Only horizontal movement is needed
				int potentialNewX = currentEnemyX + deltaX;
				if (!isWall(potentialNewX, currentEnemyY)) {
					enemy.setLocation(potentialNewX, currentEnemyY);
				}
			} else if (deltaY != 0) { // Only vertical movement is needed
				int potentialNewY = currentEnemyY + deltaY;
				if (!isWall(currentEnemyX, potentialNewY)) {
					enemy.setLocation(currentEnemyX, potentialNewY);
				}
			}

			// Collision check after the enemy has moved:
			// If an enemy lands on the player's position and the player is not invincible,
			// the player loses health.
			if (enemy.x == playerX && enemy.y == playerY && !isInvincible) {
				playerHealth--; // Decrease player health
				if (playerHealth <= 0) {
					this.gameOver = true; // Set game over if health reaches zero or below
				}
				break;
			}
		}
		updateViews(); // Notify all registered views about the world state change
	}


	// View Management

	/**
	 * Adds the given view to the list of registered views and immediately updates it once
	 * with the current state of the world.
	 * Once registered, the view will receive subsequent updates whenever the world's state changes.
	 *
	 * @param view The {@link View} object to be registered.
	 */
	public void registerView(View view) {
		views.add(view);
		view.update(this); // Initial update for the newly registered view
	}

	/**
	 * Notifies all registered views by calling their {@link View#update(World)} method.
	 * This should be called whenever the internal state of the World model changes
	 * and needs to be reflected in the visual display.
	 */
	private void updateViews() {
		// Iterate through all registered views and trigger their update method
		for (int i = 0; i < views.size(); i++) {
			views.get(i).update(this);
		}
	}

	/**
	 * Checks if a given coordinate in the world represents a wall.
	 * It also treats coordinates outside the world bounds as walls.
	 *
	 * @param x The X coordinate to check.
	 * @param y The Y coordinate to check.
	 * @return true if the coordinate is a wall or out of bounds, false otherwise (it's a path).
	 */
	public boolean isWall(int x, int y) {
		// Check if coordinates are within the valid bounds of the world grid
		if  (x >= 0 && x < width && y >= 0 && y < height) {
			return walls[x][y]; // Return true if the cell at (x,y) is marked as a wall
		}
		return true; // Treat any coordinates outside the world boundaries as impassable walls
	}

	/**
	 * Returns a string indicating the cardinal and intercardinal direction from the player's
	 * current position to the end point of the labyrinth.
	 *
	 * @return A string representing the direction (e.g., "North", "SouthEast", "Here").
	 */
	public String getDirectionToEnd() {
		// Calculate the difference in coordinates between the end point and the player
		int deltaX = endX - playerX;
		int deltaY = endY - playerY;


		StringBuilder direction = new StringBuilder();

		// Determine vertical direction
		if (deltaY < 0) {
			direction.append("North");
		} else if (deltaY > 0) {
			direction.append("South");
		}
		// Determine horizontal direction
		if (deltaX < 0) {
			direction.append("West");
		} else if (deltaX > 0) {
			direction.append("East");
		}
		return direction.toString();
	}
}
