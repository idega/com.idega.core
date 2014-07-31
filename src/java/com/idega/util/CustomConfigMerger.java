package com.idega.util;

public class CustomConfigMerger extends BundleFileMerger {

	public CustomConfigMerger() {
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		setFileHeader(xmlHeader);
		setRootXMLElement("config");
		setBundleFilePath("/WEB-INF/config.xml");
	}

}