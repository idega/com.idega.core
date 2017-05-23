package com.idega.repository.authentication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.RepositoryStartedEvent;
import com.idega.repository.RepositoryService;
import com.idega.servlet.filter.BaseFilter;
import com.idega.user.data.bean.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class RepositoryAuthenticator extends BaseFilter {

	private static final String REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME = Principal.class.getName() + "_user_principal",
								REPOSITORY_PASSWORD = "iw_repository_password",
								REPOSITORY_ROLES_UPDATED = "iw_repository_roles_updated",
								PROPERTY_UPDATE_ROLES = "repository.updateroles.enable";

	@Autowired
	private RepositoryService repository;
	@Autowired
	private AuthenticationBusiness authentication;

	private boolean defaultPermissionsApplied;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		doAuthentication(request, response, chain);

		if (!defaultPermissionsApplied) {
			defaultPermissionsApplied = true;
			defaultPermissionsApplied = applyDefaultPermissionsToRepository();

			IWMainApplication iwma = IWMainApplication.getIWMainApplication((HttpServletRequest) request);
			ELUtil.getInstance().publishEvent(new RepositoryStartedEvent(iwma));
		}
	}

	private boolean applyDefaultPermissionsToRepository() {
		try {
			if (!getRepositoryService().createFolderAsRoot(CoreConstants.CONTENT_PATH)) {
				return false;
			}
			//	TODO: use real access rights
			getRepositoryService().applyAccessControl(CoreConstants.CONTENT_PATH, new AccessControlPolicy[] {
					new IWAccessControlPolicy("content_editor", Arrays.asList(Privilege.JCR_ALL))
			});

			if (!getRepositoryService().createFolderAsRoot(CoreConstants.PUBLIC_PATH)) {
				return false;
			}

			return true;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void doAuthentication(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession(true);
		LoginBusinessBean loginBusiness = getLoginBusiness(req);

		try {
			if (loginBusiness.isLoggedOn(req)) {
				LoggedOnInfo lInfo = loginBusiness.getLoggedOnInfo(session);
				if (lInfo == null) {
					throw new RuntimeException("Unable to get logged on info for session " + session);
				}
				req = setAsAuthenticatedInRepository(req, lInfo.getLogin(), lInfo);
			} else {
				String[] loginAndPassword = loginBusiness.getLoginNameAndPasswordFromBasicAuthenticationRequest(req);
				String loggedInUser = getUserAuthenticatedByRepository(session);
				if (!ArrayUtil.isEmpty(loginAndPassword) && loginAndPassword.length >= 2) {
					String username = loginAndPassword[0];
					String password = loginAndPassword[1];
					LoggedOnInfo lInfo = loginBusiness.getLoggedOnInfo(session, username);
					if (loggedInUser == null) {
						if (isAuthenticated(req, lInfo, username, password)) {
							req = setAsAuthenticatedInRepository(req, username, lInfo);
						} else {
							setAsUnauthenticatedInRepository(session);
						}
					} else if (!username.equals(loggedInUser)) {
						if (isAuthenticated(req, lInfo, username, password)) {
							req = setAsAuthenticatedInRepository(req, username, lInfo);
						} else {
							setAsUnauthenticatedInRepository(session);
						}
					}

				} else if (loggedInUser != null) {
					setAsUnauthenticatedInRepository(session);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
			return;
		}

		chain.doFilter(request, response);
	}

	private HttpServletRequest setAsAuthenticatedInRepository(HttpServletRequest request, String loginName, LoggedOnInfo lInfo)
		throws RemoteException, IOException, RepositoryException {

		String repositoryPrincipal = loginName;
		HttpSession session = request.getSession(true);
		LoginBusinessBean loginBusiness = getLoginBusiness(request);

		if (loginBusiness.isLoggedOn(request)) {
			LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
			if (loginSession.isSuperAdmin()) {
				String rootUserName = ((SimpleCredentials) getAuthenticationBusiness().getRootUserCredentials()).getUserID();
				request = new RepositoryAuthenticatedRequest(request, rootUserName, Collections.singleton(rootUserName));
				repositoryPrincipal = rootUserName;
			} else {
				if (request.getUserPrincipal() == null && lInfo != null)
					//	Wrapping request
					request = new RepositoryAuthenticatedRequest(request, loginName, lInfo.getUserRoles());

				updateRolesForUser(request, lInfo);
			}
		} else {
			String rootUserName = ((SimpleCredentials) getAuthenticationBusiness().getRootUserCredentials()).getUserID();
			if (loginName.equals(rootUserName)) {
				request = new RepositoryAuthenticatedRequest(request, rootUserName, Collections.singleton(rootUserName));
			} else {
				request = new RepositoryAuthenticatedRequest(request, loginName, lInfo.getUserRoles());
				updateRolesForUser(request, lInfo);
			}
		}

		try {
			session.setAttribute(REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME, repositoryPrincipal);
		} catch (Exception e) {}
		return request;
	}

	private void updateRolesForUser(HttpServletRequest request, LoggedOnInfo lInfo) throws RepositoryException, RemoteException, IOException {
		if (lInfo == null) {
			return;
		}

		IWMainApplicationSettings settings = getIWMainApplication(request).getSettings();

		if (settings.getBoolean("repository.generate_home_folders", true)) {
			HttpSession session = request.getSession(true);
			Object userFolderGenerated = session.getAttribute("user_folder_generated");
			if (userFolderGenerated == null) {
				session.setAttribute("user_folder_generated", Boolean.TRUE);
				generateUserFolders(request, lInfo.getUser());
			}
		}

		if (settings.getBoolean(PROPERTY_UPDATE_ROLES, Boolean.TRUE)) {
			Object updated = lInfo.getAttribute(REPOSITORY_ROLES_UPDATED);
			if (updated instanceof Boolean && (Boolean) updated)
				return;

			AuthenticationBusiness business = getAuthenticationBusiness();
			business.updateRoleMembershipForUser(lInfo.getLogin(), lInfo.getUserRoles(), null);
			lInfo.setAttribute(REPOSITORY_ROLES_UPDATED, Boolean.TRUE);
		}
	}

	private void generateUserFolders(HttpServletRequest request, User user) throws RepositoryException {
		Boolean homeFolderGenerated = Boolean.FALSE;
		try {
			homeFolderGenerated = getRepositoryService().generateUserFolders(user, request.getRemoteUser());
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error generating home folders for " + user, e);
		}
		request.getSession(true).setAttribute("user_folder_generated", homeFolderGenerated);
	}

	private String getUserAuthenticatedByRepository(HttpSession session) {
		try {
			return (String) session.getAttribute(REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME);
		} catch (Exception e) {}
		return null;
	}

	private void setAsUnauthenticatedInRepository(HttpSession session) {
		try {
			session.removeAttribute(REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME);
		} catch (Exception e) {}
	}

	private boolean isAuthenticated(HttpServletRequest request, LoggedOnInfo info, String login, String password) {
		LoginBusinessBean loginBusiness = getLoginBusiness(request);
		if (loginBusiness.isLoggedOn(request)) {
			return true;
		} else {
			if (getAuthenticationBusiness().isRootUser(request)) {
				return true;
			}
			if (info != null) {
				String repositoryPassword = (String) info.getAttribute(REPOSITORY_PASSWORD);
				return !StringUtil.isEmpty(repositoryPassword) && repositoryPassword.equals(password);
			}
		}
		return false;
	}

	private RepositoryService getRepositoryService() {
		if (repository == null)
			autowire();

		return repository;
	}

	private AuthenticationBusiness getAuthenticationBusiness() {
		if (authentication == null)
			autowire();

		return authentication;
	}

	private void autowire() {
		ELUtil.getInstance().autowire(this);
	}

	@Override
	public void destroy() {}

}