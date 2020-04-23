package com.idega.core.accesscontrol.bean;

import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEvent;

import com.idega.presentation.IWContext;

public class UserHasLoggedInEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2519397058875073171L;

	private Integer userId;

	private String userName, loginType;

	private HttpSession session;

	private IWContext iwc;

	public UserHasLoggedInEvent(Integer userId) {
		super(userId);

		this.userId = userId;
	}

	public UserHasLoggedInEvent(Integer userId, String userName, String loginType) {
		this(userId, userName, loginType, null);
	}

	public UserHasLoggedInEvent(Integer userId, String userName, String loginType, HttpSession session) {
		this(userId);

		this.userName = userName;
		this.loginType = loginType;

		this.session = session;
	}

	public UserHasLoggedInEvent(IWContext iwc, Integer userId, String userName, String loginType, HttpSession session) {
		this(userId, userName, loginType, iwc == null ? session : iwc.getSession());

		this.iwc = iwc;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getLoginType() {
		return loginType;
	}

	public HttpSession getSession() {
		return session;
	}

	public IWContext getIwc() {
		return iwc;
	}

	@Override
	public String toString() {
		return "User ID: " + getUserId() + ", type: " + getLoginType() + ", user name: " + getUserName();
	}

}