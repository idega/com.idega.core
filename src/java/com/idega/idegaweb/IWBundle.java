/*
 * $Id: IWBundle.java,v 1.61 2003/07/21 10:49:09 aron Exp $
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.user.business.UserProperties;
import com.idega.util.FileUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.SortedProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.ejb.FinderException;

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
public class IWBundle implements java.lang.Comparable {
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

	private Hashtable localePaths;
	private Hashtable resourceBundles;

	private boolean autoCreateLocalizedResources = true;
	private boolean autoCreate = false;

	private Hashtable handlers;
	private Hashtable localeRealPaths;

	private SortedMap localizableStringsMap;
	private Properties localizableStringsProperties;

	private File localizableStringsFile;

	private IWPropertyList propertyList;

	static final String propertyFileName = "bundle.pxml";

	final static String BUNDLE_IDENTIFIER_PROPERTY_KEY = "iw_bundle_identifier";
	final static String COMPONENTLIST_KEY = "iw_components";

	private final static String COMPONENT_NAME_PROPERTY = "component_name";
	private final static String COMPONENT_TYPE_PROPERTY = "component_type";
	private final static String COMPONENT_ICON_PROPERTY = "component_icon";
	private final static String COMPONENT_CLASS_PROPERTY = "component_class";
	private final static String BUNDLE_STARTER_CLASS = "iw_bundle_starter_class";

	private IWBundleStartable starter;

	protected IWBundle(String rootRealPath, String bundleIdentifier, IWMainApplication superApplication) {
		this(rootRealPath, rootRealPath, bundleIdentifier, superApplication);
	}

	protected IWBundle(String rootRealPath, String rootVirtualPath, String bundleIdentifier, IWMainApplication superApplication) {
		this(rootRealPath, rootVirtualPath, bundleIdentifier, superApplication, false);
	}

	protected IWBundle(String rootRealPath, String rootVirtualPath, String bundleIdentifier, IWMainApplication superApplication, boolean autoCreate) {
		this.autoCreate = autoCreate;
		this.superApplication = superApplication;
		this.identifier = bundleIdentifier;
		handlers = new Hashtable();
		localeRealPaths = new Hashtable();
		resourceBundles = new Hashtable();
		setRootRealPath(rootRealPath);
		setRootVirtualPath(rootVirtualPath);
		
			try {
				loadBundle();
			}
			catch (RuntimeException e) {
				e.printStackTrace();
			}
	
		this.setProperty(BUNDLE_IDENTIFIER_PROPERTY_KEY, bundleIdentifier);
	}

	public void reloadBundle() {
		loadBundle();
		resourceBundles.clear();
		localizableStringsProperties = null;
	}

	private void loadBundle() {
		setResourcesVirtualPath(getRootVirtualPath() + "/" + "resources");
		setResourcesRealPath(getRootRealPath() + FileUtil.getFileSeparator() + "resources");
		setPropertiesRealPath(getRootRealPath() + FileUtil.getFileSeparator() + "properties");

		setClassesRealPath();
		if (autoCreate) {
			this.initializeStructure();
			propertyList = new IWPropertyList(getPropertiesRealPath(), propertyFileName, true);
		}
		else {
			propertyList = new IWPropertyList(getPropertiesRealPath(), propertyFileName, false);
		}

		StringBuffer SystemClassPath = new StringBuffer(System.getProperty("java.class.path"));
		SystemClassPath.append(File.pathSeparator);
		SystemClassPath.append(getClassesRealPath());

		System.setProperty("java.class.path", SystemClassPath.toString());

		installComponents();
		
			try {
				createDataRecords();
				registerBlockPermisionKeys();
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				logMessage("No bundle components found for "+getBundleIdentifier());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		

		runStartClass();

	}

	private void createDataRecords() throws Exception {
		
		List entities = com.idega.data.EntityFinder.findAllByColumn(com.idega.core.data.ICObjectBMPBean.getStaticInstance(ICObject.class), com.idega.core.data.ICObjectBMPBean.getObjectTypeColumnName(), com.idega.core.data.ICObjectBMPBean.COMPONENT_TYPE_DATA, com.idega.core.data.ICObjectBMPBean.getBundleColumnName(), this.getBundleIdentifier());
		if (entities != null) {
			Iterator iter = entities.iterator();
			while (iter.hasNext()) {
				ICObject ico = (ICObject) iter.next();
				
					try {
						Class c = ico.getObjectClass();
						IDOLookup.instanciateEntity(c);
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				
			}
		}
	}

	private void registerBlockPermissionKeys(Class blockClass) throws InstantiationException,IllegalAccessException {
		Object o = blockClass.newInstance();
		if (o instanceof Block)
			 ((Block) o).registerPermissionKeys();
	}

	private void registerBlockPermisionKeys() throws IDOLookupException,FinderException  {
		ICObjectHome icObjectHome =(ICObjectHome) IDOLookup.getHome(ICObject.class);
		Collection objects = icObjectHome.findAllBlocksByBundle(this.getBundleIdentifier());
		if (objects != null) {
			Iterator iter = objects.iterator();
			while (iter.hasNext()) {
				ICObject ico = (ICObject) iter.next();
				
					try {
						Class c = ico.getObjectClass();
						registerBlockPermissionKeys(c);
					}
					catch (ClassNotFoundException e) {
						logMessage("Class not found for Block: "+ico.getName());
						//e.printStackTrace();
					}
					catch (InstantiationException e) {
						e.printStackTrace();
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				
			}
		}
	}

	private void runStartClass() {
		String starterClassName = this.getProperty(this.BUNDLE_STARTER_CLASS);
		if (starterClassName != null) {
			try {
				starter = (IWBundleStartable) Class.forName(starterClassName).newInstance();
				starter.start(this);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void unload() {
		storeState();
		stopStartClass();
	}

	private void stopStartClass() {
		if (starter != null) {
			starter.stop(this);
		}
	}

	private void installComponents() {
		List list = this.getComponentKeys();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			String className = (String) iter.next();
			String componentName = getComponentName(className);
			String componentType = this.getComponentType(className);

			addComponentToDatabase(className, componentType, componentName);
		}
	}

	protected String getRootRealPath() {
		return rootRealPath;
	}

	protected String getRootVirtualPath() {
		return rootVirtualPath;
	}

	public Image getIconImage() {
		return new Image(getProperty("iconimage"));
	}

	public String getProperty(String propertyName) {
		return propertyList.getProperty(propertyName);
	}

	public String getProperty(String propertyName, String returnValueIfNull) {
		String prop = getProperty(propertyName);
		if (prop == null) {
			if (getApplication().getSettings().isAutoCreatePropertiesActive()) {
				if (getApplication().getSettings().isDebugActive())
					System.out.println("Storing property: " + propertyName);
				setProperty(propertyName, returnValueIfNull);
			}
			return returnValueIfNull;
		}
		else
			return prop;
	}

	public void removeProperty(String propertyName) {
		propertyList.removeProperty(propertyName);
	}

	public void setProperty(String propertyName, String propertyValue) {
		propertyList.setProperty(propertyName, propertyValue);
	}

	public void setProperty(String propertyName, String[] propertyValues) {
		propertyList.setProperty(propertyName, propertyValues);
	}

	public void setArrayProperty(String propertyName, String propertyValue) {
		propertyList.setArrayProperty(propertyName, propertyValue);
	}

	public IWMainApplication getApplication() {
		return this.superApplication;
	}

	public void setProperty(String propertyName) {
		propertyList.removeProperty(propertyName);
	}
	private void setResourcesRealPath(String path) {
		resourcesRealPath = path;
	}

	private void setResourcesVirtualPath(String path) {
		resourcesVirtualPath = path;
	}

	private void setPropertiesRealPath(String path) {
		propertiesRealPath = path;
	}

	private void setRootRealPath(String path) {
		rootRealPath = path;
	}

	public void setRootVirtualPath(String path) {
		rootVirtualPath = path;
	}

	public Image getLocalizedImage(String name, Locale locale) {
		return getResourceBundle(locale).getImage(name);
	}

	/**
	 * Convenience method - Recommended to create a ResourceBundle (through getResourceBundle(locale)) to use instead more efficiently
	 */
	public String getLocalizedString(String name, Locale locale) {
		return getResourceBundle(locale).getString(name);
	}

	protected String getClassesRealPath() {
		return classesRealPath;
	}

	private void setClassesRealPath() {
		classesRealPath = this.getRootRealPath() + FileUtil.getFileSeparator() + "classes";
	}

	public String[] getAvailableProperties() {
		return ((String[]) ((Vector) propertyList.getKeys()).toArray(new String[0]));
	}

	public String[] getLocalizableStrings() {
		return (String[]) getLocalizableStringsMap().keySet().toArray(new String[0]);
	}

	public boolean removeLocalizableString(String key) {
		Enumeration enum = this.resourceBundles.elements();
		while (enum.hasMoreElements()) {
			IWResourceBundle item = (IWResourceBundle) enum.nextElement();
			item.removeString(key);
		}
		return this.localizableStringsMap.remove(key) != null ? true : false;
	}

	protected Properties getLocalizableStringsProperties() {
		initializePropertiesStrings();
		return localizableStringsProperties;
	}

	protected Map getLocalizableStringsMap() {
		initializePropertiesStrings();
		return localizableStringsMap;
	}

	private void initializePropertiesStrings() {
		if (localizableStringsProperties == null) {
			localizableStringsProperties = new SortedProperties();
			try {
				localizableStringsProperties.load(new FileInputStream(getLocalizableStringsFile()));
				localizableStringsMap = new TreeMap(localizableStringsProperties);
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private File getLocalizableStringsFile() {
		if (localizableStringsFile == null) {
			try {
				localizableStringsFile = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(), "Localizable.strings");
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return localizableStringsFile;
	}

	public IWPropertyList getUserProperties(IWUserContext iwuc) {
		UserProperties properties = (UserProperties) getUserProperties(iwuc);
		if (properties != null)
			return properties.getProperties(this.getBundleName());
		return null;
	}

	public IWResourceBundle getResourceBundle(IWContext iwc) {
		return getResourceBundle(iwc.getCurrentLocale());
	}

	public IWResourceBundle getResourceBundle(Locale locale) {
		IWResourceBundle theReturn = (IWResourceBundle) resourceBundles.get(locale);
		try {
			if (theReturn == null) {
				File file;
				/**
				   * @todo: Look into this autoCreateLocalizedResources is always set true
				   */
				if (autoCreateLocalizedResources) {
					file = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(locale), "Localized.strings");
				}
				else {
					file = new File(getResourcesRealPath(locale) + FileUtil.getFileSeparator() + "Localized.strings");
				}
				theReturn = new IWResourceBundle(this, file, locale);
				resourceBundles.put(locale, theReturn);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return theReturn;
	}

	public String getVersion() {
		String theReturn = getProperty("version");
		if (theReturn == null) {
			theReturn = "1";
		}
		return theReturn;
	}

	public String getBundleType() {
		String theReturn = getProperty("bundletype");
		if (theReturn == null) {
			theReturn = "bundle";
		}
		return theReturn;
	}

	public static String getFileSeparator() {
		return FileUtil.getFileSeparator();
	}

	public void storeState() {
		propertyList.store();
		Enumeration enum = this.resourceBundles.elements();
		while (enum.hasMoreElements()) {
			IWResourceBundle item = (IWResourceBundle) enum.nextElement();
			item.storeState();
		}
		try {
			getLocalizableStringsProperties().clear();
			Iterator keyIter = getLocalizableStringsMap().keySet().iterator();
			while (keyIter.hasNext()) {
				Object key = keyIter.next();
				if (key != null) {
					Object value = getLocalizableStringsMap().get(key);
					if (value != null) {
						getLocalizableStringsProperties().put(key, value);
					}
				}
			}
			getLocalizableStringsProperties().store(new FileOutputStream(getLocalizableStringsFile()), null);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getResourcesRealPath() {
		return resourcesRealPath;
	}

	public String getResourcesURL(Locale locale) {
		return getResourcesVirtualPath(locale);
	}

	public String getResourcesURL() {
		return getResourcesVirtualPath();
	}

	public String getResourcesVirtualPath(Locale locale) {
		return this.getResourceBundle(locale).getResourcesURL();
	}

	public String getResourcesVirtualPath() {
		return getApplication().getTranslatedURIWithContext(resourcesVirtualPath);
	}

	public String getResourcesRealPath(Locale locale) {
		String path = (String) localeRealPaths.get(locale);
		if (path == null) {
			path = getResourcesRealPath() + FileUtil.getFileSeparator() + locale.toString() + ".locale";
			localeRealPaths.put(locale, path);
		}
		return path;
	}

	public String getPropertiesRealPath() {
		return propertiesRealPath;
	}

	public static IWBundle getBundle(String bundleIdentifier, IWMainApplication application) {
		return application.getBundle(bundleIdentifier);
	}

	public void addLocale(Locale locale) {
		String LocalePath = getResourcesRealPath(locale);
		File file = new File(LocalePath);
		file.mkdirs();
	}

	protected void initializeStructure() {
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
		for (int i = 0; i < dirs.length; i++) {
			File file = new File(dirs[i]);
			file.mkdirs();
		}
	}

	public String getBundleIdentifier() {
		return identifier;
	}

	/**
	 * temp implementation
	 */
	public String getBundleName() {
		return this.getBundleIdentifier();
	}

	public Image getImage(String urlInBundle) {
		return new Image(getResourcesURL() + slash + urlInBundle);
	}
	
	public String getVirtualPathWithFileNameString(String filename) {
		return getResourcesURL() + slash + filename;
	}
	
	public String getVirtualPath(){
		return getResourcesURL();
	}
	
	public String getRealPathWithFileNameString(String filename) {
		return getResourcesRealPath() + FileUtil.getFileSeparator() + filename;
	}
	
	public String getRealPath(){
		return getResourcesRealPath();
	}
	
	
	public Image getImage(String urlInBundle, int width, int height) {
		return getImage(urlInBundle, "", width, height);
	}

	public Image getImageButton(String text) {
		return this.getApplication().getImageFactory().createButton(text, this);
	}

	public Image getImageTab(String text, boolean flip) {
		return this.getApplication().getImageFactory().createTab(text, this, flip);
	}

	public Image getImage(String urlInBundle, String name, int width, int height) {
		return new Image(getResourcesURL() + slash + urlInBundle, name, width, height);
	}

	public Image getSharedImage(String urlInBundle, String name) {
		return new Image(getResourcesURL() + slash + shared + slash + urlInBundle, name);
	}

	public Image getImage(String urlInBundle, String overUrlInBundle, String name, int width, int height) {
		Image returnImage = new Image(name, getResourcesURL() + slash + urlInBundle, getResourcesURL() + slash + overUrlInBundle);
		returnImage.setWidth(width);
		returnImage.setHeight(height);
		return returnImage;
	}

	public Image getImage(String urlInBundle, String overUrlInBundle, String name) {
		Image returnImage = new Image(name, getResourcesURL() + slash + urlInBundle, getResourcesURL() + slash + overUrlInBundle);
		return returnImage;
	}

	public Image getImage(String urlInBundle, String name) {
		return new Image(getResourcesURL() + slash + urlInBundle, name);
	}

	/**
	 * Returns the ICObjects associated with this bundle
	 * Returns an empty list if nothing found
	 */
	public Collection getICObjectsList() throws FinderException,IDOLookupException {
		return getICObjectHome().findAllByBundle(this.getBundleIdentifier());
	}

	/**
	 * Returns the ICObjects associated with this bundle
	 * Returns null if there is an exception
	 * @deprecated Replaced with getICObjectsList()
	 */
	public ICObject[] getICObjects() {
		try {
			Collection l = getICObjectsList();
			return (ICObject[]) l.toArray(new ICObject[0]);
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 */
	public Collection getICObjectsList(String componentType) throws FinderException,IDOLookupException {
		return getICObjectHome().findAllByObjectTypeAndBundle(componentType,this.getBundleIdentifier());
	}

	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 * @deprecated replaced with getICObjectsList(componentType);
	 */
	public ICObject[] getICObjects(String componentType) {
		try {
			//return (ICObject[])(((com.idega.core.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy()).findAllByColumn(com.idega.core.data.ICObjectBMPBean.getBundleColumnName(),this.getBundleIdentifier(),com.idega.core.data.ICObjectBMPBean.getObjectTypeColumnName(),componentType);
			Collection l = getICObjectsList(componentType);
			return (ICObject[]) l.toArray(new ICObject[0]);
		}
		catch (Exception e) {
			return null;
		}
	}

	private IWPropertyList getPropertyList() {
		return this.propertyList;
	}

	public static List getAvailableComponentTypes() {
		return com.idega.core.data.ICObjectBMPBean.getAvailableComponentTypes();
	}

	public IWPropertyList getComponentList() {
		IWPropertyList list = getPropertyList().getPropertyList(COMPONENTLIST_KEY);
		if (list == null) {
			list = getPropertyList().getNewPropertyList(COMPONENTLIST_KEY);
		}
		return list;
	}

	public void addComponent(String className, String componentType) {
		addComponent(className, componentType, className.substring(className.lastIndexOf(".") + 1));
	}

	public void addComponent(String className, String componentType, String componentName) {
		IWProperty prop = getComponentList().getNewProperty();
		prop.setName(className);
		setComponentProperty(prop, COMPONENT_NAME_PROPERTY, componentName);
		setComponentProperty(prop, COMPONENT_TYPE_PROPERTY, componentType);
		addComponentToDatabase(className, componentType, componentName);
	}

	private void addComponentToDatabase(String className, String componentType, String componentName) {
		ICObject ico = com.idega.core.data.ICObjectBMPBean.getICObject(className);
		if (ico == null) {
			
				try {
					ico = ((com.idega.core.data.ICObjectHome) com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();
					Class c = Class.forName(className);
					ico.setObjectClass(c);
					ico.setName(componentName);
					ico.setObjectType(componentType);
					ico.setBundle(this);
					ico.insert();
					if (componentType.equals(com.idega.core.data.ICObjectBMPBean.COMPONENT_TYPE_ELEMENT) || componentType.equals(com.idega.core.data.ICObjectBMPBean.COMPONENT_TYPE_BLOCK)) {
						com.idega.core.accesscontrol.business.AccessControl.initICObjectPermissions(ico);
						if (componentType.equals(com.idega.core.data.ICObjectBMPBean.COMPONENT_TYPE_BLOCK)) {
							registerBlockPermissionKeys(c);
						}
					}
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
				catch (InstantiationException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			
		
		}
	}

	public void setComponentProperty(String className, String propertyName, String propertyValue) {
		IWProperty prop = getComponentList().getIWProperty(className);
		if (prop != null) {
			setComponentProperty(prop, propertyName, propertyValue);
		}
	}

	public void setComponentProperty(IWProperty component, String propertyName, String propertyValue) {
		IWPropertyList list = component.getPropertyList();
		if (list == null) {
			list = component.getNewPropertyList();
		}
		list.setProperty(propertyName, propertyValue);
	}

	public String getComponentProperty(String className, String property) {
		try {
			return getComponentList().getIWProperty(className).getPropertyList().getProperty(property);
		}
		catch (NullPointerException e) {
			return null;
		}
	}

	public String getComponentName(Class componentClass) {
		return getComponentName(componentClass.getName());
	}

	public String getComponentName(String className) {
		return getComponentProperty(className, COMPONENT_NAME_PROPERTY);
	}

	public String getComponentType(Class componentClass) {
		return getComponentType(componentClass.getName());
	}

	public String getComponentType(String className) {
		return getComponentProperty(className, COMPONENT_TYPE_PROPERTY);
	}

	/**
	 * Returns getComponentName(componentClass) if localized name not found
	 */
	public String getComponentName(Class componentClass, Locale locale) {
		return getComponentName(componentClass.getName(), locale);
	}

	/**
	 * Returns getComponentName(className) if localized name not found
	 */
	public String getComponentName(String className, Locale locale) {
		return getComponentName(className, locale, getComponentName(className));
	}

	public void setComponentName(Class componentClass, Locale locale, String sName) {
		setComponentName(componentClass.getName(), locale, sName);
	}

	public String getComponentName(Class componentClass, Locale locale, String returnIfNameNotLocalized) {
		return getComponentName(componentClass.getName(), locale, returnIfNameNotLocalized);
	}

	public String getComponentName(String className, Locale locale, String returnIfNameNotLocalized) {
		return this.getResourceBundle(locale).getLocalizedString("iw.component." + className + ".name", returnIfNameNotLocalized);
	}

	public void setComponentName(String className, Locale locale, String sName) {
		this.getResourceBundle(locale).setString("iw.component." + className + ".name", sName);
	}

	public void removeComponent(String className) {
		getComponentList().removeProperty(className);
		com.idega.core.data.ICObjectBMPBean.removeICObject(className);
	}

	public List getComponentKeys() {
		return getComponentList().getKeys();
	}

	public int compareTo(Object o) {
		IWBundle bundle = (IWBundle) o;
		return this.getBundleIdentifier().compareTo(bundle.getBundleIdentifier());
	}

	public void addLocalizableString(String key, String value) {
		getLocalizableStringsMap().put(key, value);
		storeState();
	}

	public boolean containsLocalizedString(String key) {
		return (getLocalizableStringsMap().containsKey(key));
	}
	
	private ICObjectHome getICObjectHome()throws IDOLookupException{
		return (ICObjectHome) IDOLookup.getHome(ICObject.class);
	}
	
	private void logMessage(String message){
		if(getApplication().isDebugActive())
			System.out.println("[IWBundle] : "+getBundleIdentifier()+" : "+message);
	 }
}