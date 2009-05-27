/*
 * Created on 13.8.2004
 * 
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.user.data;

import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDORelationshipException;

/**
 * @author aron
 * 
 *         TopNodeGroupBMPBean keeps track of users top group nodes to be
 *         displayed in group tree
 */
public class TopNodeGroupBMPBean extends GenericEntity implements TopNodeGroup {

	public final static String TABLE_NAME = "IC_USER_TOPNODES";
	public final static String COLUMN_USER_ID = "USER_ID";
	public final static String COLUMN_GROUP_ID = "GROUP_ID";
	public final static String COLUMN_LOGIN_DURATION = "LOGIN_DURATION";
	public final static String COLUMN_NUMBER_OF_PERMISSIONS = "NUMBER_OF_PERMISSIONS";
	public final static String COLUMN_LASTCHANGED = "LAST_CHANGED";
	public final static String COLUMN_COMMENT = "TN_COMMENT";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return TABLE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addManyToOneRelationship(COLUMN_USER_ID, User.class);
		setAsPrimaryKey(COLUMN_USER_ID, true);
		setNullable(COLUMN_USER_ID, false);

		addManyToOneRelationship(COLUMN_GROUP_ID, Group.class);
		setAsPrimaryKey(COLUMN_GROUP_ID, true);
		setNullable(COLUMN_GROUP_ID, false);

		addAttribute(COLUMN_LOGIN_DURATION, "Login duration", true, true, String.class);
		addAttribute(COLUMN_NUMBER_OF_PERMISSIONS, "Number of permissions", true, true, Integer.class);
		addAttribute(COLUMN_LASTCHANGED, "Last changed", true, true, Timestamp.class);
		addAttribute(COLUMN_COMMENT, "Comment", true, true, String.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.IDOEntityBean#getPrimaryKeyClass()
	 */
	public Class getPrimaryKeyClass() {
		return TopNodeGroupKey.class;
	}

	public Object ejbFindByPrimaryKey(TopNodeGroupKey primaryKey) throws FinderException {
		return super.ejbFindByPrimaryKey(primaryKey);
	}

	protected boolean doInsertInCreate() {
		return true;
	}

	public Object ejbCreate(TopNodeGroupKey primaryKey) throws CreateException {
		setPrimaryKey(primaryKey);
		return super.ejbCreate();
	}

	public Object ejbCreate(Integer userID, Integer groupID) throws CreateException {
		TopNodeGroupKey primaryKey = new TopNodeGroupKey(userID, groupID);
		setPrimaryKey(primaryKey);
		return super.ejbCreate();
	}

	public Integer getUserId() {
		return getIntegerColumnValue(COLUMN_USER_ID);
	}

	public void setUserId(Integer id) {
		setColumn(COLUMN_USER_ID, id);
	}

	public Integer getGroupId() {
		return getIntegerColumnValue(COLUMN_GROUP_ID);
	}

	public void setGroupId(Integer id) {
		setColumn(COLUMN_GROUP_ID, id);
	}

	public Timestamp getLastChanged() {
		return getTimestampColumnValue(COLUMN_LASTCHANGED);
	}

	public void setLastChanged(Timestamp stamp) {
		setColumn(COLUMN_LASTCHANGED, stamp);
	}

	public String getComment() {
		return getStringColumnValue(COLUMN_COMMENT);
	}

	public void setComment(String comment) {
		setColumn(COLUMN_COMMENT, comment);
	}

	public String getLoginDuration() {
		return getStringColumnValue(COLUMN_LOGIN_DURATION);
	}

	public void setLoginDuration(String login_duration) {
		setColumn(COLUMN_LOGIN_DURATION, login_duration);
	}

	public void setNumberOfPermissions(Integer number_of_permissions) {
		setColumn(COLUMN_NUMBER_OF_PERMISSIONS, number_of_permissions);
	}

	public Integer getNumberOfPermissions() {
		return getIntegerColumnValue(COLUMN_NUMBER_OF_PERMISSIONS);
	}

	public Collection ejbFindByUser(Integer userID) throws FinderException {
		return super.idoFindPKsByQuery(idoQueryGetSelect().appendWhereEquals(COLUMN_USER_ID, userID));
	}

	public Collection ejbFindByUser(User user) throws FinderException {
		return super.idoFindPKsByQuery(idoQueryGetSelect().appendWhereEquals(COLUMN_USER_ID, (Integer) user.getPrimaryKey()));
	}

	public Collection ejbFindByGroup(Integer groupID) throws FinderException {
		return super.idoFindPKsByQuery(idoQueryGetSelect().appendWhereEquals(COLUMN_GROUP_ID, groupID));
	}

	public Collection ejbHomegetTopNodeGroups(User user) throws IDORelationshipException {
		String sql = "Select gr.* from ic_group gr," + TABLE_NAME + " tn where gr.ic_group_id = tn." + COLUMN_GROUP_ID + " and tn." + COLUMN_USER_ID + " = " + user.getPrimaryKey().toString();
		return super.idoGetRelatedEntitiesBySQL(Group.class, sql);
	}
}
