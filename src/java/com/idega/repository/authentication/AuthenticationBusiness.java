/*
 * $Id: AuthenticationBusiness.java,v 1.5 2009/01/14 14:21:55 civilis Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.repository.authentication;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlList;
import javax.servlet.http.HttpServletRequest;

public interface AuthenticationBusiness {

//	public WebdavResources getAllRoles() throws HttpException, RepositoryException, IOException;

//	public WebdavResources getAllRoles(UsernamePasswordCredentials credentials) throws HttpException, RepositoryException, IOException;

	public String getUserURI(String userName) throws RepositoryException;

	public String getUserPath(String userName);

	public String getGroupURI(String groupName) throws RepositoryException;

	public String getGroupPath(String groupName) throws RepositoryException;

	public String getRoleURI(String roleName) throws RepositoryException;

	public String getRolePath(String roleName) throws RepositoryException;

	public void updateRoleMembershipForUser(String userLoginName, Set<String> roleNamesForUser, Set<String> loginNamesOfAllLoggedOnUsers)
		throws RepositoryException, IOException;

	public Credentials getRootUserCredentials();

	public boolean isRootUser(HttpServletRequest request);

	public AccessControlList applyDefaultPermissionsToRepository(AccessControlList acl);

	public abstract AccessControlList applyPermissionsToRepository(AccessControlList acl, Collection<String> roles);

}