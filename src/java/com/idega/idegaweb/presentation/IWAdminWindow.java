package com.idega.idegaweb.presentation;

import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.Image;
import com.idega.jmodule.object.textObject.*;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class IWAdminWindow extends Window {
  private Table T;
  private String sLinkAlign = "center";
  private String sName = "";
  private IWBundle iwb;
  private IWResourceBundle iwrb;
  private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";


  public IWAdminWindow() {
    this("");
  }

  public IWAdminWindow(String sName) {
    super(sName);
    this.sName = sName;
    T = new Table(2,3);
    super.add(T);
  }

  private void initMainTable(){
    T.setCellpadding(0);
    T.setCellspacing(0);

    T.mergeCells(1,2,2,2);
    T.mergeCells(1,3,2,3);
    T.setWidth("100%");
    T.setHeight("100%");
    T.setHeight(1,"25");
    T.setHeight(2,"25");
    T.setHeight(3,"100%");
    T.setHeight(3,"100%");
    Image idegaweb = iwb.getImage("/editorwindow/idegaweb.gif","idegaWeb");
    T.add(idegaweb,1,1);
    Text tEditor = new Text(sName+"&nbsp;&nbsp;");
    tEditor.setBold();
    tEditor.setFontColor("#FFFFFF");
    tEditor.setFontSize("3");
    T.add(tEditor,2,1);
    T.setRowColor(1,"#0E2456");
    T.setRowColor(2,"#0E2456");
    T.setAlignment(2,1,"right");
    T.setAlignment(1,2,sLinkAlign);
    T.setBorder(0);

  }

  public void setLinkAlign(String align){
    sLinkAlign = align;
  }

  public void setHeadLine(String headline){
    sName = headline;
  }

  private void initPage(){
    setAllMargins(0);
  }

  public void main(ModuleInfo modinfo) throws Exception{
    initAll(modinfo);
  }

  public void initAll(ModuleInfo modinfo){
    iwb = getBundle(modinfo);
    iwrb = getResourceBundle(modinfo);
    initPage();
    initMainTable();
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void add(ModuleObject objectToAdd){
    T.add(objectToAdd,1,3);
  }

  public void addToLink(ModuleObject objectToAdd){
    T.add(objectToAdd,1,2);
  }

  public void addToLeftHeader(ModuleObject objectToAdd){
    T.add(objectToAdd,1,1);
  }

  public void addToRightHeader(ModuleObject objectToAdd){
    T.add(objectToAdd,1,2);
  }
}