package com.idega.group.cache.business;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.idega.user.data.bean.Group;

public interface GroupsCacheService {

	public static final String GROUP_TREE_CACHE_NAME = "EPLATFORM_GROUP_TREE_CACHE";

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> notContainingTypes, Integer from, Integer to);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notContainingTypes, Integer levels, Integer from, Integer to);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notHavingTypes, Integer from, Integer to);

	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, List<String> groupTypes);

	public List<Group> getParentGroups(Integer id, List<String> groupTypes);

	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels);

	public void doCacheGroup(Integer groupId);

	public List<com.idega.user.data.bean.Group> findActiveGroupsByType(String type);

	public void doCache(List<Integer> groupsIds);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> havingTypes, List<String> notHavingTypes, Integer from, Integer to);

	public <K extends Serializable, CK extends Serializable, V extends Serializable> Map<K, Map<CK, List<V>>> getCache(Integer size, Long timeToLiveInSeconds, boolean resetable);

	public <V extends Serializable> Map<String, V> getGroupsTreeCache(boolean checkIfEmpty);

	public Map<Integer, List<Integer>> getUsersGroupsCache(boolean checkIfEmpty, Integer userId);

	public Map<String, List<com.idega.user.data.bean.Group>> getUserGroupsCache();

	public void setCacheInProgress(String name, Boolean inProgress);
	public boolean isCacheInProgress(String name);
}