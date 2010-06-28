package com.idega.event;

import org.springframework.context.ApplicationEvent;

public class HttpSessionDestroyed extends ApplicationEvent {

	private static final long serialVersionUID = 3673604280955341276L;

	private String httpSessionId;
	private long lastTimeAccessed;
	private int maxInactiveInterval;
	
	public HttpSessionDestroyed(Object source, String httpSessionId, long lastTimeAccessed, int maxInactiveInterval) {
		super(source);
		
		this.httpSessionId = httpSessionId;
		this.lastTimeAccessed = lastTimeAccessed;
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public String getHttpSessionId() {
		return httpSessionId;
	}

	public long getLastTimeAccessed() {
		return lastTimeAccessed;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
}
