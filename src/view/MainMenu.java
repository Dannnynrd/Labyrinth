package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    // Statt JButtons verwenden wir jetzt JRadioButtons
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;

    private JButton startButton;
    private ButtonGroup difficultyGroup;

    public MainMenu() {
        setTitle("Labyrinth - Hauptmenü");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        // Radio-Buttons erstellen
        easyButton = new JRadioButton("Einfach");
        mediumButton = new JRadioButton("Mittel");
        hardButton = new JRadioButton("Schwer");

        startButton = new JButton("Spiel starten");

        // Die ButtonGroup sorgt dafür, dass nur ein RadioButton ausgewählt sein kann
        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        // Eine Standard-Auswahl treffen
        mediumButton.setSelected(true);

        // Die Buttons zum Panel hinzufügen
        panel.add(easyButton);
        panel.add(mediumButton);
        panel.add(hardButton);
        panel.add(startButton);

        add(panel);
    }

    // Eine neue Methode, um die Auswahl abzufragen
    public String getSelectedDifficulty() {
        if (easyButton.isSelected()) {
            return "EASY";
        }
        if (hardButton.isSelected()) {
            return "HARD";
        }
        // Standard ist "MEDIUM"
        return "MEDIUM";
    }

    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }
}