/*

 *  $Id: Page.java,v 1.49 2002/04/06 19:07:45 tryggvil Exp $

 *

 *  Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 *  This software is the proprietary information of Idega hf.

 *  Use is subject to license terms.

 *

 */

package com.idega.presentation;



import com.idega.block.media.business.MediaBusiness;

import com.idega.idegaweb.IWConstants;

import com.idega.core.data.ICFile;

import com.idega.presentation.text.Text;

import com.idega.presentation.text.Link;

import com.idega.presentation.ui.Window;

import com.idega.servlet.IWCoreServlet;

import com.idega.util.FrameStorageInfo;

import java.util.Enumeration;

import java.util.Hashtable;

import java.util.Map;

import com.idega.idegaweb.IWMainApplication;



import com.idega.builder.business.BuilderLogic;

import com.idega.builder.business.IBXMLPage;



/**

 *@author     <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 *@created    15. mars 2002

 *@version    1.2

 */

public class Page extends PresentationObjectContainer {

  private int _ibPageID;

  private String _title;

  private Script _theAssociatedScript;

  private Script _theSourceScript;

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

  private boolean _addBody = true;

  private Hashtable _frameProperties;

  private boolean _isTemplate = false;

  private boolean _isPage = true;

  private boolean _isDraft = false;

  private boolean _isExtendingTemplate = false;

  private String _templateId = null;

  private Hashtable _styleDefinitions;

  private Hashtable _metaTags;

  private Hashtable _HTTPEquivs;



  private boolean addGlobalScript = true;

  private static String META_KEYWORDS = "keywords";

  private static String META_DESCRIPTION = "description";

  private static String META_HTTP_EQUIV_EXPIRES = "Expires";



  private static Page NULL_CLONE_PAGE = new Page();

  private static boolean NULL_CLONE_PAGE_INITIALIZED = false;



  private ICFile styleFile = null;



  static {

    Text pageNotFound = new Text("No permission", true, false, false);

    pageNotFound.setFontSize(4);

    NULL_CLONE_PAGE.add(pageNotFound);



  }



  /**

   *  Description of the Field

   */

  protected final static String ROWS_PROPERTY = "ROWS";



  /**

   *  Description of the Field

   */

  protected final static String IW_PAGE_KEY = "idegaweb_page";

  /**

   *  Description of the Field

   */

  public final static String IW_FRAME_STORAGE_PARMETER = "idegaweb_frame_page";

  /**

   *  Description of the Field

   */

  public final static String IW_FRAME_CLASS_PARAMETER = "idegaweb_frame_class";



// private final static String START_TAG="<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n<html>";



  /**

   *  By skipping the validation URL XML compliant browser still recognise

   *  attributes such as height / width *

   */

  private final static String START_TAG = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>";



  private final static String END_TAG = "</html>";



  /**

   */

  public Page() {

    this("");

  }



  /**

   *@param  s  Description of the Parameter

   */

  public Page(String s) {

    super();

    setDefaultValues();

    setTitle(s);

  }



  /**

   *@param  color  The new backgroundColor value

   */

  public void setBackgroundColor(String color) {

    setAttribute("bgcolor", color);

  }



  /**

   *@param  color  The new textColor value

   */

  public void setTextColor(String color) {

    setAttribute("text", color);

  }



  /**

   *@param  color  The new alinkColor value

   */

  public void setAlinkColor(String color) {

    setAttribute("alink", color);

  }



  /**

   *@param  color  The new hoverColor value

   */

  public void setHoverColor(String color) {

    setAttribute("alink", color);

    _hoverColor = color;

  }



  /**

   *  Sets the styleDefinition attribute of the Page object

   *

   *@param  styleName       The new styleDefinition value

   *@param  styleAttribute  The new styleDefinition value

   */

  public void setStyleDefinition(String styleName, String styleAttribute) {

    if (_styleDefinitions == null) {

      _styleDefinitions = new Hashtable();

    }

    _styleDefinitions.put(styleName, styleAttribute);

  }



  /**

   *  Sets the linkStyle attribute of the Page object

   *

   *@param  style  The new linkStyle value

   */

  public void setLinkStyle(String style) {

    setStyleDefinition("A", style);

  }



