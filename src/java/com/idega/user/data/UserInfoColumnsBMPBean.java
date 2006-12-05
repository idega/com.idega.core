/*
 * $Id: UserInfoColumnsBMPBean.java,v 1.1.2.2 2006/12/05 23:17:25 idegaweb Exp $
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
	protected static final String INFO_COLUMN_1 = "user_info_1";
	protected static final String INFO_COLUMN_2 = "user_info_2";
	protected static final String INFO_COLUMN_3 = "user_info_3";

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
		addAttribute(INFO_COLUMN_1, "User info 1", true, true, String.class);
		addAttribute(INFO_COLUMN_2, "User info 2", true, true, String.class);
		addAttribute(INFO_COLUMN_3, "User info 3", true, true, String.class);
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

	public String getUserInfo1() {
		return (String) getColumnValue(INFO_COLUMN_1);
	}

	public void setUserInfo1(String userInfo) {
		setColumn(INFO_COLUMN_1, userInfo);
	}

	public String getUserInfo2() {
		return (String) getColumnValue(INFO_COLUMN_2);
	}

	public void setUserInfo2(String userInfo) {
		setColumn(INFO_COLUMN_2, userInfo);
	}

	public String getUserInfo3() {
		return (String) getColumnValue(INFO_COLUMN_3);
	}

	public void setUserInfo3(String userInfo) {
		setColumn(INFO_COLUMN_3, userInfo);
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