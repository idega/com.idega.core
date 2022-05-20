package com.idega.user.business;

public interface UserGuardian {

	public boolean canDelete(Integer userId);

	public boolean canRemoveFromGroup(Integer groupId, Integer userId);

}