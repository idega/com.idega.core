/*
 * Created on 4.8.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.faces.renderkit;

/**
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil </a>
 * @version 1.0
 */
public class CssHelper {

	public static String BACKGROUND_COLOR_STRING = "background:";
	public static String SEMICOLON = ";";

	public String getBackgroundColorAttribute(String color) {
		if (color != null) {
			return BACKGROUND_COLOR_STRING + color + SEMICOLON;
		}
		else {
			return null;
		}
	}
}