  /**

   *  Sets the linkHoverStyle attribute of the Page object

   *

   *@param  style  The new linkHoverStyle value

   */

  public void setLinkHoverStyle(String style) {

    setStyleDefinition("A:hover", style);

  }



  /**

   *  Sets the pageStyle attribute of the Page object

   *

   *@param  style  The new pageStyle value

   */

  public void setPageStyle(String style) {

    setStyleDefinition("body", style);

    setStyleDefinition("table", style);

  }



  /**

   *  Sets the metaTag attribute of the Page object

   *

   *@param  tagName   The new metaTag value

   *@param  tagValue  The new metaTag value

   */

  public void setMetaTag(String tagName, String tagValue) {

    if (_metaTags == null) {

      _metaTags = new Hashtable();

    }

    _metaTags.put(tagName, tagValue);

  }



  /**

   *  Sets the hTTPEquivTag attribute of the Page object

   *

   *@param  tagName   The new hTTPEquivTag value

   *@param  tagValue  The new hTTPEquivTag value

   */

  protected void setHTTPEquivTag(String tagName, String tagValue) {

    if (_HTTPEquivs == null) {

      _HTTPEquivs = new Hashtable();

    }

    _HTTPEquivs.put(tagName, tagValue);

  }



  /**

   *  Sets the keywordsMetaTag attribute of the Page object

   *

   *@param  wordsCommaSeparated  The new keywordsMetaTag value

   */

  public void setKeywordsMetaTag(String wordsCommaSeparated) {

    setMetaTag(META_KEYWORDS, wordsCommaSeparated);

  }



  /**

   *  Sets the descriptionMetaTag attribute of the Page object

   *

   *@param  wordsCommaSeparated  The new descriptionMetaTag value

   */

  public void setDescriptionMetaTag(String wordsCommaSeparated) {

    setMetaTag(META_DESCRIPTION, wordsCommaSeparated);

  }



  /**

   *  Sets the expiryDate attribute of the Page object

   *

   *@param  dateString  The new expiryDate value

   */

  public void setExpiryDate(String dateString) {

    this.setHTTPEquivTag(META_HTTP_EQUIV_EXPIRES, dateString);

  }



  /**

   *  Sets the defaultValues attribute of the Page object

   */

  private void setDefaultValues() {

    if (getStyleAttribute("A") == null) {

      setStyleDefinition("A",IWConstants.LINK_STYLE);

    }

    if (getStyleAttribute("A:hover") == null) {

      setStyleDefinition("A:hover",IWConstants.LINK_HOVER_STYLE);

    }

    if (getStyleAttribute("body") == null) {

      setStyleDefinition("body",IWConstants.BODY_STYLE);

    }

    if (getStyleAttribute("table") == null) {

      setStyleDefinition("table",IWConstants.BODY_STYLE);

    }

    getAssociatedScript().addFunction("windowopen", Window.windowScript());

  }



  /**

   *  Gets the styleDefinition attribute of the Page object

   *

   *@return    The styleDefinition value

   */

  public String getStyleDefinition() {

    StringBuffer returnString = new StringBuffer();

    String styleName = "";



    if (_styleDefinitions != null) {

      Enumeration e = _styleDefinitions.keys();

      while (e.hasMoreElements()) {

	styleName = (String) e.nextElement();

	returnString.append("\t");

	returnString.append(styleName);

	String styleAttribute = getStyleAttribute(styleName);

	if (!styleAttribute.equals(slash)) {

	  returnString.append(" { ");

	  returnString.append(styleAttribute);

	  returnString.append(" }\n");

	}

	returnString.append("");

      }

    }



    return returnString.toString();

  }



  /**

   *  Gets the styleAttribute attribute of the Page object

   *

   *@param  styleName  Description of the Parameter

   *@return            The styleAttribute value

   */

  public String getStyleAttribute(String styleName) {

    if (_styleDefinitions != null) {

      return (String) _styleDefinitions.get((Object) styleName);

    } else {

      return null;

    }

  }



  /**

   *  Gets the metaTags attribute of the Page object

   *

   *@return    The metaTags value

   */

