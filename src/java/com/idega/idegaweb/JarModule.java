/*
 * $Id: JarModule.java,v 1.4 2006/09/18 12:47:11 gediminas Exp $
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
 * Extension of JarFile which implements IWModule by reading module information from
 * jar manifest.
 * </p>
 *  Last modified: $Date: 2006/09/18 12:47:11 $ by $Author: gediminas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public class JarModule extends JarFile implements IWModule {

	private File jarFile;
	
	private String moduleIdentifier;
	private String moduleName;
	private String moduleVendor;
	private String moduleVersion;
	
	/**
	 * @param jarFile
	 * @throws IOException
	 */
	public JarModule(File jarFile) throws IOException {
		super(jarFile);
		this.jarFile=jarFile;
		initialize();
	}

	/**
	 * Initializes fields with values from manifest.
	 */
	private void initialize() {
		try {
			Manifest manifest = getManifest();
			if (manifest != null) {
				Attributes attributes = manifest.getMainAttributes();
				Map entries = manifest.getEntries();
				Collection values = entries.values();
				for (Iterator iter = values.iterator(); iter.hasNext();) {
					Attributes element = (Attributes) iter.next();
					// Attributes of the entry override main attributes.
					// Correctly works only if a single entry (or none) exists in manifest
					attributes.putAll(element);
				}
				this.moduleIdentifier = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
				this.moduleVendor = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
				this.moduleVersion = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				this.moduleName = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
			}
		}
		catch (IOException e) {
			// ignore
		}
	}

	/*
	 * (non-Javadoc)
	 * 
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
		return this.moduleIdentifier;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleName()
	 */
	public String getModuleName() {
		return this.moduleName;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleVendor()
	 */
	public String getModuleVendor() {
		return this.moduleVendor;
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWModule#getModuleVersion()
	 */
	public String getModuleVersion() {
		return this.moduleVersion;
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
	
	/**
	 * Returns absolute path of the file this JarModule was loaded from
	 */
	public String getAbsolutePath() {
		return jarFile.getAbsolutePath();
	}
}