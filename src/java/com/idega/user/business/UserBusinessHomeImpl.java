/*
 * $Id: UserBusinessHomeImpl.java,v 1.2 2004/10/18 17:21:35 eiki Exp $
 * Created on Oct 18, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/10/18 17:21:35 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class UserBusinessHomeImpl extends IBOHomeImpl implements UserBusinessHome {

	protected Class getBeanInterfaceClass() {
		return UserBusiness.class;
	}

	public UserBusiness create() throws javax.ejb.CreateException {
		return (UserBusiness) super.createIBO();
	}
}
