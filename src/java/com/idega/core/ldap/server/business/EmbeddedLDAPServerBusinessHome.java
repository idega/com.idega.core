/*
 * $Id: EmbeddedLDAPServerBusinessHome.java,v 1.2 2004/09/21 18:57:59 eiki Exp $
 * Created on Sep 21, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.server.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2004/09/21 18:57:59 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public interface EmbeddedLDAPServerBusinessHome extends IBOHome {

	public EmbeddedLDAPServerBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
