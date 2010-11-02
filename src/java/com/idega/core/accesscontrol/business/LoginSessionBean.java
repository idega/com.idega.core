/*
 * $Id: LoginSessionBean.java,v 1.12 2009/05/27 09:44:51 laddi Exp $ Created
 * on 3.9.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.accesscontrol.business;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserProperties;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.user.data.bean.UserGroupRepresentative;
import com.idega.util.CoreUtil;

/**
 * 
 * Last modified: $Date: 2009/05/27 09:44:51 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.12 $
 */
public class LoginSessionBean implements LoginSession, Serializable {

	private static final long serialVersionUID = 919604997496929169L;

	private IWApplicationContext iwac;
	private SessionHelper sessionHelper = new SessionHelper();
	private Stack<SessionHelper> reservedSessionHelpers = new Stack<SessionHelper>();
	private Locale currentLocale;

	public void reset() {
		sessionHelper = new SessionHelper();
		reservedSessionHelpers = new Stack<SessionHelper>();
	}

	/**
	 * @return Returns the permissionGroups.
	 */
	public List<Group> getPermissionGroups() {
		return this.sessionHelper.permissionGroups;
	}

	/**
	 * @param permissionGroups
	 *          The permissionGroups to set.
	 */
	public void setPermissionGroups(List<Group> permissionGroups) {
		this.sessionHelper.permissionGroups = permissionGroups;
	}

	/**
	 * @return Returns the primaryGroup.
	 */
	public Group getPrimaryGroup() {
		return this.sessionHelper.primaryGroup;
	}

	/**
	 * @param primaryGroup
	 *          The primaryGroup to set.
	 */
	public void setPrimaryGroup(Group primaryGroup) {
		this.sessionHelper.primaryGroup = primaryGroup;
	}

	/**
	 * @return Returns the repGroup.
	 */
	public UserGroupRepresentative getRepresentativeGroup() {
		return this.sessionHelper.repGroup;
	}

	/**
	 * @param repGroup
	 *          The repGroup to set.
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
	 * @param user
	 *          The user to set.
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
	 * @param loggedOnInfo
	 *          The loggedOnInfo to set.
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
	 * @param loginState
	 *          The loginState to set.
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
	 * @param userLoginName
	 *          The userLoginName to set.
	 */
	public void setUserLoginName(String userLoginName) {
		this.sessionHelper.userLoginName = userLoginName;
	}

	public void setLoginAttribute(String key, Object value) {
		this.sessionHelper.mapOfExtraAttributes.put(key, value);
	}

	public Object getLoginAttribute(String key) {
		if (this.sessionHelper.mapOfExtraAttributes == null) {
			this.sessionHelper.mapOfExtraAttributes = new Hashtable();
		}
		return this.sessionHelper.mapOfExtraAttributes.get(key);
	}

	public void removeLoginAttribute(String key) {
		this.sessionHelper.mapOfExtraAttributes.remove(key);
	}

	/**
	 * @return Returns the mapOfExtraAttributes.
	 */
	protected Map getMapOfExtraAttributes() {
		return this.sessionHelper.mapOfExtraAttributes;
	}

	/**
	 * @param mapOfExtraAttributes
	 *          The mapOfExtraAttributes to set.
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
	 * @param userProperties
	 *          The userProperties to set.
	 */
	public void setUserProperties(UserProperties userProperties) {
		this.sessionHelper.userProperties = userProperties;
	}

	public void retrieve() {
		if (this.reservedSessionHelpers != null && !this.reservedSessionHelpers.isEmpty()) {
			this.sessionHelper = this.reservedSessionHelpers.pop();
		}
	}

	public void reserve() {
		this.reservedSessionHelpers.push(this.sessionHelper);
		reset();
	}

	protected class SessionHelper implements Serializable {

		private static final long serialVersionUID = 8659431184858479401L;

		protected User user = null;
		protected List<Group> permissionGroups = null;
		protected Group primaryGroup = null;
		protected UserGroupRepresentative repGroup = null;
		protected LoggedOnInfo loggedOnInfo = null;
		protected LoginState loginState = LoginState.NoState;
		protected String userLoginName = null;
		protected UserProperties userProperties = null;
		protected Map mapOfExtraAttributes = new Hashtable();
		protected User reserveUser = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.core.accesscontrol.business.LoginSession#iSuperAdmin()
	 */
	public boolean isSuperAdmin() {
		try {
			// if (this.isLoggedOn())
			User user = getUser();
			if (user != null) {
				return user.equals(this.getAccessController().getAdministratorUser());
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public IWApplicationContext getIWApplicationContext() {
		if (this.iwac == null) {
			return IWMainApplication.getDefaultIWApplicationContext();
		}
		return this.iwac;
	}

	protected AccessController getAccessController() {
		return this.getIWApplicationContext().getIWMainApplication().getAccessController();
	}

	public String getSuperAdminId() {
		if (isSuperAdmin()) {
			return getUser().getId().toString();
		}
		return null;
	}

	public Locale getCurrentLocale() {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			Logger.getLogger(getClass().getName()).warning(IWContext.class.getName() + " is unavailable!");
			return null;
		}
		
		currentLocale = iwc.getCurrentLocale();
		return currentLocale;
	}

	public boolean isLoggedIn() {
		return getUser() != null;
	}
}