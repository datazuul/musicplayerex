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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jouvieje.fmodex.*;
import org.jouvieje.fmodex.System;
import org.jouvieje.fmodex.callbacks.*;
import org.jouvieje.fmodex.defines.*;
import org.jouvieje.fmodex.enumerations.*;
import org.jouvieje.fmodex.utils.BufferUtils;
import org.jouvieje.fmodex.exceptions.*;

public class SoundEngineEx implements SongConstants, VERSIONS {
	public static String INIT_ERROR = "";
	
	private static SoundEngineEx instance = null;
	public static SoundEngineEx get() {
		if(instance == null) {
			instance = new SoundEngineEx();
		}
		return instance;
	}

	/** Sound System */
	private System fmodExSystem = null;
	/** Soundcard ID */
	private int soundcard = -1;
	/** Normalize DSP */
	private DspPluginEmbed normalizeDsp = null;
	/** Distorsion DSP */
	private DspPluginEmbed distorsionDsp = null;
	/** Delay DSP */
	private DspPluginEmbed delayDsp = null;
	/** Reverb DSP */
	private DspPluginEmbed reverbDsp = null;
	
	/** End callback */
	private FMOD_CHANNEL_CALLBACK endCallback = null;

	/** Mute value */
	private boolean mute = false;
	/** Volume value */
	private float volume = 0.5f;
	
	private SoundEngineEx() {
		
	}

	protected System getSystem() {
		if(fmodExSystem == null) {
			/*
			 * NativeFmodEx Init
			 */
			try {
//				Init.DEBUG = true;
				Init.loadLibraries();
			}
			catch(InitException e) {
				INIT_ERROR = "NativeFmodEx error! "+e.getMessage();
				Tools.println(INIT_ERROR);
				return null;
			}

			/*
			 * Checking NativeFmodEx version
			 */
			if(NATIVEFMODEX_JAR_VERSION != NATIVEFMODEX_LIBRARY_VERSION) {
				INIT_ERROR = "Error!  NativeFmodEx library version ("+NATIVEFMODEX_LIBRARY_VERSION+") is different to jar version ("+NATIVEFMODEX_JAR_VERSION+")";
				Tools.println(INIT_ERROR);
				return null;
			}

			/*==================================================*/

			fmodExSystem = new System();
			FMOD_RESULT result = FmodEx.System_Create(fmodExSystem);
			Tools.errCheck(result);

			ByteBuffer buffer = BufferUtils.newByteBuffer(BufferUtils.SIZEOF_INT);
			result = fmodExSystem.getVersion(buffer.asIntBuffer());
			Tools.errCheck(result);
			int version = buffer.getInt(0);

			Tools.println("");
			Tools.println("SOUND ENGINE EX INITIALIZATION");
			Tools.println("==============================");
			Tools.println("NativeFmodEx jar version: "+Integer.toHexString(NATIVEFMODEX_JAR_VERSION));
			Tools.println("NativeFmodEx library version: "+Integer.toHexString(NATIVEFMODEX_LIBRARY_VERSION));
			Tools.println("FMOD Ex version: "+Integer.toHexString(version));
			Tools.println("FMOD Ex requiered: "+Integer.toHexString(FMOD_VERSION));

//			result = fmodExSystem.setPluginPath("lib");
//			Tools.errCheck(result);

			//Check soundcard id
			IntBuffer numDrivers = BufferUtils.newIntBuffer(1);
			result = fmodExSystem.getNumDrivers(numDrivers);
			Tools.errCheck(result);
			if(soundcard >= numDrivers.get(0)) {
				soundcard = -1;
				Tools.println("Bad souncard, using default soundcard.");
			}

			//Select soundcard
			result = fmodExSystem.setDriver(soundcard);
			Tools.errCheck(result);

			//Display soundcard name
			if(soundcard != -1) {
				ByteBuffer soundcardName = BufferUtils.newByteBuffer(256);
				fmodExSystem.getDriverInfo(soundcard, soundcardName, soundcardName.capacity(), null);
				Tools.println("Soundcard: "+BufferUtils.toString(soundcardName));
			}
			else {
				Tools.println("Using default soundcard.");
			}

			//Increase maxinputchannels to 8, and leave ase default the other
			result = fmodExSystem.setSoftwareFormat(48000,
					FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM16, 0, 8, FMOD_DSP_RESAMPLER.FMOD_DSP_RESAMPLER_LINEAR);
			Tools.errCheck(result);
			
			//Init sound system
			Tools.print("Init FMOD Ex: ");
			result = fmodExSystem.init(1, FMOD_INITFLAGS.FMOD_INIT_NORMAL, null);
			if(result != FMOD_RESULT.FMOD_OK && result != FMOD_RESULT.FMOD_ERR_INVALID_HANDLE) {
				Tools.println("FMOD Ex error! ("+result.asInt()+") "+FmodEx.FMOD_ErrorString(result));
				Tools.println(" -> Using NOSOUND output.");

				result = fmodExSystem.setOutput(FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_NOSOUND);
				Tools.errCheck(result);

				result = fmodExSystem.init(1, FMOD_INITFLAGS.FMOD_INIT_NORMAL, null);
				Tools.errCheck(result);
				if(result != FMOD_RESULT.FMOD_OK && result != FMOD_RESULT.FMOD_ERR_INVALID_HANDLE) {
					result = fmodExSystem.close();
					Tools.errCheck(result);
					result = fmodExSystem.release();
					Tools.errCheck(result);

					INIT_ERROR = "Error!  FMOD Ex init failed.";
					Tools.println(INIT_ERROR);
					return null;
				}
			}

			Tools.println("OK");
			Tools.println("");
		}
		return fmodExSystem;
	}

