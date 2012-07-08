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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.jouvieje.MusicPlayerEx.FileTools;

public class EnvReader implements EnvConstants {
	/**
	 * @param fileName path of the file to read
	 * @return
	 */
	public static Env read(String fileName) {
		if(!fileName.endsWith(EXTENSION)) {
			return null;
		}

		try {
			File file = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.readLine(); //Skip first line

			int size = Integer.valueOf(br.readLine().substring(PLAYLIST_NUMBER_ATTRIB.length() + 1)).intValue();
			int playlistIndex = Integer.valueOf(br.readLine().substring(CURRENT_PLAYLIST_ATTRIB.length() + 1)).intValue();

			Env env = new Env(fileName, playlistIndex);
			File parent = file.getParentFile();
			for(int i = 0; i < size; i++) {
				//Playlist path
				String pathi = br.readLine().substring((PLAYLIST_PATHi_ATTRIB + i).length() + 1);
				pathi = FileTools.getAbsolutePath(pathi, parent);
				int musici = Integer.valueOf(br.readLine().substring((CURRENT_MUSICi_ATTRIB + i).length() + 1)).intValue();

				env.addItem(pathi, musici);
			}

			br.close();

			return env;
		}
		catch(Exception ioe) {
			System.err.println("FAILED TO READ THE " + fileName + " FILE !!!");
			return null;
		}
	}
}