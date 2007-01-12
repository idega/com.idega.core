package com.idega.util.text;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author @version 1.0
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

public class TextStyler implements Serializable{

	private HashMap _styleMap;
	private String _styleString;

	private String[] _styles = StyleConstants.ALL_STYLES;

	public TextStyler() {
		setDefaultValues();
	}

	public TextStyler(String styleString) {
		this();
		this._styleString = styleString;
		parseStyleString(styleString);
	}

	public String getStyleString() {
		Iterator iter = this._styleMap.keySet().iterator();
		String attribute;
		String value;
		StringBuffer styleString = new StringBuffer();
		while (iter.hasNext()) {
			attribute = (String) iter.next();
			value = (String) this._styleMap.get(attribute);
			if (value != null) {
				styleString.append(attribute).append(StyleConstants.DELIMITER_COLON).append(value).append(StyleConstants.DELIMITER_SEMICOLON);
			}
		}
		return styleString.toString();
	}

	public void parseStyleString(String styleString) {
		if (styleString != null) {
			StringTokenizer tokens = new StringTokenizer(styleString, ";");
			int a = -1;
			String attribute;
			String value;

			while (tokens.hasMoreTokens()) {
				StringTokenizer tokens2 = new StringTokenizer(tokens.nextToken(), ":");

				a = 1;
				attribute = null;
				value = null;

				while (tokens2.hasMoreTokens()) {
					if (a == 1) {
						attribute = tokens2.nextToken();
						a++;
					}
					else if (a == 2) {
						value = tokens2.nextToken();
					}
				}
				this._styleMap.put(attribute, value);
			}
		}
	}

	private void setDefaultValues() {
		if (this._styleMap == null) {
			this._styleMap = new LinkedHashMap();
		}

		if (this._styles != null) {
			for (int a = 0; a < this._styles.length; a++) {
				this._styleMap.put(this._styles[a], null);
			}
		}
	}

	public void setStyleValue(String attribute, String value) {
		this._styleMap.put(attribute, value);
	}

	public String getStyleValue(String attribute) {
		String value = (String) this._styleMap.get(attribute);
		if (value != null) {
			return value;
		}
		return "";
	}

	public boolean isStyleSet(String attribute) {
		String value = (String) this._styleMap.get(attribute);
		if (value != null) {
			return true;
		}
		return false;
	}

	public void removeStyleValue(String attribute) {
		if (isStyleSet(attribute)) {
			this._styleMap.remove(attribute);
		}
	}
}