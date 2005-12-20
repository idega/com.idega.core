/*
 * $Id: XMLNamespace.java,v 1.5 2005/12/20 16:43:46 tryggvil Exp $
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import java.io.Serializable;
import org.jdom.Namespace;


/**
 * A wrapper to hide JDOM in case we want to replace it later on.
 * 
 * Last modified: $Date: 2005/12/20 16:43:46 $ by $Author: tryggvil $
 *
 * @author Joakim Johnson
 * @version $Revision: 1.5 $
 */
public class XMLNamespace implements Serializable {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5808416982189649239L;
	//Namespace is not Serializable:
	private transient Namespace namespace;
	private String sNamespace;
	private String prefix;
	
	/**
	 * Constructor
	 * @param nsStr the namespace string. Typically it will be something like."http://xmlns.idega.com/block/article/document"
	 */
	public XMLNamespace(String nsStr) {
		sNamespace=nsStr;
		namespace = Namespace.getNamespace(nsStr);
	}
	
	/**
	 * Constructor
	 * @param nsStr the namespace string. Typically it will be something like."http://xmlns.idega.com/block/article/document"
	 * @param prefix the prefix string.
	 */
	public XMLNamespace(String prefix,String nsStr) {
		this.prefix=prefix;
		this.sNamespace=nsStr;
		namespace = Namespace.getNamespace(prefix,nsStr);
	}
	
	/**
	 * This object really returns a org.jdom.Namespace type but is declared Object because of jdom dependency issues.
	 * @return the set Namespace instance
	 */
	protected Object getNamespace() {
		if(namespace==null){
			//return namespace;
			if(prefix!=null){
				namespace=Namespace.getNamespace(prefix,sNamespace);
			}
			else{
				namespace=Namespace.getNamespace(sNamespace);
			}
		}
		return namespace;
	}
	
	public boolean equals(Object obj){
		return getNamespace().equals(obj);
	}
}
