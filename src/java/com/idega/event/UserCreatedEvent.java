package com.idega.event;

import org.springframework.context.ApplicationEvent;

import com.idega.user.data.User;

public class UserCreatedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -3764910670507278163L;
	
	private User user;
	
	public UserCreatedEvent(Object source, User user) {
		super(source);
		
		this.user = user;
	}

	public User getUser() {
		return user;
	}
	
}