package com.idega.group.cache.business.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.business.DefaultSpringBean;
import com.idega.core.persistence.Param;
import com.idega.group.cache.bean.CachedGroup;
import com.idega.group.cache.business.GroupsCacheService;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWMainApplicationStartedEvent;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.GroupRelationDAO;
import com.idega.user.dao.Property;
import com.idega.user.data.GroupTypeConstants;
import com.idega.user.data.User;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupRelation;
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
public class GroupsCacheServiceImpl extends DefaultSpringBean implements GroupsCacheService, ApplicationListener<ApplicationEvent> {

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
		return getChildGroupsIds(parentGroupsIds, childGroupTypes, null, levels, null, null);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notContainingTypes, Integer levels, Integer from, Integer to) {
		List<Integer> ids = new ArrayList<>();
		doCollectGroupsIds(null, ids, parentGroupsIds, childGroupTypes, notContainingTypes, levels, from, to, true);
		return ListUtil.isEmpty(ids) ? null : ids;
	}

	private void doCollectGroupsIds(
			Map<Integer, List<Integer>> leveledResults,
			List<Integer> plainResults,
			final List<Integer> ids,
			List<String> groupTypes,
			List<String> notContainingTypes,
			Integer levels,
			Integer from,
			Integer to,
			boolean loadChildren
	) {
		try {
			if (ListUtil.isEmpty(ids)) {
				getLogger().warning("IDs are not provided");
				return;
			}

			List<CachedGroup> filteredCachedGroups = new ArrayList<>();
			Map<Integer, CachedGroup> cache = getIdsCache();
			ids.parallelStream().forEach(id -> {
				CachedGroup cachedGroup = cache.get(id);
				if (cachedGroup != null) {
					filteredCachedGroups.add(cachedGroup);
				}
			});
			if (filteredCachedGroups.size() != ids.size()) {
				Thread cacher = new Thread(new Runnable() {

					@Override
					public void run() {
						doCache(ids);
					}

				});
				cacher.start();
				return;
			}

			final Integer level = levels == null ? Integer.MAX_VALUE : levels;
			for (CachedGroup filteredCachedGroup: filteredCachedGroups) {
				if (filteredCachedGroup == null) {
					continue;
				}

				doCollectIds(leveledResults, plainResults, loadChildren ? filteredCachedGroup.getAllChildren() : getConverted(filteredCachedGroup.getAllParents()), groupTypes, notContainingTypes, level);
			}
			if (from != null || to != null && !ListUtil.isEmpty(plainResults)) {
				if (from != null) {
				}
			}
		} finally {
			doFilterOut(leveledResults, plainResults);
		}
	}

	private void doFilterOut(Map<Integer, List<Integer>> leveledResults, List<Integer> plainResults) {
		if (leveledResults != null) {
			for (Integer level: leveledResults.keySet()) {
				if (level == null) {
					continue;
				}

				List<Integer> levelResults = leveledResults.get(level);
				if (ListUtil.isEmpty(levelResults)) {
					continue;
				}

				leveledResults.put(level, getUniqueResults(levelResults));
			}
		}
		if (plainResults != null) {
			plainResults = getUniqueResults(plainResults);
		}
	}

	private List<Integer> getUniqueResults(List<Integer> results) {
		if (ListUtil.isEmpty(results)) {
			return results;
		}

		Set<Integer> copy = new HashSet<>();
		for (Integer id: results) {
			if (id == null) {
				continue;
			}

			copy.add(id);
		}
		return new ArrayList<>(copy);
	}

	private Map<Integer, List<CachedGroup>> getConverted(Collection<CachedGroup> groups) {
		if (ListUtil.isEmpty(groups)) {
			return null;
		}

		int level = 1;
		Map<Integer, List<CachedGroup>> converted = new TreeMap<>();
		for (CachedGroup group: groups) {
			converted.put(level, Arrays.asList(group));
			level++;
		}

		return converted;
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
		List<Integer> ids = new ArrayList<>();
		doCollectGroupsIds(null, ids, groupsIds, groupTypes, null, null, null, null, false);
		return ListUtil.isEmpty(ids) ? null : ids;
	}

	@Override
	public List<Group> getParentGroups(Integer id, List<String> groupTypes) {
		List<Integer> ids = getParentGroupsIdsRecursive(Arrays.asList(id), groupTypes);
		if (ids == null) {
			return null;
		}

		return groupDAO.findGroups(ids);
	}

	@Override
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels) {
		return getChildGroups(parentGroupsIds, childGroupTypes, null, levels);
	}

	@Override
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels) {
		Map<Integer, List<Group>> results = new HashMap<>();

		Map<Integer, List<Integer>> leveledIds = new HashMap<>();
		doCollectGroupsIds(leveledIds, null, parentGroupsIds, childGroupTypes, notHavingChildGroupTypes, levels, null, null, true);
		if (MapUtil.isEmpty(leveledIds)) {
			return null;
		}

		for (Integer level: leveledIds.keySet()) {
			List<Integer> ids = leveledIds.get(level);
			if (ListUtil.isEmpty(ids)) {
				continue;
			}

			List<Group> groups = groupDAO.findGroups(ids);
			if (!ListUtil.isEmpty(groups)) {
				results.put(level, groups);
			}
		}

		return results;
	}

	private void doCollectIds(Map<Integer, List<Integer>> results, List<Integer> ids, Map<Integer, List<CachedGroup>> source, List<String> groupTypes, List<String> notHavingTypes, Integer levels) {
		if (MapUtil.isEmpty(source)) {
			getLogger().warning("Source is empty");
			return;
		}

		boolean checkTypes = !ListUtil.isEmpty(groupTypes);
		boolean checkNotHavingTypes = !ListUtil.isEmpty(notHavingTypes);
		List<Integer> allLevels = new ArrayList<>(source.keySet());
		allLevels.parallelStream().forEach(level -> {
			if (level > levels) {
				return;
			}

			List<CachedGroup> levelCachedGroups = source.get(level);
			if (!ListUtil.isEmpty(levelCachedGroups)) {
				levelCachedGroups.parallelStream().forEach(levelCachedGroup -> {
					Integer id = null;

					if (checkTypes) {
						if (groupTypes.contains(levelCachedGroup.getType())) {
							id = levelCachedGroup.getId();
						}
					}
					if (checkNotHavingTypes) {
						if (notHavingTypes.contains(levelCachedGroup.getType())) {
						} else {
							id = levelCachedGroup.getId();
						}
					}
					if (!checkTypes && !checkNotHavingTypes) {
						id = levelCachedGroup.getId();
					}

					if (id != null) {
						if (ids != null) {
							ids.add(id);
						}
						if (results != null) {
							List<Integer> levelIds = results.get(level);
							if (levelIds == null) {
								levelIds = new ArrayList<>();
								results.put(level, levelIds);
							}
							levelIds.add(id);
						}
					}
				});
			}
		});
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		/*if (event instanceof IWMainApplicationStartedEvent) {
			if (isCacheEnabled("startup")) {
				Thread cacher = new Thread(new Runnable() {

					@Override
					public void run() {
						doCacheGroups();
					}

				});
				cacher.start();
			}
		} else if (event instanceof GroupRelationChangedEvent) {
			if (isCacheEnabled(null)) {
				GroupRelationChangedEvent changed = (GroupRelationChangedEvent) event;
				switch (changed.getType()) {
				case GROUP_CHANGE:
					Integer groupRelationId = changed.getGroupRelationId();
					if (groupRelationId != null) {
						GroupRelationDAO groupRelationDAO = ELUtil.getInstance().getBean(GroupRelationDAO.class);
						GroupRelation relation = groupRelationDAO.getById(groupRelationId);
						doUpdateCacheForGroup(relation);
					}

					break;
				default:
					break;
				}
			}
		}*/
		if (event instanceof IWMainApplicationStartedEvent) {
			doStartCachingGroupRelations();
		} else if (event instanceof GroupRelationChangedEvent) {
			doUpdateGroupRelations((GroupRelationChangedEvent) event);
		}
	}

