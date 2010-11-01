package com.idega.repository.authentication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlList;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginContext;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.RepositoryService;
import com.idega.user.data.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AuthenticationBusinessImpl extends DefaultSpringBean implements AuthenticationBusiness {

	private static final long serialVersionUID = 5768690543595785783L;

	private static final String	PATH_USERS = "/users",
								PATH_GROUPS = "/groups",
								PATH_ROLES = "/roles",
								SLIDE_DEFAULT_ROOT_USER = "root",

//								SLIDE_ROLE_NAME_USER = "user",
//								GROUP_MEMBER_SET = "group-member-set",
//								NO_PASSWORD = "no_password",
								ROOT_USER_NAME = "root";

//	private static final PropertyName GROUP_MEMBER_SET_PROPERTY_NAME = new PropertyName("DAV:", GROUP_MEMBER_SET);

	private Credentials rootCredentials;

	@Autowired
	private RepositoryService repository;

	@Override
	public Credentials getRootUserCredentials() {
		if (rootCredentials == null) {
			AccessController accCtrl = IWMainApplication.getDefaultIWMainApplication().getAccessController();
			User admin = null;
			try {
				admin = accCtrl.getAdministratorUser();
			} catch (Exception e) {
			}

			String user = null;
			String password = null;
			if (admin != null) {
				LoginContext loginContext = LoginBusinessBean.getLoginContext(admin);
				user = loginContext.getUserName();
				password = loginContext.getPassword();
//				LoginInfo info = LoginDBHandler.getLoginInfo(LoginDBHandler.getUserLogin(admin));
//				if (info != null) {
//					info.get
//				}
			}
			if (user == null || password == null) {
				return null;
			}

			rootCredentials = repository.getCredentials(user, password);
		}
		return rootCredentials;
	}

//	public WebdavResources getAllRoles() throws HttpException, RemoteException, IOException {
//		return getAllRoles(null);
//	}

//	public WebdavResources getAllRoles(UsernamePasswordCredentials credentials) throws HttpException, RemoteException, IOException {
//		IWSlideService service = getSlideServiceInstance();
//		WebdavResource rolesFolder = new WebdavResource(service.getWebdavServerURL(credentials, PATH_ROLES));
//		return rolesFolder.getChildResources();
//	}

	@Override
	public String getUserURI(String userName) throws RemoteException {
		return repository.getWebdavServerURI() + getUserPath(userName);
	}

	@Override
	public String getUserPath(String userName) {
		return PATH_USERS + CoreConstants.SLASH + userName;
	}

	@Override
	public String getGroupURI(String groupName) throws RemoteException {
		return repository.getWebdavServerURI() + getGroupPath(groupName);
	}

	@Override
	public String getGroupPath(String groupName) throws RemoteException {
		return PATH_GROUPS + CoreConstants.SLASH + groupName;
	}

	@Override
	public String getRoleURI(String roleName) throws RemoteException {
		return repository.getWebdavServerURI() + getRolePath(roleName);
	}

	@Override
	public String getRolePath(String roleName) throws RemoteException {
		return PATH_ROLES + CoreConstants.SLASH + roleName;
	}

	/**
	 * @param loginName
	 * @param roleNamesForUser
	 * @param loginNameOfAllLoggedOnUsers
	 *            Set of all users that are logged on, other users are removed from roles. If the
	 *            set is null no users are removed from roles.
	 * @throws IOException
	 * @throws RemoteException
	 * @throws HttpException
	 */
	@Override
	public void updateRoleMembershipForUser(String userLoginName, Set<String> roleNamesForUser, Set<String> loginNamesOfAllLoggedOnUsers)
		throws RemoteException, IOException {

		if (userLoginName != null && userLoginName.length() > 0 && !userLoginName.equals(SLIDE_DEFAULT_ROOT_USER)) {
//			Set<String> newRoles = new HashSet<String>(roleNamesForUser);
//			Enumeration e = getAllRoles(rCredentials).getResources();
//			String userURI = getUserURI(userLoginName);
//			while (e.hasMoreElements()) {
//				WebdavResource role = (WebdavResource) e.nextElement();
//				newRoles.remove(role.getDisplayName());
//				updateRoleMembershipForUser(role, userURI, roleNamesForUser, loginNamesOfAllLoggedOnUsers);
//			}
//
//			// Add Roles that don't exist
//			for (Iterator iter = newRoles.iterator(); iter.hasNext();) {
//				String sRole = (String) iter.next();
//
//				if (!service.getExistence(getRolePath(sRole))) {
//					WebdavResource newRole = new WebdavResource(service.getWebdavServerURL(rCredentials, getRolePath(sRole)), WebdavResource.NOACTION, 0);
//					newRole.mkcolMethod();
//					updateRoleMembershipForUser(newRole, userURI, roleNamesForUser, loginNamesOfAllLoggedOnUsers);
//					newRole.close();
//				}
//			}
		}
	}

//	private void updateRoleMembershipForUser(WebdavResource role, String userURI, Set roleNamesForUser, Set userpathsOfAllLoggedOnUsers)
//		throws RemoteException, IOException {
//
//		// System.out.println("[AuthenticationBusiness]: resouce "+role.getDisplayName()+" begins");
//		boolean someChanges = false;
//		try {
//			Enumeration e = role.propfindMethod(GROUP_MEMBER_SET);
//			String propertyString = "";
//			while (e.hasMoreElements()) {
//				propertyString += (String) e.nextElement();
//			}
//			// System.out.println("\t[group-member-set1]: "+propertyString);
//
//			Set userSet = parseGroupMemberSetPropertyString(propertyString);
//
//			if (userpathsOfAllLoggedOnUsers != null) {
//				String rootUser = getUserURI(SLIDE_DEFAULT_ROOT_USER);
//				for (Iterator iter = userSet.iterator(); iter.hasNext();) {
//					String token = (String) iter.next();
//					if (!rootUser.equals(token)
//					        && !userpathsOfAllLoggedOnUsers.contains(token)) {
//						userSet.remove(token);
//						someChanges = true;
//					}
//				}
//			}
//
//			boolean userIsInRole = userSet.contains(userURI);
//			boolean userShouldBeInRole = SLIDE_ROLE_NAME_USER.equals(role
//			        .getDisplayName())
//			        || roleNamesForUser.contains(role.getDisplayName());
//
//			if (!userIsInRole && userShouldBeInRole) {
//				userSet.add(userURI);
//				someChanges = true;
//			} else if (userIsInRole && !userShouldBeInRole) {
//				userSet.remove(userURI);
//				someChanges = true;
//			}
//
//			if (someChanges) {
//				String newGroupMemberSet = encodeGroupMemberSetPropertyString(userSet);
//
//				role.proppatchMethod(GROUP_MEMBER_SET_PROPERTY_NAME,
//				    newGroupMemberSet, true);
//
//				// Enumeration e2 = role.propfindMethod(GROUP_MEMBER_SET);
//				// if (e2.hasMoreElements()) {
//				// String element2 = (String) e2.nextElement();
//				// System.out.println("\t[group-member-set2]: "+element2);
//				// }
//			}
//		} catch (HttpException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * @param userSet
	 *            Set of userpaths or grouppaths
	 * @see getUserURI(String)
	 * @see getGroupURI(String)
	 * @return
	 */
	private String encodeGroupMemberSetPropertyString(Set<String> userOrGroupSet) {
		return userOrGroupSet.toString();//PropertyParser.encodePropertyString(null, userOrGroupSet);
	}

	/**
	 * @param element
	 * @return
	 * @throws RemoteException
	 */
	private Set<String> parseGroupMemberSetPropertyString(String propertyString) throws RemoteException {
		Set<String> prop = new HashSet<String>();
		prop.add(propertyString);
		return prop;//PropertyParser.parsePropertyString(null, propertyString);
	}

	@Override
	public boolean isRootUser(HttpServletRequest request) {
		LoginBusinessBean loginBusiness = getLoginBusiness();
		String[] usernameAndPassword = loginBusiness.getLoginNameAndPasswordFromBasicAuthenticationRequest(request);
		if (ArrayUtil.isEmpty(usernameAndPassword) || usernameAndPassword.length < 2) {
			return false;
		}

		Credentials tmpCredentials = getRootUserCredentials();
		if (tmpCredentials instanceof SimpleCredentials) {
			SimpleCredentials cred = (SimpleCredentials) tmpCredentials;
			return (usernameAndPassword[0].equals(cred.getUserID()) || ROOT_USER_NAME.equals(cred.getUserID()));/* &&
					(usernameAndPassword[1].equals(cred.getPassword()) || CoreConstants.EMPTY.equals(cred.getPassword()));*/
			//tmpCredential.getUserName().equals(usernameAndPassword[0]) && tmpCredential.getPassword().equals(usernameAndPassword[1]);
		}
		return false;
	}

	protected LoginBusinessBean getLoginBusiness() {
		return LoginBusinessBean.getLoginBusinessBean(getApplication().getIWApplicationContext());
	}

	@Override
	public AccessControlList applyDefaultPermissionsToRepository(AccessControlList acl) {
		return applyPermissionsToRepository(acl, StandardRoles.ALL_STANDARD_ROLES);
	}

	@Override
	public AccessControlList applyPermissionsToRepository(AccessControlList acl, Collection<String> roles) {
		try {
			for (String role : roles) {
				String roleUri = getRoleURI(role);
				getLogger().info("Set role " + roleUri + " for " + acl);
				//	TODO!!!

//				Ace newAce = new Ace(roleUri);
//				newAce.addPrivilege(Privilege.READ);
//				newAce.addPrivilege(Privilege.WRITE);

//				AccessControlEntry editorEntry = new AccessControlEntry(newAce, AccessControlEntry.PRINCIPAL_TYPE_ROLE);
//				acl.add(editorEntry);
			}

		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Exception while applying roles permissions to repo, roles=" + roles + ", repo path=" + acl, e);
			return null;
		}

		return acl;
	}
}