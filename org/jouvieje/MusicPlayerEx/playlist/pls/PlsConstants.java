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
package org.jouvieje.MusicPlayerEx.playlist.pls;

/** PLS format */
public interface PlsConstants {
	public final static String EXTENSION = "pls";
	public final static String HEADER_ATTRIB = "[playlist]";
	public final static String PLAYLISTNAME_ATTRIB = "PlaylistName";
	public final static String NB_ENTRIES_ATTRIB = "NumberOfEntries";
	public final static String FILEi_ATTRIB = "File";
	public final static String TITLEi_ATTRIB = "Title";
	public final static String TRACKi_ATTRIB = "Track";
	public final static String VERSION_ATTRIB = "Version=2";
}