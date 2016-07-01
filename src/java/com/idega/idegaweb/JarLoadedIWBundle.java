/**
 *
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.SortedProperties;
import com.idega.util.StringHandler;


/**
 * <p>
 * Implementation of an IWBundle loaded from a jar file instead of a folder
 * </p>
 *  Last modified: $Date: 2009/01/05 10:27:32 $ by $Author: anton $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.13 $
 */

public class JarLoadedIWBundle extends DefaultIWBundle {

	private static final long serialVersionUID = 8895958470418903712L;
	private static final Logger LOGGER = Logger.getLogger(JarLoadedIWBundle.class.getName());

	private JarModule jarModule;

	/**
	 * @param rootRealPath
	 * @param bundleIdentifier
	 * @param superApplication
	 */
	public JarLoadedIWBundle(JarModule module, IWMainApplication superApplication) {
		this.jarModule=module;
		String realPath = superApplication.getBundlesRealPath() + File.separator + module.getModuleIdentifier()+".bundle";
		String virtualPath = "/idegaweb/bundles/"+module.getModuleIdentifier()+".bundle";
		initialize(realPath, virtualPath, module.getModuleIdentifier(), superApplication, false);
	}

	/**
	 * <p>
	 * Initializes a IWPropertyList relative to the 'properties' folder within the bundle
	 * Overrided from superclass to fetch the file within the jar file.
	 * </p>
	 * @param pathWithinPropertiesFolder
	 * @return
	 */
	@Override
	protected IWPropertyList initializePropertyList(String pathWithinPropertiesFolder, boolean autocreate) {
		IWPropertyList propList = null;
		String filePathWithinBundle = "properties/"+pathWithinPropertiesFolder;
		InputStream inStream = null;
		try {
			inStream = getResourceInputStream(filePathWithinBundle);
		} catch (Exception e) {
			LOGGER.warning(e.getMessage());
		}
		if (inStream == null) {
			propList = new IWPropertyList(getPropertiesRealPath(), pathWithinPropertiesFolder, autocreate);
		}
		else {
			propList = new IWPropertyList(inStream);
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

	@Override
	public InputStream getResourceInputStream(String pathWithinBundle) throws IOException {
		JarEntry entry = jarModule.getJarEntry(pathWithinBundle);

		if (entry == null) {
			if (pathWithinBundle.startsWith(CoreConstants.SLASH)) {
				pathWithinBundle = pathWithinBundle.substring(1);
				entry = jarModule.getJarEntry(pathWithinBundle);
			}
			if (entry == null)
				throw new FileNotFoundException("File not found inside jar module " + jarModule.getModuleIdentifier() + ": " + pathWithinBundle);
		}
		InputStream inStream = jarModule.getInputStream(entry);
		return inStream;
	}

	/**
	 * Returns time of jar entry identified by <code>pathWithinBundle</code>.
	 * @param pathWithinBundle resource path within jar file
	 * @return modification time of an entry, 0 if not found, or -1 if not specified
	 */
	@Override
	public long getResourceTime(String pathWithinBundle) {
		JarEntry entry = jarModule == null ? null : jarModule.getJarEntry(pathWithinBundle);
		return (entry != null ? entry.getTime() : 0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.idegaweb.DefaultIWBundle#unload(boolean)
	 */
	@Override
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
	@Override
	protected IWResourceBundle initializeResourceBundle(Locale locale) throws IOException {
		IWResourceBundle theReturn;
		try {
			InputStream defaultInputStream = getResourceInputStream(getLocalizedResourcePath(locale) + CoreConstants.SLASH + getLocalizedStringsFileName());
			IWResourceBundle defaultLocalizedResourceBundle = new IWResourceBundle(this, defaultInputStream, locale);

			if (isUsingLocalVariants()) {
				String variantPath = getLocalizedResourcePath(locale)+CoreConstants.SLASH+getLocalizedStringsVariantFileName();
				if (doesResourceExist(variantPath)) {
					InputStream variantStream = getResourceInputStream(variantPath);
					theReturn = new IWResourceBundle(defaultLocalizedResourceBundle, variantStream, locale);
				} else {
					theReturn = defaultLocalizedResourceBundle;
				}
			} else {
				theReturn = defaultLocalizedResourceBundle;
			}
		} catch (IOException e) {
			// if any error occurs, try default way (autocreated resources in webapp's bundle directory)
			theReturn = super.initializeResourceBundle(locale);
		}

		//adding resourceBundle to localized message factory
		theReturn.setBundleIdentifier(getBundleIdentifier());
		return theReturn;
	}

	@Override
	public IWResourceBundle getResourceBundle(Locale locale) {
		JarLoadedResourceBundle jarReturn = (JarLoadedResourceBundle) getApplication().getMessageFactory()
				.getResource(JarLoadedResourceBundle.RESOURCE_IDENTIFIER, getBundleIdentifier(), locale);
		IWResourceBundle theReturn = jarReturn.getResource();
		try {
			if (theReturn == null) {
				theReturn = initializeResourceBundle(locale);
			}
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, null, ex);
		}
		return theReturn;
	}

	@Override
	protected Properties initializeLocalizableStrings() {
		Properties locProps = new SortedProperties();
		Reader reader = null;
		InputStream stream = null;
		try {
			stream = getResourceInputStream("resources/" + getLocalizableStringsFileName());
			String content = StringHandler.getContentFromInputStream(stream);
			reader = new StringReader(content);
			locProps.load(reader);
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Error loading " + getLocalizableStringsFileName() + " for bundle " + getBundleIdentifier(), ex);
		} finally {
			IOUtil.close(stream);
			IOUtil.close(reader);
		}
		return locProps;
	}

}
