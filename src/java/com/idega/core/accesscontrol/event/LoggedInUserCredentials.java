package com.idega.core.accesscontrol.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;

public class LoggedInUserCredentials extends ApplicationEvent {

	private static final long serialVersionUID = -3741640128055011169L;

	public enum LoginType {

		CREDENTIALS, AUTHENTICATION_GATEWAY

	}

	private HttpServletRequest request;

	private String serverURL, userName, password;

	private LoginType type;

	private Integer loginId;

	public LoggedInUserCredentials(HttpServletRequest request, String serverURL, String userName, String password, LoginType type, Integer loginId) {
		super(request);

		this.request = request;
		this.serverURL = serverURL;
		this.userName = userName;
		this.password = password;
		this.type = type;
		this.loginId = loginId;
	}

	public HttpServletRequest getRequest() {
		return request;
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

	public LoginType getType() {
		return type;
	}

	public Integer getLoginId() {
		return loginId;
	}

	@Override
	public String toString() {
		return "Username: " + getUserName() + ", type: " + getType() + ", login ID: " + getLoginId();
	}

}