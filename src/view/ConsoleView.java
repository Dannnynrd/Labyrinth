package view;

import model.World;

/**
 * A view that prints the current state of the world to the console upon every
 * update.
 */
public class ConsoleView implements View {

	@Override
	public void update(World world) {
		// The player's position
		int playerX = world.getPlayerX();
		int playerY = world.getPlayerY();
		int EndX = world.getEndX();
		int EndY = world.getEndY();


		for (int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				/**
				 Player Position: #
				 Wall: +
				 Space: .
				 End: E
				 */

				if (row == playerY && col == playerX) {
					System.out.print("#");
				} else if (world.isWall(col, row)) {
					System.out.print("+");
				} else if (row == EndX && col == EndY) {
					System.out.print("E");
				} else {
					System.out.print(".");
				}
			}

			// A newline after every row
			System.out.println();
		}

		// A newline between every update
		System.out.println();
	}

}
