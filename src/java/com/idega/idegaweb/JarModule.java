/*
 * $Id: JarModule.java,v 1.1 2006/06/15 17:53:23 tryggvil Exp $
 * Created on 12.6.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * <p>
 * TODO tryggvil Describe Type JarModule
 * </p>
 *  Last modified: $Date: 2006/06/15 17:53:23 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class JarModule extends JarFile implements IWModule {

	/**
	 * @param arg0
	 * @throws IOException
	 */
	public JarModule(File jarFile) throws IOException {
		super(jarFile);
		System.out.println("[JarModule] Loading "+jarFile.toURL());
		initialize();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initialize
	 * </p>
	 */
	private void initialize() {
		Manifest manifest;
		try {
			manifest = getManifest();
			if(manifest!=null){
				Attributes attributes = manifest.getMainAttributes();
				Map entries = manifest.getEntries();
				Collection values = entries.values();
				for (Iterator iter = values.iterator(); iter.hasNext();) {
					Attributes element = (Attributes) iter.next();
					attributes = element;
				}
				try{
					String attrIdentifier = (String) attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
					if(attrIdentifier!=null){
						this.moduleIdentifier=attrIdentifier;
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				try{
					String attrVendor = (String) attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
					if(attrVendor!=null){
						this.moduleVendor=attrVendor;
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				try{
					String attrVersion = (String) attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
					if(attrVersion!=null){
						this.moduleVersion=attrVersion;
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				try{
					String attrName = (String) attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
					if(attrName!=null){
						this.moduleName=attrName;
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String moduleIdentifier;
	private String moduleName;
	private String moduleVendor;
	private String moduleVersion;
	
	/*public JarModule(File bundleJarFile, JarFile jarFile, String jarPath){
		
	}*/

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#canLoadLazily()
	 */
	public boolean canLoadLazily() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleIdentifier()
	 */
	public String getModuleIdentifier() {
		return moduleIdentifier;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleName()
	 */
	public String getModuleName() {
		return moduleName;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleVendor()
	 */
	public String getModuleVendor() {
		return moduleVendor;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleVersion()
	 */
	public String getModuleVersion() {
		return moduleVersion;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#load()
	 */
	public void load() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#reload()
	 */
	public void reload() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#unload()
	 */
	public void unload() {
		// TODO Auto-generated method stub
	}
}
