/*
 * $Id: LoginSession.java,v 1.2 2005/02/08 15:51:07 gimmi Exp $
 * Created on 3.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.List;

import com.idega.business.IBOSession;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.user.business.UserProperties;

/**
 * 
 *  Last modified: $Date: 2005/02/08 15:51:07 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public interface LoginSession extends IBOSession {
    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getPermissionGroups
     */
    public List getPermissionGroups() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setPermissionGroups
     */
    public void setPermissionGroups(List permissionGroups)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getPrimaryGroup
     */
    public GenericGroup getPrimaryGroup() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setPrimaryGroup
     */
    public void setPrimaryGroup(GenericGroup primaryGroup)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getRepresentativeGroup
     */
    public UserGroupRepresentative getRepresentativeGroup()
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setRepresentativeGroup
     */
    public void setRepresentativeGroup(UserGroupRepresentative repGroup)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getUser
     */
    public User getUser() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setUser
     */
    public void setUser(User user) throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getLoggedOnInfo
     */
    public LoggedOnInfo getLoggedOnInfo() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setLoggedOnInfo
     */
    public void setLoggedOnInfo(LoggedOnInfo loggedOnInfo)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getLoginState
     */
    public LoginState getLoginState() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setLoginState
     */
    public void setLoginState(LoginState loginState)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getUserLoginName
     */
    public String getUserLoginName() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setUserLoginName
     */
    public void setUserLoginName(String userLoginName)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setLoginAttribute
     */
    public void setLoginAttribute(String key, Object value)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getLoginAttribute
     */
    public Object getLoginAttribute(String key) throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#removeLoginAttribute
     */
    public void removeLoginAttribute(String key)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getUserProperties
     */
    public UserProperties getUserProperties() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setUserProperties
     */
    public void setUserProperties(UserProperties userProperties)
            throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#isReserved
     */
//    public boolean isReserved() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#retrieve
     */
    public void retrieve() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#reserve
     */
    public void reserve() throws java.rmi.RemoteException;

}
