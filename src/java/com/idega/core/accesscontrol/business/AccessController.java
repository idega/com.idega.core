package com.idega.core.accesscontrol.business;

import com.idega.core.user.data.User;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICObject;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import java.util.List;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface AccessController extends com.idega.idegaweb.IWService{

  public static final String _CATEYGORYSTRING_OBJECT_INSTATNCE_ID = "ic_object_instance_id";
  public static final String _CATEYGORYSTRING_OBJECT_ID = "ic_object_id";
  public static final String _CATEYGORYSTRING_BUNDLE_IDENTIFIER = "iw_bundle_identifier";
  public static final String _CATEYGORYSTRING_PAGE_ID = "page_id";
  public static final String _CATEYGORYSTRING_PAGE = "page";
  public static final String _CATEYGORYSTRING_JSP_PAGE = "jsp_page";

  public static final String _CATEYGORYSTRING_FILE_ID = "ic_file_id";
  public static final String _CATEYGORYSTRING_ENTITY = _CATEYGORYSTRING_OBJECT_ID; //?
  public static final String _CATEYGORYSTRING_ENTITY_RECORD_ID = "ic_entity_record_id";

  public static final String _PARAMETERSTRING_IDENTIFIER = "ic_permissionobj_identifier";
  public static final String _PARAMETERSTRING_PERMISSION_CATEGORY = "ic_permission_category";

  public static final String _PERMISSIONKEY_VIEW = "view";
  public static final String _PERMISSIONKEY_EDIT = "edit";
  public static final String _PERMISSIONKEY_ADMIN = "admin";
  public static final String _PERMISSIONKEY_OWNER = "owner";

  public static final int _CATEGORY_OBJECT_INSTANCE = 0;
  public static final int _CATEGORY_OBJECT = 1;
  public static final int _CATEGORY_BUNDLE = 2;
  public static final int _CATEGORY_PAGE_INSTANCE = 3;
  public static final int _CATEGORY_PAGE = 4;
  public static final int _CATEGORY_JSP_PAGE = 5;


  public PermissionGroup getPermissionGroupEveryOne() throws Exception ;
  public PermissionGroup getPermissionGroupUsers() throws Exception ;
  public PermissionGroup getPermissionGroupAdministrator() throws Exception ;

  public boolean isAdmin(IWContext iwc)throws Exception;
  public boolean isOwner(PresentationObject obj , IWContext iwc) throws Exception ;
  public boolean isOwner(ICFile file, IWContext iwc)throws Exception;
  public boolean isOwner(ICObject obj, int entityRecordId, IWContext iwc)throws Exception;

  public void setAsOwner(PresentationObject obj , IWContext iwc) throws Exception ;
  public void setAsOwner(ICFile file, IWContext iwc)throws Exception;
  public void setAsOwner(ICObject obj, int entityRecordId, IWContext iwc)throws Exception;


  public boolean hasPermission(String permissionKey, PresentationObject obj,IWContext iwc) throws Exception;
  public boolean hasPermission(String permissionKey, ICObject obj, IWContext iwc) throws Exception;
  public boolean hasPermission(String permissionKey, int category, String identifier, IWContext iwc) throws Exception;
  public boolean hasFilePermission(String permissionKey, int id, IWContext iwc)throws Exception;
  //temp public boolean hasDataPermission(String permissionKey, Class entity, int entityRecordId, IWContext iwc)throws Exception;
  //temp public boolean hasDataPermission(String permissionKey, Class entity, IWContext iwc)throws Exception;
  public boolean hasDataPermission(String permissionKey, ICObject obj, int entityRecordId, IWContext iwc) throws Exception;

  //public boolean hasPermission(Class someClass, int id, IWContext iwc) throws Exception;

  public boolean hasPermission(List groupIds,String permissionType, PresentationObject obj,IWContext iwc) throws Exception;
//  public boolean hasEditPermission(PresentationObject obj,IWContext iwc)throws Exception;
//  public boolean hasViewPermission(PresentationObject obj,IWContext iwc);
//  public boolean hasViewPermission(List groupIds, PresentationObject obj,IWContext iwc);
  public void setJSPPagePermission(IWContext iwc, PermissionGroup group, String PageContextValue, String permissionType, Boolean permissionValue)throws Exception;
  public void setObjectPermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception;
  public void setBundlePermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception;
  public void setObjectInstacePermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception;
  public void setPermission(int permissionCategory, IWContext iwc, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue)throws Exception;
  public void setObjectInstacePermission(IWContext iwc, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue)throws Exception;
  public int createPermissionGroup(String GroupName, String Description, String ExtraInfo, int[] userIDs, int[] groupIDs)throws Exception;
  public List getAllPermissionGroups()throws Exception ;
  public List getStandardGroups() throws Exception ;
  public User getAdministratorUser()throws Exception;
  public List getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws Exception;



  public String[] getICObjectPermissionKeys(Class ICObject);
  public String[] getBundlePermissionKeys(Class ICObject);
  public String[] getBundlePermissionKeys(String BundleIdentifier);
  public String[] getPagePermissionKeys();

/*
  public static List getPermissionGroups(User user) throws Exception;
  public static List getPermissionGroups(GenericGroup group) throws Exception;
  public static List getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws Exception;
  public static void addUserToPermissionGroup(PermissionGroup group, int userIDtoAdd) throws Exception;
  public static void addGroupToPermissionGroup(PermissionGroup group, int groupIDtoAdd)throws Exception;
  public static boolean hasAdminPermission(PresentationObject obj,IWContext iwc)throws Exception;
  public static boolean hasIdegaAdminPermission(PresentationObject obj,IWContext iwc)throws Exception;
  public static boolean hasOwnerPermission(PresentationObject obj,IWContext iwc)throws Exception;
  public static boolean removePermissionRecords(int permissionCategory, IWContext iwc, String ObjectInstanceId, String permissionKey, String[] groupsToRemove);
*/

//  private String[] getPermissionGroupFilter();  //?



}
