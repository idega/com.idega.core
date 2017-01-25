package com.idega.core.accesscontrol.bean;

import org.springframework.context.ApplicationEvent;

public class UserHasLoggedInEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2519397058875073171L;

	private Integer userId;

	private String userName;

	public UserHasLoggedInEvent(Integer userId, String userName) {
		super(userId);

		this.userId = userId;
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

}