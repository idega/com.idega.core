package com.idega.core.accesscontrol.event;

import org.springframework.context.ApplicationEvent;

public class LoggedInUserCredentials extends ApplicationEvent {

	private static final long serialVersionUID = -3741640128055011169L;

	private String serverURL, userName, password;

	public LoggedInUserCredentials(Object source, String serverURL, String userName, String password) {
		super(source);

		this.serverURL = serverURL;
		this.userName = userName;
		this.password = password;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

}