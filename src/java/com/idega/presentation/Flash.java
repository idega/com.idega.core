package com.idega.presentation;

public class Flash extends GenericPlugin{

public Flash(){
  super();
  setClassId("D27CDB6E-AE6D-11cf-96B8-444553540000");
  //setCodeBase("http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=4,0,2,0");
  setCodeBase("http://active.macromedia.com/flash2/cabs/swflash.cab#version=4,0,0,0");
  setPluginSpace("http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash");
}

public Flash(String url){
  this(url,"untitled");
}

public Flash(String url,String name){
  this();
  //setName(name);
  setURL(url);
  setParam("quality","high");
  setHeight("100%");
  setWidth("100%");
}

public Flash(String url,String name,int width,int height){
  this();
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
  this();
  setURL(url);
  setWidth(width);
  setHeight(height);
}


public void setURL(String url){
  setParam("movie",url);
}

public void setTransparent(){
  setParamAndAttribute("wmode","transparent");
}

public void setMenuVisibility(String visible){
  setParamAndAttribute("menu",visible);
}

}

