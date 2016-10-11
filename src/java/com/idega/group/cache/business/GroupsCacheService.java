package com.idega.group.cache.business;

import java.util.List;
import java.util.Map;

import com.idega.user.data.bean.Group;

public interface GroupsCacheService {

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> notContainingTypes, Integer from, Integer to);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notContainingTypes, Integer levels, Integer from, Integer to);

	public List<Integer> getChildGroupsIds(List<Integer> parentGroupsIds, List<String> municipalities, List<String> unions, List<String> years, List<String> notHavingTypes, Integer from, Integer to);

	public List<Integer> getParentGroupsIdsRecursive(List<Integer> groupsIds, List<String> groupTypes);

	public List<Group> getParentGroups(Integer id, List<String> groupTypes);

	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, Integer levels);
	public Map<Integer, List<Group>> getChildGroups(List<Integer> parentGroupsIds, List<String> childGroupTypes, List<String> notHavingChildGroupTypes, Integer levels);

}