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

import java.util.Vector;

public class Env {
	private String fileName = null;
	private int startIndex = -1;
	private Vector paths = new Vector();
	private Vector indexes = new Vector();

	public Env(String fileName, int startIndex) {
		this.fileName = fileName;
		this.startIndex = startIndex;
	}

	public int size() {
		return paths.size();
	}

	public void addItem(String path, int index) {
		paths.add(path);
		indexes.add(new Integer(index));
	}

	public String getPath(int i) {
		return (String)paths.get(i);
	}

	public int getIndex(int i) {
		return ((Integer)indexes.get(i)).intValue();
	}

	public int getStartIndex() {
		return startIndex;
	}

	public String getFileName() {
		return fileName;
	}
}