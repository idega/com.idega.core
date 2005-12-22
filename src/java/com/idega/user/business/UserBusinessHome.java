/*
 * $Id: UserBusinessHome.java,v 1.4.2.1 2005/12/22 19:59:57 eiki Exp $
 * Created on Nov 18, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/12/22 19:59:57 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4.2.1 $
 */
public interface UserBusinessHome extends IBOHome {

	public UserBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
