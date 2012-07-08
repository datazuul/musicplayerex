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
package org.jouvieje.MusicPlayerEx.env;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jouvieje.MusicPlayerEx.FileTools;
import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.playlist.Playlists;

public class EnvWriter implements EnvConstants {
	public static String verifyFileName(String name) {
		if(!name.endsWith(EXTENSION)) {
			name += "." + EXTENSION;
		}
		return name;
	}

	/**
	 * @param fileName path of the file to write in
	 * @param playlists list of playlists to save
	 * @return true if the file is created
	 */
	public static boolean write(String fileName, Playlists playlists, boolean absolute) {
		File env = new File(verifyFileName(fileName));
		env.getParentFile().mkdirs();

		try {
			//create a new buffer from the output file
			BufferedWriter bw = new BufferedWriter(new FileWriter(env));

			//write the header and the name of the play list
			bw.write(HEADER_ATTRIB); bw.newLine();
			bw.write(PLAYLIST_NUMBER_ATTRIB + "=" + playlists.size()); bw.newLine();
			bw.write(CURRENT_PLAYLIST_ATTRIB + "=" + playlists.getCurrentIndex()); bw.newLine();

			//write the musics
			File parent = env.getParentFile();
			for(int i = 0; i < playlists.size(); i++) {
				Playlist playlist = playlists.get(i);
				if(playlist.getPath() != null) {
					bw.write(PLAYLIST_PATHi_ATTRIB + i + "=" + FileTools.getPath(playlist.getPath(), parent, absolute)); bw.newLine();
					bw.write(CURRENT_MUSICi_ATTRIB + i + "=" + playlist.getCurrentIndex()); bw.newLine();
				}
				else {
					bw.write(PLAYLIST_PATHi_ATTRIB + i + "="); bw.newLine();
					bw.write(CURRENT_MUSICi_ATTRIB + i + "=-1"); bw.newLine();
				}
			}

			//close the buffer
			bw.close();
		}
		catch(IOException ioe) {
			System.err.println("FAILED TO CREATE THE " + fileName + " FILE !!!");
			return false;
		}

		return true;
	}
}