package com.idega.util.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
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

import org.apache.xerces.dom.DocumentImpl;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.11 $
 *
 * Last modified: $Date: 2009/06/12 10:49:54 $ by $Author: valdas $
 */
public class XmlUtil {

	public static final String XMLNS_NAMESPACE_ID = "xmlns";
	public static final String XHTML_NAMESPACE_ID = "xhtml";
	
	public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

	private XmlUtil() { }
	
	private static final Logger logger = Logger.getLogger(XmlUtil.class.getName());
	
	private volatile static DocumentBuilderFactory factory;
	private volatile static DocumentBuilderFactory factoryNoNamespace;
	
	private static final List<AdvancedProperty> ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(
			new AdvancedProperty("http://apache.org/xml/properties/dom/document-class-name", DocumentImpl.class.getName())
	));
	private static final List<AdvancedProperty> FEATURES = Collections.unmodifiableList(Arrays.asList(
			new AdvancedProperty("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", Boolean.FALSE.toString()),
			new AdvancedProperty("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE.toString())
	));
	
	public static DocumentBuilder getXMLBuilder() {
		try {
			return getDocumentBuilder(true);
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "Error getting document builder", e);
		}
		return null;
	}
		
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
		if (factory == null) {
			synchronized (XmlUtil.class) {
				if (factory == null) {
					factory = getFactory(Boolean.TRUE);
				}
			}
		}
		return factory;
	}
	
	private static DocumentBuilderFactory getDocumentBuilderFactory(boolean namespaceAware) {
		if (namespaceAware)
			return getDocumentBuilderFactoryNSAware();
		
		if (factoryNoNamespace == null) {
			synchronized (XmlUtil.class) {
				if(factoryNoNamespace == null) {
					factoryNoNamespace = getFactory(Boolean.FALSE);
				}
			}
		}
		return factoryNoNamespace;
	}
	
	private static DocumentBuilderFactory getFactory(boolean namespaceAware) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		factory.setNamespaceAware(namespaceAware);
		factory.setValidating(Boolean.FALSE);
		
		for (AdvancedProperty attribute: ATTRIBUTES) {
	    	factory.setAttribute(attribute.getId(), attribute.getValue());
	    }
		
		for (AdvancedProperty feature: FEATURES) {
			try {
				factory.setFeature(feature.getId(), Boolean.valueOf(feature.getValue()));
			} catch (ParserConfigurationException e) {
				logger.log(Level.WARNING, "Error setting features", e);
			}
		}
		
		return factory;
	}
	
	public static Document getXMLDocument(String source, boolean namespaceAware) {
		return getXMLDocument(source, namespaceAware, Boolean.TRUE);
	}
	
	private static Document getXMLDocument(String source, boolean namespaceAware, boolean reTry) {
		if (source == null) {
			logger.warning("Source does not provided - unable to generate XML document");
			return null;
		}
		
		InputStream stream = null;
		try {
			stream = StringHandler.getStreamFromString(source);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting InputStream from source: " + (source.length() > 30 ? source.substring(0, 30) : ""));
			return null;
		}
		
		Document doc = null;
		try {
			doc = getXMLDocumentWithException(stream, namespaceAware);
		} catch (Exception e) {
			if (reTry) {
				logger.warning("Error generating XML document from source:\n" + source + "\nWill try to clean given source and to re-generate XML");
			} else {
				logger.log(Level.SEVERE, "Error generating XML document from source:\n" + (source.length() > 30 ? source.substring(0, 30) : ""));
			}
		}
		if (doc == null && reTry) {
			doc = getXMLDocument(getCleanedSource(source), namespaceAware, Boolean.FALSE);
		}
		return doc;
	}
	
	private static String getCleanedSource(String source) {
		BuilderService builderService = null;
		try {
			builderService = BuilderServiceFactory.getBuilderService(IWMainApplication.getDefaultIWApplicationContext());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting " + BuilderService.class, e);
		}
		
		if (builderService == null) {
			return null;
		}
		
		String cleanedSource = builderService.getCleanedHtmlContent(source, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
		return cleanedSource;
	}
	
	public static Document getXMLDocument(InputStream stream) {
		return getXMLDocument(stream, true);
	}
		
	public static Document getXMLDocument(InputStream stream, boolean namespaceAware) {
		try {
			return getXMLDocumentWithException(stream, namespaceAware);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error generating XML document", e);
		}
		return null;
	}
	
	private static Document getXMLDocumentWithException(InputStream stream, boolean namespaceAware) throws Exception {
		if (stream == null) {
			return null;
		}
		
		Reader reader = null;
		try {
			reader = new InputStreamReader(stream, CoreConstants.ENCODING_UTF8);
			return getDocumentBuilder(namespaceAware).parse(new InputSource(reader));
		} finally {
			IOUtil.closeInputStream(stream);
			IOUtil.closeReader(reader);
		}
	}
	
	public static org.jdom.Document getJDOMXMLDocument(InputStream stream) {
		return getJDOMXMLDocument(getXMLDocument(stream, true));
	}
	
	public static org.jdom.Document getJDOMXMLDocument(InputStream stream, boolean namespaceAware) {
		return getJDOMXMLDocument(getXMLDocument(stream, namespaceAware));
	}
	
	public static org.jdom.Document getJDOMXMLDocument(String source) {
		return getJDOMXMLDocument(source, true);
	}
	
	public static org.jdom.Document getJDOMXMLDocument(String source, boolean namespaceAware) {
		return getJDOMXMLDocument(getXMLDocument(source, namespaceAware));
	}
	
	public static org.jdom.Document getJDOMXMLDocument(Document document) {
		if (document == null) {
			return null;
		}
		
		DOMBuilder domBuilder = new DOMBuilder();
		return domBuilder.build(document);
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
            logger.log(Level.WARNING, "Error printing node's content", e);
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
    	Format format = Format.getPrettyFormat();
    	format.setExpandEmptyElements(true);
    	format.setOmitDeclaration(IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings().getBoolean(CoreConstants.APPLICATION_PROPERTY_OMIT_DECLARATION, false));
    	outputter.setFormat(format);
    	return outputter.outputString(document);
    }
	
	public static List<Element> getElementsByXPath(Object container, String expression) {
		return getElementsByXPath(container, expression, CoreConstants.EMPTY);
	}
	
	public static List<Element> getElementsByXPath(Object container, String expression, String nameSpaceId) {
		if (container == null || expression == null) {
			return null;
		}
		
		Namespace namespace = StringUtil.isEmpty(nameSpaceId) ? null : Namespace.getNamespace(nameSpaceId, XHTML_NAMESPACE);
		List<Element> elements = getElementsByXPath(container, expression, namespace);
		
		return ListUtil.isEmpty(elements) ?
				nameSpaceId == null ?
						getElementsByXPath(container, expression, XMLNS_NAMESPACE_ID) :
						null :
				elements;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Element> getElementsByXPath(Object container, String expression, Namespace namespace) {
		JDOMXPath xPath = null;
		String xPathExpression = null;
		try {
			boolean validNameSpace = false;
			xPathExpression = expression;
			
			String prefix = XMLNS_NAMESPACE_ID;
			if (namespace != null && namespace.getURI() != null) {
				prefix = StringUtil.isEmpty(namespace.getPrefix()) ? prefix : namespace.getPrefix();
				
				xPathExpression = "//" + (prefix.equals(XHTML_NAMESPACE_ID) ? CoreConstants.EMPTY : (prefix + CoreConstants.COLON)) + expression;
				validNameSpace = true;
			}
			
			xPath = new JDOMXPath(xPathExpression);
			
			if (validNameSpace) {
				xPath.addNamespace(prefix, namespace.getURI());
			}
			
			return xPath.selectNodes(container);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error selecting elements by XPath expression: " + xPathExpression, e);
		}
		
		return null;
	}
}