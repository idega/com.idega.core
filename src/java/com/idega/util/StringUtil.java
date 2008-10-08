package com.idega.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringUtil {

	public static List<String> getValuesFromString(String value, String separator) {
		
		if (value == null || separator == null)
			return null;

		String[] values = value.split(separator);
		
		if (values == null)
			return null;

		List<String> extractedValues = new ArrayList<String>();
		Collections.addAll(extractedValues, values);
		return extractedValues;
	}
	
	public static boolean isEmpty(String str) {
		
		return str == null || str.length() == 0;
	}
}