/*
 * $Id: IWMainApplication.java,v 1.207 2009/04/17 10:45:19 valdas Exp $
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
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
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
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.appserver.AppServer;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.business.ICDomainLookup;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.messaging.MessagingSettings;
import com.idega.core.view.ViewManager;
import com.idega.data.DatastoreInterface;
import com.idega.data.EntityControl;
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
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.Executer;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;
import com.idega.util.ThreadContext;
import com.idega.util.dbschema.SQLSchemaAdapter;
import com.idega.util.expression.ELUtil;
import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceFactory;
import com.idega.util.reflect.MethodFinder;
import com.idega.util.reflect.MethodInvoker;
import com.idega.util.reflect.Property;
import com.idega.util.text.TextSoap;

/**
 * This is a class that is a base center for an idegaWeb application.<br>
 * There is typically one instance of this class per application (i.e. per servlet context).
 * This class is instanciated at startup and loads all Bundles, which can then be accessed through
 * this class.
 *
 *  Last modified: $Date: 2009/04/17 10:45:19 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.207 $
 */
public class IWMainApplication	extends Application  implements MutableClass {

	private static final Logger log = Logger.getLogger(IWMainApplication.class.getName());

	//Static final Contstants:
	/**
	 * This is the id used to store the IWMainApplication instance in the (servlet) context<br>.
	 * In JSF this can also be used to reference the instance as a ManagedBean.
	 */
	public final static String APPLICATION_BEAN_ID = "idegawebApplication";

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

    public final static String CORE_BUNDLE_IDENTIFIER = PresentationObject.CORE_IW_BUNDLE_IDENTIFIER;
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

	//private final static String APP_CONTEXT_URI_KEY = "IW_APP_CONTEXT_URI";
    private final static String SLASH = "/";
    private final static String windowOpenerURL = "/servlet/WindowOpener";
    private final static String objectInstanciatorURL = "/servlet/ObjectInstanciator";

    public final static String IMAGE_SERVLET_URL = "/servlet/ImageServlet/";
    public final static String FILE_SERVLET_URL = "/servlet/FileServlet/";

    public final static String MEDIA_SERVLET_URL = "/servlet/MediaServlet/";
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
    //This is default set to true for platform 3
    public static final boolean DEFAULT_USE_NEW_URL_SCHEME = true;
    public static boolean useNewURLScheme= DEFAULT_USE_NEW_URL_SCHEME;
	//This is default set to true for platform 3
    public static final boolean DEFAULT_USE_JSF = true;
	public static boolean useJSF = DEFAULT_USE_JSF;
    public static final boolean DEFAULT_DEBUG_FLAG = false;
    public static boolean debug = DEFAULT_DEBUG_FLAG;

    public final static String PROPERTY_DEFAULT_SERVICE_URL = "default_service_url";

    //Member variables:
    private Map<String, IWBundle> loadedBundles;
    private Properties bundlesFile;
    private File bundlesFileFile;
    private ServletContext application;
    private String propertiesRealPath;
    private String bundlesRealPath;
    private String defaultLightInterfaceColor = IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR;
    private String defaultDarkInterfaceColor = IWConstants.DEFAULT_DARK_INTERFACE_COLOR;
     //private String appContext;
    //private boolean checkedAppContext;
    private String cacheDirURI;
    private IWApplicationContext iwappContext;
	private AppServer applicationServer;
	//Holds a map of Window classes to know its dimensions etc.
    private Map windowClassesStaticInstances;
    private Application facesApplication;


