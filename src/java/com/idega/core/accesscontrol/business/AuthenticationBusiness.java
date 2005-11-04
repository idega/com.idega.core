/*
 * $Id: AuthenticationBusiness.java,v 1.3 2005/11/04 14:18:27 eiki Exp $
 * Created on Nov 4, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import com.idega.business.IBOService;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2005/11/04 14:18:27 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface AuthenticationBusiness extends IBOService {

	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationBusinessBean#addAuthenticationListener
	 */
	public void addAuthenticationListener(AuthenticationListener listener) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationBusinessBean#callOnLogonMethodInAllAuthenticationListeners
	 */
	public void callOnLogonMethodInAllAuthenticationListeners(IWContext iwc, User user)
			throws ServletFilterChainInterruptException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationBusinessBean#callOnLogoffMethodInAllAuthenticationListeners
	 */
	public void callOnLogoffMethodInAllAuthenticationListeners(IWContext iwc, User user)
			throws ServletFilterChainInterruptException, java.rmi.RemoteException;
}
