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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

	public List<Group> findGroups(List<Integer> groupsIds);
	public List<Group> findGroups(List<Integer> groupsIds, Integer from, Integer to);

	@Transactional(readOnly = false)
	public GroupType createGroupType(String type, String description, boolean visibility);

	public GroupType findGroupType(String type);

	public GroupRelationType findGroupRelationType(String type);

	public Group findByGroupTypeAndName(GroupType type, String name);

	public List<Group> getGroupsByType(String groupType);
	public List<Group> getGroupsByType(GroupType groupType);

	public List<Group> getGroupsByNameAndType(String name, String type);

	public List<Group> findGroupsByTypes(List<String> groupTypes);
	public List<Integer> findGroupsIdsByTypes(List<String> groupTypes);
	public List<Group> getGroupsByTypes(List<GroupType> groupTypes);

	public List<Group> getParentGroups(Group group);
	public List<Integer> getParentGroupsIds(List<Integer> ids);

	public List<Group> getParentGroups(Group group, Collection<GroupType> groupTypes);

	public List<Group> getParentGroups(Integer groupId, Collection<String> groupTypes);
	public List<Group> getParentGroups(List<Integer> groupsIds, Collection<String> groupTypes);

	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, Collection<String> groupTypes);

	public void createUniqueRelation(Group group, Group relatedGroup, GroupRelationType relationType, Date initiationDate);

	public List<Group> findTopNodeVisibleGroupsContained(com.idega.core.builder.data.bean.ICDomain containingDomain);
	public int getNumberOfTopNodeVisibleGroupsContained(com.idega.core.builder.data.bean.ICDomain containingDomain);

	public List<Group> findParentGroups(Integer groupId);
	public Collection<Integer> findParentGroupsIds(Integer groupId);

	public Group findGroupByName(String name);

	public List<Integer> getAllGroupsIdsForUser(User user, IWUserContext iwuc);

	public List<Integer> getChildGroupIds(List<Integer> parentGroupsIds, List<String> childGroupTypes);
	public List<Group> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notContainingTypes, Integer from, Integer to);
	public List<Group> getChildGroups(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notContainingTypes, Integer from, Integer to);

	/**
	 *
	 * @param parentGroupsIds
	 * @param childGroupTypes
	 * @param levels - how much recursive steps to make
	 * @return map where key is identifying level number
	 */
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels);

	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes);
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, boolean loadAliases);
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels);

	/**
	 * Checks if groups have users
	 *
	 * @param groups
	 * @return Group ID => flag if has users
	 */
	public Map<Integer, Boolean> hasUsers(List<Group> groups);

	public List<Group> filterGroupsByIdsAndTypes(List<Integer> groupsIds, List<String> groupTypes);
	public <T extends Serializable> List<T> filterGroupsByIdsAndTypes(List<Integer> groupsIds, List<String> groupTypes, Class<T> resultType);

	/**
	 *
	 * @param primaryKeys is {@link Collection} of {@link Group#getId()},
	 * not <code>null</code>;
	 * @return {@link Collection} of {@link Group#getPermissionControllingGroup()}
	 * or {@link Collections#emptyList()};
	 */
	Collection<Integer> findPermissionGroupPrimaryKeys(Collection<Integer> primaryKeys);

	public List<Group> findGroupsByAlias(Group aliasGroup);

	public List<Group> findGroupsByAliasAndName(Group aliasGroup, String groupName);

	public void removeGroup(Integer groupId);

	public List<Group> getGroupsByPersonalId(String personalId);

	public List<Group> getGroupsByName(String name);

	public List<Group> getGroupsByIdsAndTypes(List<Integer> ids, List<String> types);
	public List<Integer> getGroupsIdsByIdsAndTypes(List<Integer> ids, List<String> types);

	public List<Integer> getAllGroupsIds();

	public List<Integer> getDirectGroupIdsForUser(Integer userId);

	public List<Group> findActiveGroupsByType(String groupType);
	public List<Integer> findActiveGroupsIDsByType(String groupType);

	public List<Group> getDirectGroupsForUserByType(Integer userId, List<String> groupTypes);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> havingTypes, List<String> notHavingTypes, Integer from, Integer to);

	public List<String> getGroupTypes();

	public List<Property<Integer, String>> getIdsAndTypes(List<Integer> ids);

	public boolean updateEmails(Group group, List<String> emails);
	public boolean updatePhones(Group group, List<String> numbers);

	public Integer getFirstAncestorGroupIdOfType(Integer groupsId, Collection<String> groupTypes);

}