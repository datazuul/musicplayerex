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
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.jouvieje.MusicPlayerEx.FileTools;
import org.jouvieje.MusicPlayerEx.Messages;
import org.jouvieje.MusicPlayerEx.MusicPlayerEx;
import org.jouvieje.MusicPlayerEx.Options;
import org.jouvieje.MusicPlayerEx.env.Env;
import org.jouvieje.MusicPlayerEx.env.EnvReader;
import org.jouvieje.MusicPlayerEx.playlist.m3u.M3uReader;
import org.jouvieje.MusicPlayerEx.playlist.pls.PlsReader;

public class Playlists {
	/** List of Playlists */
	private Vector list;
	/** Index of the current playlist */
	private int currentIndex = -1;

	/** Path of the playlist file */
	private String path = null;

	/**
	 * Loads a list of playlist
	 * If the file don't exist the list created is empty
	 */
	public static Playlists load(String envPath) {
		if(!new File(envPath).exists()) {
			Messages.ERROR = MusicPlayerEx.lang.getString("env_not_exists");
			Messages.PLS_LOADED = true;
			return new Playlists();
		}

		Env env = EnvReader.read(envPath);
		if(env != null) {
			Playlists playlists = new Playlists(env.getFileName());
			if(env.size() > 0) {
				Messages.PLS_PRE_LOADED = false;
				Messages.PLS_LOADED = false;
				playlists.loadEnv(env);
			}
			else Messages.PLS_LOADED = true;
			return playlists;
		}
		else {
			Messages.ERROR = MusicPlayerEx.lang.getString("env_cannot_create");
			Messages.PLS_LOADED = true;
			return new Playlists();
		}
	}

	public static Playlists load(Vector lists, Vector musics) {
		Playlists pls = new Playlists(null);

		if(lists.size() > 0) {
			Env env = new Env("ShellEnv.env", 0);
			for(int i = 0; i < lists.size(); i++) {
				env.addItem(((File)lists.get(i)).getAbsolutePath(), 0);
			}
			pls.loadEnv(env);
		}

		if(musics.size() > 0) {
			final Playlist pl = new Playlist();
			pl.setName("Music selection"); //TODO add field in lang files
			pl.add(musics);
			pls.add(pl);
			pls.setCurrentIndex(pls.size());
		}

		Messages.PLS_LOADED = true;
		Messages.TEMPORARY_MESSAGE = MusicPlayerEx.lang.getString("env_loaded");

		return pls;
	}

	private Playlists() {
		this("Settings/playlists.env");
	}

	public Playlists(String path) {
		this.path = path;
		list = new Vector();
	}

	public int size() {
		return list.size();
	}

	private void loadEnv(final Env env) {
		Thread thread = new Thread(){
			public void run() {
				/*
				 * PRE-LOADING
				 * Just create playlists withour loading
				 */
				for(int i = 0; i < env.size(); i++) {
					final Playlist pl = new Playlist(env.getPath(i));
					pl.setTempName(MusicPlayerEx.lang.getString("reading"));
					add(pl);
				}
				Messages.SELECT_PL = currentIndex;

				/*
				 * LOADING
				 * Loads playlists with a particular loading order
				 */
				int i = env.getStartIndex();
				int dir = 1, offset = 1;
				boolean zigzag = true;
				while(true) {
					Playlist pl = get(i);
					load(pl);

					pl.setCurrentIndex(env.getIndex(i), false);

					if(!Messages.PLS_PRE_LOADED && (i == env.getStartIndex())) {
						setCurrentIndex(i);

						Messages.UPDATE_MS = true;
						Messages.PLS_PRE_LOADED = true;
						Messages.AUTO_PLAY = true;
					}

					i += dir * offset;

					if(zigzag) {
						if(i >= env.size()) {
							i -= dir * offset;
							i--;

							if(i < 0) break;

							zigzag = false;
							dir = -1;
							offset = 1;
						}
						else if(i < 0) {
							i -= dir * offset;
							i++;

							if(i >= env.size()) break;

							zigzag = false;
							dir = 1;
							offset = 1;
						}
					}
					else {
						if(i >= env.size() || i < 0) {
							break;
						}
					}

					if(zigzag) {
						offset++;
						dir *= -1;
					}
				}
				Messages.PLS_PRE_LOADED = true; //Just in case it is not already set
				Messages.PLS_LOADED = true;
				Messages.TEMPORARY_MESSAGE = MusicPlayerEx.lang.getString("env_loaded");
			}
		};
		thread.start();
	}

