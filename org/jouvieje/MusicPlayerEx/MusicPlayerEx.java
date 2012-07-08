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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.Timer;

import org.jouvieje.fmodex.Channel;
import org.jouvieje.fmodex.callbacks.FMOD_CHANNEL_CALLBACK;
import org.jouvieje.fmodex.enumerations.FMOD_CHANNEL_CALLBACKTYPE;
import org.jouvieje.fmodex.enumerations.FMOD_RESULT;
import org.jouvieje.fmodex.utils.Pointer;
import org.jouvieje.MusicPlayerEx.Reg.RegCreator;
import org.jouvieje.MusicPlayerEx.env.EnvConstants;
import org.jouvieje.MusicPlayerEx.env.EnvWriter;
import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.playlist.Playlists;
import org.jouvieje.MusicPlayerEx.soundengine.SharedSoundEngineEx;
import org.jouvieje.MusicPlayerEx.soundengine.Song;
import org.jouvieje.MusicPlayerEx.soundengine.SongConstants;
import org.jouvieje.MusicPlayerEx.soundengine.SoundEngineEx;

public class MusicPlayerEx extends MusicPlayerGUI_V2 implements SongConstants {
	private static final long serialVersionUID = 1L;
	
	private static String[] ARGS = null;

	public static void main(String[] args) {
		JDialog.setDefaultLookAndFeelDecorated(true);
		if(!undecorated) {
			JFrame.setDefaultLookAndFeelDecorated(true);
		}
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		if(args.length > 0) {
			if(args[0].toLowerCase().equals("/reg")) {
				RegCreator.create(args);
			}
			else {
				Messages.SHELL = true;
				ARGS = args;
				new MusicPlayerEx();
			}
		}
		else {
			new MusicPlayerEx();
		}
	}

	/** File Filters */
	private MyFileFilter streamFilter = null;
	private MyFileFilter soundFilter = null;
	private MyFileFilter supportedMusicFilter = null;
	private MyFileFilter supportedFileFilter = null;
	private MyFileFilter vstFilter = null;

	/** Player options */
	private Options options;
	/** Playlists */
	private Playlists playlists;

	public MusicPlayerEx() {
		super();

		/*
		 * Start Update loop
		 */
		Messages.LOOP = true;
		update.start();

		Thread endInit = new Thread(){
			public void run() {
				updateGUI.start();
				getFileChooser();
			}
		};
		endInit.start();

		cleanThread.start();
	}

	private void processShellArgs() {
		if(ARGS != null) {
			if(ARGS.length == 1 && ARGS[0].endsWith(".env")) {
				String envPath = ARGS[0];
				playlists = Playlists.load(envPath);
			}
			else {
				final Vector lists = new Vector();
				final Vector musics = new Vector();
				for(int i = 0; i < ARGS.length; i++) {
					File file = new File(ARGS[i]);
					if(!file.isDirectory()) {
						if(getSupportedMusicFilter().accept(file)) {
							musics.add(file);
						}
						else if(getSupportedPlaylistFilter().accept(file)) {
							lists.add(file);
						}
					}
				}
				playlists = Playlists.load(lists, musics);
			}
		}
		ARGS = null;
	}

	private Thread cleanThread = new Thread(){
		private final static int UPDATE_TIME = 10000;

		public void run() {
			while(Messages.LOOP) {
				java.lang.System.gc();

				try {
					Thread.sleep(UPDATE_TIME);
				} catch(InterruptedException e){}
			}
		}
	};

				/******************
				 * Player Options *
				 ******************/

	protected Options getOptions() {
		if(options == null) {
			options = Options.loadOptions();
			getAutoLoadCb().setSelected(options.isAutoLoad());
			getAutoPlayCb().setSelected(options.isAutoPlay());
			getAutoSaveCb().setSelected(options.isAutoSave());
			getAlwaysOnTopCb().setSelected(options.isAlwaysOnTop());
			getAbsolutePathCb().setSelected(options.isAbsolutePath());
			getSearchNameCb().setSelected(options.isSearchMusicName());
			getHideListsCb().setSelected(options.isMinimized());
			getHideMoveButtonsCb().setSelected(options.isManageButtonsHided());
			getPlayFromMemoryI().setSelected(options.isPlayFromMemory());
			if(options.isAutoLoad()) {
				switch(options.getLanguage()) {
					case Options.LANG_DEFAULT:
						getDefaultRB().setSelected(true);
						break;
					case Options.LANG_EN:
						getEnglishRB().setSelected(true);
						break;
					case Options.LANG_FR:
						getFrenchRB().setSelected(true);
						break;
					case Options.LANG_HU:
						getHungaryRB().setSelected(true);
						break;
					case Options.LANG_DE:
						getGermanRB().setSelected(true);
						break;
					case Options.LANG_EL:
						getGreekRB().setSelected(true);
						break;
					case Options.LANG_ES:
						getSpanishRB().setSelected(true);
						break;
					case Options.LANG_PL:
						getPolishRB().setSelected(true);
						break;
					case Options.LANG_SV:
						getSwedishRB().setSelected(true);
						break;
				}
				getLang();
				getVolume().setValue(options.getVolume());
				getMute().setSelected(options.isMute());
				getLoop().setSelected(options.isLoop());
				getNormalizeCbI().setSelected(options.isUseNormalizer());
				getDistorsionCbI().setSelected(options.isUseDistorsion());
				if(options.isAlwaysOnTop() && JAVA_VERSION > 4) {
					this.setAlwaysOnTop(true);
				}
				if(!Messages.SHELL) {
					playlists = Playlists.load(options.getEnvironmentPath());
				}
				else {
					processShellArgs();
				}
			}
			else {
				getDefaultRB().setSelected(true);
				getLang();
				if(!Messages.SHELL) {
					Messages.PLS_LOADED = true;
				}
				else {
					processShellArgs();
				}
			}
		}
		return options;
	}

