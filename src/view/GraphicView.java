package view;

import java.awt.*;
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

	private Rectangle bg; // Not used
	/**
	 * The rectangle we're moving.
	 */
	private final Rectangle player = new Rectangle(1, 1);

	private World world;

	public GraphicView(Dimension fieldDimension) {
		this.fieldDimension = fieldDimension;
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

		//Paint background


		// Paint End Point

		// 2. Schleife durch jede Kachel des Viewports (nicht der Welt!)
		for (int y = 0; y < VIEWPORT_HEIGHT_IN_TILES; y++) {
			for (int x = 0; x < VIEWPORT_WIDTH_IN_TILES; x++) {
				// Die Koordinate in der Welt, die wir zeichnen wollen
				int worldX = cameraTileX + x;
				int worldY = cameraTileY + y;

				// Die Position auf dem Bildschirm (in Pixeln)
				int screenX = x * fieldDimension.width;
				int screenY = y * fieldDimension.height;

				// 3. Prüfen, ob die Welt-Koordinate überhaupt im Labyrinth liegt
				if (worldX >= 0 && worldX < world.getWidth() && worldY >= 0 && worldY < world.getHeight()) {
					// We are in the Lybrinth

					// Hintergrund/Boden der Kachel
					g.setColor(Color.RED);
					g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);

					// Wand?
					if (world.isWall(worldX, worldY)) {
						g.setColor(Color.DARK_GRAY);
						g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
					}
					// Ziel?
					if (worldX == world.getEndX() && worldY == world.getEndY()) {
						g.setColor(Color.BLUE);
						g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
					}
					// Spieler?
					if (worldX == world.getPlayerX() && worldY == world.getPlayerY()) {
						g.setColor(Color.BLACK);
						g.fillRect(screenX, screenY, fieldDimension.width, fieldDimension.height);
					}
					// Gegner?
					if (world.isEnemyAt(worldX, worldY)) {
						g.setColor(Color.ORANGE);
						g.fillOval(screenX, screenY, fieldDimension.width, fieldDimension.height);
					}
				} else { // When User sees out of the world


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