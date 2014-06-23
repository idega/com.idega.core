package com.idega.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.dom.DocumentImpl;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.11 $
 *
 *          Last modified: $Date: 2009/06/12 10:49:54 $ by $Author: valdas $
 */
public class XmlUtil {

	private static final Logger LOGGER = Logger.getLogger(XmlUtil.class
			.getName());

	public static final String XMLNS_NAMESPACE_ID = "xmlns";
	public static final String XHTML_NAMESPACE_ID = "xhtml";

	public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

	private XmlUtil() {
	}

	private static final Logger logger = Logger.getLogger(XmlUtil.class
			.getName());

	private volatile static DocumentBuilderFactory factory;
	private volatile static DocumentBuilderFactory factoryNoNamespace;

	private static final List<AdvancedProperty> ATTRIBUTES = Collections
			.unmodifiableList(Arrays.asList(new AdvancedProperty(
					"http://apache.org/xml/properties/dom/document-class-name",
					DocumentImpl.class.getName())));
	private static final List<AdvancedProperty> FEATURES = Collections
			.unmodifiableList(Arrays
					.asList(new AdvancedProperty(
							"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
							Boolean.FALSE.toString()),
							new AdvancedProperty(
									"http://apache.org/xml/features/nonvalidating/load-external-dtd",
									Boolean.FALSE.toString())));

