/*
 * $Id: UUIDBusiness.java,v 1.3 2004/09/16 17:49:08 eiki Exp $
 * Created on Sep 13, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.idgenerator.business;

import javax.ejb.FinderException;
import com.idega.business.IBOService;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2004/09/16 17:49:08 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface UUIDBusiness extends IBOService {

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#generateUUIDsForAllUsersAndGroups
	 */
	public void generateUUIDsForAllUsersAndGroups() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#addUniqueKeyIfNeeded
	 */
	public void addUniqueKeyIfNeeded(Group group, String uniqueIdToCopy) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#removeUniqueIDsForUsersAndGroups
	 */
	public void removeUniqueIDsForUsersAndGroups() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#generateUUIDsForAllGroups
	 */
	public void generateUUIDsForAllGroups() throws FinderException, IDOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#generateUUIDsForAllUsers
	 */
	public void generateUUIDsForAllUsers() throws FinderException, IDOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#removeUUIDsFromAllGroups
	 */
	public void removeUUIDsFromAllGroups() throws FinderException, IDOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#removeUUIDsFromAllUsers
	 */
	public void removeUUIDsFromAllUsers() throws FinderException, IDOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#removeUniqueIdIfPresent
	 */
	public void removeUniqueIdIfPresent(Group group) throws java.rmi.RemoteException;
}
