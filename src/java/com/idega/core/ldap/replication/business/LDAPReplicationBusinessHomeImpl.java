/*
 * $Id: LDAPReplicationBusinessHomeImpl.java,v 1.6 2005/12/25 17:14:27 eiki Exp $
 * Created on Nov 30, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.replication.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2005/12/25 17:14:27 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.6 $
 */
public class LDAPReplicationBusinessHomeImpl extends IBOHomeImpl implements LDAPReplicationBusinessHome {

	protected Class getBeanInterfaceClass() {
		return LDAPReplicationBusiness.class;
	}

	public LDAPReplicationBusiness create() throws javax.ejb.CreateException {
		return (LDAPReplicationBusiness) super.createIBO();
	}
}
