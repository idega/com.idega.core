//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import java.net.*;
import com.idega.util.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*Class to serve as a base center for an IdegaWeb WebApplication
*/
public class IWMainApplication{//implements ServletContext{

  public static String IdegaEventListenerClassParameter="idegaweb_event_classname";
  public static String IWEventSessionAddressParameter="iw_event_address";     // added by gummi@idega.is
  public static String windowOpenerURL="/common/windowopener.jsp";
  public static String windowOpenerParameter="idegaweb_window_for_page";
  public static String objectInstanciatorURL="/common/instanciator.jsp";
  public static String templateParameter="idegaweb_template";
  public static String templateClassParameter="idegaweb_template_class";
  public static String classToInstanciateParameter="idegaweb_instance_class";


  private static String SETTINGS_STORAGE_PARAMETER="idegaweb_main_application_settings";


  private String defaultLightInterfaceColor=IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;
  private String defaultDarkInterfaceColor=IWConstants.DEFAULT_DARK_INTERFACE_COLOR;

  public static String ApplicationStorageParameterName="idegaweb_application";
  //public static String DefaultPropertiesStorageParameterName="idegaweb_default_properties";
  private ServletContext application;
  private LogWriter lw;


  public IWMainApplication(ServletContext application){
    this.application=application;
    application.setAttribute(ApplicationStorageParameterName,this);
    IWMainApplicationSettings settings = new IWMainApplicationSettings(this);
    application.setAttribute(SETTINGS_STORAGE_PARAMETER,settings);
    lw=new LogWriter(this.getApplicationPath(),LogWriter.INFO);
  }



  public static String getObjectInstanciatorURL(Class className,String templateName){
    return getObjectInstanciatorURL(className.getName(),templateName);
  }

  public static String getObjectInstanciatorURL(String className,String templateName){
      return objectInstanciatorURL+"?"+classToInstanciateParameter+"="+getEncryptedClassName(className)+"&"+templateParameter+"="+templateName;
  }



  public static String getObjectInstanciatorURL(String className){
      return objectInstanciatorURL+"?"+classToInstanciateParameter+"="+getEncryptedClassName(className);
  }

  public static String getObjectInstanciatorURL(Class classToInstanciate){
      return getObjectInstanciatorURL(classToInstanciate.getName());
  }

  public static String getObjectInstanciatorURL(Class classToInstanciate,Class templateClass){
    return objectInstanciatorURL+"?"+classToInstanciateParameter+"="+getEncryptedClassName(classToInstanciate)+"&"+templateClassParameter+"="+getEncryptedClassName(templateClass);
  }

  /**
   * TODO: Change this so it encrypts the classToInstanciateName
   */
  public static String getEncryptedClassName(String classToInstanciate){
      return classToInstanciate;
  }

  public static String getEncryptedClassName(Class classToInstanciate){
      return getEncryptedClassName(classToInstanciate.getName());
  }

  public static String decryptClassName(String encryptedClassName){
    return encryptedClassName;
  }




  //public ServletContext getContext(String p0){
  //  return application.getContext(p0);
  //}

  public int getMajorVersion(){
    return application.getMajorVersion();
  }

  public int getMinorVersion(){
    return application.getMinorVersion();
  }

  public String getMimeType(String p0){
    return application.getMimeType(p0);
  }

  public URL getResource(String p0) throws MalformedURLException{
    return application.getResource(p0);
  }

  public InputStream getResourceAsStream(String p0){
    return application.getResourceAsStream(p0);
  }

  //public RequestDispatcher getRequestDispatcher(String p0){
  //  return application.getRequestDispatcher(p0);
  //}

  //public RequestDispatcher getNamedDispatcher(String p0){
  //  return application.getNamedDispatcher(p0);
  //}



  public void log(String p0){
    application.log(p0);
  }



  public void log(String p0, Throwable p1){
    application.log(p0,p1);
  }

  public String getRealPath(String p0){
    return application.getRealPath(p0);
  }

  public String getServerInfo(){
    return application.getServerInfo();
  }

  public String getInitParameter(String p0){
    return application.getInitParameter(p0);
  }

  public Enumeration getInitParameterNames(){
    return application.getInitParameterNames();
  }

  public Object getAttribute(String parameterName){
    return application.getAttribute(parameterName);
  }

  public Enumeration getAttributeNames(){
    return application.getAttributeNames();
  }

  public void setAttribute(String parameterName, Object objectToStore){
    application.setAttribute(parameterName,objectToStore);
  }

  public void removeAttribute(String parameterName){
    application.removeAttribute(parameterName);
  }

  public static IWMainApplication getIWMainApplication(ServletContext application){
      return (IWMainApplication) application.getAttribute(IWMainApplication.ApplicationStorageParameterName);
  }

  public String getDefaultDarkInterfaceColor(){
    return defaultDarkInterfaceColor;
  }

  public void setDefaultDarkInterfaceColor(String color){
    defaultDarkInterfaceColor=color;
  }


  public String getDefaultLightInterfaceColor(){
    return defaultLightInterfaceColor;
  }

  public void setDefaultLightInterfaceColor(String color){
    defaultLightInterfaceColor=color;
  }

  /**
   * @deprecated Replaced with getSettings()
   */
  /*public Properties getDefaultProperties(){
    IdegaWebProperties properties = (IdegaWebProperties)application.getAttribute(this.DefaultPropertiesStorageParameterName);
    //if (properties==null){
    //  properties = new IdegaWebProperties(this.application);
    //  application.setAttribute(this.DefaultPropertiesStorageParameterName,properties);
    //}
    return properties;
  }*/


  public IWMainApplicationSettings getSettings(){
    IWMainApplicationSettings settings = (IWMainApplicationSettings)application.getAttribute(SETTINGS_STORAGE_PARAMETER);
    return settings;
  }

  /**
   * Returns the real path to the WebApplication
   */
  public String getApplicationPath(){
    return application.getRealPath("/");
  }


  public LogWriter getLogWriter(){
    return lw;
  }

  public String getApplicationSpecialPath(){
    return this.getApplicationPath()+"/idegaweb";
  }

  //public IWBundleList getBundlesRegistered(){
   //
  //}

  /**
   * Should be called before the application is put out of service
   */
  public void unload(){
      getSettings().store();
      //getBundleList().store();
  }

}
