package com.idega.idegaweb.presentation;


import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;

public class IWAdminWindow extends Window{

private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
public final static String STYLE = "font-family:arial; font-size:8pt; color:#000000; text-align: justify; border: 1 solid #000000";
public final static String STYLE_2 = "font-family:arial; font-size:8pt; color:#000000; text-align: justify;";

private IWBundle iwb;
public IWBundle iwbCore;
private IWResourceBundle iwrb;
private Form adminForm;
private Table adminTable;
private Table headerTable;
private Table leftTable;
private Table rightTable;
private boolean merged = true;

  public IWAdminWindow() {
  }

  public void _main(ModuleInfo modinfo)throws Exception{
    iwb = getBundle(modinfo);
    iwrb = getResourceBundle(modinfo);
    iwbCore = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    makeTables();
    setAllMargins(0);
    super.add(adminForm);
    super._main(modinfo);
  }

  public void main(ModuleInfo modinfo){
  }

  private void makeTables() {
    adminForm = new Form();
      adminForm.setMethod("get");

    adminTable = new Table(2,2);
      adminTable.mergeCells(1,1,2,1);
      adminTable.setCellpadding(0);
      adminTable.setCellspacing(0);
      adminTable.setWidth("100%");
      adminTable.setHeight("100%");
      adminTable.setHeight(2,"100%");
      adminTable.setColor(1,1,"#0E2456");
      adminTable.setColor(1,2,"#FFFFFF");
      if ( !merged ) {
        adminTable.setColor(2,2,"#EFEFEF");
        adminTable.setWidth(2,2,"160");
      }
      else {
        adminTable.mergeCells(1,2,2,2);
      }
      adminTable.setRowVerticalAlignment(2,"top");
      adminForm.add(adminTable);

    headerTable = new Table();
      headerTable.setCellpadding(0);
      headerTable.setCellspacing(0);
      headerTable.setWidth("100%");
      headerTable.setAlignment(2,1,"right");
      Image idegaweb = iwbCore.getImage("/editorwindow/idegaweb.gif","idegaWeb");
      headerTable.add(idegaweb,1,1);
      adminTable.add(headerTable,1,1);

    leftTable = new Table();
      leftTable.setCellpadding(8);
      leftTable.setAlignment("center");
      leftTable.setWidth("100%");
      if ( !merged ) {
        adminTable.add(leftTable,1,2);
      }

    rightTable = new Table();
      rightTable.setCellpadding(8);
      rightTable.setAlignment("center");
      rightTable.setWidth("100%");
      if ( !merged ) {
        adminTable.add(rightTable,2,2);
      }
  }

  public void addBottom(String text) {
    adminTable.add(text,1,2);
  }

  public void add(ModuleObject obj) {
    adminTable.add(obj,1,2);
  }

  public void addBottom(ModuleObject obj) {
    adminTable.add(obj,1,2);
  }

  public void addLeft(String text) {
    int rows = leftTable.getRows();
    if ( !leftTable.isEmpty(1,rows) ) {
      rows++;
    }

    leftTable.add(formatText(text),1,rows);
  }

  public void addLeft(String text,ModuleObject obj,boolean hasBreak) {
    addLeft(text,obj,hasBreak,true);
  }

  public void addLeft(String text,ModuleObject obj,boolean hasBreak,boolean useStyle) {
    int rows = leftTable.getRows();
    if ( !leftTable.isEmpty(1,rows) ) {
      rows++;
    }

    if ( useStyle ) {
      setStyle(obj);
    }

    leftTable.add(formatText(text),1,rows);
    if ( hasBreak ) {
      leftTable.add(Text.getBreak(),1,rows);
    }
    leftTable.add(obj,1,rows);
  }

  public void addLeft(String headline, String text) {
    int rows = leftTable.getRows();
    if ( !leftTable.isEmpty(1,rows) ) {
      rows++;
    }

    leftTable.add(formatHeadline(headline),1,rows);
    leftTable.add(Text.getBreak(),1,rows);
    leftTable.add(Text.getBreak(),1,rows);
    leftTable.add(formatText(text,false),1,rows);
  }

  public void addRight(String text) {
    int rows = rightTable.getRows();
    if ( !rightTable.isEmpty(1,rows) ) {
      rows++;
    }

    rightTable.add(formatText(text),1,rows);
  }

  public void addRight(String text,ModuleObject obj,boolean hasBreak) {
    addRight(text,obj,hasBreak,true);
  }

  public void addRight(String text,ModuleObject obj,boolean hasBreak,boolean useStyle) {
    int rows = rightTable.getRows();
    if ( !rightTable.isEmpty(1,rows) ) {
      rows++;
    }

    if ( useStyle ) {
      setStyle(obj);
    }

    rightTable.add(formatText(text),1,rows);
    if ( hasBreak ) {
      rightTable.add(Text.getBreak(),1,rows);
    }
    rightTable.add(obj,1,rows);
  }

  public void addSubmitButton(InterfaceObject obj) {
    int rows = rightTable.getRows();
    String height = rightTable.getHeight();

    if ( height != null ) {
      rightTable.add(obj,1,rows);
    }
    else {
      rows++;
      rightTable.setHeight("100%");
      rightTable.setHeight(1,rows,"100%");
      rightTable.setVerticalAlignment(1,rows,"bottom");
      rightTable.setAlignment(1,rows,"center");
      rightTable.add(obj,1,rows);
    }
  }

  public void addHiddenInput(HiddenInput obj) {
    adminForm.add(obj);
  }

  public void addTitle(String title) {
    Text adminTitle = new Text(title+"&nbsp;&nbsp;");
      adminTitle.setBold();
      adminTitle.setFontColor("#FFFFFF");
      adminTitle.setFontSize("3");

    super.setTitle(title);

    headerTable.add(adminTitle,2,1);
  }

  public void addHeaderObject(ModuleObject obj) {
    int rows = headerTable.getRows()+1;
    headerTable.mergeCells(1,rows,2,rows);
    headerTable.setAlignment(1,rows,"center");

    headerTable.add(obj,1,rows);
  }

  public Text formatText(String s, boolean bold){
    Text T= new Text();
    if ( s != null ) {
      T= new Text(s);
      if ( bold )
        T.setBold();
      T.setFontColor("#000000");
      T.setFontSize(Text.FONT_SIZE_7_HTML_1);
      T.setFontFace(Text.FONT_FACE_VERDANA);
    }
    return T;
  }

  public Text formatText(String s) {
    Text T = formatText(s,true);
    return T;
  }

  public Text formatHeadline(String s) {
    Text T= new Text();
    if ( s != null ) {
      T= new Text(s);
       T.setBold();
      T.setFontColor("#000000");
      T.setFontSize(Text.FONT_SIZE_10_HTML_2);
      T.setFontFace(Text.FONT_FACE_VERDANA);
    }
    return T;
  }

  public void setStyle(ModuleObject obj){
    obj.setAttribute("style",STYLE);
  }

  public void setStyle(ModuleObject obj,String style){
    obj.setAttribute("style",style);
  }

  public void setUnMerged() {
    merged = false;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

}
