package com.idega.presentation.util;



import com.idega.presentation.text.Text;
import com.idega.presentation.ui.InterfaceObject;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Edit {

  public static String colorLight = "#D7DADF";
  public static String colorMiddle = "#9fA9B3";
  public static String colorDark  = "#27334B";
  public static String colorWhite =  "#FFFFFF";
  public static String colorTextFont = "#000000";
  public static String colorTitleFont = "#FFFFFF";
  public static String colorHeaderFont = "#27334B";
  public static String colorIndexFont = "#000000";
  public static String colorRed = "#942829";
  public static String colorBlue = "#27324B";
  public static String colorLightBlue ="#ECEEF0";

  public static String bottomBarThickness = "8";

  public static int textFontSize = 1;
  public static int titleFontSize = 1;
  public static int headerFontSize = 1;
  public static String styleAttribute = "font-size: 8pt";

  public static Text formatText(String text){
    return getText(text,false,colorTextFont,textFontSize);
  }
  public static Text formatText(String text,String color){
    return getText(text,false,color,textFontSize);
  }
  public static Text formatText(String text,int size){
    return getText(text,true,colorTextFont,size);
  }
  public static Text formatText(int i){
    return formatText(String.valueOf(i));
  }
  public static Text titleText(String text){
    return getText(text,true,colorTitleFont,titleFontSize);
  }
  public static Text titleText(String text,int size){
    return getText(text,true,colorTitleFont,size);
  }
  public static Text titleText(int i){
    return titleText(String.valueOf(i));
  }
  public static Text headerText(String text){
    return getText(text,true,colorHeaderFont,headerFontSize);
  }
   public static Text headerText(String text,int size){
    return getText(text,true,colorHeaderFont,size);
  }
  public static Text headerText(int i){
    return headerText(String.valueOf(i));
  }
  private static Text getText(String text,boolean bold,String color,int size){
    Text T = new Text(text,bold,false,false);
    T.setFontColor(color);
    T.setFontSize(size);
    return T;
  }


  public static void setStyle(InterfaceObject O){
    O.setMarkupAttribute("style",styleAttribute);
  }

}
