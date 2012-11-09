package com.idega.core.accesscontrol.bean;

import org.springframework.context.ApplicationEvent;

public class UserHasLoggedInEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2519397058875073171L;

	private Integer userId;

	public UserHasLoggedInEvent(Integer userId) {
		super(userId);

		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

}