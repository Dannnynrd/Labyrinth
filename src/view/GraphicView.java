package view;

import java.awt.*;
import javax.swing.JPanel;
import model.World;

/**
 * A graphical view of the world.
 */
public class GraphicView extends JPanel implements View {

	// Remove 'final' so these can be updated
	private int WIDTH;
	private int HEIGHT;
	private Dimension fieldDimension;

	private Rectangle bg;
	/** The rectangle we're moving. */
	private final Rectangle player = new Rectangle(1, 1);

	private World world;

	public GraphicView(int width, int height, Dimension fieldDimension) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.fieldDimension = fieldDimension;
		// Still initialize the background here for the first time
		this.bg = new Rectangle(WIDTH, HEIGHT);
	}

	/**
	 * Creates a new instance.
	 */
	@Override
	public void paint(Graphics g) {
		//Paint background
		g.setColor(Color.RED);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Paint End Point
		g.setColor(Color.BLUE);
		g.fillRect(world.getEndX() * fieldDimension.width,
				world.getEndY() * fieldDimension.height,
				fieldDimension.width,
				fieldDimension.height);

		for (int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				if (world.isWall(col, row)) {
					g.setColor(Color.DARK_GRAY);
					g.fillRect(col * fieldDimension.width, row * fieldDimension.height, fieldDimension.width, fieldDimension.height);
				}
			}
		}

		// Paint player
		g.setColor(Color.BLACK);
		g.fillRect(player.x, player.y, player.width, player.height);

		// Paint enemies
		g.setColor(Color.ORANGE);
		if (world.getEnemies() != null) {
			for (Point enemy : world.getEnemies()) {
				g.fillOval(
						enemy.x * fieldDimension.width,
						enemy.y * fieldDimension.height,
						fieldDimension.width,
						fieldDimension.height
				);
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

		// Update the view's internal dimensions based on the (new) world size.
		this.WIDTH = world.getWidth() * fieldDimension.width;
		this.HEIGHT = world.getHeight() * fieldDimension.height;
		this.bg.setSize(this.WIDTH, this.HEIGHT);

		// Update player's size and location
		player.setSize(fieldDimension);
		player.setLocation(
				(int) (world.getPlayerX() * fieldDimension.width),
				(int) (world.getPlayerY() * fieldDimension.height)
		);

		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		if (world == null) {
			// Fallback, falls die Welt noch nicht existiert
			return new Dimension(100, 100);
		}
		// Die bevorzugte Größe basiert auf den aktuellen Dimensionen der Welt
		return new Dimension(world.getWidth() * fieldDimension.width,
				world.getHeight() * fieldDimension.height);
	}
}