// src/view/MainMenu.java
package view;

import model.Difficulty; // Import Difficulty enum
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel { // Changed from JFrame to JPanel

    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;

    private JButton startButton;
    private ButtonGroup difficultyGroup;

    public MainMenu() {
        // Removed JFrame specific settings: setTitle, setDefaultCloseOperation, setSize, setLocationRelativeTo

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding
        panel.setOpaque(false); // Make transparent to potentially show background if needed

        easyButton = new JRadioButton("Easy"); // Renamed to English for consistency
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");

        startButton = new JButton("Start Game"); // Renamed to English for consistency

        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        mediumButton.setSelected(true); // Default selection

        panel.add(easyButton);
        panel.add(mediumButton);
        panel.add(hardButton);
        panel.add(startButton);

        // Styling for radio buttons (optional)
        styleRadioButton(easyButton);
        styleRadioButton(mediumButton);
        styleRadioButton(hardButton);

        // Styling for start button
        styleButton(startButton);

        setLayout(new GridBagLayout()); // Use GridBagLayout to center the panel
        add(panel);

        setOpaque(false); // Make the MainMenu JPanel itself transparent (important for background visibility)
        setPreferredSize(new Dimension(300, 250)); // Set a preferred size for the menu
    }

    private void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(new Font("Arial", Font.BOLD, 16));
        radioButton.setForeground(Color.WHITE); // Text color
        radioButton.setOpaque(false); // Make background transparent
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

    public String getSelectedDifficulty() {
        if (easyButton.isSelected()) {
            return Difficulty.EASY.name(); // Use Difficulty enum names
        }
        if (hardButton.isSelected()) {
            return Difficulty.HARD.name();
        }
        return Difficulty.MEDIUM.name();
    }

    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }
}