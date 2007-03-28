/*
 * $Id: CredentialBusinessBean.java,v 1.2 2007/03/28 10:09:11 civilis Exp $
 * Created on May 10, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpSession;
import com.idega.business.IBOServiceBean;
import com.idega.business.SpringBeanLookup;
import com.idega.core.accesscontrol.jaas.IWCredential;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;


/**
 * 
 * Not fully implemented. There will be more methods pretty soon.
 * 
 * 
 *  Last modified: $Date: 2007/03/28 10:09:11 $ by $Author: civilis $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class CredentialBusinessBean extends IBOServiceBean  implements CredentialBusiness{
	
	public void addCredentialsToLink(Link link, IWContext iwc) {
		HttpSession session = iwc.getSession();
		if (!LoginBusinessBean.isLoggedOn(iwc)) {
			// not logged in  - do nothing!
			return;
		}
		
		LoginSession loginSession = (LoginSession)SpringBeanLookup.getInstance().getSpringBean(iwc.getSession(), LoginSession.class);
		LoggedOnInfo loggedOnInfo  = loginSession.getLoggedOnInfo();
		if (loggedOnInfo != null) {
			Map credentials = loggedOnInfo.getCredentials();
			Iterator iterator = credentials.values().iterator();
			while (iterator.hasNext())  {
				IWCredential credential = (IWCredential) iterator.next();
				String name = credential.getName();
				Object key = credential.getKey();
				if (key != null) {
					link.addParameter(name, key.toString());
				}
			}
		}
	}
}
