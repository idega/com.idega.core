package com.idega.util.text;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class TextStyler {

	public static String FONTFACE = "font-face";
	public static String FONTWEIGHT = "font-weight";
	public static String COLOR = "color";
	public static String FONTSIZE = "font-size";
	public static String TEXTDECORATION = "text-decoration";

	public static String defaultFontFace = "Arial, Helvetica, sans-serif";
	public static String defaultFontWeight = "bold";
	public static String defaultColor = "black";
	public static String defaultFontSize = "8pt";
	public static String defaultTextDecoration = "none";

	public static String getDefaultStyle(){
		return getStyle(defaultFontFace,defaultFontWeight,defaultColor,defaultFontSize,defaultTextDecoration);
	}

	public static String getStyle(String fontFace,String fontWeight,String Color,String fontSize,String TextDecoration){
		String colon = ": ";
		String semicolon = "; ";
		StringBuffer style = new StringBuffer();
		style.append(FONTFACE);
		style.append(colon);
		style.append(fontFace!=null?fontFace:defaultFontFace);
		style.append(semicolon);

		style.append(FONTWEIGHT);
		style.append(colon);
		style.append(fontWeight !=null?fontWeight:defaultFontWeight);
		style.append(semicolon);

		style.append(FONTWEIGHT);
		style.append(colon);
		style.append(Color !=null?Color:defaultColor);
		style.append(semicolon);

		style.append(FONTSIZE);
		style.append(colon);
		style.append(fontSize !=null?fontSize:defaultFontSize);
		style.append(semicolon);

		style.append(TEXTDECORATION);
		style.append(colon);
		style.append(TextDecoration !=null?TextDecoration:defaultTextDecoration);
		style.append(semicolon);

		return style.toString();
	}
}