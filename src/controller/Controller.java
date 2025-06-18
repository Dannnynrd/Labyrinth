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
import model.Difficulty; // Import Difficulty
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

		// Add listener for difficulty combo box
		inGameMenu.addDifficultyComboBoxListener(e -> {
			if ("comboBoxChanged".equals(e.getActionCommand())) { // Check for combo box change event
				handleDifficultyChange();
			}
		});


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
				if (!world.isPaused()) {
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
						// When showing the menu, update the selected difficulty in the dropdown
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

	}

	private void handleResumeGame() {
		world.setPaused(false);
		enemyMoveTimer.start();
		inGameMenu.setVisible(false);
		requestFocusInWindow();
		graphicView.repaint();
	}

	private void handleRestartGame() {
		// Get the currently selected difficulty from the menu for restart
		String selectedDifficultyStr = inGameMenu.getSelectedDifficulty();
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);
		world.restart(newDifficulty); // Restart with the selected difficulty

		enemyMoveTimer.stop();
		enemyMoveTimer.setInitialDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.setDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.start();
		world.setPaused(false);
		inGameMenu.setVisible(false);
		pack();
		requestFocusInWindow();
		graphicView.repaint();
	}

	private void handleExitGame() {
		System.exit(0);
	}

	// New method to handle difficulty change from dropdown
	private void handleDifficultyChange() {
		String selectedDifficultyStr = inGameMenu.getSelectedDifficulty();
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);
		// If the difficulty changes while paused, the user might expect a restart
		// You can add a confirmation dialog here if desired.
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