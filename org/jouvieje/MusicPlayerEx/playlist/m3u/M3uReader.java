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
package org.jouvieje.MusicPlayerEx.playlist.m3u;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.jouvieje.MusicPlayerEx.FileTools;
import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.soundengine.Song;

public class M3uReader implements M3uConstants {
	/**
	 * @param fileName path of the file to read
	 * @return
	 */
	public static boolean read(Playlist playlist) {
		if(!playlist.getPath().toLowerCase().endsWith(EXTENSION)) {
			return false;
		}

		try {
			File playlistFile = new File(playlist.getPath());

			Vector vector = new Vector();
			BufferedReader br = new BufferedReader(new FileReader(playlistFile));

			String line = null;
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.equals("") && !line.startsWith("#")) {
					vector.add(line);
				}
			}

			br.close();

			//Add only supported musics in the playlist
			for(int i = 0; i <= vector.size() - 1; i++) {
				Song music = new Song(FileTools.getAbsolutePath((String)vector.get(i), playlistFile.getParentFile()));
				if(music.getType() != Song.UNSUPPORTED) {
					playlist.add(music);
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