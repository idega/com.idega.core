/*
 * $Id: AccessController.java,v 1.38 2009/06/24 08:58:18 valdas Exp $
 *
 * Created in 2001 by gummi
 *
 * Copyright (C) 2001-2005 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 *
 */
package com.idega.core.accesscontrol.business;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.FinderException;
import javax.servlet.http.HttpServletRequest;

import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.accesscontrol.data.bean.PermissionGroup;
import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.component.data.bean.ICObject;
import com.idega.core.file.data.bean.ICFile;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

/**
 * <p>
 * This is the main service interface for the old generation permission system
 * in idegaWeb based around the ICPermission entity (IC_PERMISSION table).
 * </p>
 * Last modified: $Date: 2009/06/24 08:58:18 $ by $Author: valdas $
 *
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.38 $
 */
public interface AccessController extends com.idega.idegaweb.IWService {

	public static final String CATEGORY_STRING_OBJECT_INSTANCE_ID = "ic_object_instance_id";
	public static final String CATEGORY_STRING_IC_OBJECT_ID = "ic_object_id";
	public static final String CATEGORY_STRING_BUNDLE_IDENTIFIER = "iw_bundle_identifier";
	public static final String CATEGORY_STRING_PAGE_ID = "page_id";
	public static final String CATEGORY_STRING_PAGE = "page";//don't know what this is for
	public static final String CATEGORY_STRING_JSP_PAGE = "jsp_page";
	public static final String CATEGORY_STRING_FILE_ID = "ic_file_id";
	public static final String CATEGORY_STRING_GROUP_ID = "ic_group_id";

	public static final String _PARAMETERSTRING_IDENTIFIER = "ic_permissionobj_identifier";
	public static final String _PARAMETERSTRING_PERMISSION_CATEGORY = "ic_permission_category";

	public static final String PERMISSION_KEY_ROLE_MASTER = "role_master";

	public static final String PERMISSION_KEY_VIEW = "view";
	public static final String PERMISSION_KEY_EDIT = "edit";
	public static final String PERMISSION_KEY_DELETE = "delete";
	public static final String PERMISSION_KEY_CREATE = "create";
	public static final String PERMISSION_KEY_OWNER = "owner";
	public static final String PERMISSION_KEY_PERMIT = "permit";
	public static final String PERMISSION_KEY_ROLE = "role_permission";

	public static final int CATEGORY_OBJECT_INSTANCE = 0;
	public static final int CATEGORY_OBJECT = 1;
	public static final int CATEGORY_BUNDLE = 2;
	public static final int CATEGORY_PAGE_INSTANCE = 3;
	public static final int CATEGORY_PAGE = 4;
	public static final int CATEGORY_JSP_PAGE = 5;
	public static final int CATEGORY_FILE_ID = 6;
	public static final int CATEGORY_GROUP_ID = 7;
	public static final int CATEGORY_ROLE = 8;

	public Integer getUsersGroupID();
	public Integer getEveryoneGroupID();

	public PermissionGroup getPermissionGroupEveryOne() throws Exception;
	public PermissionGroup getPermissionGroupUsers() throws Exception;
	public PermissionGroup getPermissionGroupAdministrator() throws Exception;

	public boolean isAdmin(IWUserContext iwc)throws Exception;
	public boolean isOwner(Object obj , IWUserContext iwc) throws Exception;
	public boolean isOwner(ICFile file, IWUserContext iwc)throws Exception;
	public boolean isOwner(Group group, IWUserContext iwc)throws Exception;
	public boolean isOwner(ICPage page, IWUserContext iwc)throws Exception;
	public boolean isOwner(ICObject obj, int entityRecordId, IWUserContext iwc)throws Exception;

	public void setAsOwner(PresentationObject obj, int groupId, IWApplicationContext iwac) throws Exception ;
	public void setAsOwner(ICFile file, int groupId, IWApplicationContext iwac)throws Exception;

	@Deprecated
	public void setAsOwner(com.idega.user.data.Group group, int groupId, IWApplicationContext iwac)throws Exception;
	public void setAsOwner(Group group, int groupId, IWApplicationContext iwac)throws Exception;

	public void setAsOwner(ICPage page, int groupId, IWApplicationContext iwac)throws Exception;
	public void setAsOwner(ICObject obj, int entityRecordId, int groupId, IWApplicationContext iwac)throws Exception;

