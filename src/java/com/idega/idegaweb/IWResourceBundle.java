/*
 * $Id: IWResourceBundle.java,v 1.32 2004/07/01 01:52:39 eiki Exp $
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.idega.exception.IWBundleDoesNotExist;
import com.idega.presentation.Image;
import com.idega.util.EnumerationIteratorWrapper;
import com.idega.util.SortedProperties;
import com.idega.util.StringHandler;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWResourceBundle extends ResourceBundle {

	// ==================privates====================
	private TreeMap lookup;
	private Properties properties = new SortedProperties();
	//private Properties properties = new Properties();
	private Locale locale;
	private File file;
	private IWBundle iwBundleParent;
	private String resourcesURL;
	private static String slash = "/";

	/**
	 * Creates a IWResourceBundle for a specific Locale
	 * @param file file to read from.
	 * @param parent Parent IWBundle to instanciate from
	 * @param locale Locale to create from
	 */
	public IWResourceBundle(IWBundle parent, File file, Locale locale) throws IOException {
		setIWBundleParent(parent);
		setLocale(locale);
		this.file = file;

		try {
			properties.load(new FileInputStream(file));
			lookup = new TreeMap(properties);
			setResourcesURL(parent.getResourcesVirtualPath() + "/" + locale.toString() + ".locale");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Override of ResourceBundle, same semantics
	 */
	public Object handleGetObject(String key) {
		if (lookup != null) {
			Object obj = lookup.get(key);
			return obj; // once serialization is in place, you can do non-strings
		}
		else {
			IWBundle parent = getIWBundleParent();
			if (parent != null) {
				throw new IWBundleDoesNotExist(parent.getBundleIdentifier());
			}
			else {
				throw new IWBundleDoesNotExist();
			}
		}
	}

	/**
	 * Implementation of ResourceBundle.getKeys.
	 */
	public Enumeration getKeys() {
		Enumeration result = null;
		if (parent != null) {
			Iterator iter = lookup.keySet().iterator();
			final Enumeration myKeys = new EnumerationIteratorWrapper(iter);
			final Enumeration parentKeys = parent.getKeys();

			result = new Enumeration() {
				public boolean hasMoreElements() {
					if (temp == null)
						nextElement();
					return temp != null;
				}

				public Object nextElement() {
					Object returnVal = temp;
					if (myKeys.hasMoreElements())
						temp = myKeys.nextElement();
					else {
						temp = null;
						while (temp == null && parentKeys.hasMoreElements()) {
							temp = parentKeys.nextElement();
							if (lookup.containsKey(temp))
								temp = null;
						}
					}
					return returnVal;
				}

				Object temp = null;
			};
		}
		else {
			Iterator iter = lookup.keySet().iterator();
			result = new EnumerationIteratorWrapper(iter);
		}

		return result;
	}

	public Locale getLocale() {
		return locale;
	}

	private void setLocale(Locale locale) {
		this.locale = locale;
	}

	public synchronized void storeState() {
		try {
			properties.clear();
			if (lookup != null) {
				Iterator iter = lookup.keySet().iterator();
				while (iter.hasNext()) {
					Object key = iter.next();
					if (key != null) {
						Object value = lookup.get(key);
						if (value != null) {
							properties.put(key, value);
						}
					}
				}
				FileOutputStream fos = new FileOutputStream(file);
				properties.store(fos, null);
				fos.close();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Uses getString but returns null if resource is not found
	 */
	public String getLocalizedString(String key) {

		try {
			return super.getString(key);
		}
		catch (MissingResourceException e) {
			//if (getIWBundleParent().getApplication().getSettings().isAutoCreatePropertiesActive()) {
			//	setLocalizedString(key, "");
			//}
			return null;
		}
	}
	
	/**
	* Gets a localized stringvalue  and sets the value as returnValueIfNotFound if it is previously not found and returns it.
	*/
	public String getLocalizedString(String key, String returnValueIfNotFound) {
		String returnString = getLocalizedString(key);
		if ( ( (returnString == null) || StringHandler.EMPTY_STRING.equals(returnString) ) && returnValueIfNotFound!=null ) {//null check on return value IS necessary
			if (getIWBundleParent().getApplication().getSettings().isAutoCreateStringsActive()) {
				//if (getIWBundleParent().getApplication().getSettings().isDebugActive())
				//	System.out.println("Storing localized string: " + key);
				//setLocalizedString(key, returnValueIfNotFound);
				this.checkBundleLocalizedString(key,returnValueIfNotFound);
			}
			return returnValueIfNotFound;
		}
		else
			return returnString;
	}

	/**
	* Uses getLocalizedString but returns null if resource is not found
	*/
	public Image getLocalizedImageButton(String key) {
		try {
			String text = getLocalizedString(key);
			return this.iwBundleParent.getApplication().getImageFactory().createButton(text, iwBundleParent, getLocale());
		}
		catch (MissingResourceException e) {
			return null;
		}
	}

	public Image getLocalizedImageButton(String key, String returnValueIfNull) {
		String text = getLocalizedString(key, returnValueIfNull);
		return this.iwBundleParent.getApplication().getImageFactory().createButton(text, iwBundleParent, getLocale());
	}

	/**
	  * Uses getLocalizedString but returns null if resource is not found
	  */
	public Image getLocalizedImageTab(String key, boolean flip) {
		try {
			String text = getLocalizedString(key);
			return this.iwBundleParent.getApplication().getImageFactory().createTab(text, iwBundleParent, getLocale(), flip);
		}
		catch (MissingResourceException e) {
			return null;
		}
	}

	public Image getLocalizedImageTab(String key, String returnValueIfNull, boolean flip) {
		String text = getLocalizedString(key, returnValueIfNull);
		return this.iwBundleParent.getApplication().getImageFactory().createTab(text, iwBundleParent, getLocale(), flip);
	}

	/**
	 * Sets a value of a string key in this ResourceBundle. Stores the files implicitly (storeState()) to the diskafter a call to this method.
	 * @param key a String key
	 * @param value a value to the key
	 */
	public void setString(String key, String value) {
		lookup.put(key, value);
		checkBundleLocalizedString(key,value);
		this.storeState();
	}

	public boolean removeString(String key) {
		if((String) lookup.remove(key) != null ){
			this.storeState();
			return true;
		}
		return false;
	}

	private void setIWBundleParent(IWBundle parent) {
		this.iwBundleParent = parent;
	}

	public IWBundle getIWBundleParent() {
		return iwBundleParent;
	}

	/*
	 * Returns the Resource URI for the image with the internal url urlInBundle inside the resource bundle
	 */
	public String getImageURI(String urlInBundle) {
		return getResourcesURL() + slash + urlInBundle;
	}

	public Image getImage(String urlInBundle) {
		return new Image(getImageURI(urlInBundle));
	}

	public Image getImage(String urlInBundle, int width, int height) {
		return getImage(urlInBundle, "", width, height);
	}

	public Image getImage(String urlInBundle, String name, int width, int height) {
		return new Image(getResourcesURL() + slash + urlInBundle, name, width, height);
	}

	public Image getImage(String urlInBundle, String overUrlInBundle, String imageName, String overImageName) {
		return new Image(imageName, getResourcesURL() + slash + urlInBundle, getResourcesURL() + slash + overUrlInBundle);
	}

	public Image getImage(String urlInBundle, String overUrlInBundle, String name, int width, int height) {
		Image returnImage = new Image(name, getResourcesURL() + slash + urlInBundle, getResourcesURL() + slash + overUrlInBundle);
		returnImage.setWidth(width);
		returnImage.setHeight(height);
		return returnImage;
	}

	public Image getImage(String urlInBundle, String name) {
		return new Image(getResourcesURL() + slash + urlInBundle, name);
	}

	public Image getImage(String urlInBundle, String key, String defaultKeyValue) {
		return new Image(getResourcesURL() + slash + urlInBundle, getLocalizedString(key, defaultKeyValue));
	}

	private void setResourcesURL(String url) {
		resourcesURL = url;
	}

	public String getResourcesURL() {
		return resourcesURL;
	}

	/**
	 *
	 */
	public void setLocalizedString(String key, String value) {
		this.setString(key,value);
	}

	protected boolean checkBundleLocalizedString(String key, String value) {
		IWBundle bundle = getIWBundleParent();
		if (!bundle.containsLocalizedString(key)) {
			bundle.addLocalizableString(key, value);
			bundle.storeLocalizableStrings();
			return true;
		}
		return false;
	}
	
	public String toString(){
		IWBundle bundleParent = this.getIWBundleParent();
		if(bundleParent!=null){
			return bundleParent+"/"+this.locale;
		}
		else{
			return this.locale.toString();
		}
	}
}