	private ApplicationProductInfo applicationProductInfo;
	private ApplicationInstallationInfo applicationInstallationInfo;
    private boolean inDatabaseLessMode=false;
    private boolean inSetupMode=false;
    public static boolean loadBundlesFromJars=true;
	public static boolean loadBundlesFromWorkspace=false;
	static String workpaceBundlesFolder;
    static{
    	workpaceBundlesFolder = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);
    	if(workpaceBundlesFolder!=null){
    		loadBundlesFromWorkspace=true;
    	}
    }

    private boolean alreadyUnloaded = false; // for restart application
	//Defined as private variables to speed up reflection:
	private Object builderLogicInstance;
	private Method methodIsBuilderApplicationRunning;
	private boolean hasSetLocaleOnFacesApplication=false;
	//private String defKey = "Wwo2Y4qTTDTuRe+OjPpql0Hhoxhrf2P75XvHSSyLWTRmdsGHApCHzVHl1xlChPdQcqTAM0C6HNAn\nwXvqJj7newW7I+u4dVh4YJVI+miCOwt3/sn3Rk9mnV5MnE+hND4mR67SojlrT7+v/8kufV88DDmm\n4ALga+8/O8S/xWroxMKBnvcDKgBsMzdsB+/hy5FANkj2IauJ+pYcXrCZIDt3NAjYJG/md0QL4mQr\nzQt3FlGnL61Y34aSd3wG6Hq9GzojeO31SVsK6+mUZ8uWJNQz9aeHurPWIFE5yRdYPnakQ0DrpReQ\n2Sg5gfJeOKtK0ghX1p06CFU+nqaql6fu75FNm7ScpLDNSxXIyIOtKRoMUGQ5bV07Ej/74UXIRDql\ntWZrbXWXvdHNwUO4yX2dSkxQ1TQrWWSrrvZLE1li21qZK+3ZOPmGXAm6AB3WZ4N6tLqZ2Mw6f/x6\nTSJtto0m/DaHlsVKTliuFpV9RcTetnYgOcTBFfMLBs2DrJTtJ0LX0Ss0E/6lp3L3TnioBxPfy1e5\nkTD7ksRwFZkMdMndqI3hUmq9+D1U+VAJf6A+uCJQCyXDguZzZrYH+Uu22kyBCdsPWHE3JqxbPNeC\nIn+3aGqMbOjHoob+eyb/VANNGD34YbZW";

	private IWModuleLoader moduleLoader;
	@Autowired private MessageResourceFactory messageFactory;

	//Flag to set if bundles should be loaded the older way by reading /idegaweb/bundles folder
	public static boolean loadBundlesLegacy=false;

	@Autowired
	private BundleLocalizer bundleLocalizer;

    public static void unload()	{
    	defaultIWMainApplication = null;
    	cacheManager = null;
    	useNewURLScheme = DEFAULT_USE_NEW_URL_SCHEME;
    	useJSF = DEFAULT_USE_JSF;
    	debug = DEFAULT_DEBUG_FLAG;
    }

    public static void shutdownApplicationServices(){
    	// very special singletons
		IDOLookup.unload();
		IBOLookup.unload();
		ThreadContext.unload();
    	// mutable classes
    	ICLocaleBusiness.unload();
		SQLSchemaAdapter.unload();
		IWWelcomeFilter.unload();
		BaseFilter.unload();
		IWMainApplicationSettings.unload();
		DatastoreInterface.unload();
		EntityControl.unload();
    }

    public IWMainApplication(){
    	//Constructor exists for subclasses
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

    /**
     * <p>
     * Returns the version of the underlying platform
     * </p>
     * @return
     */
    public String getVersion() {
        /*String theReturn = this.getSettings().getProperty("version");
        if (theReturn == null) {
            theReturn = "1.4.3";
        }
        return theReturn;*/
    		return getProductInfo().getPlatformVersion();
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
    		if(this.applicationProductInfo==null){
    			this.applicationProductInfo = new ApplicationProductInfo(this);
    		}
    		return this.applicationProductInfo;
    }

    /**
     * Gets information about the installed application.
     * @return
     */
    public ApplicationInstallationInfo getInstallationInfo(){
    		if(this.applicationInstallationInfo==null){
    			this.applicationInstallationInfo = new ApplicationInstallationInfo(this);
    		}
    		return this.applicationInstallationInfo;
    }

    private void load() {
        this.setPropertiesRealPath();
        this.setBundlesRealPath();
        IWMainApplicationSettings settings = new IWMainApplicationSettings(this);
        setAttribute(SETTINGS_STORAGE_PARAMETER, settings);
        log.info("Starting " + getProductInfo().getFullProductName() + " - Version " + this.getVersion());
        loadCryptoProperties();
    }

    /**
	 */
	void regData() {
	}

	private boolean postponeBundleStarters = false;

	public void loadBundles() {
		postponeBundleStarters = true;

		loadBundlesFromWorkspace();
		loadBundlesLegacy();
    	loadBundlesFromJars();
        loadBundlesLocalizationsForJSF();
        loadBundlesResourcesResolvers();

        postponeBundleStarters = false;

        this.setAttribute("bundles", getLoadedBundles());

        for (IWBundle bundle : getLoadedBundles().values()) {
        	if (bundle.isPostponedBundleStartersRun()) {
        		bundle.setPostponedBundleStartersRun(false);
        		bundle.runBundleStarters();
        	}
        }
    }

    /**
	 * <p>
	 * This method loads the bundle instances from the jar files - this is the newer method
	 * instead of loading them from the /idegaweb/bundles folder.
	 * </p>
	 */
	private void loadBundlesFromJars() {
		if(loadBundlesFromJars){
			IWModuleLoader loader = getModuleLoader();
			loader.loadBundlesFromJars();
		}
	}

	public IWModuleLoader getModuleLoader(){
		if(moduleLoader==null){
			moduleLoader = new IWModuleLoader(this,this.application);
			if(loadBundlesFromJars){
				moduleLoader.getJarLoaders().add(new IWBundleLoader(this));

			}
		}
		return moduleLoader;
	}

	/**
     * Returns a Map over the Loaded bundles:
     * Key is a string (bundle identifier) and value is a IWBundle instance
     */
    public Map<String, IWBundle> getLoadedBundles() {
    	if (loadedBundles == null)
    		loadedBundles = new HashMap<String, IWBundle>();

    	return loadedBundles;
    }

    /*
     * method that loads the bundle localizations that can be used as value bindings for JSF
     */
    private void loadBundlesLocalizationsForJSF() {
    	for (IWBundle bundle: getLoadedBundles().values()) {
    		getBundleLocalizer().addBundle(bundle.getBundleIdentifier(), bundle);
    	}
    }

    private BundleLocalizer getBundleLocalizer() {
    	if (bundleLocalizer == null) {
    		ELUtil.getInstance().autowire(this);
    	}
    	return bundleLocalizer;
    }

    private void loadBundlesResourcesResolvers() {
    	Map<String, Map<String, String>> resources = new HashMap<String, Map<String,String>>();
    	for (IWBundle bundle: getLoadedBundles().values()) {
    		Map<String, String> bundleResolver = new WebResourceResolver(bundle.getBundleIdentifier());
    		resources.put(bundle.getBundleIdentifier(), bundleResolver);
    	}
    	setAttribute("iwResourceResolver", resources);
    }

    public void loadViewManager() {
		ViewManager viewManager = ViewManager.getInstance(this);

		if (useJSF) {
    		ApplicationFactory factory = getApplicationFactory();
			replaceJSFApplication(factory);

			Application app = factory.getApplication();
			ViewHandler standardViewHandler = app.getViewHandler();

			viewManager.initializeStandardViews(standardViewHandler);
		} else {
			//Here there is no default ViewHandler
			viewManager.initializeStandardViews(null);
		}
    }

    private ApplicationFactory applicationFactory;

    public void setApplicationFactory(ApplicationFactory factory) {
    	this.applicationFactory=factory;
    }

    /**
     * Get the JSF ApplicationFactory
     * @return
     */
    public ApplicationFactory getApplicationFactory(){
    	if (this.applicationFactory == null) {
    		return (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
    	} else {
    		return this.applicationFactory;
    	}
    }

    /**
     * Replaces the JSF Application instance set by default in the ApplicationFactory to be an instance of this class.
     * @param factory
     */
    protected void replaceJSFApplication(ApplicationFactory factory){
    	Application oldApp = factory.getApplication();
    	this.setFacesApplication(oldApp);
    	factory.setApplication(this);
    	log("Replaced the JSF Application of instance "+oldApp.getClass()+" with IWMainApplication");
    }


    public String getObjectInstanciatorURI(Class className, String templateName) {
        //return getObjectInstanciatorURI(className.getName(), templateName);
    	StringBuffer buffer = new StringBuffer();
		if(useNewURLScheme){
			buffer.append(getBufferedWindowOpenerURI(className,true));
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

    public String getPublicObjectInstanciatorURI(Class<? extends UIComponent> className, String templateName) {
        //return getObjectInstanciatorURI(className.getName(), templateName);
    	StringBuffer buffer = new StringBuffer();
		if(useNewURLScheme){
			buffer.append(getBufferedWindowOpenerURI(className,true));
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
			buffer.append(getPublicObjectInstanciatorURI());
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
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    public String getObjectInstanciatorURI(String className) {
		if (useNewURLScheme) {
			try {
				Class<UIComponent> ui = RefactorClassRegistry.forName(className);
				return this.getWindowOpenerURI(ui);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return getObjectInstanciatorURIOldURLScheme(className);
    }

    public String getObjectInstanciatorURI(Class<? extends UIComponent> classToInstanciate) {
    	if (useNewURLScheme) {
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

    /**
     * @todo: Change this so it encrypts the classToInstanciateName
     */
    public static String getEncryptedClassName(String classToInstanciate) {
        return getHashCode(classToInstanciate);
    }

    public static String getEncryptedClassName(Class<?> classToInstanciate) {
        return getHashCode(classToInstanciate);
    }

    public static String decryptClassName(String encryptedClassName) {
        String decryptedClassName = getHashCodedClassName(encryptedClassName);
        if (StringUtil.isEmpty(decryptedClassName)) {
        	log.warning("Unable to resolve class name from provided encryption: '" + encryptedClassName + "'");
        }
        return decryptedClassName;
    }

    public int getMajorVersion() {
        return this.application.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.application.getMinorVersion();
    }

    public String getMimeType(String p0) {
        return this.application.getMimeType(p0);
    }

    public URL getResource(String p0) throws MalformedURLException {
        return this.application.getResource(p0);
    }

    public InputStream getResourceAsStream(String p0) {
        return this.application.getResourceAsStream(p0);
    }

    public void log(String p0) {
        this.application.log(p0);
    }

    public void log(String p0, Throwable p1) {
        this.application.log(p0, p1);
    }

    public String getServerInfo() {
        return this.application.getServerInfo();
    }

    public String getInitParameter(String p0) {
        return this.application.getInitParameter(p0);
    }

    public Enumeration getInitParameterNames() {
        return this.application.getInitParameterNames();
    }

    public <V extends Object> V getAttribute(String parameterName) {
        return (V) this.application.getAttribute(parameterName);
    }

    public <V extends Object> V getAttribute(String parameterName, V defaultObjectToReturnIfValueIsNull){
    	V value = getAttribute(parameterName);
    	if (value == null) {
    		value = defaultObjectToReturnIfValueIsNull;
    	}
    	return value;
    }

    public Enumeration getAttributeNames() {
        return this.application.getAttributeNames();
    }

    public void setAttribute(String parameterName, Object objectToStore) {
        this.application.setAttribute(parameterName, objectToStore);
    }

    public void removeAttribute(String parameterName) {
        this.application.removeAttribute(parameterName);
    }

    /**
     * <p>
     * Gets the application context from the given HttpServletRequest instance.<br/>
     * This method is NOT Multi-Domain safe.
     * </p>
     * @param application
     * @return
     */
    public static IWMainApplication getIWMainApplication(ServletContext application) {
        return (IWMainApplication) application.getAttribute(IWMainApplication.APPLICATION_BEAN_ID);
    }

    /**
     * <p>
     * Gets the application context from the given FacesContext instance.<br/>
     * This method is Multi-Domain safe.
     * </p>
     * @param application
     * @return
     */
    public static IWMainApplication getIWMainApplication(FacesContext facesContext) {
		try {
			HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			return getIWMainApplication(request);
		} catch (ClassCastException cce) {
			throw new RuntimeException("IWMainApplication.getIWMainApplication(): FacesContext does not contain a HttpServletRequest", cce);
		}
    }

    /**
     * <p>
     * Gets the application context from the given HttpServletRequest instance.<br/>
     * This method is Multi-Domain safe.
     * </p>
     * @param application
     * @return
     */
    public static IWApplicationContext getIWApplicationContext(HttpServletRequest request){
    	return getIWMainApplication(request).getIWApplicationContext();
    }
    /**
     * <p>
     * Gets the application instance from the given HttpServletRequest instance.<br/>
     * This method is Multi-Domain safe.
     * </p>
     * @param application
     * @return
     */
    public static IWMainApplication getIWMainApplication(HttpServletRequest request){

		ICDomain domain = ICDomainLookup.getInstance().getDomainByRequest(request);

		if(domain.isDefaultDomain()){
			return IWMainApplication.getDefaultIWMainApplication();
		}
		else{
			String domainServerName = domain.getServerName();
			IWMainApplication subApplication =  IWMainApplication.getDefaultIWMainApplication().getSubApplication(domainServerName);
			return subApplication;
		}
    }

    public String getDefaultDarkInterfaceColor() {
        return this.defaultDarkInterfaceColor;
    }

    public void setDefaultDarkInterfaceColor(String color) {
        this.defaultDarkInterfaceColor = color;
    }

    public String getDefaultLightInterfaceColor() {
        return this.defaultLightInterfaceColor;
    }

    public void setDefaultLightInterfaceColor(String color) {
        this.defaultLightInterfaceColor = color;
    }

    public IWMainApplicationSettings getSettings() {
        IWMainApplicationSettings settings = (IWMainApplicationSettings) this.application.getAttribute(SETTINGS_STORAGE_PARAMETER);
        return settings;
    }

    public IWSystemProperties getSystemProperties() {
        IWSystemProperties settings = (IWSystemProperties) this.application.getAttribute(this.SYSTEM_PROPERTIES_STORAGE_PARAMETER);
        if (settings == null) {
            settings = new IWSystemProperties(this);
            setAttribute(this.SYSTEM_PROPERTIES_STORAGE_PARAMETER, settings);
        }
        return settings;
    }

    public void unloadInstanceAndClass() {
        if (!this.alreadyUnloaded) {
        	unloadInstance();
        	IWMainApplication.unload();
        	this.alreadyUnloaded = true;
        }
    }

    private void unloadInstance() {
        log("[idegaWeb] : shutdown : Storing application state and deleting cached/generated content");
        storeStatus();
        //IWCacheManager.deleteCachedBlobs(this);
        //      getImageFactory(true).deleteGeneratedImages(this);

        for (Iterator iter = getLoadedBundles().values().iterator(); iter.hasNext();) {
            IWBundle bundle = (IWBundle) iter.next();
            bundle.unload();
        }
        this.loadedBundles=null;
        this.bundlesFile=null;

        // see comment above!
        if(cacheManager!=null){
    		cacheManager.unload(this);
        }
        cacheManager=null;

        this.windowClassesStaticInstances=null;

        shutdownApplicationServices();
        SingletonRepository.stop();

        this.application.removeAttribute(APPLICATION_BEAN_ID);

        removeAllApplicationAttributes();

        getIWCacheManager2().shutdown();

        this.application=null;
    }

    protected void removeAllApplicationAttributes(){
        //Temp: removing all application attributes:
        Enumeration enumeration = this.application.getAttributeNames();
        List attributes = new ArrayList();
        while (enumeration.hasMoreElements()) {
			String applicationKey = (String) enumeration.nextElement();
			//application.removeAttribute(applicationKey);
			attributes.add(applicationKey);
        }
        for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			String applicationKey = (String) iter.next();
			this.application.removeAttribute(applicationKey);
		}
    }



    public void storeStatus() {
        getSystemProperties().store();
        storeCryptoProperties();
        try {
            getBundlesFile().store(new FileOutputStream(this.bundlesFileFile), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private Properties getBundlesFile() {
    		if(this.bundlesFile==null){
    	        this.bundlesFile = new Properties();
    	        try {
    	            this.bundlesFileFile = FileUtil.getFileAndCreateIfNotExists(this
    	                    .getPropertiesRealPath(), bundlesFileName);
    	            this.bundlesFile.load(new FileInputStream(this.bundlesFileFile));
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    		}
        return this.bundlesFile;
    }


    /**
     * Gets the Path to the folder where application properties are located by default.
     * e.g. /home/idegaweb/webapp1/idegaweb/properties
     * @return the path as a String
     */
    public String getPropertiesRealPath() {
        return this.propertiesRealPath;
    }

    private void setPropertiesRealPath() {

    		String privatePath = this.getApplicationPrivateRealPath()
            + File.separator + PROPERTIES_STANDARD_DIRECTORY;
    		String publicPath = this.getApplicationSpecialRealPath() + File.separator + PROPERTIES_STANDARD_DIRECTORY;
    		File publicFile = new File(publicPath+File.separator+"idegaweb.pxml");
    		if(publicFile.exists()){
    			//Setting it to the public path to remain backwards compatible:
    			this.propertiesRealPath = this.getApplicationSpecialRealPath()
                + File.separator + PROPERTIES_STANDARD_DIRECTORY;
    		}
    		else{
        		//Setting to the private path - this is the default in platform 3.0
    			this.propertiesRealPath = privatePath;
    		}
    }


    /**
     * Gets the Path to the folder where bundles are located by default.
     * e.g. /home/idegaweb/webapp1/idegaweb/bundles
     * @return the path as a String
     */
    public String getBundlesRealPath() {
        return this.bundlesRealPath;
    }

    private void setBundlesRealPath() {
		this.bundlesRealPath = this.getApplicationSpecialRealPath() + File.separator + BUNDLES_STANDARD_DIRECTORY;
	}


    /**
	 * <p>
	 * This method returns the "real" filesystem path in the operating system to
	 * the ROOT of this WebApplication
	 * </p>
	 */
    public String getApplicationRealPath() {
        String theRet = this.application.getRealPath("/");
        if(!theRet.endsWith(File.separator)){
        		theRet +=File.separator;
        }
        return theRet;
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
        if (sBundle != null) {
        	sBundle = TextSoap.findAndReplace(sBundle, "\\", "/");
        }

        String path = "/" + getApplicationSpecialVirtualPath() + "/" + sBundle;
        return path;
    }

    private String getBundleRealPath(String bundleIdentifier) {

		if(loadBundlesFromWorkspace){
	        //check for the workpace in eclipse if property is set:
			String directory = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);

			//First try the default name (with .bundle extension)
			String sBundleDirWithBundleExtension = directory+ File.separator+bundleIdentifier+DefaultIWBundle.BUNDLE_FOLDER_STANDARD_SUFFIX;
			File bundleDir = new File(sBundleDirWithBundleExtension);
			if(bundleDir.exists()){
				return sBundleDirWithBundleExtension;
			}
			//Then try the name without the bundleExtension
			String sBundleDirWithoutBundleExtension = directory+ File.separator+bundleIdentifier;
			bundleDir = new File(sBundleDirWithoutBundleExtension);
			if(bundleDir.exists()){
				return sBundleDirWithoutBundleExtension;
			}
		}

        //String sBundle = getBundlesFile().getProperty(bundleIdentifier);
        String sBundle = getInternalBundleVirtualPath(bundleIdentifier);
        if (sBundle != null) {
            if (File.separator.equals("/")) {
                sBundle = TextSoap.findAndReplace(sBundle, "\\", "/");//unix
            } else {
                sBundle = TextSoap.findAndReplace(sBundle, "/", "\\");//windows
            }
        }
        //debug
        //System.out.println("IWMainApplication : sBundle = "+sBundle);

		//default method:
        return getApplicationSpecialRealPath() + File.separator + sBundle;
    }

    /**
     * <p>
     * This method loads the bundles from the /idegaweb/bundles folder under the expanded webapp folder.<br>
     * This is the older way and is as of platform 4.0 replaced with loading from jars instead.
     * </p>
     */
    private void loadBundlesLegacy() {
		if (loadBundlesLegacy) {
			File theRoot = new File(getApplicationSpecialRealPath(), BUNDLES_STANDARD_DIRECTORY);
			loadBundlesInFolder(theRoot);
		}
	}

    /**
     * <p>
     * This method loads the bundles from the (eclipse) workspace folder where the bundles are checked out as project<br>
     * </p>
     */
    private void loadBundlesFromWorkspace() {
		if (loadBundlesFromWorkspace) {
			File theRoot = new File(workpaceBundlesFolder);
			loadBundlesInFolder(theRoot);
		}
	}
	/**
	 * <p>
	 * TODO tryggvil describe method loadBundlesInFolder
	 * </p>
	 * @param theRoot
	 */
	private void loadBundlesInFolder(File bundlesFolder) {
		File[] bundles = bundlesFolder.listFiles();
		if (bundles != null) {
			for (int i = 0; i < bundles.length; i++) {
				//if (bundles[i].isDirectory() && (bundles[i].getName().toLowerCase().indexOf(".bundle") != -1)) {
				if (bundles[i].isDirectory()) {
					File properties = new File(bundles[i], "properties");
					File propertiesFile = new File(properties, DefaultIWBundle.propertyFileName);
					IWPropertyList list = new IWPropertyList(propertiesFile);
					String bundleIdentifier = list.getProperty(DefaultIWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY);
					if (bundleIdentifier != null) {
						String bundleDir = BUNDLES_STANDARD_DIRECTORY + File.separator + bundles[i].getName();
						try {
							this.registerBundle(bundleIdentifier, bundleDir);
						}
						catch (Throwable t) {
							log.log(Level.WARNING, "Error loading bundle " + bundleIdentifier, t);
						}
					}
				}
			}
		}
	}

    private String getInternalBundleVirtualPath(String bundleIdentifier) {
		String tryString = getBundlesFile().getProperty(bundleIdentifier);
		if (tryString != null) {
			return tryString;
		}
		if(loadBundlesLegacy){
			// tryString is now null
			File theRoot = new File(this.getApplicationSpecialRealPath(), BUNDLES_STANDARD_DIRECTORY);
			if(theRoot!=null){
				File[] bundles = theRoot.listFiles();
				if(bundles!=null){
					for (int i = 0; i < bundles.length; i++) {
						if (bundles[i].isDirectory()) {
							File properties = new File(bundles[i], "properties");
							File propertiesFile = new File(properties, DefaultIWBundle.propertyFileName);
							try {
								IWPropertyList list = new IWPropertyList(propertiesFile);
								if (list.getProperty(DefaultIWBundle.BUNDLE_IDENTIFIER_PROPERTY_KEY).equalsIgnoreCase(
										bundleIdentifier)) {
									tryString = BUNDLES_STANDARD_DIRECTORY + File.separator + bundles[i].getName();
									this.registerBundle(bundleIdentifier, tryString);
									return tryString;
								}
							}
							catch (Exception e) {
								log.info("Error reading property file of bundle: " + bundles[i].getName());
							}
						}
					}
				}
			}
			throw new IWBundleDoesNotExist(bundleIdentifier);
		}
		else{
			return BUNDLES_STANDARD_DIRECTORY + File.separator + bundleIdentifier + DefaultIWBundle.BUNDLE_FOLDER_STANDARD_SUFFIX;
		}
	}

    public IWBundle getBundle(String bundleIdentifier)throws IWBundleDoesNotExist{
        return getBundle(bundleIdentifier, false);
    }

    public IWBundle getBundle(String bundleIdentifier, boolean autoCreate)throws IWBundleDoesNotExist{
        IWBundle bundle = getLoadedBundles().get(bundleIdentifier);
        if (bundle == null) {
        	if (loadBundlesFromWorkspace) {
        		try {
	            	bundle = loadBundleLegacy(bundleIdentifier, autoCreate);
	            	loadBundle(bundle);
        		} catch (Exception e) {
        			log.warning("Bundle " + bundleIdentifier + " does not exist in workspace");
        		}
        	}
        	if (bundle == null && loadBundlesFromJars) {
        		try {
	        		bundle = loadBundleFromJar(bundleIdentifier);
	        		loadBundle(bundle);
        		} catch (Exception e) {
        			log.warning("Bundle " + bundleIdentifier + " does not exist as JAR file");
        		}
        	}
        	if (bundle == null && loadBundlesLegacy) {
            	bundle = loadBundleLegacy(bundleIdentifier, autoCreate);
            	loadBundle(bundle);
        	}
        }
        return bundle;
    }

	protected IWBundle loadBundleFromJar(String bundleIdentifier) {
		getModuleLoader().tryBundleLoad(bundleIdentifier);
		//see if it exists in the bundleMap after load:
		IWBundle bundle = getLoadedBundles().get(bundleIdentifier);
		if(bundle==null){
			throw new IWBundleDoesNotExist(bundleIdentifier);
		}
		return bundle;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method loadBundleLegacy
	 * </p>
	 *
	 * @param bundleIdentifier
	 * @param autoCreate
	 * @param bundle
	 * @return
	 */
	protected IWBundle loadBundleLegacy(String bundleIdentifier, boolean autoCreate) {
		IWBundle bundle = null;
		if (!autoCreate) {
			// Check if bundle does exist only if autocreate is false:
			File file = new File(getBundleRealPath(bundleIdentifier));
			if (!file.exists()) {
				throw new IWBundleDoesNotExist(bundleIdentifier);
			}
		}
		String realBundleDir = getBundleRealPath(bundleIdentifier);
		log.fine("Loading bundle " + bundleIdentifier + " (from " + realBundleDir + ")");
		bundle = new DefaultIWBundle(realBundleDir, getBundleVirtualPath(bundleIdentifier), bundleIdentifier, this,
				autoCreate);
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
     * Registers and loads a IWBundle with the abstract pathname relative to
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
    public List<IWBundle> getRegisteredBundles() {
        List<IWBundle> bundles = new ArrayList<IWBundle>();
        for (Iterator<?> iter = getBundlesFile().keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            IWBundle bundle = getBundle(key);
            if (bundle == null)
            	continue;

            bundles.add(bundle);
        }
        return bundles;
    }

    /**
     * Returns a List of Locale Objects in use
     */
    public List<Locale> getAvailableLocales() {
        return ICLocaleBusiness.getListOfAllLocalesJAVA();
    }

    /**
     * Only works when running on Tomcat
     */
    public boolean restartApplication() {
    	IWMainApplicationSettings settings = getSettings();
    	String apache = settings.getProperty(APACHE_RESTART_PARAMETER);
    	String actions = settings.getProperty("ePlatform_restart_actions");

        String restartScript = "/idega/bin/apache_restart.sh";

        boolean restartApacheAlso = false;

        if (apache != null) {
            restartApacheAlso = Boolean.valueOf(apache.toLowerCase()).booleanValue();
        }

        unloadInstanceAndClass();

        String prePath = System.getProperty("user.dir");//return /tomcat/bin
        log.info("IWMainApplication: restarting application server at : " + prePath);

        String customActions[] = null;
        if (!StringUtil.isEmpty(actions)) {
        	customActions = actions.split(CoreConstants.COMMA);
        }

        try {
        	 String[] commands = null;

        	//windows
            if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1) {
            	if (!restartApacheAlso) {
                    commands = ArrayUtil.isEmpty(customActions) ?
                    		new String[] {
                    			prePath + "\\shutdown.bat",
                    			prePath + "\\startup.bat"
                    		} : customActions;
                } else {
                	commands = ArrayUtil.isEmpty(customActions) ?
                			new String[] {
                    			prePath + "\\shutdown.bat",
                    			prePath + "\\startup.bat",
                    			restartScript
                			} : customActions;
                }
            } else {
            	//unix
                if (!restartApacheAlso) {
                	commands = ArrayUtil.isEmpty(customActions) ?
                    		new String[] {
                    			prePath + "./shutdown.sh",
                    			prePath + "./startup.sh"
                    		} : customActions;
                } else {
                	commands = ArrayUtil.isEmpty(customActions) ?
                    		new String[] {
                    			prePath + "./shutdown.sh",
                    			prePath + "./startup.sh",
                    			restartScript
                    		} : customActions;
                }
            }
            log.info("Executing actions: " + Arrays.asList(commands));
            Executer.executeInAnotherVM(commands);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void startAccessController() {
        this.setAccessController(this.getSettings()
                .getDefaultAccessController());
        log.info("Starting service "
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
        List<IWBundle> bundles = this.getRegisteredBundles();
        for (Iterator<IWBundle> iter = bundles.iterator(); iter.hasNext();) {
            IWBundle item = iter.next();
            item.addLocale(locale);
        }
    }

    // this is not multi domain safe
    public IWCacheManager getIWCacheManager() {
        return cacheManager;
    }

    public IWCacheManager2 getIWCacheManager2(){
    	return IWCacheManager2.getInstance(this);
    }

    private static Properties cryptoCodesPropertiesKeyedByClassName = null;

    private static Properties cryptoClassNamesPropertiesKeyedByCode = null;

    private static boolean isCryptoUsed = true;

    private String SYSTEM_PROPERTIES_STORAGE_PARAMETER = "idegaweb_system_properties";

	private Map<String,IWMainApplication> subApplications;

    private static boolean isUsingCryptoProperties() {
        return isCryptoUsed;
    }

    private void initCryptoUsage() {
        String isUsed = getSettings().getCryptoUsage();
        isCryptoUsed = !"false".equals(isUsed);
    }

    private void loadCryptoProperties() {
        initCryptoUsage();
        if (isUsingCryptoProperties()) {
            cryptoClassNamesPropertiesKeyedByCode = new Properties();
            log.info("Loading Cryptonium");
    		String file = getApplicationRealPath()+"/WEB-INF/idegaweb/properties/crypto.properties";
    		File testfile = new File(file);
    		if (!testfile.exists()) {
    			file = getPropertiesRealPath() + File.separator + "crypto.properties";
    		}
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
            log.info("Storing Cryptonium");

            try {
            	//fixme this is failing when the file doesn't already exist.
        		String file = getApplicationRealPath()+"/WEB-INF/idegaweb/properties/crypto.properties";
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
            if (cryptoCodesPropertiesKeyedByClassName == null) {
							cryptoCodesPropertiesKeyedByClassName = new Properties();
						}
            if (cryptoClassNamesPropertiesKeyedByCode == null) {
							cryptoClassNamesPropertiesKeyedByCode = new Properties();
						}

            final String className = classObject.getName();

            // if crypto code for this class has already been created
            if (cryptoCodesPropertiesKeyedByClassName.containsKey(className)) {
                String code =  (String) cryptoCodesPropertiesKeyedByClassName.get(className);
                if (code != null && code.length() == 4) {
                	return createAndStoreCryptoName(className);
                } else {
                	return code;
                }
            } else {// else crypto code for this class has NOT been created
                return createAndStoreCryptoName(className);
            }

        }
				else {
					return classObject.getName();
				}
    }

    private static String createAndStoreCryptoName(String className) {

        String crypto = (String) cryptoCodesPropertiesKeyedByClassName
                .get(className);//if someone just beat us to creating it

        if (crypto != null && crypto.length() > 4) {
            return crypto;
        } else {
            crypto = calculateClassId(className);

            try{
	            int iCrypto = Integer.parseInt(crypto);
	            while (cryptoClassNamesPropertiesKeyedByCode.containsKey(crypto)) {
	                crypto = Integer.toString(++iCrypto);
	                if (isDebugActive()) {
										System.out
										        .println("Conflicting cryptos: creating new crypto number : "
										                + iCrypto);
									}
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
                && cryptoClassNamesPropertiesKeyedByCode.containsKey(crypto)) {
					return (String) cryptoClassNamesPropertiesKeyedByCode.get(crypto);
				}
				else {
					return crypto;
				}
    }


    /**
     * Returns a unique stirng for a class name:
     */
    private static String calculateClassId(String s) {
    		//return a plain uuid:
    		return UUIDGenerator.getInstance().generateUUID();
    }

    /**
     * @deprecated method, use getApplicationContextURI() instead.
     */
    @Deprecated
		public String getContextURL() {
        //return (String) this.getAttribute(CONTEXT_PATH_KEY);
    		return this.getApplicationContextURI();
    }

    public IWApplicationContext getIWApplicationContext() {
        //IWContext iwc = new IWContext(
        if (this.iwappContext == null) {
            this.iwappContext = new IWMainApplicationContext(this);
        }
        return this.iwappContext;
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

    public boolean startFileSystem(boolean logError) {
        try {
        	ICFileSystem system = ICFileSystemFactory.getFileSystem(this.getIWApplicationContext());
        	system.initialize();
        	return true;
        } catch (Exception e) {
        	if (logError)
        		log.warning("IWMainApplication.startFileSystem() : There was an error, most likely the media bundle is not installed");
        }
        return false;
    }

    /**
     * Sets the context path the application is running under.
     * This method is typically called from a ServletFilter or a base Servlet.
     * @param uri The uri to set, e.g. "/myapplication" or "/"
     */
    public void setApplicationContextURI(String uri) {
        String appContext = uri;
    		if (uri != null) {
            if (uri.startsWith(SLASH)) {
                appContext = uri;
            } else {
                appContext = SLASH + uri;
            }
        } else {
            appContext = SLASH;
        }
    	getIWApplicationContext().getDomain().setServerContextPath(appContext);
    }

    /**
     * @return The part of the request URI that belongs to this application,
     *         returns "/" if running under the ROOT context
     */
    public String getApplicationContextURI() {
        /*if (!checkedAppContext) {
            setApplicationContextURI(this.getSettings().getProperty(
                    APP_CONTEXT_URI_KEY));
            checkedAppContext = true;
        }*/
    		String appContext = getIWApplicationContext().getDomain().getServerContextPath();
        if (appContext == null) { return SLASH; }
        return appContext;
    }

    /**
     * Returns true if the context path the application is "/"
     */
    public boolean isRunningUnderRootContext() {
    		String appContext = getApplicationContextURI();
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
     * Returns the prefix for the 'window opener' URI, for the new platform this is by default only avaiable for logged in users<br>
     * For the new platform this is '/window/' but for older versions this is '/servlet/WindowOpener'
     */
    public String getWindowOpenerURIWithoutContextPath() {
		if(useNewURLScheme){
			return NEW_WINDOW_URL;
		}
		else{
			return windowOpenerURL;
		}
    }
    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open only for logged in users<br>
     * For the new platform this is '/workspace/window/E0410143-CF32-42B1-A97B-E712AA702962'
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getWindowOpenerURI(Class<? extends UIComponent> windowToOpen) {
    	return getBufferedWindowOpenerURI(windowToOpen, true).toString();
    }

    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open only for logged in users<br>
     * For the new platform this is '/workspace/window/E0410143-CF32-42B1-A97B-E712AA702962'
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'<br/>
     * This Method does not prefix the URI with the webapplication context path if any.
     *
     */
    public String getWindowOpenerURIWithoutContextPath(Class<? extends UIComponent> windowToOpen) {
    	return getBufferedWindowOpenerURI(windowToOpen, false).toString();
    }

    private StringBuffer getBufferedWindowOpenerURI(Class<? extends UIComponent> windowToOpen, boolean includeContextPath) {
    	StringBuffer buffer = new StringBuffer();
    	String windowUri = includeContextPath ? getWindowOpenerURI() : getWindowOpenerURIWithoutContextPath();
    	buffer.append(windowUri);
    	if (useNewURLScheme) {
    		buffer.append(getEncryptedClassName(windowToOpen));
    	} else {
    		buffer.append('?').append(PARAM_IW_FRAME_CLASS_PARAMETER).append('=').append(getEncryptedClassName(windowToOpen));
    	}
	    return buffer;
    }


    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/E0410143-CF32-42B1-A97B-E712AA702962'
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getPublicWindowOpenerURI(Class<? extends UIComponent> windowToOpen) {
    	return getPublicWindowOpenerURI(windowToOpen, -1);
    }

    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/E0410143-CF32-42B1-A97B-E712AA702962'
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getPublicObjectInstanciatorURI(Class<? extends UIComponent> windowToOpen) {
    	return getPublicObjectInstanciatorURI(windowToOpen, -1);
    }

    /**
     * Returns the prefix for the 'window opener' URI that is meant for windows to be open for all users (even not logged on)<br>
     * For the new platform this is '/window/E0410143-CF32-42B1-A97B-E712AA702962'
     * but for older versions this is '/servlet/WindowOpener?idegaweb_frame_class=1234'
     */
    public String getPublicWindowOpenerURI(Class<? extends UIComponent> windowToOpen, int ICObjectInstanceIDToOpen) {
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
    public String getPublicObjectInstanciatorURI(Class<? extends UIComponent> windowToOpen, int ICObjectInstanceIDToOpen) {
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

    public String getWindowOpenerURI(Class<? extends UIComponent> windowToOpen, int ICObjectInstanceIDToOpen) {
    	StringBuffer windowOpenerUri = getBufferedWindowOpenerURI(windowToOpen,true);
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
            return this.application.getRealPath(uri);
        }
    }

    public String getCacheDirectoryURI() {
        if (this.cacheDirURI == null) {
            this.cacheDirURI = getTranslatedURIWithContext(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
        }
        return this.cacheDirURI;
    }

    public void addApplicationEventListener(Class eventListenerClass) {
        List eventListeners = (List) getAttribute(ApplicationEventListenersParameter);
        if (eventListeners == null) {
					eventListeners = new ArrayList();
				}
        if (!eventListeners.contains(eventListenerClass.getName())) {
					eventListeners.add(eventListenerClass.getName());
				}
        setAttribute(ApplicationEventListenersParameter, eventListeners);
    }

    public List getApplicationEventListeners() {
        List eventListeners = (List) getAttribute(ApplicationEventListenersParameter);
        if (eventListeners == null) {
					eventListeners = new ArrayList();
				}
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
    public static IWApplicationContext getDefaultIWApplicationContext() {
    	if (getDefaultIWMainApplication() != null) {
    		return getDefaultIWMainApplication().getIWApplicationContext();
    	}

		return null;
	}

    public IWMainApplication getSubApplication(String domainServerName) {
		IWMainApplication subApplication = getSubApplications().get(domainServerName);
		if(subApplication==null){
			subApplication = new IWSubApplication(this,domainServerName);
			getSubApplications().put(domainServerName, subApplication);
		}
		return subApplication;

	}

    protected Map<String,IWMainApplication> getSubApplications(){
    	if(subApplications==null){
    		subApplications=new HashMap<String,IWMainApplication>();
    	}
    	return subApplications;
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
    			this.windowClassesStaticInstances=new WeakHashMap();
    		}
    		return this.windowClassesStaticInstances;
    }

    /**
     * Returns true if the Builder Application is running for the user.
     * @return
     */
	public boolean isBuilderApplicationRunning(IWUserContext iwuc){
		//This method was moved from IWContext but moved here because of static references
		//Reflection workaround:
		try{
			if(this.builderLogicInstance==null){
				MethodInvoker invoker = MethodInvoker.getInstance();
				MethodFinder finder = MethodFinder.getInstance();
				Class builderLogicClass = RefactorClassRegistry.forName("com.idega.builder.business.BuilderLogic");
				this.builderLogicInstance = invoker.invokeStaticMethodWithNoParameters(builderLogicClass,"getInstance");
				this.methodIsBuilderApplicationRunning = finder.getMethodWithNameAndOneParameter(builderLogicClass,"isBuilderApplicationRunning",IWUserContext.class);
			}
			Object[] args = {iwuc};
			return ((Boolean) this.methodIsBuilderApplicationRunning.invoke(this.builderLogicInstance,args)).booleanValue();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		/*return(BuilderLogic.getInstance().isBuilderApplicationRunning(this));
		 */
		return false;
	}

	protected Application getFacesApplication(){
		return this.facesApplication;
	}
	protected void setFacesApplication(Application jsfApplication){
		if(jsfApplication.equals(this)){
			throw new RuntimeException("Cannot set same instance (itself) as internal facesApplication");
		}
		this.facesApplication=jsfApplication;
	}

	//Begin JSF Application implementation


	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getActionListener()
	 */
	@Override
	public ActionListener getActionListener() {
		return getFacesApplication().getActionListener();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setActionListener(javax.faces.event.ActionListener)
	 */
	@Override
	public void setActionListener(ActionListener listener) {
		getFacesApplication().setActionListener(listener);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getDefaultLocale()
	 */
	@Override
	public Locale getDefaultLocale() {
		Locale locale = getFacesApplication().getDefaultLocale();
		if(!this.hasSetLocaleOnFacesApplication){
			locale = getSettings().getDefaultLocale();
			setDefaultLocale(locale);
			this.hasSetLocaleOnFacesApplication=true;
		}
		return locale;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setDefaultLocale(java.util.Locale)
	 */
	@Override
	public void setDefaultLocale(Locale locale) {
		getFacesApplication().setDefaultLocale(locale);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getDefaultRenderKitId()
	 */
	@Override
	public String getDefaultRenderKitId() {
		return getFacesApplication().getDefaultRenderKitId();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setDefaultRenderKitId(java.lang.String)
	 */
	@Override
	public void setDefaultRenderKitId(String renderKitId) {
		getFacesApplication().setDefaultRenderKitId(renderKitId);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getMessageBundle()
	 */
	@Override
	public String getMessageBundle() {
		return getFacesApplication().getMessageBundle();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setMessageBundle(java.lang.String)
	 */
	@Override
	public void setMessageBundle(String bundle) {
		getFacesApplication().setMessageBundle(bundle);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getNavigationHandler()
	 */
	@Override
	public NavigationHandler getNavigationHandler() {
		return getFacesApplication().getNavigationHandler();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setNavigationHandler(javax.faces.application.NavigationHandler)
	 */
	@Override
	public void setNavigationHandler(NavigationHandler handler) {
		getFacesApplication().setNavigationHandler(handler);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getPropertyResolver()
	 */
	@Override
	public PropertyResolver getPropertyResolver() {
		return getFacesApplication().getPropertyResolver();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setPropertyResolver(javax.faces.el.PropertyResolver)
	 */
	@Override
	public void setPropertyResolver(PropertyResolver resolver) {
		getFacesApplication().setPropertyResolver(resolver);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getVariableResolver()
	 */
	@Override
	public VariableResolver getVariableResolver() {
		return getFacesApplication().getVariableResolver();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setVariableResolver(javax.faces.el.VariableResolver)
	 */
	@Override
	public void setVariableResolver(VariableResolver resolver) {
		getFacesApplication().setVariableResolver(resolver);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getViewHandler()
	 */
	@Override
	public ViewHandler getViewHandler() {
		return getFacesApplication().getViewHandler();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setViewHandler(javax.faces.application.ViewHandler)
	 */
	@Override
	public void setViewHandler(ViewHandler handler) {
		getFacesApplication().setViewHandler(handler);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getStateManager()
	 */
	@Override
	public StateManager getStateManager() {
		return getFacesApplication().getStateManager();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setStateManager(javax.faces.application.StateManager)
	 */
	@Override
	public void setStateManager(StateManager manager) {
		getFacesApplication().setStateManager(manager);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addComponent(java.lang.String, java.lang.String)
	 */
	@Override
	public void addComponent(String componentType, String componentClass) {
		getFacesApplication().addComponent(componentType,componentClass);
	}

	//TODO: Move this logic to the builder package.
	public static String BUILDER_PAGE_PREFIX="BuilderPage";
	public static String BUILDER_MODULE_PREFIX="BuilderModule";
	public static String BUILDER_PREFIX="Builder";

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createComponent(java.lang.String)
	 */
	@Override
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
				if (moduleId.startsWith("#{") && moduleId.endsWith("}")) {
					moduleId = Property.getValueFromExpression(moduleId, String.class);
				}

				//String icObjectId=parameters[2];
				String componentClass;

				/*if(componentType.indexOf(ICObjectBusiness.UUID_PREFIX)!=-1){
					componentClass = parameters[4];
				}
				else{
					componentClass = parameters[3];
				}*/
				componentClass=componentType.substring(componentType.lastIndexOf("_")+1);
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
			return getFacesApplication().createComponent(componentType);
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createComponent(javax.faces.el.ValueBinding, javax.faces.context.FacesContext, java.lang.String)
	 */
	@Override
	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType) throws FacesException {
		return getFacesApplication().createComponent(componentBinding,context,componentType);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getComponentTypes()
	 */
	@Override
	public Iterator getComponentTypes() {
		return getFacesApplication().getComponentTypes();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addConverter(java.lang.String, java.lang.String)
	 */
	@Override
	public void addConverter(String converterId, String converterClass) {
		getFacesApplication().addConverter(converterId,converterClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addConverter(java.lang.Class, java.lang.String)
	 */
	@Override
	public void addConverter(Class targetClass, String converterClass) {
		getFacesApplication().addConverter(targetClass,converterClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createConverter(java.lang.String)
	 */
	@Override
	public Converter createConverter(String converterId) {
		return getFacesApplication().createConverter(converterId);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createConverter(java.lang.Class)
	 */
	@Override
	public Converter createConverter(Class targetClass) {
		return getFacesApplication().createConverter(targetClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getConverterIds()
	 */
	@Override
	public Iterator getConverterIds() {
		return getFacesApplication().getConverterIds();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getConverterTypes()
	 */
	@Override
	public Iterator getConverterTypes() {
		return getFacesApplication().getConverterTypes();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createMethodBinding(java.lang.String, java.lang.Class[])
	 */
	@Override
	public MethodBinding createMethodBinding(String ref, Class[] params) throws ReferenceSyntaxException {
		return getFacesApplication().createMethodBinding(ref,params);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getSupportedLocales()
	 */
	@Override
	public Iterator<Locale> getSupportedLocales() {
		return getFacesApplication().getSupportedLocales();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#setSupportedLocales(java.util.Collection)
	 */
	@Override
	public void setSupportedLocales(Collection<Locale> locales) {
		getFacesApplication().setSupportedLocales(locales);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#addValidator(java.lang.String, java.lang.String)
	 */
	@Override
	public void addValidator(String validatorId, String validatorClass) {
		getFacesApplication().addValidator(validatorId,validatorClass);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createValidator(java.lang.String)
	 */
	@Override
	public Validator createValidator(String validatorId) throws FacesException {
		return getFacesApplication().createValidator(validatorId);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#getValidatorIds()
	 */
	@Override
	public Iterator getValidatorIds() {
		return getFacesApplication().getValidatorIds();
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.Application#createValueBinding(java.lang.String)
	 */
	@Override
	public ValueBinding createValueBinding(String ref) throws ReferenceSyntaxException {
		return getFacesApplication().createValueBinding(ref);
	}

	public ValueExpression createValueExpression(String ref, Class<?> expectedReturnType) {
		return createValueExpression(FacesContext.getCurrentInstance().getELContext(), ref, expectedReturnType);
	}
	public ValueExpression createValueExpression(ELContext elContext, String ref, Class<?> expectedReturnType) {
		return getFacesApplication().getExpressionFactory().createValueExpression(elContext, ref, expectedReturnType);
	}

	//End JSF Application implementation

	/**
	 *
	 */
	public static void reg(String encrLic, String systemIdentifier, String productInfo) {

		byte[] bServUrl = {104, 116, 116, 112, 58, 47, 47, 115, 116, 111, 114, 101, 46, 105, 100, 101, 103, 97, 46, 99, 111, 109, 47, 115, 101, 114, 118, 105, 99, 101, 115, 47, 76, 105, 99, 101, 110, 99, 101, 83, 101, 114, 118, 105, 99, 101};
		String serviceUrl = null;
		try {
			serviceUrl = new String(bServUrl,CoreConstants.ENCODING_UTF8);
		}
		catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		String urlEncLic;
		try {
			urlEncLic = URLEncoder.encode(encrLic,CoreConstants.ENCODING_UTF8);
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
			httpconn.getContent();
			httpconn.disconnect();
		}
		catch (ProtocolException pe) {
			System.err.println(pe.getMessage());
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
		return this.inDatabaseLessMode;
	}
	public void setInDatabaseLessMode(boolean inDatabaseLessMode) {
		this.inDatabaseLessMode = inDatabaseLessMode;
	}

	/**
	 * Gets if the application is in "setup" mode, i.e. when the application hasn't been configured.
	 * This is set to true when no db.properties and installation.properties is found.
	 */
	public boolean isInSetupMode() {
		return this.inSetupMode;
	}
	public void setInSetupMode(boolean inSetupMode) {
		this.inSetupMode = inSetupMode;
	}
	/**
	 * <p>
	 * Return true if a bundle with bundleIdentifier is already loaded by application
	 * </p>
	 * @param bundleIdentifier
	 * @return
	 */
	public boolean isBundleLoaded(String bundleIdentifier) {
		return getLoadedBundles().containsKey(bundleIdentifier);
	}

	/**
	 * <p>
	 * Registers a bundle into the application and calls necessary loading on it.
	 * </p>
	 * @param bundleIdentifier
	 * @param bundle
	 */
	public void loadBundle(IWBundle bundle) {
		if(!this.isBundleLoaded(bundle.getBundleIdentifier())){
			String bundleIdentifier = bundle.getBundleIdentifier();
			getLoadedBundles().put(bundleIdentifier, bundle);
			// must be put in the loadedBundles map FIRST to prevent looping if
			// a starter class calls IWMainApplication.getBundle(...) for the
			// same bundleidentifier

			if(postponeBundleStarters) {

				bundle.setPostponedBundleStartersRun(true);

			} else {

				bundle.runBundleStarters();
			}
		}
	}

//	public void loadBundle(IWBundle bundle) {
//		loadBundle(bundle, false);
//	}

	public ServletContext getServletContext(){
		return this.application;
	}

	public Set getResourcePaths(String s){
		return this.application.getResourcePaths(s);
	}

	private MessagingSettings messagingSettings;
	public MessagingSettings getMessagingSettings(){
		if(messagingSettings==null){
			messagingSettings = new MessagingSettings(this);
		}
		return messagingSettings;
	}

	/**
	 * <p>
	 * Gets localized String message from one of many messageResources
	 * </p>
	 * @param key - message key
	 * @param valueIfNotFound - value that is set to message resource (if autoinsert is enabled) and/or returned in case if not found
	 * @param bundleIdentifier - bundleIdentifier for which message should belong
	 * @param locale - locale for string
	 */
	public String getLocalizedStringMessage(String key, String valueIfNotFound, String bundleIdentifier, Locale locale) {
		Object foundValue = getMessageFactory().getLocalizedMessage(key, valueIfNotFound, bundleIdentifier, locale);
		if (foundValue == null) {
			return null;
		} else {
			return String.valueOf(foundValue);
		}
	}

	/**
	 * <p>
	 * Gets localized String message from one of many messageResources
	 * </p>
	 * @param key - message key
	 * @param valueIfNotFound - value that is set to message resource (if autoinsert is enabled) and/or returned in case if not found
	 * @param bundleIdentifier - bundleIdentifier for which message should belong
	 */
	public String getLocalizedStringMessage(String key, String valueIfNotFound, String bundleIdentifier) {
		Locale locale = CoreUtil.getIWContext().getCurrentLocale();
		String foundValue = getMessageFactory().getLocalizedMessage(key, valueIfNotFound, bundleIdentifier, locale);
		return StringUtil.isEmpty(foundValue) ? null : foundValue;
	}

	public List<String> getAvailableMessageStorageTypes() {
		List<MessageResource> resources = getMessageFactory().getAvailableUninitializedMessageResources();

		List<String> stringTypes = new ArrayList<String>(resources.size());
		for (MessageResource resource: resources) {
			stringTypes.add(resource.getIdentifier());
		}
		return stringTypes;
	}

	public MessageResourceFactory getMessageFactory() {
		if (messageFactory == null)
			ELUtil.getInstance().autowire(this);
		return messageFactory;
	}

	@Override
	public void addELResolver(ELResolver resolver) {
		getFacesApplication().addELResolver(resolver);
	}

	@Override
	public void addELContextListener(ELContextListener listener) {
		getFacesApplication().addELContextListener(listener);
	}

	@Override
	public UIComponent createComponent(ValueExpression componentExpression,
			FacesContext facesContext, String componentType)
			throws FacesException, NullPointerException {
		return getFacesApplication().createComponent(componentExpression,
				facesContext, componentType);
	}

	@Override
	public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType) throws ELException {
		return getFacesApplication().evaluateExpressionGet(context, expression,	expectedType);
	}

	@Override
	public ELContextListener[] getELContextListeners() {
		return getFacesApplication().getELContextListeners();
	}

	@Override
	public ELResolver getELResolver() {
		return getFacesApplication().getELResolver();
	}

	@Override
	public ExpressionFactory getExpressionFactory() {
		return getFacesApplication().getExpressionFactory();
	}

	@Override
	public ResourceBundle getResourceBundle(FacesContext ctx, String name)
			throws FacesException, NullPointerException {
		return getFacesApplication().getResourceBundle(ctx, name);
	}

	@Override
	public void removeELContextListener(ELContextListener listener) {
		getFacesApplication().removeELContextListener(listener);
	}
}