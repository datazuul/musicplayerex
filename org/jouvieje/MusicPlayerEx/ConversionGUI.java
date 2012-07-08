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

import java.awt.Rectangle;
import java.io.File;
import java.util.Vector;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.soundengine.ConvertEngineEx;
import org.jouvieje.MusicPlayerEx.soundengine.SharedSoundEngineEx;
import org.jouvieje.MusicPlayerEx.soundengine.Song;

import javax.swing.JTextField;
import javax.swing.JFileChooser;

public class ConversionGUI extends JDialog {
	private static final long serialVersionUID = 1L;
	private static File dir = null;
	
	private final Playlist playlist;
	private final Vector songs;
	private boolean canceled = false;

	private JPanel jContentPane = null;
	private JProgressBar progression = null;
	private JButton cancelB = null;
	private JLabel message = null;
	private JLabel outL = null;
	private JLabel process = null;
	private JButton openFolder = null;
	private JTextField out = null;
	private JComboBox formats = null;
	private JComboBox bitrates = null;
	private JComboBox qualities = null;
	private JComboBox mp3Methods = null;
	private JComboBox oggMethods = null;
	private JFileChooser folder = null;

	public ConversionGUI(Frame owner, Playlist playlist, Vector songs) {
		super(owner, false);
		this.playlist = playlist;
		this.songs = songs;
		initialize();
		applyLang();
	}

	private void applyLang() {
		this.setTitle(MusicPlayerGUI_V2.lang.getString("convert_title"));
		Object[] args = {new Integer(songs.size())};
		String format = String.format(MusicPlayerGUI_V2.lang.getString("convert_message"), args);
		message.setText("<html><body>" + format + "</body></html>");
		getCancelB().setText(MusicPlayerGUI_V2.lang.getString("convert"));
		outL.setText(MusicPlayerGUI_V2.lang.getString("convert_output"));
		bitrates.setToolTipText(MusicPlayerGUI_V2.lang.getString("convert_bitrate"));
		mp3Methods.setToolTipText(MusicPlayerGUI_V2.lang.getString("convert_method"));
		oggMethods.setToolTipText(MusicPlayerGUI_V2.lang.getString("convert_method"));
		qualities.setToolTipText(MusicPlayerGUI_V2.lang.getString("convert_quality"));
		
		if(dir == null) {
			dir = new File(System.getProperty("user.home"), playlist.getName());
		}
		getOut().setText(dir.getPath());
	}

	private void initialize() {
		this.setSize(212, 200);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setContentPane(getJContentPane());
	}

	private JPanel getJContentPane() {
		if(jContentPane == null) {
			process = new JLabel();
			process.setBounds(new Rectangle(5, 108, 192, 16));
			outL = new JLabel();
			outL.setBounds(new Rectangle(5, 66, 192, 16));
			message = new JLabel();
			message.setBounds(new Rectangle(5, 5, 192, 36));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(message, null);
			jContentPane.add(getFormats(), null);
			jContentPane.add(getMp3Methods(), null);
			jContentPane.add(getOggMethods(), null);
			jContentPane.add(getBitrates(), null);
			jContentPane.add(getQualities(), null);
			jContentPane.add(outL, null);
			jContentPane.add(getOut(), null);
			jContentPane.add(getOpenFolder(), null);
			jContentPane.add(process, null);
			jContentPane.add(getProgression(), null);
			jContentPane.add(getCancelB(), null);
		}
		return jContentPane;
	}

	private JProgressBar getProgression() {
		if(progression == null) {
			progression = new JProgressBar(0, 100);
			progression.setBounds(new Rectangle(5, 128, 192, 15));
		}
		return progression;
	}

