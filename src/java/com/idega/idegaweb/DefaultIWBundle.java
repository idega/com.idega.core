/*
 * $Id: DefaultIWBundle.java,v 1.1 2004/07/28 23:32:16 tryggvil Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
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

import com.idega.core.component.business.BundleRegistrationListener;
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
import com.idega.util.FileUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.SortedProperties;
import com.idega.util.logging.LoggingHelper;
import com.idega.xml.XMLElement;
/**
 * A class to serve as a wrapper for an idegaWeb Bundle.
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
	private static final String DOT = "."; 
	
	//TODO: Tryggvi: Change to true
	private boolean autoMoveComponentPropertiesToFile = true;
	
	private HashMap componentPropertyListMap;
	private static final String slash = "/";
	private static final String shared = "shared";
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
	private boolean autoCreateLocalizedResources = true;
	private boolean autoCreate = false;
	//private Map handlers;
	private Map localeRealPathsLookup;
	//private SortedMap localizableStringsMap;
	private Properties localizableStringsProperties;
	private File localizableStringsFile;
	private IWPropertyList propertyList;
	static final String propertyFileName = "bundle" + IWPropertyList.DEFAULT_FILE_ENDING;
	final static String BUNDLE_IDENTIFIER_PROPERTY_KEY = "iw_bundle_identifier";
	final static String COMPONENTLIST_KEY = "iw_components";
	private final static String COMPONENT_NAME_PROPERTY = "component_name";
	private final static String COMPONENT_TYPE_PROPERTY = "component_type";
	private final static String COMPONENT_ICON_PROPERTY = "component_icon";
	private final static String COMPONENT_CLASS_PROPERTY = "component_class";
	private final static String COMPONENT_PROPERTY_FILE = "component_property_file";
	private final static String BUNDLE_STARTER_CLASS = "iw_bundle_starter_class";
	private IWBundleStartable starter;
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
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
		this.setProperty(BUNDLE_IDENTIFIER_PROPERTY_KEY, bundleIdentifier);
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
		setResourcesRealPath(getBundleBaseRealPath() + FileUtil.getFileSeparator() + "resources");
		setPropertiesRealPath(getBundleBaseRealPath() + FileUtil.getFileSeparator() + "properties");
		setClassesRealPath();
		if (autoCreate)
		{
			this.initializeStructure();
			propertyList = new IWPropertyList(getPropertiesRealPath(), propertyFileName, true);
		}
		else
		{
			propertyList = new IWPropertyList(getPropertiesRealPath(), propertyFileName, false);
		}
		StringBuffer SystemClassPath = new StringBuffer(System.getProperty("java.class.path"));
		SystemClassPath.append(File.pathSeparator);
		SystemClassPath.append(getClassesRealPath());
		System.setProperty("java.class.path", SystemClassPath.toString());
		installComponents();
		try
		{
			createDataRecords();
			registerBlockPermisionKeys();
		}
		catch (IDOLookupException e)
		{
			e.printStackTrace();
		}
		catch (FinderException e)
		{
			debug("No bundle components found for " + getBundleIdentifier());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		runStartClass();
	}
	private void createDataRecords() throws Exception
	{
		ICObjectHome icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		Collection entities = icoHome.findAllByObjectTypeAndBundle(ICObjectBMPBean.COMPONENT_TYPE_DATA, identifier);
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
					logError("Loading bundle: " + this.getBundleIdentifier() + " : Class " + e.getMessage() + " not found");
				}
			}
		}
	}
	private void registerBlockPermissionKeys(Class blockClass) throws InstantiationException, IllegalAccessException
	{
		Object o = blockClass.newInstance();
		if (o instanceof Block)
			 ((Block) o).registerPermissionKeys();
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
					debug("Class not found for Block: " + ico.getName());
					//e.printStackTrace();
				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	private void runStartClass()
	{
		// starting starter defined in bundle property
		String starterClassName = this.getProperty(BUNDLE_STARTER_CLASS);
		if (starterClassName != null)
		{
			try
			{
				starter = (IWBundleStartable) Class.forName(starterClassName).newInstance();
				starter.start(this);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// starting of default bundle starter
		startBundleStarter();
	}
	
	private void startBundleStarter() {
		IWBundleStartable bundleStarter = getBundleStarter();
		if (bundleStarter != null) {
			bundleStarter.start(this);
		}
	}
	
	private void stopBundleStarter() {
		IWBundleStartable bundleStarter = getBundleStarter();
		if (bundleStarter != null) {
			bundleStarter.stop(this);
		}
	}
	
	private IWBundleStartable getBundleStarter() {
		StringBuffer buffer = new StringBuffer(getBundleName());
		buffer.append(DOT);
		buffer.append(IWBundleStartable.DEFAULT_STARTER_CLASS);
	  	String className = buffer.toString();
	  	try {
	  		Class starterClass = Class.forName(className);
	  		return (IWBundleStartable) starterClass.newInstance();
	  	}
	  	catch (ClassNotFoundException ex) {
	  		// nothing to worry about, some bundles don't have a starter class
	  	}
	  	catch (InstantiationException ex) {
	  		logError("[IWBundle] Instantiation of bundle starter class failed: "+ className);
	  	}
	  	catch (IllegalAccessException ex) {
	  		logError("[IWBundle] Instantiation of bundle starter class failed, access problem: "+ className);
	  	}
	  	return null;
	  }
	
	/**
	 *Stores this bundle and unloads all resources;
	 */
	public void unload()
	{
		unload(true);
	}
	/**
	 *Unloads all resources for this bundle and stores the state of this bundle if storeState==true
	 *@param storeState to say if to store the state (call storeState)
	 */
	public void unload(boolean storeState){
		//resourceBundles.clear();
		resourceBundlesLookup=null;
		localizableStringsProperties = null;
		this.localePathsLookup=null;
		this.localeRealPathsLookup=null;
		if(storeState){
			storeState();
		}
		stopStartClass();
	}
	private void stopStartClass()
	{
		// stopping of starter defined in bundle property
		if (starter != null)
		{
			starter.stop(this);
		}
		// stopping of default bundle starter
		stopBundleStarter();
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
			if(className!=null && componentName != null && componentType!=null){
				addComponentToDatabase(className, componentType, componentName);
			}
			else{
				logError("Error registering component className="+className+",componentName="+componentName+",componentType="+componentType+" in bundle="+this.getBundleIdentifier());
			}
			
		}
	}
	/**
	 * gets the base path of this bundle.<br>
	 * e.g. /home/idegaweb/webapp/iw1/idegaweb/bundles/com.idega.core.bundle
	 */	
	public String getBundleBaseRealPath()
	{
		return rootRealPath;
	}
	protected String getRootVirtualPath()
	{
		return rootVirtualPath;
	}
	public Image getIconImage()
	{
		return new Image(getProperty("iconimage"));
	}
	public String getProperty(String propertyName)
	{
		return propertyList.getProperty(propertyName);
	}
	public String getProperty(String propertyName, String returnValueIfNull)
	{
		String prop = getProperty(propertyName);
		if (prop == null)
		{
			if (getApplication().getSettings().isAutoCreatePropertiesActive())
			{
				if (getApplication().getSettings().isDebugActive())
					System.out.println("Storing property: " + propertyName);
				setProperty(propertyName, returnValueIfNull);
			}
			return returnValueIfNull;
		}
		else
			return prop;
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
				if (getApplication().getSettings().isDebugActive())
					System.out.println("Storing property: " + propertyName);
				setBooleanProperty(propertyName, returnValueIfNull);
			}
			return returnValueIfNull;
		}
		else
			return Boolean.valueOf(prop).booleanValue();
	}
	public void setBooleanProperty(String propertyName, boolean setValue){
		setProperty(propertyName,Boolean.toString(setValue));
	}
	public void removeProperty(String propertyName)
	{
		propertyList.removeProperty(propertyName);
	}
	public void setProperty(String propertyName, String propertyValue)
	{
		propertyList.setProperty(propertyName, propertyValue);
	}
	public void setProperty(String propertyName, String[] propertyValues)
	{
		propertyList.setProperty(propertyName, propertyValues);
	}
	public void setArrayProperty(String propertyName, String propertyValue)
	{
		propertyList.setArrayProperty(propertyName, propertyValue);
	}
	public IWMainApplication getApplication()
	{
		return this.superApplication;
	}
	public void setProperty(String propertyName)
	{
		propertyList.removeProperty(propertyName);
	}
	private void setResourcesRealPath(String path)
	{
		resourcesRealPath = path;
	}
	private void setResourcesVirtualPath(String path)
	{
		resourcesVirtualPath = path;
	}
	private void setPropertiesRealPath(String path)
	{
		propertiesRealPath = path;
	}
	/**
	 * Sets the base path of this bundle.<br>
	 * e.g. /home/idegaweb/webapp/iw1/idegaweb/bundles/com.idega.core.bundle
	 * @param path
	 */
	protected void setBundleBaseRealPath(String path)
	{
		rootRealPath = path;
	}
	public void setRootVirtualPath(String path)
	{
		rootVirtualPath = path;
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
		return classesRealPath;
	}
	private void setClassesRealPath()
	{
		classesRealPath = this.getBundleBaseRealPath() + FileUtil.getFileSeparator() + "classes";
	}
	public String[] getAvailableProperties()
	{
		return ((String[]) ((List) propertyList.getKeys()).toArray(new String[0]));
	}
	public String[] getLocalizableStrings()
	{
		//return (String[]) getLocalizableStringsMap().keySet().toArray(new String[0]);
		return (String[]) getLocalizableStringsProperties().keySet().toArray(new String[0]);
	}
	public boolean removeLocalizableString(String key)
	{
		Iterator iter = getResourceBundles().values().iterator();
		while (iter.hasNext())
		{
			IWResourceBundle item = (IWResourceBundle) iter.next();
			item.removeString(key);
		}
		return getLocalizableStringsProperties().remove(key) != null ? true : false;
	}
	protected Properties getLocalizableStringsProperties()
	{
		initializePropertiesStrings();
		return localizableStringsProperties;
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
	private void initializePropertiesStrings()
	{
		if (localizableStringsProperties == null)
		{
			localizableStringsProperties = new SortedProperties();
			try
			{
				localizableStringsProperties.load(new FileInputStream(getLocalizableStringsFile()));
				//localizableStringsMap = new TreeMap(localizableStringsProperties);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	private File getLocalizableStringsFile()
	{
		if (localizableStringsFile == null)
		{
			try
			{
				localizableStringsFile = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(), "Localizable.strings");
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return localizableStringsFile;
	}
	public IWPropertyList getUserProperties(IWUserContext iwuc)
	{
		UserProperties properties = (UserProperties) getUserProperties(iwuc);
		if (properties != null)
			return properties.getProperties(this.getBundleName());
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
				File file;
				/**
				   * @todo: Look into this autoCreateLocalizedResources is always set true
				   */
				if (autoCreateLocalizedResources)
				{
					file = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(locale), "Localized.strings");
				}
				else
				{
					file = new File(getResourcesRealPath(locale) + FileUtil.getFileSeparator() + "Localized.strings");
				}
				theReturn = new IWResourceBundle(this, file, locale);
				getResourceBundles().put(locale, theReturn);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return theReturn;
	}
	
	/**
	 * Returns a Map of all loaded resourcebundles
	 * @return
	 */
	public Map getResourceBundles(){
		if(this.resourceBundlesLookup==null){
			resourceBundlesLookup=new HashMap();
		}
		return resourceBundlesLookup;
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
	public static String getFileSeparator()
	{
		return FileUtil.getFileSeparator();
	}
	public synchronized void storeState()
	{

		debug("Storing State");

		propertyList.store();
		boolean storeResourcesOnStore=getIfStoreResourcesOnStore();
		if(storeResourcesOnStore){
			this.storeLocalizableStrings();
			this.storeResourceBundles();
		}
		Iterator valueIter = getComponentPropertiesListMap().values().iterator();
		while (valueIter.hasNext())
		{
			IWPropertyList element = (IWPropertyList) valueIter.next();
			element.store();
		}

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
			ex.printStackTrace();
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
		return resourcesRealPath;
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
		return getApplication().getTranslatedURIWithContext(resourcesVirtualPath);
	}
	public String getResourcesRealPath(Locale locale)
	{
		String path = (String) getLocaleRealPaths().get(locale);
		if (path == null)
		{
			path = getResourcesRealPath() + FileUtil.getFileSeparator() + locale.toString() + ".locale";
			getLocaleRealPaths().put(locale, path);
		}
		return path;
	}
	protected Map getLocaleRealPaths(){
		if(this.localeRealPathsLookup==null){
			localeRealPathsLookup=new HashMap();
		}
		return localeRealPathsLookup;
	}
	public String getPropertiesRealPath()
	{
		return propertiesRealPath;
	}
	public static IWBundle getBundle(String bundleIdentifier, IWMainApplication application)
	{
		return application.getBundle(bundleIdentifier);
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
		return identifier;
	}
	/**
	 * temp implementation
	 */
	public String getBundleName()
	{
		return this.getBundleIdentifier();
	}
	public Image getImage(String urlInBundle)
	{
		return new Image(getResourcesURL() + slash + urlInBundle);
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
		return getResourcesRealPath() + FileUtil.getFileSeparator() + filename;
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
	public static List getAvailableComponentTypes()
	{
		return com.idega.core.component.data.ICObjectBMPBean.getAvailableComponentTypes();
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
	public void addComponent(String className, String componentType)
	{
		addComponent(className, componentType, className.substring(className.lastIndexOf(".") + 1));
	}
	public void addComponent(String className, String componentType, String componentName)
	{
		IWProperty prop = getComponentList().getNewProperty();
		prop.setName(className);
		String componentPropertyFileName = getDefaultComponentPropertyFileName(className);
		prop.getNewPropertyList().setProperty(this.COMPONENT_PROPERTY_FILE, componentPropertyFileName);
		IWPropertyList pl = initializeComponentPropertyList(className, componentPropertyFileName);
		pl.setProperty(COMPONENT_NAME_PROPERTY, componentName);
		pl.setProperty(COMPONENT_TYPE_PROPERTY, componentType);
		//setComponentProperty(prop, COMPONENT_NAME_PROPERTY, componentName);
		//setComponentProperty(prop, COMPONENT_TYPE_PROPERTY, componentType);
		addComponentToDatabase(className, componentType, componentName);
	}
	/**
	 * @param className
	 * @param componentPropertyFileName
	 * @return
	 */
	private IWPropertyList initializeComponentPropertyList(String className, String componentPropertyFileName)
	{
		IWPropertyList pl = new IWPropertyList(this.getPropertiesRealPath(), componentPropertyFileName, true);
		getComponentPropertiesListMap().put(className, pl);
		return pl;
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
		else
			return className + IWPropertyList.DEFAULT_FILE_ENDING;
	}
	private void addComponentToDatabase(String className, String componentType, String componentName)
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
				if (classIsRefactored)
				{
					try
					{
						ico.setObjectClass(Class.forName(newRefactoredClassName));
						ico.store();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					changeComponentInBundleRegistry(className, newRefactoredClassName);
					if (!ico.getBundleIdentifier().equals(this.getBundleIdentifier()))
					{
						System.out.println(
							"[IWBundle] : Updating bundle registry for component: "
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
						Class c = instanciateClass(className);
						//Class c = Class.forName(className);
						ico.setObjectClass(c);
						ico.setName(componentName);
						ico.setObjectType(componentType);
						ico.setBundle(this);
						ico.store();
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
						//e.printStackTrace();
						System.out.println(
							"[IWBundle] : Warning : Loading bundle: "
								+ this.getBundleIdentifier()
								+ " : Class "
								+ e.getMessage()
								+ " not found. Could be deprecated");
					}
					catch (InstantiationException e)
					{
						e.printStackTrace();
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
					catch (RegisterException e)
					{
						e.printStackTrace();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (IDOLookupException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	/**
	 * @param className
	 * @return
	 */
	private Class instanciateClass(String className) throws ClassNotFoundException
	{
		try
		{
			Class c = Class.forName(className);
			return c;
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				RefactorClassRegistry rfregistry = RefactorClassRegistry.getInstance();
				String s2 = rfregistry.getRefactoredClassName(className);
				return Class.forName(s2);
			}
			catch (ClassNotFoundException e2)
			{
			}
			catch (NullPointerException e3)
			{
			}
			throw e;
		}
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
				if (autoMoveComponentPropertiesToFile)
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
			prop.getNewPropertyList().setProperty(this.COMPONENT_PROPERTY_FILE, fileName);
			propertyList.store();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private Map getComponentPropertiesListMap()
	{
		if (componentPropertyListMap == null)
		{
			componentPropertyListMap = new HashMap();
		}
		return componentPropertyListMap;
	}
	/**
	 * @deprecated This method is obsolete
	 * @param component
	 * @param propertyName
	 * @param propertyValue
	 */
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
		com.idega.core.component.data.ICObjectBMPBean.removeICObject(className);
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
	public String toString(){
		return this.getBundleIdentifier();
	}
	
	
	//STANDARD LOGGING METHODS:
	
	/**
	 * Logs out to the default log level (which is by default INFO)
	 * @param msg The message to log out
	 */
	protected void log(String msg) {
		//System.out.println(string);
		getLogger().log(getDefaultLogLevel(),msg);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the default Logger
	 * @param e The Exception to log out
	 */
	protected void log(Exception e) {
		LoggingHelper.logException(e,this,getLogger(),getErrorLogLevel());
	}
	
	/**
	 * Logs out to the specified log level to the default Logger
	 * @param level The log level
	 * @param msg The message to log out
	 */
	protected void log(Level level,String msg) {
		//System.out.println(msg);
		getLogger().log(level,msg);
	}
	
	/**
	 * Logs out to the error log level (which is by default WARNING) to the default Logger
	 * @param msg The message to log out
	 */
	protected void logError(String msg) {
		//System.err.println(msg);
		getLogger().log(getErrorLogLevel(),msg);
	}

	/**
	 * Logs out to the debug log level (which is by default FINER) to the default Logger
	 * @param msg The message to log out
	 */
	protected void logDebug(String msg) {
		//System.err.println(msg);
		getLogger().log(getDebugLogLevel(),msg);
	}
	
	/**
	 * Logs out to the SEVERE log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logSevere(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.SEVERE,msg);
	}	
	
	
	/**
	 * Logs out to the WARNING log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logWarning(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.WARNING,msg);
	}
	
	/**
	 * Logs out to the CONFIG log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void logConfig(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.CONFIG,msg);
	}	
	
	/**
	 * Logs out to the debug log level to the default Logger
	 * @param msg The message to log out
	 */
	protected void debug(String msg) {
		String logMsg = "[IWBundle] : "+getBundleIdentifier()+" : "+msg;
		logDebug(logMsg);
	}	
	
	/**
	 * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
	 * This behaviour can be overridden in subclasses.
	 * @return the default Logger
	 */
	protected Logger getLogger(){
		return Logger.getLogger(this.getClass().getName());
	}
	
	/**
	 * Gets the log level which messages are sent to when no log level is given.
	 * @return the Level
	 */
	protected Level getDefaultLogLevel(){
		return Level.INFO;
	}
	/**
	 * Gets the log level which debug messages are sent to.
	 * @return the Level
	 */
	protected Level getDebugLogLevel(){
		return Level.FINER;
	}
	/**
	 * Gets the log level which error messages are sent to.
	 * @return the Level
	 */
	protected Level getErrorLogLevel(){
		return Level.WARNING;
	}
	
	//ENTITY SPECIFIC LOG MEHTODS:	
}