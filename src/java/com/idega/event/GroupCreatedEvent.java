package com.idega.event;

import org.springframework.context.ApplicationEvent;

import com.idega.user.data.Group;

public class GroupCreatedEvent extends ApplicationEvent {


	private static final long serialVersionUID = -8248661187533816038L;
	private Group group = null;
	public GroupCreatedEvent(Group group){
		super(group);
		this.group = group;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
}


