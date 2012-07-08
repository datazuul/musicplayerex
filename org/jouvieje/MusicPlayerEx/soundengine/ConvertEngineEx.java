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

import org.jouvieje.fmodex.Channel;
import org.jouvieje.fmodex.FmodEx;
import org.jouvieje.fmodex.Sound;
import org.jouvieje.fmodex.System;
import org.jouvieje.fmodex.callbacks.FMOD_CHANNEL_CALLBACK;
import org.jouvieje.fmodex.defines.FMOD_INITFLAGS;
import org.jouvieje.fmodex.defines.FMOD_TIMEUNIT;
import org.jouvieje.fmodex.defines.VERSIONS;
import org.jouvieje.fmodex.enumerations.FMOD_CHANNELINDEX;
import org.jouvieje.fmodex.enumerations.FMOD_CHANNEL_CALLBACKTYPE;
import org.jouvieje.fmodex.enumerations.FMOD_DSP_RESAMPLER;
import org.jouvieje.fmodex.enumerations.FMOD_OUTPUTTYPE;
import org.jouvieje.fmodex.enumerations.FMOD_RESULT;
import org.jouvieje.fmodex.enumerations.FMOD_SOUND_FORMAT;
import org.jouvieje.fmodex.enumerations.FMOD_SPEAKERMODE;
import org.jouvieje.fmodex.exceptions.InitException;
import org.jouvieje.fmodex.utils.BufferUtils;
import org.jouvieje.fmodex.utils.Pointer;
import org.jouvieje.fmodex.plugins.OutputMp3;
import org.jouvieje.fmodex.plugins.OutputOgg;
import org.jouvieje.MusicPlayerEx.Messages;
import org.jouvieje.MusicPlayerEx.MusicPlayerEx;

public class ConvertEngineEx implements SongConstants, VERSIONS {
	//to print informations on screen
	public static boolean DEBUG_MODE = true;

	private static System fmodExSystem = null;
	private static int fmodNumChannels = 2;
	private static int fmodSampleRate  = 44100;

	public final static int OUTPUT_WAV = 0;
	public final static int OUTPUT_MP3 = 1;
	public final static int OUTPUT_OGG = 2;
	public static int OUTPUT = OUTPUT_WAV;
	public static int METHOD = 0;
	public static int BITRATE = 128;
	public static int QUALITY = 5;
	private static String out = null;