	protected void closePlayer() {
		//Stop current music
		stop();

		//Stop timer/thread
		updateGUI.stop();
		Messages.LOOP = false;
		while(update.isAlive() && cleanThread.isAlive()) {
			try {
				Thread.sleep(12);
			} catch(InterruptedException e){}
		}

		//Stop sound engine
		SoundEngineEx.get().close();

		//Save options
		System.out.print(getLang().getString("save_settings"));
			options.setEnvironmentPath(getPLs().getPath());
			options.setLocation(getLocation());
			options.saveOptions(playlists);
		System.out.println(getLang().getString("finish"));
	}

					/***************
					 * Environment *
					 ***************/

	protected void loadEnv(final File file) {
		if(file != null && file.exists()) {
			MusicPlayerEx.this.stop();
			getPLs().clear();

			MusicPlayerEx.this.playlists = Playlists.load(file.getPath());
		}
	}

	protected void saveEnv() {
		if(Messages.SHELL) {
			return;
		}

		selectEnvFilters();
		int choice = getFileChooser().showSaveDialog(this);

		if(choice == JFileChooser.APPROVE_OPTION) {
			String name = getFileChooser().getSelectedFile().getPath();
			final String fileName = EnvWriter.verifyFileName(name);
			if(!askEraseExisting(fileName))
				return;

			getPLs().setPath(fileName);

			Thread write = new Thread(){
				public void run() {
					EnvWriter.write(fileName, getPLs(), getOptions().isAbsolutePath());
				}
			};
			write.start();
		}
	}

	protected void clearEnv() {
		if(!askClearList()) {
			return;
		}

		if(!isEmpty()) {
			stop(); //TODO don't stop here
			getPLs().clear();
			Messages.TEMPORARY_MESSAGE = getLang().getString("playlists_cleared");
		}
	}

					/************
					 * Playlist *
					 ************/

	protected Playlists getPLs() {
		if(playlists == null) {
			playlists = new Playlists("Settings/playlists." + EnvConstants.EXTENSION);
		}
		return playlists;
	}

	protected Playlist getCurrentPL() {
		return getPLs().getCurrent();
	}

	protected boolean isEmpty() {
		return getPLs().size() <= 0 && (!Messages.PLS_PRE_LOADED || !Messages.PLS_LOADED);
	}

	protected void newEmptyPL() {
		Playlist pl = new Playlist();

		boolean continu = true;
		int count = 1;
		while(continu) {
			continu = false;
			for(int i = 0; i < getPLs().size(); i++) {
				if(getPLs().get(i).getName().equals(count == 1 ? pl.getName() : pl.getName() + " (" + count + ")")) {
					count++;
					continu = true;
					break;
				}
			}
		}
		pl.setTempName(count == 1 ? pl.getName() : pl.getName() + " (" + count + ")");

		if(renamePL(pl)) {
			getPLs().add(pl);
			Messages.SELECT_PL = getPLs().size()-1;
		}
	}

	protected void openPLs(final File[] files) {
		if(files != null && files.length > 0) {
			Thread t = new Thread(){
				public void run() {
					getPLs().load(files);
				}
			};
			t.start();
		}
	}

	protected void removePLS(int[] indices) {
		if(!askRemoveItems()) {
			return;
		}

		if(indices.length > 0) {
			//Stop the music if it is in a playlist to be removed soon
			boolean isCurrentPlaylist = false;
			for(int i = 0; i <= indices.length - 1; i++) {
				if(indices[i] == getPLs().getCurrentIndex()) {
					isCurrentPlaylist = true;
					break;
				}
			}
			if(isCurrentPlaylist) {
				stop(); //TODO don't stop here
			}

			//Remove playlists
			getPLs().remove(indices);

			if(isCurrentPlaylist) {
				Messages.UPDATE_MS = true;
				Messages.AUTO_PLAY = true;
			}

			if(getPLs().size() <= 0) {
				Messages.MESSAGE = getLang().getString("list_empty");
			}

			Messages.SELECT_PL = getPLs().getCurrentIndex();
		}
	}

