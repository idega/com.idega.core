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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.idega.business.SpringBeanName;
import com.idega.core.persistence.GenericDao;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupRelationType;
import com.idega.user.data.bean.GroupType;
import com.idega.user.data.bean.User;

@SpringBeanName("groupDAO")
public interface GroupDAO extends GenericDao {

	public Group findGroup(Integer groupID);

	public List<Group> findGroups(List<Integer> groupsIds, Integer from, Integer to);

	@Transactional(readOnly = false)
	public GroupType createGroupType(String type, String description, boolean visibility);

	public GroupType findGroupType(String type);

	public GroupRelationType findGroupRelationType(String type);

	public Group findByGroupTypeAndName(GroupType type, String name);

	public List<Group> getGroupsByType(GroupType groupType);

	public List<Group> getGroupsByTypes(List<GroupType> groupTypes);

	public List<Group> getParentGroups(Group group);

	public List<Group> getParentGroups(Group group, Collection<GroupType> groupTypes);

	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, Collection<GroupType> groupTypes);

	public void createUniqueRelation(Group group, Group relatedGroup, GroupRelationType relationType, Date initiationDate);

	public List<Group> findTopNodeVisibleGroupsContained(com.idega.core.builder.data.bean.ICDomain containingDomain);
	public int getNumberOfTopNodeVisibleGroupsContained(com.idega.core.builder.data.bean.ICDomain containingDomain);

	public List<Group> findParentGroups(Integer groupId);
	public Collection<Integer> findParentGroupsIds(Integer groupId);

	public List<Integer> getAllGroupsIdsForUser(User user, IWUserContext iwuc);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notContainingTypes, Integer from, Integer to);
	public List<Group> getChildGroups(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notContainingTypes, Integer from, Integer to);

	public List<Group> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);

}