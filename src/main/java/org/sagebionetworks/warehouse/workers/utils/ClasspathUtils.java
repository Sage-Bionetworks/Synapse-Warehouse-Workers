package org.sagebionetworks.warehouse.workers.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class ClasspathUtils {
	
	/**
	 * Simple utility to load a classpath file as a string.
	 * @param fileName
	 * @return
	 */
	public static String loadStringFromClassPath(String fileName){
		InputStream in = ClasspathUtils.class.getClassLoader().getResourceAsStream(fileName);
		if(in == null){
			throw new IllegalArgumentException("Cannot find: "+fileName+" on the classpath");
		}
		try {
			return IOUtils.toString(in, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
