package com.idega.event;

import org.springframework.context.ApplicationEvent;

import com.idega.user.data.Group;

public class GroupEvent extends ApplicationEvent {

	private static final long serialVersionUID = -4894633897785450449L;

	private Group group = null;

	private com.idega.user.data.bean.Group groupEntity = null;

	public GroupEvent(Group group){
		super(group);

		this.group = group;
	}

	public GroupEvent(com.idega.user.data.bean.Group group){
		super(group);

		this.groupEntity = group;
	}

	public <T> T getGroup() {
		if (group != null) {
			@SuppressWarnings("unchecked")
			T result = (T) group;
			return result;
		}
		if (groupEntity != null) {
			@SuppressWarnings("unchecked")
			T result = (T) groupEntity;
			return result;
		}
		return null;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public com.idega.user.data.bean.Group getGroupEntity() {
		return groupEntity;
	}

	public void setGroupEntity(com.idega.user.data.bean.Group groupEntity) {
		this.groupEntity = groupEntity;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getGroup();
	}

}