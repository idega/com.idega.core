/*
 * $Id: IWModuleLoader.java,v 1.1 2006/02/06 12:23:49 tryggvil Exp $ Created on
 * 31.5.2005 in project com.idega.core
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import javax.faces.FacesException;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This is the class responsible for loading the bundles (the new jar method)
 * for the IWMainApplication instance.
 * </p>
 * Last modified: $Date: 2006/02/06 12:23:49 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class IWModuleLoader {

	private static final Log log = LogFactory.getLog(IWModuleLoader.class);
	IWMainApplication iwma;
	ServletContext _externalContext;
	private List jarLoaders;

	/**
	 * 
	 */
	public IWModuleLoader(IWMainApplication iwma, ServletContext sContext) {
		this.iwma = iwma;
		_externalContext = sContext;
		
		//IWBundleLoader bundleLoader = new IWBundleLoader(iwma);
		//getJarLoaders().add(bundleLoader);
		
		/*JarLoader axisLoader;
		try {
			axisLoader = (JarLoader) Class.forName("com.idega.axis.deployment.WSDDAutoDeployer").newInstance();
			getJarLoaders().add(axisLoader);
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	private ServletContext getExternalContext() {
		return _externalContext;
	}

	public void loadBundlesFromJars() {
		Set jars = getExternalContext().getResourcePaths("/WEB-INF/lib/");
		if (jars != null) {
			for (Iterator it = jars.iterator(); it.hasNext();) {
				String path = (String) it.next();
				if (path.toLowerCase().endsWith(".jar")) {
					// feedJarConfig(path);
					File file = getJarFile(path);
					JarFile jarFile;
					try {
						jarFile = new JarFile(file);
						loadJar(file, jarFile, path);
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public boolean containerExpandsWebapp() {
		// TODO: Implement this container portable
		return true;
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
		List loaders = getJarLoaders();
		for (Iterator iter = loaders.iterator(); iter.hasNext();) {
			JarLoader loader = (JarLoader) iter.next();
			loader.loadJar(bundleJarFile,jarFile,jarPath);
		}
		
	}
	
	/**
	 * <p>
	 * Gets the jar file in a container portable way
	 * </p>
	 * 
	 * @param jarPath
	 * @return
	 * @throws FacesException
	 */
	private File getJarFile(String jarPath) throws FacesException {
		try {
			// not all containers expand archives, so we have to do it the
			// generic way:
			//1. If the container allows to read the jar file directly:
			if (containerExpandsWebapp()) {
				String webappDir = iwma.getRealPath("/");
				//File webappDir = new File(iwma.getRealPath("/"));
				File file = new File(webappDir,jarPath);
				return file;
			}
//			 2. get the stream from external context
			else {
				InputStream in = getExternalContext().getResourceAsStream(jarPath);
				if (in == null) {
					if (jarPath.startsWith("/")) {
						in = getExternalContext().getResourceAsStream(jarPath.substring(1));
					}
					else {
						in = getExternalContext().getResourceAsStream("/" + jarPath);
					}
				}
				if (in == null) {
					log.error("Resource " + jarPath + " not found");
					return null;
				}
				// 2. search the jar stream for META-INF/faces-config.xml
				// JarInputStream jar = new JarInputStream(in);
				// JarEntry entry = jar.getNextJarEntry();
				/*
				 * boolean found = false;
				 * 
				 * while (entry != null) { if
				 * (entry.getName().equals("properties/bundle.pxml")) { if
				 * (log.isDebugEnabled()) log.debug("bundle.pxml found in " +
				 * jarPath); found = true; break; } entry =
				 * jar.getNextJarEntry(); } jar.close();
				 */
				File tmp = null;
				// 3. if properties/bundle.pxml was found, extract the jar and
				// copy it to a temp file; hand over the temp file
				// to the parser and delete it afterwards
				// if (found)
				// {
				tmp = File.createTempFile("idegaweb", ".jar");
				in = getExternalContext().getResourceAsStream(jarPath);
				FileOutputStream out = new FileOutputStream(tmp);
				byte[] buffer = new byte[4096];
				int r;
				while ((r = in.read(buffer)) != -1) {
					out.write(buffer, 0, r);
				}
				out.close();
				/*
				 * JarFile jarFile = new JarFile(tmp); try {
				 *  } finally { jarFile.close(); tmp.delete(); }
				 */
				// } else
				// {
				// if (log.isDebugEnabled()) log.debug("Jar " + jarPath + "
				// contains no faces-config.xml");
				// }
				return tmp;
			}
		}
		catch (Exception e) {
			throw new FacesException(e);
		}
	}

	
	/**
	 * @return Returns the jarLoaders.
	 */
	public List getJarLoaders() {
		if(jarLoaders==null){
			jarLoaders = new ArrayList();
		}
		return jarLoaders;
	}

	
	/**
	 * @param jarLoaders The jarLoaders to set.
	 */
	public void setJarLoaders(List jarLoaders) {
		this.jarLoaders = jarLoaders;
	}

}
