/*
 * $Id: IWBundleLoader.java,v 1.10 2006/10/18 13:12:34 gediminas Exp $
 * Created on 5.2.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Implementation for loading a IWBundle from a Jar file in WEB-INF/lib.
 * </p>
 *  Last modified: $Date: 2006/10/18 13:12:34 $ by $Author: gediminas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.10 $
 */
public class IWBundleLoader implements JarLoader {

	private static final Logger LOGGER = Logger.getLogger(IWBundleLoader.class.getName());

	private IWMainApplication iwma;
	
	public IWBundleLoader(IWMainApplication iwma) {
		this.iwma=iwma;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.idegaweb.JarLoader#loadJar(java.io.File,
	 *      java.util.jar.JarFile, java.lang.String)
	 */
	public void loadJar(File bundleJarFile, JarFile jarFile, String jarPath) {
		JarEntry bundleConfigFile = jarFile.getJarEntry("properties/bundle.pxml");
		if (bundleConfigFile != null) {
			LOGGER.info("Found bundle at " + jarPath);
			JarModule module = (JarModule) jarFile;
			String moduleIdentifier = module.getModuleIdentifier();
			if (moduleIdentifier != null) {
				if (!iwma.isBundleLoaded(moduleIdentifier)) {
					IWBundle bundle = new JarLoadedIWBundle(module, this.iwma);
					iwma.loadBundle(bundle);
					iwma.registerBundle(moduleIdentifier, false);
				} else {
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("Bundle " + moduleIdentifier + " was already loaded");
					}
				}
			} else {
				LOGGER.warning("Not loading " + jarPath + " because it has no moduleIdentifier");
			}
		}
	}

}