	public void load(final Vector plsFiles) {
		File[] files = new File[plsFiles.size()];
		for(int i = 0; i < files.length; i++) {
			files[i] = (File)plsFiles.get(i);
		}

		load(files);
	}

	public void load(final File[] plsFiles) {
		Playlist[] list = new Playlist[plsFiles.length];
		for(int i = 0; i < plsFiles.length; i++) {
			final Playlist pl = new Playlist(plsFiles[i].getPath());
			pl.setTempName(MusicPlayerEx.lang.getString("reading"));
			add(pl);
			list[i] = pl;
		}

		for(int i = 0; i < plsFiles.length; i++) {
			Playlist pl = list[i];
			load(pl);

			String path = plsFiles[i].getName();
			int index = path.lastIndexOf(".");
			pl.setName(path.substring(0, index != -1 ? index : path.length()));
		}

		if(plsFiles.length > 1) Messages.TEMPORARY_MESSAGE = MusicPlayerEx.lang.getString("playlists_loaded");
		else Messages.TEMPORARY_MESSAGE = MusicPlayerEx.lang.getString("playlist_loaded");
	}

	private void load(Playlist pl) {
		final File file = new File(pl.getPath());
		if(!file.exists()) {
			pl.setName(MusicPlayerEx.lang.getString("not_found"));
		}
		else {
			String extension = FileTools.getExtension(file);
			if(extension.endsWith("pls")) {
				if(!PlsReader.read(pl)) {
					pl.setName(MusicPlayerEx.lang.getString("corrupted_file"));
				}
			}
			else if(extension.endsWith("m3u")) {
				if(!M3uReader.read(pl)) {
					pl.setName(MusicPlayerEx.lang.getString("corrupted_file"));
				}
			}
		}
		Messages.UPDATE_PLS = true;
	}

	public int add(Playlist pl) {
		removeDefault();

		list.add(pl); //Add the playlist at the end of the list

		Messages.UPDATE_PLS = true;
		Messages.ENSURE_PL_VISIBLE = list.size();
		Messages.SELECT_PL = list.size() - 1;

		if(Messages.PLS_LOADED) {
			checkIndex();
		}

		return currentIndex;
	}

	public void removeDefault() {
		if(list.size() == 1 && get(0).DEFAULT && get(0).size() == 0) {
			list.remove(0);
		}
	}

