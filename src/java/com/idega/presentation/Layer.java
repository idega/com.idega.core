//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import java.util.*;
import java.io.*;
import com.idega.presentation.text.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Layer extends PresentationObjectContainer{

public static final String RELATIVE = "relative";
public static final String ABSOLUTE = "absolute";
public static final String DIV = "div";
public static final String SPAN = "span";
public static final String LEFT = "left";
public static final String TOP = "top";
public static final String ZINDEX = "z-index";
public static final String POSITION = "position";
public static final String _ATTRIB_NOWRAP = "nowrap";

public boolean _nowrap = false;

String align;
String onMouseOut;
String absoluteOrRelative;
String layerType;



public Layer(){
  this(DIV);
}

public Layer(String layerType) {
  super();
  this.layerType = layerType;
}


public String getAttributeString(){
  String returnString="";
  for (Enumeration e = getAttributes().keys(); e.hasMoreElements();){

          Object Attribute=e.nextElement();
          String AttributeString = (String) Attribute;
          returnString = returnString + " " + AttributeString + ": "+(String) attributes.get(Attribute);
          if ( AttributeString.equals(LEFT) || AttributeString.equals(TOP) )
            returnString = returnString + "px";
          returnString = returnString + ";";
  }

  return returnString;
}



public void setLeftPosition(String xpos){
  setAttribute(LEFT,xpos);
  if(absoluteOrRelative==null) setAttribute("position",ABSOLUTE);
}

public void setLeftPosition(int xpos){
  setLeftPosition(String.valueOf(xpos));
}

public void setNoWrap(){
  _nowrap = true;
}

public void setNoWrap(boolean value){
  _nowrap = value;
}

public void setTopPosition(String ypos){
  setAttribute(TOP,ypos);
  if(absoluteOrRelative==null) setAttribute("position",ABSOLUTE);
}

public void setTopPosition(int ypos){
  setTopPosition(String.valueOf(ypos));
}


public void setVisibility(String visibilityType){
  setAttribute("visibility",visibilityType);
}

public void setWidth(int width){
  setAttribute("width",Integer.toString(width)+"px");
}

public void setHeight(int height){
  setAttribute("height",Integer.toString(height)+"px");
}

public void setOverflow(String overflowType){
  setAttribute("overflow",overflowType);
}

public void setZIndex(int index){
  setZIndex(String.valueOf(index));
}

public void setZIndex(String index){
  setAttribute(ZINDEX,index);
}


public void setBackgroundColor(String backgroundColor){
  setAttribute("background-color",backgroundColor);
  setAttribute("layer-background-color",backgroundColor);
}

public void setLayerType(String layerType) {
  this.layerType=layerType;
}

/*
public void setBorder(int borderWidth,String borderColor){

}

public void setBorder(int borderWidth){

}


public void setBorderColor(String color){

}
*/

public void setPositionType(String absoluteOrRelative){
  this.absoluteOrRelative = absoluteOrRelative;
  setAttribute(POSITION,absoluteOrRelative);
}

public void setBackgroundImage(String url){
  setAttribute("background-image","url("+url+")");
  setAttribute("layer-background-image","url("+url+")");
}

public void setBackgroundImage(Image image){
  setBackgroundImage(image.getURL());
}

public void setOnMouseOut(String action) {
  onMouseOut=action;
}

private String getOnMouseOut() {
  if ( onMouseOut != null ) {
    return "onMouseOut=\""+onMouseOut+"\"";
  }
  return "";
}

public void print(IWContext iwc) throws Exception{
  initVariables(iwc);
  if( doPrint(iwc)){
          if (getLanguage().equals("HTML")){

            boolean alignSet = isAttributeSet(HORIZONTAL_ALIGNMENT);

            print("<"+layerType+" id=\""+getID()+"\" ");
            if(_nowrap){
             print(_ATTRIB_NOWRAP + " ");
            }
            if(alignSet){
              print("align=\""+getHorizontalAlignment()+"\" ");
              removeAttribute(HORIZONTAL_ALIGNMENT);//does this slow things down?
            }
            println("style=\""+getAttributeString()+"\" "+getOnMouseOut()+">");
            super.print(iwc);
            println("\n</"+layerType+">");

          }//end if (getLanguage(...
  }
}


}
