/*
 * $Id: DefaultIWBundle.java,v 1.53 2009/02/10 14:14:28 laddi Exp $
 * 
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.idega.core.component.business.BundleRegistrationListener;
import com.idega.core.component.business.ComponentRegistry;
import com.idega.core.component.business.ICObjectComponentInfo;
import com.idega.core.component.business.RegisterException;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.user.business.UserProperties;
import com.idega.util.CoreConstants;
import com.idega.util.LocaleUtil;
import com.idega.util.SortedProperties;
import com.idega.xml.XMLElement;
/**
 * The Default implementation if the IWBundle class to serve as a wrapper for an idegaWeb Bundle.
 * <br>
 * <br>
 * An idegaWeb Bundle is a wrapper for contained components and their properties and resources.
 * <br>
 * <br>
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class DefaultIWBundle implements java.lang.Comparable, IWBundle
{
	private static final Logger LOGGER = Logger.getLogger(DefaultIWBundle.class.getName());
	
	//Static final constants:
	private static final String DOT = "."; 
	static final String propertyFileName = "bundle" + IWPropertyList.DEFAULT_FILE_ENDING;
	static final String BUNDLE_IDENTIFIER_PROPERTY_KEY = "iw_bundle_identifier";
	static final String COMPONENTLIST_KEY = "iw_components";
	private final static String COMPONENT_NAME_PROPERTY = "component_name";
	private final static String COMPONENT_TYPE_PROPERTY = "component_type";
	private final static String COMPONENT_BLOCK_PROPERTY = "is_block";
	private final static String COMPONENT_WIDGET_PROPERTY = "is_widget";
	private final static String COMPONENT_DESCRIPTION_PROPERTY = "description";
	private final static String COMPONENT_ICON_URI_PROPERTY = "icon_uri";
	private final static String COMPONENT_PROPERTY_FILE = "component_property_file";
	private final static String BUNDLE_STARTER_CLASS = "iw_bundle_starter_class";
	private static final String slash = "/";
	private static final String shared = "shared";
	
	
	//Parameter that can be passed to the system to let it read bundles from another directory
	//than directly under the webapp, e.g. an Eclipse workspace folder.
	public static final String SYSTEM_BUNDLES_RESOURCE_DIR="idegaweb.bundles.resource.dir";
	public static final String BUNDLE_FOLDER_STANDARD_SUFFIX = ".bundle";

	//Member variables:
	private boolean autoMoveComponentPropertiesToFile = true;
	private HashMap componentPropertyListMap;
	private String identifier;
	private String rootVirtualPath;
	private String rootRealPath;
	private String resourcesVirtualPath;
	private String resourcesRealPath;
	private String propertiesRealPath;
	private String classesRealPath;
	private IWMainApplication superApplication;
	private Map localePathsLookup;
	private Map resourceBundlesLookup;
	private boolean autoCreate = false;
	//private Map handlers;
	private Map localeRealPathsLookup;
	//private SortedMap localizableStringsMap;
	private Properties localizableStringsProperties;
	private File localizableStringsFile;
	private IWPropertyList propertyList;
	private List bundleStarters;
	
	/**
	 * <p>
	 * Empty initialization, does nothing
	 * </p>
	 *
	 */
	protected DefaultIWBundle(){
	}
	
	protected DefaultIWBundle(String rootRealPath, String bundleIdentifier, IWMainApplication superApplication)
	{
		this(rootRealPath, rootRealPath, bundleIdentifier, superApplication);
	}
	protected DefaultIWBundle(String rootRealPath, String rootVirtualPath, String bundleIdentifier, IWMainApplication superApplication)
	{
		this(rootRealPath, rootVirtualPath, bundleIdentifier, superApplication, false);
	}
	protected DefaultIWBundle(
		String rootRealPath,
		String rootVirtualPath,
		String bundleIdentifier,
		IWMainApplication superApplication,
		boolean autoCreate)
	{
		initialize(rootRealPath, rootVirtualPath, bundleIdentifier, superApplication, autoCreate);
	}

	/**
	 * <p>
	 * Called from the constructors to initialize
	 * </p>
	 * @param rootRealPath
	 * @param rootVirtualPath
	 * @param bundleIdentifier
	 * @param superApplication
	 * @param autoCreate
	 */
	protected void initialize(String rootRealPath, String rootVirtualPath, String bundleIdentifier, IWMainApplication superApplication, boolean autoCreate) {
		this.autoCreate = autoCreate;
		this.superApplication = superApplication;
		this.identifier = bundleIdentifier;
		//handlers = new HashMap();
		//localeRealPaths = new HashMap();
		//resourceBundles = new HashMap();
		setBundleBaseRealPath(rootRealPath);
		setRootVirtualPath(rootVirtualPath);
		try
		{
			loadBundle();
			setProperty(BUNDLE_IDENTIFIER_PROPERTY_KEY, bundleIdentifier);
		}
		catch (Exception e)
		{
		    LOGGER.log(Level.WARNING, "Error loading bundle " + bundleIdentifier, e);
		}
	}
	
	
	/**
	 * Discards all unsaved changes to this bundle and loads it up again 
	 */
	public void reloadBundle()
	{
		reloadBundle(false);
	}
	/**
	 *Reloads all resources for this bundle and stores the state of this bundle first if storeState==true
	 *@param storeState to say if to store the state (call storeState) before the bundle is loaded again
	 */
	public void reloadBundle(boolean storeState)
	{
		this.unload(storeState);
		loadBundle();
	}
	/**
	 *Loads all necessary resources for this bundle
	 */
	protected void loadBundle()
	{
		setResourcesVirtualPath(getRootVirtualPath() + "/" + "resources");
		setResourcesRealPath(getBundleBaseRealPath() + File.separator + "resources");
		setPropertiesRealPath(getBundleBaseRealPath() + File.separator + "properties");
		setClassesRealPath();
		this.propertyList=initializePropertyList();
		StringBuffer SystemClassPath = new StringBuffer(System.getProperty("java.class.path"));
		SystemClassPath.append(File.pathSeparator);
		SystemClassPath.append(getClassesRealPath());
		System.setProperty("java.class.path", SystemClassPath.toString());
		if(!getApplication().isInDatabaseLessMode()){
			installComponents();
		}
		try
		{
			if(!getApplication().isInDatabaseLessMode()){
				createDataRecords();
				registerBlockPermisionKeys();
			}
		}
		catch (IDOLookupException e)
		{
			LOGGER.log(Level.WARNING, null, e);
		}
		catch (FinderException e)
		{
			LOGGER.fine("No bundle components found for " + getBundleIdentifier());
		}
	}
	/**
	 * <p>
	 * Initializes the base 'bundle.pxml' file for this bundle
	 * </p>
	 */
	protected IWPropertyList initializePropertyList() {
		IWPropertyList propList = null;
		if (this.autoCreate)
		{
			this.initializeStructure();
			propList = initializePropertyList(propertyFileName);
		}
		else
		{
			propList = initializePropertyList(propertyFileName, false);
		}
		return propList;
	}
	
	private void createDataRecords() throws IDOLookupException, FinderException
	{
		Collection entities = getDataObjects();
		//eiki used to be (SLOW!): 
		//Collection entities = icoHome.findAllByObjectType(ICObjectBMPBean.COMPONENT_TYPE_DATA);
		
		if (entities != null){
			Iterator iter = entities.iterator();
			while (iter.hasNext())
			{
				ICObject ico = (ICObject) iter.next();
				try
				{
					Class c = ico.getObjectClass();
					IDOLookup.instanciateEntity(c);
				}
				catch (ClassNotFoundException e)
				{
					LOGGER.warning("Loading bundle: " + this.getBundleIdentifier() + " : Class " + e.getMessage() + " not found");
				}
			}
		}
	}
	/**
	 * Returns all the DATA component types registered to this bundle
	 * @return a collection of ICObjects.
	 * @throws IDOLookupException
	 * @throws FinderException
	 */
	public Collection getDataObjects() throws IDOLookupException, FinderException {
		ICObjectHome icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		Collection entities = icoHome.findAllByObjectTypeAndBundle(ICObjectBMPBean.COMPONENT_TYPE_DATA, this.identifier);
		return entities;
	}
	private void registerBlockPermissionKeys(Class blockClass) throws InstantiationException, IllegalAccessException
	{
		Object o = blockClass.newInstance();
		if (o instanceof Block) {
			((Block) o).registerPermissionKeys();
		}
	}
	private void registerBlockPermisionKeys() throws IDOLookupException, FinderException
	{
		ICObjectHome icObjectHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		Collection objects = icObjectHome.findAllBlocksByBundle(this.getBundleIdentifier());
		if (objects != null)
		{
			Iterator iter = objects.iterator();
			while (iter.hasNext())
			{
				ICObject ico = (ICObject) iter.next();
				try
				{
					Class c = ico.getObjectClass();
					registerBlockPermissionKeys(c);
				}
				catch (ClassNotFoundException e)
				{
					LOGGER.info("Class not found for Block: " + ico.getName());
				}
				catch (InstantiationException e)
				{
					LOGGER.log(Level.WARNING, e.getMessage());
				}
				catch (IllegalAccessException e)
				{
					LOGGER.log(Level.WARNING, e.getMessage());
				}
			}
		}
	}
	/**
	 *	call the default bundle starter first (IWBundleStarter) because this starter might register some classes that are used by the other starters.
	 */
	public void runBundleStarters()
	{
		// starting of default bundle starter
		// call the default start first because this starter might register some classes that are used by 
		// the other starters
		// 
		IWBundleStartable defaultStarter = getNewDefaultBundleStarterInstance();
		if(defaultStarter!=null){
			LOGGER.fine("Running default bundle starter " + defaultStarter.getClass().getName());
			defaultStarter.start(this);
			getBundleStartersList().add(defaultStarter);
		}
		// starting starter defined in bundle property
		String starterClassName = this.getProperty(BUNDLE_STARTER_CLASS);
		if (starterClassName != null)
		{
			try
			{
				IWBundleStartable starter = (IWBundleStartable) RefactorClassRegistry.forName(starterClassName).newInstance();
				LOGGER.fine("Running additional bundle starter " + starter.getClass().getName());
				starter.start(this);
				getBundleStartersList().add(starter);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Error running additional bundle starter " + starterClassName, e);
			}
		}

	}

	protected List getBundleStartersList(){
		if(this.bundleStarters==null){
			this.bundleStarters=new ArrayList();
		}
		return this.bundleStarters;
	}
	
	
	private IWBundleStartable getNewDefaultBundleStarterInstance() {
		StringBuffer buffer = new StringBuffer(getBundleName());
		buffer.append(DOT);
		buffer.append(IWBundleStartable.DEFAULT_STARTER_CLASS);
	  	String className = buffer.toString();
	  	try {
	  		Class starterClass = RefactorClassRegistry.forName(className);
	  		return (IWBundleStartable) starterClass.newInstance();
	  	}
	  	catch (ClassNotFoundException ex) {
	  		// nothing to worry about, some bundles don't have a starter class
	  	}
	  	catch (InstantiationException ex) {
	  		LOGGER.warning("Instantiation of bundle starter class failed: "+ className);
	  	}
	  	catch (IllegalAccessException ex) {
	  		LOGGER.warning("Instantiation of bundle starter class failed, access problem: "+ className);
	  	}
	  	return null;
	}
	
	/**
	 *Stores this bundle and unloads all resources;
	 */
	public synchronized void unload()
	{
		if(getApplication().getSettings().getWriteBundleFilesOnShutdown()){
			unload(true);
		}
		else{
			unload(false);
		}
	}
	/**
	 *Unloads all resources for this bundle and stores the state of this bundle if storeState==true
	 *@param storeState to say if to store the state (call storeState)
	 */
	public synchronized void unload(boolean storeState){
		//resourceBundles.clear();
		this.resourceBundlesLookup=null;
		this.localizableStringsProperties = null;
		this.localePathsLookup=null;
		this.localeRealPathsLookup=null;
		if(storeState){
			storeState();
		}
		stopBundleStarters();
		this.localePathsLookup=null;
		this.resourceBundlesLookup=null;
		this.localeRealPathsLookup=null;
		this.localizableStringsProperties=null;
		Iterator valueIter = getComponentPropertiesListMap().values().iterator();
		while (valueIter.hasNext())
		{
			IWPropertyList element = (IWPropertyList) valueIter.next();
			element.unload();
		}
		this.componentPropertyListMap=null;
		if(this.propertyList!=null){
			this.propertyList.unload();
			this.propertyList=null;
		}

	}
	private synchronized void stopBundleStarters()
	{
		List l = getBundleStartersList();
		for (Iterator iter = l.iterator(); iter.hasNext();) {
			IWBundleStartable starter = (IWBundleStartable) iter.next();
			starter.stop(this);
		}
		this.bundleStarters=null;
	}
	private void installComponents()
	{
		List list = this.getComponentKeys();
		Iterator iter = list.iterator();
		while (iter.hasNext())
		{
			String className = (String) iter.next();
			String componentName = getComponentName(className);
			String componentType = this.getComponentType(className);
			
			String blockValue = getComponentProperty(className, COMPONENT_BLOCK_PROPERTY);
			boolean isBlock = blockValue != null ? new Boolean(blockValue).booleanValue() : false;

			String widgetValue = getComponentProperty(className, COMPONENT_WIDGET_PROPERTY);
			boolean isWidget = widgetValue != null ? new Boolean(widgetValue).booleanValue() : false;

			String description = getComponentProperty(className, COMPONENT_DESCRIPTION_PROPERTY);
			String iconURI = getComponentProperty(className, COMPONENT_ICON_URI_PROPERTY);

			if(className!=null && componentName != null && componentType!=null){
				try{
					addComponentToDatabase(className, componentType, componentName, isBlock, isWidget, description, iconURI);
				}
				catch(Throwable e){
					LOGGER.warning("Error registering component to database: "+e.getMessage());
				}
			}
			else{
				LOGGER.warning("Error registering component className="+className+",componentName="+componentName+",componentType="+componentType+" in bundle="+this.getBundleIdentifier());
			}
			
		}
	}
	/**
	 * gets the base path of this bundle.<br>
	 * e.g. /home/idegaweb/webapp/iw1/idegaweb/bundles/com.idega.core.bundle
	 */	
	public String getBundleBaseRealPath()
	{
		return this.rootRealPath;
	}
	public String getRootVirtualPath()
	{
		return this.rootVirtualPath;
	}
	public Image getIconImage()
	{
		return new Image(getProperty("iconimage"));
	}
	public String getProperty(String propertyName)
	{
		return this.propertyList.getProperty(propertyName);
	}
	public String getProperty(String propertyName, String returnValueIfNull)
	{
		String prop = getProperty(propertyName);
		if (prop == null)
		{
			if (getApplication().getSettings().isAutoCreatePropertiesActive())
			{
				if (getApplication().getSettings().isDebugActive()) {
					LOGGER.fine("Storing property: " + propertyName);
				}
				setProperty(propertyName, returnValueIfNull);
			}
			return returnValueIfNull;
		}
		else {
			return prop;
		}
	}
	public boolean getBooleanProperty(String propertyName)
	{
		return Boolean.valueOf(getProperty(propertyName)).booleanValue();
	}
	public boolean getBooleanProperty(String propertyName, boolean returnValueIfNull)
	{
		String prop = getProperty(propertyName);
		if (prop == null)
		{
			if (getApplication().getSettings().isAutoCreatePropertiesActive())
			{
				if (getApplication().getSettings().isDebugActive()) {
					LOGGER.fine("Storing property: " + propertyName);
				}
				setBooleanProperty(propertyName, returnValueIfNull);
			}
			return returnValueIfNull;
		}
		else {
			return Boolean.valueOf(prop).booleanValue();
		}
	}
	public void setBooleanProperty(String propertyName, boolean setValue){
		setProperty(propertyName,Boolean.toString(setValue));
	}
	public void removeProperty(String propertyName)
	{
		this.propertyList.removeProperty(propertyName);
	}
	public void setProperty(String propertyName, String propertyValue)
	{
		this.propertyList.setProperty(propertyName, propertyValue);
	}
	public void setProperty(String propertyName, String[] propertyValues)
	{
		this.propertyList.setProperty(propertyName, propertyValues);
	}
	public void setArrayProperty(String propertyName, String propertyValue)
	{
		this.propertyList.setArrayProperty(propertyName, propertyValue);
	}
	public IWMainApplication getApplication()
	{
		return this.superApplication;
	}
	public void setProperty(String propertyName)
	{
		this.propertyList.removeProperty(propertyName);
	}
	private void setResourcesRealPath(String path)
	{
		this.resourcesRealPath = path;
	}
	private void setResourcesVirtualPath(String path)
	{
		this.resourcesVirtualPath = path;
	}
	private void setPropertiesRealPath(String path)
	{
		this.propertiesRealPath = path;
	}
	/**
	 * Sets the base path of this bundle.<br>
	 * e.g. /home/idegaweb/webapp/iw1/idegaweb/bundles/com.idega.core.bundle
	 * @param path
	 */
	protected void setBundleBaseRealPath(String path)
	{
		this.rootRealPath = path;
	}
	public void setRootVirtualPath(String path)
	{
		this.rootVirtualPath = path;
	}
	public Image getLocalizedImage(String name, Locale locale)
	{
		return getResourceBundle(locale).getImage(name);
	}
	/**
	 * Convenience method - Recommended to create a ResourceBundle (through getResourceBundle(locale)) to use instead more efficiently
	 */
	public String getLocalizedString(String name, Locale locale)
	{
		return getResourceBundle(locale).getString(name);
	}
	protected String getClassesRealPath()
	{
		return this.classesRealPath;
	}
	private void setClassesRealPath()
	{
		this.classesRealPath = this.getBundleBaseRealPath() + File.separator + "classes";
	}
	public String[] getAvailableProperties()
	{
		return ((String[]) this.propertyList.getKeys().toArray(new String[0]));
	}
	public String[] getLocalizableStrings()
	{
		//return (String[]) getLocalizableStringsMap().keySet().toArray(new String[0]);
		return getLocalizableStringsProperties().keySet().toArray(new String[0]);
	}
	public boolean removeLocalizableString(String key)
	{
		Iterator iter = getResourceBundles().values().iterator();
		while (iter.hasNext())
		{
			IWResourceBundle item = (IWResourceBundle) iter.next();
			item.removeString(key);
		}
		boolean success = getLocalizableStringsProperties().remove(key) != null ? true : false;
		storeLocalizableStrings();
		storeResourceBundles();
		return success;
	}
	protected Properties getLocalizableStringsProperties()
	{
		if (this.localizableStringsProperties == null){
			this.localizableStringsProperties=initializeLocalizableStrings();
		}
		return this.localizableStringsProperties;
	}
	public String getLocalizableStringDefaultValue(String key)
	{
		return getLocalizableStringsProperties().getProperty(key);
	}
	/*protected Map getLocalizableStringsMap()
	{
		initializePropertiesStrings();
		return localizableStringsMap;
	}*/
	protected Properties initializeLocalizableStrings()
	{
		//if (this.localizableStringsProperties == null)
		//{
			Properties locProps = new SortedProperties();
			try
			{
				locProps.load(new FileInputStream(getLocalizableStringsFile()));
				//localizableStringsMap = new TreeMap(localizableStringsProperties);
			}
			catch (IOException ex)
			{
				LOGGER.log(Level.WARNING, null, ex);
			}
			return locProps;
		//}
	}
	private File getLocalizableStringsFile()
	{
		if (this.localizableStringsFile == null)
		{
			try
			{
				// TODO: save to workspace if the property is set
				this.localizableStringsFile = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(), getLocalizableStringsFileName() );
			}
			catch (IOException ex)
			{
				LOGGER.log(Level.WARNING, null, ex);
			}
		}
		return this.localizableStringsFile;
	}
	protected String getLocalizableStringsFileName(){
		return "Localizable.strings";
	}
	public IWPropertyList getUserProperties(IWUserContext iwuc)
	{
		UserProperties properties = (UserProperties) getUserProperties(iwuc);
		if (properties != null) {
			return properties.getProperties(this.getBundleName());
		}
		return null;
	}
	public IWResourceBundle getResourceBundle(IWContext iwc)
	{
		return getResourceBundle(iwc.getCurrentLocale());
	}
	public IWResourceBundle getResourceBundle(Locale locale)
	{
		IWResourceBundle theReturn = (IWResourceBundle) getResourceBundles().get(locale);
		
		try
		{
			if (theReturn == null)
			{
				theReturn = initializeResourceBundle(locale);
				getResourceBundles().put(locale, theReturn);
			}
		}
		catch (Exception ex)
		{
			LOGGER.log(Level.WARNING, null, ex);
		}
		return theReturn;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initializeResourceBundle
	 * </p>
	 * @param loca(IWResourceBundle)theReturneturn
	 * @throws IOException
	 */
	protected IWResourceBundle initializeResourceBundle(Locale locale) throws IOException {
		IWResourceBundle theReturn;
		File file;
		if (IWMainApplicationSettings.isAutoCreateStringsActive()) {
			file = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(locale), getLocalizedStringsFileName());
		}
		else {
			file = new File(getResourcesRealPath(locale), getLocalizedStringsFileName());
		}
		IWResourceBundle defaultLocalizedResourceBundle = new IWResourceBundle(this, file, locale);
		if (isUsingLocalVariants()) {
			File variantFile = new File(getResourcesRealPath(locale), getLocalizedStringsVariantFileName());
			try {
				theReturn = new IWResourceBundle(defaultLocalizedResourceBundle, variantFile, locale);
			} catch(FileNotFoundException e) {
				theReturn = defaultLocalizedResourceBundle;
			}
		}
		else {
			theReturn = defaultLocalizedResourceBundle;
		}
		return theReturn;
	}
	
	protected boolean isUsingLocalVariants(){
		String localeVariant = getApplication().getSettings().getProperty("com.idega.core.localevariant");
		if(localeVariant!=null){
			return true;
		}
		return false;
	}
	
	protected String getLocalizedStringsFileName(){
		return "Localized.strings";
	}
	
	protected String getLocalizedStringsVariantFileName(){

		String localeVariant = getApplication().getSettings().getProperty("com.idega.core.localevariant");
		String variantfileName = "Localized_"+localeVariant+".strings";
		return variantfileName;
	}
	
	/**
	 * Returns a Map of all loaded resourcebundles
	 * @return
	 */
	public Map getResourceBundles(){
		if(this.resourceBundlesLookup==null){
			this.resourceBundlesLookup=new HashMap();
		}
		return this.resourceBundlesLookup;
	}
	
	public String getVersion()
	{
		String theReturn = getProperty("version");
		if (theReturn == null)
		{
			theReturn = "1";
		}
		return theReturn;
	}
	public String getBundleType()
	{
		String theReturn = getProperty("bundletype");
		if (theReturn == null)
		{
			theReturn = "bundle";
		}
		return theReturn;
	}
	public synchronized void storeState(boolean storeAllComponents)
	{
		//This method is not called on shutdown if getApplication().getSettings().getWriteBundleFilesOnShutdown() is false
		LOGGER.fine("Storing state of bundle " + getBundleIdentifier());		
		this.propertyList.store();
		boolean storeResourcesOnStore=getIfStoreResourcesOnStore();
		if(storeResourcesOnStore){
			this.storeLocalizableStrings();
			this.storeResourceBundles();
		}

		if (storeAllComponents) {
			Iterator valueIter = getComponentPropertiesListMap().values().iterator();
			while (valueIter.hasNext())
			{
				IWPropertyList element = (IWPropertyList) valueIter.next();
				element.store();
			}
		}
	}
	public synchronized void storeState()
	{
		storeState(true);
	}
	/**
	 * Gets if to store the resoures in the storeState() method
	 * @return
	 */
	protected boolean getIfStoreResourcesOnStore()
	{
		// TODO Auto-generated method stub
		return false;
	}
	synchronized boolean storeLocalizableStrings(){
		try
		{
			/*getLocalizableStringsProperties().clear();
			Iterator keyIter = getLocalizableStringsMap().keySet().iterator();
			while (keyIter.hasNext())
			{
				Object key = keyIter.next();
				if (key != null)
				{
					Object value = getLocalizableStringsMap().get(key);
					if (value != null)
					{
						getLocalizableStringsProperties().put(key, value);
					}
				}
			}*/
			FileOutputStream fos = new FileOutputStream(getLocalizableStringsFile());
			getLocalizableStringsProperties().store(fos, null);
			fos.close();
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.WARNING, null, ex);
			return false;
		}
		return true;
	}
	
	synchronized void storeResourceBundles(){
		Iterator iter = getResourceBundles().values().iterator();
		while (iter.hasNext())
		{
			IWResourceBundle item = (IWResourceBundle) iter.next();
			item.storeState();
		}
	}
	
	public String getResourcesRealPath()
	{
		return this.resourcesRealPath;
	}
	public String getResourcesURL(Locale locale)
	{
		return getResourcesVirtualPath(locale);
	}
	public String getResourcesURL()
	{
		return getResourcesVirtualPath();
	}
	public String getResourcesVirtualPath(Locale locale)
	{
		return this.getResourceBundle(locale).getResourcesURL();
	}
	public String getResourcesVirtualPath()
	{
		return getApplication().getTranslatedURIWithContext(this.resourcesVirtualPath);
	}
	
	/**
	* @returns Returns the virtual path to the resources folder in the bundle, without the context.	
	**/
	public String getResourcesPath()
	{
		return this.resourcesVirtualPath;
	}
	/**
	 * Current locale for the user comes from IWContext.
	 * @return returns vitual path to the current locale resource folder, without the context.
	 */
	public String getResourcesPathForCurrentLocale()
	{
		IWContext iwc = IWContext.getInstance();
		return getResourcesPath(iwc.getCurrentLocale());
	}
	
	public String getResourcesRealPath(Locale locale)
	{
		String path = (String) getLocaleRealPaths().get(locale);
		if (path == null)
		{
			path = getResourcesRealPath() + File.separator + locale.toString() + ".locale";
			getLocaleRealPaths().put(locale, path);
		}
		return path;
	}
	
	/**
	 * 
	 * @param locale
	 * @return returns the veirtual path to the locale resource folder within the webapplication, without the context.
	 */
	public String getResourcesPath(Locale locale)
	{
		String path = (String) getLocalePaths().get(locale);
		if (path == null)
		{
			path = getResourcesPath() + "/" + locale.toString() + ".locale";
			getLocalePaths().put(locale, path);
		}
		return path;
	}
	
	protected Map getLocaleRealPaths(){
		if(this.localeRealPathsLookup==null){
			this.localeRealPathsLookup=new HashMap();
		}
		return this.localeRealPathsLookup;
	}
	protected Map getLocalePaths(){
		if(this.localePathsLookup==null){
			this.localePathsLookup=new HashMap();
		}
		return this.localePathsLookup;
	}
	public String getPropertiesRealPath()
	{
		return this.propertiesRealPath;
	}
	public void addLocale(Locale locale)
	{
		String LocalePath = getResourcesRealPath(locale);
		File file = new File(LocalePath);
		file.mkdirs();
	}
	protected void initializeStructure()
	{
		String[] dirs = new String[5];
		String resourcesDirectory = this.getResourcesRealPath();
		dirs[0] = resourcesDirectory;
		String propertiesDirectory = this.getPropertiesRealPath();
		dirs[1] = propertiesDirectory;
		String classesDirectory = this.getClassesRealPath();
		dirs[2] = classesDirectory;
		Locale english = Locale.ENGLISH;
		Locale icelandic = LocaleUtil.getIcelandicLocale();
		String enLocalePath = getResourcesRealPath(english);
		dirs[3] = enLocalePath;
		String isLocalePath = getResourcesRealPath(icelandic);
		dirs[4] = isLocalePath;
		for (int i = 0; i < dirs.length; i++)
		{
			File file = new File(dirs[i]);
			file.mkdirs();
		}
	}
	public String getBundleIdentifier()
	{
		return this.identifier;
	}
	/**
	 * temp implementation
	 */
	public String getBundleName()
	{
		String theReturn = getProperty("name");
		if (theReturn == null)
		{
			theReturn = this.getBundleIdentifier();
		}
		return theReturn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundle#getImageURI(java.lang.String)
	 */
	public String getImageURI(String urlInBundle){
		StringBuffer buf = new StringBuffer(getResourcesURL());
		if(!urlInBundle.startsWith(CoreConstants.SLASH)){
			buf.append(CoreConstants.SLASH);
		}
		buf.append(urlInBundle);
	    return buf.toString();
	}
	
	public Image getImage(String urlInBundle)
	{
		return new Image(getImageURI(urlInBundle));
	}
	public String getVirtualPathWithFileNameString(String filename)
	{
		return getResourcesURL() + slash + filename;
	}
	public String getVirtualPath()
	{
		return getResourcesURL();
	}
	public String getRealPathWithFileNameString(String filename)
	{
		return getResourcesRealPath() + File.separator + filename;
	}
	public String getRealPath()
	{
		return getResourcesRealPath();
	}
	public Image getImage(String urlInBundle, int width, int height)
	{
		return getImage(urlInBundle, "", width, height);
	}
	public Image getImageButton(String text)
	{
		return this.getApplication().getImageFactory().createButton(text, this);
	}
	public Image getImageTab(String text, boolean flip)
	{
		return this.getApplication().getImageFactory().createTab(text, this, flip);
	}
	public Image getImage(String urlInBundle, String name, int width, int height)
	{
		return new Image(getResourcesURL() + slash + urlInBundle, name, width, height);
	}
	public Image getSharedImage(String urlInBundle, String name)
	{
		return new Image(getResourcesURL() + slash + shared + slash + urlInBundle, name);
	}
	public Image getImage(String urlInBundle, String overUrlInBundle, String name, int width, int height)
	{
		Image returnImage = new Image(name, getResourcesURL() + slash + urlInBundle, getResourcesURL() + slash + overUrlInBundle);
		returnImage.setWidth(width);
		returnImage.setHeight(height);
		return returnImage;
	}
	public Image getImage(String urlInBundle, String overUrlInBundle, String name)
	{
		Image returnImage = new Image(name, getResourcesURL() + slash + urlInBundle, getResourcesURL() + slash + overUrlInBundle);
		return returnImage;
	}
	public Image getImage(String urlInBundle, String name)
	{
		return new Image(getResourcesURL() + slash + urlInBundle, name);
	}
	/**
	 * Returns the ICObjects associated with this bundle
	 * Returns an empty list if nothing found
	 */
	public Collection getICObjectsList() throws FinderException, IDOLookupException
	{
		return getICObjectHome().findAllByBundle(this.getBundleIdentifier());
	}
	/**
	 * Returns the ICObjects associated with this bundle
	 * Returns null if there is an exception
	 * @deprecated Replaced with getICObjectsList()
	 */
	@Deprecated
	public ICObject[] getICObjects()
	{
		try
		{
			Collection l = getICObjectsList();
			return (ICObject[]) l.toArray(new ICObject[0]);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 */
	public Collection getICObjectsList(String componentType) throws FinderException, IDOLookupException
	{
		return getICObjectHome().findAllByObjectTypeAndBundle(componentType, this.getBundleIdentifier());
	}
	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 * @deprecated replaced with getICObjectsList(componentType);
	 */
	@Deprecated
	public ICObject[] getICObjects(String componentType)
	{
		try
		{
			//return (ICObject[])(((com.idega.core.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy()).findAllByColumn(com.idega.core.data.ICObjectBMPBean.getBundleColumnName(),this.getBundleIdentifier(),com.idega.core.data.ICObjectBMPBean.getObjectTypeColumnName(),componentType);
			Collection l = getICObjectsList(componentType);
			return (ICObject[]) l.toArray(new ICObject[0]);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	private IWPropertyList getPropertyList()
	{
		return this.propertyList;
	}
	private IWPropertyList getComponentList()
	{
		IWPropertyList list = getPropertyList().getPropertyList(COMPONENTLIST_KEY);
		if (list == null)
		{
			list = getPropertyList().getNewPropertyList(COMPONENTLIST_KEY);
		}
		return list;
	}
	public void addComponent(String className, String componentType, boolean block, boolean widget, String description, String iconURI)
	{
		addComponent(className, componentType, className.substring(className.lastIndexOf(".") + 1), block, widget, description, iconURI);
	}
	public void addComponent(String className, String componentType, String componentName, boolean block, boolean widget, String description, String iconURI)
	{
		IWProperty prop = getComponentList().getNewProperty();
		prop.setName(className);
		String componentPropertyFileName = getDefaultComponentPropertyFileName(className);
		prop.getNewPropertyList().setProperty(COMPONENT_PROPERTY_FILE, componentPropertyFileName);
		IWPropertyList pl = initializeComponentPropertyList(className, componentPropertyFileName);
		pl.setProperty(COMPONENT_NAME_PROPERTY, componentName);
		pl.setProperty(COMPONENT_TYPE_PROPERTY, componentType);
		pl.setProperty(COMPONENT_BLOCK_PROPERTY, Boolean.toString(block));
		pl.setProperty(COMPONENT_WIDGET_PROPERTY, Boolean.toString(widget));
		if (iconURI != null) {
			pl.setProperty(COMPONENT_ICON_URI_PROPERTY, iconURI);
		}
		if (description != null) {
			pl.setProperty(COMPONENT_DESCRIPTION_PROPERTY, description);
		}
		//setComponentProperty(prop, COMPONENT_NAME_PROPERTY, componentName);
		//setComponentProperty(prop, COMPONENT_TYPE_PROPERTY, componentType);
		addComponentToDatabase(className, componentType, componentName, block, widget, description, iconURI);
		this.propertyList.store();

	}
	/**
	 * @param className
	 * @param componentPropertyFileName
	 * @return
	 */
	protected IWPropertyList initializeComponentPropertyList(String className, String componentPropertyFileName)
	{
		IWPropertyList pl = initializePropertyList(componentPropertyFileName);
		getComponentPropertiesListMap().put(className, pl);
		return pl;
	}
	
	/**
	 * <p>
	 * Initializes a IWPropertyList relative to the 'properties' folder within the bundle
	 * </p>
	 * @param pathWitinPropertiesFolder
	 * @return
	 */
	protected IWPropertyList initializePropertyList(String pathWitinPropertiesFolder) {
		return initializePropertyList(pathWitinPropertiesFolder, true);
	}
	
	/**
	 * <p>
	 * Initializes a IWPropertyList relative to the 'properties' folder within the bundle
	 * </p>
	 * @param pathWitinPropertiesFolder
	 * @return
	 */
	protected IWPropertyList initializePropertyList(String pathWithinPropertiesFolder, boolean autocreate) {
		return new IWPropertyList(getPropertiesRealPath(), pathWithinPropertiesFolder, autocreate);
	}
	/**
	 * @param className
	 * @return
	 */
	protected String getDefaultComponentPropertyFileName(String className)
	{
		int length = className.length();
		if (length > 250)
		{
			return className.substring(length - 250) + IWPropertyList.DEFAULT_FILE_ENDING;
		}
		else {
			return className + IWPropertyList.DEFAULT_FILE_ENDING;
		}
	}
	private void addComponentToDatabase(String className, String componentType, String componentName, boolean block, boolean widget, String description, String iconURI)
	{
		RefactorClassRegistry rfregistry = RefactorClassRegistry.getInstance();
		boolean classIsRefactored = rfregistry.isClassRefactored(className);
		String newRefactoredClassName = rfregistry.getRefactoredClassName(className);
		ICObjectHome icoHome;
		try
		{
			icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
			try
			{
				ICObject ico = icoHome.findByClassName(className);
				ComponentRegistry registry = ComponentRegistry.getInstance(this.getApplication());
				if (classIsRefactored)
				{
					if(registry.getComponentByClassName(className)==null){
					    //ICObject ico = icoHome.findByClassName(className);
						try
						{
							ico.setObjectClass(Class.forName(newRefactoredClassName));
							ico.store();
						}
						catch (Exception e)
						{
							LOGGER.log(Level.WARNING, null, e);
						}
						changeComponentInBundleRegistry(className, newRefactoredClassName);
						if (!ico.getBundleIdentifier().equals(this.getBundleIdentifier()))
						{
							LOGGER.info("Updating bundle registry for component: "
							+ ico.getClassName()
							+ " from "
							+ ico.getBundleIdentifier()
							+ " to "
							+ this.getBundleIdentifier());
							ico.setBundleIdentifier(getBundleIdentifier());
							ico.store();
						}
					}
				}
				ico.setIsBlock(new Boolean(block));
				ico.setIsWidget(new Boolean(widget));
				ico.setDescripton(description);
				ico.setIconURI(iconURI);
				ico.store();
			}
			//The object is not found by its class name in the database
			catch (FinderException fe)
			{
				if (classIsRefactored)
				{
					changeComponentInBundleRegistry(className, newRefactoredClassName);
					getComponentPropertyList(newRefactoredClassName);
				}
				else
				{
					try
					{
						ICObject ico;
						ico = icoHome.create();
						Class c = RefactorClassRegistry.forName(className);
						ico.setObjectClass(c);
						ico.setName(componentName);
						ico.setObjectType(componentType);
						ico.setBundle(this);
						ico.setIsBlock(new Boolean(block));
						ico.setIsWidget(new Boolean(widget));
						ico.setDescripton(description);
						ico.setIconURI(iconURI);
						ico.store();
						//Update the ComponentRegistry with the new component
						ComponentRegistry registry = ComponentRegistry.getInstance(this.getApplication());
						registry.registerComponent(new ICObjectComponentInfo(ico));
						
						if (componentType.equals(ICObjectBMPBean.COMPONENT_TYPE_ELEMENT)
							|| componentType.equals(ICObjectBMPBean.COMPONENT_TYPE_BLOCK))
						{
							com.idega.core.accesscontrol.business.AccessControl.initICObjectPermissions(ico);
							if (componentType.equals(ICObjectBMPBean.COMPONENT_TYPE_BLOCK))
							{
								registerBlockPermissionKeys(c);
							}
						}
						// new register part
						Class[] implementedInterfaces = c.getInterfaces();
						boolean isRegisterable = false;
						for (int j = 0; j < implementedInterfaces.length; j++) {
							if (BundleRegistrationListener.class.getName().equals(implementedInterfaces[j].getName())){
								isRegisterable = true;
							}
						}
						if(isRegisterable){
							BundleRegistrationListener regObj =(BundleRegistrationListener)c.newInstance();
							regObj.registerInBundle(this, ico);
						}
					}
					catch (ClassNotFoundException e)
					{
						LOGGER.warning("Loading bundle: "
														+ this.getBundleIdentifier()
														+ " : Class "
														+ e.getMessage()
														+ " not found. Could be deprecated");
					}
					catch (InstantiationException e)
					{
						LOGGER.log(Level.WARNING, null, e);
					}
					catch (IllegalAccessException e)
					{
						LOGGER.log(Level.WARNING, null, e);
					}
					catch (RegisterException e)
					{
						LOGGER.log(Level.WARNING, null, e);
					}
					catch (Exception e)
					{
						LOGGER.log(Level.WARNING, null, e);
					}
				}
			}
		}
		catch (IDOLookupException e1)
		{
			LOGGER.log(Level.WARNING, null, e1);
		}
	}
	/**
	 * @param className
	 * @param newClassName
	 */
	private void changeComponentInBundleRegistry(String className, String newClassName)
	{
		IWProperty propOld = getComponentList().getIWProperty(className);
		IWPropertyList pl = propOld.getPropertyList();
		IWProperty propNew = getComponentList().getNewProperty();
		propNew.setName(newClassName);
		propNew.setPropertyList(pl);
		getComponentList().removeProperty(className);
	}

	public void setComponentProperty(String className, String propertyName, String propertyValue)
	{
		if (propertyName.equals(COMPONENT_PROPERTY_FILE))
		{
			IWProperty prop = getComponentList().getIWProperty(className);
			if (prop != null)
			{
				setComponentProperty(prop, propertyName, propertyValue);
			}
		}
		else
		{
			IWPropertyList propl = getComponentPropertyList(className);
			propl.setProperty(propertyName, propertyValue);
		}
	}
	public IWPropertyList getComponentPropertyList(String className)
	{
		boolean fetchFromBundlePropertyFile = getIfFetchComponentPropertyFromBundlePropertiesFile(className);
		if (fetchFromBundlePropertyFile)
		{
			IWProperty prop = getComponentList().getIWProperty(className);
			if(prop!=null){
				return prop.getPropertyList();
			}
		}
		else
		{
			IWProperty prop = getComponentList().getIWProperty(className);
			IWPropertyList propertyList = (IWPropertyList) getComponentPropertiesListMap().get(className);
			if (propertyList == null)
			{
				if(prop!=null){
					String fileName = prop.getPropertyList().getProperty(COMPONENT_PROPERTY_FILE);
					propertyList = initializeComponentPropertyList(className, fileName);
				}
			}
			return propertyList;
		}
		return null;
	}
	/**
	 * @param className
	 * @return
	 */
	private boolean getIfFetchComponentPropertyFromBundlePropertiesFile(String className)
	{
		IWPropertyList cl = getComponentList();
		IWProperty prop = cl.getIWProperty(className);
		if (prop != null) {
			IWPropertyList pl = prop.getPropertyList();
			if (pl.getProperty(COMPONENT_PROPERTY_FILE) == null)
			{
				if (this.autoMoveComponentPropertiesToFile)
				{
					try
					{
						moveComponentPropertyFromBundleToFile(className, pl);
					}
					catch (Exception e)
					{
						return true;
					}
					return false;
				}
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * @param autoMoveComponentPropertiesToFile
	 * @param pl
	 */
	private void moveComponentPropertyFromBundleToFile(String className, IWPropertyList oldComponentPL) throws IOException
	{
		String fileName = this.getDefaultComponentPropertyFileName(className);
		IWPropertyList newPL = this.initializeComponentPropertyList(className, fileName);
		try
		{
			IWPropertyList cl = getComponentList();
			cl.removeProperty(className);
			XMLElement mapElement = oldComponentPL.getMapElement();
			mapElement.removeParent();
			newPL.setMapElement(oldComponentPL.getMapElement());
			newPL.store();
			IWProperty prop = cl.getNewProperty();
			prop.setName(className);
			prop.getNewPropertyList().setProperty(COMPONENT_PROPERTY_FILE, fileName);
			this.propertyList.store();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, null, e);
		}
	}
	private Map getComponentPropertiesListMap()
	{
		if (this.componentPropertyListMap == null)
		{
			this.componentPropertyListMap = new HashMap();
		}
		return this.componentPropertyListMap;
	}
	/**
	 * @deprecated This method is obsolete
	 * @param component
	 * @param propertyName
	 * @param propertyValue
	 */
	@Deprecated
	private void setComponentProperty(IWProperty component, String propertyName, String propertyValue)
	{
		IWPropertyList list = component.getPropertyList();
		if (list == null)
		{
			list = component.getNewPropertyList();
		}
		list.setProperty(propertyName, propertyValue);
	}
	public String getComponentProperty(String className, String propertyName)
	{
		if (propertyName.equals(COMPONENT_PROPERTY_FILE))
		{
			IWProperty prop = getComponentList().getIWProperty(className);
			if (prop != null)
			{
				return prop.getPropertyList().getProperty(COMPONENT_PROPERTY_FILE);
			}
		}
		else
		{
			IWPropertyList propl = getComponentPropertyList(className);
			if(propl!=null){
				String value = propl.getProperty(propertyName);
				return value;
			}
		}
		return null;
	}
	public String getComponentName(Class componentClass)
	{
		return getComponentName(componentClass.getName());
	}
	public String getComponentName(String className)
	{
		return getComponentProperty(className, COMPONENT_NAME_PROPERTY);
	}
	public String getComponentType(Class componentClass)
	{
		return getComponentType(componentClass.getName());
	}
	public String getComponentType(String className)
	{
		return getComponentProperty(className, COMPONENT_TYPE_PROPERTY);
	}
	/**
	 * Returns getComponentName(componentClass) if localized name not found
	 */
	public String getComponentName(Class componentClass, Locale locale)
	{
		String name = getComponentName(componentClass.getName(), locale);
		if(name!=null){
			return name;
		}
		else{
			return componentClass.getName();
		}
	}
	/**
	 * Returns getComponentName(className) if localized name not found
	 */
	public String getComponentName(String className, Locale locale)
	{
		String name = getComponentName(className, locale, getComponentName(className));
		if(name!=null){
			return name;
		}
		else{
			return className;
		}
	}
	public void setComponentName(Class componentClass, Locale locale, String sName)
	{
		setComponentName(componentClass.getName(), locale, sName);
	}
	public String getComponentName(Class componentClass, Locale locale, String returnIfNameNotLocalized)
	{
		return getComponentName(componentClass.getName(), locale, returnIfNameNotLocalized);
	}
	public String getComponentName(String className, Locale locale, String returnIfNameNotLocalized)
	{
		return this.getResourceBundle(locale).getLocalizedString("iw.component." + className + ".name", returnIfNameNotLocalized);
	}
	public void setComponentName(String className, Locale locale, String sName)
	{
		this.getResourceBundle(locale).setString("iw.component." + className + ".name", sName);
	}
	public void removeComponent(String className)
	{
		IWPropertyList pl = this.getComponentPropertyList(className);
		pl.delete();
		getComponentPropertiesListMap().remove(className);
		getComponentList().removeProperty(className);
		//com.idega.core.component.data.ICObjectBMPBean.removeICObject(className);
		
		ICObjectHome home;
		try {
			home = (ICObjectHome)IDOLookup.getHome(ICObject.class);
			ICObject ico = home.findByClassName(className);
			ico.remove();
		}
		catch (Exception e) {
			LOGGER.log(Level.WARNING, null, e);
		}
		
		this.propertyList.store();
	}
	public List getComponentKeys()
	{
		return getComponentList().getKeys();
	}
	public int compareTo(Object o)
	{
		IWBundle bundle = (IWBundle) o;
		return this.getBundleIdentifier().compareTo(bundle.getBundleIdentifier());
	}
	public void addLocalizableString(String key, String value)
	{
		getLocalizableStringsProperties().put(key, value);
		storeLocalizableStrings();
	}
	public boolean containsLocalizedString(String key)
	{
		return (getLocalizableStringsProperties().containsKey(key));
	}
	private ICObjectHome getICObjectHome() throws IDOLookupException
	{
		return (ICObjectHome) IDOLookup.getHome(ICObject.class);
	}
	@Override
	public String toString(){
		return this.getBundleIdentifier();
	}
	
	/**
	 * Returns the path to the jsp inside the bundle structure.<br/>
	 * The default path is under 'jsp/' relative to the bundle folder.<br/>
	 * This method does not include a potential webapplication context path.
	 */
	public String getJSPURI(String jspInBundle) {
		String jspPath = "/jsp/"+jspInBundle;
		return this.rootVirtualPath+jspPath;
	}
	
	/**
	 * Returns the path to the facelet inside the bundle structure.<br/>
	 * The default path is under 'facelets/' relative to the bundle folder.<br/>
	 * This method does not include a potential web application context path.
	 */
	public String getFaceletURI(String faceletInBundle) {
		return rootVirtualPath+"/facelets/"+faceletInBundle;
	}

	/**
	 * <p>
	 * Returns the URI to a resource inside the '/resources/' folder inside this bundle.<br/>
	 * This method does not include a potential webapplication context path.
	 * </p>
	 * @param pathInResourceFolder path relative to this bundles resource virtual path
	 * @return Something like '/idegaweb/bundles/com.idega.core.bundle/resources/style/style.css'
	 */
	public String getResourceURIWithoutContextPath(String pathInResourceFolder) {
		return this.resourcesVirtualPath+pathInResourceFolder;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundle#getLocalizedText(java.lang.String)
	 */
	public HtmlOutputText getLocalizedText(String localizationKey) {
		HtmlOutputText t = new HtmlOutputText();
		t = (HtmlOutputText) getLocalizedUIComponent(localizationKey, t);
		return t;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundle#getValueBinding(java.lang.String)
	 */
	public ValueBinding getValueBinding(String localizationKey) {
		String valueBinding = getLocalizedStringExpr(localizationKey);
		ValueBinding vb = getApplication().createValueBinding(valueBinding);
		return vb;
	}
	
	public ValueBinding getValueBinding(String localizationKey, String defaultValue) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return getValueBinding(ctx,localizationKey,defaultValue);
	}
	
	public ValueBinding getValueBinding(FacesContext ctx, String localizationKey, String defaultValue) {
		String valueBinding = getLocalizedStringExpr(localizationKey);
		ValueBinding vb = getApplication().createValueBinding(valueBinding);
		Object obj = vb.getValue(ctx);
		if(obj==null){
			//TODO store to localization files
			vb.setValue(ctx,((defaultValue==null)?"":defaultValue));
		}
		return vb;
	}

	/**
	 * Creates value binding expression for given key
	 * @param localizationKey
	 * @return a String #{localizedStrings['bundle']['key']}
	 */
	public String getLocalizedStringExpr(String localizationKey) {
		return "#{localizedStrings['"+getBundleIdentifier()+"']['"+localizationKey+"']}";
	}
	
	public String getLocalizedString(String localizationKey) {
		return getLocalizedString(localizationKey,localizationKey);
	}

	
	public String getLocalizedString(String localizationKey, String defaultValue) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = getValueBinding(ctx,localizationKey,defaultValue);
		return (String)vb.getValue(ctx);
	}
	
	public UIComponent getLocalizedUIComponent(String localizationKey, UIComponent component) {
		return getLocalizedUIComponent(localizationKey,component,localizationKey);
	}
	
	public UIComponent getLocalizedUIComponent(String localizationKey, UIComponent component, String defaultValue) {
//		String valueBinding = "#{bundles['"+getBundleIdentifier()+"']['"+localizationKey+"']}";
		FacesContext ctx = FacesContext.getCurrentInstance();
		component.setValueBinding("value",getValueBinding(ctx,localizationKey,defaultValue));
		return component;
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundle#getLocalizedImage(java.lang.String)
	 */
	public HtmlGraphicImage getLocalizedImage(String pathAndName) {
		return getLocalizedImage(pathAndName, IWContext.getInstance());
	}	
	public HtmlGraphicImage getLocalizedImage(String pathAndName, IWContext context) {
		HtmlGraphicImage t = new HtmlGraphicImage();
		Locale locale = context.getCurrentLocale();
		// Removing the context // copied from WebDAVListManagedBean (create by Eiki)
		t.setUrl(context.getIWMainApplication().getURIFromURL(getResourcesVirtualPath(locale)+pathAndName));
		return t;
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#canLoadLazily()
	 */
	public boolean canLoadLazily() {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleIdentifier()
	 */
	public String getModuleIdentifier() {
		return getBundleIdentifier();
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleName()
	 */
	public String getModuleName() {
		return getBundleName();
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleVendor()
	 */
	public String getModuleVendor() {
		String theReturn = getProperty("vendor");
		if (theReturn == null)
		{
			theReturn = "idega Software";
		}
		return theReturn;
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleVersion()
	 */
	public String getModuleVersion() {
		return getVersion();
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#load()
	 */
	public void load() {
		this.loadBundle();
	}
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#reload()
	 */
	public void reload() {
		this.reloadBundle();
	}
	
	/**
	 * Returns input stream for a file inside the bundle real path.
	 * @param pathWithinBundle
	 * @return FileInputStream
	 */
	public InputStream getResourceInputStream(String pathWithinBundle) throws IOException {
		
		String workspaceDir = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);
		String bundleInWorkspace;
		
		if(workspaceDir != null) {
			
			bundleInWorkspace = new StringBuilder(workspaceDir).append(CoreConstants.SLASH).append(getBundleIdentifier()).append(CoreConstants.SLASH).toString();
		} else
			bundleInWorkspace = getBundleBaseRealPath();
		
		
		File file = new File(bundleInWorkspace, pathWithinBundle);
		
		if (!file.exists())
			throw new FileNotFoundException("File not found within bundle " + bundleInWorkspace + ": " + pathWithinBundle);

		return new FileInputStream(file);
	}

	/**
	 * Returns last modified time for a file inside the bundle real path.
	 * @param pathWithinBundle
	 * @return miliseconds since Epoch, or 0 if not found
	 */
	public long getResourceTime(String pathWithinBundle) {
		File file = new File(getBundleBaseRealPath(), pathWithinBundle);
		return file.lastModified();
	}
	
	public static boolean isProductionEnvironment() {
		String directory = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);
		return (directory == null) ? true : false;
	}

}