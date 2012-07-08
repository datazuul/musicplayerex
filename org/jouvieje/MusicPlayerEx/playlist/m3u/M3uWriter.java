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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jouvieje.MusicPlayerEx.FileTools;
import org.jouvieje.MusicPlayerEx.playlist.Playlist;
import org.jouvieje.MusicPlayerEx.soundengine.Song;

public class M3uWriter implements M3uConstants {
	public static String verifyExtension(String name) {
		if(!name.endsWith(EXTENSION)) {
			name += "." + EXTENSION;
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

			bw.write(HEADER);
			bw.newLine();

			//write the musics
			for(int i = 0; i <= playlist.size() - 1; i++) {
				Song song = playlist.get(i);

				if(song.getType() == Song.CD) {
					bw.write(song.getAbsolutePath()+"/track"+(song.getTrack()+1)+".cda");
				}
				else {
					bw.write(FileTools.getPath(song.getAbsolutePath(), parent, absolute));
				}
				bw.newLine();
			}

			//close the buffer
			bw.close();
		}
		catch(IOException ioe) {
			System.err.println("Failed to write in "+playlist.getPath());
			return false;
		}

		return true;
	}
}