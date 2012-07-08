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

public class Messages {
	public static volatile boolean SHELL = false;

	public static volatile boolean LOOP = false;

	public static volatile String ERROR = "";
	public static volatile int FAIL_TO_PLAY = 0;
	public static volatile String PERSISTENT_MESSAGE = "";
	public static volatile String MESSAGE = "";
	public static volatile String TEMPORARY_MESSAGE = "";

	public static volatile boolean UPDATE_PLS = false;
	public static volatile boolean UPDATE_MS = false;
	public static volatile int ENSURE_M_VISIBLE = -1;
	public static volatile int ENSURE_PL_VISIBLE = -1;
	public static volatile boolean SHOW_PLS = false;
	public static volatile boolean SHOW_MS = false;
	public static volatile int SELECT_PL = -1;
	public static volatile int SELECT_M = -1;

	public static volatile boolean PLS_LOADED = false;
	public static volatile boolean PLS_PRE_LOADED = false;
	public static volatile boolean PLAYER_INITIALIZED = false;
	public static volatile boolean PLAYER_LOADED = false;

	//Music actions
	public static volatile int PLAY_INDEX = -1;
	public static volatile int PLAY_PLAYLIST = -1;
	public static volatile boolean AUTO_PLAY = false;
	public static volatile boolean FORCE_AUTO_PLAY = false;
	public static volatile boolean PLAY_PAUSE = false;
	public static volatile boolean PLAY_UNPAUSE = false;
	public static volatile boolean PAUSE_UNPAUSE = false;
	public static volatile boolean STOP = false;
	public static volatile int PLAY_NEXT = 0;

	public static volatile boolean UPDATE_NORMALIZER = false;
	public static volatile boolean UPDATE_DISTORSION = false;
	public static volatile boolean UPDATE_DELAY = false;
	public static volatile boolean UPDATE_REVERB = false;
}