/*
 * $Id$
 *
 * Copyright (C) 2000-2003 Idega Software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega Software.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import com.idega.data.GenericEntity;

/**
 * @author palli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserStatusBMPBean extends GenericEntity implements UserStatus {
	private static final String ENTITY_NAME = "ic_user_status";
	private static final String STATUS_LOC_KEY = "status_key";
	private static final String IC_USER = "ic_user_id";
	private static final String IC_GROUP = "ic_group_id";

	public UserStatusBMPBean() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(STATUS_LOC_KEY,"Localized key to status name",true,true,String.class);
		addManyToOneRelationship(IC_USER,User.class);
		addManyToOneRelationship(IC_GROUP,Group.class);
	}

	public String getStatusKey() {
		return getStringColumnValue(STATUS_LOC_KEY);
	}
	
	public void setStatusKey(String key) {
		setColumn(STATUS_LOC_KEY,key);
	}
	
	public int getUserId() {
		return getIntColumnValue(IC_USER);
	}
	
	public User getUser() {
		return (User)getColumn(IC_USER);
	}
	
	public void setUserId(int id) {
		setColumn(IC_USER,id);
	}
	
	public void setUser(User user) {
		setColumn(IC_USER, user);
	}
	
	public int getGroupId() {
		return getIntColumnValue(IC_GROUP);
	}
	
	public Group getGroup() {
		return (Group)getColumn(IC_GROUP);
	}
	
	public void setGroupId(int id) {
		setColumn(IC_GROUP,id);
	}
	
	public void setGroup(Group group) {
		setColumn(IC_GROUP, group);
	}	
}