//idega 2001 - Þórhallur Helgason

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.presentation.text;



import java.io.IOException;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;





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

    setMarkupAttribute("title",title);

  }



  public void setWidth(String width){

    setMarkupAttribute("width",width);

  }



  public void setWidth(int width){

    setMarkupAttribute("width",Integer.toString(width));

  }



  public void setHeight(int height) {

    setMarkupAttribute("size",Integer.toString(height));

  }



  public void setNoShade(boolean noShade) {

    if ( noShade )

      setMarkupAttributeWithoutValue("noshade");

  }



  public void setColor(String color) {

    setMarkupAttribute("color",color);

  }



  public void setNoShade() {

    setNoShade(true);

  }



  public void setStyleClass(String style) {

    setMarkupAttribute("class",style);

  }



  public void setStyle(String style) {

    setMarkupAttribute("style",style);

  }



  public void setAlignment(String alignment) {

    setMarkupAttribute("align",alignment);

  }



  public void print(IWContext iwc)throws IOException{


    if (getMarkupLanguage().equals("HTML")){

      print("<hr "+getMarkupAttributesString()+" >");

    }

  }



}



