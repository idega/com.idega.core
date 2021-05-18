/*
 * $Id: GroupDAO.java 1.1 Sep 21, 2009 laddi Exp $
 * Created on Sep 21, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.idega.business.SpringBeanName;
import com.idega.core.persistence.GenericDao;
import com.idega.user.bean.GroupRelationBean;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.data.bean.User;

@SpringBeanName(GroupRelationDAO.BEAN_NAME)
public interface GroupRelationDAO extends GenericDao {

	public static final String BEAN_NAME = "groupRelationDAO";

	public GroupRelation getById(Integer groupRelationId);

	public Long getCountByRelatedGroupType(String relatedGroupType);

	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeOrderedByDate(String relatedGroupType, Integer from, Integer to);

	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndRelatedGroupIdsAndDate(String relatedGroupType, List<Integer> relatedGroupIds, Date dateFrom, Date dateTo);

	public Integer getGroupRelationsCountByRelatedGroupTypeAndRelatedGroupIdsAndDate(String relatedGroupType, List<Integer> relatedGroupIds, Date dateFrom, Date dateTo);

	public List<Object[]> getGroupRelationsCountForPeriod(String relatedGroupType, List<Integer> relatedGroupIds, Date dateFrom, Date dateTo);

	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndGroupTypeAndDate(String relatedGroupType, List<String> groupTypes, Date dateFrom, Date dateTo);

	public Long getGroupRelationsCountByRelatedGroupTypeAndGroupIdAndPeriod(String relatedGroupType, Integer groupId, Date dateFrom, Date dateTo);

	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndGroupIdAndPeriod(String relatedGroupType, List<Integer> groupIds, Date dateFrom, Date dateTo);

	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndGroupId(String relatedGroupType, Integer groupId);

	public List<Object[]> getGroupRelationsCountByRelatedGroupTypeAndGroupIdAndPeriodGroupedByDate(String relatedGroupType, List<Integer> groupId, Date dateFrom, Date dateTo);

	public List<GroupRelationBean> getGroupRelationsByRelatedGroupTypeAndGroupTypes(String relatedGroupType, List<String> groupTypes, List<Integer> ids, List<String> entityTypes);

	public List<GroupRelation> getGroupRelationsByGroupIdAndRelatedGroupId(Integer groupId, Integer relatedGroupId);

	public void onChange(GroupRelation gr);

	public List<GroupRelation> getGroupRelationsByInitiationDate(Timestamp initiationDateStart, Timestamp initiationDateEnd);

	public List<GroupRelation> getTerminatedGroupRelationsByGroupIdsAndPeriod(List<Integer> groupIds, Date dateFrom, Date dateTo);

	public void updateTerminationDate(List<Integer> groupRelationIds, Date newDate);

	public void fixInvalidRelations();
	public List<GroupRelation> findParentGroupRelationsForGroup(Integer id);
	public List<Group> findParentGroupsForGroup(Integer id);
	public void removeParentGroupsForGroup(
			Integer group,
			List<Integer> parents,
			User byUser
	);
	public void storeGroupAsChildForGroups(
			Group group,
			List<Group> parents,
			User byUser
	);

	public List<GroupRelation> getByGroupIdsAndRelatedGroupIds(List<Integer> groupIds, List<Integer> relatedGroupIds);

	public List<GroupRelation> getPassiveGroupByParentGroupIdsAndGroupTypes(List<Integer> groupIds, List<String> groupTypes);

	public void activateTerminatedRelation(GroupRelation groupRelation);
}