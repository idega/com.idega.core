/*
 *  $Id: Page.java,v 1.113 2004/06/23 17:14:49 palli Exp $
 *
 *  Copyright (C) 2001-2004 Idega Software hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import com.idega.business.IBOLookup;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDynamicPageTrigger;
import com.idega.core.builder.data.ICPage;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;
import com.idega.event.IWFrameBusiness;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWStyleManager;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Window;
import com.idega.repository.data.ImplementorRepository;
import com.idega.servlet.IWCoreServlet;
import com.idega.util.FrameStorageInfo;
import com.idega.util.IWColor;
import com.idega.util.URLUtil;
import com.idega.util.datastructures.QueueMap;

/**
 * An instance of this class is always a top level object in UIComponent tree in an HTML presentation in idegaWeb.
 * This object maps to and renders the 
 * <code><pre>
 * <HTML><HEAD>...</HEAD> <BODY>... </BODY></HTML>
 * </pre></code>
 * tags in HTML and renders the children inside the body tags.
 * 
 *@author     <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
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
	private String _styleSheetURL;
	private String _shortCutIconURL = null;
	private int _shortCutIconID = -1;
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
	private QueueMap _styleSheets;
	private QueueMap _javascripts;
	private Hashtable _HTTPEquivs;
	protected Map _localizationMap;

	private boolean addGlobalScript = true;
	private static String META_KEYWORDS = "keywords";
	private static String META_DESCRIPTION = "description";
	private static String META_HTTP_EQUIV_EXPIRES = "Expires";

	private static Page NULL_CLONE_PAGE = new Page();
	private static boolean NULL_CLONE_PAGE_INITIALIZED = false;

	private ICFile styleFile = null;
	
	private ICDynamicPageTrigger dynamicPageTrigger = null;


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

	public final static String IW_FRAMESET_PAGE_PARAMETER = "idegaweb_frameset_path";
	public final static String IW_FRAME_NAME_PARAMETER = "idegaweb_frame_name";
	public final static String PRM_IW_BROWSE_EVENT_SOURCE = "iw_b_e_s";

	// private final static String START_TAG="<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n<html>";

	/**
	 *  By skipping the validation URL XML compliant browser still recognise
	 *  attributes such as height / width *
	 */
	private final static String START_TAG = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>";

	public final static String MARKUP_LANGUAGE = "markup_language";
	public final static String HTML = "HTML";
	public final static String XHTML = "XHTML";
	public final static String XHTML1_1 = "XHTML1.1";
	
	private final static String END_TAG = "</html>";
	private boolean _isCategory = false;
	private ICPage _windowToOpenOnLoad;
	private int _windowWidth = 800;
	private int _windowHeight = 600;
	

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
		setTitle(s);
	}

	/**
	 *@param  color  The new backgroundColor value
	 */
	public void setBackgroundColor(String color) {
		setStyleAttribute("background-color:"+color);
	}

	/**
	*@param  color  The new backgroundColor value
	*/
	public void setBackgroundColor(IWColor color) {
		setBackgroundColor(color.getHexColorString());
	}

	/**
	 *@param  color  The new textColor value
	 */
	public void setTextColor(String color) {
		setMarkupAttribute("text", color);
	}

	/**
	 *@param  color  The new alinkColor value
	 */
	public void setAlinkColor(String color) {
		setMarkupAttribute("alink", color);
	}

	/**
	 *@param  color  The new hoverColor value
	 */
	public void setHoverColor(String color) {
		setMarkupAttribute("alink", color);
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

	public void addStyleSheetURL(String URL) {
		if (_styleSheets == null) {
			_styleSheets = new QueueMap();
		}
		_styleSheets.put(URL,URL);
	}

	private String getStyleSheetURL(String markup) {
		if (_styleSheets != null && !_styleSheets.isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			Iterator iter = _styleSheets.values().iterator();
			while (iter.hasNext()) {
				String URL = (String) iter.next();
				buffer.append("<link type=\"text/css\" href=\"" + URL + "\" rel=\"stylesheet\" "+(!markup.equals(HTML) ? "/" : "")+">\n");
			}
			return buffer.toString();
		}
		return "";
	}

	public void addJavascriptURL(String URL) {
		if (_javascripts == null) {
			_javascripts = new QueueMap();
		}
		_javascripts.put(URL,URL);
	}

	protected String getJavascriptURLs(IWContext iwc) {
		if (addGlobalScript) {
			StringBuffer buffer = new StringBuffer();
			//Print a reference to the global .js script file
			String src = iwc.getIWMainApplication().getCoreBundle().getResourcesURL();
			ICDomain d = iwc.getDomain();

			if (d.getURL() != null) {
				if (src.startsWith("/")) {
					String protocol;
					/**@todo this is case sensitive and could break! move to IWContext. Also done in Link, SubmitButton, Image and PageIncluder**/
					if (iwc.getRequest().isSecure()) {
						protocol = "https://";
					}
					else {
						protocol = "http://";
					}
					src = protocol + d.getURL() + src;
				}
			}
			buffer.append("<script type=\"text/javascript\" src=\"" + src + "/iw_core.js\">");
			buffer.append("</script>");
			if (_javascripts != null && !_javascripts.isEmpty()) {
				Iterator iter = _javascripts.values().iterator();
				while (iter.hasNext()) {
					String URL = (String) iter.next();
					buffer.append("<script type=\"text/javascript\" src=\"" + URL + "\"></script>\n");
				}
			}
			return buffer.toString();
		}
		return "";
		
	}

	/**
	 *  Sets the linkStyle attribute of the Page object
	 *
	 *@param  style  The new linkStyle value
	 */
	public void setLinkStyle(String style) {
		setStyleDefinition("A", style);
	}

	public void setStyleInStyleSheet(String name, String style) {
		IWStyleManager manager = new IWStyleManager();
		if (name != null && style != null)
			manager.setStyle(name, style);
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
	public void setHTTPEquivTag(String tagName, String tagValue) {
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
		addStyleSheetURL(_styleSheetURL);
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
			returnString.append("<style type=\"text/css\">\n<!--\n");
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
			returnString.append("   -->\n</style>");
			returnString.append("\n");
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
		}
		else {
			return null;
		}
	}

	/**
	 *  Gets the metaTags attribute of the Page object
	 *
	 *@return    The metaTags value
	 */
	public String getMetaTags(String markup) {
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
				returnString.append(" "+(!markup.equals(HTML) ? "/" : "")+">\n");
			}
			returnString.append("\n");
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
				returnString.append(" "+(!markup.equals(HTML) ? "/" : "")+">\n");
			}
			returnString.append("\n");
		}

		return returnString.toString();
	}

	/**
	 *  Gets the hTTPEquivTag attribute of the Page object
	 *
	 *@param  tagName  Description of the Parameter
	 *@return          The hTTPEquivTag value
	 */
	public String getHTTPEquivTag(String tagName) {
		if (_HTTPEquivs != null) {
			return (String) this._HTTPEquivs.get((Object) tagName);
		}
		else {
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
		}
		else {
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
		addStyleSheetURL(styleSheetURL);
	}

	/**
	 *@param  color  The new vlinkColor value
	 */
	public void setVlinkColor(String color) {
		setMarkupAttribute("vlink", color);
		_visitedColor = color;
	}

	/**
	 *@param  color  The new linkColor value
	 */
	public void setLinkColor(String color) {
		setMarkupAttribute("link", color);
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

	public void setLocalizedTitle(String text) {
	}

	public void setIsCategory(boolean isCategory) {
		_isCategory = isCategory;
	}

	public String getLocalizedTitle(IWContext iwc) {
		//Map tree = PageTreeNode.getTree(iwc);
		BuilderService bservice;
		ICTreeNode node=null;
		try {
			bservice = getBuilderService(iwc);
			int pageId = bservice.getCurrentPageId(iwc);
			int currentUserId = -1;
			if(iwc.isLoggedOn()){
				currentUserId=iwc.getCurrentUserId();
				node = (ICTreeNode)bservice.getPageTree(pageId,currentUserId);
			}
			else{
				node = (ICTreeNode)bservice.getPageTree(pageId);
			}
			
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}

		if (node != null) {
			String locName = node.getNodeName(iwc.getCurrentLocale());
			if (locName != null && !locName.equals("")) {
				return locName;
			}
		}
		
		return getTitle();
	}

	/**
	 *@param  width  The new marginWidth value
	 */
	public void setMarginWidth(int width) {
		setLeftMargin(width);
		//setAttribute("marginwidth", Integer.toString(width));
	}

	/**
	 *@param  height  The new marginHeight value
	 */
	public void setMarginHeight(int height) {
		setTopMargin(height);
		//setAttribute("marginheight", Integer.toString(height));
	}

	/**
	 *@param  leftmargin  The new leftMargin value
	 */
	public void setLeftMargin(int leftmargin) {
		setStyleAttribute("margin-left:"+leftmargin+"px");
		//setAttribute("leftmargin", Integer.toString(leftmargin));
	}

	/**
	 *@param  topmargin  The new topMargin value
	 */
	public void setTopMargin(int topmargin) {
		setStyleAttribute("margin-top:"+topmargin+"px");
		//setAttribute("topmargin", Integer.toString(topmargin));
	}

	/**
	 *@param  allMargins  The new allMargins value
	 */
	public void setAllMargins(int allMargins) {
		//setMarginWidth(allMargins);
		//setMarginHeight(allMargins);
		setStyleAttribute("margin:"+allMargins+"px");
	}

	/**
	 *@return    The title value
	 */
	public String getTitle() {
		return _title;
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
		setStyleAttribute("background:url('"+imageURL+"')");
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
		}
		else {
			return image.getMediaURL();
		}

	}

	/**
	 *@param  action  The new onLoad value
	 */
	public void setOnLoad(String action) {
		setMarkupAttributeMultivalued("onload", action);
	}

	/**
	 * Sets an alert that is displayed on page load,
	 * @param alert	The alert to display.
	 */
	public void setAlertOnLoad(String alert) {
		setOnLoad("alert('" + alert + "');");
	}

	/**
	 *@param  action  The new onBlur value
	 */
	public void setOnBlur(String action) {
		setMarkupAttributeMultivalued("onblur", action);
	}

	/**
	 *@param  action  The new onUnLoad value
	 */
	public void setOnUnLoad(String action) {
		setMarkupAttributeMultivalued("onunload", action);
	}

	/**
	 * Sets an alert that is displayed on page unload,
	 * @param alert	The alert to display.
	 */
	public void setAlertOnUnLoad(String alert) {
		setOnUnLoad("alert('" + alert + "');");
	}

	/**
	 *  Sets the window to close immediately when this page is loaded
	 */
	public void close() {
		setOnLoad("window.close()");
	}

	/**
	 *  Sets the window to maintain focus when it is blurred
	 */
	public void keepFocus() {
		setOnBlur("window.focus()");
	}

	/**
	 * Sets the page to go directly back in history one step on load of this page
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
	
	public void setParentToReloadWithURL(String url) {
		setOnUnLoad("window.opener.location.href='"+url+"'");
	}

	/**
	 *  Sets the parent (caller) window to submit its first form when this page unloads if there is one
	 */
	public void setParentPageMainFormToSubmitOnUnLoad() {
		setParentPageFormToSubmitOnUnLoad(0);
	}
	
	/**
	 *  Sets the parent (caller) window to submit its form at index formIndex if there is one, on unload of this page.
	 * @param formIndex index of the form in the parent page
	 */
	public void setParentPageFormToSubmitOnUnLoad(int formIndex) {
		setOnUnLoad("window.opener.document.forms["+formIndex+"].submit()");
	}
	
	/**
	 *  Sets the parent (caller) window to submit the form with the given name if there is one, on unload of this page.
	 * @param formIndex index of the form in the parent page
	 */
	public void setParentPageFormToSubmitOnUnLoad(String formName) {
		setOnUnLoad("javascript:window.opener.document.getElementById('" + formName + "').submit()");
	}
	
	/**
	 *Sets the parent (caller) page to change location (URL) when this page unloads
	 *@param  URL  The new toRedirect value
	 */
	public void setParentToRedirect(String URL) {
		setOnUnLoad("javascript:window.opener.location = '"+URL+"';");
	}

	/**
	 * Displays an alert on load of this page.<br>
	 *@author aron@idega.is
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
	/**
	 *  Sets the defaultAttributes attribute of the Page object
	 *
	 *@param  iwc  The new defaultAttributes value
	 */
	private void setDefaultAttributes(IWContext iwc) {
		/*if (!isAttributeSet("bgcolor")) {
		  setBackgroundColor(iwc.getDefaultBackgroundColor());
		}*/
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

	public Object clonePermissionChecked(IWUserContext iwuc, boolean askForPermission) {

		//return this.clone(iwc,true);
		if (askForPermission) {
			if (iwuc.hasViewPermission(this)) {

				return this.clone(iwuc, askForPermission);

			}
			else {
				if (!NULL_CLONE_PAGE_INITIALIZED) {
					try{
						IWContext iwc = IWContext.getInstance();
						//Text pageNotFound = new Text("No permission", true, false, false);
						//pageNotFound.setFontSize(4);
						//NULL_CLONE_PAGE.add(pageNotFound);
						
						Image noPermissionImage = getBundle(iwc).getImage("shared/stopalert.gif");
						NULL_CLONE_PAGE.add(noPermissionImage);
	
						
						if (iwc != null) {
							BuilderService bservice = getBuilderService(iwc);
							int pageId = 1;
							String page = null;
							// getProperty  //iwc.getParameter(_PRM_PAGE_ID);
							if (page != null) {
								try {
									pageId = Integer.parseInt(page);
								}
								catch (NumberFormatException ex) {
									pageId = bservice.getRootPageId();
								}
							}
							else {
								pageId = bservice.getRootPageId();
							}
							NULL_CLONE_PAGE.setOnLoad("document.location='" + bservice.getPageURI(pageId) + "'");
						}
						NULL_CLONE_PAGE_INITIALIZED = true;
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				return NULL_CLONE_PAGE;
			}
		}
		else {
			return this.clone();
		}

	}

	/**
	 *@param  iwc               Description of the Parameter
	 *@param  askForPermission  Description of the Parameter
	 *@return                   Description of the Return Value
	 */
	public Object clone(IWUserContext iwc, boolean askForPermission) {
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
			if (_javascripts != null) {
				obj._javascripts = _javascripts;
			}
			if (_styleSheets != null) {
				obj._styleSheets = _styleSheets;
			}
			if (_styleDefinitions != null) {
				obj._styleDefinitions = _styleDefinitions;
			}
			if (dynamicPageTrigger != null) {
			    obj.dynamicPageTrigger = (ICDynamicPageTrigger) dynamicPageTrigger.clone();
			}
		}
		catch (Exception ex) {
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
			}
			else {
				setToRedirect(iwc.getRequestURI());
				iwc.getSession().setAttribute("idega_special_reload", "true");
			}
		}

		/* get the files cached url */
		if (styleFile != null) {
			ICFileSystem fsystem = getICFileSystem(iwc);
			String styleSheetURL =  fsystem.getFileURI(((Integer)styleFile.getPrimaryKey()).intValue());
			setStyleSheetURL(styleSheetURL);
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
		PresentationObject parent = getParentObject();
		if (parent != null) {
			if (parent instanceof Page) {
				if (parent instanceof FrameSet) {
					return false;
				}
				else {
					return true;
				}
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * @return a boolean wether (this) has a parent that is a FrameSet
	 */
	protected boolean isInFrameSet() {
		PresentationObject parent = getParentObject();
		if (parent != null) {
			if (parent instanceof FrameSet || parent instanceof Frame) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * <code>Gets the contents inside the Head <head> </head> tags with the exception of the title and
	 * the "associated script.</code>
	 * @param iwc
	 * @return
	 */
	protected String getHeadContents(IWContext iwc){
		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		String characterEncoding = settings.getCharacterEncoding(); 
		String markup = iwc.getApplicationSettings().getProperty(MARKUP_LANGUAGE, HTML);
		return getHeadContents(markup,characterEncoding,iwc);
	}
	
	/**
	 * <code>Gets the contents inside the Head <head> </head> tags with the exception of the title and
	 * the "associated script.</code>
	 * @param iwc
	 * @return
	 */
	protected String getHeadContents(String markup,String characterEncoding,IWContext iwc){
		StringBuffer buf = new StringBuffer();
		
		buf.append(getPrintableSchortCutIconURL(iwc));
		
		buf.append(getMetaInformation(markup, characterEncoding));
		buf.append(getMetaTags(markup));
		buf.append(getJavascriptURLs(iwc));
		buf.append(getStyleSheetURL(markup));
		buf.append(getStyleDefinition());	
		return buf.toString();
	}

	
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#initVariables(com.idega.presentation.IWContext)
	 */
	public void initVariables(IWContext iwc) throws IOException {
		super.initVariables(iwc);
		if (this._styleSheetURL == null)
			_styleSheetURL = iwc.getIWMainApplication().getTranslatedURIWithContext("/idegaweb/style/style.css");

		setDefaultValues();
		setDefaultAttributes(iwc);
	}
	/**
	 *@param  iwc            Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	public void print(IWContext iwc) throws Exception {

		boolean isInsideOtherPage = this.isChildOfOtherPage();

		if (getLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			if (!isInsideOtherPage) {
				IWMainApplicationSettings settings = iwc.getApplicationSettings();
				String characterEncoding = settings.getCharacterEncoding(); 
				String markup = iwc.getApplicationSettings().getProperty(MARKUP_LANGUAGE, HTML);
				println(getStartTag(iwc.getCurrentLocale(), markup, characterEncoding));
				if (_zeroWait) {
					setDoPrint(false);
				}

				if (_windowToOpenOnLoad != null) {
					URLUtil url = new URLUtil(iwc, _windowToOpenOnLoad);
					setOnLoad("javascript:"+Window.getWindowCallingScript(url.toString(), "Window", false, false, false, false, false, true, true, true, false, _windowWidth, _windowHeight));	
				}
				
				println("<head>");
				println("<title>" + getLocalizedTitle(iwc) + "</title>\n");
				/*
				//shortcut icon
				println(getPrintableSchortCutIconURL(iwc));
				print(getMetaInformation(markup, characterEncoding));
				print(getMetaTags(markup));
				print(getJavascriptURLs(iwc));
				if (getAssociatedScript() != null) {
					getAssociatedScript()._print(iwc);
				}
				print(getStyleSheetURL(markup));
				print(getStyleDefinition());
				*/

				print(getHeadContents(markup,characterEncoding,iwc));
				if (getAssociatedScript() != null) {
					getAssociatedScript()._print(iwc);
				}

				//Laddi: Made obsolete with default style sheet
				/*if (_addStyleSheet) {
					println("<link rel=\"stylesheet\" href=\"" + _styleSheetURL + "\" type=\"text/css\">\n");
				}*/

				println("\n</head>");

				if (_addBody) {
					println("<body " + getMarkupAttributesString() + ">");
				}
				//added by Eiki for frameSet in a page support

			}
			//Catch all exceptions that are thrown in print functions of objects stored inside
			try {
				super.print(iwc);
			}
			catch (Exception ex) {
				println("<h1>An Error Occurred!</h1>");
				println("IW Error");
				println("<pre>");
				println(ex.getMessage());
				ex.printStackTrace(System.err);
				println("</pre>");
			}

			if (!isInsideOtherPage) {
				if (_addBody) {
					println("\n\n</body>");
				}
				println(getEndTag());
			}
		}
		else if (getLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			println("<?xml version=\"1.0\"?>");
			println("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.3//EN\" \"http://www.wapforum.org/DTD/wml13.dtd\">");
			println("<wml>");
			println("<card title=\"" + getLocalizedTitle(iwc) + "\" id=\"card1\">");

			//Catch all exceptions that are thrown in print functions of objects stored inside
			try {
				super.print(iwc);
			}
			catch (Exception ex) {
				println("<p>Villa var&eth;!</p>");
				println("<p>IWError</p>");
				println("<p>");
				println(ex.getMessage());
				ex.printStackTrace(System.err);
				println("</p>");
			}

			println("</card>");
			println("</wml>");
		}
		else if(getLanguage().equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)){
			println("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
			//println("<!DOCTYPE ITEXT SYSTEM \"http://www.lowagie.com/iText/itext.dtd\">");
			println("<itext producer=\"Idega Software, http://www.idega.com\">");
			try {
				super.print(iwc);
			}
			catch (Exception ex) {
				println("<paragraph leading=\"18.0\" font=\"unknown\" align=\"Default\">An error occurred</paragraph>");
			}
			println("</itext>");

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
	public static String getStartTag(Locale locale, String markup, String encoding) {
		if (markup.equals(XHTML)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"").append(encoding != null ? encoding : "ISO-8859-1").append("\"?>").append("\n");
			buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"").append("\n");
			buffer.append("\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">").append("\n");
			buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\""+locale.getLanguage()+"\" lang=\""+locale.getLanguage()+"\">");
			return buffer.toString();
		}
		else if (markup.equals(XHTML1_1)) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"").append(encoding != null ? encoding : "ISO-8859-1").append("\"?>").append("\n");
			buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"").append("\n");
			buffer.append("\t\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">").append("\n");
			buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\""+locale.getLanguage()+"\">");
			return buffer.toString();
		}
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
	public String getMetaInformation(String markup, String characterEncoding) {

		boolean addIdegaAuthorAndCopyRight = false;
		
		String theReturn = "<meta http-equiv=\"content-type\" content=\"text/html; charset="+characterEncoding+"\" "+(!markup.equals(HTML) ? "/" : "")+">\n<meta name=\"generator\" content=\"idegaWeb 1.3\" "+(!markup.equals(HTML) ? "/" : "")+">\n";

		//If the user is logged on then there is no caching by proxy servers
		boolean notUseProxyCaching = true;

		if (notUseProxyCaching) {
			theReturn += "<meta http-equiv=\"pragma\" content=\"no-cache\" "+(!markup.equals(HTML) ? "/" : "")+">\n";
		}
		if (getRedirectInfo() != null) {
			theReturn += "<meta http-equiv=\"refresh\" content=\"" + getRedirectInfo() + "\" "+(!markup.equals(HTML) ? "/" : "")+">\n";
		}

		if (addIdegaAuthorAndCopyRight) {
			theReturn += "<meta name=\"author\" content=\"idega.is\"/>\n<meta name=\"copyright\" content=\"idega.is\" "+(!markup.equals(HTML) ? "/" : "")+">\n";
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
		String framePathKey = iwc.getParameter(IW_FRAMESET_PAGE_PARAMETER);
		String frameNameKey = iwc.getParameter(IW_FRAME_NAME_PARAMETER);

		if (framePathKey != null && frameNameKey != null) {
			/**
			 * @todo EJB create
			 */
			IWFrameBusiness fb = (IWFrameBusiness) IBOLookup.getSessionInstance(iwc, IWFrameBusiness.class);
			Page pg = fb.getFrame(framePathKey, frameNameKey);
			if (pg != null) {
				//	if( iwc.getParameter(PRM_IW_BROWSE_EVENT_SOURCE) != null && pg instanceof IWBrowseControl){
				//	  //System.out.println("dispatchEvent(iwc)");
				//	  ((IWBrowseControl)pg).dispatchEvent(iwc);
				//	}
				//        else {
				//          System.out.println("!dispatchEvent(iwc)");
				//        }
				return pg;
			}
			else {
				Page defaultPage = new Page();
				//defaultPage.setBackgroundColor("#FF0000");
				System.err.println("[" + Page.class +"]: Frame " + frameNameKey + ": page is null");
				return defaultPage;
			}

		}
		else if (frameKey != null) {
			Page page = getPage(getFrameStorageInfo(iwc), iwc);
			System.out.println("com.idega.presentation.Page: Trying to get page stored in session");
			return page;
		}
		else if (classKey != null) {
			//try{
			String className = IWMainApplication.decryptClassName(classKey);
			Page page = null;
			try {
				page = (Page) Class.forName(className).newInstance();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
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
			}
			catch (NumberFormatException ex) {
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
		}
		else {
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
			}
			catch (Exception ex) {
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
	
	public void setWindowToOpenOnLoad(ICPage page) {
		setWindowToOpenOnLoad(page, 800, 600);
	}
	
	public void setWindowToOpenOnLoad(ICPage page, int width, int height) {
		_windowToOpenOnLoad = page;
		_windowWidth = width;
		_windowHeight = height;
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
	
	/**
	 * Gets the file id of the shortcut icon
	 * @return the shortcut icon file id
	 */
	public int getShortCutIconID() {
		return _shortCutIconID;
	}

	/**
	 *  Gets the URL of the shortcut icon
	 * @return URL to shortcut icon
	 */
	public String getShortCutIconURL() {
		return _shortCutIconURL;
	}

	/**
	 * Sets the file id of the shortcut icon
	 * @param id of the  icon file
	 */
	public void setShortCutIconID(int id) {
		_shortCutIconID = id;
	}

	/**
	 * Sets the URL to the shortcut icon
	 * @param url to the icon file
	 */
	public void setShortCutIconURL(String url) {
		_shortCutIconURL = url;
	}
	
	private String getPrintableSchortCutIconURL(IWContext iwc){
		String url = null;
		if(getShortCutIconID()>0){
			ICFileSystem fsystem;
			try {
				fsystem = getICFileSystem(iwc);
				url =  fsystem.getFileURI(getShortCutIconID());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else if(getShortCutIconURL()!=null){
			url = getShortCutIconURL();
		}
		if(url!=null)
			return    "<link type=\"shortcut icon\" href=\""+url+"\" />";
		return "";
		//<link rel="shortcut icon" href="/favicon.ico">
	}

	
	public ICDynamicPageTrigger getDynamicPageTrigger() {
		if (dynamicPageTrigger == null) {
			dynamicPageTrigger = (ICDynamicPageTrigger) ImplementorRepository.getInstance().getImplementorOrNull(ICDynamicPageTrigger.class, this.getClass());
		}
		return dynamicPageTrigger;
	}
	
}