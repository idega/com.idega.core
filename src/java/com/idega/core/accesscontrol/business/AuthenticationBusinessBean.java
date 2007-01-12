/*
 * $Id: AuthenticationBusinessBean.java,v 1.3.2.1 2007/01/12 19:31:56 idegaweb Exp $
 * Created on Nov 1, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.idega.business.IBOServiceBean;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;

/**
 * A service bean that is/will be used for all standard idegaweb authentication methods.<br>
 * Also provides plugable behavior for objects that want to be notified when a user logs on and off (see AuthenticationListener interface).<br>
 * This bean is supposed to gradually replace LoginBusinessBean and AccessController/AccessControl and weed out static and obsolete methods.
 * 
 *  Last modified: $Date: 2007/01/12 19:31:56 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.3.2.1 $
 */
public class AuthenticationBusinessBean extends IBOServiceBean implements AuthenticationBusiness{
	
	Map authenticationListeners = new HashMap();

	public AuthenticationBusinessBean(){}

	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationListener
	 */
	public void addAuthenticationListener(AuthenticationListener listener){
		String listenerName = listener.getAuthenticationListenerName();
		if(!this.authenticationListeners.containsKey(listenerName)){
			this.authenticationListeners.put(listenerName, listener);
		}
	}
		
	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationListener
	 */
	public void callOnLogonMethodInAllAuthenticationListeners(IWContext iwc, User user) throws ServletFilterChainInterruptException{
		//do we need to worry about thread problems?
		Collection listeners = this.authenticationListeners.values();
		
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			AuthenticationListener listener = (AuthenticationListener) iter.next();
			listener.onLogon(iwc, user);
		}
		
	}
	
	/**
	 * @see com.idega.core.accesscontrol.business.AuthenticationListener
	 */
	public void callOnLogoffMethodInAllAuthenticationListeners(IWContext iwc, User user) throws ServletFilterChainInterruptException{
		//do we need to worry about thread problems?
		Collection listeners = this.authenticationListeners.values();
		
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			AuthenticationListener listener = (AuthenticationListener) iter.next();
			listener.onLogoff(iwc, user);
		}
		
	}
	
	
}
