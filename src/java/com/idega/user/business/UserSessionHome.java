/*
 * $Id: UserSessionHome.java,v 1.1 2005/01/25 08:56:31 laddi Exp $
 * Created on 24.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOHome;


/**
 * Last modified: $Date: 2005/01/25 08:56:31 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface UserSessionHome extends IBOHome {

	public UserSession create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
