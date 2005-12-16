/*
 * $Id: IWMainApplicationSettings.java,v 1.33 2005/12/16 12:43:15 thomas Exp $
 * Created in 2001 by Tryggvi Larusson
 * 
 * Copyright (C) 2001-2005 Idega software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Logger;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.business.ICApplicationBindingBusiness;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.EntityControl;
import com.idega.presentation.Page;
import com.idega.repository.data.MutableClass;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.LocaleUtil;
/**
 * <p>
 * This class is used by IWMainApplication as the holder of most properties set
 * on an application-wide basis. This class is responsible for reading the properties
 * set in idegaweb.pxml and also holds some default values that don't have to be 
 * explicitly set in the idegaweb.pxml properties file.
 * </p>
 * Copyright: Copyright (c) 2001-2005 idega software<br/>
 * Last modified: $Date: 2005/12/16 12:43:15 $ by $Author: thomas $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.33 $
 */


public class IWMainApplicationSettings implements MutableClass {
	private static final String CHARACTER_ENCODING_KEY = "character_encoding";
	private static final String IDO_ENTITY_BEAN_CACHING_KEY = "ido_entity_bean_caching";
	private static final String IDO_ENTITY_QUERY_CACHING_KEY = "ido_entity_query_caching";
	public static final String IW_POOLMANAGER_TYPE = "iw_poolmanager";
	public static final String AUTO_CREATE_LOCALIZED_STRINGS_KEY="auto-create-localized-strings";
	public static final String AUTO_CREATE_PROPERTIES_KEY="auto-create-properties";
	public static final String DEFAULT_MARKUP_LANGUAGE_KEY="markup_language";

	public final static String DEFAULT_MARKUP_LANGUAGE = Page.XHTML;
	
	private static String DEFAULT_CHARACTER_ENCODING; //= "ISO-8859-1";
	//public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	
	private static String DEFAULT_TEMPLATE_NAME;
	private static String DEFAULT_TEMPLATE_CLASS;
	private static String DEFAULT_FONT;
	private static String DEFAULT_FONT_SIZE;
	private static String DEFAULT_LOCALE_KEY;
	private static String _SERVICE_CLASSES_KEY;
		
	public static boolean DEBUG_FLAG = false;
	public static boolean CREATE_STRINGS = true;
	public static boolean CREATE_PROPERTIES = true;
	
	static {
		// initialize all values
		initializeClassVariables();
	}
	
	public static void unload() {
		initializeClassVariables();
	}
	
	private static void initializeClassVariables() {
		DEBUG_FLAG = false;
		CREATE_STRINGS = true;
		CREATE_PROPERTIES = true;
		DEFAULT_CHARACTER_ENCODING = null;
		DEFAULT_TEMPLATE_NAME = "defaulttemplatename";
		DEFAULT_TEMPLATE_CLASS = "defaulttemplateclass";
		DEFAULT_FONT = "defaultfont";
		DEFAULT_FONT_SIZE = "defaultfontsize";
		DEFAULT_LOCALE_KEY = "defaultlocale";
		_SERVICE_CLASSES_KEY = "iw_service_class_key";
	}
	
	static private Logger getLogger(){
		 return Logger.getLogger(IWMainApplicationSettings.class.getName());
	 }
	
	//instance variables:
	private IWMainApplication application = null;
	private Locale cachedDefaultLocale = null;
	private ICApplicationBindingBusiness applicationBindingBusiness = null;
	
	public IWMainApplicationSettings(IWMainApplication application) {
		this.application=application;	
	}
	
	private IWMainApplication getApplication(){
		return application;
	}
	
	/**
	 * old method, propably not used at all
	 */
	public void setDefaultTemplate(String templateName, String classname) {
		putInApplicationBinding(DEFAULT_TEMPLATE_NAME, templateName);
		putInApplicationBinding(DEFAULT_TEMPLATE_CLASS, classname);
	}
	
	/**
	 * old method, propably not used at all
	 */
	public String getDefaultTemplateName() {
		return getFromApplicationBinding(DEFAULT_TEMPLATE_NAME);
	}
	
	/**
	 * old method, propably not used at all
	 */
	public String getDefaultTemplateClass() {
		return getFromApplicationBinding(DEFAULT_TEMPLATE_CLASS);
	}
	
