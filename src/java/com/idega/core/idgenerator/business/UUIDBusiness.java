/*
 * $Id: UUIDBusiness.java,v 1.2 2004/09/09 16:24:14 eiki Exp $
 * Created on Sep 9, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.idgenerator.business;

import com.idega.business.IBOService;
import com.idega.user.data.Group;

/**
 * 
 *  Last modified: $Date: 2004/09/09 16:24:14 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public interface UUIDBusiness extends IBOService{
/**
 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#createUniqueIDsForUsersAndGroups
 */
public void createUniqueIDsForUsersAndGroups() throws java.rmi.RemoteException;
/**
 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#addUniqueKeyIfNeeded
 */
public void addUniqueKeyIfNeeded(Group group, String uniqueIdToCopy) throws java.rmi.RemoteException;
/**
 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#removeUniqueIDsForUsersAndGroups
 */
public void removeUniqueIDsForUsersAndGroups() throws java.rmi.RemoteException;
/**
 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#removeUniqueIdIfPresent
 */
public void removeUniqueIdIfPresent(Group group) throws java.rmi.RemoteException;

}
