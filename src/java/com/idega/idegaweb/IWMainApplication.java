// idega 2001 - Tryggvi Larusson
/*
 * Copyright 2001 idega.is All Rights Reserved.
 */

package com.idega.idegaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.appserver.AppServer;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.exception.IWBundleDoesNotExist;
import com.idega.graphics.generator.ImageFactory;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.util.Executer;
import com.idega.util.FileUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.LogWriter;
import com.idega.util.text.TextSoap;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 * 
 * Class to serve as a base center for an IdegaWeb WebApplication
 */
public class IWMainApplication {//implements ServletContext{

    public static String IdegaEventListenerClassParameter = "idegaweb_event_classname";
    public static String ApplicationEventListenersParameter = "idegaweb_application_events";
    public static String IWEventSessionAddressParameter = "iw_event_address"; // added
    
    protected static IWMainApplication defaultIWMainApplication;
                                                                              // gummi@idega.is
    public static final String windowOpenerParameter = Page.IW_FRAME_STORAGE_PARMETER;
    
    private static String windowOpenerURL = "/servlet/WindowOpener";
    private static String objectInstanciatorURL = "/servlet/ObjectInstanciator";
    public static String IMAGE_SERVLET_URL = "/servlet/ImageServlet/";
    public static String FILE_SERVLET_URL = "/servlet/FileServlet/";
    private static String MEDIA_SERVLET_URL = "/servlet/MediaServlet/";
    private static String BUILDER_SERVLET_URL = "/servlet/IBMainServlet/";
    private static String _IFRAME_CONTENT_URL = "/servlet/IBIFrameServlet/";
    private static String IDEGAWEB_APP_SERVLET_URI = "/servlet/idegaweb";
    
    public static String templateParameter = "idegaweb_template";
    public static String templateClassParameter = "idegaweb_template_class";
    public static String classToInstanciateParameter = "idegaweb_instance_class";

    private static String PARAM_IW_FRAME_CLASS_PARAMETER = com.idega.presentation.Page.IW_FRAME_CLASS_PARAMETER;
    
    public static boolean USE_NEW_URL_SCHEME=false;
    
    private static String NEW_WINDOW_URL="/window/";
    private static String NEW_BUILDER_PAGE_URL="/pages/";

    	
    private Map loadedBundles;
    private Properties bundlesFile;
    private File bundlesFileFile;
    private String propertiesRealPath;
    private String bundlesRealPath;
    private final static String BUNDLES_STANDARD_DIRECTORY = "bundles";
    private final static String IDEGAWEB_SPECIAL_DIRECTORY = "idegaweb";
    private final static String PROPERTIES_STANDARD_DIRECTORY = "properties";
    public final static String CORE_BUNDLE_IDENTIFIER = PresentationObject.IW_BUNDLE_IDENTIFIER;
    public final static String CORE_BUNDLE_FONT_FOLDER_NAME = "iw_fonts";
    public final static String CORE_DEFAULT_FONT = "default.ttf";
    public final static String IW_ACCESSCONTROL_TYPE_PROPERTY = "iw_accesscontrol_type";
    public final static String _PROPERTY_USING_EVENTSYSTEM = "using_eventsystem";
    public final static String _ADDRESS_ACCESSCONTROLER = "iwmainapplication.ic_accesscontroler";
    public static final String _PARAMETER_IC_OBJECT_INSTANCE_ID = "parent.ic_object_instance_id";
    private static String SETTINGS_STORAGE_PARAMETER = "idegaweb_main_application_settings";
    private static String bundlesFileName = "bundles.properties";
    private String defaultLightInterfaceColor = IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;
    private String defaultDarkInterfaceColor = IWConstants.DEFAULT_DARK_INTERFACE_COLOR;
    public static String ApplicationStorageParameterName = "idegaweb_application";
    //public static String
    // DefaultPropertiesStorageParameterName="idegaweb_default_properties";
    private ServletContext application;
    private LogWriter lw;
    private static IWCacheManager cacheManager;
    private static boolean alreadyUnLoaded = false;//for restartApplication
    private static final String APACHE_RESTART_PARAMETER = "restart_apache";
    private static final String CONTEXT_PATH_KEY = "IW_CONTEXT_PATH";
    private String APP_CONTEXT_URI_KEY = "IW_APP_CONTEXT_URI";
    private String appContext;
    private static String SLASH = "/";
    private boolean checkedAppContext;
    private String cacheDirURI;
    private IWApplicationContext iwappContext;
    public static boolean DEBUG_FLAG = false;
	public static final String PROPERTY_NEW_URL_STRUCTURE = "new_url_structure";
	public static final String PROPERTY_JSF_RENDERING = "jsf_rendering";
	private AppServer applicationServer;

