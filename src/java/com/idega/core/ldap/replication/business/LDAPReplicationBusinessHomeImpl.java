/*
 * $Id: LDAPReplicationBusinessHomeImpl.java,v 1.4 2004/10/31 22:35:21 eiki Exp $
 * Created on Oct 31, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.replication.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/10/31 22:35:21 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public class LDAPReplicationBusinessHomeImpl extends IBOHomeImpl implements LDAPReplicationBusinessHome {

	protected Class getBeanInterfaceClass() {
		return LDAPReplicationBusiness.class;
	}

	public LDAPReplicationBusiness create() throws javax.ejb.CreateException {
		return (LDAPReplicationBusiness) super.createIBO();
	}
}
