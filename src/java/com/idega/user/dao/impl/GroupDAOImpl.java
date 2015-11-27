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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.business.IBOLookup;
import com.idega.core.builder.data.bean.ICDomain;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.dao.GroupDAO;
import com.idega.user.data.GroupBMPBean;
import com.idega.user.data.GroupRelationBMPBean;
import com.idega.user.data.GroupTypeBMPBean;
import com.idega.user.data.GroupTypeConstants;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupDomainRelation;
import com.idega.user.data.bean.GroupDomainRelationType;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.data.bean.GroupRelationType;
import com.idega.user.data.bean.GroupType;
import com.idega.user.data.bean.User;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

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
	public List<Group> findGroups(List<Integer> groupsIds, Integer from, Integer to) {
		if (ListUtil.isEmpty(groupsIds)) {
			return null;
		}

		try {
			return getResultList(Group.QUERY_FIND_BY_IDS, Group.class, from, to, null, new Param("ids", groupsIds));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by IDs: " + groupsIds, e);
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
	public List<Group> getGroupsByTypes(List<GroupType> groupTypes) {
		Param param = new Param("groupTypes", groupTypes);

		return getResultList("group.findAllByGroupTypes", Group.class, param);
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

		String query = "select distinct gdr.relatedGroup from " + GroupDomainRelation.class.getName() + " gdr where gdr.domain.id = " + containingDomain.getId() + " and gdr.relationship.type = '" +
				GroupDomainRelationType.RELATION_TYPE_TOP_NODE + "' and gdr.status is null";

		if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("load_groups_under_domain", false)) {
			try {
				List<Group> groups = getResultListByInlineQuery(query, Group.class);
				getLogger().info("Found groups under domain: " + containingDomain + ": " + query);
				return groups;
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error getting top visible nodes in domain " + containingDomain + ". Query: " + query, e);
			}
		} else {
			getLogger().warning("Groups not loaded under domain: " + containingDomain);
		}

		return Collections.emptyList();
	}

	@Override
	public int getNumberOfTopNodeVisibleGroupsContained(ICDomain containingDomain) {
		List<Group> groups = findTopNodeVisibleGroupsContained(containingDomain);
		return ListUtil.isEmpty(groups) ? 0 : groups.size();
	}

	@Override
	public Collection<Integer> findParentGroupsIds(Integer groupId) {
		List<Group> parentGroups = findParentGroups(groupId);
		if (ListUtil.isEmpty(parentGroups)) {
			return null;
		}

		Collection<Integer> results = new ArrayList<Integer>();
		for (Group parentGroup: parentGroups) {
			results.add(parentGroup.getID());
		}
		return results;
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
		return getChildGroups(Group.class, parentGroupsIds, municipalities, unions, years, notHavingTypes, null, from, to);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notHavingTypes, Integer from, Integer to) {
		return getChildGroups(Integer.class, parentGroupsIds, municipalities, unions, years, notHavingTypes, null, from, to);
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
			Integer to
	) {
		if (ListUtil.isEmpty(parentGroupsIds)) {
			return null;
		}

		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<>();
			params.add(new Param("ids", parentGroupsIds));

			query = new StringBuilder("select ").append(resultType.getName().equals(Integer.class.getName()) ?
					"distinct case g.groupType.groupType when '".concat(GroupTypeBMPBean.TYPE_ALIAS).concat("' then g.alias.id else g.id end as id") :
					"g"
			).append(" from ");
			query.append(GroupRelation.class.getName()).append(" gr inner join gr.relatedGroup g");
			if (!ListUtil.isEmpty(municipalities)) {
				query.append(" inner join gr.group.addresses a");
			}
			query.append(" where gr.group.id in (:ids) and (gr.groupRelationType.type = '").append(GroupBMPBean.RELATION_TYPE_GROUP_PARENT).append("' or gr.groupRelationType is null) ");
			query.append(" and (gr.status = '").append(GroupRelationBMPBean.STATUS_ACTIVE).append("' or gr.status = '").append(GroupRelationBMPBean.STATUS_PASSIVE_PENDING).append("') ");

			if (ListUtil.isEmpty(havingTypes)) {
				//	Making sure only groups will be selected
				if (ListUtil.isEmpty(notHavingTypes)) {
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

			if (!ListUtil.isEmpty(municipalities)) {
				query.append(" and a.city in (:municipalities)");
				params.add(new Param("municipalities", municipalities));
			}

			query.append(" order by g.name");

			List<T> results = getResultListByInlineQuery(query.toString(), resultType, from, to, "groupChildGroupsWithFilterAndTypesAndPaging", ArrayUtil.convertListToArray(params));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting child groups for group(s) " + parentGroupsIds + " by query: " + query.toString(), e);
		}
		return null;
	}

	private List<Integer> getParentGroupsIds(List<Integer> ids, Collection<String> groupTypes) {
		try {
			StringBuilder query = new StringBuilder("select distinct r.group.id from GroupRelation r join r.group g where r.relatedGroup.id in (:ids)");
			query.append(") and g.groupType.groupType in (:groupTypes) and r.status = '").append(GroupRelation.STATUS_ACTIVE).append("' and r.groupRelationType = '");
			query.append(GroupRelation.RELATION_TYPE_GROUP_PARENT).append("'");
			List<Integer> results = getResultListByInlineQuery(query.toString(), Integer.class, new Param("ids", ids), new Param("groupTypes", groupTypes));
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting parent groups for groups with IDs " + ids + " and group types " + groupTypes, e);
		}
		return null;
	}

	@Override
	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, Collection<String> groupTypes) {
		try {
			List<Integer> ids = new ArrayList<>();

			List<Integer> parentIds = null;
			while (!ListUtil.isEmpty(parentIds = getParentGroupsIds(groupsIds, groupTypes))) {
				ids.addAll(parentIds);
				groupsIds = parentIds;
			}

			return ids;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting parent groups for groups with IDs " + groupsIds + " and group types " + groupTypes, e);
		}
		return null;
	}

	@Override
	public List<Integer> getAllGroupsIdsForUser(User user, IWUserContext iwuc) {
		if (user == null) {
			getLogger().warning("User is not provided");
			return null;
		}

		try {
			UserBusiness userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);

			List<Integer> ids = new ArrayList<>();

			Collection<com.idega.user.data.Group> userTopGroups = userBusiness.getUsersTopGroupNodesByViewAndOwnerPermissions(userBusiness.getUser(user.getId()), iwuc);
			List<Integer> parentGroupsIds = new ArrayList<>();
			for (Iterator<com.idega.user.data.Group> groupsIter = userTopGroups.iterator(); groupsIter.hasNext();) {
				parentGroupsIds.add(Integer.valueOf(groupsIter.next().getId()));
			}
			ids.addAll(parentGroupsIds);

			List<Integer> childrenIds = null;
			while (!ListUtil.isEmpty(childrenIds = getChildGroupsIds(parentGroupsIds, null, null, null, null, null, null))) {
				ids.addAll(childrenIds);
				parentGroupsIds = childrenIds;
			}

			return ids;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting all groups for " + user, e);
		}

		return null;
	}

	@Override
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels) {
		return getChildGroups(parentGroupsIds, childGroupTypes, levels, Group.class);
	}

	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes) {
		return getChildGroups(parentGroupsIds, childGroupTypes, null, Integer.class);
	}

	private <T> Map<Integer, List<T>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels, Class<T> resultType) {
		Map<Integer, List<T>> results = new TreeMap<Integer, List<T>>();
		int currentLevel = 1;
		levels = levels == null || levels < 0 ? Integer.MAX_VALUE : levels;
		while (currentLevel <= levels && !ListUtil.isEmpty(parentGroupsIds)) {
			List<T> levelGroups = getChildGroups(resultType, parentGroupsIds, null, null, null, null, childGroupTypes, null, null);
			if (!ListUtil.isEmpty(levelGroups)) {
				results.put(currentLevel, levelGroups);
			}
			currentLevel++;

			parentGroupsIds = getChildGroupsIds(parentGroupsIds, null, null, null, null, null, null);
		}
		return results;
	}

	@Override
	public Map<Integer, Boolean> hasUsers(List<Group> groups) {
		if (ListUtil.isEmpty(groups)) {
			return null;
		}

		List<Integer> ids = new ArrayList<>();
		groups.parallelStream().forEach(group -> {
			if (group != null) {
				Integer id = group.getID();
				if (id != null) {
					ids.add(group.getID());
				}
			}
		});

		try {
			StringBuilder query = new StringBuilder("SELECT gr.ic_group_id, count(gr.related_ic_group_id) FROM IC_GROUP_RELATION gr WHERE gr.IC_GROUP_ID in (");
			for (Iterator<Integer> idsIter = ids.iterator(); idsIter.hasNext();) {
				Integer id = idsIter.next();
				if (id != null) {
					query.append(id);
					if (idsIter.hasNext()) {
						query.append(", ");
					}
				}
			}
			query.append(") and gr.RELATIONSHIP_TYPE = 'GROUP_PARENT' and (gr.GROUP_RELATION_STATUS = 'ST_ACTIVE' or gr.GROUP_RELATION_STATUS = 'PASS_PEND') ")
				.append("and gr.related_GROUP_TYPE = 'ic_user_representative' group by gr.ic_group_id");
			List<Serializable[]> dbResults = SimpleQuerier.executeQuery(query.toString(), 2);
			if (ListUtil.isEmpty(dbResults)) {
				return null;
			}

			Map<Integer, Boolean> results = new HashMap<>();
			for (Serializable[] data: dbResults) {
				if (ArrayUtil.isEmpty(data) || data.length != 2) {
					continue;
				}

				Serializable id = data[0];
				Serializable count = data[0];
				if (id instanceof Number && count instanceof Number) {
					results.put(((Number) id).intValue(), ((Number) count).intValue() > 0);
				}
			}
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error checking if groups " + groups + " have users");
		}

		return null;
	}

}