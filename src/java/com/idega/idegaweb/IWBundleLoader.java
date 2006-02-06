/*
 * $Id: IWBundleLoader.java,v 1.2 2006/02/06 12:23:49 tryggvil Exp $
 * Created on 5.2.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * Implementation for loading a IWBundle from a Jar file in WEB-INF/lib.
 * </p>
 *  Last modified: $Date: 2006/02/06 12:23:49 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class IWBundleLoader implements JarLoader{

	private IWMainApplication iwma;

	private static final Log log = LogFactory.getLog(IWBundleLoader.class);
	/**
	 * 
	 */
	public IWBundleLoader(IWMainApplication iwma) {
		this.iwma=iwma;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method loadBundle
	 * </p>
	 * 
	 * @param stream
	 * @param jarPath
	 * @param systemId
	 */
	public void loadJar(File bundleJarFile, JarFile jarFile, String jarPath) {
		JarEntry bundleConfigFile = jarFile.getJarEntry("properties/bundle.pxml");
		JarEntry facesConfigFile = jarFile.getJarEntry("META-INF/faces-config.xml");
		if (bundleConfigFile != null) {
			if (log.isDebugEnabled())
				log.debug("bundle.pxml found in jar " + jarPath);
			// InputStream stream = jarFile.getInputStream(configFile);
			// String systemId = "jar:" + tmp.toURL() + "!/" +
			// configFile.getName();
			// if (log.isInfoEnabled()) log.info("Reading config " + systemId);
			// _dispenser.feed(_unmarshaller.getFacesConfig(stream, systemId));
			// loadBundle(tmp,jarFile);
			// InputStream stream = jarFile.getInputStream(configFile);
			// String systemId = "jar:" + tmp.toURL() + "!/" +
			// configFile.getName();
			// if (log.isInfoEnabled()) log.info("Reading config " + systemId);
			// _dispenser.feed(_unmarshaller.getFacesConfig(stream, systemId));
			String bundleIdentifier = null;
			String version = null;
			String implementationVendor = null;
			// IWBundle bundle = new DefaultIWBundle(stream,jarPath,systemId);
			// System.out.println("Found idegaweb bundle in jar: "+jarPath+" and
			// systemId: "+systemId);
			try {
				Manifest mf = jarFile.getManifest();
				Attributes attributes = mf.getMainAttributes();
				Set keySet = attributes.keySet();
				// Map entries = mf.getEntries();
				// Set keySet = entries.keySet();
				for (Iterator iter = keySet.iterator(); iter.hasNext();) {
					Object key = iter.next();
					Object value = attributes.get(key);
					String sKey = key.toString();
					String sValue = value.toString();
					// String key = (String) iter.next();
					// String value = (String) attributes.get(key);
					if (sKey.equals("Extension-Name")) {
						bundleIdentifier = sValue;
					}
					else if (sKey.equals("Implementation-Version")) {
						version = sValue;
					}
					else if (sKey.equals("Implementation-Vendor")) {
						implementationVendor = sValue;
					}
					System.out.println(key + ":" + value);
				}
				// bundleIdentifier = (String) entries.get("Extension-Name");
				// version = (String) entries.get("Implementation-Version");
				// implementationVendor = (String)
				// entries.get("Implementation-Vendor");
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Found idegaweb bundle in jar: " + bundleJarFile);
			if (bundleIdentifier != null) {
				IWBundle bundle = iwma.getBundle(bundleIdentifier);
				if (version != null) {
					// bundle.setVersion(version);
				}
				if (implementationVendor != null) {
					// bundle.setImplementationVendor(implementatioVendor);
				}
			}
		}
	}

}
