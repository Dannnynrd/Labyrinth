package model;

import java.util.ArrayList;

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
	private int startX = 0;
	private int startY = 0;

	/** End Block */
	private int EndX = 0;
	private int EndY = 0;

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
		this.startX = 0;
		this.startY = 0;
		this.EndX = 9;
		this.EndY = 9; // The wall overrides the ENDPOINT
		walls[1][0] = true;
		walls[1][1] = true;
		walls[1][2] = true;
		walls[1][3] = true;
		walls[1][4] = true;
		walls[1][5] = true;

		walls[3][2] = true;
		walls[3][3] = true;
		walls[3][4] = true;
		walls[3][5] = true;
		walls[3][6] = true;
		walls[3][7] = true;

		walls[5][0] = true;
		walls[5][1] = true;
		walls[5][2] = true;
		walls[5][3] = true;
		walls[5][4] = true;

		walls[7][8] = true;
		walls[7][7] = true;
		walls[7][6] = true;
		walls[7][5] = true;
		walls[7][4] = true;
		walls[7][3] = true;

		walls[8][3] = true;
		walls[9][3] = true;
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
		return EndX;
	}
	public int getEndY() {
		return EndY;
	}
	public int getStartX() {
		return startX;
	}
	public int getStartY() {
		return startY;
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
			return walls[y][x];
		}
		return true; //
	}
}
