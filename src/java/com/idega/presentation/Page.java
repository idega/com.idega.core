/*
 * $Id: Page.java,v 1.13 2001/11/09 12:05:42 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import com.idega.presentation.text.Text;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Window;
import com.idega.servlet.IWCoreServlet;
import com.idega.util.FrameStorageInfo;
import java.util.Enumeration;
import java.util.Hashtable;
import com.idega.idegaweb.IWMainApplication;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLPage;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class Page extends PresentationObjectContainer {
  private int _ibPageID;
  private String _title;
  private Script _theAssociatedScript;
  private boolean _zeroWait = false;
  private String _redirectInfo;
  private boolean _doReload = false;
  private String _linkColor = "#000000";
  private String _visitedColor = "#000000";
  private String _hoverColor = "#000000";
  private String _textDecoration = "underline";
  private String _hoverDecoration = "underline";
  private String _pageStyleFont = Text.FONT_FACE_ARIAL;
  private String _pageStyleFontSize = Text.FONT_SIZE_10_STYLE_TAG;
  private String _pageStyleFontStyle = Text.FONT_FACE_STYLE_NORMAL;
  private String _styleSheetURL = "/style/style.css";
  private boolean _addStyleSheet = false;
  private Hashtable _frameProperties;
  private boolean _isTemplate = false;
  private boolean _isPage = true;
  private boolean _isDraft = false;
  private boolean _isExtendingTemplate = false;
  private String _templateId = null;
  private Hashtable _styleDefinitions;

  private static Page NULL_CLONE_PAGE = new Page();

  static{
    Text pageNotFound = new Text("Page not found",true,false,false);
    pageNotFound.setFontSize(4);
    NULL_CLONE_PAGE.add(pageNotFound);
  }


  protected static final String ROWS_PROPERTY = "ROWS";

  protected static final String IW_PAGE_KEY = "idegaweb_page";
  public static final String IW_FRAME_STORAGE_PARMETER = "idegaweb_frame_page";
  public static final String IW_FRAME_CLASS_PARAMETER = "idegaweb_frame_class";

  private final static String START_TAG="<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/REC-html40/loose.dtd\">\n<html>";
  private final static String END_TAG="</html>";

  /**
   *
   */
  public Page() {
    this("");
  }

  /**
   *
   */
  public Page(String s) {
    super();
    setDefaultValues();
    setTitle(s);
  }

  /**
   *
   */
  public void setBackgroundColor(String color) {
    setAttribute("bgcolor",color);
  }

  /**
   *
   */
  public void setTextColor(String color) {
    setAttribute("text",color);
  }

  /**
   *
   */
  public void setAlinkColor(String color) {
    setAttribute("alink",color);
  }

  /**
   *
   */
  public void setHoverColor(String color) {
    setAttribute("alink",color);
    _hoverColor = color;
  }

  public void setStyleDefinition(String styleName, String styleAttribute) {
    if (_styleDefinitions == null) {
      _styleDefinitions = new Hashtable();
    }
    _styleDefinitions.put(styleName,styleAttribute);
  }

  private void setDefaultValues() {
	  setStyleDefinition("A:link","color:"+_linkColor+"; font-size: "+_pageStyleFontSize+"; text-decoration:"+_textDecoration+";");
    setStyleDefinition("A:visited","color:"+_visitedColor+"; font-size: "+_pageStyleFontSize+"; text-decoration:"+_textDecoration+";");
    setStyleDefinition("A:hover","color:"+_hoverColor+"; font-size: "+_pageStyleFontSize+"; text-decoration:"+_hoverDecoration+";");
    setStyleDefinition("body","font-family: "+_pageStyleFont+"; font-size: "+_pageStyleFontSize+"; font-style: "+_pageStyleFontStyle+";");
  }

  public String getStyleDefinition() {
    StringBuffer returnString = new StringBuffer();
    String styleName ="";

    if (_styleDefinitions != null) {
      Enumeration e = _styleDefinitions.keys();
      while (e.hasMoreElements()) {
        styleName = (String)e.nextElement();
        returnString.append("\t");
        returnString.append(styleName);
        String styleAttribute=getStyleAttribute(styleName);
        if(!styleAttribute.equals(slash)){
          returnString.append(" { ");
          returnString.append(styleAttribute);
          returnString.append(" }\n");
        }
        returnString.append("");
      }
    }

    return returnString.toString();
  }

  public String getStyleAttribute(String styleName) {
    if (_styleDefinitions != null){
      return (String)_styleDefinitions.get((Object)styleName);
    }
    else {
      return null;
    }
  }

  /**
   *
   */
  public void setTextDecoration(String textDecoration) {
    _textDecoration = textDecoration;
  }

  /**
   *
   */
  public void setHoverDecoration(String hoverDecoration) {
    _hoverDecoration = hoverDecoration;
  }

  /**
   *
   */
  public void setStyleSheetURL(String styleSheetURL) {
    _addStyleSheet = true;
    _styleSheetURL = styleSheetURL;
  }

  /**
   *
   */
  public void setVlinkColor(String color) {
    setAttribute("vlink",color);
    _visitedColor = color;
  }

  /**
   *
   */
  public void setLinkColor(String color) {
    setAttribute("link",color);
    _linkColor = color;
  }

  /**
   *
   */
  public void setPageFontFace(String textFontFace) {
    _pageStyleFont = textFontFace;
  }

  /**
   *
   */
  public void setPageFontSize(String textFontSize) {
    _pageStyleFont = textFontSize;
  }

  /**
   *
   */
  public void setPageFontStyle (String textFontStyle) {
    _pageStyleFontStyle = textFontStyle;
  }

  /**
   *
   */
  public String getPageFontFace() {
    return(_pageStyleFont);
  }

  /**
   *
   */
  public String getPageFontSize() {
    return(_pageStyleFont);
  }

  /**
   *
   */
  public String getPageFontStyle () {
    return(_pageStyleFontStyle);
  }

  /**
   *
   */
  public void setTitle(String title) {
    _title = title;
    setName(title);
  }

  /**
   *
   */
  public void setMarginWidth(int width) {
    setAttribute("marginwidth",Integer.toString(width));
  }

  /**
   *
   */
  public void setMarginHeight(int height) {
    setAttribute("marginheight",Integer.toString(height));
  }

  /**
   *
   */
  public void setLeftMargin(int leftmargin) {
    setAttribute("leftmargin",Integer.toString(leftmargin));
  }

  /**
   *
   */
  public void setTopMargin(int topmargin) {
    setAttribute("topmargin",Integer.toString(topmargin));
  }

  /**
   *
   */
  public void setAllMargins(int allMargins) {
    setMarginWidth(allMargins);
    setMarginHeight(allMargins);
    setLeftMargin(allMargins);
    setTopMargin(allMargins);
  }

  /**
   *
   */
  public String getTitle() {
    return getName();
  }

  /**
   *
   */
  public void setAssociatedScript(Script myScript) {
    _theAssociatedScript = myScript;
  }

  /*
   *
   */
  private void initializeAssociatedScript() {
    if(_theAssociatedScript == null) {
      _theAssociatedScript = new Script();
    }
  }

  /**
   *
   */
  public Script getAssociatedScript() {
    initializeAssociatedScript();
    return _theAssociatedScript;
  }

  /**
   *
   */
  public void setBackgroundImage(String imageURL) {
    setAttribute("background",imageURL);
  }

  /**
   *
   */
  public void setBackgroundImage(Image backgroundImage) {
    setAttribute("background",backgroundImage.getURL());
  }

  /**
   *
   */
  public void setOnLoad(String action) {
    setAttributeMultivalued("onLoad",action);
  }

  /**
   *
   */
  public void setOnUnLoad(String action) {
    setAttributeMultivalued("onUnLoad",action);
  }

  /**
   * Sets the window to close immediately
   */
  public void close() {
    setOnLoad("window.close()");
  }

  /**
   *
   */
  public void setToGoBack() {
    setOnLoad("history.go(-1)");
  }


  /**
   * Sets the parent (caller) window to reload on Unload
   */
  public void setParentToReload() {
    setOnUnLoad("window.opener.location.reload()");
  }

  /**
   * Displaying an Alert
   * aron@idega.is
   */
  public void setToLoadAlert(String sMessage) {
    setOnLoad("alert('"+sMessage+"')");
  }

  /**
   *
   */
  public boolean doPrint(IWContext iwc) {
    boolean returnBoole;
    if (iwc.getParameter("idegaspecialrequesttype") == null) {
      returnBoole = true;
    }
    else if (iwc.getParameter("idegaspecialrequesttype").equals("page") && iwc.getParameter("idegaspecialrequestname").equals(this.getName())) {
      returnBoole = true;
    }
    else {
      returnBoole = false;
    }

    return returnBoole;
  }

  /*
   *
   */
  private void setDefaultAttributes(IWContext iwc) {
    if (!isAttributeSet("bgcolor")) {
      setBackgroundColor(iwc.getDefaultBackgroundColor());
    }
  }

  /**
   *
   */
  public void setToReload() {
    _doReload = true;
  }

  /**
   *
   */
  public void setToRedirect(String URL) {
    _zeroWait = true;
    setToRedirect(URL,0);
  }

  /**
   *
   */
  public void setToRedirect(String URL,int secondInterval) {
    _redirectInfo = "" + secondInterval + " ;URL=" + URL;
  }

  /**
   *
   */
  public String getRedirectInfo() {
    return _redirectInfo;
  }

  /**
   *
   */
  public void setToClose(int milliseconds){
    getAssociatedScript().addFunction("close_time","setTimeout(\"window.close()\","+milliseconds +")");
  }



  /*
   *
   */
  protected void prepareClone(PresentationObject newObjToCreate) {
   super.prepareClone(newObjToCreate);
   Page newPage = (Page)newObjToCreate;
   newPage._title = _title;
   Script newScript = (Script)_theAssociatedScript;
   if(newScript!=null){
    newPage._theAssociatedScript = (Script)newScript.clone();
   }
   newPage._zeroWait = _zeroWait;
   newPage._redirectInfo = _redirectInfo;
   newPage._doReload = _doReload;
   newPage._linkColor = _linkColor;
   newPage._visitedColor = _visitedColor;
   newPage._hoverColor = _hoverColor;
  }


  public synchronized Object _clone(IWContext iwc, boolean askForPermission){
    return this.clone(iwc,true);
    /*if(com.idega.core.accesscontrol.business.AccessControl.hasViewPermission(this,iwc)){
      return this.clone(iwc,askForPermission);
    } else {
      return NULL_CLONE_PAGE;
    }
*/
  }


  /**
   *
   */
  public synchronized Object clone(IWContext iwc, boolean askForPermission) {
    Page obj = null;
    try {
      obj = (Page)super.clone(iwc, askForPermission);
      if (_theAssociatedScript != null) {
        obj._theAssociatedScript = (Script)_theAssociatedScript.clone();
      }
      obj._title = _title;
      obj._zeroWait = _zeroWait;
      obj._redirectInfo = _redirectInfo;
      obj._doReload = _doReload;
      obj._linkColor = _linkColor;
      obj._visitedColor = _visitedColor;
      obj._hoverColor = _hoverColor;
      obj._textDecoration = _textDecoration;
      obj._styleSheetURL = _styleSheetURL;
      obj._addStyleSheet = _addStyleSheet;
      obj._ibPageID = _ibPageID;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }



  /*
  public synchronized Object clone() {
    Page obj = null;
    try {
      obj = (Page)super.clone();
      if (this.theAssociatedScript != null) {
        obj.theAssociatedScript = (Script)this.theAssociatedScript.clone();
      }
      obj.title = this.title;
      obj.zeroWait = this.zeroWait;
      obj.redirectInfo = this.redirectInfo;
      obj.doReload = this.doReload;
      obj.linkColor = this.linkColor;
      obj.visitedColor = this.visitedColor;
      obj.hoverColor = this.hoverColor;
      obj.textDecoration = this.textDecoration;
      obj.styleSheetURL = this.styleSheetURL;
      obj.addStyleSheet = this.addStyleSheet;

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }

  */



  /**
   *
   */
  public void main(IWContext iwc) throws Exception {
    if (_doReload) {
      if(iwc.getSession().getAttribute("idega_special_reload") != null) {
        iwc.getSession().removeAttribute("idega_special_reload");
      }
      else {
        setToRedirect(iwc.getRequestURI());
        iwc.getSession().setAttribute("idega_special_reload","true");
      }
    }
  }

  /*
   *
   */
  protected boolean isChildOfOtherPage(){
    boolean isInsideOtherPage = false;
    PresentationObject parent = getParentObject();
    if(parent!=null){
      if(parent instanceof Page){
        if(parent instanceof FrameSet){
          return false;
        }
        else{
          return true;
        }
      }
      else{
        return true;
      }
    }
    else{
      return false;
    }
  }

  /**
   *
   */
  public void print(IWContext iwc) throws Exception {
    initVariables(iwc);
    setDefaultAttributes(iwc);

    boolean isInsideOtherPage = this.isChildOfOtherPage();


    if (getLanguage().equals("HTML")) {
      if(!isInsideOtherPage){
        println(getStartTag());
        if(_zeroWait) {
          setDoPrint(false);
        }
        println("\n<head>");
        if (getAssociatedScript() != null) {
          getAssociatedScript()._print(iwc);
        }

        println(getMetaInformation(iwc));
        println("<title>"+getTitle()+"</title>");
        if (_addStyleSheet) {
          println("<link rel=\"stylesheet\" href=\""+_styleSheetURL+"\" type=\"text/css\">\n");
        }
        else {
          println("<STYLE TYPE=\"text/css\">\n" +
                  "<!--\n" +
                  getStyleDefinition() +
                  "   -->\n</STYLE>");
        }
        println("</head>\n<body  "+getAttributeString()+" >\n");
      }
      //Catch all exceptions that are thrown in print functions of objects stored inside
      try {
        super.print(iwc);
      }
      catch(Exception ex) {
        println("<h1>Villa var&eth;!</h1>");
        println("IW Error");
        println("<pre>");
        ex.printStackTrace(iwc.getWriter());
        println("</pre>");
      }

      if(!isInsideOtherPage){
        println("\n</body>");
        println(getEndTag());
      }
    }
    else if (getLanguage().equals("WML")) {
      println("<?xml version=\"1.0\"?>");
      println("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">");
      println("<wml>");
      println("<card title=\""+getTitle()+"\" id=\"card1\">");

      //Catch all exceptions that are thrown in print functions of objects stored inside
      try {
        super.print(iwc);
      }
      catch(Exception ex) {
        println("<p>Villa var&eth;!</p>");
        println("<p>IWError</p>");
        println("<p>");
        ex.printStackTrace(iwc.getWriter());
        println("</p>");
      }

      println("</card>");
      println("</wml>");
    }
  }

  /**
   *
   */
  public void setProperty(String key, String values[]) {
    if (key.equalsIgnoreCase("title")) {
      setTitle(values[0]);
    }
  }

  /**
   *
   */
  public static String getStartTag(){
    return START_TAG;
  }

  /**
   *
   */
  public static String getEndTag(){
    return END_TAG;
  }

  /**
   *
   */
  public String getMetaInformation(IWContext iwc){
    String theReturn = "\n<meta http-equiv=\"pragma\" content=\"no-cache\"/>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\"/>\n<meta name=\"generator\" content=\"idegaWeb 1.3\"/>\n<meta name=\"author\" content=\"idega.is\"/>\n<meta name=\"copyright\" content=\"idega.is\"/>\n";
    if (getRedirectInfo() != null) {
      theReturn += "<meta http-equiv=\"refresh\" content=\""+getRedirectInfo()+"\"/>";
    }
    return theReturn;
  }

  /**
   * Used to find the Page object to be printed in top of the current page
   */
  public static Page getPage(IWContext iwc){
    Page page =  (Page) IWCoreServlet.retrieveObject(IW_PAGE_KEY);
    return page;
  }

  /**
   *
   */
  public static Page loadPage(IWContext iwc){
    String classKey = iwc.getParameter(IW_FRAME_CLASS_PARAMETER);
    String frameKey = iwc.getParameter(IW_FRAME_STORAGE_PARMETER);

    if(frameKey!=null){
      Page page = getPage(getFrameStorageInfo(iwc),iwc);
      System.out.println("com.idega.presentation.Page: Trying to get page stored in session");
      return page;
    }
    else if(classKey!=null){
      try{
      Page page = (Page)Class.forName(classKey).newInstance();
      String sID = iwc.getParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID);
      try {
        if(sID != null){
          System.err.println("sID: "+sID);
          page.setICObjectInstanceID(Integer.parseInt(sID));
          //this.ib_object_instance_id = Integer.parseInt(sID);
          System.err.println("Integer.parseInt(sID): "+Integer.parseInt(sID));
          System.err.println("getICObjectInstanceID: "+page.getICObjectInstanceID());
        }
/*        else{
          System.err.println("sID == null");
        }*/
      }
      catch (NumberFormatException ex) {
        System.err.println(page+": cannot init ic_object_instance_id");
      }
      return page;
      }
      catch(Exception e){
        Page page = new Page();
        page.add("Page invalid");
        page.addBreak();
        page.add(e.getClass().getName()+"Message: "+e.getMessage());
        e.printStackTrace();
        return page;
      }
    }
    else{
      return new Page();
    }
  }

  /*
   *
   */
  private static FrameStorageInfo getFrameStorageInfo(IWContext iwc){
    String key = iwc.getParameter(IW_FRAME_STORAGE_PARMETER);
    FrameStorageInfo info =  (FrameStorageInfo)iwc.getSessionAttribute(key);
    if(info==null){
      info = FrameStorageInfo.EMPTY_FRAME;
    }
    return info;
  }

  /*
   *
   */
  private static Page getPage(FrameStorageInfo info,IWContext iwc){
    String key = info.getStorageKey();
    Page theReturn = (Page)iwc.getSessionAttributeWeak(key);
    if (theReturn ==null){
      try{
        theReturn = (Page)info.getFrameClass().newInstance();
      }
      catch(Exception ex){
        if(theReturn==null){
          theReturn = new Page("Expired");
          theReturn.add("This page has expired");
        }
        ex.printStackTrace();
      }
      storePage(theReturn,iwc);
    }
    return theReturn;
  }

  /**
   *
   */
  public static void storePage(Page page,IWContext iwc){
    String storageKey = page.getID();
    String infoKey=storageKey;
    FrameStorageInfo info = new FrameStorageInfo(storageKey,page.getClass());
    iwc.setSessionAttribute(infoKey,info);
    iwc.setSessionAttributeWeak(storageKey,page);
  }

  /**
   *
   */
  public static void setTopPage(Page page){
    IWCoreServlet.storeObject(IW_PAGE_KEY,page);
  }

  /**
   *
   */
  public static boolean isRequestingTopPage(IWContext iwc){
    return !iwc.isParameterSet(IW_FRAME_STORAGE_PARMETER);
  }


  /**
   *Sets the ID (BuilderPage ID)
   */
  public void setPageID(int id){
    this._ibPageID=id;
  }

  /**
   *Returns set the (BuilderPage) ID set to this page
   */
  public int getPageID(){
    return this._ibPageID;
  }

  /**
   * Sets this page to be a template page
   */
  public void setIsTemplate() {
    _isTemplate = true;
    _isPage = false;
    _isDraft = false;
  }

  /**
   * Sets this page to be a "normal" page
   */
  public void setIsPage() {
    _isTemplate = false;
    _isPage = true;
    _isDraft = false;
  }

  /**
   * Sets this page to be a draft
   */
  public void setIsDraft() {
    _isTemplate = false;
    _isPage = false;
    _isDraft = true;
  }

  /**
   *
   */
  public boolean getIsTemplate(){
    return(_isTemplate);
  }

  /**
   *
   */
  public boolean getIsPage(){
    return(_isPage);
  }

  /**
   *
   */
  public boolean getIsDraft(){
    return(_isDraft);
  }

  /**
   *
   */
  public void setIsExtendingTemplate() {
    _isExtendingTemplate = true;
  }

  /**
   *
   */
  public boolean getIsExtendingTemplate() {
    return(_isExtendingTemplate);
  }

  public void setWindowToOpenOnLoad(Link link,IWContext iwc){
    this.setOnLoad(link.getWindowToOpenCallingScript(iwc));
  }

  public void setTemplateId(String id) {
    _templateId = id;
  }

  public String getTemplateId() {
    return(_templateId);
  }
}
