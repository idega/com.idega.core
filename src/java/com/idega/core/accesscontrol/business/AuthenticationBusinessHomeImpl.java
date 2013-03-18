/*
 * $Id: AuthenticationBusinessHomeImpl.java,v 1.3 2005/11/04 14:18:27 eiki Exp $
 * Created on Nov 4, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import com.idega.business.IBOHomeImpl;


/**
 *
 *  Last modified: $Date: 2005/11/04 14:18:27 $ by $Author: eiki $
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public class AuthenticationBusinessHomeImpl extends IBOHomeImpl implements AuthenticationBusinessHome {

	private static final long serialVersionUID = 8300355422017802540L;

	@Override
	protected Class<AuthenticationBusiness> getBeanInterfaceClass() {
		return AuthenticationBusiness.class;
	}

	@Override
	public AuthenticationBusiness create() throws javax.ejb.CreateException {
		return (AuthenticationBusiness) super.createIBO();
	}
}
