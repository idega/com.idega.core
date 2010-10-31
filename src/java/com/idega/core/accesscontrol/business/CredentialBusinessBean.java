/*
 * $Id: CredentialBusinessBean.java,v 1.5 2008/07/02 19:27:34 civilis Exp $
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

import com.idega.business.IBOServiceBean;
import com.idega.core.accesscontrol.jaas.IWCredential;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.util.expression.ELUtil;


/**
 *
 * Not fully implemented. There will be more methods pretty soon.
 *
 *
 *  Last modified: $Date: 2008/07/02 19:27:34 $ by $Author: civilis $
 *
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.5 $
 */
public class CredentialBusinessBean extends IBOServiceBean  implements CredentialBusiness{

	private static final long serialVersionUID = 8019421364512045310L;

	@Override
	public void addCredentialsToLink(Link link, IWContext iwc) {
		if (!LoginBusinessBean.isLoggedOn(iwc)) {
			// not logged in  - do nothing!
			return;
		}

		LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
		LoggedOnInfo loggedOnInfo  = loginSession.getLoggedOnInfo();
		if (loggedOnInfo != null) {
			Map<String, IWCredential> credentials = loggedOnInfo.getCredentials();
			for (Iterator<IWCredential> iterator = credentials.values().iterator(); iterator.hasNext();)  {
				IWCredential credential = iterator.next();
				String name = credential.getName();
				Object key = credential.getKey();
				if (key != null) {
					link.addParameter(name, key.toString());
				}
			}
		}
	}
}
