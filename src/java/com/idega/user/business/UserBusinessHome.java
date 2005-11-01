/*
 * $Id: UserBusinessHome.java,v 1.4 2005/11/01 16:11:58 eiki Exp $
 * Created on Nov 1, 2005
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
 *  Last modified: $Date: 2005/11/01 16:11:58 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public interface UserBusinessHome extends IBOHome {

	public UserBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
