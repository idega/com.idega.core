//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.idegaweb.IWMainApplication;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IFrame extends InterfaceObject{

public static final String ALIGN_TOP = "top";
public static final String ALIGN_MIDDLE = "middle";
public static final String ALIGN_BOTTOM = "bottom";
public static final String ALIGN_LEFT = "left";
public static final String ALIGN_RIGHT = "right";
public static final String ALIGN_CENTER = "center";
public static final String SCROLLING_YES = "yes";
public static final String SCROLLING_NO = "no";
public static final String SCROLLING_AUTO = "auto";
public static final int FRAMEBORDER_ON = 1;
public static final int FRAMEBORDER_OFF = 0;

public IFrame(){
	this("untitled");
}

public IFrame(String name){
	this(name,"");
}

public IFrame(String name,Class classToInstanciate){
	this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
}

public IFrame(String name,String classToInstanciate,String template){
	this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
}

public IFrame(String name,Class classToInstanciate,Class template){
	this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
}

public IFrame(String name,Class classToInstanciate,String template){
	this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
}

public IFrame(String name,String URL){
	super();
	setName(name);
	setSrc(URL);
}

public IFrame(String name,int width,int height){
	this(name,"");
	setWidth(width);
	setHeight(height);
}

public IFrame(String name,String URL,int width,int height){
	this(name,URL);
	setWidth(width);
	setHeight(height);
}

  public void setTitle(String title){
    setAttribute("title",title);
  }

  public void setSrc(String source){
    setAttribute("src",source);
  }

  public void setSrc(Class classToAdd){
    setSrc(IWMainApplication.getObjectInstanciatorURL(classToAdd));
  }

  public void setSrc(Class classToAdd, Class templateClass) {
    setSrc(IWMainApplication.getObjectInstanciatorURL(classToAdd,templateClass));
  }

  public void setWidth(String width){
    setAttribute("width",width);
  }

  public void setWidth(int width){
    setAttribute("width",Integer.toString(width));
  }

  public void setHeight(String height){
    setAttribute("height",height);
  }

  public void setHeight(int height){
    setAttribute("height",Integer.toString(height));
  }

  public void setStyleClass(String style) {
    setAttribute("class",style);
  }

  public void setStyle(String style) {
    setAttribute("style",style);
  }

  public void setBorder(int border) {
    setAttribute("frameborder",Integer.toString(border));
  }

  public void setMarginWidth(int width) {
    setAttribute("marginwidth",Integer.toString(width));
  }

  public void setMarginHeight(int height) {
    setAttribute("marginheight",Integer.toString(height));
  }

  public void setScrolling(String scrolling) {
    setAttribute("scrolling",scrolling);
  }

  public void setAlignment(String alignment) {
    setAttribute("align",alignment);
  }

  public void print(IWContext iwc)throws IOException{
    initVariables(iwc);
    if (getLanguage().equals("HTML")){
      print("<iframe name=\""+getName()+"\""+getAttributeString()+" >");
      print(super.getContent());
      print("</iframe>");
    }
  }

}

