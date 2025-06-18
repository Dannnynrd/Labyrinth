package view;

import model.Difficulty; // Import Difficulty enum
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class InGameMenu extends JPanel {

    private JButton resumeButton;
    private JButton restartButton;
    private JButton exitButton;
    private JComboBox<String> difficultyComboBox; // Changed to JComboBox

    public InGameMenu() {
        // Create an inner panel to hold the buttons and center it
        JPanel buttonContainerPanel = new JPanel();
        buttonContainerPanel.setLayout(new GridLayout(4, 1, 10, 10)); // Now 4 rows
        buttonContainerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        buttonContainerPanel.setOpaque(false); // Make this inner panel transparent as well

        resumeButton = new JButton("Resume Game");
        restartButton = new JButton("Restart Game");
        exitButton = new JButton("Exit Game");

        // Initialize JComboBox for difficulty
        String[] difficulties = {Difficulty.EASY.name(), Difficulty.MEDIUM.name(), Difficulty.HARD.name()};
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setFont(new Font("Arial", Font.BOLD, 18)); // Apply style
        ((JLabel)difficultyComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center text
        // Set default selection (optional, can be based on current game difficulty later)
        difficultyComboBox.setSelectedItem(Difficulty.MEDIUM.name());

        buttonContainerPanel.add(resumeButton);
        buttonContainerPanel.add(difficultyComboBox); // Add JComboBox
        buttonContainerPanel.add(restartButton);
        buttonContainerPanel.add(exitButton);


        // Styling for buttons
        styleButton(resumeButton);
        styleButton(restartButton);
        styleButton(exitButton);
        // JComboBox styling done inline as it's slightly different
        difficultyComboBox.setBackground(new Color(70, 130, 180));
        difficultyComboBox.setForeground(Color.BLACK);


        // Set the layout for the InGameMenu JPanel itself to center the buttonContainerPanel
        setLayout(new GridBagLayout()); // Using GridBagLayout for centering
        add(buttonContainerPanel); // Add the container panel to the InGameMenu

        // Make the InGameMenu JPanel transparent by default so the game view can be seen through it.
        setOpaque(false);

        // INCREASE THE PREFERRED SIZE FOR THE MENU PANEL ITSELF
        // This will make the entire menu overlay larger
        setPreferredSize(new Dimension(300, 250));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(70, 130, 180)); // SteelBlue
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw a semi-transparent black background for the menu panel area
        g.setColor(new Color(0, 0, 0, 180)); // Black with 70% opacity
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public void addResumeButtonListener(ActionListener listener) {
        resumeButton.addActionListener(listener);
    }

    public void addRestartButtonListener(ActionListener listener) {
        restartButton.addActionListener(listener);
    }

    public void addExitButtonListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    // New method to add listener for difficulty dropdown
    public void addDifficultyComboBoxListener(ActionListener listener) {
        difficultyComboBox.addActionListener(listener);
    }

    // New method to get selected difficulty string
    public String getSelectedDifficulty() {
        return (String) difficultyComboBox.getSelectedItem();
    }

    // Method to set the selected difficulty, useful when showing the menu
    public void setSelectedDifficulty(String difficultyName) {
        difficultyComboBox.setSelectedItem(difficultyName);
    }
}