package com.idega.presentation;

import java.io.*;

public class Applet extends PresentationObject{

private StringBuffer params= new StringBuffer();
private boolean usePlugin = false;

  public Applet(){
    setName(this.getID());
    setWidth(0);
    setHeight(0);
  }

  public Applet(String appletClass){
    setAppletClass(appletClass);
    setName(this.getID());
    setWidth(0);
    setHeight(0);
  }

  public Applet(String appletClass,String archive){
    this(appletClass);
    setName(this.getID());
    setWidth(0);
    setHeight(0);
    setCodeArchive(archive);

  }

  public Applet(String appletClass,String codeBase, String archive){
    this(appletClass);
    setName(this.getID());
    setWidth(0);
    setHeight(0);
    setCodebase(codeBase);
    setCodeArchive(archive);
  }

  public Applet(String appletClass,String archive,int width,int height){
    this(appletClass);
    setAppletName(this.getID());
    setWidth(width);
    setHeight(height);
    setCodeArchive(archive);
  }

  public Applet(String appletClass,int width,int height){
    this(appletClass);
    setAppletName(this.getID());
    setWidth(width);
    setHeight(height);
    setCodebase(".");
  }

  public Applet(String appletClass,String name,int width,int height, String codeBase){
    this(appletClass);
    setAppletName(name);
    setWidth(width);
    setHeight(height);
    setCodebase(codeBase);
  }

  public Applet(String appletClass,int width,int height, String codeBase){
    this(appletClass);
    setAppletName(this.getID());
    setWidth(width);
    setHeight(height);
    setCodebase(codeBase);
  }

  //------------>
  public void setParam(String name,String value){
    params.append("<param name=\"");
    params.append(name);
    params.append("\" value=\"");
    params.append(value);
    params.append("\" >\n");
  }

  public StringBuffer getParams(){
    return params;
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

  public void setBackgroundColor(String ColorStaticColorString){
    setParam("BGCOLOR",ColorStaticColorString);
  }

  public void setCodebase(String CODEBASE){
    setAttribute("CODEBASE",CODEBASE);
  }

  public String getCodebase(){
    return getAttribute("CODEBASE");
  }

  public void setCodeArchive(String ARCHIVE){
    setAttribute("ARCHIVE",ARCHIVE);
  }

  public String getCodeArchive(){
    return getAttribute("ARCHIVE");
  }

  public void setAppletClass(String CODE){
    setAttribute("CODE",CODE);
  }

  public String getAppletClass(){
    return getAttribute("CODE");
  }

  public void setAppletName(String NAME){
    setAttribute("NAME",NAME);
  }

  public String getAppletName(){
    return getAttribute("NAME");
  }

  public void setAlignment(String ALIGN){
    setAttribute("ALIGN",ALIGN);
  }

  public String getAlignment(){
    return getAttribute("ALIGN");
  }

  public void setHSpace(String HSPACE){
    setAttribute("HSPACE",HSPACE);
  }

  public String getHSpace(){
    return getAttribute("HSPACE");
  }

  public void setVSpace(String VSPACE){
    setAttribute("VSPACE",VSPACE);
  }

  public String getVSpace(){
    return getAttribute("VSPACE");
  }

  public void setAlt(String alt){
    setAttribute("alt",alt);
  }

  public String getAlt(){
    return getAttribute("alt");
  }

  public void print(IWContext iwc)throws IOException{
    initVariables(iwc);
    if( doPrint(iwc) ){
      if (getLanguage().equals("HTML")){
        print("<APPLET");
        print(getAttributeString());
        print(" >\n");
        print(params.toString());
        if( getAlt() != null ) print(getAlt());
        print("</APPLET>");

      }
    }
  }

}