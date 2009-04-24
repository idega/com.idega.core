/*
 * $Id: Page.java,v 1.186 2009/04/24 08:39:08 valdas Exp $ Created in 2000 by Tryggvi Larusson Copyright (C) 2001-2005 Idega Software hf. All Rights
 * Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 * 
 */
package com.idega.presentation;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDynamicPageTrigger;
import com.idega.core.builder.data.ICPage;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDONoDatastoreError;
import com.idega.event.IWFrameBusiness;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWStyleManager;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.include.GlobalIncludeManager;
import com.idega.idegaweb.include.JavaScriptLink;
import com.idega.idegaweb.include.PageResourceConstants;
import com.idega.idegaweb.include.StyleSheetLink;
import com.idega.io.serialization.FileObjectReader;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Window;
import com.idega.repository.data.ImplementorRepository;
import com.idega.repository.data.PropertyDescription;
import com.idega.repository.data.PropertyDescriptionHolder;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.servlet.IWCoreServlet;
import com.idega.util.CoreConstants;
import com.idega.util.FacesUtil;
import com.idega.util.FrameStorageInfo;
import com.idega.util.IWColor;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.URLUtil;
import com.idega.util.datastructures.QueueMap;
import com.idega.util.reflect.Property;
import com.idega.util.resources.ResourcesAdder;

/**
 * <p>
 * An instance of this class (or subclass) is always a top level object in UIComponent tree in an HTML presentation in idegaWeb. This object maps to
 * and renders the
 * 
 * <pre>
 *   &lt;HTML&gt;&lt;HEAD&gt;...&lt;/HEAD&gt; &lt;BODY&gt;... &lt;/BODY&gt;&lt;/HTML&gt;
 * </pre>
 * 
 * tags in HTML and renders the children inside the body tags.
 * </p>
 * Last modified: $Date: 2009/04/24 08:39:08 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.186 $
 */
public class Page extends PresentationObjectContainer implements PropertyDescriptionHolder {

	// static variables:
	private static Page NULL_CLONE_PAGE = new Page();
	private static boolean NULL_CLONE_PAGE_INITIALIZED = false;
	protected final static String ROWS_PROPERTY = "ROWS";
	protected final static String IW_PAGE_KEY = "idegaweb_page";
	public final static String IW_FRAME_STORAGE_PARMETER = "idegaweb_frame_page";
	public final static String IW_FRAME_CLASS_PARAMETER = "idegaweb_frame_class";
	public final static String IW_FRAMESET_PAGE_PARAMETER = "idegaweb_frameset_path";
	public final static String IW_FRAME_NAME_PARAMETER = "idegaweb_frame_name";
	public final static String PRM_IW_BROWSE_EVENT_SOURCE = "iw_b_e_s";
	// private final static String START_TAG="<!DOCTYPE HTML PUBLIC
	// \"-//W3C//DTD HTML 4.01 Transitional//EN\"
	// \"http://www.w3.org/TR/html4/loose.dtd\">\n<html>";
	/**
	 * By skipping the validation URL XML compliant browser still recognise attributes such as height / width *
	 */
	public final static String DOCTYPE_HTML_4_0_1_TRANSITIONAL = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">";
	public final static String DOCTYPE_HTML_4_0_1_STRICT = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
	public final static String DOCTYPE_XHTML_1_0_TRANSITIONAL = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	public final static String DOCTYPE_XHTML_1_1 = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n\t\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">";
	/**
	 * Constant used to declare if the rendering should be in HTML 4.0 or lower.<br/> This can be set as a property in
	 * IWMainApplicationSettings.getDefaultMarkupLanguage() for backwards compatability.
	 */
	public final static String HTML = "HTML";
	/**
	 * Constant used to declare if the rendering should be in XHTML 1.0 (transitional).<br/> This is used by
	 * IWMainApplicationSettings.getDefaultMarkupLanguage() and is the default value in ePlatform 3.0
	 */
	public final static String XHTML = "XHTML";
	/**
	 * Constant used to declare if the rendering should be in XHTML 1.1 (strict).<br/> This can be set as a property in
	 * IWMainApplicationSettings.getDefaultMarkupLanguage().
	 */
	public final static String XHTML1_1 = "XHTML1.1";
	// private final static String START_TAG = "<!DOCTYPE HTML PUBLIC
	// \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>";
	private final static String START_TAG_HTML_4_0 = "<html>";
	private final static String END_TAG = "</html>";
	private static String META_KEYWORDS = "keywords";
	private static String META_DESCRIPTION = "description";
	private static String META_HTTP_EQUIV_EXPIRES = "Expires";
	private final static String NEWLINE = "\n";
	// State held variables:
	private int _ibPageID = -1;
	private String _title;
	private boolean _zeroWait = false;
	private int _redirectSecondInterval = -1;
	private String _redirectURL = null;
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
	private String _shortCutIconURL = null;
	private int _shortCutIconID = -1;
	private boolean _addStyleSheet = false;
	private boolean _addBody = true;
	private boolean _isTemplate = false;
	private boolean _isPage = true;
	private boolean _isDraft = false;
	private boolean _isExtendingTemplate = false;
	private String _templateId = null;
	private Map<String, String> _styleDefinitions;
	private Map<String, String> _metaTags;
	private QueueMap<String, StyleSheetLink> _styleSheets;
	private QueueMap<String, JavaScriptLink> _javascripts;
	private QueueMap<String, JavaScriptLink> javaScriptActions;
	private QueueMap<String, JavaScriptLink> _javascriptStringsBeforeJSUrls;
	private QueueMap<String, JavaScriptLink> _javascriptStringsAfterJSUrls;
	private Map<String, String> _HTTPEquivs;
	private boolean addGlobalScript = true;
	private ICFile styleFile = null;
	private ICDynamicPageTrigger dynamicPageTrigger = null;
	private boolean _isCategory = false;
	private ICPage forwardPage;
	private String docType;
	private boolean useIE7Extension = false;
	private boolean useHtmlTag = true;
	private boolean printScriptSourcesDirectly = true;
	private boolean hideBuilder = false;

	/**
	 */
	public Page() {
		this("");
	}

	/**
	 * @param s
	 *          Description of the Parameter
	 */
	public Page(String s) {
		super();
		setTransient(false);
		setTitle(s);
	}

	/**
	 * @param color
	 *          The new backgroundColor value
	 */
	public void setBackgroundColor(String color) {
		setStyleAttribute("background-color:" + color);
	}

	/**
	 * @param color
	 *          The new backgroundColor value
	 */
	public void setBackgroundColor(IWColor color) {
		setBackgroundColor(color.getHexColorString());
	}

	/**
	 * @param color
	 *          The new textColor value
	 */
	public void setTextColor(String color) {
		setMarkupAttribute("text", color);
	}

	/**
	 * @param color
	 *          The new alinkColor value
	 */
	public void setAlinkColor(String color) {
		setMarkupAttribute("alink", color);
	}

	/**
	 * @param color
	 *          The new hoverColor value
	 */
	public void setHoverColor(String color) {
		setMarkupAttribute("alink", color);
		this._hoverColor = color;
	}

	/**
	 * Sets the styleDefinition attribute of the Page object
	 * 
	 * @param styleName
	 *          The new styleDefinition value
	 * @param styleAttribute
	 *          The new styleDefinition value
	 */
	public void setStyleDefinition(String styleName, String styleAttribute) {
		if (this._styleDefinitions == null) {
			this._styleDefinitions = new Hashtable<String, String>();
		}
		this._styleDefinitions.put(styleName, styleAttribute);
	}

	public void addStyleSheetURLForPrint(String URL) {
		addStyleSheetURL(URL, PageResourceConstants.MEDIA_PRINT);
	}

	public void addStyleSheetURL(String URL) {
		addStyleSheetURL(URL, PageResourceConstants.MEDIA_SCREEN);
	}
	
	protected void addStyleSheetURL(String URL, String mediaType) {
		if (this._styleSheets == null) {
			this._styleSheets = new QueueMap<String, StyleSheetLink>();
		}
		this._styleSheets.put(URL, new StyleSheetLink(URL, StringUtil.isEmpty(mediaType) ? PageResourceConstants.MEDIA_ALL : mediaType));
	}

