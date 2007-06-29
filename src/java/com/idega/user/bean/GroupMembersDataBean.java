package com.idega.user.bean;

import java.util.List;

public class GroupMembersDataBean {
	
	private String groupName = null;
	private List<GroupMemberDataBean> membersInfo = null;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<GroupMemberDataBean> getMembersInfo() {
		return membersInfo;
	}
	public void setMembersInfo(List<GroupMemberDataBean> membersInfo) {
		this.membersInfo = membersInfo;
	}

}
