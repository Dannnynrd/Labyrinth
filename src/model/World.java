package model;

import java.util.ArrayList;
import java.util.Random;

import view.View;

/**
 * The world is our model. It saves the bare minimum of information required to
 * accurately reflect the state of the game. Note how this does not know
 * anything about graphics.
 */
public class World {

	/** The world's width. */
	private final int width;
	/** The world's height. */
	private final int height;
	/** The player's x position in the world. */
	private int playerX = 0;
	/** The player's y position in the world. */
	private int playerY = 0;
	/** walls */
	private boolean[][] walls;
	/** Start Block */


	/** End Block */
	private int endX = 0;
	private int endY = 0;

	/** Set of views registered to be notified of world updates. */
	private final ArrayList<View> views = new ArrayList<>();

	/**
	 * Creates a new world with the given size.t
	 */
	public World(int width, int height) {
		// Normally, we would check the arguments for proper values
		this.width = width;
		this.height = height;
		this.walls = new boolean[width][height];

		Random rand = new Random();

		// Random Position Player
		this.playerX = rand.nextInt(width);
		this.playerY = rand.nextInt(height);

		// Random EndPosition that is noot the Startposition

		do {
			this.endX = rand.nextInt(width);
			this.endY = rand.nextInt(height);
		} while (this.endX == this.playerX && this.endY == this.playerY);

		// Random Walls (30%)
		int numberOfWalls = (int) (width * height * 0.3);

		for (int i = 0; i < numberOfWalls; i++) {
			int wallX = rand.nextInt(width);
			int wallY = rand.nextInt(height);

			boolean isPlayerPos  = (wallX == this.playerX) && (wallY == this.playerY);
			boolean isEndPos = (wallX == this.endX) && (wallY == this.endY);

			if (!isPlayerPos && !isEndPos) {
				this.walls[wallX][wallY] = true;
			}
		}


	}

	///////////////////////////////////////////////////////////////////////////
	// Getters and Setters

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
		
		updateViews();
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
		
		updateViews();
	}

	public int getEndX() {
		return endX;
	}
	public int getEndY() {
		return endY;
	}


	///////////////////////////////////////////////////////////////////////////
	// Player Management
	
	/**
	 * Moves the player along the given direction.
	 * 
	 * @param direction where to move.
	 */

	public void movePlayer(Direction direction) {	
		// The direction tells us exactly how much we need to move along
		// every direction
		int newPlayerX = getPlayerX() + direction.deltaX;
		int newPlayerY = getPlayerY() + direction.deltaY;
		// Checks whether the Player is moving Towards a wall if there is a wall the player cant move to its direction
		if (!isWall(newPlayerX, newPlayerY)) {
			setPlayerX(newPlayerX);
			setPlayerY(newPlayerY);
		}
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
