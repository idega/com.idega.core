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
import com.idega.util.FileUtil;
import com.idega.util.text.TextSoap;
import com.idega.util.Executer;
import com.idega.util.caching.BlobCacher;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*Class to serve as a base center for an IdegaWeb WebApplication
*/
public class IWMainApplication{//implements ServletContext{

  public static String IdegaEventListenerClassParameter="idegaweb_event_classname";
  public static String IWEventSessionAddressParameter="iw_event_address";     // added by gummi@idega.is
  public static String windowOpenerURL="/servlet/WindowOpener";
  public static String windowOpenerParameter="idegaweb_window_for_page";
  public static String objectInstanciatorURL="/servlet/ObjectInstanciator";
  public static String IMAGE_SERVLET_URL="/servlet/ImageServlet/";
  public static String FILE_SERVLET_URL="/servlet/FileServlet/";
  public static String BUILDER_SERVLET_URL="/servlet/BuilderServlet/";

  public static String templateParameter="idegaweb_template";
  public static String templateClassParameter="idegaweb_template_class";
  public static String classToInstanciateParameter="idegaweb_instance_class";
  private Hashtable loadedBundles;
  private Properties bundlesFile;
  private File bundlesFileFile;
  private String propertiesRealPath;


  private static String SETTINGS_STORAGE_PARAMETER="idegaweb_main_application_settings";
  private static String bundlesFileName="bundles.properties";


  private String defaultLightInterfaceColor=IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;
  private String defaultDarkInterfaceColor=IWConstants.DEFAULT_DARK_INTERFACE_COLOR;

  public static String ApplicationStorageParameterName="idegaweb_application";
  //public static String DefaultPropertiesStorageParameterName="idegaweb_default_properties";
  private ServletContext application;
  private LogWriter lw;


  public IWMainApplication(ServletContext application){
    this.application=application;
    application.setAttribute(ApplicationStorageParameterName,this);
    load();
  }

  public String getVersion(){
    String theReturn = this.getSettings().getProperty("version");
    if(theReturn == null){
      theReturn = "1.0";
    }
    return theReturn;
  }

  private void load(){
    lw=new LogWriter(this.getApplicationRealPath(),LogWriter.INFO);
    this.setPropertiesRealPath();
    IWMainApplicationSettings settings = new IWMainApplicationSettings(this);
    setAttribute(SETTINGS_STORAGE_PARAMETER,settings);
    bundlesFile = new Properties();
    loadedBundles = new Hashtable();
    try{
    bundlesFileFile = FileUtil.getFileAndCreateIfNotExists(this.getPropertiesRealPath(),bundlesFileName);
    bundlesFile.load(new FileInputStream(bundlesFileFile));
    }
    catch(Exception e){
      e.printStackTrace();
    }
    System.out.println("Starting the IdegaWEB Application Framework - Version "+this.getVersion());
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

  //public IWBundleList getBundlesRegistered(){
   //
  //}

  /**
   * Should be called before the application is put out of service
   */

  public LogWriter getLogWriter(){
    return lw;
  }

  public void unload(){
    storeStatus();
    BlobCacher.deleteCache(this);
  }


  public void storeStatus(){
      getSettings().store();
      try{
      getBundlesFile().store(new FileOutputStream(bundlesFileFile),null);
      }
      catch(Exception ex){
          ex.printStackTrace();
      }
      for(Enumeration enum = loadedBundles.keys();enum.hasMoreElements();){
        Object key = enum.nextElement();
        IWBundle bundle = (IWBundle)loadedBundles.get(key);
        bundle.storeState();
      }
  }

  private Properties getBundlesFile(){
    return bundlesFile;
  }

  public String getPropertiesRealPath(){
      return propertiesRealPath;
  }

  private void setPropertiesRealPath(){
    this.propertiesRealPath=this.getApplicationSpecialRealPath()+FileUtil.getFileSeparator()+"properties";
    //debug
    System.out.println("setPropertiesRealPath : "+propertiesRealPath);
  }

  public String getRealPath(String p0){
    return application.getRealPath(p0);
  }

  /**
   * Returns the real path to the WebApplication
   */
  public String getApplicationRealPath(){
    return application.getRealPath(FileUtil.getFileSeparator());
  }


  public String getApplicationSpecialRealPath(){
    return this.getApplicationRealPath()+getApplicationSpecialVirtualPath();
  }

  public String getApplicationSpecialVirtualPath(){
   return "idegaweb";
  }

  private String getBundleVirtualPath(String bundleIdentifier){
    String path = "/"+getApplicationSpecialVirtualPath()+"/"+TextSoap.findAndReplace(getBundlesFile().getProperty(bundleIdentifier),"\\","/");
    return path;
  }


  private String getBundleRealPath(String bundleIdentifier){
    String sBundle;
    if( FileUtil.getFileSeparator().equals("/") )
      sBundle = TextSoap.findAndReplace(getBundlesFile().getProperty(bundleIdentifier),"\\","/");//unix
    else sBundle = TextSoap.findAndReplace(getBundlesFile().getProperty(bundleIdentifier),"/","\\");//windows
  //debug
    System.out.println("IWMainApplication : sBundle = "+sBundle);

    return getApplicationSpecialRealPath()+FileUtil.getFileSeparator()+sBundle;
  }

  public IWBundle getBundle(String bundleIdentifier){
    IWBundle bundle = (IWBundle)loadedBundles.get(bundleIdentifier);
    if(bundle == null){
      System.out.println("BUNDLE IS NULL");
      bundle = new IWBundle(getBundleRealPath(bundleIdentifier),getBundleVirtualPath(bundleIdentifier),bundleIdentifier,this);
      loadedBundles.put(bundleIdentifier,bundle);
    }
    return bundle;
  }

  public boolean registerBundle(String bundleIdentifier,String bundlesPath){
    bundlesFile.setProperty(bundleIdentifier,bundlesPath);
    return true;
  }

  /**
   * Returns a List of IWBundle Objects
   */
  public List getRegisteredBundles(){
      Vector vector = new Vector();
      Iterator iter = bundlesFile.keySet().iterator();
      while (iter.hasNext()) {
        String key = (String)iter.next();
        vector.add(getBundle(key));
      }
      return vector;
  }


    /**
   * Returns a List of Locale Objects
   */
  public List getAvailableLocales(){
    Vector vector = new Vector();
    vector.add(LocaleUtil.getIcelandicLocale());
    vector.add(Locale.ENGLISH);
    return vector;
  }

  /**
   * Only works when running on Tomcat (3.2)
   */
  public boolean restartApplication(){
    String prePath = System.getProperty("tomcat.home");
    try{
      if(System.getProperty("os.name").toLowerCase().indexOf("win")==-1){
        //Runtime.getRuntime().exec(prePath+"/bin/restart");
        String[] array = {prePath+"/bin/restart"};
        Executer.executeInAnotherVM(array);
      }
      else{
        //Runtime.getRuntime().exec(prePath+"\\bin\\restart.bat");
        String[] array = {prePath+"\\bin\\restart.bat"};
        Executer.executeInAnotherVM(array);
      }
      return true;
    }
    catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
  }

}
