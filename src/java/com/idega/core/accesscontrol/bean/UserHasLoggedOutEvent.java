package com.idega.core.accesscontrol.bean;

import org.springframework.context.ApplicationEvent;

public class UserHasLoggedOutEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1890656874303084556L;

	private Integer userId;

	private String loginType;

	public UserHasLoggedOutEvent(Integer userId, String loginType) {
		super(userId);

		this.userId = userId;
		this.loginType = loginType;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

}