/*
 * Created on Aug 15, 2004
 */
package com.idega.core.idgenerator.business;


import com.idega.business.IBOService;
import com.idega.user.data.Group;

/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface UUIDBusiness extends IBOService{
	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#createUniqueIDsForUsersAndGroups
	 */
	public void createUniqueIDsForUsersAndGroups()
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.idgenerator.business.UUIDBusinessBean#addUniqueKeyIfNeeded
	 */
	public void addUniqueKeyIfNeeded(Group group, String uniqueIdToCopy)
			throws java.rmi.RemoteException;

}
