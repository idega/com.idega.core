/*
 * $Id: UserBusinessHome.java,v 1.3 2005/02/16 11:11:16 laddi Exp $
 * Created on 2.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;




import com.idega.business.IBOHome;


/**
 * Last modified: $Date: 2005/02/16 11:11:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public interface UserBusinessHome extends IBOHome {

	public UserBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
