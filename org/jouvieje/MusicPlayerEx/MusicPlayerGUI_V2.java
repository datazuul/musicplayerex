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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;

import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.playlist.Playlists;
import org.jouvieje.MusicPlayerEx.soundengine.DspPlugin;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;

import jouvieje.util.AboutMeDialog;

abstract class MusicPlayerGUI_V2 extends JFrame {
	private static final long serialVersionUID = 1L;

	protected boolean isShown() {
		return this.getExtendedState() != JFrame.ICONIFIED //Most of the time, it is iconified => don't refresh GUI
			&& (this.isActive() || getOptions().isAlwaysOnTop());
	}

	protected static boolean undecorated = false;
	protected static int JAVA_VERSION;

	protected final static int WIDTH = 212;
	protected final static int MIN_HEIGHT = 199;
	private final static Dimension minimumSize = new Dimension(WIDTH, 136);

	private ImageIcon top = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Top.png"));
	private ImageIcon topOver = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/TopOver.png"));
	private ImageIcon up = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Up.png"));
	private ImageIcon upOver = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/UpOver.png"));
	private ImageIcon down = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Down.png"));
	private ImageIcon downOver = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/DownOver.png")); //  @jve:decl-index=0:
	private ImageIcon down2 = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Down2.png"));
	private ImageIcon down2Over = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Down2Over.png"));
	private ImageIcon bottom = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Bottom.png"));
	private ImageIcon bottomOver = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/BottomOver.png"));
	private ImageIcon right = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Right.png"));
	private ImageIcon rightOver = new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/RightOver.png")); //  @jve:decl-index=0:

	private MouseListener mainPopupListener = null;
	private MouseListener mouseListener = null;
	private MouseMotionListener mouseMotionListener = null;

	/** Components */
	private JPanel jContentPane = null;
	private Display display = null;
	private JButton previous = null;
	private JButton open = null;
	private JButton playUnpause = null;
	private JButton stop = null;
	private JButton next = null;
	private JCheckBox loop = null;
	private JProgressBar progressBar = null;
	private JSlider volume = null;
	/** File chooser */
	private JFileChooser fileChooser = null;
	/** Musics and playlists */
	private JTabbedPane tabbedPane = null;
	private JScrollPane playlistsS = null;
	private JScrollPane musicsS = null;
	private JList musicsL = null;
	private JList playlistsL = null;
	private JButton pauseUnpause = null;
	private JButton hide = null;
	private JPanel mainP = null;
	private JPanel listsP = null;
	private JCheckBox mute = null;
	/** Main Menu */
	private JPopupMenu mainPop = null;
	private JMenu openM = null;
	private JMenuItem openCdI = null;
	private JMenuItem openMusicsI = null;
	private JMenuItem openUrlI = null;
	private JMenu playM = null;
	private JMenuItem stopI = null;
	private JMenuItem playPauseI = null;
	private JMenuItem soundcardI = null;
	private JCheckBoxMenuItem playFromMemoryI = null;
	private JCheckBoxMenuItem muteCbI = null;
	private JCheckBoxMenuItem loopCbI = null;
	private JMenu settingsM = null;
	private JMenu langI = null;
	private JRadioButtonMenuItem defaultRB = null;
	private JRadioButtonMenuItem englishRB = null;
	private JRadioButtonMenuItem frenchRB = null;
	private JRadioButtonMenuItem hungaryRB = null;
	private JRadioButtonMenuItem germanRB = null;
	private JRadioButtonMenuItem greekRB = null;
	private JRadioButtonMenuItem spanishRB = null;
	private JRadioButtonMenuItem polishRB = null;
	private JRadioButtonMenuItem swedishRB = null;
	private JCheckBoxMenuItem autoSaveCb = null;
	private JCheckBoxMenuItem autoLoadCb = null;
	private JCheckBoxMenuItem autoPlayCb = null;
	private JCheckBoxMenuItem alwaysOnTopCb = null;
	private JCheckBoxMenuItem absolutePathCb = null;
	private JCheckBoxMenuItem searchNameCb = null;
	private JMenuItem checkVersionI = null;
	private JMenuItem aboutI = null;
	private JMenuItem exitI = null;
	/** Menus */
	private JPopupMenu musicsPop = null;
	private JMenu open2M = null;
	private JMenuItem openCd2I = null;
	private JMenuItem openMusics2I = null;
	private JMenuItem openUrl2I = null;
	private JMenuItem removeMusicI = null;
	private JMenuItem titleMusicI = null;
	private JMenuItem convertMusicsI = null;
	private JMenuItem sortI = null;
	private JMenuItem clearI = null;
	private JMenuItem saveMusicsI = null;

	private JPopupMenu playlistsPop = null;
	private JMenuItem addPlaylistI = null;
	private JMenuItem newPlaylistI = null;
	private JMenuItem sortPlaylistI = null;
	private JMenuItem clearPlaylistI = null;
	private JMenuItem removePlaylistI = null;
	private JMenuItem renamePlaylistI = null;
	private JMenuItem loadPlaylistI = null;
	private JMenuItem savePlaylistsI = null;

	private JPanel moveItemP = null;
	private JButton moveUp = null;
	private JButton moveTop = null;
	private JButton moveDown = null;
	private JButton moveBottom = null;
	private JMenu guiM = null;
	private JCheckBoxMenuItem hideMoveButtonsCb = null;
	private JCheckBoxMenuItem hideListsCb = null;

	/** Plugin menu item*/
	private JMenu pluginsM = null;
	private JCheckBoxMenuItem normalizeCbI = null;
	private JCheckBoxMenuItem distorsionCbI = null;
	private JCheckBoxMenuItem delayCbI = null;
	private JCheckBoxMenuItem reverbCbI = null;
	private JMenuItem vstPluginsCbI = null;

