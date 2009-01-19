//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import java.util.HashMap;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class IWConstants{

  public final static String CENTER_ALIGNMENT="center";
  public final static String RIGHT_ALIGNMENT="right";
  public final static String LEFT_ALIGNMENT="left";
  public final static String TOP_ALIGNMENT="top";
  public final static String BOTTOM_ALIGNMENT="bottom";
  public final static String MIDDLE_ALIGNMENT="middle";

  public static final String BODY_STYLE = "font-family:Arial,Helvetica,sans-serif;font-size:12px;";
  public static final String LINK_STYLE = "color:#000000;font-size:12px;text-decoration:underline;";
  public static final String LINK_HOVER_STYLE = "color:#000000;font-size:12px;text-decoration:underline;";
  public static final String FORM_STYLE = "margin-top:0px;margin-bottom:0px";
  public static final String IMAGE_STYLE = "border:0px solid #000000;";
  
  public static String HEADER_STYLE_1 = "font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #000000; font-weight: bold";
  public static final String HEADER_STYLE_1_NAME = "HeaderText1";
  public static String HEADER_STYLE_2 = "font-family: Arial, Helvetica, sans-serif; font-size: 11px; color: #000000; font-weight: bold";
  public static final String HEADER_STYLE_2_NAME = "HeaderText2";
  public static String HEADER_STYLE_3 = "font-family: Arial, Helvetica,sans-serif; font-size: 10px; color: #999999;";
  public static final String HEADER_STYLE_3_NAME = "HeaderText3";

  public static String BODY_STYLE_1 = "font-family: Arial, Helvetica,sans-serif; font-size: 11px; color: #000000; text-decoration: none;";
  public static final String BODY_STYLE_1_NAME = "BodyText1";
  public static String BODY_STYLE_2 = "";
  public static final String BODY_STYLE_2_NAME = "BodyText2";
  public static String BODY_STYLE_3 = "";
  public static final String BODY_STYLE_3_NAME = "BodyText3";

  public static String LINK_STYLE_1 = "font-family: Arial, Helvetica,sans-serif; font-size: 10px; color: #000000; text-decoration: none;";
  public static String LINK_HOVER_STYLE_1 = "text-decoration: underline;";
  public static final String LINK_STYLE_1_NAME = "Link1";
  public static String LINK_STYLE_2 = "font-family: Arial, Helvetica,sans-serif; font-size: 11px; color: #000000; text-decoration: underline; font-weight:bold;";
  public static String LINK_HOVER_STYLE_2 = "";
  public static final String LINK_STYLE_2_NAME = "Link2";
  public static String LINK_STYLE_3 = "font-family: Arial, Helvetica,sans-serif; font-size: 11px; color: #000000; text-decoration: underline;";
  public static String LINK_HOVER_STYLE_3 = "";
  public static final String LINK_STYLE_3_NAME = "Link3";
  public static final String HOVER = ":hover";
  
  public static String INPUT_STYLE = "font-size: 9px; border: 1 solid #000000;";
  public static final String INPUT_STYLE_NAME = "Input1";
  

  public final static String DEFAULT_INTERFACE_COLOR="#F2F2F2";
  public final static String DEFAULT_LIGHT_INTERFACE_COLOR="#FFFFFF";
  public final static String DEFAULT_DARK_INTERFACE_COLOR="#38485C";

  public final static String IW_CONTROLLER_FRAME_NAME="iw_controller_frame";
  public final static String IW_MAIN_FRAME_NAME="iw_main_frame";

  public final static String PARAM_NAME_OUTPUT_MARKUP_LANGUAGE = "iw_output_markup";
  public static final String MARKUP_LANGUAGE_HTML="HTML";
	public static final String MARKUP_LANGUAGE_PDF_XML="PDF_XML";
  public static final String MARKUP_LANGUAGE_WML="WML";

  public static final String BUILDER_FONT_STYLE_LARGE = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;font-weight:bold;color:#000000;";
  public static final String BUILDER_FONT_STYLE_SMALL = "font-family:Arial,Helvetica,sans-serif;font-size:7pt;color:#000000;";
  public static final String BUILDER_FONT_STYLE_TITLE = "font-family:Verdana,Arial,Helvetica,sans-serif;font-size:9pt;font-weight:bold;color:#FFFFFF;";
  public static final String BUILDER_FONT_STYLE_INTERFACE = "font-size: 8pt; border: 1 solid #000000;";
  public static final String BUILDER_FONT_STYLE_INTERFACE_SMALL = "font-family:Arial,Helvetica,sans-serif;font-size: 7pt; border: 1 solid #000000;";

  public static final String BUILDER_FONT_STYLE_LARGE_RED = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;font-weight:bold;color:#FF0000;";
  
  public static final String SERVER_URL_PROPERTY_NAME = "Server URL";
  
  public static HashMap getDefaultStyles() {
  	HashMap map = new HashMap();
  	String[] styleNames = {HEADER_STYLE_1_NAME,HEADER_STYLE_2_NAME,HEADER_STYLE_3_NAME,BODY_STYLE_1_NAME,BODY_STYLE_2_NAME,BODY_STYLE_3_NAME,LINK_STYLE_1_NAME,LINK_STYLE_1_NAME+HOVER,LINK_STYLE_2_NAME,LINK_STYLE_2_NAME+HOVER,LINK_STYLE_3_NAME,LINK_STYLE_3_NAME+HOVER,INPUT_STYLE_NAME};
  	String[] styleValues = {HEADER_STYLE_1,HEADER_STYLE_2,HEADER_STYLE_3,BODY_STYLE_1,BODY_STYLE_2,BODY_STYLE_3,LINK_STYLE_1,LINK_HOVER_STYLE_1,LINK_STYLE_2,LINK_HOVER_STYLE_2,LINK_STYLE_3,LINK_HOVER_STYLE_3,INPUT_STYLE};
  		
  	for ( int a = 0; a < styleNames.length; a++ ) {
  		map.put(styleNames[a], styleValues[a]);	
  	}
  	
  	return map;	
  }
}