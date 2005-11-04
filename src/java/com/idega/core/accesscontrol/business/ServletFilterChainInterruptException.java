/*
 * $Id: ServletFilterChainInterruptException.java,v 1.1 2005/11/04 14:18:27 eiki Exp $
 * Created on Nov 4, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

/**
 * Throw this exception to tell IWAuthenticator (servlet filter) to stop the servlet filter chain.
 * If you e.g. use response.sendRedirect(String) you have to do this or else you will get an illegalstateexception!
 * 
 *  Last modified: $Date: 2005/11/04 14:18:27 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class ServletFilterChainInterruptException extends Exception {
	
	public ServletFilterChainInterruptException(String message){
		super(message);
	}
}
