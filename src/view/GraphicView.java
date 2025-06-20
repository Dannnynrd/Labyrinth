package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JPanel;


import model.World;
import model.Powerup;
import model.PowerupType;

/**
 * A graphical view component that displays the Labyrinth game world.
 * It renders the maze, player, enemies, end point, and power-ups using images or default colors.
 * The view includes a viewport that follows the player, showing only a portion of the larger world.
 * It also displays game information like current level, player health, and direction to the goal.
 */
public class GraphicView extends JPanel implements View {

	/** The dimension (width and height) of a single tile/field in pixels. */
	private Dimension fieldDimension;
	/** The width of the visible game area (viewport) in number of tiles. */
	private static final int VIEWPORT_WIDTH_IN_TILES = 25;
	/** The height of the visible game area (viewport) in number of tiles. */
	private static final int VIEWPORT_HEIGHT_IN_TILES = 25;

	/** The World model instance that this view is currently rendering. */
	private World world;

	// Declare Images variables for various game assets
	private BufferedImage wallImage; // Image for the Walls
	private BufferedImage playerImage; // Image for the Player
	private BufferedImage enemyImage; // Image for the eneimes
	private BufferedImage endImage; // Images for the goal (end)
	private BufferedImage floorImage; // Image for the walkable path/background
	private BufferedImage healthPowerupImage; // Image for health power-up
	private BufferedImage invincibilityPowerupImage; // Image for invincibility power-up
	private BufferedImage freezePowerupImage; // Image for enemy freeze power-up


	/**
	 * Constructs a new GraphicView.
	 * @param fieldDimension The preferred pixel dimensions for each individual tile on the screen.
	 */
	public GraphicView(Dimension fieldDimension) {
		this.fieldDimension = fieldDimension;
		loadImages(); // Load all necessary images when the view is created
	}

