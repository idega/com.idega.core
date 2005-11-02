/*
 * $Id: AuthenticationBusinessBean.java,v 1.2 2005/11/02 15:57:47 eiki Exp $
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
 *  Last modified: $Date: 2005/11/02 15:57:47 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public class AuthenticationBusinessBean extends IBOServiceBean implements AuthenticationBusiness{
	
	Map authenticationListeners = new HashMap();

	public AuthenticationBusinessBean(){}

	public void addAuthenticationListener(AuthenticationListener listener){
		String listenerName = listener.getAuthenticationListenerName();
		if(!authenticationListeners.containsKey(listenerName)){
			authenticationListeners.put(listenerName, listener);
		}
	}
		
	public void callOnLogonMethodInAllAuthenticationListeners(IWContext iwc, User user){
		//do we need to worry about thread problems?
		Collection listeners = authenticationListeners.values();
		
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			AuthenticationListener listener = (AuthenticationListener) iter.next();
			listener.onLogon(iwc, user);
		}
		
	}
	
	public void callOnLogoffMethodInAllAuthenticationListeners(IWContext iwc, User user){
		//do we need to worry about thread problems?
		Collection listeners = authenticationListeners.values();
		
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			AuthenticationListener listener = (AuthenticationListener) iter.next();
			listener.onLogoff(iwc, user);
		}
		
	}
	
	
}
