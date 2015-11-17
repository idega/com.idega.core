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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.builder.data.bean.ICDomain;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.dao.GroupDAO;
import com.idega.user.data.GroupBMPBean;
import com.idega.user.data.GroupRelationBMPBean;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupDomainRelation;
import com.idega.user.data.bean.GroupDomainRelationType;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.data.bean.GroupRelationType;
import com.idega.user.data.bean.GroupType;
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
			getLogger().info("$$$$$$$$$$$$ Groups not loaded under domain: " + containingDomain);
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
	public List<Group> getChildGroups(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, Integer from, Integer to) {
		List<Integer> ids = getChildGroupsIds(parentGroupsIds, municipalities, unions, years, from, to);
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		try {
			return getResultListByInlineQuery("select g from " + Group.class.getName() + " g where g.id in (:ids)", Group.class, new Param("ids", ids));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting child groups by IDs (" + ids + ") for group(s) " + parentGroupsIds + ", municipalities: " + municipalities + ", unions: " + unions +
					", years: " + years + ", from: " + from + ", to: " + to);
		}
		return null;
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, Integer from, Integer to) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<>();
			params.add(new Param("ids", parentGroupsIds));

			query = new StringBuilder("select distinct gr.group.id from ");
			query.append(GroupRelation.class.getName()).append(" gr inner join gr.group as g ");
			if (!ListUtil.isEmpty(municipalities)) {
				query.append(" inner join gr.group.addresses a");
			}
			query.append(" where gr.relatedGroup.id in (:ids) and (gr.groupRelationType.type = '").append(GroupBMPBean.RELATION_TYPE_GROUP_PARENT).append("' or gr.groupRelationType is null) ");
			query.append(" and (gr.status = '").append(GroupRelationBMPBean.STATUS_ACTIVE).append("' or gr.status = '").append(GroupRelationBMPBean.STATUS_PASSIVE_PENDING).append("') ");

			if (!ListUtil.isEmpty(municipalities)) {
				query.append(" and a.city in (:municipalities)");
				params.add(new Param("municipalities", municipalities));
			}

			query.append(" order by g.name");

			return getResultListByInlineQuery(query.toString(), Integer.class, from, to, "groupChildGroupsWithFilterAndPaging", ArrayUtil.convertListToArray(params));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting child groups for group(s) " + parentGroupsIds + ", municipalities: " + municipalities + ", unions: " + unions +
					", years: " + years + ", from: " + from + ", to: " + to + ". Query: " + query.toString());
		}
		return null;
	}

}