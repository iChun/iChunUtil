package ichun.common.core.util;

/*
 * Taken from http://www.rgagnon.com/javadetails/java-0416.html
 * and http://www.rgagnon.com/javadetails/java-0596.html
 * Modified for use. Thanks!
 */

import ichun.common.iChunUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5Checksum {

	static final byte[] HEX_CHAR_TABLE = {
		(byte)'0', (byte)'1', (byte)'2', (byte)'3',
		(byte)'4', (byte)'5', (byte)'6', (byte)'7',
		(byte)'8', (byte)'9', (byte)'a', (byte)'b',
		(byte)'c', (byte)'d', (byte)'e', (byte)'f'
	};    

	private static byte[] createChecksum(File filename) throws
	Exception
	{
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(File file){
		try{
			byte[] b = createChecksum(file);
			String hex = getHexString(b);
			return hex;
		} catch(Exception e){
			iChunUtil.console("Failed to generate MD5 checksum for " + file.getName(), true);
			return null;
		}
	}

	private static String getHexString(byte[] raw) 
			throws UnsupportedEncodingException 
			{
		byte[] hex = new byte[2 * raw.length];
		int index = 0;

		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
			}
}