	protected void savePlaylist(final Playlist pl) {
		if(pl != null) {
			selectPlaylistFilters();
			getFileChooser().setSelectedFile(new File(pl.getName()));
			int choice = getFileChooser().showSaveDialog(this);

			if(choice == JFileChooser.APPROVE_OPTION) {
				String name = getFileChooser().getSelectedFile().getPath();

				String ext = "pls";
				if(getFileChooser().getFileFilter().equals(getPlsFilter())) {
					ext = "pls";
				}
				else if(getFileChooser().getFileFilter().equals(getM3uFilter())) {
					ext = "m3u";
				}

				final String path = Playlist.verifyExtension(name, ext);
				if(!askEraseExisting(path)) {
					return;
				}

				pl.setPath(path);
				pl.autoSave(getOptions().isAbsolutePath());
			}
		}
	}

	protected boolean renamePL(Playlist pl) {
		String value = JOptionPane.showInputDialog(this, getLang().getString("playlist_name") + " :", pl.getName());
		if(value != null) {
			pl.setName(value);
			return true;
		}
		return false;
	}

	protected void clearPL(Playlist pl) {
		if(!askClearList()) {
			return;
		}

		if(!isEmpty() && pl.size() > 0) {
			stop(); //TODO don't stop here
			pl.clear();
			Messages.TEMPORARY_MESSAGE = getLang().getString("playlist_cleared");
		}
	}

					/**********
					 * Musics *
					 **********/

	protected void openMusics() {
		selectMusicFilters();
		int choice = getFileChooser().showOpenDialog(this);

		if(choice == JFileChooser.APPROVE_OPTION) {
			File[] files = getFileChooser().getSelectedFiles();
			Vector musics = new Vector();
			Vector pls = new Vector();

			for(int i = 0; i < files.length; i++) {
				File file = files[i];
				if(getSupportedMusicFilter().accept(file)) {
					musics.add(file);
				}
				else if(getSupportedPlaylistFilter().accept(file)) {
					pls.add(file);
				}
			}

			openMedias(pls, musics);
		}
	}

	protected void openMedias(final Vector pls, final Vector musics) {
		if(musics.isEmpty() && pls.isEmpty()) {
			return;
		}

		if(!musics.isEmpty()) {
			Playlist playList = getCurrentPL();
			boolean clear = playList.size() <= 0;
			playList.add(musics);

			Messages.AUTO_PLAY = true;
			Messages.SELECT_M = getCurrentPL().getCurrentIndex();
			Messages.ENSURE_M_VISIBLE = Messages.SELECT_M;
			if(clear) {
				Messages.MESSAGE = " ";
			}
		}
		if(!pls.isEmpty()) {
			Thread t = new Thread(){
				public void run() {
					getPLs().load(pls); //Loading in a thread

					if(musics.size() <= 0) {
						Messages.AUTO_PLAY = true;
						Messages.SELECT_M = getCurrentPL().getCurrentIndex();
						Messages.ENSURE_M_VISIBLE = Messages.SELECT_M;
					}
				}
			};
			t.start();
		}
	}

	protected void openURL() {
		String urlString = (String)JOptionPane.showInputDialog(
				this,
				getLang().getString("open_url"),
				getLang().getString("open_url"),
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"http://");

		if(urlString != null && urlString.startsWith("http://")) {
			urlString = urlString.replace(" ", "%20");
			Song song = new Song(urlString);

			Playlist playList = getCurrentPL();
			boolean clear = playList.size() <= 0;
			playList.add(song);
			if(clear) {
				Messages.MESSAGE = " ";
			}
		}
	}
	private Object[] driveDescriptions = null;

	private Object[] getDriveDescriptions() {
		if(driveDescriptions == null) {
			File[] files = File.listRoots();
			driveDescriptions = new Object[files.length];
			driveLetters = new String[files.length];
			FileSystemView systemView = FileSystemView.getFileSystemView();
			for(int i = 0; i < files.length; i++) {
				driveLetters[i] = "" + files[i].getPath().charAt(0);
				if(!files[i].getPath().toLowerCase().contains("a:")) {
					String description = systemView.getSystemDisplayName(files[i]);
					if(description == null || description.equals("")) {
						description = systemView.getSystemTypeDescription(files[i]) + " (" + files[i].getPath().substring(0, files[i].getPath().length() - 1) + ")";
					}
					driveDescriptions[i] = description;
				}
				else {
					driveDescriptions[i] = systemView.getSystemTypeDescription(files[i])+" ("+files[i].getPath().substring(0, files[i].getPath().length()-1)+")";
				}
			}
		}
		return driveDescriptions;
	}
	private String[] driveLetters = null;