	/**
	 * Remove some playlists from the list
	 * @param indices of playlists to remove
	 * @return array that contains playlists removed
	 */
	public Playlist[] remove(int[] indices) {
		//Get the current playlist
		Object current = list.get(currentIndex);

		Playlist[] pls = new Playlist[indices.length];
		int nbBefore = -1;
		for(int i = indices.length - 1; i >= 0; i--) {
			pls[i] = (Playlist)list.remove(indices[i]);
			if(nbBefore == -1 && pls[i] == current) {
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

		Messages.UPDATE_PLS = true;
		Messages.SELECT_PL = currentIndex;

		return pls;
	}

	public void remove(Playlist pl) {
		//Get the current playlist
		Object current = list.get(currentIndex);

		list.remove(pl);

		//Search the new indice for the current music playing
		int newIndex = list.indexOf(current);
		if(newIndex == -1) {
			checkIndex();
		}
		else {
			currentIndex = newIndex;
		}

		Messages.UPDATE_PLS = true;
		Messages.SELECT_PL = currentIndex;
	}

	/*
	 * Remove all playlists
	 */
	public void clear() {
		list.clear();
		list = new Vector();
		currentIndex = -1;

		Messages.UPDATE_PLS = true;
		Messages.UPDATE_MS = true;
	}

	/*Sort the list*/

	protected static Comparator playlistComparator = new Comparator(){
		/**
		 * Implementation of the interface Comparator
		 */
		public int compare(Object o1, Object o2) {
			//get the mane of the musics
			String s1 = ((Playlist)o1).getName().toLowerCase();
			String s2 = ((Playlist)o2).getName().toLowerCase();

			//get the minimum number of character
			int min = Math.min(s1.length(), s2.length());

			//loop of each character of the string
			for(int i = 0; i < min; i++) {
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
	 * @return the index of the current playlist
	 */
	public int sort() {
		Object current = list.get(currentIndex);

		/*
		 * Sort the list using the comparator object
		 */
		Collections.sort(list, playlistComparator);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_PLS = true;

		return currentIndex;
	}

	/*Getter and Setter*/

	/**
	 * @return the index of the current playlist
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * @param index index of the current playlist
	 * @return the index of the playlist selected
	 */
	private int setCurrentIndex(int index) {
		if(index != currentIndex) {
			int oldIndex = currentIndex;
			currentIndex = index;
			checkIndex();

			if(oldIndex != currentIndex) {
				Messages.UPDATE_MS = true;
				Messages.ENSURE_PL_VISIBLE = currentIndex;
				Messages.SELECT_M = get(currentIndex).getCurrentIndex();
			}
		}
		return currentIndex;
	}

	public int select(int index, Options options) {
		int oldIndex = currentIndex;
		int newIndex = setCurrentIndex(index);
		if(oldIndex != -1 && options.isAutoSave()) {
			get(oldIndex).autoSave(options);
		}
		return newIndex;
	}

	/**
	 * @return true if the index is a valid playlist
	 */
	public boolean isIndexValid(int index) {
		return (index >= 0 && index < list.size());
	}

	/**
	 * Select the next playlist
	 * @return the new index of the playlist used
	 */
	public int nextPlaylist(int offset) {
		int newIndex = currentIndex + offset;
		if(newIndex < 0) {
			newIndex = size() - 1;
		}
		return setCurrentIndex(newIndex);
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

	/**
	 * @return the playlist that correspond to the index in the list
	 */
	public Playlist get(int index) {
		return (Playlist)list.get(index);
	}

	/**
	 * @return Get the current music playing
	 */
	public Playlist getCurrent() {
		if(list.size() <= 0) {
			Playlist pl = new Playlist();
			pl.DEFAULT = true;
			pl.setTempName(MusicPlayerEx.lang.getString("default_playlist_name"));
			add(pl);
		}
		checkIndex();
		return get(currentIndex);
	}

	/**
	 * @return Get the play list file path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the play list file path
	 * @param path path of the playlist
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/* List management */

	public int moveFirst(int index) {
		Object current = list.get(currentIndex);

		list.insertElementAt(list.remove(index), 0);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_PLS = true;
		Messages.SELECT_PL = 0;

		return currentIndex;
	}

	public int moveUp(int index) {
		Object current = list.get(currentIndex);

		list.insertElementAt(list.remove(index), index - 1);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_PLS = true;
		Messages.SELECT_PL = index - 1;

		return currentIndex;
	}

	public int moveDown(int index) {
		Object current = list.get(currentIndex);

		list.insertElementAt(list.remove(index), index + 1);

		currentIndex = list.indexOf(current);

		Messages.UPDATE_PLS = true;
		Messages.SELECT_PL = index + 1;

		return currentIndex;
	}

	public int moveLast(int index) {
		Object current = list.get(currentIndex);

		list.add(list.remove(index));

		currentIndex = list.indexOf(current);

		Messages.UPDATE_PLS = true;
		Messages.SELECT_PL = list.size() - 1;

		return currentIndex;
	}
}