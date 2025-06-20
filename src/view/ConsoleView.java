package view;

import model.World;
import model.Powerup;
import model.PowerupType;

/**
 * A simple console-based view of the Labyrinth game world.
 * This view prints a textual representation of the game state to the console
 * upon every update from the {@link World} model. It shows as much information as possible in the console view.
 */
public class ConsoleView implements View {

	/**
	 * Updates the console view based on the current state of the game world.
	 *
	 * Character Legend:
	 * P : Player (normal)
	 * @ : Player (invincible)
	 * E : Enemy (normal)
	 * F : Enemy (frozen)
	 * H : Health Power-up
	 * I : Invincibility Power-up
	 * Z : Freeze Enemies Power-up
	 * X : End Point
	 * # : Wall
	 * . : Path/Floor
	 *
	 * @param world The {@link World} object representing the current game state.
	 */
	@Override
	public void update(World world) {

		// Get player, end
		int playerX = world.getPlayerX();
		int playerY = world.getPlayerY();
		int endX = world.getEndX();
		int endY = world.getEndY();

		// --- Draw the game map ---
		for (int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				char charToDraw = '.'; // Default to path/floor

				// Determine character based on object hierarchy (player > enemy > powerup > end > wall)
				if (col == playerX && row == playerY) {
					// Player takes highest priority
					charToDraw = world.isInvincible() ? '@' : 'P'; // Player (P) or Invincible Player (@)
				} else if (world.isEnemyAt(col, row)) {
					// Check if it's an enemy, and if enemies are frozen
					charToDraw = world.areEnemiesFrozen() ? 'F' : 'E'; // Frozen Enemy (F) or normal Enemy (E)
				} else if (world.isPowerupAt(col, row)) {
					// Check for power-ups
					Powerup p = world.getPowerupAt(col, row);
					if (p != null) {
						switch (p.type) {
							case HEALTH:
								charToDraw = 'H'; // Health Powerup
								break;
							case INVINCIBILITY:
								charToDraw = 'I'; // Invincibility Powerup
								break;
							case FREEZE_ENEMIES:
								charToDraw = 'Z'; // Freeze Enemies Powerup
								break;
						}
					}
				} else if (col == endX && row == endY) {
					charToDraw = 'X'; // End Point
				} else if (world.isWall(col, row)) {
					charToDraw = '#'; // Wall
				}

				System.out.print(charToDraw + " "); // Print character and a space for readability
			}
			System.out.println(); // Newline after each row
		}

		// --- Display game information ---
		System.out.println("\n--- Game Info ---");
		System.out.println("Level: " + world.getCurrentLevel());
		System.out.println("Health: " + world.getPlayerHealth() + "/" + world.getMaxPlayerHealth());
		System.out.println("Goal: " + world.getDirectionToEnd());

		// --- Debug game information ---
		System.out.println("\n--- Debug Info ---");
		System.out.println("Player Coordinates: (" + world.getPlayerX() + ", " + world.getPlayerY() + ")");
		System.out.println("End Coords: (" + world.getEndX() + ", " + world.getEndY() + ")");
		System.out.println("Maze Size: " + world.getWidth() + "x" + world.getHeight());
		System.out.println("Difficulty: " + world.getDifficulty().name());
		System.out.println("Enemies: " + world.getEnemies().size());
		System.out.println("Power-ups Remaining: " + world.getPowerups().size());
		System.out.println("Enemy Speed (ms): " + world.getEnemyMoveIntervalMillis());
		int distanceToEnd = Math.abs(playerX - endX) + Math.abs(playerY - endY);
		System.out.println("Distance to Goal: " + distanceToEnd + " steps");


		// Display power-up statuses if active
		if (world.isInvincible()) {
			System.out.println("Invincible: Active!");
		}
		if (world.areEnemiesFrozen()) {
			System.out.println("Enemies Frozen: Active!");
		}

		// Display Game Over message
		if (world.isGameOver()) {
			System.out.println("\n--- GAME OVER ---");
			System.out.println("You reached Level: " + world.getCurrentLevel());
		} else if (world.isPaused()) {
			System.out.println("\n--- GAME PAUSED ---");
		}

		// Separator for next update to make it clearer in the console
		System.out.println("\n----------------------\n");
	}
}