	protected MusicPlayerGUI_V2() {
		super();

		try {
			String v = System.getProperty("java.specification.version");
			JAVA_VERSION = Integer.parseInt(v.replaceFirst("1.", ""));
		}
		catch(Exception e) {
			JAVA_VERSION = 4;
		}

		System.out.println(
			" *****************************************\n" +
			" *     Music Player Ex using FMOD Ex     *\n" +
			" *****************************************\n" +
			" * Author: Jérôme JOUVIE                 *\n" +
			" * Email:  jerome.jouvie@gmail.com       *\n" +
			" * Web:    http://jerome.jouvie.free.fr/ *\n" +
			" *         http://bonzaiengine.com/      *\n" +
			" *****************************************\n");

		System.out.print("Loading settings ...");
		getOptions();
		System.out.println(getLang().getString("finish"));

		System.out.print(getLang().getString("create_interface"));
		initialize();
		if(getOptions().isAutoLoad()) {
			Point location = getOptions().getLocation();
			if(location != null) this.setLocation(location);
			if(getOptions().isMinimized()) minimize();
			else this.setSize(WIDTH, getOptions().getHeight());
			if(getOptions().isManageButtonsHided()) getMoveItemP().setVisible(false);
		}
		System.out.println(getLang().getString("finish"));

		if(undecorated) {
			this.setUndecorated(true);
		}

		if(JAVA_VERSION >= 6) {
			try {
				if(SystemTray.isSupported()) {
					MenuItem about = new MenuItem(getLang().getString("about"));
					about.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							displayAboutDialog();
						}
					});
					MenuItem exit = new MenuItem(getLang().getString("exit"));
					exit.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							closePlayer();
							System.exit(0);
						}
					});
					MenuItem play = new MenuItem(getLang().getString("play_pause"));
					play.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							Messages.PLAY_PAUSE = true;
						}
					});
					MenuItem stop = new MenuItem(getLang().getString("stop"));
					stop.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							Messages.STOP = true;
						}
					});
					Menu nextPrevious = new Menu(getLang().getString("next_previous"));
					MenuItem next = new MenuItem(getLang().getString("next_music"));
					next.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							playNext(1);
						}
					});
					MenuItem previous = new MenuItem(getLang().getString("previous_music"));
					previous.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							playNext(-1);
						}
					});
					MenuItem nextP = new MenuItem(getLang().getString("next_playlist"));
					nextP.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							playNextPlaylist(1);
						}
					});
					MenuItem previousP = new MenuItem(getLang().getString("previous_playlist"));
					previousP.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							playNextPlaylist(-1);
						}
					});
					nextPrevious.add(next);
					nextPrevious.add(previous);
					nextPrevious.addSeparator();
					nextPrevious.add(nextP);
					nextPrevious.add(previousP);
					final CheckboxMenuItem mute = new CheckboxMenuItem(getLang().getString("mute"));
					mute.addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent e) {
							getMute().setSelected(mute.getState());
						}
					});

					PopupMenu popup = new PopupMenu();
					popup.setFont(getAboutI().getFont());
					popup.add(play);
					popup.add(stop);
					popup.add(nextPrevious);
					popup.add(mute);
					popup.addSeparator();
					popup.add(about);
					popup.add(exit);

					Image icon = Toolkit.getDefaultToolkit().createImage(
							getResource("/org/jouvieje/MusicPlayerEx/media/TrayIcon.png"));
					TrayIcon trayIcon = new TrayIcon(icon, "MusicPlayerEx");
					trayIcon.addMouseListener(new MouseListener(){
						public void mouseReleased(MouseEvent e) {}
						public void mousePressed(MouseEvent e) {}
						public void mouseExited(MouseEvent e) {}
						public void mouseEntered(MouseEvent e) {}
						public void mouseClicked(MouseEvent e) {
							if(e.getClickCount() == 2) {
								System.out.println("HELLO");
								MusicPlayerGUI_V2.this.setExtendedState(JFrame.NORMAL);
							}
						}
					});
					trayIcon.setPopupMenu(popup);
					SystemTray.getSystemTray().add(trayIcon);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		this.setVisible(true);
	}

	private URL getResource(String string) {
		return getClass().getResource(string);
	}

	private void initialize() {
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getResource("/org/jouvieje/MusicPlayerEx/media/Logo.png")));
		this.setSize(212, 278);
		if(JAVA_VERSION > 4) this.setMinimumSize(minimumSize);
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				closePlayer();
			}
		});
		this.setLocationRelativeTo(null);
		this.getRootPane().addMouseListener(getMainPopupListener());
		this.getRootPane().addMouseListener(getMouseListener());
		this.getRootPane().addMouseMotionListener(getMouseMotionListener());

		getMusicsPop();
		getPlaylistsPop();
	}

	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getMainP(), BorderLayout.NORTH);
			jContentPane.add(getListsP(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	private JPanel getMainP() {
		if(mainP == null) {
			mainP = new JPanel();
			mainP.setLayout(null);
			mainP.setPreferredSize(new Dimension(0, 103));
			mainP.setMinimumSize(new Dimension(0, 103));
			mainP.add(getDisplay());
			mainP.add(getPrevious());
			mainP.add(getOpen());
			mainP.add(getPlayUnpause());
			mainP.add(getPauseUnpause());
			mainP.add(getStop());
			mainP.add(getNext());
			mainP.add(getLoop());
			mainP.add(getMute());
			mainP.add(getVolume());
			mainP.add(getProgressBar(), null);
			mainP.add(getHide(), null);
		}
		return mainP;
	}

	protected Display getDisplay() {
		if(display == null) {
			display = new Display();
			display.setLocation(3, 3);
			display.addMouseListener(getMouseListener());
			display.addMouseMotionListener(getMouseMotionListener());
			display.addMouseListener(getMainPopupListener());
		}
		return display;
	}

	protected JProgressBar getProgressBar() {
		if(progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setBounds(new Rectangle(3, 62, 176, 15));
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			progressBar.addMouseListener(getMainPopupListener());
			progressBar.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						int position = (int)(e.getX() / (float)getProgressBar().getSize().width * 100);
						position = Math.min(position, 100);
						setPosition(position);
					}
				}
			});
			progressBar.addMouseMotionListener(new MouseMotionAdapter(){
				public void mouseDragged(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						int position = (int)(e.getX() / (float)getProgressBar().getSize().width * 100);
						position = Math.min(position, 100);
						setPosition(position);
					}
				}
			});
		}
		return progressBar;
	}

	private JButton getPrevious() {
		if(previous == null) {
			previous = new JButton();
			previous.setFocusable(false);
			previous.setBounds(new Rectangle(20, 80, 20, 20));
			previous.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Previous.gif")));
			previous.setRolloverIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/PreviousOver.gif")));
			previous.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					playNext(-1);
				}
			});
		}
		return previous;
	}

	private JButton getOpen() {
		if(open == null) {
			open = new JButton();
			open.setFocusable(false);
			open.setBounds(new Rectangle(43, 79, 22, 22));
			open.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Open.gif")));
			open.setRolloverIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/OpenOver.gif")));
			open.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openMusics();
				}
			});
		}
		return open;
	}

	private JButton getPlayUnpause() {
		if(playUnpause == null) {
			playUnpause = new JButton();
			playUnpause.setFocusable(false);
			playUnpause.setBounds(new Rectangle(68, 79, 22, 22));
			playUnpause.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Play.gif")));
			playUnpause.setRolloverIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/PlayOver.gif")));
			playUnpause.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(!isEmpty()) {
						boolean playMusic = false;
						if(getTabbedPane().getSelectedIndex() == 1) {
							int index = getPlaylistsL().getSelectedIndex();
							if(index == -1) {
								index = getPLs().getCurrentIndex();
							}

							if(index != getPLs().getCurrentIndex()) {
								Messages.FORCE_AUTO_PLAY = true;
								Messages.PLAY_PLAYLIST = index;
							}
							else {
								playMusic = true;
							}
						}
						if(playMusic || getTabbedPane().getSelectedIndex() == 0) {
							int index = getMusicsL().getSelectedIndex();
							if(index == -1) {
								index = getCurrentPL().getCurrentIndex();
							}

							Messages.PLAY_INDEX = index;
						}
					}
					else {
						openMusics();
					}
				}
			});
		}
		return playUnpause;
	}

	private JButton getPauseUnpause() {
		if(pauseUnpause == null) {
			pauseUnpause = new JButton();
			pauseUnpause.setFocusable(false);
			pauseUnpause.setBounds(new Rectangle(68, 79, 22, 22));
			pauseUnpause.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Pause.gif")));
			pauseUnpause.setRolloverIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/PauseOver.gif")));
			pauseUnpause.setVisible(false);
			pauseUnpause.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Messages.PAUSE_UNPAUSE = true;
				}
			});
		}
		return pauseUnpause;
	}

	private JButton getStop() {
		if(stop == null) {
			stop = new JButton();
			stop.setFocusable(false);
			stop.setBounds(new Rectangle(93, 79, 22, 22));
			stop.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Stop.gif")));
			stop.setRolloverIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/StopOver.gif")));
			stop.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Messages.STOP = true;
				}
			});
		}
		return stop;
	}

	private JButton getNext() {
		if(next == null) {
			next = new JButton();
			next.setFocusable(false);
			next.setBounds(new Rectangle(118, 80, 20, 20));
			next.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Next.gif")));
			next.setRolloverIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/NextOver.gif")));
			next.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					playNext(1);
				}
			});
		}
		return next;
	}

	protected JCheckBox getLoop() {
		if(loop == null) {
			loop = new JCheckBox();
			loop.setSelected(false);
			loop.setBounds(new Rectangle(138, 84, 14, 14));
			loop.setBorder(null);
			loop.setContentAreaFilled(false);
			loop.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/LoopOver.gif")));
			loop.setSelectedIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Loop.gif")));
			loop.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setLoop(loop.isSelected());
					getLoopCbI().setSelected(loop.isSelected());
				}
			});
		}
		return loop;
	}

	protected JCheckBox getMute() {
		if(mute == null) {
			mute = new JCheckBox();
			mute.setBounds(new Rectangle(158, 78, 24, 24));
			mute.setBorder(null);
			mute.setContentAreaFilled(false);
			mute.setIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Mute.gif")));
			mute.setSelectedIcon(new ImageIcon(getResource("/org/jouvieje/MusicPlayerEx/media/Mute_.gif")));
			mute.addItemListener(new java.awt.event.ItemListener(){
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getOptions().setMute(mute.isSelected());
					getMuteCbI().setSelected(mute.isSelected());
					setMute(mute.isSelected());
				}
			});
		}
		return mute;
	}

	protected JSlider getVolume() {
		if(volume == null) {
			volume = new JSlider();
			volume.setOrientation(JSlider.VERTICAL);
			volume.setBounds(184, 62, 16, 38);
			volume.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					getOptions().setVolume(volume.getValue());
					setVolume(volume.getValue());
					volume.setToolTipText(getLang().getString("volume") + "=" + volume.getValue() + "%");
				}
			});
		}
		return volume;
	}

	private JPanel getListsP() {
		if(listsP == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
//			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.insets = new Insets(0, 2, 0, 2);
			listsP = new JPanel();
			listsP.setLayout(new GridBagLayout());
			listsP.add(getMoveItemP(), gridBagConstraints);
			listsP.add(getTabbedPane(), gridBagConstraints2);
		}
		return listsP;
	}

	protected JTabbedPane getTabbedPane() {
		if(tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
			tabbedPane.setFont(new Font("Dialog", Font.BOLD, 11));
			tabbedPane.addTab(null, null, getMusicsS(), null);
			tabbedPane.addTab(null, null, getPlaylistsS(), null);
			tabbedPane.addMouseMotionListener(getMouseMotionListener());
			tabbedPane.setSelectedIndex(1);
		}
		return tabbedPane;
	}

	protected JScrollPane getMusicsS() {
		if(musicsS == null) {
			JScrollBar sbh = new JScrollBar(JScrollBar.HORIZONTAL);
			sbh.setMaximumSize(new Dimension(32767, 10));
			sbh.setPreferredSize(new Dimension(55, 10));
			sbh.setMinimumSize(new Dimension(15, 10));
			JScrollBar sbv = new JScrollBar(JScrollBar.VERTICAL);
			sbv.setMaximumSize(new Dimension(10, 32767));
			sbv.setPreferredSize(new Dimension(10, 55));
			sbv.setMinimumSize(new Dimension(10, 15));
			sbv.setUnitIncrement(17);
			musicsS = new JScrollPane();
			musicsS.setViewportView(getMusicsL());
			musicsS.setHorizontalScrollBar(sbh);
			musicsS.setVerticalScrollBar(sbv);
		}
		return musicsS;
	}

	protected JList getMusicsL() {
		if(musicsL == null) {
			musicsL = new JList();
			musicsL.setDragEnabled(true);
			musicsL.setTransferHandler(new FileTransferHandler(this));
			musicsL.setCellRenderer(new MusicRenderer());
			musicsL.setFont(new Font("Dialog", Font.BOLD, 11));
			musicsL.setLayoutOrientation(JList.VERTICAL);
			musicsL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			musicsL.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					int index = getMusicsL().locationToIndex(e.getPoint());
					if(e.getClickCount() >= 2 && !isEmpty() && getCurrentPL().size() > 0) {
						if(index != -1) {
							Messages.PLAY_INDEX = index;
						}
					}
					else if(e.getClickCount() == 1 && !isEmpty() && getCurrentPL().size() > 0) {
						changeSelectedMusic();
					}
				}
			});
			musicsL.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "del");
			musicsL.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ent");
			musicsL.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spa");
			musicsL.getActionMap().put("del", new AbstractAction(){
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					remove();
				}
			});
			musicsL.getActionMap().put("ent", new AbstractAction(){
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					int index = getMusicsL().getSelectedIndex();
					if(!isEmpty() && getCurrentPL().size() > 0 && index != -1) {
						Messages.PLAY_INDEX = index;
					}
				}
			});
			musicsL.getActionMap().put("spa", new AbstractAction(){
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					if(!isEmpty()) {
						Messages.PAUSE_UNPAUSE = true;
					}
				}
			});
		}
		return musicsL;
	}

	class MusicRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			//Default cell render
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			//+ show in italic current music
			if(!isEmpty() && index == getPLs().getCurrent().getCurrentIndex()) {
				this.setFont(getFont().deriveFont(Font.ITALIC | Font.BOLD));
				this.setForeground(Color.GRAY);
			}
			return this;
		}
	}

	protected JScrollPane getPlaylistsS() {
		if(playlistsS == null) {
			JScrollBar sbh = new JScrollBar(JScrollBar.HORIZONTAL);
			sbh.setMaximumSize(new Dimension(32767, 10));
			sbh.setPreferredSize(new Dimension(55, 10));
			sbh.setMinimumSize(new Dimension(15, 10));
			JScrollBar sbv = new JScrollBar();
			sbv.setMaximumSize(new Dimension(10, 32767));
			sbv.setPreferredSize(new Dimension(10, 55));
			sbv.setMinimumSize(new Dimension(10, 15));
			sbv.setUnitIncrement(17);
			playlistsS = new JScrollPane();
			playlistsS.setViewportView(getPlaylistsL());
			playlistsS.setVerticalScrollBar(sbv);
			playlistsS.setHorizontalScrollBar(sbh);
		}
		return playlistsS;
	}

	protected JList getPlaylistsL() {
		if(playlistsL == null) {
			playlistsL = new JList();
			playlistsL.setDragEnabled(true);
			playlistsL.setTransferHandler(new FileTransferHandler(this));
			playlistsL.setCellRenderer(new PlaylistRenderer());
			playlistsL.setFont(new Font("Dialog", Font.BOLD, 11));
			playlistsL.setLayoutOrientation(JList.VERTICAL);
			playlistsL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			playlistsL.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() >= 2 && !isEmpty()) {
						int index = getPlaylistsL().locationToIndex(e.getPoint());
						if(index != -1) Messages.PLAY_PLAYLIST = index;
					}
					else if(e.getClickCount() == 1 && !isEmpty()) {
						changeSelectedPlaylist();
					}
				}
			});
			playlistsL.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "del");
			playlistsL.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ent");
			playlistsL.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spa");
			playlistsL.getActionMap().put("del", new AbstractAction(){
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					remove();
				}
			});
			playlistsL.getActionMap().put("ent", new AbstractAction(){
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					if(!isEmpty()) {
						int index = getPlaylistsL().getSelectedIndex();
						if(index != -1) {
							Messages.PLAY_PLAYLIST = index;
						}
					}
				}
			});
			playlistsL.getActionMap().put("spa", new AbstractAction(){
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					if(!isEmpty()) {
						Messages.PAUSE_UNPAUSE = true;
					}
				}
			});
		}
		return playlistsL;
	}

	class PlaylistRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if(index == getPLs().getCurrentIndex()) {
				this.setFont(getFont().deriveFont(Font.ITALIC | Font.BOLD));
				this.setForeground(Color.GRAY);
			}

			return this;
		}
	}

	private JButton getHide() {
		if(hide == null) {
			hide = new JButton();
			hide.setFocusable(false);
			hide.setContentAreaFilled(true);
			hide.setBounds(new Rectangle(3, 86, 14, 14));
			hide.setIcon(down2);
			hide.setRolloverIcon(down2Over);
			hide.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					updateMinimizeState();
				}
			});
		}
		return hide;
	}

	protected void updateMinimizeState() {
		getOptions().setMinimized(getTabbedPane().isVisible());

		if(getTabbedPane().isVisible()) {
			minimize();
		}
		else {
			maximize();
		}

		getHideListsCb().setSelected(getOptions().isMinimized());
	}

	protected void minimize() {
		MusicPlayerGUI_V2.this.setSize(minimumSize);
		getTabbedPane().setVisible(false);
		hide.setIcon(right);
		hide.setRolloverIcon(rightOver);
		MusicPlayerGUI_V2.this.validate();
	}

	protected void maximize() {
		MusicPlayerGUI_V2.this.setSize(new Dimension(WIDTH, getOptions().getHeight()));
		getTabbedPane().setVisible(true);
		hide.setIcon(down2);
		hide.setRolloverIcon(down2Over);
		MusicPlayerGUI_V2.this.validate();
	}

	protected void updateHideButtonsState() {
		getOptions().setManageButtonsHided(getMoveItemP().isVisible());
		hideItems(!getMoveItemP().isVisible());
	}

	protected void hideItems(boolean visible) {
		getMoveItemP().setVisible(visible);
	}

	private JPopupMenu getMusicsPop() {
		if(musicsPop == null) {
			musicsPop = new JPopupMenu();
			musicsPop.add(getOpen2M());
			musicsPop.add(getRemoveMusicI());
			musicsPop.addSeparator();
			musicsPop.add(getSortI());
			musicsPop.add(getClearI());
			musicsPop.addSeparator();
			musicsPop.add(getTitleMusicI());
			musicsPop.add(getConvertMusicsI());
			musicsPop.addSeparator();
			musicsPop.add(getSaveMusicsI());

			MouseListener popupListener = new MouseAdapter(){
				public void mousePressed(MouseEvent e) {
					showPopup(e);
				}
				public void mouseReleased(MouseEvent e) {
					showPopup(e);
				}
				private void showPopup(MouseEvent e) {
					if(e.isPopupTrigger()) getMusicsPop().show(e.getComponent(), e.getX(), e.getY());
				}
			};
			getMusicsL().addMouseListener(popupListener);
		}
		return musicsPop;
	}

	private JPopupMenu getPlaylistsPop() {
		if(playlistsPop == null) {
			playlistsPop = new JPopupMenu();
			playlistsPop.add(getAddPlaylistI());
			playlistsPop.add(getNewPlaylistI());
			playlistsPop.add(getRemovePlaylistI());
			playlistsPop.addSeparator();
			playlistsPop.add(getRenamePlaylistI());
			playlistsPop.addSeparator();
			playlistsPop.add(getSortPlaylistI());
			playlistsPop.add(getClearPlaylistI());
			playlistsPop.addSeparator();
			playlistsPop.add(getSavePlaylistsI());
			playlistsPop.add(getLoadPlaylistI());

			MouseListener popupListener = new MouseAdapter(){
				public void mousePressed(MouseEvent e) {
					showPopup(e);
				}
				public void mouseReleased(MouseEvent e) {
					showPopup(e);
				}
				private void showPopup(MouseEvent e) {
					if(e.isPopupTrigger()) getPlaylistsPop().show(e.getComponent(), e.getX(), e.getY());
				}
			};
			getPlaylistsL().addMouseListener(popupListener);
		}
		return playlistsPop;
	}

	private JPopupMenu getMainPop() {
		if(mainPop == null) {
			mainPop = new JPopupMenu();
			mainPop.add(getOpenM());
			mainPop.add(getPlayM());
			mainPop.add(getPluginsM());
			mainPop.add(getGuiM());
			mainPop.add(getSettingsM());
			mainPop.add(getLangI());
			mainPop.add(getCheckVersionI());
			mainPop.add(getAboutI());
			mainPop.add(getExitI());
		}
		return mainPop;
	}

	private JMenu getOpenM() {
		if(openM == null) {
			openM = new JMenu();
			openM.add(getOpenMusicsI());
			openM.add(getOpenCdI());
			openM.add(getOpenUrlI());
		}
		return openM;
	}

	private JMenuItem getOpenMusicsI() {
		if(openMusicsI == null) {
			openMusicsI = new JMenuItem();
			openMusicsI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openMusics();
				}
			});
		}
		return openMusicsI;
	}

	private JMenuItem getOpenCdI() {
		if(openCdI == null) {
			openCdI = new JMenuItem();

			openCdI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Thread open = new Thread(){
						public void run() {
							openCD();
						}
					};
					open.start();
				}
			});
		}
		return openCdI;
	}

	private JMenuItem getOpenUrlI() {
		if(openUrlI == null) {
			openUrlI = new JMenuItem();
			openUrlI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openURL();
				}
			});
		}
		return openUrlI;
	}

	private JMenu getPlayM() {
		if(playM == null) {
			playM = new JMenu();
			playM.add(getSoundcardI());
			playM.add(getPlayFromMemoryI());
			playM.addSeparator();
			playM.add(getPlayPauseI());
			playM.add(getStopI());
			playM.add(getLoopCbI());
			playM.add(getMuteCbI());
		}
		return playM;
	}

	private JMenuItem getSoundcardI() {
		if(soundcardI == null) {
			soundcardI = new JMenuItem();
			soundcardI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					new SoundcardGUI(MusicPlayerGUI_V2.this).setVisible(true);
				}
			});
		}
		return soundcardI;
	}

	protected JCheckBoxMenuItem getPlayFromMemoryI() {
		if(playFromMemoryI == null) {
			playFromMemoryI = new JCheckBoxMenuItem();
			playFromMemoryI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setPlayFromMemory(playFromMemoryI.isSelected());
				}
			});
		}
		return playFromMemoryI;
	}

	private JMenuItem getPlayPauseI() {
		if(playPauseI == null) {
			playPauseI = new JMenuItem();
			playPauseI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Messages.PLAY_PAUSE = true;
				}
			});
		}
		return playPauseI;
	}

	private JMenuItem getStopI() {
		if(stopI == null) {
			stopI = new JMenuItem();
			stopI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Messages.STOP = true;
				}
			});
		}
		return stopI;
	}

	private JCheckBoxMenuItem getMuteCbI() {
		if(muteCbI == null) {
			muteCbI = new JCheckBoxMenuItem();
			muteCbI.addItemListener(new java.awt.event.ItemListener(){
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if(muteCbI.isSelected() != getMute().isSelected()) {
						getMute().setSelected(muteCbI.isSelected());
					}
				}
			});
		}
		return muteCbI;
	}

	private JCheckBoxMenuItem getLoopCbI() {
		if(loopCbI == null) {
			loopCbI = new JCheckBoxMenuItem();
			loopCbI.addItemListener(new java.awt.event.ItemListener(){
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if(loopCbI.isSelected() != getLoop().isSelected()) {
						getLoop().setSelected(loopCbI.isSelected());
					}
				}
			});
		}
		return loopCbI;
	}

	private JMenu getSettingsM() {
		if(settingsM == null) {
			settingsM = new JMenu();
			settingsM.add(getAutoLoadCb());
			settingsM.add(getAutoSaveCb());
			settingsM.add(getAutoPlayCb());
			settingsM.add(getAbsolutePathCb());
			settingsM.add(getSearchNameCb());
		}
		return settingsM;
	}

	protected JMenu getLangI() {
		if(langI == null) {
			langI = new JMenu();
			langI.add(getDefaultRB());
			langI.add(getEnglishRB());
			langI.add(getFrenchRB());
			langI.add(getHungaryRB());
			langI.add(getGermanRB());
			langI.add(getGreekRB());
			langI.add(getSpanishRB());
			langI.add(getPolishRB());
			langI.add(getSwedishRB());

			ButtonGroup group = new ButtonGroup();
			group.add(getDefaultRB());
			group.add(getEnglishRB());
			group.add(getFrenchRB());
			group.add(getHungaryRB());
			group.add(getGermanRB());
			group.add(getGreekRB());
			group.add(getSpanishRB());
			group.add(getPolishRB());
			group.add(getSwedishRB());
		}
		return langI;
	}

	protected JRadioButtonMenuItem getDefaultRB() {
		if(defaultRB == null) {
			defaultRB = new JRadioButtonMenuItem();
			defaultRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(defaultRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_DEFAULT);
						lang = null;
						getLang();
					}
				}
			});
		}
		return defaultRB;
	}

	protected JRadioButtonMenuItem getEnglishRB() {
		if(englishRB == null) {
			englishRB = new JRadioButtonMenuItem();
			englishRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(englishRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_EN);
						lang = null;
						getLang();
					}
				}
			});
		}
		return englishRB;
	}

	protected JRadioButtonMenuItem getFrenchRB() {
		if(frenchRB == null) {
			frenchRB = new JRadioButtonMenuItem();
			frenchRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(frenchRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_FR);
						lang = null;
						getLang();
					}
				}
			});
		}
		return frenchRB;
	}

	protected JRadioButtonMenuItem getHungaryRB() {
		if(hungaryRB == null) {
			hungaryRB = new JRadioButtonMenuItem();
			hungaryRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(hungaryRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_HU);
						lang = null;
						getLang();
					}
				}
			});
		}
		return hungaryRB;
	}

	protected JRadioButtonMenuItem getGermanRB() {
		if(germanRB == null) {
			germanRB = new JRadioButtonMenuItem();
			germanRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(germanRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_DE);
						lang = null;
						getLang();
					}
				}
			});
		}
		return germanRB;
	}

	protected JRadioButtonMenuItem getGreekRB() {
		if(greekRB == null) {
			greekRB = new JRadioButtonMenuItem();
			greekRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(greekRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_EL);
						lang = null;
						getLang();
					}
				}
			});
		}
		return greekRB;
	}

	protected JRadioButtonMenuItem getSpanishRB() {
		if(spanishRB == null) {
			spanishRB = new JRadioButtonMenuItem();
			spanishRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(spanishRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_ES);
						lang = null;
						getLang();
					}
				}
			});
		}
		return spanishRB;
	}

	protected JRadioButtonMenuItem getPolishRB() {
		if(polishRB == null) {
			polishRB = new JRadioButtonMenuItem();
			polishRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(polishRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_PL);
						lang = null;
						getLang();
					}
				}
			});
		}
		return polishRB;
	}

	protected JRadioButtonMenuItem getSwedishRB() {
		if(swedishRB == null) {
			swedishRB = new JRadioButtonMenuItem();
			swedishRB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(swedishRB.isSelected()) {
						getOptions().setLanguage(Options.LANG_SV);
						lang = null;
						getLang();
					}
				}
			});
		}
		return swedishRB;
	}
	
	protected JCheckBoxMenuItem getAutoLoadCb() {
		if(autoLoadCb == null) {
			autoLoadCb = new JCheckBoxMenuItem();
			autoLoadCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setAutoLoad(autoLoadCb.isSelected());
				}
			});
		}
		return autoLoadCb;
	}

	protected JCheckBoxMenuItem getAutoSaveCb() {
		if(autoSaveCb == null) {
			autoSaveCb = new JCheckBoxMenuItem();
			autoSaveCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setAutoSave(autoSaveCb.isSelected());
				}
			});
		}
		return autoSaveCb;
	}

	protected JCheckBoxMenuItem getAutoPlayCb() {
		if(autoPlayCb == null) {
			autoPlayCb = new JCheckBoxMenuItem();
			autoPlayCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setAutoPlay(autoPlayCb.isSelected());
				}
			});
		}
		return autoPlayCb;
	}

	protected JCheckBoxMenuItem getAlwaysOnTopCb() {
		if(alwaysOnTopCb == null) {
			alwaysOnTopCb = new JCheckBoxMenuItem();
			alwaysOnTopCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setAlwaysOnTop(alwaysOnTopCb.isSelected());
					if(JAVA_VERSION > 4) MusicPlayerGUI_V2.this.setAlwaysOnTop(alwaysOnTopCb.isSelected());
				}
			});
		}
		return alwaysOnTopCb;
	}

	protected JCheckBoxMenuItem getAbsolutePathCb() {
		if(absolutePathCb == null) {
			absolutePathCb = new JCheckBoxMenuItem();
			absolutePathCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setAbsolutePath(absolutePathCb.isSelected());
				}
			});
		}
		return absolutePathCb;
	}

	protected JCheckBoxMenuItem getSearchNameCb() {
		if(searchNameCb == null) {
			searchNameCb = new JCheckBoxMenuItem();
			searchNameCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					getOptions().setSearchMusicName(searchNameCb.isSelected());
				}
			});
		}
		return searchNameCb;
	}

	private JMenuItem getCheckVersionI() {
		if(checkVersionI == null) {
			checkVersionI = new JMenuItem();
			checkVersionI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					try {
						if(Desktop.isDesktopSupported()) {
							Desktop desktop = Desktop.getDesktop();
							if(desktop.isSupported(Desktop.Action.BROWSE)) {
								desktop.browse(new URI("http://jerome.jouvie.free.fr/index.php"));
							}
						}
					} catch(Throwable t) {}
				}
			});
		}
		return checkVersionI;
	}

	private JMenuItem getAboutI() {
		if(aboutI == null) {
			aboutI = new JMenuItem();
			aboutI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					displayAboutDialog();
				}
			});
		}
		return aboutI;
	}

	private void displayAboutDialog() {
		String translator = getLang().getString("translator");
		String site = getLang().getString("translator_site");
		String mail = getLang().getString("translator_mail");
		String[] format = new String[]{"Lang translator: " + translator,
				"Web: <font color=\"#639ACE\"><a href=\"" + site + "\">" + site + "</a></font>",
				"E-mail: <font color=\"#639ACE\"><a href=\"mailto:" + mail + "\">" + mail + "</a></font>"};

		//TODO write a 'better' dialog
		new AboutMeDialog(MusicPlayerGUI_V2.this, true, MusicPlayerGUI_V2.this.getTitle(), format).setVisible(true);
	}

	private JMenuItem getExitI() {
		if(exitI == null) {
			exitI = new JMenuItem();
			exitI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					closePlayer();
					System.exit(0);
				}
			});
		}
		return exitI;
	}

	private JMenu getOpen2M() {
		if(open2M == null) {
			open2M = new JMenu();
			open2M.add(getOpenMusics2I());
			open2M.add(getOpenCd2I());
			open2M.add(getOpenUrl2I());
		}
		return open2M;
	}

	private JMenuItem getOpenMusics2I() {
		if(openMusics2I == null) {
			openMusics2I = new JMenuItem();
			openMusics2I.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openMusics();
				}
			});
		}
		return openMusics2I;
	}

	private JMenuItem getOpenCd2I() {
		if(openCd2I == null) {
			openCd2I = new JMenuItem();

			openCd2I.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Thread open = new Thread(){
						public void run() {
							openCD();
						}
					};
					open.start();
				}
			});
		}
		return openCd2I;
	}

	private JMenuItem getOpenUrl2I() {
		if(openUrl2I == null) {
			openUrl2I = new JMenuItem();
			openUrl2I.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					openURL();
				}
			});
		}
		return openUrl2I;
	}

	private JMenuItem getRemoveMusicI() {
		if(removeMusicI == null) {
			removeMusicI = new JMenuItem();
			removeMusicI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					remove();
				}
			});
		}
		return removeMusicI;
	}

	private JMenuItem getTitleMusicI() {
		if(titleMusicI == null) {
			titleMusicI = new JMenuItem();
			titleMusicI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					rename();
				}
			});
		}
		return titleMusicI;
	}

	private JMenuItem getConvertMusicsI() {
		if(convertMusicsI == null) {
			convertMusicsI = new JMenuItem();
			convertMusicsI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveItem();
				}
			});
		}
		return convertMusicsI;
	}

	private JMenuItem getSortI() {
		if(sortI == null) {
			sortI = new JMenuItem();
			sortI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					sortList();
				}
			});
		}
		return sortI;
	}

	private JMenuItem getClearI() {
		if(clearI == null) {
			clearI = new JMenuItem();
			clearI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					clearList();
				}
			});
		}
		return clearI;
	}

	private JMenuItem getSaveMusicsI() {
		if(saveMusicsI == null) {
			saveMusicsI = new JMenuItem();
			saveMusicsI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveList();
				}
			});
		}
		return saveMusicsI;
	}

	private JMenuItem getAddPlaylistI() {
		if(addPlaylistI == null) {
			addPlaylistI = new JMenuItem();
			addPlaylistI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					add();
				}
			});
		}
		return addPlaylistI;
	}

	private JMenuItem getNewPlaylistI() {
		if(newPlaylistI == null) {
			newPlaylistI = new JMenuItem();
			newPlaylistI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					newItem();
				}
			});
		}
		return newPlaylistI;
	}

	private JMenuItem getRemovePlaylistI() {
		if(removePlaylistI == null) {
			removePlaylistI = new JMenuItem();
			removePlaylistI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					remove();
				}
			});
		}
		return removePlaylistI;
	}

	private JMenuItem getRenamePlaylistI() {
		if(renamePlaylistI == null) {
			renamePlaylistI = new JMenuItem();
			renamePlaylistI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					rename();
				}
			});
		}
		return renamePlaylistI;
	}

	private JMenuItem getSavePlaylistsI() {
		if(savePlaylistsI == null) {
			savePlaylistsI = new JMenuItem();
			savePlaylistsI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					saveList();
				}
			});
		}
		return savePlaylistsI;
	}

	private JMenuItem getLoadPlaylistI() {
		if(loadPlaylistI == null) {
			loadPlaylistI = new JMenuItem();
			loadPlaylistI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					load();
				}
			});
		}
		return loadPlaylistI;
	}

	private JMenuItem getSortPlaylistI() {
		if(sortPlaylistI == null) {
			sortPlaylistI = new JMenuItem();
			sortPlaylistI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					sortList();
				}
			});
		}
		return sortPlaylistI;
	}

	private JMenuItem getClearPlaylistI() {
		if(clearPlaylistI == null) {
			clearPlaylistI = new JMenuItem();
			clearPlaylistI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					clearList();
				}
			});
		}
		return clearPlaylistI;
	}

	protected JFileChooser getFileChooser() {
		if(fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setControlButtonsAreShown(true);
			fileChooser.setSelectedFile(FileSystemView.getFileSystemView().getHomeDirectory());
			fileChooser.setApproveButtonMnemonic(KeyEvent.VK_O);
		}
		return fileChooser;
	}

	/*Tabbed pane*/

	protected boolean isMusicsShown() {
		return getTabbedPane().getSelectedIndex() == 0;
	}

	protected void showMusicsList() {
		getTabbedPane().setSelectedIndex(0);
	}

	protected boolean isPlaylistsShown() {
		return getTabbedPane().getSelectedIndex() == 1;
	}

	protected void showPlaylistsList() {
		getTabbedPane().setSelectedIndex(1);
	}

	/**********
	 * Events *
	 **********/

	private void add() {
		if(getTabbedPane().getSelectedIndex() == 0) {
			openMusics();
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			openPlaylists();
		}
	}

	private void newItem() {
		if(getTabbedPane().getSelectedIndex() == 1) {
			newEmptyPL();
		}
	}

	private void rename() {
		if(isEmpty()) {
			return;
		}

		if(getTabbedPane().getSelectedIndex() == 0) {
			if(getMusicsL().getSelectedIndex() != -1) {
				renameMusic(getCurrentPL(), getMusicsL().getSelectedIndex());
			}
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			if(getPlaylistsL().getSelectedIndex() != -1) {
				renamePL(getPLs().get(getPlaylistsL().getSelectedIndex()));
			}
		}
	}

	private void remove() {
		if(isEmpty()) {
			return;
		}

		if(getTabbedPane().getSelectedIndex() == 0) {
			//get the index selected in the list of musics
			int[] indices = getMusicsL().getSelectedIndices();
			if(indices != null && indices.length > 0) removeMusics(getCurrentPL(), indices);
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			//get the index selected in the list of playlists
			int[] indices = getPlaylistsL().getSelectedIndices();
			if(indices != null && indices.length > 0) removePLS(indices);
		}
	}

	private void sortList() {
		if(isEmpty()) {
			return;
		}

		if(getTabbedPane().getSelectedIndex() == 0) {
			//Sort the playlist and refresh the list (select the current music playing)
			int index = getCurrentPL().sort(); //returns the index of the current music
			Messages.ENSURE_M_VISIBLE = index;
			Messages.SELECT_M = index;
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			//Sort playlists by names
			int index = getPLs().sort(); //returns the index of the current playlist
			Messages.ENSURE_PL_VISIBLE = index;
			Messages.SELECT_PL = index;
		}
	}

	private void clearList() {
		if(isEmpty()) {
			return;
		}

		if(getTabbedPane().getSelectedIndex() == 0) {
			clearPL(getCurrentPL());
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			clearEnv();
		}
	}

	private void saveItem() {
		if(isEmpty()) {
			return;
		}

		if(getTabbedPane().getSelectedIndex() == 0) {
			if(getMusicsL().getSelectedIndex() != -1) {
				convertMusics(getCurrentPL(), getMusicsL().getSelectedIndices());
			}
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			if(getPlaylistsL().getSelectedIndex() != -1) {
				savePlaylist(getPLs().get(getPlaylistsL().getSelectedIndex()));
			}
		}
	}

	private void saveList() {
		if(isEmpty()) {
			return;
		}

		if(getTabbedPane().getSelectedIndex() == 0) {
			savePlaylist(getCurrentPL());
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			saveEnv();
		}
	}

	private void load() {
		if(getTabbedPane().getSelectedIndex() == 0) {
			openPlaylists();
		}
		else if(getTabbedPane().getSelectedIndex() == 1) {
			selectEnvFilters();
			int choice = getFileChooser().showOpenDialog(this);

			if(choice == JFileChooser.APPROVE_OPTION) {
				File env = getFileChooser().getSelectedFile();
				loadEnv(env);
			}
		}
	}

	/*Language support*/

	public static ResourceBundle lang = null; //  @jve:decl-index=0:

	public ResourceBundle getLang() {
		if(lang == null) {
			Locale locale = null;
			switch(getOptions().getLanguage()) {
				default:
				case Options.LANG_DEFAULT:
					locale = Locale.getDefault();
					break;
				case Options.LANG_EN:
					locale = new Locale("en");
					break;
				case Options.LANG_FR:
					locale = new Locale("fr");
					break;
				case Options.LANG_HU:
					locale = new Locale("hu");
					break;
				case Options.LANG_DE:
					locale = new Locale("de");
					break;
				case Options.LANG_EL:
					locale = new Locale("el");
					break;
				case Options.LANG_ES:
					locale = new Locale("es");
					break;
				case Options.LANG_PL:
					locale = new Locale("pl");
					break;
				case Options.LANG_SV:
					locale = new Locale("sv");
					break;
			}
			
			final PropertyResourceBundle bundle = (PropertyResourceBundle)ResourceBundle.getBundle("org.jouvieje.MusicPlayerEx.lang.player", locale);
			lang = new ResourceBundle() {
				public Enumeration getKeys() {
					return bundle.getKeys();
				}
				protected Object handleGetObject(String key) {
					String value = (String)bundle.handleGetObject(key);
					try {
						return new String(value.getBytes("ISO-8859-1"), "UTF-8");
					} catch(Exception e) {
						return key;
					}
				}
			};
			
			applyLang();
		}
		return lang;
	}

	protected void applyLang() {
		this.setTitle(getLang().getString("title"));
		getDisplay().setDisplayText(getLang().getString("player_init"));
		getProgressBar().setToolTipText(getLang().getString("music_progression"));
		getPrevious().setToolTipText(getLang().getString("previous"));
		getOpen().setToolTipText(getLang().getString("open"));
		getPlayUnpause().setToolTipText(getLang().getString("play_unpause"));
		getPauseUnpause().setToolTipText(getLang().getString("pause_unpause"));
		getStop().setToolTipText(getLang().getString("stop"));
		getNext().setToolTipText(getLang().getString("next"));
		getLoop().setToolTipText(getLang().getString("loop"));
		getMute().setToolTipText(getLang().getString("mute_unmute"));
		getVolume().setToolTipText(getLang().getString("volume") + "=" + volume.getValue() + "%");
		getHide().setToolTipText(getLang().getString("hide_show_playlists"));
		getConvertMusicsI().setText(getLang().getString("convert_musics"));
		getTitleMusicI().setText(getLang().getString("music_title"));
		getRemoveMusicI().setText(getLang().getString("remove_musics"));
		getClearI().setText(getLang().getString("remove_all"));
		getSaveMusicsI().setText(getLang().getString("save_playlist"));
		getSortI().setText(getLang().getString("sort_name"));
		getAddPlaylistI().setText(getLang().getString("add_playlists"));
		getRenamePlaylistI().setText(getLang().getString("playlist_name"));
		getLoadPlaylistI().setText(getLang().getString("load_env"));
		getSavePlaylistsI().setText(getLang().getString("save_env"));
		getClearPlaylistI().setText(getLang().getString("remove_all"));
		getRemovePlaylistI().setText(getLang().getString("remove_playlists"));
		getNewPlaylistI().setText(getLang().getString("new_playlist"));
		getSortPlaylistI().setText(getLang().getString("sort_name"));
		getFileChooser().setName(getLang().getString("file_chooser"));
		getFileChooser().setDialogTitle(getLang().getString("file_chooser"));
		getOpenCdI().setText(getLang().getString("open_cd"));
		getOpenCd2I().setText(getLang().getString("open_cd"));
		getOpenMusicsI().setText(getLang().getString("open_musics"));
		getOpenMusics2I().setText(getLang().getString("open_musics"));
		getOpenUrlI().setText(getLang().getString("open_url"));
		getOpenUrl2I().setText(getLang().getString("open_url"));
		getSoundcardI().setText(getLang().getString("soundcard"));
		getPlayFromMemoryI().setText(getLang().getString("play_from_memory"));
		getPlayPauseI().setText(getLang().getString("play_pause"));
		getStopI().setText(getLang().getString("stop"));
		getMuteCbI().setText(getLang().getString("mute"));
		getLoopCbI().setText(getLang().getString("loop"));
		getExitI().setText(getLang().getString("exit"));
		getAutoLoadCb().setText(getLang().getString("auto_load"));
		getAlwaysOnTopCb().setText(getLang().getString("always_on_top"));
		getAutoSaveCb().setText(getLang().getString("auto_save"));
		getAbsolutePathCb().setText(getLang().getString("absolute_path"));
		getAutoPlayCb().setText(getLang().getString("auto_play"));
		getSearchNameCb().setText(getLang().getString("search_name"));
		getTabbedPane().setTitleAt(0, getLang().getString("musics"));
		getTabbedPane().setTitleAt(1, getLang().getString("playlists"));
		getOpenM().setText(getLang().getString("open"));
		getOpen2M().setText(getLang().getString("open"));
		getPlayM().setText(getLang().getString("play"));
		getCheckVersionI().setText(getLang().getString("check_version"));
		getAboutI().setText(getLang().getString("about"));
		getSettingsM().setText(getLang().getString("settings"));
		getLangI().setText(getLang().getString("lang"));
		getDefaultRB().setText(getLang().getString("default"));
		getEnglishRB().setText(getLang().getString("english"));
		getFrenchRB().setText(getLang().getString("french"));
		getHungaryRB().setText(getLang().getString("hungarian"));
		getGermanRB().setText(getLang().getString("german"));
		getGreekRB().setText(getLang().getString("greek"));
		getSpanishRB().setText(getLang().getString("spanish"));
		getPolishRB().setText(getLang().getString("polish"));
		getSwedishRB().setText(getLang().getString("swedish"));

		getMoveTop().setToolTipText(getLang().getString("top"));
		getMoveUp().setToolTipText(getLang().getString("up"));
		getMoveDown().setToolTipText(getLang().getString("down"));
		getMoveBottom().setToolTipText(getLang().getString("bottom"));
		getHideMoveButtonsCb().setText(getLang().getString("hide_move_buttons"));
		getGuiM().setText(getLang().getString("gui"));
		getHideListsCb().setText(getLang().getString("hide_playlists"));

		getPluginsM().setText(getLang().getString("plugins"));
		getNormalizeCbI().setText(getLang().getString("normalize"));
		getDistorsionCbI().setText(getLang().getString("distorsion"));
		getDelayCbI().setText(getLang().getString("delay"));
		getReverbCbI().setText(getLang().getString("reverb"));
		getVSTPluginsCbI().setText(getLang().getString("vst_plugins"));
	}

	protected void updatePlayPause(boolean playing) {
		if(playing) {
			/*
			 * Active Pause button only if :
			 *  selected music != current
			 * or
			 *  current music is playing
			 */
			getPlayUnpause().setVisible(false);
			getPauseUnpause().setVisible(true);
		}
		else {
			getPlayUnpause().setVisible(true);
			getPauseUnpause().setVisible(false);
		}
	}

	private MouseListener getMainPopupListener() {
		if(mainPopupListener == null) {
			mainPopupListener = new MouseAdapter(){
				public void mousePressed(MouseEvent e) {
					showPopup(e);
				}

				public void mouseReleased(MouseEvent e) {
					showPopup(e);
				}

				private void showPopup(MouseEvent e) {
					if(e.isPopupTrigger()) getMainPop().show(e.getComponent(), e.getX(), e.getY());
				}
			};
		}
		return mainPopupListener;

	}

	private Point start = null;
	private boolean resizeMode = false;

	private MouseListener getMouseListener() {
		if(mouseListener == null) {
			mouseListener = new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						if(e.getClickCount() == 1) {
							if(e.getSource() == getDisplay()) getDisplay().changeDisplayMode();
						}
						else if(e.getClickCount() == 2) {
							if(e.getSource() == getDisplay()) getDisplay().discardDisplayMode();
							updateMinimizeState();
						}
					}
				}

				public void mousePressed(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						if(!resizeMode) MusicPlayerGUI_V2.this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
						start = e.getPoint();
					}
				}

				public void mouseReleased(MouseEvent e) {
					if(start != null) {
						MusicPlayerGUI_V2.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						start = null;
					}
				}
			};
		}
		return mouseListener;
	}

	private MouseMotionListener getMouseMotionListener() {
		if(mouseMotionListener == null) {
			mouseMotionListener = new MouseMotionAdapter(){
				public void mouseDragged(MouseEvent e) {
					if(start != null) {
						if(resizeMode) {
							int delta = (int)(e.getPoint().getY() - start.getY());
							int height = getHeight() + delta;
							int newHeight = height < MIN_HEIGHT ? MIN_HEIGHT : height;
							if(newHeight > MIN_HEIGHT) {
								start = e.getPoint();
							}

							if(getHeight() != newHeight) {
								MusicPlayerGUI_V2.this.setSize(new Dimension(WIDTH, newHeight));
								MusicPlayerGUI_V2.this.validate();
								getOptions().setHeight(newHeight);
							}
						}
						else {
							Point current = e.getPoint();
							Point old = MusicPlayerGUI_V2.this.getLocation();
							Point location = new Point(old.x + current.x - start.x, old.y + current.y - start.y);
							if(!location.equals(old)) {
								MusicPlayerGUI_V2.this.setLocation(location);
								//Don't need of "start = current;" because the frame is translated
							}
						}
					}
				}

				public void mouseMoved(MouseEvent e) {
					if((e.getPoint().getY() > getHeight() - 10)
							&& (e.getPoint().getX() > 20 || e.getPoint().getX() < getWidth() - 20)) {
						if(!resizeMode && !getOptions().isMinimized()) {
							MusicPlayerGUI_V2.this.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
							resizeMode = true;
						}
					}
					else if(resizeMode && e.getButton() != MouseEvent.BUTTON1) {
						MusicPlayerGUI_V2.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						resizeMode = false;
					}
				}
			};
		}
		return mouseMotionListener;
	}

	/* Pre implementation*/

	protected void openPlaylists() {
		selectPlaylistFilters();
		int choice = getFileChooser().showOpenDialog(this);

		if(choice == JFileChooser.APPROVE_OPTION) {
			openPLs(getFileChooser().getSelectedFiles());
		}
	}

	/* Dialog boxes */

	protected boolean askEraseExisting(String file) {
		if(new File(file).exists()) {
			int result = JOptionPane.showConfirmDialog(this, getLang().getString("erase_file"), getLang().getString(
					"erasing_file"), JOptionPane.OK_CANCEL_OPTION);
			if(result != JOptionPane.OK_OPTION) {
				Messages.MESSAGE = getLang().getString("aborded");
				return false;
			}
		}
		return true;
	}

	protected boolean askClearList() {
		int result = JOptionPane.showConfirmDialog(this, getLang().getString("clear_list"), getLang().getString(
				"clearing_list"), JOptionPane.OK_CANCEL_OPTION);
		if(result != JOptionPane.OK_OPTION) {
			Messages.MESSAGE = getLang().getString("aborded");
			return false;
		}
		return true;
	}

	protected boolean askRemoveItems() {
		int result = JOptionPane.showConfirmDialog(this, getLang().getString("remove_item"), getLang().getString(
				"removing_item"), JOptionPane.OK_CANCEL_OPTION);
		if(result != JOptionPane.OK_OPTION) {
			Messages.MESSAGE = getLang().getString("aborded");
			return false;
		}
		return true;
	}

	/* File chooser */

	protected abstract MyFileFilter getSupportedMusicFilter();
	protected abstract MyFileFilter getSupportedFileFilter();
	protected abstract FileFilter getSoundFilter();
	protected abstract FileFilter getStreamFilter();
	protected abstract FileFilter getVstFilter();

	private FileFilter envFilter = null;
	private FileFilter plsFilter = null;
	private FileFilter m3uFilter = null;
	private MyFileFilter supportedPlaylistFilter = null;

	protected FileFilter getPlsFilter() {
		if(plsFilter == null) {
			plsFilter = new FileFilter(){
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith("pls") || f.isDirectory();
				}

				public String getDescription() {
					return "Pls " + getLang().getString("playlist") + " (pls)";
				}
			};
		}
		return plsFilter;
	}

	protected FileFilter getM3uFilter() {
		if(m3uFilter == null) {
			m3uFilter = new FileFilter(){
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith("m3u") || f.isDirectory();
				}

				public String getDescription() {
					return "M3u " + getLang().getString("playlist") + " (m3u)";
				}
			};
		}
		return m3uFilter;
	}

	protected FileFilter getEnvFilter() {
		if(envFilter == null) {
			envFilter = new FileFilter(){
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith("env") || f.isDirectory();
				}

				public String getDescription() {
					return getLang().getString("env_file") + " (env)";
				}
			};
		}
		return envFilter;
	}

	protected MyFileFilter getSupportedPlaylistFilter() {
		if(supportedPlaylistFilter == null) {
			supportedPlaylistFilter = new MyFileFilter(new String[]{"pls", "m3u"}, getLang().getString(
					"supported_playlist"), false);
		}
		return supportedPlaylistFilter;
	}

	protected void selectPlaylistFilters() {
		getFileChooser().setDialogTitle(getLang().getString("choose_playlist"));
		getFileChooser().setMultiSelectionEnabled(true);
		getFileChooser().resetChoosableFileFilters();
		getFileChooser().addChoosableFileFilter(getM3uFilter());
		getFileChooser().addChoosableFileFilter(getPlsFilter());
	}

	protected void selectEnvFilters() {
		getFileChooser().setDialogTitle(getLang().getString("choose_env"));
		getFileChooser().setMultiSelectionEnabled(false);
		getFileChooser().resetChoosableFileFilters();
		getFileChooser().addChoosableFileFilter(getEnvFilter());
	}

	protected void selectMusicFilters() {
		getFileChooser().setDialogTitle(getLang().getString("choose_music"));
		getFileChooser().setMultiSelectionEnabled(true);
		getFileChooser().resetChoosableFileFilters();
		getFileChooser().addChoosableFileFilter(getSupportedFileFilter());
		getFileChooser().addChoosableFileFilter(getSupportedMusicFilter());
		getFileChooser().addChoosableFileFilter(getSoundFilter());
		getFileChooser().addChoosableFileFilter(getStreamFilter());
	}

	protected void selectVSTPluginsFilters() {
		getFileChooser().setDialogTitle(getLang().getString("vst_plugins"));
		getFileChooser().setMultiSelectionEnabled(false);
		getFileChooser().resetChoosableFileFilters();
		getFileChooser().addChoosableFileFilter(getVstFilter());
	}

	/********************
	 * Abstract methods *
	 ********************/

	protected abstract void openMusics();
	protected abstract void openMedias(Vector playlists, Vector musics);
	protected abstract void openCD();
	protected abstract void openURL();
	protected abstract void convertMusics(Playlist playlist, int[] indices);
	protected abstract void playNext(int offset);
	protected abstract void playNextPlaylist(int offset);

	//State
	protected abstract void setVolume(int volume);
	protected abstract int setPosition(int position);
	protected abstract void setMute(boolean mute);
	protected abstract void changeSelectedMusic();
	protected abstract void changeSelectedPlaylist();

	//Getter
	protected abstract Options getOptions();
	protected abstract void closePlayer();
	protected abstract Playlists getPLs();
	protected abstract Playlist getCurrentPL();
	protected abstract boolean isEmpty();

	//Operations
	protected abstract void newEmptyPL();
	protected abstract void renameMusic(Playlist playlist, int musicIndex);
	protected abstract boolean renamePL(Playlist playlist);
	protected abstract void removeMusics(Playlist playlist, int[] indices);
	protected abstract void removePLS(int[] indices);
	protected abstract void clearPL(Playlist p);
	protected abstract void clearEnv();
	protected abstract void openPLs(File[] files);
	protected abstract void loadEnv(File file);
	protected abstract void savePlaylist(Playlist p);
	protected abstract void saveEnv();

	private JPanel getMoveItemP() {
		if(moveItemP == null) {
			moveItemP = new JPanel();
			moveItemP.setLayout(null);
			moveItemP.setMinimumSize(new Dimension(15, 0));
			moveItemP.setMaximumSize(new Dimension(15, 0));
			moveItemP.add(getMoveTop(), null);
			moveItemP.add(getMoveUp(), null);
			moveItemP.add(getMoveDown(), null);
			moveItemP.add(getMoveBottom(), null);
		}
		return moveItemP;
	}

	private JButton getMoveTop() {
		if(moveTop == null) {
			moveTop = new JButton();
			moveTop.setFocusable(false);
			moveTop.setContentAreaFilled(true);
			moveTop.setBounds(new Rectangle(1, 0, 14, 14));
			moveTop.setIcon(top);
			moveTop.setRolloverIcon(topOver);
			moveTop.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(getTabbedPane().getSelectedIndex() == 0) {
						int index = getMusicsL().getSelectedIndex();
						if(index != -1) getCurrentPL().moveFirst(index);
					}
					else {
						int index = getPlaylistsL().getSelectedIndex();
						if(index != -1) getPLs().moveFirst(index);
					}
				}
			});
		}
		return moveTop;
	}

	private JButton getMoveUp() {
		if(moveUp == null) {
			moveUp = new JButton();
			moveUp.setFocusable(false);
			moveUp.setContentAreaFilled(true);
			moveUp.setBounds(new Rectangle(1, 16, 14, 14));
			moveUp.setIcon(up);
			moveUp.setRolloverIcon(upOver);
			moveUp.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(getTabbedPane().getSelectedIndex() == 0) {
						int index = getMusicsL().getSelectedIndex();
						if(index != -1) getCurrentPL().moveUp(index);
					}
					else {
						int index = getPlaylistsL().getSelectedIndex();
						if(index != -1) getPLs().moveUp(index);
					}
				}
			});
		}
		return moveUp;
	}

	private JButton getMoveDown() {
		if(moveDown == null) {
			moveDown = new JButton();
			moveDown.setFocusable(false);
			moveDown.setContentAreaFilled(true);
			moveDown.setBounds(new Rectangle(1, 32, 14, 14));
			moveDown.setIcon(down);
			moveDown.setRolloverIcon(downOver);
			moveDown.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(getTabbedPane().getSelectedIndex() == 0) {
						int index = getMusicsL().getSelectedIndex();
						if(index != -1) getCurrentPL().moveDown(index);
					}
					else {
						int index = getPlaylistsL().getSelectedIndex();
						if(index != -1) getPLs().moveDown(index);
					}
				}
			});
		}
		return moveDown;
	}

	private JButton getMoveBottom() {
		if(moveBottom == null) {
			moveBottom = new JButton();
			moveBottom.setFocusable(false);
			moveBottom.setContentAreaFilled(true);
			moveBottom.setBounds(new Rectangle(1, 48, 14, 14));
			moveBottom.setIcon(bottom);
			moveBottom.setRolloverIcon(bottomOver);
			moveBottom.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(getTabbedPane().getSelectedIndex() == 0) {
						int index = getMusicsL().getSelectedIndex();
						if(index != -1) getCurrentPL().moveLast(index);
					}
					else {
						int index = getPlaylistsL().getSelectedIndex();
						if(index != -1) getPLs().moveLast(index);
					}
				}
			});
		}
		return moveBottom;
	}

	private JMenu getGuiM() {
		if(guiM == null) {
			guiM = new JMenu();
			guiM.add(getAlwaysOnTopCb());
			guiM.add(getHideListsCb());
			guiM.add(getHideMoveButtonsCb());
		}
		return guiM;
	}

	protected JCheckBoxMenuItem getHideMoveButtonsCb() {
		if(hideMoveButtonsCb == null) {
			hideMoveButtonsCb = new JCheckBoxMenuItem();
			hideMoveButtonsCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					updateHideButtonsState();
				}
			});
		}
		return hideMoveButtonsCb;
	}

	protected JCheckBoxMenuItem getHideListsCb() {
		if(hideListsCb == null) {
			hideListsCb = new JCheckBoxMenuItem();
			hideListsCb.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					updateMinimizeState();
				}
			});
		}
		return hideListsCb;
	}

	private JMenu getPluginsM() {
		if(pluginsM == null) {
			pluginsM = new JMenu();
			pluginsM.add(getNormalizeCbI());
			pluginsM.add(getDistorsionCbI());
			pluginsM.add(getDelayCbI());
			pluginsM.add(getReverbCbI());
			if(System.getProperty("os.name").toLowerCase().contains("win")) {
				pluginsM.addSeparator();
				pluginsM.add(getVSTPluginsCbI());
			}
		}
		return pluginsM;
	}

	protected JCheckBoxMenuItem getNormalizeCbI() {
		if(normalizeCbI == null) {
			normalizeCbI = new JCheckBoxMenuItem();
			normalizeCbI.addItemListener(new java.awt.event.ItemListener(){
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getOptions().setUseNormalizer(normalizeCbI.isSelected());
					Messages.UPDATE_NORMALIZER = true;
				}
			});
		}
		return normalizeCbI;
	}

	protected JCheckBoxMenuItem getDistorsionCbI() {
		if(distorsionCbI == null) {
			distorsionCbI = new JCheckBoxMenuItem();
			distorsionCbI.addItemListener(new java.awt.event.ItemListener(){
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getOptions().setUseDistorsion(distorsionCbI.isSelected());
					Messages.UPDATE_DISTORSION = true;
				}
			});
		}
		return distorsionCbI;
	}

	protected JCheckBoxMenuItem getDelayCbI() {
		if(delayCbI == null) {
			delayCbI = new JCheckBoxMenuItem();
			delayCbI.addItemListener(new java.awt.event.ItemListener(){
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getOptions().setUseDelay(delayCbI.isSelected());
					Messages.UPDATE_DELAY = true;
				}
			});
		}
		return delayCbI;
	}

	protected JCheckBoxMenuItem getReverbCbI() {
		if(reverbCbI == null) {
			reverbCbI = new JCheckBoxMenuItem();
			reverbCbI.addItemListener(new java.awt.event.ItemListener(){
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					getOptions().setUseReverb(reverbCbI.isSelected());
					Messages.UPDATE_REVERB = true;
				}
			});
		}
		return reverbCbI;
	}

	DspPlugin plugin = null;

	protected JMenuItem getVSTPluginsCbI() {
		if(vstPluginsCbI == null) {
			vstPluginsCbI = new JMenuItem();
			vstPluginsCbI.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					selectVSTPluginsFilters();
					int result = getFileChooser().showOpenDialog(MusicPlayerGUI_V2.this);
					if(result == JFileChooser.APPROVE_OPTION) {
						File file = getFileChooser().getSelectedFile();
						plugin = DspPlugin.load(file);
						if(plugin != null) {
							DSPPluginGUI pluginGUI = new DSPPluginGUI(MusicPlayerGUI_V2.this, plugin);
							pluginGUI.setVisible(true);
							pluginGUI.init();
						}
					}
				}
			});
		}
		return vstPluginsCbI;
	}
}