	private JButton getCancelB() {
		if(cancelB == null) {
			cancelB = new JButton();
			cancelB.setBounds(new Rectangle(110, 147, 87, 18));
			cancelB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!start) {
						getFormats().setEnabled(false);
						getMp3Methods().setEnabled(false);
						getOggMethods().setEnabled(false);
						getBitrates().setEnabled(false);
						getQualities().setEnabled(false);
						sync();
						
						start = true;
						cancelB.setText(MusicPlayerGUI_V2.lang.getString("cancel"));
						conversion.start();
					}
					else {
						canceled = true;
						while(conversion.isAlive()) {
							try {
								Thread.sleep(10);
							} catch(InterruptedException e1) {}
						}
						ConversionGUI.this.dispose();
					}
				}
			});
		}
		return cancelB;
	}

	private JButton getOpenFolder() {
		if(openFolder == null) {
			openFolder = new JButton();
			openFolder.setBounds(new Rectangle(162, 86, 34, 18));
			openFolder.setText("...");
			openFolder.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getFolder().setSelectedFile(dir);
					getFolder().showOpenDialog(ConversionGUI.this);
					if(getFolder().getSelectedFile() != null) {
						dir = getFolder().getSelectedFile();
						getOut().setText(dir.getPath());
					}
				}
			});
		}
		return openFolder;
	}

	private JTextField getOut() {
		if(out == null) {
			out = new JTextField();
			out.setEditable(false);
			out.setBounds(new Rectangle(5, 86, 151, 18));
		}
		return out;
	}

	private JComboBox getFormats() {
		if(formats == null) {
			String formatss[];
			if(SharedSoundEngineEx.get().getPlatform() == SharedSoundEngineEx.WIN) {
				formatss = new String[]{"WAV", "MP3", "OGG"};
			}
			else {
				formatss = new String[]{"WAV", "MP3"};
			}

			formats = new JComboBox(formatss);
			formats.setBounds(new Rectangle(12, 42, 60, 18));
			formats.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					int index = formats.getSelectedIndex();
					if(index == 0) {
						ConvertEngineEx.OUTPUT = ConvertEngineEx.OUTPUT_WAV;
						getMp3Methods().setVisible(false);
						getOggMethods().setVisible(false);
						getBitrates().setVisible(false);
						getQualities().setVisible(false);
					}
					else if(index == 1) {
						ConvertEngineEx.OUTPUT = ConvertEngineEx.OUTPUT_MP3;
						getMp3Methods().setVisible(true);
						getOggMethods().setVisible(false);
						getBitrates().setVisible(ConvertEngineEx.METHOD != 2);
						getQualities().setVisible(ConvertEngineEx.METHOD == 2);
					}
					else if(index == 2) {
						ConvertEngineEx.OUTPUT = ConvertEngineEx.OUTPUT_OGG;
						getMp3Methods().setVisible(false);
						getOggMethods().setVisible(true);
						getBitrates().setVisible(ConvertEngineEx.METHOD != 1);
						getQualities().setVisible(ConvertEngineEx.METHOD == 1);
					}
				}
			});
			// Initialize
			switch(ConvertEngineEx.OUTPUT) {
				default:
				case ConvertEngineEx.OUTPUT_WAV: {
					formats.setSelectedIndex(0);
				} break;
				case ConvertEngineEx.OUTPUT_MP3: {
					formats.setSelectedIndex(1);
					getMp3Methods().setSelectedIndex(Math.min(ConvertEngineEx.METHOD, getMp3Methods().getItemCount() - 1));
				} break;
				case ConvertEngineEx.OUTPUT_OGG: {
					if(formatss.length <= 2) {
						ConvertEngineEx.OUTPUT = ConvertEngineEx.OUTPUT_WAV;
						formats.setSelectedIndex(0);
					}
					else {
						formats.setSelectedIndex(2);
						getOggMethods().setSelectedIndex(Math.min(ConvertEngineEx.METHOD, getOggMethods().getItemCount() - 1));
					}
				} break;
			}
		}
		return formats;
	}

	private JComboBox getMp3Methods() {
		if(mp3Methods == null) {
			mp3Methods = new JComboBox(new String[]{"CBR", "ABR", "VBR"});
			mp3Methods.setVisible(false);
			mp3Methods.setBounds(new Rectangle(80, 42, 58, 18));
			mp3Methods.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ConvertEngineEx.METHOD = mp3Methods.getSelectedIndex();
					getBitrates().setVisible(ConvertEngineEx.METHOD != 2);
					getQualities().setVisible(ConvertEngineEx.METHOD == 2);
				}
			});
		}
		return mp3Methods;
	}

	private JComboBox getOggMethods() {
		if(oggMethods == null) {
			oggMethods = new JComboBox(new String[]{"ABR/CBR", "VBR"});
			oggMethods.setVisible(false);
			oggMethods.setBounds(new Rectangle(80, 42, 58, 18));
			oggMethods.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ConvertEngineEx.METHOD = oggMethods.getSelectedIndex();
					getBitrates().setVisible(ConvertEngineEx.METHOD != 1);
					getQualities().setVisible(ConvertEngineEx.METHOD == 1);
				}
			});
		}
		return oggMethods;
	}

	private JComboBox getBitrates() {
		if(bitrates == null) {
			bitrates = new JComboBox(new Integer[]{new Integer(32), new Integer(40), new Integer(48), new Integer(56),
					new Integer(64), new Integer(80), new Integer(96), new Integer(122), new Integer(128),
					new Integer(160), new Integer(192), new Integer(224), new Integer(256), new Integer(320)});
			bitrates.setVisible(false);
			bitrates.setBounds(new Rectangle(145, 42, 50, 18));
			bitrates.setSelectedIndex(8);
			bitrates.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ConvertEngineEx.BITRATE = ((Integer)bitrates.getSelectedItem()).intValue();
				}
			});
			bitrates.setSelectedItem(new Integer(ConvertEngineEx.BITRATE));
		}
		return bitrates;
	}

	private JComboBox getQualities() {
		if(qualities == null) {
			qualities = new JComboBox(new Integer[]{new Integer(0), new Integer(1), new Integer(2), new Integer(3),
					new Integer(4), new Integer(5), new Integer(6), new Integer(7), new Integer(8), new Integer(9),
					new Integer(10)});
			qualities.setVisible(false);
			qualities.setBounds(new Rectangle(145, 42, 50, 18));
			qualities.setSelectedIndex(5);
			qualities.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ConvertEngineEx.QUALITY = ((Integer)qualities.getSelectedItem()).intValue();
				}
			});
			qualities.setSelectedItem(new Integer(ConvertEngineEx.QUALITY));
		}
		return qualities;
	}

	private JFileChooser getFolder() {
		if(folder == null) {
			folder = new JFileChooser();
			folder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		return folder;
	}

	private final void sync() {
		switch(getFormats().getSelectedIndex()) {
			default:
			case 0: {
				ConvertEngineEx.OUTPUT = ConvertEngineEx.OUTPUT_WAV;
				ConvertEngineEx.METHOD = 0;
			} break;
			case 1: {
				ConvertEngineEx.OUTPUT = ConvertEngineEx.OUTPUT_MP3;
				ConvertEngineEx.METHOD = getMp3Methods().getSelectedIndex();
			} break;
			case 2: {
				ConvertEngineEx.OUTPUT = ConvertEngineEx.OUTPUT_OGG;
				ConvertEngineEx.METHOD = getOggMethods().getSelectedIndex();
			} break;
		}
		ConvertEngineEx.BITRATE = ((Integer)getBitrates().getSelectedItem()).intValue();
		ConvertEngineEx.QUALITY = ((Integer)getQualities().getSelectedItem()).intValue();
	}
	
	/*END GUI*/

	private boolean start = false;
	private Thread conversion = new Thread(){
		public void run() {
			long startTime = java.lang.System.currentTimeMillis();
			
			//If output directory don't exists
			dir.mkdirs();

			final String EXT;
			if(ConvertEngineEx.OUTPUT == ConvertEngineEx.OUTPUT_WAV) {
				EXT = ".wav";
			}
			else if(ConvertEngineEx.OUTPUT == ConvertEngineEx.OUTPUT_MP3) {
				EXT = ".mp3";
			}
			else if(ConvertEngineEx.OUTPUT == ConvertEngineEx.OUTPUT_OGG) {
				EXT = ".ogg";
			}
			else {
				EXT = "";
			}

			boolean problem = false;
			for(int i = 0; i < songs.size(); i++) {
				if(canceled) break;

				Song song = (Song)songs.get(i);
				String file = new File(dir, song.getTitle() + EXT).getPath();

				//Init convert engine
				ConvertEngineEx.init(file);
				if(ConvertEngineEx.isInitialized()) {
					if(!ConvertEngineEx.convert(song)) {
						problem = true;
						continue;
					}

					process.setText(MusicPlayerEx.lang.getString("processing") + " " + song.getTitle());
					process.setToolTipText(process.getText());

					while(!ConvertEngineEx.convertFinish && !canceled) {
						int position = ConvertEngineEx.update(song);
						getProgression().setValue(position);
					}

					ConvertEngineEx.stop(song);
					ConvertEngineEx.close();
				}
				else {
					problem = true;
				}
			}

			if(!problem && ConvertEngineEx.convertFinish) {
				Object[] args = {"" + (java.lang.System.currentTimeMillis() - startTime) / 1000};
				String format = String.format(MusicPlayerGUI_V2.lang.getString("convert_ok"), args);
				dispose(format);
			}
			else {
				if(problem) dispose(null);
			}
		}
	};

	private void dispose(String message) {
		if(message != null && !message.equals("")) {
			JOptionPane.showMessageDialog(ConversionGUI.this, message);
		}
		ConversionGUI.this.dispose();
	}
}