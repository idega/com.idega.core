//idega 2001 - Tryggvi Larusson
/*

*Copyright 2001 idega.is All Rights Reserved.

*/
package com.idega.idegaweb;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.EntityControl;
import com.idega.util.LocaleUtil;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0 - Under development

*/
public class IWMainApplicationSettings extends IWPropertyList {
	public static final String CHARACTER_ENCODING = "character_encoding";
	public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";
	//public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	
	private static String IW_SERVICE_CLASS_NAME = "iw_service_class_name";
	private static String DEFAULT_TEMPLATE_NAME = "defaulttemplatename";
	private static String DEFAULT_TEMPLATE_CLASS = "defaulttemplateclass";
	private static String DEFAULT_FONT = "defaultfont";
	private static String DEFAULT_FONT_SIZE = "defaultfontsize";
	private static String DEFAULT_LOCALE = "defaultlocale";
	private static String _SERVICE_CLASSES_KEY = "iw_service_class_key";
	private static final String IDO_ENTITY_BEAN_CACHING_KEY =
		"ido_entity_bean_caching";
	private static final String IDO_ENTITY_QUERY_CACHING_KEY =
		"ido_entity_query_caching";
	public static final String IW_POOLMANAGER_TYPE = "iw_poolmanager";
	public static boolean DEBUG_FLAG = false;
	public static boolean CREATE_STRINGS = false;
	public static boolean CREATE_PROPERTIES = false;
	
	//instance variables:
	private IWMainApplication application;
	
	public IWMainApplicationSettings(IWMainApplication application) {
		super(application.getPropertiesRealPath(), "idegaweb.pxml", true);
		this.application=application;	
	}
	
	private IWMainApplication getApplication(){
		return application;
	}
	
	public void setDefaultTemplate(String templateName, String classname) {
		setProperty(DEFAULT_TEMPLATE_NAME, templateName);
		setProperty(DEFAULT_TEMPLATE_CLASS, classname);
	}
	public String getDefaultTemplateName() {
		return getProperty(DEFAULT_TEMPLATE_NAME);
	}
	public String getDefaultTemplateClass() {
		return getProperty(DEFAULT_TEMPLATE_CLASS);
	}
	public String getDefaultFont() {
		return getProperty(DEFAULT_FONT);
	}
	public void setDefaultFont(String fontname) {
		setProperty(DEFAULT_FONT, fontname);
	}
	public int getDefaultFontSize() {
		return Integer.parseInt(getProperty(DEFAULT_FONT_SIZE));
	}
	public void setDefaultFontSize(int size) {
		setProperty(DEFAULT_FONT_SIZE, size);
	}
	/*public void setDefaultLocale(Locale locale){
	
	setProperty(DEFAULT_LOCALE,locale.toString());
	
	}*/
	public void setDefaultLocale(Locale locale) {
		setProperty(DEFAULT_LOCALE, locale.toString());
	}
	/*public Locale getDefaultLocale(){
	
	  return (new Locale(getProperty(DEFAULT_LOCALE)));
	
	}*/
	
	/**
	 * Gets the default locale which is assigned to all users if they have not chosen a locale. 
	 *
	 * @return The set application default locale. If not set it returns the english locale.
	 **/
	public Locale getDefaultLocale() {
		String localeIdentifier = getProperty(DEFAULT_LOCALE);
		Locale locale = null;
		boolean firstTimeSave = false;
		Locale englishLocal = Locale.ENGLISH;
		if (localeIdentifier == null) {
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
		return locale;
	}
	
	
	
	public AccessController getDefaultAccessController() {
		return (AccessController) new com
			.idega
			.core
			.accesscontrol
			.business
			.AccessControl();
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
		IWPropertyList plist = getIWPropertyList(_SERVICE_CLASSES_KEY);
		if (plist != null) {
			List l = new Vector();
			Iterator iter = plist.iterator();
			while (iter.hasNext()) {
				IWProperty item = (IWProperty) iter.next();
				String serviceClass = item.getValue();
				try {
					l.add(Class.forName(serviceClass));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return l;
		}
		return null;
	}
	public void setEntityAutoCreation(boolean ifAutoCreate) {
		this.setProperty("entity-auto-create", ifAutoCreate);
		EntityControl.setAutoCreationOfEntities(ifAutoCreate);
	}
	public boolean getIfEntityAutoCreate() {
		String value = getProperty("entity-auto-create");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public boolean getIfEntityBeanCaching() {
		String value = getProperty("ido_entity_bean_caching");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public boolean getIfEntityQueryCaching() {
		String value = getProperty("ido_entity_query_caching");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public void setDebug(boolean ifDebug) {
		this.setProperty("debug", ifDebug);
		setDebugMode(ifDebug);
	}
	public boolean getIfDebug() {
		String value = getProperty("debug");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public void setUsePreparedStatement(boolean usage) {
		this.setProperty("PreparedStatement", usage);
		com.idega.data.DatastoreInterface.usePreparedStatement = usage;
	}
	public boolean getIfUsePreparedStatement() {
		String value = getProperty("PreparedStatement");
		boolean ret = false;
		if (value == null) {
			ret =  false;
		} else {
			ret = Boolean.valueOf(value).booleanValue();
		}
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
		this.setProperty("auto-create-localized-strings", ifAutoCreate);
		setAutoCreateStringsMode(ifAutoCreate);
	}
	public boolean getIfAutoCreateStrings() {
		String value = getProperty("auto-create-localized-strings");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	public static void setAutoCreateStringsMode(boolean ifAutoCreate) {
		CREATE_STRINGS = ifAutoCreate;
	}
	public static boolean isAutoCreateStringsActive() {
		return CREATE_STRINGS;
	}
	public void setEntityBeanCaching(boolean onOrOff) {
		this.setProperty(IDO_ENTITY_BEAN_CACHING_KEY, onOrOff);
		com.idega.data.IDOContainer.getInstance().setBeanCaching(onOrOff);
		if (!onOrOff) {
			setEntityQueryCaching(false);
		}
	}
	public void setEntityQueryCaching(boolean onOrOff) {
		this.setProperty(IDO_ENTITY_QUERY_CACHING_KEY, onOrOff);
		com.idega.data.IDOContainer.getInstance().setQueryCaching(onOrOff);
		if (onOrOff) {
			setEntityBeanCaching(true);
		}
	}
	public void setAutoCreateProperties(boolean ifAutoCreate) {
		this.setProperty("auto-create-properties", ifAutoCreate);
		setAutoCreatePropertiesMode(ifAutoCreate);
	}
	public boolean getIfAutoCreateProperties() {
		String value = getProperty("auto-create-properties");
		if (value == null) {
			return false;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
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
		return getProperty(CHARACTER_ENCODING, DEFAULT_CHARACTER_ENCODING);
	}
	
	
	/**
	 * Gets if the application should automatically write down bundle property files (.pxml) fiiles on shutdown.
	 * This defaults to true;
	 */
	public boolean getWriteBundleFilesOnShutdown(){
		String value = getProperty("write_bundle_files_on_shudown");
		if (value == null) {
			return true;
		} else {
			return Boolean.valueOf(value).booleanValue();
		}
	}
	/**
	 * Sets if the application should automatically write down bundle property files (.pxml) fiiles on shutdown.
	 * This defaults to true, but can be specified on startup with setting
	 * the variable write_bundle_files_on_shudown=false in idegaweb.pxml
	 */
	public void setWriteBundleFilesOnShutdown(boolean ifWriteDown){
		this.setProperty("write_bundle_files_on_shudown", ifWriteDown);
	}
}
