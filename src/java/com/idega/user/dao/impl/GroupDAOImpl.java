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
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.dao.GroupDAO;
import com.idega.user.data.bean.Group;
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
		return find(Group.class, groupID);
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
		List<Group> parentGroups = new ArrayList<Group>();
		if (group == null)
			return parentGroups;

		Param param = new Param("relatedGroup", group);
		List<GroupRelation> relations = getResultList("groupRelation.findByRelatedGroup", GroupRelation.class, param);
		for (GroupRelation groupRelation : relations) {
			parentGroups.add(groupRelation.getGroup());
		}

		return parentGroups;
	}

	@Override
	public List<Group> getParentGroups(Group group, Collection<GroupType> groupTypes) {
		List<Group> parentGroups = new ArrayList<Group>();

		Param param1 = new Param("relatedGroup", group);
		Param param2 = new Param("groupTypes", groupTypes);
		List<GroupRelation> relations = getResultList("groupRelation.findByRelatedGroup", GroupRelation.class, param1, param2);
		for (GroupRelation groupRelation : relations) {
			parentGroups.add(groupRelation.getGroup());
		}

		return parentGroups;
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
}