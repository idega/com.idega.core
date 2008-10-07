package com.idega.util.xml;


/**
 * when using xpath variables, access to XPathUtil instance needs to be synchronized
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/10/07 13:34:11 $ by $Author: civilis $
 */
public class Prefix {

	private String prefix;
	private String namespace;
	
	public Prefix(String prefix, String namespace) {
		
		if(prefix == null || namespace == null)
			throw new IllegalArgumentException("No nulls accepted. Prefix="+prefix+", namespace="+namespace);
		
		this.prefix = prefix;
		this.namespace = namespace;
	}
	
	public String getPrefix() {
		return prefix;
	}
	public String getNamespace() {
		return namespace;
	}
}