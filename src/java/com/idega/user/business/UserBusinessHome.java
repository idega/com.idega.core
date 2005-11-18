/*
 * $Id: UserBusinessHome.java,v 1.5 2005/11/18 16:20:54 eiki Exp $
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
 *  Last modified: $Date: 2005/11/18 16:20:54 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public interface UserBusinessHome extends IBOHome {

	public UserBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
