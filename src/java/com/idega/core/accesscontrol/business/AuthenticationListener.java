/*
 * $Id: AuthenticationListener.java,v 1.1 2005/11/01 22:17:00 eiki Exp $
 * Created on Nov 1, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.user.data.User;

/**
 * Implement this interface and register your class as a listener for login/logout events etc. via AuthenticationBusiness.
 * 
 *  Last modified: $Date: 2005/11/01 22:17:00 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
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
	 * 
	 * @param request
	 * @param response
	 * @param currentUser
	 */
	public void onLogon(HttpServletRequest request,HttpServletResponse response,User currentUser);
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param lastUser
	 */
	public void onLogoff(HttpServletRequest request,HttpServletResponse response,User lastUser);

	
}
