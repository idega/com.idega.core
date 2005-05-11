/*
 * $Id: XMLNamespace.java,v 1.3 2005/05/11 18:20:34 gummi Exp $
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import org.jdom.Namespace;


/**
 * A wrapper to hide JDOM in case we want to replace it later on.
 * 
 * Last modified: $Date: 2005/05/11 18:20:34 $ by $Author: gummi $
 *
 * @author Joakim Johnson
 * @version $Revision: 1.3 $
 */
public class XMLNamespace {
	Namespace namespace;
	
	/**
	 * Constructor
	 * @param nsStr the namespace string. Typically it will be something like."http://xmlns.idega.com/block/article/document"
	 */
	public XMLNamespace(String nsStr) {
		namespace = Namespace.getNamespace(nsStr);
	}
	
	/**
	 * Constructor
	 * @param nsStr the namespace string. Typically it will be something like."http://xmlns.idega.com/block/article/document"
	 * @param prefix the prefix string.
	 */
	public XMLNamespace(String prefix,String nsStr) {
		namespace = Namespace.getNamespace(prefix,nsStr);
	}
	
	/**
	 * This object really returns a org.jdom.Namespace type but is declared Object because of jdom dependency issues.
	 * @return the set Namespace instance
	 */
	protected Object getNamespace() {
		return namespace;
	}
	
	public boolean equals(Object obj){
		return namespace.equals(obj);
	}
}
