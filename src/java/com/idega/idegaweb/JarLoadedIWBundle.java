/**
 * 
 */
package com.idega.idegaweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.idega.util.SortedProperties;


/**
 * <p>
 * Implementation of an IWBundle loaded from a jar file instead of a folder
 * </p>
 *  Last modified: $Date: 2006/09/18 13:34:43 $ by $Author: gediminas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class JarLoadedIWBundle extends DefaultIWBundle {

	private static final Logger LOGGER = Logger.getLogger(JarLoadedIWBundle.class.getName());
	private JarModule jarModule;
	
	/**
	 * @param rootRealPath
	 * @param bundleIdentifier
	 * @param superApplication
	 */
	public JarLoadedIWBundle(JarModule module, IWMainApplication superApplication) {
		this.jarModule=module;
		String virtualPath = "/idegaweb/bundles/"+module.getModuleIdentifier()+".bundle";
		initialize(module.getAbsolutePath(), virtualPath, module.getModuleIdentifier(), superApplication, false);
	}
	
	/**
	 * <p>
	 * Initializes a IWPropertyList relative to the 'properties' folder within the bundle
	 * Overrided from superclass to fetch the file within the jar file.
	 * </p>
	 * @param pathWithinPropertiesFolder
	 * @return
	 */
	protected IWPropertyList initializePropertyList(String pathWithinPropertiesFolder, boolean autocreate) {
		IWPropertyList propList = null;
		String filePathWithinBundle = "properties/"+pathWithinPropertiesFolder;
		try {
			InputStream inStream = getResourceInputStream(filePathWithinBundle);
			propList = new IWPropertyList(inStream);
		}
		catch (IOException ex) {
			LOGGER.log(Level.WARNING, "Error initializing property list from file " + pathWithinPropertiesFolder, ex);
		}
		return propList;
	}

	public boolean doesResourceExist(String pathWithinBundle){
		JarEntry entry = jarModule.getJarEntry(pathWithinBundle);
		if (entry != null) {
			return true;
		}
		return false;
	}
	
	public InputStream getResourceInputStream(String pathWithinBundle) throws IOException {
		JarEntry entry = jarModule.getJarEntry(pathWithinBundle);
		InputStream inStream = jarModule.getInputStream(entry);
		return inStream;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.idegaweb.DefaultIWBundle#unload(boolean)
	 */
	public synchronized void unload(boolean storeState) {
		super.unload(storeState);
		this.jarModule=null;	
	}

	protected String getLocalizedResourcePath(Locale locale){
		return "resources/" + locale.toString() + ".locale";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.idegaweb.DefaultIWBundle#initializeResourceBundle(java.util.Locale)
	 */
	protected IWResourceBundle initializeResourceBundle(Locale locale) throws IOException {
		IWResourceBundle theReturn = null;
		InputStream defaultInputStream = getResourceInputStream(getLocalizedResourcePath(locale)+"/"+getLocalizedStringsFileName());
		IWResourceBundle defaultLocalizedResourceBundle = new IWResourceBundle(this, defaultInputStream, locale);
		if(isUsingLocalVariants()){
			//Locale variants are used:
			String variantfileName = getLocalizedStringsVariantFileName();
			String variantPath = getLocalizedResourcePath(locale)+"/"+variantfileName;
			if(doesResourceExist(variantPath)){
				InputStream variantStream = getResourceInputStream(variantPath);
				theReturn = new IWResourceBundle(defaultLocalizedResourceBundle, variantStream, locale);
			}
		}
		else{
			theReturn = defaultLocalizedResourceBundle;
		}
		return theReturn;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.idegaweb.DefaultIWBundle#initializeLocalizableStrings()
	 */
	protected Properties initializeLocalizableStrings() {
		Properties locProps = new SortedProperties();
		try {
			locProps.load(getResourceInputStream("resources/" + getLocalizableStringsFileName()));
			// localizableStringsMap = new TreeMap(localizableStringsProperties);
		}
		catch (IOException ex) {
			LOGGER.log(Level.WARNING, null, ex);
		}
		return locProps;
	}
	
}
