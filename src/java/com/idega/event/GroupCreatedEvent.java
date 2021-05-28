package com.idega.event;

import com.idega.user.data.Group;

public class GroupCreatedEvent extends GroupEvent {

	private static final long serialVersionUID = -8248661187533816038L;

	public GroupCreatedEvent(Group group){
		super(group);
	}

	public GroupCreatedEvent(com.idega.user.data.bean.Group group){
		super(group);
	}

}