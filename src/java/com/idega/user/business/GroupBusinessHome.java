/*
 * $Id: GroupBusinessHome.java,v 1.8 2004/11/16 14:53:32 eiki Exp $
 * Created on Nov 16, 2004
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
 *  Last modified: $Date: 2004/11/16 14:53:32 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.8 $
 */
public interface GroupBusinessHome extends IBOHome {

	public GroupBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
