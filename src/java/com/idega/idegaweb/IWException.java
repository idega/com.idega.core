/*
 * $Id: IWException.java,v 1.5 2005/11/18 14:42:16 tryggvil Exp $
 * 
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

/**
 * <p>
 * This is a custom exception used in some places in idegaWeb to 
 * singal special exceptions.
 * </p>
 * Copyright: Copyright (c) 2001-2005 idega software<br/>
 * Last modified: $Date: 2005/11/18 14:42:16 $ by $Author: tryggvil $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 */
public class IWException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8977895189197995682L;

	public IWException() {
		super("IWException");
	}

	public IWException(String message) {
		super("IWException: " + message);
	}
}