	private String getDriverLetter(String driveDescription) {
		for(int i = 0; i < getDriveDescriptions().length; i++) {
			if(getDriveDescriptions()[i].equals(driveDescription)) {
				return driveLetters[i];
			}
		}
		return null;
	}

	protected void openCD() {
		final String cdDrive;
		switch(SharedSoundEngineEx.get().getPlatform()) {
			case SharedSoundEngineEx.WIN:
				if(driveDescriptions == null) {
					Messages.PERSISTENT_MESSAGE = getLang().getString("please_wait");
					getDriveDescriptions();
					Messages.PERSISTENT_MESSAGE = "";
					Messages.TEMPORARY_MESSAGE = getDriveDescriptions().length+" "+getLang().getString("drive_found");
				}

				String description = (String)JOptionPane.showInputDialog(
						this,
						getLang().getString("where_cd"),
						getLang().getString("cd_drive"),
						JOptionPane.PLAIN_MESSAGE,
						null,
						getDriveDescriptions(),
						getDriveDescriptions()[0]);
				cdDrive = getDriverLetter(description)+":";
				break;
			case SharedSoundEngineEx.LINUX:
			case SharedSoundEngineEx.MAC:
				cdDrive = JOptionPane.showInputDialog(
						this,
						getLang().getString("where_cd"),
						getLang().getString("cd_drive"),
						JOptionPane.PLAIN_MESSAGE);
				break;
			default:
				return;
		}
		if(cdDrive != null && !cdDrive.equals("")) {
			final Playlist pl;
			final boolean newPL;
			if(!isEmpty() && isMusicsShown()) {
				pl = getCurrentPL();
				newPL = false;
			}
			else {
				pl = new Playlist();
				pl.setTempName(getLang().getString("opening_cd"));
				getPLs().add(pl);
				newPL = true;
			}

			Thread t = new Thread(){
				public void run() {
					Messages.PERSISTENT_MESSAGE = getLang().getString("opening_cd");
					int numTracks = SoundEngineEx.get().getCDNumTracks(cdDrive);
					if(numTracks > 0) {
						for(int i = 0; i <= numTracks - 1; i++) {
							String s = String.format(getLang().getString("track_name"), new Object[]{new Integer(i+1)});
							pl.add(new Song(cdDrive, i, s));
						}

						pl.setName(getLang().getString("audio_cd")+" ("+cdDrive.replace(":", "")+")");

						Messages.AUTO_PLAY = true;
						Messages.PERSISTENT_MESSAGE = "";
						Messages.MESSAGE = numTracks+" "+getLang().getString("tracks_found");
					}
					else {
						if(newPL) {
							getPLs().remove(pl);
						}

						Messages.PERSISTENT_MESSAGE = "";
						Messages.ERROR = getLang().getString("no_track");
					}
				}
			};
			t.start();
		}
		return;
	}

	protected void convertMusics(Playlist pl, int[] indices) {
		//TODO Stop if you hear some noice
//		Messages.STOP = true;

		Vector v = new Vector();
		for(int i = 0; i < indices.length; i++) {
			v.add(pl.get(indices[i]).clone());
		}

//		while(Messages.STOP) {
//			Thread.yield();
//		}
		new ConversionGUI(this, pl, v).setVisible(true);
	}

	protected void removeMusics(Playlist pl, int[] indices) {
		if(!askRemoveItems()) {
			return;
		}

		if(indices.length > 0 && pl != null) {
			//Stop the music if it is currently playing
			boolean isCurrentMusic = false;
			int currentIndex = pl.getCurrentIndex();
			for(int i = 0; i <= indices.length - 1; i++) {
				if(indices[i] == currentIndex) {
					isCurrentMusic = true;
					stop();			//TODO don't stop here
					break;
				}
			}

			//remove the item from the playlist and refresh the playlistlist
			pl.remove(indices);

			//play a music if the music deleted is the current music
			if(isCurrentMusic) {
				Messages.AUTO_PLAY = true;
			}

			if(pl.size() <= 0) {
				Messages.TEMPORARY_MESSAGE = getLang().getString("empty_playlist");
			}

			Messages.SELECT_M = getCurrentPL().getCurrentIndex();
		}
	}

	protected void renameMusic(Playlist pl, int songIndex) {
		Song song = pl.get(songIndex);
		String value = JOptionPane.showInputDialog(this, getLang().getString("music_title") + " :", song.getTitle());
		if(value != null && !value.equals("")) {
			pl.rename(song, value);
		}
	}

					/*Update loop*/

