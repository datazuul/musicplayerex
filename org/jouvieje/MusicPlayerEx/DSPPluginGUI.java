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

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jouvieje.MusicPlayerEx.soundengine.DspPlugin;

public class DSPPluginGUI extends JDialog {
	private static final long serialVersionUID = 1L;

	private final DspPlugin plugin;
	private JPanel panel = null;
	private Canvas canvas = null;

	DSPPluginGUI(MusicPlayerGUI_V2 owner, DspPlugin plugin) {
		super(owner, plugin.getName());
		this.plugin = plugin;
		buildGUI();
	}

	private void buildGUI() {
		this.setContentPane(getPluginPanel());
		this.pack();
		this.setLocationRelativeTo(getOwner());
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				//Close GUI
				DSPPluginGUI.this.setVisible(false);
				DSPPluginGUI.this.dispose();
				//Destroy plugin
				plugin.destroyGUI(canvas);
				DspPlugin.unload(plugin);
			}
		});
	}

	private JPanel getPluginPanel() {
		if(panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(getCanvas());
		}
		return panel;
	}

	private Canvas getCanvas() {
		if(canvas == null) {
			Dimension dim = new Dimension(plugin.getWidth(), plugin.getHeight());

			canvas = new Canvas();
			canvas.setSize(dim);
			canvas.setPreferredSize(dim);
			canvas.setMinimumSize(dim);
			canvas.setMaximumSize(dim);
		}
		return canvas;
	}

	public void init() {
		if(plugin == null) {
			return;
		}
		plugin.createGUI(getCanvas());
	}
}
