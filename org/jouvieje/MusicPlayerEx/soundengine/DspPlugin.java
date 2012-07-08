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

import java.awt.Canvas;
import java.io.File;

import org.jouvieje.fmodex.DSP;

public class DspPlugin {
	protected DSP dsp = new DSP(); /*Use by sound engine*/
	protected String name = null; /*Used by the GUI*/
	protected boolean hasGui = false;
	protected int width = -1; /*Used by the GUI*/
	protected int height = -1; /*Used by the GUI*/

	protected DspPlugin() {}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}

	public boolean createGUI(Canvas canvas) {
		if(dsp == null) return false;
		dsp.showConfigDialog(canvas, true);
		return true;
	}
	public boolean destroyGUI(Canvas canvas) {
		if(dsp == null) return false;
		dsp.showConfigDialog(canvas, false);
		return true;
	}

	public static DspPlugin load(File file) {
		return SoundEngineEx.get().loadDstPlugin(file);
	}

	public static void unload(DspPlugin plugin) {
		SoundEngineEx.get().unloadDstPlugin(plugin);
	}
}
