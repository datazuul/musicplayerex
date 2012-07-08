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

import java.io.File;
import java.util.Vector;
import javax.swing.filechooser.FileFilter;

public class MyFileFilter extends FileFilter {
	private Vector extensions = new Vector();
	private String description = "";
	private final boolean extensionInDescription;

	/**
	 * @param extensions store the extensions accepted
	 * @param description description shown in the FileChooser
	 */
	public MyFileFilter(String[] extensions, String description) {
		this(extensions, description, true);
	}

	/**
	 * @param extensions store the extensions accepted
	 * @param description description shown in the FileChooser
	 * @param extensionInDescription extension shown in the description ?
	 */
	public MyFileFilter(String[] extensions, String description, boolean extensionInDescription) {
		this.extensionInDescription = extensionInDescription;
		registerExtensions(extensions);
		addDescription(description, extensions, true);
	}

	private void registerExtensions(String[] exts) {
		for(int i = 0; i <= exts.length - 1; i++) {
			extensions.add(exts[i]);
		}
	}

	private void addDescription(String descript, String[] extensions, boolean init) {
		if(descript != null && init) {
			description += descript;
		}

		if(extensionInDescription) {
			if(init) {
				description = descript + " (";
			}
			else {
				description = description.substring(0, description.length() - 1);
			}

			for(int i = 0; i <= extensions.length - 1; i++) {
				if(i != 0 || (i == 0 && !init)) {
					description += ", ";
				}
				description += extensions[i];
			}
			description += ")";
		}
	}

	public boolean accept(File f) {
		if(f.isDirectory()) {
			return true;
		}
		return extensions.indexOf(FileTools.getExtension(f)) != -1;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Add extensions
	 * @param newExtensions extensions to adds
	 */
	public void addExtensions(String[] newExtensions) {
		registerExtensions(newExtensions);
		addDescription(null, newExtensions, false);
	}

	/**
	 * Add an extension
	 * @param newExtension extension to add
	 */
	public void addExtension(String newExtension) {
		extensions.add(newExtension);
		addDescription(null, new String[]{newExtension}, false);
	}

	public boolean containsExtension(String extension) {
		return extensions.contains(extension);
	}

	/**
	 * @return true if the extension is shown in the description of the file chooser
	 */
	public boolean isExtensionInDescription() {
		return extensionInDescription;
	}
}
