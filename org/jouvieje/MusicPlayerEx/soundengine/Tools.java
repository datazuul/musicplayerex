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

import org.jouvieje.fmodex.FmodEx;
import org.jouvieje.fmodex.enumerations.FMOD_RESULT;

public class Tools {
	protected static boolean DEBUG_MODE = true;

	public static void print(String message) {
		java.lang.System.out.print(message);
	}

	public static void println(String message) {
		java.lang.System.out.println(message);
	}

	protected static boolean errCheckSilent(FMOD_RESULT result) {
		if(result != FMOD_RESULT.FMOD_OK && result != FMOD_RESULT.FMOD_ERR_INVALID_HANDLE) {
			return false;
		}
		return true;
	}

	protected static boolean errCheck(FMOD_RESULT result) {
		if(result != FMOD_RESULT.FMOD_OK && result != FMOD_RESULT.FMOD_ERR_INVALID_HANDLE) {
			println("FMOD error! (" + result.asInt() + ") " + FmodEx.FMOD_ErrorString(result));
			return false;
		}
		return true;
	}

	protected static void printDebug(String message) {
		if(DEBUG_MODE) java.lang.System.out.println(message);
	}
}