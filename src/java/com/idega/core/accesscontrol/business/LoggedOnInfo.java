/*
 * $Id: LoggedOnInfo.java,v 1.21 2007/01/22 08:16:38 tryggvil Exp $
 *
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.idega.core.accesscontrol.data.LoginRecord;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.jaas.IWCredential;
import com.idega.core.accesscontrol.jaas.PersonalIdCredential;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * <p>
 * An instance of this class is stored in HttpSession for each logged in user in
 * idegaWeb.<br/>
 * This class implements HttpSessionBindingListener so that the login
 * information is cleaned up when the users session times out.
 * </p>
 * 
 * Last modified: $Date: 2007/01/22 08:16:38 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>, <a
 *         href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.21 $
 */
public class LoggedOnInfo implements HttpSessionBindingListener {

	private static final String TICKET_CREDENTIAL = "ticket";

	private User _user = null;
	private IWTimestamp _timeOfLogon = null;
	private LoginTable _loginTable;
	private String _login = null;
	private LoginRecord _loginRecord;
	private String _encryptionType = null;
	private String _loginType = null;
	private Set<String> _userRoles = null;
	private Map<Object, Object> _loggedOnInfoAttribute = new HashMap<Object, Object>();
	private Map<String, IWCredential> credentials = new HashMap<String, IWCredential>();

	public LoggedOnInfo() {
	}

	public void setUser(User user) {
		this._user = user;
		if (user != null) {
			initializePersonalIdCredential(user);
		}
	}

	/**
	 * <p>
	 * Initializes the PersonalIdCredential object set in the Credentials map
	 * </p>
	 * 
	 * @param user
	 */
	private void initializePersonalIdCredential(User user) {
		Map<String, IWCredential> credentials = getCredentials();
		String personalId = user.getPersonalID();
		if (personalId != null) {
			PersonalIdCredential pidCredential = new PersonalIdCredential(
					personalId);
			credentials.put("PersonalIdCredential", pidCredential);
		}
	}

	public void setTimeOfLogon(IWTimestamp timeOfLogon) {
		this._timeOfLogon = timeOfLogon;
	}

	public void setLogin(String login) {
		this._login = login;
	}

	public void setLoginRecord(LoginRecord loginRecord) {
		this._loginRecord = loginRecord;
	}

	public void setEncryptionType(String encryptionType) {
		this._encryptionType = encryptionType;
	}

	public User getUser() {
		return this._user;
	}

	public IWTimestamp getTimeOfLogon() {
		return this._timeOfLogon;
	}

	public String getLogin() {
		return this._login;
	}

	public LoginRecord getLoginRecord() {
		return this._loginRecord;
	}

	public String getEncryptionType() {
		return this._encryptionType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LoggedOnInfo) {
			return this.equals((LoggedOnInfo) obj);
		}
		return false;
	}

	public boolean equals(LoggedOnInfo obj) {
		return this.getUser().equals(obj.getUser());
	}

	public void valueBound(HttpSessionBindingEvent event) {
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		String name = this._user == null ? "Unknown" : this._user.getName();
		HttpSession session = event.getSession();
		LoginBusinessBean loginBean = LoginBusinessBean
				.getLoginBusinessBean(session);
		boolean success = loginBean.logOutUserOnSessionTimeout(session, this);
		System.out
				.println("LoggedOnInfo: Session has expired logging off user: "
						+ name + ". Success = " + success);
	}

	public String getLoginType() {
		return this._loginType;
	}

	public void setLoginType(String loginType) {
		this._loginType = loginType;
	}

	public LoginTable getLoginTable() {
		return this._loginTable;
	}

	public void setLoginTable(LoginTable login) {
		this._loginTable = login;
	}

	public Set<String> getUserRoles() {
		return this._userRoles;
	}

	public void setUserRoles(Set<String> roles) {
		this._userRoles = roles;
	}

	public void setAttribute(Object key, Object value) {
		this._loggedOnInfoAttribute.put(key, value);
	}

	public Object getAttribute(Object key) {
		return this._loggedOnInfoAttribute.get(key);
	}

	public IWCredential putCredential(String originator, IWCredential credential) {
		return this.credentials.put(originator, credential);
	}

	public Map<String, IWCredential> getCredentials() {
		return this.credentials;
	}

	/**
	 * <p>
	 * Gets a Ticket that is used with Single-Sign-On solutions, and if that is
	 * in use. If no single sign-on is set up this method returns null.
	 * </p>
	 * 
	 * @return
	 */
	public String getTicket() {
		Map<String, IWCredential> credentials = getCredentials();
		if (credentials != null) {
			for (Iterator<String> iter = credentials.keySet().iterator(); iter
					.hasNext();) {
				IWCredential credential = credentials.get(iter.next());

				String name = credential.getName();
				Object key = credential.getKey();
				String sKey = key.toString();

				if (name.equals(TICKET_CREDENTIAL)) {
					return sKey;
				}
			}
		}
		return null;
	}
}