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
import com.idega.block.media.business.MediaBundleStarter;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*
*Class to serve as a base center for an IdegaWeb WebApplication
*/
public class IWMainApplication{//implements ServletContext{

  public static String IdegaEventListenerClassParameter="idegaweb_event_classname";
  public static String IWEventSessionAddressParameter="iw_event_address";     // added by gummi@idega.is
  public static final String windowOpenerParameter=Page.IW_FRAME_STORAGE_PARMETER;

  private static String windowOpenerURL="/servlet/WindowOpener";
  private static String objectInstanciatorURL="/servlet/ObjectInstanciator";
  public static String IMAGE_SERVLET_URL="/servlet/ImageServlet/";
  public static String FILE_SERVLET_URL="/servlet/FileServlet/";
  private static String MEDIA_SERVLET_URL="/servlet/MediaServlet/";
  private static String BUILDER_SERVLET_URL="/servlet/IBMainServlet/";
  private static String _IFRAME_CONTENT_URL="/servlet/IBIFrameServlet/";

  public static String templateParameter="idegaweb_template";
  public static String templateClassParameter="idegaweb_template_class";
  public static String classToInstanciateParameter="idegaweb_instance_class";

  private static String PARAM_IW_FRAME_CLASS_PARAMETER = com.idega.presentation.Page.IW_FRAME_CLASS_PARAMETER;

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
  private static boolean alreadyUnLoaded = false;//for restartApplication

  private static final String APACHE_RESTART_PARAMETER = "restart_apache";

  private static final String CONTEXT_PATH_KEY="IW_CONTEXT_PATH";

  public static boolean DEBUG_FLAG = false;



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
    loadCryptoProperties();
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

  public String getObjectInstanciatorURI(Class className,String templateName){
    return getObjectInstanciatorURI(className.getName(),templateName);
  }

  public  String getObjectInstanciatorURI(String className,String templateName){
      return getObjectInstanciatorURI()+"?"+classToInstanciateParameter+"="+getEncryptedClassName(className)+"&"+templateParameter+"="+getEncryptedClassName(templateName);
  }


  public  String getObjectInstanciatorURI(String className){
      return getObjectInstanciatorURI()+"?"+classToInstanciateParameter+"="+getEncryptedClassName(className);
  }

  public  String getObjectInstanciatorURI(Class classToInstanciate){
      return getObjectInstanciatorURI(classToInstanciate.getName());
  }

  public  String getObjectInstanciatorURI(Class classToInstanciate,Class templateClass){
    return this.getObjectInstanciatorURI()+"?"+classToInstanciateParameter+"="+getEncryptedClassName(classToInstanciate)+"&"+templateClassParameter+"="+getEncryptedClassName(templateClass);
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
    if( !alreadyUnLoaded ){
      System.out.println("[idegaWeb] : shutdown : Storing application state and deleting cached/generated content");
      storeStatus();
      storeCryptoProperties();
      IWCacheManager.deleteCachedBlobs(this);
      getImageFactory().deleteGeneratedImages(this);
      alreadyUnLoaded = true;
    }
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
    String sBundle = getInternalBundleVirtualPath(bundleIdentifier);
    if( sBundle!= null ) sBundle = TextSoap.findAndReplace(getBundlesFile().getProperty(bundleIdentifier),"\\","/");

    String path = "/"+getApplicationSpecialVirtualPath()+"/"+sBundle;
    return path;
  }


