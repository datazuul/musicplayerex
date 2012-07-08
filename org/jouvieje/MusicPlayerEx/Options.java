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

import java.awt.Point;
import java.io.*;

import org.jouvieje.MusicPlayerEx.env.EnvWriter;
import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.playlist.Playlists;

public class Options {
	//Name of the file containing the options
	public static final String optionFile = "Settings/MusicPlayer";
	public static final String fileExtension = ".properties";
	//Format of the file
	private static final String LANGUAGE = "LANGUAGE";
	private static final String X = "X";
	private static final String Y = "Y";
	private static final String HEIGHT = "HEIGHT";
	private static final String AUTO_LOAD = "AUTO_LOAD";
	private static final String AUTO_PLAY = "AUTO_PLAY";
	private static final String AUTO_PLAY_NEXT_PLAYLIST = "AUTO_PLAY_NEXT_PLAYLIST";
	private static final String AUTO_SAVE = "AUTO_SAVE";
	private static final String NORMALIZER = "NORMALIZER";
	private static final String DISTORSION = "DISTORSION";
	private static final String DELAY = "DELAY";
	private static final String REVERB = "REVERB";
	private static final String ALWAYS_ON_TOP = "ALWAYS_ON_TOP";
	private static final String ABSOLUTE_PATH = "ABSOLUTE_PATH";
	private static final String SEARCH_MUSIC_NAME = "SEARCH_MUSIC_NAME";
	private static final String VOLUME = "VOLUME";
	private static final String MUTE = "MUTE";
	private static final String LOOP = "LOOP";
	private static final String IS_MINIMIZED = "IS_MINIMIZED";
	private static final String MANAGE_PANEL_HIDED = "MANAGE_PANEL_HIDED";
	private static final String ENVIRONMENT_PATH = "ENVIRONMENT_PATH";
	private static final String SOUNDCARD = "SOUNDCARD";
	private static final String PLAYFROMMEMORY = "PLAYFROMMEMORY";

	public static final int LANG_DEFAULT = 0;
	public static final int LANG_EN = 1;
	public static final int LANG_FR = 2;
	public static final int LANG_HU = 3;
	public static final int LANG_DE = 4;
	public static final int LANG_EL = 5;
	public static final int LANG_ES = 6;
	public static final int LANG_PL = 7;
	public static final int LANG_SV = 8;

	//defaut values of the Player parameters
	/** Language */
	private int language = LANG_DEFAULT;
	/** Location/Size */
	private int x = -1;
	private int y = -1;
	private int height = MusicPlayerGUI_V2.MIN_HEIGHT;
	/** Default load automatically the player settings */
	private boolean autoLoad = false;
	/** Default play automatically musics at beginning  */
	private boolean autoPlay = false;
	/** Default play automatically next playlist at the end of the current one.  */
	private boolean autoPlayNextPlaylist = false;
	/** Default save automatically the current playlist */
	private boolean autoSave = false;
	/** Always on top */
	private boolean alwaysOnTop = false;
	/** Default : relative path */
	private boolean absolutePath = false;
	private boolean loadingAbsolutePath = false;
	/** Default search automatically music name  */
	private boolean searchMusicName = false;
	/** Default music volume */
	private int volume = 50;
	/** Default music mute state */
	private boolean mute = false;
	/** Default music looping mode */
	private boolean loop = false;
	/** Use normalizer plugin */
	private boolean useNormalizer = false;
	private boolean useDistorsion = false;
	private boolean useDelay = false;
	private boolean useReverb = false;
	/** Default window stae */
	private boolean minimized = false;
	/** Default manage panel state */
	private boolean manageButtonsHided = false;
	/** Environment path */
	private String envPath = null;
	/** Soundcard */
	private int soundcard = -1;
	private boolean playFromMemory = false;

