/*
 * $Id: GroupDAOImpl.java 1.1 Sep 21, 2009 laddi Exp $
 * Created on Sep 21, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.business.IBOLookup;
import com.idega.core.builder.data.bean.ICDomain;
import com.idega.core.contact.data.ContactType;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.Phone;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.IDOUtil;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.Property;
import com.idega.user.data.GroupBMPBean;
import com.idega.user.data.GroupRelationBMPBean;
import com.idega.user.data.GroupTypeBMPBean;
import com.idega.user.data.GroupTypeConstants;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupDomainRelation;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.data.bean.GroupRelationType;
import com.idega.user.data.bean.GroupType;
import com.idega.user.data.bean.User;
import com.idega.util.ArrayUtil;
import com.idega.util.DBUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("groupDAO")
@Transactional(readOnly = true)
public class GroupDAOImpl extends GenericDaoImpl implements GroupDAO {

	@Override
	public Group findGroup(Integer groupID) {
		try {
			return find(Group.class, groupID);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group by ID: " + groupID, e);
		}
		return null;
	}

	@Override
	public List<Group> findGroups(List<Integer> groupsIds) {
		return findGroups(groupsIds, null, null);
	}

	@Override
	public List<Group> findGroups(List<Integer> groupsIds, Integer from, Integer to) {
		if (ListUtil.isEmpty(groupsIds)) {
			return null;
		}

		try {
			return getResultList(Group.QUERY_FIND_BY_IDS, Group.class, from, to, null, new Param("ids", groupsIds));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by IDs: " + groupsIds + ". From: " + from + ", to: " + to, e);
		}
		return null;
	}

	@Override
	public List<Group> filterGroupsByIdsAndTypes(List<Integer> groupsIds, List<String> groupTypes) {
		return filterGroupsByIdsAndTypes(groupsIds, groupTypes, Group.class);
	}

	@Override
	public <T extends Serializable> List<T> filterGroupsByIdsAndTypes(List<Integer> groupsIds, List<String> groupTypes, Class<T> resultType) {
		if ((ListUtil.isEmpty(groupsIds)) || (ListUtil.isEmpty(groupTypes))){
			return null;
		}

		try {
			List<T> groups = getResultList(
					resultType.getName().equals(Integer.class.getName()) ? Group.QUERY_FIND_ACTIVE_GROUPS_IDS_BY_IDS_AND_TYPES : Group.QUERY_FIND_ACTIVE_GROUPS_BY_IDS_AND_TYPES,
					resultType,
					new Param("ids", groupsIds),
					new Param("groupTypes", groupTypes)
			);
			return ListUtil.isEmpty(groups) ? null : groups;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by IDs: " + groupsIds + " and types " + groupTypes, e);
		}
		return null;
	}

	@Override
	public List<Group> filterActiveParentGroupsByIdsAndTypes(List<Integer> groupsIds, List<String> groupTypes) {
		if ((ListUtil.isEmpty(groupsIds)) || (ListUtil.isEmpty(groupTypes))){
			return null;
		}

		try {
			List<Group> groups = getResultList(
					Group.QUERY_FIND_PARENT_ACTIVE_GROUPS_BY_IDS_AND_TYPES,
					Group.class,
					new Param("ids", groupsIds),
					new Param("groupTypes", groupTypes)
			);
			return ListUtil.isEmpty(groups) ? null : groups;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by IDs: " + groupsIds + " and types " + groupTypes, e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public GroupType createGroupType(String type, String description, boolean visibility) {
		GroupType groupType = new GroupType();
		groupType.setGroupType(type);
		groupType.setDescription(description);
		groupType.setIsVisible(visibility);
		persist(groupType);

		return groupType;
	}

	@Override
	public GroupType findGroupType(String type) {
		return find(GroupType.class, type);
	}

	@Override
	public GroupRelationType findGroupRelationType(String type) {
		return find(GroupRelationType.class, type);
	}

	@Override
	public Group findByGroupTypeAndName(GroupType type, String name) {
		Param param1 = type == null ? null : new Param("groupType", type);
		Param param2 = StringUtil.isEmpty(name) ? null : new Param("name", name);

		if (param1 == null && param2 == null) {
			return null;
		}

		if (param1 != null && param2 != null) {
			return getSingleResult("group.findByGroupTypeAndName", Group.class, param1, param2);
		} else {
			List<Param> params = new ArrayList<Param>();
			String query = "select g from " + Group.class.getName() + " g where ";
			if (param1 != null) {
				query += " g.groupType = :groupType";
				params.add(param1);
			}
			if (param2 != null) {
				query += "g.name = :name";
				params.add(param2);
			}
			List<Group> groups = getResultListByInlineQuery(query, Group.class, ArrayUtil.convertListToArray(params));
			return ListUtil.isEmpty(groups) ? null : groups.get(0);
		}
	}

	@Override
	public List<Group> getGroupsByType(GroupType groupType) {
		Param param = new Param("groupType", groupType);

		return getResultList("group.findAllByGroupType", Group.class, param);
	}

	@Override
	public List<Group> getGroupsByType(String groupType) {
		if (StringUtil.isEmpty(groupType)) {
			return null;
		}

		return getResultList(Group.QUERY_FIND_BY_GROUP_TYPE, Group.class, new Param("groupTypeValue", groupType));
	}

	@Override
	public List<Group> getGroupsByNameAndType(String name, String type) {
		if (StringUtil.isEmpty(name) || StringUtil.isEmpty(type)) {
			return null;
		}

		return getResultList(Group.QUERY_FIND_BY_NAME_AND_GROUP_TYPE, Group.class, new Param("name", name), new Param("groupTypeValue", type));
	}

	@Override
	public List<Group> getGroupsByPersonalId(String personalId) {
		Param param = new Param("personalId", personalId);

		return getResultList(Group.QUERY_FIND_BY_PERSONAL_ID, Group.class, param);
	}

	@Override
	public List<Group> getGroupsByName(String name) {
		Param param = new Param("name", name);

		return getResultList(Group.QUERY_FIND_BY_NAME, Group.class, param);
	}

	@Override
	public List<Group> getGroupsByTypes(List<GroupType> groupTypes) {
		Param param = new Param("groupTypes", groupTypes);

		return getResultList("group.findAllByGroupTypes", Group.class, param);
	}

	@Override
	public List<Group> findGroupsByTypes(List<String> groupTypes) {
		return findGroupsByTypes(groupTypes, Group.class);
	}

	@Override
	public List<Integer> findGroupsIdsByTypes(List<String> groupTypes) {
		return findGroupsByTypes(groupTypes, Integer.class);
	}

	private <T> List<T> findGroupsByTypes(List<String> groupTypes, Class<T> resultType) {
		if (ListUtil.isEmpty(groupTypes)) {
			return null;
		}

		return getResultList(
				resultType.getName().equals(Integer.class.getName()) ? Group.QUERY_FIND_IDS_BY_TYPES : Group.QUERY_FIND_BY_TYPES,
				resultType,
				new Param("groupTypes", groupTypes)
		);
	}

	@Override
	public List<Group> getParentGroups(Group group) {
		if (group == null) {
			getLogger().warning("Parent group is not provided");
			return new ArrayList<Group>();
		}

		return getResultList(GroupRelation.QUERY_FIND_BY_RELATED_GROUP, Group.class, new Param("relatedGroup", group));
	}

	@Override
	public List<Group> getParentGroups(Group group, Collection<GroupType> groupTypes) {
		if (group == null) {
			getLogger().warning("Parent group is not provided");
			return new ArrayList<Group>();
		}

		Param param1 = new Param("relatedGroup", group);
		Param param2 = new Param("groupTypes", groupTypes);
		return getResultList(GroupRelation.QUERY_FIND_BY_RELATED_GROUP_AND_TYPE, Group.class, param1, param2);
	}

	@Override
	public List<Group> getParentGroups(Integer groupId, Collection<String> groupTypes) {
		if (groupId == null) {
			getLogger().warning("Child group id is not provided");
			return new ArrayList<Group>();
		}

		Param param1 = new Param("relatedGroupId", groupId);
		Param param2 = new Param("groupTypes", groupTypes);
		return getResultList(GroupRelation.QUERY_FIND_BY_RELATED_GROUP_ID_AND_TYPE, Group.class, param1, param2);
	}

	@Override
	public List<Group> getParentGroups(List<Integer> groupsIds, Collection<String> groupTypes) {
		if (ListUtil.isEmpty(groupsIds) || ListUtil.isEmpty(groupTypes)) {
			return null;
		}

		return getResultList(GroupRelation.QUERY_FIND_BY_RELATED_GROUPS_IDS_AND_TYPES, Group.class, new Param("relatedGroupsIds", groupsIds), new Param("groupTypes", groupTypes));
	}

	@Override
	public void createUniqueRelation(Group group, Group relatedGroup, GroupRelationType relationType, Date initiationDate) {
		GroupRelation relation = new GroupRelation();
		relation.setGroup(group);
		relation.setRelatedGroup(relatedGroup);
		relation.setRelatedGroupType(relatedGroup.getGroupType());
		if (initiationDate != null) {
			relation.setInitiationDate(initiationDate);
		}
		persist(relation);
	}

	@Override
	public List<Group> findTopNodeVisibleGroupsContained(ICDomain containingDomain) {
		if (containingDomain == null) {
			getLogger().warning("Domain is not provided");
			return Collections.emptyList();
		}

		try {
			List<Group> groups = getResultList(GroupDomainRelation.QUERY_FIND_TOP_NODES_UNDER_DOMAIN, Group.class, new Param("domainId", containingDomain.getId()));
			return groups;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting top visible nodes in domain " + containingDomain, e);
		}

		return Collections.emptyList();
	}

	@Override
	public int getNumberOfTopNodeVisibleGroupsContained(ICDomain containingDomain) {
		if (containingDomain == null) {
			getLogger().warning("Domain is not provided");
			return 0;
		}

		try {
			Integer number = getSingleResult(GroupDomainRelation.QUERY_COUNT_TOP_NODES_UNDER_DOMAIN, Integer.class, new Param("domainId", containingDomain.getId()));
			return number;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting top visible nodes in domain " + containingDomain, e);
		}

		return 0;
	}

	@Override
	public Collection<Integer> findParentGroupsIds(Integer groupId) {
		if (groupId != null) {
			return getResultList(
					GroupRelation.QUERY_FIND_PARENT_IDS,
					Integer.class,
					new Param("ids", groupId));
		}

		return Collections.emptyList();
	}

	@Override
	public Collection<Integer> findPermissionGroupPrimaryKeys(
			Collection<Integer> primaryKeys) {
		if (!ListUtil.isEmpty(primaryKeys)) {
			return getResultList(
					Group.QUERY_FIND_PERMISSION_GROUP_IDS,
					Integer.class,
					new Param("ids", primaryKeys));
		}

		return Collections.emptyList();
	}

	@Override
	public List<Group> findParentGroups(Integer groupId) {
		String query = "select distinct gr.group from " + GroupRelation.class.getName() + " gr where gr.relatedGroup.id = " + groupId +
				" and (gr.groupRelationType.type='GROUP_PARENT' OR gr.groupRelationType.type is null) and (gr.status = '" +
				GroupRelation.STATUS_ACTIVE + "' OR gr.status = '" + GroupRelation.STATUS_PASSIVE_PENDING + "')";
		return getResultListByInlineQuery(query, Group.class);
	}

	@Override
	public List<Group> getChildGroups(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notHavingTypes, Integer from, Integer to) {
		return getChildGroups(parentGroupsIds, municipalities, unions, years, notHavingTypes, from, to, false);
	}
	private List<Group> getChildGroups(
			List<Integer> parentGroupsIds,
			List<String> municipalities,
			List<String> unions,
			List<String> years,
			List<String> notHavingTypes,
			Integer from,
			Integer to,
			boolean loadAliases
	) {
		return getChildGroups(Group.class, parentGroupsIds, municipalities, unions, years, notHavingTypes, null, from, to, loadAliases);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notHavingTypes, Integer from, Integer to) {
		return getChildGroups(Integer.class, parentGroupsIds, municipalities, unions, years, notHavingTypes, null, from, to, false);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> havingTypes, List<String> notHavingTypes, Integer from, Integer to) {
		return getChildGroups(Integer.class, parentGroupsIds, null, null, null, notHavingTypes, havingTypes, from, to, false);
	}

	private <T> List<T> getChildGroups(
			Class<T> resultType,
			List<Integer> parentGroupsIds,
			List<String> municipalities,
			List<String> unions,
			List<String> years,
			List<String> notHavingTypes,
			List<String> havingTypes,
			Integer from,
			Integer to,
			boolean loadAliases
	) {
		if (ListUtil.isEmpty(parentGroupsIds)) {
			return null;
		}

		if (!ListUtil.isEmpty(havingTypes)) {
			ArrayList<String> types = new ArrayList<String>();
			for (String havingType: havingTypes) {
				if (havingType != null && !havingType.equals("null")) {
					types.add(havingType);
				}
			}

			if (!ListUtil.isEmpty(types)) {
				havingTypes = types;
			} else {
				havingTypes = null;
			}
		}

		List<Param> params = new ArrayList<>();
		StringBuilder query = null;
		try {
			params.add(new Param("ids", parentGroupsIds));

			query = new StringBuilder("select ").append(
					resultType.getName().equals(Integer.class.getName()) ?
									loadAliases ?	"distinct g.id as id" :
													"distinct case g.groupType.groupType when '".concat(GroupTypeBMPBean.TYPE_ALIAS).concat("' then g.alias.id else g.id end as id") :
									"g"
			).append(" from ");
			query.append(GroupRelation.class.getName()).append(" gr inner join gr.relatedGroup g");
			if (!ListUtil.isEmpty(municipalities) && !municipalities.contains("null")) {
				query.append(" inner join gr.group.addresses a");
			}
			query.append(" where gr.group.id in (:ids) and (gr.groupRelationType.type = '").append(GroupBMPBean.RELATION_TYPE_GROUP_PARENT).append("' or gr.groupRelationType is null) ");
			query.append(" and (gr.status = '").append(GroupRelationBMPBean.STATUS_ACTIVE).append("' or gr.status = '").append(GroupRelationBMPBean.STATUS_PASSIVE_PENDING).append("') ");

			if (ListUtil.isEmpty(havingTypes) || havingTypes.contains("null")) {
				//	Making sure only groups will be selected
				if (ListUtil.isEmpty(notHavingTypes) || notHavingTypes.contains("null")) {
					notHavingTypes = new ArrayList<>();
				}
				if (!notHavingTypes.contains(com.idega.user.data.bean.UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE)) {
					notHavingTypes.add(com.idega.user.data.bean.UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE);
				}
				query.append(" and g.groupType.groupType not in (:notHavingTypes) ");
				params.add(new Param("notHavingTypes", notHavingTypes));
			} else {
				if (havingTypes.contains(GroupTypeConstants.GROUP_TYPE_ALIAS)) {
					query.append(" and gr.relatedGroupType.groupType in (:havingTypes) ");
				} else {
					query.append(" and g.groupType.groupType in (:havingTypes) ");
				}
				params.add(new Param("havingTypes", havingTypes));
			}

			if (!ListUtil.isEmpty(municipalities) && !municipalities.contains("null")) {
				query.append(" and a.city in (:municipalities)");
				params.add(new Param("municipalities", municipalities));
			}

			if (resultType.getName().equals(Integer.class.getName())) {
				if (!loadAliases) {
					query.append(" and ((g.groupType.groupType = '".concat(GroupTypeBMPBean.TYPE_ALIAS).concat("' and g.alias.id is not null) or (g.id is not null and g.groupType.groupType <> '"
						.concat(GroupTypeBMPBean.TYPE_ALIAS).concat("'))")));
				}
			}

			query.append(" order by g.name");

			List<T> results = null;
			if (!ListUtil.isEmpty(params)) {
				results = getResultListByInlineQuery(
						query.toString(),
						resultType,
						from,
						to,
						"groupChildGroupsWithFilterAndTypesAndPaging",
						ArrayUtil.convertListToArray(params));
			} else {
				results = getResultListByInlineQuery(
						query.toString(),
						resultType,
						from,
						to,
						"groupChildGroupsWithFilterAndTypesAndPaging");
			}

			return new ArrayList<>(results);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting child groups for group(s) " + parentGroupsIds + " by query: " + query.toString(), e);
		}
		return null;
	}

	@Override
	public List<Integer> getParentGroupsIds(List<Integer> ids) {
		return getParentGroupsIds(ids, false);
	}

	@Override
	public List<Integer> getParentGroupsIds(List<Integer> ids, boolean selectPassive) {
		try {
			if (ListUtil.isEmpty(ids)) {
				return null;
			}

			List<Param> params = new ArrayList<Param>();
			StringBuilder query = new StringBuilder("select distinct r.group.id from ");
			query.append(GroupRelation.class.getName()).append(" r join r.group g where r.relatedGroup.id in (:ids) ");
			if (!selectPassive) {
				query.append(" and (r.status = '").append(GroupRelation.STATUS_ACTIVE).append("' or r.status = '").append(GroupRelationBMPBean.STATUS_PASSIVE_PENDING).append("')");
			}
			query.append(" and r.groupRelationType = '").append(GroupRelation.RELATION_TYPE_GROUP_PARENT).append("'");
			params.add(new Param("ids", ids));

			List<Integer> results = getResultListByInlineQuery(query.toString(), Integer.class, ArrayUtil.convertListToArray(params));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting parent groups for groups with IDs " + ids, e);
		}
		return null;
	}

	@Override
	public Integer getFirstAncestorGroupIdOfType(Integer groupId, List<String> groupTypes) {
		return getFirstAncestorGroupIdOfType(groupId, groupTypes, false);
	}

	@Override
	public Integer getFirstAncestorGroupIdOfType(Integer groupId, List<String> groupTypes, boolean selectPassive) {
		if (groupId == null) {
			return null;
		}

		List<Integer> ids = getFirstAncestorGroupIdsOfType(Arrays.asList(groupId), groupTypes, selectPassive);
		return ListUtil.isEmpty(ids) ? null : ids.iterator().next();
	}

	@Override
	public List<Integer> getFirstAncestorGroupIdsOfType(List<Integer> groupsIds, List<String> groupTypes, boolean selectPassive) {
		if (ListUtil.isEmpty(groupsIds) || ListUtil.isEmpty(groupTypes)) {
			return null;
		}

		try {
			List<Integer> ids = new ArrayList<>();
			List<Integer> parentIds = null;
			while (ListUtil.isEmpty(ids) && !ListUtil.isEmpty(parentIds = getParentGroupsIds(groupsIds, selectPassive))) {
				if (parentIds.size() == groupsIds.size() && parentIds.containsAll(groupsIds)) {
					groupsIds = null;
				} else {
					ids.addAll(parentIds);
					groupsIds = parentIds;
					if (!ListUtil.isEmpty(groupTypes) && !ListUtil.isEmpty(ids)) {
						ids = getGroupsIdsByIdsAndTypes(ids, new ArrayList<>(groupTypes));
					}
				}
			}
			return ids;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting parent groups for group with IDs " + groupsIds + " and group types " + groupTypes, e);
		}
		return null;
	}

	@Override
	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, Collection<String> groupTypes) {
		try {
			List<Integer> ids = new ArrayList<>();

			List<Integer> parentIds = null;
			while (!ListUtil.isEmpty(parentIds = getParentGroupsIds(groupsIds))) {
				if (parentIds.size() == groupsIds.size() && parentIds.containsAll(groupsIds)) {
					groupsIds = null;
				} else {
					ids.addAll(parentIds);
					groupsIds = parentIds;
				}
			}

			if (!ListUtil.isEmpty(groupTypes) && !ListUtil.isEmpty(ids)) {
				ids = getGroupsIdsByIdsAndTypes(ids, new ArrayList<>(groupTypes));
			}

			return ids;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting parent groups for groups with IDs " + groupsIds + " and group types " + groupTypes, e);
		}
		return null;
	}

	@Override
	public List<Integer> getDirectGroupIdsForUser(Integer userId) {
		if (userId == null) {
			return null;
		}

		try {
			return getResultList(GroupRelation.QUERY_FIND_DIRECT_GROUP_IDS_FOR_USER, Integer.class, new Param("userId", userId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Erorr getting direct groups for user " + userId, e);
		}

		return null;
	}

	@Override
	public List<Integer> getAllGroupsIdsForUser(User user, IWUserContext iwuc) {
		return getAllGroupsIdsForUser(user, iwuc, false);
	}

	private Collection<com.idega.user.data.Group> getUserGroupsByPermissions(IWUserContext iwuc, User user, UserBusiness userBusiness) throws Exception {
		return userBusiness.getUsersTopGroupNodesByViewAndOwnerPermissions(userBusiness.getUser(user.getId()), iwuc);
	}
	private Collection<com.idega.user.data.Group> getUserGroupsByHierarchy(User user, UserBusiness userBusiness, GroupBusiness groupBusiness) throws Exception {
		return groupBusiness.getParentGroups(userBusiness.getUser(user.getId()));
	}

	@Override
	public List<Integer> getAllGroupsIdsForUser(User user, IWUserContext iwuc, boolean byPermissions) {
		if (user == null) {
			getLogger().warning("User is not provided");
			return null;
		}

		try {
			List<Integer> ids = new ArrayList<>();

			UserBusiness userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
			GroupBusiness groupBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), GroupBusiness.class);
			Collection<com.idega.user.data.Group> userTopGroups = null;
			if (byPermissions) {
				userTopGroups = getUserGroupsByPermissions(iwuc, user, userBusiness);
				if (ListUtil.isEmpty(userTopGroups)) {
					userTopGroups = getUserGroupsByHierarchy(user, userBusiness, groupBusiness);
					if (ListUtil.isEmpty(userTopGroups)) {
						getLogger().warning("Did not find any top groups for " + user + " (ID: " + user.getId() + ", personal ID: " + user.getPersonalID() + ") by permissions" +
							" and also did not find any directly related groups");
					}
				}
			} else {
				userTopGroups = getUserGroupsByHierarchy(user, userBusiness, groupBusiness);
				if (ListUtil.isEmpty(userTopGroups)) {
					userTopGroups = getUserGroupsByPermissions(iwuc, user, userBusiness);
					if (ListUtil.isEmpty(userTopGroups)) {
						getLogger().warning("Did not find any top groups for " + user + " (ID: " + user.getId() + ", personal ID: " + user.getPersonalID() + ") directly related groups " +
							" and also did not find any groups by permissions");
					}
				}
			}

			List<Integer> parentGroupsIds = new ArrayList<>();
			List<Integer> groupsToSkip = Arrays.asList(489017, 329932, 329937);
			for (Iterator<com.idega.user.data.Group> groupsIter = userTopGroups.iterator(); groupsIter.hasNext();) {
				Integer groupId = Integer.valueOf(groupsIter.next().getId());
				if (!groupsToSkip.contains(groupId)) {
					parentGroupsIds.add(groupId);
				}
			}
			ids.addAll(parentGroupsIds);

			List<Integer> childrenIds = null;
			while (!ListUtil.isEmpty(childrenIds = getChildGroupsIds(parentGroupsIds, null, null, null, null, null, null))) {
				if (childrenIds.size() == parentGroupsIds.size() && childrenIds.containsAll(parentGroupsIds)) {
					parentGroupsIds = null;
				} else {
					ids.addAll(childrenIds);
					parentGroupsIds = childrenIds;
				}
			}

			return getUniqueIds(ids);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting all groups for " + user, e);
		}

		return null;
	}

	private List<Integer> getUniqueIds(List<Integer> ids) {
		if (ListUtil.isEmpty(ids)) {
			return ids;
		}

		Map<Integer, Boolean> uniqueIds = new ConcurrentHashMap<>();
		ids.parallelStream().forEach(id -> {
			uniqueIds.put(id, Boolean.TRUE);
		});

		return new ArrayList<>(uniqueIds.keySet());
	}

	@Override
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels) {
		return getChildGroups(parentGroupsIds, childGroupTypes, notHavingChildGroupTypes, levels, Group.class, false);
	}
	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels) {
		return getChildGroups(parentGroupsIds, childGroupTypes, notHavingChildGroupTypes, levels, Integer.class, false);
	}

	@Override
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels) {
		return getChildGroups(parentGroupsIds, childGroupTypes, levels, Group.class, false);
	}

	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes) {
		return getChildGroupsIds(parentGroupsIds, childGroupTypes, false);
	}
	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels) {
		return getChildGroups(parentGroupsIds, childGroupTypes, null, levels, Integer.class, false);
	}
	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, boolean loadAliases) {
		return getChildGroups(parentGroupsIds, childGroupTypes, null, Integer.class, loadAliases);
	}

	@Override
	public List<Integer> getChildGroupIds(List<Integer> parentGroupsIds, List<String> childGroupTypes) {
		Map<Integer, List<Integer>> data = getChildGroups(parentGroupsIds, childGroupTypes, null, Integer.class, false);
		if (MapUtil.isEmpty(data)) {
			return Collections.emptyList();
		}

		List<Integer> ids = new ArrayList<>();
		for (List<Integer> childGroupsIds: data.values()) {
			ids.addAll(childGroupsIds);
		}
		return ids;
	}

	@Override
	public List<Group> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes) {
		Map<Integer, List<Group>> data = getChildGroups(parentGroupsIds, childGroupTypes, null, Group.class, false);
		if (MapUtil.isEmpty(data)) {
			return Collections.emptyList();
		}

		List<Group> groups = new ArrayList<>();
		for (List<Group> childGroups: data.values()) {
			groups.addAll(childGroups);
		}
		return groups;
	}

	private <T> Map<Integer, List<T>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels, Class<T> resultType, boolean loadAliases) {
		Map<Integer, List<T>> results = new TreeMap<Integer, List<T>>();
		int currentLevel = 1;
		levels = levels == null || levels < 0 ? Integer.MAX_VALUE : levels;
		while (currentLevel <= levels && !ListUtil.isEmpty(parentGroupsIds)) {
			List<T> levelGroups = getChildGroups(resultType, parentGroupsIds, null, null, null, notHavingChildGroupTypes, childGroupTypes, null, null, loadAliases);
			if (!ListUtil.isEmpty(levelGroups)) {
				results.put(currentLevel, levelGroups);
			}
			currentLevel++;

			parentGroupsIds = getChildGroupsIds(parentGroupsIds, null, null, null, null, null, null);
		}
		return results;
	}

	private <T> Map<Integer, List<T>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels, Class<T> resultType, boolean loadAliases) {
		return getChildGroups(parentGroupsIds, childGroupTypes, null, levels, resultType, loadAliases);
	}

	@Override
	public Map<Integer, Boolean> hasUsers(List<Group> groups) {
		if (ListUtil.isEmpty(groups)) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		try {
			query.append("SELECT gr.group.id, count(gr.relatedGroup.id) FROM ").append(GroupRelation.class.getName()).append(" gr WHERE gr.group.id in (");
			for (Iterator<Group> groupsIter = groups.iterator(); groupsIter.hasNext();) {
				Group group = groupsIter.next();
				if (group == null) {
					continue;
				}
				group = DBUtil.getInstance().lazyLoad(group);

				if (group != null) {
					Integer id = group.getID();
					if (id != null) {
						query.append(id);
						if (groupsIter.hasNext()) {
							query.append(", ");
						}
					}
				}
			}
			query.append(") and gr.groupRelationType.type = 'GROUP_PARENT' and (gr.status = 'ST_ACTIVE' or gr.status = 'PASS_PEND') ")
				.append("and gr.relatedGroupType.groupType = 'ic_user_representative' group by gr.group.id");
			List<Object[]> dbResults = getResultListByInlineQuery(query.toString(), Object[].class);
			if (ListUtil.isEmpty(dbResults)) {
				return null;
			}

			Map<Integer, Boolean> results = new HashMap<>();
			for (Object[] data: dbResults) {
				if (ArrayUtil.isEmpty(data) || data.length != 2) {
					continue;
				}

				Object id = data[0];
				Object count = data[0];
				if (id instanceof Number && count instanceof Number) {
					results.put(((Number) id).intValue(), ((Number) count).intValue() > 0);
				}
			}
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error checking if groups have users. Query: " + query.toString(), e);
		}

		return null;
	}

	@Override
	public List<Group> findGroupsByAlias(Group aliasGroup) {
		if (aliasGroup == null) {
			return null;
		}

		try {
			return getResultList(Group.QUERY_FIND_BY_ALIAS, Group.class, new Param("alias", aliasGroup));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by alias: " + aliasGroup, e);
		}
		return null;
	}

	@Override
	public List<Group> findGroupsByAliasAndName(Group aliasGroup, String groupName) {
		if (aliasGroup == null) {
			return null;
		}

		try {
			Param param1 = new Param("alias", aliasGroup);
			Param param2 = StringUtil.isEmpty(groupName) ? null : new Param("name", groupName);

			if (param1 != null && param2 != null) {
				return getResultList(Group.QUERY_FIND_BY_ALIAS_AND_NAME, Group.class, param1, param2);
			} else {
				getResultList(Group.QUERY_FIND_BY_ALIAS, Group.class, param1);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by alias: " + aliasGroup, e);
		}
		return null;
	}


	@Override
	@Transactional(readOnly = false)
	public void removeGroup(Integer groupId) {
		if (groupId == null) {
			getLogger().warning("Group ID is not provided");
			return;
		}

		try {
			Group group = findGroup(groupId);
			if (group != null) {
				remove(group);
			} else {
				throw new Exception("Group is not found.");
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not remove the group with ID " + groupId + ". Error message was: " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Group findGroupByName(String name) {
		if(StringUtil.isEmpty(name)) {
			return null;
		}
		List<Group> groups = getResultList("group.findByName", Group.class, new Param("name", name));
		if(ListUtil.isEmpty(groups)) {
			return null;
		}
		return groups.get(0);
	}

	@Override
	public List<Group> getGroupsByIdsAndTypes(List<Integer> ids, List<String> types) {
		if (ListUtil.isEmpty(ids)) {
			getLogger().warning("IDs not provided");
			return null;
		}

		if (ListUtil.isEmpty(types)) {
			getLogger().warning("Types not provided");
			return null;
		}

		try {
			return getResultList(Group.QUERY_FIND_GROUPS_BY_IDS_AND_TYPES, Group.class, new Param("ids", ids), new Param("types", types));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by IDs (" + ids + ") and types " + types, e);
		}

		return null;
	}

	@Override
	public List<Integer> getGroupsIdsByIdsAndTypes(List<Integer> ids, List<String> types) {
		if (ListUtil.isEmpty(ids)) {
			getLogger().warning("IDs not provided");
			return null;
		}

		if (ListUtil.isEmpty(types)) {
			getLogger().warning("Types not provided");
			return null;
		}

		try {
			return getResultList(Group.QUERY_FIND_IDS_BY_IDS_AND_TYPES, Integer.class, new Param("ids", ids), new Param("types", types));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by IDs (" + ids + ") and types " + types, e);
		}

		return null;
	}

	@Override
	public List<Integer> getAllGroupsIds() {
		try {
			return getResultList(Group.QUERY_FIND_ALL_IDS, Integer.class);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting IDs for all groups", e);
		}
		return null;
	}


	@Override
	public List<Group> findActiveGroupsByTypes(List<String> groupTypes) {
		if (ListUtil.isEmpty(groupTypes)) {
			getLogger().log(Level.WARNING, "Types are not provided");
			return null;
		}

		try {
			return getResultList(
					Group.QUERY_FIND_ACTIVE_GROUPS_BY_TYPES,
					Group.class,
					new Param("groupType", groupTypes)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting active groups by group types " + groupTypes, e);
		}
		return null;
	}

	@Override
	public List<Group> findActiveGroupsByType(String groupType) {
		return findActiveGroupsByType(groupType, Group.class);
	}

	@Override
	public List<Integer> findActiveGroupsIDsByType(String groupType) {
		return findActiveGroupsByType(groupType, Integer.class);
	}

	private <T> List<T> findActiveGroupsByType(String groupType, Class<T> resultType) {
		if (StringUtil.isEmpty(groupType)) {
			getLogger().log(Level.WARNING, "Type is not provided");
			return null;
		}

		try {
			return getResultList(
					resultType.getName().equals(Integer.class.getName()) ? Group.QUERY_FIND_ACTIVE_GROUPS_IDS_BY_TYPE : Group.QUERY_FIND_ACTIVE_GROUPS_BY_TYPE,
					resultType,
					new Param("groupType", groupType)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting active groups by group type " + groupType, e);
		}
		return null;
	}

	@Override
	public List<Group> getDirectGroupsForUserByType(Integer userId, List<String> groupTypes) {
		if (userId == null || ListUtil.isEmpty(groupTypes)) {
			return null;
		}

		try {
			return getResultList(GroupRelation.QUERY_FIND_DIRECT_GROUPS_FOR_USER_BY_TYPE, Group.class, new Param("userId", userId),  new Param("groupTypes", groupTypes));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Erorr getting direct groups for user " + userId, e);
		}

		return null;
	}

	@Override
	public List<String> getGroupTypes() {
		try {
			return getResultList(GroupType.QUERY_FIND_GROUP_TYPES, String.class);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group types", e);
		}
		return null;
	}

	@Override
	public List<Property<Integer, String>> getIdsAndTypes(List<Integer> ids) {
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		try {
			List<Object[]> data = getResultList(Group.QUERY_FIND_IDS_AND_TYPES_BY_IDS, Object[].class, new Param("ids", ids));
			if (ListUtil.isEmpty(data)) {
				return null;
			}

			List<Property<Integer, String>> results = new ArrayList<>();
			for (Object[] dataItem: data) {
				if (!ArrayUtil.isEmpty(dataItem) && dataItem.length > 1 && dataItem[0] != null && dataItem[1] != null) {
					results.add(new Property<Integer, String>((Integer) dataItem[0], (String) dataItem[1]));
				}
			}

			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting IDs and types by IDs: " + ids, e);
		}

		return null;
	}

	@Override
	public boolean updateEmails(Group group, List<String> emails) {
		return updateContacts(group, emails, Email.class);
	}

	@Override
	public boolean updatePhones(Group group, List<String> numbers) {
		return updateContacts(group, numbers, Phone.class);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	private <T extends ContactType> boolean updateContacts(Group group, List<String> contacts, Class<T> contactType) {
		try {
			if (group == null || ListUtil.isEmpty(contacts) || contactType == null) {
				return false;
			}

			List<? extends ContactType> currentContacts = null;

			boolean email = Email.class.getName().equals(contactType.getName()), phone = Phone.class.getName().equals(contactType.getName());

			if (email) {
				currentContacts = group.getEmails();
			} else if (phone) {
				currentContacts = group.getPhones();
			}

			Set<T> unchangedContacts = new HashSet<>();

			Set<T> newContacts = null;
			for (String contact: contacts) {
				if (StringUtil.isEmpty(contact)) {
					continue;
				}
				contact = contact.trim();
				if (StringUtil.isEmpty(contact)) {
					continue;
				}

				ContactType existingContact = null;
				if (!ListUtil.isEmpty(currentContacts)) {
					for (Iterator<? extends ContactType> currentContactsIter = currentContacts.iterator(); (currentContactsIter.hasNext() && existingContact == null);) {
						existingContact = currentContactsIter.next();
						if (contact.equals(existingContact.getContact())) {
							unchangedContacts.add((T) existingContact);
						} else {
							existingContact = null;
						}
					}
				}
				if (existingContact == null) {
					ContactType newContact = contactType.newInstance();
					newContact.setContact(contact);
					persist(newContact);

					if (newContact != null && newContact.getId() != null) {
						newContacts = newContacts == null ? new HashSet<>() : newContacts;
						newContacts.add((T) newContact);
					}
				}
			}

			List<T> contactsForGroup = null;
			Set<T> tmpContactsForGroup = new HashSet<>();
			if (ListUtil.isEmpty(currentContacts)) {
				contactsForGroup = newContacts == null ? null : new ArrayList<>(newContacts);
			} else {
				if (!ListUtil.isEmpty(newContacts)) {
					tmpContactsForGroup.addAll(newContacts);
				}
				if (!ListUtil.isEmpty(currentContacts)) {
					currentContacts.retainAll(unchangedContacts);
				}
				if (!ListUtil.isEmpty(currentContacts)) {
					tmpContactsForGroup.addAll((List<T>) currentContacts);
				}
				contactsForGroup = new ArrayList<>(tmpContactsForGroup);
			}

			if (email) {
				group.setEmails((List<Email>) contactsForGroup);
			} else if (phone) {
				group.setPhones((List<Phone>) contactsForGroup);
			}

			//Save
			merge(group);
			return true;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error updating contacts " + contacts + " for group " + group, e);
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean setMetadata(Group group, String key, String value) {
		try {
			group.setMetaData(key, value);
			merge(group);
			return true;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error setting metadata " + key + "=" + value + " for group " + group, e);
		}
		return false;
	}

	@Override
	public List<Integer> getGroupsIdsByGroupTypeAndMetadata(String type, String key, String value) {
		if (StringUtil.isEmpty(type) || StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
			return null;
		}

		try {
			return getResultList(
					Group.QUERY_FIND_IDS_BY_TYPE_AND_METADATA,
					Integer.class,
					new Param("metadataKey", key),
					new Param("metadataValue", value),
					new Param("groupType", type)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups IDs by type " + type + " and metadada: " + key + "=" + value, e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.GroupDAO#findAliases(java.util.Collection)
	 */
	@Override
	public Map<Integer, Integer> findAliasesIds(Collection<Integer> groupIds) {
		HashMap<Integer, Integer> map = new HashMap<>();

		if (!ListUtil.isEmpty(groupIds)) {
			StringBuilder query = new StringBuilder();
			query
			.append("SELECT g.IC_GROUP_ID, g.ALIAS_ID ")
			.append("FROM ic_group g ")
			.append("WHERE g.ALIAS_ID IS NOT NULL ")
			.append("AND g.IC_GROUP_ID IN (")
			.append(IDOUtil.toString(groupIds))
			.append(")");

			List<Serializable[]> lines = null;
			try {
				lines = SimpleQuerier.executeQuery(query.toString(), 2);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to get results by query: " + query.toString());
			}

			if (!ListUtil.isEmpty(lines)) {
				for (Serializable[] column : lines) {
					map.put((Integer) column[0], (Integer) column[1]);
				}
			}
		}

		return map;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.GroupDAO#findAliases(java.util.Collection)
	 */
	@Override
	public Map<Integer, Group> findAliases(Collection<Integer> groupIds) {
		Map<Integer, Group> map = new HashMap<>();

		Map<Integer, Integer> aliases = findAliasesIds(groupIds);
		if (!MapUtil.isEmpty(aliases)) {
			List<Group> aliasGroups = findGroups(new ArrayList<>(aliases.values()), null, null);
			if (!ListUtil.isEmpty(aliasGroups)) {
				for (Group aliasGroup : aliasGroups) {
					for (Entry<Integer, Integer> entry : aliases.entrySet()) {
						if (entry.getValue().equals(aliasGroup.getID())) {
							map.put(entry.getKey(), aliasGroup);
						}
					}
				}
			}
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.GroupDAO#update(com.idega.user.data.bean.Group)
	 */
	@Override
	@Transactional(readOnly = false)
	public <T extends Group> T update(T entity) {
		if (entity != null) {
			if (entity.getId() == null) {
				persist(entity);
				if (entity.getID() != null) {
					getLogger().fine("Entity: " + entity + " created!");
					return entity;
				}
			} else {
				entity = merge(entity);
				if (entity != null) {
					getLogger().fine("Entity: " + entity + " updated");
					return entity;
				}
			}
		}

		getLogger().warning("Failed to create/update entity: " + entity);
		return null;
	}

}