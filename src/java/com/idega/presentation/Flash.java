package com.idega.presentation;

import java.io.*;
import java.util.*;
import java.sql.*;



public class Flash extends PresentationObject{

private Map params;

public Flash(){
  this("");
}

public Flash(String url){
  this(url,"untitled");
}

public Flash(String url,String name){
	super();
	//setName(name);
	setURL(url);
	setParam("quality","high");
	setHeight("100%");
	setWidth("100%");
}

public Flash(String url,String name,int width,int height){
	super();
	//setName(name);
	setURL(url);
	setWidth(width);
	setHeight(height);
	setParam("quality","high");
}
/*
* The usual constructor
*/
public Flash(String url,int width,int height){
	super();
	setURL(url);
	setWidth(width);
	setHeight(height);
}


public void setURL(String url){
	setSrc(url);
	setParam("movie",url);
}

public void setSrc(String src){
	setAttribute("src",src);
}

public void setParam(String name,String value){
  if( params == null ) params = new Hashtable();
  params.put(name,value);
}

public String getParams(){
  StringBuffer paramString = new StringBuffer();
  Iterator iter = params.keySet().iterator();
  String key;
  while( iter.hasNext() ){
    key = (String)iter.next();
    paramString.append("<param name=\"");
    paramString.append(key);
    paramString.append("\" value=\"");
    paramString.append(params.get(key));
    paramString.append("\" >\n");
  }

  return paramString.toString();
}


public void setWidth(int width){
	setWidth(Integer.toString(width));
}

public void setWidth(String width){
	setAttribute("width",width);
}

public String getWidth(){
	return getAttribute("width");
}


public void setHeight(int height){
	setHeight(Integer.toString(height));

}

public void setHeight(String height){
	setAttribute("height",height);

}

public String getHeight(){
	return getAttribute("height");

}

public String getURL(){
	return this.getAttribute("src");
}

public String getHeightAndWidth(){
	String ReturnString = " height=\""+getHeight()+"\" width=\""+getWidth()+"\" ";
	return ReturnString;
}

public void setTransparent(){

	setAttribute("wmode","transparent");
 	setParam("wmode","transparent");
}

public void setMenuVisibility(String visible){

	setAttribute("menu",visible);
 	setParam("menu",visible);
}

public void setBackgroundColor(String bgColor){
	setAttribute("BGCOLOR",bgColor);
 	setParam("BGCOLOR",bgColor);
}

public void print(IWContext iwc)throws IOException{
	initVariables(iwc);
	if( doPrint(iwc) ){
		if (getLanguage().equals("HTML")){


			//if (getInterfaceStyle().equals("something")){
			//}
			//else{
				print("<object  classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=4,0,2,0\" "+getHeightAndWidth()+">\n"+getParams()+"\n"+"<embed pluginspage=\"http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash\""+getAttributeString()+">\n</embed>\n</object>");
		}
	}
}


  public synchronized Object clone() {
    Flash obj = null;
    try {
      obj = (Flash)super.clone();
      obj.params = this.params;
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }


}