	/**
	 * old method, propably not used at all
	 */
	public String getDefaultFont() {
		return getFromApplicationBinding(DEFAULT_FONT);
	}
	
	/**
	 * old method, propably not used at all
	 */
	public void setDefaultFont(String fontname) {
		putInApplicationBinding(DEFAULT_FONT, fontname);
	}
	
	/**
	 * old method, propably not used at all
	 */
	public int getDefaultFontSize() {
		return Integer.parseInt(getFromApplicationBinding(DEFAULT_FONT_SIZE));
	}
	
	/**
	 * old method, propably not used at all
	 */
	public void setDefaultFontSize(int size) {
		putInApplicationBinding(DEFAULT_FONT_SIZE, Integer.toString(size));
	}
	
	public void setDefaultLocale(Locale locale) {
		String sLocale = locale.toString();
		putInApplicationBinding(DEFAULT_LOCALE_KEY,sLocale);
		cachedDefaultLocale=null;
	}
	/**
	 * Gets the default locale which is assigned to all users if they have not chosen a locale. 
	 *
	 * @return The set application default locale. If not set it returns the english locale.
	 **/
	public Locale getDefaultLocale() {
		if(cachedDefaultLocale==null){
			// Note: database entry always wins!
			String localeIdentifier = getFromApplicationBinding(DEFAULT_LOCALE_KEY);
			Locale locale = null;
			boolean firstTimeSave = false;
			Locale englishLocal = Locale.ENGLISH;
			if (localeIdentifier ==null) {
				//Set default to International English
				locale = englishLocal;
				firstTimeSave = true;
			}
			else{
				locale = LocaleUtil.getLocale(localeIdentifier);
			}
			if(!getApplication().isInDatabaseLessMode()){
				List localesInUse = ICLocaleBusiness.getListOfLocalesJAVA();
				//if it is a legal locale depending on the users settings then set that as the default otherwise use the first in the list
				if(localesInUse.contains(locale)){
					if(firstTimeSave){
						setDefaultLocale(locale);
					}
				}
				else{
					if(localesInUse.contains(englishLocal)){
						//try to use the english one
						locale = englishLocal;
					}
					else{
						//else just the first we find
						locale = (Locale)localesInUse.iterator().next();
					}
					setDefaultLocale(locale);//to fix the default locale or set it for the first time
				}
			}
			cachedDefaultLocale=locale;
			return locale;
		}
		return cachedDefaultLocale;
	}
	
	
	
