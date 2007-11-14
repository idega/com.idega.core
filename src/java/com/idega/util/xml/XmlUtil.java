package com.idega.util.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/11/14 12:50:40 $ by $Author: civilis $
 */
public class XmlUtil {

	private XmlUtil() { }
	
	private static DocumentBuilderFactory factory;
	
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		
		DocumentBuilderFactory factory = getDocumentBuilderFactory();
		
		synchronized (factory) {
			return factory.newDocumentBuilder();
		}
	}
	
	private static synchronized DocumentBuilderFactory getDocumentBuilderFactory() {
	
		if(factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(true);
		    factory.setValidating(false);
		    factory.setAttribute("http://apache.org/xml/properties/dom/document-class-name",
			"org.apache.xerces.dom.DocumentImpl");
		}
		return factory;
	}
}