package controller;

import model.Difficulty;
import model.World;
import view.ConsoleView;
import view.GraphicView;
import javax.swing.JFrame;
import java.awt.*;

/**
 * This is the main program class for the Labyrinth game.
 * It is responsible for creating and configuring the core components of the
 * MVC pattern and setting up the game window.
 * It runs the Game
 */
public class Labyrinth {

    /**
     * The main entry point of the program.
     * It initializes the UI and
     * creates the initial game components (Controller, GraphicView, MainMenu),
     * and makes the main game window visible.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create a temporary World and GraphicView initially.
            // The actual World will be instantiated and configured when the "Start Game" button
            // is pressed in the MainMenu, which is handled by the Controller.
            World temporary = new World(Difficulty.MEDIUM); // Diffculty Medium will be enabled by default
            Dimension fieldDimensions = new Dimension(45, 45); // Size x * y
            GraphicView gview = new GraphicView(fieldDimensions); // Create the graphical view

            // Initialize ConsoleView
            ConsoleView cview = new ConsoleView();

            // Create the main Controller instance, which manages the game logic and UI interactions.
            Controller controller = new Controller(temporary, fieldDimensions, gview,cview);
            controller.setTitle("Labyrinth Game"); // Set the window title
            controller.setResizable(false); // Prevent window resizing to maintain layout integrity
            controller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // When closed, the app will be terminated
            controller.pack(); // Adjust the window size to fit its components
            controller.setVisible(true); // Make the main game window visible

        });
    }
}
