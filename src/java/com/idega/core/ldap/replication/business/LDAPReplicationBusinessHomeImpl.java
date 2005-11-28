/*
 * $Id: LDAPReplicationBusinessHomeImpl.java,v 1.5 2005/11/28 17:53:12 eiki Exp $
 * Created on Nov 28, 2005
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
 *  Last modified: $Date: 2005/11/28 17:53:12 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public class LDAPReplicationBusinessHomeImpl extends IBOHomeImpl implements LDAPReplicationBusinessHome {

	protected Class getBeanInterfaceClass() {
		return LDAPReplicationBusiness.class;
	}

	public LDAPReplicationBusiness create() throws javax.ejb.CreateException {
		return (LDAPReplicationBusiness) super.createIBO();
	}
}
