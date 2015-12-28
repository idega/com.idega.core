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

import java.util.Date;
import java.util.List;

import com.idega.business.SpringBeanName;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.GroupRelation;

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

	public List<GroupRelation> getGroupRelationsByRelatedGroupTypeAndGroupIdAndPeriod(String relatedGroupType, Integer groupId, Date dateFrom, Date dateTo);


}