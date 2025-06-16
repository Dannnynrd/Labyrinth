package controller;

import model.Difficulty;
import model.World;
import view.ConsoleView;
import view.GraphicView;
import view.MainMenu;

import javax.swing.JFrame;
import java.awt.*;

/**
 * This is our main program. It is responsible for creating all of the objects
 * that are part of the MVC pattern and connecting them with each other.
 */
public class Labyrinth {

    /**
     * The main entry point of the program.
     * It creates and shows the main menu.
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);

            // This is the action for the "Start Game" button
            mainMenu.addStartButtonListener(e -> {
                // 1. Get the selected difficulty as a String (e.g., "EASY")
                String selectedDifficultyStr = mainMenu.getSelectedDifficulty();

                // 2. Convert the String into a real Difficulty object
                Difficulty selectedDifficulty = Difficulty.valueOf(selectedDifficultyStr);

                mainMenu.dispose(); // Close the menu

                // 3. Call startGame WITH the selected difficulty
                startGame(selectedDifficulty);
            });
        });
    }

    /**
     * This method sets up and starts the actual game with the chosen difficulty.
     * @param difficulty The difficulty level selected by the player.
     */
    public static void startGame(Difficulty difficulty) {
        // Create a new game world with the selected difficulty.
        World world = new World(difficulty);

        // Get world dimensions from the world object itself
        int width = world.getWidth();
        int height = world.getHeight();

        // Size of a field in the graphical view.
        Dimension fieldDimensions = new Dimension(25, 25);
        // Create and register graphical view.
        GraphicView gview = new GraphicView(
                width * fieldDimensions.width,
                height * fieldDimensions.height,
                fieldDimensions);
        world.registerView(gview);
        gview.setVisible(true);

        // Create and register console view.
        ConsoleView cview = new ConsoleView();
        world.registerView(cview);

        // Create controller and initialize JFrame.
        Controller controller = new Controller(world);
        controller.setTitle("Labyrinth Game");
        controller.setResizable(false);
        controller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controller.getContentPane().add(gview, BorderLayout.CENTER);
        // pack() is needed before JFrame size can be calculated.
        controller.pack();

        // Calculate size of window by size of insets (titlebar + border) and size of
        // graphical view.
        Insets insets = controller.getInsets();

        int windowX = width * fieldDimensions.width + insets.left + insets.right;
        int windowY = height * fieldDimensions.height + insets.bottom + insets.top;
        Dimension size = new Dimension(windowX, windowY);
        controller.setSize(size);
        controller.setMinimumSize(size);
        controller.setVisible(true);
    }
}