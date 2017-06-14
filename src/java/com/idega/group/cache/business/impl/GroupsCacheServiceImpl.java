package com.idega.group.cache.business.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.business.DefaultSpringBean;
import com.idega.core.persistence.Param;
import com.idega.group.cache.bean.CachedGroup;
import com.idega.group.cache.business.GroupsCacheService;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.user.bean.GroupRelationBean;
import com.idega.user.dao.GroupDAO;
import com.idega.user.data.GroupTypeConstants;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.data.bean.UserGroupRepresentative;
import com.idega.user.events.GroupRelationChangedEvent;
import com.idega.user.events.GroupRelationChangedEvent.EventType;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class GroupsCacheServiceImpl extends DefaultSpringBean implements GroupsCacheService {

	@Autowired
	private GroupDAO groupDAO;

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds) {
		return getChildGroupIds(parentGroupsIds, null, null);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> notContainingTypes, Integer from, Integer to) {
		return getChildGroupsIds(parentGroupsIds, null, notContainingTypes, null, from, to);
	}

	@Override
	public List<Integer> getChildGroupIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels) {
		Map<Integer, List<Integer>> allIds = getChildGroupsIds(parentGroupsIds, childGroupTypes, levels);
		if (MapUtil.isEmpty(allIds)) {
			return null;
		}

		Set<Integer> results = new HashSet<>();
		for (List<Integer> ids: allIds.values()) {
			if (ListUtil.isEmpty(ids)) {
				continue;
			}

			results.addAll(ids);
		}

		return new ArrayList<>(results);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notContainingTypes, Integer levels, Integer from, Integer to) {
		List<Integer> ids = getAllRelatedGroupsIds(parentGroupsIds, childGroupTypes, false, levels);
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		Set<Integer> results = null;
		if (ListUtil.isEmpty(notContainingTypes)) {
			results = new HashSet<>(ids);
		} else {
			results = new HashSet<>();
			for (Integer id: ids) {
				CachedGroup group = groups.get(id);
				if (group == null || !group.isActive() || notContainingTypes.contains(group.getType())) {
					continue;
				}

				results.add(group.getId());
			}
		}
		if (ListUtil.isEmpty(results)) {
			return null;
		}

		List<Integer> allResults = new ArrayList<>(results);
		if (from != null && to != null && from <= allResults.size() && to <= allResults.size()) {
			return allResults.subList(from, to);
		}

		return allResults;
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notHavingTypes, Integer from, Integer to) {
		if (!ListUtil.isEmpty(municipalities) || !ListUtil.isEmpty(unions) || !ListUtil.isEmpty(years)) {
			return groupDAO.getChildGroupsIds(parentGroupsIds, municipalities, unions, years, notHavingTypes, from, to);
		}

		return getChildGroupsIds(parentGroupsIds, notHavingTypes, from, to);
	}

	@Override
	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, List<String> groupTypes) {
		return getAllRelatedGroupsIds(groupsIds, groupTypes, true, null);
	}

	@Override
	public List<Group> getParentGroups(Integer id, List<String> groupTypes) {
		if (id == null) {
			return null;
		}

		return getParentGroups(Arrays.asList(id), groupTypes);
	}

	@Override
	public List<Integer> getParentGroupsIds(List<Integer> ids, List<String> groupTypes) {
		return getParentGroupsIdsRecursive(ids, groupTypes);
	}

	@Override
	public List<Group> getParentGroups(List<Integer> ids, List<String> groupTypes) {
		List<Integer> parentGroupsIds = getParentGroupsIds(ids, groupTypes);
		if (parentGroupsIds == null) {
			return null;
		}

		return groupDAO.findGroups(parentGroupsIds);
	}

	@Override
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels) {
		return getChildGroups(parentGroupsIds, childGroupTypes, null, levels);
	}

	@Override
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels) {
		Map<Integer, List<Integer>> data = getChildGroupsIds(parentGroupsIds, childGroupTypes, notHavingChildGroupTypes, levels);
		if (MapUtil.isEmpty(data)) {
			return null;
		}

		Set<Integer> ids = new HashSet<>();
		for (List<Integer> levelIds: data.values()) {
			if (ListUtil.isEmpty(levelIds)) {
				continue;
			}

			ids.addAll(levelIds);
		}

		Map<Integer, List<Group>> results = new HashMap<>();
		List<Group> groups = groupDAO.findGroups(new ArrayList<>(ids));
		if (ListUtil.isEmpty(groups)) {
			return results;
		}

		for (Group group: groups) {
			if (group == null) {
				continue;
			}

			Integer id = group.getID();
			if (id == null) {
				continue;
			}

			for (Integer level: data.keySet()) {
				List<Integer> levelIds = data.get(level);
				if (ListUtil.isEmpty(levelIds) || !levelIds.contains(id)) {
					continue;
				}

				List<Group> levelGroups = results.get(level);
				if (levelGroups == null) {
					levelGroups = new ArrayList<>();
					results.put(level, levelGroups);
				}
				levelGroups.add(group);
			}
		}
		return results;
	}

	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels) {
		Map<Integer, List<Integer>> results = new HashMap<>();

		Map<Integer, List<Integer>> leveledIds = getAllRelatedGroupsIds(parentGroupsIds, childGroupTypes, levels, false);
		if (MapUtil.isEmpty(leveledIds)) {
			return null;
		}

		for (Integer level: leveledIds.keySet()) {
			List<Integer> ids = leveledIds.get(level);
			if (ListUtil.isEmpty(ids)) {
				continue;
			}

			Set<Integer> idsToLoad = null;
			if (ListUtil.isEmpty(notHavingChildGroupTypes)) {
				idsToLoad = new HashSet<>(ids);
			} else {
				idsToLoad = new HashSet<>();
				for (Integer id: ids) {
					CachedGroup group = groups.get(id);
					if (group == null || !group.isActive() || notHavingChildGroupTypes.contains(group.getType())) {
						continue;
					}

					idsToLoad.add(id);
				}
			}

			if (!ListUtil.isEmpty(idsToLoad)) {
				results.put(level, new ArrayList<>(idsToLoad));
			}
		}

		return results;
	}

	@Override
	public void doUpdateGroupRelations(GroupRelationChangedEvent event, List<String> groupTypesForChangesAtEntities) {
		try {
			if (!isCacheEnabled("_relations")) {
				return;
			}

			Integer relationId = event == null ? null : event.getGroupRelationId();
			if (relationId == null) {
				return;
			}

			Integer groupId = event.getGroupId();
			String groupName = event.getGroupName();
			String groupType = event.getGroupType();

			Integer relatedGroupId = event.getRelatedGroupId();
			String relatedGroupName = event.getRelatedGroupName();
			String relatedGroupType = event.getRelatedGroupType();

			String status = event.getStatus();

			Timestamp initiationDate = event.getInitiationDate();
			Timestamp terminationDate = event.getTerminationDate();
			Timestamp initiationModificationDate = event.getInitiationModificationDate();
			Timestamp terminationModificationDate = event.getTerminationModificationDate();
			if (groupId == null || relatedGroupId == null) {
				GroupRelation groupRelation = groupDAO.find(GroupRelation.class, relationId);
				if (groupRelation == null) {
					getLogger().warning("Did not find group relation by ID: " + relationId);
					return;
				} else {
					getLogger().info("Found group relation by ID: " + relationId);
				}

				Group group = groupRelation.getGroup();
				groupId = group == null ? null : group.getID();
				groupName = group == null ? groupName : group.getName();
				groupType = group == null ? null : group.getType();

				Group relatedGroup = groupRelation.getRelatedGroup();
				relatedGroupId = relatedGroup == null ? null : relatedGroup.getID();
				relatedGroupName = relatedGroup == null ? relatedGroupName : relatedGroup.getName();
				relatedGroupType = relatedGroup == null ? null : relatedGroup.getType();

				status = groupRelation.getStatus();

				Date tmp = groupRelation.getInitiationDate();
				initiationDate = tmp == null ? null : new Timestamp(tmp.getTime());

				tmp = groupRelation.getTerminationDate();
				terminationDate = tmp == null ? null : new Timestamp(tmp.getTime());

				tmp = groupRelation.getInitiationModificationDate();
				initiationModificationDate = tmp == null ? null : new Timestamp(tmp.getTime());

				tmp = groupRelation.getTerminationModificationDate();
				terminationModificationDate = tmp == null ? null : new Timestamp(tmp.getTime());
			}

			if (groupId == null || relatedGroupId == null || StringUtil.isEmpty(groupType) || StringUtil.isEmpty(relatedGroupType)) {
				getLogger().warning("Can not update relations cache: insufficient data. ID of changed relation: " + relationId);
				return;
			}

			if (UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE.equals(relatedGroupType) && !isUserCacheOn()) {
				return;
			}

			boolean active = isActive(status);

			GroupRelationBean relation = getRelations().get(relationId);
			if (relation == null) {
				relation = new GroupRelationBean(
						relationId,
						groupId,
						groupName,
						groupType,
						relatedGroupId,
						relatedGroupName,
						relatedGroupType,
						active,
						initiationDate,
						terminationDate,
						initiationModificationDate,
						terminationModificationDate
				);
			} else {
				relation.setActive(active);
				relation.setInitiationDate(initiationDate);
				relation.setTerminationDate(terminationDate);
				relation.setInitiationModificationDate(initiationModificationDate);
				relation.setTerminationModificationDate(terminationModificationDate);
			}

			CachedGroup cachedParent = groups.get(groupId);
			CachedGroup parent = cachedParent == null ?
					new CachedGroup(relationId, groupId, groupName, groupType, true) :
					cachedParent;
			CachedGroup child = new CachedGroup(relationId, relatedGroupId, relatedGroupName, relatedGroupType, active);

			addRelations(relation, parent, child);

			addGroupRelationBean(relation, groupTypesForChangesAtEntities);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error updating group relation: " + event.getGroupRelationId(), e);
		}
	}

	private boolean isActive(String status) {
		return status != null && (GroupRelation.STATUS_ACTIVE.equals(status) || GroupRelation.STATUS_ACTIVE_PENDING.equals(status));
	}

	private Map<Integer, GroupRelationBean> relations = null;
	@Override
	public Map<Integer, GroupRelationBean> getRelations() {
		if (relations == null) {
			relations = new HashMap<>();
		}
		return relations;
	}

	private Map<String, Set<Integer>> relatedGroupTypeRelations = null;
	@Override
	public Map<String, Set<Integer>> getRelatedGroupTypeRelationsIds() {
		if (relatedGroupTypeRelations == null) {
			relatedGroupTypeRelations = new HashMap<>();
		}
		return relatedGroupTypeRelations;
	}

	/*			Integer -> group ID, Set<Integer> -> children groups	*/
	private Map<Integer, Set<Integer>> childrenOfGroups = null;
	@Override
	public Map<Integer, Set<Integer>> getChildrenOfGroups() {
		if (childrenOfGroups == null) {
			childrenOfGroups = new HashMap<>();
		}
		return childrenOfGroups;
	}

	/*			Integer -> group ID, Set<Integer> -> parent groups	*/
	private Map<Integer, Set<Integer>> parentsOfGroups = null;
	@Override
	public Map<Integer, Set<Integer>> getParentsOfGroups() {
		if (parentsOfGroups == null) {
			parentsOfGroups = new HashMap<>();
		}
		return parentsOfGroups;
	}

	private Map<Integer, CachedGroup> groups = new HashMap<>();
	@Override
	public Map<Integer, CachedGroup> getGroups() {
		return groups;
	}

	private Map<String, Map<Integer, Boolean>> types = null;
	@Override
	public Map<String, Map<Integer, Boolean>> getTypes() {
		if (types == null) {
			types = new HashMap<>();
		}
		return types;
	}

	private Map<Integer, Integer> aliases = null;
	@Override
	public Map<Integer, Integer> getAliases() {
		if (aliases == null) {
			aliases = new HashMap<>();
		}
		return aliases;
	}

	private Map<Integer, Integer> groupsAliases = null;
	private Map<Integer, Integer> getGroupsAliases() {
		if (groupsAliases == null) {
			groupsAliases = new HashMap<>();
		}
		return groupsAliases;
	}

	@Override
	public void doStartCachingGroupRelations(List<String> groupTypesForChangesAtEntities) {
		if (!isCacheEnabled("_relations")) {
			return;
		}

		Thread cacher = new Thread(new Runnable() {

			@Override
			public void run() {
				doCacheGroupRelations(groupTypesForChangesAtEntities);
			}
		});
		if (DefaultIWBundle.isProductionEnvironment()) {
			cacher.run();
		} else {
			cacher.start();
		}
	}

	@Override
	public boolean isUserCacheOn() {
		String property = "cache_groups_and_users_relations";
		try {
			return getSettings().getBoolean(property, false);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting property " + property, e);
		}
		return false;
	}

	@Transactional(readOnly = true)
	private void doCacheGroupRelations(List<String> groupTypesForChanges) {
		long start = System.currentTimeMillis();
		try {
			getLogger().info("Starting to cache groups relations");

			String query = "from " + GroupRelation.class.getName() + " gr";
			if (!isUserCacheOn()) {
				query = query.concat(" where gr.relatedGroup.groupType.groupType != '").concat(UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE).concat("'");
			}

			Long count = groupDAO.getSingleResultByInlineQuery("select count(gr.groupRelationID) ".concat(query), Long.class);
			if (count == null || count <= 0) {
				return;
			}

			int columns = 12;
			String relationsQuery = "select gr.groupRelationID, gr.group.id, gr.group.name, gr.group.groupType.groupType, gr.relatedGroup.id, gr.relatedGroup.name, gr.relatedGroup.groupType.groupType, gr.status, "
									.concat("gr.initiationDate, gr.terminationDate, gr.initiationModificationDate, gr.terminationModificationDate ").concat(query);

			Map<Integer, Integer> aliasesCache = getAliases();
			Map<Integer, Integer> groupsAliasesCache = getGroupsAliases();

			int index = 0;
			int step = 100000;
			while (index < count) {
				List<Object[]> data = groupDAO.getResultListByInlineQuery(relationsQuery, Object[].class, index, step, null);
				if (ListUtil.isEmpty(data)) {
					continue;
				}

				Map<Integer, Integer> aliasesIds = new HashMap<>();
				for (Object[] relationData: data) {
					if (ArrayUtil.isEmpty(relationData) || relationData.length != columns) {
						continue;
					}

					Integer relationId = ((Number) relationData[0]).intValue();

					Integer groupId = ((Number) relationData[1]).intValue();
					String groupName = (String) relationData[2];
					String groupType = (String) relationData[3];

					Integer relatedGroupId = ((Number) relationData[4]).intValue();
					String relatedGroupName = (String) relationData[5];
					String relatedGroupType = (String) relationData[6];
					if (GroupTypeConstants.GROUP_TYPE_ALIAS.equals(relatedGroupType)) {
						aliasesIds.put(relatedGroupId, groupId);
					}

					String status = (String) relationData[7];
					boolean active = isActive(status);

					Timestamp initiationDate = (Timestamp) relationData[8];
					Timestamp terminationDate = (Timestamp) relationData[9];
					Timestamp initiationModificationDate = (Timestamp) relationData[10];
					Timestamp terminationModificationDate = (Timestamp) relationData[11];
					addRelations(
							new GroupRelationBean(
									relationId,
									groupId,
									groupName,
									groupType,
									relatedGroupId,
									relatedGroupName,
									relatedGroupType,
									active,
									initiationDate,
									terminationDate,
									initiationModificationDate,
									terminationModificationDate
							),
							new CachedGroup(relationId, groupId, groupName, groupType, true),
							new CachedGroup(relationId, relatedGroupId, relatedGroupName, relatedGroupType, active)
					);
				}

				if (!MapUtil.isEmpty(aliasesIds)) {
					int aliasColumns = 4;
					List<Object[]> aliases = groupDAO.getResultListByInlineQuery(
							"select g.id, g.alias.id, g.alias.name, g.alias.groupType.groupType from " + Group.class.getName() + " g where g.id in :aliasesIds",
							Object[].class,
							new Param("aliasesIds", new ArrayList<>(aliasesIds.keySet()))
					);
					if (!ListUtil.isEmpty(aliases)) {
						for (Object[] alias: aliases) {
							if (ArrayUtil.isEmpty(alias) || alias.length != aliasColumns) {
								continue;
							}

							Integer aliasId = ((Number) alias[0]).intValue();
							Integer parentId = aliasesIds.get(aliasId);

							CachedGroup parent = parentId == null ? null : groups.get(parentId);

							Integer relatedGroupId = ((Number) alias[1]).intValue();
							String relatedGroupName = (String) alias[2];
							String relatedGroupType = (String) alias[3];

							addRelations(
									null,
									parent,
									new CachedGroup(parent == null ? null : parent.getGroupRelationId(), relatedGroupId, relatedGroupName, relatedGroupType, true)
							);

							if (aliasId != null && relatedGroupId != null) {
								aliasesCache.put(aliasId, relatedGroupId);
								groupsAliasesCache.put(relatedGroupId, aliasId);
							}
						}
					}
				}

				getLogger().info("Cached group relations from " + index + " to " + (index + step) + " from " + count);
				index += step;
			}

			getLogger().info("Starting to check all group relations...");
			doCheckRelations();
			getLogger().info("Finished checking all group relations");
			getLogger().info("Starting to load group relation changes for groups with type " + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE + "...");
			doLoadGroupRelationChanges(UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE, groupTypesForChanges);
			getLogger().info("Finished loading group relation changes for groups with type " + UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching group relations", e);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".doCacheGroupRelations");
		}
	}

	private void addGroupRelation(Map<Integer, Set<Integer>> relationships, Integer groupId, Integer relatedGroupId) {
		if (relationships == null || groupId == null || relatedGroupId == null) {
			return;
		}

		Set<Integer> ids = relationships.get(groupId);
		if (ids == null) {
			ids = new HashSet<>();
			relationships.put(groupId, ids);
		}
		ids.add(relatedGroupId);
	}

	private void addRelations(GroupRelationBean relation, CachedGroup parent, CachedGroup child) {
		if (relation != null) {
			getRelations().put(relation.getId(), relation);

			if (parent != null && child != null) {
				addGroupRelation(getChildrenOfGroups(), parent.getId(), child.getId());
				addGroupRelation(getParentsOfGroups(), child.getId(), parent.getId());
			}

			if (child != null) {
				String relatedGroupType = child.getType();
				if (!StringUtil.isEmpty(relatedGroupType)) {
					Map<String, Set<Integer>> relatedGroupTypeRelations = getRelatedGroupTypeRelationsIds();

					Set<Integer> relationsForRelatedGroupType = relatedGroupTypeRelations.get(relatedGroupType);
					if (relationsForRelatedGroupType == null) {
						relationsForRelatedGroupType = new HashSet<>();
						relatedGroupTypeRelations.put(relatedGroupType, relationsForRelatedGroupType);
					}
					relationsForRelatedGroupType.add(relation.getId());
				}
			}
		}

		addRelation(null, parent);
		addRelation(relation, child);

		if (parent != null && child != null) {
			parent.addChild(child);
			child.addParent(parent);
		}

		doCheckRelations(relation);
	}

	private boolean isLatestVersionOfRelation(GroupRelationBean relation, CachedGroup cachedGroup) {
		if (relation == null || cachedGroup == null) {
			return true;
		}

		if (UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE.equals(cachedGroup.getType())) {
			return true;
		}

		CachedGroup existingGroup = groups.get(cachedGroup.getId());
		if (existingGroup == null || existingGroup.getGroupRelationId() == null) {
			return true;
		}

		GroupRelationBean existingRelation = getRelations().get(existingGroup.getGroupRelationId());
		if (existingRelation == null) {
			return true;
		}

		Date latestDateForExistingRelation = getLatestDate(existingRelation);
		if (latestDateForExistingRelation == null) {
			return true;
		}

		Date latestDateForRelation = getLatestDate(relation);
		if (latestDateForRelation == null) {
			return true;
		}

		long latestTimesampForExistingRelation = latestDateForExistingRelation.getTime();
		long latestTimesampForRelation = latestDateForRelation.getTime();

		//	Timestamps are the same, relying on the IDs
		if (latestTimesampForExistingRelation == latestTimesampForRelation) {
			return relation.getId() > existingRelation.getId();
		}

		return latestTimesampForRelation > latestTimesampForExistingRelation;
	}

	private void addRelation(GroupRelationBean relation, CachedGroup cachedGroup) {
		if (cachedGroup == null) {
			return;
		}

		Integer id = cachedGroup.getId();
		if (id == null) {
			return;
		}

		if (!isLatestVersionOfRelation(relation, cachedGroup)) {
			if (canDebug(cachedGroup.getType())) {
				getLogger().warning("Already exists cached group (" + groups.get(id) + "), not replacing it with cached group: " + cachedGroup + " (relation ID: " + relation.getId() + ")");
			}
			return;
		}

		if (groups.get(id) != null && canDebug(groups.get(id).getType())) {
			getLogger().info("Replacing group " + groups.get(id) + " with group " + cachedGroup);
		}
		groups.put(id, cachedGroup);

		String type = cachedGroup.getType();
		if (!StringUtil.isEmpty(type)) {
			Map<String, Map<Integer, Boolean>> types = getTypes();

			Map<Integer, Boolean> idsByType = types.get(type);
			if (idsByType == null) {
				idsByType = new HashMap<>();
				types.put(type, idsByType);
			}
			idsByType.put(id, Boolean.TRUE);
		}
	}

	private void doCheckRelations() {
		Map<Integer, GroupRelationBean> relations = getRelations();

		List<Integer> relationsIds = new ArrayList<>(relations.keySet());
		for (Integer relationId: relationsIds) {
			try {
				doCheckRelations(relationId);

				GroupRelationBean relation = relations.get(relationId);
				if (relation != null) {
					CachedGroup parentGroup = groups.get(relation.getGroupId());
					getParents(parentGroup);
					getChildren(parentGroup);
					CachedGroup relatedGroup = groups.get(relation.getRelatedGroupId());
					getParents(relatedGroup);
					getChildren(relatedGroup);
				}
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error checking relation with ID: " + relationId, e);
			}
		}
		List<Integer> ids = new ArrayList<>(groups.keySet());
		for (Integer id: ids) {
			CachedGroup group = groups.get(id);
			getParents(group);
			getChildren(group);
		}
	}

	private void doCheckRelations(Integer relationId) {
		if (relationId == null) {
			return;
		}

		GroupRelationBean relation = getRelations().get(relationId);
		doCheckRelations(relation);
	}

	private void doCheckRelations(GroupRelationBean relation) {
		if (relation == null) {
			return;
		}

		if (relation.isActive()) {
			CachedGroup parent = groups.get(relation.getGroupId());
			if (parent == null) {
				parent = new CachedGroup(relation.getId(), relation.getGroupId(), relation.getGroupName(), relation.getGroupType(), true);
			} else {
				parent.setActive(true);
			}
			groups.put(parent.getId(), parent);

			CachedGroup child = groups.get(relation.getRelatedGroupId());
			if (child == null) {
				child = new CachedGroup(relation.getId(), relation.getRelatedGroupId(), relation.getRelatedGroupName(), relation.getRelatedGroupType(), true);
				if (isLatestVersionOfRelation(relation, child)) {
					groups.put(child.getId(), child);
				}
			} else if (isLatestVersionOfRelation(relation, child)) {
				child.setActive(true);
				groups.put(child.getId(), child);
			}

			if (parent != null && child != null) {
				parent.addChild(child);
				child.addParent(parent);
			}
		} else {
			Integer relatedGroupId = relation.getRelatedGroupId();
			if (relatedGroupId != null) {
				CachedGroup existingCachedGroup = groups.get(relatedGroupId);
				if (existingCachedGroup != null) {
					CachedGroup tmp = new CachedGroup(relation.getId(), relation.getRelatedGroupId(), relation.getRelatedGroupName(), relation.getRelatedGroupType(), false);
					if (isLatestVersionOfRelation(relation, tmp)) {
						if (canDebug(existingCachedGroup.getType())) {
							getLogger().info("Setting group " + existingCachedGroup + " inactive because group " + tmp + " is the lates version");
						}
						existingCachedGroup.setActive(false);
					} else if (canDebug(existingCachedGroup.getType())) {
						getLogger().info("Not setting inactive existing cached group " + existingCachedGroup + " because group " + tmp + " is not latest version");
					}
				}
			}
		}
	}

	private boolean canDebug(String type) {
		if (ListUtil.isEmpty(typesForDebug) || StringUtil.isEmpty(type)) {
			return false;
		}

		return typesForDebug.contains(type);
	}

	private boolean isCacheEnabled(String option) {
		String key = "groups_cacher.cache_enabled";
		if (option != null) {
			key = key.concat(option);
		}
		return getApplication().getSettings().getBoolean(key, option == null ? true: false);
	}

	@Override
	public List<Group> findActiveGroupsByType(String type) {
		if (StringUtil.isEmpty(type)) {
			return null;
		}

		List<Integer> ids = findActiveCachedGroupsIdsByTypes(Arrays.asList(type));
		if (ids == null) {
			return null;
		}

		return ids.size() == 0 ? Collections.emptyList() : groupDAO.findGroups(ids);
	}

	@Override
	public List<Integer> findGroupsIdsByTypes(List<String> types) {
		if (ListUtil.isEmpty(types)) {
			return null;
		}

		return findActiveCachedGroupsIdsByTypes(types);
	}

	private List<Integer> findActiveCachedGroupsIdsByTypes(List<String> types) {
		if (ListUtil.isEmpty(types)) {
			return null;
		}

		long start = System.currentTimeMillis();
		try {
			Map<String, Map<Integer, Boolean>> typesCache = getTypes();

			Set<Integer> results = new HashSet<>();
			for (String type: types) {
				Map<Integer, Boolean> idsByType = typesCache.get(type);
				if (MapUtil.isEmpty(idsByType)) {
					continue;
				}

				List<Integer> ids = new ArrayList<>(idsByType.keySet());
				for (Integer id: ids) {
					CachedGroup cachedGroup = groups.get(id);
					if (cachedGroup == null || !cachedGroup.isActive()) {
						continue;
					}

					results.add(id);
				}
			}

			return new ArrayList<>(results);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".findActiveCachedGroupsIdsByTypes Types: " + types);
		}
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> havingTypes, List<String> notHavingTypes, Integer from, Integer to) {
		return getChildGroupsIds(parentGroupsIds, havingTypes, notHavingTypes, null, from, to);
	}

	@Override
	public <K extends Serializable, CK extends Serializable, V extends Serializable> Map<K, Map<CK, List<V>>> getCache(Integer size, Long timeToLiveInSeconds, boolean resetable) {
		return getCache("EPLATFORM.childGroupsCache", timeToLiveInSeconds, timeToLiveInSeconds, size, resetable);
	}

	private Lock lockForGroupsTree = new ReentrantLock();
	@Override
	public <V extends Serializable> Map<String, V> getGroupsTreeCache(boolean checkIfEmpty) {
		lockForGroupsTree.lock();
		Map<String, V> cache = null;
		try {
			cache = getCache(GROUP_TREE_CACHE_NAME, 2592000, 2592000, Integer.MAX_VALUE, true);
			if (checkIfEmpty && MapUtil.isEmpty(cache)) {
				ELUtil.getInstance().publishEvent(new GroupRelationChangedEvent(EventType.EMPTY));
			}
			return cache;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting cache groups tree cache", e);
			return Collections.emptyMap();
		} finally {
			lockForGroupsTree.unlock();
		}
	}

	private Lock lockForUsersGroups = new ReentrantLock();
	@Override
	public Map<String, List<Integer>> getUsersGroupsCache(boolean checkIfEmpty, Integer userId) {
		lockForUsersGroups.lock();
		Map<String, List<Integer>> cache = null;
		try {
			cache = getCache("EPLATFORM_USERS_GROUPS_CACHE", 2592000, 2592000, Integer.MAX_VALUE, false);

			if (userId != null && (MapUtil.isEmpty(cache) || !cache.containsKey(userId))) {
				ELUtil.getInstance().publishEvent(new GroupRelationChangedEvent(EventType.USER_UPDATE, false, userId));
			}

			if (checkIfEmpty && MapUtil.isEmpty(cache)) {
				ELUtil.getInstance().publishEvent(new GroupRelationChangedEvent(EventType.EMPTY));
			}
			return cache;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups tree cache", e);
			return Collections.emptyMap();
		} finally {
			lockForUsersGroups.unlock();
		}
	}

	@Override
	public Map<String, List<com.idega.user.data.bean.Group>> getUserGroupsCache() {
		Map<String, List<com.idega.user.data.bean.Group>> cache = null;
		try {
			cache = getCache("EPLATFORM_USER_GROUPS_CACHE", 2592000, 2592000, Integer.MAX_VALUE, true);
			return cache;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting user groups cache", e);
			return Collections.emptyMap();
		}
	}

	private Map<String, Boolean> progress = new HashMap<>();

	@Override
	public void setCacheInProgress(String name, Boolean inProgress) {
		inProgress = inProgress == null ? Boolean.FALSE : inProgress;
		progress.put(name, inProgress);
	}

	@Override
	public boolean isCacheInProgress(String name) {
		if (StringUtil.isEmpty(name)) {
			return Boolean.FALSE;
		}

		Boolean inProgress = progress.get(name);
		return inProgress == null ? Boolean.FALSE : inProgress;
	}

	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentIds, List<String> childGroupsTypes) {
		return getChildGroupsIds(parentIds, childGroupsTypes, null);
	}

	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentIds, List<String> childGroupsTypes, Integer levels) {
		if (ListUtil.isEmpty(parentIds)) {
			return null;
		}

		long start = System.currentTimeMillis();
		try {
			return getChildren(parentIds, childGroupsTypes, levels);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".getChildGroupsIds Parents IDs: "
					+ parentIds + ", child groups types: " + childGroupsTypes + ", levels: " + levels);
		}
	}

	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentIds, List<String> childGroupsTypes, boolean loadAliases) {
		return getChildGroupsIds(parentIds, childGroupsTypes);
	}

	@Override
	public List<Integer> getGroupsIdsByIdsAndTypes(List<Integer> ids, List<String> childGroupsTypes) {
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		long start = System.currentTimeMillis();
		try {
			Map<Integer, List<Integer>> allResults = getChildren(ids, childGroupsTypes, null);
			if (MapUtil.isEmpty(allResults)) {
				allResults = getParents(ids, childGroupsTypes, null);
			}
			if (allResults == null) {
				return null;
			}

			Set<Integer> results = new HashSet<>();
			for (List<Integer> tmpResults: allResults.values()) {
				if (ListUtil.isEmpty(tmpResults)) {
					continue;
				}

				results.addAll(tmpResults);
			}

			return new ArrayList<>(results);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".getGroupsIdsByIdsAndTypes Parent IDs: "
					+ ids + ", child groups types: " + childGroupsTypes);
		}
	}

	private Map<Integer, Boolean> getRealGroupsIds(List<Integer> ids) {
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		Map<Integer, Integer> aliasesCache = getAliases();

		Map<Integer, Boolean> results = new HashMap<>();
		for (Integer id: ids) {
			if (id == null) {
				continue;
			}

			Integer groupId = aliasesCache.get(id);
			if (groupId == null) {
				if (groups.get(id) != null) {
					results.put(id, Boolean.TRUE);
				}
			} else {
				results.put(groupId, Boolean.TRUE);
			}
		}

		return results;
	}

	private Map<Integer, List<Integer>> getChildren(List<Integer> parentsIds, List<String> childGroupsTypes, Integer maxLevels) {
		return getRelatedIds(parentsIds, childGroupsTypes, maxLevels, getParentsOfGroups(), "children");
	}
	private Map<Integer, List<Integer>> getParents(List<Integer> ids, List<String> groupsTypes, Integer maxLevels) {
		return getRelatedIds(ids, groupsTypes, maxLevels, getChildrenOfGroups(), "parent");
	}
	private Map<Integer, List<Integer>> getRelatedIds(
			List<Integer> ids,
			List<String> groupsTypes,
			Integer maxLevels,
			Map<Integer, Set<Integer>> groupsRelations,
			String relationName
	) {
		if (ListUtil.isEmpty(ids) || ListUtil.isEmpty(groupsTypes) || MapUtil.isEmpty(groupsRelations)) {
			return null;
		}

		//	Making sure there are active groups by provided types
		List<Integer> activeGroupsIdsByTypes = findActiveCachedGroupsIdsByTypes(groupsTypes);
		if (ListUtil.isEmpty(activeGroupsIdsByTypes)) {
			getLogger().warning("No groups found by types: " + groupsTypes);
			return null;
		}

		//	Making sure aliases are not used
		Map<Integer, Boolean> realGroupsIds = getRealGroupsIds(ids);
		if (MapUtil.isEmpty(realGroupsIds)) {
			getLogger().warning("No real groups IDs found for " + ids);
			return null;
		}

		Map<Integer, Boolean> results = new HashMap<>();
		for (Integer activeGroupIdByType: activeGroupsIdsByTypes) {
			if (realGroupsIds.get(activeGroupIdByType) != null) {
				results.put(activeGroupIdByType, Boolean.TRUE);
				continue;
			}

			//	Checking if group by requested type has relations
			Set<Integer> relatedGroupsIdsForGroupWithRequestedType = groupsRelations.get(activeGroupIdByType);
			if (ListUtil.isEmpty(relatedGroupsIdsForGroupWithRequestedType)) {
				continue;
			}

			//	Checking if provided groups IDs are in relations with group by requested type
			relatedGroupsIdsForGroupWithRequestedType = new HashSet<>(relatedGroupsIdsForGroupWithRequestedType);
			relatedGroupsIdsForGroupWithRequestedType.retainAll(realGroupsIds.keySet());
			if (!ListUtil.isEmpty(relatedGroupsIdsForGroupWithRequestedType)) {
				results.put(activeGroupIdByType, Boolean.TRUE);
			}

			if (results.get(activeGroupIdByType) == null && ids.contains(activeGroupIdByType)) {
				results.put(activeGroupIdByType, Boolean.TRUE);
			}
		}

		Map<Integer, List<Integer>> allResults = new HashMap<>();

		if (MapUtil.isEmpty(results) && relationName.equals("children") && !groupsTypes.contains(GroupTypeConstants.GROUP_TYPE_ALIAS)) {
			for (Integer id: ids) {
				CachedGroup group = groups.get(id);
				List<Integer> allChildrenForGroup = getAllChildrenIds(group, groupsTypes);
				if (!ListUtil.isEmpty(allChildrenForGroup)) {
					for (Integer childGroupIdForGroup: allChildrenForGroup) {
						results.put(childGroupIdForGroup, Boolean.TRUE);
					}
				}
			}
		}

		if (MapUtil.isEmpty(results)) {
			getLogger().warning("Failed to find " + relationName + " groups IDs for group types " + groupsTypes + " and narrowed by groups IDs " + realGroupsIds.keySet());
		} else {
			allResults.put(1, new ArrayList<>(results.keySet()));
		}
		return allResults;
	}

	private List<Integer> getAllParentsIds(CachedGroup group) {
		if (group == null) {
			return null;
		}

		Set<Integer> allParents = getParentsOfGroups().get(group.getId());
		if (ListUtil.isEmpty(allParents)) {
			getLogger().warning("No parent groups found for " + group);
			return null;
		}

		return new ArrayList<>(allParents);
	}

	private Map<Integer, List<Integer>> getAllRelatedGroupsIds(List<Integer> groupsIds, List<String> groupsTypes, Integer levels, boolean parent) {
		long start = System.currentTimeMillis();
		try {
			Map<Integer, List<Integer>> allRelations = new HashMap<>();
			for (Integer groupId: groupsIds) {
				CachedGroup group = groups.get(groupId);
				if (group == null) {
					continue;
				}

				Map<Integer, List<Integer>> groupRelations = parent ? getParents(group, groupsTypes, levels) : getChildren(group, groupsTypes, levels);
				if (MapUtil.isEmpty(groupRelations)) {
					continue;
				}

				MapUtil.append(allRelations, groupRelations);
			}

			return allRelations;
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".getAllRelatedGroupsIds<MAP> for IDs " + groupsIds + " with types: " + groupsTypes);
		}
	}
	private List<Integer> getAllRelatedGroupsIds(List<Integer> groupsIds, List<String> groupsTypes, boolean parent, Integer levels) {
		if (ListUtil.isEmpty(groupsIds) || ListUtil.isEmpty(groupsTypes)) {
			return null;
		}

		Map<Integer, List<Integer>> allRelations = getAllRelatedGroupsIds(groupsIds, groupsTypes, levels, parent);
		if (MapUtil.isEmpty(allRelations)) {
			return null;
		}

		Set<Integer> results = new HashSet<>();
		for (List<Integer> ids: allRelations.values()) {
			if (ListUtil.isEmpty(ids)) {
				continue;
			}

			results.addAll(ids);
		}

		if (ListUtil.isEmpty(results)) {
			return null;
		}

		return new ArrayList<>(results);
	}

	private List<Integer> getAllChildrenIds(CachedGroup group, List<String> childGroupsTypes) {
		if (group == null) {
			return null;
		}

		return getAllRelatedGroupsIds(Arrays.asList(group.getId()), childGroupsTypes, false, null);
	}

	private Map<Integer, List<Integer>> getChildren(CachedGroup group, List<String> childGroupsTypes, Integer levels) {
		return getRelatedGroups(group, childGroupsTypes, levels, null, null, null, false);
	}
	private Map<Integer, List<Integer>> getParents(CachedGroup group, List<String> parentGroupsTypes, Integer levels) {
		return getRelatedGroups(group, parentGroupsTypes, levels, null, null, null, true);
	}
	private Map<Integer, List<Integer>> getRelatedGroups(
			CachedGroup group,
			List<String> groupsTypes,
			Integer maxLevels,
			Integer currentLevel,
			Map<Integer, List<Integer>> results,
			Set<Integer> checkedIds,
			boolean parents
	) {
		if (results == null) {
			results = new HashMap<>();
		}

		if (group == null || !group.isActive()) {
			return results;
		}
		if (ListUtil.isEmpty(groupsTypes)) {
			return results;
		}

		if (currentLevel == null) {
			currentLevel = 1;
		} else {
			currentLevel++;
		}
		if (maxLevels != null && maxLevels > currentLevel) {
			return results;
		}

		if (checkedIds == null) {
			checkedIds = new HashSet<>();
		}
		if (checkedIds.contains(group.getId())) {
			return results;
		}
		checkedIds.add(group.getId());

		Map<Integer, CachedGroup> relatedGroups = parents ? group.getParents() : group.getChildren();
		if (MapUtil.isEmpty(relatedGroups)) {
			relatedGroups = parents ? getParents(group) : getChildren(group);
		}
		if (MapUtil.isEmpty(relatedGroups)) {
			return results;
		}

		List<Integer> levelResults = results.get(currentLevel);
		if (levelResults == null) {
			levelResults = new ArrayList<>();
			results.put(currentLevel, levelResults);
		}

		Map<Integer, Integer> aliasesCache = getAliases();
		for (CachedGroup relatedGroup: relatedGroups.values()) {
			if (!relatedGroup.isActive()) {
				continue;
			}

			if (groupsTypes.contains(relatedGroup.getType())) {
				levelResults.add(relatedGroup.getId());
			} else if (GroupTypeConstants.GROUP_TYPE_ALIAS.equals(relatedGroup.getType())) {
				Integer realGroupId = aliasesCache.get(relatedGroup.getId());
				if (realGroupId == null) {
					continue;
				}

				CachedGroup realGroup = groups.get(realGroupId);
				if (realGroup == null || !realGroup.isActive()) {
					continue;
				}

				if (groupsTypes.contains(realGroup.getType())) {
					checkedIds.add(realGroupId);
					levelResults.add(realGroupId);
				}
				relatedGroup = realGroup;
			}

			getRelatedGroups(relatedGroup, groupsTypes, maxLevels, currentLevel, results, checkedIds, parents);
		}

		return results;
	}

	@Override
	public <T extends Serializable> List<T> filterGroupsByIdsAndTypes(List<Integer> parentIds, List<String> childGroupsTypes, Class<T> resultType) {
		String name = resultType.getName();
		boolean id = Integer.class.getName().equals(name);
		boolean entities = Group.class.getName().equals(name);
		if (!id && !entities) {
			getLogger().warning("Unknown result type: " + name);
			return null;
		}

		List<Integer> ids = getGroupsIdsByIdsAndTypes(parentIds, childGroupsTypes);
		if (ids == null) {
			return null;
		}

		if (id) {
			@SuppressWarnings("unchecked")
			List<T> results = (List<T>) ids;
			return results;
		} else if (entities) {
			@SuppressWarnings("unchecked")
			List<T> results = ids.size() == 0 ? Collections.emptyList() : (List<T>) groupDAO.findGroups(ids);
			return results;
		}

		return null;
	}

	private Map<Integer, Map<String, Integer>> cachedGroupRelationChanges = new HashMap<>();
	@Override
	public Map<Integer, Map<String, Integer>> getCachedGroupRelationChanges() {
		return cachedGroupRelationChanges;
	}
	private Map<Integer, Map<String, Long>> latestChangesInEntities = new HashMap<>();

	private void doLoadGroupRelationChanges(String relatedGroupType, List<String> entityTypes) {
		if (StringUtil.isEmpty(relatedGroupType)) {
			getLogger().warning("Related group type is not provided");
			return;
		}
		if (ListUtil.isEmpty(entityTypes)) {
			return;
		}

		Set<Integer> ids = getRelatedGroupTypeRelationsIds().get(relatedGroupType);
		if (ListUtil.isEmpty(ids)) {
			getLogger().warning("There are no relations for group type: " + relatedGroupType);
			return;
		} else {
			getLogger().info("Total relations to load: " + ids.size());
		}

		Map<Integer, GroupRelationBean> relations = getRelations();

		List<Integer> relationsIds = new ArrayList<>(ids);
		for (Integer id: relationsIds) {
			GroupRelationBean relation = relations.get(id);
			if (relation != null && relatedGroupType.equals(relation.getRelatedGroupType())) {
				addGroupRelationBean(cachedGroupRelationChanges, relation, entityTypes);
			}
		}
	}

	@Override
	public List<GroupRelationBean> getGroupRelationsByRelatedGroupTypeAndGroupTypes(
			String relatedGroupType,
			List<String> parentGroupTypes,
			List<Integer> parentGroupsIds,
			List<String> entityTypes
	) {
		if (MapUtil.isEmpty(cachedGroupRelationChanges)) {
			getLogger().warning("There is nothing cached");
			return null;
		}

		List<Integer> entitiesIds = new ArrayList<>(cachedGroupRelationChanges.keySet());

		Map<Integer, GroupRelationBean> relations = getRelations();

		boolean checkTypes = !ListUtil.isEmpty(parentGroupTypes);
		boolean checkIds = !ListUtil.isEmpty(parentGroupsIds);

		Map<Integer, GroupRelationBean> results = new HashMap<>();
		for (Integer entityId: entitiesIds) {
			if (entityId == null) {
				continue;
			}

			Map<String, Integer> changesForEntity = cachedGroupRelationChanges.get(entityId);
			if (MapUtil.isEmpty(changesForEntity)) {
				continue;
			}

			List<String> groupTypes = new ArrayList<>(changesForEntity.keySet());
			for (String groupType: groupTypes) {
				if (StringUtil.isEmpty(groupType)) {
					continue;
				}

				Integer relationId = changesForEntity.get(groupType);
				if (relationId == null) {
					continue;
				}

				GroupRelationBean relation = relations.get(relationId);
				if (relation == null) {
					continue;
				}

				if (!checkTypes && !checkIds) {
					results.put(entityId, relation);
				} else {
					if (checkIds && checkTypes) {
						if (parentGroupsIds.contains(relation.getGroupId()) && parentGroupTypes.contains(relation.getGroupType())) {
							results.put(entityId, relation);
						}
					} else if (checkIds) {
						if (parentGroupsIds.contains(relation.getGroupId())) {
							results.put(entityId, relation);
						}
					} else if (checkTypes) {
						if (parentGroupTypes.contains(relation.getGroupType())) {
							results.put(entityId, relation);
						}
					}
				}
			}
		}

		if (MapUtil.isEmpty(results)) {
			getLogger().warning("No results found in cached data for related group type: " + relatedGroupType + ", entity types: " + entityTypes + ", parent group types: " + parentGroupTypes +
					", parent groups IDs: " + parentGroupsIds + ". Cached data: " + cachedGroupRelationChanges);
			return null;
		}

		List<GroupRelationBean> data = new ArrayList<>(results.values());
		getLogger().info("Found " + data.size() + " results in cached data for related group type: " + relatedGroupType + ", entity types: " + entityTypes + ", parent group types: " + parentGroupTypes +
					", parent groups IDs: " + parentGroupsIds);
		return data;
	}

	private Date getLatestDate(GroupRelationBean relation) {
		if (relation == null) {
			return null;
		}

		List<Date> dates = new ArrayList<>();
		Date initiationDate = relation.getInitiationDate();
		if (initiationDate != null) {
			dates.add(initiationDate);
		}
		Date initiationModificationDate = relation.getInitiationModificationDate();
		if (initiationModificationDate != null) {
			dates.add(initiationModificationDate);
		}
		Date terminationDate = relation.getTerminationDate();
		if (terminationDate != null) {
			dates.add(terminationDate);
		}
		Date terminationModificationDate = relation.getTerminationModificationDate();
		if (terminationModificationDate != null) {
			dates.add(terminationModificationDate);
		}

		if (ListUtil.isEmpty(dates)) {
			return null;
		}

		Collections.sort(dates);
		return dates.get(dates.size() - 1);
	}

	private void addGroupRelationBean(GroupRelationBean relation, List<String> entityTypes) {
		if (relation == null || !UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE.equals(relation.getRelatedGroupType())) {
			return;
		}
		if (ListUtil.isEmpty(entityTypes)) {
			return;
		}

		addGroupRelationBean(cachedGroupRelationChanges, relation, entityTypes);
	}

	private void addGroupRelationBean(Map<Integer, Map<String, Integer>> entitiesRelations, GroupRelationBean relation, List<String> entityTypes) {
		if (relation == null) {
			return;
		}
		if (ListUtil.isEmpty(entityTypes)) {
			return;
		}

		CachedGroup parentEntity = null;
		if (relation.getParentEntityId() == null) {
			CachedGroup parentGroup = groups.get(relation.getGroupId());
			if (parentGroup != null && entityTypes.contains(parentGroup.getType())) {
				parentEntity = parentGroup;
			}
			if (parentEntity == null) {
				CachedGroup relatedGroup = groups.get(relation.getRelatedGroupId());
				if (relatedGroup != null && entityTypes.contains(relatedGroup.getType())) {
					parentEntity = relatedGroup;
				}
			}
			if (parentEntity == null) {
				parentEntity = getParentGroupByType(relation.getGroupId(), entityTypes);
			}
		} else {
			parentEntity = groups.get(relation.getParentEntityId());
		}
		if (parentEntity == null) {
			return;
		}

		Date latestDate = getLatestDate(relation);
		if (latestDate == null) {
			return;
		}
		Long latestDateTimestamp = latestDate.getTime();

		Integer entityId = parentEntity.getId();
		String parentGroupType = relation.getGroupType();

		Map<String, Long> knownChangesInEntity = latestChangesInEntities.get(entityId);
		if (knownChangesInEntity == null) {
			knownChangesInEntity = new HashMap<>();
			latestChangesInEntities.put(entityId, knownChangesInEntity);
		}
		Long knownChangeInEntity = knownChangesInEntity.get(parentGroupType);
		if (knownChangeInEntity == null || knownChangeInEntity < latestDateTimestamp) {
			Map<String, Integer> changesByTypeForEntity = entitiesRelations.get(entityId);
			if (changesByTypeForEntity == null) {
				changesByTypeForEntity = new HashMap<>();
				entitiesRelations.put(entityId, changesByTypeForEntity);
			}
			changesByTypeForEntity.put(parentGroupType, relation.getId());

			knownChangesInEntity.put(parentGroupType, latestDateTimestamp);

			relation.setParentEntityId(entityId);
			relation.setParentEntityType(parentEntity.getType());
		}
	}

	private Map<Integer, CachedGroup> getChildren(CachedGroup group) {
		if (group == null) {
			return null;
		}

		Map<Integer, CachedGroup> children = group.getChildren();
		if (!MapUtil.isEmpty(children)) {
			return children;
		}

		Set<Integer> groupsIdsForChildrenGroups = getChildrenOfGroups().get(group.getId());
		if (ListUtil.isEmpty(groupsIdsForChildrenGroups)) {
			return null;
		}

		for (Integer id: groupsIdsForChildrenGroups) {
			CachedGroup child = groups.get(id);
			if (child != null) {
				group.addChild(child);
			}
		}

		return group.getChildren();
	}

	private Map<Integer, CachedGroup> getParents(CachedGroup group) {
		if (group == null) {
			return null;
		}

		Map<Integer, CachedGroup> parents = group.getParents();
		if (!MapUtil.isEmpty(parents)) {
			return parents;
		}

		Set<Integer> groupsIdsForParentGroups = getParentsOfGroups().get(group.getId());
		if (ListUtil.isEmpty(groupsIdsForParentGroups)) {
			return null;
		}

		for (Integer id: groupsIdsForParentGroups) {
			CachedGroup parent = groups.get(id);
			if (parent != null) {
				group.addParent(parent);
			}
		}

		return group.getParents();
	}

	@Override
	public Integer getFirstAncestorGroupIdOfType(Integer groupId, List<String> groupTypes) {
		return getFirstAncestorGroupIdOfType(groupId, groupTypes, false);
	}

	@Override
	public Integer getFirstAncestorGroupIdOfType(Integer groupId, List<String> groupTypes, boolean selectPassive) {
		CachedGroup parent = getFirstParentGroupIdOfType(groupId, groupTypes, !selectPassive);
		return parent == null ? null : parent.getId();
	}

	@Override
	public List<Integer> getFirstAncestorGroupIdsOfType(List<Integer> groupsIds, List<String> groupTypes, boolean selectPassive) {
		List<CachedGroup> parentGroups = getFirstParentGroupsOfType(groupsIds, groupTypes, !selectPassive);
		if (ListUtil.isEmpty(parentGroups)) {
			return null;
		}

		List<Integer> ids = new ArrayList<>();
		for (CachedGroup parentGroup: parentGroups) {
			ids.add(parentGroup.getId());
		}
		return ids;
	}

	private List<CachedGroup> getFirstParentGroupsOfType(List<Integer> groupsIds, List<String> groupTypes, boolean checkIfActive) {
		if (ListUtil.isEmpty(groupsIds)) {
			return null;
		}

		Set<CachedGroup> results = new HashSet<>();
		for (Integer groupId: groupsIds) {
			CachedGroup parentGroup = getFirstParentGroupIdOfType(groupId, groupTypes, checkIfActive);
			if (parentGroup != null) {
				results.add(parentGroup);
			}
		}

		return ListUtil.isEmpty(results) ? null : new ArrayList<>(results);
	}

	private CachedGroup getFirstParentGroupIdOfType(Integer groupId, List<String> groupTypes, boolean checkIfActive) {
		if (groupId == null || ListUtil.isEmpty(groupTypes)) {
			return null;
		}

		long start = System.currentTimeMillis();
		try {
			CachedGroup group = groups.get(groupId);
			if (group == null) {
				return null;
			}
			if (groupTypes.contains(group.getType())) {
				return group;
			}

			return getFirstAncestorGroupIdOfType(group.getParents(), new HashMap<>(), groupTypes, null, 1, checkIfActive);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".getFirstParentGroupIdOfType group ID:" + groupId);
		}
	}

	private CachedGroup getFirstAncestorGroupIdOfType(
			Map<Integer, CachedGroup> parents,
			Map<Integer, Boolean> checkedGroups,
			List<String> groupTypes,
			Integer maxLevels,
			Integer currentLevel,
			boolean checkIfActive
	) {
		if (MapUtil.isEmpty(parents)) {
			return null;
		}
		if (maxLevels != null && maxLevels > currentLevel) {
			return null;
		}

		for (Integer id: parents.keySet()) {
			if (id == null) {
				continue;
			}
			if (checkedGroups.containsKey(id)) {
				continue;
			}

			CachedGroup parent = groups.get(id);
			if (parent == null) {
				parent = parents.get(id);
			}
			if (parent == null) {
				continue;
			}

			checkedGroups.put(parent.getId(), Boolean.TRUE);

			boolean proceed = true;
			if (checkIfActive) {
				proceed = parent.isActive();
			}
			if (!proceed) {
				continue;
			}

			if (groupTypes.contains(parent.getType())) {
				return parent;
			}

			return getFirstAncestorGroupIdOfType(parent.getParents(), checkedGroups, groupTypes, maxLevels, currentLevel + 1, checkIfActive);
		}

		return null;
	}

	private CachedGroup getParentGroupByType(Integer groupId, List<String> parentGroupTypes) {
		if (groupId == null || ListUtil.isEmpty(parentGroupTypes)) {
			return null;
		}

		CachedGroup group = groups.get(groupId);
		if (group == null) {
			return null;
		}
		if (parentGroupTypes.contains(group.getType())) {
			return group;
		}

		CachedGroup parentGroup = getFirstParentGroupIdOfType(groupId, parentGroupTypes, false);
		if (parentGroup == null) {
			List<Integer> allParents = getAllParentsIds(group);
			if (ListUtil.isEmpty(allParents)) {
				return null;
			}

			for (Iterator<Integer> idsIter = allParents.iterator(); (parentGroup == null && idsIter.hasNext());) {
				parentGroup = groups.get(idsIter.next());
				if (parentGroup != null && parentGroupTypes.contains(parentGroup.getType())) {
					return parentGroup;
				}
			}
		}

		if (parentGroup == null) {
			getLogger().warning("Did not find parent group with one of the types " + parentGroupTypes + " got group with ID: " + groupId);
		}
		return parentGroup;
	}

	@Override
	public List<Integer> getGroupsAliasesIdsFromAliasesIdsAndGroupTypes(List<Integer> aliasesIds, List<String> groupsTypes) {
		if (ListUtil.isEmpty(aliasesIds) || ListUtil.isEmpty(groupsTypes)) {
			return null;
		}

		long start = System.currentTimeMillis();
		try {
			Map<Integer, Integer> groupsAliasesCache = getGroupsAliases();

			List<Integer> groupsIds = findActiveCachedGroupsIdsByTypes(groupsTypes);
			if (ListUtil.isEmpty(groupsIds)) {
				return null;
			}

			Set<Integer> filteredAliasesIds = new HashSet<>();
			for (Integer groupId: groupsIds) {
				Integer aliasId = groupsAliasesCache.get(groupId);
				if (aliasId == null) {
					continue;
				}

				filteredAliasesIds.add(aliasId);
			}

			return ListUtil.isEmpty(filteredAliasesIds) ? null : new ArrayList<>(filteredAliasesIds);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".getGroupsAliasesIdsFromAliasesIdsAndGroupTypes" +
					" Aliases IDs: " + aliasesIds + ", groups types: " + groupsTypes);
		}
	}

	@Override
	public List<Integer> getGroupsIdsFromAliasesIds(List<Integer> aliasesIds) {
		if (ListUtil.isEmpty(aliasesIds)) {
			return null;
		}

		long start = System.currentTimeMillis();
		try {
			Map<Integer, Integer> aliasesCache = getAliases();

			Set<Integer> ids = new HashSet<>();
			for (Integer aliasId: aliasesIds) {
				Integer id = aliasesCache.get(aliasId);
				if (id == null) {
					continue;
				}

				ids.add(id);
			}

			return ListUtil.isEmpty(ids) ? null : new ArrayList<>(ids);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".getGroupsIdsFromAliasesIds Aliases IDs: " + aliasesIds);
		}
	}

	private List<String> typesForDebug = null;

	@Override
	public void setGroupTypesForDebug(List<String> types) {
		typesForDebug = types;
	}

}