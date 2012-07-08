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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jouvieje.fmodex.Channel;
import org.jouvieje.fmodex.Sound;
import org.jouvieje.fmodex.System;
import org.jouvieje.fmodex.defines.FMOD_MODE;
import org.jouvieje.fmodex.defines.FMOD_TIMEUNIT;
import org.jouvieje.fmodex.enumerations.FMOD_RESULT;
import org.jouvieje.fmodex.utils.BufferUtils;
import org.jouvieje.libloader.LibLoader;

public class SharedSoundEngineEx implements SongConstants {
	private static SharedSoundEngineEx instance = null;
	public static SharedSoundEngineEx get() {
		if(instance == null) {
			instance = new SharedSoundEngineEx();
		}
		return instance;
	}

	private static int defaultMode = FMOD_MODE.FMOD_SOFTWARE | FMOD_MODE.FMOD_2D | FMOD_MODE.FMOD_LOOP_OFF | FMOD_MODE.FMOD_IGNORETAGS
//		| FMOD_MODE.FMOD_UNICODE
	;
	private static int mode = defaultMode | FMOD_MODE.FMOD_ACCURATETIME;
	private static int cdMode = FMOD_MODE.FMOD_OPENONLY;
	private static int seekOrNetworkModeFlag = FMOD_MODE.FMOD_MPEGSEARCH;
	private static int memoryModeFlag = FMOD_MODE.FMOD_CREATECOMPRESSEDSAMPLE;
	
	/** */
	private boolean bufferCDSize = false;
	/** Play all sound from memory */
	private boolean playFromMemory = false;
	

	public void close(System system) {
		if(system != null && !system.isNull()) {
			FMOD_RESULT result = system.close();
			Tools.errCheck(result);
			result = system.release();
			Tools.errCheck(result);
			system = null;
		}
	}
	

	public int getNumSubSounds(System system, String path) {
		Sound sound = new Sound();

		FMOD_RESULT result = system.createStream/*Sound*/(path, FMOD_MODE.FMOD_OPENONLY, null, sound);
		Tools.errCheck(result);

		if(!sound.isNull()) {
			IntBuffer nbSubSound = BufferUtils.newIntBuffer(1);
			result = sound.getNumSubSounds(nbSubSound);
			Tools.errCheck(result);

			result = sound.release();
			Tools.errCheck(result);

			return nbSubSound.get(0);
		}
		return 0;
	}
	
	public boolean load(System system, Song song) {
		//If the music is not loaded, load it
		if(song.isLoadedInMemory()) {
			return true;
		}
		
		Sound sound = new Sound();
		Sound depSound = null;
		FMOD_RESULT result = null;

		String filename = song.getAbsolutePath();
//		ByteBuffer utf16Filename = BufferUtils.fromStringUTF16(filename);
		String utf16Filename = filename;
		
		switch(song.getType()) {
			case SOUND:
			case STREAM:
				if(bufferCDSize) {
					result = system.setStreamBufferSize(64*1024, FMOD_TIMEUNIT.FMOD_TIMEUNIT_RAWBYTES);
					Tools.errCheck(result);

					bufferCDSize = false;
				}
				if(song.getTrack() != -1) {
					Sound mainsound = new Sound();

					if(playFromMemory) {
						result = system.createSound(utf16Filename, cdMode | memoryModeFlag, null, mainsound);
						Tools.errCheck(result);
					}
					else {
						result = system.createStream(utf16Filename, cdMode, null, mainsound);
						Tools.errCheck(result);
					}

					if(!mainsound.isNull()) {
						result = mainsound.getSubSound(song.getTrack(), sound);
						Tools.errCheck(result);
					}
					
					depSound = mainsound;
				}
				else {
					if(playFromMemory) {
						result = system.createSound(utf16Filename, mode | memoryModeFlag, null, sound);
						Tools.errCheck(result);
					}
					else {
						result = system.createStream(utf16Filename, mode, null, sound);
						Tools.errCheck(result);
					}
				}
				break;
			case CD:
				if(!bufferCDSize) {
					result = system.setStreamBufferSize(256*1024, FMOD_TIMEUNIT.FMOD_TIMEUNIT_RAWBYTES);
					Tools.errCheck(result);

					bufferCDSize = true;
				}

				Sound cdsound = new Sound();

				result = system.createStream(utf16Filename, cdMode, null, cdsound);
				Tools.errCheck(result);

				if(!cdsound.isNull()) {
					result = cdsound.getSubSound(song.getTrack(), sound);
					Tools.errCheck(result);
				}
				
				depSound = cdsound;
				
				break;
			case NETWORK:
				if(!bufferCDSize) {
					result = system.setStreamBufferSize(256*1024, FMOD_TIMEUNIT.FMOD_TIMEUNIT_RAWBYTES);
					Tools.errCheck(result);

					bufferCDSize = true;
				}
				result = system.createStream(utf16Filename, defaultMode | seekOrNetworkModeFlag, null, sound);
				Tools.errCheck(result);
				break;
			default: Tools.println("UNKNOW SOUND TYPE"); return false;
		}

		/*
		 * Is it an albumwrap ?
		 *
		 * Take a look to the topic :
		 *   http://www.fmod.org/forum/viewtopic.php?t=3266&highlight=albumwrap
		 */
		if(sound.isNull() && song.getType() != CD && song.getType() != NETWORK) {
			Tools.printDebug("Seeking file ...");

			switch(song.getType()) {
				case SOUND:
				case STREAM:
					int playMode = mode | seekOrNetworkModeFlag;
					if(playFromMemory) {
						playMode |= memoryModeFlag;
					}
					result = system.createStream(utf16Filename, mode | seekOrNetworkModeFlag, null, sound);
					Tools.errCheck(result);
					break;
			}
		}

		if(sound.isNull()) {
			Tools.printDebug("FAILED TO LOAD : "+song.getTitle()+" ["+song.getAbsolutePath()+"]");
			return false;
		}
		
		song.setSound(sound);
		song.setDepSound(depSound);
		song.setLoadedInMemory(true);
		
		return true;
	}
	