	public AccessController getDefaultAccessController() {
		return new com.idega.core.accesscontrol.business.AccessControl();
	}
	
	
	/**
	
	 * Returns false if the removing fails
	
	 */
	public boolean removeIWService(Class serviceClass) {
		return false;
	}
	/**
	
	 * Returns false if the class is wrong or it fails
	
	 */
	public boolean addIWService(Class serviceClass) {
		return false;
	}
	/**
	
	 * Returns a list of Class objects corresponding to the IWService Classes
	
	 */
	public List getServiceClasses() {
		//return null;
		IWPropertyList plist = getLegacyApplicationSettings().getIWPropertyList(_SERVICE_CLASSES_KEY);
		// list is not being modified, call of store not necessary
		if (plist != null) {
			List l = new Vector();
			Iterator iter = plist.iterator();
			while (iter.hasNext()) {
				IWProperty item = (IWProperty) iter.next();
				String serviceClass = item.getValue();
				try {
					l.add(RefactorClassRegistry.forName(serviceClass));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return l;
		}
		return null;
	}
	
	public void setEntityAutoCreation(boolean ifAutoCreate) {
		putInApplicationBinding("entity-auto-create", Boolean.toString(ifAutoCreate));
		EntityControl.setAutoCreationOfEntities(ifAutoCreate);
	}
	
	/**
	 * Returns true if the is no entry.
	 * 
	 * @return
	 */
	public boolean getIfEntityAutoCreate() {
		String value = getFromApplicationBinding("entity-auto-create");
		// returns true if the value is null!
		if (value == null) {
			return true;
		} 
		return Boolean.getBoolean(value);
	}
	
	public boolean getIfEntityBeanCaching() {
		String value = getFromApplicationBinding("ido_entity_bean_caching");
		return Boolean.getBoolean(value);
	}
	
	public boolean getIfEntityQueryCaching() {
		String value = getFromApplicationBinding("ido_entity_query_caching");
		return Boolean.getBoolean(value);
	}
	
	public void setDebug(boolean ifDebug) {
		putInApplicationBinding("debug", Boolean.toString(ifDebug));
		setDebugMode(ifDebug);
	}
	
	public boolean getIfDebug() {
		String value = getFromApplicationBinding("debug");
		return Boolean.getBoolean(value);
	}
	
	public void setUsePreparedStatement(boolean usage) {
		putInApplicationBinding("PreparedStatement", Boolean.toString(usage));
		com.idega.data.DatastoreInterface.usePreparedStatement = usage;
	}
	
	public boolean getIfUsePreparedStatement() {
		String value = getFromApplicationBinding("PreparedStatement");
		boolean	ret = Boolean.getBoolean(value);
		com.idega.data.DatastoreInterface.usePreparedStatement = ret;
		return ret;
	}
	
	public void setDebugMode(boolean debugFlag) {
		DEBUG_FLAG = debugFlag;
		com.idega.data.EntityFinder.debug = debugFlag;
	}
	
	public boolean isDebugActive() {
		return DEBUG_FLAG;
	}
	
	public void setAutoCreateStrings(boolean ifAutoCreate) {
		this.putInApplicationBinding(AUTO_CREATE_LOCALIZED_STRINGS_KEY, Boolean.toString(ifAutoCreate));
		setAutoCreateStringsMode(ifAutoCreate);
	}
	
	/**
	 * <p>
	 * Gets if strings should be automatically created in bundle localization files
	 * if they do not pre-exist. This defaults to true.
	 * </p>
	 * @return
	 */
	public boolean getIfAutoCreateStrings() {
		String value = getFromApplicationBinding(AUTO_CREATE_LOCALIZED_STRINGS_KEY);
		if (value == null) {
			return true;
		} 
		return Boolean.getBoolean(value);
	}
	
	public static void setAutoCreateStringsMode(boolean ifAutoCreate) {
		CREATE_STRINGS = ifAutoCreate;
	}
	
	public static boolean isAutoCreateStringsActive() {
		return CREATE_STRINGS;
	}
	
	public void setEntityBeanCaching(boolean onOrOff) {
		putInApplicationBinding(IDO_ENTITY_BEAN_CACHING_KEY, Boolean.toString(onOrOff));
		com.idega.data.IDOContainer.getInstance().setBeanCaching(onOrOff);
		if (!onOrOff) {
			setEntityQueryCaching(false);
		}
	}
	
	public void setEntityQueryCaching(boolean onOrOff) {
		putInApplicationBinding(IDO_ENTITY_QUERY_CACHING_KEY, Boolean.toString(onOrOff));
		com.idega.data.IDOContainer.getInstance().setQueryCaching(onOrOff);
		if (onOrOff) {
			setEntityBeanCaching(true);
		}
	}
	public void setAutoCreateProperties(boolean ifAutoCreate) {
		putInApplicationBinding(AUTO_CREATE_PROPERTIES_KEY, Boolean.toString(ifAutoCreate));
		setAutoCreatePropertiesMode(ifAutoCreate);
	}
	
	/**
	 * Returns true if the is no entry.
	 * 
	 * @return
	 */
	public boolean getIfAutoCreateProperties() {
		String value = getFromApplicationBinding(AUTO_CREATE_PROPERTIES_KEY);
		if (value == null) {
			return true;
		} 
		return Boolean.getBoolean(value);
	}
	
	public void setAutoCreatePropertiesMode(boolean ifAutoCreate) {
		CREATE_PROPERTIES = ifAutoCreate;
	}
	
	public boolean isAutoCreatePropertiesActive() {
		return CREATE_PROPERTIES;
	}
	
	/**
	 * Gets the locale set for the current application for application scoped tasks.
	 * @return The set application locale. If not set it returns the default locale of the application
	 **/
	public Locale getApplicationLocale(){
		/**
		 * @todo: implement better
		 */
		return this.getDefaultLocale();
	}
	
	/**
	 * @return The character encoding string for example UTF-16 or the default ISO-8859-1
	 */
	public String getCharacterEncoding() {
		String encodingProperty = getFromApplicationBinding(CHARACTER_ENCODING_KEY);
		//check the encodingproperty and return it if set:
		if(encodingProperty!=null){
			return encodingProperty;
		}
		//else return the default:
		if(DEFAULT_CHARACTER_ENCODING==null){
			//Try first to get the value from the file.encoding system property:
			String fileEncoding =System.getProperty("file.encoding");
			DEFAULT_CHARACTER_ENCODING=fileEncoding;
			if(DEFAULT_CHARACTER_ENCODING==null){
				//if still there is no character encoding from the jvm, set it as unicode:
				DEFAULT_CHARACTER_ENCODING="UTF-8";
			}
		}
		return DEFAULT_CHARACTER_ENCODING;
	}
	
	
	/**
	 * 
	 * Gets if the application should automatically write down bundle property files (.pxml) fiiles on shutdown.
	 * This defaults to true;
	 */
	public boolean getWriteBundleFilesOnShutdown(){
		String value = getFromApplicationBinding("write_bundle_files_on_shudown");
		if (value == null) {
			return true;
		} 
		return Boolean.getBoolean(value);
	}
	
	/**
	 * 
	 * Sets if the application should automatically write down bundle property files (.pxml) fiiles on shutdown.
	 * This defaults to true, but can be specified on startup with setting
	 * the variable write_bundle_files_on_shudown=false in idegaweb.pxml
	 */
	public void setWriteBundleFilesOnShutdown(boolean ifWriteDown){
		this.putInApplicationBinding("write_bundle_files_on_shudown", Boolean.toString(ifWriteDown));
	}
	
	/**
	 * <p>
	 * Gets the default markup language for the application.<br/>
	 * In ePlatform version 3 this is xhtml.
	 * </p>
	 * @return
	 */
	public String getDefaultMarkupLanguage(){
		String value = getFromApplicationBinding(DEFAULT_MARKUP_LANGUAGE_KEY);
		if (value == null) {
			return DEFAULT_MARKUP_LANGUAGE;
		} 
		return value;
	}
	/**
	 * 
	 */
	public void setDefaultMarkupLanguage(String markupLanguage){
		putInApplicationBinding(DEFAULT_MARKUP_LANGUAGE_KEY, markupLanguage);
	}
	
	private String getFromApplicationBinding(String key) {
		try {
			return getApplicationBindingBusiness().get(key);
		}
		catch (IOException e) {
			getLogger().warning("[IWMainApplicationSettings] Could not fetch key: " + key);
		}
		return null;
	}

	private String putInApplicationBinding(String key, String value) {
		try {
			return getApplicationBindingBusiness().put(key, value);
		}
		catch (IOException e) {
			getLogger().warning("[IWMainApplicationSettings] Could not set key: " + key + " with value: " + value);
		}
		return null;
	}
	
	private ICApplicationBindingBusiness getApplicationBindingBusiness() {
		if (applicationBindingBusiness == null) {
			IWApplicationContext iwac = getApplication().getIWApplicationContext();
			try {
				applicationBindingBusiness = (ICApplicationBindingBusiness) IBOLookup.getServiceInstance(iwac, ICApplicationBindingBusiness.class);
			}
			catch (IBOLookupException ex) {
				getLogger().warning("[IWMainApplicationSettings] ICApplicationBindingBusiness could not be found");
				throw new IBORuntimeException(ex.getMessage());
			}
		}
		return applicationBindingBusiness;
	}
	
	
	/**
	 * @deprecated
	 * 
	 * Use ICApplicationBindingBusiness.
	 * 
	 * Do not use this method. Will be removed pretty soon.
	 * It is a temporary method.
	 * 
	 * !!!!!!!!!!!!!! Note: caller should store the list immediately, store method is not called anywhere !!!!!!!!!!!!!!!!!!!!!!!!
	 * 
	 * @param key
	 * @return
	 */
	public IWPropertyList getLegacyApplicationSettings() {
		String propertiesRealPath = getApplication().getPropertiesRealPath();
		return new IWPropertyList(propertiesRealPath, "idegaweb.pxml", true);
	}
	
}
