/*
 * $Id: IWModuleLoader.java,v 1.9 2007/10/02 04:47:58 valdas Exp $ Created on
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
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * <p>
 * This is the class responsible for loading the bundles (the new jar method)
 * for the IWMainApplication instance.
 * </p>
 * Last modified: $Date: 2007/10/02 04:47:58 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.9 $
 */
public class IWModuleLoader {

	private static final Logger LOGGER = Logger.getLogger(IWBundleLoader.class.getName());

	IWMainApplication iwma;
	ServletContext _externalContext;
	private List<JarLoader> jarLoaders;

	private String defaultLibPath = "/WEB-INF/lib/";
	
	/**
	 * @deprecated
	 * 
	 * There is no need to send ServletContext. Instead of it can be used iwma.getServletContext().
	 */
	public IWModuleLoader(IWMainApplication iwma, ServletContext sContext) {
		this.iwma = iwma;
		this._externalContext = sContext;
	}

	public IWModuleLoader(IWMainApplication iwma) {
		this.iwma = iwma;
	}

	/**
	 * Tries to load a bundle from a jar, it assumes that the jar file-name begins with the bundleIdentifier
	 */
	public void tryBundleLoad(String bundleIdentifier){
		Set jars = getJarSet();
		if (jars != null) {
			for (Iterator it = jars.iterator(); it.hasNext();) {
				String path = (String) it.next();
				String pathMinusPrefix = path.substring(defaultLibPath.length(),path.length());
				if (pathMinusPrefix.startsWith(bundleIdentifier)&&pathMinusPrefix.toLowerCase().endsWith(".jar")) {
					loadJar(path);
				}
			}
		}
	}
	
	public void loadBundlesFromJars() {
		Set jars = getJarSet();
		if (jars != null) {
			for (Iterator it = jars.iterator(); it.hasNext();) {
				String path = (String) it.next();
				if (path.toLowerCase().endsWith(".jar")) {
					loadJar(path);
				}
			}
		}
	}
	
	protected Set getJarSet(){
//		Set jars = getExternalContext().getResourcePaths(defaultLibPath);
		Set jars = iwma.getResourcePaths(defaultLibPath);
		return jars;
	}

	/**
	 * <p>
	 * Loads jar file from path
	 * </p>
	 * @param path
	 */
	protected void loadJar(String path) {
		// feedJarConfig(path);
		try {
			File file = getJarFile(path);
			JarFile jarFile = new JarModule(file);
			loadJar(file, jarFile, path);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean containerExpandsWebapp() {
		// TODO: Implement this container portable
		return true;
	}


	/**
	 * Loads jar file with each defined jarLoader
	 * 
	 * @param bundleJarFile
	 * @param jarFile
	 * @param jarPath
	 */
	public void loadJar(File bundleJarFile, JarFile jarFile, String jarPath) {
		List<JarLoader> loaders = getJarLoaders();
		if (loaders.isEmpty()) {
			LOGGER.warning("No Jar Loaders defined");
			return;
		}
		for (Iterator<JarLoader> iter = loaders.iterator(); iter.hasNext();) {
			iter.next().loadJar(bundleJarFile, jarFile, jarPath);
		}
		
	}
	
	/**
	 * <p>
	 * Gets the jar file in a container portable way
	 * </p>
	 * 
	 * @param jarPath
	 * @return
	 * @throws IOException
	 */
	private File getJarFile(String jarPath) throws IOException {
//		try {
			// not all containers expand archives, so we have to do it the
			// generic way:
			//1. If the container allows to read the jar file directly:
			if (containerExpandsWebapp()) {
				String webappDir = this.iwma.getApplicationRealPath();
				//File webappDir = new File(iwma.getRealPath("/"));
				File file = new File(webappDir,jarPath);
				return file;
			}
//			 2. get the stream from external context
			else {
//				InputStream in = getExternalContext().getResourceAsStream(jarPath);
				InputStream in = iwma.getResourceAsStream(jarPath);
				if (in == null) {
					if (jarPath.startsWith("/")) {
//						in = getExternalContext().getResourceAsStream(jarPath.substring(1));
						in = iwma.getResourceAsStream(jarPath.substring(1));						
					}
					else {
//						in = getExternalContext().getResourceAsStream("/" + jarPath);
						in = iwma.getResourceAsStream("/" + jarPath);
					}
				}
				if (in == null) {
					LOGGER.warning("Resource " + jarPath + " not found in webapp archive");
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
//		}
//		catch (Exception e) {
//			throw new FacesException(e);
//		}
	}

	
	/**
	 * @return Returns the jarLoaders.
	 */
	public List<JarLoader> getJarLoaders() {
		if(this.jarLoaders==null){
			this.jarLoaders = new ArrayList<JarLoader>();
		}
		return this.jarLoaders;
	}

	
	/**
	 * @param jarLoaders The jarLoaders to set.
	 */
	public void setJarLoaders(List<JarLoader> jarLoaders) {
		this.jarLoaders = jarLoaders;
	}

}