  public String getMetaTags() {

    StringBuffer returnString = new StringBuffer();

    String tagName = "";



    if (_metaTags != null) {

      Enumeration e = _metaTags.keys();

      while (e.hasMoreElements()) {

	tagName = (String) e.nextElement();

	returnString.append("<meta name=\"");

	returnString.append(tagName);

	returnString.append("\" ");

	String tagValue = getMetaTag(tagName);

	if (tagValue != null) {

	  returnString.append(" content=\"");

	  returnString.append(tagValue);

	  returnString.append("\"");

	}

	returnString.append(">\n");

      }

    }



    if (this._HTTPEquivs != null) {

      Enumeration e = _HTTPEquivs.keys();

      while (e.hasMoreElements()) {

	tagName = (String) e.nextElement();

	returnString.append("<meta http-equiv=\"");

	returnString.append(tagName);

	returnString.append("\" ");

	String tagValue = getHTTPEquivTag(tagName);

	if (tagValue != null) {

	  returnString.append(" content=\"");

	  returnString.append(tagValue);

	  returnString.append("\"");

	}

	returnString.append(">\n");

      }

    }



    return returnString.toString();

  }



  /**

   *  Gets the hTTPEquivTag attribute of the Page object

   *

   *@param  tagName  Description of the Parameter

   *@return          The hTTPEquivTag value

   */

  protected String getHTTPEquivTag(String tagName) {

    if (_HTTPEquivs != null) {

      return (String) this._HTTPEquivs.get((Object) tagName);

    } else {

      return null;

    }

  }





  /**

   *  Gets the metaTag attribute of the Page object

   *

   *@param  tagName  Description of the Parameter

   *@return          The metaTag value

   */

  public String getMetaTag(String tagName) {

    if (_metaTags != null) {

      return (String) _metaTags.get((Object) tagName);

    } else {

      return null;

    }

  }



  /**

   *@param  textDecoration  The new textDecoration value

   */

  public void setTextDecoration(String textDecoration) {

    _textDecoration = textDecoration;

  }



  /**

   *@param  hoverDecoration  The new hoverDecoration value

   */

  public void setHoverDecoration(String hoverDecoration) {

    _hoverDecoration = hoverDecoration;

  }



  /**

   *@param  styleSheetURL  The new styleSheetURL value

   */

  public void setStyleSheetURL(String styleSheetURL) {

    _addStyleSheet = true;

    _styleSheetURL = styleSheetURL;

  }



  /**

   *@param  color  The new vlinkColor value

   */

  public void setVlinkColor(String color) {

    setAttribute("vlink", color);

    _visitedColor = color;

  }



  /**

   *@param  color  The new linkColor value

   */

  public void setLinkColor(String color) {

    setAttribute("link", color);

    _linkColor = color;

  }



  /**

   *@param  textFontFace  The new pageFontFace value

   */

  public void setPageFontFace(String textFontFace) {

    _pageStyleFont = textFontFace;

  }



  /**

   *@param  textFontSize  The new pageFontSize value

   */

  public void setPageFontSize(String textFontSize) {

    _pageStyleFont = textFontSize;

  }



  /**

   *@param  textFontStyle  The new pageFontStyle value

   */

  public void setPageFontStyle(String textFontStyle) {

    _pageStyleFontStyle = textFontStyle;

  }



  /**

   *@return    The pageFontFace value

   */

  public String getPageFontFace() {

    return (_pageStyleFont);

  }



  /**

   *@return    The pageFontSize value

   */

  public String getPageFontSize() {

    return (_pageStyleFont);

  }



  /**

   *@return    The pageFontStyle value

   */

  public String getPageFontStyle() {

    return (_pageStyleFontStyle);

  }



  /**

   *@param  title  The new title value

   */

  public void setTitle(String title) {

    _title = title;

    setName(title);

  }



  /**

   *@param  width  The new marginWidth value

   */

  public void setMarginWidth(int width) {

    setAttribute("marginwidth", Integer.toString(width));

  }



  /**

   *@param  height  The new marginHeight value

   */

  public void setMarginHeight(int height) {

    setAttribute("marginheight", Integer.toString(height));

  }



