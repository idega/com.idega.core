package com.idega.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.StringHandler;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.7 $
 *
 * Last modified: $Date: 2009/04/24 08:38:30 $ by $Author: valdas $
 */
public class XmlUtil {

	private XmlUtil() { }
	
	private static final Logger logger = Logger.getLogger(XmlUtil.class.getName());
	
	private volatile static DocumentBuilderFactory factory;
	private volatile static DocumentBuilderFactory factoryNoNamespace;
	
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		
		return getDocumentBuilder(true);
	}
	
	public static DocumentBuilder getDocumentBuilder(boolean namespaceAware) throws ParserConfigurationException {
		
		DocumentBuilderFactory factory = getDocumentBuilderFactory(namespaceAware);
		
		synchronized (factory) {
			return factory.newDocumentBuilder();
		}
	}
	
	private static DocumentBuilderFactory getDocumentBuilderFactoryNSAware() {
	
		if(factory == null) {
			
			synchronized (XmlUtil.class) {
				
				if(factory == null) {
			
					factory = DocumentBuilderFactory.newInstance();
				    factory.setNamespaceAware(true);
				    factory.setValidating(false);
				    factory.setAttribute("http://apache.org/xml/properties/dom/document-class-name",
					"org.apache.xerces.dom.DocumentImpl");
				}
			}
		}
		return factory;
	}
	
	private static DocumentBuilderFactory getDocumentBuilderFactory(boolean namespaceAware) {
		
		if(namespaceAware)
			return getDocumentBuilderFactoryNSAware();
		
		if(factoryNoNamespace == null) {
			
			synchronized (XmlUtil.class) {
				
				if(factoryNoNamespace == null) {
			
					factoryNoNamespace = DocumentBuilderFactory.newInstance();
					factoryNoNamespace.setNamespaceAware(false);
					factoryNoNamespace.setValidating(false);
					factoryNoNamespace.setAttribute("http://apache.org/xml/properties/dom/document-class-name",
					"org.apache.xerces.dom.DocumentImpl");
				}
			}
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
	
	/**
	 * <p>taken from org.chiba.xml.dom.DOMUtil</p>
	 * <p>prints out formatted node content to System.out stream</p>
	 * 
	 * 
	 * @param node
	 */
	public static void prettyPrintDOM(Node node) {
        try {
            prettyPrintDOM(node, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    /**
     * <p>taken from org.chiba.xml.dom.DOMUtil</p>
     * Serializes the specified node to the given stream. Serialization is achieved by an identity transform.
     *
     * @param node   the node to serialize
     * @param stream the stream to serialize to.
     * @throws TransformerException if any error ccurred during the identity transform.
     */
    public static void prettyPrintDOM(Node node, OutputStream stream) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.transform(new DOMSource(node), new StreamResult(stream));
    }
    
    public static String getPrettyJDOMDocument(org.jdom.Document document) {
    	XMLOutputter outputter = new XMLOutputter();
    	outputter.setFormat(Format.getPrettyFormat());
    	return outputter.outputString(document);
    }

	@SuppressWarnings("unchecked")
	public static List<Element> getElementsByXPath(Element root, Namespace namespace, String expression) {
    	try {
    		JDOMXPath xp = new JDOMXPath(expression);
			if (namespace != null) {
				xp.addNamespace(namespace.getPrefix(), namespace.getURI());
			}
			return xp.selectNodes(root);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return null;
    }
}