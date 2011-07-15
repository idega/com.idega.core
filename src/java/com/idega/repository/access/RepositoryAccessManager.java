package com.idega.repository.access;

import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import com.idega.user.data.bean.User;

public interface RepositoryAccessManager extends AccessControlManager {

	public boolean hasPermission(User user, String path, String privilegeName);
	public boolean hasPermission(User user, String path, Privilege privilege);

}