//idega 2000-2001 - Tryggvi Larusson
/*
*Copyright 2000-2001 idega.is All Rights Reserved.
*/
package com.idega.presentation;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.idega.block.login.business.LoginBusiness;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.data.IBDomain;
import com.idega.builder.data.IBPage;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.data.ICObject;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.user.data.User;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.io.UploadFile;
import com.idega.presentation.ui.Parameter;
import com.idega.util.datastructures.HashtableMultivalued;
/**
 * A class to serve as the context of a user request in an idegaWeb application.
 * <br>
 * This class gives access to User specific information and Application specific information.
 * <br>
 * An instance of this class should be used under the interfaces com.idega.idegaweb.IWUserContext and
 * com.idega.idegaweb.IWApplicationContext where it is applicable (i.e. when only working with User scoped
 * functionality or Application scoped functionality).
 *<br>
 *
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWContext extends Object implements IWUserContext, IWApplicationContext {
	private HttpServletRequest _request;
	private HttpServletResponse _response;
	private final static String LOCALE_ATTRIBUTE = "idegaweb_locale";
	private final static String WEAK_HASHMAP_KEY = "idegaweb_weak_hashmap";
	private final static String IWAPP_CURRENT_DOMAIN_ID = "iw_current_domain_id";
	//private HttpSession session;
	private String language; //Variable to set the language i.e. HTML
	private String interfaceStyle; //Variable to enable multiple interface looks
	private Connection Conn;
	private Hashtable preferences;
	private String spokenLanguage;
	//private Hashtable sessionAttributes;
	private ServletContext servletContext;
	private boolean _doneHandHeldCheck = false;
	private boolean _clientIsHandHeld = false;
	private boolean isCaching = false;
	private PrintWriter cacheWriter;
	private HashtableMultivalued _multipartParameters = null;
	private UploadFile _uploadedFile = null;
	/**
	 *Default constructor
	 **/
	public IWContext() {
	}
	public IWContext(HttpServletRequest request, HttpServletResponse response) {
		this.setRequest(request);
		this.setResponse(response);
		setLanguage(getRightLanguage(request, response));
		setAllDefault();
	}
	public IWContext(HttpServletRequest request, HttpServletResponse response, String language) {
		this.setRequest(request);
		this.setResponse(response);
		setLanguage(language);
		setAllDefault();
	}
	public IWContext(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		this.setRequest(request);
		this.setResponse(response);
		setLanguage(getRightLanguage(request, response));
		setAllDefault();
		//this.session=session;
	}
	private void setAllDefault() {
		this.interfaceStyle = "default";
		setDefaultBackgroundColor("#FFFFFF");
		setDefaultFontFace("helvetica,arial");
		setDefaultFontSize("2");
		setDefaultFontColor("black");
		setDefaultFontStyle("regular");
		setDefaultPrimaryInterfaceColor("gray");
		setDefaultSecondaryInterfaceColor("dark-gray");
		setDefaultInterfaceFontFace("helvetica,arial");
		setDefaultInterfaceFontColor("#FFFFFF");
		setDefaultInterfaceFontSize("14pt");
		setDefaultInterfaceFontStyle("regular");
		setCompanyPrimaryColor("#000000");
		setCompanySecondaryColor("#FFFFFF");
		setCompanyFontFace("Courier New, Courier, mono");
		setCompanyFontColor("#FF9933");
		setCompanyFontSize("5");
		setCompanyFontStyle("regular");
	}
	/*public void setSession(HttpSession session){
		this.session = session;
	}*/
	public HttpSession getSession() {
		return getRequest().getSession();
	}
	public boolean isMultipartFormData() {
		String contentType = this.getRequestContentType();
		if (contentType != null) {
			return (contentType.indexOf("multipart") != -1);
		} else {
			return false;
		}
	}
	public void setMultipartParameter(String key, String value) {
		if (_multipartParameters == null) {
			_multipartParameters = new HashtableMultivalued();
		}
		_multipartParameters.put(key, value);
	}
	public String getMultipartParameter(String key) {
		if (_multipartParameters != null) {
			return (String) _multipartParameters.get(key);
		} else {
			return null;
		}
	}
	public UploadFile getUploadedFile() {
		return _uploadedFile;
	}
	public void setUploadedFile(UploadFile file) {
		_uploadedFile = file;
	}
	public String getUserAgent() {
		return getRequest().getHeader("User-agent");
	}
	public String getReferer() {
		return getRequest().getHeader("Referer");
	}
	public boolean isMacOS() {
		boolean isMac = false;
		if (getUserAgent().indexOf("Mac") != -1) {
			isMac = true;
		} else if (getUserAgent().indexOf("mac") != -1) {
			isMac = true;
		}
		return isMac;
	}
	public boolean isNetscape() {
		if (getUserAgent().indexOf("Mozilla") != -1) {
			//if not Internet Explorer then Netscape :)
			if (getUserAgent().indexOf("MSIE") != -1) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	public boolean isIE() {
		if (getUserAgent().indexOf("MSIE") != -1) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isOpera() {
		if (getUserAgent().indexOf("Opera") != -1) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isSearchEngine() {
		if (getUserAgent().indexOf("Ultraseek") != -1) {
			return true;
		} else {
			return false;
		}
	}
	/*Under Construction*/
	private String getRightLanguage(HttpServletRequest Request, HttpServletResponse Response) {
		//Todo: set the language to WML when the user-agent is of that type
		//--only implemented for the UPG1 test WAP browser
		String user_agent = Request.getHeader("User-agent");
		if (user_agent != null) {
			//Sets for WML browser
			if (user_agent.indexOf("UPG1") != -1) {
				return IWConstants.MARKUP_LANGUAGE_WML;
			} else if (user_agent.toLowerCase().indexOf("wap") != -1) {
				return IWConstants.MARKUP_LANGUAGE_WML;
			} else if (user_agent.toLowerCase().indexOf("nokia") != -1) {
				return IWConstants.MARKUP_LANGUAGE_WML;
			} else if (user_agent.toLowerCase().indexOf("ericsson") != -1) {
				return IWConstants.MARKUP_LANGUAGE_WML;
			} else if (user_agent.toLowerCase().indexOf("symbian") != -1) {
				return IWConstants.MARKUP_LANGUAGE_WML;
			} else if (user_agent.toLowerCase().indexOf("wapman") != -1) {
				return IWConstants.MARKUP_LANGUAGE_WML;
			} else {
				return IWConstants.MARKUP_LANGUAGE_HTML;
			}
		} else {
			return IWConstants.MARKUP_LANGUAGE_HTML;
		}
	}
	public boolean isParameterSet(String parameterName) {
		if (parameterName == null)
			return false;
		boolean theReturn = false;
		if (getRequest().getParameter(parameterName) != null) {
			theReturn = true;
		}
		if (getRequest().getParameter(parameterName + ".x") != null) {
			theReturn = true;
		}
		return theReturn;
	}
	public boolean isParameterSet(Parameter parameter) {
		boolean theReturn = false;
		if (getRequest().getParameter(parameter.getName()) != null) {
			theReturn = true;
		}
		return theReturn;
	}
	public boolean parameterEquals(Parameter parameter) {
		boolean theReturn = false;
		if (parameter != null) {
			if (getRequest().getParameter(parameter.getName()) != null) {
				if (getRequest().getParameter(parameter.getName()).equals(parameter.getValue())) {
					theReturn = true;
				}
			}
		}
		return theReturn;
	}
	public boolean parameterEquals(String parameterName, String parameterValue) {
		boolean theReturn = false;
		if (getRequest().getParameter(parameterName) != null) {
			if (getRequest().getParameter(parameterName).equals(parameterValue)) {
				theReturn = true;
			}
		}
		return theReturn;
	}
	public void setPreference(String Name, String Value) {
		if (preferences == null) {
			preferences = new Hashtable();
			preferences.put((Object) Name, (Object) Value);
		} else {
			preferences.put((Object) Name, (Object) Value);
		}
	}
	public String getPreference(String name) {
		if (preferences == null) {
			return null;
		} else {
			return (String) preferences.get((Object) name);
		}
	}
	/**
	 * @deprecated
	 */
	public void setRequest(HttpServletRequest request) {
		this._request = request;
	}
	/**
	 * @deprecated
	 */
	public void setResponse(HttpServletResponse response) {
		this._response = response;
	}
	public void setLanguage(String language) {
		this.language = language;
		if (language.equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			setContentType("text/vnd.wap.wml");
		}
		if (language.equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			setContentType("text/html");
		}
	}
	public void setSpokenLanguage(String spokenLanguage) {
		this.spokenLanguage = spokenLanguage;
	}
	public void setInterfaceStyle(String InterfaceStyle) {
		this.interfaceStyle = InterfaceStyle;
	}
	public HttpServletRequest getRequest() {
		return this._request;
	}
	public Cookie[] getCookies() {
		return this.getRequest().getCookies();
	}
	public void addCookies(Cookie cookie) {
		this.getResponse().addCookie(cookie);
	}
	public boolean isCookieSet(String cookieName) {
		Cookie[] cookies = (Cookie[]) this.getCookies();
		boolean returner = false;
		if (cookies != null) {
			if (cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals(cookieName)) {
						returner = true;
						continue;
					}
				}
			}
		}
		return returner;
	}
	public String getParameter(String parameterName) {
		String prm = null;
		if (_multipartParameters != null) {
			prm = getMultipartParameter(parameterName);
		} else {
			prm = getRequest().getParameter(parameterName);
		}
		return prm;
	}
	public Enumeration getParameterNames() {
		if (_multipartParameters != null) {
			return _multipartParameters.keys();
		} else {
			return getRequest().getParameterNames();
		}
	}
	public String[] getParameterValues(String parameterName) {
		if (_multipartParameters != null) {
			Collection values = _multipartParameters.getCollection(parameterName);
			if (values != null) {
				return (String[]) values.toArray(new String[values.size()]);
			} else {
				return null;
			}
		} else {
			return getRequest().getParameterValues(parameterName);
		}
	}
	public String getQueryString() {
		return getRequest().getQueryString();
	}
	public HttpServletResponse getResponse() {
		return this._response;
	}
	public Object getSessionAttribute(String attributeName) {
		return getSession().getAttribute(attributeName);
	}
	public void setSessionAttribute(String attributeName, Object attribute) {
		getSession().setAttribute(attributeName, attribute);
	}
	public String getSessionId() {
		return getSession().getId();
	}
	/**
	 * @deprecated Replaced with removeSessionAttribute()
	 */
	public void removeAttribute(String attributeName) {
		removeSessionAttribute(attributeName);
	}
	public void removeSessionAttribute(String attributeName) {
		getSession().removeAttribute(attributeName);
	}
	public String getLanguage() {
		return this.language;
	}
	public String getSpokenLanguage() {
		if (this.spokenLanguage == null)
			this.setSpokenLanguage("IS");
		return this.spokenLanguage;
	}
	public String getInterfaceStyle() {
		return this.interfaceStyle;
	}
	/**
	 * @deprecated
	 */
	public Connection getConnection() {
		return this.Conn;
	}
	/**
	 * @deprecated
	 */
	public void setConnection(Connection conn) {
		this.Conn = Conn;
	}
	public void setDefaultBackgroundColor(String s) {
		setPreference("DefaultBackgroundColor", s);
	}
	public String getDefaultBackgroundColor() {
		return getPreference("DefaultBackgroundColor");
	}
	public void setDefaultFontFace(String s) {
		setPreference("DefaultFontFace", s);
	}
	public String getDefaultFontFace() {
		return getPreference("DefaultFontFace");
	}
	public void setDefaultFontSize(String s) {
		setPreference("DefaultFontSize", s);
	}
	public String getDefaultFontSize() {
		return getPreference("DefaultFontSize");
	}
	public void setDefaultFontColor(String s) {
		setPreference("DefaultFontColor", s);
	}
	public String getDefaultFontColor() {
		return getPreference("DefaultFontColor");
	}
	public void setDefaultFontStyle(String s) {
		setPreference("DefaultFontStyle", s);
	}
	public String getDefaultFontStyle() {
		return getPreference("DefaultFontStyle");
	}
	public void setDefaultPrimaryInterfaceColor(String s) {
		setPreference("DefaultPrimaryInterfaceColor", s);
	}
	public String getDefaultPrimaryInterfaceColor() {
		return getPreference("DefaultPrimaryInterfaceColor");
	}
	public void setDefaultSecondaryInterfaceColor(String s) {
		setPreference("DefaultSecondaryInterfaceColor", s);
	}
	public String getDefaultSecondaryInterfaceColor() {
		return getPreference("DefaultSecondaryInterfaceColor");
	}
	public void setDefaultInterfaceFontFace(String s) {
		setPreference("DefaultInterfaceFontFace", s);
	}
	public String getDefaultInterfaceFontFace() {
		return getPreference("DefaultInterfaceFontFace");
	}
	public void setDefaultInterfaceFontColor(String s) {
		setPreference("DefaultInterfaceFontColor", s);
	}
	public String getDefaultInterfaceFontColor() {
		return getPreference("DefaultInterfaceFontColor");
	}
	public void setDefaultInterfaceFontSize(String s) {
		setPreference("DefaultInterfaceFontSize", s);
	}
	public String getDefaultInterfaceFontSize() {
		return getPreference("DefaultInterfaceFontSize");
	}
	public void setDefaultInterfaceFontStyle(String s) {
		setPreference("DefaultInterfaceFontStyle", s);
	}
	public String getDefaultInterfaceFontStyle() {
		return getPreference("DefaultInterfaceFontStyle");
	}
	public void setCompanyPrimaryColor(String s) {
		setPreference("CompanyPrimaryColor", s);
	}
	public String getCompanyPrimaryColor() {
		return getPreference("CompanyPrimaryColor");
	}
	public void setCompanySecondaryColor(String s) {
		setPreference("CompanySecondaryColor", s);
	}
	public String getCompanySecondaryColor() {
		return getPreference("CompanySecondaryColor");
	}
	public void setCompanyFontFace(String s) {
		setPreference("CompanyFontFace", s);
	}
	public String getCompanyFontFace() {
		return getPreference("CompanyFontFace");
	}
	public void setCompanyFontColor(String s) {
		setPreference("CompanyFontColor", s);
	}
	public String getCompanyFontColor() {
		return getPreference("CompanyFontColor");
	}
	public void setCompanyFontSize(String s) {
		setPreference("CompanyFontSize", s);
	}
	public String getCompanyFontSize() {
		return getPreference("CompanyFontSize");
	}
	public void setCompanyFontStyle(String s) {
		setPreference("CompanyFontStyle", s);
	}
	public String getCompanyFontStyle() {
		return getPreference("CompanyFontStyle");
	}
	/*private Hashtable getSessionHashtable(){
	  Hashtable sessionAttributes;
	  Object theAttribute = this.getSession().getAttribute("idega_special_session_attribute");
	  if (theAttribute==null){
	    sessionAttributes=new Hashtable();
	    this.getSession().setAttribute("idega_special_session_attribute",sessionAttributes);
	  }
	  else{
	    return (Hashtable) theAttribute;
	  }
	  return sessionAttributes;
	}
	
	public void setIdegaSessionAttribute(String attributeName,Object attributeValue){
	  getSessionHashtable().put(attributeName,attributeValue);
	}
	
	public Object getIdegaSessionAttribute(String attributeName){
	  return getSessionHashtable().get(attributeName);
	}
	
	public void removeIdegaSessionAttribute(String attributeName){
	  getSessionHashtable().remove(attributeName);
	}
	
	public void removeAllIdegaSessionAttributes(){
	  getSessionHashtable().clear();
	}*/
	/**
	 * @ deprecated replaced width getApplication
	 */
	public ServletContext getServletContext() {
		return servletContext;
	}
	public void setServletContext(ServletContext context) {
		this.servletContext = context;
	}
	/**
	 * @deprecated UNIMPLEMENTED
	 */
	public void maintainParameter(Parameter parameter) {
		Hashtable theParameters = (Hashtable) this.getSessionAttribute("idega_special_maintained_parameters");
		if (theParameters == null) {
			theParameters = new Hashtable();
			theParameters.put(parameter.getName(), parameter);
		} else {
			//Parameter previousParameter = theParameters.get(parameter.getName());
			theParameters.put(parameter.getName(), parameter);
		}
	}
	/**
	 * @deprecated UNIMPLEMENTED
	 */
	public Map getTheMaintainedParameters() {
		return (Map) this.getSessionAttribute("idega_special_maintained_parameters");
	}
	/*public void maintainParameter(String parameterName,String parameterValue){
	  setSessionAttribute(new Parameter(parameterName,parameterValue));
	}*/
	public String getRequestURI() {
		return getRequest().getRequestURI();
	}
	public String getServerName() {
		return getRequest().getServerName();
	}
	public int getServerPort() {
		return getRequest().getServerPort();
	}
	public PrintWriter getWriter() throws IOException {
		if (cacheWriter == null) {
			return getResponse().getWriter();
		} else {
			if (this.isCacheing()) {
				return cacheWriter;
			} else {
				return getResponse().getWriter();
			}
		}
	}
	public void sendRedirect(String URL) {
		try {
			getResponse().sendRedirect(getResponse().encodeRedirectURL(URL));
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
	public void setApplicationAttribute(String attributeName, Object attributeValue) {
		getApplication().setAttribute(attributeName, attributeValue);
	}
	public Object getApplicationAttribute(String attributeName) {
		return getApplication().getAttribute(attributeName);
	}
	public void removeApplicationAttribute(String attributeName) {
		getApplication().removeAttribute(attributeName);
	}
	public IWMainApplication getApplication() {
		return IWMainApplication.getIWMainApplication(this.servletContext);
	}
	public IWMainApplicationSettings getApplicationSettings() {
		return getApplication().getSettings();
	}
	public Locale getCurrentLocale() {
		Locale theReturn = (Locale) this.getSessionAttribute(LOCALE_ATTRIBUTE);
		if (theReturn == null) {
			theReturn = getApplication().getSettings().getDefaultLocale();
			setCurrentLocale(theReturn);
		}
		return theReturn;
	}
	public int getCurrentLocaleId() {
		return ICLocaleBusiness.getLocaleId(getCurrentLocale());
	}
	public void setCurrentLocale(Locale locale) {
		this.setSessionAttribute(LOCALE_ATTRIBUTE, locale);
	}
	/**
	 * Sets the object with Weak reference so that it could be garbagecollected anytime
	 */
	public void setSessionAttributeWeak(String attributeName, Object attributeValue) {
		getWeakHashMap().put(attributeName, attributeValue);
	}
	public Object getSessionAttributeWeak(String propertyName) {
		return getWeakHashMap().get(propertyName);
	}
	private Map getWeakHashMap() {
		WeakHashMap map = (WeakHashMap) getSessionAttribute(WEAK_HASHMAP_KEY);
		if (map == null) {
			map = new WeakHashMap();
			setSessionAttribute(WEAK_HASHMAP_KEY, map);
		}
		return map;
	}
	public void setContentType(String contentType) {
		getResponse().setContentType(contentType);
	}
	void setCacheing(boolean ifCacheing) {
		this.isCaching = ifCacheing;
	}
	boolean isCacheing() {
		return isCaching;
	}
	public void setCacheWriter(PrintWriter writer) {
		this.cacheWriter = writer;
	}
	public User getUser() {
		return (LoginBusiness.getUser(this));
	}
	public int getUserId() {
		User usr = getUser();
		if (usr != null) {
			return (usr.getID());
		}
		return -1;
	}
	public AccessController getAccessController() {
		return ((AccessController) this.getApplication().getAccessController());
	}
	public String getRequestContentType() {
		return getRequest().getContentType();
	}
	public String getRemoteIpAddress() {
		return getRequest().getRemoteAddr();
	}
	public String getRemoteHostName() {
		return getRequest().getRemoteHost();
	}
	public String getRemoteUser() {
		return getRequest().getRemoteUser();
	}
	public boolean hasPermission(String permissionKey, PresentationObject obj) {
		try {
			return this.getAccessController().hasPermission(permissionKey, obj, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasViewPermission(PresentationObject obj) {
		return this.hasPermission(AccessController._PERMISSIONKEY_VIEW, obj);
	}
	public boolean hasEditPermission(PresentationObject obj) {
		return this.hasPermission(AccessController._PERMISSIONKEY_EDIT, obj);
	}
	public boolean hasPermission(List groupIds, String permissionKey, PresentationObject obj) {
		try {
			return this.getAccessController().hasPermission(groupIds, permissionKey, obj, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasFilePermission(String permissionKey, int id) {
		try {
			return this.getAccessController().hasFilePermission(permissionKey, id, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId) {
		try {
			return this.getAccessController().hasDataPermission(permissionKey, obj, entityRecordId, this);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean hasViewPermission(List groupIds, PresentationObject obj) {
		return this.hasPermission(groupIds, AccessController._PERMISSIONKEY_VIEW, obj);
	}
	public boolean hasEditPermission(List groupIds, PresentationObject obj) {
		return this.hasPermission(groupIds, AccessController._PERMISSIONKEY_EDIT, obj);
	}
	public boolean isSuperAdmin() {
		try {
			if (this.isLoggedOn())
				return this.getUser().equals(this.getAccessController().getAdministratorUser());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	public boolean isLoggedOn() {
		return com.idega.block.login.business.LoginBusiness.isLoggedOn(this);
	}
	/**
	 * Expensive method, not recommended to use frequently
	 *
	 * @throws UnavailableIWContext if the IWContext is not set
	 *
	 */
	public static IWContext getInstance() throws UnavailableIWContext {
		return com.idega.servlet.IWPresentationServlet.getIWContext();
	}
	public String getCurrentState(PresentationObject obj) {
		if (obj != null) {
			return getCurrentState(obj.getParentObjectInstanceID());
		} else {
			return null;
		}
	}
	/**
	* @todo implement
	*/
	public String getCurrentState(int instanceId) {
		String historyId = this.getParameter(BuilderLogic.PRM_HISTORY_ID);
		//System.err.println("in iwc.getCurrentState()");
		if (historyId != null) {
			//System.err.println("historyId != null");
			HttpSession s = this.getSession();
			//System.err.println(" - from Session.hashCode() -> "+s.hashCode());
			List historyList = (List) s.getAttribute(BuilderLogic.SESSION_OBJECT_STATE);
			//List historyList = (List)this.getSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE);
			if (historyList != null && historyList.contains(historyId)) {
				int index = historyList.indexOf(historyId);
				//System.err.println("current state historyIndex = "+index + " for instance " + instanceId);
				Object ob = ((Hashtable) historyList.get(index + 1)).get(Integer.toString(instanceId));
				//System.err.println("current state = "+ob);
				//System.err.println("iwc.getCurrentState() ends");
				return (String) ob;
			}
		}
		//System.err.println("iwc.getCurrentState() ends");
		return null;
	}
	public IWApplicationContext getApplicationContext() {
		return this;
	}
	public boolean isInPreviewMode() {
		boolean preview = false;
		if (isParameterSet("view")) {
			if (BuilderLogic.getInstance().isBuilderApplicationRunning(this)) {
				String view = getParameter("view");
				if (view.equals("preview"))
					preview = true;
			}
		}
		return (preview);
	}
	public boolean isInEditMode() {
		boolean edit = false;
		if (isParameterSet("view")) {
			if (BuilderLogic.getInstance().isBuilderApplicationRunning(this)) {
				String view = getParameter("view");
				if (view.equals("builder"))
					edit = true;
			}
		}
		return (edit);
	}
	/**
	 * @return true if the client is a handheld device such as a PalmPilot, a PocketPC device or a phone
	 */
	public boolean isClientHandheld() {
		if (!_doneHandHeldCheck) {
			String user_agent = this.getUserAgent();
			if (user_agent.indexOf("Windows CE") != -1) {
				_clientIsHandHeld = true;
			} else if (user_agent.indexOf("Palm") != -1) {
				_clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("wap") != -1) {
				_clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("nokia") != -1) {
				_clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("ericsson") != -1) {
				_clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("symbian") != -1) {
				_clientIsHandHeld = true;
			} else if (user_agent.toLowerCase().indexOf("wapman") != -1) {
				_clientIsHandHeld = true;
			}
			_doneHandHeldCheck = true;
		}
		return _clientIsHandHeld;
	}
	public IBDomain getDomain() throws RemoteException {
		try {
			String id = (String) this.getApplicationAttribute(IWAPP_CURRENT_DOMAIN_ID);
			int domainID = 1;
			if (id != null) {
				try {
					domainID = Integer.parseInt(id);
				} catch (NumberFormatException nfe) {
				}
			}
			return com.idega.builder.data.IBDomainBMPBean.getDomain(domainID);
			/**
			 * @todo: Comment in when EntityBeanCaching is default on:
			 */
			//IBDomainHome domainHome = (IBDomainHome)IDOLookup.getHome(IBDomain.class);
			//return domainHome.findByPrimaryKey(domainID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	public void forwardToIBPage(Page fromPage, IBPage page) {
		/**@todo temporary workaround find out why this doesn't work
		 * This is supposed to work but I always get: IllegalStateException: cannot forward because writer or stream has been obtained.
		 */
		/*try{
		  RequestDispatcher req = this.getRequest().getRequestDispatcher(BuilderLogic.getInstance().getIBPageURL(this.getApplicationContext(),((Integer)page.getPrimaryKeyValue()).intValue()));
		  req.forward(this.getRequest(),this.getResponse());
		}
		catch(Exception e){
		 e.printStackTrace(System.err);
		}
		
		this does not work either
		sendRedirect(URL.toString());
		
		
		*/
		StringBuffer URL = new StringBuffer();
		URL.append(BuilderLogic.getInstance().getIBPageURL(this.getApplicationContext(), ((Integer) page.getPrimaryKeyValue()).intValue()));
		URL.append('&');
		URL.append(getRequest().getQueryString());
		fromPage.setToRedirect(URL.toString());
		fromPage.empty();
	}
}
