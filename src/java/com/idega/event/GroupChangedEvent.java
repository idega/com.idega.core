package com.idega.event;

import com.idega.user.data.Group;

public class GroupChangedEvent extends GroupEvent {

	private static final long serialVersionUID = -9065009885192950106L;

	public GroupChangedEvent(Group group){
		super(group);
	}

	public GroupChangedEvent(com.idega.user.data.bean.Group group){
		super(group);
	}

}