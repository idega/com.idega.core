//idega 2000-2001 - Tryggvi Larusson
/*
*Copyright 2000-2001 idega.is All Rights Reserved.
*/

package com.idega.presentation;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import com.idega.presentation.ui.*;
import java.io.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.LocaleUtil;
import java.util.WeakHashMap;
import com.idega.block.login.business.LoginBusiness;
import com.idega.core.user.data.User;
import com.idega.core.data.ICObject;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.builder.business.BuilderLogic;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWContext extends Object{


private HttpServletRequest Request;
private HttpServletResponse Response;
private final static String LOCALE_ATTRIBUTE="idegaweb_locale";

private final static String WEAK_HASHMAP_KEY ="idegaweb_weak_hashmap";

//private HttpSession session;
private String language; //Variable to set the language i.e. HTML
private String interfaceStyle;  //Variable to enable multiple interface looks
private Connection Conn;
private Hashtable preferences;
private String spokenLanguage;
//private Hashtable sessionAttributes;
private ServletContext servletContext;

private boolean isCaching = false;
private PrintWriter cacheWriter;


public IWContext(HttpServletRequest Request,HttpServletResponse Response){
	this.Request=Request;
	this.Response=Response;
	setLanguage(getRightLanguage(Request,Response));
	setAllDefault();
}

public IWContext(HttpServletRequest Request,HttpServletResponse Response,String language){
	this.Request=Request;
	this.Response=Response;
	setLanguage(language);
	setAllDefault();
}

public IWContext(HttpServletRequest Request,HttpServletResponse Response,HttpSession session){
	this.Request=Request;
	this.Response=Response;
	setLanguage(getRightLanguage(Request,Response));
	setAllDefault();
	//this.session=session;
}

private void setAllDefault(){

	this.interfaceStyle="default";
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

public HttpSession getSession(){
	return getRequest().getSession();
}

public String getUserAgent(){
	return Request.getHeader("User-agent");
}

public String getReferrer(){
	return Request.getHeader("Referer");
}

public boolean isNetscape(){
	if (getUserAgent().indexOf("Mozilla") != -1){
		//if not Internet Explorer then Netscape :)
		if (getUserAgent().indexOf("MSIE") != -1){
			return false;
		}
		else{
			return true;
		}
	}
	else{
		return false;
	}
}

public boolean isIE(){
  if (getUserAgent().indexOf("MSIE") != -1){
    return true;
  }
  else{
    return false;
  }
}

public boolean isOpera(){
  if (getUserAgent().indexOf("Opera") != -1){
    return true;
  }
  else{
    return false;
  }
}

public boolean isSearchEngine(){
  if (getUserAgent().indexOf("Ultraseek") != -1){
    return true;
  }
  else{
    return false;
  }
}

/*Under Construction*/
private String getRightLanguage(HttpServletRequest Request,HttpServletResponse Response){
	//Todo: set the language to WML when the user-agent is of that type
	//--only implemented for the UPG1 test WAP browser

	String user_agent = Request.getHeader("User-agent");

	if (user_agent != null){

		//Sets for WML browser
		if(user_agent.indexOf("UPG1") != -1){
			return "WML";
		}
		else if(user_agent.toLowerCase().indexOf("wap") != -1){
			return "WML";
		}
		else if(user_agent.toLowerCase().indexOf("nokia") != -1){
			return "WML";
		}
		else if(user_agent.toLowerCase().indexOf("ericsson") != -1){
			return "WML";
		}
		else if(user_agent.toLowerCase().indexOf("symbian") != -1){
			return "WML";
		}
                else if(user_agent.toLowerCase().indexOf("wapman") != -1){
			return "WML";
		}
		else
		{
			return "HTML";
		}
	}
	else
	{
		return "HTML";
	}
}

public boolean isParameterSet(String parameterName){
	boolean theReturn = false;
	if (Request.getParameter(parameterName) != null){
		theReturn = true;
	}
	return theReturn;
}

public boolean isParameterSet(Parameter parameter){
	boolean theReturn = false;
	if (Request.getParameter(parameter.getName()) != null){
		theReturn = true;
	}
	return theReturn;
}


public boolean parameterEquals(Parameter parameter){
	boolean theReturn = false;
	if (parameter != null){
		if (Request.getParameter(parameter.getName()) != null){
			if (Request.getParameter(parameter.getName()).equals(parameter.getValue())){
				theReturn=true;
			}
		}
	}
	return theReturn;
}

public boolean parameterEquals(String parameterName,String parameterValue){
	boolean theReturn = false;
	if (Request.getParameter(parameterName) != null){
		if (Request.getParameter(parameterName).equals(parameterValue)){
		theReturn=true;
		}
	}
	return theReturn;
}

public void setPreference(String Name, String Value){
	if (preferences == null){
		preferences = new Hashtable();
		preferences.put((Object) Name,(Object) Value);
	}
	else{
		preferences.put((Object) Name,(Object) Value);
	}
}

public String getPreference(String name){
	if (preferences == null){
		return null;
	}
	else{
		return (String) preferences.get( (Object) name);
	}
}

/**
 * @deprecated
 */
public void setRequest(HttpServletRequest Request){
	this.Request=Request;
}

/**
 * @deprecated
 */
public void setResponse(HttpServletResponse Response){
	this.Response = Response;
}

public void setLanguage(String language){
	this.language = language;
	if (language.equals("WML")){
		this.Response.setContentType("text/vnd.wap.wml");
	}
	if (language.equals("HTML")){
		this.Response.setContentType("text/html");
	}

}


public void setSpokenLanguage(String spokenLanguage){
  this.spokenLanguage = spokenLanguage;
}

public void setInterfaceStyle(String InterfaceStyle){
	this.interfaceStyle = InterfaceStyle;
}

/**
 * @deprecated
 */
public HttpServletRequest getRequest(){
	return this.Request;
}

public Cookie[] getCookies() {
  return this.getRequest().getCookies();
}

public void addCookies(Cookie cookie) {
  this.getResponse().addCookie(cookie);
}

public String getParameter(String parameterName){
  return getRequest().getParameter(parameterName);
}

public Enumeration getParameterNames(){
  return getRequest().getParameterNames();
}

public String[] getParameterValues(String parameterName){
  return getRequest().getParameterValues(parameterName);
}

public String getQueryString(){
  return getRequest().getQueryString();
}

/**
 * @deprecated
 */
public HttpServletResponse getResponse(){
	return this.Response;
}

public Object getSessionAttribute(String attributeName){
	return getSession().getAttribute(attributeName);
}

public void setSessionAttribute(String attributeName,Object attribute){
	getSession().setAttribute(attributeName,attribute);
}


/**
 * @deprecated Replaced with removeSessionAttribute()
 */
public void removeAttribute(String attributeName){
	removeSessionAttribute(attributeName);
}

public void removeSessionAttribute(String attributeName){
	getSession().removeAttribute(attributeName);
}


public String getLanguage(){
	return this.language;
}

public String getSpokenLanguage(){
        if (this.spokenLanguage == null)
          this.setSpokenLanguage("IS");

	return this.spokenLanguage;
}

public String getInterfaceStyle(){
	return this.interfaceStyle;
}

public Connection getConnection(){
	return this.Conn;
}

public void setConnection(Connection conn){
	this.Conn = Conn;
}








public void setDefaultBackgroundColor(String s){
	setPreference("DefaultBackgroundColor",s);
}

public String getDefaultBackgroundColor(){
	return getPreference("DefaultBackgroundColor");
}




public void setDefaultFontFace(String s){
	setPreference("DefaultFontFace",s);
}

public String getDefaultFontFace(){
	return getPreference("DefaultFontFace");
}




public void setDefaultFontSize(String s){
	setPreference("DefaultFontSize",s);
}

public String getDefaultFontSize(){
	return getPreference("DefaultFontSize");
}




public void setDefaultFontColor(String s){
	setPreference("DefaultFontColor",s);
}

public String getDefaultFontColor(){
	return getPreference("DefaultFontColor");
}




public void setDefaultFontStyle(String s){
	setPreference("DefaultFontStyle",s);
}

public String getDefaultFontStyle(){
	return getPreference("DefaultFontStyle");
}





public void setDefaultPrimaryInterfaceColor(String s){
	setPreference("DefaultPrimaryInterfaceColor",s);
}

public String getDefaultPrimaryInterfaceColor(){
	return getPreference("DefaultPrimaryInterfaceColor");
}




public void setDefaultSecondaryInterfaceColor(String s){
	setPreference("DefaultSecondaryInterfaceColor",s);
}

public String getDefaultSecondaryInterfaceColor(){
	return getPreference("DefaultSecondaryInterfaceColor");
}




public void setDefaultInterfaceFontFace(String s){
	setPreference("DefaultInterfaceFontFace",s);
}

public String getDefaultInterfaceFontFace(){
	return getPreference("DefaultInterfaceFontFace");
}




public void setDefaultInterfaceFontColor(String s){
	setPreference("DefaultInterfaceFontColor",s);
}

public String getDefaultInterfaceFontColor(){
	return getPreference("DefaultInterfaceFontColor");
}




public void setDefaultInterfaceFontSize(String s){
	setPreference("DefaultInterfaceFontSize",s);
}

public String getDefaultInterfaceFontSize(){
	return getPreference("DefaultInterfaceFontSize");
}




public void setDefaultInterfaceFontStyle(String s){
	setPreference("DefaultInterfaceFontStyle",s);
}

public String getDefaultInterfaceFontStyle(){
	return getPreference("DefaultInterfaceFontStyle");
}





public void setCompanyPrimaryColor(String s){
	setPreference("CompanyPrimaryColor",s);
}

public String getCompanyPrimaryColor(){
	return getPreference("CompanyPrimaryColor");
}




public void setCompanySecondaryColor(String s){
	setPreference("CompanySecondaryColor",s);
}

public String getCompanySecondaryColor(){
	return getPreference("CompanySecondaryColor");
}




public void setCompanyFontFace(String s){
	setPreference("CompanyFontFace",s);
}

public String getCompanyFontFace(){
	return getPreference("CompanyFontFace");
}




public void setCompanyFontColor(String s){
	setPreference("CompanyFontColor",s);
}

public String getCompanyFontColor(){
	return getPreference("CompanyFontColor");
}




public void setCompanyFontSize(String s){
	setPreference("CompanyFontSize",s);
}

public String getCompanyFontSize(){
	return getPreference("CompanyFontSize");
}




public void setCompanyFontStyle(String s){
	setPreference("CompanyFontStyle",s);
}

public String getCompanyFontStyle(){
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
public ServletContext getServletContext(){
  return servletContext;
}


public void setServletContext(ServletContext context){
    this.servletContext=context;
}

/**
 * @deprecated UNIMPLEMENTED
 */
public void maintainParameter(Parameter parameter){
  Hashtable theParameters = (Hashtable)this.getSessionAttribute("idega_special_maintained_parameters");
  if (theParameters==null){
    theParameters=new Hashtable();
    theParameters.put(parameter.getName(),parameter);
  }
  else{
    //Parameter previousParameter = theParameters.get(parameter.getName());
    theParameters.put(parameter.getName(),parameter);
  }
}


/**
 * @deprecated UNIMPLEMENTED
 */
public Map getTheMaintainedParameters(){
  return (Map) this.getSessionAttribute("idega_special_maintained_parameters");
}

/*public void maintainParameter(String parameterName,String parameterValue){
  setSessionAttribute(new Parameter(parameterName,parameterValue));
}*/


public String getRequestURI(){
  return getRequest().getRequestURI();
}

public String getServerName(){
  return getRequest().getServerName();
}

public int getServerPort() {
  return getRequest().getServerPort();
}

public PrintWriter getWriter()throws IOException{
    if(cacheWriter==null){
      return getResponse().getWriter();
    }
    else{
      if(this.isCacheing()){
        return cacheWriter;
      }
      else{
        return getResponse().getWriter();
      }

    }
}


public void sendRedirect(String URL){
  try {
    getResponse().sendRedirect(URL);
  }
  catch (IOException e) {
    e.printStackTrace(System.err);
  }
}

public void setApplicationAttribute(String attributeName,Object attributeValue){
  getApplication().setAttribute(attributeName,attributeValue);
}


public Object getApplicationAttribute(String attributeName){
  return getApplication().getAttribute(attributeName);
}

public void removeApplicationAttribute(String attributeName){
  getApplication().removeAttribute(attributeName);
}


public IWMainApplication getApplication(){
    return IWMainApplication.getIWMainApplication(this.servletContext);
}

public IWMainApplicationSettings getApplicationSettings(){
    return getApplication().getSettings();
}

public Locale getCurrentLocale(){
  Locale theReturn = (Locale)this.getSessionAttribute(LOCALE_ATTRIBUTE);
  if(theReturn==null){
    theReturn = getApplication().getSettings().getDefaultLocale();
    setCurrentLocale(theReturn);
  }
  return theReturn;
}

public void setCurrentLocale(Locale locale){
  this.setSessionAttribute(LOCALE_ATTRIBUTE,locale);
}

/**
 * Sets the object with Weak reference so that it could be garbagecollected anytime
 */
public void setSessionAttributeWeak(String attributeName,Object attributeValue){
  getWeakHashMap().put(attributeName,attributeValue);
}

public Object getSessionAttributeWeak(String propertyName){
  return getWeakHashMap().get(propertyName);
}

private Map getWeakHashMap(){
  WeakHashMap map = (WeakHashMap)getSessionAttribute(WEAK_HASHMAP_KEY);
  if(map==null){
    map = new WeakHashMap();
    setSessionAttribute(WEAK_HASHMAP_KEY,map);
  }
  return map;
}

public void setContentType(String contentType){
  Response.setContentType(contentType);
}

void setCacheing(boolean ifCacheing){
  this.isCaching=ifCacheing;
}

boolean isCacheing(){
  return isCaching;
}

public void setCacheWriter(PrintWriter writer){
  this.cacheWriter=writer;
}

  public User getUser() {
    return(LoginBusiness.getUser(this));
  }

  public int getUserId() {
    User usr = getUser();
    if(usr != null){
      return(usr.getID());
    }
    return -1;
  }


  public AccessController getAccessController(){
    return ((AccessController)this.getApplication().getAccessController());
  }

  public String getRequestContentType(){
    return Request.getContentType();
  }

  public boolean hasPermission(String permissionKey, PresentationObject obj){
    try {
      return this.getAccessController().hasPermission(permissionKey,obj,this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean hasViewPermission(PresentationObject obj){
    return this.hasPermission(AccessController._PERMISSIONKEY_VIEW,obj);
  }

  public boolean hasEditPermission(PresentationObject obj){
    return this.hasPermission(AccessController._PERMISSIONKEY_EDIT,obj);
  }

  public boolean hasPermission(List groupIds, String permissionKey, PresentationObject obj){
    try {
      return this.getAccessController().hasPermission(groupIds, permissionKey,obj,this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean hasFilePermission(String permissionKey, int id){
    try {
      return this.getAccessController().hasFilePermission(permissionKey, id , this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId){
    try {
      return this.getAccessController().hasDataPermission(permissionKey,obj,entityRecordId,this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }


  public boolean hasViewPermission(List groupIds, PresentationObject obj){
    return this.hasPermission(groupIds, AccessController._PERMISSIONKEY_VIEW,obj);
  }

  public boolean hasEditPermission(List groupIds, PresentationObject obj){
    return this.hasPermission(groupIds, AccessController._PERMISSIONKEY_EDIT,obj);
  }


  public boolean isSuperAdmin(){
    try {
      return this.getUser().equals(this.getAccessController().getAdministratorUser());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean isLoggedOn(){
    return com.idega.block.login.business.LoginBusiness.isLoggedOn(this);
  }


  /**
   * Expensive method, not recommended to use frequently
   */
  public static IWContext getInstance(){
    return com.idega.servlet.IWPresentationServlet.getIWContext();
  }


  public String getCurrentState(PresentationObject obj){
    if(obj != null){
      return getCurrentState(obj.getParentObjectInstanceID());
    } else {
      return null;
    }
  }

  /**
  * @todo implement
  */
  public String getCurrentState(int instanceId){
    String historyId = this.getParameter(BuilderLogic.PRM_HISTORY_ID);
    if(historyId != null){
      List historyList = (List)this.getSessionAttribute(BuilderLogic.SESSION_OBJECT_STATE);
      if(historyList != null && historyList.contains(historyId)){
        int index = historyList.indexOf(historyId);
        Object ob = ((Hashtable)historyList.get(index+1)).get(Integer.toString(instanceId));
        //System.err.println("current state = "+ob);
        return (String)ob;
      }
    }
    return null;
  }

}


