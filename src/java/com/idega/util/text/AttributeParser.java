/*
 * Created on 21.7.2004
 */
package com.idega.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * @author laddi
 */
public class AttributeParser {

	public static Map parse(String stringToParse) {
		Map map = new HashMap();
		
		StringTokenizer tokens = new StringTokenizer(stringToParse, " ");
		while (tokens.hasMoreTokens()) {
			String attributes = tokens.nextToken();
			if (attributes.indexOf("=") != -1) {
				String[] values = attributes.split("=");
				String attribute = values[0].trim();
				String value = values[1];
				
				map.put(attribute, value);
			}
		}
		
		return map;
	}
}
