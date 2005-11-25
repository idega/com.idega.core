/*
 * $Id: IWMainApplicationSettings.java,v 1.29 2005/11/25 15:16:22 tryggvil Exp $
 * Created in 2001 by Tryggvi Larusson
 * 
 * Copyright (C) 2001-2005 Idega software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.ejb.FinderException;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.EntityControl;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.Page;
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
 * Last modified: $Date: 2005/11/25 15:16:22 $ by $Author: tryggvil $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.29 $
 */
public class IWMainApplicationSettings extends IWPropertyList {
	private static final String CHARACTER_ENCODING_KEY = "character_encoding";
	private static String DEFAULT_CHARACTER_ENCODING; //= "ISO-8859-1";
	//public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	
	private static String DEFAULT_TEMPLATE_NAME = "defaulttemplatename";
	private static String DEFAULT_TEMPLATE_CLASS = "defaulttemplateclass";
	private static String DEFAULT_FONT = "defaultfont";
	private static String DEFAULT_FONT_SIZE = "defaultfontsize";
	private static String DEFAULT_LOCALE_KEY = "defaultlocale";
	private static String _SERVICE_CLASSES_KEY = "iw_service_class_key";
	private static final String IDO_ENTITY_BEAN_CACHING_KEY =
		"ido_entity_bean_caching";
	private static final String IDO_ENTITY_QUERY_CACHING_KEY =
		"ido_entity_query_caching";
	public static final String IW_POOLMANAGER_TYPE = "iw_poolmanager";
	public static final String AUTO_CREATE_LOCALIZED_STRINGS_KEY="auto-create-localized-strings";
	public static final String AUTO_CREATE_PROPERTIES_KEY="auto-create-properties";
	public static final String DEFAULT_MARKUP_LANGUAGE_KEY="markup_language";

	public final static String DEFAULT_MARKUP_LANGUAGE = Page.XHTML;
	
	public static boolean DEBUG_FLAG = false;
	public static boolean CREATE_STRINGS = true;
	public static boolean CREATE_PROPERTIES = true;
	
	//instance variables:
	private IWMainApplication application;
	private Locale cachedDefaultLocale = null;
	
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
	
	/**
	 * <p>
	 * Gets the property value if set in the IC_APPLICATION_BINDING table.
	 * </p>
	 * @param propertyKey
	 * @return
	 */
	public String getApplicationBindingProperty(String propertyKey){
		ICApplicationBindingHome bindingHome;
		try {
			bindingHome = (ICApplicationBindingHome) IDOLookup.getHome(ICApplicationBinding.class);
			ICApplicationBinding binding = bindingHome.findByPrimaryKey(propertyKey);
			if(binding!=null){
				return binding.getValue();
			}
		}
		catch (Exception e) {
		}
		return null;
	}
	/**
	 * <p>
	 * Sets the property value or updates in the IC_APPLICATION_BINDING table.
	 * </p>
	 * @param propertyKey
	 * @return
	 */
	public void setApplicationBindingProperty(String key,String value) {
		ICApplicationBindingHome bindingHome;
		try {
			bindingHome = (ICApplicationBindingHome) IDOLookup.getHome(ICApplicationBinding.class);
			ICApplicationBinding binding = null;
			try{
				bindingHome.findByPrimaryKey(key);
			}
			catch(FinderException fe){
			}
			if(binding==null){
				binding = bindingHome.create();
			}
			binding.setKey(key);
			binding.setValue(value);
			binding.store();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setDefaultLocale(Locale locale) {
		//setProperty(DEFAULT_LOCALE_KEY, locale.toString());
		String sLocale = locale.toString();
		setApplicationBindingProperty(DEFAULT_LOCALE_KEY,sLocale);
		cachedDefaultLocale=null;
	}
	/**
	 * Gets the default locale which is assigned to all users if they have not chosen a locale. 
	 *
	 * @return The set application default locale. If not set it returns the english locale.
	 **/
	public Locale getDefaultLocale() {
		if(cachedDefaultLocale==null){
			String localeIdentifierFromApplicationBinding = getApplicationBindingProperty(DEFAULT_LOCALE_KEY);
			String localeIdentifierFromProperty = getProperty(DEFAULT_LOCALE_KEY);
			Locale locale = null;
			boolean firstTimeSave = false;
			Locale englishLocal = Locale.ENGLISH;
			if (localeIdentifierFromApplicationBinding == null && localeIdentifierFromProperty==null) {
				//Set default to International English
				locale = englishLocal;
				firstTimeSave = true;
			}
			else if(localeIdentifierFromApplicationBinding!=null){
				locale = LocaleUtil.getLocale(localeIdentifierFromApplicationBinding);
			}
			else{
				locale = LocaleUtil.getLocale(localeIdentifierFromProperty);
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
		else{
			return cachedDefaultLocale;
		}
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
		this.setProperty("entity-auto-create", ifAutoCreate);
		EntityControl.setAutoCreationOfEntities(ifAutoCreate);
	}
	public boolean getIfEntityAutoCreate() {
		String value = getProperty("entity-auto-create");
		if (value == null) {
			return true;
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
		this.setProperty(AUTO_CREATE_LOCALIZED_STRINGS_KEY, ifAutoCreate);
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
		String value = getProperty(AUTO_CREATE_LOCALIZED_STRINGS_KEY);
		if (value == null) {
			return true;
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
		this.setProperty(AUTO_CREATE_PROPERTIES_KEY, ifAutoCreate);
		setAutoCreatePropertiesMode(ifAutoCreate);
	}
	public boolean getIfAutoCreateProperties() {
		String value = getProperty(AUTO_CREATE_PROPERTIES_KEY);
		if (value == null) {
			return true;
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
		String encodingProperty = getProperty(CHARACTER_ENCODING_KEY);
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
	
	/**
	 * <p>
	 * Gets the default markup language for the application.<br/>
	 * In ePlatform version 3 this is xhtml.
	 * </p>
	 * @return
	 */
	public String getDefaultMarkupLanguage(){
		String value = getProperty(DEFAULT_MARKUP_LANGUAGE_KEY);
		if (value == null) {
			return DEFAULT_MARKUP_LANGUAGE;
		} else {
			return value;
		}
	}
	/**
	 * 
	 */
	public void setDefaultMarkupLanguage(String markupLanguage){
		this.setProperty(DEFAULT_MARKUP_LANGUAGE_KEY, markupLanguage);
	}
}
