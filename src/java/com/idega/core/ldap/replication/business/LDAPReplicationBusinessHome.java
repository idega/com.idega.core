/*
 * $Id: LDAPReplicationBusinessHome.java,v 1.1 2004/08/31 02:29:32 eiki Exp $
 * Created on Aug 30, 2004
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
 *  Last modified: $Date: 2004/08/31 02:29:32 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public interface LDAPReplicationBusinessHome extends IBOHome {

	public LDAPReplicationBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
