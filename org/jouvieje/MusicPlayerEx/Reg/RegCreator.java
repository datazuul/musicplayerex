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
package org.jouvieje.MusicPlayerEx.Reg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RegCreator {
	public static void create(final String[] args) {
		if(args.length > 1) {
			try {
				File backup = new File("Backup.bat");
				BufferedWriter bw = new BufferedWriter(new FileWriter(backup));
				bw.write("regedit /s Keys.reg"); bw.newLine();
				bw.write("del Keys.reg"); bw.newLine();

				File keys = new File("Keys.reg");
				BufferedWriter bw_keys = new BufferedWriter(new FileWriter(keys));
				bw_keys.write("REGEDIT4"); bw_keys.newLine();
				bw_keys.newLine();

				File tmp0 = new File("TMP0");
				BufferedWriter bw_tmp = new BufferedWriter(new FileWriter(tmp0));
				bw_tmp.write("REGEDIT4"); bw_tmp.newLine();
				bw_tmp.newLine();

				for(int i = 1; i < args.length; i++) {
					bw.write("regedit /a TMP"+i+" HKEY_CLASSES_ROOT\\."+args[i].toLowerCase()); bw.newLine();
					bw_keys.write("[HKEY_CLASSES_ROOT\\."+args[i].toLowerCase()+"]"); bw_keys.newLine(); bw_keys.newLine();
					bw_tmp.write("[-HKEY_CLASSES_ROOT\\."+args[i].toLowerCase()+"]"); bw_tmp.newLine();
				}
				bw_tmp.write("[-HKEY_CLASSES_ROOT\\Applications\\MusicPlayerEx_shell.bat]"); bw_tmp.newLine(); bw_tmp.newLine();

				bw.write("copy /b TMP0");
				for(int i = 1; i < args.length; i++) {
					bw.write("+TMP"+i);
				}
				bw.write(" Backup.reg"); bw.newLine();
				bw.write("attrib +R Backup.reg"); bw.newLine();

				for(int i = 0; i < args.length; i++) {
					bw.write("del TMP"+i); bw.newLine();
				}
				bw.close();
				bw_keys.close();
				bw_tmp.close();
			}
			catch(IOException e) {
				e.printStackTrace();
				return;
			}
		}

		File file = new File("MusicPlayerEx.reg");
		String lecteur, path;

		try {
			path = new File("./").getCanonicalPath();
			if(!path.endsWith(File.separator)) {
				path += File.separator;
			}

			lecteur = path.substring(0, 2);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("MusicPlayerEx.reg creator");
		System.out.println("-------------------------");
		System.out.println("Install partition = '"+lecteur+"'");
		System.out.println("Install folder = '"+path+"'");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			bw.write("REGEDIT4"); bw.newLine();
			bw.newLine();
			for(int i = 1; i < args.length; i++) {
				String ext = args[i].toLowerCase();
				bw.write("[HKEY_CLASSES_ROOT\\."+ext+"]"); bw.newLine();
				bw.write("@=\"Applications\\\\MusicPlayerEx_shell.bat\""); bw.newLine();
				bw.write("[HKEY_CLASSES_ROOT\\."+ext+"\\OpenWithList\\MusicPlayerEx_shell.bat]"); bw.newLine();
				bw.newLine();
				System.out.println("Register extension = '"+ext+"'");
			}
			bw.write("[HKEY_CLASSES_ROOT\\Applications\\MusicPlayerEx_shell.bat]"); bw.newLine();
			bw.write("@=\"MusicPlayerEx\""); bw.newLine();
			bw.write(""); bw.newLine();
			bw.write("[HKEY_CLASSES_ROOT\\Applications\\MusicPlayerEx_shell.bat\\DefaultIcon]"); bw.newLine();
			//Dependent value
			bw.write("@=\""+path.replace("\\", "\\\\")+"MusicPlayerEx.exe,0\""); bw.newLine();
			bw.write(""); bw.newLine();
			bw.write("[HKEY_CLASSES_ROOT\\Applications\\MusicPlayerEx_shell.bat\\shell]"); bw.newLine();
			bw.write("@=\"open\""); bw.newLine();
			bw.write("\"FriendlyCache\"=\"MusicPlayerEx\""); bw.newLine();
			bw.write(""); bw.newLine();
			bw.write("[HKEY_CLASSES_ROOT\\Applications\\MusicPlayerEx_shell.bat\\shell\\open]"); bw.newLine();
			bw.write("@=\"&Play with MusicPlayerEx\""); bw.newLine();
			bw.write(""); bw.newLine();
			bw.write("[HKEY_CLASSES_ROOT\\Applications\\MusicPlayerEx_shell.bat\\shell\\open\\command]"); bw.newLine();
			//Dependent value
			bw.write("@=\"\\\""+path.replace("\\", "\\\\")+"MusicPlayerEx_shell.bat\\\" "+lecteur+" \\\""+path.replace("\\", "\\\\")+"\\\" \\\"%*\\\" \\\"%1\\\"\""); bw.newLine();
			bw.write(""); bw.newLine();

			bw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}