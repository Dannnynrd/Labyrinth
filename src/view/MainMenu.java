package view;

import model.Difficulty;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Represents the main menu of the Labyrinth game.
 * This JPanel allows the user to select a difficulty level and start a new game.
 * It is displayed at the beginning of the application.
 */
public class MainMenu extends JPanel {

    /** Radio button for selecting Easy difficulty. */
    private JRadioButton easyButton;
    /** Radio button for selecting Medium difficulty. */
    private JRadioButton mediumButton;
    /** Radio button for selecting Hard difficulty. */
    private JRadioButton hardButton;

    /** Button to start the game with the selected difficulty. */
    private JButton startButton;
    /** A ButtonGroup to ensure only one difficulty radio button can be selected at a time. */
    private ButtonGroup difficultyGroup;

    /**
     * Constructs a new MainMenu panel.
     * Initializes and styles the radio buttons for difficulty selection and the start button.
     * Sets up the layout to center the menu components and makes the panel semi-transparent.
     */
    public MainMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 rows: Easy, Medium, Hard, Start Button
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the content
        panel.setOpaque(false); // Make this inner panel transparent

        // Initialize difficulty radio buttons
        easyButton = new JRadioButton("Easy");
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");

        // Initialize the start game button
        startButton = new JButton("Start Game");

        // Group the radio buttons so that only one can be selected at a time.
        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        // Set Medium as the default selected difficulty.
        mediumButton.setSelected(true);

        // Add components to the inner panel
        panel.add(easyButton);
        panel.add(mediumButton);
        panel.add(hardButton);
        panel.add(startButton);

        // Apply custom styling to the radio buttons
        styleRadioButton(easyButton);
        styleRadioButton(mediumButton);
        styleRadioButton(hardButton);

        // Apply custom styling to the start button
        styleButton(startButton);

        // Set the layout for the MainMenu JPanel itself to center the 'panel' containing the controls.
        setLayout(new GridBagLayout());
        add(panel);

        // Make the MainMenu JPanel transparent so any background behind it can be seen.
        setOpaque(false);
        setPreferredSize(new Dimension(300, 250));
    }

    /**
     * Applies a consistent style to a given JRadioButton.
     * @param radioButton The JRadioButton to be styled.
     */
    private void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(new Font("Arial", Font.BOLD, 16));
        radioButton.setForeground(Color.WHITE); // Set text color to white
        radioButton.setOpaque(false); // Make background transparent
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
     * for the menu area, providing a visual overlay when the menu is active.
     * @param g The {@link Graphics} context used for drawing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Returns the name of the currently selected difficulty level.
     * This string corresponds to the name of a {@link Difficulty} enum constant.
     * @return A string representing the selected difficulty ("EASY", "MEDIUM", or "HARD").
     */
    public String getSelectedDifficulty() {
        if (easyButton.isSelected()) {
            return Difficulty.EASY.name();
        }
        if (hardButton.isSelected()) {
            return Difficulty.HARD.name();
        }
        return Difficulty.MEDIUM.name(); // Default to Medium if no other is selected
    }

    /**
     * Adds an {@link ActionListener} to the "Start Game" button.
     * This listener will be notified when the button is clicked.
     * @param listener The listener to be added.
     */
    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }
}
