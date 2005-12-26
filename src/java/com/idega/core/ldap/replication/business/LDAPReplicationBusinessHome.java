/*
 * $Id: LDAPReplicationBusinessHome.java,v 1.7 2005/12/26 11:49:08 eiki Exp $
 * Created on Dec 26, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.replication.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/12/26 11:49:08 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.7 $
 */
public interface LDAPReplicationBusinessHome extends IBOHome {

	public LDAPReplicationBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
