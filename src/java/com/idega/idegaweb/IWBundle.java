/*
 * Created on 28.7.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.idegaweb;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.core.component.data.ICObject;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IWBundle {
	/**
	 * Discards all unsaved changes to this bundle and loads it up again 
	 */
	public abstract void reloadBundle();

	/**
	 *Reloads all resources for this bundle and stores the state of this bundle first if storeState==true
	 *@param storeState to say if to store the state (call storeState) before the bundle is loaded again
	 */
	public abstract void reloadBundle(boolean storeState);

	/**
	 *Stores this bundle and unloads all resources;
	 */
	public abstract void unload();

	/**
	 *Unloads all resources for this bundle and stores the state of this bundle if storeState==true
	 *@param storeState to say if to store the state (call storeState)
	 */
	public abstract void unload(boolean storeState);

	/**
	 * gets the base path of this bundle.<br>
	 * e.g. /home/idegaweb/webapp/iw1/idegaweb/bundles/com.idega.core.bundle
	 */
	public abstract String getBundleBaseRealPath();

	public abstract Image getIconImage();

	public abstract String getProperty(String propertyName);

	public abstract String getProperty(String propertyName,
			String returnValueIfNull);

	public abstract boolean getBooleanProperty(String propertyName);

	public abstract boolean getBooleanProperty(String propertyName,
			boolean returnValueIfNull);

	public abstract void setBooleanProperty(String propertyName,
			boolean setValue);

	public abstract void removeProperty(String propertyName);

	public abstract void setProperty(String propertyName, String propertyValue);

	public abstract void setProperty(String propertyName,
			String[] propertyValues);

	public abstract void setArrayProperty(String propertyName,
			String propertyValue);

	public abstract IWMainApplication getApplication();

	public abstract void setProperty(String propertyName);

	public abstract void setRootVirtualPath(String path);

	public abstract Image getLocalizedImage(String name, Locale locale);

	/**
	 * Convenience method - Recommended to create a ResourceBundle (through getResourceBundle(locale)) to use instead more efficiently
	 */
	public abstract String getLocalizedString(String name, Locale locale);

	public abstract String[] getAvailableProperties();

	public abstract String[] getLocalizableStrings();

	public abstract boolean removeLocalizableString(String key);

	public abstract String getLocalizableStringDefaultValue(String key);

	public abstract IWPropertyList getUserProperties(IWUserContext iwuc);

	public abstract IWResourceBundle getResourceBundle(IWContext iwc);

	public abstract IWResourceBundle getResourceBundle(Locale locale);

	/**
	 * Returns a Map of all loaded resourcebundles
	 * @return
	 */
	public abstract Map getResourceBundles();

	public abstract String getVersion();

	public abstract String getBundleType();

	public abstract void storeState();

	public abstract String getResourcesRealPath();

	public abstract String getResourcesURL(Locale locale);

	public abstract String getResourcesURL();

	public abstract String getResourcesVirtualPath(Locale locale);

	public abstract String getResourcesVirtualPath();

	public abstract String getResourcesRealPath(Locale locale);

	public abstract String getPropertiesRealPath();

	public abstract void addLocale(Locale locale);

	public abstract String getBundleIdentifier();

	/**
	 * temp implementation
	 */
	public abstract String getBundleName();

	public abstract Image getImage(String urlInBundle);
	
	public abstract String getImageURI(String urlInBundle);

	public abstract String getVirtualPathWithFileNameString(String filename);

	public abstract String getVirtualPath();

	public abstract String getRealPathWithFileNameString(String filename);

	public abstract String getRealPath();

	public abstract Image getImage(String urlInBundle, int width, int height);

	public abstract Image getImageButton(String text);

	public abstract Image getImageTab(String text, boolean flip);

	public abstract Image getImage(String urlInBundle, String name, int width,
			int height);

	public abstract Image getSharedImage(String urlInBundle, String name);

	public abstract Image getImage(String urlInBundle, String overUrlInBundle,
			String name, int width, int height);

	public abstract Image getImage(String urlInBundle, String overUrlInBundle,
			String name);

	public abstract Image getImage(String urlInBundle, String name);

	/**
	 * Returns the ICObjects associated with this bundle
	 * Returns an empty list if nothing found
	 */
	public abstract Collection getICObjectsList() throws FinderException,
			IDOLookupException;

	/**
	 * Returns the ICObjects associated with this bundle
	 * Returns null if there is an exception
	 * @deprecated Replaced with getICObjectsList()
	 */
	public abstract ICObject[] getICObjects();

	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 */
	public abstract Collection getICObjectsList(String componentType)
			throws FinderException, IDOLookupException;

	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 * @deprecated replaced with getICObjectsList(componentType);
	 */
	public abstract ICObject[] getICObjects(String componentType);

	public abstract void addComponent(String className, String componentType);

	public abstract void addComponent(String className, String componentType,
			String componentName);

	public abstract void setComponentProperty(String className,
			String propertyName, String propertyValue);

	public abstract IWPropertyList getComponentPropertyList(String className);

	public abstract String getComponentProperty(String className,
			String propertyName);

	public abstract String getComponentName(Class componentClass);

	public abstract String getComponentName(String className);

	public abstract String getComponentType(Class componentClass);

	public abstract String getComponentType(String className);

	/**
	 * Returns getComponentName(componentClass) if localized name not found
	 */
	public abstract String getComponentName(Class componentClass, Locale locale);

	/**
	 * Returns getComponentName(className) if localized name not found
	 */
	public abstract String getComponentName(String className, Locale locale);

	public abstract void setComponentName(Class componentClass, Locale locale,
			String sName);

	public abstract String getComponentName(Class componentClass,
			Locale locale, String returnIfNameNotLocalized);

	public abstract String getComponentName(String className, Locale locale,
			String returnIfNameNotLocalized);

	public abstract void setComponentName(String className, Locale locale,
			String sName);

	public abstract void removeComponent(String className);

	public abstract List getComponentKeys();

	public abstract int compareTo(Object o);

	public abstract void addLocalizableString(String key, String value);

	public abstract boolean containsLocalizedString(String key);

	public abstract String toString();
	
	public void runBundleStarters();
}