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
import view.ConsoleView;
import view.GraphicView;
import view.InGameMenu;
import view.MainMenu;

/**
 * The Controller class acts as the mediator between the Model (World) and the View (GraphicView, MainMenu, InGameMenu).
 * It listens for user input (key events, mouse events, action events from UI components)
 * and translates them into actions that update the World model or change the view state.
 * It also manages game timers for enemy movement and power-up durations.
 */
public class Controller extends JFrame implements KeyListener, ActionListener, MouseListener {

	/** The world that is updated upon every key press. */
	private World world;
	/** The dimensions of each field (tile) in the graphical view. */
	private final Dimension fieldDimensions;
	/** Timer responsible for controlling enemy movement intervals. */
	private Timer enemyMoveTimer;
	/** Timer for general game updates, specifically power-up durations. */
	private Timer gameTimer;
	/** Stores the time of the last game update to calculate delta time for timers. */
	private long lastGameUpdateTime;

	/** The in-game menu, displayed when the game is paused. */
	private InGameMenu inGameMenu;
	/** A layered pane to manage the z-order of different UI components (game view, menus). */
	private JLayeredPane layeredPane;
	/** The graphical representation of the game world. */
	private GraphicView graphicView;
	/** The main menu, displayed when the application is started. */
	private MainMenu mainMenu;

	/** The Button displayed on the game over screen to restart the game. */
	private JButton gameOverRestartButton;
	private ConsoleView consoleView;
	/**
	 * Creates a new instance of the Controller.
	 * Sets up the main JFrame, initializes UI components, and configures game timers.
	 *
	 * @param initialWorld The initial world instance. This will be replaced when a new game starts from the main menu
	 * @param fieldDimensions The dimensions of each tile in the game grid, used by the GraphicView.
	 * @param graphicView The graphical view component that displays the game world
	 */
	public Controller(World initialWorld, Dimension fieldDimensions, GraphicView graphicView, ConsoleView consoleView) {
		this.world = initialWorld;
		this.fieldDimensions = fieldDimensions;
		this.graphicView = graphicView;
		this.consoleView = consoleView;

		// Set the layout of the JFrame to BorderLayout
		setLayout(new BorderLayout());

		// Initialize JLayeredPane to manage overlapping components
		layeredPane = new JLayeredPane();
		// Set preferred size of the layered pane based on the graphical view
		layeredPane.setPreferredSize(graphicView.getPreferredSize());

		// Set the background of the layered pane to black and make it opaque
		layeredPane.setBackground(Color.BLACK);
		layeredPane.setOpaque(true);

		// Configure the bounds for the graphic view within the layered pane
		this.graphicView.setBounds(0, 0, graphicView.getPreferredSize().width, graphicView.getPreferredSize().height);
		// Add graphic view to the default layer
		layeredPane.add(this.graphicView, JLayeredPane.DEFAULT_LAYER);
		// first hide GraphicView, because the MainMenu will be shown first
		this.graphicView.setVisible(false);

		// Initialize and position the main menu
		mainMenu = new MainMenu();
		int menuWidth = mainMenu.getPreferredSize().width;
		int menuHeight = mainMenu.getPreferredSize().height;
		int x = (graphicView.getPreferredSize().width - menuWidth) / 2;
		int y = (graphicView.getPreferredSize().height - menuHeight) / 2;
		mainMenu.setBounds(x, y, menuWidth, menuHeight);
		mainMenu.setVisible(true); // Show main menu initially
		layeredPane.add(mainMenu, JLayeredPane.MODAL_LAYER); // Add main menu above the game view ( however doesent work properly)

		// Initialize and position the in-game menu (for pausing)
		inGameMenu = new InGameMenu();
		menuWidth = inGameMenu.getPreferredSize().width;
		menuHeight = inGameMenu.getPreferredSize().height;
		x = (graphicView.getPreferredSize().width - menuWidth) / 2;
		y = (graphicView.getPreferredSize().height - menuHeight) / 2;
		inGameMenu.setBounds(x, y, menuWidth, menuHeight);
		inGameMenu.setVisible(false); // Hide in-game menu first
		layeredPane.add(inGameMenu, JLayeredPane.PALETTE_LAYER); // Add in-game menu above default layer

		// Initialize and position the game over restart button
		gameOverRestartButton = new JButton("Restart Game");
		gameOverRestartButton.setFont(new Font("Arial", Font.BOLD, 24));
		gameOverRestartButton.setBackground(new Color(70, 130, 180));
		gameOverRestartButton.setForeground(Color.BLACK);
		gameOverRestartButton.setFocusPainted(false); // Remove focus border
		gameOverRestartButton.setVisible(false); // Hide button initially

		int buttonWidth = 200;
		int buttonHeight = 50;
		int buttonX = (graphicView.getPreferredSize().width - buttonWidth) / 2;
		int buttonY = (graphicView.getPreferredSize().height / 2) + 70;
		gameOverRestartButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		layeredPane.add(gameOverRestartButton, JLayeredPane.POPUP_LAYER); // Add button on the topmost layer

		// Add the layered pane to the JFrame's center
		this.add(layeredPane, BorderLayout.CENTER);

		// start enemy movement timer
		enemyMoveTimer = new Timer(1, e -> {
			world.moveEnemies(); // Move enemies
			if (world.isGameOver()) {
				enemyMoveTimer.stop(); // Stop enemy movement if game is over
				gameOverRestartButton.setVisible(true); // Show restart button
			}
		});

		// game timer starts for power-up durations and other time-based updates
		gameTimer = new Timer(50, e -> { // Updates every 50 milliseconds
			long currentTime = System.currentTimeMillis();
			long deltaTime = currentTime - lastGameUpdateTime; // Calculate time elapsed since last update
			lastGameUpdateTime = currentTime; // Update last update time

			world.decreaseInvincibilityTimer(deltaTime); // Decrease invincibility timer
			// Only move enemies if they are not frozen
			if (!world.areEnemiesFrozen()) {
				world.decreaseEnemyFreezeTimer(deltaTime); // Decrease freeze timer only if already frozen
			} else {
				// If enemies are frozen, continue to decrease the freeze timer
				world.decreaseEnemyFreezeTimer(deltaTime);
				if (!world.areEnemiesFrozen()) { // If enemies just unfroze
					enemyMoveTimer.start(); // Restart enemy movement
				}
			}

			// If enemies become frozen, stop their movement timer
			if (world.areEnemiesFrozen() && enemyMoveTimer.isRunning()) {
				enemyMoveTimer.stop();
			} else if (!world.areEnemiesFrozen() && !enemyMoveTimer.isRunning() && !world.isPaused() && !world.isGameOver()) {
				// If enemies unfroze, and game is not paused/over, restart their movement
				enemyMoveTimer.start();
			}

			graphicView.repaint(); // Repaint to show their current timer-based changes (e.g. power-up status)
		});


		inGameMenu.addResumeButtonListener(e -> handleResumeGame()); // Handles Resume Button
		inGameMenu.addRestartButtonListener(e -> handleRestartGame()); // Handeles Restart Button
		inGameMenu.addExitButtonListener(e -> handleExitGame()); // Handles Exit button -> Terminates
		// Add action listener for difficulty box in in-game menu
		inGameMenu.addDifficultyComboBoxListener(e -> { // Handles difficultyChange()
			if ("comboBoxChanged".equals(e.getActionCommand())) {
				handleDifficultyChange();
			}
		});
		gameOverRestartButton.addActionListener(e -> handleRestartGame());	// Handles game over restart button
		mainMenu.addStartButtonListener(e -> handleStartGameFromMenu()); // Handles GameStartFromMenu() when "Start Game" is pressed

		// Add key and mouse listeners to the controller frame
		addKeyListener(this);
		addMouseListener(this);

		// Ensure the frame can receive keyboard input
		setFocusable(true);
		requestFocusInWindow();
	}