						/*Initialization and de-initialization*/

	/**
	 * Initialize the SoundEngine<BR>
	 * You MUST call this before any others methods of this class !!!
	 * @return true if the SoudnEngine is initialized correctly
	 */
	public boolean init(int soundcardID) {
		soundcard = soundcardID;
		return getSystem() != null;
	}

	public boolean isInitialized() {
		return fmodExSystem != null;
	}

	public String[] getSoundcardNames() {
		final System system = getSystem();
		
		FMOD_RESULT result;

		//Allocate memory
		ByteBuffer buffer = BufferUtils.newByteBuffer(256);
		IntBuffer numDrivers = BufferUtils.newIntBuffer(1);

		//Number of soundcards
		result = system.getNumDrivers(numDrivers);
		Tools.errCheck(result);

		//Retrieve name list
		String[] names = new String[numDrivers.get(0)];
		for(int i = 0; i < names.length; i++) {
			system.getDriverInfo(i, buffer, buffer.capacity(), null);
			names[i] = BufferUtils.toString(buffer);
		}
		return names;
	}

	public synchronized void update() {
		final System system = getSystem();
		system.update();
	}

	/**
	 * Code to execute at the end of a stream or a sample music
	 * @param callback_ callback method
	 */
	public void setEndCallback(FMOD_CHANNEL_CALLBACK callback) {
		endCallback = callback;
	}

	public void close() {
		if(fmodExSystem != null) {
			Tools.println("");
			Tools.println("SOUND ENGINE EX CLOSING");
			Tools.println("=======================");
			Tools.print("Closing FMOD Ex: ");
			SharedSoundEngineEx.get().close(fmodExSystem);
			fmodExSystem = null;
			Tools.println("OK");
			Tools.println("");
		}
	}

								/*Load, play, stop, free*/

	/**
	 * Load a music
	 * @return true if the music is loaded (or already loaded)
	 */
	public boolean load(Song song) {
		final System system = getSystem();
		return SharedSoundEngineEx.get().load(system, song);
	}

	/**
	 * Play a music
	 * @return true on success
	 * 		   false on failure
	 */
	public boolean play(Song song) {
		/*
		 * If the music is not loaded, load it!
		 */
		if(!song.isLoadedInMemory()) {
			if(!load(song)) {
				return false;
			}
		}

		if(isPaused(song)) {
			//unpause the pusic
			return pauseUnpause(song);
		}
		else if(!isPlaying(song)) {
			final System system = getSystem();
			
			Channel channel = new Channel();
			FMOD_RESULT result = null;

			switch(song.getType()) {
				case STREAM:
				case SOUND:
					result = system.playSound(FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE, song.getSound(), true, channel);
					break;
				case CD:
					result = system.playSound(FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE, song.getSound(), true, channel);
					break;
				case NETWORK:
//					long startTime = java.lang.System.currentTimeMillis();
//					do {
						result = system.playSound(FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE, song.getSound(), true, channel);
						Tools.errCheck(result);

//						try {
//							Thread.sleep(50);
//						} catch(InterruptedException e1){}
//
//						if(java.lang.System.currentTimeMillis() > startTime + MAX_SEARCH_TIME)
//							break;
//					} while(channel.isNull());
//
//					if(channel.isNull()) {
//						stop(song);
//						return false;
//					}

					break;
				default: Tools.println("UNKNOW SOUND TYPE"); return false;
			}
			Tools.errCheck(result);

			if(channel.isNull()) {
				Tools.printDebug("FAILED TO PLAY : "+song.getTitle());
				stop(song);
				return false;
			}

			if(endCallback != null) {
				result = channel.setCallback(endCallback);
				Tools.errCheck(result);
			}

			song.setChannel(channel);

			refreshVolume(song);	//Set the volume to the current volume
			refreshMute(song);

			// Force pos to 0 (sometimes problem with FBX, and possibly CD)
			setPosition(song, 0);
			
			result = channel.setPaused(false);
			Tools.errCheck(result);
		}
		return true;
	}

