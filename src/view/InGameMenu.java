package view;

import model.Difficulty;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Represents the in-game menu, displayed when the game is paused.
 * This menu provides options to resume, restart, exit the game, and change difficulty.
 * It is a JPanel designed to overlay the game view with a transparent effect.
 */
public class InGameMenu extends JPanel {

    /** Button to resume the current game. */
    private JButton resumeButton;
    /** Button to restart the game from the beginning with selected difficulty. */
    private JButton restartButton;
    /** Button to exit the application. */
    private JButton exitButton;
    /** Dropdown menu for selecting game difficulty. */
    private JComboBox<String> difficultyComboBox;

    /**
     * Constructs a new InGameMenu panel.
     * Initializes and styles the buttons and the difficulty combo box.
     * Sets up the layout to center the menu components and makes the panel semi-transparent.
     */
    public InGameMenu() {
        // Create an inner panel to hold the buttons and center it within the InGameMenu panel
        JPanel buttonContainerPanel = new JPanel();
        // Use GridLayout for a vertical stack of components with spacing
        buttonContainerPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 rows: Resume, Difficulty, Restart, Exit
        buttonContainerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around components
        buttonContainerPanel.setOpaque(false); // Make this inner panel transparent

        // Initialize buttons
        resumeButton = new JButton("Resume Game");
        restartButton = new JButton("Restart Game");
        exitButton = new JButton("Exit Game");

        // Initialize JComboBox for difficulty selection
        // Populate with names of Difficulty enum constants
        String[] difficulties = {Difficulty.EASY.name(), Difficulty.MEDIUM.name(), Difficulty.HARD.name()};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFont(new Font("Arial", Font.BOLD, 18)); // Apply consistent font style
        // Centers the text within the combo box for better aesthetics
        ((JLabel)difficultyComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        // Set a default selection (medium)
        difficultyComboBox.setSelectedItem(Difficulty.MEDIUM.name());

        // Add components to the button container panel
        buttonContainerPanel.add(resumeButton);
        buttonContainerPanel.add(difficultyComboBox);
        buttonContainerPanel.add(restartButton);
        buttonContainerPanel.add(exitButton);


        // Apply common styling to buttons
        styleButton(resumeButton);
        styleButton(restartButton);
        styleButton(exitButton);
        // Apply specific styling to JComboBox
        difficultyComboBox.setBackground(new Color(70, 130, 180));
        difficultyComboBox.setForeground(Color.BLACK);


        // Set the layout for the InGameMenu JPanel itself to center the buttonContainerPanel
        setLayout(new GridBagLayout());
        add(buttonContainerPanel);

        // Make the InGameMenu JPanel transparent so the game view will be seen in the background.
        setOpaque(false);

        // Set a preferred size for the entire menu panel.
        setPreferredSize(new Dimension(300, 250));
    }

    /**
     * Applies a consistent style to a given JButton.
     * @param button The JButton to be styled.
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
    }

    /**
     * Overrides the paintComponent method to draw a semi-transparent background
     * for the menu area, creating a visual distinction when the menu is active.
     * @param g The {@link Graphics} context used for drawing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw a semi-transparent black rectangle over the menu area
        g.setColor(new Color(0, 0, 0, 180)); // Black with 70% opacity
        g.fillRect(0, 0, getWidth(), getHeight()); // Fill the entire panel area
    }

    /**
     * Adds an {@link ActionListener} to the "Resume Game" button.
     * @param listener The listener to be added.
     */
    public void addResumeButtonListener(ActionListener listener) {
        resumeButton.addActionListener(listener);
    }

    /**
     * Adds an {@link ActionListener} to the "Restart Game" button.
     * @param listener The listener to be added.
     */
    public void addRestartButtonListener(ActionListener listener) {
        restartButton.addActionListener(listener);
    }

    /**
     * Adds an {@link ActionListener} to the "Exit Game" button.
     * @param listener The listener to be added.
     */
    public void addExitButtonListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    /**
     * Adds an {@link ActionListener} to the difficulty combo box.
     * This listener will be notified when the selected item changes.
     * @param listener The listener to be added.
     */
    public void addDifficultyComboBoxListener(ActionListener listener) {
        difficultyComboBox.addActionListener(listener);
    }

    /**
     * Returns the currently selected difficulty string from the combo box.
     * This string corresponds to the name of a {@link Difficulty} enum constant.
     * @return The name of the selected difficulty.
     */
    public String getSelectedDifficulty() {
        return (String) difficultyComboBox.getSelectedItem();
    }

    /**
     * Sets the selected difficulty in the combo box.
     * This is useful when the menu is displayed, ensuring the combo box reflects
     * the current game's difficulty setting.
     * @param difficultyName The name of the difficulty to set as selected.
     */
    public void setSelectedDifficulty(String difficultyName) {
        difficultyComboBox.setSelectedItem(difficultyName);
    }
}