	/**
	 * Handles the event when the "Start Game" button is pressed in the MainMenu.
	 * It initializes a new World based on the selected difficulty, makes the game view visible,
	 * starts the game timers, and sets the game to unpaused.
	 */
	private void handleStartGameFromMenu() {
		// Gets the selected difficulty from the main menu
		Difficulty selectedDifficulty = Difficulty.valueOf(mainMenu.getSelectedDifficulty());
		// Create a new World instance with the chosen difficulty
		this.world = new World(selectedDifficulty);
		// Register the graphical view and console view with the new world to receive updates
		this.world.registerView(graphicView);
		this.world.registerView(this.consoleView);


		mainMenu.setVisible(false);  // Hide the mainMenu
		graphicView.setVisible(true); // Show the Game
		this.world.setPaused(false); // game is not paused anymore

		// Configure and start enemy movement timer based on difficulty
		enemyMoveTimer.setInitialDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.setDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.start();

		// Start the general game timer for power-ups and other effects
		lastGameUpdateTime = System.currentTimeMillis(); // Reset last update time for accurate delta calculation
		gameTimer.start();

		// Request focus back to the frame for keyboard input
		requestFocusInWindow();
		graphicView.repaint(); // Repaint the view to show the new game state
	}

	// Not used but necessary for running
	@Override
	public void keyTyped(KeyEvent e) { }

