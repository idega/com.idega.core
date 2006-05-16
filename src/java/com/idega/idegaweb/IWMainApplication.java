/*
 * $Id: IWMainApplication.java,v 1.149.2.2 2006/05/16 15:29:54 sigtryggur Exp $
 * Created in 2001 by Tryggvi Larusson
 * 
 * Copyright (C) 2001-2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;
import javax.servlet.ServletContext;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.appserver.AppServer;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.view.ViewManager;
import com.idega.data.DatastoreInterface;
import com.idega.data.IDOLookup;
import com.idega.exception.IWBundleDoesNotExist;
import com.idega.graphics.generator.ImageFactory;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.MutableClass;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.SingletonRepository;
import com.idega.servlet.filter.BaseFilter;
import com.idega.servlet.filter.IWWelcomeFilter;
import com.idega.util.Executer;
import com.idega.util.FileUtil;
import com.idega.util.LogWriter;
import com.idega.util.ThreadContext;
import com.idega.util.dbschema.SQLSchemaAdapter;
import com.idega.util.reflect.MethodFinder;
import com.idega.util.reflect.MethodInvoker;
import com.idega.util.text.TextSoap;

/**
 * This is a class that is a base center for an idegaWeb application.<br>
 * There is typically one instance of this class per application (i.e. per servlet context).
 * This class is instanciated at startup and loads all Bundles, which can then be accessed through
 * this class.
 * 
 *  Last modified: $Date: 2006/05/16 15:29:54 $ by $Author: sigtryggur $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.149.2.2 $
 */
public class IWMainApplication	extends Application  implements MutableClass {

	//Static final Contstants:
	/**
	 * This is the id used to store the IWMainApplication instance in the (servlet) context<br>.
	 * In JSF this can also be used to reference the instance as a ManagedBean.
	 */
	public final static String APPLICATION_BEAN_ID = "idegaweb_application";
	
    public final static String IdegaEventListenerClassParameter = "idegaweb_event_classname";
    public final static String ApplicationEventListenersParameter = "idegaweb_application_events";
    public final static String IWEventSessionAddressParameter = "iw_event_address"; // added
	public final static String windowOpenerParameter = Page.IW_FRAME_STORAGE_PARMETER;
	
    private final static String PARAM_IW_FRAME_CLASS_PARAMETER = com.idega.presentation.Page.IW_FRAME_CLASS_PARAMETER;
    
    public final static String templateParameter = "idegaweb_template";
    public final static String templateClassParameter = "idegaweb_template_class";
    
    public final static String classToInstanciateParameter = "idegaweb_instance_class";
    
    private final static String BUNDLES_STANDARD_DIRECTORY = "bundles";
    private final static String IDEGAWEB_SPECIAL_DIRECTORY = "idegaweb";
    private final static String IDEGAWEB_PRIVATE_DIRECTORY = "WEB-INF/idegaweb";
    private final static String PROPERTIES_STANDARD_DIRECTORY = "properties";
    
    public final static String CORE_BUNDLE_IDENTIFIER = PresentationObject.IW_BUNDLE_IDENTIFIER;
    public final static String CORE_BUNDLE_FONT_FOLDER_NAME = "iw_fonts";
    public final static String CORE_DEFAULT_FONT = "default.ttf";
    public final static String IW_ACCESSCONTROL_TYPE_PROPERTY = "iw_accesscontrol_type";
    public final static String _PROPERTY_USING_EVENTSYSTEM = "using_eventsystem";
    public final static String _ADDRESS_ACCESSCONTROLER = "iwmainapplication.ic_accesscontroler";
    public final static String _PARAMETER_IC_OBJECT_INSTANCE_ID = "parent.ic_object_instance_id";
    
    private static final String SETTINGS_STORAGE_PARAMETER = "idegaweb_main_application_settings";
    private static final String bundlesFileName = "bundles.properties";
    
    private final static String APACHE_RESTART_PARAMETER = "restart_apache";
    private final static String CONTEXT_PATH_KEY = "IW_CONTEXT_PATH";
	
    public final static String PROPERTY_NEW_URL_STRUCTURE = "new_url_structure";
	public final static String PROPERTY_JSF_RENDERING = "jsf_rendering";
    
	private final static String APP_CONTEXT_URI_KEY = "IW_APP_CONTEXT_URI";
    private final static String SLASH = "/";
    private final static String windowOpenerURL = "/servlet/WindowOpener";
    private final static String objectInstanciatorURL = "/servlet/ObjectInstanciator";
    
    public final static String IMAGE_SERVLET_URL = "/servlet/ImageServlet/";
    public final static String FILE_SERVLET_URL = "/servlet/FileServlet/";
    
    private final static String MEDIA_SERVLET_URL = "/servlet/MediaServlet/";
    private final static String BUILDER_SERVLET_URL = "/servlet/IBMainServlet/";
    private final static String _IFRAME_CONTENT_URL = "/servlet/IBIFrameServlet/";
    private final static String IDEGAWEB_APP_SERVLET_URI = "/servlet/idegaweb";
    
    private final static String NEW_WINDOW_URL="/workspace/window/";
    private final static String NEW_PUBLIC_WINDOW_URL="/window/";
    private final static String NEW_BUILDER_PAGE_URL="/pages/";
    private final static String WORKSPACE_URI="/workspace/";
    private final static String LOGIN_URI="/login/";
    
    //mutable class variables:
    protected static IWMainApplication defaultIWMainApplication = null;
    private static IWCacheManager cacheManager = null;
    public static final boolean DEFAULT_USE_NEW_URL_SCHEME = false;
    public static boolean useNewURLScheme= DEFAULT_USE_NEW_URL_SCHEME;
	//This is a temporary solution should be removed when JSF implementation is done:
    public static final boolean DEFAULT_USE_JSF = false;
	public static boolean useJSF = DEFAULT_USE_JSF;
    public static final boolean DEFAULT_DEBUG_FLAG = false;
    public static boolean debug = DEFAULT_DEBUG_FLAG;
    
