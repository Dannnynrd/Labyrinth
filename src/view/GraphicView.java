package view;

import java.awt.*;

import javax.swing.JPanel;

import model.World;

/**
 * A graphical view of the world.
 */
public class GraphicView extends JPanel implements View {

	/** The view's width. */
	private final int WIDTH;
	/** The view's height. */
	private final int HEIGHT;

	private Dimension fieldDimension;

	public GraphicView(int width, int height, Dimension fieldDimension) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.fieldDimension = fieldDimension;
		this.bg = new Rectangle(WIDTH, HEIGHT);
	}

	/** The background rectangle. */
	private final Rectangle bg;
	/** The rectangle we're moving. */
	private final Rectangle player = new Rectangle(1, 1);

	/**
	 * Creates a new instance.
	 */
	@Override
	public void paint(Graphics g) {
		// Paint background
		g.setColor(Color.RED);
		g.fillRect(bg.x, bg.y, bg.width, bg.height);

		// Paint End Point
		g.setColor(Color.BLUE);
		g.fillRect(world.getEndX() * fieldDimension.width,
				   world.getEndY() * fieldDimension.height, fieldDimension.width
				, fieldDimension.height);

		for(int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				if(world.isWall(col,row)){
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
		if(world.getEnemies() != null){
			for(Point enemy: world.getEnemies()){
				g.fillOval(
						enemy.x * fieldDimension.width,
						enemy.y * fieldDimension.height,
						fieldDimension.width,
						fieldDimension.height
				);
			}
		}

		if (world.isGameOver()){
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, getWidth(), getHeight());

			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			String msg = "GAME OVER";
			int textWidth = g.getFontMetrics().stringWidth(msg);
			g.drawString(msg, (getWidth() - textWidth) / 2, getHeight() / 2);
		}
	}
	private World world;

	@Override
	public void update(World world) {
		this.world = world;
		// Update players size and location
		player.setSize(fieldDimension);
		player.setLocation(
			(int) (world.getPlayerX() * fieldDimension.width),
			(int) (world.getPlayerY() * fieldDimension.height)
		);
		repaint();
	}

}
