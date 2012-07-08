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

import java.io.*;
import java.util.Hashtable;

import org.jouvieje.MusicPlayerEx.FileTools;
import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.soundengine.Song;

public class PlsReader implements PlsConstants {
	/**
	 * @param fileName path of the file to read
	 * @return
	 */
	public static boolean read(final Playlist playlist) {
		if(!playlist.getPath().toLowerCase().endsWith(EXTENSION)) {
			return false;
		}

		try {
			//TODO use to much memory

			//stock temporary the musics
			final Hashtable files = new Hashtable();
			final Hashtable titles = new Hashtable();
			final Hashtable tracks = new Hashtable();

			final File file = new File(playlist.getPath());
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.readLine();

//			int nbEntries = -1;
			String line;
			while((line = br.readLine()) != null) {
				line = line.trim();

				if(line.startsWith(PLAYLISTNAME_ATTRIB)) {
					int index = line.indexOf("=");
					playlist.setName(line.substring(index+1).trim());
				}
//				else if(line.startsWith(NB_ENTRIES_ATTRIB)) {
//					int index = line.indexOf("=");
//					nbEntries = Integer.parseInt(line.substring(index+1).trim());
//				}
				else if(line.startsWith(FILEi_ATTRIB) || line.startsWith(TITLEi_ATTRIB) || line.startsWith(TRACKi_ATTRIB)) {
					break;
				}
			}

			while(line != null) {
				if(line.startsWith(FILEi_ATTRIB)) {
					int start = line.indexOf(FILEi_ATTRIB)+FILEi_ATTRIB.length();
					int end = line.indexOf("=");
					int i = Integer.parseInt(line.substring(start, end));
					files.put(new Integer(i), line.substring(end+1).trim());
				}
				else if(line.startsWith(TITLEi_ATTRIB)) {
					int start = line.indexOf(TITLEi_ATTRIB)+TITLEi_ATTRIB.length();
					int end = line.indexOf("=");
					int i = Integer.parseInt(line.substring(start, end));
					titles.put(new Integer(i), line.substring(end+1).trim());
				}
				else if(line.startsWith(TRACKi_ATTRIB)) {
					int start = line.indexOf(TRACKi_ATTRIB)+TRACKi_ATTRIB.length();
					int end = line.indexOf("=");
					int i = Integer.parseInt(line.substring(start, end));
					tracks.put(new Integer(i), line.substring(end+1).trim());
				}

				line = br.readLine();
			}

			br.close();

			File parent = file.getParentFile();
			for(int i = 0; i < files.size(); i++) {
				Object key = new Integer(i);
				String music = (String)files.get(key);
				String title = (String)titles.get(key);
				int track = -1;
				try {
					String trackStr = (String)tracks.get(key);
					if(trackStr != null) {
						track = Integer.parseInt(trackStr);
					}
				} catch(Exception e){}

				Song song = new Song(FileTools.getAbsolutePath(music, parent), track, title);

				if(song.getType() != Song.UNSUPPORTED) {
					playlist.add(song);
				}
			}

			playlist.setModified(false);

			return true;
		}
		catch(Exception ioe) {
			System.err.println("FAILED TO READ THE "+playlist.getPath()+" FILE !!!");
			return false;
		}
	}
}