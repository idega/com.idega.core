/*
 * $Id: LDAPInterfaceException.java,v 1.1 2006/03/21 12:31:45 tryggvil Exp $
 * Created on 21.3.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap;


/**
 * <p>
 * TODO tryggvil Describe Type LDAPInterfaceException
 * </p>
 *  Last modified: $Date: 2006/03/21 12:31:45 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class LDAPInterfaceException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3639539471240403880L;

	/**
	 * 
	 */
	public LDAPInterfaceException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public LDAPInterfaceException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LDAPInterfaceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public LDAPInterfaceException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
}
