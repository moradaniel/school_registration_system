package com.registration.util;

import java.util.List;


public class StringUtils {
	/**
	 * This is to avoid people creating new instances of this class.
	 * Utils should be accessed in a static way.
	 *  
	 */
	private StringUtils(){}
	
	public static String getStringsSeparatedBy(String separator, String... elements) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			result.append(elements[i]);
			if (i < elements.length - 1) {
				result.append(separator);
			}
		}
		return result.toString();
	}

	public static String getStringsSeparatedBy(String separator, List<String> elemens) {
		return getStringsSeparatedBy(separator, elemens.toArray(new String[elemens.size()]));
	}

	
}
