/*
 * $Id: LoginSessionBean.java,v 1.2.2.1 2007/01/12 19:31:55 idegaweb Exp $
 * Created on 3.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import com.idega.business.IBOSessionBean;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.user.business.UserProperties;

/**
 * 
 *  Last modified: $Date: 2007/01/12 19:31:55 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2.2.1 $
 */
public class LoginSessionBean extends IBOSessionBean  implements LoginSession{
    

    private SessionHelper sessionHelper = new SessionHelper();
    private Stack reservedSessionHelpers = new Stack();
    
    private void reset(){
        this.sessionHelper = null;
        this.sessionHelper = new SessionHelper();
        /*
        sessionHelper.user=null;
        sessionHelper.permissionGroups = null;
        sessionHelper.primaryGroup = null;
        sessionHelper.repGroup = null;
        sessionHelper.loggedOnInfo = null;
        sessionHelper.loginState = LoginState.NoState;
        sessionHelper.userLoginName = null;
        sessionHelper.userProperties = null;
        sessionHelper.mapOfExtraAttributes = null;
        */
    }
    
    
    /**
     * @return Returns the permissionGroups.
     */
    public List getPermissionGroups() {
        return this.sessionHelper.permissionGroups;
    }
    /**
     * @param permissionGroups The permissionGroups to set.
     */
    public void setPermissionGroups(List permissionGroups) {
        this.sessionHelper.permissionGroups = permissionGroups;
    }
    /**
     * @return Returns the primaryGroup.
     */
    public GenericGroup getPrimaryGroup() {
        return this.sessionHelper.primaryGroup;
    }
    /**
     * @param primaryGroup The primaryGroup to set.
     */
    public void setPrimaryGroup(GenericGroup primaryGroup) {
        this.sessionHelper.primaryGroup = primaryGroup;
    }
    /**
     * @return Returns the repGroup.
     */
    public UserGroupRepresentative getRepresentativeGroup() {
        return this.sessionHelper.repGroup;
    }
    /**
     * @param repGroup The repGroup to set.
     */
    public void setRepresentativeGroup(UserGroupRepresentative repGroup) {
        this.sessionHelper.repGroup = repGroup;
    }
    /**
     * @return Returns the user.
     */
    public User getUser() {
        return this.sessionHelper.user;
    }
    /**
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.sessionHelper.user = user;
    }
    /**
     * @return Returns the loggedOnInfo.
     */
    public LoggedOnInfo getLoggedOnInfo() {
        return this.sessionHelper.loggedOnInfo;
    }
    /**
     * @param loggedOnInfo The loggedOnInfo to set.
     */
    public void setLoggedOnInfo(LoggedOnInfo loggedOnInfo) {
        this.sessionHelper.loggedOnInfo = loggedOnInfo;
    }
    /**
     * @return Returns the loginState.
     */
    public LoginState getLoginState() {
        return this.sessionHelper.loginState;
    }
    /**
     * @param loginState The loginState to set.
     */
    public void setLoginState(LoginState loginState) {
        this.sessionHelper.loginState = loginState;
    }
    /**
     * @return Returns the userLoginName.
     */
    public String getUserLoginName() {
        return this.sessionHelper.userLoginName;
    }
    /**
     * @param userLoginName The userLoginName to set.
     */
    public void setUserLoginName(String userLoginName) {
        this.sessionHelper.userLoginName = userLoginName;
    }
    
    public void setLoginAttribute(String key, Object value){
        this.sessionHelper.mapOfExtraAttributes.put(key,value);
    }
    
    public Object getLoginAttribute(String key){
        if(this.sessionHelper.mapOfExtraAttributes==null) {
			this.sessionHelper.mapOfExtraAttributes = new Hashtable();
		}
        return this.sessionHelper.mapOfExtraAttributes.get(key);
    }
    
    public void removeLoginAttribute(String key){
        this.sessionHelper.mapOfExtraAttributes.remove(key);
    }
    /**
     * @return Returns the mapOfExtraAttributes.
     */
    protected Map getMapOfExtraAttributes() {
        return this.sessionHelper.mapOfExtraAttributes;
    }
    /**
     * @param mapOfExtraAttributes The mapOfExtraAttributes to set.
     */
    protected void setMapOfExtraAttributes(Map mapOfExtraAttributes) {
        this.sessionHelper.mapOfExtraAttributes = mapOfExtraAttributes;
    }
    /**
     * @return Returns the userProperties.
     */
    public UserProperties getUserProperties() {
        return this.sessionHelper.userProperties;
    }
    /**
     * @param userProperties The userProperties to set.
     */
    public void setUserProperties(UserProperties userProperties) {
        this.sessionHelper.userProperties = userProperties;
    }
  
    public void retrieve(){
    	if (this.reservedSessionHelpers != null && !this.reservedSessionHelpers.isEmpty()) {
    		this.sessionHelper = (SessionHelper) this.reservedSessionHelpers.pop();
    	}
    }
    
    public void reserve(){
    	this.reservedSessionHelpers.push(this.sessionHelper);
        reset();
    }
    
    private class SessionHelper{
        protected User user = null;
        protected List permissionGroups = null;
        protected GenericGroup primaryGroup = null;
        protected UserGroupRepresentative repGroup = null;
        protected LoggedOnInfo loggedOnInfo = null;
        protected LoginState loginState = LoginState.NoState;
        protected String userLoginName = null;
        protected UserProperties userProperties = null;
        protected Map mapOfExtraAttributes = new Hashtable();
        protected User reserveUser = null;
    }
}
