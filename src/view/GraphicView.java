// src/view/GraphicView.java
package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JPanel;


import model.World;
import model.Powerup; // Import the Powerup class
import model.PowerupType; // Import the PowerupType enum

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
	private BufferedImage healthPowerupImage; // NEW: For health power-up image
	private BufferedImage invincibilityPowerupImage; // NEW: For invincibility power-up image
	private BufferedImage freezePowerupImage; // NEW: For enemy freeze power-up image


	public GraphicView(Dimension fieldDimension) {
		this.fieldDimension = fieldDimension;
		loadImages(); // Load images when the view is created
	}

	// load the images
	private void loadImages() {
		try {
			wallImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/wallr.jpg")));
			playerImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/player.png")));
			enemyImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/enemy.png")));
			endImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/end2.png")));
			floorImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/floor2.png")));
			healthPowerupImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/powerup.png"))); // Using existing powerup.png for health
			// Assuming new images exist for special power-ups, otherwise use default colors
			invincibilityPowerupImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/powerupboost.png")));
			freezePowerupImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/freeze.png")));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not load images! Using default colors.");
			wallImage = null;
			playerImage = null;
			enemyImage = null;
			endImage = null;
			floorImage = null;
			healthPowerupImage = null;
			invincibilityPowerupImage = null;
			freezePowerupImage = null;
		}
	}

	/**
	 * Creates a new instance.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g); // Call super.paint to ensure proper painting of the panel

		if (world == null) {
			return;
		}

		int cameraTileX = world.getPlayerX() - VIEWPORT_WIDTH_IN_TILES / 2;
		int cameraTileY = world.getPlayerY() - VIEWPORT_HEIGHT_IN_TILES / 2;

		for (int y = 0; y < VIEWPORT_HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < VIEWPORT_WIDTH_IN_TILES; x++) {
				int worldX = cameraTileX + x;
				int worldY = cameraTileY + y;

				int screenX = x * fieldDimension.width;
				int screenY = y * fieldDimension.height;

				if (worldX >= 0 && worldX < world.getWidth() && worldY >= 0 && worldY < world.getHeight()) {
					if (floorImage != null) {
						g.drawImage(floorImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
					} else {
						g.setColor(Color.LIGHT_GRAY);
						g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
					}

					if (world.isWall(worldX, worldY)) {
						if (wallImage != null) {
							g.drawImage(wallImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.DARK_GRAY);
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					} else if (worldX == world.getEndX() && worldY == world.getEndY()) {
						if (endImage != null) {
							g.drawImage(endImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.BLUE);
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
					// NEW: Draw power-ups based on their type
					Powerup currentPowerup = world.getPowerupAt(worldX, worldY);
					if (currentPowerup != null) {
						BufferedImage powerupDrawImage = null;
						Color defaultColor = Color.MAGENTA; // Default for unknown type
						switch (currentPowerup.type) {
							case HEALTH:
								powerupDrawImage = healthPowerupImage;
								defaultColor = Color.GREEN;
								break;
							case INVINCIBILITY:
								powerupDrawImage = invincibilityPowerupImage;
								defaultColor = Color.YELLOW;
								break;
							case FREEZE_ENEMIES:
								powerupDrawImage = freezePowerupImage;
								defaultColor = Color.CYAN;
								break;
						}

						if (powerupDrawImage != null) {
							g.drawImage(powerupDrawImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(defaultColor);
							g.fillOval(screenX + fieldDimension.width/4, screenY + fieldDimension.height/4, fieldDimension.width/2, fieldDimension.height/2);
						}
					}

					if (world.isEnemyAt(worldX, worldY)) {
						// NEW: Apply visual effect for frozen enemies
						if (world.areEnemiesFrozen()) {
							g.setColor(new Color(0, 200, 255, 150)); // Semi-transparent light blue overlay for frozen
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}

						if (enemyImage != null) {
							g.drawImage(enemyImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.ORANGE);
							g.fillOval(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
					if (worldX == world.getPlayerX() && worldY == world.getPlayerY()) {
						// NEW: Apply visual effect for invincible player
						if (world.isInvincible()) {
							g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow overlay for invincibility
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
						if (playerImage != null) {
							g.drawImage(playerImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.BLACK);
							g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
				} else {
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

		// Draw Level indicator during gameplay
		if (world.getCurrentLevel() > 0 && !world.isGameOver() && !world.isPaused()) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			String levelText = "Level: " + world.getCurrentLevel();
			g.drawString(levelText, 10, 25); // Top-left corner

			// NEW: Draw health bar
			int healthBarX = 10;
			int healthBarY = 40;
			int healthBarWidth = 100;
			int healthBarHeight = 15;
			int healthSegmentWidth = healthBarWidth / world.getMaxPlayerHealth();

			g.setColor(Color.RED); // Background for empty health
			g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

			g.setColor(Color.GREEN); // Current health
			g.fillRect(healthBarX, healthBarY, world.getPlayerHealth() * healthSegmentWidth, healthBarHeight);

			g.setColor(Color.WHITE); // Border for health bar
			g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

			// Optionally, draw current health text on top of the bar or next to it
			g.drawString("HP", healthBarX + healthBarWidth + 5, healthBarY + healthBarHeight - 2);

			// Draw current health value as text
			String healthValueText = world.getPlayerHealth() + "/" + world.getMaxPlayerHealth();
			g.drawString(healthValueText, healthBarX + (healthBarWidth / 2) - (g.getFontMetrics().stringWidth(healthValueText) / 2), healthBarY + healthBarHeight - 2);


		}


		// Draw game over or paused overlay
		if (world.isGameOver()) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			String gameOverMsg = "GAME OVER";
			int textWidth = g.getFontMetrics().stringWidth(gameOverMsg);
			g.drawString(gameOverMsg, (getWidth() - textWidth) / 2, getHeight() / 2 - 50); // Positioned higher

			// Display current level on game over screen
			g.setFont(new Font("Arial", Font.BOLD, 24));
			String levelMsg = "Level: " + world.getCurrentLevel();
			int levelTextWidth = g.getFontMetrics().stringWidth(levelMsg);
			g.drawString(levelMsg, (getWidth() - levelTextWidth) / 2, getHeight() / 2 + 20); // Positioned below "GAME OVER"


		} else if (world.isPaused()) { // Draw semi-transparent overlay when paused
			g.setColor(new Color(0, 0, 0, 100)); // Black with 40% opacity
			g.fillRect(0, 0, getWidth(), getHeight());

		} else {
			// Draw direction to end only when not game over and not paused
			g.setColor(Color.YELLOW); // Choose a color that stands out
			g.setFont(new Font("Arial", Font.BOLD, 20));
			String directionText = "Goal: " + world.getDirectionToEnd();
			g.drawString(directionText, 10, 75); // Positioned below Level and Health Bar
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