	/**
	 * Load the MusicPlayer Settings
	 */
	public static Options loadOptions() {
		try {
			Options playerOption = new Options();

			File file = new File(optionFile + fileExtension);
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line = null;
			while((line = br.readLine()) != null) {
				int index = line.indexOf("=");
				if(index == -1) continue;

				String name = line.substring(0, index);
				String value = line.substring(index + 1, line.length());

				if(name.equals(LANGUAGE)) {
					try {
						playerOption.setLanguage(Integer.parseInt(value));
					} catch(Exception e) {}
				}
				else if(name.equals(X)) {
					try {
						playerOption.setX(Integer.parseInt(value));
					} catch(Exception e) {}
				}
				else if(name.equals(Y)) {
					try {
						playerOption.setY(Integer.parseInt(value));
					} catch(Exception e) {}
				}
				else if(name.equals(HEIGHT)) {
					try {
						playerOption.setHeight(Integer.parseInt(value));
					} catch(Exception e) {}
				}
				else if(name.equals(AUTO_LOAD)) {
					playerOption.setAutoLoad(value.toLowerCase().equals("true"));
				}
				else if(name.equals(AUTO_PLAY)) {
					playerOption.setAutoPlay(value.toLowerCase().equals("true"));
				}
				else if(name.equals(AUTO_PLAY_NEXT_PLAYLIST)) {
					playerOption.setAutoPlayNextPlaylist(value.toLowerCase().equals("true"));
				}
				else if(name.equals(AUTO_SAVE)) {
					playerOption.setAutoSave(value.toLowerCase().equals("true"));
				}
				else if(name.equals(ALWAYS_ON_TOP)) {
					playerOption.setAlwaysOnTop(value.toLowerCase().equals("true"));
				}
				else if(name.equals(ABSOLUTE_PATH)) {
					playerOption.setAbsolutePath(value.toLowerCase().equals("true"));
					playerOption.loadingAbsolutePath = playerOption.isAbsolutePath();
				}
				else if(name.equals(SEARCH_MUSIC_NAME)) {
					playerOption.setSearchMusicName(value.toLowerCase().equals("true"));
				}
				else if(name.equals(VOLUME)) {
					try {
						playerOption.setVolume(Integer.parseInt(value));
					} catch(Exception e) {}
				}
				else if(name.equals(MUTE)) {
					playerOption.setMute(value.toLowerCase().equals("true"));
				}
				else if(name.equals(LOOP)) {
					playerOption.setLoop(value.toLowerCase().equals("true"));
				}
				else if(name.equals(NORMALIZER)) {
					playerOption.setUseNormalizer(value.toLowerCase().equals("true"));
				}
				else if(name.equals(DISTORSION)) {
					playerOption.setUseDistorsion(value.toLowerCase().equals("true"));
				}
				else if(name.equals(DELAY)) {
					playerOption.setUseDelay(value.toLowerCase().equals("true"));
				}
				else if(name.equals(REVERB)) {
					playerOption.setUseReverb(value.toLowerCase().equals("true"));
				}
				else if(name.equals(IS_MINIMIZED)) {
					playerOption.setMinimized(value.toLowerCase().equals("true"));
				}
				else if(name.equals(MANAGE_PANEL_HIDED)) {
					playerOption.setManageButtonsHided(value.toLowerCase().equals("true"));
				}
				else if(!Messages.SHELL && name.equals(ENVIRONMENT_PATH)) {
					playerOption.setEnvironmentPath(FileTools.getAbsolutePath(value, new File(optionFile + fileExtension).getParentFile()));
				}
				else if(name.equals(SOUNDCARD)) {
					try {
						playerOption.setSoundcard(Integer.parseInt(value));
					} catch(Exception e) {}
				}
				else if(name.equals(PLAYFROMMEMORY)) {
					playerOption.setPlayFromMemory(value.toLowerCase().equals("true"));
				}
			}

			br.close();

			return playerOption;
		}
		catch(Exception e) {
			System.err.println("Property file don't exists !");
			return new Options();
		}
	}

	/**
	 * Save the MusicPlayer Settings
	 */
	public boolean saveOptions(Playlists list) {
		if(Messages.SHELL) return false;

		try {
			File file = new File(optionFile + fileExtension);
			file.getParentFile().mkdirs();

			//create a buffer writer
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			//write the parameters
			bw.write(LANGUAGE + "=" + language); bw.newLine();
			bw.write(X + "=" + x); bw.newLine();
			bw.write(Y + "=" + y); bw.newLine();
			bw.write(HEIGHT + "=" + height); bw.newLine();
			bw.write(AUTO_LOAD + "=" + autoLoad); bw.newLine();
			bw.write(AUTO_PLAY + "=" + autoPlay); bw.newLine();
			bw.write(AUTO_PLAY_NEXT_PLAYLIST + "=" + autoPlayNextPlaylist); bw.newLine();
			bw.write(AUTO_SAVE + "=" + autoSave); bw.newLine();
			bw.write(ALWAYS_ON_TOP + "=" + alwaysOnTop); bw.newLine();
			bw.write(ABSOLUTE_PATH + "=" + absolutePath); bw.newLine();
			bw.write(SEARCH_MUSIC_NAME + "=" + searchMusicName); bw.newLine();
			bw.write(VOLUME + "=" + volume); bw.newLine();
			bw.write(MUTE + "=" + mute); bw.newLine();
			bw.write(LOOP + "=" + loop); bw.newLine();
			bw.write(NORMALIZER + "=" + useNormalizer); bw.newLine();
			bw.write(DISTORSION + "=" + useDistorsion); bw.newLine();
			bw.write(DELAY + "=" + useDelay); bw.newLine();
			bw.write(REVERB + "=" + useReverb); bw.newLine();
			bw.write(IS_MINIMIZED + "=" + minimized); bw.newLine();
			bw.write(MANAGE_PANEL_HIDED + "=" + manageButtonsHided); bw.newLine();
			bw.write(ENVIRONMENT_PATH + "=" + FileTools.getPath(envPath, file.getParentFile(), isAbsolutePath())); bw.newLine();
			bw.write(SOUNDCARD + "=" + soundcard); bw.newLine();
			bw.write(PLAYFROMMEMORY + "=" + playFromMemory); bw.newLine();

			//close the buffer
			bw.close();
		}
		catch(Exception e) {
			System.err.println("FAILED TO SAVE THE OPTIONS !!!");
			e.printStackTrace();
			return false;
		}

		if(isAutoSave()) {
			list.removeDefault();

			boolean saveAll = (isAbsolutePath() != loadingAbsolutePath);
			for(int i = 0; i < list.size(); i++) {
				Playlist playlist = list.get(i);

				//If a list is not saved, save it in a default pls file
				if(playlist.getPath() == null) {
					playlist.generatePath();
				}
				else if(!playlist.isModified() && !saveAll) {
					continue;
				}

				Playlist.write(playlist, isAbsolutePath());
			}

			EnvWriter.write(envPath, list, isAbsolutePath());
		}

		return true;
	}

