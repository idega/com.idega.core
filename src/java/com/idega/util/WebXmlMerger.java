/*
 * Created on 14.7.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.util;

import java.io.File;

/**
 * A Class to merge contents of many web.xml's into one destination web.xml file.
 * 
 * @author tryggvil
 *
 */
public class WebXmlMerger extends BundleFileMerger {
	
	public WebXmlMerger(){
		String webXmlHeader = 
			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
			+"<!DOCTYPE web-app\n"	
			+"\tPUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"\n"
			+"\t\"http://java.sun.com/dtd/web-app_2_3.dtd\">\n";
			//+"<!-- Generated file by idegaWeb please don't modify the module markers -->";
		
		setFileHeader(webXmlHeader);
		setRootXMLElement("web-app");
		setBundleFilePath("/WEB-INF/web.xml");
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
	

}
