/*
 * $Id: GroupBusinessHome.java,v 1.7 2004/11/16 12:36:21 laddi Exp $
 * Created on 16.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;




import com.idega.business.IBOHome;


/**
 * Last modified: 16.11.2004 13:37:13 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.7 $
 */
public interface GroupBusinessHome extends IBOHome {

	public GroupBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
