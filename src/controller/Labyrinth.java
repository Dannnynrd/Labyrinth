// src/controller/Labyrinth.java
package controller;

import model.Difficulty;
import model.World;
import view.ConsoleView;
import view.GraphicView;
import view.MainMenu; // No longer imported as JFrame, but still used conceptually

import javax.swing.JFrame;
import java.awt.*;

/**
 * This is our main program. It is responsible for creating all of the objects
 * that are part of the MVC pattern and connecting them with each other.
 */
public class Labyrinth {

    /**
     * The main entry point of the program.
     * It creates and shows the main menu initially within the main game window.
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create a dummy World and GraphicView initially to set up the Controller's frame.
            // The actual World will be created when "Start Game" is pressed.
            World dummyWorld = new World(Difficulty.MEDIUM); // Use a default difficulty for setup
            Dimension fieldDimensions = new Dimension(45, 45);
            GraphicView gview = new GraphicView(fieldDimensions);
            // No need to register dummyWorld with gview yet.

            ConsoleView cview = new ConsoleView(); // Initialize ConsoleView if it's always desired

            Controller controller = new Controller(dummyWorld, fieldDimensions, gview); // Pass dummyWorld
            controller.setTitle("Labyrinth Game");
            controller.setResizable(false);
            controller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            controller.pack();
            controller.setVisible(true);

            // The main menu is now handled internally by the Controller,
            // so we don't need to explicitly create and manage it here.
            // The controller's MainMenu start button listener will initiate the game.
        });
    }

    // The startGame method is now effectively integrated into the Controller's
    // handleStartGameFromMenu method, which is called when the start button
    // in the MainMenu (now a JPanel) is clicked.
    // So, this method is no longer directly called from main.
    /*
    public static void startGame(Difficulty difficulty) {
        // This logic is now part of Controller.handleStartGameFromMenu()
    }
    */
}