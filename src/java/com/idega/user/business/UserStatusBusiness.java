/*
 * Created on 24.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import javax.ejb.CreateException;

import com.idega.business.IBOService;
import com.idega.user.data.Status;
import com.idega.user.data.StatusHome;
import com.idega.user.data.UserStatus;
import com.idega.user.data.UserStatusHome;

/**
 * @author aron
 *
 * UserStatusBusiness TODO Describe this type
 */
public interface UserStatusBusiness extends IBOService {
    /**
     * @see com.idega.user.business.UserStatusBusinessBean#removeUserFromGroup
     */
    public boolean removeUserFromGroup(int user_id, int group_id)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#setUserGroupStatus
     */
    public boolean setUserGroupStatus(int user_id, int group_id, int status_id)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#setUserGroupStatus
     */
    public boolean setUserGroupStatus(int user_id, int group_id, int status_id,
            int doneByUserId) throws java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#getUserGroupStatus
     */
    public int getUserGroupStatus(int user_id, int group_id)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#getUserStatusHome
     */
    public UserStatusHome getUserStatusHome() throws RemoteException,
            java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#getDeceasedStatusKey
     */
    public String getDeceasedStatusKey() throws java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#getStatusHome
     */
    public StatusHome getStatusHome() throws RemoteException,
            java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#getDeceasedStatus
     */
    public Status getDeceasedStatus() throws RemoteException,
            java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#createDeceasedStatus
     */
    public Status createDeceasedStatus() throws RemoteException,
            CreateException, java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#getDeceasedStatusCreateIfNone
     */
    public Status getDeceasedStatusCreateIfNone() throws RemoteException,
            java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#getDeceasedUserStatus
     */
    public UserStatus getDeceasedUserStatus(Integer userID)
            throws RemoteException, java.rmi.RemoteException;

    /**
     * @see com.idega.user.business.UserStatusBusinessBean#setUserAsDeceased
     */
    public void setUserAsDeceased(Integer userID, Date deceasedDate)
            throws RemoteException, java.rmi.RemoteException;
    
    public Status getStatusByStatusId(int statusId) throws RemoteException;

	public Collection getAllUserStatuses(int userId) throws RemoteException;
	
	public Collection getAllUsersWithStatus(int statusId);

}
