package com.idega.bean;

import java.util.List;

public class GroupMembersDataBean {
	
	private String groupName = null;
	private List membersInfo = null;	// Should accept only GroupMemberDataBean instances
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List getMembersInfo() {
		return membersInfo;
	}
	public void setMembersInfo(List membersInfo) {
		this.membersInfo = membersInfo;
	}

}
