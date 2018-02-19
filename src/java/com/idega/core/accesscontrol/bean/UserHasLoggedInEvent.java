package com.idega.core.accesscontrol.bean;

import org.springframework.context.ApplicationEvent;

public class UserHasLoggedInEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2519397058875073171L;

	private Integer userId;

	private String userName, loginType;

	public UserHasLoggedInEvent(Integer userId) {
		super(userId);

		this.userId = userId;
	}

	public UserHasLoggedInEvent(Integer userId, String userName, String loginType) {
		this(userId);

		this.userName = userName;
		this.loginType = loginType;
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

	@Override
	public String toString() {
		return "User ID: " + getUserId() + ", type: " + getLoginType() + ", user name: " + getUserName();
	}

}