package controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.Timer;

import model.Direction;
import model.Difficulty;
import model.World;
import view.GraphicView;
import view.InGameMenu;

/**
 * Our controller listens for key events on the main window.
 */
public class Controller extends JFrame implements KeyListener, ActionListener, MouseListener {

	/** The world that is updated upon every key press. */
	private final World world;
	private final Dimension fieldDimensions;
	private Timer enemyMoveTimer;
	private InGameMenu inGameMenu;
	private JLayeredPane layeredPane;
	private GraphicView graphicView;

	/**
	 * Creates a new instance.
	 *
	 * @param world the world to be updated whenever the player should move.
	 * @param fieldDimensions the dimensions of each field in the graphical view.
	 * @param graphicView the graphical view to be displayed and updated.
	 */
	public Controller(World world, Dimension fieldDimensions, GraphicView graphicView) {
		this.world = world;
		this.fieldDimensions = fieldDimensions;
		this.graphicView = graphicView;

		setLayout(new BorderLayout());

		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(graphicView.getPreferredSize());

		this.graphicView.setBounds(0, 0, graphicView.getPreferredSize().width, graphicView.getPreferredSize().height);
		layeredPane.add(this.graphicView, JLayeredPane.DEFAULT_LAYER);

		inGameMenu = new InGameMenu();
		int menuWidth = inGameMenu.getPreferredSize().width;
		int menuHeight = inGameMenu.getPreferredSize().height;
		int x = (graphicView.getPreferredSize().width - menuWidth) / 2;
		int y = (graphicView.getPreferredSize().height - menuHeight) / 2;
		inGameMenu.setBounds(x, y, menuWidth, menuHeight);
		inGameMenu.setVisible(false);

		layeredPane.add(inGameMenu, JLayeredPane.PALETTE_LAYER);

		this.add(layeredPane, BorderLayout.CENTER);

		enemyMoveTimer = new Timer((int) world.getEnemyMoveIntervalMillis(), e -> {
			world.moveEnemies();
			if (world.isGameOver()) {
				enemyMoveTimer.stop();
			}
		});
		enemyMoveTimer.start();

		inGameMenu.addResumeButtonListener(e -> handleResumeGame());
		inGameMenu.addRestartButtonListener(e -> handleRestartGame());
		inGameMenu.addExitButtonListener(e -> handleExitGame());
		inGameMenu.addDifficultyComboBoxListener(e -> {
			if ("comboBoxChanged".equals(e.getActionCommand())) {
				handleDifficultyChange();
			}
		});

		// Set the controller as the action listener for the GraphicView's restart button
		graphicView.setRestartButtonListener(e -> handleRestartGame()); // IMPORTANT: This links the game over restart button

		addKeyListener(this);
		addMouseListener(this);

		setFocusable(true);
		requestFocusInWindow();
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	/////////////////// Key Events ////////////////////////////////

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				if (!world.isPaused() && !world.isGameOver()) { // Ensure player can't move if game is over
					world.movePlayer(Direction.fromKeyCode(e.getKeyCode()));
				}
				break;
			case KeyEvent.VK_ESCAPE:
				if (!world.isGameOver()) {
					boolean newPausedState = !world.isPaused();
					world.setPaused(newPausedState);
					inGameMenu.setVisible(newPausedState);
					if (newPausedState) {
						enemyMoveTimer.stop();
						inGameMenu.setSelectedDifficulty(world.getDifficulty().name());
					} else {
						enemyMoveTimer.start();
					}
					graphicView.repaint();
				}
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { }

	/////////////////// Action Events ////////////////////////////////

	@Override
	public void actionPerformed(ActionEvent e) {
		// This method is now empty as the bottom restart button was removed
	}

	private void handleResumeGame() {
		world.setPaused(false);
		enemyMoveTimer.start();
		inGameMenu.setVisible(false);
		requestFocusInWindow();
		graphicView.repaint();
	}

	private void handleRestartGame() {
		// When restarting, use the difficulty currently set in the world (if game over)
		// or the one selected in the menu (if menu was open)
		String selectedDifficultyStr = inGameMenu.isVisible() ? inGameMenu.getSelectedDifficulty() : world.getDifficulty().name();
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);

		// Reset level to 1 when restarting the game from game over or restart menu
		// (This ensures a fresh start for the level progression)
		world.setCurrentLevel(1); // Direct access for simplicity, consider a public setter in World if preferred

		world.restart(newDifficulty); // Restart the world with chosen difficulty

		enemyMoveTimer.stop();
		enemyMoveTimer.setInitialDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.setDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.start();
		world.setPaused(false); // Ensure game is unpaused
		inGameMenu.setVisible(false); // Hide in-game menu
		pack(); // Adjust frame size if world size changed
		requestFocusInWindow(); // Restore focus to game
		graphicView.repaint(); // Repaint game view
	}

	private void handleExitGame() {
		System.exit(0);
	}

	private void handleDifficultyChange() {
		String selectedDifficultyStr = inGameMenu.getSelectedDifficulty();
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);
		if (world.getDifficulty() != newDifficulty) {
			handleRestartGame(); // Restart the game with the new difficulty
		}
	}

	/////////////////// Mouse Events ////////////////////////////////
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}