//idega 2000-2001 - Tryggvi Larusson
/*
*Copyright 2000-2001 idega.is All Rights Reserved.
*/
package com.idega.presentation;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.Message;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ApplicationEvent;
import javax.faces.event.FacesEvent;
import javax.faces.lifecycle.ApplicationHandler;
import javax.faces.lifecycle.ViewHandler;
import javax.faces.tree.Tree;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.builder.business.BuilderConstants;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.user.data.User;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWSystemProperties;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.io.UploadFile;
import com.idega.presentation.ui.Parameter;
import com.idega.user.business.UserProperties;
import com.idega.user.util.Converter;
import com.idega.util.datastructures.HashtableMultivalued;
import com.idega.util.reflect.MethodFinder;
import com.idega.util.reflect.MethodInvoker;
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
public class IWContext
extends javax.faces.context.FacesContext
implements IWUserContext, IWApplicationContext {

	private HttpServletRequest _request;
	private HttpServletResponse _response;
	private final static String LOCALE_ATTRIBUTE = "idegaweb_locale";
	private final static String WEAK_HASHMAP_KEY = "idegaweb_weak_hashmap";
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
	private PrintWriter writer = null;
	private HashtableMultivalued _multipartParameters = null;
	private UploadFile _uploadedFile = null;
	//Defined as private static variables to speed up reflection:
	private static Object builderLogicInstance;
	private static Method methodIsBuilderApplicationRunning;
	
	protected static final String IWC_SESSION_ATTR_NEW_USER_KEY = "iwc_new_user";
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
	public boolean isSafari() {
		if (getUserAgent().indexOf("Safari") != -1) {
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
		String value = getParameter(parameterName) ;
		
		if (value!= null && value.length() > 0) {
			theReturn = true;
		}
		
		value = getParameter(parameterName + ".x");
		
		if(  value != null && value.length() > 0) {
			theReturn = true;
		}
		return theReturn;
	}
	
	
	public boolean isParameterSet(Parameter parameter) {
		return isParameterSet( parameter.getName() );
	}
	
	public boolean parameterEquals(Parameter parameter) {
		boolean theReturn = false;
		if (parameter != null) {
			if (getParameter(parameter.getName()) != null) {
				if (getParameter(parameter.getName()).equals(parameter.getValueAsString())) {
					theReturn = true;
				}
			}
		}
		return theReturn;
	}
	public boolean parameterEquals(String parameterName, String parameterValue) {
		boolean theReturn = false;
		if (getParameter(parameterName) != null) {
			if (getParameter(parameterName).equals(parameterValue)) {
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
		if (language.equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			setContentType("text/html");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_WML)) {
			setContentType("text/vnd.wap.wml");
		}
		else if (language.equals(IWConstants.MARKUP_LANGUAGE_PDF_XML)) {
			setContentType("application/pdf");
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
		this.Conn = conn;
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
	
	public String getProtocol(){
		return getRequest().getProtocol();
	}
	
	public int getServerPort() {
		return getRequest().getServerPort();
	}
	public PrintWriter getWriter() throws IOException {
		if (this.isCacheing() && cacheWriter!=null) {
			return cacheWriter;
		} else {
			if( writer == null ){
				writer = getResponse().getWriter(); 
			}
			
			return writer;
		}
		
	}
	
	public boolean isWriterNull(){
		return (writer==null);
	}
	
	public void setWriter(PrintWriter writer){
		this.writer = writer;
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
		return IWMainApplication.getIWMainApplication(getServletContext());
	}
	public IWMainApplicationSettings getApplicationSettings() {
		return getApplication().getSettings();
	}
  public IWSystemProperties getSystemProperties() {
  	return getApplication().getSystemProperties();
  }
  public UserProperties getUserProperties() {
  	return (UserProperties) LoginBusinessBean.getUserProperties(this);
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
	
	/**
	 * Only handles http and https, use getServerURLWithoutProtocol() for other stuff.
	 * @return the servername with port and protocol, e.g. http://www.idega.com:8080/
	 */
	public String getServerURL(){
		StringBuffer buf = new StringBuffer();
		if(isSecure()){
				buf.append("https://");
		}
		else{
			buf.append("http://");
		}

		buf.append(getServerName());
		if( getServerPort()!=80 ){
			buf.append(":").append(getServerPort());
		}
		
		buf.append("/");
		
		return buf.toString();
	}
	
	/**
	 * 
	 * @return the servername with port and protocol, e.g. http://www.idega.com:8080/
	 */
	public String getServerURLWithoutProtocol(){
		StringBuffer buf = new StringBuffer();

		buf.append(getServerName());
		if( getServerPort()!=80 ){
			buf.append(":").append(getServerPort());
		}
		
		buf.append("/");
		
		return buf.toString();
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

	/**
   	* @deprecated Replaced with getCurrentUser()
   	**/
	public User getUser() {
		return (LoginBusinessBean.getUser(this));
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
		return this.hasPermission(AccessController.PERMISSION_KEY_VIEW, obj);
	}
	public boolean hasEditPermission(PresentationObject obj) {
		return this.hasPermission(AccessController.PERMISSION_KEY_EDIT, obj);
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
		return this.hasPermission(groupIds, AccessController.PERMISSION_KEY_VIEW, obj);
	}
	public boolean hasEditPermission(List groupIds, PresentationObject obj) {
		return this.hasPermission(groupIds, AccessController.PERMISSION_KEY_EDIT, obj);
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
		return com.idega.core.accesscontrol.business.LoginBusinessBean.isLoggedOn(this);
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
		String historyId = this.getParameter(BuilderConstants.PRM_HISTORY_ID);
		//System.err.println("in iwc.getCurrentState()");
		if (historyId != null) {
			//System.err.println("historyId != null");
			HttpSession s = this.getSession();
			//System.err.println(" - from Session.hashCode() -> "+s.hashCode());
			List historyList = (List) s.getAttribute(BuilderConstants.SESSION_OBJECT_STATE);
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
	/**
	 * Gets if this object is in "Preview" mode in the Builder or in regular view not inside the Builder.
	 * @return true if in preview mode
	 */
	public boolean isInPreviewMode() {
		boolean preview = false;
		if (isParameterSet("view")) {
			if (isBuilderApplicationRunning()) {
				String view = getParameter("view");
				if (view.equals("preview"))
					preview = true;
			}
		}
		return (preview);
	}
	/**
	 * Gets if this object is in "Edit" mode in the Builder
	 * @return true if in edit mode
	 */
	public boolean isInEditMode() {
		boolean edit = false;
		if (isParameterSet("view")) {
			if (isBuilderApplicationRunning()) {
				String view = getParameter("view");
				if (view.equals("builder"))
					edit = true;
			}
		}
		return (edit);
	}
	private boolean isBuilderApplicationRunning(){
		//Reflection workaround:
		try{
			if(builderLogicInstance==null){
				MethodInvoker invoker = MethodInvoker.getInstance();
				MethodFinder finder = MethodFinder.getInstance();
				Class builderLogicClass = Class.forName("com.idega.builder.business.BuilderLogic");
				builderLogicInstance = invoker.invokeStaticMethodWithNoParameters(builderLogicClass,"getInstance");
				methodIsBuilderApplicationRunning = finder.getMethodWithNameAndOneParameter(builderLogicClass,"isBuilderApplicationRunning",IWUserContext.class);
			}
			Object[] args = {this};
			methodIsBuilderApplicationRunning.invoke(builderLogicInstance,args);
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		/*return(BuilderLogic.getInstance().isBuilderApplicationRunning(this));
		*/
		return false;
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
	public ICDomain getDomain() {
		return getApplication().getIWApplicationContext().getDomain();
	}
	
	public void forwardToIBPage(Page fromPage, ICPage page){
		forwardToIBPage(fromPage,((Integer) page.getPrimaryKeyValue()).intValue());
	}
	
	public void forwardToIBPage(Page fromPage, int pageID){
		forwardToIBPage(fromPage,pageID,0);
	}
		
	public void forwardToIBPage(Page fromPage, int pageID,int secondInterval) {
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
		BuilderService bs;
		try
		{
			bs = BuilderServiceFactory.getBuilderService(this.getApplicationContext());
			URL.append(bs.getPageURI(pageID));
			URL.append('&');
			URL.append(getRequest().getQueryString());
			if(secondInterval>0)
				fromPage.setToRedirect(URL.toString(),secondInterval);
			else
				fromPage.setToRedirect(URL.toString());
			fromPage.empty();
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//URL.append(BuilderLogic.getInstance().getIBPageURL(this.getApplicationContext(), pageID));
	}

	/*
	 *  Returns null if not found
   */
  public Cookie getCookie(String cookieName) {
    Cookie[] cookies = (Cookie[]) this.getCookies();

    if (cookies != null) {
      if (cookies.length > 0) {
				for (int i = 0 ; i < cookies.length ; i++) {
					if ( cookies[i].getName().equals(cookieName) ) {
						return cookies[i];
					}
				}
      }
    }

    return null;
  }


  /**
   * Gets the current user associated with this context
   * <br>This method is meant to replace getUser()
   * @return The current user if there is one associated with the current context. If there is none the method returns null.
   **/
  public com.idega.user.data.User getCurrentUser(){
		com.idega.core.user.data.User user = getUser();
		if(user!=null){
			try{
				String sessKey = IWC_SESSION_ATTR_NEW_USER_KEY+user.getPrimaryKey().toString();
				com.idega.user.data.User newUser = (com.idega.user.data.User) getSessionAttribute(sessKey);
				if(newUser==null){
					newUser = Converter.convertToNewUser(user);
					setSessionAttribute(sessKey,newUser);
				}
				return newUser;
			}
			catch(Exception e){
				throw new RuntimeException("IWContext.getCurrentUser(): Error getting primary key of user. Exception was: "+e.getClass().getName()+" : "+e.getMessage());
			}
		}
		return null;
  }

	/**
	 * Gets the Id of the current user associated with this context
	 * <br>This method is meant to replace getUserId()
	 * @return The Id of the current user. If there is one associated with the current context. If there is none the method returns -1.
	 **/
	public int getCurrentUserId(){
		com.idega.user.data.User user = getCurrentUser();
		if(user!=null){
			return ((Integer)user.getPrimaryKey()).intValue();
		}
		return -1;
	}
  
  /**
   * TODO reimplement
   * @return The pageId for the current IBPage that is being displayed. Returns -1 if an error occurred.
   */
  public int getCurrentIBPageID(){
	BuilderService bs;
	try
	{
		bs = BuilderServiceFactory.getBuilderService(this.getApplicationContext());
		return bs.getCurrentPageId(this);
	}
	catch (RemoteException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return -1;
  }

	public boolean isSecure(){
		return getRequest().isSecure();
	}
	
	/*
	 * BEGIN ABSTRACT METHODS FROM FacesContext
	 */
	
	
	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#addApplicationEvent(javax.faces.event.ApplicationEvent)
	 */
	public void addApplicationEvent(ApplicationEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#addFacesEvent(javax.faces.event.FacesEvent)
	 */
	public void addFacesEvent(FacesEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#addMessage(javax.faces.component.UIComponent, javax.faces.context.Message)
	 */
	public void addMessage(UIComponent arg0, Message arg1)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getApplicationEvents()
	 */
	public Iterator getApplicationEvents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getApplicationEventsCount()
	 */
	public int getApplicationEventsCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getApplicationHandler()
	 */
	public ApplicationHandler getApplicationHandler()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getFacesEvents()
	 */
	public Iterator getFacesEvents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getHttpSession()
	 */
	public HttpSession getHttpSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getLocale()
	 */
	public Locale getLocale()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMaximumSeverity()
	 */
	public int getMaximumSeverity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMessages()
	 */
	public Iterator getMessages()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getMessages(javax.faces.component.UIComponent)
	 */
	public Iterator getMessages(UIComponent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getModelType(java.lang.String)
	 */
	public Class getModelType(String arg0) throws FacesException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getModelValue(java.lang.String)
	 */
	public Object getModelValue(String arg0) throws FacesException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getResponseStream()
	 */
	public ResponseStream getResponseStream()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getResponseWriter()
	 */
	public ResponseWriter getResponseWriter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getServletRequest()
	 */
	public ServletRequest getServletRequest()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getServletResponse()
	 */
	public ServletResponse getServletResponse()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getTree()
	 */
	public Tree getTree()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#getViewHandler()
	 */
	public ViewHandler getViewHandler()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#release()
	 */
	public void release()
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#renderResponse()
	 */
	public void renderResponse()
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#responseComplete()
	 */
	public void responseComplete()
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale arg0)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setModelValue(java.lang.String, java.lang.Object)
	 */
	public void setModelValue(String arg0, Object arg1) throws FacesException
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setResponseStream(javax.faces.context.ResponseStream)
	 */
	public void setResponseStream(ResponseStream arg0)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setResponseWriter(javax.faces.context.ResponseWriter)
	 */
	public void setResponseWriter(ResponseWriter arg0)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.context.FacesContext#setTree(javax.faces.tree.Tree)
	 */
	public void setTree(Tree arg0)
	{
		// TODO Auto-generated method stub
	}

	/*
	 * BEGIN ABSTRACT METHODS FROM FacesContext
	 */
	
}
