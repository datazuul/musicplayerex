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
package org.jouvieje.MusicPlayerEx.playlist;

import java.io.File;
import java.util.Comparator;
import java.util.Vector;
import java.util.Collections;

import org.jouvieje.MusicPlayerEx.FileTools;
import org.jouvieje.MusicPlayerEx.Messages;
import org.jouvieje.MusicPlayerEx.MusicPlayerEx;
import org.jouvieje.MusicPlayerEx.Options;
import org.jouvieje.MusicPlayerEx.playlist.m3u.M3uWriter;
import org.jouvieje.MusicPlayerEx.playlist.pls.PlsWriter;
import org.jouvieje.MusicPlayerEx.soundengine.Song;
import org.jouvieje.MusicPlayerEx.soundengine.SongConstants;
import org.jouvieje.MusicPlayerEx.soundengine.SoundEngineEx;

public class Playlist implements SongConstants {
	private static boolean DEBUG_MODE = false;

	/** List of Song */
	private Vector list;
	/** Index of the current music */
	private int currentIndex = -1;

	/** Name of the playlist */
	private String name = null;

	/** Path of the playlist file */
	private String path = null;

	private boolean isModified = false;
	public boolean DEFAULT = false;

	public static boolean write(Playlist pl, boolean absolute) {
		String extension = FileTools.getExtension(new File(pl.getPath()));
		if(extension.equals("pls")) {
			boolean result = PlsWriter.write(pl, absolute);
			pl.isModified = !result;
			return result;
		}
		if(extension.equals("m3u")) {
			boolean result = M3uWriter.write(pl, absolute);
			pl.isModified = !result;
			return result;
		}
		return false;
	}

	public static String verifyExtension(String path, String extension) {
		if(extension.equals("pls")) return PlsWriter.verifyExtension(path);
		if(extension.equals("m3u")) return M3uWriter.verifyExtension(path);
		return null;
	}

	public Playlist() {
		list = new Vector();
		currentIndex = -1;
//		name = MusicPlayerEx.lang.getString("default_playlist_name");
	}

	public Playlist(String path) {
		this();
		this.path = path;
	}

	public String toString() {
		return getName();
	}

	public int size() {
		return list.size();
	}

	public void autoSave(final boolean absolutePath) {
		Thread autoSave = new Thread(){
			public void run() {
				generatePath();
				if(isModified) {
					Playlist.write(Playlist.this, absolutePath);
				}
			}
		};
		autoSave.start();
	}

	protected void autoSave(final Options options) {
		if(!options.isAutoSave()) return;
		autoSave(options.isAbsolutePath());
	}

	public void add(Song music) {
		list.add(music); //Add the song at the last of the list
		if(Messages.PLS_LOADED) {
			Messages.UPDATE_MS = true;
			Messages.ENSURE_M_VISIBLE = list.size() - 1;
		}
		isModified = true;
	}

	/**
	 * Add music to the playlist with there coresponding file object.
	 * @return the Vector of music names
	 */
	public void add(Vector files) {
		System.out.println("");
		if(DEBUG_MODE) {
			System.out.println(MusicPlayerEx.lang.getString("add_to_playlist") + " :");
		}

		for(int i = 0; i < files.size(); i++) {
			File file = (File)files.get(i);
			String path = file.getAbsolutePath();

			if(file.getName().endsWith("fsb")) {
				int subSound = SoundEngineEx.get().getFsbNumSubSounds(path);
				if(subSound > 0) {
					for(int j = 0; j < subSound; j++) {
						Song song = new Song(path, j);
						add_(song);
					}
					continue;
				}
			}
			add_(new Song(path));
		}
	}

	private void add_(Song song) {
		if(song.getType() != UNSUPPORTED) {
			if(DEBUG_MODE) {
				System.out.print(" " + song.getAbsolutePath() + " (");
				switch(song.getType()) {
					case STREAM:
						System.out.println("Stream)");
						break;
					case NETWORK:
						System.out.println("Network)");
						break;
					case SOUND:
						System.out.println("Sequence)");
						break;
					case CD:
						System.out.println("Audio CD track)");
						break;
				}
			}
			add(song);
		}
		else if(DEBUG_MODE) {
			System.err.println(" " + song.getAbsolutePath() + " "+ MusicPlayerEx.lang.getString("not_supported") + " !");
		}
	}

	/**
	 * Remove a list of musics
	 * @return musics deleted
	 */
	public Song[] remove(int[] indices) {
		//Get the current music playing
		Object current = list.get(getCurrentIndex());

		Song[] songs = new Song[indices.length];
		int nbBefore = -1;
		for(int i = indices.length - 1; i >= 0; i--) {
			songs[i] = (Song)list.remove(indices[i]);
			if(nbBefore == -1 && songs[i] == current) {
				nbBefore = i;
			}
		}

		//Search the new indice for the current music playing
		int newIndex = list.indexOf(current);
		if(newIndex == -1) {
			currentIndex -= nbBefore;
			checkIndex();
		}
		else {
			currentIndex = newIndex;
		}

		Messages.UPDATE_MS = true;
		isModified = true;

		return songs;
	}