	private static System getSystem() {
		String pluginPath = "lib";
		if(!new File(pluginPath).exists() && new File("bin\\lib").exists()) {
			pluginPath = "bin\\lib"; // Dev under eclipse
		}
		
		if(fmodExSystem == null) {
			fmodExSystem = new System();
			FMOD_RESULT result = FmodEx.System_Create(fmodExSystem);
			Tools.errCheck(result);

			int initFlags = FMOD_INITFLAGS.FMOD_INIT_STREAM_FROM_UPDATE;

			ByteBuffer buff = null;
			int pluginHandle = -1;
			if(OUTPUT == OUTPUT_WAV) {
				result = fmodExSystem.setOutput(FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_WAVWRITER_NRT);
				if(!Tools.errCheck(result)) {
					close();
					return null;
				}

				buff = BufferUtils.newByteBuffer(out.length()+1);
				BufferUtils.putString(buff, out);
				BufferUtils.putNullTerminal(buff);
				buff.rewind();
			}
			else if(OUTPUT == OUTPUT_MP3 && SharedSoundEngineEx.get().getPlatform() == SharedSoundEngineEx.WIN) {
				result = fmodExSystem.setPluginPath(pluginPath);
				Tools.errCheck(result);

				IntBuffer handleBuff = BufferUtils.newIntBuffer(1);
				if(SharedSoundEngineEx.get().isPlatform64Bits()) {
					result = fmodExSystem.loadPlugin("output_mp364.dll", handleBuff, 0);
				}
				else {
					result = fmodExSystem.loadPlugin("output_mp3.dll", handleBuff, 0);
				}
				if(!Tools.errCheck(result)) {
					Messages.ERROR = String.format(MusicPlayerEx.lang.getString("convert_plugin_not_loaded"), new Object[]{"output_mp3"});
					Messages.PERSISTENT_MESSAGE = MusicPlayerEx.lang.getString("use_full_release");
					close();
					return null;
				}
				pluginHandle = handleBuff.get(0);

				try {
					OutputMp3.loadLibraries();
				} catch(InitException e) {
					Messages.ERROR = e.getMessage();
					Messages.PERSISTENT_MESSAGE = MusicPlayerEx.lang.getString("use_full_release");
					result = fmodExSystem.unloadPlugin(pluginHandle);
					Tools.errCheck(result);
					close();
					return null;
				}

				result = fmodExSystem.setOutputByPlugin(pluginHandle);
				Tools.errCheck(result);

				buff = OutputMp3.createSettings(out, (byte)METHOD, BITRATE, QUALITY);
			}
			else if(OUTPUT == OUTPUT_OGG && SharedSoundEngineEx.get().getPlatform() == SharedSoundEngineEx.WIN) {
				result = fmodExSystem.setPluginPath(pluginPath);
				Tools.errCheck(result);

				IntBuffer handleBuff = BufferUtils.newIntBuffer(1);
				if(SharedSoundEngineEx.get().isPlatform64Bits()) {
					result = fmodExSystem.loadPlugin("output_ogg64.dll", handleBuff, 0);
				}
				else {
					result = fmodExSystem.loadPlugin("output_ogg.dll", handleBuff, 0);
				}
				if(!Tools.errCheck(result)) {
					Messages.ERROR = String.format(MusicPlayerEx.lang.getString("convert_plugin_not_loaded"), new Object[]{"output_ogg"});
					Messages.PERSISTENT_MESSAGE = MusicPlayerEx.lang.getString("use_full_release");
					close();
					return null;
				}
				pluginHandle = handleBuff.get(0);

				try {
					OutputOgg.loadLibraries();
				} catch(InitException e) {
					Messages.ERROR = e.getMessage();
					Messages.PERSISTENT_MESSAGE = MusicPlayerEx.lang.getString("use_full_release");
					result = fmodExSystem.unloadPlugin(pluginHandle);
					Tools.errCheck(result);
					close();
					return null;
				}

				result = fmodExSystem.setOutputByPlugin(pluginHandle);
				Tools.errCheck(result);

				buff = OutputOgg.createSettings(out, (byte)METHOD, BITRATE, QUALITY);
			}
			else {
				Messages.ERROR = "Wrong or unsupported output format.";
			}

			Tools.println("");
			Tools.println("CONVERT ENGINE INITIALIZATION");
			Tools.println("=============================");
			Tools.print("Num channels=");
			fmodNumChannels = toSupportNumChannels(fmodNumChannels);
			switch(fmodNumChannels) {
//				case 1: result = fmodExSystem.setSpeakerMode(FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_MONO);     break;
				default:
				case 2: result = fmodExSystem.setSpeakerMode(FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_STEREO);   break;
				case 4: result = fmodExSystem.setSpeakerMode(FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_QUAD);     break;
				case 5: result = fmodExSystem.setSpeakerMode(FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_SURROUND); break;
				case 7:
				case 6: result = fmodExSystem.setSpeakerMode(FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_5POINT1);  break;
				case 8: result = fmodExSystem.setSpeakerMode(FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_7POINT1);  break;
			}
			Tools.errCheck(result);
			Tools.print(fmodNumChannels+"\n");
			
			Tools.print("Frequency=");
			result = fmodExSystem.setSoftwareFormat(fmodSampleRate,
					FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM16, 0, 8, FMOD_DSP_RESAMPLER.FMOD_DSP_RESAMPLER_LINEAR);
			Tools.errCheck(result);
			Tools.print(fmodSampleRate+"\n");

			Tools.print("Init FMOD Ex...");
			result = fmodExSystem.init(12, initFlags, BufferUtils.asPointer(buff));
			if(!Tools.errCheck(result)) {
				final boolean fsbChannelFix = (result == FMOD_RESULT.FMOD_ERR_OUTPUT_NOSOFTWARE);
				if(fsbChannelFix) {
					Tools.print(" Force stereo...");
					result = fmodExSystem.setSpeakerMode(FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_STEREO);
					Tools.errCheck(result);
				}
				else {
					initFlags = FMOD_INITFLAGS.FMOD_INIT_NORMAL;
				}
				result = fmodExSystem.init(1, initFlags, BufferUtils.asPointer(buff));

				if(!Tools.errCheck(result)) {
					if(fsbChannelFix) {
						initFlags = FMOD_INITFLAGS.FMOD_INIT_NORMAL;
						result = fmodExSystem.init(1, initFlags, BufferUtils.asPointer(buff));
					}

					if(!fsbChannelFix || !Tools.errCheck(result)) {
						Messages.ERROR = FmodEx.FMOD_ErrorString(result);

						if(pluginHandle != -1) {
							fmodExSystem.unloadPlugin(pluginHandle);
						}
						close();
						return null;
					}
				}
				
				if(fsbChannelFix) {
					if(fmodNumChannels != 2) {
						String warning = String.format(MusicPlayerEx.lang.getString("convert_force_stereo"),
								new Object[]{new Integer(fmodNumChannels)});
						Tools.print(warning);
						Messages.ERROR = warning;
					}
				}
			}

			Tools.println("OK");
		}
		return fmodExSystem;
	}

	/*Initialisation and desinitialisation*/

	/**
	 * Initialize the SoundEngine<BR>
	 * You MUST call this before any others methods of this class !!!
	 * @return true if the SoudnEngine is initialized correctly
	 */
	public static boolean init(String out) {
		ConvertEngineEx.out = out;
		return getSystem() != null;
	}

