/*
 * $Id: AuthenticationBusinessHome.java,v 1.2 2005/11/02 15:57:47 eiki Exp $
 * Created on Nov 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/11/02 15:57:47 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public interface AuthenticationBusinessHome extends IBOHome {

	public AuthenticationBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
