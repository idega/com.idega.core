/*
 * $Id: UserBusinessHomeImpl.java,v 1.5 2005/11/18 16:20:53 eiki Exp $
 * Created on Nov 18, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2005/11/18 16:20:53 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public class UserBusinessHomeImpl extends IBOHomeImpl implements UserBusinessHome {

	protected Class getBeanInterfaceClass() {
		return UserBusiness.class;
	}

	public UserBusiness create() throws javax.ejb.CreateException {
		return (UserBusiness) super.createIBO();
	}
}