	private String getStyleSheetURL(String markup, IWContext iwc) {
		StringBuffer buffer = new StringBuffer();

		// The default style sheet MUST come first so we can override it in
		// latter sheets!
		List<StyleSheetLink> sheets = GlobalIncludeManager.getInstance().getStyleSheets();
		for (Iterator<StyleSheetLink> iter = sheets.iterator(); iter.hasNext();) {
			StyleSheetLink sheet = iter.next();
			String url = sheet.getUrl();
			String styleSheetURL = iwc.getIWMainApplication().getTranslatedURIWithContext(url);

			if (sheet.getMedia() != null) {
				addStyleSheet(iwc, buffer, markup, styleSheetURL, sheet.getMedia());
			}
			else {
				addStyleSheet(iwc, buffer, markup, styleSheetURL);
			}
		}
		
		String className = this.getClass().getName().toLowerCase();
		if (className.indexOf(CoreConstants.WORKSPACE_VIEW_MANAGER_ID) != -1) {
			addStyleSheet(iwc, buffer, markup, iwc.getIWMainApplication().getBundle(CoreConstants.WORKSPACE_BUNDLE_IDENTIFIER)
					.getVirtualPathWithFileNameString("style/workspace.css"));
		}
		
		// Now the added style
		if (this._styleSheets != null && !this._styleSheets.isEmpty()) {
			for (StyleSheetLink styleSheet: this._styleSheets.values()) {
				addStyleSheet(iwc, buffer, markup, styleSheet.getUrl(), styleSheet.getMedia());
			}
		}
		
		return buffer.toString();
	}

	private StringBuffer addStyleSheet(IWContext iwc, StringBuffer buffer, String markup, String URL) {
		return addStyleSheet(iwc, buffer, markup, URL, PageResourceConstants.MEDIA_SCREEN);
	}

	private StringBuffer addStyleSheet(IWContext iwc, StringBuffer buffer, String markup, String URL, String media) {
		if (!(PageResourceConstants.MEDIA_ALL.equals(media) || PageResourceConstants.MEDIA_SCREEN.equals(media)) && !ResourcesAdder.isCSSOptimizationTurnedOn()) {
			return buffer.append("<link type=\"text/css\" href=\"").append(URL).append("\" rel=\"stylesheet\" media=\"").append(media).append("\" ")
				.append((!markup.equals(HTML) ? CoreConstants.SLASH : CoreConstants.EMPTY)).append(">\n");
		}

		PresentationUtil.addStyleSheetToHeader(iwc, URL, media);
		return buffer;
	}
	
	public void addJavaScriptAction(String action) {
		if (this.javaScriptActions == null) {
			this.javaScriptActions = new QueueMap<String, JavaScriptLink>();
		}

		addScript(this.javaScriptActions, action, action);
	}
	
	protected void includeJavaScriptActions(IWContext iwc) {
		if (this.javaScriptActions == null || this.javaScriptActions.isEmpty()) {
			return;
		}
		
		for (JavaScriptLink scriptAction: this.javaScriptActions.values()) {
			PresentationUtil.addJavaScriptActionsToBody(iwc, scriptAction.getActions());
		}
	}

	public void addJavascriptURL(String URL) {
		if (this._javascripts == null) {
			this._javascripts = new QueueMap<String, JavaScriptLink>();
		}
		if (!this._javascripts.containsKey(URL)) {
			this._javascripts.put(URL, new JavaScriptLink(URL));
		}
	}

	protected String getJavascriptURLs(IWContext iwc) {
		if (this.addGlobalScript) {
			StringBuilder coreScript = new StringBuilder(iwc.getIWMainApplication().getCoreBundle().getResourcesURL()).append("/iw_core.js");
			PresentationUtil.addJavaScriptSourceLineToHeader(iwc, coreScript.toString());
			
			if (this._javascripts != null && !this._javascripts.isEmpty()) {
				for (JavaScriptLink source: this._javascripts.values()) {
					PresentationUtil.addJavaScriptSourceLineToHeader(iwc, source.getUrl());
				}
			}
		}
		return CoreConstants.EMPTY;
	}

	/**
	 * Sets the linkStyle attribute of the Page object
	 * 
	 * @param style
	 *          The new linkStyle value
	 */
	public void setLinkStyle(String style) {
		setStyleDefinition("A", style);
	}

	public void setStyleInStyleSheet(String name, String style) {
		IWStyleManager manager = IWStyleManager.getInstance();
		if (name != null && style != null) {
			manager.setStyle(name, style);
		}
	}

	/**
	 * Sets the linkHoverStyle attribute of the Page object
	 * 
	 * @param style
	 *          The new linkHoverStyle value
	 */
	public void setLinkHoverStyle(String style) {
		setStyleDefinition("A:hover", style);
	}

	/**
	 * Sets the pageStyle attribute of the Page object
	 * 
	 * @param style
	 *          The new pageStyle value
	 */
	public void setPageStyle(String style) {
		setStyleDefinition("body", style);
		setStyleDefinition("table", style);
	}

	/**
	 * Sets the metaTag attribute of the Page object
	 * 
	 * @param tagName
	 *          The new metaTag value
	 * @param tagValue
	 *          The new metaTag value
	 */
	public void setMetaTag(String tagName, String tagValue) {
		if (this._metaTags == null) {
			this._metaTags = new Hashtable<String, String>();
		}
		this._metaTags.put(tagName, tagValue);
	}

	/**
	 * Sets the hTTPEquivTag attribute of the Page object
	 * 
	 * @param tagName
	 *          The new hTTPEquivTag value
	 * @param tagValue
	 *          The new hTTPEquivTag value
	 */
	public void setHTTPEquivTag(String tagName, String tagValue) {
		if (this._HTTPEquivs == null) {
			this._HTTPEquivs = new Hashtable<String, String>();
		}
		this._HTTPEquivs.put(tagName, tagValue);
	}

	/**
	 * Sets the keywordsMetaTag attribute of the Page object
	 * 
	 * @param wordsCommaSeparated
	 *          The new keywordsMetaTag value
	 */
	public void setKeywordsMetaTag(String wordsCommaSeparated) {
		setMetaTag(META_KEYWORDS, wordsCommaSeparated);
	}

	/**
	 * Sets the descriptionMetaTag attribute of the Page object
	 * 
	 * @param wordsCommaSeparated
	 *          The new descriptionMetaTag value
	 */
	public void setDescriptionMetaTag(String wordsCommaSeparated) {
		setMetaTag(META_DESCRIPTION, wordsCommaSeparated);
	}

	/**
	 * Sets the expiryDate attribute of the Page object
	 * 
	 * @param dateString
	 *          The new expiryDate value
	 */
	public void setExpiryDate(String dateString) {
		this.setHTTPEquivTag(META_HTTP_EQUIV_EXPIRES, dateString);
	}

	/**
	 * Sets the defaultValues attribute of the Page object
	 */
	private void setDefaultValues() {
	}

