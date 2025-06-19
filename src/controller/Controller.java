// src/controller/Controller.java
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
import view.MainMenu;

/**
 * Our controller listens for key events on the main window.
 */
public class Controller extends JFrame implements KeyListener, ActionListener, MouseListener {

	/** The world that is updated upon every key press. */
	private World world;
	private final Dimension fieldDimensions;
	private Timer enemyMoveTimer;
	private InGameMenu inGameMenu;
	private JLayeredPane layeredPane;
	private GraphicView graphicView;
	private MainMenu mainMenu;

	private JButton gameOverRestartButton;

	/**
	 * Creates a new instance.
	 *
	 * @param initialWorld the world to be updated whenever the player should move.
	 * @param fieldDimensions the dimensions of each field in the graphical view.
	 * @param graphicView the graphical view to be displayed and updated.
	 */
	public Controller(World initialWorld, Dimension fieldDimensions, GraphicView graphicView) {
		this.world = initialWorld;
		this.fieldDimensions = fieldDimensions;
		this.graphicView = graphicView;

		setLayout(new BorderLayout());

		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(graphicView.getPreferredSize());

		// Set the background of the layered pane to black and make it opaque
		layeredPane.setBackground(Color.BLACK);
		layeredPane.setOpaque(true);

		this.graphicView.setBounds(0, 0, graphicView.getPreferredSize().width, graphicView.getPreferredSize().height);
		layeredPane.add(this.graphicView, JLayeredPane.DEFAULT_LAYER);
		this.graphicView.setVisible(false); // Initially hide GraphicView, show MainMenu instead

		mainMenu = new MainMenu();
		int menuWidth = mainMenu.getPreferredSize().width;
		int menuHeight = mainMenu.getPreferredSize().height;
		int x = (graphicView.getPreferredSize().width - menuWidth) / 2;
		int y = (graphicView.getPreferredSize().height - menuHeight) / 2;
		mainMenu.setBounds(x, y, menuWidth, menuHeight);
		mainMenu.setVisible(true);
		layeredPane.add(mainMenu, JLayeredPane.MODAL_LAYER);

		inGameMenu = new InGameMenu();
		menuWidth = inGameMenu.getPreferredSize().width;
		menuHeight = inGameMenu.getPreferredSize().height;
		x = (graphicView.getPreferredSize().width - menuWidth) / 2;
		y = (graphicView.getPreferredSize().height - menuHeight) / 2;
		inGameMenu.setBounds(x, y, menuWidth, menuHeight);
		inGameMenu.setVisible(false);

		layeredPane.add(inGameMenu, JLayeredPane.PALETTE_LAYER);

		gameOverRestartButton = new JButton("Restart Game");
		gameOverRestartButton.setFont(new Font("Arial", Font.BOLD, 24));
		gameOverRestartButton.setBackground(new Color(70, 130, 180));
		gameOverRestartButton.setForeground(Color.BLACK);
		gameOverRestartButton.setFocusPainted(false);
		gameOverRestartButton.setVisible(false);

		int buttonWidth = 200;
		int buttonHeight = 50;
		int buttonX = (graphicView.getPreferredSize().width - buttonWidth) / 2;
		int buttonY = (graphicView.getPreferredSize().height / 2) + 70;
		gameOverRestartButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		layeredPane.add(gameOverRestartButton, JLayeredPane.POPUP_LAYER);

		this.add(layeredPane, BorderLayout.CENTER);

		enemyMoveTimer = new Timer(1, e -> {
			world.moveEnemies();
			if (world.isGameOver()) {
				enemyMoveTimer.stop();
				gameOverRestartButton.setVisible(true);
			}
		});

		inGameMenu.addResumeButtonListener(e -> handleResumeGame());
		inGameMenu.addRestartButtonListener(e -> handleRestartGame());
		inGameMenu.addExitButtonListener(e -> handleExitGame());
		inGameMenu.addDifficultyComboBoxListener(e -> {
			if ("comboBoxChanged".equals(e.getActionCommand())) {
				handleDifficultyChange();
			}
		});

		gameOverRestartButton.addActionListener(e -> handleRestartGame());

		mainMenu.addStartButtonListener(e -> handleStartGameFromMenu());

		addKeyListener(this);
		addMouseListener(this);

		setFocusable(true);
		requestFocusInWindow();
	}

	private void handleStartGameFromMenu() {
		Difficulty selectedDifficulty = Difficulty.valueOf(mainMenu.getSelectedDifficulty());
		this.world = new World(selectedDifficulty);
		this.world.registerView(graphicView);

		mainMenu.setVisible(false);
		graphicView.setVisible(true);
		this.world.setPaused(false);
		enemyMoveTimer.setInitialDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.setDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.start();
		requestFocusInWindow();
		graphicView.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) {
		if (mainMenu.isVisible()) {
			return;
		}

		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				if (!world.isPaused() && !world.isGameOver()) {
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
						gameOverRestartButton.setVisible(false);
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

	@Override
	public void actionPerformed(ActionEvent e) { }

	private void handleResumeGame() {
		world.setPaused(false);
		enemyMoveTimer.start();
		inGameMenu.setVisible(false);
		requestFocusInWindow();
		graphicView.repaint();
	}

	private void handleRestartGame() {
		String selectedDifficultyStr = inGameMenu.isVisible() ? inGameMenu.getSelectedDifficulty() : world.getDifficulty().name();
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);

		this.world = new World(newDifficulty);
		this.world.registerView(graphicView);

		enemyMoveTimer.stop();
		enemyMoveTimer.setInitialDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.setDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.start();
		world.setPaused(false);
		inGameMenu.setVisible(false);
		gameOverRestartButton.setVisible(false);
		pack();
		requestFocusInWindow();
		graphicView.repaint();
	}

	private void handleExitGame() {
		System.exit(0);
	}

	private void handleDifficultyChange() {
		String selectedDifficultyStr = inGameMenu.getSelectedDifficulty();
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);
		if (world.getDifficulty() != newDifficulty) {
			handleRestartGame();
		}
	}

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