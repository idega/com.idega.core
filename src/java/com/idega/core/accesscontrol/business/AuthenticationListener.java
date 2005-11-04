/*
 * $Id: AuthenticationListener.java,v 1.3 2005/11/04 14:18:27 eiki Exp $
 * Created on Nov 1, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import com.idega.presentation.IWContext;
import com.idega.user.data.User;

/**
 * Implement this interface and register your class as a listener for login/logout events etc. via AuthenticationBusiness.
 * 
 *  Last modified: $Date: 2005/11/04 14:18:27 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface AuthenticationListener {
	//the action methods here might need to be able to throw an interrupting exception 
	//that would stop authenticationbusiness from going to the next listener, might be necessery if a listener does a response.sendRedirect...
	/**
	 * 
	 * @return a human readable identifier for the listener
	 */
	public String getAuthenticationListenerName();
	
	/**
	 * Called when a user successfully logs on
	 * @param iwc request and response wrapper
	 * @throws ServletFilterChainInterruptException If you use methods like response.sendRedirect(String) you have throw this exception or else you will get an illegalstateexception!
	 * @param currentUser
	 */
	public void onLogon(IWContext iwc,User currentUser) throws ServletFilterChainInterruptException;
	
	/**
	 * Called when a user successfully logs off
	 * @param iwc request and response wrapper
	 * @param lastUser
	 * @throws ServletFilterChainInterruptException If you use methods like response.sendRedirect(String) you have throw this exception or else you will get an illegalstateexception!
	 */
	public void onLogoff(IWContext iwc, User lastUser) throws ServletFilterChainInterruptException;

	
}
