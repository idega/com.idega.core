/**
 * 
 */
package com.idega.idegaweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import com.idega.util.SortedProperties;


/**
 * <p>
 * Implementation of an IWBundle loaded from a jar file instead of a folder
 * </p>
 *  Last modified: $Date: 2006/09/18 12:47:11 $ by $Author: gediminas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class JarLoadedIWBundle extends DefaultIWBundle {

	private JarModule jarModule;
	
	/**
	 * @param rootRealPath
	 * @param bundleIdentifier
	 * @param superApplication
	 */
	public JarLoadedIWBundle(JarModule module,IWMainApplication superApplication) {
		this.jarModule=module;
		String virtualPath = "/idegaweb/bundles/"+module.getModuleIdentifier()+".bundle";
		initialize(module.getAbsolutePath(),virtualPath, module.getModuleIdentifier(), superApplication,false);
	}
	
	/**
	 * <p>
	 * Initializes a IWPropertyList relative to the 'properties' folder within the bundle
	 * Overrided from superclass to fetch the file within the jar file.
	 * </p>
	 * @param pathWitinPropertiesFolder
	 * @return
	 */
	protected IWPropertyList initializePropertyList(String pathWitinPropertiesFolder,boolean autocreate) {
		IWPropertyList propList = null;
		String filePathWithinBundle = "properties/"+pathWitinPropertiesFolder;
		try {
			InputStream inStream = getResourceInputStream(filePathWithinBundle);
			propList=new IWPropertyList(inStream);
		}
		catch (IOException ex) {
			log(ex);
		}
		return propList;
	}

	public boolean doesResourceExist(String pathWithinBundle){
		JarEntry entry = jarModule.getJarEntry(pathWithinBundle);
		if(entry!=null){
			return true;
		}
		return false;
	}
	
	public InputStream getResourceInputStream(String pathWithinBundle) throws IOException {
		JarEntry entry = jarModule.getJarEntry(pathWithinBundle);
		InputStream inStream;
		inStream = jarModule.getInputStream(entry);
		return inStream;
	}
	
	/**
	 *Unloads all resources for this bundle and stores the state of this bundle if storeState==true
	 *@param storeState to say if to store the state (call storeState)
	 */
	public synchronized void unload(boolean storeState){
		
		super.unload(storeState);
		this.jarModule=null;
		
	}

	
	protected String getLocalizedResourcePath(Locale locale){
		return "resources/" + locale.toString() + ".locale";
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method initializeResourceBundle
	 * </p>
	 * @param locale
	 * @return
	 * @throws IOException
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
	
	protected Properties initializeLocalizableStrings() {
		Properties locProps = new SortedProperties();
		try {
			locProps.load(getResourceInputStream("resources/" + getLocalizableStringsFileName()));
			// localizableStringsMap = new TreeMap(localizableStringsProperties);
		}
		catch (IOException ex) {
			log(ex);
		}
		return locProps;
	}
	
}