	private Thread update = new Thread(){
		private final static int UPDATE_TIME = 15;
		private boolean first = true;
		public void run() {
			if(first) {
				first = false;

				/*
				 * Initialization of FMOD/FMOD Ex
				 */
				if(!SoundEngineEx.get().init(getOptions().getSoundcard())) {
					JOptionPane.showMessageDialog(MusicPlayerEx.this, getLang().getString("init_fail")+"\n"+SoundEngineEx.INIT_ERROR);
					System.exit(0);
				}
				SoundEngineEx.get().setEndCallback(callback);
			}
			while(Messages.LOOP) {
				if(Messages.AUTO_PLAY) {
					Messages.AUTO_PLAY = false;
					if(getOptions().isAutoPlay() && !isEmpty() && getCurrentPL().size() > 0 && !isPlaying()) Messages.PLAY_UNPAUSE = true;
				}

				if(Messages.PLAY_NEXT != 0) {
					int offset = Messages.PLAY_NEXT;
					Messages.PLAY_NEXT = 0;
					if(getOptions().isLoop()) {
						offset = 0;
					}

					playNext(offset);
				}
				else if(Messages.STOP) {
					Messages.STOP = false;
					MusicPlayerEx.this.stop();
				}
				else if(Messages.PLAY_UNPAUSE) {
					Messages.PLAY_UNPAUSE = false;
					checkPlay(playUnpause());
				}
				else if(Messages.PLAY_PAUSE) {
					Messages.PLAY_PAUSE = false;
					checkPlay(playPause());
				}
				else if(Messages.PAUSE_UNPAUSE) {
					Messages.PAUSE_UNPAUSE = false;
					pauseUnpause();
				}
				else if(Messages.PLAY_INDEX != -1) {
					int index = Messages.PLAY_INDEX;
					Messages.PLAY_INDEX = -1;

					if(index != getCurrentPL().getCurrentIndex()) {
						MusicPlayerEx.this.stop();
						getCurrentPL().setCurrentIndex(index, true);
					}
					checkPlay(playUnpause());
				}
				else if(Messages.PLAY_PLAYLIST != -1) {
					int index = Messages.PLAY_PLAYLIST;
					Messages.PLAY_PLAYLIST = -1;

					if(index != getPLs().getCurrentIndex()) {
						MusicPlayerEx.this.stop();
						getPLs().select(index, getOptions());
						if(getOptions().isAutoPlay() || Messages.FORCE_AUTO_PLAY) {
							Messages.FORCE_AUTO_PLAY = false;
							checkPlay(playUnpause());
						}
					}
					else if(!isPlaying()) {
						if(getOptions().isAutoPlay() || Messages.FORCE_AUTO_PLAY) {
							Messages.FORCE_AUTO_PLAY = false;
							checkPlay(playUnpause());
						}
					}
					Messages.SHOW_MS = true;
				}

				if(Messages.UPDATE_NORMALIZER) {
					Messages.UPDATE_NORMALIZER = false;
					if(getOptions().isUseNormalizer())
						SoundEngineEx.get().enableNormalization();
					else
						SoundEngineEx.get().disableNormalization();
				}
				
				if(Messages.UPDATE_DISTORSION) {
					Messages.UPDATE_DISTORSION = false;
					if(getOptions().isUseDistorsion())
						SoundEngineEx.get().enableDistorsion();
					else
						SoundEngineEx.get().disableDistorsion();
				}
				
				if(Messages.UPDATE_DELAY) {
					Messages.UPDATE_DELAY = false;
					if(getOptions().isUseDelay())
						SoundEngineEx.get().enableDelay();
					else
						SoundEngineEx.get().disableDelay();
				}
				
				if(Messages.UPDATE_REVERB) {
					Messages.UPDATE_REVERB = false;
					if(getOptions().isUseReverb())
						SoundEngineEx.get().enableReverb();
					else
						SoundEngineEx.get().disableReverb();
				}

				SoundEngineEx.get().update();

				try {
					Thread.sleep(UPDATE_TIME);
				} catch(InterruptedException e){}
			}
		}
	};
	private FMOD_CHANNEL_CALLBACK callback = new FMOD_CHANNEL_CALLBACK(){
		public FMOD_RESULT FMOD_CHANNEL_CALLBACK(Channel channel, FMOD_CHANNEL_CALLBACKTYPE type, Pointer commanddata1,
				Pointer commanddata2) {
			if(type == FMOD_CHANNEL_CALLBACKTYPE.FMOD_CHANNEL_CALLBACKTYPE_END) {
//				playNext(1);	//TODO FMOD Ex's bug !
				Messages.PLAY_NEXT = 1;
			}
			return FMOD_RESULT.FMOD_OK;
		}
	};

	protected void playNext(int offset) {
		//Stop the current music and play the following
		MusicPlayerEx.this.stop();

		Playlist pl = getCurrentPL();
		if(getOptions().isAutoPlayNextPlaylist()) {
			if(pl.isPlayingLast()) {
				pl.setCurrentIndex(0, true);
				getPLs().nextPlaylist(1);
			}
			else {
				pl.next(offset);
			}
		}
		else {
			pl.next(offset);
		}
		checkPlay(playUnpause());
	}

