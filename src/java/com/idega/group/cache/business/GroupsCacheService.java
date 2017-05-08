package com.idega.group.cache.business;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.idega.user.bean.GroupRelationBean;
import com.idega.user.data.bean.Group;

public interface GroupsCacheService {

	public static final String GROUP_TREE_CACHE_NAME = "EPLATFORM_GROUP_TREE_CACHE";

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> notContainingTypes, Integer from, Integer to);

	public List<Integer> getChildGroupIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notContainingTypes, Integer levels, Integer from, Integer to);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notHavingTypes, Integer from, Integer to);

	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, List<String> groupTypes);

	public List<Group> getParentGroups(Integer id, List<String> groupTypes);

	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels);

	public void doCacheGroup(Integer groupId);

	public List<Group> findActiveGroupsByType(String type);

	public void doCache(List<Integer> groupsIds);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> havingTypes, List<String> notHavingTypes, Integer from, Integer to);

	public <K extends Serializable, CK extends Serializable, V extends Serializable> Map<K, Map<CK, List<V>>> getCache(Integer size, Long timeToLiveInSeconds, boolean resetable);

	public <V extends Serializable> Map<String, V> getGroupsTreeCache(boolean checkIfEmpty);

	public Map<String, List<Integer>> getUsersGroupsCache(boolean checkIfEmpty, Integer userId);

	public Map<String, List<Group>> getUserGroupsCache();

	public void setCacheInProgress(String name, Boolean inProgress);
	public boolean isCacheInProgress(String name);

	public void doCacheGroupRelations();

	public List<Integer> findGroupsIdsByTypes(List<String> types);

	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentIds, List<String> childGroupsTypes);
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentIds, List<String> childGroupsTypes, Integer levels);
	public Map<Integer, List<Integer>> getChildGroupsIds(List<Integer> parentIds, List<String> childGroupsTypes, boolean loadAliases);

	public List<Integer> getGroupsIdsByIdsAndTypes(List<Integer> parentIds, List<String> childGroupsTypes);

	public <T extends Serializable> List<T> filterGroupsByIdsAndTypes(List<Integer> parentIds, List<String> childGroupsTypes, Class<T> resultType);

	public boolean isUserCacheOn();

	public List<GroupRelationBean> getGroupRelationsByRelatedGroupTypeAndGroupTypes(String relatedGroupType, List<String> groupTypes, List<Integer> groupsIds, List<String> entityTypes);

	public Integer getFirstAncestorGroupIdOfType(Integer groupId, List<String> groupTypes);
	public Integer getFirstAncestorGroupIdOfType(Integer groupId, List<String> groupTypes, boolean selectPassive);
	public List<Integer> getFirstAncestorGroupIdsOfType(List<Integer> groupsIds, List<String> groupTypes, boolean selectPassive);

}