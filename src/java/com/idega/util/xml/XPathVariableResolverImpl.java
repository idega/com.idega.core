package com.idega.util.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/10/30 21:55:22 $ by $Author: civilis $
 */
public final class XPathVariableResolverImpl implements XPathVariableResolver {
	
	public XPathVariableResolverImpl() { }

	public Object resolveVariable(QName variable) {

		if(variable == null)
			throw new NullPointerException("Variable parameter not provided");
		
		return getVariables().get(variable);
	}
	
	private Map<QName, Object> variables;

	public Map<QName, Object> getVariables() {
		
		if(variables == null)
			variables = new HashMap<QName, Object>();
		
		return variables;
	}

	public void setVariables(Map<QName, Object> variables) {
		this.variables = variables;
	}
	
	public void addVariable(QName variable, Object value) {
		getVariables().put(variable, value);
	}
	
	public void clearVariables() {
		if(variables != null)
			variables.clear();
	}
}