	public static DocumentBuilder getXMLBuilder() {
		try {
			return getDocumentBuilder(true);
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "Error getting document builder", e);
		}
		return null;
	}

	public static DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		return getDocumentBuilder(true);
	}

	public static DocumentBuilder getDocumentBuilder(boolean namespaceAware)
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = getDocumentBuilderFactory(namespaceAware);

		return factory.newDocumentBuilder();
	}

	private static DocumentBuilderFactory getDocumentBuilderFactoryNSAware() {
		if (factory == null) {
			factory = getFactory(Boolean.TRUE);
		}
		return factory;
	}

	private static DocumentBuilderFactory getDocumentBuilderFactory(
			boolean namespaceAware) {
		if (namespaceAware)
			return getDocumentBuilderFactoryNSAware();

		if (factoryNoNamespace == null) {
			factoryNoNamespace = getFactory(Boolean.FALSE);
		}
		return factoryNoNamespace;
	}

	private static DocumentBuilderFactory getFactory(boolean namespaceAware) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(namespaceAware);
		factory.setValidating(Boolean.FALSE);

		for (AdvancedProperty attribute : ATTRIBUTES) {
			factory.setAttribute(attribute.getId(), attribute.getValue());
		}

		for (AdvancedProperty feature : FEATURES) {
			try {
				factory.setAttribute(feature.getId(),
						Boolean.valueOf(feature.getValue()));
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error setting features", e);
			}
		}

		return factory;
	}

	public static Document getXMLDocument(String source, boolean namespaceAware) {
		return getXMLDocument(source, namespaceAware, Boolean.TRUE);
	}

	private static Document getXMLDocument(String source,
			boolean namespaceAware, boolean reTry) {
		if (source == null) {
			logger.warning("Source does not provided - unable to generate XML document");
			return null;
		}

		InputStream stream = null;
		try {
			stream = StringHandler.getStreamFromString(source);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting InputStream from source: "
					+ (source.length() > 30 ? source.substring(0, 30) : ""));
			return null;
		}

		Document doc = null;
		try {
			doc = getXMLDocumentWithException(stream, namespaceAware);
		} catch (Exception e) {
			if (reTry) {
				logger.warning("Error generating XML document from source: "
						+ (source.length() > 30 ? source.substring(0, 30) : "")
						+ "\nWill try to clean given source and to re-generate XML");
			} else {
				logger.log(
						Level.SEVERE,
						"Error generating XML document from source:\n"
								+ (source.length() > 30 ? source.substring(0,
										30) : ""));
			}
		}
		if (doc == null && reTry) {
			doc = getXMLDocument(getCleanedSource(source), namespaceAware,
					Boolean.FALSE);
		}
		return doc;
	}

	private static String getCleanedSource(String source) {
		BuilderService builderService = null;
		try {
			builderService = BuilderServiceFactory
					.getBuilderService(IWMainApplication
							.getDefaultIWApplicationContext());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error getting " + BuilderService.class, e);
		}

		if (builderService == null) {
			return null;
		}

		String cleanedSource = builderService.getCleanedHtmlContent(source,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
		return cleanedSource;
	}

	public static Document getXMLDocument(InputStream stream) {
		return getXMLDocument(stream, true);
	}

	public static Document getXMLDocument(InputStream stream,
			boolean namespaceAware) {
		try {
			return getXMLDocumentWithException(stream, namespaceAware);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error generating XML document", e);
		}
		return null;
	}

	private static Document getXMLDocumentWithException(InputStream stream,
			boolean namespaceAware) throws Exception {
		if (stream == null) {
			return null;
		}

		Reader reader = null;
		try {
			reader = new InputStreamReader(stream, CoreConstants.ENCODING_UTF8);
			return getDocumentBuilder(namespaceAware).parse(
					new InputSource(reader));
		} finally {
			IOUtil.closeInputStream(stream);
			IOUtil.closeReader(reader);
		}
	}

	public static org.jdom2.Document getJDOMXMLDocument(InputStream stream) {
		return getJDOMXMLDocument(getXMLDocument(stream, true));
	}

	public static org.jdom2.Document getJDOMXMLDocument(InputStream stream,
			boolean namespaceAware) {
		return getJDOMXMLDocument(getXMLDocument(stream, namespaceAware));
	}

	public static org.jdom2.Document getJDOMXMLDocument(String source) {
		return getJDOMXMLDocument(source, true);
	}

	public static org.jdom2.Document getJDOMXMLDocument(String source,
			boolean namespaceAware) {
		return getJDOMXMLDocument(getXMLDocument(source, namespaceAware));
	}

	public static org.jdom2.Document getJDOMXMLDocument(Document document) {
		if (document == null)
			return null;

		DOMBuilder domBuilder = new DOMBuilder();
		return domBuilder.build(document);
	}

	private static final String XMLNAMESPACE = "xmlns";

	/**
	 * Set a namespace/prefix on an element if it is not set already. First off,
	 * it searches for the element for the prefix associated with the specified
	 * namespace. If the prefix isn't null, then this is returned. Otherwise, it
	 * creates a new attribute using the namespace/prefix passed as parameters.
	 *
	 * @param element
	 * @param namespace
	 * @param prefix
	 * @return the prefix associated with the set namespace
	 */
	public static String setNamespace(org.w3c.dom.Element element,
			String namespace, String prefix) {

		String pre = getPrefixRecursive(element, namespace);
		if (pre != null) {
			return pre;
		}
		element.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:"
				+ prefix, namespace);
		return prefix;
	}

	public static String getPrefixRecursive(org.w3c.dom.Element el, String ns) {
		String prefix = getPrefix(el, ns);
		if (prefix == null && el.getParentNode() instanceof Element) {
			prefix = getPrefixRecursive(
					(org.w3c.dom.Element) el.getParentNode(), ns);
		}
		return prefix;
	}

	public static String getPrefix(org.w3c.dom.Element el, String ns) {
		NamedNodeMap atts = el.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node node = atts.item(i);
			String name = node.getNodeName();
			if (ns.equals(node.getNodeValue())
					&& (name != null && (XMLNAMESPACE.equals(name) || name
							.startsWith(XMLNAMESPACE + ":")))) {
				return node.getPrefix();
			}
		}
		return null;
	}

	/**
	 * <p>
	 * taken from org.chiba.xml.dom.DOMUtil
	 * </p>
	 * <p>
	 * prints out formatted node content to System.out stream
	 * </p>
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
	 * <p>
	 * taken from org.chiba.xml.dom.DOMUtil
	 * </p>
	 * Serializes the specified node to the given stream. Serialization is
	 * achieved by an identity transform.
	 *
	 * @param node
	 *            the node to serialize
	 * @param stream
	 *            the stream to serialize to.
	 * @throws TransformerException
	 *             if any error ccurred during the identity transform.
	 */
	public static void prettyPrintDOM(Node node, OutputStream stream)
			throws TransformerException {
		getTransformer().transform(new DOMSource(node),
				new StreamResult(stream));
	}

	private static Transformer getTransformer() throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		return transformer;
	}

	public static void prettyPrintDOM(Node node, Writer writer) throws TransformerException {
		getTransformer().transform(new DOMSource(node), new StreamResult(writer));
	}

	public static String getPrettyPrintedDOM(Node node) {
		StringWriter writer = new StringWriter();
		try {
			prettyPrintDOM(node, writer);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	public static final Format getPrettyFormat(boolean omitDeclaration) {
		Format format = Format.getPrettyFormat();
		format.setExpandEmptyElements(true);
		format.setOmitDeclaration(omitDeclaration);
		return format;
	}

	public static final Format getPrettyFormat(IWMainApplicationSettings settings) {
		return getPrettyFormat(settings.getBoolean(CoreConstants.APPLICATION_PROPERTY_OMIT_DECLARATION, false));
	}

	public static String getPrettyJDOMDocument(org.jdom2.Document document) {
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(getPrettyFormat(IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings()));
		return outputter.outputString(document);
	}

	public static List<Element> getElementsByXPath(Content container, String expression) {
		return getElementsByXPath(container, expression, CoreConstants.EMPTY);
	}

	public static List<Element> getElementsByXPath(Content container, String expression, String nameSpaceId) {
		List<Element> elements = getContentByXPath(container, expression, nameSpaceId, Filters.element());
		if (!ListUtil.isEmpty(elements))
			return elements;

		Namespace namespace = StringUtil.isEmpty(nameSpaceId) ? null : Namespace.getNamespace(nameSpaceId, XHTML_NAMESPACE);

		if (container instanceof Element) {
			Filter<Element> f = namespace == null ? new ElementFilter(expression) : new ElementFilter(expression, namespace);
			elements = ((Element) container).getContent(f);
			if (!ListUtil.isEmpty(elements))
				return elements;
		}

		elements = getElementsByXPath(container, expression, namespace);
		if (ListUtil.isEmpty(elements)) {
			if (StringUtil.isEmpty(nameSpaceId))
				elements = getElementsByXPath(container, expression, XMLNS_NAMESPACE_ID);
			else
				elements = getElementsByXPath(container, expression, Namespace.NO_NAMESPACE);
		}
		return elements;
	}

	public static <T> List<T> getContentByXPath(Content container, String expression, Filter<T> filter) {
		List<T> content = getContentByXPath(container, expression, XHTML_NAMESPACE_ID, filter);
		if (ListUtil.isEmpty(content))
			content = getContentByXPath(container, expression, CoreConstants.EMPTY, filter);
		return content;
	}

	public static <T> List<T> getContentByXPath(Content container, String expression, String nameSpaceId, Filter<T> filter) {
		if (container == null || expression == null)
			return null;

		Namespace namespace = StringUtil.isEmpty(nameSpaceId) ?
				Namespace.NO_NAMESPACE :
				Namespace.getNamespace(nameSpaceId, XHTML_NAMESPACE);
		try {
			XPathExpression<T> xpathExpression = XPathFactory.instance().compile(expression, filter, null, namespace);
			List<T> content = xpathExpression.evaluate(container);
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Element> getElementsByXPath(Content container, String expression, Namespace namespace) {
		if (!(container instanceof Element))
			return Collections.emptyList();

		try {
			boolean validNameSpace = false;
			String prefix = XMLNS_NAMESPACE_ID;
			if (namespace != null && namespace.getURI() != null) {
				prefix = StringUtil.isEmpty(namespace.getPrefix()) ? prefix : namespace.getPrefix();
				validNameSpace = true;
			}

			Element root = (Element) container;
			org.jdom2.filter.Filter<Element> filter = null;
			if (validNameSpace) {
				filter = new ElementFilter(expression, namespace);
			} else
				filter = new ElementFilter(expression);

			List<Element> tmp = new ArrayList<Element>();
			for (Iterator<Element> results = root.getDescendants(filter); results.hasNext();)
				tmp.add(results.next());

			return tmp;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error selecting elements by expression: " + expression, e);
		}

		return null;
	}

	public static String getCleanedXml(InputStream stream) {
		String xmlString = null;
		try {
			xmlString = StringHandler.getContentFromInputStream(stream);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error cleaning XML document", e);
		}
		return getCleanedXml(xmlString);
	}

	public static String getCleanedXml(String xmlString) {
		xmlString = StringHandler.replace(xmlString, "&#0;", CoreConstants.EMPTY);
		return xmlString;
	}

	/**
	 * <p>Check all {@link Node#getChildNodes()} of given node, if it has
	 * given namespaceURI or name or both of them.</p>
	 * @param node - where to check. Not <code>null</code>.
	 * @param namespaceURI of node to search for.
	 * @param name of node to search for.
	 * @return {@link List} of {@link Node}s, which contains given criteria.
	 * {@link Collections#emptyList()} on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static List<Node> getChildNodes(org.w3c.dom.Element node,
			String namespaceURI, String name,
			String attribute, String attributeValue) {

		List<Node> items = new ArrayList<Node>();
		if (node == null || !node.hasChildNodes())
			return items;

		boolean isNamespaceSet = !StringUtil.isEmpty(namespaceURI);
		boolean isNameSet = !StringUtil.isEmpty(name);
		boolean isAttributeNameSet = !StringUtil.isEmpty(attribute);
		boolean isAttributeValueSet = !StringUtil.isEmpty(attributeValue);

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			org.w3c.dom.Element element = null;

			Node item = list.item(i);

			if (!(item instanceof org.w3c.dom.Element))
				continue;

			element = (org.w3c.dom.Element) item;

			boolean doAddItem = Boolean.TRUE;

			if (isNameSet && !name.equals(item.getLocalName()))
				doAddItem = Boolean.FALSE;
			if (isNamespaceSet && !namespaceURI.equals(item.getNamespaceURI()))
				doAddItem = Boolean.FALSE;
			if (isAttributeNameSet && StringUtil.isEmpty(element.getAttribute(attribute)))
				doAddItem = Boolean.FALSE;
			if (isAttributeValueSet && !attributeValue.equals(element.getAttribute(attribute)))
				doAddItem = Boolean.FALSE;

			if (doAddItem)
				items.add(item);

			items.addAll(getChildNodes(element, namespaceURI, name, attribute, attributeValue));
		}

		return items;
	}

	/**
	 * <p>Searches recursively for parent {@link Node} of given {@link Element}
	 * with given parameters:</p>
	 * @param element
	 * @param name
	 * @return {@link org.w3c.dom.Element} or <code>null</code> on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static org.w3c.dom.Element getParentElement(Node element, String name) {
		if (element == null)
			return null;

		Node parentNode = element.getParentNode();
		if (parentNode == null)
			return null;

		boolean isRequiredElement = Boolean.TRUE;
		if (!(parentNode instanceof org.w3c.dom.Element))
			isRequiredElement = Boolean.FALSE;

		if (!StringUtil.isEmpty(name)) {
			if (!name.equals(parentNode.getLocalName())
					&& !name.equals(parentNode.getNodeName())) {
				isRequiredElement = Boolean.FALSE;
			}
		}

		return isRequiredElement ?
				(org.w3c.dom.Element) parentNode :
				getParentElement(parentNode, name);
	}

	/**
	 * <p>Simply checks if {@link Node}s are instances of
	 * {@link org.w3c.dom.Element}s.</p>
	 * @param items to check;
	 * @return {@link org.w3c.dom.Element}s of {@link List} of given
	 * {@link Node}s or <code>null</code>.
 	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public List<Element> convertToElements(List<Node> items) {
		if (ListUtil.isEmpty(items))
			return null;

		List<Element> elements = new ArrayList<Element>();
		for (Node node : items) {
			if (node instanceof Element) {
				elements.add((Element) node);
			}
		}

		return elements;
	}

	/**
	 *
	 * <p>Converts {@link Document} to {@link Byte} array.</p>
	 * @param node to convert, not <code>null</code>;
	 * @return converted bytes or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static byte[] getBytes(Node node) {
		if (node == null) {
			return null;
		}

		Source source = new DOMSource(node);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Result result = new StreamResult(out);
		TransformerFactory factory = TransformerFactory.newInstance();

		try {
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return out.toByteArray();
		} catch (TransformerConfigurationException e) {
			LOGGER.log(Level.WARNING, "Unable to create " + Transformer.class
					+ " cause of: ", e);
		} catch (TransformerException e) {
			LOGGER.log(Level.WARNING, "Unable to transform document case of: ",
					e);
		}

		return null;
	}

	/**
	 *
	 * <p>Parses {@link InputStream} to {@link Document}.</p>
	 * @param in - {@link InputStream} to parse, not <code>null</code>;
	 * @return parsed {@link Document} or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static Document getDocument(InputStream in) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document ret = null;

		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LOGGER.log(Level.WARNING, "Unable to create parser: ", e);
		}

		try {
			ret = builder.parse(new InputSource(in));
		} catch (SAXException e) {
			LOGGER.log(Level.WARNING, "Failed to parse " + InputStream.class +
					" cause of: ", e);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to get " + InputStream.class +
					" cause of :" , e);
		}
		return ret;
	}

	/**
	 *
	 * <p>Converts whole {@link Document} to {@link String}</p>
	 * @param document to convert, not <code>null</code>;
	 * @return xml {@link Document} in {@link String} format or <code>null</code>
	 * on failure;
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static String toString(Document document) {
		if (document == null) {
			return null;
		}

		StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();

        Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			LOGGER.log(Level.WARNING,
					"Unable to creat new transformer cause of: ", e);
			return null;
		}

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        try {
			transformer.transform(new DOMSource(document), new StreamResult(sw));
		} catch (TransformerException e) {
			LOGGER.log(Level.WARNING,
					"Failed to transform document cause of: ", e);
		}

        return sw.toString();
	}
}