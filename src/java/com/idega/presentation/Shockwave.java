package com.idega.presentation;

import java.io.*;
import java.util.*;
import java.sql.*;



public class Shockwave extends Flash{


public Shockwave(){
  this("");
}

public Shockwave(String url){
  super(url,"untitled");
}

public Shockwave(String url,String name){
  super(url,name);
}

public Shockwave(String url,String name,int width,int height){
  super(url,name,width,height);

}
/*
* The usual constructor
*/
public Shockwave(String url,int width,int height){
  super(url,width,height);
}


public void setURL(String url){
  setSrc(url);
}

public void print(IWContext iwc)throws IOException{
  initVariables(iwc);
  if( doPrint(iwc) ){
    if (getLanguage().equals("HTML")){
      print("<object  classid=\"clsid:166B1BCA-3F9C-11CF-8075-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=8,0,0,0\" "+getHeightAndWidth()+">\n"+getParams()+"\n"+"<embed pluginspage=\"http://www.macromedia.com/shockwave/download/\""+getAttributeString()+">\n</embed>\n</object>");
    }
  }
}



  public synchronized Object clone() {
    Shockwave obj = null;
    try {
      obj = (Shockwave)super.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }


}

