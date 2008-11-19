package com.idega.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.input.DOMBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.StringHandler;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.5 $
 *
 * Last modified: $Date: 2008/11/19 12:25:19 $ by $Author: valdas $
 */
public class XmlUtil {

	private XmlUtil() { }
	
	private static final Logger logger = Logger.getLogger(XmlUtil.class.getName());
	
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
	
	public static Document getXMLDocument(String source) {
		InputStream stream = null;
		try {
			stream = StringHandler.getStreamFromString(source);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting InputStream from source: " + source, e);
			return null;
		}
		
		return getXMLDocument(stream);
	}
	
	public static Document getXMLDocument(InputStream stream) {
		if (stream == null) {
			return null;
		}
		
		Reader reader = null;
		try {
			reader = new InputStreamReader(stream, CoreConstants.ENCODING_UTF8);
			return getDocumentBuilder().parse(new InputSource(reader));
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Error generating XML document", e);
		} finally {
			IOUtil.closeInputStream(stream);
			closeReader(reader);
		}
		
		return null;
	}
	
	public static org.jdom.Document getJDOMXMLDocument(InputStream stream) {
		return getJDOMXMLDocument(getXMLDocument(stream));
	}
	
	public static org.jdom.Document getJDOMXMLDocument(String source) {
		return getJDOMXMLDocument(getXMLDocument(source));
	}
	
	public static org.jdom.Document getJDOMXMLDocument(Document document) {
		if (document == null) {
			return null;
		}
		
		DOMBuilder domBuilder = new DOMBuilder();
		return domBuilder.build(document);
	}
	
	private static void closeReader(Reader reader) {
		if (reader == null) {
			return;
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}