    //Member variables:
    private Map loadedBundles;
    private Properties bundlesFile;
    private File bundlesFileFile;
    private ServletContext application;
    private String propertiesRealPath;
    private String bundlesRealPath;
    private String defaultLightInterfaceColor = IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;
    private String defaultDarkInterfaceColor = IWConstants.DEFAULT_DARK_INTERFACE_COLOR;    
    private LogWriter lw;
    private String appContext;
    private boolean checkedAppContext;
    private String cacheDirURI;
    private IWApplicationContext iwappContext;
	private AppServer applicationServer;
	//Holds a map of Window classes to know its dimensions etc.
    private Map windowClassesStaticInstances;
    private Application realJSFApplication;
    private ApplicationProductInfo applicationProductInfo;
    private boolean inDatabaseLessMode=false;
    private boolean inSetupMode=false;
    private boolean loadBundlesFromJars=false;
    private boolean alreadyUnloaded = false; // for restart application

	private String defKey = "Wwo2Y4qTTDTuRe+OjPpql0Hhoxhrf2P75XvHSSyLWTRmdsGHApCHzVHl1xlChPdQcqTAM0C6HNAn\nwXvqJj7newW7I+u4dVh4YJVI+miCOwt3/sn3Rk9mnV5MnE+hND4mR67SojlrT7+v/8kufV88DDmm\n4ALga+8/O8S/xWroxMKBnvcDKgBsMzdsB+/hy5FANkj2IauJ+pYcXrCZIDt3NAjYJG/md0QL4mQr\nzQt3FlGnL61Y34aSd3wG6Hq9GzojeO31SVsK6+mUZ8uWJNQz9aeHurPWIFE5yRdYPnakQ0DrpReQ\n2Sg5gfJeOKtK0ghX1p06CFU+nqaql6fu75FNm7ScpLDNSxXIyIOtKRoMUGQ5bV07Ej/74UXIRDql\ntWZrbXWXvdHNwUO4yX2dSkxQ1TQrWWSrrvZLE1li21qZK+3ZOPmGXAm6AB3WZ4N6tLqZ2Mw6f/x6\nTSJtto0m/DaHlsVKTliuFpV9RcTetnYgOcTBFfMLBs2DrJTtJ0LX0Ss0E/6lp3L3TnioBxPfy1e5\nkTD7ksRwFZkMdMndqI3hUmq9+D1U+VAJf6A+uCJQCyXDguZzZrYH+Uu22kyBCdsPWHE3JqxbPNeC\nIn+3aGqMbOjHoob+eyb/VANNGD34YbZW";
	
    public static void unload()	{
    	defaultIWMainApplication = null;
    	cacheManager = null;
    	useNewURLScheme = DEFAULT_USE_NEW_URL_SCHEME;
    	useJSF = DEFAULT_USE_JSF;
    	debug = DEFAULT_DEBUG_FLAG;
    }
    
    public IWMainApplication(ServletContext application,AppServer appserver) {
        this.application = application;
        setApplicationServer(appserver);        
        application.setAttribute(APPLICATION_BEAN_ID, this);
        //set the default application instance to this
        if(defaultIWMainApplication==null){
        		defaultIWMainApplication=this;
        }
        //attention this must be reviewed if we implement multi domains within
        // one virtualmachine
        // comment by thomas: 
        // The implementation is wrong and not fixed yet:
        // The cacheManager is a singleton and is stored twice:
        // As an attribute of an instance of the IWMainApplication and in a class variable
        // of the class IWMainApplication.. At least it is the same object.
        cacheManager = IWCacheManager.getInstance(this);
        load();
    }

    public String getVersion() {
        /*String theReturn = this.getSettings().getProperty("version");
        if (theReturn == null) {
            theReturn = "1.4.3";
        }
        return theReturn;*/
    		return getProductInfo().getVersion();
    }

    public String getBuildNumber() {
        /*String theReturn = this.getSettings().getProperty("iw_build_num");
        if (theReturn == null) {
            theReturn = "220b";
        }
        return theReturn;*/
    		return getProductInfo().getBuildId();
    }

    /**
     * Gets information about the installed application.
     * @return
     */
    public ApplicationProductInfo getProductInfo(){
    		if(applicationProductInfo==null){
    			applicationProductInfo = new ApplicationProductInfo(this);
    		}
    		return applicationProductInfo;
    }
    
    private void load() {
        lw = new LogWriter(this.getApplicationRealPath(), LogWriter.INFO);
        this.setPropertiesRealPath();
        this.setBundlesRealPath();
        IWMainApplicationSettings settings = new IWMainApplicationSettings(this);
        setAttribute(SETTINGS_STORAGE_PARAMETER, settings);
        // log("Starting the idegaWeb Application Framework - Version "
        //        + this.getVersion());
        sendStartupMessage("Starting "+getProductInfo().getFullProductName()+" - Version "
                + this.getVersion());
        loadCryptoProperties();
    }

