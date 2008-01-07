package com.idega.util.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/01/07 15:05:55 $ by $Author: civilis $
 */
public class XmlUtil {

	private XmlUtil() { }
	
	private static DocumentBuilderFactory factory;
	private static DocumentBuilderFactory factoryNoNamespace;
	
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		
		DocumentBuilderFactory factory = getDocumentBuilderFactory();
		
		synchronized (factory) {
			return factory.newDocumentBuilder();
		}
	}
	
	public static DocumentBuilder getDocumentBuilder(boolean namespaceAware) throws ParserConfigurationException {
		
		DocumentBuilderFactory factory = getDocumentBuilderFactory(namespaceAware);
		
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
	
	private static synchronized DocumentBuilderFactory getDocumentBuilderFactory(boolean namespaceAware) {
		
		if(namespaceAware)
			return getDocumentBuilderFactory();
		
		if(factoryNoNamespace == null) {
			factoryNoNamespace = DocumentBuilderFactory.newInstance();
			factoryNoNamespace.setNamespaceAware(false);
			factoryNoNamespace.setValidating(false);
			factoryNoNamespace.setAttribute("http://apache.org/xml/properties/dom/document-class-name",
			"org.apache.xerces.dom.DocumentImpl");
		}
		return factoryNoNamespace;
	}
}