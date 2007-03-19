package com.idega.core.accesscontrol.business;

import java.util.List;

import com.idega.business.IBOSessionBeanWrapper;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.user.business.UserProperties;
import com.idega.user.data.User;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version 1.0
 * 
 */
public class LoginSessionManagedBean extends IBOSessionBeanWrapper implements LoginSession {
    
    private static final long serialVersionUID = 8323181033483315418L;

	/**
     * @return Returns the permissionGroups.
     */
    public List getPermissionGroups() {
    	
        return getIBOSessionBean().getPermissionGroups();
    }
    /**
     * @param permissionGroups The permissionGroups to set.
     */
    public void setPermissionGroups(List permissionGroups) {
    	getIBOSessionBean().setPermissionGroups(permissionGroups);
    }
    /**
     * @return Returns the primaryGroup.
     */
    public GenericGroup getPrimaryGroup() {
    	return getIBOSessionBean().getPrimaryGroup();
    }
    /**
     * @param primaryGroup The primaryGroup to set.
     */
    public void setPrimaryGroup(GenericGroup primaryGroup) {
    	getIBOSessionBean().setPrimaryGroup(primaryGroup);
    }
    
    /**
     * @return Returns the repGroup.
     */
    public UserGroupRepresentative getRepresentativeGroup() {
    	return getIBOSessionBean().getRepresentativeGroup();
    }
    /**
     * @param repGroup The repGroup to set.
     */
    public void setRepresentativeGroup(UserGroupRepresentative repGroup) {
    	getIBOSessionBean().setRepresentativeGroup(repGroup);
    }
    /**
     * @return Returns the user.
     */
    public User getUser() {
    	return getIBOSessionBean().getUser();
    }
    /**
     * @param user The user to set.
     */
    public void setUser(User user) {
    	getIBOSessionBean().setUser(user);
    }
    /**
     * @return Returns the loggedOnInfo.
     */
    public LoggedOnInfo getLoggedOnInfo() {
    	return getIBOSessionBean().getLoggedOnInfo();
    }
    /**
     * @param loggedOnInfo The loggedOnInfo to set.
     */
    public void setLoggedOnInfo(LoggedOnInfo loggedOnInfo) {
    	getIBOSessionBean().setLoggedOnInfo(loggedOnInfo);
    }
    /**
     * @return Returns the loginState.
     */
    public LoginState getLoginState() {
    	return getIBOSessionBean().getLoginState();
    }
    /**
     * @param loginState The loginState to set.
     */
    public void setLoginState(LoginState loginState) {
    	getIBOSessionBean().setLoginState(loginState);
    }
    /**
     * @return Returns the userLoginName.
     */
    public String getUserLoginName() {
    	return getIBOSessionBean().getUserLoginName();
    }
    /**
     * @param userLoginName The userLoginName to set.
     */
    public void setUserLoginName(String userLoginName) {
    	getIBOSessionBean().setUserLoginName(userLoginName);
    }
    
    public void setLoginAttribute(String key, Object value) {
    	getIBOSessionBean().setLoginAttribute(key, value);
    }
    
    public Object getLoginAttribute(String key){
    	return getIBOSessionBean().getLoginAttribute(key);
    }
    
    public void removeLoginAttribute(String key){
    	getIBOSessionBean().removeLoginAttribute(key);
    }
    
    /**
     * @return Returns the userProperties.
     */
    public UserProperties getUserProperties() {
    	return getIBOSessionBean().getUserProperties();
    }
    /**
     * @param userProperties The userProperties to set.
     */
    public void setUserProperties(UserProperties userProperties) {
    	getIBOSessionBean().setUserProperties(userProperties);
    }
  
    public void retrieve(){
    	getIBOSessionBean().retrieve();
    }
    
    public void reserve(){
    	getIBOSessionBean().reserve();
    }

	public boolean isSuperAdmin() {
		return getIBOSessionBean().isSuperAdmin();
	}

	@Override
	protected Class getIBOSessionBeanInterfaceClass() {
		
		return LoginSession.class;
	}
	
	@Override
	protected LoginSessionBean getIBOSessionBean() {
		return (LoginSessionBean)super.getIBOSessionBean();
	}
}