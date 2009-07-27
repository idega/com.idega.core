/*
 * $Id: IWMainApplicationSettings.java,v 1.62 2009/05/21 12:48:08 laddi Exp $
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
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.MimeUtility;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.business.ICApplicationBindingBusiness;
import com.idega.core.data.ICApplicationBindingBMPBean;
import com.idega.core.event.client.MethodWrapper;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.EntityControl;
import com.idega.presentation.Page;
import com.idega.repository.data.MutableClass;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.CoreConstants;
import com.idega.util.LocaleUtil;
import com.idega.util.StringHandler;
/**
 * <p>
 * This class is used by IWMainApplication as the holder of most properties set
 * on an application-wide basis. This class is responsible for reading the properties
 * set in idegaweb.pxml and also holds some default values that don't have to be 
 * explicitly set in the idegaweb.pxml properties file.
 * </p>
 * Copyright: Copyright (c) 2001-2005 idega software<br/>
 * Last modified: $Date: 2009/05/21 12:48:08 $ by $Author: laddi $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.62 $
 */


public class IWMainApplicationSettings implements MutableClass {

	/**
	 * Comment for <code>USE_PREPARED_STATEMENT</code>
	 */
	public static final String USE_PREPARED_STATEMENT = "PreparedStatement";

	private static final int MAX_KEY_LENGTH = ICApplicationBindingBMPBean.MAX_KEY_LENGTH; 
	
	private static final String ATTRIBUTE_APPLICATION_BINDING_MAP = "application_binding_map";
	
	public static final String AUTO_CREATE_LOCALIZED_STRINGS_KEY="auto-create-localized-strings";
	public static final String AUTO_CREATE_PROPERTIES_KEY="auto-create-properties";
	public static final String DEFAULT_MARKUP_LANGUAGE_KEY="markup_language";
	public static final String DEFAULT_MARKUP_LANGUAGE = Page.XHTML;
	public static final String DEFAULT_SYSTEM_LOCALE = "idegaweb.default.locale";
	
	public static boolean DEBUG_FLAG = false;
	public static boolean CREATE_STRINGS = true;
	public static boolean CREATE_PROPERTIES = true;

	private static String DEFAULT_CHARACTER_ENCODING; //= "ISO-8859-1";
	
	private static String DEFAULT_TEMPLATE_NAME;
	private static String DEFAULT_TEMPLATE_CLASS;
	private static String DEFAULT_FONT;
	private static String DEFAULT_FONT_SIZE;
	private static String DEFAULT_LOCALE_KEY;
	private static String _SERVICE_CLASSES_KEY;

	private static final String CHARSET_SEND_MAIL = "charset_sendmail";
	
	private static final String IDEGAWEB_PROPERTY_FILE_NAME = "idegaweb.pxml";

	private static final String CHARACTER_ENCODING_KEY = "character_encoding";
	public static final String IDO_ENTITY_BEAN_CACHING_KEY = "ido_entity_bean_caching";
	public static final String IDO_ENTITY_QUERY_CACHING_KEY = "ido_entity_query_caching";
	public static final String SESSION_POLLING_KEY = "session_polling";
	public static final String REVERSE_AJAX_KEY = "reverse_ajax";
	
	// the following three properties seem not to be set but  
	// they are read BEFORE the database is initialized, that is
	// these values should always be stored in the idegaweb.pxml file.
    private static final String USE_CRYPTO_PROPERTIES = "use_crypto_properties";
	private static final String IW_POOLMANAGER_TYPE = "iw_poolmanager";
	private static final String JDBC_DATASOURCE_DEFAULT_URL = "JDBC_DATASOURCE_DEFAULT_URL";
	
	// very special property
	public static final String ENTITY_AUTO_CREATE =  "entity-auto-create";
	public static final String USE_DEBUG_MODE =  "debug";
	
	//sets if to cache the properties into an application map
	private boolean cache=true;
	
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
	private IWPropertyList idegawebPropertyList = null;

	private boolean preloadedCache=false;
	
	// extension for handling events
	private IWMainApplicationSettingsEventClient iwApplicationSettingsEventClient = null;
	
