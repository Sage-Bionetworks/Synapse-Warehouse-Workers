package org.sagebionetworks.warehouse.workers.config;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility to link property values that contain property keys to the values of the referenced key.
 * For example, given the following properties:
 * <p>
 * <code>
 * prop.zero=blank <br>
 * prop.one=foo <br>
 * prop.two=${prop.one}-${prop.three}<br>
 * prop.three=pre-${prop.zero}-post<br>
 * </code>
 * </p>
 * This utility will link and replaced each referenced key resulting in the following:
 * <p>
 * <code>
 * prop.zero=blank<br>
 * prop.one=foo<br>
 * prop.two=foo-pre-blank-post<br>
 * prop.three=pre-blank-post<br>
 * </code>
 * </p>
 * @author jhill
 *
 */
public class PropertyLinker {
	
	 private static Pattern pattern = Pattern.compile("\\$\\{[-a-zA-Z0-9._]+\\}");
	
	/**
	 * Link and replace all property values in the input that contain references to other property values in
	 * the set. 
	 * @param input
	 * @return
	 */
	public static Properties linkAndReplace(Properties input){
		Properties output = new Properties();
		for(String key: input.stringPropertyNames()){
			linkAndReplaceRecursive(input, output, key);
		}
		return output;
	}
	
	/**
	 * Recursive method to link and replace.
	 * @param input
	 * @param output
	 * @param key
	 * @return
	 */
	private static String linkAndReplaceRecursive(Properties input, Properties output, String key){
		if(output.contains(key)){
			// This key has already been processed
			return output.getProperty(key);
		}
		String startValue = input.getProperty(key);
		if(startValue == null){
			throw new IllegalArgumentException("Referenced key: '"+key+"' was not found in the input Properties");
		}
		Matcher matcher = pattern.matcher(startValue);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String newKey = startValue.substring(matcher.start() + 2, matcher.end() - 1);
			String replacement = linkAndReplaceRecursive(input, output, newKey);
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		String finalValue = sb.toString();
		output.put(key, finalValue);
		return finalValue;
	}

}
