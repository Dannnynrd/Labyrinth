package model;

import java.awt.event.KeyEvent; // Import KeyEvent

/**
 * Represents a direction in the game.
 */
public enum Direction {
	/** No movement. */
	NONE(0, 0),
	/** Up movement. */
	UP(0, -1),
	/** Down movement. */
	DOWN(0, 1),
	/** Left movement. */
	LEFT(-1, 0),
	/** Right movement. */
	RIGHT(1, 0);

	/** The amount to move in the X direction. */
	public final int deltaX;
	/** The amount to move in the Y direction. */
	public final int deltaY;

	/**
	 * Creates a new direction with the given movement values.
	 * * @param deltaX The amount to move in the X direction.
	 * @param deltaY The amount to move in the Y direction.
	 */
	private Direction(int deltaX, int deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	/**
	 * Returns the Direction corresponding to a given KeyEvent key code.
	 *
	 * @param keyCode The key code from a KeyEvent.
	 * @return The corresponding Direction, or NONE if no match.
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