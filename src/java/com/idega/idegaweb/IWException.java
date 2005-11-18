/*
 * $Id: IWException.java,v 1.4 2005/11/18 14:40:28 tryggvil Exp $
 * 
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

/**
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 * 
 */
public class IWException extends Exception {

	public IWException() {
		super("IWException");
	}

	public IWException(String message) {
		super("IWException: " + message);
	}
}
