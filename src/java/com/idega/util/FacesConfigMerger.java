/*
 * Created on 14.7.2004
 *
 */
package com.idega.util;

/**
 * A Class to merge contents of many faces-config.xml's into one destination faces-config.xml file.
 * 
 * @author tryggvil
 */
public class FacesConfigMerger extends BundleFileMerger {
	
	public FacesConfigMerger(){
		String xmlHeader = 
			"<?xml version=\"1.0\"?>\n"+
			"<!DOCTYPE faces-config PUBLIC\n"+
			"\t\"-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN\"\n"+
			"\t\"http://java.sun.com/dtd/web-facesconfig_1_0.dtd\">\n";
			//+"<!-- Generated file by idegaWeb please don't modify the module markers -->";
		setFileHeader(xmlHeader);
		setRootXMLElement("faces-config");
		setBundleFilePath("/WEB-INF/faces-config.xml");
	}
	
}
