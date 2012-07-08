/**
 * Music Player Ex
 * Copyright © 2004-2011 Jérôme JOUVIE
 * 
 * Created on 6 mai. 2004
 * @version build 10/12/201
 * 
 * @author Jérôme JOUVIE
 * @mail   jerome.jouvie@gmail.com
 * @site   http://jerome.jouvie.free.fr/
 *         http://bonzaiengine.com/
 * 
 * ABOUT
 * A music player written in Java technology and FMOD Ex sound system.
 */
package org.jouvieje.MusicPlayerEx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jouvieje.MusicPlayerEx.soundengine.Analyser;

import java.awt.Rectangle;
import java.nio.FloatBuffer;

public class Display extends JPanel {
	interface DisplayMode {
		public final static int DEFAULT = 0;
		public final static int SPECTRUM_LOG = 1;
		public final static int OSCILLATOR = 2;
	}

	private int mode = DisplayMode.DEFAULT;
	private final static int PLOT_WIDTH = 190;
	private final static int PLOT_HEIGHT = 50;
	private final static Dimension PLOT = new Dimension(PLOT_WIDTH, PLOT_HEIGHT);

	private static final long serialVersionUID = 1L;
	private JPanel defaultP = null;
	private JLabel info = null;
	private JLabel time = null;
	private JPanel osciloscope = null;
	private JPanel spectrum = null;

	public Display() {
		super();
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setSize(196, 56);
		this.setOpaque(true);
		this.setDoubleBuffered(true);
		this.setFont(getFont().deriveFont(Font.BOLD));
		this.setBackground(Color.BLACK);
		this.setForeground(Color.LIGHT_GRAY);
		this.add(getDefaultP(), null);
		this.add(getOsciloscope(), null);
		this.add(getSpectrum(), null);
	}

	private JLabel getInfo() {
		if(info == null) {
			info = new JLabel();
			info.setBounds(new Rectangle(offset_x, offset_y, 400, 13));
			info.setForeground(this.getForeground());
			info.setFont(getFont());
		}
		return info;
	}

	private JLabel getTime() {
		if(time == null) {
			time = new JLabel();
			time.setBounds(new Rectangle(offset_x, 20, 80, 14));
			time.setForeground(this.getForeground());
			time.setFont(getFont());
		}
		return time;
	}

	private JPanel getDefaultP() {
		if(defaultP == null) {
			defaultP = new JPanel();
			defaultP.setLayout(null);
			defaultP.setOpaque(false);
			defaultP.setBounds(new Rectangle(0, 0, getWidth(), getHeight()));
			defaultP.add(getInfo(), null);
			defaultP.add(getTime(), null);
		}
		return defaultP;
	}

	private JPanel getOsciloscope() {
		if(osciloscope == null) {
			osciloscope = new JPanel();
			osciloscope.setBounds(new Rectangle(3, 3, PLOT_WIDTH, PLOT_HEIGHT));
			osciloscope.setBackground(java.awt.Color.black);
			osciloscope.setMinimumSize(PLOT);
			osciloscope.setPreferredSize(PLOT);
			osciloscope.setMaximumSize(PLOT);
			osciloscope.setVisible(false);
		}
		return osciloscope;
	}

	private JPanel getSpectrum() {
		if(spectrum == null) {
			spectrum = new JPanel();
			spectrum.setBounds(new Rectangle(3, 3, PLOT_WIDTH, PLOT_HEIGHT));
			spectrum.setBackground(java.awt.Color.black);
			spectrum.setMinimumSize(PLOT);
			spectrum.setPreferredSize(PLOT);
			spectrum.setMaximumSize(PLOT);
			spectrum.setVisible(false);
		}
		return spectrum;
	}

	protected void paintComponent(Graphics g) {
		if(!isVisible()) return;

		/*Display background*/
		if(isOpaque()) {
			g.setColor(getInfo().getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getBackground());
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
		}
	}

