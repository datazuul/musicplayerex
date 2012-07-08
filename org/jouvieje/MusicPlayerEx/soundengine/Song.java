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
package org.jouvieje.MusicPlayerEx.soundengine;

import java.io.File;

import org.jouvieje.fmodex.Channel;
import org.jouvieje.fmodex.Sound;
import org.jouvieje.MusicPlayerEx.MusicPlayerEx;

public class Song implements SongConstants {
	private final static boolean DEBUG_MODE = false;

	/** Path of the music */
	private final String absolutePath;
	/** Title of the music */
	private String title = null;

	/* Track of the music, used for Audio CD and FSB */
	private final int track;
	/* Type of music : use internaly by the SoundEngine */
	private final int type;

	/** Play the music in loop */
	private boolean loop = false;		//Always play the music one time

	/* Holds the object used by the SoundEngine as a reference to the music */
	private Sound sound = null;
	private Sound depSound = null;
	/* Channel in which the music is playing */
	private Channel channel = null;
	/* Music is loaded in memory ? */
	private boolean loadedInMemory = false;

	private Song(String absolutePath, int type, int track) {
		this.absolutePath = absolutePath;
		this.type = type;
		this.track = track;
	}

	/**
	 * Constructor of a Song
	 * @param absolutePath path of the final (It can be a fully qualified name or a relative path)
	 */
	public Song(String absolutePath) {
		this(absolutePath, -1);
	}
	/**
	 * Constructor of a Song
	 * @param absolutePath path of the final (It can be a fully qualified name or a relative path)
	 * @param title name or title of the music
	 */
	public Song(String absolutePath, int track) {
		this(absolutePath, track, "");
		if(!absolutePath.endsWith("cda")) {
			String s = new File(absolutePath).getName();
			int index = s.lastIndexOf(".");
			if(index != -1)
				s = s.substring(0, index);
			if(track != -1)
				s += " ("+(track+1)+")";
			title = s;
		}
	}

	/**
	 * Constructor of a Song
	 * @param absolutePath path of the final (It can be a fully qualified name or a relative path)
	 * @param track is the track number of music of an AudioCD (first track is 0)
	 * @param title name or title of the music
	 */
	public Song(String absolutePath, int track, String title) {
		if(absolutePath.endsWith("cda")) {
			String pathTemp;
			String titleTemp;
			int trackTemp;
			int typeTemp;
			try {
				pathTemp = absolutePath.substring(0, absolutePath.indexOf("/"));
				trackTemp = Integer.parseInt(absolutePath.substring(absolutePath.indexOf("track")+5, absolutePath.indexOf(".cda")))-1;
				titleTemp = String.format(MusicPlayerEx.lang.getString("track_name"), new Object[]{new Integer(trackTemp+1)});
				typeTemp = SongConstants.CD;
			} catch(Exception e) {
				pathTemp = absolutePath;
				titleTemp = title;
				trackTemp = -1;
				typeTemp = Song.getType(this.absolutePath);
			}

			this.absolutePath = pathTemp;
			this.track = trackTemp;
			this.title = titleTemp;
			this.type = typeTemp;
		}
		else {
			this.absolutePath = absolutePath;
			this.track = track;
			this.title = title;
			this.type = Song.getType(this.absolutePath);
		}
		printDebug("PATH="+this.absolutePath);
		printDebug("TITLE="+title);
		printDebug("TRACK="+track);
		printDebug("TYPE="+type);
	}

	public Object clone() {
		Song song = new Song(absolutePath, type, track);
		song.loop = loop;
		song.title = title;
		song.title = title;
		song.reset();
		return song;
	}

	/*
	 * This method is called by the Garbage Collector when the object has been ended
	 * to use.
	 */
	protected void finalize() {
		//Free the memory used if needed
		if(loadedInMemory) {
			SoundEngineEx.get().stop(this);
		}
	}

	/*
	 * Reset the music when it is finished ot play (after the memory free)
	 */
	protected void reset() {
		sound = null;
		depSound = null;
		channel = null;
		loadedInMemory = false;
	}

	/**
	 * Get the type of the Song ie STREAM, SAMPLE, AUDIO CD TRACK or NETWROK
	 */
	private static int getType(String path) {
		String extension;

		if(path.startsWith("http://")) {
			return NETWORK;
		}

		if(path.length() == 2 && path.endsWith(":") &&
		   path.toLowerCase().charAt(0) >= 'c' &&
		   path.toLowerCase().charAt(0) <= 'z') {
			return CD;
		}
		if(path.endsWith("/")) {
			return CD;
		}

		try {
			extension = getExtension(path);
		} catch(IndexOutOfBoundsException e) {
			if(Tools.DEBUG_MODE)
				System.out.println("File : "+path+" is not supported !!!");
			return UNSUPPORTED;
		}

		//convert the extension to a music type
		if(STREAM_SUPPORTED.indexOf(extension) != -1) {
			return STREAM;
		}
		else if(SOUND_SUPPORTED.indexOf(extension) != -1) {
			return SOUND;
		}

		if(Tools.DEBUG_MODE) {
			System.out.println("File : "+path+" is not supported !!!");
		}
		return UNSUPPORTED;
	}
	/**
	 * @param name file name
	 * @return the extension of the file in lower case
	 * @throws IndexOutOfBoundsException
	 */
	private static String getExtension(String name) throws IndexOutOfBoundsException {
		int index = name.lastIndexOf(".");
		return name.substring(index + 1, name.length()).toLowerCase();
	}

	/*Getter and Setter*/

	/** @return the path of the music */
	public String getAbsolutePath() {
		return absolutePath;
	}

	/** @return the title of the music */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of the music
	 * @param title title of the music
	 */
	public void setTitle(String title) {
		if(getType() != SongConstants.CD) {
			this.title = title;
		}
	}

	/** @return the type of the music */
	public int getType() {
		return type;
	}

	/** @return the title of the music */
	public int getTrack() {
		return track;
	}

	/** @return the channel in which the music is playing */
	protected Channel getChannel() {
		return channel;
	}
	/** @param channel channel in which the music is playing */
	protected void setChannel(Channel channel) {
		this.channel = channel;
	}

	/** @return if the music should be played in loop */
	public boolean isLoop() {
		return loop;
	}
	/** @param loop music should be played in loop ? */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	/** @return true if the music is loaded in the memory */
	public boolean isLoadedInMemory() {
		return loadedInMemory;
	}
	/**
	 * Defines if the music is loaded in memory
	 *
	 * @param loadedInMemory
	 */
	public void setLoadedInMemory(boolean loadedInMemory) {
		this.loadedInMemory = loadedInMemory;
	}

	/* @return Used by SoundEngine to get the reference of the music */
	protected Sound getSound() {
		return sound;
	}
	/* @param  Use by SoundEngine, handler reference to the music */
	protected void setSound(Sound sound) {
		this.sound = sound;
	}

	/* @return Used by SoundEngine to get the reference of the music */
	protected Sound getDepSound() {
		return depSound;
	}
	protected Sound getSoundOrDepSound() {
		return (depSound != null) ? depSound : sound;
	}
	/* @param  Use by SoundEngine, handler reference to the music */
	protected void setDepSound(Sound depSound) {
		this.depSound = depSound;
	}

	private void printDebug(String message) {
		if(DEBUG_MODE) System.out.println("DEBUG : " + message);
	}
	
	public final boolean isSubSound() {
		return (depSound != null);
	}
}