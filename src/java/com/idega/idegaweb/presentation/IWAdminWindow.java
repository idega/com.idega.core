package com.idega.idegaweb.presentation;


import com.idega.presentation.text.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWResourceBundle;

public class IWAdminWindow extends Window {

public static final String MENU_COLOR = "#EFEFEF";
private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
public final static String STYLE = "font-family:arial; font-size:8pt; color:#000000; text-align: justify; border: 1 solid #000000;";
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
private boolean displayEmpty = false;
private String styleScript = "DefaultStyle.css";
private boolean useStyleSheetFromCoreBundle = true;


private String rightWidth = "160";
private String method = "post";
private int _cellPadding = 0;

public static String HEADER_COLOR = "#0E2456";

  public IWAdminWindow(){
    super();
  }

  public IWAdminWindow(String name){
    super(name);
  }

  public IWAdminWindow(int width, int heigth) {
    super(width,heigth);
  }

  public IWAdminWindow(String name,int width,int height){
    super(name,width,height);
  }

  public IWAdminWindow(String name,String url){
    super(name,url);
  }

  public IWAdminWindow(String name, int width, int height, String url){
    super(name,width,height,url);
  }

  public IWAdminWindow(String name,String classToInstanciate,String template){
    super(name,classToInstanciate,template);
  }

  public IWAdminWindow(String name,Class classToInstanciate,Class template){
    super(name,classToInstanciate,template);
  }

  public IWAdminWindow(String name,Class classToInstanciate){
    super(name,classToInstanciate);
  }

  public Form getUnderlyingForm(){
    return adminForm;
  }

  public void _main(IWContext iwc)throws Exception{
    iwb = getBundle(iwc);
    iwrb = getResourceBundle(iwc);
    iwbCore = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    if( !displayEmpty ){
      makeTables();
      setAllMargins(0);
      HEADER_COLOR = iwbCore.getProperty("adminHeaderColor",HEADER_COLOR);

      if ( merged ){
	super.add(adminTable);
      }
      else{
	super.add(adminForm);
      }
    }

    super._main(iwc);
    //add the stylesheet path and filename of the chosen stylesheet to the page
    this.setStyleSheetURL(getStyleSheetPath(iwc)+styleScript);
  }

  public void main(IWContext iwc)throws Exception{
  }

  private void makeTables() {
    adminForm = new Form();
      adminForm.setMethod(method);

    adminTable = new Table(2,2);
      adminTable.mergeCells(1,1,2,1);
      adminTable.setCellpadding(0);
      adminTable.setCellspacing(0);
      adminTable.setWidth("100%");
      adminTable.setHeight("100%");
      adminTable.setHeight(2,"100%");
      adminTable.setColor(1,1,HEADER_COLOR);
      adminTable.setColor(1,2,"#FFFFFF");
      if ( !merged ) {
	adminTable.setColor(2,2,MENU_COLOR);
	adminTable.setWidth(2,2,rightWidth);
      }
      else {
	adminTable.mergeCells(1,2,2,2);
      }
      adminTable.setRowVerticalAlignment(2,"top");
      adminForm.add(adminTable);

    headerTable = new Table();
    	  headerTable.setStyleClass("top");
      headerTable.setCellpadding(0);
      headerTable.setCellspacing(0);
      headerTable.setWidth("100%");
      headerTable.setAlignment(2,1,"right");
//      Image idegaweb = iwbCore.getImage("/editorwindow/idegaweb.gif","idegaWeb");
//      headerTable.add(idegaweb,1,1);
      headerTable.setStyleClass(1,1,"banner");
      adminTable.add(headerTable,1,1);

    leftTable = new Table();
      leftTable.setCellpadding(_cellPadding);
      leftTable.setWidth("100%");
      if ( merged ) {
	leftTable.setHeight("100%");
	leftTable.setCellspacing(0);
	leftTable.setVerticalAlignment(1,1,"top");
      }
      adminTable.setAlignment(1,2,"center");
      adminTable.add(leftTable,1,2);

    rightTable = new Table();
      rightTable.setCellpadding(8);
      rightTable.setWidth("100%");
      if ( !merged ) {
        adminTable.setAlignment(2,2,"center");
	adminTable.add(rightTable,2,2);
      }
  }

  public void addBottom(String text) {
    adminTable.add(text,1,2);
  }

  public void add(PresentationObject obj) {
    if( !displayEmpty ){
      if(adminTable==null){
	makeTables();
	super.add(adminTable);
      }
      leftTable.add(obj,1,1);
    }
    else super.add(obj);

  }

