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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import java.util.Vector;

import org.jouvieje.MusicPlayerEx.soundengine.SoundEngineEx;

import javax.swing.SwingConstants;

public class SoundcardGUI extends JDialog {
	protected void applyLang() {
		this.setTitle(MusicPlayerGUI_V2.lang.getString("soundcard"));
		selectSoundcardLabel.setText(MusicPlayerGUI_V2.lang.getString("select_soundcard"));
//		getSelectB().setText(MusicPlayerGUI_V2.lang.getString("select"));	//TODO Lang
		getSelectB().setText("OK");
	}

	private final MusicPlayerGUI_V2 player;

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel selectSoundcardLabel = null;
	private JComboBox soundcards = null;
	private JButton selectB = null;

	public SoundcardGUI(MusicPlayerGUI_V2 player) {
		super(player);
		this.player = player;
		initialize();
		applyLang();
	}

	private void initialize() {
		this.setSize(280, 130);
		this.setContentPane(getJContentPane());
		this.setLocationRelativeTo(getOwner());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	private JPanel getJContentPane() {
		if(jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			selectSoundcardLabel = new JLabel();
			selectSoundcardLabel.setText("");
			selectSoundcardLabel.setHorizontalTextPosition(JLabel.CENTER);
			selectSoundcardLabel.setHorizontalAlignment(SwingConstants.CENTER);
			selectSoundcardLabel.setVerticalTextPosition(JLabel.CENTER);
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(selectSoundcardLabel, gridBagConstraints);
			jContentPane.add(getSoundcards(), gridBagConstraints1);
			jContentPane.add(getSelectB(), gridBagConstraints11);
		}
		return jContentPane;
	}

	private JComboBox getSoundcards() {
		if(soundcards == null) {
			String[] names = SoundEngineEx.get().getSoundcardNames();
			Vector vector = new Vector();
			vector.add(MusicPlayerGUI_V2.lang.getString("default_soundcard"));
			for(int i = 0; i < names.length; i++) {
				vector.add(names[i]);
			}

			soundcards = new JComboBox(vector);
			soundcards.setSelectedIndex(player.getOptions().getSoundcard() + 1);
		}
		return soundcards;
	}

	private JButton getSelectB() {
		if(selectB == null) {
			selectB = new JButton();
			selectB.addActionListener(new java.awt.event.ActionListener(){
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int soundcard = getSoundcards().getSelectedIndex() - 1;
					if(soundcard != player.getOptions().getSoundcard()) {
						player.getOptions().setSoundcard(soundcard);
						JOptionPane.showMessageDialog(SoundcardGUI.this, MusicPlayerGUI_V2.lang.getString("restart_needed"));
					}
					SoundcardGUI.this.dispose();
				}
			});
		}
		return selectB;
	}
} //  @jve:decl-index=0:visual-constraint="10,10"
