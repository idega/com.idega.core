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
import com.idega.presentation.Page;
import com.idega.util.caching.BlobCacher;
import com.idega.exception.IWBundleDoesNotExist;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.graphics.generator.ImageFactory;
import com.idega.presentation.PresentationObject;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*Class to serve as a base center for an IdegaWeb WebApplication
*/
public class IWMainApplication{//implements ServletContext{

  public static String IdegaEventListenerClassParameter="idegaweb_event_classname";
  public static String IWEventSessionAddressParameter="iw_event_address";     // added by gummi@idega.is
  public static String windowOpenerURL="/servlet/WindowOpener";
  public static final String windowOpenerParameter=Page.IW_FRAME_STORAGE_PARMETER;


  public static String objectInstanciatorURL="/servlet/ObjectInstanciator";
  public static String IMAGE_SERVLET_URL="/servlet/ImageServlet/";
  public static String FILE_SERVLET_URL="/servlet/FileServlet/";
  public static String MEDIA_SERVLET_URL="/servlet/MediaServlet/";
  public static String BUILDER_SERVLET_URL="/servlet/IBMainServlet/";
  public static String _IFRAME_CONTENT_URL="/servlet/IBIFrameServlet/";

  public static String templateParameter="idegaweb_template";
  public static String templateClassParameter="idegaweb_template_class";
  public static String classToInstanciateParameter="idegaweb_instance_class";
  private Hashtable loadedBundles;
  private Properties bundlesFile;
  private File bundlesFileFile;
  private String propertiesRealPath;

  public final static String BUNDLES_STANDARD_DIRECTORY = "bundles";
  public final static String IDEGAWEB_SPECIAL_DIRECTORY = "idegaweb";
  public final static String CORE_BUNDLE_IDENTIFIER = PresentationObject.IW_BUNDLE_IDENTIFIER;
  public final static String CORE_BUNDLE_FONT_FOLDER_NAME = "iw_fonts";
  public final static String CORE_DEFAULT_FONT = "default.ttf";

  public final static String IW_ACCESSCONTROL_TYPE_PROPERTY="iw_accesscontrol_type";
  public final static String _PROPERTY_USING_EVENTSYSTEM = "using_eventsystem";
  public final static String _ADDRESS_ACCESSCONTROLER = "iwmainapplication.ic_accesscontroler";
  public static final String _PARAMETER_IC_OBJECT_INSTANCE_ID  = "parent.ic_object_instance_id";

  private static String SETTINGS_STORAGE_PARAMETER="idegaweb_main_application_settings";
  private static String bundlesFileName="bundles.properties";


  private String defaultLightInterfaceColor=IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;
  private String defaultDarkInterfaceColor=IWConstants.DEFAULT_DARK_INTERFACE_COLOR;

  public static String ApplicationStorageParameterName="idegaweb_application";
  //public static String DefaultPropertiesStorageParameterName="idegaweb_default_properties";
  private ServletContext application;
  private LogWriter lw;
  private static IWCacheManager cacheManager;


  public IWMainApplication(ServletContext application){
    this.application=application;
    application.setAttribute(ApplicationStorageParameterName,this);
    //attention this must be reviewed if we implement multi domains within one virtualmachine
    cacheManager = IWCacheManager.getInstance(this);
    load();
  }

  public String getVersion(){
    String theReturn = this.getSettings().getProperty("version");
    if(theReturn == null){
      theReturn = "1.3";
    }
    return theReturn;
  }

  public String getBuildNumber(){
    String theReturn = this.getSettings().getProperty("iw_build_num");
    if(theReturn == null){
      theReturn = "220b";
    }
    return theReturn;
  }

  private void load(){
    lw=new LogWriter(this.getApplicationRealPath(),LogWriter.INFO);
    this.setPropertiesRealPath();
    IWMainApplicationSettings settings = new IWMainApplicationSettings(this);
    setAttribute(SETTINGS_STORAGE_PARAMETER,settings);
    System.out.println("Starting the idegaWEB Application Framework - Version "+this.getVersion());
  }

  public void loadBundles(){
    bundlesFile = new Properties();
    loadedBundles = new Hashtable();
    try{
    bundlesFileFile = FileUtil.getFileAndCreateIfNotExists(this.getPropertiesRealPath(),bundlesFileName);
    bundlesFile.load(new FileInputStream(bundlesFileFile));
    }
    catch(Exception e){
      e.printStackTrace();
    }
    checkForInstalledBundles();
  }

