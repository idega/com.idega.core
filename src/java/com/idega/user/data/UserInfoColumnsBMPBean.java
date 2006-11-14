/*
 * $Id: UserInfoColumnsBMPBean.java,v 1.1.2.1 2006/11/14 15:06:24 idegaweb Exp $
 * Created on Nov 14, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.GenericEntity;

public class UserInfoColumnsBMPBean extends GenericEntity implements UserInfoColumns {

	protected static final String ENTITY_NAME = "ic_user_info_columns";
	protected static final String IC_USER_ID_COLUMN = "ic_user_id";
	protected static final String IC_GROUP_ID_COLUMN = "ic_group_id";
	protected static final String INFO_COLUMN = "user_info";

	public UserInfoColumnsBMPBean() {
		super();
	}

	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(IC_USER_ID_COLUMN, User.class);
		addManyToOneRelationship(IC_GROUP_ID_COLUMN, Group.class);
		addAttribute(INFO_COLUMN, "User info", true, true, String.class);
	}

	public int getUserId() {
		return getIntColumnValue(IC_USER_ID_COLUMN);
	}

	public User getUser() {
		return (User) getColumnValue(IC_USER_ID_COLUMN);
	}

	public void setUserId(int id) {
		setColumn(IC_USER_ID_COLUMN, id);
	}

	public void setUser(User user) {
		setColumn(IC_USER_ID_COLUMN, user);
	}

	public int getGroupId() {
		return getIntColumnValue(IC_GROUP_ID_COLUMN);
	}

	public Group getGroup() {
		return (Group) getColumnValue(IC_GROUP_ID_COLUMN);
	}

	public void setGroupId(int id) {
		setColumn(IC_GROUP_ID_COLUMN, id);
	}

	public void setGroup(Group group) {
		setColumn(IC_GROUP_ID_COLUMN, group);
	}

	public String getUserInfo() {
		return (String) getColumnValue(INFO_COLUMN);
	}

	public void setUserInfo(String userInfo) {
		setColumn(INFO_COLUMN, userInfo);
	}

	//public Collection ejbFindAll() throws FinderException {
	//	return super.idoFindAllIDsBySQL();
	//}
	
	public Collection ejbFindAllByUserIdAndGroupId(int user_id, int group_id) throws FinderException {
			StringBuffer sql = new StringBuffer("select * from ");
			sql.append(ENTITY_NAME);
			sql.append(" where ");
			sql.append(IC_USER_ID_COLUMN);
			sql.append(" = ");
			sql.append(user_id);
			sql.append(" and ");
			sql.append(IC_GROUP_ID_COLUMN);
			sql.append(" = ");
			sql.append(group_id);
			return super.idoFindIDsBySQL(sql.toString());
	}
}