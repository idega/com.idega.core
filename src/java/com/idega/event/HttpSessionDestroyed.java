package com.idega.event;

import org.springframework.context.ApplicationEvent;

public class HttpSessionDestroyed extends ApplicationEvent {

	private static final long serialVersionUID = 3673604280955341276L;

	private String httpSessionId;
	
	public HttpSessionDestroyed(Object source, String httpSessionId) {
		super(source);
		
		this.httpSessionId = httpSessionId;
	}

	public String getHttpSessionId() {
		return httpSessionId;
	}
}
