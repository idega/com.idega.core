package com.idega.presentation;










public class Shockwave extends Flash{





public Shockwave(){

  super();

  setClassId("166B1BCA-3F9C-11CF-8075-444553540000");

  setCodeBase("http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=8,0,0,0");

  setPluginSpace("http://www.macromedia.com/shockwave/download/");

}



public Shockwave(String url){

  super(url);

}



public Shockwave(String url,String name){

  super(url,name);

}



public Shockwave(String url,String name,int width,int height){

  super(url,name,width,height);

}



public Shockwave(String url,int width,int height){

  super(url,width,height);

}

//Alex fix
public void clearParams(){};


}



