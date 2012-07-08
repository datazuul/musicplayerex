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

import java.nio.FloatBuffer;

import org.jouvieje.fmodex.enumerations.FMOD_DSP_FFT_WINDOW;
import org.jouvieje.fmodex.enumerations.FMOD_RESULT;
import org.jouvieje.fmodex.utils.BufferUtils;

public class Analyser {
	private static FloatBuffer spectrum = null;

	public static FloatBuffer getSpectrum(int channel) {
		if(spectrum == null) spectrum = BufferUtils.newFloatBuffer(512);

		FMOD_RESULT result = SoundEngineEx.get().getSystem().getSpectrum(spectrum, spectrum.capacity(), channel, FMOD_DSP_FFT_WINDOW.FMOD_DSP_FFT_WINDOW_TRIANGLE);
		if(result != FMOD_RESULT.FMOD_OK) {
			return null;
		}
		return spectrum;
	}

	private static FloatBuffer wave = null;

	public static FloatBuffer getWaveData(int channel, int size) {
		if(wave == null || wave.capacity() < size) {
			wave = BufferUtils.newFloatBuffer(size);
		}

		FMOD_RESULT result = SoundEngineEx.get().getSystem().getWaveData(wave, size, channel);
		if(result != FMOD_RESULT.FMOD_OK) {
			return null;
		}
		return wave;
	}
}
