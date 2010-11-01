package com.idega.repository.authentication;

import java.security.Principal;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.idega.core.accesscontrol.jaas.IWUserPrincipal;

public class RepositoryAuthenticatedRequest extends HttpServletRequestWrapper {

	private Principal userPrincipal;
	private Set<String> userRoles;

	public RepositoryAuthenticatedRequest(HttpServletRequest request, String loginName, Set<String> roles) {
		super(request);
		this.userPrincipal = new IWUserPrincipal(loginName);
		this.userRoles = roles;
	}

	@Override
	public Principal getUserPrincipal() {
		return this.userPrincipal;
	}

	@Override
	public String getRemoteUser() {
		return this.userPrincipal.getName();
	}

	@Override
	public boolean isUserInRole(String role) {
		return this.userRoles.contains(role);
	}

}