	protected void playNextPlaylist(int offset) {
		//Stop the current music and play the following
		MusicPlayerEx.this.stop();

		getPLs().nextPlaylist(offset);
		checkPlay(playUnpause());
	}
	private Timer updateGUI = new Timer(50, new ActionListener(){
		private long endDisplayTime = -1;
		private String currentDisplay = "";
		private int DISPLAY_ERROR_TIME = 2500;
		private int DISPLAY_TIME = 1250;

		public void actionPerformed(ActionEvent e) {
			if(endDisplayTime == -2 && !Messages.PERSISTENT_MESSAGE.equals(currentDisplay)) {
				endDisplayTime = -1;
			}
			else if((System.currentTimeMillis() - endDisplayTime) > 0) {
				endDisplayTime = -1;
			}
			boolean infoDisplayed = (endDisplayTime != -1);

			if(!Messages.PLAYER_LOADED && (Messages.PLS_PRE_LOADED || Messages.PLS_LOADED)) {
				if(!Messages.PLAYER_INITIALIZED) {
					Messages.PLAYER_LOADED = true;
					Messages.PLAYER_INITIALIZED = true;
					Messages.PERSISTENT_MESSAGE = getLang().getString("init_ok");
				}
				if(Messages.ERROR.equals("")) {
					Messages.AUTO_PLAY = true;
					Messages.SHOW_MS = true;
					Messages.SELECT_M = getCurrentPL().getCurrentIndex();
				}
			}

			if(!infoDisplayed) {
				if(!Messages.ERROR.equals("")) {
					infoDisplayed = true;
					endDisplayTime = System.currentTimeMillis() + DISPLAY_ERROR_TIME;
					getDisplay().setDisplayText(Messages.ERROR);
					Messages.ERROR = "";
				}
				else if(!Messages.PERSISTENT_MESSAGE.equals("")) {
					infoDisplayed = true;
					endDisplayTime = -2;
					currentDisplay = Messages.PERSISTENT_MESSAGE;
					getDisplay().setDisplayText(Messages.PERSISTENT_MESSAGE);
				}
				else if(!Messages.MESSAGE.equals("")) {
					infoDisplayed = true;
					endDisplayTime = System.currentTimeMillis() + DISPLAY_TIME;
					getDisplay().setDisplayText(Messages.MESSAGE);
					Messages.MESSAGE = "";
				}
				else if(isPlaying() || isPaused()) {
					infoDisplayed = true;
					getDisplay().setPaused(isPaused());
					getDisplay().setDisplayText(getCurrentPL().getCurrent().getTitle());
				}
				else if(!Messages.TEMPORARY_MESSAGE.equals("")) {
					infoDisplayed = true;
					getDisplay().setPaused(false);
					getDisplay().setDisplayText(Messages.TEMPORARY_MESSAGE);
				}
				Messages.TEMPORARY_MESSAGE = "";
			}

			if(Messages.SHOW_PLS) {
				Messages.SHOW_PLS = false;
				showPlaylistsList();
			}
			else if(Messages.SHOW_MS) {
				Messages.SHOW_MS = false;
				showMusicsList();
			}

			if(Messages.UPDATE_PLS && isPlaylistsShown()) {
				Messages.UPDATE_PLS = false;
				updatePlaylistsL();
			}
			if(Messages.UPDATE_MS && isMusicsShown()) {
				Messages.UPDATE_MS = false;
				updateMusicsL();
			}

			if(isPlaying() || isPaused()) {
				try {
					getProgressBar().setValue(getProgression());
				} catch(ArithmeticException ae) {}
			}
			else {
				getProgressBar().setValue(0);
			}

			if(!isEmpty()) {
				if(Messages.SELECT_PL != -1 && isPlaylistsShown()) {
					getPlaylistsL().ensureIndexIsVisible(Messages.SELECT_PL);
					getPlaylistsL().setSelectedIndex(Messages.SELECT_PL);
					changeSelectedPlaylist();
					Messages.SELECT_PL = -1;
					Messages.ENSURE_PL_VISIBLE = -1;
				}
				if(Messages.SELECT_M != -1 && isMusicsShown() && getCurrentPL().size() > 0) {
					getMusicsL().ensureIndexIsVisible(Messages.SELECT_M);
					getMusicsL().setSelectedIndex(Messages.SELECT_M);
					changeSelectedMusic();
					Messages.SELECT_M = -1;
					Messages.ENSURE_M_VISIBLE = -1;
				}
			}
			if(Messages.ENSURE_PL_VISIBLE != -1 && isPlaylistsShown()) {
				getPlaylistsL().ensureIndexIsVisible(Messages.ENSURE_PL_VISIBLE);
				Messages.ENSURE_PL_VISIBLE = -1;
			}
			if(Messages.ENSURE_M_VISIBLE != -1 && isMusicsShown()) {
				getMusicsL().ensureIndexIsVisible(Messages.ENSURE_M_VISIBLE);
				Messages.ENSURE_M_VISIBLE = -1;
			}

			updateGUITime();
			if(isShown()) {
				getDisplay().updateTime();
				getDisplay().updateSound();
			}
		}
	});

