// In einer neuen Datei: view/InGameMenu.java
package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class InGameMenu extends JDialog {

    private JButton continueButton;
    private JButton restartButton;

    // Der Konstruktor braucht das Hauptfenster (unseren Controller),
    // um sich daran "anzukoppeln".
    public InGameMenu(JFrame owner) {
        // 'super(owner, true)' erstellt ein modales Dialogfenster.
        // "Modal" bedeutet, dass man erst das Menü schliessen muss,
        // bevor man wieder mit dem Hauptspiel interagieren kann.
        super(owner, "Menü", true);

        // Buttons erstellen
        continueButton = new JButton("Weitermachen");
        restartButton = new JButton("Neustart");

        // Layout für das Dialogfenster
        setLayout(new GridLayout(2, 1, 10, 10));
        add(continueButton);
        add(restartButton);

        setSize(200, 150); // Eine passende Grösse für das Menü
        setLocationRelativeTo(owner); // Zentriert das Menü über dem Spiel
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Schliesst nur das Menü, nicht das Spiel
    }

    // Methoden, damit der Controller von aussen auf die Button-Klicks lauschen kann
    public void addContinueListener(ActionListener listener) {
        continueButton.addActionListener(listener);
    }

    public void addRestartListener(ActionListener listener) {
        restartButton.addActionListener(listener);
    }
}