/*
 * $Id: IWResourceBundle.java,v 1.54 2009/03/11 10:06:04 civilis Exp $
 * 
 * Copyright (C) 2001-2005 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.exception.IWBundleDoesNotExist;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.EnumerationIteratorWrapper;
import com.idega.util.FileUtil;
import com.idega.util.SortedProperties;
import com.idega.util.StringHandler;
import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceImportanceLevel;

/**
 * <p>
 * This is an idegaWeb representation of a localization folder/file for each
 * locale in an idegaWeb Bundle.<br/> There is an instance of this class for
 * each localization file (e.g.
 * com.idega.core.bundle/en.locale/Localized.strings) and is an extension to the
 * standard Java ResourceBundle.
 * </p>
 * Last modified: $Date: 2009/03/11 10:06:04 $ by $Author: civilis $<br/>
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.54 $
 */

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IWResourceBundle extends ResourceBundle implements MessageResource {

	public static final String RESOURCE_IDENTIFIER = "bundle_resource";
	
//	private static final String AUTO_INSERT_PROPERTY = RESOURCE_IDENTIFIER + "_autoinsert";
//	private static final String PRIORITY_PROPERTY = RESOURCE_IDENTIFIER + "_property";
	
	// ==================privates====================
	Map<String, String> lookup;
	private Properties properties;
	// private Properties properties = new Properties();
	private Locale locale;
	private File file;
	private IWBundle iwBundleParent;
	private String resourcesURL;
	
	private String identifier;
	private Level usagePriorityLevel;
	private boolean autoInsert;
	private String bundleIdentifier;

	// private IWResourceBundle parentResourceBundle;
	
	
	/**
	 * Empty constructor for use in extending classes
	 * 
	 */
	public IWResourceBundle() {
		initProperities();
	}

	/**
	 * Creates a IWResourceBundle for a specific Locale
	 * 
	 * @param file
	 *          file to read from.
	 * @param parent
	 *          Parent IWBundle to instanciate from
	 * @param locale
	 *          Locale to create from
	 */
	public IWResourceBundle(IWBundle parent, File file, Locale locale) throws IOException {
		this();
		initialize(parent, new FileInputStream(file), file, locale);
	}
	
	public IWResourceBundle(IWBundle parent, InputStream stream, Locale locale) throws IOException {
		this();
		initialize(parent,stream,null,locale);
	}

	/**
	 * <p>
	 * This constructor is used for locale variants, and the parent resourceBundle
	 * includes the default localizations.
	 * </p>
	 */
	public IWResourceBundle(IWResourceBundle parent, File file, Locale locale) throws IOException {
		this(parent.getIWBundleParent(), file, locale);
		setParent(parent);
	}

	/**
	 * <p>
	 * This constructor is used for locale variants, and the parent resourceBundle
	 * includes the default localizations.
	 * </p>
	 */
	public IWResourceBundle(IWResourceBundle parent, InputStream inputStream, Locale locale) throws IOException {
		this(parent.getIWBundleParent(), inputStream, locale);
		setParent(parent);
	}
	
	protected void initProperities() {
		setIdentifier(RESOURCE_IDENTIFIER);
		setLevel(MessageResourceImportanceLevel.LAST_ORDER);
		setAutoInsert(false);
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method initialize
	 * </p>
	 * @param parent
	 * @param file
	 * @param locale
	 * @throws IOException
	 */
	protected void initialize(IWBundle parent, InputStream streamForRead, File file, Locale locale) throws IOException {
		setIWBundleParent(parent);
		setLocale(locale);
		this.file = file;
		try {
			this.properties = new SortedProperties();
			this.properties.load(streamForRead);
		}
		catch (FileNotFoundException e) {
			// System.err.println("IWResourceBundle: File Not
			// Found:"+file.getAbsolutePath());
		}
		this.lookup = new TreeMap(this.properties);
		setResourcesURL(parent.getResourcesVirtualPath() + "/" + locale.toString() + ".locale");
	}
	
	public void initialize(String bundleIdentifier, Locale newLocale) throws IOException, OperationNotSupportedException {
		if(bundleIdentifier == null || bundleIdentifier.equals(MessageResource.NO_BUNDLE)) {
			throw new OperationNotSupportedException("Empty bundle is not supported supported");
		} else {
			setBundleIdentifier(bundleIdentifier);
			IWContext iwc = IWContext.getCurrentInstance();
	
			if(newLocale == null)
				newLocale = iwc.getCurrentLocale();
			
			IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
	
			if (IWMainApplicationSettings.isAutoCreateStringsActive()) {
				file = FileUtil.getFileAndCreateIfNotExists(bundle.getResourcesRealPath(newLocale), getLocalizedStringsFileName());
			}
			else {
				file = new File(bundle.getResourcesRealPath(newLocale), getLocalizedStringsFileName());
			}
			
//			setAutoInsert(iwc.getApplicationSettings().getBoolean(AUTO_INSERT_PROPERTY, false));
	
			initialize(bundle, new FileInputStream(file), file, newLocale);
		}
		
	}

	/**
	 * Override of ResourceBundle, same semantics
	 */
	@Override
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
	@SuppressWarnings("unchecked")
	@Override
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

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	protected void setLocale(Locale locale) {
		this.locale = locale;
	}

	public synchronized void storeState() {
		if(file!=null){
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
					if (!this.file.exists()) {
						this.file.createNewFile();
						System.out.println("IWResourceBundle: Created new file: " + this.file.getAbsolutePath());
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
		else{
			System.out.println("IWResourceBundle: Cannot save, was nat read from file ");
		}
	}

	/**
	 * Uses factory to get localisedMessage from all available message resources
	 * @deprecated IWMainApplication.getLocalisedStringMessage should be used instead
	 * @param key
	 * @return found string 
	 */
	@Deprecated
	public String getLocalizedString(String key) {
		String bundleIdentifier = getIWBundleParent().getBundleIdentifier();
		Locale locale = CoreUtil.getIWContext().getCurrentLocale();
		return getIWBundleParent().getApplication().getLocalisedStringMessage(key, null, bundleIdentifier, locale);
	}
	
	/**
	 * Uses getString but returns null if resource is not found
	 * 
	 * @param key
	 * @return
	 */
	protected String getBundleLocalizedString(String key) {

		try {
			return super.getString(key);
		}
		catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	 * Gets a localized string value and sets the value as returnValueIfNotFound if it is previously not found and returns it. <br/>
	 * However if e.g. an english version does not exist for the english local the value from Localizable.strings is used, unless it is null or empty then returnValueIfNotFound is used<br/>
	 * 
	 * @param key
	 * @param returnValueIfNotFound
	 * @return a string localized in the IWRB locale or the default value from Localizable.strings or the returnValueIfNotFound if that is null or empty. 
	 */
	public String getLocalizedString(String key, String returnValueIfNotFound) {
		return getLocalizedString(locale == null ? CoreUtil.getIWContext().getCurrentLocale() : locale, key, returnValueIfNotFound);
	}
	
	public String getLocalizedString(Locale locale, String key, String returnValueIfNotFound) {
		String bundleIdentifier = getIWBundleParent().getBundleIdentifier();
		IWMainApplication iwma = IWMainApplication.defaultIWMainApplication;
		return iwma.getLocalisedStringMessage(key, returnValueIfNotFound, bundleIdentifier, locale);
	}
	
	/**
	 * Gets a localized string value and sets the value as returnValueIfNotFound if it is previously not found and returns it. <br/>
	 * However if e.g. an english version does not exist for the english local the value from Localizable.strings is used, unless it is null or empty then returnValueIfNotFound is used<br/>
	 * 
	 * @param key
	 * @param returnValueIfNotFound
	 * @return a string localized in the IWRB locale or the default value from Localizable.strings or the returnValueIfNotFound if that is null or empty. 
	 */
	private String getBundleLocalizedString(String key, String returnValueIfNotFound) {
		String returnString = getBundleLocalizedString(key);
		if (((returnString == null) || StringHandler.EMPTY_STRING.equals(returnString))) {
			IWBundle bundle = getIWBundleParent();
			String value = bundle.getLocalizableStringDefaultValue(key);
			
			if( value==null || ("".equals(value) && (returnValueIfNotFound!=null)) ){
				return returnValueIfNotFound;
			}
			else{
				return value;
			}
		} else {
			return returnString;
		}
	}
	
//	private String getBundleLocalizedString(String key, String returnValueIfNotFound) {
//		String returnString = getBundleLocalizedString(key);
//		if (((returnString == null) || StringHandler.EMPTY_STRING.equals(returnString)) && returnValueIfNotFound != null) {// null IS necessary
//			if (IWMainApplicationSettings.isAutoCreateStringsActive()) {
//
//				boolean hadToCreate = this.checkBundleLocalizedString(key, returnValueIfNotFound);
//				
//				//Localizable.strings always wins unless
//				if(!hadToCreate){
//					IWBundle bundle = getIWBundleParent();
//					String value = bundle.getLocalizableStringDefaultValue(key);
//					if( value==null || ("".equals(value) && (returnValueIfNotFound!=null)) ){
//						return returnValueIfNotFound;
//					}
//					else{
//						return value;
//					}
//				}
//				
//			}
//			return returnValueIfNotFound;
//		}
//		else {
//			return returnString;
//		}
//	}
	
	
//	public String getLocalizedString(String key, String returnValueIfNotFound) {
//		String bundleIdentifier = getIWBundleParent().getBundleIdentifier();
//		return getIWBundleParent().getApplication().getLocalisedStringMessage(key, returnValueIfNotFound, bundleIdentifier);
//	}

	/**
	 * * Gets a localized stringvalue and sets the value as returnValueIfNotFound
	 * if it is previously not found and <br>
	 * THEN formats the string using
	 * java.text.MessageFormat.format(thestring,arrayofvariables).<br>
	 * The variables in the array then replace varibles in the localized string
	 * <br>
	 * For example: the localized string : "Hello my name is {0}" and the array
	 * contains object that implements the toString() method.<br>
	 * When we call java.text.MessageFormat.format(thestring,arrayofvariables) the
	 * variable {0} is then replaced with the arrayofvariables[0].toString() item
	 * of the array.<br>
	 * 
	 * @param key
	 * @param returnValueIfNotFound
	 * @param messageFormatVariables
	 *          an Object array of .toString() implementing objects
	 * @return the string localized and formatted
	 */
	public String getLocalizedAndFormattedString(String key, String returnValueIfNotFound, Object[] messageFormatVariables) {
		String localizedAndFormatted = getLocalizedString(key, returnValueIfNotFound);

		if (messageFormatVariables != null) {
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
	 * Sets a value of a string key in this ResourceBundle. Stores the files
	 * implicitly (storeState()) to the diskafter a call to this method.
	 * 
	 * @param key
	 *          a String key
	 * @param value
	 *          a value to the key
	 */
	public void setString(String key, String value) {
		this.lookup.put(key, value);
		checkBundleLocalizedString(key, value);
		storeState();
	}
	
	public void setStrings(Map<String, String> values) {
		for(String key : values.keySet()) {
			this.lookup.put(key, values.get(key));
			checkBundleLocalizedString(key, values.get(key));
		}
		storeState();
	}

	public boolean removeString(String key) {
		if (this.lookup.remove(key) != null) {
			storeState();
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
	 * Returns the Resource URI for the image with the internal url urlInBundle
	 * inside the resource bundle
	 */
	public String getImageURI(String urlInBundle) {
		StringBuffer buf = new StringBuffer(getResourcesURL());
		if(!urlInBundle.startsWith(CoreConstants.SLASH)){
			buf.append(CoreConstants.SLASH);
		}
		buf.append(urlInBundle);
	    return buf.toString();
	}

	public Image getImage(String urlInBundle) {
		return new Image(getImageURI(urlInBundle));
	}

	public Image getImage(String urlInBundle, int width, int height) {
		return getImage(urlInBundle, "", width, height);
	}

	public Image getImage(String urlInBundle, String name, int width, int height) {
		return new Image(getResourcesURL() + CoreConstants.SLASH + urlInBundle, name, width, height);
	}

	public Image getImage(String urlInBundle, String overUrlInBundle, String imageName, String overImageName) {
		return new Image(imageName, getResourcesURL() + CoreConstants.SLASH + urlInBundle, getResourcesURL() + CoreConstants.SLASH + overUrlInBundle);
	}

	public Image getImage(String urlInBundle, String overUrlInBundle, String name, int width, int height) {
		Image returnImage = new Image(name, getResourcesURL() + CoreConstants.SLASH + urlInBundle, getResourcesURL() + CoreConstants.SLASH + overUrlInBundle);
		returnImage.setWidth(width);
		returnImage.setHeight(height);
		return returnImage;
	}

	public Image getImage(String urlInBundle, String name) {
		return new Image(getResourcesURL() + CoreConstants.SLASH + urlInBundle, name);
	}

	public Image getImage(String urlInBundle, String key, String defaultKeyValue) {
		return new Image(getResourcesURL() + CoreConstants.SLASH + urlInBundle, getLocalizedString(key, defaultKeyValue));
	}

	private void setResourcesURL(String url) {
		this.resourcesURL = url;
	}

	public String getResourcesURL() {
		return this.resourcesURL;
	}

	public void setLocalizedString(String key, String value) {
		this.setString(key, value);
	}

	protected boolean checkBundleLocalizedString(String key, String value) {
		IWBundle bundle = getIWBundleParent();
		if (!bundle.containsLocalizedString(key)) {
			bundle.addLocalizableString(key, value);
			try {
				((DefaultIWBundle) bundle).storeLocalizableStrings();
			}
			catch (ClassCastException ce) {
				System.err.println("Cant store LocalizableStrings becauase bundle " + bundle.getBundleIdentifier() + " is not subclass of DefaultIWBundle");
				ce.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		IWBundle bundleParent = this.getIWBundleParent();
		if (bundleParent != null) {
			return bundleParent + "/" + this.locale;
		}
		else {
			return this.locale.toString();
		}
	}

	/**
	 * @return the parentResourceBundle
	 */
	public IWResourceBundle getParentResourceBundle() {
		return (IWResourceBundle) this.parent;
	}

	/**
	 * @param parentResourceBundle
	 *          the parentResourceBundle to set
	 */
	public void setParentResourceBundle(IWResourceBundle parentResourceBundle) {
		super.setParent(parentResourceBundle);
	}

	@SuppressWarnings("unchecked")
	protected Map getLookup() {
		return lookup;
	}

	@SuppressWarnings("unchecked")
	protected void setLookup(TreeMap lookup) {
		this.lookup = lookup;
	}
	
	protected Properties getProperties() {
		return properties;
	}

	/**
	 * @return object that was found in resource or null if nothing is found
	 */
	public Object getMessage(Object key) {
		return getBundleLocalizedString(String.valueOf(key), null);
	}
	



	/**
	 * @return object that was set or null if there was a failure setting object
	 */
	public Object setMessage(Object key, Object value) {
		try {
			setString(String.valueOf(key), String.valueOf(value));
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	public void setMessages(Map<Object, Object> values) {
		try {
			Map<String, String> stringMap = new HashMap<String, String>();

			for(Object key : values.keySet()) {
				stringMap.put(String.valueOf(key), String.valueOf(values.get(key)));
			}
			setStrings(stringMap);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	//TODO use IWBundle class method
	private String getLocalizedStringsFileName(){
		return "Localized.strings";
	}

	@SuppressWarnings("unchecked")
	public Set<Object> getAllLocalisedKeys() {
		if(/*DefaultIWBundle.isProductionEnvironment() && */!getBundleIdentifier().equals(MessageResource.NO_BUNDLE)) {
			IWBundle bundle = getIWMainApplication().getBundle(getBundleIdentifier());
			String[] str = bundle.getLocalizableStrings();
			return new TreeSet(Arrays.asList(str));
		} else {
			return getLookup().keySet();
		}
	}
	
	public void removeMessage(Object key) {
//		try {
//			initialize(bundleIdentifier, locale);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		getLookup().remove(key);
		this.storeState();
	}
	
	private IWMainApplication getIWMainApplication() {
		IWContext iwc = IWContext.getCurrentInstance();
		IWMainApplication iwma = iwc == null ? IWMainApplication.getDefaultIWMainApplication() : iwc.getApplicationContext().getIWMainApplication();
		return iwma;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Level getLevel() {
		return usagePriorityLevel;
	}

	public boolean isAutoInsert() {
		return autoInsert;
	}

	public void setAutoInsert(boolean value) {
		autoInsert = value;
	}

	public void setLevel(Level priorityLevel) {
		usagePriorityLevel = priorityLevel;
	}

	public String getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setBundleIdentifier(String bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}
}