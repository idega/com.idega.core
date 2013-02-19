/*
 * $Id: XMLNamespace.java,v 1.6 2006/04/09 12:13:14 laddi Exp $
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import java.io.Serializable;

import org.jdom2.Namespace;


/**
 * A wrapper to hide JDOM in case we want to replace it later on.
 *
 * Last modified: $Date: 2006/04/09 12:13:14 $ by $Author: laddi $
 *
 * @author Joakim Johnson
 * @version $Revision: 1.6 $
 */
public class XMLNamespace implements Serializable {

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
		this.sNamespace=nsStr;
		this.namespace = Namespace.getNamespace(nsStr);
	}

	/**
	 * Constructor
	 * @param nsStr the namespace string. Typically it will be something like."http://xmlns.idega.com/block/article/document"
	 * @param prefix the prefix string.
	 */
	public XMLNamespace(String prefix,String nsStr) {
		this.prefix=prefix;
		this.sNamespace=nsStr;
		this.namespace = Namespace.getNamespace(prefix,nsStr);
	}

	/**
	 * This object really returns a org.jdom.Namespace type but is declared Object because of jdom dependency issues.
	 * @return the set Namespace instance
	 */
	protected Object getNamespace() {
		if(this.namespace==null){
			//return namespace;
			if(this.prefix!=null){
				this.namespace=Namespace.getNamespace(this.prefix,this.sNamespace);
			}
			else{
				this.namespace=Namespace.getNamespace(this.sNamespace);
			}
		}
		return this.namespace;
	}

	@Override
	public boolean equals(Object obj){
		return getNamespace().equals(obj);
	}
}