	private void updatePlaylistsL() {
		getPlaylistsL().setModel(new ListModel(){
			public void removeListDataListener(ListDataListener l) {}
			public void addListDataListener(ListDataListener l) {}
			public Object getElementAt(int index) {
				return getPLs().get(index).getName();
			}
			public int getSize() {
				return getPLs().size();
			}
		});
		getPlaylistsS().validate();
	}

	private void updateMusicsL() {
		getMusicsL().setModel(new ListModel(){
			public void removeListDataListener(ListDataListener l) {}
			public void addListDataListener(ListDataListener l) {}
			public Object getElementAt(int index) {
				return isEmpty() ? null : getCurrentPL().get(index).getTitle();
			}
			public int getSize() {
				return isEmpty() ? 0 : getCurrentPL().size();
			}
		});
		getMusicsS().validate();
	}

	private void updateGUITime() {
		if(isPlaying() || isPaused()) {
			Song song = getCurrentPL().getCurrent();
			String position = longToTime(SoundEngineEx.get().getPosition(song) / 1000);
			String length = longToTime(SoundEngineEx.get().getLength(song) / 1000);
			String time = position + "/" + length;
			getDisplay().setDisplayTime(time);
			getProgressBar().setToolTipText(time);
		}
		else {
			getDisplay().setDisplayTime("0:00/0:00");
			getProgressBar().setToolTipText(null);
		}
	}

	private static String longToTime(long s) {
		long sec = s%60;
		long min = s/60;
		long h = min/60/60;
		String time ="";
		time += (h > 0) ? h+":" : "";
		time += (min <= 9 && h > 0) ? "0"+min+":" : min+":";
		time += (sec <= 9) ? "0"+sec : ""+sec;
		return time;
	}

	protected void changeSelectedMusic() {
		int index = getMusicsL().getSelectedIndex();

		if(index != getCurrentPL().getCurrentIndex()) {
			updatePlayPause(false);
		}
		else {
			updatePlayPause(isPlaying() && !isPaused());
		}
	}

	protected void changeSelectedPlaylist() {
		int index = getPlaylistsL().getSelectedIndex();

		if(index != getPLs().getCurrentIndex()) {
			updatePlayPause(false);
		}
		else {
			changeSelectedMusic();
		}
	}

						/*Linking to Sound Engine*/

	/** @return false if fail */
	protected boolean playPause() {
		if(!isEmpty()) {
			int index = getMusicsL().getSelectedIndex();
			if(index == -1 || index == getCurrentPL().getCurrentIndex()) {
				if(index == -1) Messages.SELECT_M = getCurrentPL().getCurrentIndex();

				if(isPlaying() || isPaused()) {
					pauseUnpause();
					return true;
				}
				else return playUnpause();
			}
			else {
				stop();
				getCurrentPL().setCurrentIndex(getMusicsL().getSelectedIndex(), true);
				return playUnpause();
			}
		}
		else {
			//For opening a new music
			return playUnpause();
		}
	}
	/** @return false if fail */
	protected boolean playUnpause() {
		if(isEmpty() || getCurrentPL().size() <= 0) {
			Thread t = new Thread(){
				public void run() {
					openMusics();
				}
			};
			t.start();
			updatePlayPause(false);
			return false;
		}

		if(getCurrentPL().getCurrentIndex() == -1) getCurrentPL().setCurrentIndex(0, true);

		if(isPlaying()) {
			if(isPaused()) {
				boolean paused =  SoundEngineEx.get().pauseUnpause(getCurrentPL().getCurrent());
				updatePlayPause(!paused);
				return true;
			}
			else {
				updatePlayPause(true);
				return true;
			}
		}
		else {
			Playlist pl = getCurrentPL();
			Song song = pl.getCurrent();

			Messages.PERSISTENT_MESSAGE = getLang().getString("playing");
			if(!SoundEngineEx.get().load(song)) {
				Messages.PERSISTENT_MESSAGE = "";
				Messages.ERROR = getLang().getString("cannot_load");
				updatePlayPause(false);
				return false;
			}
			if(!SoundEngineEx.get().play(song)) {
				Messages.PERSISTENT_MESSAGE = "";
				Messages.ERROR = getLang().getString("cannot_play");
				updatePlayPause(false);
				return false;
			}
			Messages.PERSISTENT_MESSAGE = "";

			//Search the music name if the option is check
			if(getOptions().isSearchMusicName() || song.isSubSound()) {
				String title = SoundEngineEx.get().getTitle(song);
				if(title != null && !title.equals("") && !new File(title).exists()) {
					pl.rename(song, title);
				}
			}
			updatePlayPause(true);
			return true;
		}
	}
	/**
	 * @return true if paused
	 */
	protected boolean pauseUnpause() {
		boolean isPaused = false;
		if(!isEmpty()) {
			Song song = getCurrentPL().getCurrent();
			if(song != null) {
				isPaused = SoundEngineEx.get().pauseUnpause(song);
			}
		}
		updatePlayPause(!isPaused);
		return isPaused;
	}
	/** @return false if fail */
	protected boolean stop() {
		boolean stop = true;
		if(!isEmpty()) {
			Song song = getCurrentPL().getCurrent();
			if(song != null) {
				stop = SoundEngineEx.get().stop(song);
				Messages.TEMPORARY_MESSAGE = getLang().getString("stop");
			}
		}
		updatePlayPause(false);
		return stop;
	}

