/*
 * $Id: LDAPReplicationBusinessHome.java,v 1.3 2004/10/28 11:49:09 eiki Exp $
 * Created on Oct 25, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.replication.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2004/10/28 11:49:09 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface LDAPReplicationBusinessHome extends IBOHome {

	public LDAPReplicationBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
