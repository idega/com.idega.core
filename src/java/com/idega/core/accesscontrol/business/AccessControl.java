/*
 * $Id: AccessControl.java,v 1.129 2009/06/11 07:17:00 valdas Exp $
 * Created in 2001
 *
 * Copyright (C) 2001-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.dao.PermissionDAO;
import com.idega.core.accesscontrol.dao.UserLoginDAO;
import com.idega.core.accesscontrol.dao.UsernameExistsException;
import com.idega.core.accesscontrol.data.ICPermissionHome;
import com.idega.core.accesscontrol.data.ICRoleHome;
import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.accesscontrol.data.bean.PermissionGroup;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.dao.ICPageDAO;
import com.idega.core.builder.data.ICDynamicPageTrigger;
import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.component.dao.ICObjectDAO;
import com.idega.core.component.data.bean.ICObject;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.idgenerator.business.UUIDBusiness;
import com.idega.core.persistence.Param;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWServiceImpl;
import com.idega.idegaweb.IWServiceNotStartedException;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.ImplementorRepository;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.user.business.GroupBusiness;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.GroupHome;
import com.idega.user.data.UserHome;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.GroupRelation;
import com.idega.user.data.bean.GroupRelationType;
import com.idega.user.data.bean.GroupType;
import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.reflect.FieldAccessor;

/**
 * <p>
 * This is the standard implementation of the AccessController interface and the standard class for handling
 * access control information (with ICPermission) in idegaWeb.
 * </p>
 *
 * Last modified: $Date: 2009/06/11 07:17:00 $ by $Author: valdas $
 *
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson </a>,
 *         Eirikur Hrafnsson, Tryggvi Larusson
 *
 * @version $Revision: 1.129 $
 */
public class AccessControl extends IWServiceImpl implements AccessController {

	private PermissionGroup AdministratorPermissionGroup = null;
	private PermissionGroup PermissionGroupEveryOne = null;
	private PermissionGroup PermissionGroupUsers = null;
	private List<Group> standardGroups = null;

	private static final String _APPADDRESS_ADMINISTRATOR_USER = "ic_super_admin";

	private static final String PROPERTY_USERS_GROUP_ID = "accesscontrol.users.id";
	private static final String PROPERTY_EVERYONE_GROUP_ID = "accesscontrol.everyone.id";

	public static final int _GROUP_ID_EVERYONE = -7913;
	public static final int _GROUP_ID_USERS = -1906;

	private static final int _notBuilderPageID = -1;
	private PermissionCacher permissionCacher;

	private static ICObject staticPageICObject = null;
	private static ICObject staticFileICObject = null;
	private List<String> rolesList;

	@Autowired
	UserLoginDAO userLoginDAO;

	@Autowired
	GroupDAO groupDAO;

	@Autowired
	UserDAO userDAO;

	@Autowired
	PermissionDAO permissionDAO;

	@Autowired
	ICPageDAO pageDAO;

	@Autowired
	ICObjectDAO objectDAO;

	@Override
	public Integer getUsersGroupID() {
		String key = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(PROPERTY_USERS_GROUP_ID, String.valueOf(_GROUP_ID_USERS));
		if (key != null) {
			return new Integer(key);
		}
		return null;
	}

	@Override
	public Integer getEveryoneGroupID() {
		String key = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(PROPERTY_EVERYONE_GROUP_ID, String.valueOf(_GROUP_ID_EVERYONE));
		if (key != null) {
			return new Integer(key);
		}
		return null;
	}

  protected Logger getLogger(){
  	return Logger.getLogger(this.getClass().getName());
  }

  private PermissionCacher getPermissionCacher(){
		if(this.permissionCacher==null){
			this.permissionCacher=new PermissionCacher();
		}
		return this.permissionCacher;

	}

	//This method has to exist static because of many callers are static
	private static PermissionCacher getPermissionCacherStatic(){

		AccessController instance = IWMainApplication.getDefaultIWMainApplication().getAccessController();
		return ((AccessControl)instance).getPermissionCacher();

	}

	private void initAdministratorPermissionGroup() {
		PermissionGroup permission = getPermissionDAO().getPermissionGroup(AccessControl.getAdministratorGroupName());
		if (permission != null) {
			permission = getPermissionDAO().createPermissionGroup(AccessControl.getAdministratorGroupName(), "Administrator permission");
		}
		this.AdministratorPermissionGroup = permission;
	}

	private void initPermissionGroupEveryone() {
		PermissionGroup permission = getPermissionDAO().findPermissionGroup(getEveryoneGroupID());
		if (permission == null) {
			permission = getPermissionDAO().createPermissionGroup("Everyone", "Permission if not logged on");
			IWMainApplication.getDefaultIWMainApplication().getSettings().setProperty(PROPERTY_EVERYONE_GROUP_ID, permission.getID().toString());
		}
		this.PermissionGroupEveryOne = permission;
	}

	private void initPermissionGroupUsers() {
		PermissionGroup permission = getPermissionDAO().findPermissionGroup(getUsersGroupID());
		if (permission == null) {
			permission = getPermissionDAO().createPermissionGroup("Users", "Permission if logged on");
			IWMainApplication.getDefaultIWMainApplication().getSettings().setProperty(PROPERTY_USERS_GROUP_ID, permission.getID().toString());
		}
		this.PermissionGroupUsers = permission;
	}

	@Override
	public PermissionGroup getPermissionGroupEveryOne() throws Exception {
		if (this.PermissionGroupEveryOne == null) {
			initPermissionGroupEveryone();
		}
		return this.PermissionGroupEveryOne;
	}

	@Override
	public PermissionGroup getPermissionGroupUsers() {
		if (this.PermissionGroupUsers == null) {
			initPermissionGroupUsers();
		}
		return this.PermissionGroupUsers;
	}

	@Override
	public PermissionGroup getPermissionGroupAdministrator() {
		if (this.AdministratorPermissionGroup == null) {
			initAdministratorPermissionGroup();
		}
		return this.AdministratorPermissionGroup;
	}

	@Override
	public boolean isAdmin(IWUserContext iwc) throws Exception {
		try {
			Object ob = LoginBusinessBean.getLoginAttribute(getAdministratorGroupName(), iwc);
			if (ob instanceof Boolean) {
				return ((Boolean) ob).booleanValue();
			} else {
				User admin = getAdministratorUser();
				User currentUser = LoginBusinessBean.getUser(iwc);
				if (admin != null && currentUser != null && admin.getId().intValue() == currentUser.getId().intValue()) {
					LoginBusinessBean.setLoginAttribute(getAdministratorGroupName(), Boolean.TRUE, iwc);
					return true;
				}
				
				List<Group> groups = LoginBusinessBean.getPermissionGroups(iwc);
				if (groups != null) {
					for (Iterator<Group> iter = groups.iterator(); iter.hasNext();) {
						Group item = iter.next();
						if (getAdministratorGroupName().equals(item.getName())) {
							LoginBusinessBean.setLoginAttribute(getAdministratorGroupName(), Boolean.TRUE, iwc);
							return true;
						}
					}
				}
			}
			LoginBusinessBean.setLoginAttribute(getAdministratorGroupName(), Boolean.FALSE, iwc);
			return false;
		} catch (NotLoggedOnException ex) {
			return false;
		}
	}

	/**
	 * @todo page ownership
	 */
	@Override
	public boolean isOwner(Object obj, IWUserContext iwc) throws Exception {
		Boolean returnVal = Boolean.FALSE;
		if (iwc.isLoggedOn()) {
			User user = iwc.getLoggedInUser();

			List<String>[] permissionOrder = new ArrayList[2];
			permissionOrder[0] = new ArrayList<String>();
			permissionOrder[0].add(user.getId().toString());
			permissionOrder[1] = new ArrayList<String>();
			permissionOrder[1].add(user.getPrimaryGroup().getID().toString());

			returnVal = checkForPermission(permissionOrder, obj, AccessController.PERMISSION_KEY_OWNER, iwc);
		}

		if (returnVal != null) {
			return returnVal.booleanValue();
		}
		else {
			return false;
		}

	}

	public boolean isOwner(int category, String identifier, IWUserContext iwc) throws Exception {
		Boolean returnVal = Boolean.FALSE;
		if (iwc.isLoggedOn()) {
			User user = iwc.getLoggedInUser();

			List<String>[] permissionOrder = new ArrayList[2];
			permissionOrder[0] = new ArrayList<String>();
			permissionOrder[0].add(user.getId().toString());
			permissionOrder[1] = new ArrayList<String>();
			permissionOrder[1].add(user.getPrimaryGroup().getID().toString());

			returnVal = checkForPermission(permissionOrder, category, identifier, AccessController.PERMISSION_KEY_OWNER,  IWMainApplication.getDefaultIWApplicationContext());
		}

		if (returnVal != null) {
			return returnVal.booleanValue();
		}
		else {
			return false;
		}
	}

