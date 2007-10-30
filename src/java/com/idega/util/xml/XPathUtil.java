package com.idega.util.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * when using xpath variables, access to XPathUtil instance needs to be synchronized
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/10/30 21:55:22 $ by $Author: civilis $
 */
public class XPathUtil {

	private XPathExpression xpathExpression;
	private XPathVariableResolverImpl variableResolver;
	
	public XPathUtil(String xpathExpressionStr) {
		
		NamespaceContextImpl nmspcContext = new NamespaceContextImpl();
		nmspcContext.addPrefix("xf", "http://www.w3.org/2002/xforms");
		nmspcContext.addPrefix("h", "http://www.w3.org/1999/xhtml");
		
		compileXPathExpression(xpathExpressionStr, nmspcContext);
	}
	
	public XPathUtil(String xpathExpressionStr, NamespaceContext nmspcContext) {
		
		compileXPathExpression(xpathExpressionStr, nmspcContext);
	}
	
	private void compileXPathExpression(String xpathExpressionStr, NamespaceContext nmspcContext) {
		
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			variableResolver = new XPathVariableResolverImpl();
			xpath.setXPathVariableResolver(variableResolver);
			
			if(nmspcContext != null)
				xpath.setNamespaceContext(nmspcContext);
			
			xpathExpression = xpath.compile(xpathExpressionStr);
			
		} catch (XPathException e) {
			throw new RuntimeException("Could not compile XPath expression: " + e.getMessage(), e);
		}
	}
	
	public Node getNode(Node context) {
		
		try {
			synchronized (xpathExpression) {
				return (Node)xpathExpression.evaluate(context, XPathConstants.NODE);
			}
				
		} catch (XPathException e) {
			throw new RuntimeException("Could not evaluate XPath expression: " + e.getMessage(), e);
		}
	}
	
	public NodeList getNodeset(Node context) {
		
		try {
			synchronized (xpathExpression) {
				return (NodeList)xpathExpression.evaluate(context, XPathConstants.NODESET);
			}
				
		} catch (XPathException e) {
			throw new RuntimeException("Could not evaluate XPath expression: " + e.getMessage(), e);
		}
	}
	
	public String getString(Node context) {
		
		try {
			synchronized (xpathExpression) {
				return (String)xpathExpression.evaluate(context, XPathConstants.STRING);
			}
				
		} catch (XPathException e) {
			throw new RuntimeException("Could not evaluate XPath expression: " + e.getMessage(), e);
		}
	}
	
	public Object getObject(Node context, QName objType) {

		try {
			synchronized (xpathExpression) {
				return xpathExpression.evaluate(context, objType);
			}
				
		} catch (XPathException e) {
			throw new RuntimeException("Could not evaluate XPath expression: " + e.getMessage(), e);
		}
	}
	
	public void setVariable(QName variable, Object value) {
		variableResolver.addVariable(variable, value);
	}
	
	public void setVariable(String variable, String value) {
		
		QName qvariable = new QName(variable);
		setVariable(qvariable, value);
	}
	
	public void clearVariables() {
		variableResolver.clearVariables();
	}
}