package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.World;

/**
 * A graphical view of the world.
 */
public class GraphicView extends JPanel implements View {

	private Dimension fieldDimension;
	// Field that the user can see
	private static final int VIEWPORT_WIDTH_IN_TILES = 25;
	private static final int VIEWPORT_HEIGHT_IN_TILES = 25;

	private World world;

	// Declare BufferedImage variables for the assets
	private BufferedImage wallImage;
	private BufferedImage playerImage;
	private BufferedImage enemyImage;
	private BufferedImage endImage;
	private BufferedImage floorImage; // For the walkable path/background

	public GraphicView(Dimension fieldDimension) {
		this.fieldDimension = fieldDimension;
		loadImages(); // Load images when the view is created
	}

	// load the images
	private void loadImages() {
		try {
			wallImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/wall.jpg"))); // <-- HIER: wall.png zu wall.jpg geändert
			playerImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/player.png")));
			enemyImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/enemy.png")));
			endImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/end.jpg")));
			floorImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/floor.jpg"))); // Load floor image
		} catch (IOException e) {
			e.printStackTrace();
			// Handle the error show an error message
			System.err.println("Could not load images! Using default colors.");
			wallImage = null; // Set to null to indicate image loading failure
			playerImage = null;
			enemyImage = null;
			endImage = null;
			floorImage = null;
		}
	}

	/**
	 * Creates a new instance.
	 */
	@Override
	public void paint(Graphics g) {

		if (world == null) {
			return;
		}
		int cameraTileX = world.getPlayerX() - VIEWPORT_WIDTH_IN_TILES / 2;
		int cameraTileY = world.getPlayerY() - VIEWPORT_HEIGHT_IN_TILES / 2;

		// 2. Loop through each tile of the viewport (not the whole world!)
		for (int y = 0; y < VIEWPORT_HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < VIEWPORT_WIDTH_IN_TILES; x++) {
				// The coordinate in the world that we want to draw
				int worldX = cameraTileX + x;
				int worldY = cameraTileY + y;

				// The position on the screen (in pixels)
				int screenX = x * fieldDimension.width;
				int screenY = y * fieldDimension.height;

				// 3. Check if the world coordinate is within the labyrinth bounds
				if (worldX >= 0 && worldX < world.getWidth() && worldY >= 0 && worldY < world.getHeight()) {
					// We are inside the Labyrinth

					// Draw the floor image first as a background for every tile within the labyrinth
					if (floorImage != null) {
						g.drawImage(floorImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
					} else {
						g.setColor(Color.LIGHT_GRAY); // Fallback color for floor
						g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
					}

					// Wall?
					if (world.isWall(worldX, worldY)) {
						if (wallImage != null) {
							g.drawImage(wallImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.DARK_GRAY); // Fallback color
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
					// End point?
					else if (worldX == world.getEndX() && worldY == world.getEndY()) { // Use else if to prioritize drawing the end over the floor if they overlap
						if (endImage != null) {
							g.drawImage(endImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.BLUE); // Fallback color
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
					// Enemy?
					if (world.isEnemyAt(worldX, worldY)) { // Can be on top of floor or end
						if (enemyImage != null) {
							g.drawImage(enemyImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.ORANGE); // Fallback color
							g.fillOval(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
					// Player? (Draw last to ensure it's on top of everything else)
					if (worldX == world.getPlayerX() && worldY == world.getPlayerY()) {
						if (playerImage != null) {
							g.drawImage(playerImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.BLACK); // Fallback color
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
				} else { // When User sees out of the world (e.g., black space with stars)
					g.setColor(Color.BLACK);
					g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);

					g.setColor(Color.WHITE);
					if ((worldX * worldX + worldY * worldY) % 31 == 0) {
						g.fillRect(screenX + 8, screenY + 8, 3, 3);
					}
					if ((worldX * 5 + worldY * 3) % 17 == 0) {
						g.fillOval(screenX + 18, screenY + 15, 2, 2);
					}
				}
			}
		}

		if (world.isGameOver()) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			String msg = "GAME OVER";
			int textWidth = g.getFontMetrics().stringWidth(msg);
			g.drawString(msg, (getWidth() - textWidth) / 2, getHeight() / 2);
		}
	}

	@Override
	public void update(World world) {
		this.world = world;
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(VIEWPORT_WIDTH_IN_TILES * fieldDimension.width, VIEWPORT_HEIGHT_IN_TILES * fieldDimension.height);
	}
}