    public IWMainApplication(ServletContext application,AppServer appserver) {
        this.application = application;
        setApplicationServer(appserver);        
        application.setAttribute(ApplicationStorageParameterName, this);
        //set the default application instance to this
        if(defaultIWMainApplication==null){
        		defaultIWMainApplication=this;
        }
        //attention this must be reviewed if we implement multi domains within
        // one virtualmachine
        cacheManager = IWCacheManager.getInstance(this);
        load();
    }

    public String getVersion() {
        String theReturn = this.getSettings().getProperty("version");
        if (theReturn == null) {
            theReturn = "1.4.3";
        }
        return theReturn;
    }

    public String getBuildNumber() {
        String theReturn = this.getSettings().getProperty("iw_build_num");
        if (theReturn == null) {
            theReturn = "220b";
        }
        return theReturn;
    }

    private void load() {
        lw = new LogWriter(this.getApplicationRealPath(), LogWriter.INFO);
        this.setPropertiesRealPath();
        this.setBundlesRealPath();
        IWMainApplicationSettings settings = new IWMainApplicationSettings(this);
        setAttribute(SETTINGS_STORAGE_PARAMETER, settings);
        IWSystemProperties systemProperties = new IWSystemProperties(this);
        setAttribute(SYSTEM_PROPERTIES_STORAGE_PARAMETER, systemProperties);
        log("Starting the idegaWeb Application Framework - Version "
                + this.getVersion());        
        loadCryptoProperties();
    }

