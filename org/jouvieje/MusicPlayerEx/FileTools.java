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
import java.io.IOException;

public class FileTools {
	/**
	 * Search the file extension
	 * @param f a file
	 * @return the file extension in lower case<BR>
	 * null if the file is null<BR>
	 * "" if no extensions.
	 */
	public static String getExtension(File f) {
		if(f != null) {
			String filename = f.getName().toLowerCase();
			int index = filename.lastIndexOf('.');
			if(index > 0 && index < filename.length() - 1) {
				return filename.substring(index + 1);
			}
			return "";
		}
		return null;
	}

	public static String getPath(String path, File parent, boolean absolute) {
		return absolute ? getAbsolutePath(path, parent) : getRelativePath(path, parent);
	}

	public static String getAbsolutePath(String path, File parent) {
		if(path.endsWith("cda")) {
			return path;
		}

		File file = new File(path);
		if(file.exists()) {
			if(file.isDirectory()) {//May be an Audio CD ?
				return path;
			}
			return file.getAbsolutePath();
		}

		File f = new File(parent, path);
		if(f.exists()) {
			try {
				return f.getCanonicalPath();
			} catch(IOException e) {
				return f.getAbsolutePath();
			}
		}

		System.out.println("NOT_FOUND=" + parent + "|" + path + "[" + f.getAbsolutePath() + "]");

		return path;
	}

	public static String getRelativePath(String path, File parent) {
		try {
			File file = new File(path);
			if(!file.exists() || path.startsWith("http") || path.startsWith("cda")) {
				return path;
			}

			String canonicalFile = file.getCanonicalPath();
			String canonicalParent = parent.getCanonicalPath();

			int min = Math.min(canonicalFile.length(), canonicalParent.length());
			int maxIndex = -1;
			for(int i = 0; i < min; i++) {
				if(canonicalFile.charAt(i) != canonicalParent.charAt(i)) {
					maxIndex = i - 1;
					break;
				}
				else if(i == min - 1) {
					maxIndex = min - 1;
				}
			}
			if(maxIndex == -1) {
				return path;
			}

			if(maxIndex == min - 1) {
				return canonicalFile.substring(min + 1);
			}
			else {
				int index = canonicalParent.substring(0, maxIndex + 1).lastIndexOf(File.separatorChar);

				String relativePath = canonicalFile.substring(index + 1);
				String upDirs = canonicalParent.substring(index + 1);
				if(upDirs.endsWith("" + File.separatorChar)) {
					upDirs = upDirs.substring(0, upDirs.length());
				}

				if(!upDirs.equals("")) {
					relativePath = ".." + File.separatorChar + relativePath;

					for(int i = 0; i < upDirs.length(); i++) {
						if(upDirs.charAt(i) == File.separatorChar) {
							relativePath = ".." + File.separatorChar + relativePath;
						}
					}
				}
				return relativePath;
			}
		} catch(IOException e) {
			return path;
		}
	}
}