/*
 * $Id: LinkContainer.java,v 1.17 2004/06/24 14:01:51 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.text;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICBuilderConstants;
import com.idega.core.builder.data.ICPage;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.ui.Window;
import com.idega.repository.data.ImplementorRepository;
import com.idega.util.StringHandler;
import com.idega.util.text.TextSoap;

/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.2
 *@modified by  <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public class LinkContainer extends PresentationObjectContainer {

	private StringBuffer _parameterString;
	private boolean openInNewWindow = false;

	public static final String HASH = "#";
	public static final String TARGET_ATTRIBUTE = "target";
	public static final String HREF_ATTRIBUTE = "href";

	public static final String TARGET_NEW_WINDOW = "_new";
	public static final String TARGET_SELF_WINDOW = "_self";
	public static final String TARGET_BLANK_WINDOW = "_blank";
	public static final String TARGET_PARENT_WINDOW = "_parent";
	public static final String TARGET_TOP_WINDOW = "_top";

	private static final  String IB_PAGE_PARAMETER = ((ICBuilderConstants) ImplementorRepository.getInstance().getImplementorOrNull(ICBuilderConstants.class, LinkContainer.class)).getPageParameter();
	
	private boolean _addSessionId = true;

	private String _windowWidth, _windowHeight, _windowName;
	private boolean _toolbar, _location, _directories, _status, _menu, _title, _scroll, _resize, _fullscreen = false;
	private ICFile _file;
	/**
	 *
	 */
	public void setURL(String url) {
		StringTokenizer urlplusprm = new StringTokenizer(url, "?");
		String newUrl = urlplusprm.nextToken();
		if (urlplusprm.hasMoreTokens()) {
			String prm = urlplusprm.nextToken();
			StringTokenizer param = new StringTokenizer(prm, "=&");
			while (param.hasMoreTokens()) {
				String p = param.nextToken();
				String v = null;
				if (param.hasMoreTokens()) {
					v = param.nextToken();
				}
				if (v != null) {
					this.addParameter(p, v);
				}
			}
		}
		setMarkupAttribute(HREF_ATTRIBUTE, newUrl);
	}

	public String getURL() {
		return (getMarkupAttribute(HREF_ATTRIBUTE));
	}

	/**
	 *
	 */
	public void addParameter(String parameterName, Class theClass) {
		addParameter(parameterName, IWMainApplication.getEncryptedClassName(theClass));
	}

	public boolean isParameterSet(String prmName) {
		if (_parameterString != null) {
			if (!(prmName != null && !prmName.equals(""))) {
				return true;
			}
			String prmString = _parameterString.toString();
			if (prmString.length() > 0) {
				if ((prmString.charAt(0) == '?') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				if ((prmString.charAt(0) == '&') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				StringTokenizer token = new StringTokenizer(prmString, "&=", false);
				int index = 0;
				while (token.hasMoreTokens()) {
					String st = token.nextToken();
					if (token.hasMoreTokens()) {
						token.nextToken();
						if (prmName.equals(st)) {
							return true;
							//System.out.println("token "+index+" : "+st+" / true");
							//System.err.println("token "+index+" : "+st+" / true");
						}
						//else{
						//System.out.println("token "+index+" : "+st+" / false");
						//System.err.println("token "+index+" : "+st+" / false");
						//}
						index++;
					}
				}
			}
			else {
				return false;
			}
		}
		return false; // false
	}

	/**
	 *
	 */
	public void addParameter(String parameterName, String parameterValue) {
		if ((parameterName != null) && (parameterValue != null)) {
			parameterName = java.net.URLEncoder.encode(parameterName);
			parameterValue = java.net.URLEncoder.encode(parameterValue);

			if (_parameterString == null) {
				_parameterString = new StringBuffer();
				_parameterString.append("?");
			}
			else {
				_parameterString.append("&");
			}

			_parameterString.append(parameterName);
			_parameterString.append("=");
			_parameterString.append(parameterValue);
		}
		else
			if (parameterName != null) {
				parameterName = java.net.URLEncoder.encode(parameterName);
			}
			else
				if (parameterValue != null) {
					parameterValue = java.net.URLEncoder.encode(parameterValue);
				}
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
	public void setTarget(String target) {
		setMarkupAttribute(TARGET_ATTRIBUTE, target);
	}

	/**
	 *
	 */
	public void setToOpenInNewWindow(boolean newWindow) {
		if (newWindow)
			setMarkupAttribute(TARGET_ATTRIBUTE, TARGET_NEW_WINDOW);
	}

	/**
	 *
	 */
	private void setSessionId(boolean addSessionId) {
		_addSessionId = addSessionId;
	}

	/**
	 *
	 */
	public void setLocale(String languageString) {
		//setEventListener(LocaleSwitcher.class.getName());
		addParameter(LocaleSwitcher.languageParameterString, languageString);
	}

	/**
	 * method for adding a link to a page object
	 */
	public void setPage(ICPage page) {
		if ((page != null) && (page.getID() != -1)) {
			String value = this.getParameterValue(IB_PAGE_PARAMETER);
			if (value != null) {
				removeParameter(IB_PAGE_PARAMETER);
			}
			addParameter(IB_PAGE_PARAMETER, page.getID());
		}
	}

	public int getPage() {
		String value = this.getParameterValue(IB_PAGE_PARAMETER);
		if (value != null && !value.equals("")) {
			return Integer.parseInt(value);
		}
		else {
			return 0;
		}
	}

	public String getParameterValue(String prmName) {
		if (_parameterString != null) {
			if (!(prmName != null && prmName.endsWith(""))) {
				return null;
			}
			String prmString = _parameterString.toString();
			if (prmString.length() > 0) {
				if ((prmString.charAt(0) == '?') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				if ((prmString.charAt(0) == '&') && (prmString.length() > 1)) {
					prmString = prmString.substring(1, prmString.length());
				}
				StringTokenizer token = new StringTokenizer(prmString, "&=", false);
				int index = 0;
				while (token.hasMoreTokens()) {
					String st = token.nextToken();
					if (token.hasMoreTokens()) {
						String value = token.nextToken();
						if (prmName.equals(st)) {
							return value;
						}
						index++;
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
	 */
	public void setFile(ICFile file) {
		_file = file;
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
	public synchronized Object clone() {
		LinkContainer linkObj = null;
		try {
			linkObj = (LinkContainer) super.clone();

			linkObj._parameterString = _parameterString;
			linkObj._addSessionId = _addSessionId;

			if (_parameterString != null) {
				linkObj._parameterString = new StringBuffer(_parameterString.toString());
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
		List list = com.idega.idegaweb.IWURL.getGloballyMaintainedParameters(iwc);
		if (list != null) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				String parameterName = (String) iter.next();
				String parameterValue = iwc.getParameter(parameterName);
				if (parameterValue != null) {
					if (!this.isParameterSet(parameterName)) {
						addParameter(parameterName, parameterValue);
					}
				}
			}
		}
	}

	/**
	 *
	 */
	protected String getParameterString(IWContext iwc, String URL) {
		if (isLinkOpeningOnSamePage()) {
			addTheMaintainedParameters(iwc);
		}

		if (URL == null) {
			URL = "";
		}

		if ((!this.isParameterSet("idega_session_id"))) {
			if (_parameterString == null) {
				_parameterString = new StringBuffer();
				if (_addSessionId && (!iwc.isSearchEngine())) {
					if (URL.equals("#")) {
						return ("");
					}
					else
						if (URL.indexOf("://") == -1) { //does not include ://
							if (URL.indexOf("?") != -1) {
								_parameterString.append("&idega_session_id=");
								_parameterString.append(iwc.getSession().getId());
								return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
							}
							else
								if ((URL.indexOf("//") != -1) && (URL.lastIndexOf("/") == URL.lastIndexOf("//") + 1)) {
									//the case where the URL is etc. http://www.idega.is
									_parameterString.append("/?idega_session_id=");
									_parameterString.append(iwc.getSession().getId());
									return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
								}
								else {
									if (URL.indexOf("/") != -1) {
										//If the URL ends with a "/"
										if (URL.lastIndexOf("/") == (URL.length() - 1)) {
											_parameterString.append("?idega_session_id=");
											_parameterString.append(iwc.getSession().getId());
											return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
										}
										else {
											//There is a dot after the last "/" interpreted as a file not a directory
											if (URL.lastIndexOf(".") > URL.lastIndexOf("/")) {
												_parameterString.append("?idega_session_id=");
												_parameterString.append(iwc.getSession().getId());
												return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
											}
											else {
												_parameterString.append("/?idega_session_id=");
												_parameterString.append(iwc.getSession().getId());
												return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
											}
										}
									}
									else {
										_parameterString.append("?idega_session_id=");
										_parameterString.append(iwc.getSession().getId());
										return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
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
					if (_addSessionId && (!iwc.isSearchEngine())) {
						if (_parameterString.toString().indexOf("?") == -1) {
							_parameterString.insert(0, '?');
						}
						_parameterString.append("&");
						if (URL.indexOf("://") == -1) {
							_parameterString.append("idega_session_id=");
							_parameterString.append(iwc.getSession().getId());
						}
					}
				}
				else {
					if (_addSessionId && (!iwc.isSearchEngine())) {
						_parameterString.append("&");
						if (URL.indexOf("://") == -1) {
							_parameterString.append("idega_session_id=");
							_parameterString.append(iwc.getSession().getId());
						}
					}
				}
				return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
			}
		}
		if (_parameterString != null) {
			return (TextSoap.convertSpecialCharacters(_parameterString.toString()));
		}
		else {
			return ("");
		}
	}

	/**
	 *
	 */
	public void clearParameters() {
		_parameterString = null;
	}

	protected void setFinalUrl(String url) {
		setMarkupAttribute(HREF_ATTRIBUTE, url);
	}

	/**
	 *
	 */
	public void print(IWContext iwc) throws Exception {

		boolean addParameters = true;
		String oldURL = getURL();

		if (!com.idega.core.accesscontrol.business.LoginBusinessBean.isLoggedOn(iwc)) {
			setSessionId(false);
		}

		if (oldURL == null) {
			oldURL = iwc.getRequestURI();
			setFinalUrl(oldURL);
		}
		else
			if (oldURL.equals(StringHandler.EMPTY_STRING)) {
				oldURL = iwc.getRequestURI();
				setFinalUrl(oldURL);
			}

		if (isLinkOpeningOnSamePage()) {
			addTheMaintainedParameters(iwc);
		}

		if (oldURL.equals(HASH)) {
			addParameters = false;
		}

		if (getLanguage().equals("HTML")) {
			if (openInNewWindow) {
				String URL = getURL();
				if (getPage() != 0)
				{
					BuilderService bservice = getBuilderService(iwc);
					URL = bservice.getPageURI(getPage());
				}
				else{
					URL = URL + getParameterString(iwc, URL);
				}
				
				if (_windowName == null)
					_windowName = "Popup";
				if (_windowWidth == null)
					_windowWidth = "400";
				if (_windowHeight == null)
					_windowHeight = "400";

				setFinalUrl("javascript:" + Window.getWindowCallingScript(URL, _windowName, _toolbar, _location, _directories, _status, _menu, _title, _scroll, _resize, _fullscreen, Integer.parseInt(_windowWidth), Integer.parseInt(_windowHeight)));
			}
			else {
				if (_file != null) {
					ICFileSystem fsystem = getICFileSystem(iwc);
					setFinalUrl(fsystem.getFileURI(_file));
				}
				else {
					setFinalUrl(oldURL + getParameterString(iwc, oldURL));
				}
			}

			print("<a " + getMarkupAttributesString() + " >");

			List theObjects = this.getChildren();
			if (theObjects != null) {
				Iterator iter = theObjects.iterator();
				while (iter.hasNext()) {
					PresentationObject item = (PresentationObject) iter.next();
					item._print(iwc);
				}
			}

			print("</a>");
		}
		else
			if (getLanguage().equals("WML")) {
				if (addParameters) {
					setFinalUrl(oldURL + getParameterString(iwc, oldURL));
				}
				print("<a " + getMarkupAttributesString() + " >");

				List theObjects = this.getChildren();
				if (theObjects != null) {
					Iterator iter = theObjects.iterator();
					while (iter.hasNext()) {
						PresentationObject item = (PresentationObject) iter.next();
						item._print(iwc);
					}
				}

				print("</a>");
			}

		setFinalUrl(oldURL);
	}

	/**
	 *
	 */
	public void setAsBackLink(boolean asBackLink, int backUpHowManyPages) {
		if (asBackLink) {
			setMarkupAttribute("onClick", "history.go(-" + backUpHowManyPages + ")");
			setFinalUrl(HASH);
		}
	}

	/**
	 *
	 */
	public void setAsBackLink(boolean asBackLink) {
		setAsBackLink(asBackLink, 1);
	}

	/**
	 *
	 */
	public void setAsBackLink(int backUpHowManyPages) {
		setAsBackLink(true, backUpHowManyPages);
	}

	/**
	 *
	 */
	private void setEventListener(String eventListenerClassName) {
		addParameter(IWMainApplication.IdegaEventListenerClassParameter, IWMainApplication.getEncryptedClassName(eventListenerClassName));
	}

	public void setAsPopup(String name, String width, String height) {
		setAsPopup(name, width, height, false, false, false, false, false, false, false, false, false);
	}

	public void setAsPopup(String name, String width, String height, boolean toolbar, boolean location, boolean directories, boolean status, boolean menu, boolean title, boolean scroll, boolean resize, boolean fullscreen) {
		_windowWidth = width;
		_windowHeight = height;
		_windowName = name;
		_toolbar = toolbar;
		_location = location;
		_directories = directories;
		_status = status;
		_menu = menu;
		_title = title;
		_scroll = scroll;
		_resize = resize;
		_fullscreen = fullscreen;
		openInNewWindow = true;
	}

	public void removeParameter(String prmName) {
		if (_parameterString != null) {
			if (!(prmName != null && !prmName.equals(""))) {
				return;
			}

			StringBuffer newBuffer = new StringBuffer();
			String prmString = _parameterString.toString();

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
						String value = token2.nextToken();
						if (!name.equals(prmName)) {
							if (!firstToken) {
								newBuffer.append("&");
							}
							newBuffer.append(name);
							newBuffer.append("=");
							newBuffer.append(value);
						}
					}
					/*else {
					  newBuffer.append("&" + st);
					}*/
					firstToken = false;
				}
			}
			_parameterString = newBuffer;
			return;
		}
	}
}