    public void loadBundles() {
        bundlesFile = new Properties();
        loadedBundles = new HashMap();
        try {
            bundlesFileFile = FileUtil.getFileAndCreateIfNotExists(this
                    .getPropertiesRealPath(), bundlesFileName);
            bundlesFile.load(new FileInputStream(bundlesFileFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkForInstalledBundles();
    }

    public String getObjectInstanciatorURI(Class className, String templateName) {
        //return getObjectInstanciatorURI(className.getName(), templateName);
		if(USE_NEW_URL_SCHEME){
			return this.getWindowOpenerURI(className)
			+ templateParameter + "=" + getEncryptedClassName(templateName);
		}
		else{
	        return getObjectInstanciatorURI() + "?" + classToInstanciateParameter
	                + "=" + getEncryptedClassName(className) + "&"
	                + templateParameter + "=" + getEncryptedClassName(templateName);
		}    
    }

    public String getObjectInstanciatorURI(String className, String templateName) {
		try {
			return getObjectInstanciatorURI(Class.forName(className),templateName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    public String getObjectInstanciatorURI(String className) {
		if(USE_NEW_URL_SCHEME){
			try {
				return this.getWindowOpenerURI(Class.forName(className));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		else{
	        return getObjectInstanciatorURI() + "?" + classToInstanciateParameter
            + "=" + getEncryptedClassName(className);
		}
    }

    public String getObjectInstanciatorURI(Class classToInstanciate) {
    		if(USE_NEW_URL_SCHEME){
    			return this.getWindowOpenerURI(classToInstanciate);
    		}
    		else{
    			return getObjectInstanciatorURI(classToInstanciate.getName());
    		}
    }

    /*public String getObjectInstanciatorURI(Class classToInstanciate,
            Class templateClass) {
        return this.getObjectInstanciatorURI() + "?"
                + classToInstanciateParameter + "="
                + getEncryptedClassName(classToInstanciate) + "&"
                + templateClassParameter + "="
                + getEncryptedClassName(templateClass);
    }*/

    /**
     * @todo: Change this so it encrypts the classToInstanciateName
     */
    public static String getEncryptedClassName(String classToInstanciate) {
        return getHashCode(classToInstanciate);
    }

    public static String getEncryptedClassName(Class classToInstanciate) {
        return getHashCode(classToInstanciate);
    }

    public static String decryptClassName(String encryptedClassName) {
        return getHashCodedClassName(encryptedClassName);
    }

    //public ServletContext getContext(String p0){
    //  return application.getContext(p0);
    //}

    public int getMajorVersion() {
        return application.getMajorVersion();
    }

    public int getMinorVersion() {
        return application.getMinorVersion();
    }

    public String getMimeType(String p0) {
        return application.getMimeType(p0);
    }

    public URL getResource(String p0) throws MalformedURLException {
        return application.getResource(p0);
    }

    public InputStream getResourceAsStream(String p0) {
        return application.getResourceAsStream(p0);
    }

    //public RequestDispatcher getRequestDispatcher(String p0){
    //  return application.getRequestDispatcher(p0);
    //}

    //public RequestDispatcher getNamedDispatcher(String p0){
    //  return application.getNamedDispatcher(p0);
    //}

    public void log(String p0) {
        application.log(p0);
    }

    public void log(String p0, Throwable p1) {
        application.log(p0, p1);
    }

    public String getServerInfo() {
        return application.getServerInfo();
    }

    public String getInitParameter(String p0) {
        return application.getInitParameter(p0);
    }

    public Enumeration getInitParameterNames() {
        return application.getInitParameterNames();
    }

    public Object getAttribute(String parameterName) {
        return application.getAttribute(parameterName);
    }
    
    public Object getAttribute(String parameterName, Object defaultObjectToReturnIfValueIsNull){
    		Object value = getAttribute(parameterName);
    		if(value==null){
    			value = defaultObjectToReturnIfValueIsNull;
    		}
    		return value;
    }
    
    public Enumeration getAttributeNames() {
        return application.getAttributeNames();
    }

    public void setAttribute(String parameterName, Object objectToStore) {
        application.setAttribute(parameterName, objectToStore);
    }

    public void removeAttribute(String parameterName) {
        application.removeAttribute(parameterName);
    }

    public static IWMainApplication getIWMainApplication(
            ServletContext application) {
        return (IWMainApplication) application
                .getAttribute(IWMainApplication.ApplicationStorageParameterName);
    }

    public String getDefaultDarkInterfaceColor() {
        return defaultDarkInterfaceColor;
    }

    public void setDefaultDarkInterfaceColor(String color) {
        defaultDarkInterfaceColor = color;
    }

    public String getDefaultLightInterfaceColor() {
        return defaultLightInterfaceColor;
    }

    public void setDefaultLightInterfaceColor(String color) {
        defaultLightInterfaceColor = color;
    }

    /*
     * public Properties getDefaultProperties(){ IdegaWebProperties properties =
     * (IdegaWebProperties)application.getAttribute(this.DefaultPropertiesStorageParameterName);
     * //if (properties==null){ // properties = new
     * IdegaWebProperties(this.application); //
     * application.setAttribute(this.DefaultPropertiesStorageParameterName,properties);
     * //} return properties; }
     */

    public IWMainApplicationSettings getSettings() {
        IWMainApplicationSettings settings = (IWMainApplicationSettings) application
                .getAttribute(SETTINGS_STORAGE_PARAMETER);
        return settings;
    }

    public IWSystemProperties getSystemProperties() {
        IWSystemProperties settings = (IWSystemProperties) application
                .getAttribute(SYSTEM_PROPERTIES_STORAGE_PARAMETER);
        return settings;
    }

    //public IWBundleList getBundlesRegistered(){
    //
    //}

    /**
     * Should be called before the application is put out of service
     */

    public LogWriter getLogWriter() {
        return lw;
    }

    public void unload() {
        if (!alreadyUnLoaded) {
            log("[idegaWeb] : shutdown : Storing application state and deleting cached/generated content");
            storeStatus();
            //IWCacheManager.deleteCachedBlobs(this);
            //      getImageFactory(true).deleteGeneratedImages(this);

            for (Iterator keyIter = loadedBundles.keySet().iterator(); keyIter
                    .hasNext();) {
                Object key = keyIter.next();
                IWBundle bundle = (IWBundle) loadedBundles.get(key);
                bundle.unload();
            }

            alreadyUnLoaded = true;
        }
    }

    public void storeStatus() {
        getSettings().store();
        getSystemProperties().store();
        storeCryptoProperties();
        try {
            getBundlesFile().store(new FileOutputStream(bundlesFileFile), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private Properties getBundlesFile() {
        return bundlesFile;
    }

    
    /**
     * Gets the Path to the folder where application properties are located by default.
     * e.g. /home/idegaweb/webapp1/idegaweb/properties
     * @return the path as a String
     */    
    public String getPropertiesRealPath() {
        return propertiesRealPath;
    }

    private void setPropertiesRealPath() {
        this.propertiesRealPath = this.getApplicationSpecialRealPath()
                + FileUtil.getFileSeparator() + PROPERTIES_STANDARD_DIRECTORY;
        //debug
        //System.out.println("setPropertiesRealPath : "+propertiesRealPath);
    }

    
    /**
     * Gets the Path to the folder where bundles are located by default.
     * e.g. /home/idegaweb/webapp1/idegaweb/bundles
     * @return the path as a String
     */
    public String getBundlesRealPath() {
        return bundlesRealPath;
    }

    private void setBundlesRealPath() {
        this.bundlesRealPath = this.getApplicationSpecialRealPath()
                + FileUtil.getFileSeparator() + BUNDLES_STANDARD_DIRECTORY;
        //debug
        //System.out.println("setPropertiesRealPath : "+propertiesRealPath);
    }
    
    
    /**
     * Returns the real path to the WebApplication
     */
    public String getApplicationRealPath() {
        return application.getRealPath(FileUtil.getFileSeparator());
    }

    public String getApplicationSpecialRealPath() {
        return this.getApplicationRealPath()
                + getApplicationSpecialVirtualPath();
    }

    public String getApplicationSpecialVirtualPath() {
        return IDEGAWEB_SPECIAL_DIRECTORY;
    }

    private String getBundleVirtualPath(String bundleIdentifier) {
        //String sBundle = getBundlesFile().getProperty(bundleIdentifier);
        String sBundle = getInternalBundleVirtualPath(bundleIdentifier);
        if (sBundle != null)
                sBundle = TextSoap.findAndReplace(getBundlesFile().getProperty(
                        bundleIdentifier), "\\", "/");

        String path = "/" + getApplicationSpecialVirtualPath() + "/" + sBundle;
        return path;
    }

    private String getBundleRealPath(String bundleIdentifier) {
        //String sBundle = getBundlesFile().getProperty(bundleIdentifier);
        String sBundle = getInternalBundleVirtualPath(bundleIdentifier);
        if (sBundle != null) {
            if (FileUtil.getFileSeparator().equals("/")) {
                sBundle = TextSoap.findAndReplace(sBundle, "\\", "/");//unix
            } else {
                sBundle = TextSoap.findAndReplace(sBundle, "/", "\\");//windows
            }
        }
        //debug
        //System.out.println("IWMainApplication : sBundle = "+sBundle);

        return getApplicationSpecialRealPath() + FileUtil.getFileSeparator()
                + sBundle;
    }

    private void checkForInstalledBundles() {
        File theRoot = new File(this.getApplicationSpecialRealPath(),
                BUNDLES_STANDARD_DIRECTORY);
        File[] bundles = theRoot.listFiles();
        for (int i = 0; i < bundles.length; i++) {
            if (bundles[i].isDirectory()
                    && (bundles[i].getName().toLowerCase().indexOf(".bundle") != -1)) {
                File properties = new File(bundles[i], "properties");
                File propertiesFile = new File(properties,
                        IWBundle.propertyFileName);
                IWPropertyList list = new IWPropertyList(propertiesFile);
                String bundleIdentifier = list
                        .getProperty(IWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY);
                if (bundleIdentifier != null) {
                    String bundleDir = BUNDLES_STANDARD_DIRECTORY
                            + File.separator + bundles[i].getName();
                    try {
                        this.registerBundle(bundleIdentifier, bundleDir);
                    } catch (Throwable t) {
                        this.sendStartupMessage("Error loading bundle "
                                + bundleIdentifier);
                        t.printStackTrace();
                    }
                }
            }
        }
    }

    private String getInternalBundleVirtualPath(String bundleIdentifier) {
        String tryString = getBundlesFile().getProperty(bundleIdentifier);
        if (tryString == null) {
            File theRoot = new File(this.getApplicationSpecialRealPath(),
                    BUNDLES_STANDARD_DIRECTORY);
            File[] bundles = theRoot.listFiles();
            for (int i = 0; i < bundles.length; i++) {
                if (bundles[i].isDirectory()) {
                    File properties = new File(bundles[i], "properties");
                    File propertiesFile = new File(properties,
                            IWBundle.propertyFileName);
                    try {
                        IWPropertyList list = new IWPropertyList(propertiesFile);
                        if (list.getProperty(
                                IWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY)
                                .equalsIgnoreCase(bundleIdentifier)) {
                            tryString = BUNDLES_STANDARD_DIRECTORY
                                    + File.separator + bundles[i].getName();
                            this.registerBundle(bundleIdentifier, tryString);
                            return tryString;
                        }
                    } catch (Exception e) {
                        throw new IWBundleDoesNotExist(bundleIdentifier);
                    }
                }
            }
        }
        return tryString;

    }

    public IWBundle getBundle(String bundleIdentifier)throws IWBundleDoesNotExist{
        return getBundle(bundleIdentifier, false);
    }

    public IWBundle getBundle(String bundleIdentifier, boolean autoCreate)throws IWBundleDoesNotExist{
        IWBundle bundle = (IWBundle) loadedBundles.get(bundleIdentifier);
        if (bundle == null) {
        		//if to throw out the IWBundleDoesNotExist exception:
        		boolean throwException=false;
        		if(!autoCreate){
        			//Check if bundle does exist only if autocreate is false:
        			File file = new File(getBundleRealPath(bundleIdentifier));
        			if(!file.exists()){
        				throwException=true;
        			}
        		}
        		if(!throwException){
	            sendStartupMessage("Loading bundle " + bundleIdentifier);
	            bundle = new IWBundle(getBundleRealPath(bundleIdentifier),
	                    getBundleVirtualPath(bundleIdentifier), bundleIdentifier,
	                    this, autoCreate);
	            loadedBundles.put(bundleIdentifier, bundle);
        		}
        		else{
        			throw new IWBundleDoesNotExist(bundleIdentifier);
        		}
        }
        return bundle;
    }

    /**
     * Regsters and loads a IWBundle with the abstact pathname relative to
     * /idegaweb on the WebServer and the identifier specified by
     * bundleIdentifier autoCr
     */
    public boolean registerBundle(String bundleIdentifier, String bundlesPath) {
        return registerBundle(bundleIdentifier, bundlesPath, false);
    }

    /**
     * Regsters and loads a IWBundle with the abstact pathname relative to
     * /idegaweb on the WebServer and the identifier specified by
     * bundleIdentifier <br>
     * <br>
     * Does automatically create the bundle on disk if autoCreate==true;
     */
    public boolean registerBundle(String bundleIdentifier, String bundlesPath,
            boolean autoCreate) {
        getBundlesFile().setProperty(bundleIdentifier, bundlesPath);
        getBundle(bundleIdentifier, autoCreate);
        return true;
    }

    /**
     * Returns a List of IWBundle Objects
     */
    public List getRegisteredBundles() {
        List vector = new ArrayList();
        Iterator iter = bundlesFile.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            vector.add(getBundle(key));
        }
        return vector;
    }

    /**
     * Returns a List of Locale Objects
     */
    public List getAvailableLocales() {
        List vector = new ArrayList();
        vector.add(LocaleUtil.getIcelandicLocale());
        vector.add(Locale.ENGLISH);
        return vector;
    }

    /**
     * Only works when running on Tomcat
     */
    public boolean restartApplication() {
        String apache = this.getSettings()
                .getProperty(APACHE_RESTART_PARAMETER);//restart string
        String restartScript = "/idega/bin/apache_restart.sh";

        boolean restartApacheAlso = false;

        if (apache != null) {
            restartApacheAlso = Boolean.valueOf(apache.toLowerCase())
                    .booleanValue();
        }

        unload();

        String prePath = System.getProperty("user.dir");//return /tomcat/bin
        System.out
                .println("IWMainApplication: restarting application server at : "
                        + prePath);

        try {//windows
            if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1) {
                if (!restartApacheAlso) {
                    String[] array = { prePath + "\\shutdown.bat",
                            prePath + "\\startup.bat"};
                    Executer.executeInAnotherVM(array);
                } else {
                    String[] array = { prePath + "\\shutdown.bat",
                            prePath + "\\startup.bat", restartScript};
                    Executer.executeInAnotherVM(array);
                }
            } else {//unix
                if (!restartApacheAlso) {
                    String[] array = { prePath + "/shutdown.sh",
                            prePath + "/startup.sh"};
                    Executer.executeInAnotherVM(array);
                } else {
                    String[] array = { prePath + "/shutdown.sh",
                            prePath + "/startup.sh", restartScript};
                    Executer.executeInAnotherVM(array);
                }

            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public void startAccessController() {
        this.setAccessController(this.getSettings()
                .getDefaultAccessController());
        System.out.println("Starting service "
                + this.getAccessController().getServiceName());
        this.getAccessController().startService(this);
    }

    public AccessController getAccessController() {
        AccessController controler = (AccessController) this
                .getAttribute(_ADDRESS_ACCESSCONTROLER);
        if (controler != null) {
            return controler;
        } else {
            log("AccessController has not been started");
            return null;
        }
    }

    private void setAccessController(AccessController controler) {
        this.setAttribute(_ADDRESS_ACCESSCONTROLER, controler);
    }

    public ImageFactory getImageFactory(boolean shutdown) {
        return ImageFactory.getStaticInstance(this, shutdown);
    }

    public ImageFactory getImageFactory() {
        return ImageFactory.getStaticInstance(this);
    }

    public IWBundle getCoreBundle() {
        return getBundle(CORE_BUNDLE_IDENTIFIER);
    }

    public Locale getCoreLocale() {
        return Locale.ENGLISH;
    }

    public void addLocaleToRegisteredBundles(Locale locale) {
        List bundles = this.getRegisteredBundles();
        Iterator iter = bundles.iterator();
        while (iter.hasNext()) {
            IWBundle item = (IWBundle) iter.next();
            item.addLocale(locale);
        }
    }

    // this is not multi domain safe
    public static IWCacheManager getIWCacheManager() {
        return cacheManager;
    }

    // hashcode referencing
    private static Map hashClasses = null;

    private static Properties cryptoCodesPropertiesKeyedByClassName = null;

    private static Properties cryptoClassNamesPropertiesKeyedByCode = null;

    protected static String USE_CRYPTO_PROPERTIES = "use_crypto_properties";

    private static boolean isCryptoUsed = true;

    private String SYSTEM_PROPERTIES_STORAGE_PARAMETER = "idegaweb_system_properties";

    private static boolean isUsingCryptoProperties() {
        return isCryptoUsed;
    }

    private void initCryptoUsage() {
        String isUsed = getSettings().getProperty(USE_CRYPTO_PROPERTIES);
        isCryptoUsed = !"false".equals(isUsed);
    }

    private void loadCryptoProperties() {
        initCryptoUsage();
        if (isUsingCryptoProperties()) {
            cryptoClassNamesPropertiesKeyedByCode = new Properties();
            sendStartupMessage("Loading Cryptonium");
            String file = getPropertiesRealPath() + FileUtil.getFileSeparator()
                    + "crypto.properties";
            try {
                cryptoClassNamesPropertiesKeyedByCode.load(new FileInputStream(
                        file));
                // temporary property cleaning
                String clean = cryptoClassNamesPropertiesKeyedByCode
                        .getProperty("clean");
                if (clean == null) {
                    cryptoClassNamesPropertiesKeyedByCode.clear();
                    cryptoClassNamesPropertiesKeyedByCode.setProperty("clean",
                            "true");
                }
                /////////////////////////////
                cryptoCodesPropertiesKeyedByClassName = new Properties();
                if (cryptoClassNamesPropertiesKeyedByCode.size() > 0) {
                    Iterator iter = cryptoClassNamesPropertiesKeyedByCode
                            .entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry me = (Map.Entry) iter.next();
                        cryptoCodesPropertiesKeyedByClassName.put(
                                me.getValue(), me.getKey());
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    private void storeCryptoProperties() {
        if (isUsingCryptoProperties()
                && cryptoClassNamesPropertiesKeyedByCode != null) {
            sendShutdownMessage("Storing Cryptonium");

            try {
                String file = getPropertiesRealPath()
                        + FileUtil.getFileSeparator() + "crypto.properties";
                cryptoClassNamesPropertiesKeyedByCode.store(
                        new FileOutputStream(file), "Cryptonium");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        initCryptoUsage();
    }

    public static String getHashCode(String className) {
        try {
            return getHashCode(Class.forName(className));
        } catch (ClassNotFoundException ex) {

        }
        return String.valueOf(className.hashCode());
    }

    /**
     * Method getHashCode. Used to get the encrypted classname of a class.
     * 
     * @param classObject
     * @param addon
     *            an integer to that is added to the number calculate() makes to
     *            avoid two different classes having the same code
     * @return String
     */
    public static String getHashCode(Class classObject) {

        if (isUsingCryptoProperties()) {
            if (cryptoCodesPropertiesKeyedByClassName == null)
                    cryptoCodesPropertiesKeyedByClassName = new Properties();
            if (cryptoClassNamesPropertiesKeyedByCode == null)
                    cryptoClassNamesPropertiesKeyedByCode = new Properties();

            final String className = classObject.getName();

            // if crypto code for this class has already been created
            if (cryptoCodesPropertiesKeyedByClassName.containsKey(className)) {
                return (String) cryptoCodesPropertiesKeyedByClassName
                        .get(className);
            } else {// else crypto code for this class has NOT been created
                return createAndStoreCryptoName(className);
            }

        } else
            return classObject.getName();
    }

    private synchronized static String createAndStoreCryptoName(String className) {

        String crypto = (String) cryptoCodesPropertiesKeyedByClassName
                .get(className);//if someone just beat us to creating it

        if (crypto != null) {
            return crypto;
        } else {
            int iCrypto = calculate(className);
            crypto = Integer.toString(iCrypto);

            while (cryptoClassNamesPropertiesKeyedByCode.containsKey(crypto)) {
                crypto = Integer.toString(++iCrypto);
                if (isDebugActive())
                        System.out
                                .println("Conflicting cryptos: creating new crypto number : "
                                        + iCrypto);
            }

            cryptoCodesPropertiesKeyedByClassName.put(className, crypto);
            cryptoClassNamesPropertiesKeyedByCode.put(crypto, className);

            return crypto;
        }

    }

    public static String getHashCodedClassName(String crypto) {
        if (cryptoClassNamesPropertiesKeyedByCode != null && crypto != null
                && cryptoClassNamesPropertiesKeyedByCode.containsKey(crypto))
            return (String) cryptoClassNamesPropertiesKeyedByCode.get(crypto);
        else
            return crypto;
    }

    /**
     * calculates the crosssum of a string
     */
    private static int calculate(String s) {
        char[] c = s.toCharArray();
        int sum = 0;
        for (int i = 0; i < c.length; i++) {
            if (i == 4) sum *= 3;
            if (i == 10) sum *= 2;
            sum += ((int) c[i]);
        }
        return sum;
    }

    /**
     * Returns the part of the URL that is the context path for this application
     */
    public String getContextURL() {
        return (String) this.getAttribute(CONTEXT_PATH_KEY);
    }

    public IWApplicationContext getIWApplicationContext() {
        //IWContext iwc = new IWContext(
        if (iwappContext == null) {
            iwappContext = new IWApplicationContextImpl(this);
        }
        return iwappContext;
    }

    void setContextURL(String contextURL) {
        this.setAttribute(CONTEXT_PATH_KEY, contextURL);
    }

    public static void setDebugMode(boolean debugFlag) {
        DEBUG_FLAG = debugFlag;
    }

    public static boolean isDebugActive() {
        return DEBUG_FLAG;
    }

    public void startFileSystem() {
        try {
            ICFileSystem fs = ICFileSystemFactory.getFileSystem(this
                    .getIWApplicationContext());
            fs.initialize();
        } catch (Exception e) {
            System.err
                    .println("IWMainApplication.startFileSystem() : There was an error, most likely the media bundle is not installed");
        }
    }

    public void sendStartupMessage(String message) {
        System.out.println("[idegaWebApp] : " + message);
    }

    public void sendShutdownMessage(String message) {
        System.out.println("[idegaWebApp] : " + message);
    }

    public void setApplicationContextURI(String uri) {
        if (uri != null) {
            if (uri.startsWith(SLASH)) {
                appContext = uri;
            } else {
                appContext = SLASH + uri;
            }
        } else {
            appContext = SLASH;
        }
        checkedAppContext = true;
    }

    /**
     * @return The part of the request URI that belongs to this application,
     *         returns "/" if running under the ROOT context
     */
    public String getApplicationContextURI() {
        if (!checkedAppContext) {
            setApplicationContextURI(this.getSettings().getProperty(
                    APP_CONTEXT_URI_KEY));
            checkedAppContext = true;
        }
        if (appContext == null) { return this.SLASH; }
        return appContext;
    }

    public boolean isRunningUnderRootContext() {
        if (appContext == null) {
            return true;
        } else if (appContext.equals(SLASH)) {
            return true;
        } else {
            return false;
        }

    }

    public String getTranslatedURIWithContext(String url) {
        String appContext = getApplicationContextURI();
        if (isRunningUnderRootContext()) {
            return url;
        } else {
            if (url.startsWith(this.SLASH)) {
                return appContext + url;
            } else {
                return appContext + SLASH + url;
            }
        }
    }

    /*
     * protected String getTranslatedURLWithContext(String url){ // // @todo:
     * implement // return url; }
     */
    public String getWindowOpenerURI() {
		if(USE_NEW_URL_SCHEME){
			return getTranslatedURIWithContext(NEW_WINDOW_URL);
		}
		else{	
			return getTranslatedURIWithContext(windowOpenerURL);
		}
    }

    public String getWindowOpenerURI(Class windowToOpen) {
    		if(USE_NEW_URL_SCHEME){
    			return getWindowOpenerURI()+getEncryptedClassName(windowToOpen);
    		}
    		else{
	        StringBuffer url = new StringBuffer();
	        url.append(getWindowOpenerURI()).append('?').append(
	                PARAM_IW_FRAME_CLASS_PARAMETER).append('=').append(
	                getEncryptedClassName(windowToOpen));
	
	        return url.toString();
	        //return
	        // getWindowOpenerURI()+"?"+PARAM_IW_FRAME_CLASS_PARAMETER+"="+windowToOpen.getName();
    		}
    }

    public String getWindowOpenerURI(Class windowToOpen,
            int ICObjectInstanceIDToOpen) {
        return getWindowOpenerURI(windowToOpen) + "&"
                + _PARAMETER_IC_OBJECT_INSTANCE_ID + "="
                + ICObjectInstanceIDToOpen;
    }

    public String getObjectInstanciatorURI() {
    		if(USE_NEW_URL_SCHEME){
    			return getTranslatedURIWithContext(NEW_WINDOW_URL);  
    		}
    		else{
    			return getTranslatedURIWithContext(objectInstanciatorURL);    
    		}
    }

    public String getMediaServletURI() {
        return getTranslatedURIWithContext(MEDIA_SERVLET_URL);
    }

    public String getBuilderPagePrefixURI(){
    		if(USE_NEW_URL_SCHEME){
    			return getTranslatedURIWithContext(NEW_BUILDER_PAGE_URL);
    		}
    		else{
    			return getTranslatedURIWithContext(BUILDER_SERVLET_URL);
    		}
    }
    
   
    private String getBuilderServletURI() {
        return getTranslatedURIWithContext(BUILDER_SERVLET_URL);
    }

    public String getIFrameContentURI() {
        return getTranslatedURIWithContext(_IFRAME_CONTENT_URL);
    }

    public String getIdegaWebApplicationsURI() {
        return getTranslatedURIWithContext(IDEGAWEB_APP_SERVLET_URI);
    }

    /*
     * public String getRealPath(String p0){ return application.getRealPath(p0); }
     */
    /**
     * Returns the real path to the resource associated with the request URI.
     * <br>
     * <br>
     * This method takes into account the application context URI that the
     * application is running under unlike ServletContext.getRealPath(String s0)
     */
    public String getRealPath(String requestURI) {
        if (this.isRunningUnderRootContext()) {
            return this.application.getRealPath(requestURI);
        } else {
            String uri = requestURI.substring(this.getApplicationContextURI()
                    .length(), requestURI.length());
            return application.getRealPath(uri);
        }
    }

    public String getCacheDirectoryURI() {
        if (cacheDirURI == null) {
            cacheDirURI = getTranslatedURIWithContext(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
        }
        return cacheDirURI;
    }

    public void addApplicationEventListener(Class eventListenerClass) {
        List eventListeners = (List) getAttribute(ApplicationEventListenersParameter);
        if (eventListeners == null) eventListeners = new ArrayList();
        if (!eventListeners.contains(eventListenerClass.getName()))
                eventListeners.add(eventListenerClass.getName());
        setAttribute(ApplicationEventListenersParameter, eventListeners);
    }

    public List getApplicationEventListeners() {
        List eventListeners = (List) getAttribute(ApplicationEventListenersParameter);
        if (eventListeners == null) eventListeners = new ArrayList();
        return eventListeners;
    }
    /**
     * Gets the default IWMainApplication instance running.
     * This is set when the first IWMainApplication is instanciated.
     * @return the default application instance
     */
    public static IWMainApplication getDefaultIWMainApplication(){
    		return defaultIWMainApplication;
    }
    /**
     * Gets the context for the default IWMainApplication instance running.
     * This is set when the first IWMainApplication is instanciated.
     * @return the default application context
     */    
    public static IWApplicationContext getDefaultIWApplicationContext(){
		return getDefaultIWMainApplication().getIWApplicationContext();
	}

    /**
     * @param appServer
     */
    public void setApplicationServer(AppServer appServer) {
        this.applicationServer = appServer;
    }
    
    /**
     * @param appServer
     */
    public AppServer getApplicationServer(){
        return this.applicationServer;
    }

}