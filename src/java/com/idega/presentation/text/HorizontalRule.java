//idega 2001 - Þórhallur Helgason
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.presentation.text;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.idegaweb.IWMainApplication;


/**
*@author <a href="mailto:laddi@idega.is">Þórhallur Helgason</a>
*@version 1.0
*/
public class HorizontalRule extends PresentationObject {

public static final String ALIGN_LEFT = "left";
public static final String ALIGN_RIGHT = "right";
public static final String ALIGN_CENTER = "center";

public HorizontalRule(){
}

public HorizontalRule(int width) {
  this(Integer.toString(width));
}

public HorizontalRule(String width) {
  setWidth(width);
}

public HorizontalRule(int width, int height) {
  this(Integer.toString(width),height);
}

public HorizontalRule(String width, int height) {
  setWidth(width);
  setHeight(height);
}

public HorizontalRule(int width, int height, String style) {
  this(Integer.toString(width),height,style);
}

public HorizontalRule(String width, int height, String style) {
  setWidth(width);
  setHeight(height);
  setStyle(style);
}

public HorizontalRule(int width, int height, String style, boolean noShade) {
  this(Integer.toString(width),height,style,noShade);
}

public HorizontalRule(String width, int height, String style, boolean noShade) {
  setWidth(width);
  setHeight(height);
  setStyle(style);
  setNoShade(noShade);
}

  public void setTitle(String title){
    setAttribute("title",title);
  }

  public void setWidth(String width){
    setAttribute("width",width);
  }

  public void setWidth(int width){
    setAttribute("width",Integer.toString(width));
  }

  public void setHeight(int height) {
    setAttribute("size",Integer.toString(height));
  }

  public void setNoShade(boolean noShade) {
    if ( noShade )
      setAttribute("noshade");
  }

  public void setNoShade() {
    setNoShade(true);
  }

  public void setStyleClass(String style) {
    setAttribute("class",style);
  }

  public void setStyle(String style) {
    setAttribute("style",style);
  }

  public void setAlignment(String alignment) {
    setAttribute("align",alignment);
  }

  public void print(IWContext iwc)throws IOException{
    initVariables(iwc);
    if (getLanguage().equals("HTML")){
      print("<hr "+getAttributeString()+" >");
    }
  }

}

