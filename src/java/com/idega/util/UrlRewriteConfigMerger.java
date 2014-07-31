package com.idega.util;

/**
 * A Class to merge contents of many urlrewrite.xml's into one destination urlrewrite.xml file.
 * 
 * @author sauliusm
 */
public class UrlRewriteConfigMerger extends BundleFileMerger {
	
	public UrlRewriteConfigMerger(){
		String xmlHeader = 
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
			"<!DOCTYPE urlrewrite PUBLIC \"-//tuckey.org//DTD UrlRewrite 4.0//EN\" " +
		    "\"http://www.tuckey.org/res/dtds/urlrewrite4.0.dtd\">\n";
		setFileHeader(xmlHeader);
		setRootXMLElement("urlrewrite");
		setBundleFilePath("/WEB-INF/urlrewrite.xml");
	}
	
}
