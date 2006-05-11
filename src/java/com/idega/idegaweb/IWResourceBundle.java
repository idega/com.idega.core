/*
 * $Id: IWResourceBundle.java,v 1.39 2006/05/11 16:59:54 tryggvil Exp $
 *
 * Copyright (C) 2001-2005 Idega hf. All Rights Reserved.
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
import java.text.MessageFormat;
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
 * <p>
 * This is an idegaWeb representation of a localization folder/file for each locale in an idegaWeb Bundle.<br/>
 * There is an instance of this class for each localization file (e.g. com.idega.core.bundle/en.locale/Localized.strings)
 * and is an extension to the standard Java ResourceBundle.
 * </p>
 * Last modified: $Date: 2006/05/11 16:59:54 $ by $Author: tryggvil $<br/>
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.39 $
 */
public class IWResourceBundle extends ResourceBundle {

	// ==================privates====================
	TreeMap lookup;
	private Properties properties = new SortedProperties();
	//private Properties properties = new Properties();
	private Locale locale;
	private File file;
	private IWBundle iwBundleParent;
	private String resourcesURL;
	private static String slash = "/";
	//private IWResourceBundle parentResourceBundle;

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
			this.properties.load(new FileInputStream(file));
		}
		catch (FileNotFoundException e) {
			//System.err.println("IWResourceBundle: File Not Found:"+file.getAbsolutePath());
		}
		this.lookup = new TreeMap(this.properties);
		setResourcesURL(parent.getResourcesVirtualPath() + "/" + locale.toString() + ".locale");
	}
	
	/**
	 * <p>
	 * This constructor is used for locale variants, and the parent resourceBundle includes the default localizations.
	 * </p>
	 */
	public IWResourceBundle(IWResourceBundle parent, File file, Locale locale) throws IOException {
		this(parent.getIWBundleParent(),file,locale);
		setParent(parent);
	}
	
	/**
	 * Override of ResourceBundle, same semantics
	 */
	public Object handleGetObject(String key) {
		if (this.lookup != null) {
			Object obj = this.lookup.get(key);
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
		if (this.parent != null) {
			Iterator iter = this.lookup.keySet().iterator();
			final Enumeration myKeys = new EnumerationIteratorWrapper(iter);
			final Enumeration parentKeys = this.parent.getKeys();

			result = new Enumeration() {
				public boolean hasMoreElements() {
					if (this.temp == null) {
						nextElement();
					}
					return this.temp != null;
				}

				public Object nextElement() {
					Object returnVal = this.temp;
					if (myKeys.hasMoreElements()) {
						this.temp = myKeys.nextElement();
					}
					else {
						this.temp = null;
						while (this.temp == null && parentKeys.hasMoreElements()) {
							this.temp = parentKeys.nextElement();
							if (IWResourceBundle.this.lookup.containsKey(this.temp)) {
								this.temp = null;
							}
						}
					}
					return returnVal;
				}

				Object temp = null;
			};
		}
		else {
			Iterator iter = this.lookup.keySet().iterator();
			result = new EnumerationIteratorWrapper(iter);
		}

		return result;
	}

	public Locale getLocale() {
		return this.locale;
	}

	private void setLocale(Locale locale) {
		this.locale = locale;
	}

	public synchronized void storeState() {
		try {
			this.properties.clear();
			if (this.lookup != null) {
				Iterator iter = this.lookup.keySet().iterator();
				while (iter.hasNext()) {
					Object key = iter.next();
					if (key != null) {
						Object value = this.lookup.get(key);
						if (value != null) {
							this.properties.put(key, value);
						}
					}
				}
				if(!this.file.exists()){
					file.createNewFile();
					System.out.println("IWResourceBundle: Created new file: "+file.getAbsolutePath());
				}
				FileOutputStream fos = new FileOutputStream(this.file);
				this.properties.store(fos, null);
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
 * @param key
 * @return
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
 *  Gets a localized stringvalue and sets the value as returnValueIfNotFound if it is previously not found and returns it.
 * @param key
 * @param returnValueIfNotFound
 * @return
 */
	public String getLocalizedString(String key, String returnValueIfNotFound) {
		String returnString = getLocalizedString(key);
		if ( ( (returnString == null) || StringHandler.EMPTY_STRING.equals(returnString) ) && returnValueIfNotFound!=null ) {//null check on return value IS necessary
			if (IWMainApplicationSettings.isAutoCreateStringsActive()) {
				//if (getIWBundleParent().getApplication().getSettings().isDebugActive())
				//	System.out.println("Storing localized string: " + key);
				//setLocalizedString(key, returnValueIfNotFound);
				this.checkBundleLocalizedString(key,returnValueIfNotFound);
			}
			return returnValueIfNotFound;
		}
		else {
			return returnString;
		}
	}
	
	/**
	 * 	 * Gets a localized stringvalue and sets the value as returnValueIfNotFound if it is previously not found and <br>
	 * THEN formats the string using java.text.MessageFormat.format(thestring,arrayofvariables).<br>
	 * The variables in the array then replace varibles in the localized string <br>
	 * For example: the localized string : "Hello my name is {0}" and the array contains object that implements the toString() method.<br>
	 * When we call java.text.MessageFormat.format(thestring,arrayofvariables) the variable {0} is then replaced with the arrayofvariables[0].toString() item of the array.<br>
	 * @param key
	 * @param returnValueIfNotFound
	 * @param messageFormatVariables an Object array of .toString() implementing objects
	 * @return the string localized and formatted
	 */
	public String getLocalizedAndFormattedString(String key, String returnValueIfNotFound, Object[] messageFormatVariables) {
		String localizedAndFormatted = getLocalizedString(key, returnValueIfNotFound);
		
		if(messageFormatVariables!=null){
			localizedAndFormatted = MessageFormat.format(localizedAndFormatted, messageFormatVariables);
		}
		
		return localizedAndFormatted;
	}

	/**
	* Uses getLocalizedString but returns null if resource is not found
	*/
	public Image getLocalizedImageButton(String key) {
		try {
			String text = getLocalizedString(key);
			return this.iwBundleParent.getApplication().getImageFactory().createButton(text, this.iwBundleParent, getLocale());
		}
		catch (MissingResourceException e) {
			return null;
		}
	}

	public Image getLocalizedImageButton(String key, String returnValueIfNull) {
		String text = getLocalizedString(key, returnValueIfNull);
		return this.iwBundleParent.getApplication().getImageFactory().createButton(text, this.iwBundleParent, getLocale());
	}

	/**
	  * Uses getLocalizedString but returns null if resource is not found
	  */
	public Image getLocalizedImageTab(String key, boolean flip) {
		try {
			String text = getLocalizedString(key);
			return this.iwBundleParent.getApplication().getImageFactory().createTab(text, this.iwBundleParent, getLocale(), flip);
		}
		catch (MissingResourceException e) {
			return null;
		}
	}

	public Image getLocalizedImageTab(String key, String returnValueIfNull, boolean flip) {
		String text = getLocalizedString(key, returnValueIfNull);
		return this.iwBundleParent.getApplication().getImageFactory().createTab(text, this.iwBundleParent, getLocale(), flip);
	}

	/**
	 * Sets a value of a string key in this ResourceBundle. Stores the files implicitly (storeState()) to the diskafter a call to this method.
	 * @param key a String key
	 * @param value a value to the key
	 */
	public void setString(String key, String value) {
		this.lookup.put(key, value);
		checkBundleLocalizedString(key,value);
		this.storeState();
	}

	public boolean removeString(String key) {
		if((String) this.lookup.remove(key) != null ){
			this.storeState();
			return true;
		}
		return false;
	}

	private void setIWBundleParent(IWBundle parent) {
		this.iwBundleParent = parent;
	}

	public IWBundle getIWBundleParent() {
		return this.iwBundleParent;
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
		this.resourcesURL = url;
	}

	public String getResourcesURL() {
		return this.resourcesURL;
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
			try{
				((DefaultIWBundle)bundle).storeLocalizableStrings();
			}
			catch(ClassCastException ce){
				System.err.println("Cant store LocalizableStrings becauase bundle "+bundle.getBundleIdentifier()+" is not subclass of DefaultIWBundle");
				ce.printStackTrace();
			}
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

	
	/**
	 * @return the parentResourceBundle
	 */
	public IWResourceBundle getParentResourceBundle() {
		return (IWResourceBundle)parent;
	}

	
	/**
	 * @param parentResourceBundle the parentResourceBundle to set
	 */
	public void setParentResourceBundle(IWResourceBundle parentResourceBundle) {
		super.setParent(parentResourceBundle);
	}
}