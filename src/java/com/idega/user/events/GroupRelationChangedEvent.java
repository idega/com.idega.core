package com.idega.user.events;

import org.springframework.context.ApplicationEvent;

public class GroupRelationChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1391585374701171810L;

	private String reason = null;
	
	public GroupRelationChangedEvent(Object source) {
		super(source);
	}

	public GroupRelationChangedEvent(String reason) {
		super(reason);
		this.setReason(reason);
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}