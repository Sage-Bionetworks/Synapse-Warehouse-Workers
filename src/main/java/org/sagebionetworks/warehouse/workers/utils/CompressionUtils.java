package org.sagebionetworks.warehouse.workers.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

	/**
	 * Compress a string to a gzip byte array using UTF-8 encoding
	 * 
	 * @param tobeCompressed
	 * @return
	 */
	public static byte[] compressStringUTF8(String tobeCompressed) {
		if (tobeCompressed == null) throw new IllegalArgumentException();
		byte[] tobeZipped = tobeCompressed.getBytes(StandardCharsets.UTF_8);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream zipper = new GZIPOutputStream(out);
			zipper.write(tobeZipped);
			zipper.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Decompress a zipped byte array to a string using UTF-8 encoding
	 * 
	 * @param zippedBytes
	 * @return
	 */
	public static String decompressUTF8(byte[] zippedBytes){
		if (zippedBytes == null) throw new IllegalArgumentException();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(zippedBytes);
			GZIPInputStream unZipper = new GZIPInputStream(in);
			InputStreamReader reader = new InputStreamReader(unZipper, StandardCharsets.UTF_8);
			StringWriter sw = new StringWriter();
			char[] buff = new char[1024];
			int len = 0;
			while ((len = reader.read(buff)) > 0) {
				sw.write(buff, 0, len);
			}
			unZipper.close();
			reader.close();
			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