  public static String getObjectInstanciatorURL(Class className,String templateName){
    return getObjectInstanciatorURL(className.getName(),templateName);
  }

  public static String getObjectInstanciatorURL(String className,String templateName){
      return objectInstanciatorURL+"?"+classToInstanciateParameter+"="+getEncryptedClassName(className)+"&"+templateParameter+"="+getEncryptedClassName(templateName);
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
   * @todo: Change this so it encrypts the classToInstanciateName
   */
  public static String getEncryptedClassName(String classToInstanciate){
/*      System.err.println("classToInstanciate "+classToInstanciate);
      if(classToInstanciate != null){
      char[] characters = classToInstanciate.toCharArray();

      int max = 255;

      char[] encryptedChars = new char[characters.length];

      System.err.println("max "+max);

      for (int i = 0; i < characters.length; i++) {
        int ch = (int)characters[i];
        encryptedChars[i] = ((char)((ch+11)%max));
        System.err.println();
        System.err.println(characters[i]+ " -> "+ ((char)((ch+5)%max)));
        System.err.println(ch + " -> "+ ((ch+11)%max));
      }

      return String.valueOf(encryptedChars);
      }else{*/
  //      return classToInstanciate;
//      }
    return getHashCode(classToInstanciate);
  }

  public static String getEncryptedClassName(Class classToInstanciate){
      //return getEncryptedClassName(classToInstanciate.getName());
      return getHashCode(classToInstanciate);
  }

  public static String decryptClassName(String encryptedClassName){
/*
      if(encryptedClassName != null){
        System.err.println("encryptedClassName "+encryptedClassName);
        char[] characters = encryptedClassName.toCharArray();

        int max = 255;

        for (int i = 0; i < characters.length; i++) {
          int ch = (int)characters[i];
          int ch2 = (char)(ch-11);
          if(ch2 < 0){
            ch2 = max - ch2;
          }
          characters[i] = (char)ch2;
        }

        System.err.println("String.valueOf(characters)"+String.valueOf(characters));
        return String.valueOf(characters);
    }else{*/
    //  return encryptedClassName;
    //}

    return getHashCodedClassName(encryptedClassName);
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
    System.out.println("idegaWeb : shutdown : Storing application state and deleting cached/generated content");
    storeStatus();
    IWCacheManager.deleteCachedBlobs(this);
    getImageFactory().deleteGeneratedImages(this);
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
    //System.out.println("setPropertiesRealPath : "+propertiesRealPath);
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
   return IDEGAWEB_SPECIAL_DIRECTORY;
  }

  private String getBundleVirtualPath(String bundleIdentifier){
    //String sBundle = getBundlesFile().getProperty(bundleIdentifier);
    String sBundle = getInternalBundeVirtualPath(bundleIdentifier);
    if( sBundle!= null ) sBundle = TextSoap.findAndReplace(getBundlesFile().getProperty(bundleIdentifier),"\\","/");

    String path = "/"+getApplicationSpecialVirtualPath()+"/"+sBundle;
    return path;
  }


  private String getBundleRealPath(String bundleIdentifier){
    //String sBundle = getBundlesFile().getProperty(bundleIdentifier);
    String sBundle = getInternalBundeVirtualPath(bundleIdentifier);
    if( sBundle!=null){
      if( FileUtil.getFileSeparator().equals("/") )
        sBundle = TextSoap.findAndReplace(sBundle,"\\","/");//unix
      else sBundle = TextSoap.findAndReplace(sBundle,"/","\\");//windows
    }
    //debug
    //System.out.println("IWMainApplication : sBundle = "+sBundle);

    return getApplicationSpecialRealPath()+FileUtil.getFileSeparator()+sBundle;
  }


  private void checkForInstalledBundles(){
      File theRoot = new File(this.getApplicationSpecialRealPath(),BUNDLES_STANDARD_DIRECTORY);
      File[] bundles = theRoot.listFiles();
      for (int i = 0; i < bundles.length; i++) {
        if(bundles[i].isDirectory() && (bundles[i].getName().toLowerCase().indexOf(".bundle") != -1)){
          File properties = new File(bundles[i],"properties");
          File propertiesFile = new File(properties,IWBundle.propertyFileName);
          IWPropertyList list = new IWPropertyList(propertiesFile);
            String bundleIdentifier = list.getProperty(IWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY);
            if(bundleIdentifier!=null){
              String bundleDir = BUNDLES_STANDARD_DIRECTORY+File.separator+bundles[i].getName();
              this.registerBundle(bundleIdentifier,bundleDir);
            }
        }
      }
  }

  private String getInternalBundeVirtualPath(String bundleIdentifier){
    String tryString = getBundlesFile().getProperty(bundleIdentifier);
    if(tryString==null){
      File theRoot = new File(this.getApplicationSpecialRealPath(),BUNDLES_STANDARD_DIRECTORY);
      File[] bundles = theRoot.listFiles();
      for (int i = 0; i < bundles.length; i++) {
        if(bundles[i].isDirectory()){
          File properties = new File(bundles[i],"properties");
          File propertiesFile = new File(properties,IWBundle.propertyFileName);
          try{
          IWPropertyList list = new IWPropertyList(propertiesFile);
          if(list.getProperty(IWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY).equalsIgnoreCase(bundleIdentifier)){
            tryString = BUNDLES_STANDARD_DIRECTORY+File.separator+bundles[i].getName();
            this.registerBundle(bundleIdentifier,tryString);
            return tryString;
          }
          }
          catch(Exception e){
            throw new IWBundleDoesNotExist(bundleIdentifier);
          }
        }
      }
    }
    return tryString;

  }

  public IWBundle getBundle(String bundleIdentifier){
    IWBundle bundle = (IWBundle)loadedBundles.get(bundleIdentifier);
    if(bundle == null){
      System.out.println("Loading bundle "+bundleIdentifier);
      bundle = new IWBundle(getBundleRealPath(bundleIdentifier),getBundleVirtualPath(bundleIdentifier),bundleIdentifier,this);
      loadedBundles.put(bundleIdentifier,bundle);
    }
    return bundle;
  }

  /**
   * Regsters and loads a IWBundle with the abstact pathname relative to /idegaweb on the WebServer
   * and the identifier specified by bundleIdentifier
   */
  public boolean registerBundle(String bundleIdentifier,String bundlesPath){
    getBundlesFile().setProperty(bundleIdentifier,bundlesPath);
    getBundle(bundleIdentifier);
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

  public void startAccessController(){
    this.setAccessController(this.getSettings().getDefaultAccessController());
    System.out.println("Starting service "+this.getAccessController().getServiceName());
    this.getAccessController().startService(this);
  }

  public AccessController getAccessController(){
    AccessController controler = (AccessController)this.getAttribute(_ADDRESS_ACCESSCONTROLER);
    if(controler != null){
      return controler;
    } else {
      System.err.println("AccessController has not been started");
      return null;
    }
  }

  private void setAccessController(AccessController controler){
    this.setAttribute(_ADDRESS_ACCESSCONTROLER, controler);
  }

  public ImageFactory getImageFactory(){
    return ImageFactory.getStaticInstance(this);
  }

  public IWBundle getCoreBundle(){
   return getBundle(CORE_BUNDLE_IDENTIFIER);
  }

  public void addLocaleToRegisteredBundles(Locale locale){
    List bundles = this.getRegisteredBundles();
    Iterator iter = bundles.iterator();
    while (iter.hasNext()) {
      IWBundle item = (IWBundle)iter.next();
      item.addLocale(locale);
    }
  }

  // this is not multi domain safe
  public static IWCacheManager getIWCacheManager(){
    return cacheManager;
  }


  // hashcode referencing
  private static Hashtable hashClasses = null;
  private static Hashtable hashCodes = null;

  public static String getHashCode(String className){
    try{
      return getHashCode(Class.forName(className));
    }
    catch(ClassNotFoundException ex){

    }
    return String.valueOf(className.hashCode());
  }

  public static String getHashCode(Class classObject){
    String hashcode = Integer.toString(classObject.hashCode());
    System.err.println(classObject.getName()+" "+hashcode);
    if(hashClasses==null)
      hashClasses = new Hashtable();
    if(!hashClasses.containsKey(hashcode) ){
      hashClasses.put(hashcode,classObject.getName());
    }
    if(hashCodes == null)
      hashCodes = new Hashtable();
    if(!hashCodes.containsKey(classObject.getName())){
      hashCodes.put(classObject.getName(),hashcode);
    }
    return hashcode;
  }

  public static String getHashCodedClassName(String hashcode){
    if(hashClasses!=null && hashcode!=null && hashClasses.containsKey(hashcode))
     return (String)hashClasses.get(hashcode);
    return null;
  }

}
