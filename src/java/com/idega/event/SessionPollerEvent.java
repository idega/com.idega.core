package com.idega.event;

import org.springframework.context.ApplicationEvent;

public class SessionPollerEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6523470729308962381L;

	public SessionPollerEvent(Object source) {
		super(source);
	}
}
