/*
 * $Id: GroupBusinessHome.java,v 1.6 2004/10/19 19:54:41 eiki Exp $
 * Created on Oct 18, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2004/10/19 19:54:41 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.6 $
 */
public interface GroupBusinessHome extends IBOHome {

	public GroupBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
