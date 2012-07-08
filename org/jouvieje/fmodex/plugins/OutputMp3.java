
package org.jouvieje.fmodex.plugins;

import java.nio.ByteBuffer;

import org.jouvieje.fmodex.exceptions.InitException;
import org.jouvieje.fmodex.utils.BufferUtils;
import org.jouvieje.libloader.LibLoader;

public class OutputMp3 {
	public static final byte METHOD_CBR = 0;
	public static final byte METHOD_ABR = 1;
	public static final byte METHOD_VBR = 2;
	
	public static boolean loadLibraries() throws InitException {
		return loadLibraries("lame_enc");
	}
	
	protected static boolean loadLibraries(String s) throws InitException {
		if(!LibLoader.loadLibrary(s, false)) {
			throw new InitException("no "+s+" in java.library.path or org.lwjgl.librarypath");
		}
		return true;
	}
	
	/**
	 * @param outputPath Output file to write in the output as mp3.
	 * @param method METHOD_CBR, METHOD_ABR or METHOD_VBR
	 * @param bitrate 32, 40, 48, 56, 64, 80, 96, 122, 128, 160, 192, 224, 256 or 320 (in kbps)
	 * @param quality 0 .. 9 (low .. to high)
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