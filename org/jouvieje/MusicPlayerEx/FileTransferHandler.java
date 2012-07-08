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
package org.jouvieje.MusicPlayerEx;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

class FileTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	
	private final DataFlavor fileFlavor;
	private final MusicPlayerGUI_V2 musicPlayer;

	public FileTransferHandler(MusicPlayerGUI_V2 musicPlayer) {
		this.musicPlayer = musicPlayer;
		fileFlavor = DataFlavor.javaFileListFlavor;
	}

	public boolean importData(JComponent c, Transferable t) {
		if(!canImport(c, t.getTransferDataFlavors())) return false;

		List files = null;
		try {
			files = (List)t.getTransferData(fileFlavor);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		final Vector musics = new Vector();
		final Vector playlists = new Vector();
		for(int i = 0; i < files.size(); i++) {
			File file = (File)files.get(i);
			if(!file.isDirectory()) {
				if(musicPlayer.getSupportedMusicFilter().accept(file)) {
					musics.add(file);
				}
				else if(musicPlayer.getSupportedPlaylistFilter().accept(file)) {
					playlists.add(file);
				}
			}
		}

		musicPlayer.openMedias(playlists, musics);

		return true;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for(int i = 0; i < flavors.length; i++) {
			if(fileFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}
}