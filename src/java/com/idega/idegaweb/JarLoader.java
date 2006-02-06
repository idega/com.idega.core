/*
 * $Id: JarLoader.java,v 1.1 2006/02/06 12:23:49 tryggvil Exp $
 * Created on 5.2.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.File;
import java.util.jar.JarFile;


/**
 * <p>
 * Interface that can be implemented to intercept the loading of all jar files
 * from WEB-INF/lib in the startup of the idegaWeb application.
 * </p>
 *  Last modified: $Date: 2006/02/06 12:23:49 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface JarLoader {
	
	/**
	 * <p>
	 * Call-back method that is called by IWModuleLoader when loading each jar file.
	 * </p>
	 * @param bundleJarFile
	 * @param jarFile
	 * @param jarPath
	 */
	public void loadJar(File bundleJarFile, JarFile jarFile, String jarPath);
}
