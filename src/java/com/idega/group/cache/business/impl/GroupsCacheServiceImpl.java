package com.idega.group.cache.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.group.cache.bean.CachedGroup;
import com.idega.group.cache.business.GroupsCacheService;
import com.idega.idegaweb.IWMainApplicationStartedEvent;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.GroupRelationDAO;
import com.idega.user.dao.Property;
import com.idega.user.data.GroupTypeBMPBean;
import com.idega.user.data.User;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.events.GroupRelationChangedEvent;
import com.idega.util.ArrayUtil;
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
		return getChildGroupsIds(parentGroupsIds, null, null);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> notContainingTypes, Integer from, Integer to) {
		return getChildGroupsIds(parentGroupsIds, null, notContainingTypes, null, from, to);
	}

	@Override
	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels) {
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
			List<Integer> ids,
			List<String> groupTypes,
			List<String> notContainingTypes,
			Integer levels,
			Integer from,
			Integer to,
			boolean loadChildren
	) {
		if (ListUtil.isEmpty(ids)) {
			return;
		}

		List<CachedGroup> cachedGroups = new ArrayList<>();
		Map<Integer, CachedGroup> cache = getCache();
		ids.parallelStream().forEach(id -> {
			CachedGroup cachedGroup = cache.get(id);
			if (cachedGroup != null) {
				cachedGroups.add(cachedGroup);
			}
		});
		if (cachedGroups.size() != ids.size()) {
			return;
		}

		final Integer level = levels == null ? Integer.MAX_VALUE : levels;
		cachedGroups.parallelStream().forEach(group -> {
			doCollectIds(leveledResults, plainResults, loadChildren ? group.getChildren() : getConverted(group.getParents()), groupTypes, notContainingTypes, level);
		});
		if (from != null || to != null && !ListUtil.isEmpty(plainResults)) {
			if (from != null) {

			}
			//	todo: paging
		}
	}

	private Map<Integer, List<CachedGroup>> getConverted(List<CachedGroup> groups) {
		if (ListUtil.isEmpty(groups)) {
			return null;
		}

		Map<Integer, List<CachedGroup>> converted = new TreeMap<>();
		for (int i = 0; i < groups.size(); i++) {
			converted.put(i + 1, Arrays.asList(groups.get(0)));
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

		return groupDAO.findGroups(ids, null, null);
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

			List<Group> groups = groupDAO.findGroups(ids, null, null);
			if (!ListUtil.isEmpty(groups)) {
				results.put(level, groups);
			}
		}

		return results;
	}

	private void doCollectIds(Map<Integer, List<Integer>> results, List<Integer> ids, Map<Integer, List<CachedGroup>> source, List<String> groupTypes, List<String> notHavingTypes, Integer levels) {
		if (MapUtil.isEmpty(source)) {
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
					} else if (checkNotHavingTypes) {
						if (notHavingTypes.contains(levelCachedGroup.getType())) {
						} else {
							id = levelCachedGroup.getId();
						}
					} else {
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
		if (event instanceof IWMainApplicationStartedEvent) {
			if (isCacheEnabled()) {
				Thread cacher = new Thread(new Runnable() {

					@Override
					public void run() {
						doCacheGroups();
					}

				});
				cacher.start();
			}
		} else if (event instanceof GroupRelationChangedEvent) {
			if (isCacheEnabled()) {
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
		}
	}

	private boolean isCacheEnabled() {
		return getApplication().getSettings().getBoolean("groups_cacher.cache_enabled", false);
	}

	private void doCacheGroups() {
		try {
			GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

			List<String> groupTypes = groupDAO.getGroupTypes();
			if (ListUtil.isEmpty(groupTypes)) {
				return;
			}

			groupTypes.parallelStream().forEach(type -> {
				doCacheGroupsByType(groupDAO, type);
			});
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching groups", e);
		}
	}

	private Map<Integer, CachedGroup> getCache() {
		Map<Integer, CachedGroup> cache = getCache("EPLATFORM_ACTIVE_GROUPS_CACHE", 2592000, 2592000, Integer.MAX_VALUE, false);
		return cache;
	}

	private List<CachedGroup> getCachedGroups(GroupDAO groupDAO, List<Integer> ids) {
		if (ListUtil.isEmpty(ids)) {
			return null;
		}

		try {
			List<Property<Integer, String>> data = groupDAO.getIdsAndTypes(ids);
			if (ListUtil.isEmpty(data)) {
				return null;
			}

			List<CachedGroup> results = new ArrayList<>();
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

	private void doCacheChildren(GroupDAO groupDAO, List<Integer> ids, CachedGroup cachedGroup) {
		Map<Integer, List<Integer>> children = groupDAO.getChildGroupsIds(ids, null);
		Map<Integer, List<CachedGroup>> cachedChildren = new TreeMap<>();
		cachedGroup.setChildren(cachedChildren);
		List<Integer> levels = new ArrayList<>(children.keySet());
		levels.parallelStream().forEach(level -> {
			List<Integer> levelIds = children.get(level);
			cachedChildren.put(level, getCachedGroups(groupDAO, levelIds));
		});
	}

	private void doCacheParents(GroupDAO groupDAO, List<Integer> ids, CachedGroup cachedGroup) {
		List<Integer> parents = groupDAO.getParentGroupsIdsRecursive(ids, null);
		cachedGroup.setParents(getCachedGroups(groupDAO, parents));
	}

	private void doCacheGroupsByType(GroupDAO groupDAO, String type) {
		String query = "select case g.groupType.groupType when '".concat(GroupTypeBMPBean.TYPE_ALIAS).concat("' then g.alias.id else g.id end as id");
		query = query.concat(", case g.groupType.groupType when '").concat(GroupTypeBMPBean.TYPE_ALIAS).concat("' then g.alias.groupType.groupType else g.groupType.groupType end as type");
		query = query.concat(" from ").concat(GroupRelation.class.getName()).concat(" gr inner join gr.relatedGroup g");
		query = query.concat(" where gr.relatedGroupType.groupType = :groupType and (gr.status = '").concat(GroupRelation.STATUS_ACTIVE).concat("' OR gr.status = '");
		query = query.concat(GroupRelation.STATUS_PASSIVE_PENDING).concat("') and gr.relatedGroupType.groupType != '").concat(User.USER_GROUP_TYPE).concat("'");
		try {
			List<Object[]> results = groupDAO.getResultListByInlineQuery(
					query,
					Object[].class,
					new com.idega.core.persistence.Param("groupType", type)
			);
			if (ListUtil.isEmpty(results)) {
				return;
			}

			Map<Integer, CachedGroup> cache = getCache();

			results = new CopyOnWriteArrayList<>(results);
			int sliceSize = getSettings().getInt("cache_slice_size", 15);
			Integer totalLoaded = 0, total = results.size();
			while (results.size() > 0) {
				int slicesLoaded = 0;
				final List<Object[]> slice = results.size() > sliceSize ?	new CopyOnWriteArrayList<>(results.subList(0, sliceSize)) :
																			new CopyOnWriteArrayList<>(results);
					results.removeAll(slice);

					List<Object[]> tmp = new CopyOnWriteArrayList<>(slice);
					for (Object[] data: tmp) {
						doCacheGroup(data, cache, groupDAO, slice, true);
					}

					while (slice.size() > 0) {
						slicesLoaded = sliceSize - slice.size();
						getLogger().info("Slices loaded: " + slicesLoaded + " from " + sliceSize + ". Totally loaded " + totalLoaded + " from " + total);
						Thread.sleep(500);
					}
					totalLoaded += sliceSize;
				}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting groups by type: " + type + ", query: " + query, e);
		}
	}

	private void doCacheGroup(Object[] data, Map<Integer, CachedGroup> cache, GroupDAO groupDAO, List<Object[]> slice, boolean runInThread) {
		if (ArrayUtil.isEmpty(data) || data.length < 2 || data[0] == null || data[1] == null) {
			return;
		}

		Thread loader = new Thread(new Runnable() {

			@Override
			public void run() {
				Integer id = null;
				try {
					//	Group
					id = (Integer) data[0];
					String type = (String) data[1];
					CachedGroup cachedGroup = new CachedGroup(id, type);
					cache.put(id, cachedGroup);

					List<Integer> ids = Arrays.asList(id);

					ExecutorService service = Executors.newFixedThreadPool(2);
					List<Future<Boolean>> futures = new ArrayList<>();

					//	All children
					Callable<Boolean> groupChildren = new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							doCacheChildren(groupDAO, ids, cachedGroup);
							return Boolean.TRUE;
						}
					};
					futures.add(service.submit(groupChildren));

					//	All parents
					Callable<Boolean> groupParents = new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							doCacheParents(groupDAO, ids, cachedGroup);
							return Boolean.TRUE;
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
					getLogger().log(Level.WARNING, "Error caching group with ID: " + id, e);
				} finally {
					if (slice != null) {
						slice.remove(data);
					}
				}
			}
		});
		if (runInThread) {
			loader.start();
		} else {
			loader.run();
		}
	}

	private void doUpdateCacheForGroup(GroupRelation relation) {
		if (relation == null) {
			return;
		}

		Map<Integer, CachedGroup> cache = getCache();

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

			cache.remove(groupId);

			String status = relation.getStatus();
			if (StringUtil.isEmpty(status) || GroupRelation.STATUS_ACTIVE.equals(status) || GroupRelation.STATUS_ACTIVE_PENDING.equals(status)) {
				GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);
				doCacheGroup(new Object[] {relatedGroup.getID(), relatedGroup.getType()}, cache, groupDAO, null, false);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error cache for group. Related group: " + relatedGroup, e);
		}
	}

	@Override
	public void doCacheGroup(Integer groupId) {
		if (groupId == null) {
			return;
		}

		try {
			Group group = groupDAO.findGroup(groupId);
			if (group == null) {
				return;
			}

			doCacheGroup(new Object[] {group.getID(), group.getType()}, getCache(), groupDAO, null, true);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error caching group by ID: " + groupId, e);
		}
	}

}