	/**
	 * Loads image assets from the classpath.
	 * If an image cannot be loaded, the corresponding BufferedImage variable will remain null,
	 * and the paint method will fall back to drawing default colors/shapes.
	 */
	private void loadImages() {
		try {
			// Load images using getClass().getResource() for JAR-friendly path resolution
			wallImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/wallr.jpg")));
			playerImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/player.png")));
			enemyImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/enemy.png")));
			endImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/end2.png")));
			floorImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/floor2.png")));
			healthPowerupImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/powerup.png")));
			invincibilityPowerupImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/powerupboost.png")));
			freezePowerupImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/freeze.png")));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not load images!");
		}
	}

	/**
	 * Overrides the paint method to render the game world.
	 * This method is called automatically when the component needs to be repainted.
	 * It draws the visible portion of the labyrinth, including walls, paths, player, enemies,
	 * end point, power-ups, and overlays for game status (game over, paused, invincibility, frozen enemies).
	 *
	 * @param g The {@link Graphics} context used for drawing.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// If no world model is set, do nothing
		if (world == null) {
			return;
		}

		// Calculate the top-left tile coordinates of the viewport based on the player's position,
		// ensuring the player is centered or near-centered in the view.
		int cameraTileX = world.getPlayerX() - VIEWPORT_WIDTH_IN_TILES / 2;
		int cameraTileY = world.getPlayerY() - VIEWPORT_HEIGHT_IN_TILES / 2;

		// Iterate through each tile in the viewport
		for (int y = 0; y < VIEWPORT_HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < VIEWPORT_WIDTH_IN_TILES; x++) {
				// Calculate corresponding world coordinates for the current viewport tile
				int worldX = cameraTileX + x;
				int worldY = cameraTileY + y;

				// Calculate screen coordinates for drawing
				int screenX = x * fieldDimension.width;
				int screenY = y * fieldDimension.height;

				// Only draw game elements if the world coordinates are within the actual world bounds
				if (worldX >= 0 && worldX < world.getWidth() && worldY >= 0 && worldY < world.getHeight()) {
					// Draw floor/path first
					if (floorImage != null) {
						g.drawImage(floorImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
					} else {
						g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
					}

					// Draw walls
					if (world.isWall(worldX, worldY)) {
						if (wallImage != null) {
							g.drawImage(wallImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
					// Draw end point
					else if (worldX == world.getEndX() && worldY == world.getEndY()) {
						if (endImage != null) {
							g.drawImage(endImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}

					// Draw power-ups
					Powerup currentPowerup = world.getPowerupAt(worldX, worldY);
					if (currentPowerup != null) {
						BufferedImage powerupDrawImage = null;

						// Select the correct image based on power-up type
						switch (currentPowerup.type) {
							case HEALTH:
								powerupDrawImage = healthPowerupImage;
								break;
							case INVINCIBILITY:
								powerupDrawImage = invincibilityPowerupImage;
								break;
							case FREEZE_ENEMIES:
								powerupDrawImage = freezePowerupImage;
								break;
						}


							g.drawImage(powerupDrawImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);

					}

					// Draw enemies
					if (world.isEnemyAt(worldX, worldY)) {
						// Apply visual effect for frozen enemies
						if (world.areEnemiesFrozen()) {
							g.setColor(new Color(0, 200, 255, 150)); // Semi-transparent light blue overlay
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
							g.drawImage(enemyImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
					}
					// Draw player (player is drawn last to ensure it's on top of other elements)
					if (worldX == world.getPlayerX() && worldY == world.getPlayerY()) {
						// Apply visual effect for invincible player
						if (world.isInvincible()) {
							g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow overlay
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
							g.drawImage(playerImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
					}
				} else {
					// Draw outside world bounds ("space")
					g.setColor(Color.BLACK);
					g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);

					// Draw some "stars"
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

		// Draw Level indicator during gameplay (not on game over/paused screens)
		if (world.getCurrentLevel() > 0 && !world.isGameOver() && !world.isPaused()) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			String levelText = "Level: " + world.getCurrentLevel();
			g.drawString(levelText, 10, 25); // Positioned in the top-left corner

			// Draw health bar
			int healthBarX = 10;
			int healthBarY = 40;
			int healthBarWidth = 100;
			int healthBarHeight = 15;
			int healthSegmentWidth = healthBarWidth / world.getMaxPlayerHealth();

			g.setColor(Color.RED); // Background for empty health bar
			g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

			g.setColor(Color.GREEN); // Current health fill
			g.fillRect(healthBarX, healthBarY, world.getPlayerHealth() * healthSegmentWidth, healthBarHeight);

			g.setColor(Color.WHITE); // Border around the health bar
			g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);


			// Draw direction to end only when not game over and not paused
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			String directionText = "Goal: " + world.getDirectionToEnd();
			g.drawString(directionText, 10, 85); // Below Health Bar
		}


		// Draw game over or paused overlay
		if (world.isGameOver()) {
			g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black overlay
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			String gameOverMsg = "GAME OVER";
			int textWidth = g.getFontMetrics().stringWidth(gameOverMsg);
			g.drawString(gameOverMsg, (getWidth() - textWidth) / 2, getHeight() / 2 - 50); // Positioned higher

			// Display current level reached on game over screen
			g.setFont(new Font("Arial", Font.BOLD, 24));
			String levelMsg = "Level: " + world.getCurrentLevel();
			int levelTextWidth = g.getFontMetrics().stringWidth(levelMsg);
			g.drawString(levelMsg, (getWidth() - levelTextWidth) / 2, getHeight() / 2 + 20); // Positioned below "GAME OVER"

		} else if (world.isPaused()) { // Draw semi-transparent overlay when paused
			g.setColor(new Color(0, 0, 0, 100)); // Black with 40% opacity
			g.fillRect(0, 0, getWidth(), getHeight());

		}
	}

	/**
	 * This method is called by the {@link World} model whenever its state changes.
	 * It updates the internal {@code world} reference and triggers a repaint of the view
	 * to reflect the latest game state.
	 *
	 * @param world The updated {@link World} object.
	 */
	@Override
	public void update(World world) {
		this.world = world;
		repaint(); // Request a repaint of this JPanel
	}

	/**
	 * Returns the preferred size of this GraphicView panel.
	 * The preferred size is calculated based on the viewport dimensions in tiles
	 * and the pixel dimensions of each individual tile.
	 *
	 * @return A {@link Dimension} object representing the preferred width and height of the view.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(VIEWPORT_WIDTH_IN_TILES * fieldDimension.width, VIEWPORT_HEIGHT_IN_TILES * fieldDimension.height);
	}
}