	public void updateTime() {
		if(!isVisible()) return;

		switch(mode) {
			default:
			case DisplayMode.DEFAULT:
				updateText();
				break;
		}
	}

	public void updateSound() {
		if(!isVisible()) return;

		switch(mode) {
			case DisplayMode.SPECTRUM_LOG:
				updateSpectrum(true);
				break;
			case DisplayMode.OSCILLATOR:
				updateOscillator();
				break;
		}
	}

	private static FontRenderContext frc = new FontRenderContext(new AffineTransform(), false, false); //  @jve:decl-index=0:

	private final int offset_x = 5;
	private final int offset_y = 4;

	private int sens = -1;
	private static final int PAUSED_TIME = 2000;
	private static int WAIT = PAUSED_TIME;
	private long start = -1;
	private int text_x = 0;

	private boolean paused = false;
	private int tempo = 0;
	private final int TEMPO_TIME = 8;

	public void updateText() {
		//If text is bigger than the space
		int rect_w = (int)getInfo().getFont().getStringBounds(getInfo().getText(), frc).getWidth();
		int old_text_x = text_x;
		if(rect_w > getWidth() - 2 * offset_x) {
			if(rect_w + text_x < getWidth() - 2 * offset_x && sens != 1) {
				sens = 1;
				start = -1;
				WAIT = PAUSED_TIME / 2;
			}
			else if(text_x > 0) {
				resetText();
			}

			if(start == -1) {
				start = System.currentTimeMillis();
			}
			else if(System.currentTimeMillis() - start > WAIT) {
				text_x += sens;
			}
		}
		//Update text position
		if(text_x != old_text_x) {
			getInfo().setLocation(offset_x + text_x, offset_y);
		}
		if(paused) {
			tempo++;
			boolean changeState = false;
			if(tempo > TEMPO_TIME) {
				tempo = 0;
				changeState = true;
			}
			if(changeState) getInfo().setVisible(!getInfo().isVisible());
		}
	}

	private void resetText() {
		start = -1;
		text_x = 0;
		sens = -1;
		WAIT = PAUSED_TIME;
		getInfo().setLocation(offset_x, offset_y);
	}

	private void updateSpectrum(boolean log) {
		BufferedImage image = new BufferedImage(PLOT_WIDTH, PLOT_HEIGHT, BufferedImage.TYPE_INT_RGB);

		/*
		 * Draw a black square with grey lines through it.
		 */
		int grey = 0x404040;
		for(int x = 0; x < PLOT_WIDTH; x++) {
			for(int y = 0; y < PLOT_HEIGHT; y += 3) {
				image.setRGB(x, y, grey);
			}
		}
		for(int x = 0; x <= 4; x++) {
			for(int y = 0; y < PLOT_HEIGHT; y++) {
				image.setRGB(x * (PLOT_WIDTH - 1) / 4, y, grey);
			}
		}

		//returns an array of 512 floats
		FloatBuffer spectrumBuffer = Analyser.getSpectrum(0);
		if(spectrumBuffer == null) {
			return;
		}

		float max = 0;
		for(int j = 0; j < 512; j++) {
			if(spectrumBuffer.get(j) > max) {
				max = spectrumBuffer.get(j);
			}
		}

		if(max > 0.0001f) {
			/*
			 * Spectrum graphic is 256 entries wide, and the spectrum is 512 entries.
			 * The upper band of frequencies at 44khz is pretty boring (ie 11-22khz), so we are only
			 * going to display the first 256 frequencies, or (0-11khz)
			 */
			for(int x = 0; x < 512; x++) {
				int y;
				float val = spectrumBuffer.get(x);

				if(log) {
					/*
					 * 1.0   = 0db
					 * 0.5   = -6db
					 * 0.25  = -12db
					 * 0.125 = -24db
					 */
					val = 10.0f * (float)Math.log10(val) * 2.0f;
					if(val < -150) val = -150;

					val /= -150.0f;
					val = 1.0f - val;

					y = (int)(val * PLOT_HEIGHT);
				}
				else {
					y = (int)(val / max * PLOT_HEIGHT);
				}

				if(y >= PLOT_HEIGHT) y = PLOT_HEIGHT - 1;

				for(int j = 0; j < y; j++) {
					int r = (j << 1);
					int g = 0xFF - (j << 1);
					int b = 0x1F;

					image.setRGB(x * PLOT_WIDTH / 512, PLOT_HEIGHT - 1 - j, (r << 16) + (g << 8) + b);
				}
			}
		}

		//Draw the spectrum on the screen
		getSpectrum().getGraphics().drawImage(image, 0, 0, null);
	}

