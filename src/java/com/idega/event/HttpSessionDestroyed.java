package com.idega.event;

import org.springframework.context.ApplicationEvent;

public class HttpSessionDestroyed extends ApplicationEvent {

	private static final long serialVersionUID = 3673604280955341276L;

	private String httpSessionId;
	private long lastTimeAccessed;
	private int maxInactiveInterval;
	private Integer loggedInUserId;
	private String loginName;

	public HttpSessionDestroyed(Object source, String httpSessionId, long lastTimeAccessed, int maxInactiveInterval, Integer loggedInUserId, String loginName) {
		super(source);

		this.httpSessionId = httpSessionId;
		this.lastTimeAccessed = lastTimeAccessed;
		this.maxInactiveInterval = maxInactiveInterval;
		this.loggedInUserId = loggedInUserId;
		this.loginName = loginName;
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

	public Integer getLoggedInUserId() {
		return loggedInUserId;
	}

	public String getLoginName() {
		return loginName;
	}

}