//	private void doUpdateGroupRelation(CachedGroup cachedGroup, CachedGroup parent, CachedGroup child) {
//		cachedGroup.setParentId(parentGroupId);
//		cachedGroup.setId(groupId);
//		cachedGroup.setType(relatedGroupType);
//		cachedGroup.setActive(active);
//
//		for (String type: types.keySet()) {
//			Set<Integer> idsByType = types.get(type);
//			if (!ListUtil.isEmpty(idsByType)) {
//				idsByType.remove(groupId);
//			}
//		}
//
//		Set<Integer> idsByType = types.get(relatedGroupType);
//		if (idsByType == null) {
//			idsByType = new HashSet<>();
//			types.put(relatedGroupType, idsByType);
//		}
//		idsByType.add(groupId);
//	}

	private void doUpdateGroupRelations(GroupRelationChangedEvent event) {
		try {
			if (!isCacheEnabled("_relations")) {
				return;
			}

			Integer relationId = event == null ? null : event.getGroupRelationId();
			if (relationId == null) {
				return;
			}

			GroupRelationDAO groupRelationDAO = ELUtil.getInstance().getBean(GroupRelationDAO.class);
			GroupRelation groupRelation = groupRelationDAO.getById(relationId);
			if (groupRelation == null) {
				return;
			}

			Group group = groupRelation.getGroup();
			Integer groupId = group.getID();
			String groupType = group.getType();

			Group relatedGroup = groupRelation.getRelatedGroup();
			Integer relatedGroupId = relatedGroup.getID();
			String relatedGroupType = relatedGroup.getType();
			boolean active = isActive(groupRelation.getStatus());

			com.idega.group.cache.bean.GroupRelation relation = relations.get(relationId);
			CachedGroup parent = new CachedGroup(relationId, groupId, groupType, active);
			CachedGroup child = new CachedGroup(relationId, relatedGroupId, relatedGroupType, active);
			if (active) {
				addRelations(relation, parent, child);
			} else {
				relations.remove(relationId);
				groups.remove(relatedGroupId);
			}

			doCheckRelations(groupId);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error updating group relation: " + event.getGroupRelationId(), e);
		}
	}

	private boolean isActive(String status) {
		return status != null && (GroupRelation.STATUS_ACTIVE.equals(status) || GroupRelation.STATUS_ACTIVE_PENDING.equals(status));
	}

	private Map<Integer, com.idega.group.cache.bean.GroupRelation> relations = new HashMap<>();
	private Map<Integer, CachedGroup> groups = new HashMap<>();
	private Map<String, Set<Integer>> types = new HashMap<>();

	private void doStartCachingGroupRelations() {
		if (!isCacheEnabled("_relations")) {
			return;
		}

		Thread cacher = new Thread(new Runnable() {

			@Override
			public void run() {
				doCacheGroupRelations();
			}
		});
		if (DefaultIWBundle.isProductionEnvironment()) {
			cacher.run();
		} else {
			cacher.start();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void doCacheGroupRelations() {
		long start = System.currentTimeMillis();
		try {
			relations = new HashMap<>();

			Long count = groupDAO.getSingleResultByInlineQuery("select count(gr.groupRelationID) from " + GroupRelation.class.getName() +
					" gr where gr.status = '" + GroupRelation.STATUS_ACTIVE + "' or gr.status = '" + GroupRelation.STATUS_ACTIVE_PENDING + "'", Long.class);
//			Integer count = 8801;
			if (count == null || count <= 0) {
				return;
			}

			int columns = 5;
			String query = "select gr.groupRelationID, gr.group.id, gr.group.groupType.groupType, gr.relatedGroup.id, gr.relatedGroup.groupType.groupType from " + GroupRelation.class.getName() +
					" gr where gr.status = '" + GroupRelation.STATUS_ACTIVE + "' or gr.status = '" + GroupRelation.STATUS_ACTIVE_PENDING + "'";

			int index = 0;
			int step = 50000;
			while (index < count) {
				List<Object[]> data = groupDAO.getResultListByInlineQuery(query, Object[].class, index, step, null);
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
					String groupType = (String) relationData[2];

					Integer relatedGroupId = ((Number) relationData[3]).intValue();
					String relatedGroupType = (String) relationData[4];
					if (GroupTypeConstants.GROUP_TYPE_ALIAS.equals(relatedGroupType)) {
						aliasesIds.put(relatedGroupId, groupId);
					}

//					String status = (String) relationData[5];
					boolean active = true;//isActive(status);
					addRelations(
							new com.idega.group.cache.bean.GroupRelation(relationId, groupId, groupType, relatedGroupId, relatedGroupType, active),
							new CachedGroup(relationId, groupId, groupType, active),
							new CachedGroup(relationId, relatedGroupId, relatedGroupType, active)
					);
				}

				if (!MapUtil.isEmpty(aliasesIds)) {
					int aliasColumns = 3;
					List<Object[]> aliases = groupDAO.getResultListByInlineQuery(
							"select g.id, g.alias.id, g.alias.groupType.groupType from " + Group.class.getName() + " g where g.id in :aliasesIds",
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
							String relatedGroupType = (String) alias[2];

							addRelations(null, parent, new CachedGroup(parent == null ? null : parent.getGroupRelationId(), relatedGroupId, relatedGroupType, true));
						}
					}
				}

				getLogger().info("Cached group relations from " + index + " to " + (index + step) + " from " + count);
				index += step;
			}

			getLogger().info("Starting to check all group relations...");
			doCheckRelations();
			getLogger().info("Finished checking all group relations");
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching group relations", e);
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), getClass().getSimpleName() + ".doCacheGroupRelations");
		}
	}

	private void addRelations(com.idega.group.cache.bean.GroupRelation relation, CachedGroup parent, CachedGroup child) {
		if (relation != null) {
			relations.put(relation.getId(), relation);
		}

		addRelation(parent);
		addRelation(child);

		if (parent != null && child != null) {
			parent.addChild(child);
			child.addParent(parent);
		}
	}

	private void addRelation(CachedGroup cachedGroup) {
		if (cachedGroup == null || !cachedGroup.isActive()) {
			return;
		}

		groups.put(cachedGroup.getId(), cachedGroup);
		Integer id = cachedGroup.getId();
		String type = cachedGroup.getType();
		if (!StringUtil.isEmpty(type) && id != null) {
			Set<Integer> idsByType = types.get(type);
			if (idsByType == null) {
				idsByType = new HashSet<>();
				types.put(type, idsByType);
			}
			idsByType.add(id);
		}
	}

	private void doCheckRelations() {
		List<Integer> relationsIds = new ArrayList<>(relations.keySet());
		for (Integer relationId: relationsIds) {
			doCheckRelations(relationId);
		}
	}

	private void doCheckRelations(Integer relationId) {
		if (relationId == null) {
			return;
		}

		com.idega.group.cache.bean.GroupRelation relation = relations.get(relationId);
		doCheckRelations(relation);
	}

	private void doCheckRelations(com.idega.group.cache.bean.GroupRelation relation) {
		if (relation == null) {
			return;
		}

		CachedGroup parent = groups.get(relation.getGroupId());
		if (parent == null) {
			parent = new CachedGroup(relation.getId(), relation.getGroupId(), relation.getGroupType(), true);
			groups.put(parent.getId(), parent);
		}
		CachedGroup child = groups.get(relation.getRelatedGroupId());
		if (child == null) {
			child = new CachedGroup(relation.getId(), relation.getRelatedGroupId(), relation.getRelatedGroupType(), true);
			groups.put(child.getId(), child);
		}
		if (parent != null && child != null) {
			parent.addChild(child);
			child.addChild(parent);
		}
	}

	private boolean isCacheEnabled(String option) {
		String key = "groups_cacher.cache_enabled";
		if (option != null) {
			key = key.concat(option);
		}
		return getApplication().getSettings().getBoolean(key, option == null ? true: false);
	}

	private void doCacheGroups() {
		try {
			List<String> groupTypes = groupDAO.getGroupTypes();
			if (ListUtil.isEmpty(groupTypes)) {
				return;
			}

			groupTypes.parallelStream().forEach(type -> {
				long start = System.currentTimeMillis();
				int amount = doCacheGroupsByType(type);
				getLogger().info("Cached " + amount + " groups with type '" + type + "' in " + (System.currentTimeMillis() - start) + " ms");
			});
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching groups", e);
		}
	}

	private Map<Integer, CachedGroup> getIdsCache() {
		Map<Integer, CachedGroup> cache = getCache("EPLATFORM_ACTIVE_GROUPS_IDS_CACHE", 2592000, 2592000, Integer.MAX_VALUE, false);
		return cache;
	}
	private Map<String, Set<Integer>> getTypesCache() {
		Map<String, Set<Integer>> cache = getCache("EPLATFORM_ACTIVE_GROUPS_TYPES_CACHE", 2592000, 2592000, Integer.MAX_VALUE, false);
		return cache;
	}

	private List<CachedGroup> getCachedGroups(List<Integer> ids) {
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		try {
			List<Property<Integer, String>> data = groupDAO.getIdsAndTypes(ids);
			if (ListUtil.isEmpty(data)) {
				return null;
			}

			List<CachedGroup> results = new CopyOnWriteArrayList<>();
			for (Property<Integer, String> property: data) {
				if (property == null) {
					continue;
				}

				results.add(new CachedGroup(property.getKey(), property.getValue()));
			}
			return results;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group info by IDs: " + ids, e);
		}
		return null;
	}

	private Integer getCachedChildren(Integer id, CachedGroup cachedGroup) {
		Map<Integer, List<Integer>> children = groupDAO.getChildGroupsIds(Arrays.asList(id), null);

		Map<Integer, List<CachedGroup>> cachedChildren = new TreeMap<>();
		cachedGroup.setAllChildren(cachedChildren);

		if (MapUtil.isEmpty(children)) {
			return id;
		}

		for (Integer level: children.keySet()) {
			List<Integer> levelIds = children.get(level);
			List<CachedGroup> cachedGroups = getCachedGroups(levelIds);
			cachedChildren.put(level, cachedGroups);
		}
		return id;
	}

	private Integer getCachedParents(Integer id, CachedGroup cachedGroup) {
		List<Integer> parentGroupsIds = groupDAO.getParentGroupsIdsRecursive(Arrays.asList(id), null);
		if (ListUtil.isEmpty(parentGroupsIds)) {
			return null;
		}

		List<CachedGroup> cachedParentGroups = getCachedGroups(parentGroupsIds);
		cachedGroup.setAllParents(cachedParentGroups);
		return id;
	}

	private String getQuery(List<Integer> ids, String type) {
		if (ListUtil.isEmpty(ids) && StringUtil.isEmpty(type)) {
			return null;
		}

		String query = "select g.id, g.groupType.groupType from " + Group.class.getName() + " g where ";
		if (!StringUtil.isEmpty(type)) {
			query = query.concat("g.groupType.groupType = :groupType");
		} else if (!ListUtil.isEmpty(ids)) {
			query = query.concat("g.id in (:ids) and g.groupType.groupType != '").concat(User.USER_GROUP_TYPE).concat("'");
		}

		return query;
	}

	private int doCacheGroupsByType(String type) {
		return doCacheGroups(null, type);
	}

	private int doCacheGroups(List<Integer> ids, String type) {
		if (ListUtil.isEmpty(ids) && StringUtil.isEmpty(type)) {
			return 0;
		}
		if (!isCacheEnabled("_by_ids")) {
			return 0;
		}

		String query = getQuery(ids, type);
		if (query == null) {
			getLogger().warning("Unable to cache. IDs: " + ids + ", type: " + type);
			return 0;
		}
		try {
			Param param = new Param(StringUtil.isEmpty(type) ? "ids" : "groupType", StringUtil.isEmpty(type) ? ids : type);
			List<Object[]> results = groupDAO.getResultListByInlineQuery(
					query,
					Object[].class,
					param
			);
			if (ListUtil.isEmpty(results)) {
				getLogger().warning("There are no groups with " + (StringUtil.isEmpty(type) ? "IDs " + ids : "type " + type));
				return 0;
			}

			Map<Integer, CachedGroup> idsCache = getIdsCache();
			Map<String, Set<Integer>> typesCache = getTypesCache();

			Integer cached = 0, total = results.size();
			for (Object[] data: results) {
				cached++;

				if (ArrayUtil.isEmpty(data)) {
					continue;
				}
				if (idsCache.containsKey(data[0])) {
					continue;
				}

				doCacheGroup(data, idsCache, typesCache);
				getLogger().info("Cached " + cached + " groups out of " + total);
			}
			return cached;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching groups with type " + (StringUtil.isEmpty(type) ? "IDs " + ids : "type " + type) + ", query: " + query, e);
		}
		return 0;
	}

	private void doCacheGroup(Object[] data, Map<Integer, CachedGroup> idsCache, Map<String, Set<Integer>> typesCache) {
		if (ArrayUtil.isEmpty(data) || data.length < 2 || data[0] == null || data[1] == null) {
			return;
		}

		Integer groupId = null;
		try {
			//	Group
			final Integer id = (Integer) data[0];
			groupId = id;
			String type = (String) data[1];

			CachedGroup cachedGroup = new CachedGroup(id, type);
			idsCache.put(id, cachedGroup);

			Set<Integer> typeIds = typesCache.get(type);
			if (typeIds == null) {
				typeIds = new LinkedHashSet<>();
				typesCache.put(type, typeIds);
			}
			typeIds.add(id);

			ExecutorService service = Executors.newFixedThreadPool(2);
			List<Future<Integer>> futures = new ArrayList<>();

			//	All children
			Callable<Integer> groupChildren = new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					return getCachedChildren(id, cachedGroup);
				}

				@Override
				public String toString() {
					return "Children for group with ID " + id;
				}

			};
			futures.add(service.submit(groupChildren));

			//	All parents
			Callable<Integer> groupParents = new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					return getCachedParents(id, cachedGroup);
				}

				@Override
				public String toString() {
					return "Parents for group with ID " + id;
				}

			};
			futures.add(service.submit(groupParents));

			futures.parallelStream().forEach(future -> {
				try {
					future.get();
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error executing " + future, e);
				}
			});

			service.shutdown();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching group with ID: " + groupId, e);
		}
	}

	private void doUpdateCacheForGroup(GroupRelation relation) {
		if (relation == null) {
			return;
		}

		Map<Integer, CachedGroup> idsCache = getIdsCache();
		Map<String, Set<Integer>> typesCache = getTypesCache();

		Group relatedGroup = null;
		try {
			relatedGroup = relation.getRelatedGroup();
			if (relatedGroup == null) {
				return;
			}

			Integer groupId = relatedGroup.getID();
			if (groupId == null) {
				return;
			}

			idsCache.remove(groupId);

			String type = relatedGroup.getType();
			if (!StringUtil.isEmpty(type)) {
				Set<Integer> typesIds = typesCache.get(type);
				if (!ListUtil.isEmpty(typesIds)) {
					typesIds.remove(groupId);
				}
			}

			String status = relation.getStatus();
			if (StringUtil.isEmpty(status) || GroupRelation.STATUS_ACTIVE.equals(status) || GroupRelation.STATUS_ACTIVE_PENDING.equals(status)) {
				doCacheGroup(new Object[] {relatedGroup.getID(), type}, idsCache, typesCache);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error updating cache for group. Related group: " + relatedGroup, e);
		}
	}

	@Override
	public void doCacheGroup(Integer groupId) {
		if (groupId == null) {
			return;
		}

		try {
			doCache(Arrays.asList(groupId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching group by ID: " + groupId, e);
		}
	}

	@Override
	public List<Group> findActiveGroupsByType(String type) {
		if (StringUtil.isEmpty(type)) {
			return null;
		}

		Map<String, Set<Integer>> typesCache = getTypesCache();
		Set<Integer> ids = typesCache.get(type);
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		return groupDAO.findGroups(new ArrayList<>(ids));
	}

	@Override
	public List<Integer> findGroupsIdsByTypes(List<String> types) {
		if (ListUtil.isEmpty(types)) {
			return null;
		}

		Set<Integer> results = new HashSet<>();
		for (String type: types) {
			Set<Integer> idsByType = this.types.get(type);
			if (!ListUtil.isEmpty(idsByType)) {
				results.addAll(idsByType);
			}
		}

		if (ListUtil.isEmpty(results)) {
			return groupDAO.findGroupsIdsByTypes(types);
		}

		return new ArrayList<>(results);
	}

	@Override
	public void doCache(List<Integer> groupsIds) {
		if (ListUtil.isEmpty(groupsIds)) {
			return;
		}

		List<Integer> toCache = new ArrayList<>();
		Map<Integer, CachedGroup> cache = getIdsCache();
		for (Integer id: groupsIds) {
			if (id == null || cache.containsKey(id)) {
				continue;
			}

			toCache.add(id);
		}

		doCacheGroups(toCache, null);
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

		Map<Integer, List<Integer>> results = new HashMap<>();
		for (Integer parentId: parentIds) {
			CachedGroup parent = groups.get(parentId);
			Map<Integer, List<Integer>> tmpResults = getChildren(parent, childGroupsTypes, levels);
			if (MapUtil.isEmpty(tmpResults)) {
				continue;
			}

			MapUtil.append(results, tmpResults);
		}

		return results;
	}

	@Override
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentIds, List<String> childGroupsTypes, boolean loadAliases) {
		return getChildGroupsIds(parentIds, childGroupsTypes);
	}

	@Override
	public List<Integer> getGroupsIdsByIdsAndTypes(List<Integer> parentIds, List<String> childGroupsTypes) {
		if (ListUtil.isEmpty(parentIds)) {
			return null;
		}

		Set<Integer> results = new HashSet<>();
		for (Integer parentId: parentIds) {
			CachedGroup parent = groups.get(parentId);
			Map<Integer, List<Integer>> tmpResults = getChildren(parent, childGroupsTypes, null);
			if (MapUtil.isEmpty(tmpResults)) {
				continue;
			}

			for (List<Integer> levelResults: tmpResults.values()) {
				if (ListUtil.isEmpty(levelResults)) {
					continue;
				}

				results.addAll(levelResults);
			}
		}

		return new ArrayList<>(results);
	}

	private Map<Integer, List<Integer>> getChildren(CachedGroup parent, List<String> childGroupsTypes, Integer maxLevels) {
		if (parent == null || ListUtil.isEmpty(childGroupsTypes)) {
			return null;
		}

		Integer parentId = parent.getId();
		Set<Integer> results = new HashSet<>();
		for (String type: childGroupsTypes) {
			Set<Integer> ids = types.get(type);
			if (ListUtil.isEmpty(ids)) {
				continue;
			}

			for (Integer id: ids) {
				if (id.intValue() == parentId.intValue()) {
					results.add(id);
					continue;
				}

				CachedGroup group = groups.get(id);
				if (group == null) {
					continue;
				}

				Map<Integer, Boolean> allParents = new HashMap<>();
				collectAllParentsIds(group.getParents(), allParents, maxLevels, 1);

				if (!MapUtil.isEmpty(allParents) && allParents.get(parentId) != null) {
					results.add(id);
				}
			}
		}

		Map<Integer, List<Integer>> allResults = new HashMap<>();
		allResults.put(1, new ArrayList<>(results));
		return allResults;

//		Map<Integer, List<Integer>> results = new HashMap<>();
//		return getChildren(parent, childGroupsTypes, maxLevels, null, results, null);
	}

	private void collectAllParentsIds(Map<Integer, CachedGroup> parents, Map<Integer, Boolean> results, Integer maxLevels, Integer currentLevel) {
		if (MapUtil.isEmpty(parents)) {
			return;
		}
		if (maxLevels != null && maxLevels > currentLevel) {
			return;
		}

		for (Integer id: parents.keySet()) {
			if (results.containsKey(id)) {
				continue;
			}

			results.put(id, Boolean.TRUE);

			CachedGroup parent = parents.get(id);
			if (parent == null) {
				continue;
			}

			collectAllParentsIds(parent.getParents(), results, maxLevels, currentLevel + 1);
		}
	}

	private Map<Integer, List<Integer>> getChildren(
			CachedGroup parent,
			List<String> childGroupsTypes,
			Integer maxLevels,
			Integer currentLevel,
			Map<Integer, List<Integer>> results,
			Set<Integer> checkedIds
	) {
		if (results == null) {
			results = new HashMap<>();
		}

		if (parent == null) {
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
		if (checkedIds.contains(parent.getId())) {
			return results;
		}
		checkedIds.add(parent.getId());

		Map<Integer, CachedGroup> children = parent.getChildren();
		if (MapUtil.isEmpty(children)) {
			return results;
		}

		List<Integer> levelResults = results.get(currentLevel);
		if (ListUtil.isEmpty(levelResults)) {
			levelResults = new ArrayList<>();
			results.put(currentLevel, levelResults);
		}
		for (CachedGroup child: children.values()) {
			if (childGroupsTypes.contains(child.getType())) {
				levelResults.add(child.getId());
			}

			Map<Integer, List<Integer>> childrenResults = getChildren(child, childGroupsTypes, maxLevels, currentLevel, results, checkedIds);
			getLogger().info("Children: " + childrenResults + " for " + child);
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
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		if (id) {
			@SuppressWarnings("unchecked")
			List<T> results = (List<T>) ids;
			return results;
		} else if (entities) {
			@SuppressWarnings("unchecked")
			List<T> results = (List<T>) groupDAO.findGroups(ids);
			return results;
		}

		return null;
	}

}