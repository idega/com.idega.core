/*
 * $Id: AuthenticationBusiness.java,v 1.1 2005/11/01 22:16:59 eiki Exp $
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
import com.idega.business.IBOService;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2005/11/01 22:16:59 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public interface AuthenticationBusiness extends IBOService {

	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationBusinessBean#addAuthenticationListener
	 */
	public void addAuthenticationListener(AuthenticationListener listener) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationBusinessBean#callOnLogonMethodInAllAuthenticationListeners
	 */
	public void callOnLogonMethodInAllAuthenticationListeners(HttpServletRequest request, HttpServletResponse response,
			User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationBusinessBean#callOnLogoffMethodInAllAuthenticationListeners
	 */
	public void callOnLogoffMethodInAllAuthenticationListeners(HttpServletRequest request, HttpServletResponse response,
			User user) throws java.rmi.RemoteException;
}
