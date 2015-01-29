/*
 * $Id: IWResourceBundle.java,v 1.55 2009/05/27 09:45:29 laddi Exp $
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.exception.IWBundleDoesNotExist;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.EnumerationIteratorWrapper;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.SortedProperties;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
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
 * Last modified: $Date: 2009/05/27 09:45:29 $ by $Author: laddi $<br/>
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.55 $
 */

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IWResourceBundle extends ResourceBundle implements MessageResource, Serializable {

	private static final long serialVersionUID = 2495736573750344100L;

	private static final Logger LOGGER = Logger.getLogger(IWResourceBundle.class.getName());

	public static final String RESOURCE_IDENTIFIER = "bundle_resource";

	// ==================privates====================
	private Map<String, String> lookup;
	private Properties properties;
	private Locale locale;
	private File file;
	private IWBundle iwBundleParent;
	private String resourcesURL;

	private String identifier;
	private Level usagePriorityLevel;
	private boolean autoInsert;
	private String bundleIdentifier;

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
	 * @param parent
	 * @param file
	 * @param locale
	 * @throws IOException
	 */
	protected void initialize(IWBundle parent, InputStream streamForRead, File file, Locale locale) throws IOException {
		setIWBundleParent(parent);
		setLocale(locale);
		this.file = file;

		String content = null;
		Reader reader = null;
		try {
			this.properties = new SortedProperties();
			content = StringHandler.getContentFromInputStream(streamForRead);
			if (content != null) {
				reader = new StringReader(content);
				this.properties.load(reader);
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error initializing bundle " + getBundleIdentifier(), e);
		} finally {
			IOUtil.close(reader);
		}

		synchronized (this) {
			this.lookup = new TreeMap<String, String>();
			for (Entry<Object, Object> entry: this.properties.entrySet()) {
				lookup.put(entry.getKey().toString(), entry.getValue().toString());
			}

			doMakeSureAllFilesLoaded(lookup);
		}

		setResourcesURL(parent.getResourcesVirtualPath() + CoreConstants.SLASH + locale.toString() + ".locale");
	}

	@Override
	public void initialize(String bundleIdentifier, Locale newLocale) throws IOException, OperationNotSupportedException {
		if (bundleIdentifier == null || bundleIdentifier.equals(MessageResource.NO_BUNDLE)) {
			throw new OperationNotSupportedException("Empty bundle is not supported supported");
		} else {
			setBundleIdentifier(bundleIdentifier);
			IWContext iwc = IWContext.getCurrentInstance();

			if (newLocale == null)
				newLocale = iwc.getCurrentLocale();

			IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);

			if (IWMainApplicationSettings.isAutoCreateStringsActive()) {
				file = FileUtil.getFileAndCreateIfNotExists(bundle.getResourcesRealPath(newLocale), getLocalizedStringsFileName());
			} else {
				file = new File(bundle.getResourcesRealPath(newLocale), getLocalizedStringsFileName());
			}

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
		} else {
			IWBundle parent = getIWBundleParent();
			if (parent != null) {
				throw new IWBundleDoesNotExist(parent.getBundleIdentifier());
			} else {
				throw new IWBundleDoesNotExist();
			}
		}
	}

	/**
	 * Implementation of ResourceBundle.getKeys.
	 */
	@Override
	public Enumeration<String> getKeys() {
		Enumeration<String> result = null;
		if (this.parent != null) {
			Iterator<String> iter = this.lookup.keySet().iterator();
			final Enumeration<String> myKeys = new EnumerationIteratorWrapper<String>(iter);
			final Enumeration<String> parentKeys = this.parent.getKeys();

			result = new Enumeration<String>() {

				@Override
				public boolean hasMoreElements() {
					if (this.temp == null) {
						nextElement();
					}
					return this.temp != null;
				}

				@Override
				public String nextElement() {
					String returnVal = this.temp;
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

				String temp = null;
			};
		} else {
			Iterator<String> iter = this.lookup.keySet().iterator();
			result = new EnumerationIteratorWrapper<String>(iter);
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

	@Override
	public void store() {
		storeState();
	}

	public void storeState() {
		if (file != null) {
			try {
				this.properties.clear();
				if (this.lookup != null) {
					for (String key: this.lookup.keySet()) {
						if (key != null) {
							Object value = this.lookup.get(key);
							if (value != null) {
								this.properties.put(key, value);
							}
						}
					}
					if (!this.file.exists()) {
						this.file.createNewFile();
						LOGGER.info("Created new file: " + this.file.getAbsolutePath());
					}
					FileOutputStream fos = new FileOutputStream(this.file);
					this.properties.store(fos, null);
					fos.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			LOGGER.warning("Cannot save " + getBundleIdentifier() + ", was nat read from file ");
		}
	}

	/**
	 * Uses factory to get localizedMessage from all available message resources
	 * @deprecated IWMainApplication.getLocalizedStringMessage should be used instead
	 * @param key
	 * @return found string
	 */
	@Deprecated
	public String getLocalizedString(String key) {
		String bundleIdentifier = getIWBundleParent().getBundleIdentifier();
		Locale locale = this.locale == null ? CoreUtil.getIWContext().getCurrentLocale() : this.locale;
		return getIWBundleParent().getApplication().getLocalizedStringMessage(key, null, bundleIdentifier, locale);
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
		} catch (MissingResourceException e) {
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
		return iwma.getLocalizedStringMessage(key, returnValueIfNotFound, bundleIdentifier, locale);
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
		String localization = getBundleLocalizedString(key);
		if (StringUtil.isEmpty(localization)) {
			IWBundle bundle = getIWBundleParent();
			String defaultLocalization = bundle.getLocalizableStringDefaultValue(key);

			if (StringUtil.isEmpty(defaultLocalization) && returnValueIfNotFound != null) {
				return returnValueIfNotFound;
			} else {
				return defaultLocalization;
			}
		} else {
			return localization;
		}
	}

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
		} catch (MissingResourceException e) {
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
		} catch (MissingResourceException e) {
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
			} catch (ClassCastException ce) {
				LOGGER.log(Level.WARNING, "Cant store LocalizableStrings becauase bundle " + bundle.getBundleIdentifier() +
						" is not subclass of DefaultIWBundle", ce);
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		IWBundle bundleParent = this.getIWBundleParent();
		if (bundleParent != null) {
			return bundleParent + CoreConstants.SLASH + this.locale;
		} else {
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

	protected Map<String, String> getLookup() {
		return lookup;
	}

	protected void setLookup(Map<String, String> lookup) {
		this.lookup = lookup;
	}

	protected Properties getProperties() {
		return properties;
	}

	/**
	 * @return object that was found in resource or null if nothing is found
	 */
	@Override
	public String getMessage(String key) {
		return getBundleLocalizedString(String.valueOf(key), null);
	}

	/**
	 * @return object that was set or null if there was a failure setting object
	 */
	@Override
	public String setMessage(String key, String value) {
		try {
			setString(key, value);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setMessages(Map<String, String> values) {
		try {
			Map<String, String> stringMap = new HashMap<String, String>();

			for (Object key : values.keySet())
				stringMap.put(String.valueOf(key), String.valueOf(values.get(key)));

			setStrings(stringMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//TODO use IWBundle class method
	private String getLocalizedStringsFileName(){
		return "Localized.strings";
	}

	@Override
	public Set<String> getAllLocalizedKeys() {
		if (getBundleIdentifier() != null && !getBundleIdentifier().equals(MessageResource.NO_BUNDLE)) {
			IWBundle bundle = getIWMainApplication().getBundle(getBundleIdentifier());
			String[] str = bundle.getLocalizableStrings();
			return new TreeSet<String>(Arrays.asList(str));
		} else {
			doMakeSureAllFilesLoaded(getLookup());
			return getLookup().keySet();
		}
	}

	private void doMakeSureAllFilesLoaded(Map<String, String> data) {
		String file = null;
		try {
			String identifier = getBundleIdentifier();
			if (StringUtil.isEmpty(identifier)) {
				return;
			}

			file = "resources/" + getLocale() + ".locale/" + getLocalizedStringsFileName();
			InputStream stream = IOUtil.getStreamFromJar(getBundleIdentifier(), file);
			String content = StringHandler.getContentFromInputStream(stream);
			if (StringUtil.isEmpty(content)) {
				LOGGER.warning("No content in " + getBundleIdentifier() + ", file: " + file + " for " + locale);
				return;
			}

			List<String> lines = StringUtil.getLinesFromString(content);
			if (ListUtil.isEmpty(lines)) {
				LOGGER.warning("No lines in " + getBundleIdentifier() + ", file: " + file + " for " + locale + ", content: " + content);
				return;
			}

			List<String> newKeys = new ArrayList<String>();
			for (String line: lines) {
				if (StringUtil.isEmpty(line)) {
					continue;
				}

				String[] parts = line.split(CoreConstants.EQ);
				if (ArrayUtil.isEmpty(parts) || parts.length != 2) {
					continue;
				}

				String key = parts[0];
				if (!data.containsKey(key)) {
					setString(key, parts[1]);
					newKeys.add(key);
				}
			}
			if (!ListUtil.isEmpty(newKeys)) {
				LOGGER.info("Found missing keys in " + getBundleIdentifier() + ", file: " + file + " for " + locale + "\n" + newKeys);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error for checking missing keys in JAR file of " + getBundleIdentifier() + ", file: " + file + " for " + locale, e);
		}
	}

	@Override
	public void removeMessage(String key) {
		getLookup().remove(key);
		this.storeState();
	}

	private IWMainApplication getIWMainApplication() {
		IWContext iwc = IWContext.getCurrentInstance();
		IWMainApplication iwma = iwc == null ? IWMainApplication.getDefaultIWMainApplication() : iwc.getApplicationContext().getIWMainApplication();
		return iwma;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public Level getLevel() {
		return usagePriorityLevel;
	}

	@Override
	public boolean isAutoInsert() {
		return autoInsert;
	}

	@Override
	public void setAutoInsert(boolean value) {
		autoInsert = value;
	}

	@Override
	public void setLevel(Level priorityLevel) {
		usagePriorityLevel = priorityLevel;
	}

	@Override
	public String getBundleIdentifier() {
		return bundleIdentifier;
	}

	@Override
	public void setBundleIdentifier(String bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	}
}