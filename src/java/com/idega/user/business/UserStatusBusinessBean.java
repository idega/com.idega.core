/*
 * $Id$
 *
 * Copyright (C) 2000-2003 Idega Software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega Software.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.business.IBOServiceBean;
import com.idega.data.IDOStoreException;
import com.idega.user.data.Status;
import com.idega.user.data.StatusHome;
import com.idega.user.data.UserStatus;
import com.idega.user.data.UserStatusHome;
import com.idega.util.IWTimestamp;

/**
 * @author palli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserStatusBusinessBean extends IBOServiceBean implements UserStatusBusiness {
	
	public final static String STATUS_DECEASED = "deceased"; 
	
	public boolean removeUserFromGroup(int user_id, int group_id) {
		return setUserGroupStatus(user_id,group_id,-1,-1);
	}
	
	public boolean setUserGroupStatus(int user_id, int group_id, int status_id){
	    return setUserGroupStatus(user_id,group_id,status_id,-1);
	}
	
	public boolean setUserGroupStatus(int user_id, int group_id, int status_id,int doneByUserId) {
		try {
			Collection obj = getUserStatusHome().findAllByUserIdAndGroupId(user_id,group_id);
			
			IWTimestamp now = new IWTimestamp();
			if (obj != null) {
				Iterator it = obj.iterator();
				while (it.hasNext()) {
					UserStatus uStatus = (UserStatus)it.next();
					if (uStatus.getDateTo() == null) {
						uStatus.setDateTo(now.getTimestamp());
						uStatus.store();
					}
				}
			}
			
			if (status_id > 0) {
				UserStatus uStatus = getUserStatusHome().create();
				uStatus.setUserId(user_id);
				uStatus.setGroupId(group_id);
				uStatus.setDateFrom(now.getTimestamp());
				uStatus.setStatusId(status_id);
				if(doneByUserId>0)
				   uStatus.setCreatedBy(doneByUserId);
				uStatus.store();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return false;
		}
		
		
		return true;
	}
	
	public int getUserGroupStatus(int user_id, int group_id) {
		try {
			Collection obj = getUserStatusHome().findAllByUserIdAndGroupId(user_id,group_id);
			int ret = -1;

			if (obj != null && obj.size() > 0) {
				UserStatus uStatus = (UserStatus)obj.toArray()[obj.size()-1];
				ret = uStatus.getStatusId();			
			}
			
			return ret;
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return -1;
		}
	}
	
	public UserStatusHome getUserStatusHome() throws RemoteException{
		return (UserStatusHome) getIDOHome(UserStatus.class);
	}
	
	public String getDeceasedStatusKey(){
		return STATUS_DECEASED;
	}
	
	public StatusHome getStatusHome() throws RemoteException{
		return (StatusHome) getIDOHome(Status.class);
	}
	
	public Status getDeceasedStatus() throws RemoteException{
		try {
			return getStatusHome().findByStatusKey(STATUS_DECEASED);
		}
		catch (FinderException e) {
			
		}
		return null;
	}
	
	public Status createDeceasedStatus() throws RemoteException,CreateException{
		Status status = getStatusHome().create();
		status.setStatusKey(STATUS_DECEASED);
		status.store();
		return status;
	}
	
	public Status getDeceasedStatusCreateIfNone() throws RemoteException{
		Status status = getDeceasedStatus();
		if(status!=null)
			return status;
		else{
			try {
				status = createDeceasedStatus();
				return status;
			}
			catch (CreateException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public UserStatus getDeceasedUserStatus(Integer userID) throws RemoteException{
		try {
			Status deceasedStatus = getDeceasedStatusCreateIfNone();
			Collection coll = getUserStatusHome().findAllByUserIDAndStatusID(userID,(Integer) deceasedStatus.getPrimaryKey());
			if(coll !=null && !coll.isEmpty())
				return (UserStatus) coll.iterator().next();
		}
		catch (EJBException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	public void setUserAsDeceased(Integer userID,Date deceasedDate) throws RemoteException{
		try {
			Status deceasedStatus = getDeceasedStatusCreateIfNone();
			UserStatus dUserStatus = getDeceasedUserStatus(userID);
			if(dUserStatus ==null){
				dUserStatus = getUserStatusHome().create();
				dUserStatus.setUserId(userID.intValue());
				//dUserStatus.setGroupId(group_id);
				dUserStatus.setDateFrom(new Timestamp(deceasedDate.getTime()));
				dUserStatus.setStatusId(((Integer)deceasedStatus.getPrimaryKey()).intValue());
				dUserStatus.store();
			}
		}
		catch (IDOStoreException e) {
			e.printStackTrace();
			
		}
	
		catch (EJBException e) {
			e.printStackTrace();
			
		}
		
		catch (CreateException e) {
			e.printStackTrace();
			
		}
		
	}
	
	
}