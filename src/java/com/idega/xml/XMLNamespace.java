/*
 * $Id: XMLNamespace.java,v 1.1 2005/02/15 17:11:56 joakim Exp $
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
 * Last modified: $Date: 2005/02/15 17:11:56 $ by $Author: joakim $
 *
 * @author Joakim Johnson
 * @version $Revision: 1.1 $
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
	 * This object really returns a org.jdom.Namespace type but is declared Object becaluse of jdom dependency issues.
	 * @return the set Namespace instance
	 */
	protected Object getNamespace() {
		return namespace;
	}
}
