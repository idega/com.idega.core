/*
 * $Id: IWBundle.java,v 1.100 2009/03/11 08:07:35 civilis Exp $
 * Created on 28.7.2004 by tryggvil - interface created, class refactored
 *
 * Copyright (C) 2001-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.FinderException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputText;

import com.idega.core.component.data.ICObject;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
 * This is the declaration of the class that represents each bundle folder (or file).
 * An instance of this class is loaded on startup for each bundle that is installed
 * in the idegaWeb application. (by default in /idegaweb/bundles/).
 * An idegaWeb Bundle is convenient class to access properties and resources etc. for the
 * components contained in the bundle.<br>
 * The default implementation for this is DefaultIWBundle.<br>
 *
 * Last modified: $Date: 2009/03/11 08:07:35 $ by $Author: civilis $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.100 $
 */
public interface IWBundle extends IWModule, Comparable<IWBundle> {
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
	@Override
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

	public String getRootVirtualPath();

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
	 * Returns a Map of all loaded resource bundles
	 * @return
	 */
	public abstract Map<Locale, IWResourceBundle> getResourceBundles();

	public abstract String getVersion();

	public abstract String getBundleType();

	public abstract void storeState();

	public abstract void storeState(boolean storeAllComponents);

	public abstract String getResourcesRealPath();

	public abstract String getResourcesURL(Locale locale);

	public abstract String getResourcesURL();

	public String getResourcesPath();

	public abstract String getResourcesVirtualPath(Locale locale);

	public abstract String getResourcesVirtualPath();

	public abstract String getResourcesRealPath(Locale locale);

	public abstract String getPropertiesRealPath();

	public abstract void addLocale(Locale locale);

	public boolean isLocaleEnabled(Locale locale);

	public List<Locale> getEnabledLocales();

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
	public abstract Collection<ICObject> getICObjectsList() throws FinderException, IDOLookupException;

	/**
	 * Returns the ICObjects associated with this bundle
	 * Returns null if there is an exception
	 * @deprecated Replaced with getICObjectsList()
	 */
	@Deprecated
	public abstract ICObject[] getICObjects();

	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 */
	public abstract Collection<ICObject> getICObjectsList(String componentType) throws FinderException, IDOLookupException;

	/**
	 * Returns the ICObjects associated with this bundle and of the specified componentType
	 * Returns null if there is an exception
	 * @deprecated replaced with getICObjectsList(componentType);
	 */
	@Deprecated
	public abstract ICObject[] getICObjects(String componentType);

	public abstract void addComponent(String className, String componentType, boolean block, boolean widget, String description, String iconURI);

	public abstract void addComponent(String className, String componentType,
			String componentName, boolean block, boolean widget, String description, String iconURI);

	public abstract void setComponentProperty(String className,
			String propertyName, String propertyValue);

	public abstract IWPropertyList getComponentPropertyList(String className);

	public abstract String getComponentProperty(String className,
			String propertyName);

	public abstract String getComponentName(Class<?> componentClass);

	public abstract String getComponentName(String className);

	public abstract String getComponentType(Class<? extends UIComponent> componentClass);

	public abstract String getComponentType(String className);

	/**
	 * Returns getComponentName(componentClass) if localized name not found
	 */
	public abstract String getComponentName(Class<? extends UIComponent> componentClass, Locale locale);

	/**
	 * Returns getComponentName(className) if localized name not found
	 */
	public abstract String getComponentName(String className, Locale locale);

	public abstract void setComponentName(Class<? extends UIComponent> componentClass, Locale locale, String sName);

	public abstract String getComponentName(Class<? extends UIComponent> componentClass, Locale locale, String returnIfNameNotLocalized);

	public abstract String getComponentName(String className, Locale locale, String returnIfNameNotLocalized);

	public abstract void setComponentName(String className, Locale locale, String sName);

	public abstract void removeComponent(String className);

	public abstract List<String> getComponentKeys();

	public abstract void addLocalizableString(String key, String value);

	public abstract boolean containsLocalizedString(String key);

	@Override
	public abstract String toString();

	public void runBundleStarters();

	/**
	 * Returns the URI of a jsp page found inside this bundle
	 * @param jspInBundle
	 * @return
	 */
	public String getJSPURI(String jspInBundle);

	/**
	 * Returns the URI of a facelet page found inside this bundle
	 * @param faceletInBundle
	 * @return
	 */
	public abstract String getFaceletURI(String faceletUri);

	public HtmlGraphicImage getLocalizedImage(String pathAndName);
	public HtmlGraphicImage getLocalizedImage(String pathAndName, IWContext context);

	public ValueExpression getValueExpression(String localizationKey);

	/**
	 * Returns a localized HtmlOutputText object.
	 * @param localizationKey LocalizationKey
	 * @return
	 */
	public HtmlOutputText getLocalizedText(String localizationKey);

	/**
	 * Returns a localized UIComponent object. Value is bound to the <code>value</code> attribute.
	 * @param localizationKey LocalizationKey
	 * @return
	 */
	public <T extends UIComponent> T getLocalizedUIComponent(String localizationKey, T component);

	/**
	 * Returns a localized UIComponent object. Value is bound to the <code>value</code> attribute.
	 * @param localizationKey LocalizationKey
	 * @param defaultValue The default value
	 * @return
	 */
	public <T extends UIComponent> T getLocalizedUIComponent(String localizationKey, T component, String defaultValue);

	/**
	 * Returns a localized string.
	 * Use ONLY if you need the string, otherwise use the getLocalizedText or getLocalizeUIComponent
	 * @param localizationKey LocalizationKey
	 * @return
	 */
	public String getLocalizedString(String localizationKey);

	/**
	 * Returns a localized string.
	 * Use ONLY if you need the string, otherwise use the getLocalizedText or getLocalizeUIComponent
	 * @param localizationKey LocalizationKey
	 * @param defaultValue The default value
	 * @return
	 */
	public String getLocalizedString(String localizationKey, String defaultValue);

	/**
	 * Returns all the DATA component types registered to this bundle
	 * @return a collection of ICObjects.
	 * @throws IDOLookupException
	 * @throws FinderException
	 */
	public Collection<ICObject> getDataObjects() throws IDOLookupException, FinderException;


	/**
	 * Current locale is the current locale for the user that is running the request.
	 * @return returns vitual path to the current locale resource folder, without the context.
	 */
	public String getResourcesPathForCurrentLocale();

	/**
	 * <p>
	 * Returns the URI to a resource inside the '/resources/' folder inside this bundle.<br/>
	 * This method does not include a potential webapplication context path.
	 * </p>
	 * @param pathInResourceFolder path relative to this bundles resource virtual path
	 * @return
	 */
	public String getResourceURIWithoutContextPath(String pathInResourceFolder);

	/**
	 * <p>
	 * Returns InputStream for a resource identified by <code>pathWithinBundle</code>.
	 * Path starts at the root of the bundle.
	 * </p>
	 * @param pathWithinBundle
	 * @return
	 * @throws IOException
	 */
	public InputStream getResourceInputStream(String pathWithinBundle) throws IOException;

	/**
	 * <p>
	 * Returns time when resource identified by <code>pathWithinBundle</code> was last modified.
	 * Path starts at the root of the bundle.
	 * </p>
	 * @param pathWithinBundle
	 * @return miliseconds since Epoch, or 0 if not found
	 */
	public long getResourceTime(String pathWithinBundle);

	public abstract boolean isPostponedBundleStartersRun();

	public abstract void setPostponedBundleStartersRun(boolean postponedBundleStartersRun);
}