	@Deprecated
	public void setCurrentUserAsOwner(com.idega.core.builder.data.ICPage page, IWUserContext iwc) throws Exception;
	public void setCurrentUserAsOwner(ICPage page,IWUserContext iwc)throws Exception;

	public boolean hasPermission(String permissionKey, Object obj,IWUserContext iwc) throws Exception;
	public boolean hasPermissionForGroup(String permissionKey, Group obj,IWUserContext iwc) throws Exception;

	public boolean hasFilePermission(String permissionKey, int id, IWUserContext iwc)throws Exception;

	@Deprecated
	public boolean hasDataPermission(String permissionKey, com.idega.core.component.data.ICObject obj, int entityRecordId, IWUserContext iwc) throws Exception;
	public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId, IWUserContext iwc) throws Exception;

	public boolean hasPermission(List<Integer> groupIds,String permissionType, Object obj,IWUserContext iwc) throws Exception;

	@Deprecated
	public boolean hasCreatePermissionFor(com.idega.user.data.Group group,IWUserContext iwuc);
	public boolean hasCreatePermissionFor(Group group,IWUserContext iwuc);

	@Deprecated
	public boolean hasDeletePermissionFor(com.idega.user.data.Group group,IWUserContext iwuc);
	public boolean hasDeletePermissionFor(Group group,IWUserContext iwuc);

	public boolean hasViewPermission(PresentationObject obj,IWUserContext iwc);
	public void setJSPPagePermission(IWUserContext iwc, Group group, String PageContextValue, String permissionType, Boolean permissionValue)throws Exception;
	public void setObjectPermission(IWUserContext iwc, Group group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception;
	public void setBundlePermission(IWUserContext iwc, Group group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception;
	public void setObjectInstacePermission(IWUserContext iwc, Group group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception;
	public ICPermission setPermission(int permissionCategory, IWApplicationContext iwac, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue)throws Exception;
	public void setObjectInstacePermission(IWUserContext iwc, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue)throws Exception;
	public int createPermissionGroup(String GroupName, String Description, String ExtraInfo, int[] userIDs, int[] groupIDs)throws Exception;
	public List<Group> getAllPermissionGroups()throws Exception;
	public List<Group> getStandardGroups() throws Exception;

	@Deprecated
	public com.idega.user.data.User getAdministratorUserLegacy() throws Exception;
	public User getAdministratorUser() throws Exception;

	public List<Group> getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws Exception;

	public String[] getICObjectPermissionKeys(Class<?> ICObject);
	public String[] getBundlePermissionKeys(Class<?> ICObject);
	public String[] getBundlePermissionKeys(String BundleIdentifier);
	public String[] getPagePermissionKeys();

	@Deprecated
	public boolean hasEditPermissionFor(com.idega.user.data.Group group,IWUserContext iwuc);
	public boolean hasEditPermissionFor(Group group,IWUserContext iwuc);

	@Deprecated
	public boolean hasViewPermissionFor(com.idega.user.data.Group group,IWUserContext iwuc);
	public boolean hasViewPermissionFor(Group group,IWUserContext iwuc);

//  public boolean hasEditPermission(PresentationObject obj,IWUserContext iwc)throws Exception;
//  public boolean hasViewPermission(List groupIds, PresentationObject obj,IWUserContext iwc);

  public boolean hasPermitPermissionFor(Group group, IWUserContext iwuc);
  public boolean hasRole(String roleKey, IWUserContext iwuc);
  public boolean hasRole(String roleKey, Group group, IWUserContext iwuc);
  public boolean hasRole(User user, String roleKey);

  /**
   *
   * @param user to check access for, not <code>null</code>;
   * @param roleKeys is {@link ICRole}s of {@link Group}s, which should be
   * checked for, not <code>null</code>;
   * @return <code>true</code> if user has at least one of these roles,
   * <code>false</code> otherwise;
   * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
   */
  public boolean hasRole(User user, Collection<String> roleKeys);
  public boolean isRoleMaster(IWUserContext iwuc);

  @Deprecated
  public void addGroupAsRoleMaster(com.idega.user.data.Group group, IWApplicationContext iwma);
  public void addGroupAsRoleMaster(Group group, IWApplicationContext iwac);

  @Deprecated
  public void addRoleToGroup(String roleKey, com.idega.user.data.Group group, IWApplicationContext iwac                  );
  public void addRoleToGroup(String roleKey, Group group, IWApplicationContext iwac                  );
  public boolean addRoleToGroup(String roleKey, Integer groupId, IWApplicationContext iwac                  );
  public boolean addRoleToGroup(String roleKey, String permissionKey, Integer groupId, IWApplicationContext iwac                  );

  public Collection<ICPermission> getAllRolesForGroup(Group group);

  public Collection<ICPermission> getAllRolesWithRolePermissionsForGroup(Group group);
  public Collection<ICPermission> getAllRolesWithRolePermissionsForGroupCollection(Collection<Group> groups);

  public Collection<ICRole> getAllRoles();
  public Collection<Group> getAllGroupsThatAreRoleMasters(IWApplicationContext iwac                  );
  public Collection<Group> getAllGroupsForRoleKey(String roleKey, IWApplicationContext iwac                  );

  @Deprecated
  public void removeGroupFromRoleMastersList(com.idega.user.data.Group group, IWApplicationContext iwac                  );
  public void removeGroupFromRoleMastersList(Group group, IWApplicationContext iwac                  );

  public boolean removeRoleFromGroup(String roleKey, Integer groupId, IWApplicationContext iwac                  );
  public boolean removeRoleFromGroup(String roleKey,String permissionKey, Integer groupId, IWApplicationContext iwac                  );
  public boolean removeRoleFromGroup(String roleKey, Group group, IWApplicationContext iwac                  );
  public ICRole createRoleWithRoleKey(String roleKey);
  public ICRole getRoleByRoleKey(String roleKey);
  public String getRoleIdentifier();
  public Set<String> getAllRolesForCurrentUser(IWUserContext iwc);
  public Set<String> getAllRolesForUser(User user);
  public Collection<Group> getAllUserGroupsForRoleKey(String roleKey, IWUserContext iwuc, User user);
  public Collection<String> getAllRolesKeysForGroup(Group group);

/*
  public static List getPermissionGroups(User user) throws Exception;
  public static List getPermissionGroups(GenericGroup group) throws Exception;
  public static List getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws Exception;
  public static void addUserToPermissionGroup(PermissionGroup group, int userIDtoAdd) throws Exception;
  public static void addGroupToPermissionGroup(PermissionGroup group, int groupIDtoAdd)throws Exception;
  public static boolean hasAdminPermission(PresentationObject obj,IWUserContext iwc)throws Exception;
  public static boolean hasIdegaAdminPermission(PresentationObject obj,IWUserContext iwc)throws Exception;
  public static boolean hasOwnerPermission(PresentationObject obj,IWUserContext iwc)throws Exception;
  public static boolean removePermissionRecords(int permissionCategory, IWUserContext iwc, String ObjectInstanceId, String permissionKey, String[] groupsToRemove);
*/

//  private String[] getPermissionGroupFilter();  //?

  public boolean hasRole(String roleKey, com.idega.user.data.Group group, IWUserContext iwuc);
  public Collection<com.idega.core.accesscontrol.data.ICRole> getAllRolesLegacy();
  public Collection<com.idega.user.data.Group> getAllGroupsForRoleKeyLegacy(String roleKey, IWApplicationContext iwac);
  public Collection<com.idega.core.accesscontrol.data.ICPermission> getAllRolesWithRolePermissionsForGroup(com.idega.user.data.Group group);
  public boolean hasRole(com.idega.user.data.User user, String roleKey);
  public Set<String> getAllRolesForUser(com.idega.user.data.User user);
  public Collection<String> getAllRolesKeysForGroup(com.idega.user.data.Group group);
  public Collection<com.idega.core.accesscontrol.data.ICPermission> getAllRolesForGroup(com.idega.user.data.Group group);
  public boolean hasPermitPermissionFor(com.idega.user.data.Group group, IWUserContext iwuc);
  public com.idega.core.accesscontrol.data.ICRole getRoleByRoleKeyOld(String roleKey) throws FinderException;

  /**
   * Check for "view" or read access for a page by URI
   */
  public boolean hasViewPermissionForPageURI(String pageUri,HttpServletRequest request);
  public boolean hasViewPermissionForPageKey(String pageKey,IWUserContext iwuc);

  public boolean checkIfRoleExistsInDataBaseAndCreateIfMissing(String roleKey);
}