	/**
	 * Move to another position
	 * @return true on success
	 * 		   false on failure
	 */
	public int setPosition(Song song, int position) {
		return SharedSoundEngineEx.get().setPosition(song, position);
	}

	/**
	 * Pause/Unpause the music
	 * @return isPaused
	 */
	public boolean pauseUnpause(Song song) {
		Channel channel = song.getChannel();
		if(channel == null || channel.isNull()) {
			return false;
		}

		boolean pause = !isPaused(song);
		FMOD_RESULT result = channel.setPaused(pause);
		Tools.errCheck(result);

		if(result == FMOD_RESULT.FMOD_OK) {
			return pause;
		}
		else {
			return !pause;
		}
	}

	/**
	 * Stop the current music
	 * @return stopped
	 */
	public boolean stop(Song song) {
		return SharedSoundEngineEx.get().stop(song);
	}

	public int getFsbNumSubSounds(String path) {
		final System system = getSystem();
		return SharedSoundEngineEx.get().getNumSubSounds(system, path);
	}

	public int getCDNumTracks(String cdDrive) {
		final System system = getSystem();
		return SharedSoundEngineEx.get().getNumSubSounds(system, cdDrive);
	}

				/** Plugins */

	private DspPluginEmbed getNormalizeDsp() {
		if(normalizeDsp == null) {
			normalizeDsp = new DspPluginEmbed(FMOD_DSP_TYPE.FMOD_DSP_TYPE_NORMALIZE);
		}
		return normalizeDsp;
	}
	public boolean enableNormalization() {
		final System system = getSystem();
		return getNormalizeDsp().enable(system);
	}
	public boolean disableNormalization() {
		return getNormalizeDsp().disable();
	}
	
	private DspPluginEmbed getDistorsionDsp() {
		if(distorsionDsp == null) {
			distorsionDsp = new DspPluginEmbed(FMOD_DSP_TYPE.FMOD_DSP_TYPE_DISTORTION);
		}
		return distorsionDsp;
	}
	public boolean enableDistorsion() {
		final System system = getSystem();
		return getDistorsionDsp().enable(system);
	}
	public boolean disableDistorsion() {
		return getDistorsionDsp().disable();
	}
	
	private DspPluginEmbed getDelayDsp() {
		if(delayDsp == null) {
			delayDsp = new DspPluginEmbed(FMOD_DSP_TYPE.FMOD_DSP_TYPE_DELAY);
		}
		return delayDsp;
	}
	public boolean enableDelay() {
		final System system = getSystem();
		return getDelayDsp().enable(system);
	}
	public boolean disableDelay() {
		return getDelayDsp().disable();
	}
	
	private DspPluginEmbed getReverbDsp() {
		if(reverbDsp == null) {
			reverbDsp = new DspPluginEmbed(FMOD_DSP_TYPE.FMOD_DSP_TYPE_REVERB);
		}
		return reverbDsp;
	}
	public boolean enableReverb() {
		final System system = getSystem();
		return getReverbDsp().enable(system);
	}
	public boolean disableReverb() {
		return getReverbDsp().disable();
	}

						/*Music properties: volume, progression...*/

	/**
	 * Store the volume to set for the musics
	 * Call refreshVolume to set the volume to the music
	 * @param newVolume volume in percentage
	 * @see SoundEngineEx#refreshVolume(Song)
	 */
	public void setVolume(int newVolume) {
		volume = newVolume / 100.f;
	}
	public boolean setVolume(Song song, int newVolume) {
		setVolume(newVolume);
		return refreshVolume(song);
	}
	public boolean refreshVolume(Song song) {
		Channel channel = song.getChannel();
		if(channel != null && !channel.isNull()) {
			FMOD_RESULT result = channel.setVolume(volume);
			return result == FMOD_RESULT.FMOD_OK;
		}
		return false;
	}

