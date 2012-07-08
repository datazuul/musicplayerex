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

import org.jouvieje.fmodex.DSP;
import org.jouvieje.fmodex.System;
import org.jouvieje.fmodex.enumerations.FMOD_DSP_TYPE;
import org.jouvieje.fmodex.enumerations.FMOD_RESULT;

class DspPluginEmbed {
	private final FMOD_DSP_TYPE dspType;
	private DSP dsp;
	
	public DspPluginEmbed(FMOD_DSP_TYPE dstType) {
		this.dspType = dstType;
	}
	
	private DSP getDsp(System system) {
		if(dsp == null) {
			DSP dsp_ = new DSP();
			FMOD_RESULT result = system.createDSPByType(dspType, dsp_);
			if(!Tools.errCheck(result)) {
				return null;
			}
			
			if (dspType == FMOD_DSP_TYPE.FMOD_DSP_TYPE_DISTORTION) {
				dsp_.setParameter(0, 0.7f);
			}
			else if (dspType == FMOD_DSP_TYPE.FMOD_DSP_TYPE_DELAY) {
				
			}
			else if (dspType == FMOD_DSP_TYPE.FMOD_DSP_TYPE_REVERB) {
				
			}
			
			dsp = dsp_;
		}
		return dsp;
	}
	public boolean enable(System system) {
		DSP dsp = getDsp(system);
		if(dsp != null) {
			FMOD_RESULT result = system.addDSP(dsp, null);
			return Tools.errCheck(result);
		}
		return false;
	}
	public boolean disable() {
		if(dsp != null) {
			FMOD_RESULT result = dsp.remove();
			return Tools.errCheck(result);
		}
		return false;
	}
}