	protected int setPosition(int position) {
		if(!isEmpty() && isPlaying()) {
			return SoundEngineEx.get().setPosition(getCurrentPL().getCurrent(), position);
		}
		return 0;
	}

	protected void setVolume(int volume) {
		if(!isEmpty()) {
			Song song = getCurrentPL().getCurrent();
			if(song != null) {
				SoundEngineEx.get().setVolume(song, volume);
				return;
			}
		}
		SoundEngineEx.get().setVolume(volume);
	}

	protected void setMute(boolean mute) {
		if(!isEmpty()) {
			Song song = getCurrentPL().getCurrent();
			if(song != null) {
				SoundEngineEx.get().mute(song, mute);
				return;
			}
		}
		SoundEngineEx.get().mute(mute);
	}

	protected int getProgression() {
		if(isEmpty()) {
			return 0;
		}

		Song song = getCurrentPL().getCurrent();
		if(song != null) {
			return SoundEngineEx.get().getProgression(song);
		}
		return 0;
	}

	protected boolean isPlaying() {
		if(isEmpty()) {
			return false;
		}

		Song song = getCurrentPL().getCurrent();
		if(song != null) {
			return SoundEngineEx.get().isPlaying(song);
		}
		return false;
	}

	protected boolean isPaused() {
		if(isEmpty()) {
			return false;
		}

		Song song = getCurrentPL().getCurrent();
		if(song != null) {
			return SoundEngineEx.get().isPaused(song);
		}
		return false;
	}

					/****************
					 * File filters *
					 ****************/

	protected FileFilter getSoundFilter() {
		if(soundFilter == null) {
			soundFilter = new MyFileFilter(SOUND_FORMAT_SUPPORTED, "Sound");
		}
		return soundFilter;
	}

	protected FileFilter getStreamFilter() {
		if(streamFilter == null) {
			streamFilter = new MyFileFilter(STREAM_FORMAT_SUPPORTED, "Stream");
		}
		return streamFilter;
	}

	protected FileFilter getVstFilter() {
		if(vstFilter == null) {
			vstFilter = new MyFileFilter(new String[]{"dll"}, "VST Plugins");
		}
		return vstFilter;
	}

	protected MyFileFilter getSupportedMusicFilter() {
		if(supportedMusicFilter == null) {
			supportedMusicFilter = new MyFileFilter(STREAM_FORMAT_SUPPORTED, getLang().getString("supported_musics"), false);
			supportedMusicFilter.addExtensions(SOUND_FORMAT_SUPPORTED);
		}
		return supportedMusicFilter;
	}

	protected MyFileFilter getSupportedFileFilter() {
		if(supportedFileFilter == null) {
			supportedFileFilter = new MyFileFilter(STREAM_FORMAT_SUPPORTED, getLang().getString("supported_files"), false);
			supportedFileFilter.addExtensions(SOUND_FORMAT_SUPPORTED);
			supportedFileFilter.addExtension("pls");
			supportedFileFilter.addExtension("m3u");
		}
		return supportedFileFilter;
	}

	private void checkPlay(boolean sucess) {
		if(!isEmpty() && getCurrentPL().size() <= 0) {
			return;
		}

		if(sucess) {
			//Reset errors
			Messages.FAIL_TO_PLAY = 0;

			//Mark music title
			Playlist p = getCurrentPL();
			Song s = p.getCurrent();
			if(s.getTitle().startsWith("[KO] ")) p.rename(s, s.getTitle().substring(5));
		}
		else {
			//Mark music title
			Playlist p = getCurrentPL();
			Song s = p.getCurrent();
			if(!s.getTitle().startsWith("[KO] ")) p.rename(s, "[KO] " + s.getTitle());

			Messages.FAIL_TO_PLAY++;
			if(Messages.FAIL_TO_PLAY < getCurrentPL().size()) playNext(1);

		}
	}
}