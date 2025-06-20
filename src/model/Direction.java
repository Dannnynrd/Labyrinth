package model;

import java.awt.event.KeyEvent;

/**
 * Represents a direction in the game grid.
 * Each direction has corresponding delta values for X and Y coordinates.
 */
public enum Direction {
	/** No movement */
	NONE(0, 0),
	/** Upward movement (decreases Y coordinate). */
	UP(0, -1),
	/** Downward movement (increases Y coordinate). */
	DOWN(0, 1),
	/** Leftward movement (decreases X coordinate). */
	LEFT(-1, 0),
	/** Rightward movement (increases X coordinate). */
	RIGHT(1, 0);

	/** The amount to move in the X direction. */
	public final int deltaX;
	/** The amount to move in the Y direction. */
	public final int deltaY;

	/**
	 * Creates a new direction with the given movement values.
	 * @param deltaX The amount to change the X coordinate.
	 * @param deltaY The amount to change the Y coordinate.
	 */
	private Direction(int deltaX, int deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	/**
	 * Returns the Direction enum constant corresponding to a given KeyEvent key code.
	 * This is useful for mapping keyboard inputs to in-game movement directions.
	 *
	 * @param keyCode The key code from a KeyEvent (e.g., KeyEvent.VK_UP).
	 * @return The corresponding Direction, or {@link #NONE} if the key code does not
	 * map to a defined movement direction.
	 */
	public static Direction fromKeyCode(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_UP:
				return UP;
			case KeyEvent.VK_DOWN:
				return DOWN;
			case KeyEvent.VK_LEFT:
				return LEFT;
			case KeyEvent.VK_RIGHT:
				return RIGHT;
			default:
				return NONE;
		}
	}
}