	/*
	 * Remove all music from the playlist
	 */
	public void clear() {
		list.clear();
		list = new Vector();
		currentIndex = -1;

		Messages.UPDATE_MS = true;
		isModified = true;
	}

	/*Sort the list*/

	private Comparator songComparator = new Comparator(){
		/**
		 * Implementation of the interface Comparator
		 *
		 * Compare two elements : here two Music Object
		 * We sort the list by the name of the music file
		 */
		public int compare(Object o1, Object o2) {
			//get the mane of the musics
			String s1 = ((Song)o1).getTitle().toLowerCase();
			String s2 = ((Song)o2).getTitle().toLowerCase();

			//get the minimum number of character
			int min = Math.min(s1.length(), s2.length());

			//loop of each character of the string
			for(int i = 0; i <= min - 1; i++) {
				if(s1.charAt(i) < s2.charAt(i)) {
					//the first must be placed before the second
					return -1;
				}
				else if(s1.charAt(i) > s2.charAt(i)) {
					//the first must be placed after the second
					return 1;
				}
			}

			if(s1.length() < s2.length()) {
				return -1;
			}
			else if(s2.length() < s1.length()) {
				return 1;
			}

			//the names are equal
			return 0;
		}
	};

	/**
	 * Sort the list by name
	 * @return the index of the current music
	 */
	public int sort() {
		Object current = list.get(currentIndex);

		/*
		 * Sort the list using the comparator object
		 */
		Collections.sort(list, songComparator);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_MS = true;
		isModified = true;

		return currentIndex;
	}

	/*Getter and Setter*/

	/**
	 * @return true if the music playing is the last of the playlist
	 */
	public boolean isPlayingLast() {
		return getCurrentIndex() == (list.size() - 1);
	}

	/**
	 * @return the index of the current music
	 */
	public int getCurrentIndex() {
		checkIndex();
		return currentIndex;
	}

	/**
	 * @param index index of the music playing
	 * @return the index of the music selected
	 */
	public int setCurrentIndex(int index, boolean current) {
		int oldIndex = currentIndex;
		currentIndex = index < 0 ? size() - 1 : (index > size() - 1 ? 0 : index);
		checkIndex();
		if(oldIndex != currentIndex && current) {
			Messages.UPDATE_MS = true;
			Messages.ENSURE_M_VISIBLE = currentIndex;
		}
		return currentIndex;
	}

	/**
	 * @return true if the index is a valid music
	 */
	public boolean isIndexValid(int index) {
		return (index >= 0 && index < list.size());
	}

	/**
	 * Select the next song
	 * @return the new index of the song
	 */
	public int next(int offset) {
		return setCurrentIndex(currentIndex + offset, true);
	}

	//Check if the currentIndex is in the good range
	private void checkIndex() {
		if(size() > 0 && !isIndexValid(currentIndex)) {
			currentIndex = 0;
		}
		else if(size() <= 0) {
			currentIndex = -1;
		}
	}

	public String getName() {
		return name;
	}

	public void setTempName(String name) {
		this.name = name;
		//Tempory name -> don't change modifications
		Messages.UPDATE_PLS = true;
	}

	public void setName(String name) {
		this.name = name;
		isModified = true;
		Messages.UPDATE_PLS = true;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		isModified = true;
	}

	public void generatePath() {
		if(path != null) {
			return;
		}

		String name = "Settings/" + getName().replace(File.separatorChar, '_');
		if(new File(name + ".pls").exists()) {
			int count = 0;
			while(new File(name + "_" + count + ".pls").exists()) {
				count++;
			}
			setPath(name + "_" + count + ".pls");
		}
		else {
			setPath(name + ".pls");
		}
	}

	public Song get(int index) {
		return (Song)list.get(index);
	}

	public Song getCurrent() {
		if(currentIndex == -1) {
			return null;
		}
		return get(currentIndex);
	}

	public void rename(Song song, String title) {
		song.setTitle(title);
		isModified = true;
		Messages.UPDATE_MS = true;
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	/* List management */

	public int moveFirst(int index) {
		Object current = list.get(currentIndex);

		list.insertElementAt(list.remove(index), 0);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_MS = true;
		Messages.SELECT_M = 0;
		isModified = true;

		return currentIndex;
	}

	public int moveUp(int index) {
		Object current = list.get(currentIndex);

		list.insertElementAt(list.remove(index), index - 1);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_MS = true;
		Messages.SELECT_M = index - 1;
		isModified = true;

		return currentIndex;
	}

	public int moveDown(int index) {
		Object current = list.get(currentIndex);

		list.insertElementAt(list.remove(index), index + 1);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_MS = true;
		Messages.SELECT_M = index + 1;
		isModified = true;

		return currentIndex;
	}

	public int moveLast(int index) {
		Object current = list.get(currentIndex);

		list.add(list.remove(index));

		currentIndex = list.indexOf(current);

		Messages.UPDATE_MS = true;
		Messages.SELECT_M = list.size() - 1;
		isModified = true;

		return currentIndex;
	}
}