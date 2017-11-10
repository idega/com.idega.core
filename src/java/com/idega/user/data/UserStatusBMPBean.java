/*
 * $Id$
 *
 * Copyright (C) 2000-2003 Idega Software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega Software. Use is
 * subject to license terms.
 */
package com.idega.user.data;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;

/**
 * @author palli
 *
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class UserStatusBMPBean extends GenericEntity implements UserStatus {

	protected static final String ENTITY_NAME = "ic_usergroup_status";
	protected static final String STATUS_ID = "status_id";
	protected static final String IC_USER = "ic_user_id";
	protected static final String IC_GROUP = "ic_group_id";
	protected static final String DATE_FROM = "date_from";
	protected static final String DATE_TO = "date_to";
	protected static final String CREATED_BY = "created_by";
	public UserStatusBMPBean() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(STATUS_ID, Status.class);
		addManyToOneRelationship(IC_USER, User.class);
		addManyToOneRelationship(IC_GROUP, Group.class);
		addManyToOneRelationship(CREATED_BY, User.class);
		addAttribute(DATE_FROM, "Date from", true, true, java.sql.Timestamp.class);
		addAttribute(DATE_TO, "Date to", true, true, java.sql.Timestamp.class);

		addIndex("IDX_IC_USERGROUP_STATUS_1", new String[]{IC_USER, DATE_FROM});
	}

	@Override
	public int getStatusId() {
		return getIntColumnValue(STATUS_ID);
	}

	@Override
	public Status getStatus() {
		return (Status) getColumnValue(STATUS_ID);
	}

	@Override
	public void setStatusId(int id) {
		setColumn(STATUS_ID, id);
	}

	@Override
	public void setStatus(Status status) {
		setColumn(STATUS_ID, status);
	}

	@Override
	public int getUserId() {
		return getIntColumnValue(IC_USER);
	}

	@Override
	public User getUser() {
		return (User) getColumnValue(IC_USER);
	}

	@Override
	public void setUserId(int id) {
		setColumn(IC_USER, id);
	}

	@Override
	public void setUser(User user) {
		setColumn(IC_USER, user);
	}

	@Override
	public User getCreatedBy() {
		return (User) getColumnValue(CREATED_BY);
	}

	@Override
	public int getCreatedById() {
		return getIntColumnValue(CREATED_BY);
	}

	@Override
	public void setCreatedBy(int userID) {
		setColumn(CREATED_BY, userID);
	}

	@Override
	public void setCreatedBy(Integer userID) {
		setColumn(CREATED_BY, userID);
	}

	@Override
	public void setCreatedBy(User user) {
		setColumn(CREATED_BY, user);
	}

	@Override
	public int getGroupId() {
		return getIntColumnValue(IC_GROUP);
	}

	@Override
	public Group getGroup() {
		return (Group) getColumnValue(IC_GROUP);
	}

	@Override
	public void setGroupId(int id) {
		setColumn(IC_GROUP, id);
	}

	@Override
	public void setGroup(Group group) {
		setColumn(IC_GROUP, group);
	}

	@Override
	public void setDateFrom(Timestamp from) {
		setColumn(DATE_FROM, from);
	}

	@Override
	public Timestamp getDateFrom() {
		return (Timestamp) getColumnValue(DATE_FROM);
	}

	@Override
	public void setDateTo(Timestamp to) {
		setColumn(DATE_TO, to);
	}

	@Override
	public Timestamp getDateTo() {
		return (Timestamp) getColumnValue(DATE_TO);
	}

	public Collection ejbFindAll() throws FinderException {
		return super.idoFindAllIDsBySQL();
	}

	public Collection ejbFindAllByUserId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(id);
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllActiveByUserId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(id);
		sql.append(" and ");
		sql.append(DATE_TO);
		sql.append(" is NULL ");
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllByStatusId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(STATUS_ID);
		sql.append(" = ");
		sql.append(id);
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindPKsBySQL(sql.toString());
	}

	public Collection ejbFindAllActiveByStatusId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(STATUS_ID);
		sql.append(" = ");
		sql.append(id);
		sql.append(" and ");
		sql.append(DATE_TO);
		sql.append(" is NULL ");
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindPKsBySQL(sql.toString());
	}

	public Collection ejbFindAllActiveByGroupId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_GROUP);
		sql.append(" = ");
		sql.append(id);
		sql.append(" and ");
		sql.append(DATE_TO);
		sql.append(" is NULL ");
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllActiveBySearchCriteria(List<Integer> groupIds, List<String> roles) throws FinderException {
		StringBuffer sql = new StringBuffer("select ugs.* from ");
		sql.append(ENTITY_NAME).append(" as ugs ");
		if (!ListUtil.isEmpty(roles)) {
			sql.append(", ").append(StatusBMPBean.ENTITY_NAME).append(" as us ");
		}
		sql.append(" where ");
		sql.append(" 1 = 1 ");
		sql.append(" and ");
		if (!ListUtil.isEmpty(roles)) {
			sql.append(" us.ic_user_status_id = ugs.status_id ");
			sql.append(" and ");
			sql.append(" us.").append(StatusBMPBean.STATUS_LOC_KEY).append(" IN (");
			for (Iterator<String> rolesIter = roles.iterator(); rolesIter.hasNext();) {
				sql.append("'").append(rolesIter.next()).append("'");
				if (rolesIter.hasNext()) {
					sql.append(CoreConstants.COMMA).append(CoreConstants.SPACE);
				}
			}
			sql.append(") and ");
		}
		sql.append(IC_GROUP);
		sql.append(" IN (");
		for (Iterator<Integer> idsIter = groupIds.iterator(); idsIter.hasNext();) {
			sql.append(idsIter.next());
			if (idsIter.hasNext()) {
				sql.append(CoreConstants.COMMA).append(CoreConstants.SPACE);
			}
		}
		sql.append(")");
		sql.append(" and ");
		sql.append(DATE_TO);
		sql.append(" is NULL ");
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllByGroupId(int id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_GROUP);
		sql.append(" = ");
		sql.append(id);
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllByUserIdAndGroupId(int user_id, int group_id) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(user_id);
		sql.append(" and ");
		sql.append(IC_GROUP);
		sql.append(" = ");
		sql.append(group_id);
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllActiveByUserIdAndGroupId(int user_id, int group_id) throws FinderException {
		StringBuffer sql = new StringBuffer("select ");
		sql.append(getIDColumnName());
		sql.append(" from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(user_id);
		sql.append(" and ");
		sql.append(IC_GROUP);
		sql.append(" = ");
		sql.append(group_id);
		sql.append(" and ");
		sql.append(DATE_TO);
		sql.append(" is NULL ");
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllByUserIDAndStatusID(Integer userID, Integer statusID) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(userID);
		sql.append(" and ");
		sql.append(STATUS_ID);
		sql.append(" = ");
		sql.append(statusID);
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}

	public Collection ejbFindAllActiveByUserIDAndStatusID(Integer userID, Integer statusID) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(ENTITY_NAME);
		sql.append(" where ");
		sql.append(IC_USER);
		sql.append(" = ");
		sql.append(userID);
		sql.append(" and ");
		sql.append(STATUS_ID);
		sql.append(" = ");
		sql.append(statusID);
		sql.append(" and ");
		sql.append(DATE_TO);
		sql.append(" is NULL ");
		sql.append(" order by ");
		sql.append(DATE_FROM);
		return super.idoFindIDsBySQL(sql.toString());
	}
}