	/**
	 * Gets the styleDefinition attribute of the Page object
	 * 
	 * @return The styleDefinition value
	 */
	public String getStyleDefinition() {
		StringBuffer returnString = new StringBuffer();
		if (this._styleDefinitions != null) {
			returnString.append("<style type=\"text/css\">\n<!--\n");
			for (String styleName: this._styleDefinitions.keySet()) {
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
	 * <p>
	 * This method gets the script fragment that calls the javascript for the IE7 (plugin) that makes IE more standards compliant.<br/> See: <a
	 * href="http://dean.edwards.name/IE7/">http://dean.edwards.name/IE7/</a>
	 * </p>
	 * 
	 * @return
	 */
	public String getIE7() {
		String scriptUrl = IWMainApplication.getDefaultIWMainApplication().getCoreBundle().getResourcesURL() + "/ie7/ie7-standard-p.js";
		String scriptString = "<!-- compliance patch for microsoft browsers -->\n" + "<!--[if lt IE 7]><script src=\"" + scriptUrl + "\" type=\"text/javascript\"></script><![endif]-->";
		return scriptString;
	}

	/**
	 * <p>
	 * Gets if the IE7 Code fragment is rendered out in the header of the page. Defaults to false.
	 * </p>
	 * 
	 * @return
	 */
	public boolean getUseIE7Extension() {
		return this.useIE7Extension;
	}

	/**
	 * <p>
	 * Sets if the IE7 Extension (http://dean.edwards.name/IE7/) should be used. Default is false.
	 * </p>
	 */
	public void setUseIE7Extension(boolean useIE7Extension) {
		this.useIE7Extension = useIE7Extension;
	}

	/**
	 * Gets the styleAttribute attribute of the Page object
	 * 
	 * @param styleName
	 *          Description of the Parameter
	 * @return The styleAttribute value
	 */
	public String getStyleAttribute(String styleName) {
		if (this._styleDefinitions != null) {
			return this._styleDefinitions.get(styleName);
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the metaTags attribute of the Page object
	 * 
	 * @return The metaTags value
	 */
	public String getMetaTags(String markup) {
		StringBuffer returnString = new StringBuffer();
		if (this._metaTags != null) {
			for (String tagName: this._metaTags.keySet()) {
				returnString.append("<meta name=\"");
				returnString.append(tagName);
				returnString.append("\" ");
				String tagValue = getMetaTag(tagName);
				if (tagValue != null) {
					returnString.append(" content=\"");
					returnString.append(tagValue);
					returnString.append("\"");
				}
				returnString.append(" " + (!markup.equals(HTML) ? "/" : "") + ">\n");
			}
			returnString.append("\n");
		}
		if (this._HTTPEquivs != null) {
			for (String tagName: this._HTTPEquivs.keySet()) {
				returnString.append("<meta http-equiv=\"");
				returnString.append(tagName);
				returnString.append("\" ");
				String tagValue = getHTTPEquivTag(tagName);
				if (tagValue != null) {
					returnString.append(" content=\"");
					returnString.append(tagValue);
					returnString.append("\"");
				}
				returnString.append(" " + (!markup.equals(HTML) ? "/" : "") + ">\n");
			}
			returnString.append("\n");
		}
		return returnString.toString();
	}

	/**
	 * Gets the hTTPEquivTag attribute of the Page object
	 * 
	 * @param tagName
	 *          Description of the Parameter
	 * @return The hTTPEquivTag value
	 */
	public String getHTTPEquivTag(String tagName) {
		if (this._HTTPEquivs != null) {
			return this._HTTPEquivs.get(tagName);
		}
		else {
			return null;
		}
	}

	/**
	 * Gets the metaTag attribute of the Page object
	 * 
	 * @param tagName
	 *          Description of the Parameter
	 * @return The metaTag value
	 */
	public String getMetaTag(String tagName) {
		if (this._metaTags != null) {
			return this._metaTags.get(tagName);
		}
		else {
			return null;
		}
	}

	/**
	 * @param textDecoration
	 *          The new textDecoration value
	 */
	public void setTextDecoration(String textDecoration) {
		this._textDecoration = textDecoration;
	}

	/**
	 * @param hoverDecoration
	 *          The new hoverDecoration value
	 */
	public void setHoverDecoration(String hoverDecoration) {
		this._hoverDecoration = hoverDecoration;
	}

	/**
	 * @param styleSheetURL
	 *          The new styleSheetURL value
	 */
	public void setStyleSheetURL(String styleSheetURL) {
		if (StringUtil.isEmpty(styleSheetURL)) {
			return;
		}
		
		String sources = Property.getValueFromExpression(styleSheetURL, String.class);
		if (!StringUtil.isEmpty(sources)) {
			styleSheetURL = sources;
		}
		
		int index = styleSheetURL.indexOf(CoreConstants.COMMA);
		while (index > -1) {
			addStyleSheetURL(styleSheetURL.substring(0, index));
			try {
				styleSheetURL = styleSheetURL.substring(index + 1);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				styleSheetURL = styleSheetURL.substring(index);
			}
			styleSheetURL.trim();
			index = styleSheetURL.indexOf(CoreConstants.COMMA);
		}
		addStyleSheetURL(styleSheetURL);
	}

	/**
	 * @param color
	 *          The new vlinkColor value
	 */
	public void setVlinkColor(String color) {
		setMarkupAttribute("vlink", color);
		this._visitedColor = color;
	}

	/**
	 * @param color
	 *          The new linkColor value
	 */
	public void setLinkColor(String color) {
		setMarkupAttribute("link", color);
		this._linkColor = color;
	}

	/**
	 * @param textFontFace
	 *          The new pageFontFace value
	 */
	public void setPageFontFace(String textFontFace) {
		this._pageStyleFont = textFontFace;
	}

	/**
	 * @param textFontSize
	 *          The new pageFontSize value
	 */
	public void setPageFontSize(String textFontSize) {
		this._pageStyleFont = textFontSize;
	}

	/**
	 * @param textFontStyle
	 *          The new pageFontStyle value
	 */
	public void setPageFontStyle(String textFontStyle) {
		this._pageStyleFontStyle = textFontStyle;
	}

	/**
	 * @return The pageFontFace value
	 */
	public String getPageFontFace() {
		return (this._pageStyleFont);
	}

	/**
	 * @return The pageFontSize value
	 */
	public String getPageFontSize() {
		return (this._pageStyleFont);
	}

	/**
	 * @return The pageFontStyle value
	 */
	public String getPageFontStyle() {
		return (this._pageStyleFontStyle);
	}

	/**
	 * @param title
	 *          The new title value
	 */
	@Override
	public void setTitle(String title) {
		this._title = title;
		setName(title);
	}

	public void setLocalizedTitle(String text) {
	}

	public void setIsCategory(boolean isCategory) {
		this._isCategory = isCategory;
	}

	public String getLocalizedTitle(IWContext iwc) {
		if (getTitle() == null) {
			BuilderService bservice;
			ICTreeNode node = null;
			try {
				bservice = getBuilderService(iwc);
				int pageId = bservice.getCurrentPageId(iwc);
				int currentUserId = -1;
				if (iwc.isLoggedOn()) {
					currentUserId = iwc.getCurrentUserId();
					node = bservice.getPageTree(pageId, currentUserId);
				}
				else {
					node = bservice.getPageTree(pageId);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			catch (IDONoDatastoreError de) {}
			if (node != null) {
				String locName = node.getNodeName(iwc.getCurrentLocale());
				if (locName != null && !locName.equals("")) {
					return locName;
				}
			}
		}
		return getTitle();
	}

	/**
	 * @param width
	 *          The new marginWidth value
	 */
	public void setMarginWidth(int width) {
		setLeftMargin(width);
	}

	/**
	 * @param height
	 *          The new marginHeight value
	 */
	public void setMarginHeight(int height) {
		setTopMargin(height);
	}

	/**
	 * @param leftmargin
	 *          The new leftMargin value
	 */
	public void setLeftMargin(int leftmargin) {
		setStyleAttribute("margin-left:" + leftmargin + "px");
	}

	/**
	 * @param topmargin
	 *          The new topMargin value
	 */
	public void setTopMargin(int topmargin) {
		setStyleAttribute("margin-top:" + topmargin + "px");
	}

	/**
	 * @param allMargins
	 *          The new allMargins value
	 */
	public void setAllMargins(int allMargins) {
		setStyleAttribute("margin:" + allMargins + "px");
	}

	/**
	 * @return The title value
	 */
	@Override
	public String getTitle() {
		return this._title;
	}

	/**
	 * @param myScript
	 *          The new associatedScript value
	 */
	@Override
	public void setAssociatedScript(Script myScript) {
		getFacets().put("page_associated_script", myScript);
	}

	/*
	 * 
	 */
	/**
	 * Description of the Method
	 */
	private void initializeAssociatedScript() {
		Script _theAssociatedScript = (Script) getFacets().get("page_associated_script");
		if (_theAssociatedScript == null) {
			_theAssociatedScript = new Script();
			setAssociatedScript(_theAssociatedScript);
		}
	}

	/**
	 * @return The associatedScript value
	 */
	@Override
	public Script getAssociatedScript() {
		initializeAssociatedScript();
		return (Script) getFacets().get("page_associated_script");
	}

	/**
	 * @param imageURL
	 *          The new backgroundImage value
	 */
	public void setBackgroundImage(String imageURL) {
		setStyleAttribute("background:url('" + imageURL + "')");
	}

	/**
	 * @param backgroundImage
	 *          The new backgroundImage value
	 * @todo : this must implemented in the print method...like in the Link class IMPORTANT! for this to work you must have an application property
	 *       called IW_USES_OLD_MEDIA_TABLES (set to anything)
	 */
	public void setBackgroundImage(Image backgroundImage) {
		if (backgroundImage != null) {
			setBackgroundImage(getImageUrl(backgroundImage));
		}
	}

	/**
	 * @param image
	 *          Description of the Parameter
	 * @return The imageUrl value
	 * @todo : replace this with a implementation in print IMPORTANT! for this to work you must have an application property called
	 *       IW_USES_OLD_MEDIA_TABLES (set to anything)
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
	 * @param action
	 *          The new onLoad value
	 */
	public void setOnLoad(String action) {
		setMarkupAttributeMultivalued("onload", action);
	}

	/**
	 * Sets an alert that is displayed on page load,
	 * 
	 * @param alert
	 *          The alert to display.
	 */
	public void setAlertOnLoad(String alert) {
		setOnLoad("alert('" + alert + "');");
	}

	/**
	 * @param action
	 *          The new onBlur value
	 */
	public void setOnBlur(String action) {
		setMarkupAttributeMultivalued("onblur", action);
	}

	/**
	 * @param action
	 *          The new onUnLoad value
	 */
	public void setOnUnLoad(String action) {
		setMarkupAttributeMultivalued("onunload", action);
	}

	/**
	 * Sets an alert that is displayed on page unload,
	 * 
	 * @param alert
	 *          The alert to display.
	 */
	public void setAlertOnUnLoad(String alert) {
		setOnUnLoad("alert('" + alert + "');");
	}

	/**
	 * Sets the window to close immediately when this page is loaded
	 */
	public void close() {
		setOnLoad("window.close()");
	}

	/**
	 * Sets the window to close immediately when page is loaded and the focus on its parent ( opener ) if exists
	 * 
	 * @param focusOnparent
	 */
	public void close(boolean focusOnParent) {
		if (focusOnParent) {
			setOnLoad("if(window.opener && window.opener.focus){ window.opener.focus(); } window.close()");
		}
		else {
			close();
		}
	}

	/**
	 * Sets the window to maintain focus when it is blurred
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
	 * Sets the parent (caller) window to reload on Unload
	 */
	public void setParentToReload() {
		setOnUnLoad("window.opener.location.reload()");
	}

	public void setParentToReloadWithURL(String url) {
		setOnUnLoad("window.opener.location.href='" + url + "'");
	}

	/**
	 * Sets the parent (caller) window to submit its first form when this page unloads if there is one
	 */
	public void setParentPageMainFormToSubmitOnUnLoad() {
		setParentPageFormToSubmitOnUnLoad(0);
	}

	/**
	 * Sets the parent (caller) window to submit its form at index formIndex if there is one, on unload of this page.
	 * 
	 * @param formIndex
	 *          index of the form in the parent page
	 */
	public void setParentPageFormToSubmitOnUnLoad(int formIndex) {
		setOnUnLoad("window.opener.document.forms[" + formIndex + "].submit()");
	}

	/**
	 * Sets the parent (caller) window to submit the form with the given name if there is one, on unload of this page.
	 * 
	 * @param formIndex
	 *          index of the form in the parent page
	 */
	public void setParentPageFormToSubmitOnUnLoad(String formName) {
		setOnUnLoad("javascript:window.opener.document.getElementById('" + formName + "').submit()");
	}

	/**
	 * Sets the parent (caller) page to change location (URL) when this page unloads
	 * 
	 * @param URL
	 *          The new toRedirect value
	 */
	public void setParentToRedirect(String URL) {
		setOnUnLoad("javascript:window.opener.location = '" + URL + "';");
	}

	/**
	 * Displays an alert on load of this page.<br>
	 * 
	 * @author aron@idega.is
	 * @param sMessage
	 *          The new toLoadAlert value
	 */
	public void setToLoadAlert(String sMessage) {
		setOnLoad("alert('" + sMessage + "')");
	}

	/**
	 * @param iwc
	 *          Description of the Parameter
	 * @return Description of the Return Value
	 */
	@Override
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

	/**
	 * Sets the defaultAttributes attribute of the Page object
	 * 
	 * @param iwc
	 *          The new defaultAttributes value
	 */
	private void setDefaultAttributes(IWContext iwc) {
	}

	/**
	 */
	public void setToReload() {
		this._doReload = true;
	}

	/**
	 * Sets the addBody attribute of the Page object
	 * 
	 * @param addBodyTag
	 *          The new addBody value
	 */
	public void setAddBody(boolean addBodyTag) {
		this._addBody = addBodyTag;
	}

	/**
	 * @param URL
	 *          The new toRedirect value
	 */
	public void setToRedirect(String URL) {
		this._zeroWait = true;
		setToRedirect(URL, 0);
	}

	/**
	 * @param URL
	 *          The new toRedirect value
	 * @param secondInterval
	 *          The new toRedirect value
	 */
	public void setToRedirect(String URL, int secondInterval) {
		this._redirectInfo = "" + secondInterval + " ;URL=" + URL;
		this._redirectSecondInterval = secondInterval;
		this._redirectURL = URL;
	}

	/**
	 * @return The redirectInfo value
	 */
	public String getRedirectInfo() {
		return this._redirectInfo;
	}

	public void setToForwardToPage(ICPage page) {
		this.forwardPage = page;
	}

	/**
	 * @param milliseconds
	 *          The new toClose value
	 */
	public void setToClose(int milliseconds) {
		getAssociatedScript().addFunction("close_time", "setTimeout(\"window.close()\"," + milliseconds + ")");
	}

	/**
	 * Description of the Method
	 * 
	 * @param newObjToCreate
	 *          Description of the Parameter
	 */
	@Override
	protected void prepareClone(PresentationObject newObjToCreate) {
		super.prepareClone(newObjToCreate);
		Page newPage = (Page) newObjToCreate;
		newPage._title = this._title;
		newPage._zeroWait = this._zeroWait;
		newPage._redirectInfo = this._redirectInfo;
		newPage._doReload = this._doReload;
		newPage._linkColor = this._linkColor;
		newPage._visitedColor = this._visitedColor;
		newPage._hoverColor = this._hoverColor;
	}

	/**
	 * Description of the Method
	 * 
	 * @param iwc
	 *          Description of the Parameter
	 * @param askForPermission
	 *          Description of the Parameter
	 * @return Description of the Return Value
	 */
	@Override
	public Object clonePermissionChecked(IWUserContext iwuc, boolean askForPermission) {
		if (askForPermission) {
			if (iwuc.getApplicationContext().getIWMainApplication().getAccessController().hasViewPermission(this, iwuc)) {
				return this.clone(iwuc, askForPermission);
			}
			else {
				if (!NULL_CLONE_PAGE_INITIALIZED) {
					try {
						IWContext iwc = IWContext.getInstance();
						Image noPermissionImage = getBundle(iwc).getImage("shared/stopalert.gif");
						NULL_CLONE_PAGE.add(noPermissionImage);
						if (iwc != null) {
							BuilderService bservice = getBuilderService(iwc);
							int pageId = 1;
							String page = null;
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
					catch (Exception e) {
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
	 * @param iwc
	 *          Description of the Parameter
	 * @param askForPermission
	 *          Description of the Parameter
	 * @return Description of the Return Value
	 */
	@Override
	public Object clone(IWUserContext iwc, boolean askForPermission) {
		Page obj = null;
		try {
			obj = (Page) super.clone(iwc, askForPermission);

			obj._title = this._title;
			obj._zeroWait = this._zeroWait;
			obj._redirectInfo = this._redirectInfo;
			obj._doReload = this._doReload;
			obj._linkColor = this._linkColor;
			obj._visitedColor = this._visitedColor;
			obj._hoverColor = this._hoverColor;
			obj._textDecoration = this._textDecoration;
			obj._addStyleSheet = this._addStyleSheet;
			obj._ibPageID = this._ibPageID;
			obj.styleFile = this.styleFile;
			if (this._javascripts != null) {
				obj._javascripts = this._javascripts;
			}
			if (this._styleSheets != null) {
				obj._styleSheets = this._styleSheets;
			}
			if (this._styleDefinitions != null) {
				obj._styleDefinitions = this._styleDefinitions;
			}
			if (this.dynamicPageTrigger != null) {
				obj.dynamicPageTrigger = (ICDynamicPageTrigger) this.dynamicPageTrigger.clone();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	/**
	 * @param iwc
	 *          Description of the Parameter
	 * @exception Exception
	 *              Description of the Exception
	 */
	@Override
	public void main(IWContext iwc) throws Exception {
		if (this.forwardPage != null) {
			iwc.forwardToIBPage(this, this.forwardPage);
		}
		if (this._doReload) {
			if (iwc.getSession().getAttribute("idega_special_reload") != null) {
				iwc.getSession().removeAttribute("idega_special_reload");
			}
			else {
				setToRedirect(iwc.getRequestURI());
				iwc.getSession().setAttribute("idega_special_reload", "true");
			}
		}
		/* get the files cached url */
		if (this.styleFile != null) {
			ICFileSystem fsystem = getICFileSystem(iwc);
			String styleSheetURL = fsystem.getFileURI(((Integer) this.styleFile.getPrimaryKey()).intValue());
			setStyleSheetURL(styleSheetURL);
		}
	}

	/*
	 * 
	 */
	/**
	 * Gets the childOfOtherPage attribute of the Page object
	 * 
	 * @return The childOfOtherPage value
	 */
	protected boolean isChildOfOtherPage() {
		UIComponent parent = getParent();
		if (parent != null) {
			if (parent instanceof Page) {
				if (parent instanceof FrameSet) {
					return false;
				}
				else {
					return true;
				}
			}
			else if (parent instanceof UIViewRoot) {
				return false;
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
		UIComponent parent = getParent();
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
	 * 
	 * @param iwc
	 * @return
	 */
	protected String getHeadContents(IWContext iwc) {
		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		String characterEncoding = settings.getCharacterEncoding();
		String markup = iwc.getApplicationSettings().getDefaultMarkupLanguage();
		return getHeadContents(markup, characterEncoding, iwc);
	}

	/**
	 * <code>Gets the contents inside the Head <head> </head> tags with the exception of the title and
	 * the "associated script.</code>
	 * 
	 * @param iwc
	 * @return
	 */
	protected String getHeadContents(String markup, String characterEncoding, IWContext iwc) {
		StringBuffer buf = new StringBuffer();
		buf.append(getPrintableSchortCutIconURL(iwc));
		if (getUseIE7Extension()) {
			buf.append(getIE7());
		}
		buf.append(getMetaInformation(markup, characterEncoding));
		buf.append(getMetaTags(markup));
		buf.append(getJavaScriptBeforeJavascriptURLs(iwc));
		buf.append(getJavascriptURLs(iwc));
		buf.append(getJavaScriptAfterJavascriptURLs(iwc));
		buf.append(getStyleSheetURL(markup, iwc));
		buf.append(getStyleDefinition());
		
		includeJavaScriptActions(iwc);
		
		return buf.toString();
	}

	/**
	 * <code>Adds the script string to the <head> of the page before javascript.js files are loaded, the added string are printed in the same order as they come in</code>
	 * 
	 * @param script
	 */
	public void addJavaScriptBeforeJavaScriptURLs(String keyInMap, String script) {
		if (this._javascriptStringsBeforeJSUrls == null) {
			this._javascriptStringsBeforeJSUrls = new QueueMap<String, JavaScriptLink>();
		}
		
		addScript(this._javascriptStringsBeforeJSUrls, keyInMap, script);
	}

	/**
	 * <code>Adds the script string to the <head> of the page after javascript.js files are loaded, the added string are printed in the same order as they come in</code>
	 * 
	 * @param script
	 */
	public void addJavaScriptAfterJavaScriptURLs(String keyInMap, String script) {
		if (this._javascriptStringsAfterJSUrls == null) {
			this._javascriptStringsAfterJSUrls = new QueueMap<String, JavaScriptLink>();
		}
		
		addScript(this._javascriptStringsAfterJSUrls, keyInMap, script);
	}

	private void addScript(QueueMap<String, JavaScriptLink> scripts, String key, String script) {
		JavaScriptLink scriptAction = scripts.get(key);
		if (scriptAction == null) {
			scriptAction = new JavaScriptLink();
		}
		scriptAction.addAction(script);
		
		scripts.put(key, scriptAction);
	}
	
	public void removeJavaScriptFromJavascriptBeforeJavaScriptsUrlsMap(String key) {
		if (this._javascriptStringsBeforeJSUrls != null) {
			this._javascriptStringsBeforeJSUrls.remove(key);
		}
	}

	public void removeJavaScriptFromJavascriptAfterJavaScriptsUrlsMap(String key) {
		if (this._javascriptStringsAfterJSUrls != null) {
			this._javascriptStringsAfterJSUrls.remove(key);
		}
	}

	/**
	 * Gets a block of free form javascript (just strings) to insert BEFORE importing some javascript.js files
	 * 
	 * @param iwc
	 * @return a javascript block
	 */
	private String getJavaScriptBeforeJavascriptURLs(IWContext iwc) {
		return getScriptActions(this._javascriptStringsBeforeJSUrls);
	}

	private String getScriptActions(QueueMap<String, JavaScriptLink> scripts) {
		if (scripts == null || scripts.isEmpty()) {
			return CoreConstants.EMPTY;
		}
		
		StringBuffer buffer = new StringBuffer();
		for (JavaScriptLink script: scripts.values()) {
			buffer.append("<script type=\"text/javascript\">\n");
			for (String action: script.getActions()) {
				buffer.append(action).append("\n");
			}
			buffer.append("</script>\n");
		}
		
		return buffer.toString();
	}
	
	/**
	 * Gets a block of free form javascript (just strings) to insert AFTER importing some javascript.js files
	 * 
	 * @param iwc
	 * @return a javascript block
	 */
	private String getJavaScriptAfterJavascriptURLs(IWContext iwc) {
		return getScriptActions(this._javascriptStringsAfterJSUrls);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#initVariables(com.idega.presentation.IWContext)
	 */
	@Override
	public void initVariables(IWContext iwc) throws IOException {
		super.initVariables(iwc);
		setDefaultValues();
		setDefaultAttributes(iwc);
	}
	
	protected void addSessionPollingDWRFiles(IWContext iwc) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(IWContext.getInstance());
		IWMainApplicationSettings applicationSettings = iwma.getSettings();

		if (applicationSettings.getIfUseSessionPolling()) {
			PresentationUtil.addJavaScriptActionToBody(iwc, new StringBuilder("registerEvent(window, 'load', function() {IWCORE.activeSessionPolling(")
				.append(applicationSettings.getProperty("iw.core.polling_interval", "1200000")).append(", true);});").toString());
		}
	}

	/**
	 * @param iwc
	 *          Description of the Parameter
	 * @exception Exception
	 *              Description of the Exception
	 */
	@Override
	public void print(IWContext iwc) throws Exception {
		this.printBegin(iwc);
		// Catch all exceptions that are thrown in print functions of objects
		// stored inside
		try {
			super.print(iwc);
		}
		catch (Exception ex) {
			println("<h1>An Error Occurred!</h1>");
			println("IW Error");
			println("<pre>");
			String message = ex.getMessage();
			if (message != null) {
				println(message);
			}
			ex.printStackTrace(System.err);
			println("</pre>");
		}
		this.printEnd(iwc);
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		callMain(context);
		this.printBegin(IWContext.getIWContext(context));
	}

	/**
	 * Bridging method for JSF:
	 * 
	 * @throws Exception
	 */
	public void printBegin(IWContext iwc) throws IOException {
		this.initVariables(iwc);
		boolean isInsideOtherPage = this.isChildOfOtherPage();
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			if (!isInsideOtherPage) {
				IWMainApplicationSettings settings = iwc.getApplicationSettings();
				String characterEncoding = settings.getCharacterEncoding();
				String markup = getMarkupLanguageForPage();
				String docType = getDocType();
				println(getStartTag(iwc.getCurrentLocale(), docType, characterEncoding));
				if (this._zeroWait) {
					setDoPrint(false);
				}
				println("<head>");
				println("<title>" + getLocalizedTitle(iwc) + "</title>\n");
				/*
				 * //shortcut icon println(getPrintableSchortCutIconURL(iwc)); print(getMetaInformation(markup, characterEncoding));
				 * print(getMetaTags(markup)); print(getJavascriptURLs(iwc)); if (getAssociatedScript() != null) { getAssociatedScript()._print(iwc); }
				 * print(getStyleSheetURL(markup)); print(getStyleDefinition());
				 */
				print(getHeadContents(markup, characterEncoding, iwc));
				if (getAssociatedScript() != null) {
					// getAssociatedScript()._print(iwc);
					UIComponent script = getAssociatedScript();
					this.renderChild(iwc, script);
				}
				// Laddi: Made obsolete with default style sheet
				/*
				 * if (_addStyleSheet) { println("<link rel=\"stylesheet\" href=\"" + _styleSheetURL + "\" type=\"text/css\">\n"); }
				 */
				println("\n</head>");
				if (this._addBody) {
					println("<body " + getMarkupAttributesString() + ">");
					if (!getAssociatedBodyScript().isEmpty()) {
						// getAssociatedBodyScript()._print(iwc);
						UIComponent script = getAssociatedBodyScript();
						this.renderChild(iwc, script);
					}
				}
				// added by Eiki for frameSet in a page support
			}
		}
		else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			println("<?xml version=\"1.0\"?>");
			if (true) {
				println("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">");
			}
			else {
				println("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.3//EN\" \"http://www.wapforum.org/DTD/wml13.dtd\">");
			}
			println("<wml>");
			println("<head>");
			println("<meta http-equiv=\"cache-control\" content=\"no-cache\"/>");
			println("</head>");
			print("<card title=\"" + getLocalizedTitle(iwc) + "\"");
			if (this._redirectSecondInterval > -1) {
				print(" ontimer=\"" + this._redirectURL + "\"");
				println(" id=\"card1\">");
				println("<timer value=\"" + this._redirectSecondInterval * 10 + "\"/>");
			}
			else {
				println(" id=\"card1\">");
			}
		}
		else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)) {
			println("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
			// println("<!DOCTYPE ITEXT SYSTEM
			// \"http://www.lowagie.com/iText/itext.dtd\">");
			println("<itext producer=\"Idega Software, http://www.idega.com\">");
		}
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		List<UIComponent> children = getChildren();
		// This is a temporary workaround, because of iterator
		// NoSuchElementException problem (iterator should be used when it
		// starts working)
		try {
			for (UIComponent child: children) {
				renderChild(context, child);
			}
		}
		catch (NotLoggedOnException noex) {
			// TODO: Change this, this is a workaround till a better not logged
			// on error page is created:
			IWContext iwc = castToIWContext(context);
			String notLoggedOnString = getResourceBundle(iwc).getLocalizedString("error_not_logged_on", "You are not logged on, please go to login page and log in.");
			println("<h2>" + notLoggedOnString + "</h2>");
		}
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		this.printEnd(IWContext.getIWContext(context));
		resetGoneThroughMain();
		encodeRenderTime(context);
	}

	/**
	 * <p>
	 * Prints out the render time in millisconds as a comment. This is by default called last in encodeEnd()
	 * </p>
	 * 
	 * @param context
	 * @throws IOException
	 */
	protected void encodeRenderTime(FacesContext context) throws IOException {
		long time = FacesUtil.registerRequestEnd(context);
		String renderingText = time + " ms";
		context.getResponseWriter().writeComment(renderingText);
	}

	/**
	 * Bridging method for JSF:
	 */
	public void printEnd(IWContext iwc) throws IOException {
		boolean isInsideOtherPage = this.isChildOfOtherPage();
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			if (!isInsideOtherPage) {
				if (this._addBody) {
					println("\n\n</body>");
				}
				println(getEndTag());
			}
		}
		else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			println("</card>");
			println("</wml>");
		}
		else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)) {
			println("</itext>");
		}
	}

	/**
	 * @param key
	 *          The new property value
	 * @param values
	 *          The new property value
	 */
	@Override
	public void setProperty(String key, String values[]) {
		if (key.equalsIgnoreCase("title")) {
			setTitle(values[0]);
		}
	}

	/**
	 * @return The startTag value
	 */
	public String getStartTag(Locale locale, String docType, String encoding) {
		StringBuffer buffer = new StringBuffer();
		if (isUseHtmlTag()) {
			if (docType.equals(DOCTYPE_XHTML_1_0_TRANSITIONAL)) {
				buffer.append("<?xml version=\"1.0\" encoding=\"").append(encoding != null ? encoding : "ISO-8859-1").append("\"?>").append("\n");
				buffer.append(docType);
				buffer.append(NEWLINE);
				
				buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"");
				buffer.append(locale.getLanguage());
				buffer.append("\" lang=\"");
				buffer.append(locale.getLanguage());
				buffer.append("\">");
				
				return buffer.toString();
			}
			else if (docType.equals(DOCTYPE_XHTML_1_1)) {
				buffer.append("<?xml version=\"1.0\" encoding=\"").append(encoding != null ? encoding : "ISO-8859-1").append("\"?>").append("\n");
				buffer.append(docType);
				buffer.append(NEWLINE);
				buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"");
				buffer.append(locale.getLanguage());
				buffer.append("\">");
				return buffer.toString();
			}
			else {
				buffer.append(docType);
				buffer.append(NEWLINE);
				buffer.append(START_TAG_HTML_4_0);
				buffer.append(NEWLINE);
				return buffer.toString();
			}
		}
		return CoreConstants.EMPTY;
		/*
		 * if (markup.equals(XHTML)) { StringBuffer buffer = new StringBuffer(); buffer.append("<?xml version=\"1.0\" encoding=\"").append(encoding !=
		 * null ? encoding : "ISO-8859-1").append("\"?>").append("\n"); //buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0
		 * Transitional//EN\"").append("\n"); //buffer.append("\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">").append("\n");
		 * buffer.append(DOCTYPE_XHTML_1_0_TRANSITIONAL); buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"
		 * xml:lang=\""+locale.getLanguage()+"\" lang=\""+locale.getLanguage()+"\">"); return buffer.toString(); } else if (markup.equals(XHTML1_1)) {
		 * StringBuffer buffer = new StringBuffer(); buffer.append("<?xml version=\"1.0\" encoding=\"").append(encoding != null ? encoding :
		 * "ISO-8859-1").append("\"?>").append("\n"); //buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"").append("\n");
		 * //buffer.append("\t\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">").append("\n"); buffer.append(DOCTYPE_XHTML_1_1); buffer.append("<html
		 * xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\""+locale.getLanguage()+"\">"); return buffer.toString(); } return START_TAG;
		 */
	}

	/**
	 * @return The endTag value
	 */
	public String getEndTag() {
		if (isUseHtmlTag()) {
			return END_TAG;
		}
		return CoreConstants.EMPTY;
	}

	private boolean isUseHtmlTag() {
		return useHtmlTag;
	}

	public void setUseHtmlTag(boolean useHtmlTag) {
		this.useHtmlTag = useHtmlTag;
	}

	/**
	 * @param iwc
	 *          Description of the Parameter
	 * @return The metaInformation value
	 */
	public String getMetaInformation(String markup, String characterEncoding) {
		boolean addIdegaAuthorAndCopyRight = false;
		String theReturn = "<meta http-equiv=\"content-type\" content=\"text/html; charset=" + characterEncoding + "\" " + (!markup.equals(HTML) ? "/" : "") + ">\n<meta name=\"generator\" content=\"idegaWeb " + IWContext.getInstance().getIWMainApplication().getProductInfo().getVersion() + "\" " + (!markup.equals(HTML) ? "/" : "") + ">\n";
		// If the user is logged on then there is no caching by proxy servers
		boolean notUseProxyCaching = true;
		if (notUseProxyCaching) {
			theReturn += "<meta http-equiv=\"pragma\" content=\"no-cache\" " + (!markup.equals(HTML) ? "/" : "") + ">\n";
		}
		if (getRedirectInfo() != null) {
			theReturn += "<meta http-equiv=\"refresh\" content=\"" + getRedirectInfo() + "\" " + (!markup.equals(HTML) ? "/" : "") + ">\n";
		}
		if (addIdegaAuthorAndCopyRight) {
			theReturn += "<meta name=\"author\" content=\"idega.is\"/>\n<meta name=\"copyright\" content=\"idega.is\" " + (!markup.equals(HTML) ? "/" : "") + ">\n";
		}
		return theReturn;
	}

	/**
	 * Used to find the Page object to be printed in top of the current page
	 * 
	 * @param iwc
	 *          Description of the Parameter
	 * @return The page value
	 */
	public static Page getPage(IWContext iwc) {
		Page page = (Page) IWCoreServlet.retrieveObject(IW_PAGE_KEY);
		return page;
	}

	/**
	 * @param iwc
	 *          Description of the Parameter
	 * @return Description of the Return Value
	 * @exception Exception
	 *              Description of the Exception
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
				return pg;
			}
			else {
				Page defaultPage = new Page();
				Logger.getLogger(Page.class.getName()).warning("Frame " + frameNameKey + ": page is null");
				return defaultPage;
			}
		}
		else if (frameKey != null) {
			Page page = getPage(getFrameStorageInfo(iwc), iwc);
			Logger.getLogger(Page.class.getName()).info("Trying to get page stored in session");
			return page;
		}
		else if (classKey != null) {
			String className = IWMainApplication.decryptClassName(classKey);
			Page page = null;
			try {
				page = (Page) RefactorClassRegistry.forName(className).newInstance();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new IWPageInitializationException("There was an error, your session is probably expired");
			}
			String sID = iwc.getParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID);
			try {
				if (sID != null) {
					Logger.getLogger(Page.class.getName()).warning("sID: " + sID);
					page.setICObjectInstanceID(Integer.parseInt(sID));
					Logger.getLogger(Page.class.getName()).warning("Integer.parseInt(sID): " + Integer.parseInt(sID));
					Logger.getLogger(Page.class.getName()).warning("getICObjectInstanceID: " + page.getICObjectInstanceID());
				}
			}
			catch (NumberFormatException ex) {
				Logger.getLogger(Page.class.getName()).warning(page + ": cannot init ic_object_instance_id");
			}
			return page;
		}
		else {
			return new Page();
		}
	}

	/*
	 * 
	 */
	/**
	 * Gets the frameStorageInfo attribute of the Page class
	 * 
	 * @param iwc
	 *          Description of the Parameter
	 * @return The frameStorageInfo value
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
	 * Gets the page attribute of the Page class
	 * 
	 * @param info
	 *          Description of the Parameter
	 * @param iwc
	 *          Description of the Parameter
	 * @return The page value
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
	 * @param page
	 *          Description of the Parameter
	 * @param iwc
	 *          Description of the Parameter
	 */
	public static void storePage(Page page, IWContext iwc) {
		String storageKey = page.getID();
		String infoKey = storageKey;
		FrameStorageInfo info = new FrameStorageInfo(storageKey, page.getClass());
		iwc.setSessionAttribute(infoKey, info);
		iwc.setSessionAttributeWeak(storageKey, page);
	}

	/**
	 * @param page
	 *          The new topPage value
	 */
	public static void setTopPage(Page page) {
		IWCoreServlet.storeObject(IW_PAGE_KEY, page);
	}

	/**
	 * @param iwc
	 *          Description of the Parameter
	 * @return The requestingTopPage value
	 */
	public static boolean isRequestingTopPage(IWContext iwc) {
		return !iwc.isParameterSet(IW_FRAME_STORAGE_PARMETER);
	}

	/**
	 * Sets the ID (BuilderPage ID)
	 * 
	 * @param id
	 *          The new pageID value
	 */
	public void setPageID(int id) {
		this._ibPageID = id;
	}

	/**
	 * method for adding a style sheet file the url generating is done in the main method
	 * 
	 * @param file
	 *          The new styleSheet value
	 */
	public void setStyleSheet(ICFile file) {
		this.styleFile = file;
	}

	/**
	 * Returns set the (BuilderPage) ID set to this page or -1 if not a builder page
	 * 
	 * @return The pageID value
	 */
	public int getPageID() {
		return this._ibPageID;
	}

	/**
	 * Sets this page to be a template page
	 */
	public void setIsTemplate() {
		this._isTemplate = true;
		this._isPage = false;
		this._isDraft = false;
	}

	/**
	 * Sets this page to be a "normal" page
	 */
	public void setIsPage() {
		this._isTemplate = false;
		this._isPage = true;
		this._isDraft = false;
	}

	/**
	 * Sets this page to be a draft
	 */
	public void setIsDraft() {
		this._isTemplate = false;
		this._isPage = false;
		this._isDraft = true;
	}

	/**
	 * @return The isTemplate value
	 */
	public boolean getIsTemplate() {
		return (this._isTemplate);
	}

	/**
	 * @return The isPage value
	 */
	public boolean getIsPage() {
		return (this._isPage);
	}

	/**
	 * @return The isDraft value
	 */
	public boolean getIsDraft() {
		return (this._isDraft);
	}

	/**
	 */
	public void setIsExtendingTemplate() {
		this._isExtendingTemplate = true;
	}

	/**
	 * @return The isExtendingTemplate value
	 */
	public boolean getIsExtendingTemplate() {
		return (this._isExtendingTemplate);
	}

	/**
	 * Sets the windowToOpenOnLoad attribute of the Page object
	 * 
	 * @param link
	 *          The new windowToOpenOnLoad value
	 * @param iwc
	 *          The new windowToOpenOnLoad value
	 */
	public void setWindowToOpenOnLoad(Link link, IWContext iwc) {
		this.setOnLoad(link.getWindowToOpenCallingScript(iwc));
	}

	public void setWindowToOpenOnLoad(ICPage page) {
		setWindowToOpenOnLoad(page, 800, 600);
	}

	public void setWindowToOpenOnLoad(ICPage page, int width, int height) {
		URLUtil url = new URLUtil(getIWApplicationContext(), page);
		setOnLoad("javascript:" + Window.getWindowCallingScript(url.toString(), "Window", false, false, false, false, false, true, true, true, false, width, height));
	}

	/**
	 * Sets the templateId attribute of the Page object
	 * 
	 * @param id
	 *          The new templateId value
	 */
	@Override
	public void setTemplateId(String id) {
		this._templateId = id;
	}

	/**
	 * Gets the templateId attribute of the Page object
	 * 
	 * @return The templateId value
	 */
	@Override
	public String getTemplateId() {
		return (this._templateId);
	}

	/**
	 * Used to add source of scriptfiles (JavaScript) The file url should end on the form "scriptfile.js"
	 * 
	 * @param jsString
	 *          The feature to be added to the ScriptSource attribute
	 */
	public void addScriptSource(String jsString) {
		getAssociatedScript().addScriptSource(jsString);
	}

	/**
	 * Gets the file id of the shortcut icon
	 * 
	 * @return the shortcut icon file id
	 */
	public int getShortCutIconID() {
		return this._shortCutIconID;
	}

	/**
	 * Gets the URL of the shortcut icon
	 * 
	 * @return URL to shortcut icon
	 */
	public String getShortCutIconURL() {
		return this._shortCutIconURL;
	}

	/**
	 * Sets the file id of the shortcut icon
	 * 
	 * @param id
	 *          of the icon file
	 */
	public void setShortCutIconID(int id) {
		this._shortCutIconID = id;
	}

	/**
	 * Sets the URL to the shortcut icon
	 * 
	 * @param url
	 *          to the icon file
	 */
	public void setShortCutIconURL(String url) {
		this._shortCutIconURL = url;
	}

	private String getPrintableSchortCutIconURL(IWContext iwc) {
		String url = null;
		if (getShortCutIconID() > 0) {
			ICFileSystem fsystem;
			try {
				fsystem = getICFileSystem(iwc);
				url = fsystem.getFileURI(getShortCutIconID());
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else if (getShortCutIconURL() != null) {
			url = getShortCutIconURL();
		}
		if (url != null) {
			return "<link type=\"shortcut icon\" href=\"" + url + "\" />";
		}
		return "";
	}

	public ICDynamicPageTrigger getDynamicPageTrigger() {
		if (this.dynamicPageTrigger == null) {
			this.dynamicPageTrigger = (ICDynamicPageTrigger) ImplementorRepository.getInstance().newInstanceOrNull(ICDynamicPageTrigger.class, this.getClass());
			if (this.dynamicPageTrigger == null) {
				throw new RuntimeException("[Page] Implementation of ICDynamicPageTrigger could not be found. Implementing bundle was not loaded.");
			}
		}
		return this.dynamicPageTrigger;
	}

	/**
	 * Returns the associatedBodyScript.
	 * 
	 * @return Script
	 */
	public Script getAssociatedBodyScript() {
		Script associatedBodyScript = (Script) getFacets().get("page_associated_body_script");
		if (associatedBodyScript == null) {
			associatedBodyScript = new Script();
			setAssociatedBodyScript(associatedBodyScript);
		}
		return associatedBodyScript;
	}

	/**
	 * Sets the associatedScript.
	 * 
	 * @param associatedScript
	 *          The associatedScript to set
	 */
	public void setAssociatedBodyScript(Script script) {
		getFacets().put("page_associated_body_script", script);
	}

	/**
	 * Set the docType for the header of the page. Default it is set to Html 4.0.1. transitional. Most commonn doctypes are defined in the static
	 * contsants DOCTYPE_... in this class.
	 * 
	 * @param docType
	 */
	public void setDoctype(String docType) {
		this.docType = docType;
	}

	/**
	 * Get the set docType. If no doctype/markupLanguage is set in the page/system then this method returns the HTML 4.0.1 Transitional.
	 * 
	 * @return
	 */
	public String getDocType() {
		if (this.docType == null) {
			String markup = getSetApplicationMarkupLanguage();
			if (markup.equals(XHTML)) {
				return DOCTYPE_XHTML_1_0_TRANSITIONAL;
			}
			else if (markup.equals(XHTML1_1)) {
				return DOCTYPE_XHTML_1_1;
			}
			else {
				return DOCTYPE_HTML_4_0_1_TRANSITIONAL;
			}
		}
		else {
			return this.docType;
		}
	}

	/**
	 * Checks if an XHTML doctype is defined for the page or the system.
	 * 
	 * @return True if an XHTML doctype has been set for the document or XHTML markup for the application.
	 */
	public boolean isXHtmlDocTypeDeclared() {
		String docType = getDocType();
		if (docType.equals(DOCTYPE_XHTML_1_0_TRANSITIONAL)) {
			return true;
		}
		else if (docType.equals(DOCTYPE_XHTML_1_1)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets if the Markup Language for the Page. This method uses the set DocType (if any) to calculate the used MarkupLanguage String.
	 * 
	 * @return
	 */
	public String getMarkupLanguageForPage() {
		if (this.docType != null) {
			if (this.docType.equals(DOCTYPE_XHTML_1_0_TRANSITIONAL)) {
				return XHTML;
			}
			else if (this.docType.equals(DOCTYPE_XHTML_1_1)) {
				return XHTML1_1;
			}
		}
		return getSetApplicationMarkupLanguage();
	}

	/**
	 * Add javascript urls to page HEAD, comma separated
	 * 
	 * @param urls
	 */
	public void setJavascriptURLs(String urls) {
		if (StringUtil.isEmpty(urls)) {
			return;
		}
		
		String sources = Property.getValueFromExpression(urls, String.class);
		if (!StringUtil.isEmpty(sources)) {
			urls = sources;
		}
		
		int index = urls.indexOf(CoreConstants.COMMA);
		while (index > -1) {
			String tmp = urls.substring(0, index);
			addJavascriptURL(tmp.trim());
			urls = urls.substring(index + 1);
			index = urls.indexOf(CoreConstants.COMMA);
		}
		addJavascriptURL(urls.trim());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this._ibPageID = ((Integer) values[1]).intValue();
		this._title = (String) values[2];
		this._zeroWait = ((Boolean) values[3]).booleanValue();
		this._redirectSecondInterval = ((Integer) values[4]).intValue();
		this._redirectURL = (String) values[5];
		this._redirectInfo = (String) values[6];
		this._redirectURL = (String) values[7];
		this._doReload = ((Boolean) values[8]).booleanValue();
		this._linkColor = (String) values[9];
		this._visitedColor = (String) values[10];
		this._hoverColor = (String) values[11];
		this._textDecoration = (String) values[12];
		this._hoverDecoration = (String) values[13];
		this._pageStyleFont = (String) values[14];
		this._pageStyleFontSize = (String) values[15];
		this._pageStyleFontStyle = (String) values[16];
		this._shortCutIconURL = (String) values[17];
		this._shortCutIconID = ((Integer) values[18]).intValue();
		this._addStyleSheet = ((Boolean) values[19]).booleanValue();
		this._addBody = ((Boolean) values[20]).booleanValue();
		this._isTemplate = ((Boolean) values[21]).booleanValue();
		this._isPage = ((Boolean) values[22]).booleanValue();
		this._isDraft = ((Boolean) values[23]).booleanValue();
		this._isExtendingTemplate = ((Boolean) values[24]).booleanValue();
		this._templateId = (String) values[25];
		this._styleDefinitions = (Map<String, String>) values[26];
		this._metaTags = (Map<String, String>) values[27];
		this._styleSheets = (QueueMap<String, StyleSheetLink>) values[28];
		this._javascripts = (QueueMap<String, JavaScriptLink>) values[29];
		this._javascriptStringsBeforeJSUrls = (QueueMap<String, JavaScriptLink>) values[30];
		this._javascriptStringsAfterJSUrls = (QueueMap<String, JavaScriptLink>) values[31];
		this._HTTPEquivs = (Map<String, String>) values[32];
		this.addGlobalScript = ((Boolean) values[33]).booleanValue();
		this.styleFile = (ICFile) values[34];
		this._isCategory = ((Boolean) values[35]).booleanValue();
		this.forwardPage = (ICPage) values[36];
		this.docType = (String) values[37];
		this.useIE7Extension = ((Boolean) values[38]).booleanValue();
		this.javaScriptActions = (QueueMap<String, JavaScriptLink>) values[39];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[40];
		values[0] = super.saveState(context);
		values[1] = new Integer(this._ibPageID);
		values[2] = this._title;
		values[3] = Boolean.valueOf(this._zeroWait);
		values[4] = new Integer(this._redirectSecondInterval);
		values[5] = this._redirectURL;
		values[6] = this._redirectInfo;
		values[7] = this._redirectURL;
		values[8] = Boolean.valueOf(this._doReload);
		values[9] = this._linkColor;
		values[10] = this._visitedColor;
		values[11] = this._hoverColor;
		values[12] = this._textDecoration;
		values[13] = this._hoverDecoration;
		values[14] = this._pageStyleFont;
		values[15] = this._pageStyleFontSize;
		values[16] = this._pageStyleFontStyle;
		values[17] = this._shortCutIconURL;
		values[18] = new Integer(this._shortCutIconID);
		values[19] = Boolean.valueOf(this._addStyleSheet);
		values[20] = Boolean.valueOf(this._addBody);
		values[21] = Boolean.valueOf(this._isTemplate);
		values[22] = Boolean.valueOf(this._isPage);
		values[23] = Boolean.valueOf(this._isDraft);
		values[24] = Boolean.valueOf(this._isExtendingTemplate);
		values[25] = this._templateId;
		values[26] = this._styleDefinitions;
		values[27] = this._metaTags;
		values[28] = this._styleSheets;
		values[29] = this._javascripts;
		values[30] = this._javascriptStringsBeforeJSUrls;
		values[31] = this._javascriptStringsAfterJSUrls;
		values[32] = this._HTTPEquivs;
		values[33] = Boolean.valueOf(this.addGlobalScript);
		values[34] = this.styleFile;
		values[35] = Boolean.valueOf(this._isCategory);
		values[36] = this.forwardPage;
		values[37] = this.docType;
		values[38] = Boolean.valueOf(this.useIE7Extension);
		values[39] = this.javaScriptActions;
		
		return values;
	}

	public List<PropertyDescription> getPropertyDescriptions() {
		List<PropertyDescription> list = new ArrayList<PropertyDescription>();
		list.add(new PropertyDescription("method:1:implied:void:setStyleSheetURL:java.lang.String:", "1", File.class.getName(), FileObjectReader.class.getName(),
				false));
		list.add(new PropertyDescription(":method:1:implied:void:setTemplateId:java.lang.String:", "1", ICPage.class.getName(), ICPage.class.getName(), true));
		return list;
	}
	
	protected boolean isPrintScriptSourcesDirectly() {
		return printScriptSourcesDirectly;
	}

	protected void setPrintScriptSourcesDirectly(boolean printScriptSourcesDirectly) {
		this.printScriptSourcesDirectly = printScriptSourcesDirectly;
	}

	
	/**
	 * @param addGlobalScript the addGlobalScript to set
	 */
	public void setAddGlobalScript(boolean addGlobalScript) {
		this.addGlobalScript = addGlobalScript;
	}

	
	/**
	 * @return the hideBuilder
	 */
	public boolean isHideBuilder() {
		return this.hideBuilder;
	}

	
	/**
	 * @param hideBuilder the hideBuilder to set
	 */
	public void setHideBuilder(boolean hideBuilder) {
		this.hideBuilder = hideBuilder;
	}

}