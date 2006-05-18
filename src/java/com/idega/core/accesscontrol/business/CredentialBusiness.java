/*
 * $Id: CredentialBusiness.java,v 1.1 2006/05/18 16:18:33 thomas Exp $
 * Created on May 10, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import com.idega.business.IBOService;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;


/**
 * 
 *  Last modified: $Date: 2006/05/18 16:18:33 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public interface CredentialBusiness extends IBOService {

	/**
	 * @see com.idega.core.accesscontrol.business.CredentialBusinessBean#addCredentialsToLink
	 */
	public void addCredentialsToLink(Link link, IWContext iwc) throws java.rmi.RemoteException;
}
