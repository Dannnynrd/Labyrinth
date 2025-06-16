package controller;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.*;

import model.Direction;
import model.World;
import view.View;

/**
 * Our controller listens for key events on the main window.
 */
public class Controller extends JFrame implements KeyListener, ActionListener, MouseListener {

	/** The world that is updated upon every key press. */
	private final World world;
	private final JButton restartButton;
	private List<View> views;
	private final Dimension fieldDimensions;

	/**
	 * Creates a new instance.
	 * 
	 * @param world the world to be updated whenever the player should move.
	 * @param caged the {@link GraphicsProgram} we want to listen for key presses
	 *              on.
	 */
	public Controller(World world, Dimension fieldDimensions) {
		// Remember the world
		this.world = world;
		this.fieldDimensions = fieldDimensions;
		setLayout(new BorderLayout());

		this.restartButton = new JButton("Restart");

		this.restartButton.addActionListener(this);

		this.restartButton.setFocusable(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(restartButton);

		this.add(buttonPanel, BorderLayout.SOUTH);


		// Listen for key events
		addKeyListener(this);
		// Listen for mouse events.
		// Not used in the current implementation.
		addMouseListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	/////////////////// Key Events ////////////////////////////////

	@Override
	public void keyPressed(KeyEvent e) {
		// Check if we need to do something. Tells the world to move the player.
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			world.movePlayer(Direction.UP);
			break;

		case KeyEvent.VK_DOWN:
			world.movePlayer(Direction.DOWN);
			break;

		case KeyEvent.VK_LEFT:
			world.movePlayer(Direction.LEFT);
			break;

		case KeyEvent.VK_RIGHT:
			world.movePlayer(Direction.RIGHT);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	/////////////////// Action Events ////////////////////////////////

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == restartButton) {
			world.restart(); // Das Modell und die Ansichten werden aktualisiert

			// Sagt dem Fenster, seine Größe an den neuen Inhalt anzupassen.
			// pack() wird getPreferredSize() von GraphicView aufrufen.
			pack();
		}
	}
	
	/////////////////// Mouse Events ////////////////////////////////

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