	private int oscColor = 0xffffaf;

	private void updateOscillator() {
		//Retrieve wave datas
		FloatBuffer oscBuffer = Analyser.getWaveData(0, PLOT_WIDTH);
		if(oscBuffer == null) return;

		BufferedImage image = new BufferedImage(PLOT_WIDTH, PLOT_HEIGHT, BufferedImage.TYPE_INT_RGB);

		/*
		 * xoff is the x position that is scaled lookup of the dsp block according to the graphical
		 * window size.
		 */
		int xoff = 0;
		int step = 1;

		for(int i = 0; i < PLOT_WIDTH - 1; i++) {
			int x = xoff;
			int y = (int)((oscBuffer.get(x) + 1.0f) / 2.0f * PLOT_HEIGHT);
			int y2 = (int)((oscBuffer.get(x + step) + 1.0f) / 2.0f * PLOT_HEIGHT);

			y  = y  < 0 ? 0 : y  >= PLOT_HEIGHT ? PLOT_HEIGHT - 1 : y;
			y2 = y2 < 0 ? 0 : y2 >= PLOT_HEIGHT ? PLOT_HEIGHT - 1 : y2;

			if(y > y2) {
				int tmp = y;
				y = y2;
				y2 = tmp;
			}

			for(int j = y; j <= y2; j++) {
				image.setRGB(i, j, oscColor);
			}

			xoff += step;
		}

		//Draw the oscilloscope on the screen
		getOsciloscope().getGraphics().drawImage(image, 0, 0, null);
	}

	public void setDisplayText(String text) {
		String old = getInfo().getText();
		getInfo().setText(text);
		if(!old.equals(text)) {
			resetText();
		}
	}

	public void setDisplayTime(String time) {
		getTime().setText(time);
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
		if(!paused && mode == DisplayMode.DEFAULT) {
			getInfo().setVisible(true);
		}
	}

	boolean discardChangeMode = false; //Trick to discard clic event when it is a double clic

	public void changeDisplayMode() {
		discardChangeMode = false;
		Thread t = new Thread(){
			public void run() {
				//Wait if a double clic event occures discarding the simple clic event
				try {
					Thread.sleep(200);
				} catch(InterruptedException e) {}

				if(discardChangeMode) {
					return; //A double clic was performed, discard
				}

				//It's not a double clic, process

				switch(mode) {
					default:
					case DisplayMode.DEFAULT:
						mode = DisplayMode.SPECTRUM_LOG;
						getDefaultP().setVisible(false);
						getOsciloscope().setVisible(true);
						getSpectrum().setVisible(false);
						break;
					case DisplayMode.SPECTRUM_LOG:
						mode = DisplayMode.OSCILLATOR;
						getDefaultP().setVisible(false);
						getOsciloscope().setVisible(true);
						getSpectrum().setVisible(false);
						break;
					case DisplayMode.OSCILLATOR:
						mode = DisplayMode.DEFAULT;
						getDefaultP().setVisible(true);
						getOsciloscope().setVisible(false);
						getSpectrum().setVisible(false);
						break;
				}
			}
		};
		t.start();
	}

	public void discardDisplayMode() {
		discardChangeMode = true;
	}
}