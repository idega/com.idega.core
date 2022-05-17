package com.idega.group.business;

public interface GroupRelationGuardian {

	public boolean canDelete(Integer groupRelationId, Integer groupId, Integer relatedGroupId);

}