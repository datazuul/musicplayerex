/*===============================================================================================
 output_ogg.dll
 © Copyright 2006 Jérôme JOUVIE (Jouvieje)

 Most of this source code that does the encoding is taken from the Vorbis encoder example.
 
 This library uses OGG and Vorbis libraries which originates from http://www.xiph.org/.
 OGG/Vorbis are under the GPL and as an external FMOD plugin with full source code for the interface
 it is allowable under the LGPL to be distributed in this fashion.
===============================================================================================*/

package org.jouvieje.fmodex.plugins;

import java.nio.ByteBuffer;

import org.jouvieje.fmodex.exceptions.InitException;
import org.jouvieje.fmodex.utils.BufferUtils;
import org.jouvieje.libloader.LibLoader;

public class OutputOgg {
	public static final byte METHOD_ABR_CBR = 0;
	public static final byte METHOD_VBR = 1;
	
	public static boolean loadLibraries() throws InitException {
		return loadLibraries("libogg") && loadLibraries("libvorbis");
	}
	
	protected static boolean loadLibraries(String s) throws InitException {
		if(!LibLoader.loadLibrary(s, false)) {
			throw new InitException("no "+s+" in java.library.path or org.lwjgl.librarypath");
		}
		return true;
	}
	
	/**
	 * @param outputPath Output file to write in the output as ogg.
	 * @param method METHOD_ABR_CBR or METHOD_VBR
	 * @param bitrate 32, 40, 48, 56, 64, 80, 96, 122, 128, 160, 192, 224, 256 or 320 (in kbps)
	 * @param quality 0 .. 10 (low .. to high)
	 */
	public static ByteBuffer createSettings(String outputPath, byte method, int bitrate, int quality) {
		ByteBuffer buffer = BufferUtils.newByteBuffer(1+3*BufferUtils.SIZEOF_INT+outputPath.length()+1);
		buffer.put(method);						//CBR/ABR/VBR
		buffer.putInt(bitrate);					//Bitrate
		buffer.putInt(quality);					//Quality
		buffer.putInt(outputPath.length());		//output file path length
		buffer.put(outputPath.getBytes());		//output file path
		BufferUtils.putNullTerminal(buffer);
		buffer.rewind();
		return buffer;
	}
}