	/**
	 * @return the length of the music in milliseconds
	 */
	public int getLength(Song song) {
		Sound sound = song.getSound();
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

	/**
	 * @return the progression of the current music in percentage
	 */
	public int getPosition(Song song) {
		Channel channel = song.getChannel();
		if(channel == null || channel.isNull()) {
			return 0;
		}

		IntBuffer position = BufferUtils.newIntBuffer(1);
		FMOD_RESULT result = channel.getPosition(position, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MODORDER);
		if(result != FMOD_RESULT.FMOD_OK) {
			result = channel.getPosition(position, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS);
			Tools.errCheck(result);
		}
		return position.get(0);
	}

	/**
	 * @return the progression of the current music in percentage
	 */
	public int getProgression(Song song) {
		int position = getPosition(song);
		int length = getLength(song);

		int progression = 0;
		if(length != 0) {
			progression = 100 * position / length;
		}
		return progression;
	}

	private ByteBuffer titleBuffer = BufferUtils.newByteBuffer(36);
	public String getTitle(Song song) {
		Sound sound = song.getSound();
		if((sound == null) || sound.isNull()) {
			return "";
		}

		FMOD_RESULT result = sound.getName(titleBuffer, titleBuffer.capacity());
		Tools.errCheck(result);

		return BufferUtils.toString(titleBuffer);
	}

	public void mute(boolean mute_) {
		mute = mute_;
	}
	public void mute(Song song, boolean mute_) {
		if(mute != mute_) {
			mute(mute_);
			refreshMute(song);
		}
	}
	public void refreshMute(Song song) {
		Channel channel = song.getChannel();
		if(channel == null || channel.isNull())
			return;

		FMOD_RESULT result = channel.setMute(mute);
		Tools.errCheck(result);
	}

	public boolean isPlaying(Song song) {
		return SharedSoundEngineEx.get().isPlaying(song);
	}

	public boolean isPaused(Song song) {
		return SharedSoundEngineEx.get().isPaused(song);
	}

	protected DspPlugin loadDstPlugin(File file) {
		final System system = getSystem();
		
		FMOD_PLUGINTYPE[] plugintype = new FMOD_PLUGINTYPE[1];
		IntBuffer handleBuff = BufferUtils.newIntBuffer(1);
		FMOD_RESULT result;

		result = system.loadPlugin(file.getPath(), handleBuff, 0);
		if(result != FMOD_RESULT.FMOD_OK) {
			Tools.printDebug("Not a plugin !");
			return null;
		}
		int handle = handleBuff.get(0);

		if(plugintype[0] != FMOD_PLUGINTYPE.FMOD_PLUGINTYPE_DSP) {
			Tools.printDebug("Not a DSP plugin !");
			system.unloadPlugin(handle);
			return null;
		}


		DspPlugin dspPlugin = new DspPlugin();
		result = system.createDSPByPlugin(handle, dspPlugin.dsp);
		if(result != FMOD_RESULT.FMOD_OK) {
			Tools.printDebug("Fail to create the DSP unit !");
			system.unloadPlugin(handle);
			return null;
		}

		//Load the DSP unit in the sound system
		result = system.addDSP(dspPlugin.dsp, null);
		if(result != FMOD_RESULT.FMOD_OK) {
			result = dspPlugin.dsp.release();

			Tools.printDebug("Fail to add the DSP unit in the DSP unit chain !");
			system.unloadPlugin(handle);
			return null;
		}
		//Activate the DSP unit
		result = dspPlugin.dsp.setActive(true);
		if(result != FMOD_RESULT.FMOD_OK) {
			result = dspPlugin.dsp.release();

			Tools.printDebug("Fail to activate DSP unit !");
			system.unloadPlugin(handle);
			return null;
		}

		/*
		 * Plugin informations
		 */
		ByteBuffer name = BufferUtils.newByteBuffer(32);
		IntBuffer width = BufferUtils.newIntBuffer(1);
		IntBuffer height = BufferUtils.newIntBuffer(1);

		dspPlugin.dsp.getInfo(name, null, null, width, height);
		dspPlugin.name = BufferUtils.toString(name);
		dspPlugin.width = width.get(0);
		dspPlugin.height = height.get(0);

		Tools.printDebug("DSP plugin loaded: "+dspPlugin.name);
		return dspPlugin;
	}
	protected void unloadDstPlugin(DspPlugin plugin) {
		FMOD_RESULT result;

		result = plugin.dsp.setActive(false);
		if(result != FMOD_RESULT.FMOD_OK)
			Tools.printDebug("Error while stopping DSP unit !");

		result = plugin.dsp.remove();
		if(result != FMOD_RESULT.FMOD_OK)
			Tools.printDebug("Error while removing DSP unit !");

		result = plugin.dsp.release();
		if(result != FMOD_RESULT.FMOD_OK)
			Tools.printDebug("Error while releasing DSP unit !");
	}
}