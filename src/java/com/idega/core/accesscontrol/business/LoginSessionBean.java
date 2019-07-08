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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.data.bean.PermissionGroup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserProperties;
import com.idega.user.business.UserSession;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.user.data.bean.UserGroupRepresentative;
import com.idega.util.CoreUtil;
import com.idega.util.expression.ELUtil;

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
	private com.idega.user.data.User legacyUser;
	private User emulatedUser;
	
	@Autowired
	private UserDAO userDAO;
	
	private UserDAO getUserDAO() {
		if (userDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return userDAO;
	}

	@Override
	public void reset() {
		sessionHelper = new SessionHelper();
		reservedSessionHelpers = new Stack<SessionHelper>();
		legacyUser = null;
	}

	/**
	 * @return Returns the permissionGroups.
	 */
	@Override
	public List<Group> getPermissionGroups() {
		return this.sessionHelper.permissionGroups;
	}

	/**
	 * @param permissionGroups
	 *          The permissionGroups to set.
	 */
	@Override
	public void setPermissionGroups(List<Group> permissionGroups) {
		this.sessionHelper.permissionGroups = permissionGroups;
	}

	/**
	 * @return Returns the primaryGroup.
	 */
	@Override
	public Group getPrimaryGroup() {
		return this.sessionHelper.primaryGroup;
	}

	/**
	 * @param primaryGroup
	 *          The primaryGroup to set.
	 */
	@Override
	public void setPrimaryGroup(Group primaryGroup) {
		this.sessionHelper.primaryGroup = primaryGroup;
	}

	/**
	 * @return Returns the repGroup.
	 */
	@Override
	public UserGroupRepresentative getRepresentativeGroup() {
		return this.sessionHelper.repGroup;
	}

	/**
	 * @param repGroup
	 *          The repGroup to set.
	 */
	@Override
	public void setRepresentativeGroup(UserGroupRepresentative repGroup) {
		this.sessionHelper.repGroup = repGroup;
	}

	/**
	 * @return Returns the user.
	 */
	@Override
	public com.idega.user.data.User getUser() {
		if (legacyUser != null)
			return legacyUser;

		User user = getUserEntity();
		if (user == null)
			return null;

		try {
			UserBusiness userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
			legacyUser = userBusiness.getUser(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return legacyUser;
	}

	@Override
	public User getUserEntity() {
		if (emulatedUser != null) {
			return emulatedUser;
		}
		
		try {
			IWContext iwc = CoreUtil.getIWContext();
			if (iwc != null) {
				UserSession userSession = IBOLookup.getSessionInstance(iwc, UserSession.class);
				com.idega.user.data.User user = userSession.getUser();
				if (user != null) {
					emulatedUser = getUserDAO().getUser(user.getPersonalID());
					if (emulatedUser != null) {
						return emulatedUser;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.sessionHelper.user;
	}

	/**
	 * @param user
	 *          The user to set.
	 */
	@Override
	public void setUser(User user) {
		this.sessionHelper.user = user;
	}

	/**
	 * @return Returns the loggedOnInfo.
	 */
	@Override
	public LoggedOnInfo getLoggedOnInfo() {
		return this.sessionHelper.loggedOnInfo;
	}

	/**
	 * @param loggedOnInfo
	 *          The loggedOnInfo to set.
	 */
	@Override
	public void setLoggedOnInfo(LoggedOnInfo loggedOnInfo) {
		this.sessionHelper.loggedOnInfo = loggedOnInfo;
	}

	/**
	 * @return Returns the loginState.
	 */
	@Override
	public LoginState getLoginState() {
		return this.sessionHelper.loginState;
	}

	/**
	 * @param loginState
	 *          The loginState to set.
	 */
	@Override
	public void setLoginState(LoginState loginState) {
		this.sessionHelper.loginState = loginState;
	}

	/**
	 * @return Returns the userLoginName.
	 */
	@Override
	public String getUserLoginName() {
		return this.sessionHelper.userLoginName;
	}

	/**
	 * @param userLoginName
	 *          The userLoginName to set.
	 */
	@Override
	public void setUserLoginName(String userLoginName) {
		this.sessionHelper.userLoginName = userLoginName;
	}

	@Override
	public void setLoginAttribute(String key, Object value) {
		this.sessionHelper.mapOfExtraAttributes.put(key, value);
	}

	@Override
	public Object getLoginAttribute(String key) {
		if (this.sessionHelper.mapOfExtraAttributes == null) {
			this.sessionHelper.mapOfExtraAttributes = new Hashtable<String, Object>();
		}
		return this.sessionHelper.mapOfExtraAttributes.get(key);
	}

	@Override
	public void removeLoginAttribute(String key) {
		this.sessionHelper.mapOfExtraAttributes.remove(key);
	}

	/**
	 * @return Returns the mapOfExtraAttributes.
	 */
	protected Map<String, Object> getMapOfExtraAttributes() {
		return this.sessionHelper.mapOfExtraAttributes;
	}

	/**
	 * @param mapOfExtraAttributes
	 *          The mapOfExtraAttributes to set.
	 */
	protected void setMapOfExtraAttributes(Map<String, Object> mapOfExtraAttributes) {
		this.sessionHelper.mapOfExtraAttributes = mapOfExtraAttributes;
	}

	/**
	 * @return Returns the userProperties.
	 */
	@Override
	public UserProperties getUserProperties() {
		return this.sessionHelper.userProperties;
	}

	/**
	 * @param userProperties
	 *          The userProperties to set.
	 */
	@Override
	public void setUserProperties(UserProperties userProperties) {
		this.sessionHelper.userProperties = userProperties;
	}

	@Override
	public void retrieve() {
		if (this.reservedSessionHelpers != null && !this.reservedSessionHelpers.isEmpty()) {
			this.sessionHelper = this.reservedSessionHelpers.pop();
		}
	}

	@Override
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
		protected LoginState loginState = LoginState.NO_STATE;
		protected String userLoginName = null;
		protected UserProperties userProperties = null;
		protected Map<String, Object> mapOfExtraAttributes = new Hashtable<String, Object>();
		protected User reserveUser = null;
	}

	private Boolean superAdmin = null;

	@Override
	public boolean isSuperAdmin() {
		if (superAdmin != null) {
			return superAdmin;
		}

		User user = null;
		try {
			user = getUserEntity();
			if (user == null) {
				superAdmin = false;
				return superAdmin;
			}

			if (user.equals(this.getAccessController().getAdministratorUser())) {
				superAdmin = true;
				return superAdmin;
			}

			PermissionGroup permGroup = getAccessController().getPermissionGroupAdministrator();
			if (permGroup == null) {
				superAdmin = false;
				return superAdmin;
			}

			UserBusiness userBusiness = IBOLookup.getServiceInstance(iwac, UserBusiness.class);
			superAdmin = userBusiness.isMemberOfGroup(permGroup.getID(), userBusiness.getUser(user.getId()));
			return superAdmin;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error resolving if " + user + " is super admin", e);
			superAdmin = false;
		}

		return superAdmin;
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

	@Override
	public String getSuperAdminId() {
		if (isSuperAdmin()) {
			return getUserEntity().getId().toString();
		}
		return null;
	}

	@Override
	public Locale getCurrentLocale() {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			Logger.getLogger(getClass().getName()).warning(IWContext.class.getName() + " is unavailable!");
			return null;
		}

		currentLocale = iwc.getCurrentLocale();
		return currentLocale;
	}

	@Override
	public boolean isLoggedIn() {
		return getUser() != null;
	}
}