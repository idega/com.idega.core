/*
 * $Id: Link.java,v 1.183 2009/05/30 09:29:48 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.text;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.event.EventLogic;
import com.idega.event.IWLinkEvent;
import com.idega.event.IWLinkListener;
import com.idega.event.IWPresentationEvent;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.Window;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.text.TextSoap;

/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.2
 *@modified by  <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public class Link extends Text {

	//Static variables:
	private static String _sessionStorageName = IWMainApplication.windowOpenerParameter;
	private static final String IB_PAGE_PARAMETER = ICBuilderConstants.IB_PAGE_PARAMETER;
	private static final String PRM_HISTORY_ID = ICBuilderConstants.PRM_HISTORY_ID;
	public static final String HASH = "#";
	public static final String JAVASCRIPT = "javascript:";
	public static final String TARGET_ATTRIBUTE = "target";
	public static final String HREF_ATTRIBUTE = "href";
	protected static final String OBJECT_TYPE_MODULEOBJECT = "PresentationObject";
	protected static final String OBJECT_TYPE_TEXT = "Text";
	protected static final String OBJECT_TYPE_IMAGE = "Image";
	public static final String TARGET_NEW_WINDOW = "_new";
	public static final String TARGET_SELF_WINDOW = "_self";
	public static final String TARGET_BLANK_WINDOW = "_blank";
	public static final String TARGET_PARENT_WINDOW = "_parent";
	public static final String TARGET_TOP_WINDOW = "_top";
	
	//Instance variables:
	private PresentationObject _obj;
	private Window _myWindow = null;
	private boolean iFormToSubmit = false;
	private Class<? extends UIComponent> _windowClass = null;
	private Window _windowInstance = null;
	private int icObjectInstanceIDForWindow = -1;
	protected StringBuffer _parameterString;
	private String _objectType;
	private String windowOpenerJavascriptString = null;
	private boolean isImageButton = false;
	private boolean isImageTab = false;
	private boolean useTextAsLocalizedTextKey = false;
	private boolean flip = true;
	private boolean isOutgoing = false;
	private boolean hasClass = false;
	private boolean _maintainAllGlobalParameters = false;
	private boolean _maintainBuilderParameters = true;
	private boolean _addSessionId = true;
	private boolean _maintainAllParameters = false;
	private int _imageId;
	private String _hostname = null;
	private int _onMouseOverImageId;
	private int _onClickImageId;
	private Image _onMouseOverImage = null;
	private Image _onClickImage = null;
	private boolean usePublicWindow = false;

	//If Link is constructed to open an instance of an object in a new page via ObjectInstanciator
	private Class<? extends UIComponent> classToInstanciate;
	//private Class templatePageClass;
	private String templateForObjectInstanciation;
	private List<Object> listenerInstances = null;
	private List<String> maintainedParameters = null;
	private boolean https = false;
	private String protocol = null;
	private int fileId = -1;
	private String datasource = null;
	private final static String DEFAULT_TEXT_STRING = "No text";
	public static boolean usingEventSystem = false;
	//A BuilderPage to link to:
	private int ibPage=0;
	//todo use the methods in the image object
	private Map<Locale, Object> _overImageLocalizationMap;
	private Map<Locale, Object> _ImageLocalizationMap;
	private Map<Locale, String> _toolTipLocalizationMap;
	private boolean usePublicOjbectInstanciator = false;
	
	private String charEncoding = CoreConstants.ENCODING_UTF8;
	private boolean forceToReplaceAfterEncoding;
	
	/**
	 *
	 */
	public Link() {
		this(DEFAULT_TEXT_STRING);
	}

	/**
	 *
	 */
	public Link(String text) {
		this(new Text(text));
	}

	/**
	 *
	 */
	public Link(PresentationObject mo, Window myWindow) {
		this.setWindow(myWindow);
		this.setPresentationObject(mo);
	}

	/**
	 *
	 */
	public Link(Window myWindow) {
		this(new Text(myWindow.getName()), myWindow);
	}

	/**
	 *
	 */
	public Link(PresentationObject mo) {
		this.setPresentationObject(mo);
	}

	/**
	 *
	 */
	public Link(Text text) {
		this.setText(text);
	}

	/**
	 *
	 */
	public Link(String text, String url) {
		this(new Text(text), url);
	}

	/**
	 *
	 */
	public Link(PresentationObject mo, String url) {
		this.setPresentationObject(mo);
		this.setURL(url);
	}

	/**
	 *
	 */
	public Link(Text text, String url) {
		this.text = text.getText();
		this.setText(text);
		setURL(url);
		this._obj.setParentObject(this);
		this._objectType = OBJECT_TYPE_MODULEOBJECT;
	}

	/**
	 * Construct a link to a file
	 */
	public Link(int icFileId) {
		this("File");
		String uri;
		try	{
			uri = this.getICFileSystem(IWContext.getInstance()).getFileURI(icFileId);
			setURL(uri);
		} catch (RemoteException e)	{
			e.printStackTrace();
		} catch (UnavailableIWContext e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Construct a link to a file with text
	 */
	public Link(int icFileId, String textOnLink) {
		this(icFileId);
		setText(textOnLink);
	}
	
		/**
		 * Construct a link to a file on a presentation object
		 */
	public Link(PresentationObject mo, int icFileId) {
		this(icFileId);
		this._obj = mo;
		this._obj.setParentObject(this);
		this._objectType = OBJECT_TYPE_MODULEOBJECT;
	}

	public Link(PresentationObject mo, Class<? extends UIComponent> classToInstanciate) {
		this.setPresentationObject(mo);
		setClassToInstanciate(classToInstanciate);
	}

	public Link(Class<? extends UIComponent> classToInstanciate) {
		this(Link.DEFAULT_TEXT_STRING);
		this.setClassToInstanciate(classToInstanciate);
	}

	@SuppressWarnings("unchecked")
	public Link(PresentationObject mo, String classToInstanciate, String template) {
		this.setPresentationObject(mo);
		try {
			this.setClassToInstanciate(RefactorClassRegistry.forName(classToInstanciate), template);
		} catch (Exception e) {
			throw new RuntimeException(e.toString() + e.getMessage());
		}
	}

	/**
	 * Opens a new object of type classToInstanciate (has to be a PresentationObject)
	 * in the same window.
	 */
	public Link(String displayText, Class<? extends UIComponent> classToInstanciate) {
		this.setText(displayText);
		this.setClassToInstanciate(classToInstanciate);
	}

	/**
	 * Opens a new object of type classToInstanciate (has to be a PresentationObject)
	 * in the window of target specified by "target"
	 */
	public Link(String displayText, Class<? extends UIComponent> classToInstanciate, String target) {
		this.setText(displayText);
		this.setClassToInstanciate(classToInstanciate);
		setTarget(target);
	}

	/**
	 * Opens a new object of type classToInstanciate (has to be a PresentationObject)
	 * in the same window with the template of name templateName
	 */
	@SuppressWarnings("unchecked")
	public Link(String displayText, String classToInstanciate, String templateName) {
		this.setText(displayText);
		try {
			this.setClassToInstanciate(RefactorClassRegistry.forName(classToInstanciate), templateName);
		} catch (Exception e) {
			throw new RuntimeException(e.toString() + e.getMessage());
		}
	}

	/**
	 *
	 */
	public void setWindow(Window window) {
		this._myWindow = window;
		if (this._obj == null) {
			setText(this._myWindow.getName());
		}
	}
	
	/**
	 * sets the hostname in the links URL
	 * @param hostname the hostname
	 */
	public void setHostname(String hostname) {
		this._hostname = hostname;
	}
	
	/**
	 * 
	 * Gets the hostname of the URL
	 * @return the hostname
	 */
	public String getHostname() {
		return this._hostname;
	}

	/**
	 *
	 */
	public void setPresentationObject(PresentationObject object) {

		if (object instanceof Image) {
			setImage((Image) object);
		}
		else if (object instanceof Text) {
			setText((Text) object);
		}
		else if (object instanceof Window) {
			setWindow((Window) object);
		}
		else {
			this._objectType = OBJECT_TYPE_MODULEOBJECT;
			this._obj = object;
			object.setParentObject(this);
		}
	}

	/**
	 *
	 */
	@Override
	public void main(IWContext iwc) throws Exception {
		if (this.fileId != -1){
			ICFileSystem fsystem = getICFileSystem(iwc);
			String fileURL = datasource != null ? fsystem.getFileURI(this.fileId, datasource) : fsystem.getFileURI(this.fileId);
			setURL(fileURL);
		}
		setURIToClassToInstanciate(iwc);
		setURIToWindowOpenerClass(iwc);
		//Builder edit mode
		if (iwc.isInEditMode()) {
			addParameter("view", "builder"); /**@todo this doesn't update all the frames**/

		}

		if (!isParameterSet(LocaleSwitcher.languageParameterString) && !isMarkupAttributeSet(HREF_ATTRIBUTE)) {
			setLocale(iwc.getCurrentLocale());
		}

		if (this._myWindow != null) {
			String windowOpenerURI = iwc.getIWMainApplication().getWindowOpenerURI();
			if (this._myWindow.getURL(iwc).indexOf(windowOpenerURI) != -1) {
				String sessionParameterName = com.idega.servlet.WindowOpener.storeWindow(iwc, this._myWindow);
				addParameter(_sessionStorageName, sessionParameterName);
			}
		}

		if (this._obj != null) {
			if (this._obj instanceof Image) {
				if (this._onMouseOverImage != null) {
					((Image) this._obj).setOverImage(this._onMouseOverImage);
				}
				else if (this._onMouseOverImageId > 0) {
					((Image) this._obj).setOverImage(new Image(this._onMouseOverImageId));
				}
				
				if (this._onClickImage != null) {
					((Image) this._obj).setOnClickImage(this._onClickImage);
				}
				else if (this._onClickImageId > 0) {
					((Image) this._obj).setOnClickImage(new Image(this._onClickImageId));
				}
			}

		}
		else if (this._imageId > 0) {
			Image image = new Image(this._imageId);
			if (this._onMouseOverImage != null) {
				image.setOverImage(this._onMouseOverImage);
			}
			else if (this._onMouseOverImageId > 0) {
				image.setOverImage(new Image(this._onMouseOverImageId));
			}
			if (this._onClickImage != null) {
				image.setOnClickImage(this._onClickImage);
			}
			else if (this._onClickImageId > 0) {
				image.setOnClickImage(new Image(this._onClickImageId));
			}
			setImage(image);
		}

		if (this.isImageButton) { //get a generated button gif image
			if (this.useTextAsLocalizedTextKey) { //the text entered is a local key
				setPresentationObject(iwc.getIWMainApplication().getCoreBundle().getResourceBundle(iwc).getLocalizedImageButton(this.text, this.text));
			}
			else {
				setPresentationObject(iwc.getIWMainApplication().getCoreBundle().getImageButton(getLocalizedText(iwc)));
			}
		}
		else if (this.isImageTab) { //get a generated button gif image
			if (this.useTextAsLocalizedTextKey) { //the text entered is a local key
				setPresentationObject(iwc.getIWMainApplication().getCoreBundle().getResourceBundle(iwc).getLocalizedImageTab(this.text, this.text, this.flip));
			}
			else {
				setPresentationObject(iwc.getIWMainApplication().getCoreBundle().getImageTab(getLocalizedText(iwc), this.flip));
			}
		}

		if (this.isImageTab || this.isImageButton) {
			if (isSetToSubmitForm()) {
				((Image) this._obj).removeMarkupAttribute("onMouseDown"); //so that it doesn't interfere with the link onclick event
			}
		}

		if (this._obj != null) {
			this._obj.main(iwc);
		}
		
		try {
			getInlineEditable().makeInlineEditable(iwc, this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public void setURL(String url, boolean maintainAllGlobalParameters, boolean maintainBuilderParameters) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		
		StringTokenizer urlplusprm = new StringTokenizer(url, "?");
		String newUrl = urlplusprm.nextToken();
		if (urlplusprm.hasMoreTokens()) {
			String prm = urlplusprm.nextToken();
			StringTokenizer tokens = new StringTokenizer(prm, "&");
			while (tokens.hasMoreTokens()) {
				String parameter = tokens.nextToken();
				StringTokenizer token = new StringTokenizer(parameter, "=");
				while (token.hasMoreTokens()) {
					String name = token.nextToken();
					String value = null;
					if (token.hasMoreTokens()) {
						value = token.nextToken();
					}
					else if (name.length() + 1 == parameter.length()) {
						value = "";
					}
					addParameter(name, value);
				}
			}
		}
		setMarkupAttribute(HREF_ATTRIBUTE, newUrl);
		this._maintainAllGlobalParameters = maintainAllGlobalParameters;
		this._maintainBuilderParameters = maintainBuilderParameters;
	}

	public void setURL(String url) {
		setURL(url, false, false);
	}

	/**
	 *
	 */
	public String getURL(IWContext iwc) {
		if ((this.protocol == null) && this.https) {
			String attr = getMarkupAttribute(HREF_ATTRIBUTE);
			StringBuffer url = new StringBuffer();
			url.append("https://").append(iwc.getServerName());
			if (attr != null) {
				url.append(attr);
			} else {
				url.append(slash);
			}
			return url.toString();
		}
		else if (this.protocol != null) {
			String attr = getMarkupAttribute(HREF_ATTRIBUTE);
			StringBuffer url = new StringBuffer();
			url.append(this.protocol).append("://").append(iwc.getServerName()).append(slash);
			if (attr != null) {
				url.append(attr);
			}
			return url.toString();
		}
		else {
			return (getMarkupAttribute(HREF_ATTRIBUTE));
		}
	}

	public String getURL() {
		return (getMarkupAttribute(HREF_ATTRIBUTE));
	}

	/**
	 *
	 */
	public void addParameter(Parameter parameter) {
		addParameter(parameter.getName(), parameter.getValueAsString());
	}

	/**
	 *
	 */
	public void addParameter(String parameterName, Class theClass) {
		addParameter(parameterName, IWMainApplication.getEncryptedClassName(theClass));
	}

	public boolean isParameterSet(String prmName) {
		if (this._parameterString != null) {
			if (!(prmName != null && !prmName.equals(""))) {
				return true;
			}
			String prmString = this._parameterString.toString();
			if (prmString.length() > 0) {
				if ((prmString.charAt(0) == '?') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				if ((prmString.charAt(0) == '&') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				StringTokenizer token = new StringTokenizer(prmString, "&=", false);
				//int index = 0;
				while (token.hasMoreTokens()) {
					String st = token.nextToken();
					if (token.hasMoreTokens()) {
						token.nextToken();
						if (prmName.equals(st)) {
							return true;
						}
						//index++;
					}
				}
			}
			else {
				return false;
			}
		}
		return false;
	}

	public void setParameter(Map<String, String> parameterMap) {
		if (parameterMap != null) {
			Iterator<Map.Entry<String, String>> parameters = parameterMap.entrySet().iterator();
			while (parameters.hasNext()) {
				Map.Entry<String, String> entry = parameters.next();
				String key = entry.getKey();
				String value = entry.getValue();
				setParameter(key, value);
			}
		}
	}
	
	public void setParameter(String parameterName, String parameterValue) {
		addParameter(parameterName, parameterValue);
	}
	
	/**
	 * adds a parameter name and its value at the beginning of the links parameter list
	 * so the added parameter is written out after the '?' :
	 * .../?p=s&... becomes .../?parameterName=parameterValue&p=s&...
	 * @param parameterName 
	 * @param parameterValue
	 */
	public void addFirstParameter(String parameterName, String parameterValue) {
		if ((parameterName != null) && (parameterValue != null)) {
			parameterName = getEncodedValue(parameterName);
			parameterValue = getEncodedValue(parameterValue);

			if (this._parameterString == null) {
				this._parameterString = new StringBuffer();
				this._parameterString.append("?");
				this._parameterString.append(parameterName);
				this._parameterString.append("=");
				this._parameterString.append(parameterValue);
			}
			else {
				StringBuffer temp = new StringBuffer();
				temp.append(parameterName);
				temp.append("=");
				temp.append(parameterValue);
				temp.append("&");
				this._parameterString.insert(1,temp);
			}
		}
		else if (parameterName != null) {
			parameterName = getEncodedValue(parameterName);
		}
		else if (parameterValue != null) {
			parameterValue = getEncodedValue(parameterValue);
		}
	}

	/**
	 *
	 */
	public void addParameter(String parameterName, String parameterValue) {
		if ((parameterName != null)) {
			parameterName = getEncodedValue(parameterName);
			if (parameterValue != null) {
				parameterValue = getEncodedValue(parameterValue);
			}

			if (this._parameterString == null) {
				this._parameterString = new StringBuffer();
			}
			
			if (this._parameterString.toString().length() == 0) {
				this._parameterString.append("?");
			}
			else {
				this._parameterString.append("&");
			}

			this._parameterString.append(parameterName);
			if (parameterValue != null) {
				this._parameterString.append("=");
				this._parameterString.append(parameterValue);
			}
		}
		else if (parameterValue != null) {
			parameterValue = getEncodedValue(parameterValue);
		}
	}
	
	private String getEncodedValue(String value) {
		try {
			value = URLEncoder.encode(value, charEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if (forceToReplaceAfterEncoding) {
			value = StringHandler.replace(value, "+", CoreConstants.SPACE);
		}
		
		return value;
	}

	/**
	 *
	 */
	public void addParameter(String parameterName, int parameterValue) {
		addParameter(parameterName, Integer.toString(parameterValue));
	}

	/**
	 *
	 */
	public void maintainParameter(String parameterName, IWContext iwc) {
		if(parameterName == null)
			return;
		
		String parameterValue = iwc.getParameter(parameterName);
		if (parameterValue != null) {
			addParameter(parameterName, parameterValue);
		}
	}

	public void setToMaintainParameter(String name, boolean maintain) {
		if (this.maintainedParameters == null) {
			this.maintainedParameters = new ArrayList<String>();
		}
		if (maintain) {
			this.maintainedParameters.add(name);
		}
	}
	
	public void setToMaintainAllParameter(boolean value) {
		this._maintainAllParameters = value;
	}
	
	/**
	 * Adds a collection of parameter names (String)
	 * for the link to maintain from the request
	 * @param parameterNames
	 */
	public void setToMaintainParameters(Collection<String> parameterNames){
		if (this.maintainedParameters == null) {
			this.maintainedParameters = new ArrayList<String>();
		}
		this.maintainedParameters.addAll(parameterNames);
	}

	/*
	 *
	 */
	private void setOnEvent(String eventType, String eventString) {
		setMarkupAttributeMultivalued(eventType, eventString);
	}

	/**
	 *
	 */
	public void setOnFocus(String s) {
		setOnEvent("onfocus", s);
	}

	/**
	 *
	 */
	public void setOnBlur(String s) {
		setOnEvent("onblur", s);
	}

	/**
	 *
	 */
	public void setOnMouseOver(String s) {
		setOnEvent("onmouseover", s);
	}

	/**
	 *
	 */
	public void setOnMouseOut(String s) {
		setOnEvent("onmouseout", s);
	}

	/**
	 *
	 */
	public void setOnSelect(String s) {
		setOnEvent("onselect", s);
	}

	/**
	 *
	 */
	public void setOnChange(String s) {
		setOnEvent("onchange", s);
	}

	/**
	 *
	 */
	public void setOnClick(String s) {
		setOnEvent("onclick", s);
	}

	/**
	 *
	 */
	public String getOnFocus() {
		return getMarkupAttribute("onfocus");
	}

	/**
	 *
	 */
	public String getOnBlur() {
		return getMarkupAttribute("onblur");
	}

	/**
	 *
	 */
	public String getOnSelect() {
		return getMarkupAttribute("onselect");
	}

	/**
	 *
	 */
	public String getOnChange() {
		return getMarkupAttribute("onchange");
	}

	/**
	 *
	 */
	public String getOnClick() {
		return getMarkupAttribute("onclick");
	}

	/**
	 *
	 */
	public void setTarget(String target) {
		setMarkupAttribute(TARGET_ATTRIBUTE, target);
	}

	/**
	 *
	 */
	public String getTarget() {
		return getMarkupAttribute(TARGET_ATTRIBUTE);
	}

	/**
	 *
	 */
	@Override
	public void setFontSize(String s) {
		if (isText()) {
			((Text) this._obj).setFontSize(s);
		}
	}

	/**
	 *
	 */
	@Override
	public void setFontSize(int i) {
		setFontSize(Integer.toString(i));
	}

	/**
	 *
	 */
	@Override
	public void setFontFace(String s) {
		if (isText()) {
			((Text) this._obj).setFontFace(s);
		}
	}

	/**
	 *
	 */
	@Override
	public void setFontColor(String color) {
		if (isText()) {
			((Text) this._obj).setFontColor(color);
		}
	}

	/**
	 *
	 */
	@Override
	public void setFontStyle(String style) {
		if (isText()) {
			((Text) this._obj).setFontStyle(style);
		}
	}

	/**
	 *
	 */
	@Override
	public void setFontClass(String styleClass) {
		if (isText()) {
			((Text) this._obj).setFontClass(styleClass);
		}
	}

	@Override
	public void setStyle(String style) {
		super.setStyle(style);
		this.hasClass = true;
	}

	/**
	 *
	 */
	public void setSessionId(boolean addSessionId) {
		this._addSessionId = addSessionId;
	}

	/**
	 *
	 */
	@Override
	public void addBreak() {
		if (isText()) {
			((Text) this._obj).addBreak();
		}
	}

	/**
	 *
	 */
	@Override
	public void setTeleType() {
		if (isText()) {
			((Text) this._obj).setTeleType();
		}
	}

	/**
	 *
	 */
	@Override
	public void setBold() {
		if (isText()) {
			((Text) this._obj).setBold();
		}
	}

	/**
	 *
	 */
	@Override
	public void setItalic() {
		if (isText()) {
			((Text) this._obj).setItalic();
		}
	}

	/**
	 *
	 */
	@Override
	public void setUnderline() {
		if (isText()) {
			((Text) this._obj).setUnderline();
		}
	}

	/**
	 *
	 */
	@Override
	public void setText(String text) {
		if (isText()) {
			((Text) this._obj).setText(text);
			this.text = text;
		}
		else {
			setText(new Text(text));
			this.text = text;
		}
	}

	@Override
	public String getText() {
		String toReturn = this.text;
		if (toReturn == null && this._obj == null && this._obj instanceof Text && !(this._obj instanceof Link)) {
			toReturn = ((Text) this._obj).getText();
		}
		return toReturn;
	}

	/**
	*
	*/
	public void setText(Text text) {
		this.text = text.getText();
		this._obj = text;
		this._obj.setParentObject(this);
		this._objectType = OBJECT_TYPE_TEXT;
	}

	@Override
	public void setLocalizedText(String localeString, String text) {
		if (isText()) {
			((Text) this._obj).setLocalizedText(localeString, text);
		}
		else {
			super.setLocalizedText(localeString, text);
		}
	}

	/**
	 *
	 */
	@Override
	public void setLocalizedText(int icLocaleID, String text) {
		if (isText()) {
			((Text) this._obj).setLocalizedText(icLocaleID, text);
		}
		else {
			super.setLocalizedText(icLocaleID, text);
		}
	}

	/**
	 *
	 */
	@Override
	public void setLocalizedText(Locale locale, String text) {
		if (isText()) {
			((Text) this._obj).setLocalizedText(locale, text);
		}
		else {
			super.setLocalizedText(locale, text);
		}
	}

	/**
	 *
	 */
	@Override
	public void addToText(String text) {
		if (isText()) {
			((Text) this._obj).addToText(text);
		}
	}

	/**
	 *
	 */
	public void setTextOnLink(String text) {
		setText(text);
	}

	/**
	 *
	 */
	public void setObject(PresentationObject object) {
		this._obj = object;
		this._objectType = OBJECT_TYPE_MODULEOBJECT;

		this._obj.setParentObject(this);
	}

	/**
	 *
	 */
	public void setLocale(String languageString) {
		//setEventListener(LocaleSwitcher.class.getName());
		addParameter(LocaleSwitcher.languageParameterString, languageString);
	}

	/**
	 *
	 */
	public void setLocale(Locale locale) {
		setLocale(locale.toString());
	}

	/**
	 * method for adding an image to the link
	 */
	public void setImage(Image image) {
		this._obj = image;
		String toolTip = getToolTip();
		if (toolTip != null) {
			((Image) this._obj).setAlt(toolTip);
		}
		this._objectType = OBJECT_TYPE_IMAGE;
		this._obj.setParentObject(this);
	}

	public void setLocalizedImage(String localeString, int imageID) {
		setLocalizedImage(ICLocaleBusiness.getLocaleFromLocaleString(localeString), imageID);
	}

	public void setLocalizedImage(Locale locale, int imageID) {
		this._objectType = OBJECT_TYPE_IMAGE;
		getImageLocalizationMap().put(locale, new Integer(imageID));
	}
	
	public void setLocalizedOverImage(String localeString, int overImageID) {
		this._objectType = OBJECT_TYPE_IMAGE;
		getOverImageLocalizationMap().put(ICLocaleBusiness.getLocaleFromLocaleString(localeString), new Integer(overImageID));
	}

	private Map<Locale, Object> getImageLocalizationMap() {
		if (this._ImageLocalizationMap == null) {
			this._ImageLocalizationMap = new HashMap<Locale, Object>();
		}
		return this._ImageLocalizationMap;
	}
	
	private Map<Locale, Object> getOverImageLocalizationMap() {
		if (this._overImageLocalizationMap == null) {
			this._overImageLocalizationMap = new HashMap<Locale, Object>();
		}
		return this._overImageLocalizationMap;
	}

	public boolean isImage() {
		if (this._objectType == OBJECT_TYPE_IMAGE) {
			return true;
		}
		else {
			if (this._obj == null) {
				if (this._ImageLocalizationMap != null) {
					return true;
				}
			}
			else {
				if (this._obj instanceof Image) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isSetToSubmitForm() {
		return this.iFormToSubmit;
	}

	/**
	 * Returns the correct Image, localized or not depending on what has been set.
	 */
	private Image getTheCorrectDefaultImage(IWContext iwc) throws Exception {
		Integer imageID = getTheCorrectDefaultImageID(iwc);
		if (imageID == null) {
			return (Image) this._obj;
		}
		else {
			return new Image(imageID.intValue());
		}
	}

	/**
	 * Returns the correct ImageID, localized or not depending on what has been set.
	 * Returns null if nothing localized shas been set
	 */
	private Integer getTheCorrectDefaultImageID(IWContext iwc) throws Exception {
		if (this._ImageLocalizationMap != null) {
			Locale currLocale = iwc.getCurrentLocale();

			Integer localizedImageID = (Integer) this.getImageLocalizationMap().get(currLocale);
			if (localizedImageID != null) {
				return localizedImageID;
			}
			else {
				Integer defImageID = (Integer) this.getImageLocalizationMap().get(iwc.getIWMainApplication().getSettings().getDefaultLocale());
				if (defImageID != null) {
					return defImageID;
				}
			}
		}
		return null;
	}

	public void setImageId(int imageId) {
		this._imageId = imageId;
	}

	public void setOnMouseOverImage(Image image) {
		this._onMouseOverImage = image;
	}

	public void setOnMouseOverImageId(int imageId) {
		this._onMouseOverImageId = imageId;
	}

	public void setOnClickImage(Image image) {
		this._onClickImage = image;
	}

	public void setOnClickImageId(int imageId) {
		this._onClickImageId = imageId;
	}

	/**
	 *  set the target object instance
	 */
	public void setTargetObjectInstance(ICObjectInstance instance) {
		if ((instance != null) && (instance.getID() != -1)) {
			addParameter(TARGET_OBJ_INS, instance.getID());
		}
	}

	/**
	*  set the target object instance
	*/
	public void setTargetObjectInstance(int instanceid) {
		addParameter(TARGET_OBJ_INS, instanceid);
	}

	/**
	 * method for adding a link to a page object
	 */
	public void setPage(ICPage page) {
		setPage(new Integer(page.getPrimaryKey().toString()).intValue());
	}
	
	public void setPage(int pageID) {
		if (pageID != -1) {
			this.ibPage = pageID;
			if(IWMainApplication.useNewURLScheme){
				try {
					this.setURL(this.getBuilderService(getIWApplicationContext()).getPageURI(pageID));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			else{
				String value = this.getParameterValue(IB_PAGE_PARAMETER);
				if (value != null) {
					removeParameter(IB_PAGE_PARAMETER);
				}
				addParameter(IB_PAGE_PARAMETER, String.valueOf(pageID));
			}
		}
	}

	public int getPage() {
		/*
		String value = this.getParameterValue(IB_PAGE_PARAMETER);
		if (value != null && !value.equals("")) {
			return Integer.parseInt(value);
		}
		else {
			return 0;
		}*/
		return this.ibPage;
	}

	public String getParameterValue(String prmName) {
		if (this._parameterString != null) {
			if (!(prmName != null && prmName.endsWith(""))) {
				return null;
			}
			String prmString = this._parameterString.toString();
			if (prmString.length() > 0) {
				if ((prmString.charAt(0) == '?') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				if ((prmString.charAt(0) == '&') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				StringTokenizer token = new StringTokenizer(prmString, "&=", false);
				//int index = 0;
				while (token.hasMoreTokens()) {
					String st = token.nextToken();
					if (token.hasMoreTokens()) {
						String value = token.nextToken();
						if (prmName.equals(st)) {
							return value;
						}
						//index++;
					}
				}
			}
			else {
				return null;
			}
		}
		return null;
	}

	/**
	 * method for adding a link to a file object
	 * the url generation is done in the main method
	 */
	public void setFile(ICFile file) {
		this.fileId = new Integer(file.getPrimaryKey().toString()).intValue();
		this.datasource = file.getDatasource();
	}

	/**
	 * @todo reimplement
	 */
	public void setFile(int fileId) {
		this.fileId = fileId;
	}

	/**
	 *
	 */
	public PresentationObject getObject() {
		return (this._obj);
	}

	/*
	 *
	 */
	private boolean isLinkOpeningOnSamePage() {
		return (!isMarkupAttributeSet(TARGET_ATTRIBUTE));
	}

	/**
	 *
	 */
	@Override
	public Object clone() {
		Link linkObj = null;
		try {
			linkObj = (Link) super.clone();

			if (this._obj != null) {
				linkObj._obj = (PresentationObject) this._obj.clone();
			}
			if (this._myWindow != null) {
				linkObj._myWindow = (Window) this._myWindow.clone();
			}

			if (this._windowClass != null) {
				linkObj._windowClass = this._windowClass;
			}

			linkObj._objectType = this._objectType;
			linkObj._parameterString = this._parameterString;
			linkObj._addSessionId = this._addSessionId;
			linkObj._maintainAllGlobalParameters = this._maintainAllGlobalParameters;
			linkObj._maintainBuilderParameters = this._maintainBuilderParameters;
			linkObj._maintainAllParameters = this._maintainAllParameters;
			linkObj.text = this.text;
			linkObj.isImageButton = this.isImageButton;
			linkObj.useTextAsLocalizedTextKey = this.useTextAsLocalizedTextKey;
			linkObj.isImageTab = this.isImageTab;
			linkObj.flip = this.flip;
			linkObj.classToInstanciate = this.classToInstanciate;
			linkObj.templateForObjectInstanciation = this.templateForObjectInstanciation;
			//linkObj.templatePageClass = this.templatePageClass;
			linkObj.protocol = this.protocol;

			if (this._parameterString != null) {
				linkObj._parameterString = new StringBuffer(this._parameterString.toString());
			}
			if (this._ImageLocalizationMap != null) {
				linkObj._ImageLocalizationMap = this._ImageLocalizationMap;
			}
			
			if (this._overImageLocalizationMap != null) {
				linkObj._overImageLocalizationMap = this._overImageLocalizationMap;
			}

		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

		return (linkObj);
	}

	/*
	 *
	 */
	private void addTheMaintainedParameters(IWContext iwc) {
		List<String> list = com.idega.idegaweb.IWURL.getGloballyMaintainedParameters(iwc);
		if (list != null && !list.isEmpty()) {
			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				String parameterName = iter.next();
				String parameterValue = iwc.getParameter(parameterName);
				if (parameterValue != null) {
					if (!this.isParameterSet(parameterName)) {
						addParameter(parameterName, parameterValue);
					}
				}
			}
		}
	}

	/*
	*
	*/
	private void addTheMaintainedBuilderParameters(IWContext iwc) {
		List<String> list = com.idega.idegaweb.IWURL.getGloballyMaintainedBuilderParameters(iwc);
		if (list != null) {
			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				String parameterName = iter.next();
				String parameterValue = iwc.getParameter(parameterName);
				if (parameterValue != null) {
					if (!this.isParameterSet(parameterName)) {
						//System.out.println("false");
						addParameter(parameterName, parameterValue);
					}
					else {
						//System.out.println("true");
					}
				}
				else {
					//System.out.println("null");
				}
			}
		}
	}

	/**
	 *
	 */
	public void setToMaintainGlobalParameters() {
		this._maintainAllGlobalParameters = true;
	}

	public void setToMaintainBuilderParameters(boolean value) {
		this._maintainBuilderParameters = value;
	}

	/**
	 *
	 */
	protected String getParameterString(IWContext iwc, String URL) {
		if (usingEventSystem) {
			this.removeParameter(PRM_HISTORY_ID);
			this.addParameter(PRM_HISTORY_ID, (String) iwc.getSessionAttribute(PRM_HISTORY_ID));
		}

		if (this._maintainBuilderParameters) {
			addTheMaintainedBuilderParameters(iwc);
		} else {
			if (isLinkOpeningOnSamePage()) {
				addTheMaintainedParameters(iwc);
			}
		}

		if (this._maintainAllGlobalParameters) {
			addTheMaintainedParameters(iwc);
		} else {
			if (isLinkOpeningOnSamePage()) {
				addTheMaintainedParameters(iwc);
			}
		}
		List<Object> l = getIWPOListeners();
		if (l != null) {
			int size = l.size();
			if (size > 1) {
				int[] pages = new int[size];
				int[] inst = new int[size];
				ListIterator<Object> lIter = l.listIterator();
				while (lIter.hasNext()) {
					int index = lIter.nextIndex();
					Object lItem = lIter.next();
					if (lItem instanceof String) {
						String str = (String) lItem;
						int indexof_ = str.indexOf('_');
						if (indexof_ != -1) {
							try {
								pages[index] = Integer.parseInt(str.substring(0, indexof_));
								inst[index] = Integer.parseInt(str.substring(indexof_ + 1, str.length()));
							}
							catch (NumberFormatException e) {
								System.err.println("Link: Listener coordenates not right");
							}
						}
					}
					else if (lItem instanceof PresentationObject) {
						PresentationObject obj = (PresentationObject) lItem;
						pages[index] = obj.getParentPageID();
						inst[index] = obj.getParentObjectInstanceID();
					}
				}
				EventLogic.setICObjectInstanceListeners(this, pages, inst);
				EventLogic.setICObjectInstanceEventSource(this, this.getParentPageID(), this.getParentObjectInstanceID());
			}
			else if (size == 1) {
				PresentationObject obj = (PresentationObject) l.get(0);
				if (obj != null) {
					EventLogic.setICObjectInstanceListener(this, obj.getParentPageID(), obj.getParentObjectInstanceID());
					EventLogic.setICObjectInstanceEventSource(this, this.getParentPageID(), this.getParentObjectInstanceID());
				}
			}
		}

		if (URL == null) {
			URL = "";
		}
		if ((!this.isParameterSet(IWContext.IDEGA_SESSION_KEY))) {
			if (this._parameterString == null) {
				this._parameterString = new StringBuffer();
				if (this._addSessionId && (!iwc.isSearchEngine())) {
					if (URL.equals("#")) {
						return ("");
					}
					else if (URL.indexOf("://") == -1) { //does not include ://
						if (URL.indexOf("?") != -1) {
							this._parameterString.append("&");
							this._parameterString.append(IWContext.IDEGA_SESSION_KEY);
							this._parameterString.append("=");
							this._parameterString.append(iwc.getIdegaSessionId());
							//return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
						}
						else if ((URL.indexOf("//") != -1) && (URL.lastIndexOf("/") == URL.lastIndexOf("//") + 1)) {
							//the case where the URL is etc. http://www.idega.is
							this._parameterString.append("/?"+IWContext.IDEGA_SESSION_KEY+"=");
							this._parameterString.append(iwc.getIdegaSessionId());
							//return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
						}
						else {
							if (URL.indexOf("/") != -1) {
								//If the URL ends with a "/"
								if (URL.lastIndexOf("/") == (URL.length() - 1)) {
									this._parameterString.append("?");
									this._parameterString.append(IWContext.IDEGA_SESSION_KEY);
									this._parameterString.append("=");
									this._parameterString.append(iwc.getIdegaSessionId());
									//return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
								}
								else {
									//There is a dot after the last "/" interpreted as a file not a directory
									if (URL.lastIndexOf(".") > URL.lastIndexOf("/")) {
										this._parameterString.append("?");
										this._parameterString.append(IWContext.IDEGA_SESSION_KEY);
										this._parameterString.append("=");
										this._parameterString.append(iwc.getIdegaSessionId());
										return (TextSoap.convertSpecialCharacters(this._parameterString.toString()));
									}
									else {
										this._parameterString.append("/?");
										this._parameterString.append(IWContext.IDEGA_SESSION_KEY);
										this._parameterString.append("=");
										this._parameterString.append(iwc.getIdegaSessionId());
										//return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
									}
								}
							}
							else {
								this._parameterString.append("?");
								this._parameterString.append(IWContext.IDEGA_SESSION_KEY);
								this._parameterString.append("=");
								this._parameterString.append(iwc.getIdegaSessionId());
								//return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
							}
						}
					}
					else {
						/**
						 * @todo Temporary solution??? :// in link then no idega_session_id
						 */
						return ("");
					}
				}
				else {
					return ("");
				}

			}
			else {
				/**
				* @todo Temporary solution??? :// in link then no idega_session_id
				*/
				if (URL.indexOf("?") == -1) {
					if (this._addSessionId && (!iwc.isSearchEngine())) {
						if (this._parameterString.toString().indexOf("?") == -1) {
							this._parameterString.insert(0, '?');
						}
						if (URL.indexOf("://") == -1) {
							this._parameterString.append("&");
							this._parameterString.append(IWContext.IDEGA_SESSION_KEY);
							this._parameterString.append("=");
							this._parameterString.append(iwc.getIdegaSessionId());
						}
					}
				}
				else {
					if (this._addSessionId && (!iwc.isSearchEngine())) {
						if (URL.indexOf("://") == -1) {
							this._parameterString.append("&");
							this._parameterString.append(IWContext.IDEGA_SESSION_KEY);
							this._parameterString.append("=");
							this._parameterString.append(iwc.getIdegaSessionId());
						}
					}
				}
				//return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
			}
		}
		
		String returner = this._parameterString.toString();
		return TextSoap.forHTMLTag(returner);
	}

	/**
	 *
	 */
	public void clearParameters() {
		this._parameterString = null;
	}

	public String getWindowToOpenCallingScript(IWContext iwc) {
		try {
			this._main(iwc);
		}
		catch (Exception ex) {

		}
		if (isOpeningInNewWindow()) {
			//if (_objectType==(OBJECT_TYPE_WINDOW)) {
			if (this._windowClass == null) {
				return this._myWindow.getCallingScriptString(iwc, this._myWindow.getURL(iwc) + getParameterString(iwc, this._myWindow.getURL(iwc)));
			}
			else {
				return Window.getCallingScriptString(this._windowClass, getURL(iwc) + getParameterString(iwc, getURL(iwc)), true, iwc);
			}
		}
		return "";
	}

	protected void setFinalUrl(String url) {
		setMarkupAttribute(HREF_ATTRIBUTE, url);
	}

	public String getFinalUrl(IWContext iwc) {
		String uri = iwc.getRequestURI();
		return uri + getParameterString(iwc, uri);
	}
	
	private void maintainParameters(IWContext iwc) {
		Iterator<String> iter = this.maintainedParameters.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			String value = iwc.getParameter(name);
			if (value != null) {
				addParameter(name, value);
			}
		}
	}
	
	private void maintainAllParameters(IWContext iwc) {
		Enumeration<String> pNames = iwc.getParameterNames();
		if(pNames != null) {
			while (pNames.hasMoreElements()) {
				String pName = pNames.nextElement();
				if(!isParameterSet(pName)) {
					String[] pValues = iwc.getParameterValues(pName);
					if(pValues!=null) {
						for (int i = 0; i < pValues.length; i++) {
							addParameter(pName,pValues[i]);
						}
					}
				}
			}
		}
	}

	/**
	 *
	 */
	@Override
	public void print(IWContext iwc) throws Exception {
		boolean addParameters = true;
		String oldURL = getURL(iwc);

		if (this._maintainAllParameters) {
			maintainAllParameters(iwc);
		} else if (this.maintainedParameters != null) {
			maintainParameters(iwc);
		}

		/**
		 * @todo: Is this the right solution? - If the user is not logged on then do not add a session id to the link
		 */
		if (!com.idega.core.accesscontrol.business.LoginBusinessBean.isLoggedOn(iwc)) {
			setSessionId(false);
		}

		if (oldURL == null) {
			oldURL = iwc.getRequestURI();
			setFinalUrl(oldURL);
		} else if (oldURL.equals(com.idega.util.StringHandler.EMPTY_STRING)) {
			oldURL = iwc.getRequestURI();
			setFinalUrl(oldURL);
		}

		if (oldURL.equals(HASH)) {
			addParameters = false;
		} else if (oldURL.startsWith(JAVASCRIPT)) {
			addParameters = false;
		}

		if (getMarkupLanguage().equals("HTML")) {
			boolean openInNewWindow = isOpeningInNewWindow();
			boolean alignSet = isMarkupAttributeSet(HORIZONTAL_ALIGNMENT);

			if (alignSet) {
				print("<div align=\"" + getHorizontalAlignment() + "\">");
				removeMarkupAttribute(HORIZONTAL_ALIGNMENT); //does this slow things down?
			}

			if (openInNewWindow) {
				setFinalUrl(this.getWindowOpenerJavascriptString(iwc));
			} else if (!this.isOutgoing) {
				//Should not happen when a new window is opened AND not if isOutgoing
				if (addParameters) {
					setFinalUrl(oldURL + getParameterString(iwc, oldURL));
				}
			}

			print("<a " + getMarkupAttributesString() + " >");
			if (this.isText()) {
				if (this.hasClass) {
					if (this._obj != null) {
						String text = ((Text) this._obj).getLocalizedText(iwc);
						if (text != null) {
							print(text);
						}
					}
				} else {
					if (this._obj != null) {
						renderChild(iwc,this._obj);
					}
				}
			} else if (this.isImage()) {
				Image image = this.getTheCorrectDefaultImage(iwc);
				if (image != null) {
					if(this._overImageLocalizationMap!=null){
						image.setOverImageLocalizationMap(getOverImageLocalizationMap());
					}
					renderChild(iwc,image);
				}
			} else {
				if (this._obj != null) {
					renderChild(iwc,this._obj);
				}
			}
			print("</a>");

			if (alignSet) {
				print("</div>");
			}
		} else if (getMarkupLanguage().equals("WML")) {
			if (this._myWindow != null) {
				setFinalUrl(this._myWindow.getURL(iwc) + getParameterString(iwc, oldURL)); // ????????????
				setFinalUrl(HASH);
				String url = getURL();
				url = iwc.getResponse().encodeURL(url);
				setFinalUrl(url);
				print("<a " + getMarkupAttributesString() + " >");
				print(this._myWindow.getName());
				print("</a>");
			} else {
				if (addParameters) {
					setFinalUrl(oldURL + getParameterString(iwc, oldURL));
				}
				String url = getURL();
				url = iwc.getResponse().encodeURL(url);
				setFinalUrl(url);
				print("<a " + getMarkupAttributesString() + " >");
				this._obj._print(iwc);
				print("</a>");
			}
		}
	}

	/**
	 *
	 */
	@Override
	public void addIWLinkListener(IWLinkListener l, IWContext iwc) {
		if (!listenerAdded()) {
			postIWLinkEvent(iwc);
		}
		super.addIWLinkListener(l, iwc);
	}

	/*
	 *
	 */
	private void postIWLinkEvent(IWContext iwc) {
		this.eventLocationString = getID();
		IWLinkEvent event = new IWLinkEvent(this, IWLinkEvent.LINK_ACTION_PERFORMED);
		if (!this.iFormToSubmit) {
			addParameter(sessionEventStorageName, this.eventLocationString);
		}
		iwc.setSessionAttribute(this.eventLocationString, event);
		listenerAdded(true);
	}

	/**
	 *
	 */
	public void setToFormSubmit(Form form) {
		setToFormSubmit(form, false);
	}

	/**
	 *
	 */
	public void setToFormSubmit(String formID) {
		setToFormSubmit(formID, false);
	}

	public void setImageToOpenInPopUp(Image image) {
		this.setOnClick("img_wnd=window.open('','','width=100,height=100,left='+((screen.width/2)-50)+',top='+((screen.height/2)-50)+',resizable=yes,scrollbars=no'); doopen('" + image.getMediaURL() + "'); return true;");
		setFinalUrl("javascript:void(0)");
		setTarget(TARGET_SELF_WINDOW);
	}
	
	public void setToOpenAlert(String message) {
		setFinalUrl(JAVASCRIPT + "alert('" + TextSoap.cleanText(message) + "')");
	}

	public void setToFormSubmit(Form form, boolean useEvent) {
		setToFormSubmit(form.getID(), useEvent);
	}
	
	/**
	 *
	 */
	public void setToFormSubmit(String formID, boolean useEvent) {
		this.iFormToSubmit = true;
		//setFinalUrl(HASH);
		String action = "";
		if ((getIWLinkListeners() != null && getIWLinkListeners().length != 0) || useEvent) {
			//setOnClick("document."+form.getID()+"."+IWMainApplication.IWEventSessionAddressParameter+".value=this.id ;document."+form.getID()+".submit()");

			action = ("document.forms['" + formID + "']." + IWMainApplication.IWEventSessionAddressParameter + ".value='" + this.getID() + "';document.forms['" + formID + "'].submit();");
		}
		else {
			action = ("document.forms['" + formID + "'].submit();");
		}
		//setOnClick(action);
		setFinalUrl(JAVASCRIPT + action);
	}
	
	public void setFormToSubmit(Form form, boolean useFormValidation) {
		Script script = form.getAssociatedFormScript();
		
		StringBuffer method = new StringBuffer();
		method.append("function submitForm(formID) {").append("\n\t");
		if (useFormValidation) {
			method.append("if (checkSubmit(findObj(formID))) {").append("\n\t\t");
			method.append("findObj(formID).submit();").append("\n\t");
			method.append("}").append("\n");
		}
		else {
			method.append("findObj(formID).submit();").append("\n");
		}
		method.append("}");
		
		script.addMethod("submitForm", method.toString());
		setFinalUrl(JAVASCRIPT + "submitForm('" + form.getID() + "')");
	}
	
	public void setFormToSubmit(String formID) {
		String action = ("document.forms['" + formID + "'].submit()");
		setFinalUrl(JAVASCRIPT + action);
	}
	
	public void setToFormReset(Form form) {
		String action = ("document.forms['" + form.getID() + "'].reset();");
		setFinalUrl(JAVASCRIPT + action);
	}
	
	/**
	 *
	 */
	public void setAsBackLink(int backUpHowManyPages) {
		setOnClick("history.go(-" + backUpHowManyPages + ")");
		setFinalUrl(HASH);
	}
	
	public void setAsCloseLink() {
		setOnClick("top.window.close()");
		setFinalUrl(HASH);
	}
	
	public void setAsPrintLink() {
		setOnClick("javascript:window.print();");
		setFinalUrl(HASH);
	}

	/**
	 *
	 */
	public void setAsBackLink() {
		setAsBackLink(1);
	}

	public void setNoURL() {
		setFinalUrl(HASH);
	}

	/**
	 *
	 */
	@Override
	public void setProperty(String key, String values[]) {
		if (key.equalsIgnoreCase("text")) {
			setText(values[0]);
		}
		else if (key.equalsIgnoreCase("url")) {
			setURL(values[0]);
		}
	}

	/**
	 *
	 */
	public void setEventListener(Class<?> eventListenerClass) {
		String eventListenerEncryptedClassName = IWMainApplication.getEncryptedClassName(eventListenerClass.getName());
		setEventListener(eventListenerEncryptedClassName);
	}

	public void setEventListener(String encryptedClassName) {
		addParameter(IWMainApplication.IdegaEventListenerClassParameter, encryptedClassName);
	}
	
	/**
	 *
	 */
	public void sendToControllerFrame() {
		setTarget(IWConstants.IW_CONTROLLER_FRAME_NAME);
	}

	
	public void setWindowToOpen(Class windowClass, String width, String height, boolean resizable, boolean scrollbar) {
		try{
			this._windowInstance = (Window)windowClass.newInstance();
			this._windowInstance.setResizable(resizable);
			this._windowInstance.setScrollbar(scrollbar);
			this._windowInstance.setWidth(width);
			this._windowInstance.setHeight(height);
		}
		catch(Exception e){
		}
		
		setWindowToOpen(windowClass);
	}
	
	public void setPublicWindowToOpen(Class windowClass) {
		this._windowClass = windowClass;
		this.usePublicWindow = true;
		/**
		 * @todo Temporary workaround - Find out why this is needed, copied from setWindowToOpen...
		 */
		try {
			this.setURIToWindowOpenerClass(IWContext.getInstance());
		}
		catch (Exception e) {

		}
	}
	/**
	 *
	 */
	public void setWindowToOpen(Class windowClass) {
		//_objectType=OBJECT_TYPE_WINDOW;
		this._windowClass = windowClass;

		/**
		 * @todo Temporary workaround - Find out why this is needed
		 */
		try {
			this.setURIToWindowOpenerClass(IWContext.getInstance());
		}
		catch (Exception e) {

		}

		//setURL(IWMainApplication.windowOpenerURL);
		//addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass);
	}

public void setWindowToOpen(String className) {
	try {
		setWindowToOpen(RefactorClassRegistry.forName(className));
	}
	catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
}
	public void setWindowToOpen(Class windowClass, int instanceId) {
		//_objectType=OBJECT_TYPE_WINDOW;
		setWindowToOpen(windowClass);
		//setURL(IWMainApplication.windowOpenerURL);
		//addParameter(Page.IW_FRAME_CLASS_PARAMETER,windowClass);
		//this.addParameter(IWMainApplication._PARAMETER_IC_OBJECT_INSTANCE_ID,instanceId);
		this.icObjectInstanceIDForWindow = instanceId;
	}

	public void setNoTextObject(boolean noText) {
		if (isText()) {
			this.hasClass = noText;
		}
	}

	private boolean isOpeningInNewWindow() {
		if (this._myWindow != null || this._windowClass != null || this.windowOpenerJavascriptString != null) {
			return true;
		}
		return false;
	}

	public boolean isText() {
		if (this._objectType == Link.OBJECT_TYPE_TEXT) {
			if (this._obj != null) {
				if (this._obj instanceof Text) {
					return true;
				}
			}
			else {
				//return (this._objectType==this.OBJECT_TYPE_TEXT);
				return true;
			}
		}
		return false;
	}

	public boolean isLabelSet() {
		if (this._obj == null) {
			return false;
		}

		if (this._obj instanceof Text) {
			if (((Text) this._obj).getText().equals(DEFAULT_TEXT_STRING)) {
				return false;
			}
		}

		return true;
	}

	public void setAsImageButton(boolean isImageButton) {
		this.isImageButton = isImageButton;
		this.isImageTab = false; //can't have both
	}

	public void setAsLocalizedImageButton(boolean useTextAsLocalizedTextKey) {
		this.useTextAsLocalizedTextKey = useTextAsLocalizedTextKey;
	}

	public void setAsImageButton(boolean isImageButton, boolean useTextAsLocalizedTextKey) {
		setAsImageButton(isImageButton);
		setAsLocalizedImageButton(useTextAsLocalizedTextKey);
	}

	public void setAsImageTab(boolean isImageTab, boolean flip) {
		this.isImageTab = isImageTab;
		this.flip = flip;
		this.isImageButton = false; //can't have both

	}

	public void setAsLocalizedImageTab(boolean useTextAsLocalizedTextKey) {
		this.useTextAsLocalizedTextKey = useTextAsLocalizedTextKey;
	}

	public void setAsImageTab(boolean isImageTab, boolean useTextAsLocalizedTextKey, boolean flip) {
		setAsImageTab(isImageTab, flip);
		setAsLocalizedImageTab(useTextAsLocalizedTextKey);
	}

	public void addIWPOListener(PresentationObject obj) {
		if (this.listenerInstances == null) {
			this.listenerInstances = new ArrayList<Object>();
		}
		if (!this.listenerInstances.contains(obj)) {
			this.listenerInstances.add(obj);
		}
	}

	public void addIWPOListener(String page_instID) {
		if (this.listenerInstances == null) {
			this.listenerInstances = new ArrayList<Object>();
		}
		if (page_instID != null) {
			StringTokenizer token = new StringTokenizer(page_instID, ",", false);
			while (token.hasMoreTokens()) {
				String pointer = token.nextToken();
				if (!this.listenerInstances.contains(pointer)) {
					this.listenerInstances.add(pointer);
				}
			}
		}
	}

	public List<Object> getIWPOListeners() {
		return this.listenerInstances;
	}

	/**
	 * 
	 * @uml.property name="https"
	 */
	public void setHttps(boolean useHttps) {
		this.https = useHttps;
	}


	public void setOnMouseOverImage(Image image, Image mouseOverImage) {
		image.setOverImage(mouseOverImage);

		setMarkupAttribute("onMouseOver", "swapImage('" + image.getName() + "','','" + mouseOverImage.getMediaURL() + "',1)");
		setMarkupAttribute("onMouseOut", "swapImgRestore()");
	}

	public void removeParameter(String prmName) {
		if (this._parameterString != null) {
			if (!(prmName != null && !prmName.equals(""))) {
				return;
			}

			StringBuffer newBuffer = new StringBuffer();
			String prmString = this._parameterString.toString();

			if (prmString.length() > 0) {
				if ((prmString.charAt(0) == '?') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
					newBuffer.append("?");
				}
				if ((prmString.charAt(0) == '&') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
					newBuffer.append("&");
				}
				StringTokenizer token = new StringTokenizer(prmString, "&", false);
				boolean firstToken = true;
				while (token.hasMoreTokens()) {
					String st = token.nextToken();
					StringTokenizer token2 = new StringTokenizer(st, "=", false);
					if (token2.hasMoreTokens()) {
						String name = token2.nextToken();
						if (!name.equals(prmName)) {
							if (!firstToken) {
								newBuffer.append("&");
							}
							newBuffer.append(name);
							if (token2.hasMoreTokens()) {
								String value = token2.nextToken();
								newBuffer.append("=");
								newBuffer.append(value);
							}
						}
					}
					/*else {
					  newBuffer.append("&" + st);
					}*/
					firstToken = false;
				}
			}
			this._parameterString = newBuffer;
			return;
		}
	}

	public void setWindowToOpenScript(String scriptString) {
		this.windowOpenerJavascriptString = scriptString;
	}

	private String getWindowOpenerJavascriptString(IWContext iwc) {
		if (this.windowOpenerJavascriptString == null) {
			if (this._windowClass == null) {
				return ("javascript:" + this._myWindow.getCallingScriptString(iwc, this._myWindow.getURL(iwc) + getParameterString(iwc, this._myWindow.getURL(iwc))));
			}
			else {
				if (this._windowInstance != null) {
					return ("javascript:" + this._windowInstance.getCallingScriptString(iwc, this._windowInstance.getURL(iwc) + getParameterString(iwc, this._windowInstance.getURL(iwc))));
				} else {
					return ("javascript:" + Window.getCallingScriptString(this._windowClass, getURL(iwc) + getParameterString(iwc, getURL(iwc)), true, iwc));
				}
			}
		}
		else {

			this.windowOpenerJavascriptString = URLDecoder.decode(this.windowOpenerJavascriptString);
			List between = TextSoap.FindAllBetween(this.windowOpenerJavascriptString, "iwOpenWindow('", "'");
			String theUrl = "";
			if (between != null && between.size() > 0) {
				theUrl = (String) between.get(0);
			}

			String paramString = this.getParameterString(iwc, this.getURL(iwc));
			if (theUrl.indexOf("?") != -1) {
				paramString = TextSoap.findAndReplace(paramString, "?", "&");
			}

			if (!paramString.equals("") || !theUrl.equals("")) {
				this.windowOpenerJavascriptString = TextSoap.findAndInsertAfter(this.windowOpenerJavascriptString, theUrl, paramString);
			}

			return "javascript:" + this.windowOpenerJavascriptString;
		}

	}

	/**
	 * 
	 * @uml.property name="classToInstanciate"
	 */
	public void setClassToInstanciate(Class<? extends UIComponent> presentationObjectClass) {
		this.classToInstanciate = presentationObjectClass;
	}

	public void setClassToInstanciate(Class<? extends UIComponent> presentationObjectClass, String template) {
		setClassToInstanciate(presentationObjectClass);
		this.templateForObjectInstanciation = template;
	}
	
	public void setClickConfirmation(String message) {
		setOnClick("return confirm('"+message+"');");
	}

	private void setURIToClassToInstanciate(IWContext iwc) {
		if (this.classToInstanciate != null) {
			if (this.templateForObjectInstanciation != null) {
				if (this.usePublicOjbectInstanciator) {
					this.setURL(iwc.getIWMainApplication().getPublicObjectInstanciatorURI(this.classToInstanciate, this.templateForObjectInstanciation));
				} else {
					this.setURL(iwc.getIWMainApplication().getObjectInstanciatorURI(this.classToInstanciate, this.templateForObjectInstanciation));
				}
			} else {
				if (this.usePublicOjbectInstanciator) {
					this.setURL(iwc.getIWMainApplication().getPublicObjectInstanciatorURI(this.classToInstanciate));
				} else {
					this.setURL(iwc.getIWMainApplication().getObjectInstanciatorURI(this.classToInstanciate));
				}
			}
		}
	}

	private void setURIToWindowOpenerClass(IWContext iwc) {
		if (this._windowClass != null) {
			if (this.usePublicWindow) {
				if (this.icObjectInstanceIDForWindow <= 0) {
					setURL(iwc.getIWMainApplication().getPublicWindowOpenerURI(this._windowClass));
				}
				else {
					setURL(iwc.getIWMainApplication().getPublicWindowOpenerURI(this._windowClass, this.icObjectInstanceIDForWindow));
				}
			} else {
				if (this.icObjectInstanceIDForWindow <= 0) {
					setURL(iwc.getIWMainApplication().getWindowOpenerURI(this._windowClass));
				}
				else {
					setURL(iwc.getIWMainApplication().getWindowOpenerURI(this._windowClass, this.icObjectInstanceIDForWindow));
				}
			}
		}
	}

	public void addEventModel(IWPresentationEvent model) {
		for (Iterator<Parameter> iter = model.getParameters(); iter.hasNext();) {
			Parameter prm = iter.next();
			this.addParameter(prm);
		}
  }

  public void addEventModel(IWPresentationEvent model, IWContext iwc) {
    for (Iterator<Parameter> iter = model.getParameters(); iter.hasNext();) {
      Parameter prm = iter.next();
      this.addParameter(prm);
    }
    setTarget(model.getEventTarget());
    setURL(model.getEventHandlerURL(iwc));
  }

	public void setOutgoing(boolean outgoing) {
		this.isOutgoing = outgoing;	
	}

	/**
	 * 
	 * @uml.property name="protocol"
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * 
	 * @uml.property name="protocol"
	 */
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public void setToolTip(String toolTip) 	{
		super.setToolTip(toolTip);
		if (this._objectType == OBJECT_TYPE_IMAGE) {
			((Image) this._obj).setAlt(toolTip);
		}
	}
	
	public void setLocalizedToolTip(Locale locale, String toolTip) {
		getToolTipLocalizationMap().put(locale, toolTip);
		Image im = (Image) getImageLocalizationMap().get(locale);
		if (im != null) {
			im.setAlt(toolTip);
		}
		im = (Image) getOverImageLocalizationMap().get(locale);
		if (im != null) {
			im.setAlt(toolTip);
		}
	}
	
	private Map<Locale, String> getToolTipLocalizationMap() {
		if (this._toolTipLocalizationMap == null) {
			this._toolTipLocalizationMap = new HashMap<Locale, String>();
		}
		return this._toolTipLocalizationMap;
	}
	
	public void setUsePublicObjectInstanciator(boolean use) {
		this.usePublicOjbectInstanciator = use;
	}
	
	/**
	 * Sets the value of the given interface object when the link is clicked.
	 * @param objectToChange	The interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnClick(InterfaceObject objectToChange, String value) {
		setValueOnClick(objectToChange.getName(), value);
	}

	/**
	 * Sets the value of the given interface object when the link is clicked.
	 * @param objectToChange	The interface object to change value of.
	 * @param value	The new value to set.
	 */
	public void setValueOnClick(String objectToChangeName, String value) {
		setOnClick("changeInputValue(findObj('" + objectToChangeName+ "'), '" + value + "');");
	}
	
	/**
	 * Sets the accesskey html attribute so you can activate this element (causes a "click" on it) with a keyboard command
	 * @param accessKey
	 */
	public void setAccessKey(String accessKey){
		setMarkupAttribute("accesskey",accessKey);
	}
	
	/**
	 * 
	 * @return The access key that has been set for this element
	 */
	public String getAccessKey(){
		return getMarkupAttribute("accesskey");
	}
	
	public String getCharEncoding() {
		return charEncoding;
	}

	public void setCharEncoding(String charEncoding) {
		this.charEncoding = charEncoding;
	}

	public boolean isForceToReplaceAfterEncoding() {
		return forceToReplaceAfterEncoding;
	}

	public void setForceToReplaceAfterEncoding(boolean forceToReplaceAfterEncoding) {
		this.forceToReplaceAfterEncoding = forceToReplaceAfterEncoding;
	}
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		try {
			String className = (String) values[1];
			if (className != null) {
				Object objState = values[2];
				PresentationObject t = (PresentationObject) Class.forName(className).newInstance();
				t.restoreState(context, objState);
				_obj = t;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String className = (String) values[3];
			if (className != null) {
				Object objState = values[4];
				Window t = (Window) Class.forName(className).newInstance();
				t.restoreState(context, objState);
				_myWindow = t;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		iFormToSubmit = ((Boolean) values[5]).booleanValue();
		_windowClass = (Class) values[6];
		try {
			String className = (String) values[7];
			if (className != null) {
				Object objState = values[8];
				Window t = (Window) Class.forName(className).newInstance();
				t.restoreState(context, objState);
				_windowInstance = t;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		icObjectInstanceIDForWindow = ((Integer) values[9]).intValue();
		_parameterString = (StringBuffer) values[10];
		_objectType = (String) values[11];
		windowOpenerJavascriptString = (String) values[12];
		isImageButton = ((Boolean) values[13]).booleanValue();
		isImageTab = ((Boolean) values[14]).booleanValue();
		useTextAsLocalizedTextKey = ((Boolean) values[15]).booleanValue();
		flip = ((Boolean) values[16]).booleanValue();
		isOutgoing = ((Boolean) values[17]).booleanValue();
		hasClass = ((Boolean) values[18]).booleanValue();
		_maintainAllGlobalParameters = ((Boolean) values[19]).booleanValue();
		_maintainBuilderParameters = ((Boolean) values[20]).booleanValue();
		_addSessionId = ((Boolean) values[21]).booleanValue();
		_maintainAllParameters = ((Boolean) values[22]).booleanValue();
		_imageId = ((Integer) values[23]).intValue();
		_hostname = (String) values[24];
		_onMouseOverImageId = ((Integer) values[25]).intValue();
		_onClickImageId = ((Integer) values[26]).intValue();

		try {
			String className = (String) values[27];
			if (className != null) {
				Object objState = values[28];
				Image t = (Image) Class.forName(className).newInstance();
				t.restoreState(context, objState);
				_onMouseOverImage = t;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		
		try {
			String className = (String) values[29];
			if (className != null) {
				Object objState = values[30];
				Image t = (Image) Class.forName(className).newInstance();
				t.restoreState(context, objState);
				_onClickImage = t;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		usePublicWindow = ((Boolean) values[31]).booleanValue();
		classToInstanciate = (Class<? extends UIComponent>) values[32];
		templateForObjectInstanciation = (String) values[33];
		listenerInstances = (List<Object>) values[34];
		maintainedParameters = (List) values[35];
		https = ((Boolean) values[36]).booleanValue();
		protocol = (String) values[37];
		fileId = ((Integer) values[38]).intValue();
		ibPage = ((Integer) values[39]).intValue();
		_overImageLocalizationMap = (Map) values[40];
		_ImageLocalizationMap = (Map) values[41];
		_toolTipLocalizationMap = (Map) values[42];
		usePublicOjbectInstanciator = ((Boolean) values[43]).booleanValue();
		datasource = (String) values[44];
		charEncoding = values[45].toString();
		forceToReplaceAfterEncoding = (values[46] instanceof Boolean) ? (Boolean) values[46] : Boolean.FALSE;
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[47];
		values[0] = super.saveState(context);
		if (_obj != null) {
			Object objState = _obj.saveState(context);
			values[1] = this._obj.getClass().getName();
			values[2] = objState;
		}
		if (_myWindow != null) {
			Object objState = _myWindow.saveState(context);
			values[3] = this._myWindow.getClass().getName();
			values[4] = objState;
		}
		values[5] = new Boolean(iFormToSubmit);
		values[6] = _windowClass;
		if (_windowInstance != null) {
			Object objState = _windowInstance.saveState(context);
			values[7] = this._windowInstance.getClass().getName();
			values[8] = objState;
		}
		values[9] = new Integer(icObjectInstanceIDForWindow);
		values[10] = _parameterString;
		values[11] = _objectType;
		values[12] = windowOpenerJavascriptString;
		values[13] = new Boolean(isImageButton);
		values[14] = new Boolean(isImageTab);
		values[15] = new Boolean(useTextAsLocalizedTextKey);
		values[16] = new Boolean(flip);
		values[17] = new Boolean(isOutgoing);
		values[18] = new Boolean(hasClass);
		values[19] = new Boolean(_maintainAllGlobalParameters);
		values[20] = new Boolean(_maintainBuilderParameters);
		values[21] = new Boolean(_addSessionId);
		values[22] = new Boolean(_maintainAllParameters);
		values[23] = new Integer(_imageId);
		values[24] = _hostname;
		values[25] = new Integer(_onMouseOverImageId);
		values[26] = new Integer(_onClickImageId);
		if (_onMouseOverImage != null) {
			Object objState = _onMouseOverImage.saveState(context);
			values[27] = this._onMouseOverImage.getClass().getName();
			values[28] = objState;
		}
		if (_onClickImage != null) {
			Object objState = _onClickImage.saveState(context);
			values[29] = this._onClickImage.getClass().getName();
			values[30] = objState;
		}
		values[31] = new Boolean(usePublicWindow);
		values[32] = classToInstanciate;
		values[33] = templateForObjectInstanciation;
		values[34] = listenerInstances;
		values[35] = maintainedParameters;
		values[36] = new Boolean(https);
		values[37] = protocol;
		values[38] = new Integer(fileId);
		values[39] = new Integer(ibPage);
		values[40] = _overImageLocalizationMap;
		values[41] = _ImageLocalizationMap;
		values[42] = _toolTipLocalizationMap;
		values[43] = new Boolean(usePublicOjbectInstanciator);
		values[44] = datasource;
		values[45] = charEncoding;
		values[46] = Boolean.valueOf(forceToReplaceAfterEncoding);
		return values;
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException { 
		Collection<UIComponent> children = this.getChildren();
    	for(UIComponent child : children) {
    		if (child instanceof UIParameter) {
    			UIParameter param = (UIParameter) child;
				addParameter(param.getName(), String.valueOf(param.getValue()));
			}
    	}
    	
    	super.encodeBegin(context);
	}
}