	public boolean isOwner(List groupIds, Object obj, IWUserContext iwc) throws Exception {
		Boolean returnVal = Boolean.FALSE;
		List[] permissionOrder = new ArrayList[1];
		permissionOrder[0] = groupIds;
		returnVal = checkForPermission(permissionOrder, obj, AccessController.PERMISSION_KEY_OWNER, iwc);

		if (returnVal != null) {
			return returnVal.booleanValue();
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isOwner(ICFile file, IWUserContext iwc) throws Exception {
		return isOwner(AccessController.CATEGORY_FILE_ID, file.getId().toString(), iwc);
	}

	@Override
	public boolean isOwner(Group group, IWUserContext iwc) throws Exception {
		return isOwner(AccessController.CATEGORY_GROUP_ID, group.getID().toString(), iwc);
	}

	public boolean isGroupOwnerRecursively(Group group, IWUserContext iwc) throws Exception {
		boolean value = isOwner(group, iwc);

		if (!value) { //check parents to see if user is an owner of them
			Collection parents = getGroupDAO().getParentGroups(group); //little at at time not all groups recursive

			if (parents != null && !parents.isEmpty()) {
				Iterator parentIter = parents.iterator();
				while (parentIter.hasNext() && !value) {
					Group parent = (Group) parentIter.next();
					value = isGroupOwnerRecursively(parent, iwc);
				}

				return value;

			}
			else {
				return false; //ran out of parents to check
			}

		}
		else {
			return true;
		}

	}

	@Override
	public boolean isOwner(ICPage page, IWUserContext iwc) throws Exception {
		return isOwner(AccessController.CATEGORY_PAGE_INSTANCE, Integer.toString(page.getID()), iwc);
	}

	/**
	 * @todo implement isOwner(ICObject obj, int entityRecordId, IWUserContext iwc)throws Exception
	 */
	@Override
	public boolean isOwner(ICObject obj, int entityRecordId, IWUserContext iwc) throws Exception {
		return false;
	}

	/**
	 * use this method when writing to database to avoid errors in database. If the name-string changes this will be the only method to change.
	 */
	public static String getAdministratorGroupName() {
		return "administrator";
	}

	public GroupBusiness getGroupBusiness(IWApplicationContext iwac) {
		try {
			return com.idega.business.IBOLookup.getServiceInstance(iwac, GroupBusiness.class);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}

		return null;
	}



	/**
	 *
	 * @deprecated only used in idegaWeb Project removed in next major version
	 */
	@Deprecated
	public boolean hasPermission(String permissionKey, int category, String identifier, IWUserContext iwuc) throws Exception {
		Boolean myPermission = null;
		// Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked

		if (isAdmin(iwuc)) {
			return true;
		}

		User user = LoginBusinessBean.getUser(iwuc);

		Collection<Group> groups = null;
		List[] permissionOrder = null; // Everyone, users, user, primaryGroup, otherGroups

		if (user == null) {
			permissionOrder = new List[1];
			permissionOrder[0] = new ArrayList();
			permissionOrder[0].add(Integer.toString(getPermissionGroupEveryOne().getID()));
		}
		else {

			String recurseParents = getRecurseParentsSettings(iwuc.getApplicationContext());
			if ( !"true".equalsIgnoreCase(recurseParents) ) { //old crap
				//			TODO Eiki remove this old crap, one should not recurse the parents! Done in more places
				groups = LoginBusinessBean.getPermissionGroups(iwuc);
			}
			else { //the correct version
				groups = getParentGroupsAndPermissionControllingParentGroups(permissionKey, iwuc.getLoggedInUser());

			}

			Group primaryGroup = LoginBusinessBean.getPrimaryGroup(iwuc);

			if (groups != null && !groups.isEmpty()) {
				if (primaryGroup != null) {
					groups.remove(primaryGroup);
				}
				List<String> groupIds = new ArrayList<String>();
				Iterator<Group> iter = groups.iterator();
				while (iter.hasNext()) {
					groupIds.add(Integer.toString(iter.next().getID()));
				}
				permissionOrder = new List[5];
				permissionOrder[4] = groupIds;
			}
			else {
				permissionOrder = new List[4];
			}
			permissionOrder[0] = new ArrayList();
			permissionOrder[0].add(Integer.toString(getPermissionGroupEveryOne().getID()));
			permissionOrder[1] = new ArrayList();
			permissionOrder[1].add(Integer.toString(getPermissionGroupUsers().getID()));
			permissionOrder[2] = new ArrayList();
			permissionOrder[2].add(Integer.toString(user.getId()));
			permissionOrder[3] = new ArrayList();
			permissionOrder[3].add(Integer.toString(user.getPrimaryGroup().getID()));
			// Everyone, user, primaryGroup, otherGroups
		}
		myPermission = checkForPermission(permissionOrder, category, identifier, permissionKey,  IWMainApplication.getDefaultIWApplicationContext());
		if (myPermission != null) {
			return myPermission.booleanValue();
		}

		if (permissionKey.equals(AccessController.PERMISSION_KEY_EDIT) || permissionKey.equals(AccessController.PERMISSION_KEY_VIEW)) {
			return isOwner(category, identifier, iwuc);
		}
		else {
			return false;
		}

	}

	private static Boolean checkForPermission(List[] permissionGroupLists, int category, String identifier, String permissionKey, IWApplicationContext iwc)
		throws Exception {
		Boolean myPermission = null;
		if (permissionGroupLists != null) {
			int arrayLength = permissionGroupLists.length;
			switch (category) {
				case AccessController.CATEGORY_OBJECT_INSTANCE :
				case AccessController.CATEGORY_OBJECT :
				case AccessController.CATEGORY_BUNDLE :
				case AccessController.CATEGORY_PAGE_INSTANCE :
				case AccessController.CATEGORY_PAGE :
					//PageInstance
					if (category == AccessController.CATEGORY_PAGE_INSTANCE && !identifier.equals(Integer.toString(_notBuilderPageID))) {
						for (int i = 0; i < arrayLength; i++) {
							myPermission = getPermissionCacherStatic().hasPermissionForPage(identifier, iwc, permissionKey, permissionGroupLists[i]);
							if (myPermission != null) {
								return myPermission;
							}
						}

						if (!permissionKey.equals(AccessController.PERMISSION_KEY_OWNER)) {
							// Global - (Page)
							if (!getPermissionCacherStatic().anyInstancePermissionsDefinedForPage(identifier, iwc, permissionKey)) {
								ICObject page = getStaticPageICObject();
								if (page != null) {
									for (int i = 0; i < arrayLength; i++) {
										myPermission = getPermissionCacherStatic().hasPermission(page, iwc, permissionKey, permissionGroupLists[i]);
										if (myPermission != null) {
											return myPermission;
										}
									}
								}
							}
							// Global - (Page)
						}

						return myPermission;
					}
					else {
						//instance
						for (int i = 0; i < arrayLength; i++) {
							myPermission = getPermissionCacherStatic().hasPermissionForObjectInstance(identifier, iwc, permissionKey, permissionGroupLists[i]);
							if (myPermission != null) {
								return myPermission;
							}
						}
						//instance

						if (!permissionKey.equals(AccessController.PERMISSION_KEY_OWNER)) {
							// Global - (object)
							if (!getPermissionCacherStatic().anyInstancePermissionsDefinedForObject(identifier, iwc, permissionKey)) {
								for (int i = 0; i < arrayLength; i++) {
									myPermission = getPermissionCacherStatic().hasPermissionForObject(identifier, iwc, permissionKey, permissionGroupLists[i]);
									if (myPermission != null) {
										return myPermission;
									}
								}
							}
							// Global - (object)
						}

						return myPermission;
					}
				case AccessController.CATEGORY_JSP_PAGE :
					for (int i = 0; i < arrayLength; i++) {
						myPermission = getPermissionCacherStatic().hasPermissionForJSPPage(identifier, iwc, permissionKey, permissionGroupLists[i]);
						if (myPermission != null) {
							return myPermission;
						}
					}

					return myPermission;
				case AccessController.CATEGORY_FILE_ID :
					for (int i = 0; i < arrayLength; i++) {
						myPermission = getPermissionCacherStatic().hasPermissionForFile(identifier, iwc, permissionKey, permissionGroupLists[i]);
						if (myPermission != null) {
							return myPermission;
						}
					}

					if (!permissionKey.equals(AccessController.PERMISSION_KEY_OWNER)) {
						// Global - (File)
						if (!getPermissionCacherStatic().anyInstancePermissionsDefinedForFile(identifier, iwc, permissionKey)) {
							ICObject file = getStaticFileICObject();
							if (file != null) {
								for (int i = 0; i < arrayLength; i++) {
									myPermission = getPermissionCacherStatic().hasPermission(file, iwc, permissionKey, permissionGroupLists[i]);
									if (myPermission != null) {
										return myPermission;
									}
								}
							}
						}
						// Global - (File)
					}

					return myPermission;

				case AccessController.CATEGORY_GROUP_ID :
					for (int i = 0; i < arrayLength; i++) {
						myPermission = getPermissionCacherStatic().hasPermissionForGroup(identifier, iwc, permissionKey, permissionGroupLists[i]);
						if (myPermission != null) {
							return myPermission;
						}
					}
			} //switch ends
		}
		return myPermission;
	}

	/**
	 * The main hasPermission method all hasXYZPesmission methodscall this method.
	 *
	 * @see com.idega.core.accesscontrol.business.AccessController#hasPermission(String, PresentationObject, IWUserContext)
	 */
	@Override
	public boolean hasPermission(String permissionKey, Object obj, IWUserContext iwuc) throws Exception {
		Boolean myPermission = null;
		// Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked

		if (isAdmin(iwuc)) { //this is almost a security hole - eiki
			return true;
		}

		User user = LoginBusinessBean.getUser(iwuc);

		Collection<Group> groups = null;
		//The order that is checked for : Everyone Group, Logged on users group, user, primaryGroup, otherGroups
		//This is an ordered list to check against the permissions set in the database
		List<String>[] usersGroupsToCheckAgainstPermissions = null;
		//

		if (user == null) { //everyone group check
			usersGroupsToCheckAgainstPermissions = new List[1];
			usersGroupsToCheckAgainstPermissions[0] = new ArrayList<String>();
			usersGroupsToCheckAgainstPermissions[0].add(Integer.toString(getPermissionGroupEveryOne().getID()));
		}
		else { //user check

			String recurseParents = getRecurseParentsSettings(iwuc.getApplicationContext());
			if ( !Boolean.TRUE.toString().equalsIgnoreCase(recurseParents) )  { //old crap
				//TODO Eiki remove this old crap, one should not recurse the parents! Done in more places
				groups = LoginBusinessBean.getPermissionGroups(iwuc);
			}
			else { //the correct version
				groups = getParentGroupsAndPermissionControllingParentGroups(permissionKey, iwuc.getLoggedInUser());
			}

			Group primaryGroup = LoginBusinessBean.getPrimaryGroup(iwuc);

			if (!ListUtil.isEmpty(groups)) {
				if (primaryGroup != null) {
					groups.remove(primaryGroup);
				}
				List<String> groupIds = new ArrayList<String>();
				for (Object o: groups) {
					if (o instanceof Group) {
						Group group = (Group) o;
						groupIds.add(Integer.toString(group.getID()));
					}
				}

				usersGroupsToCheckAgainstPermissions = new List[5];
				usersGroupsToCheckAgainstPermissions[4] = groupIds;
			}
			else {
				usersGroupsToCheckAgainstPermissions = new List[4];
			}
			usersGroupsToCheckAgainstPermissions[0] = new ArrayList<String>();
			usersGroupsToCheckAgainstPermissions[0].add(Integer.toString(getPermissionGroupEveryOne().getID()));
			usersGroupsToCheckAgainstPermissions[1] = new ArrayList<String>();
			usersGroupsToCheckAgainstPermissions[1].add(Integer.toString(getPermissionGroupUsers().getID()));
			usersGroupsToCheckAgainstPermissions[2] = new ArrayList<String>();
			usersGroupsToCheckAgainstPermissions[2].add(Integer.toString(user.getId()));
			usersGroupsToCheckAgainstPermissions[3] = new ArrayList<String>();

			Group userPrimaryGroup = user.getPrimaryGroup();
			if (userPrimaryGroup != null) {
				usersGroupsToCheckAgainstPermissions[3].add(Integer.toString(userPrimaryGroup.getID()));
			} else {
				getLogger().warning(user + " (ID: " + user.getId() + ") does not have primary group!");
			}
			// ([0])Everyone, ([1])users, ([2])user, ([3])primaryGroup, ([4])otherGroups
		}

		myPermission = checkForPermission(usersGroupsToCheckAgainstPermissions, obj, permissionKey,  iwuc);

		boolean hasPermission = false;
		if (myPermission != null) {
			hasPermission = myPermission.booleanValue();
			if (hasPermission) {
				return true;
			}
		}

		//if the user is an owner these rights are given. double checking really
		if (permissionKey.equals(AccessController.PERMISSION_KEY_EDIT) || permissionKey.equals(AccessController.PERMISSION_KEY_VIEW)) {
			if (obj instanceof Group) {
				return isGroupOwnerRecursively((Group) obj, iwuc); //because owners parents groups always get read/write access
			}
			else {
				return isOwner(obj, iwuc);
			}
		}
		else {
			return false;
		}

	} // method hasPermission

	private Collection<Group> getParentGroupsAndPermissionControllingParentGroups(String permissionKey, User user) throws RemoteException {
		Collection<Group> groups = getGroupDAO().getParentGroups(user.getUserRepresentative()); //com.idega.user.data.User

		List<Group> groupsToCheckForPermissions = new ArrayList<Group>();
		if (!ListUtil.isEmpty(groups)) {
			for (Iterator<Group> iter = groups.iterator(); iter.hasNext();) {
				Group parent = iter.next();
				Group permissionControllingParentGroup = parent.getPermissionControllingGroup();
				if (!AccessController.PERMISSION_KEY_OWNER.equals(permissionKey) && parent!=null && permissionControllingParentGroup != null) {
					groupsToCheckForPermissions.add(permissionControllingParentGroup);
				}
			}
		}

		if (groups != null && groupsToCheckForPermissions != null) {
			groups.addAll(groupsToCheckForPermissions);
		}

		return groups;
	}

	/**
	 * Assembles the grouplist for this user to check agains the permission maps in memory.
	 *
	 * @see com.idega.core.accesscontrol.business.AccessController#hasPermission(List, String, PresentationObject, IWUserContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasPermission(List<Integer> groupIds, String permissionKey, Object obj, IWUserContext iwc) throws Exception {
		Boolean myPermission = null;
		// Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked
		//TODO Eiki make one universal haspermission method
		List<String>[] permissionOrder = null; // Everyone, users, (primaryGroup), otherGroups

		List<String> tmp = null;
		if (groupIds != null) {
			tmp = new ArrayList<String>(groupIds.size());
			for (Integer id: groupIds)
				tmp.add(id.toString());
		}

		if (groupIds != null) {
			if (groupIds.contains(Integer.toString(getPermissionGroupAdministrator().getID()))) {
				return true;
			}
			else {
				if (groupIds.size() == 1) {
					if (groupIds.get(0).equals(Integer.toString(getEveryoneGroupID()))) {
						permissionOrder = new List[1];
						permissionOrder[0] = new ArrayList<String>();
						permissionOrder[0].add(Integer.toString(getPermissionGroupEveryOne().getID()));
					}
					else {
						if (groupIds.get(0).equals(Integer.toString(getUsersGroupID()))) {
							permissionOrder = new List[2];
						}
						else {
							permissionOrder = new List[3];
							permissionOrder[2] = tmp;
						}
						permissionOrder[0] = new ArrayList<String>();
						permissionOrder[0].add(Integer.toString(getPermissionGroupEveryOne().getID()));
						permissionOrder[1] = new ArrayList<String>();
						permissionOrder[1].add(Integer.toString(getPermissionGroupUsers().getID()));
					}
				}
				else if (groupIds.size() > 1) {
					permissionOrder = new List[3];
					permissionOrder[0] = new ArrayList<String>();
					permissionOrder[0].add(Integer.toString(getPermissionGroupEveryOne().getID()));
					permissionOrder[1] = new ArrayList<String>();
					permissionOrder[1].add(Integer.toString(getPermissionGroupUsers().getID()));
					permissionOrder[2] = tmp;
					// Everyone, users, (primaryGroup), otherGroups
				}
				else {
					return false;
				}
			}
		}
		else {
			return false;
		}
		myPermission = checkForPermission(permissionOrder, obj, permissionKey, iwc);

		boolean hasPermission = false;
		if (myPermission != null) {
			hasPermission = myPermission.booleanValue();
			if (hasPermission) {
				return true;
			}
		}

		if (permissionKey.equals(AccessController.PERMISSION_KEY_EDIT) || permissionKey.equals(AccessController.PERMISSION_KEY_VIEW)) {
			if (obj instanceof Group) {
				return isGroupOwnerRecursively((Group) obj, iwc); //because owners parents groups always get read/write access
			}
			else {
				return isOwner(obj, iwc);
			}
		}
		else {
			return false;
		}

	} // method hasPermission

	private static Boolean checkForPermission(List<String>[] permissionGroupLists, Object obj, String permissionKey, IWUserContext iwc) throws Exception {
		Boolean myPermission = Boolean.FALSE;
		if (permissionGroupLists != null) {
			int arrayLength = permissionGroupLists.length;
			IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
			//JSP PAGE
			if (obj == null) {
				for (int i = 0; i < arrayLength; i++) {
					myPermission = getPermissionCacherStatic().hasPermissionForJSPPage(iwac, permissionKey, permissionGroupLists[i]);
					if (Boolean.TRUE.equals(myPermission)) {
						return myPermission;
					}
				}

				return myPermission;
			}
			//JSP PAGE ENDS

			//PAGE
			if ((obj instanceof Page && ((Page) obj).getPageID() != _notBuilderPageID) || (obj instanceof PagePermissionObject)){
				//builder users should always get edit/view permission on pages:
				if(IWMainApplication.getDefaultIWMainApplication().getAccessController().hasRole(StandardRoles.ROLE_KEY_BUILDER,iwc)){
					return Boolean.TRUE;
				}

				for (int i = 0; i < arrayLength; i++) {
					myPermission = getPermissionCacherStatic().hasPermissionForPage(obj, iwac, permissionKey, permissionGroupLists[i]);
					if (Boolean.TRUE.equals(myPermission)) {
						return myPermission;
					}
				}

				if (!permissionKey.equals(AccessController.PERMISSION_KEY_OWNER)) {
					// Global - (Page)
					boolean noPermissionsSet = !getPermissionCacherStatic().anyInstancePermissionsDefinedForPage(obj, iwac, permissionKey);
					if(noPermissionsSet){
						ICObject page = getStaticPageICObject();
						if (page != null) {
							for (int i = 0; i < arrayLength; i++) {
								myPermission = getPermissionCacherStatic().hasPermission(page, iwac, permissionKey, permissionGroupLists[i]);
								if (Boolean.TRUE.equals(myPermission)) {
									return myPermission;
								}
							}
						}
					}
					// Global - (Page)
				}

				return myPermission;
			} //PAGE ENDS
			else if (obj instanceof Group) { // Group checking
				for (int i = 0; i < arrayLength; i++) {
					myPermission = getPermissionCacherStatic().hasPermissionForGroup((Group) obj, iwac, permissionKey, permissionGroupLists[i]);

					if (Boolean.TRUE.equals(myPermission)) {
						return myPermission;
					}
				}

			}
			else if(obj instanceof RoleHelperObject){
				for (int i = 0; i < arrayLength; i++) {
					myPermission = getPermissionCacherStatic().hasPermissionForRole( ((RoleHelperObject) obj).toString(), iwac, permissionKey, permissionGroupLists[i]);

					if (Boolean.TRUE.equals(myPermission)) {
						return myPermission;
					}
				}

			}
			else {
				//editor users should always get edit/view permission on pages:
				if(iwc.getApplicationContext().getIWMainApplication().getAccessController().hasRole(StandardRoles.ROLE_KEY_EDITOR,iwc)){
					return Boolean.TRUE;
				}
				//author users should always get edit/view permission on pages:
				else if(iwc.getApplicationContext().getIWMainApplication().getAccessController().hasRole(StandardRoles.ROLE_KEY_AUTHOR,iwc)){
					return Boolean.TRUE;
				}

				//Object instance
				myPermission=Boolean.FALSE; //TODO: should be set to null and the haspermissionForObjectInstance should return null if there is no permission set and then later return false if still null, because permission could be stored as false permission
				for (int i = 0; i < arrayLength; i++) {
					myPermission = getPermissionCacherStatic().hasPermissionForObjectInstance((PresentationObject) obj, iwac, permissionKey, permissionGroupLists[i]);
					if (Boolean.TRUE.equals(myPermission)) {
						return myPermission;
					}
				}
				//instance

				//Template object instance permission.  Used for DynamicPageTrigger
				if(Boolean.FALSE.equals(myPermission)) { //TODO: should check if myPermission is null when the todo above has been changed (myPermission should be set null there)
					PresentationObject currentObject = (PresentationObject)obj; //TODO: user interfase to avoid using presentation object in business logic
					ICDynamicPageTrigger dynamicPageTrigger = (ICDynamicPageTrigger) ImplementorRepository.getInstance().newInstanceOrNull(ICDynamicPageTrigger.class, AccessControl.class);
					if (dynamicPageTrigger == null) {
						throw new RuntimeException("[AccessControl] Implementation of ICDynamicPageTrigger could not be found. Implementing bundle was not loaded.");
					}

					Boolean hasRelationToPage = dynamicPageTrigger.hasRelationTo(currentObject, permissionGroupLists, iwc);
					String templateID = currentObject.getTemplateId();
					if(Boolean.TRUE.equals(hasRelationToPage) && templateID!=null) {
						//if so, check for permission for the template object
						//TODO: it might be ncessary to remove everyone and users from permissionGroupLists
						PresentationObject templateParentObject = currentObject.getTemplateObject();
						myPermission = checkForPermission(permissionGroupLists,templateParentObject,permissionKey,iwc);
						if (Boolean.TRUE.equals(myPermission)) {
							return myPermission;
						}
					}
				}
				//Template object instance permission ends

				//          page permission inheritance
				//          if(obj.allowPagePermissionInheritance()){
				//            Page p = obj.getParentPage();
				//            if(p != null && p.getPageID() != _notBuilderPageID ){
				//              myPermission = checkForPermission(permissionGroupLists,p,permissionType,iwc);
				//              if(myPermission != null){
				//                return myPermission;
				//              }
				//            }
				//          }
				//          //page permission inheritance
				//
				if (!permissionKey.equals(AccessController.PERMISSION_KEY_OWNER)) {
					// Global - (object)
					if (!getPermissionCacherStatic().anyInstancePermissionsDefinedForObject((PresentationObject) obj, iwac, permissionKey)) {
						for (int i = 0; i < arrayLength; i++) {
							myPermission = getPermissionCacherStatic().hasPermissionForObject((PresentationObject) obj, iwac, permissionKey, permissionGroupLists[i]);
							if (Boolean.TRUE.equals(myPermission)) {
								return myPermission;
							}
						}
					}
					// Global - (object)
				}

				if(myPermission==null) {
					return Boolean.FALSE;
				}
				return myPermission;
			}
		}
		return myPermission;
	}

	//temp
	private static ICObject getStaticPageICObject() {
		if (staticPageICObject == null) {
			ICObjectDAO dao = ELUtil.getInstance().getBean(ICObjectDAO.class);
			staticPageICObject = dao.findByClass(Page.class);
		}
		return staticPageICObject;
	}

	// temp
	private static ICObject getStaticFileICObject() {
		if (staticFileICObject == null) {
			ICObjectDAO dao = ELUtil.getInstance().getBean(ICObjectDAO.class);
			staticPageICObject = dao.findByClass(ICFile.class);
		}
		return staticFileICObject;
	}

	public boolean hasEditPermission(PresentationObject obj, IWUserContext iwc) throws Exception {
		return hasPermission(PERMISSION_KEY_EDIT, obj, iwc);
	}

	@Override
	public boolean hasViewPermission(PresentationObject obj, IWUserContext iwc) {
		try {
			return hasPermission(PERMISSION_KEY_VIEW, obj, iwc);
		}
		catch (Exception ex) {
			return false;
		}
	}

	public boolean hasViewPermission(List<Integer> groupIds, PresentationObject obj, IWUserContext iwc) {
		try {
			return hasPermission(groupIds, PERMISSION_KEY_VIEW, obj, iwc);
		}
		catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void setJSPPagePermission(IWUserContext iwc, Group group, String PageContextValue, String permissionType, Boolean permissionValue) throws Exception {
		ICPermission permission = getPermissionDAO().findPermission(CATEGORY_STRING_JSP_PAGE, PageContextValue, permissionType, group);
		if (permission == null) {
			permission = getPermissionDAO().createPermission(CATEGORY_STRING_JSP_PAGE, PageContextValue, group, permissionType, permissionValue);
		}
		else {
			permission.setPermissionValue(permissionValue);
			getPermissionDAO().merge(permission);
		}
		getPermissionCacher().updateJSPPagePermissions(PageContextValue, permissionType, IWMainApplication.getDefaultIWApplicationContext());
	}

	@Override
	public void setObjectPermission(IWUserContext iwc, Group group, PresentationObject obj, String permissionType, Boolean permissionValue) throws Exception {
		ICPermission permission = getPermissionDAO().findPermission(CATEGORY_STRING_IC_OBJECT_ID, String.valueOf(obj.getICObjectID()), permissionType, group);
		if (permission == null) {
			permission = getPermissionDAO().createPermission(CATEGORY_STRING_IC_OBJECT_ID, String.valueOf(obj.getICObjectID()), group, permissionType, permissionValue);
		}
		else {
			permission.setPermissionValue(permissionValue);
			getPermissionDAO().merge(permission);
		}
		getPermissionCacher().updateObjectPermissions(Integer.toString(obj.getICObjectID()), permissionType, IWMainApplication.getDefaultIWApplicationContext());
	}

	@Override
	public void setBundlePermission(IWUserContext iwc, Group group, PresentationObject obj, String permissionType, Boolean permissionValue) throws Exception {
		ICPermission permission = getPermissionDAO().findPermission(CATEGORY_STRING_BUNDLE_IDENTIFIER, obj.getBundleIdentifier(), permissionType, group);
		if (permission == null) {
			permission = getPermissionDAO().createPermission(CATEGORY_STRING_BUNDLE_IDENTIFIER, obj.getBundleIdentifier(), group, permissionType, permissionValue);
		}
		else {
			permission.setPermissionValue(permissionValue);
			getPermissionDAO().merge(permission);
		}
		getPermissionCacher().updateBundlePermissions(obj.getBundleIdentifier(), permissionType, IWMainApplication.getDefaultIWApplicationContext());
	}

	@Override
	public void setObjectInstacePermission(
		IWUserContext iwc,
		Group group,
		PresentationObject obj,
		String permissionType,
		Boolean permissionValue)
		throws Exception {
		setObjectInstacePermission(
			iwc,
			Integer.toString(group.getID()),
			Integer.toString(obj.getICObjectInstance().getID()),
			permissionType,
			permissionValue);
	}

	public static boolean removeICObjectInstancePermissionRecords(IWUserContext iwc, String ObjectInstanceId, String permissionKey, String[] groupsToRemove) {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

		Collection<Group> groups = new ArrayList<Group>();
		if (groupsToRemove != null && groupsToRemove.length > 0) {
			for (int g = 0; g < groupsToRemove.length; g++) {
				Group group = groupDAO.findGroup(new Integer(groupsToRemove[g]));
				groups.add(group);
			}
		}
		if (!groups.isEmpty()) {
			try {
				dao.removePermissions(CATEGORY_STRING_OBJECT_INSTANCE_ID, ObjectInstanceId, permissionKey, groups);
				getPermissionCacherStatic().updateObjectInstancePermissions(ObjectInstanceId, permissionKey, IWMainApplication.getDefaultIWApplicationContext());
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else {
			return true;
		}

	}

	/**
	 * @deprecated no permission should be deleted just it set to Boolean.FALSE
	 * @param permissionCategory
	 * @param iwc
	 * @param identifier
	 * @param permissionKey
	 * @param groupsToRemove
	 * @return
	 */
	@Deprecated
	public static boolean removePermissionRecords(int permissionCategory, IWUserContext iwc, String identifier, String permissionKey, String[] groupsToRemove) {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

		Collection<Group> groups = new ArrayList<Group>();
		if (groupsToRemove != null && groupsToRemove.length > 0) {
			for (int g = 0; g < groupsToRemove.length; g++) {
				Group group = groupDAO.findGroup(new Integer(groupsToRemove[g]));
				groups.add(group);
			}
		}
		if (!groups.isEmpty()) {
			try {
				switch (permissionCategory) {
					case AccessController.CATEGORY_OBJECT_INSTANCE :
						dao.removePermissions(CATEGORY_STRING_OBJECT_INSTANCE_ID, identifier, permissionKey, groups);
						break;
					case AccessController.CATEGORY_OBJECT :
						dao.removePermissions(CATEGORY_STRING_IC_OBJECT_ID, identifier, permissionKey, groups);
						break;
					case AccessController.CATEGORY_BUNDLE :
						dao.removePermissions(CATEGORY_STRING_BUNDLE_IDENTIFIER, identifier, permissionKey, groups);
						break;
					case AccessController.CATEGORY_PAGE_INSTANCE :
						dao.removePermissions(CATEGORY_STRING_PAGE_ID, identifier, permissionKey, groups);
						break;
					case AccessController.CATEGORY_PAGE :
						dao.removePermissions(CATEGORY_STRING_PAGE, identifier, permissionKey, groups);
						break;
					case AccessController.CATEGORY_JSP_PAGE :
						dao.removePermissions(CATEGORY_STRING_JSP_PAGE, identifier, permissionKey, groups);
						break;
				}

				getPermissionCacherStatic().updatePermissions(permissionCategory, identifier, permissionKey, IWMainApplication.getDefaultIWApplicationContext());

				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public ICPermission setPermission(int permissionCategory, IWApplicationContext iwac, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue) throws Exception {
		Group group = getGroupDAO().findGroup(new Integer(permissionGroupId));
		if (group == null) {
			getLogger().warning("Can not find group by ID: " + permissionGroupId);
			return null;
		}

		ICPermission permission = null;
		switch (permissionCategory) {
			case AccessController.CATEGORY_OBJECT_INSTANCE :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_OBJECT_INSTANCE_ID, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_OBJECT :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_IC_OBJECT_ID, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_BUNDLE :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_BUNDLE_IDENTIFIER, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_PAGE_INSTANCE :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_PAGE_ID, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_PAGE :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_PAGE, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_JSP_PAGE :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_JSP_PAGE, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_FILE_ID :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_FILE_ID, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_GROUP_ID :
				permission = getPermissionDAO().findPermission(CATEGORY_STRING_GROUP_ID, identifier, permissionKey, group);
				break;
			case AccessController.CATEGORY_ROLE :
				permission = getPermissionDAO().findPermission(RoleHelperObject.getStaticInstance().toString(), identifier, permissionKey, group);
				break;
		}

		if (permission == null) {
			String contextType = null;
			switch (permissionCategory) {
				case AccessController.CATEGORY_OBJECT_INSTANCE :
					contextType = AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID;
					break;
				case AccessController.CATEGORY_OBJECT :
					contextType = AccessController.CATEGORY_STRING_IC_OBJECT_ID;
					break;
				case AccessController.CATEGORY_BUNDLE :
					contextType = AccessController.CATEGORY_STRING_BUNDLE_IDENTIFIER;
					break;
				case AccessController.CATEGORY_PAGE_INSTANCE :
					contextType = AccessController.CATEGORY_STRING_PAGE_ID;
					break;
				case AccessController.CATEGORY_PAGE :
					contextType = AccessController.CATEGORY_STRING_PAGE;
					break;
				case AccessController.CATEGORY_JSP_PAGE :
					contextType = AccessController.CATEGORY_STRING_JSP_PAGE;
					break;
				case AccessController.CATEGORY_FILE_ID :
					contextType = AccessController.CATEGORY_STRING_FILE_ID;
					break;
				case AccessController.CATEGORY_GROUP_ID :
					contextType = AccessController.CATEGORY_STRING_GROUP_ID;
					break;
				case AccessController.CATEGORY_ROLE :
					contextType = RoleHelperObject.getStaticInstance().toString();
					break;
			}

			permission = getPermissionDAO().createPermission(contextType, identifier, group, permissionKey, permissionValue);
		} else {
			//	Updating
			permission.setPermissionValue(permissionValue);
			if (PERMISSION_KEY_OWNER.equals(permission.getPermissionString()) || PERMISSION_KEY_ROLE.equals(identifier)) {
				if (permissionValue.booleanValue()) {
					permission.setActive();
				} else {
					permission.setPassive();
				}
			}
			getPermissionDAO().merge(permission);
		}

		getPermissionCacher().updatePermissions(permissionCategory, identifier, permissionKey, iwac);
		return permission;
	}

	@Override
	public void setObjectInstacePermission(IWUserContext iwc, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue) throws Exception {
		Group group = getGroupDAO().findGroup(new Integer(permissionGroupId));

		ICPermission permission = getPermissionDAO().findPermission(CATEGORY_STRING_OBJECT_INSTANCE_ID, ObjectInstanceId, permissionType, group);
		if (permission == null) {
			getPermissionDAO().createPermission(CATEGORY_STRING_OBJECT_INSTANCE_ID, ObjectInstanceId, group, permissionType, permissionValue);
		}
		else {
			permission.setPermissionValue(permissionValue);
			getPermissionDAO().merge(permission);
		}
		getPermissionCacher().updateObjectInstancePermissions(ObjectInstanceId, permissionType, IWMainApplication.getDefaultIWApplicationContext());
	}

	@Override
	public int createPermissionGroup(String GroupName, String Description, String ExtraInfo, int[] userIDs, int[] groupIDs) throws Exception {
		PermissionGroup newGroup = getPermissionDAO().createPermissionGroup(GroupName, Description);
		if (ExtraInfo != null) {
			newGroup.setExtraInfo(ExtraInfo);
			getPermissionDAO().merge(newGroup);
		}

		int newGroupID = newGroup.getID();

		if (userIDs != null) {
			for (int i = 0; i < userIDs.length; i++) {
				addUserToPermissionGroup(newGroup, userIDs[i]);
			}
		}
		if (groupIDs != null) {
			for (int j = 0; j < groupIDs.length; j++) {
				addGroupToPermissionGroup(newGroup, groupIDs[j]);
			}
		}

		return newGroupID;

	}

	public static void addUserToPermissionGroup(Group group, int userIDtoAdd) throws Exception {
		UserDAO userDAO = ELUtil.getInstance().getBean(UserDAO.class);
		User userToAdd = userDAO.getUser(userIDtoAdd);

		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);
		GroupRelationType relationType = groupDAO.findGroupRelationType(GroupRelation.RELATION_TYPE_GROUP_PARENT);
		groupDAO.createUniqueRelation(group, userToAdd.getUserRepresentative(), relationType, null);
	}

	public static void addGroupToPermissionGroup(Group group, int groupIDtoAdd) throws Exception {
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);
		Group relatedGroup = groupDAO.findGroup(groupIDtoAdd);
		GroupRelationType relationType = groupDAO.findGroupRelationType(GroupRelation.RELATION_TYPE_GROUP_PARENT);
		groupDAO.createUniqueRelation(group, relatedGroup, relationType, null);
	}

	/**
	 * @todo implement filter to get grouptypes from property file
	 */
	private static String[] getPermissionGroupFilter() {
		//filter begin
		String[] groupsToReturn = new String[2];
		groupsToReturn[0] = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
		//TODO: This is a hack, implement better
		try {
			//groupsToReturn[1] = com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroupBMPBean.getStaticGroupInstance().getGroupTypeValue();
			groupsToReturn[1] =
				FieldAccessor.getInstance().getStaticStringFieldValue(
					RefactorClassRegistry.forName("com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroupBMPBean"),
					"GROUP_TYPE");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return groupsToReturn;
	}

	public static List<Group> getPermissionGroups(User user) throws Exception {
		return getPermissionGroups(user.getGroup());
	}

	public static List<com.idega.user.data.Group> getPermissionGroups(com.idega.user.data.Group group) throws Exception {
		GroupDAO dao = ELUtil.getInstance().getBean(GroupDAO.class);
		Group g = dao.findGroup(new Integer(group.getPrimaryKey().toString()));

		List<com.idega.user.data.Group> groups = new ArrayList<com.idega.user.data.Group>();
		List<Group> gr = getPermissionGroups(g);
		GroupHome home = (GroupHome) IDOLookup.getHome(com.idega.user.data.Group.class);
		for (Group group2 : gr) {
			try {
				groups.add(home.findByPrimaryKey(group2.getID()));
			}
			catch (FinderException fe) {
				fe.printStackTrace();
			}
		}

		return groups;
	}

	public static List<Group> getPermissionGroups(Group group) throws Exception {
		GroupDAO dao = ELUtil.getInstance().getBean(GroupDAO.class);

		Collection<GroupType> groupTypes = new ArrayList<GroupType>();
		String[] permissionFilter = getPermissionGroupFilter();
		for (String string : permissionFilter) {
			GroupType type = dao.findGroupType(string);
			if (type != null) {
				groupTypes.add(type);
			}
		}

		return dao.getParentGroups(group, groupTypes);
	}

	@Override
	public List<Group> getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws Exception {
		List<Group> toReturn = new ArrayList<Group>();
		List<ICPermission> permissions = null;

		switch (permissionCategory) {
			case AccessController.CATEGORY_OBJECT_INSTANCE :
				permissions = getPermissionDAO().findPermissions(CATEGORY_STRING_OBJECT_INSTANCE_ID, identifier, permissionKey, CoreConstants.Y);
				break;
			case AccessController.CATEGORY_OBJECT :
				permissions = getPermissionDAO().findPermissions(CATEGORY_STRING_IC_OBJECT_ID, identifier, permissionKey, CoreConstants.Y);
				break;
			case AccessController.CATEGORY_BUNDLE :
				permissions = getPermissionDAO().findPermissions(CATEGORY_STRING_BUNDLE_IDENTIFIER, identifier, permissionKey, CoreConstants.Y);
				break;
			case AccessController.CATEGORY_PAGE_INSTANCE :
				permissions = getPermissionDAO().findPermissions(CATEGORY_STRING_PAGE_ID, identifier, permissionKey, CoreConstants.Y);
				break;
			case AccessController.CATEGORY_PAGE :
				permissions = getPermissionDAO().findPermissions(CATEGORY_STRING_PAGE, identifier, permissionKey, CoreConstants.Y);
				break;
			case AccessController.CATEGORY_JSP_PAGE :
				permissions = getPermissionDAO().findPermissions(CATEGORY_STRING_JSP_PAGE, identifier, permissionKey, CoreConstants.Y);
				break;
		}

		if (permissions != null) {
			for (ICPermission icPermission : permissions) {
				toReturn.add(icPermission.getPermissionGroup());
			}
		}

		toReturn.remove(this.AdministratorPermissionGroup);
		return toReturn;
	}

	@Override
	public List<Group> getAllPermissionGroups() throws Exception {
		List<GroupType> groupTypes = new ArrayList<GroupType>();
		String[] permissionFilter = getPermissionGroupFilter();
		for (String string : permissionFilter) {
			GroupType type = getGroupDAO().findGroupType(string);
			if (type != null) {
				groupTypes.add(type);
			}
		}

		List<Group> permissionGroups = getGroupDAO().getGroupsByTypes(groupTypes);
		if (permissionGroups != null) {
			permissionGroups.remove(getPermissionGroupAdministrator());
		}

		return permissionGroups;
	}

	@Override
	public List<Group> getStandardGroups() throws Exception {
		if (this.standardGroups == null) {
			initStandardGroups();
		}
		return this.standardGroups;
	}

	private void initStandardGroups() throws Exception {
		this.standardGroups = new ArrayList<Group>();
		this.standardGroups.add(this.getPermissionGroupEveryOne());
		this.standardGroups.add(this.getPermissionGroupUsers());
	}

	@Override
	@Deprecated
	public com.idega.user.data.User getAdministratorUserLegacy() throws Exception {
		User u = getAdministratorUser();
		if (u != null) {
			UserHome home = (UserHome) IDOLookup.getHome(com.idega.user.data.User.class);
			return home.findByPrimaryKey(u.getId());
		}

		return null;
	}

	@Override
	public User getAdministratorUser() throws Exception {
		Object ob = getApplication().getAttribute(_APPADDRESS_ADMINISTRATOR_USER);
		if (ob instanceof User) {
			return (User) ob;
		} else {
			try {
				initAdministratorUser();
				return (User) getApplication().getAttribute(_APPADDRESS_ADMINISTRATOR_USER);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

		}
	}

	private User createAdministratorUser() throws UsernameExistsException {
		User newUser = getUserDAO().createUser(User.ADMINISTRATOR_DEFAULT_NAME, null, null, User.ADMINISTRATOR_DEFAULT_NAME, null, null, null, null, getPermissionGroupAdministrator());
		getUserLoginDAO().createLogin(newUser, User.ADMINISTRATOR_DEFAULT_NAME, "idega", true, true, true, -1, false);

		return newUser;
	}

	private void initAdministratorUser() throws UsernameExistsException, IWServiceNotStartedException {
		Collection<User> users = getUserDAO().getResultListByInlineQuery(
				"select u from " + User.class.getName() + " u where u.firstName = :name",
				User.class,
				new Param("name", User.ADMINISTRATOR_DEFAULT_NAME)
		);
		
		User adminUser = null;
		if (ListUtil.isEmpty(users)) {
			getLogger().warning("No administrators exist, creating a new one");
			adminUser = createAdministratorUser();
		} else {
			adminUser = users.iterator().next();
		}

		try {
			getApplication().setAttribute(_APPADDRESS_ADMINISTRATOR_USER, adminUser);
		} catch (IWServiceNotStartedException e) {
			e.printStackTrace();
		}
		if (adminUser.getUniqueId() == null) {
			UUIDBusiness uuidBean;
			try {
				uuidBean = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UUIDBusiness.class);
				uuidBean.addUniqueKeyIfNeeded(adminUser, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		getApplication().setAttribute(_APPADDRESS_ADMINISTRATOR_USER, adminUser);
	}

	@Override
	public void executeService() {
		try {
			GroupType permissionType = getGroupDAO().findGroupType(PermissionGroup.GROUP_TYPE_PERMISSION);

			Group group = getGroupDAO().findByGroupTypeAndName(permissionType, getAdministratorGroupName());
			if (group != null) {
				this.AdministratorPermissionGroup = getPermissionDAO().findPermissionGroup(group.getID());
			}

			if (this.AdministratorPermissionGroup == null) {
				initAdministratorPermissionGroup();
			}
		}
		catch (Exception ex) {
			System.err.println("AccessControl: PermissionGroup administrator not initialized");
			ex.printStackTrace();
		}

		initPermissionGroupEveryone();
		initPermissionGroupUsers();

		try {
			initAdministratorUser();
		} catch (IWServiceNotStartedException e) {
			e.printStackTrace();
		} catch (UsernameExistsException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getServiceName() {
		return "AccessControl";
	}

	public static boolean isValidUsersFirstName(String name) {
		return User.ADMINISTRATOR_DEFAULT_NAME.equals(name);
	}

	@Override
	public String[] getICObjectPermissionKeys(Class<?> icObject) {
		String[] keys = new String[2];

		keys[0] = PERMISSION_KEY_VIEW;
		keys[1] = PERMISSION_KEY_EDIT;

		return keys;
	}

	@Override
	public String[] getBundlePermissionKeys(Class<?> icObject) {
		return getICObjectPermissionKeys(icObject);
	}

	@Override
	public String[] getBundlePermissionKeys(String bundleIdentifier) {
		String[] keys = new String[2];

		keys[0] = PERMISSION_KEY_VIEW;
		keys[1] = PERMISSION_KEY_EDIT;

		return keys;
	}

	@Override
	public String[] getPagePermissionKeys() {
		String[] keys = new String[2];

		keys[0] = PERMISSION_KEY_VIEW;
		keys[1] = PERMISSION_KEY_EDIT;

		return keys;
	}

	@Deprecated
	public static void initICObjectPermissions(com.idega.core.component.data.ICObject obj) throws Exception {
		ICObjectDAO dao = ELUtil.getInstance().getBean(ICObjectDAO.class);
		ICObject object = dao.findObject(new Integer(obj.getPrimaryKey().toString()));
		initICObjectPermissions(object);
	}

	public static void initICObjectPermissions(ICObject obj) throws Exception {
		AccessController instance = IWMainApplication.getDefaultIWMainApplication().getAccessController();
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);
		PermissionGroup group = permissionDAO.findPermissionGroup(instance.getEveryoneGroupID());
		permissionDAO.createPermission(AccessController.CATEGORY_STRING_IC_OBJECT_ID, Integer.toString(obj.getId()), group, AccessController.PERMISSION_KEY_VIEW, Boolean.TRUE);
	}

	/**
	 * @todo implement hasFilePermission(String permissionKey, int id, IWUserContext iwc)throws Exception
	 */
	@Override
	public boolean hasFilePermission(String permissionKey, int id, IWUserContext iwc) throws Exception {
		return true;
	}

	@Override
	@Deprecated
	public boolean hasDataPermission(String permissionKey, com.idega.core.component.data.ICObject obj, int entityRecordId, IWUserContext iwc) throws Exception {
		return hasDataPermission(permissionKey, getObjectDAO().findObject(new Integer(obj.getPrimaryKey().toString())), entityRecordId, iwc);
	}

	/**
	 * @todo implement hasDataPermission(String permissionKey, ICObject obj, int entityRecordId, IWUserContext iwc)
	 */
	@Override
	public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId, IWUserContext iwc) throws Exception {
		return true;
	}

	@Override
	@Deprecated
	public void setCurrentUserAsOwner(com.idega.core.builder.data.ICPage page, IWUserContext iwc) throws Exception {
		setCurrentUserAsOwner(getPageDAO().findPage(new Integer(page.getPrimaryKey().toString())), iwc);
	}

	@Override
	public void setCurrentUserAsOwner(ICPage page, IWUserContext iwc) throws Exception {
		if (iwc.isLoggedOn()) {
			User user = iwc.getLoggedInUser();

			int groupId = -1;
			Group group = user.getPrimaryGroup();
			if (group != null) {
				groupId = group.getID();
			}
			if (groupId == -1) {
				groupId = user.getId();
			}

			if (groupId != -1) {
				setAsOwner(page, groupId, iwc.getApplicationContext());
			}
		}
	}

	/**
	 * @todo implement setAsOwner(ICFile file, IWApplicationContext iwc)throws Exception
	 */
	@Override
	public void setAsOwner(ICPage page, int groupId, IWApplicationContext iwac) throws Exception {
		setPermission(
			AccessController.CATEGORY_PAGE_INSTANCE,
			iwac,
			Integer.toString(groupId),
			Integer.toString(page.getID()),
			AccessController.PERMISSION_KEY_OWNER,
			Boolean.TRUE);
	}

	/**
	 * @todo implement setAsOwner(PresentationObject obj , IWApplicationContext iwac) throws Exception
	 */
	@Override
	public void setAsOwner(PresentationObject obj, int groupId, IWApplicationContext iwac) throws Exception {
	}

	/**
	 * @todo implement setAsOwner(ICFile file, IWUserContext iwc)throws Exception
	 */
	@Override
	public void setAsOwner(ICFile file, int groupId, IWApplicationContext iwac) throws Exception {
		setPermission(
			AccessController.CATEGORY_FILE_ID,
			iwac,
			Integer.toString(groupId),
			file.getId().toString(),
			AccessController.PERMISSION_KEY_OWNER,
			Boolean.TRUE);
	}

	@Override
	@Deprecated
	public void setAsOwner(com.idega.user.data.Group group, int groupId, IWApplicationContext iwac) throws Exception {
		setAsOwner(getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString())), groupId, iwac);
	}

	@Override
	public void setAsOwner(Group group, int groupId, IWApplicationContext iwac) throws Exception {
		setPermission(
			AccessController.CATEGORY_GROUP_ID,
			iwac,
			Integer.toString(groupId),
			group.getID().toString(),
			AccessController.PERMISSION_KEY_OWNER,
			Boolean.TRUE);
	}

	/**
	 * @todo implement setAsOwner(ICObject obj, int entityRecordId, IWUserContext iwc)throws Exception
	 */
	@Override
	public void setAsOwner(ICObject obj, int entityRecordId, int groupId, IWApplicationContext iwac) throws Exception {
		throw new Exception(this.getClass().getName() + ".setAsOwner(...) : not implemented");
	}

	public static void copyObjectInstancePermissions(String idToCopyFrom, String idToCopyTo) throws SQLException {
		copyPermissions(AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID, idToCopyFrom, idToCopyTo);
	}

	public static void copyPagePermissions(String idToCopyFrom, String idToCopyTo) throws SQLException {
		copyPermissions(AccessController.CATEGORY_STRING_PAGE_ID, idToCopyFrom, idToCopyTo);
	}

	public static List<ICPermission> getGroupsPermissions(String category, Group group, Set<String> identifiers) throws SQLException {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

		List<ICPermission> permissions = new ArrayList<ICPermission>();
		if (!identifiers.isEmpty()) {
			Group permGroup = groupDAO.findGroup(group.getID());
			List<ICPermission> perms = dao.findPermissions(category, identifiers, permGroup);
			if (perms != null)
				permissions.addAll(perms);
		}

		return permissions;
	}

	public static List<ICPermission> getGroupsPermissionsForInstances(Group group, Set<String> instances) throws SQLException {
		return getGroupsPermissions(AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID, group, instances);
	}

	public static List<ICPermission> getGroupsPermissionsForPages(Group group, Set<String> instances) throws SQLException {
		return getGroupsPermissions(AccessController.CATEGORY_STRING_PAGE_ID, group, instances);
	}

	public static boolean replicatePermissionForNewGroup(ICPermission permission, Group group) {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);
		Group permGroup = groupDAO.findGroup(group.getID());

		dao.createPermission(permission.getContextType(), permission.getContextValue(), permGroup, permission.getPermissionString(), permission.getPermissionValue());
		return true;
	}

	public static void copyPermissions(String contextType, String identifierToCopyFrom, String identifierToCopyTo) throws SQLException {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		List<ICPermission> permissions = dao.findPermissions(contextType, identifierToCopyFrom);
		if (permissions != null) {
			for (ICPermission permission : permissions) {
				dao.createPermission(contextType, identifierToCopyTo, permission.getPermissionGroup(), permission.getPermissionString(), permission.getPermissionValue());
			}
		}
	}


	/**
	 * Checks if the current user has a certain permission to a group depending on his active roles (or his parent groups roles).
	 * The supplied group must have a role set to it (the opposit of having a role) that is allowed the permission the current user is asking for.
	 *
	 * @param permissionKey a type of permission such as view,edit etc.
	 * @param group the group that has some roles associated with permission types / keys
	 * @param iwuc user context so we can get the current user
	 * @return true if the current users role is set to the group and the permissionkeys match else false
	 */
	public boolean hasPermissionForGroupByRole(String permissionKey, Group group, IWUserContext iwuc) throws RemoteException {
	    //get all the roles of the current users parent groups or permission controlling groups
	    //use a find method that searches for active and true ICPermissions with the following
	    //context_value=permissionKey, permission_string=collection of the current users roles, group_id = group.getPrimaryKey()
	    //If something is found then we return true, otherwise false

	    //get the parent groups
	    List<Group> permissionControllingGroups = new ArrayList<Group>();
	    Collection<Group> parents = getGroupDAO().getParentGroups(iwuc.getLoggedInUser().getUserRepresentative());

	    if(parents!=null && !parents.isEmpty()) {
	        Map<String, String> roleMap= new HashMap<String, String>();

	        //get the real permission controlling group if not the parent
	        for (Iterator<Group> iterator = parents.iterator(); iterator.hasNext();) {
                Group parent = iterator.next();
                Group permissionControllingParentGroup = parent.getPermissionControllingGroup();
                if(permissionControllingParentGroup != null) {
                    permissionControllingGroups.add(permissionControllingParentGroup);
                }else {
                    permissionControllingGroups.add(parent);
                }
	        }

		    //create the role map we need
	        Collection<ICPermission> permissions = getAllRolesForGroupCollection(permissionControllingGroups);

	        if(!permissions.isEmpty()) {
		        for (Iterator<ICPermission> iter = permissions.iterator(); iter.hasNext();) {
	                ICPermission perm = iter.next();
	                String roleKey = perm.getPermissionString();
	                if(!roleMap.containsKey(roleKey)) {
	                    roleMap.put(roleKey,roleKey);
	                }
	            }
	        }
	        else {
	            return false;
	        }

	        //see if we find role with the correct permission key and group
	        //if so we return true
	        //this could be optimized by doing a count sql instead
	        Group permGroup = getGroupDAO().findGroup(group.getID());
	        Collection<ICPermission> validPerms = getPermissionDAO().findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup(RoleHelperObject.getStaticInstance().toString(), permissionKey, roleMap.values(), permGroup);
            if(validPerms != null && !validPerms.isEmpty()) {
	            return true;
	        }
	    }

        //has no roles or does not have the correct role
	    return false;
	}


	@Override
	public boolean hasPermissionForGroup(String permissionKey, Group group, IWUserContext iwuc) {
	    try {
	        //check for regular permission, then by role
	        boolean hasPermission = hasPermission(permissionKey, group, iwuc);
	        if(!hasPermission) {
	            hasPermission = hasPermissionForGroupByRole(permissionKey, group, iwuc);
	        }
	        return hasPermission;
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	@Override
	@Deprecated
	public boolean hasEditPermissionFor(com.idega.user.data.Group group, IWUserContext iwuc) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		return hasEditPermissionFor(g, iwuc);
	}

    @Override
	public boolean hasEditPermissionFor(Group group, IWUserContext iwuc) {
        //check for regular permission, then by role
        return hasPermissionForGroup(AccessController.PERMISSION_KEY_EDIT, group, iwuc);
    }

    @Override
	@Deprecated
    public boolean hasViewPermissionFor(com.idega.user.data.Group group, IWUserContext iwuc) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		return hasViewPermissionFor(g, iwuc);
    }

    @Override
	public boolean hasViewPermissionFor(Group group, IWUserContext iwuc) {
//	        check for regular permission, then by role
		    return hasPermissionForGroup(AccessController.PERMISSION_KEY_VIEW, group, iwuc);
	}

    @Override
	@Deprecated
	public boolean hasCreatePermissionFor(com.idega.user.data.Group group, IWUserContext iwuc) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		return hasCreatePermissionFor(g, iwuc);
	}

	@Override
	public boolean hasCreatePermissionFor(Group group, IWUserContext iwuc) {
//			check for regular permission, then by role
			return hasPermissionForGroup(AccessController.PERMISSION_KEY_CREATE, group, iwuc);
	}

	@Override
	@Deprecated
	public boolean hasDeletePermissionFor(com.idega.user.data.Group group, IWUserContext iwuc) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		return hasDeletePermissionFor(g, iwuc);
	}

	@Override
	public boolean hasDeletePermissionFor(Group group, IWUserContext iwuc) {
		//check for regular permission, then by role
	    return hasPermissionForGroup(AccessController.PERMISSION_KEY_DELETE, group, iwuc);
	}

	@Override
	@Deprecated
	public boolean hasPermitPermissionFor(com.idega.user.data.Group group, IWUserContext iwuc) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		return hasPermitPermissionFor(g, iwuc);
	}

	/**
	 * The permission to give other groups permissions to this group
	 */
	@Override
	public boolean hasPermitPermissionFor(Group group, IWUserContext iwuc) {
		    //check for regular permission, then by role
			return hasPermissionForGroup(AccessController.PERMISSION_KEY_PERMIT, group, iwuc);
	}

	//Methods added after big changes by Eiki
	public static Collection<ICPermission> getAllPermissions(Group group, String contextType) {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		return dao.findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(contextType, dao.findPermissionGroup(group.getID()));
	}

	@Deprecated
	public static Collection<com.idega.core.accesscontrol.data.ICPermission> getAllGroupPermissionsForGroupLegacy(com.idega.user.data.Group group) {
		GroupDAO dao = ELUtil.getInstance().getBean(GroupDAO.class);
		Group g = dao.findGroup(new Integer(group.getPrimaryKey().toString()));

		Collection<com.idega.core.accesscontrol.data.ICPermission> oldPermissions = new ArrayList<com.idega.core.accesscontrol.data.ICPermission>();
		Collection<ICPermission> permissions = getAllGroupPermissionsForGroup(g);
		if (ListUtil.isEmpty(permissions))
			return oldPermissions;

		try {
			ICPermissionHome permissionHome = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
			for (ICPermission permission: permissions) {
				com.idega.core.accesscontrol.data.ICPermission tmp = permissionHome.findByPrimaryKey(permission.getId());
				if (tmp == null)
					continue;

				oldPermissions.add(tmp);
			}
		} catch (Exception e) {
			Logger.getLogger(AccessControl.class.getName()).log(Level.WARNING, "Error converting permissions " + permissions, e);
		}

		return oldPermissions;
	}

	public static Collection<ICPermission> getAllGroupPermissionsForGroup(Group group) {
		return getAllPermissions(group, AccessController.CATEGORY_STRING_GROUP_ID);
	}

	public static Collection<ICPermission> getAllPermissionsOwnedByGroup(Group group, String contextType) {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		return dao.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(dao.findPermissionGroup(group.getID()), AccessController.PERMISSION_KEY_OWNER, contextType);
	}

	public static Collection<ICPermission> getAllPermissionsForContextTypeAndContextValue(String contextType, String contextValue) {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		return dao.findPermissions(contextType, contextValue);
	}

	@Deprecated
	public static Collection<com.idega.core.accesscontrol.data.ICPermission> getAllGroupPermissionsReverseForGroup(com.idega.user.data.Group group) {
		Collection<com.idega.core.accesscontrol.data.ICPermission> oldPermissions = new ArrayList<com.idega.core.accesscontrol.data.ICPermission>();
		Collection<ICPermission> permissions = getAllPermissionsForContextTypeAndContextValue(AccessController.CATEGORY_STRING_GROUP_ID, group.getPrimaryKey().toString());

		try {
			ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
			for (ICPermission permission : permissions) {
				try {
					oldPermissions.add(home.findByPrimaryKey(permission.getId()));
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldPermissions;
	}

	public static Collection<ICPermission> getAllGroupPermissionsReverseForGroup(Group group) {
		return getAllPermissionsForContextTypeAndContextValue(AccessController.CATEGORY_STRING_GROUP_ID, group.getID().toString());
	}

	/**
	 * Gets a list of all permissions associtated with the supplied group and permission string (type e.g. view,edit,owner etc)
	 *
	 * @param group
	 *            the group that you want to see what permissions have been set to.
	 * @param permissionString
	 *            the type
	 * @return
	 */
	public static Collection<ICPermission> getAllGroupPermissionsReverseForGroupAndPermissionString(Group group, String permissionString) {
		PermissionDAO dao = ELUtil.getInstance().getBean(PermissionDAO.class);
		return dao.findPermissions(AccessController.CATEGORY_STRING_GROUP_ID, group.getID().toString(), permissionString);
	}

	@Override
	@Deprecated
	public Collection<com.idega.core.accesscontrol.data.ICPermission> getAllRolesForGroup(com.idega.user.data.Group group) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		Collection<ICPermission> permissions = getAllRolesForGroup(g);

		Collection<com.idega.core.accesscontrol.data.ICPermission> oldPermissions = new ArrayList<com.idega.core.accesscontrol.data.ICPermission>();
		try {
			ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
			for (ICPermission permission : permissions) {
				try {
					oldPermissions.add(home.findByPrimaryKey(permission.getId()));
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldPermissions;
	}

	/**
	 * Gets all the role permissions the group has. It does not return role-permissionkey permissions
	 */
	@Override
	public Collection<ICPermission> getAllRolesForGroup(Group group) {
		if (group == null)
			return Collections.emptyList();

		Group permGroup = getGroupDAO().findGroup(group.getID());
		Collection<ICPermission> groupPermissions = new ArrayList<ICPermission>(); //empty
		Collection<ICPermission> permissions = getPermissionDAO().findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(RoleHelperObject.getStaticInstance().toString(), permGroup);

		if (ListUtil.isEmpty(permissions)) {
			return groupPermissions;
		}

		for (ICPermission perm: permissions) {
			//	perm.getPermissionString().equals(perm.getContextValue()) is true if it is a marker for an active role for a group
			//	If not it is a role for a permission key
			if (perm.getPermissionValue() && perm.getContextValue().equals(perm.getContextType())) {
				groupPermissions.add(perm);
			}
		}

		return groupPermissions;
	}

	@Override
	@Deprecated
	public Collection<String> getAllRolesKeysForGroup(com.idega.user.data.Group group) {
		if (group == null)
			return Collections.emptyList();

		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		return getAllRolesKeysForGroup(g);
	}

	@Override
	public Collection<String> getAllRolesKeysForGroup(Group group) {
		Collection<ICPermission> permissions = getAllRolesForGroup(group);
		if (ListUtil.isEmpty(permissions)) {
			return new ArrayList<String>(0);
		}

		Collection<String> keys = new ArrayList<String>(permissions.size());
		for (ICPermission permission: permissions) {
			keys.add(permission.getPermissionString());
		}

		return keys;
	}

	/**
	 * Gets all the role permissions the collection of group have. It does not return role-permissionkey permissions
	 */
	public Collection<ICPermission> getAllRolesForGroupCollection(Collection<Group> groups) {
	    Collection<ICPermission> returnCol = new ArrayList<ICPermission>(); //empty
	    if (ListUtil.isEmpty(groups)) {
	    	return ListUtil.getEmptyList();
	    }

	    Collection<Group> permGroups = new ArrayList<Group>();
	    for (Group group : groups) {
			permGroups.add(getGroupDAO().findGroup(group.getID()));
		}

        Collection<ICPermission> permissions = getPermissionDAO().findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(
                    RoleHelperObject.getStaticInstance().toString(),
                    permGroups);

        //only return active and only actual roles and not group permission definitation roles
        if(permissions!=null && !permissions.isEmpty()){
            for ( Iterator<ICPermission> permissionsIter = permissions.iterator(); permissionsIter.hasNext();) {
                ICPermission perm = permissionsIter.next();
                //perm.getPermissionString().equals(perm.getContextValue()) is true if it is a marker for an active role for a group
                //if not it is a role for a permission key
                if(perm.getPermissionValue() && perm.getContextValue().equals(perm.getContextType())){
                    returnCol.add(perm);
                }
            }
        }

		return returnCol;
	}

	@Override
	public Set<String> getAllRolesForCurrentUser(IWUserContext iwc) {
		return getAllRolesForUser(iwc.getLoggedInUser());
	}

	@Override
	@Deprecated
	public Set<String> getAllRolesForUser(com.idega.user.data.User user) {
		User u = getUserDAO().getUser(new Integer(user.getPrimaryKey().toString()));
		return getAllRolesForUser(u);
	}

	@Override
	public Set<String> getAllRolesForUser(User user) {
		Set<String> s = new HashSet<String>();

		Collection<String> userRolesFromGroup = getAllRolesKeysForGroup(user.getGroup());
		if (!ListUtil.isEmpty(userRolesFromGroup)) {
			for (String key: userRolesFromGroup)
				s.add(key);
		}

		try {
			Collection<ICPermission> c = getAllRolesForGroupCollection(getParentGroupsAndPermissionControllingParentGroups(null, user));
			if (c == null)
				return s;

			for (ICPermission p: c) {
				if (p.isActive()) {
					String key = p.getPermissionString();
					if (!s.contains(key)) {
						s.add(key);
					}
				}
			}
			return s;
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return Collections.emptySet();
	}

	@Override
	public boolean hasRole(com.idega.user.data.User user, String roleKey) {
		if (user == null || StringUtil.isEmpty(roleKey)) {
			return false;
		}

		User u = getUserDAO().getUser(new Integer(user.getPrimaryKey().toString()));
		return hasRole(u, roleKey);
	}

	@Override
	public boolean hasRole(User user, String roleKey) {
		if (user == null || StringUtil.isEmpty(roleKey)) {
			return false;
		}

		Set<String> allUserRoles = getAllRolesForUser(user);
		if (ListUtil.isEmpty(allUserRoles)) {
			return false;
		}

		return allUserRoles.contains(roleKey);
	}

	@Override
	@Deprecated
	public Collection<com.idega.core.accesscontrol.data.ICPermission> getAllRolesWithRolePermissionsForGroup(com.idega.user.data.Group group) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));

		Collection<com.idega.core.accesscontrol.data.ICPermission> oldPermissions = new ArrayList<com.idega.core.accesscontrol.data.ICPermission>();
		Collection<ICPermission> permissions = getAllRolesWithRolePermissionsForGroup(g);
		try {
			ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
			for (ICPermission permission : permissions) {
				try {
					oldPermissions.add(home.findByPrimaryKey(permission.getId()));
				} catch (FinderException e) {
					e.printStackTrace();
				}
			}
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldPermissions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.accesscontrol.business.AccessController#hasRole(com.idega.user.data.User, java.util.Collection)
	 */
	@Override
	public boolean hasRole(User user, Collection<String> roleKeys) {
		if (user == null || ListUtil.isEmpty(roleKeys)) {
			return Boolean.FALSE;
		}

		Set<String> allUserRoles = getAllRolesForUser(user);
		if (ListUtil.isEmpty(allUserRoles)) {
			return Boolean.FALSE;
		}

		for (String roleKey: roleKeys) {
			if (allUserRoles.contains(roleKey)) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * Gets all the role permissions the group has and also role-permission key roles.
	 */
	@Override
	public Collection<ICPermission> getAllRolesWithRolePermissionsForGroup(Group group) {
		Group permGroup = getGroupDAO().findGroup(group.getID());

		Collection<ICPermission> returnCol = new ArrayList<ICPermission>();
		Collection<ICPermission> permissions = getPermissionDAO().findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(
				RoleHelperObject.getStaticInstance().toString(),
				permGroup);

		if(permissions!=null && !permissions.isEmpty()){
			Iterator<ICPermission> permissionsIter = permissions.iterator();
			while (permissionsIter.hasNext()) {
				ICPermission perm = permissionsIter.next();
				if(perm.getPermissionValue()){
					returnCol.add(perm);
				}
			}
		}

		return returnCol;
	}

	/**
	 * Gets all the role permissions the group collection has and also role-permission key roles.
	 */
	@Override
	public Collection<ICPermission> getAllRolesWithRolePermissionsForGroupCollection(Collection<Group> groups) {
	    Collection<Group> permGroups = new ArrayList<Group>();
	    for (Group group : groups) {
			permGroups.add(getGroupDAO().findGroup(group.getID()));
		}

	    Collection<ICPermission> returnCol = new ArrayList<ICPermission>();
        Collection<ICPermission> permissions = getPermissionDAO().findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(
                    RoleHelperObject.getStaticInstance().toString(),
                    permGroups);

        if(permissions!=null && !permissions.isEmpty()){
            Iterator<ICPermission> permissionsIter = permissions.iterator();
            while (permissionsIter.hasNext()) {
                ICPermission perm = permissionsIter.next();

                if(perm.getPermissionValue()){
                    returnCol.add(perm);
                }
            }
        }

		return returnCol;
	}

	@Override
	@Deprecated
	public Collection<com.idega.user.data.Group> getAllGroupsForRoleKeyLegacy(String roleKey, IWApplicationContext iwac) {
		Collection<com.idega.user.data.Group> oldGroups = new ArrayList<com.idega.user.data.Group>();
		Collection<Group> groups = getAllGroupsForRoleKey(roleKey, iwac);

		try {
			GroupHome home = (GroupHome) IDOLookup.getHome(com.idega.user.data.Group.class);
			for (Group group : groups) {
				try {
					oldGroups.add(home.findByPrimaryKey(group.getID()));
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldGroups;
	}

	@Override
	public Collection<Group> getAllGroupsForRoleKey(String roleKey, IWApplicationContext iwac) {
		Collection<Group> groups = new ArrayList<Group>();

		Collection<ICPermission> permissions = getPermissionDAO().findPermissions(RoleHelperObject.getStaticInstance().toString(),RoleHelperObject.getStaticInstance().toString(),roleKey);
		if (permissions!=null && !permissions.isEmpty()){
			for (ICPermission permission : permissions) {
				groups.add(permission.getPermissionGroup());
			}
		}

		return groups;
	}

	@Deprecated
	public Collection<com.idega.user.data.Group> getAllUserGroupsForRoleKey(String roleKey, IWApplicationContext iwac, com.idega.user.data.User user) {
		User u = getUserDAO().getUser(new Integer(user.getPrimaryKey().toString()));
		Collection<Group> groups = getAllUserGroupsForRoleKey(roleKey, CoreUtil.getIWContext(), u);

		Collection<com.idega.user.data.Group> oldGroups = new ArrayList<com.idega.user.data.Group>();
		try {
			GroupHome home = (GroupHome) IDOLookup.getHome(com.idega.user.data.Group.class);
			for (Group group : groups) {
				try {
					oldGroups.add(home.findByPrimaryKey(group.getID()));
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldGroups;
	}

	@Override
	public Collection<Group> getAllUserGroupsForRoleKey(String roleKey, IWUserContext iwuc, User user) {
		Collection<Group> groupsForRoleKey = getAllGroupsForRoleKey(roleKey, IWMainApplication.getDefaultIWApplicationContext());
		Collection<Group> userGroups = getGroupDAO().getParentGroups(user.getGroup());
		Collection<Group> groups = new ArrayList<Group>();

		for(Group group : groupsForRoleKey) {
			if(userGroups.contains(group)) {
				groups.add(group);
			}
		}

		if(groups.size() > 0) {
			//TODO sort groups that prefferable group would have highest index
		}

		return groups;
	}

	@Override
	public Collection<Group> getAllGroupsThatAreRoleMasters(IWApplicationContext iwac){
		return getAllGroupsForRoleKey(PERMISSION_KEY_ROLE_MASTER,iwac);
	}

	@Override
	@Deprecated
	public Collection<com.idega.core.accesscontrol.data.ICRole> getAllRolesLegacy() {
		try {
			ICRoleHome home = (ICRoleHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICRole.class);
			return home.findAllRoles();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}

		return new ArrayList<com.idega.core.accesscontrol.data.ICRole>();
	}

	@Override
	public Collection<ICRole> getAllRoles() {
		return getPermissionDAO().findAllRoles();
	}

	@Deprecated
	public static Collection<com.idega.core.accesscontrol.data.ICPermission> getAllOwnerGroupPermissionsReverseForGroup(com.idega.user.data.Group group) {
		GroupDAO dao = ELUtil.getInstance().getBean(GroupDAO.class);
		Group g = dao.findGroup(new Integer(group.getPrimaryKey().toString()));

		Collection<com.idega.core.accesscontrol.data.ICPermission> oldPermissions = new ArrayList<com.idega.core.accesscontrol.data.ICPermission>();
		Collection<ICPermission> permissions = getAllGroupPermissionsReverseForGroupAndPermissionString(g, AccessController.PERMISSION_KEY_OWNER);
		try {
			ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
			for (ICPermission permission : permissions) {
				try {
					oldPermissions.add(home.findByPrimaryKey(permission.getId()));
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldPermissions;
	}

	public static Collection<ICPermission> getAllOwnerGroupPermissionsReverseForGroup(Group group) {
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group, AccessController.PERMISSION_KEY_OWNER);
	}

	public static Collection<ICPermission> getAllEditGroupPermissionsReverseForGroup(Group group) {
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group, AccessController.PERMISSION_KEY_EDIT);
	}

	public static Collection<ICPermission> getAllViewGroupPermissionsReverseForGroup(Group group) {
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group, AccessController.PERMISSION_KEY_VIEW);
	}

	public static Collection<ICPermission> getAllDeleteGroupPermissionsReverseForGroup(Group group) {
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group, AccessController.PERMISSION_KEY_DELETE);
	}

	public static Collection<ICPermission> getAllCreateGroupPermissionsReverseForGroup(Group group) {
		return getAllGroupPermissionsReverseForGroupAndPermissionString(group, AccessController.PERMISSION_KEY_CREATE);
	}

	@Deprecated
	public static Collection<com.idega.core.accesscontrol.data.ICPermission> getAllGroupOwnerPermissionsByGroup(com.idega.user.data.Group group) {
		GroupDAO dao = ELUtil.getInstance().getBean(GroupDAO.class);
		Group g = dao.findGroup(new Integer(group.getPrimaryKey().toString()));

		Collection<ICPermission> permissions = getAllGroupOwnerPermissionsByGroup(g);
		Collection<com.idega.core.accesscontrol.data.ICPermission> oldPermissions = new ArrayList<com.idega.core.accesscontrol.data.ICPermission>();

		try {
			ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
			for (ICPermission permission : permissions) {
				try {
					oldPermissions.add(home.findByPrimaryKey(permission.getId()));
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldPermissions;
	}

	public static Collection<ICPermission> getAllGroupOwnerPermissionsByGroup(Group group) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);

		return permissionDAO.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(group, AccessController.PERMISSION_KEY_OWNER, AccessController.CATEGORY_STRING_GROUP_ID); //empty
	}

	public static Collection<ICPermission> getAllGroupPermitPermissionsByGroup(Group group) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);

		return permissionDAO.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(group, AccessController.PERMISSION_KEY_PERMIT, AccessController.CATEGORY_STRING_GROUP_ID); //empty
	}

	@Deprecated
	public static Collection<com.idega.core.accesscontrol.data.ICPermission> getAllGroupPermitPermissionsOld(
			Collection<com.idega.user.data.Group> groups) {
		GroupDAO dao = ELUtil.getInstance().getBean(GroupDAO.class);
		Collection<Group> newGroups = new ArrayList<Group>();
		for (com.idega.user.data.Group group2 : groups) {
			Group g = dao.findGroup(new Integer(group2.getPrimaryKey().toString()));
			newGroups.add(g);
		}

		Collection<ICPermission> permissions = getAllGroupPermitPermissions(newGroups);
		Collection<com.idega.core.accesscontrol.data.ICPermission> oldPermissions = new ArrayList<com.idega.core.accesscontrol.data.ICPermission>();

		try {
			ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
			for (ICPermission permission : permissions) {
				try {
					oldPermissions.add(home.findByPrimaryKey(permission.getId()));
				} catch (FinderException e) {
					e.printStackTrace();
				}
			}
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}

		return oldPermissions;
	}

	/**
	 * @param groups
	 * @return all ICPermissions owned by these groups
	 */
	public static Collection<ICPermission> getAllGroupPermitPermissions(Collection<Group> groups) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

	    Collection<Group> permGroups = new ArrayList<Group>();
	    for (Group group : groups) {
			permGroups.add(groupDAO.findGroup(group.getID()));
		}

	    Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(
	    			permGroups,
					AccessController.PERMISSION_KEY_PERMIT,
					AccessController.CATEGORY_STRING_GROUP_ID);
		return returnCol;
	}

	@Deprecated
	public static Collection<com.idega.core.accesscontrol.data.ICPermission> getAllGroupViewPermissionsLegacy(com.idega.user.data.Group group)
			throws FinderException, IDOLookupException {
		ICPermissionHome permissionHome = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);

		return permissionHome.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(
					group,
					AccessController.PERMISSION_KEY_VIEW,
					AccessController.CATEGORY_STRING_GROUP_ID);

	}
	public static Collection<ICPermission> getAllGroupViewPermissions(Group group) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);

		Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(
					group,
					AccessController.PERMISSION_KEY_VIEW,
					AccessController.CATEGORY_STRING_GROUP_ID);

		return returnCol;

	}

	public static Collection<ICPermission> getAllGroupDeletePermissions(Group group) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);

		Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(
					group,
					AccessController.PERMISSION_KEY_DELETE,
					AccessController.CATEGORY_STRING_GROUP_ID);

		return returnCol;

	}

	public static Collection<ICPermission> getAllGroupEditPermissions(Group group) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);

		Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(
					group,
					AccessController.PERMISSION_KEY_EDIT,
					AccessController.CATEGORY_STRING_GROUP_ID);

		return returnCol;

	}

	public static Collection<ICPermission> getAllGroupCreatePermissions(Group group) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);

		Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(
					group,
					AccessController.PERMISSION_KEY_CREATE,
					AccessController.CATEGORY_STRING_GROUP_ID);

		return returnCol;

	}

	/**
	 * @param groups
	 * @return all ICPermissions owned by these groups
	 */
	public static Collection<ICPermission> getAllGroupEditPermissions(Collection<Group> groups) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

	    Collection<Group> permGroups = new ArrayList<Group>();
	    for (Group group : groups) {
			permGroups.add(groupDAO.findGroup(group.getID()));
		}

	    Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(
	    			permGroups,
					AccessController.PERMISSION_KEY_EDIT,
					AccessController.CATEGORY_STRING_GROUP_ID);
		return returnCol;
	}

	/**
	 * Use {@link AccessControl.getAllGroupViewPermissions}
	 *
	 * @param groups
	 * @return
	 * @throws FinderException
	 * @throws IDOLookupException
	 * @throws RemoteException
	 */
	@Deprecated
	public static Collection<com.idega.core.accesscontrol.data.ICPermission> getAllGroupViewPermissionsLegacy(
			Collection<com.idega.user.data.Group> groups
		) throws IDOLookupException, RemoteException {

		ICPermissionHome permissionHome = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
		GroupBusiness groupBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), GroupBusiness.class);

