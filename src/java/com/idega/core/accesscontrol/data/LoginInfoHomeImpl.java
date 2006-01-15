/*
 * $Id: LoginInfoHomeImpl.java,v 1.3 2006/01/15 17:29:35 laddi Exp $
 * Created on Jan 15, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO laddi Describe Type LoginInfoHomeImpl
 * </p>
 *  Last modified: $Date: 2006/01/15 17:29:35 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class LoginInfoHomeImpl extends IDOFactory implements LoginInfoHome {

	protected Class getEntityInterfaceClass() {
		return LoginInfo.class;
	}

	public LoginInfo create() throws javax.ejb.CreateException {
		return (LoginInfo) super.createIDO();
	}

	public LoginInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (LoginInfo) super.findByPrimaryKeyIDO(pk);
	}
}
