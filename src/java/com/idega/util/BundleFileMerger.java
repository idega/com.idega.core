/*
 * Created on 6.7.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.util;

import java.io.File;

/**
 * This class is to merge files from many bundles (WEB-INF/web.xml's primarily)
 * to a single destination file (/WEB-INF/web.xml under the webapp e.g.).
 * @author tryggvil
 */
public class BundleFileMerger extends ModuleFileMerger {
	
	private String bundleFilePath;
	
	
	public BundleFileMerger(){
	}

	/**
	 * Sets the relative path to the file in the bundle to merge from.
	 * e.g. "/WEB-INF/web.xml"
	 *
	 */
	public void setBundleFilePath(String filePath){
		this.bundleFilePath = filePath;
	}
	
	public String getBundleFilePath(){
		return bundleFilePath;
	}
	
	
	/**
	 * Sets the folder where the bundles are stored (e.g. /home/idegaweb/webapps/webapp1/idegaweb/bundles)
	 * @param bundlesDir
	 */
	public void setBundlesFolder(File bundlesDir){
		
		File[] bundles = bundlesDir.listFiles();
		for (int i = 0; i < bundles.length; i++) {
			File bundle = bundles[i];
			String path = bundle.getAbsolutePath();
			String sWebXml =path+getBundleFilePath();
			File webXml = new File(sWebXml);
			if(webXml.exists()){
				String bundleFolderName = bundle.getName();
				String bundleId = null;
				if(bundleFolderName.endsWith(".bundle")){
					bundleId = bundleFolderName.substring(0,bundleFolderName.indexOf(".bundle"));
				}
				else{
					bundleId=bundleFolderName;
				}
				addMergeInSourceFile(webXml,bundleId);
			}
		}
		
	}
	
}