	    Collection<com.idega.user.data.Group> permGroups = new ArrayList<com.idega.user.data.Group>();
	    for (com.idega.user.data.Group group: groups) {
	    	try {
	    		permGroups.add(groupBusiness.getGroupByGroupID(Integer.valueOf(group.getId())));
	    	} catch (FinderException e) {}
	    }

		try {
			return permissionHome.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(
						permGroups,
						AccessController.PERMISSION_KEY_VIEW,
						AccessController.CATEGORY_STRING_GROUP_ID);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	/**
	 * @param groups
	 * @return all ICPermissions owned by these groups
	 */
	public static Collection<ICPermission> getAllGroupViewPermissions(Collection<Group> groups) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

	    Collection<Group> permGroups = new ArrayList<Group>();
	    for (Group group: groups)
			permGroups.add(groupDAO.findGroup(group.getID()));

		Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(
					permGroups,
					AccessController.PERMISSION_KEY_VIEW,
					AccessController.CATEGORY_STRING_GROUP_ID);
		return returnCol;
	}

	/**
	 * @param groups
	 * @return all ICPermissions owned by these groups
	 */
	public static Collection<ICPermission> getAllGroupCreatePermissions(Collection<Group> groups) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

	    Collection<Group> permGroups = new ArrayList<Group>();
	    for (Group group : groups) {
			permGroups.add(groupDAO.findGroup(group.getID()));
		}

		Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(
					permGroups,
					AccessController.PERMISSION_KEY_CREATE,
					AccessController.CATEGORY_STRING_GROUP_ID);
		return returnCol;
	}

	/**
	 * @param groups
	 * @return all ICPermissions owned by these groups
	 */
	public static Collection<ICPermission> getAllGroupDeletePermissions(Collection<Group> groups) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);
		GroupDAO groupDAO = ELUtil.getInstance().getBean(GroupDAO.class);

	    Collection<Group> permGroups = new ArrayList<Group>();
	    for (Group group : groups) {
			permGroups.add(groupDAO.findGroup(group.getID()));
		}

		Collection<ICPermission> returnCol = permissionDAO.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(
					permGroups,
					AccessController.PERMISSION_KEY_DELETE,
					AccessController.CATEGORY_STRING_GROUP_ID);
		return returnCol;
	}

	public static ICPermission getICPermissionForGroupAndPermissionKeyContextTypeAndContextValue(Group group, String key, String contextType, String contextValue) {
		PermissionDAO permissionDAO = ELUtil.getInstance().getBean(PermissionDAO.class);

	    ICPermission perm = permissionDAO.findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue(group, key, contextType, contextValue);

	    return perm;
	}

	public static com.idega.core.accesscontrol.data.ICPermission getGroupICPermissionForGroupAndPermissionKeyAndContextValue(com.idega.user.data.Group group, String key, String contextValue) throws FinderException {
		GroupDAO dao = ELUtil.getInstance().getBean(GroupDAO.class);
		Group g = dao.findGroup(new Integer(group.getPrimaryKey().toString()));

		ICPermission permission = getGroupICPermissionForGroupAndPermissionKeyAndContextValue(g, key, contextValue);
		if (permission != null) {
			try {
				ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICPermission.class);
				return home.findByPrimaryKey(permission.getId());
			}
			catch (IDOLookupException e) {
				throw new FinderException(e.getMessage());
			}
		}
		else {
			throw new FinderException("No permission found");
		}
	}

	public static ICPermission getGroupICPermissionForGroupAndPermissionKeyAndContextValue(Group group, String key, String contextValue) {
	    return getICPermissionForGroupAndPermissionKeyContextTypeAndContextValue(group,key,AccessController.CATEGORY_STRING_GROUP_ID,contextValue);
	}

	@Override
	public boolean hasRole(String roleKey, IWUserContext iwuc) {
		try {
			boolean noErrors = checkIfRoleExistsInDataBaseAndCreateIfMissing(roleKey);
			if(!noErrors){
				return false;//something is wrong!
			}
			else{
				return hasPermission(roleKey, RoleHelperObject.getStaticInstance(), iwuc);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Deprecated
	public boolean hasRole(String roleKey, com.idega.user.data.Group group, IWUserContext iwuc){
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		return hasRole(roleKey, g, iwuc);
	}

	/**
	 * Check if the supplied group has this role
	 * @param roleKey
	 * @param group
	 * @param iwuc
	 * @return
	 */
	@Override
	public boolean hasRole(String roleKey, Group group, IWUserContext iwuc){

		@SuppressWarnings("unchecked")
		List<String>[] usersGroupsToCheckAgainstPermissions = new List[1];
		usersGroupsToCheckAgainstPermissions[0] = new ArrayList<String>(1);
		usersGroupsToCheckAgainstPermissions[0].add(group.getID().toString());

		Boolean myPermission;
		try {
			myPermission = checkForPermission(usersGroupsToCheckAgainstPermissions, RoleHelperObject.getStaticInstance(), roleKey, iwuc);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (myPermission!=null){
			return myPermission.booleanValue();
		} else {
			return false;
		}
	}

	/**
	 * Checks with ICRole if this roleKey exists if not it creates it so we can get it in the roles window
	 * @param roleKey
	 */
	@Override
	public boolean checkIfRoleExistsInDataBaseAndCreateIfMissing(String roleKey) {
		if (this.rolesList == null){
			this.rolesList = new ArrayList<String>();
		}

		if (!this.rolesList.contains(roleKey)){
			ICRole role = getPermissionDAO().findRole(roleKey);
			if (role != null) {
				this.rolesList.add(roleKey);
				return true;
			}
			else {
				if(roleKey!=null){
					getLogger().info("AccessControl: the role "+roleKey+" does not exist creating it!");

					if(createRoleWithRoleKey(roleKey)!=null){
						this.rolesList.add(roleKey);
						return true;
					}
					else {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	@Deprecated
	public com.idega.core.accesscontrol.data.ICRole getRoleByRoleKeyOld(String roleKey) throws FinderException {
		try {
			ICRoleHome home = (ICRoleHome) IDOLookup.getHome(com.idega.core.accesscontrol.data.ICRole.class);
			return home.findByPrimaryKey(roleKey);
		}
		catch (IDOLookupException ile) {
			ile.printStackTrace();
			return null;
		}
	}

	@Override
	public ICRole getRoleByRoleKey(String roleKey) {
		return getPermissionDAO().findRole(roleKey);
	}

	@Override
	public ICRole createRoleWithRoleKey(String roleKey) {
		return getPermissionDAO().createRole(roleKey, "ROLE."+roleKey+".description", "ROLE."+roleKey+".name");
	}

	@Override
	public boolean isRoleMaster(IWUserContext iwuc) {
		try {
			return hasRole(AccessController.PERMISSION_KEY_ROLE_MASTER, iwuc);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Deprecated
	public void addGroupAsRoleMaster(com.idega.user.data.Group group, IWApplicationContext iwma) {
		Group g = groupDAO.findGroup(new Integer(group.getPrimaryKey().toString()));
		addGroupAsRoleMaster(g, iwma);
	}

	@Override
	public void addGroupAsRoleMaster(Group group, IWApplicationContext iwma) {
		//role master can give other groups roles and is a role itself
		addRoleToGroup(AccessController.PERMISSION_KEY_ROLE_MASTER, group, iwma);
	}

	@Override
	@Deprecated
	public void removeGroupFromRoleMastersList(com.idega.user.data.Group group, IWApplicationContext iwma) {
		Group g = getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString()));
		removeGroupFromRoleMastersList(g, iwma);
	}

	@Override
	public void removeGroupFromRoleMastersList(Group group, IWApplicationContext iwma) {
		removeRoleFromGroup(AccessController.PERMISSION_KEY_ROLE_MASTER, group, iwma);
	}

	@Override
	public boolean removeRoleFromGroup(String roleKey, Integer groupId, IWApplicationContext iwma) {
		try {
			setPermission(AccessController.CATEGORY_ROLE, iwma, groupId.toString(), RoleHelperObject.getStaticInstance().toString(),roleKey,Boolean.FALSE);
			return true;
		}
		catch (Exception e) { //setPermission throws Exception!? but does it rollback on errors?
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeRoleFromGroup(String roleKey, Group group, IWApplicationContext iwac) {
		return removeRoleFromGroup(roleKey, group.getID(), iwac);
	}

	@Override
	public String getRoleIdentifier(){
		return RoleHelperObject.getStaticInstance().toString();
	}

	@Override
	public void addRoleToGroup(String roleKey, com.idega.user.data.Group group, IWApplicationContext iwac) {
		addRoleToGroup(roleKey, (Integer) group.getPrimaryKey(), iwac);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.core.accesscontrol.business.AccessController#addRoleToGroup(java.lang.String, com.idega.user.data.Group,
	 *      com.idega.idegaweb.IWApplicationContext)
	 */
	@Override
	public void addRoleToGroup(String roleKey, Group group, IWApplicationContext iwac) {
		addRoleToGroup(roleKey, group.getID(), iwac);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.core.accesscontrol.business.AccessController#addRoleToGroup(java.lang.String, java.lang.String,
	 *      com.idega.idegaweb.IWApplicationContext)
	 */
	@Override
	public boolean addRoleToGroup(String roleKey, Integer groupId, IWApplicationContext iwac) {
		try {
			setPermission(AccessController.CATEGORY_ROLE, iwac, groupId.toString(), RoleHelperObject.getStaticInstance().toString() ,roleKey, Boolean.TRUE);
			return true;
		}
		catch (Exception e) { //setPermission throws Exception!? but does it rollback on errors?
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Add a role with a permission key connection
	 * */
	@Override
	public boolean addRoleToGroup(String roleKey, String permissionKey, Integer groupId, IWApplicationContext iwac) {
		try {
			setPermission(AccessController.CATEGORY_ROLE, iwac, groupId.toString(), permissionKey, roleKey, Boolean.TRUE);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeRoleFromGroup(String roleKey, String permissionKey, Integer groupId, IWApplicationContext iwac) {
		try {
			setPermission(AccessController.CATEGORY_ROLE, iwac, groupId.toString(), permissionKey, roleKey,Boolean.FALSE);
			return true;
		}
		catch (Exception e) { //setPermission throws Exception!? but does it rollback on errors?
			e.printStackTrace();
			return false;
		}
	}


		/**
		 * Gets if the user has 'view' permission for the page with the given pageUri.
		 * @param pageUri the URI for a page request e.g. '/pages/1234'
		 */
	  @Override
	public boolean hasViewPermissionForPageURI(String pageUri,HttpServletRequest request){

	  	HttpSession session = request.getSession();
	  	ServletContext sc = session.getServletContext();

	  	//TODO: Optimize this since this has to be done for each request:
	  	IWUserContext iwuc = new IWUserContextImpl(session,sc);

	  	try {
	  		String serverName = request.getServerName();
	  		BuilderService bs = BuilderServiceFactory.getBuilderService(iwuc.getApplicationContext());
	  		String pageKey = bs.getPageKeyByRequestURIAndServerName(pageUri,serverName);
	  		if(pageKey!=null){
	  			return hasViewPermissionForPageKey(pageKey,iwuc);
	  		}
	  		else{
	  			//getLogger().warning("No pageKey found for : "+pageUri);
	  			return false;
	  		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	  	return false;
	  }

	  /**
	   * Gets if the user has 'view' permission for the page with the given pageId
	   * @param pageKey identifier for page e.g. '1234'
	   * @param iwuc context for the current user
	   * @return
	   */
	  @Override
	public boolean hasViewPermissionForPageKey(String pageKey,IWUserContext iwuc) {
  		PagePermissionObject pageKeyObject = new PagePermissionObject(pageKey);
		try {
			boolean permission = hasPermission(PERMISSION_KEY_VIEW, pageKeyObject, iwuc);
			if (!permission) {
				//if regular user check fails do extra check for content roles
				return hasRole(StandardRoles.ROLE_KEY_AUTHOR, iwuc) || hasRole(StandardRoles.ROLE_KEY_EDITOR, iwuc);
			}

			//even if you have the view rights the page might not be published yet.
			//extra check for unpublished pages
			ICPage page = getPageDAO().findPage(new Integer(pageKey));
			if (page != null && !page.isPublished()) {
				//only editors can view unpublished pages.
				return hasRole(StandardRoles.ROLE_KEY_AUTHOR, iwuc) || hasRole(StandardRoles.ROLE_KEY_EDITOR, iwuc);
			}

			return permission;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	  }


	  private String getRecurseParentsSettings(IWApplicationContext iwac) {
		  return iwac.getApplicationSettings().getProperty("TEMP_ACCESS_CONTROL_DO_NOT_RECURSE_PARENTS");
	  }

	public UserLoginDAO getUserLoginDAO() {
		if (userLoginDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return this.userLoginDAO;
	}

	public GroupDAO getGroupDAO() {
		if (groupDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return this.groupDAO;
	}

	public UserDAO getUserDAO() {
		if (userDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return this.userDAO;
	}

	public PermissionDAO getPermissionDAO() {
		if (permissionDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return this.permissionDAO;
	}

	public ICPageDAO getPageDAO() {
		if (pageDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return this.pageDAO;
	}

	public ICObjectDAO getObjectDAO() {
		if (objectDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return this.objectDAO;
	}

}
