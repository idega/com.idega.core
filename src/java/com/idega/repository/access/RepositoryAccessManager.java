package com.idega.repository.access;

import java.util.Collection;

import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import com.idega.presentation.IWContext;
import com.idega.user.data.bean.User;

public interface RepositoryAccessManager extends AccessControlManager {

	public boolean hasPermission(User user, String path, String privilegeName);
	public boolean hasPermission(User user, String path, Privilege privilege);

	public boolean hasPermission(IWContext iwc, String path) throws Exception;
	public boolean hasPermission(IWContext iwc, Collection<String> paths) throws Exception;

}