  /**

   *@param  leftmargin  The new leftMargin value

   */

  public void setLeftMargin(int leftmargin) {

    setAttribute("leftmargin", Integer.toString(leftmargin));

  }



  /**

   *@param  topmargin  The new topMargin value

   */

  public void setTopMargin(int topmargin) {

    setAttribute("topmargin", Integer.toString(topmargin));

  }



  /**

   *@param  allMargins  The new allMargins value

   */

  public void setAllMargins(int allMargins) {

    setMarginWidth(allMargins);

    setMarginHeight(allMargins);

    setLeftMargin(allMargins);

    setTopMargin(allMargins);

  }



  /**

   *@return    The title value

   */

  public String getTitle() {

    return getName();

  }



  /**

   *@param  myScript  The new associatedScript value

   */

  public void setAssociatedScript(Script myScript) {

    _theAssociatedScript = myScript;

  }



  /*

   *

   */

  /**

   *  Description of the Method

   */

  private void initializeAssociatedScript() {

    if (_theAssociatedScript == null) {

      _theAssociatedScript = new Script();

    }

  }



  /**

   *@return    The associatedScript value

   */

  public Script getAssociatedScript() {

    initializeAssociatedScript();

    return _theAssociatedScript;

  }



  /**

   *@param  imageURL  The new backgroundImage value

   */

  public void setBackgroundImage(String imageURL) {

    setAttribute("background", imageURL);

  }



  /**

   *@param  backgroundImage  The new backgroundImage value

   *@todo                    : this must implemented in the print method...like

   *      in the Link class IMPORTANT! for this to work you must have an

   *      application property called IW_USES_OLD_MEDIA_TABLES (set to anything)

   */

  public void setBackgroundImage(Image backgroundImage) {

    if (backgroundImage != null) {

      setBackgroundImage(getImageUrl(backgroundImage));

    }

  }



  /**

   *@param  image  Description of the Parameter

   *@return        The imageUrl value

   *@todo          : replace this with a implementation in print IMPORTANT! for

   *      this to work you must have an application property called

   *      IW_USES_OLD_MEDIA_TABLES (set to anything)

   */

  private String getImageUrl(Image image) {



    if (image.getURL() != null) {

      return image.getURL();

    } else {

      return image.getMediaURL();

    }



  }



  /**

   *@param  action  The new onLoad value

   */

  public void setOnLoad(String action) {

    setAttributeMultivalued("onLoad", action);

  }



  /**

   *@param  action  The new onBlur value

   */

  public void setOnBlur(String action) {

    setAttributeMultivalued("onBlur", action);

  }



  /**

   *@param  action  The new onUnLoad value

   */

  public void setOnUnLoad(String action) {

    setAttributeMultivalued("onUnLoad", action);

  }



  /**

   *  Sets the window to close immediately

   */

  public void close() {

    setOnLoad("window.close()");

  }



  /**

   *  Sets the window to close immediately

   */

  public void keepFocus() {

    setOnBlur("window.focus()");

  }



  /**

   */

  public void setToGoBack() {

    setOnLoad("history.go(-1)");

  }





  /**

   *  Sets the parent (caller) window to reload on Unload

   */

  public void setParentToReload() {

    setOnUnLoad("window.opener.location.reload()");

  }



  /**

   *  Displaying an Alert aron@idega.is

   *

   *@param  sMessage  The new toLoadAlert value

   */

  public void setToLoadAlert(String sMessage) {

    setOnLoad("alert('" + sMessage + "')");

  }



  /**

   *@param  iwc  Description of the Parameter

   *@return      Description of the Return Value

   */

  public boolean doPrint(IWContext iwc) {

    boolean returnBoole;

    if (iwc.getParameter("idegaspecialrequesttype") == null) {

      returnBoole = true;

    } else if (iwc.getParameter("idegaspecialrequesttype").equals("page") && iwc.getParameter("idegaspecialrequestname").equals(this.getName())) {

      returnBoole = true;

    } else {

      returnBoole = false;

    }



    return returnBoole;

  }



  /*

   *

   */

  /**

   *  Sets the defaultAttributes attribute of the Page object

   *

   *@param  iwc  The new defaultAttributes value

   */

