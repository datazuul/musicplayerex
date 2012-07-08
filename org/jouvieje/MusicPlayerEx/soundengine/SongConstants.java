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

public interface SongConstants {
	public final static int UNSUPPORTED = -1;
	public final static int STREAM = 1;
	public final static int SOUND = 2;
	public final static int CD = 3;
	public final static int NETWORK = 4;

	public static final String STREAM_SUPPORTED = "wav,mp2,mp3,ogg,raw,wma";
	public static final String SOUND_SUPPORTED = "mid,mod,s3m,it,xm,fsb,rmi,sgt,aiff";

	public static final String[] STREAM_FORMAT_SUPPORTED = new String[]{"wav", "mp2", "mp3", "ogg", "raw", "wma"};
	public static final String[] SOUND_FORMAT_SUPPORTED = new String[]{"mid", "mod", "s3m", "it", "xm", "fsb", "rmi", "sgt", "aiff"};
}