	/*Getter ans Setter*/

	/**
	 * @return auto load settings
	 */
	public boolean isAutoLoad() {
		return autoLoad;
	}

	/**
	 * @param value auto save the settings
	 */
	public void setAutoLoad(boolean value) {
		autoLoad = value;
	}

	/**
	 * @return auto play musics at beginning
	 */
	public boolean isAutoPlay() {
		return autoPlay;
	}

	/**
	 * @param value auto play musics at beginning
	 */
	public void setAutoPlay(boolean value) {
		autoPlay = value;
	}

	/**
	 * @return auto save the playlis
	 */
	public boolean isAutoSave() {
		return autoSave;
	}

	/**
	 * @param value auto save the playlist
	 */
	public void setAutoSave(boolean value) {
		autoSave = value;
	}

	public boolean isSearchMusicName() {
		return searchMusicName;
	}

	public void setSearchMusicName(boolean value) {
		searchMusicName = value;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int newVolume) {
		volume = newVolume;
	}

	/**
	 * @return the value of the music volume
	 */
	public String getEnvironmentPath() {
		return envPath;
	}

	/**
	 * @param path path to an environment file (env)
	 */
	public void setEnvironmentPath(String path) {
		envPath = path;
	}

	public boolean isMinimized() {
		return minimized;
	}

	public void setMinimized(boolean isMinimized) {
		this.minimized = isMinimized;
	}

	public boolean isManageButtonsHided() {
		return manageButtonsHided;
	}

	public void setManageButtonsHided(boolean manageButtonsHided) {
		this.manageButtonsHided = manageButtonsHided;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public boolean isUseNormalizer() {
		return useNormalizer;
	}

	public void setUseNormalizer(boolean useNormalizer) {
		this.useNormalizer = useNormalizer;
	}

	public boolean isUseDistorsion() {
		return useDistorsion;
	}

	public void setUseDistorsion(boolean useDistorsion) {
		this.useDistorsion = useDistorsion;
	}

	public boolean isUseDelay() {
		return useDelay;
	}

	public void setUseDelay(boolean useDelay) {
		this.useDelay = useDelay;
	}

	public boolean isUseReverb() {
		return useReverb;
	}

	public void setUseReverb(boolean useReverb) {
		this.useReverb = useReverb;
	}

	public boolean isAutoPlayNextPlaylist() {
		return autoPlayNextPlaylist;
	}

	public void setAutoPlayNextPlaylist(boolean autoPlayNextPlaylist) {
		this.autoPlayNextPlaylist = autoPlayNextPlaylist;
	}

	public boolean isAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(boolean isAbsolutePath) {
		this.absolutePath = isAbsolutePath;
	}

	public boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
		if(this.language != LANG_DEFAULT &&
				this.language != LANG_EN &&
				this.language != LANG_FR &&
				this.language != LANG_HU &&
				this.language != LANG_DE &&
				this.language != LANG_EL &&
				this.language != LANG_ES &&
				this.language != LANG_PL &&
				this.language != LANG_SV) {
			this.language = LANG_DEFAULT;
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height < MusicPlayerGUI_V2.MIN_HEIGHT ? MusicPlayerGUI_V2.MIN_HEIGHT : height;
	}

	private void setX(int x) {
		if(x < 0) {
			x = 0;
		}
		this.x = x;
	}

	private void setY(int y) {
		if(y < 0) {
			y = 0;
		}
		this.y = y;
	}

	public void setLocation(Point location) {
		if(location == null) {
			x = 0;
			y = 0;
		}
		else {
			x = location.x;
			y = location.y;
		}
	}

	public Point getLocation() {
		if(x == -1 || y == -1) {
			return null;
		}
		return new Point(x, y);
	}

	public int getSoundcard() {
		return soundcard;
	}

	public void setSoundcard(int soundcard) {
		this.soundcard = soundcard;
	}

	public boolean isPlayFromMemory() {
		return playFromMemory;
	}

	public void setPlayFromMemory(boolean playFromMemory) {
		this.playFromMemory = playFromMemory;
	}
}