  private void setDefaultAttributes(IWContext iwc) {

    if (!isAttributeSet("bgcolor")) {

      setBackgroundColor(iwc.getDefaultBackgroundColor());

    }

  }



  /**

   */

  public void setToReload() {

    _doReload = true;

  }



  /**

   *  Sets the addBody attribute of the Page object

   *

   *@param  addBodyTag  The new addBody value

   */

  public void setAddBody(boolean addBodyTag) {

    _addBody = addBodyTag;

  }



  /**

   *@param  URL  The new toRedirect value

   */

  public void setToRedirect(String URL) {

    _zeroWait = true;

    setToRedirect(URL, 0);

  }



  /**

   *@param  URL             The new toRedirect value

   *@param  secondInterval  The new toRedirect value

   */

  public void setToRedirect(String URL, int secondInterval) {

    _redirectInfo = "" + secondInterval + " ;URL=" + URL;

  }



  /**

   *@return    The redirectInfo value

   */

  public String getRedirectInfo() {

    return _redirectInfo;

  }



  /**

   *@param  milliseconds  The new toClose value

   */

  public void setToClose(int milliseconds) {

    getAssociatedScript().addFunction("close_time", "setTimeout(\"window.close()\"," + milliseconds + ")");

  }





  /*

   *

   */

  /**

   *  Description of the Method

   *

   *@param  newObjToCreate  Description of the Parameter

   */

  protected void prepareClone(PresentationObject newObjToCreate) {

    super.prepareClone(newObjToCreate);

    Page newPage = (Page) newObjToCreate;

    newPage._title = _title;

    Script newScript = (Script) _theAssociatedScript;

    if (newScript != null) {

      newPage._theAssociatedScript = (Script) newScript.clone();

    }

    newPage._zeroWait = _zeroWait;

    newPage._redirectInfo = _redirectInfo;

    newPage._doReload = _doReload;

    newPage._linkColor = _linkColor;

    newPage._visitedColor = _visitedColor;

    newPage._hoverColor = _hoverColor;

  }





  /**

   *  Description of the Method

   *

   *@param  iwc               Description of the Parameter

   *@param  askForPermission  Description of the Parameter

   *@return                   Description of the Return Value

   */

  public Object _clone(IWContext iwc, boolean askForPermission) {

    //return this.clone(iwc,true);

    if (iwc.hasViewPermission(this)) {

      return this.clone(iwc, askForPermission);

    } else {

      if (!NULL_CLONE_PAGE_INITIALIZED) {



	int pageId = 1;

	String page = null;

	// getProperty  //iwc.getParameter(_PRM_PAGE_ID);

	if (page != null) {

	  try {

	    pageId = Integer.parseInt(page);

	  } catch (NumberFormatException ex) {

	    pageId = BuilderLogic.getStartPageId(iwc);

	  }



	} else {

	  pageId = BuilderLogic.getStartPageId(iwc);

	}

	NULL_CLONE_PAGE.setOnLoad("document.location='" + BuilderLogic.getInstance().getIBPageURL(iwc, pageId) + "'");

      }

      return NULL_CLONE_PAGE;

    }



  }





  /**

   *@param  iwc               Description of the Parameter

   *@param  askForPermission  Description of the Parameter

   *@return                   Description of the Return Value

   */

