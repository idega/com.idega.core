package com.idega.xml;

import java.io.Serializable;

import org.jdom.DocType;

public class XMLDocType implements Serializable {
	
	DocType type = null;
	
	public XMLDocType(String elementName) {
		type = new DocType(elementName);
	}

	public XMLDocType(String elementName, String systemID) {
		type = new DocType(elementName, systemID);
	}

	public XMLDocType(String elementName, String publicID, String systemID) {
		type = new DocType(elementName, publicID, systemID);
	}
	
	public Object getDocType() {
		return type;
	}
}