  private String getBundleRealPath(String bundleIdentifier){
    //String sBundle = getBundlesFile().getProperty(bundleIdentifier);
    String sBundle = getInternalBundleVirtualPath(bundleIdentifier);
    if( sBundle!=null){
      if( FileUtil.getFileSeparator().equals("/") ) {
	sBundle = TextSoap.findAndReplace(sBundle,"\\","/");//unix
      }
      else {
	sBundle = TextSoap.findAndReplace(sBundle,"/","\\");//windows
      }
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

  private String getInternalBundleVirtualPath(String bundleIdentifier){
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
    return getBundle(bundleIdentifier,false);
  }

  public IWBundle getBundle(String bundleIdentifier,boolean autoCreate){
    IWBundle bundle = (IWBundle)loadedBundles.get(bundleIdentifier);
    if(bundle == null){
      sendStartupMessage("Loading bundle "+bundleIdentifier);
      bundle = new IWBundle(getBundleRealPath(bundleIdentifier),getBundleVirtualPath(bundleIdentifier),bundleIdentifier,this,autoCreate);
      loadedBundles.put(bundleIdentifier,bundle);
    }
    return bundle;
  }


  /**
   * Regsters and loads a IWBundle with the abstact pathname relative to /idegaweb on the WebServer
   * and the identifier specified by bundleIdentifier
   * autoCr
   */
  public boolean registerBundle(String bundleIdentifier,String bundlesPath){
    return registerBundle(bundleIdentifier,bundlesPath,false);
  }


  /**
   * Regsters and loads a IWBundle with the abstact pathname relative to /idegaweb on the WebServer
   * and the identifier specified by bundleIdentifier<br><br>
   * Does automatically create the bundle on disk if autoCreate==true;
   */
  public boolean registerBundle(String bundleIdentifier,String bundlesPath,boolean autoCreate){
    getBundlesFile().setProperty(bundleIdentifier,bundlesPath);
    getBundle(bundleIdentifier,autoCreate);
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
   * Only works when running on Tomcat
   */
  public boolean restartApplication(){
    String apache = this.getSettings().getProperty(APACHE_RESTART_PARAMETER);//restart string
    String restartScript = "/idega/bin/apache_restart.sh";

    boolean restartApacheAlso = false;

    if( apache != null ){
      restartApacheAlso = Boolean.valueOf(apache.toLowerCase()).booleanValue();
    }

    unload();

    String prePath = System.getProperty("user.dir");//return /tomcat/bin
    System.out.println("IWMainApplication: restarting application server at : " +prePath);

    try{//windows
      if(System.getProperty("os.name").toLowerCase().indexOf("win")!=-1){
        if(!restartApacheAlso){
          String[] array = {prePath+"\\shutdown.bat",prePath+"\\startup.bat"};
          Executer.executeInAnotherVM(array);
        }
        else{
          String[] array = {prePath+"\\shutdown.bat",prePath+"\\startup.bat",restartScript};
          Executer.executeInAnotherVM(array);
        }
      }
      else{//unix
        if(!restartApacheAlso){
          String[] array = {prePath+"/shutdown.sh",prePath+"/startup.sh"};
          Executer.executeInAnotherVM(array);
        }
        else{
          String[] array = {prePath+"/shutdown.sh",prePath+"/startup.sh",restartScript};
          Executer.executeInAnotherVM(array);
        }

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
  private static Properties cryptoCodes = null;
  private static Properties cryptoProps = null;


  private void loadCryptoProperties(){
    cryptoProps = new Properties();
    sendStartupMessage("Loading Cryptonium");
    String file = getPropertiesRealPath()+FileUtil.getFileSeparator()+"crypto.properties";
    try{
      cryptoProps.load(new FileInputStream(file));
      // temporary property cleaning
      String clean = cryptoProps.getProperty("clean");
      if(clean == null){
        cryptoProps.clear();
        cryptoProps.setProperty("clean","true");
      }
      /////////////////////////////
      cryptoCodes = new Properties();
      if(cryptoProps.size() > 0){
        Iterator iter = cryptoCodes.entrySet().iterator();
        while(iter.hasNext()){
          Map.Entry me = (Map.Entry) iter.next();
          cryptoCodes.put(me.getValue(),me.getKey());
        }
      }
    }
    catch(Exception ex){}
  }

   private void storeCryptoProperties(){
    if(cryptoProps!=null){
      sendShutdownMessage("Storing Cryptonium");
      try{
      String file = getPropertiesRealPath()+FileUtil.getFileSeparator()+"crypto.properties";
      cryptoProps.store(new FileOutputStream(file),"Cryptonium");
      }
      catch(Exception ex){}
    }
  }

  public static String getHashCode(String className){
    try{
      return getHashCode(Class.forName(className));
    }
    catch(ClassNotFoundException ex){

    }
    return String.valueOf(className.hashCode());
  }

  public static String getHashCode(Class classObject){

    if(cryptoCodes == null)
      cryptoCodes = new Properties();

    if(cryptoProps == null)
      cryptoProps =new Properties();

    String crypto;
    if(cryptoCodes.containsKey(classObject.getName())){
      crypto = (String) cryptoCodes.get(classObject.getName());
    }
    else{
      crypto = Long.toString(calculate(classObject.getName()));
      cryptoCodes.put(classObject.getName(),crypto);
    }

    cryptoProps.put(crypto,classObject.getName());


    return crypto;
  }

  public static String getHashCodedClassName(String crypto){
    if(cryptoProps!=null && crypto!=null && cryptoProps.containsKey(crypto))
     return (String)cryptoProps.get(crypto);
    else
     return crypto;
  }

  /**
   *  calculates the crosssum of a string
   */
  private static int calculate(String s){
    char[] c = s.toCharArray();
    int sum = 0;
    for (int i = 0; i < c.length; i++) {
      if(i == 4)
        sum*=3;
      if(i == 10)
        sum*=2;
      sum += ((int)c[i]);
    }
    return sum;
  }


  /**
   * Returns the part of the URL that is the context path for this application
   */
  public String getContextURL(){
    return (String)this.getAttribute(CONTEXT_PATH_KEY);
  }

  void setContextURL(String contextURL){
    this.setAttribute(CONTEXT_PATH_KEY,contextURL);
  }

  public static void setDebugMode(boolean debugFlag){
    DEBUG_FLAG = debugFlag;
  }

  public static boolean isDebugActive(){
    return DEBUG_FLAG;
  }

  public void createMediaTables(){
    MediaBundleStarter starter = new MediaBundleStarter();
    starter.start(this);

  }

  public void sendStartupMessage(String message){
    System.out.println("[idegaWebApp] : "+message);
  }

  public void sendShutdownMessage(String message){
    System.out.println("[idegaWebApp] : "+message);
  }

  protected String getTranslatedURLWithContext(String url){
    /**
     * @todo: implement
     */
    return url;
  }

  public String getWindowOpenerURI(){
    return getTranslatedURLWithContext(this.windowOpenerURL);
  }

  public  String getWindowOpenerURI(Class windowToOpen){
    return getWindowOpenerURI()+"?"+PARAM_IW_FRAME_CLASS_PARAMETER+"="+windowToOpen.getName();
  }

  public  String getWindowOpenerURI(Class windowToOpen,int ICObjectInstanceIDToOpen){
    return getWindowOpenerURI(windowToOpen)+"&"+_PARAMETER_IC_OBJECT_INSTANCE_ID+"="+ICObjectInstanceIDToOpen;
  }

  public String getObjectInstanciatorURI(){
    return getTranslatedURLWithContext(objectInstanciatorURL);
  }

  public String getMediaServletURI(){
    return getTranslatedURLWithContext(this.MEDIA_SERVLET_URL);
  }

  public String getBuilderServletURI(){
    return getTranslatedURLWithContext(this.BUILDER_SERVLET_URL);
  }

  public String getIFrameContentURI(){
    return getTranslatedURLWithContext(this._IFRAME_CONTENT_URL);
  }



}
