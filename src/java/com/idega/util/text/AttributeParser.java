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

	public static Map<String, String> parse(String stringToParse) {
		Map<String, String> map = new HashMap<String, String>();
		
		StringTokenizer tokens = new StringTokenizer(stringToParse, " ");
		while (tokens.hasMoreTokens()) {
			String attributes = tokens.nextToken();
			if (attributes.indexOf("=") != -1) {
				String[] values = attributes.split("=");
				String attribute = values[0].trim();
				String value = values[1];
				
				map.put(attribute.toLowerCase(), TextSoap.findAndCut(value, "\""));
			}
		}
		
		return map;
	}
}
