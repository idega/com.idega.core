package com.idega.core.accesscontrol.business;

import com.idega.user.data.bean.User;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author <br>
 *         <a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */
public class LoginContext {

	User user;
	String username;
	String password;

	public LoginContext(User user, String username, String password) {
		this.user = user;
		this.username = username;
		this.password = password;
	}

	public User getUser() {
		return this.user;
	}

	public String getUserName() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}
}
