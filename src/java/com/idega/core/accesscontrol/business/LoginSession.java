/*
 * $Id: LoginSession.java,v 1.9 2008/06/05 18:24:14 civilis Exp $
 * Created on 3.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.List;
import java.util.Locale;

import com.idega.business.SpringBeanName;
import com.idega.core.data.GenericGroup;
import com.idega.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.user.business.UserProperties;

/**
 *  <p />Last modified: $Date: 2008/06/05 18:24:14 $ by $Author: civilis $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.9 $
 */
@SpringBeanName("loginSession")
public interface LoginSession {
    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getPermissionGroups
     */
    public List getPermissionGroups();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setPermissionGroups
     */
    public void setPermissionGroups(List permissionGroups);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getPrimaryGroup
     */
    public GenericGroup getPrimaryGroup();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setPrimaryGroup
     */
    public void setPrimaryGroup(GenericGroup primaryGroup);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getRepresentativeGroup
     */
    public UserGroupRepresentative getRepresentativeGroup();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setRepresentativeGroup
     */
    public void setRepresentativeGroup(UserGroupRepresentative repGroup);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getUser
     */
    public User getUser();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setUser
     */
    public void setUser(User user);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getLoggedOnInfo
     */
    public LoggedOnInfo getLoggedOnInfo();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setLoggedOnInfo
     */
    public void setLoggedOnInfo(LoggedOnInfo loggedOnInfo);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getLoginState
     */
    public LoginState getLoginState();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setLoginState
     */
    public void setLoginState(LoginState loginState);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getUserLoginName
     */
    public String getUserLoginName();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setUserLoginName
     */
    public void setUserLoginName(String userLoginName);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setLoginAttribute
     */
    public void setLoginAttribute(String key, Object value);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getLoginAttribute
     */
    public Object getLoginAttribute(String key);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#removeLoginAttribute
     */
    public void removeLoginAttribute(String key);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#getUserProperties
     */
    public UserProperties getUserProperties();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#setUserProperties
     */
    public void setUserProperties(UserProperties userProperties);

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#isReserved
     */
//    public boolean isReserved() throws java.rmi.RemoteException;

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#retrieve
     */
    public void retrieve();

    /**
     * @see com.idega.core.accesscontrol.business.LoginSessionBean#reserve
     */
    public void reserve();
    
    public boolean isSuperAdmin();
    
    public abstract void reset();
    
    public String getSuperAdminId();
    
    public Locale getCurrentLocale();
    
    public abstract boolean isLoggedIn();
}