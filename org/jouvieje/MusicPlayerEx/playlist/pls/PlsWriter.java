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

import org.jouvieje.MusicPlayerEx.FileTools;
import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.soundengine.Song;

public class PlsWriter implements PlsConstants {
	public static String verifyExtension(String name) {
		if(!name.endsWith(EXTENSION)) {
			name += "."+EXTENSION;
		}
		return name;
	}

	/**
	 * @param fileName path of the file to write in
	 * @param playlist playlist to save
	 * @return true if the file is created
	 */
	public static boolean write(Playlist playlist, boolean absolute) {
		File file = new File(verifyExtension(playlist.getPath()));
		File parent = file.getParentFile();
		if(parent == null) {
			return false;
		}
		parent.mkdirs();

		try {
			//create a new buffer from the output file
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			//write the header and the name of the play list
			bw.write(HEADER_ATTRIB);
			bw.newLine();
			bw.write(PLAYLISTNAME_ATTRIB+"="+playlist.getName());
			bw.newLine();
			bw.write(NB_ENTRIES_ATTRIB+"="+playlist.size());
			bw.newLine();

			//write the musics
			for(int i = 0; i <= playlist.size() - 1; i++) {
				Song song = playlist.get(i);
				bw.write(FILEi_ATTRIB+i+"="+FileTools.getPath(song.getAbsolutePath(), parent, absolute));
				bw.newLine();
				bw.write(TITLEi_ATTRIB+i+"="+song.getTitle());
				bw.newLine();
				if(song.getTrack() >= 0) {
					bw.write(TRACKi_ATTRIB+i+"="+song.getTrack());
					bw.newLine();
				}
			}

			//write the version of the file format
			bw.write(VERSION_ATTRIB);
			bw.newLine();

			//close the buffer
			bw.close();
		}
		catch(IOException ioe) {
			System.err.println("Failed to write in "+playlist.getPath()+" !!!");
			return false;
		}

		return true;
	}
}