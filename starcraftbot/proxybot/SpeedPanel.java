package starcraftbot.proxybot;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpeedPanel extends JPanel {

	public int initialSpeed = 20;
	public int slowest = 100;
	public int fastest = 0;

	/** the parent of this component */
	private JFrame frame;
	
	public SpeedPanel(final Game game) {
		
		game.getCommandQueue().setGameSpeed(initialSpeed);
		final JSlider slider = new JSlider(JSlider.HORIZONTAL, fastest, slowest, initialSpeed);
		slider.addChangeListener(new ChangeListener() {			
			public void stateChanged(ChangeEvent e) {
				System.out.println("Setting game speed: " + slider.getValue());
				game.getCommandQueue().setGameSpeed(slider.getValue());
			}
		});
		
		setPreferredSize(new Dimension(300, 40));
		add(slider);
		
		frame = new JFrame("Game Speed");
		frame.add(this);
		frame.pack();
		frame.setLocation(320, 0);
		frame.setVisible(true);			
	}
	
	/**
	 * Shuts down the GUI.
	 */
	public void stop() {
		frame.setVisible(false);
	}
}
