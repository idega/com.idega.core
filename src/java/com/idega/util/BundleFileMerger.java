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
	
	
	
	public BundleFileMerger(){
		String webXmlHeader = 
			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
			+"<!DOCTYPE web-app\n"	
			+"\tPUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"\n"
			+"\thttp://java.sun.com/dtd/web-app_2_3.dtd\">\n"
			+"<!-- Generated file by idegaWeb please don't modify the module markers -->";
		
		setFileHeader(webXmlHeader);
	}
	
	/**
	 * Test method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		performWebXmlsTest();
	}
	
	/**
	 * Test method
	 * @throws Exception
	 */
	public static void performWebXmlsTest()throws Exception{
		
		BundleFileMerger instance = new BundleFileMerger();

		//doTag: bundlesFolder = /Users/tryggvil/idega/eclipse/maven/applications/base/target/com.idega.idegaweb.base/idegaweb/bundles
		//doTag: toFile = /Users/tryggvil/idega/eclipse/maven/applications/base/target/com.idega.idegaweb.base/WEB-INF/web.xml

		//String sBundlesDir = "/idega/eclipse/maven/bundles";
		String sBundlesDir = "/Users/tryggvil/idega/eclipse/maven/applications/base/target/com.idega.idegaweb.base/idegaweb/bundles";
		//String sFromFile = "/tmp/web.xml";
		//String sFromFile = "/Users/tryggvil/idega/eclipse/maven/applications/base/WEB-INF/web.xml";
		//String sToFile = "/tmp/web.xml";
		String sToFile = "/Users/tryggvil/idega/eclipse/maven/applications/base/target/com.idega.idegaweb.base/WEB-INF/web.xml";
		
		File bundlesDir = new File(sBundlesDir);
		
		instance.setBundlesFolder(bundlesDir);
		
		

		File toFile = new File(sToFile);
		if(!toFile.exists()){
			toFile.createNewFile();
		}
		/*File fromFile = new File(sFromFile);
		if(fromFile.exists()){
			FileReader reader = new FileReader(fromFile);
			Reader input = new BufferedReader(reader);
			instance.setInput(input);
		}*/
		
		//FileWriter output = new FileWriter(toFile);
		
		instance.setOutputFile(toFile);
		instance.process();
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
			String sWebXml =path+"/WEB-INF/web.xml";
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