    /**
	 */
	void regData() {
		try{
			reg(defKey,getIWApplicationContext().getDomain().getName(),getProductInfo().getName());
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public void loadBundles() {
    		loadBundlesFromJars();
        loadBundlesLegacy();
        loadBundlesLocalizationsForJSF();
        this.setAttribute("bundles",getLoadedBundles());
        
    }
    
    /**
	 * <p>
	 * This method loads the bundle instances from the jar files - this is the newer method 
	 * instead of loading them from the /idegaweb/bundles folder.
	 * </p>
	 */
	private void loadBundlesFromJars() {
		if(loadBundlesFromJars){
			IWBundleLoader loader = new IWBundleLoader(this,application);
			loader.loadBundlesFromJars();
		}
	}

	/*
     * Returns a Map over the Loaded bundles:
     * Key is a string (bundle identifier) and value is a IWBundle instance
     */
    protected Map getLoadedBundles() {
    	if(loadedBundles==null) {
    		 loadedBundles = new HashMap();
    	}
    	return loadedBundles;
    }
    
    /*
     * method that loads the bundle localizations that can be used as value bindings for JSF
     */
    private void loadBundlesLocalizationsForJSF() {
    	Map bundleForLocalizations = new HashMap();
    	for (Iterator iter = getLoadedBundles().keySet().iterator(); iter.hasNext();) {
			String bundleIdentifier = (String) iter.next();
			IWBundle bundle = getBundle(bundleIdentifier);
			BundleLocalizationMap bLocalizationMap = new BundleLocalizationMap(bundle);
			bundleForLocalizations.put(bundleIdentifier,bLocalizationMap);
    	}
    	this.setAttribute("localizedStrings",bundleForLocalizations);
    }

    public void loadViewManager(){
    		

		ViewManager viewManager = ViewManager.getInstance(this);
		if(useJSF){
	    	
	    		ApplicationFactory factory = getApplicationFactory();
			replaceJSFApplication(factory);
			
			Application app = factory.getApplication();
			ViewHandler standardViewHandler = app.getViewHandler();
			//ViewHandler iwViewHandler=origViewHandler;
			
			viewManager.initializeStandardViews(standardViewHandler);
			
			//Note: this ViewHandler instance will be changed later by 
			// the IWFacesInstaller and IWViewHandlerImpl.
		}
		else{
			//Here there is no default ViewHandler
			viewManager.initializeStandardViews(null);
		}
    }
    
    
    
    /**
     * Get the JSF ApplicationFactory
     * @return
     */
    public static ApplicationFactory getApplicationFactory(){
    		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		//Application app = factory.getApplication();
    		return factory;
    }
    
    /**
     * Replaces the JSF Application instance set by default in the ApplicationFactory to be an instance of this class.
     * @param factory
     */
    protected void replaceJSFApplication(ApplicationFactory factory){
    		Application oldApp = factory.getApplication();
    		this.setRealJSFApplication(oldApp);
    		factory.setApplication(this);
    		log("Replaced the JSF Application of instance "+oldApp.getClass()+" with IWMainApplication");      
    }
    
    
    public String getObjectInstanciatorURI(Class className, String templateName) {
        //return getObjectInstanciatorURI(className.getName(), templateName);
    	StringBuffer buffer = new StringBuffer();
		if(useNewURLScheme){
			buffer.append(getBufferedWindowOpenerURI(className));
			if (buffer.indexOf("?") < 0) {
				// there is no parameter
				buffer.append('?');
			}
			else {
				// there are already parameters
				buffer.append('&');
			}
			buffer.append(templateParameter).append('=').append(getEncryptedClassName(templateName));
		}
		else{
			buffer.append(getObjectInstanciatorURI());
			buffer.append('?').append(classToInstanciateParameter);
			buffer.append('=').append(getEncryptedClassName(className));
			buffer.append('&').append(templateParameter);
			buffer.append('=').append(getEncryptedClassName(templateName));
		} 
		return buffer.toString();
    }

    public String getObjectInstanciatorURI(String className, String templateName) {
		try {
			return getObjectInstanciatorURI(RefactorClassRegistry.forName(className),templateName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    public String getObjectInstanciatorURI(String className) {
		if(useNewURLScheme){
			try {
				return this.getWindowOpenerURI(RefactorClassRegistry.forName(className));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return getObjectInstanciatorURIOldURLScheme(className);
    }

    public String getObjectInstanciatorURI(Class classToInstanciate) {
    	if(useNewURLScheme){
    		return getWindowOpenerURI(classToInstanciate);
    	}
    	return getObjectInstanciatorURIOldURLScheme(classToInstanciate.getName());
    }

    private String getObjectInstanciatorURIOldURLScheme(String className) {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(getObjectInstanciatorURI());
    	buffer.append('?').append(classToInstanciateParameter);
    	buffer.append('=').append(getEncryptedClassName(className));
    	return buffer.toString();
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

    /**
     * Gets the application instance from the given ServletContext instance
     * @param application
     * @return
     */
    public static IWMainApplication getIWMainApplication(
            ServletContext application) {
        return (IWMainApplication) application
                .getAttribute(IWMainApplication.APPLICATION_BEAN_ID);
    }

    /**
     * Gets the application instance from the given FacesContext instance
     * @param application
     * @return
     */
    public static IWMainApplication getIWMainApplication(
            FacesContext facesContext) {
    	
    		try{
    			ServletContext servletContext = (ServletContext)facesContext.getExternalContext().getContext();
    			return getIWMainApplication(servletContext);
    		}
    		catch(ClassCastException cce){
    			throw new RuntimeException("IWMainApplication.getIWMainApplication(): FacesContext does not contain a ServletContext",cce);
    		}
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
        if (settings == null) {
            settings = new IWSystemProperties(this);
            setAttribute(SYSTEM_PROPERTIES_STORAGE_PARAMETER, settings);
        }
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
    
    public synchronized void unloadInstanceAndClass() {
        if (!alreadyUnloaded) {
        	unloadInstance();
        	IWMainApplication.unload();
        	alreadyUnloaded = true;
        }
    }
    
    private void unloadInstance() {
        log("[idegaWeb] : shutdown : Storing application state and deleting cached/generated content");
        storeStatus();
        //IWCacheManager.deleteCachedBlobs(this);
        //      getImageFactory(true).deleteGeneratedImages(this);

        for (Iterator keyIter = getLoadedBundles().keySet().iterator(); keyIter
                .hasNext();) {
            Object key = keyIter.next();
            IWBundle bundle = (IWBundle) getLoadedBundles().get(key);
            bundle.unload();
        }
        loadedBundles=null;
        bundlesFile=null;
        
        // see comment above!
        if(cacheManager!=null){
    		cacheManager.unload(this);
        }
        cacheManager=null;
        
        windowClassesStaticInstances=null;
        
        shutdownApplicationServices();
        SingletonRepository.stop();
        
        application.removeAttribute(APPLICATION_BEAN_ID);

        removeAllApplicationAttributes();
        
        application=null;
    }
    
    protected void removeAllApplicationAttributes(){
        //Temp: removing all application attributes:
        Enumeration enumeration = application.getAttributeNames();
        List attributes = new ArrayList();
        while (enumeration.hasMoreElements()) {
			String applicationKey = (String) enumeration.nextElement();
			//application.removeAttribute(applicationKey);
			attributes.add(applicationKey);
        }
        for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			String applicationKey = (String) iter.next();
			application.removeAttribute(applicationKey);
		}
    }

    public void shutdownApplicationServices(){
    	// very special singletons
		IDOLookup.unload();
		IBOLookup.unload();
		ThreadContext.unload();
    	// mutable classes
    	ICLocaleBusiness.unload();
		SQLSchemaAdapter.unload();
		IWWelcomeFilter.unload();
		BaseFilter.unload();
		DatastoreInterface.unload();
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
    		if(bundlesFile==null){
    	        bundlesFile = new Properties();
    	        try {
    	            bundlesFileFile = FileUtil.getFileAndCreateIfNotExists(this
    	                    .getPropertiesRealPath(), bundlesFileName);
    	            bundlesFile.load(new FileInputStream(bundlesFileFile));
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    		}
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
    	
    		String privatePath = this.getApplicationPrivateRealPath()
            + FileUtil.getFileSeparator() + PROPERTIES_STANDARD_DIRECTORY;
    		File privateFile = new File(privatePath+FileUtil.getFileSeparator()+"idegaweb.pxml");
    		if(privateFile.exists()){
        		//Setting to the private path if it exists:
    			this.propertiesRealPath = privatePath;
    		}
    		else{
    			//Setting it to the public path to remain backwards compatible
    			this.propertiesRealPath = this.getApplicationSpecialRealPath()
                + FileUtil.getFileSeparator() + PROPERTIES_STANDARD_DIRECTORY;
    		}    
        //debug
        //sendStartupMessage("setting propertyRealPath to : "+propertiesRealPath);
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

    /**
     * Gets the full canonical path to the open application path, e.g. /home/idegaweb/idegawebapp1/idegaweb
     * @return
     */
    public String getApplicationSpecialRealPath() {
        return this.getApplicationRealPath()
                + getApplicationSpecialVirtualPath();
    }
    /**
     * Gets the full canonical path to the private application path, e.g. /home/idegaweb/idegawebapp1/WEB-INF/idegaweb
     * @return
     */
    public String getApplicationPrivateRealPath() {
        return this.getApplicationRealPath()
                + getApplicationPrivateVirtualPath();
    }

    /**
     * Gets the open application directctory undier /idegaweb/ under the webapp root
     * @return
     */
    public String getApplicationSpecialVirtualPath() {
        return IDEGAWEB_SPECIAL_DIRECTORY;
    }
    /**
     * Gets the private application directctory under /WEB-INF/idegaweb/ under the webapp root
     * @return
     */    
    public String getApplicationPrivateVirtualPath() {
        return IDEGAWEB_PRIVATE_DIRECTORY;
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

        //check for the workpace in eclipse if property is set:
		String directory = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);
		if(directory!=null){
			//First try the default name (with .bundle extension)
			String sBundleDirWithBundleExtension = directory+ FileUtil.getFileSeparator()+sBundle;
			File bundleDir = new File(sBundleDirWithBundleExtension);
			if(bundleDir.exists()){
				return sBundleDirWithBundleExtension;
			}
			//Then try the name without the bundleExtension
			String sBundleDirWithoutBundleExtension = directory+ FileUtil.getFileSeparator()+bundleIdentifier;
			bundleDir = new File(sBundleDirWithoutBundleExtension);
			if(bundleDir.exists()){
				return sBundleDirWithoutBundleExtension;
			}
		}
		
		//default method:
        return getApplicationSpecialRealPath() + FileUtil.getFileSeparator()
                + sBundle;
    }

    /**
     * <p>
     * This method loads the bundles from the /idegaweb/bundles folder under the expanded webapp folder.<br>
     * This is the older way and will be replaced with loading from jars instead.
     * </p>
     */
    private void loadBundlesLegacy() {
        File theRoot = new File(this.getApplicationSpecialRealPath(),
                BUNDLES_STANDARD_DIRECTORY);
        File[] bundles = theRoot.listFiles();
        for (int i = 0; i < bundles.length; i++) {
            if (bundles[i].isDirectory()
                    && (bundles[i].getName().toLowerCase().indexOf(".bundle") != -1)) {
                File properties = new File(bundles[i], "properties");
                File propertiesFile = new File(properties,
                        DefaultIWBundle.propertyFileName);
                IWPropertyList list = new IWPropertyList(propertiesFile);
                String bundleIdentifier = list
                        .getProperty(DefaultIWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY);
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
                            DefaultIWBundle.propertyFileName);
                    try {
                        IWPropertyList list = new IWPropertyList(propertiesFile);
                        if (list.getProperty(
                        		DefaultIWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY)
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
        IWBundle bundle = (IWBundle) getLoadedBundles().get(bundleIdentifier);
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
	            
        			String realBundleDir = getBundleRealPath(bundleIdentifier);
        			sendStartupMessage("Loading bundle " + bundleIdentifier+" (from "+realBundleDir+")");
	            
	            bundle = new DefaultIWBundle(realBundleDir,
	                    getBundleVirtualPath(bundleIdentifier), bundleIdentifier,
	                    this, autoCreate);
	            
	            getLoadedBundles().put(bundleIdentifier, bundle);
	            //must be put in the loadedBundles map FIRST to prevent looping if a starter class calls IWMainApplication.getBundle(...) for the same bundleidentifier
	            bundle.runBundleStarters();
        		}
        		else{
        			throw new IWBundleDoesNotExist(bundleIdentifier);
        		}
        }
        return bundle;
    }

    
    /**
     * Regsters and loads a IWBundle with the default bundlePath and the bundle
     * is automatically created if it does not exist and autoCreate==true
     */
    public boolean registerBundle(String bundleIdentifier, boolean autoCreate) {
		String bundleDir = bundleIdentifier + ".bundle";
		if (bundleDir.indexOf(BUNDLES_STANDARD_DIRECTORY) == -1) {
			bundleDir = IWMainApplication.BUNDLES_STANDARD_DIRECTORY + File.separator + bundleDir;
		}
        return registerBundle(bundleIdentifier, bundleDir, autoCreate);
    }    
    
    /**
     * Regsters and loads a IWBundle with the abstact pathname relative to
     * /idegaweb on the WebServer and the identifier specified by
     * bundleIdentifier autoCr
     */
    public boolean registerBundle(String bundleIdentifier, String bundlePath) {
        return registerBundle(bundleIdentifier, bundlePath, false);
    }

    /**
     * Regsters and loads a IWBundle with the abstact pathname relative to
     * /idegaweb on the WebServer and the identifier specified by
     * bundleIdentifier <br>
     * <br>
     * Does automatically create the bundle on disk if autoCreate==true;
     */
    public boolean registerBundle(String bundleIdentifier, String bundlePath,
            boolean autoCreate) {
        getBundlesFile().setProperty(bundleIdentifier, bundlePath);
        getBundle(bundleIdentifier, autoCreate);
        return true;
    }

    /**
     * Returns a List of IWBundle Objects
     */
    public List getRegisteredBundles() {
        List vector = new ArrayList();
        Iterator iter = getBundlesFile().keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            vector.add(getBundle(key));
        }
        return vector;
    }

    /**
     * Returns a List of Locale Objects in use
     */
    public List getAvailableLocales() {
        return ICLocaleBusiness.getListOfAllLocalesJAVA();
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

        unloadInstanceAndClass();

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

    public ImageFactory getImageFactory() {
        return ImageFactory.getInstance(this);
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
            return getHashCode(RefactorClassRegistry.forName(className));
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
            crypto = calculateClassId(className);
            
            try{
	            int iCrypto = Integer.parseInt(crypto);
	            while (cryptoClassNamesPropertiesKeyedByCode.containsKey(crypto)) {
	                crypto = Integer.toString(++iCrypto);
	                if (isDebugActive())
	                        System.out
	                                .println("Conflicting cryptos: creating new crypto number : "
	                                        + iCrypto);
	            }
            }
            catch(NumberFormatException nfe){}

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
     * Returns a unique stirng for a class name:
     */
    private static String calculateClassId(String s) {
    		//return a plain uuid:
    		return UUIDGenerator.getInstance().generateUUID();
    }
    
    /**
     * This is the old implementation for crypto properties.
     * calculates the crosssum of a string
     */
    private static String calculateClassIdOld(String s) {
        char[] c = s.toCharArray();
        int sum = 0;
        for (int i = 0; i < c.length; i++) {
            if (i == 4) sum *= 3;
            if (i == 10) sum *= 2;
            sum += ((int) c[i]);
        }
        return Integer.toString(sum);
    }

    /**
     * @deprecated method, use getApplicationContextURI() instead.
     */
    public String getContextURL() {
        //return (String) this.getAttribute(CONTEXT_PATH_KEY);
    		return this.getApplicationContextURI();
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
        debug = debugFlag;
    }

    public static boolean isDebugActive() {
        return debug;
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
    		String newString = "[idegaWeb] : startup : " + message;
    		log(newString);
    		System.out.println(newString);
    }

    public void sendShutdownMessage(String message) {
    		String newString = "[idegaWeb] : shutdown : " + message;
    		log(newString);
    		System.out.println(newString);
    }

    /**
     * Sets the context path the application is running under.
     * This method is typically called from a ServletFilter or a base Servlet.
     * @param uri The uri to set, e.g. "/myapplication" or "/"
     */
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
        if (appContext == null) { return SLASH; }
        return appContext;
    }

    /**
     * Returns true if the context path the application is "/"
     */
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
            if (url.startsWith(SLASH)) {
                return appContext + url;
            } else {
                return appContext + SLASH + url;
            }
        }
    }

    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/' but for older versions this is '/servlet/WindowOpener'
     */
    public String getPublicWindowOpenerURI() {
		if(useNewURLScheme){
			return getTranslatedURIWithContext(NEW_PUBLIC_WINDOW_URL);
		}
		else{	
			return getTranslatedURIWithContext(windowOpenerURL);
		}
    }
    
    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/' but for older versions this is '/servlet/ObjectInstanciator'
     */
    public String getPublicObjectInstanciatorURI() {
		if(useNewURLScheme){
			return getTranslatedURIWithContext(NEW_PUBLIC_WINDOW_URL);
		}
		else{	
			return getTranslatedURIWithContext(objectInstanciatorURL);
		}
    }
    
    /**
     * Returns the prefix for the 'window opener' URI, for the new platform this is by default only avaiable for logged in users<br>
     * For the new platform this is '/window/' but for older versions this is '/servlet/WindowOpener'
     */
    public String getWindowOpenerURI() {
		if(useNewURLScheme){
			return getTranslatedURIWithContext(NEW_WINDOW_URL);
		}
		else{	
			return getTranslatedURIWithContext(windowOpenerURL);
		}
    }
    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open only for logged in users<br>
     * For the new platform this is '/workspace/window/E0410143-CF32-42B1-A97B-E712AA702962' 
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getWindowOpenerURI(Class windowToOpen) {
    	return getBufferedWindowOpenerURI(windowToOpen).toString();
    }
    	
    	
    private StringBuffer getBufferedWindowOpenerURI(Class windowToOpen) {
    	StringBuffer buffer = new StringBuffer();
    	if(useNewURLScheme){
    		buffer.append(getWindowOpenerURI()).append(getEncryptedClassName(windowToOpen));
    	}
    	else{
    		buffer.append(getWindowOpenerURI()).append('?').append(PARAM_IW_FRAME_CLASS_PARAMETER).append('=');
			buffer.append( getEncryptedClassName(windowToOpen));
    	}
	    return buffer;
	    //return
	    // getWindowOpenerURI()+"?"+PARAM_IW_FRAME_CLASS_PARAMETER+"="+windowToOpen.getName();
    }
    
    
    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/E0410143-CF32-42B1-A97B-E712AA702962' 
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getPublicWindowOpenerURI(Class windowToOpen) {
    	return getPublicWindowOpenerURI(windowToOpen, -1);
    }    
    
    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/E0410143-CF32-42B1-A97B-E712AA702962' 
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getPublicObjectInstanciatorURI(Class windowToOpen) {
    	return getPublicObjectInstanciatorURI(windowToOpen, -1);
    }    
    
    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/E0410143-CF32-42B1-A97B-E712AA702962' 
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getPublicWindowOpenerURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		if(useNewURLScheme){
			StringBuffer url = new StringBuffer();
			url.append(getPublicWindowOpenerURI()+getEncryptedClassName(windowToOpen));
	        if (ICObjectInstanceIDToOpen > 0) {
	        	url.append("?").append( _PARAMETER_IC_OBJECT_INSTANCE_ID).append('=').append(ICObjectInstanceIDToOpen);
	
	        }
			return url.toString();
		}
		else{
	        StringBuffer url = new StringBuffer();
	        url.append(getWindowOpenerURI()).append('?').append(
	                PARAM_IW_FRAME_CLASS_PARAMETER).append('=').append(
	                getEncryptedClassName(windowToOpen));
	        
	        if (ICObjectInstanceIDToOpen > 0) {
	        	url.append("&").append( _PARAMETER_IC_OBJECT_INSTANCE_ID).append('=').append(ICObjectInstanceIDToOpen);
	
	        }
	        return url.toString();
	        //return
	        // getWindowOpenerURI()+"?"+PARAM_IW_FRAME_CLASS_PARAMETER+"="+windowToOpen.getName();
			}
    	}    
    
    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/E0410143-CF32-42B1-A97B-E712AA702962' 
     * but for older versions this is '/servlet/ObjectInstanciator?idegaweb_frame_class=1234'
     */
    public String getPublicObjectInstanciatorURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
		if(useNewURLScheme){
			StringBuffer url = new StringBuffer();
			url.append(getPublicObjectInstanciatorURI()+getEncryptedClassName(windowToOpen));
	        if (ICObjectInstanceIDToOpen > 0) {
	        	url.append("?").append( _PARAMETER_IC_OBJECT_INSTANCE_ID).append('=').append(ICObjectInstanceIDToOpen);
	
	        }
			return url.toString();
		}
		else{
	        StringBuffer url = new StringBuffer();
	        url.append(getObjectInstanciatorURIOldURLScheme(windowToOpen.getName()));
	
	        if (ICObjectInstanceIDToOpen > 0) {
	        	url.append("&").append( _PARAMETER_IC_OBJECT_INSTANCE_ID).append('=').append(ICObjectInstanceIDToOpen);
	
	        }
	        return url.toString();
	        //return
	        // getWindowOpenerURI()+"?"+PARAM_IW_FRAME_CLASS_PARAMETER+"="+windowToOpen.getName();
			}
    	}    
    
    public String getWindowOpenerURI(Class windowToOpen, int ICObjectInstanceIDToOpen) {
    	StringBuffer windowOpenerUri = getBufferedWindowOpenerURI(windowToOpen);
    	if (windowOpenerUri.indexOf("?") < 0) {
    		// there is no parameter
    		windowOpenerUri.append('?');
    	}
    	else {
    		// there are already parameters
    		windowOpenerUri.append('&');
    	}
    	windowOpenerUri.append( _PARAMETER_IC_OBJECT_INSTANCE_ID).append('=').append(ICObjectInstanceIDToOpen);
    	return windowOpenerUri.toString();
    }

    public String getObjectInstanciatorURI() {
    		if(useNewURLScheme){
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
    		if(useNewURLScheme){
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
    		if(useNewURLScheme){
    			return getLoginURI();
    		}
    		else{
    			return getTranslatedURIWithContext(IDEGAWEB_APP_SERVLET_URI);
    		}
    	}
    
    /**
     * Gets the URI to the workspace environment (/workspace) with prefixed application context path if any.
     * @return
     */
    public String getWorkspaceURI(){
    		return getTranslatedURIWithContext(WORKSPACE_URI);
    }
    
    /**
     * Gets the URI to the standard login page  (/login) with prefixed application context path if any.
     * @return
     */
    public String getLoginURI(){
    		return getTranslatedURIWithContext(LOGIN_URI);
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
     * Removes the application context prefix from a RELATIVE PATH (an URL without the http/servername/port) and returns the URI (url without context)
     * @param URL
     */
    public String getURIFromURL(String URL){
	    String contextUri = getApplicationContextURI();
		if(!contextUri.equals("/") ){
			URL = URL.substring(URL.indexOf(contextUri)+contextUri.length());
		}
	
		return URL;
    }

    /**
     * @param appServer
     */
    public void setApplicationServer(AppServer appServer) {
        this.applicationServer = appServer;
    }
    
    /**
     * Returns the AppServer instance detected for this application
     * @param appServer
     */
    public AppServer getApplicationServer(){
        return this.applicationServer;
    }

    /**
     * Returns a Map used to store temporary instances of Window classes, to use to remember their instance attributes.<br>
     * This method should only be used by the com.idega.presentation.ui.Window class
     * @return
     */
    public Map getStaticWindowInstances(){
    		if(this.windowClassesStaticInstances==null){
    			windowClassesStaticInstances=new WeakHashMap();
    		}
    		return windowClassesStaticInstances;
    }



	//Defined as private variables to speed up reflection:
	private Object builderLogicInstance;
	private Method methodIsBuilderApplicationRunning;
    /**
     * Returns true if the Builder Application is running for the user.
     * @return
     */	
	public boolean isBuilderApplicationRunning(IWUserContext iwuc){
		//This method was moved from IWContext but moved here because of static references
		//Reflection workaround:
		try{
			if(builderLogicInstance==null){
				MethodInvoker invoker = MethodInvoker.getInstance();
				MethodFinder finder = MethodFinder.getInstance();
				Class builderLogicClass = RefactorClassRegistry.forName("com.idega.builder.business.BuilderLogic");
				builderLogicInstance = invoker.invokeStaticMethodWithNoParameters(builderLogicClass,"getInstance");
				methodIsBuilderApplicationRunning = finder.getMethodWithNameAndOneParameter(builderLogicClass,"isBuilderApplicationRunning",IWUserContext.class);
			}
			Object[] args = {iwuc};
			return ((Boolean) methodIsBuilderApplicationRunning.invoke(builderLogicInstance,args)).booleanValue();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		/*return(BuilderLogic.getInstance().isBuilderApplicationRunning(this));
		 */
		return false;
	}

	protected Application getRealJSFApplication(){
		return realJSFApplication;
	}
	protected void setRealJSFApplication(Application jsfApplication){
		this.realJSFApplication=jsfApplication;
	}
	
	//Begin JSF Application implementation
	
	
	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getActionListener()
	 */
	public ActionListener getActionListener() {
		return getRealJSFApplication().getActionListener();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setActionListener(javax.faces.event.ActionListener)
	 */
	public void setActionListener(ActionListener listener) {
		getRealJSFApplication().setActionListener(listener);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getDefaultLocale()
	 */
	public Locale getDefaultLocale() {
		return getRealJSFApplication().getDefaultLocale();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setDefaultLocale(java.util.Locale)
	 */
	public void setDefaultLocale(Locale locale) {
		getRealJSFApplication().setDefaultLocale(locale);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getDefaultRenderKitId()
	 */
	public String getDefaultRenderKitId() {
		return getRealJSFApplication().getDefaultRenderKitId();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setDefaultRenderKitId(java.lang.String)
	 */
	public void setDefaultRenderKitId(String renderKitId) {
		getRealJSFApplication().setDefaultRenderKitId(renderKitId);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getMessageBundle()
	 */
	public String getMessageBundle() {
		return getRealJSFApplication().getMessageBundle();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setMessageBundle(java.lang.String)
	 */
	public void setMessageBundle(String bundle) {
		getRealJSFApplication().setMessageBundle(bundle);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getNavigationHandler()
	 */
	public NavigationHandler getNavigationHandler() {
		return getRealJSFApplication().getNavigationHandler();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setNavigationHandler(javax.faces.application.NavigationHandler)
	 */
	public void setNavigationHandler(NavigationHandler handler) {
		getRealJSFApplication().setNavigationHandler(handler);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getPropertyResolver()
	 */
	public PropertyResolver getPropertyResolver() {
		return getRealJSFApplication().getPropertyResolver();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setPropertyResolver(javax.faces.el.PropertyResolver)
	 */
	public void setPropertyResolver(PropertyResolver resolver) {
		getRealJSFApplication().setPropertyResolver(resolver);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getVariableResolver()
	 */
	public VariableResolver getVariableResolver() {
		return getRealJSFApplication().getVariableResolver();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setVariableResolver(javax.faces.el.VariableResolver)
	 */
	public void setVariableResolver(VariableResolver resolver) {
		getRealJSFApplication().setVariableResolver(resolver);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getViewHandler()
	 */
	public ViewHandler getViewHandler() {
		return getRealJSFApplication().getViewHandler();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setViewHandler(javax.faces.application.ViewHandler)
	 */
	public void setViewHandler(ViewHandler handler) {
		getRealJSFApplication().setViewHandler(handler);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getStateManager()
	 */
	public StateManager getStateManager() {
		return getRealJSFApplication().getStateManager();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setStateManager(javax.faces.application.StateManager)
	 */
	public void setStateManager(StateManager manager) {
		getRealJSFApplication().setStateManager(manager);	
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addComponent(java.lang.String, java.lang.String)
	 */
	public void addComponent(String componentType, String componentClass) {
		getRealJSFApplication().addComponent(componentType,componentClass);		
	}

	//TODO: Move this logic to the builder package.
	static String BUILDER_PAGE_PREFIX="BuilderPage";
	static String BUILDER_MODULE_PREFIX="BuilderModule";
	static String BUILDER_PREFIX="Builder";
	
	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createComponent(java.lang.String)
	 */
	public UIComponent createComponent(String componentType) throws FacesException {
		
		if(componentType.startsWith(BUILDER_PREFIX)){
			if(componentType.startsWith(BUILDER_PAGE_PREFIX)){
				String sPageId = componentType.substring(BUILDER_PAGE_PREFIX.length()+1,componentType.length());
				//int pageId = Integer.parseInt(sPageId);
				BuilderService bService;
				try {
					//int pageId = Integer.parseInt(sPageId);
					bService = BuilderServiceFactory.getBuilderService(this.getIWApplicationContext());
					return bService.getPage(sPageId);
				}
				catch (NumberFormatException e) {
					return new Page();
				}
				catch (RemoteException e) {
					e.printStackTrace();
					throw new RuntimeException("Failed initializing page with id="+sPageId);
				}
			}
			else if(componentType.startsWith(BUILDER_MODULE_PREFIX)){
				String separator = "_";
				String[] parameters = componentType.split(separator);
				String moduleId=parameters[1];
				//String icObjectId=parameters[2];
				String componentClass=parameters[3];

				
				try {
					Object instance = RefactorClassRegistry.forName(componentClass).newInstance();
					UIComponent uicomp = (UIComponent)instance;
					uicomp.setId(moduleId);
					return uicomp;
				}
				catch (InstantiationException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
			throw new RuntimeException("Error creating component for componentType: "+componentType);
		}
		else{
			//The default to fall back to the default JSF application
			return getRealJSFApplication().createComponent(componentType);
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createComponent(javax.faces.el.ValueBinding, javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType) throws FacesException {
		return getRealJSFApplication().createComponent(componentBinding,context,componentType);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getComponentTypes()
	 */
	public Iterator getComponentTypes() {
		return getRealJSFApplication().getComponentTypes();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addConverter(java.lang.String, java.lang.String)
	 */
	public void addConverter(String converterId, String converterClass) {
		getRealJSFApplication().addConverter(converterId,converterClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addConverter(java.lang.Class, java.lang.String)
	 */
	public void addConverter(Class targetClass, String converterClass) {
		getRealJSFApplication().addConverter(targetClass,converterClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createConverter(java.lang.String)
	 */
	public Converter createConverter(String converterId) {
		return getRealJSFApplication().createConverter(converterId);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createConverter(java.lang.Class)
	 */
	public Converter createConverter(Class targetClass) {
		return getRealJSFApplication().createConverter(targetClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getConverterIds()
	 */
	public Iterator getConverterIds() {
		return getRealJSFApplication().getConverterIds();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getConverterTypes()
	 */
	public Iterator getConverterTypes() {
		return getRealJSFApplication().getConverterTypes();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createMethodBinding(java.lang.String, java.lang.Class[])
	 */
	public MethodBinding createMethodBinding(String ref, Class[] params) throws ReferenceSyntaxException {
		return getRealJSFApplication().createMethodBinding(ref,params);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getSupportedLocales()
	 */
	public Iterator getSupportedLocales() {
		return getRealJSFApplication().getSupportedLocales();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setSupportedLocales(java.util.Collection)
	 */
	public void setSupportedLocales(Collection locales) {
		getRealJSFApplication().setSupportedLocales(locales);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addValidator(java.lang.String, java.lang.String)
	 */
	public void addValidator(String validatorId, String validatorClass) {
		getRealJSFApplication().addValidator(validatorId,validatorClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createValidator(java.lang.String)
	 */
	public Validator createValidator(String validatorId) throws FacesException {
		return getRealJSFApplication().createValidator(validatorId);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getValidatorIds()
	 */
	public Iterator getValidatorIds() {
		return getRealJSFApplication().getValidatorIds();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createValueBinding(java.lang.String)
	 */
	public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return getRealJSFApplication().createValueBinding(ref);
	}
	
	//End JSF Application implementation
	
	/**
	 * 
	 */
	public static void reg(String encrLic, String systemIdentifier, String productInfo) {
		
		String serviceUrl = "http://store.idega.com/services/LicenceService";
		String urlEncLic;
		try {
			urlEncLic = URLEncoder.encode(encrLic,"UTF-8");
			serviceUrl +="?method=validateEncryptedLicence";
			serviceUrl +="&in0="+urlEncLic;
			serviceUrl +="&in1="+URLEncoder.encode(systemIdentifier);
			serviceUrl +="&in2="+URLEncoder.encode(productInfo);
		}
		catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		URL url;
		try {
			url = new URL(serviceUrl);
			URLConnection urlconn = url.openConnection();
			HttpURLConnection httpconn = (HttpURLConnection)urlconn;
			httpconn.connect();
			Object content = httpconn.getContent();
			httpconn.disconnect();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets if the application is in "database-less" mode, i.e. when the application doesn't know which database to talk to.
	 * This is set to true when no db.properties is found.
	 */
	public boolean isInDatabaseLessMode() {
		return inDatabaseLessMode;
	}
	public void setInDatabaseLessMode(boolean inDatabaseLessMode) {
		this.inDatabaseLessMode = inDatabaseLessMode;
	}
	
	/**
	 * Gets if the application is in "setup" mode, i.e. when the application hasn't been configured.
	 * This is set to true when no db.properties and installation.properties is found.
	 */
	public boolean isInSetupMode() {
		return inSetupMode;
	}
	public void setInSetupMode(boolean inSetupMode) {
		this.inSetupMode = inSetupMode;
	}
}