	public IWMainApplicationSettings(IWMainApplication application) {
		this.application=application;	
		initializeEventClient();
	}
	

	
	/**
	 * <p>
	 * Preloads the instances of ICApplicationBinding to the bean cache
	 * </p>
	 */
	private void preloadCache() {
		if(!this.preloadedCache){
			try {

				/*
				@SuppressWarnings("unchecked")
				Set<String> keys = getApplicationBindingBusiness().keySet();
				
				for (String key : keys) {
					//cache
					if(key!=null){}
				}
				*/
				
				this.preloadedCache=true;
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private IWMainApplication getApplication(){
		return this.application;
	}
	

	public String getProperty(String key) {
		preloadCache();
		key = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		return getFromApplicationBinding(key);
	}
	
	/**
	 * Returns the corresponding value of the specified key.
	 * If there is no entry the specified defaultReturnValue is stored and
	 * returned.
	 * 
	 * @param key
	 * @param defaultReturnValue
	 * @return
	 */
	public String getProperty(String key, String defaultReturnValue) {
		// try to get a value
		String value = getProperty(key);
		// if the value is null store the default value and return that value
		if (value == null) {
			setProperty(key, defaultReturnValue);
			return defaultReturnValue;
		}
		// value was not null, return the found value
		return value;
		
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		String value = getProperty(key);
		if (value != null) {
			return Boolean.valueOf(value).booleanValue();
		}
		else {
			setProperty(key, Boolean.toString(defaultValue));

			return defaultValue;
		}
	}

	public boolean getBoolean(String key) {
		String value = getProperty(key);
		return Boolean.valueOf(value).booleanValue();
	}
	
	public void setProperty(String key, String value) {
		setProperty(key, value, null);
	}

	public void setProperty(String key, String value, String type) {
		key = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		putInApplicationBinding(key, value, type);
	}
	
	public void removeProperty(String key) {
		key = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		removeFromApplicationBinding(key);
	}
	
	public Set keySet() {
		// merge the keys from idegaweb.pxml file and ICApplicationBinding
		try {
			Set keysFromApplicationBinding = getApplicationBindingBusiness().keySet();
			Iterator iterator = getIdegawebPropertyList().getIWPropertyListIterator();
			while (iterator.hasNext()) {
				IWProperty property = (IWProperty) iterator.next();
				String key = property.getKey();
				keysFromApplicationBinding.add(key);
			}
			
			keysFromApplicationBinding.remove(ENTITY_AUTO_CREATE);
			keysFromApplicationBinding.remove(IDO_ENTITY_BEAN_CACHING_KEY);
			keysFromApplicationBinding.remove(IDO_ENTITY_QUERY_CACHING_KEY);
			keysFromApplicationBinding.remove(SESSION_POLLING_KEY);
			keysFromApplicationBinding.remove(USE_PREPARED_STATEMENT);
			keysFromApplicationBinding.remove(AUTO_CREATE_LOCALIZED_STRINGS_KEY);
			keysFromApplicationBinding.remove(AUTO_CREATE_PROPERTIES_KEY);
			keysFromApplicationBinding.remove(USE_DEBUG_MODE);
			keysFromApplicationBinding.remove(DEFAULT_LOCALE_KEY);
			keysFromApplicationBinding.remove(DEFAULT_MARKUP_LANGUAGE_KEY);
			
			return keysFromApplicationBinding;
		}
		catch (IOException e) {
			getLogger().warning("[IWMainApplicationSettings] Could not fetch keys from databse");
			throw new IBORuntimeException(e);
		}
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
	
	/**
	 * 
	 * Special method that is called by IWMainApplicationStarter.
	 * The value is not used at the moment, returns therefore null.
	 * Keep in mind that during the call the database is not initialized yet. 
	 * The value is always fetched from idegaweb.pxml file.
	 * 
	 * @return
	 */
	public String getPoolManagerType() {
		return getIdegawebPropertyList().getProperty(IW_POOLMANAGER_TYPE);
	}
	
	/**
	 * 
	 * Special method that is called by IWMainApplication.
	 * The value is not used at the moment, returns therefore null.
	 * Keep in mind that during the call the database is not initialized yet. 
	 * The value is always fetched from idegaweb.pxml file.
	 * 
	 * @return
	 */
	public String getCryptoUsage() {
		return getIdegawebPropertyList().getProperty(USE_CRYPTO_PROPERTIES);
	}
	
	/**
	 * 
	 * Special method that is called by IWMainApplicationStarter.
	 * The value is not used at the moment, returns therefore null.
	 * Keep in mind that during the call the database is not initialized yet. 
	 * The value is always fetched from idegaweb.pxml file.
	 * 
	 * @return
	 */
	public String getJDBCDatasourceDefaultURL() {
		return getIdegawebPropertyList().getProperty(JDBC_DATASOURCE_DEFAULT_URL);
	}
	
	public void setDefaultLocale(Locale locale) {
		String sLocale = locale.toString();
		putInApplicationBinding(DEFAULT_LOCALE_KEY,sLocale);
		this.cachedDefaultLocale=null;
	}
	
	public Locale getDefaultSystemLocale() {
		String systemLocale = System.getProperty(DEFAULT_SYSTEM_LOCALE);
		Locale locale = null;
		Locale englishLocal = Locale.ENGLISH;

		if (systemLocale ==null) {
			locale = englishLocal;
		}
		else{
			locale = LocaleUtil.getLocale(systemLocale);
		}
		
		return locale;
	}
	
	/**
	 * Gets the default locale which is assigned to all users if they have not chosen a locale. 
	 *
	 * @return The set application default locale. If not set it returns the english locale.
	 **/
	public Locale getDefaultLocale() {
		if(this.cachedDefaultLocale==null){
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
				if (localesInUse.isEmpty()) {
					locale = englishLocal;
				}
				else {
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
			}
			this.cachedDefaultLocale=locale;
			return locale;
		}
		return this.cachedDefaultLocale;
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
		IWPropertyList plist = getIdegawebPropertyList().getIWPropertyList(_SERVICE_CLASSES_KEY);
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
		putInApplicationBinding(ENTITY_AUTO_CREATE, Boolean.toString(ifAutoCreate));
		EntityControl.setAutoCreationOfEntities(ifAutoCreate);
	}
	
	/**
	 * Returns true if the is no entry.
	 * 
	 * @return
	 */
	public boolean getIfEntityAutoCreate() {
		String value = getFromApplicationBinding(ENTITY_AUTO_CREATE);
		// returns true if the value is null!
		if (value == null) {
			return true;
		} 
		return Boolean.valueOf(value).booleanValue();
	}
	
	public boolean getFactorySettingsForAutoCreateEntities() {
		String value = 	getIdegawebPropertyList().getProperty(ENTITY_AUTO_CREATE);
		// returns true if the value is null!
		if (value == null) {
			return true;
		}
		return Boolean.valueOf(value).booleanValue();
	}
	
	public boolean getIfEntityBeanCaching() {
		String value = getFromApplicationBinding("ido_entity_bean_caching");
		return Boolean.valueOf(value).booleanValue();
	}
	
	public boolean getIfEntityQueryCaching() {
		String value = getFromApplicationBinding("ido_entity_query_caching");
		return Boolean.valueOf(value).booleanValue();
	}
	
	public void setDebug(boolean ifDebug) {
		putInApplicationBinding(USE_DEBUG_MODE, Boolean.toString(ifDebug));
		setDebugMode(ifDebug);
	}
	
	public boolean getIfDebug() {
		String value = getFromApplicationBinding(USE_DEBUG_MODE);
		return Boolean.valueOf(value).booleanValue();
	}
	
	public void setUsePreparedStatement(boolean usage) {
		putInApplicationBinding(USE_PREPARED_STATEMENT, Boolean.toString(usage));
		com.idega.data.DatastoreInterface.usePreparedStatement = usage;
	}
	
	public boolean getIfUsePreparedStatement() {
		String value = getFromApplicationBinding(USE_PREPARED_STATEMENT);
		boolean	ret = Boolean.valueOf(value).booleanValue();
		com.idega.data.DatastoreInterface.usePreparedStatement = ret;
		return ret;
	}
	
	public boolean getIfUseSessionPolling() {
		String value = getFromApplicationBinding(SESSION_POLLING_KEY);
		boolean ret = Boolean.valueOf(value).booleanValue();
		return ret;
	}
	
	public boolean isReverseAjaxEnabled() {
		return getBoolean(REVERSE_AJAX_KEY, Boolean.FALSE);
	}
	
	public void setDebugMode(boolean debugFlag) {
		DEBUG_FLAG = debugFlag;
		com.idega.data.EntityFinder.debug = debugFlag;
		
//		setting/unsetting finer level for root logger of all loggers
//		info is default Level, so setting to that, when debug is false
		Level levelToSet = debugFlag ? Level.FINER : Level.INFO;
		
		Logger parentLogger = Logger.global.getParent();
		Logger rootLogger = parentLogger;
		
		while (parentLogger != null) {
			rootLogger = parentLogger;
			parentLogger = parentLogger.getParent();
		}
		
		if(rootLogger.getLevel() != levelToSet) {
			
			Handler[] handlers =
			      rootLogger.getHandlers();
			
			for (Handler handler : handlers) {
				handler.setLevel(levelToSet);
			}
			
			rootLogger.setLevel(levelToSet);
		}
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
		return Boolean.valueOf(value).booleanValue();
	}
	
	public static void setAutoCreateStringsMode(boolean ifAutoCreate) {
		CREATE_STRINGS = ifAutoCreate;
	}
	
	public static boolean isAutoCreateStringsActive() {
		return CREATE_STRINGS;
	}
	
	public void setEntityBeanCaching(boolean onOrOff) {
		putInApplicationBinding(IDO_ENTITY_BEAN_CACHING_KEY, Boolean.toString(onOrOff));
		com.idega.data.IDOContainer.getInstance().setBeanCachingActiveByDefault(onOrOff);
		if (!onOrOff) {
			setEntityQueryCaching(false);
		}
	}
	
	public void setEnableSessionPolling(boolean onOrOff) {
		putInApplicationBinding(SESSION_POLLING_KEY, Boolean.toString(onOrOff));
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
		return Boolean.valueOf(value).booleanValue();
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
	
	
	public String getCharSetForSendMail() {
		String charSet = getFromApplicationBinding(CHARSET_SEND_MAIL);
		if (StringHandler.isEmpty(charSet)) {
			charSet = MimeUtility.getDefaultJavaCharset();
		}
		return charSet;
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
				DEFAULT_CHARACTER_ENCODING=CoreConstants.ENCODING_UTF8;
			}
		}
		return DEFAULT_CHARACTER_ENCODING;
	}
	
	public void setDefaultCharacterEncoding(String encoding) {
		this.putInApplicationBinding(CHARACTER_ENCODING_KEY, encoding);
	}
	
	public boolean isSetDefaultCharacterEncoding() {
		try {
			return getApplicationBindingBusiness().get(CHARACTER_ENCODING_KEY) != null;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
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
		return Boolean.valueOf(value).booleanValue();
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
		String value = null;
		if (isApplicationBindingInMap(key)) {
			value = getApplicationBindingFromMap(key);
		}
		else {
			try {
				value = getApplicationBindingBusiness().get(key);
				if (value != null) {
					setApplicationBindingInMap(key, value);
				}
			}
			catch (IOException e) {
				getLogger().warning("[IWMainApplicationSettings] Could not fetch key: " + key);
				throw new IBORuntimeException(e);
			}
			if (value == null) {
				value = getIdegawebPropertyList().getProperty(key);
				this.putInApplicationBinding(key, value);
			}
		}
		return value;
	}
	
	private boolean isApplicationBindingInMap(String key) {
		if(this.cache){
			Map map = (Map) getApplication().getIWApplicationContext().getApplicationAttribute(ATTRIBUTE_APPLICATION_BINDING_MAP);
			if (map != null) {
				return map.containsKey(key);
			}
		}
		return false;
	}
	
	private String getApplicationBindingFromMap(String key) {
		if(this.cache){
			Map map = (Map) getApplication().getIWApplicationContext().getApplicationAttribute(ATTRIBUTE_APPLICATION_BINDING_MAP);
			if (map != null) {
				return (String) map.get(key);
			}
		}
		return null;
	}
	
	void setApplicationBindingInMap(String key, String value) {
		// note that on the other servers the cache might be active!
		iwApplicationSettingsEventClient.setApplicationBindingInMap(key, value);
		if(this.cache){
			Map map = (Map) getApplication().getIWApplicationContext().getApplicationAttribute(ATTRIBUTE_APPLICATION_BINDING_MAP);
			if (map == null) {
				map = new HashMap();
			}
			map.put(key, value);
			getApplication().getIWApplicationContext().setApplicationAttribute(ATTRIBUTE_APPLICATION_BINDING_MAP, map);
		}
	}

	void removeApplicationBindingFromMap(String key) {
		// note that on the other servers the cache might be active!
		iwApplicationSettingsEventClient.removeApplicationBindingFromMap(key);
		if(this.cache){
			Map map = (Map) getApplication().getIWApplicationContext().getApplicationAttribute(ATTRIBUTE_APPLICATION_BINDING_MAP);
			if (map != null) {
				map.remove(key);
			}
		}
	}

	private String putInApplicationBinding(String key, String value) {
		return putInApplicationBinding(key, value, null);
	}
	
	private String putInApplicationBinding(String key, String value, String type) {
		try {
			setApplicationBindingInMap(key, value);
			return getApplicationBindingBusiness().put(key, value, type);
		}
		catch (IOException e) {
			getLogger().warning("[IWMainApplicationSettings] Could not set key: " + key + " with value: " + value);
			throw new IBORuntimeException(e);
		}
	}
	
	private void removeFromApplicationBinding(String key) {
		try {
			getApplicationBindingBusiness().remove(key);
			removeApplicationBindingFromMap(key);
		}
		catch (IOException e) {
			getLogger().warning("[IWMainApplicationSettings] Could not remove key: " + key);
			throw new IBORuntimeException(e);
		}
	}
	
	private ICApplicationBindingBusiness getApplicationBindingBusiness() {
		if (this.applicationBindingBusiness == null) {
			IWApplicationContext iwac = getApplication().getIWApplicationContext();
			try {
				this.applicationBindingBusiness = (ICApplicationBindingBusiness) IBOLookup.getServiceInstance(iwac, ICApplicationBindingBusiness.class);
			}
			catch (IBOLookupException ex) {
				getLogger().warning("[IWMainApplicationSettings] ICApplicationBindingBusiness could not be found");
				throw new IBORuntimeException(ex.getMessage());
			}
		}
		return this.applicationBindingBusiness;
	}

	private IWPropertyList getIdegawebPropertyList() {
		if (this.idegawebPropertyList == null) {
			String propertiesRealPath = getApplication().getPropertiesRealPath();
			this.idegawebPropertyList = new IWPropertyList(propertiesRealPath, IDEGAWEB_PROPERTY_FILE_NAME, true);
		}
		return this.idegawebPropertyList;
	}

	
	/**
	 * @deprecated
	 * 
	 * Use setProperty(String, String), getProperty(String), getBoolean(String), getProperty(String, String)
	 * 
	 * DO NOT USE this method. Will be removed pretty soon.
	 * It is a temporary method.
	 * Only used by IBColorChooserWindow that is storing a color list.
	 * 
	 * !!!!!!!!!!!!!! Note: caller should store the list immediately, store method is not called anywhere !!!!!!!!!!!!!!!!!!!!!!!!
	 * In the past store was called during shutdown of the application.
	 * 
	 * @param key
	 * @return
	 */
	@Deprecated
	public IWPropertyList getLegacyApplicationSettings() {
		return getIdegawebPropertyList();
	}
	
	/**
	 * Register the private methods to the event client as method wrappers.
	 * In that way the event client is able to call them.
	 *
	 */
	private void initializeEventClient() {
		iwApplicationSettingsEventClient = new IWMainApplicationSettingsEventClient();		
		MethodWrapper methodWrapper1 = new MethodWrapper() {

			@Override
			public String getIdentifier() {
				return IWMainApplicationSettingsEventClient.SET_APPLICATION_BINDING_IN_MAP;
			}

			@Override
			public void perform(Object object1, Object object2) {
				setApplicationBindingInMap((String) object1, (String) object2);
			}
		};
		iwApplicationSettingsEventClient.addMethod(methodWrapper1);
		MethodWrapper methodWrapper2 = new MethodWrapper() {
			
			@Override
			public String getIdentifier() {
				return IWMainApplicationSettingsEventClient.REMOVE_APPLICATION_BINDING_FROM_MAP;
			}
			
			@Override
			public void perform(Object object1) {
				removeApplicationBindingFromMap((String) object1);
			}
		};
		iwApplicationSettingsEventClient.addMethod(methodWrapper2);
	}
	
}