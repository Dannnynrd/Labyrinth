package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JButton; // ADDED THIS IMPORT
import java.awt.event.ActionListener; // ADDED THIS IMPORT


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

	// New: Restart Button
	private JButton restartButton;

	public GraphicView(Dimension fieldDimension) {
		this.fieldDimension = fieldDimension;
		loadImages(); // Load images when the view is created

		// Initialize Restart Button (initially hidden)
		restartButton = new JButton("Restart");
		restartButton.setVisible(false);
		restartButton.setFocusable(false); // Remove focus border
		restartButton.setFont(new Font("Arial", Font.BOLD, 24));
		restartButton.setBackground(new Color(70, 130, 180)); // SteelBlue
		restartButton.setForeground(Color.WHITE);
		setLayout(null); // Use absolute layout to position the button
		add(restartButton); // Add the button to the panel
	}

	// Method to set the controller as the action listener for the restart button
	public void setRestartButtonListener(ActionListener listener) {
		restartButton.addActionListener(listener);
	}

	// load the images
	private void loadImages() {
		try {
			wallImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/wall.jpg")));
			playerImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/player.png")));
			enemyImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/enemy.png")));
			endImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/end.jpg")));
			floorImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/floor.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not load images! Using default colors.");
			wallImage = null;
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
					if (world.isEnemyAt(worldX, worldY)) {
						if (enemyImage != null) {
							g.drawImage(enemyImage, screenX, screenY, fieldDimension.width, fieldDimension.height, null);
						} else {
							g.setColor(Color.ORANGE);
							g.fillOval(screenX, screenY, fieldDimension.width, fieldDimension.height);
						}
					}
					if (worldX == world.getPlayerX() && worldY == world.getPlayerY()) {
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

			// Display current level
			g.setFont(new Font("Arial", Font.BOLD, 24));
			String levelMsg = "Level: " + world.getCurrentLevel();
			int levelTextWidth = g.getFontMetrics().stringWidth(levelMsg);
			g.drawString(levelMsg, (getWidth() - levelTextWidth) / 2, getHeight() / 2 + 20); // Positioned below "GAME OVER"

			// Position and show the restart button
			int buttonWidth = 150;
			int buttonHeight = 40;
			restartButton.setBounds((getWidth() - buttonWidth) / 2, getHeight() / 2 + 70, buttonWidth, buttonHeight); // Position below level
			restartButton.setVisible(true);

		} else if (world.isPaused()) { // Draw semi-transparent overlay when paused
			g.setColor(new Color(0, 0, 0, 100)); // Black with 40% opacity
			g.fillRect(0, 0, getWidth(), getHeight());
			restartButton.setVisible(false); // Hide the restart button when paused
		} else {
			restartButton.setVisible(false); // Ensure button is hidden when not game over or paused
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