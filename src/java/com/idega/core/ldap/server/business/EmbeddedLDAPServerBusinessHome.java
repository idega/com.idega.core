/*
 * $Id: EmbeddedLDAPServerBusinessHome.java,v 1.3 2005/11/22 18:25:58 eiki Exp $
 * Created on Nov 22, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.server.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/11/22 18:25:58 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface EmbeddedLDAPServerBusinessHome extends IBOHome {

	public EmbeddedLDAPServerBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