	public int setPosition(Song song, int position) {
		Channel channel = song.getChannel();

		if(position != 0) {
			final int length = getLength(song);
			position = (int)( position * length / 100.f );
		}

		FMOD_RESULT result = channel.setPosition(position, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MODORDER);
		if(result != FMOD_RESULT.FMOD_OK) {
			result = channel.setPosition(position, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS);
		}

		if(result == FMOD_RESULT.FMOD_OK) {
			return position;
		}

		return 0;
	}
	public int getLength(Song song) {
		Sound sound = song.getSoundOrDepSound();
		if((sound == null) || sound.isNull()) {
			return 0;
		}
		
		IntBuffer length = BufferUtils.newIntBuffer(1);
		FMOD_RESULT result = sound.getLength(length, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MODORDER);
		if(result != FMOD_RESULT.FMOD_OK) {
			result = sound.getLength(length, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS);
			Tools.errCheck(result);
		}
		return length.get(0);
	}
	
	public boolean stop(Song song) {
		Sound sound = song.getSound();
		Sound depSound = song.getDepSound();
		Channel channel = song.getChannel();

		FMOD_RESULT result = null;
		if(channel != null && !channel.isNull()) {
			result = channel.setCallback(null);
			Tools.errCheck(result);

			result = channel.stop();
			Tools.errCheck(result);
			channel = null;
		}
		if(sound != null && !sound.isNull()) {
			result = sound.release();
			Tools.errCheck(result);
			sound = null;
		}
		if(depSound != null && !depSound.isNull()) {
			result = depSound.release();
			Tools.errCheck(result);
			depSound = null;
		}
		song.reset();

		return result == FMOD_RESULT.FMOD_OK;
	}

	public boolean isPlaying(Song song) {
		Channel channel = song.getChannel();
		if(channel == null || channel.isNull()) {
			return false;
		}

		ByteBuffer isPlaying = BufferUtils.newByteBuffer(1);
		FMOD_RESULT result = channel.isPlaying(isPlaying);
		Tools.errCheck(result);

		return isPlaying.get(0) != 0;
	}

	public boolean isPaused(Song song) {
		Channel channel = song.getChannel();
		if(channel == null || channel.isNull()) {
			return false;
		}

		ByteBuffer isPaused = BufferUtils.newByteBuffer(1);
		FMOD_RESULT result = channel.getPaused(isPaused);
		Tools.errCheck(result);

		return isPaused.get(0) != 0;
	}
	
	public final static int UNKNOWN = -1;
	public final static int WIN = 0;
	public final static int LINUX = 1;
	public final static int MAC = 2;

	public int getPlatform() {
		switch(LibLoader.getPlatform()) {
			case LibLoader.PLATFORM_WINDOWS: return WIN;
			case LibLoader.PLATFORM_LINUX: return LINUX;
			case LibLoader.PLATFORM_MAC: return MAC;
			default: return UNKNOWN;
		}
	}
	public boolean isPlatform64Bits() {
		return LibLoader.isPlatform64Bits();
	}
	
	public boolean isPlayFromMemory() { return playFromMemory; }
	public void setPlayFromMemory(boolean playFromMemory) { this.playFromMemory = playFromMemory; }
}