  public Object clone(IWContext iwc, boolean askForPermission) {

    Page obj = null;

    try {

      obj = (Page) super.clone(iwc, askForPermission);

      if (_theAssociatedScript != null) {

	obj._theAssociatedScript = (Script) _theAssociatedScript.clone();

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

      obj.styleFile = styleFile;



    } catch (Exception ex) {

      ex.printStackTrace(System.err);

    }



    return obj;

  }





  /*

   *  public synchronized Object clone() {

   *  Page obj = null;

   *  try {

   *  obj = (Page)super.clone();

   *  if (this.theAssociatedScript != null) {

   *  obj.theAssociatedScript = (Script)this.theAssociatedScript.clone();

   *  }

   *  obj.title = this.title;

   *  obj.zeroWait = this.zeroWait;

   *  obj.redirectInfo = this.redirectInfo;

   *  obj.doReload = this.doReload;

   *  obj.linkColor = this.linkColor;

   *  obj.visitedColor = this.visitedColor;

   *  obj.hoverColor = this.hoverColor;

   *  obj.textDecoration = this.textDecoration;

   *  obj.styleSheetURL = this.styleSheetURL;

   *  obj.addStyleSheet = this.addStyleSheet;

   *  }

   *  catch(Exception ex) {

   *  ex.printStackTrace(System.err);

   *  }

   *  return obj;

   *  }

   */



  /**

   *@param  iwc            Description of the Parameter

   *@exception  Exception  Description of the Exception

   */

  public void main(IWContext iwc) throws Exception {

    if (_doReload) {

      if (iwc.getSession().getAttribute("idega_special_reload") != null) {

	iwc.getSession().removeAttribute("idega_special_reload");

      } else {

	setToRedirect(iwc.getRequestURI());

	iwc.getSession().setAttribute("idega_special_reload", "true");

      }

    }



    /* get the files cached url */

    if( styleFile!=null ){

      setStyleSheetURL(MediaBusiness.getMediaURL(styleFile.getID(),iwc.getApplication()));

    }



  }



  /*

   *

   */

  /**

   *  Gets the childOfOtherPage attribute of the Page object

   *

   *@return    The childOfOtherPage value

   */

  protected boolean isChildOfOtherPage() {

    boolean isInsideOtherPage = false;

    PresentationObject parent = getParentObject();

    if (parent != null) {

      if (parent instanceof Page) {

	if (parent instanceof FrameSet) {

	  return false;

	} else {

	  return true;

	}

      } else {

	return true;

      }

    } else {

      return false;

    }

  }



  /**

   *@param  iwc            Description of the Parameter

   *@exception  Exception  Description of the Exception

   */

  public void print(IWContext iwc) throws Exception {

    initVariables(iwc);

    setDefaultAttributes(iwc);



    boolean isInsideOtherPage = this.isChildOfOtherPage();



    if (getLanguage().equals("HTML")) {

      if (!isInsideOtherPage) {

	println(getStartTag());

	if (_zeroWait) {

	  setDoPrint(false);

	}

	println("\n<head>");

	if (addGlobalScript) {

	  //Print a reference to the global .js script file

	  println("<script type=\"text/javascript\" language=\"javascript\" src=\"" + iwc.getApplication().getCoreBundle().getResourcesURL() + "/iw_core.js\">");

	  println("</script>");

	}

	if (getAssociatedScript() != null) {

	  getAssociatedScript()._print(iwc);

	}



	println(getMetaInformation(iwc));

	println(getMetaTags());

	println("<title>" + getTitle() + "</title>");

	if (_addStyleSheet) {

	  println("<link rel=\"stylesheet\" href=\"" + _styleSheetURL + "\" type=\"text/css\">\n");

	} else {

	  println("<style type=\"text/css\">\n" +

	      "<!--\n" +

	      getStyleDefinition() +

	      "   -->\n</style>");

	}

	println("</head>\n");

	if (_addBody) {

	  println("<body  " + getAttributeString() + " >\n");

	}

	//added by Eiki for frameSet in a page support



      }

      //Catch all exceptions that are thrown in print functions of objects stored inside

      try {

	super.print(iwc);

      } catch (Exception ex) {

	println("<h1>Villa var&eth;!</h1>");

	println("IW Error");

	println("<pre>");

	ex.printStackTrace(iwc.getWriter());

	println("</pre>");

      }



      if (!isInsideOtherPage) {

	if (_addBody) {

	  println("\n</body>");

	}

	println(getEndTag());

      }

    } else if (getLanguage().equals("WML")) {

      println("<?xml version=\"1.0\"?>");

      println("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">");

      println("<wml>");

      println("<card title=\"" + getTitle() + "\" id=\"card1\">");



      //Catch all exceptions that are thrown in print functions of objects stored inside

      try {

	super.print(iwc);

      } catch (Exception ex) {

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

   *@param  key     The new property value

   *@param  values  The new property value

   */

  public void setProperty(String key, String values[]) {

    if (key.equalsIgnoreCase("title")) {

      setTitle(values[0]);

    }

  }



  /**

   *@return    The startTag value

   */

  public static String getStartTag() {

    return START_TAG;

  }



  /**

   *@return    The endTag value

   */

  public static String getEndTag() {

    return END_TAG;

  }



  /**

   *@param  iwc  Description of the Parameter

   *@return      The metaInformation value

   */

  public String getMetaInformation(IWContext iwc) {



    boolean addIdegaAuthorAndCopyRight = false;



    String theReturn = "\n<meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\"/>\n<meta name=\"generator\" content=\"idegaWeb 1.3\"/>\n";



    //If the user is logged on then there is no caching by proxy servers

    boolean notUseProxyCaching = true;



    if (notUseProxyCaching) {

      theReturn += "\n<meta http-equiv=\"pragma\" content=\"no-cache\"/>";

    }

    if (getRedirectInfo() != null) {

      theReturn += "<meta http-equiv=\"refresh\" content=\"" + getRedirectInfo() + "\"/>";

    }



    if (addIdegaAuthorAndCopyRight) {

      theReturn += "<meta name=\"author\" content=\"idega.is\"/>\n<meta name=\"copyright\" content=\"idega.is\"/>\n";

    }

    return theReturn;

  }



  /**

   *  Used to find the Page object to be printed in top of the current page

   *

   *@param  iwc  Description of the Parameter

   *@return      The page value

   */

  public static Page getPage(IWContext iwc) {

    Page page = (Page) IWCoreServlet.retrieveObject(IW_PAGE_KEY);

    return page;

  }



  /**

   *@param  iwc            Description of the Parameter

   *@return                Description of the Return Value

   *@exception  Exception  Description of the Exception

   */

  public static Page loadPage(IWContext iwc) throws Exception {

    String classKey = iwc.getParameter(IW_FRAME_CLASS_PARAMETER);

    String frameKey = iwc.getParameter(IW_FRAME_STORAGE_PARMETER);



    if (frameKey != null) {

      Page page = getPage(getFrameStorageInfo(iwc), iwc);

      System.out.println("com.idega.presentation.Page: Trying to get page stored in session");

      return page;

    } else if (classKey != null) {

      //try{

      String className = IWMainApplication.decryptClassName(classKey);

      Page page = null;

      try {

	page = (Page) Class.forName(className).newInstance();

      } catch (ClassNotFoundException e) {

	throw new IWPageInitializationException("There was an error, your session is probably expired");

      }

      String sID = iwc.getParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID);

      try {

	if (sID != null) {

	  System.err.println("sID: " + sID);

	  page.setICObjectInstanceID(Integer.parseInt(sID));

	  //this.ib_object_instance_id = Integer.parseInt(sID);

	  System.err.println("Integer.parseInt(sID): " + Integer.parseInt(sID));

	  System.err.println("getICObjectInstanceID: " + page.getICObjectInstanceID());

	}

	/*

	 *  else{

	 *  System.err.println("sID == null");

	 *  }

	 */

      } catch (NumberFormatException ex) {

	System.err.println(page + ": cannot init ic_object_instance_id");

      }

      return page;

      //}

      //catch(Exception e){

      //  Page page = new Page();

      //  page.add("Page invalid");

      //  page.addBreak();

      //  page.add(e.getClass().getName()+"Message: "+e.getMessage());

      //  e.printStackTrace();

      //  return page;

      //}

    } else {

      return new Page();

    }

  }



  /*

   *

   */

  /**

   *  Gets the frameStorageInfo attribute of the Page class

   *

   *@param  iwc  Description of the Parameter

   *@return      The frameStorageInfo value

   */

  private static FrameStorageInfo getFrameStorageInfo(IWContext iwc) {

    String key = iwc.getParameter(IW_FRAME_STORAGE_PARMETER);

    FrameStorageInfo info = (FrameStorageInfo) iwc.getSessionAttribute(key);

    if (info == null) {

      info = FrameStorageInfo.EMPTY_FRAME;

    }

    return info;

  }



  /*

   *

   */

  /**

   *  Gets the page attribute of the Page class

   *

   *@param  info  Description of the Parameter

   *@param  iwc   Description of the Parameter

   *@return       The page value

   */

  private static Page getPage(FrameStorageInfo info, IWContext iwc) {

    String key = info.getStorageKey();

    Page theReturn = (Page) iwc.getSessionAttributeWeak(key);

    if (theReturn == null) {

      try {

	theReturn = (Page) info.getFrameClass().newInstance();

      } catch (Exception ex) {

	if (theReturn == null) {

	  theReturn = new Page("Expired");

	  theReturn.add("This page has expired");

	}

	ex.printStackTrace();

      }

      storePage(theReturn, iwc);

    }

    return theReturn;

  }



  /**

   *@param  page  Description of the Parameter

   *@param  iwc   Description of the Parameter

   */

  public static void storePage(Page page, IWContext iwc) {

    String storageKey = page.getID();

    String infoKey = storageKey;

    FrameStorageInfo info = new FrameStorageInfo(storageKey, page.getClass());

    iwc.setSessionAttribute(infoKey, info);

    iwc.setSessionAttributeWeak(storageKey, page);

  }



  /**

   *@param  page  The new topPage value

   */

  public static void setTopPage(Page page) {

    IWCoreServlet.storeObject(IW_PAGE_KEY, page);

  }



  /**

   *@param  iwc  Description of the Parameter

   *@return      The requestingTopPage value

   */

  public static boolean isRequestingTopPage(IWContext iwc) {

    return !iwc.isParameterSet(IW_FRAME_STORAGE_PARMETER);

  }





  /**

   *  Sets the ID (BuilderPage ID)

   *

   *@param  id  The new pageID value

   */

  public void setPageID(int id) {

    this._ibPageID = id;

  }



  /**

   *  method for adding a style sheet file

   *  the url generating is done in the main method

   *@param  file  The new styleSheet value

   */

  public void setStyleSheet(ICFile file) {

    this.styleFile = file;

  }



  /**

   *  Returns set the (BuilderPage) ID set to this page

   *

   *@return    The pageID value

   */

  public int getPageID() {

    return this._ibPageID;

  }



  /**

   *  Sets this page to be a template page

   */

  public void setIsTemplate() {

    _isTemplate = true;

    _isPage = false;

    _isDraft = false;

  }



  /**

   *  Sets this page to be a "normal" page

   */

  public void setIsPage() {

    _isTemplate = false;

    _isPage = true;

    _isDraft = false;

  }



  /**

   *  Sets this page to be a draft

   */

  public void setIsDraft() {

    _isTemplate = false;

    _isPage = false;

    _isDraft = true;

  }



  /**

   *@return    The isTemplate value

   */

  public boolean getIsTemplate() {

    return (_isTemplate);

  }



  /**

   *@return    The isPage value

   */

  public boolean getIsPage() {

    return (_isPage);

  }



  /**

   *@return    The isDraft value

   */

  public boolean getIsDraft() {

    return (_isDraft);

  }



  /**

   */

  public void setIsExtendingTemplate() {

    _isExtendingTemplate = true;

  }



  /**

   *@return    The isExtendingTemplate value

   */

  public boolean getIsExtendingTemplate() {

    return (_isExtendingTemplate);

  }



  /**

   *  Sets the windowToOpenOnLoad attribute of the Page object

   *

   *@param  link  The new windowToOpenOnLoad value

   *@param  iwc   The new windowToOpenOnLoad value

   */

  public void setWindowToOpenOnLoad(Link link, IWContext iwc) {

    this.setOnLoad(link.getWindowToOpenCallingScript(iwc));

  }



  /**

   *  Sets the templateId attribute of the Page object

   *

   *@param  id  The new templateId value

   */

  public void setTemplateId(String id) {

    _templateId = id;

  }



  /**

   *  Gets the templateId attribute of the Page object

   *

   *@return    The templateId value

   */

  public String getTemplateId() {

    return (_templateId);

  }



  /**

   *  Used to add source of scriptfiles (JavaScript) The file url should end on

   *  the form "scriptfile.js"

   *

   *@param  jsString  The feature to be added to the ScriptSource attribute

   */

  public void addScriptSource(String jsString) {

    getAssociatedScript().addScriptSource(jsString);

  }



}

