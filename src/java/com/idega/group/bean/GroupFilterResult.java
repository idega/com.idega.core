package com.idega.group.bean;

import com.idega.user.data.Group;

public class GroupFilterResult {

	private int level = 0;
	private Group group;
	
	public GroupFilterResult(int level, Group group) {
		this.level = level;
		this.group = group;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}