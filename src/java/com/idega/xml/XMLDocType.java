package com.idega.xml;

import java.io.Serializable;

import org.jdom2.DocType;

public class XMLDocType implements Serializable {

	private static final long serialVersionUID = -92530920747835544L;

	DocType type = null;

	public XMLDocType(String elementName) {
		this.type = new DocType(elementName);
	}

	public XMLDocType(String elementName, String systemID) {
		this.type = new DocType(elementName, systemID);
	}

	public XMLDocType(String elementName, String publicID, String systemID) {
		this.type = new DocType(elementName, publicID, systemID);
	}

	public Object getDocType() {
		return this.type;
	}
}