	/**
	 * Invoked when a key has been pressed.
	 * Handles player movement (UP, DOWN, LEFT, RIGHT) and pausing/unpausing the game (ESCAPE).
	 * @param e the event to be processed
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// If main menu is visible, ignore key presses (game hasn't started)
		if (mainMenu.isVisible()) {
			return;
		}

		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				// Only allow player movement if the game is not paused and not over
				if (!world.isPaused() && !world.isGameOver()) {
					world.movePlayer(Direction.fromKeyCode(e.getKeyCode()));
				}
				break;
			case KeyEvent.VK_ESCAPE:
				// Allow pausing/unpausing only if the game is not over
				if (!world.isGameOver()) {
					boolean newPausedState = !world.isPaused();
					world.setPaused(newPausedState); // Toggle paused state
					inGameMenu.setVisible(newPausedState); // Show/hide in-game menu

					if (newPausedState) { // If pausing the game
						enemyMoveTimer.stop(); // Stop enemy movement
						gameTimer.stop(); // Stop general game timer
						inGameMenu.setSelectedDifficulty(world.getDifficulty().name()); // Set selected difficulty in menu
					} else { // If resuming the game
						enemyMoveTimer.start(); // Restart enemy movement
						lastGameUpdateTime = System.currentTimeMillis(); // Reset last update time for game timer
						gameTimer.start(); // Restart general game timer
					}
					graphicView.repaint(); // Repaint the view to reflect paused/unpaused state
				}
				break;
		}
	}

	// Not used but necessary for running
	@Override
	public void keyReleased(KeyEvent e) { }

	// Not used but necessary for running
	@Override
	public void actionPerformed(ActionEvent e) { }

	/**
	 * Handles resuming the game from the in-game menu.
	 * Sets the world to unpaused, restarts timers, hides the menu, and requests focus.
	 */
	private void handleResumeGame() {
		world.setPaused(false); // Unpause the game
		enemyMoveTimer.start(); // Restart enemy movement timer
		lastGameUpdateTime = System.currentTimeMillis(); // Reset last update time for accurate timer calculation
		gameTimer.start(); // Restart general game timer
		inGameMenu.setVisible(false); // Hide the in-game menu
		requestFocusInWindow(); // Request focus back to the game frame
		graphicView.repaint(); // Repaint the graphical view
	}

	/**
	 * Handles restarting the game.
	 * Creates a new World instance based on the current (or selected) difficulty,
	 * resets game state, restarts timers, and hides any active menus/buttons.
	 */
	private void handleRestartGame() {

		String selectedDifficultyStr;
		if (inGameMenu.isVisible()) { // when user is in the inGamemenu get the selected Diffculty
			selectedDifficultyStr = inGameMenu.getSelectedDifficulty();
		} else { // When player lost and just restarts the game use Same diffuculty as before
			selectedDifficultyStr = world.getDifficulty().name();
		}
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);

		// Create a new World instance with the selected or current difficulty
		this.world = new World(newDifficulty);
		this.world.registerView(graphicView);
		this.world.registerView(this.consoleView);

		// Stop and reconfigure enemy movement timer for the new world's settings
		enemyMoveTimer.stop();
		gameTimer.stop(); // Stop game timer as well
		enemyMoveTimer.setInitialDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.setDelay((int) world.getEnemyMoveIntervalMillis());
		enemyMoveTimer.start(); // Start enemy movement

		lastGameUpdateTime = System.currentTimeMillis(); // Reset last update time for accurate delta calculation
		gameTimer.start(); // Start general game timer

		// Reset paused and game over states
		world.setPaused(false);
		inGameMenu.setVisible(false); // Hide in-game menu
		gameOverRestartButton.setVisible(false); // Hide game over restart button
		pack(); // Adjust frame size if necessary
		requestFocusInWindow(); // Request focus back to the game frame
		graphicView.repaint(); // Repaint the graphical view
	}

	/**
	 * Handles exiting the game. Terminates the application.
	 */
	private void handleExitGame() {
		System.exit(0); // Exit the Java Virtual Machine
	}

	/**
	 * Handles a change if the Difficulty will be changed by user
	 * Only if the new difficulty is different from the current one, the game is restarted else nothing happens.
	 */
	private void handleDifficultyChange() {
		String selectedDifficultyStr = inGameMenu.getSelectedDifficulty();
		Difficulty newDifficulty = Difficulty.valueOf(selectedDifficultyStr);
		// If the selected difficulty is different, restart the game with the new difficulty
		if (world.getDifficulty() != newDifficulty) {
			handleRestartGame();
		}
	}

	// Not used but necessary for running
	@Override
	public void mouseClicked(MouseEvent e) {}
	// Not used but necessary for running
	@Override
	public void mousePressed(MouseEvent e) {}
	// Not used but necessary for running
	@Override
	public void mouseReleased(MouseEvent e) {}
	// Not used but necessary for running
	@Override
	public void mouseEntered(MouseEvent e) {}
	// Not used but necessary for running
	@Override
	public void mouseExited(MouseEvent e) {}
}