	public static boolean isInitialized() {
		return fmodExSystem != null;
	}

	public static void close() {
		if(fmodExSystem != null && !fmodExSystem.isNull()) {
			Tools.println("CONVERT ENGINE CLOSING");
			Tools.println("======================");
			Tools.print("Closing FMOD Ex...");
			SharedSoundEngineEx.get().close(fmodExSystem);
			fmodExSystem = null;
			Tools.println("FINISH");
			Tools.println("");
		}
	}

	public static boolean convertFinish = false;

	public static boolean convert(Song song) {
		convertFinish = false;
		if(!load(song, true)) {
			return false;
		}
		if(!play(song)) {
			return false;
		}
		return true;
	}

	/**
	 * Load a music
	 * @return true if the music is loaded (or already loaded)
	 */
	public static boolean load(Song song, boolean prelauch) {
		if(song.isLoadedInMemory()) {
			return true;
		}
		
		final boolean loaded = SharedSoundEngineEx.get().load(getSystem(), song);
		if(loaded && prelauch) {
			FMOD_RESULT result;
			Sound sound = song.getSound();
			
			ByteBuffer buffer = BufferUtils.newByteBuffer(BufferUtils.SIZEOF_INT);
			
			//Get sound number of channels
			result = sound.getFormat(null, null, buffer.asIntBuffer(), null);
			Tools.errCheck(result);
			int numChannels = toSupportNumChannels(buffer.getInt(0));
			
			if((OUTPUT == OUTPUT_OGG) || (OUTPUT == OUTPUT_MP3)) {
				if(numChannels != 2) {
					Tools.println("Converter did not support "+numChannels+" channel. Force stereo output.");
					numChannels = 2;
				}
			}

			//Get sound frequency
			result = sound.getDefaults(buffer.asFloatBuffer(), null, null, null);
			Tools.errCheck(result);
			int frequency = (int)buffer.getFloat(0);

			if((numChannels != fmodNumChannels) || (frequency != fmodSampleRate)) {
				Tools.println("CONVERT ENGINE RE-INITIALIZATION");
				Tools.println("================================");

				fmodNumChannels = numChannels;
				fmodSampleRate  = frequency;
				stop(song);
				close();	//Close engine

				return load(song, false);
			}
		}
		return loaded;
	}

	private static int toSupportNumChannels(int numChannels) {
		return numChannels;
	}

	/**
	 * Play a music
	 * @return true on success
	 * 		   false on failure
	 */
	public static boolean play(Song song) {
		Channel channel = new Channel();
		FMOD_RESULT result = null;

		switch(song.getType()) {
			case STREAM:
			case SOUND:
				result = getSystem().playSound(FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE, song.getSound(), true, channel);
				break;
			case CD:
				result = getSystem().playSound(FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE, song.getSound(), true, channel);
				break;
			case NETWORK:
				result = getSystem().playSound(FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE, song.getSound(), true, channel);
				Tools.errCheck(result);

				break;
			default:
				Tools.println("UNKNOW SOUND TYPE");
				return false;
		}
		Tools.errCheck(result);

		if(channel.isNull()) {
			Tools.printDebug("FAILED TO PLAY : "+song.getTitle());
			stop(song);
			return false;
		}

		result = channel.setCallback(endCallback);
		Tools.errCheck(result);

		song.setChannel(channel);

		// Force pos to 0 (sometimes problem with FBX, and possibly CD)
		SharedSoundEngineEx.get().setPosition(song, 0);
		
		result = channel.setPaused(false);
		Tools.errCheck(result);

		return true;
	}

	public static boolean stop(Song song) {
		return SharedSoundEngineEx.get().stop(song);
	}

	private static FMOD_CHANNEL_CALLBACK endCallback = new FMOD_CHANNEL_CALLBACK(){
		public FMOD_RESULT FMOD_CHANNEL_CALLBACK(Channel channel, FMOD_CHANNEL_CALLBACKTYPE type, Pointer commanddata1,
				Pointer commanddata2) {
			if(type == FMOD_CHANNEL_CALLBACKTYPE.FMOD_CHANNEL_CALLBACKTYPE_END) {
				convertFinish = true;
			}
			return FMOD_RESULT.FMOD_OK;
		}
	};

	private static IntBuffer position = BufferUtils.newIntBuffer(1);
	private static IntBuffer length = BufferUtils.newIntBuffer(1);

	public static int update(Song song) {
		if(convertFinish) {
			return 100;
		}

		FMOD_RESULT result = getSystem().update();
		Tools.errCheck(result);

//		//Requiere a little pause (else crash)
//		if(OUTPUT != OUTPUT_WAV) {
//			try {
//				Thread.sleep(1);
//			} catch(Exception e) {}
//		}

		result = song.getSound().getLength(length, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS);
		Tools.errCheck(result);
		result = song.getChannel().getPosition(position, FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS);
		Tools.errCheck(result);

		return (100 * position.get(0)) / length.get(0);
	}
}