  public void addBottom(PresentationObject obj) {
    adminTable.add(obj,1,2);
  }

  public void addLeft(String text) {
    int rows = leftTable.getRows();
    if ( !leftTable.isEmpty(1,rows) ) {
      rows++;
    }

    leftTable.add(formatText(text),1,rows);
  }

  public void addLeft(PresentationObject obj) {
    addLeft(obj,true);
  }

  public void addLeft(PresentationObject obj,boolean useStyle) {
    int rows = leftTable.getRows();
    if ( !leftTable.isEmpty(1,rows) ) {
      rows++;
    }

    if ( useStyle ) {
      setStyle(obj);
    }

    leftTable.add(obj,1,rows);
  }

  public void addLeft(String text,PresentationObject obj,boolean hasBreak) {
    addLeft(text,obj,hasBreak,true);
  }

  public void addLeft(String text,PresentationObject obj,boolean hasBreak,boolean useStyle) {
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

  public void addRight(String text,PresentationObject obj,boolean hasBreak) {
    addRight(text,obj,hasBreak,true);
  }

  public void addRight(String text,PresentationObject obj,boolean hasBreak,boolean useStyle) {
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
      adminTitle.setFontFace(Text.FONT_FACE_ARIAL);

    super.setTitle(title);

    headerTable.add(adminTitle,2,1);
  }

  public void addTitle(String title,String style) {
    Text adminTitle = new Text(title+"&nbsp;&nbsp;");
      adminTitle.setFontStyle(style);

    super.setTitle(title);

    headerTable.add(adminTitle,2,1);
  }

  public void addHeaderObject(PresentationObject obj) {
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

  public void formatText(Text text, boolean bold){
    if ( bold )
      text.setBold();
    text.setFontColor("#000000");
    text.setFontSize(Text.FONT_SIZE_7_HTML_1);
    text.setFontFace(Text.FONT_FACE_VERDANA);
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

  public void setStyle(PresentationObject obj){
    if(obj instanceof Text){
      this.setStyle((Text)obj);
    } else {
      obj.setMarkupAttribute("style",STYLE);
    }
  }

  public void setStyle(Text obj){
    obj.setMarkupAttribute("style",STYLE_2);
  }

  public void setEmpty(){
    this.displayEmpty = true;
  }

  public void setStyle(PresentationObject obj,String style){
    obj.setMarkupAttribute("style",style);
  }

  public void setUnMerged() {
    merged = false;
    _cellPadding = 8;
  }

  public void setRightWidth(int rightWidth) {
    this.rightWidth = Integer.toString(rightWidth);
  }

  public void setRightWidth(String rightWidth) {
    this.rightWidth = rightWidth;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setCellpadding(int padding) {
    _cellPadding = padding;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
  
  /**
   * This method depends on iwbCore and iwb to be initialized
   * @return the path of the chosen stylesheet
   */
  private String getStyleSheetPath(IWContext iwc) {
  	IWProperty styleSheet = null;
	String styleSrc = null;
  	if(useStyleSheetFromCoreBundle) {
  		styleSheet = iwc.getIWMainApplication().getSystemProperties().getIWProperty(IW_BUNDLE_IDENTIFIER+".editorwindow_styleSheet_path");
  		if(styleSheet==null) {
			styleSrc = iwbCore.getVirtualPath()+"/editorwindow/";
			iwc.getIWMainApplication().getSystemProperties().getNewProperty().setProperty(IW_BUNDLE_IDENTIFIER+".editorwindow_styleSheet_path",styleSrc);
		} else {
			styleSrc = styleSheet.getValue();
		}
  	} else {
  		styleSheet = iwc.getIWMainApplication().getSystemProperties().getIWProperty(getBundleIdentifier()+".editorwindow_styleSheet_name");
  		if(styleSheet==null) {
  			styleSrc = iwb.getVirtualPath()+"/editorwindow/";
  			iwc.getIWMainApplication().getSystemProperties().getNewProperty().setProperty(getBundleIdentifier()+".editorwindow_styleSheet_name",styleSrc);
  		} else {
  			styleSrc = styleSheet.getValue();
  		}
  	}
	return styleSrc;
  }
  
  public void setStyleScript(String styleScriptName) {
  	styleScript= styleScriptName;
  }
  
  /**
   * 
   * @param value if false it uses the current bundle of the extended class.  Default value is true.
   */
  public void setToUseStyleSheetFromCoreBundle(boolean value) {
  	useStyleSheetFromCoreBundle = value;
  }

}
