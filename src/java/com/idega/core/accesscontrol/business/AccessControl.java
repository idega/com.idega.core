package com.idega.core.accesscontrol.business;

import java.util.List;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;
import com.idega.presentation.*;
import com.idega.block.login.business.*;
import com.idega.core.data.*;
import com.idega.core.user.data.User;
import com.idega.data.EntityFinder;
import com.idega.core.accesscontrol.data.*;
import com.idega.core.business.*;
import com.idega.core.user.business.UserBusiness;
import com.idega.util.idegaTimestamp;
import com.idega.data.SimpleQuerier;
import com.idega.util.EncryptionType;
import com.idega.idegaweb.IWServiceImpl;
import com.idega.idegaweb.IWServiceNotStartedException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.core.user.data.UserGroupRepresentative;




/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author       <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AccessControl extends IWServiceImpl implements AccessController {
/**
 * @todo change next 4 variables to applicationAddesses
 */
  private PermissionGroup AdministratorPermissionGroup = null;
  private PermissionGroup PermissionGroupEveryOne = null;
  private PermissionGroup PermissionGroupUsers = null;
  private List standardGroups = null;

  private static final String _APPADDRESS_ADMINISTRATOR_USER = "ic_super_admin";
  private static final String _ADMINISTRATOR_NAME = "Administrator";


  private static final int _GROUP_ID_EVERYONE = -7913;
  private static final int _GROUP_ID_USERS = -1906;

  private static final int _notBuilderPageID = 0;

  //temp
  private static ICObject staticPageICObject = null;



  private void initAdministratorPermissionGroup() throws Exception {
    PermissionGroup permission = new PermissionGroup();
    permission.setName(AccessControl.getAdministratorGroupName());
    permission.setDescription("Administrator permission");
    permission.insert();
    AdministratorPermissionGroup = permission;
  }

  private void initPermissionGroupEveryone() throws Exception {
    PermissionGroup permission = new PermissionGroup();
    permission.setID(_GROUP_ID_EVERYONE);
    permission.setName("Everyone");
    permission.setDescription("Permission if not logged on");
    permission.insert();
    PermissionGroupEveryOne = permission;
  }

  private void initPermissionGroupUsers() throws Exception {
    PermissionGroup permission = new PermissionGroup();
    permission.setID(_GROUP_ID_USERS);
    permission.setName("Users");
    permission.setDescription("Permission if logged on");
    permission.insert();
    PermissionGroupUsers = permission;
  }

  public PermissionGroup getPermissionGroupEveryOne() throws Exception {
    if(PermissionGroupEveryOne == null){
      initPermissionGroupEveryone();
    }
    return PermissionGroupEveryOne;
  }

  public PermissionGroup getPermissionGroupUsers() throws Exception {
    if(PermissionGroupUsers == null){
      initPermissionGroupUsers();
    }
    return PermissionGroupUsers;
  }

  public PermissionGroup getPermissionGroupAdministrator() throws Exception {
    if(AdministratorPermissionGroup == null){
      initAdministratorPermissionGroup();
    }
    return AdministratorPermissionGroup;
  }

  public boolean isAdmin(IWContext iwc)throws Exception{
    try {
      Object ob = LoginBusiness.getLoginAttribute(getAdministratorGroupName(), iwc);
      if(ob != null){
        return ((Boolean)ob).booleanValue();
      }else{
        if(getAdministratorUser().equals(LoginBusiness.getUser(iwc))){
          LoginBusiness.setLoginAttribute(getAdministratorGroupName(),Boolean.TRUE,iwc);
          return true;
        }
        List groups = LoginBusiness.getPermissionGroups(iwc);
        if (groups != null){
          Iterator iter = groups.iterator();
          while (iter.hasNext()) {
            GenericGroup item = (GenericGroup)iter.next();
            if (getAdministratorGroupName().equals(item.getName())){
              LoginBusiness.setLoginAttribute(getAdministratorGroupName(),Boolean.TRUE,iwc);
              return true;
            }
          }
        }
      }
      LoginBusiness.setLoginAttribute(getAdministratorGroupName(),Boolean.FALSE,iwc);
      return false;
    }
    catch (NotLoggedOnException ex) {
      return false;
    }
  }

  /**
   * @todo page ownership
   */
  public boolean isOwner(PresentationObject obj , IWContext iwc) throws Exception {
    User user = LoginBusiness.getUser(iwc);
    if(user == null){
      return false;
    }
    Boolean myPermission = null;
    String permissionType = AccessControl._PERMISSIONKEY_OWNER;
    List permissionGroup = new Vector();
    permissionGroup.add(Integer.toString(user.getGroupID()));
    if (obj == null){ // JSP page
      myPermission = PermissionCacher.hasPermissionForJSPPage(obj,iwc,permissionType,permissionGroup);
    } else { // if (obj != null)
      if(obj instanceof Page && ((Page)obj).getPageID() != _notBuilderPageID){
        myPermission = PermissionCacher.hasPermissionForPage(obj,iwc,permissionType,permissionGroup);
      }else{
        //instance
        myPermission = PermissionCacher.hasPermissionForObjectInstance(obj,iwc,permissionType,permissionGroup);
        //instance
      }
      if(myPermission != null){
        return true;
      }
    }
    return false;
  }


  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getAdministratorGroupName(){
    return "administrator";
  }



  public boolean hasPermission(String permissionType, PresentationObject obj,IWContext iwc) throws Exception{
    Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked
/*
    // Default permission: view == true if not Page, else false
    // If no permission set, view = true for all objects other than Page objects
    if( _PERMISSIONKEY_VIEW.equals(permissionType) && obj != null && !(obj instanceof Page && ((Page)obj).getPageID() != _notBuilderPageID) ){
      // if some view permission for object, bundle, ... are set
      // then do nothing
      // else true
      // => view hashtable for obj, ...  has object
      if(!PermissionCacher.somePermissionSet( obj, iwc, permissionType)){
        return true;
      }
    }
*/
    if (isAdmin(iwc)){
      return true;
    }

    User user = LoginBusiness.getUser(iwc);
    ICPermission permission = ICPermission.getStaticInstance();
    ICPermission[] Permissions = null;
    List groups = null;
    List tempGroupList = new Vector();
    List[] permissionOrder = null; // Everyone, users, user, primaryGroup, otherGroups

    if (user == null){
      permissionOrder = new List[1];
      permissionOrder[0] = new Vector();
      permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
    } else {

      groups = LoginBusiness.getPermissionGroups(iwc);
      GenericGroup primaryGroup = LoginBusiness.getPrimaryGroup(iwc);

      if (groups != null && groups.size() > 0){
        if(primaryGroup != null){
          groups.remove(primaryGroup);
        }
        List groupIds = new Vector();
        Iterator iter = groups.iterator();
        while (iter.hasNext()) {
          groupIds.add(Integer.toString(((GenericGroup)iter.next()).getID()));
        }
        permissionOrder = new List[5];
        permissionOrder[4] = groupIds;
      } else {
        permissionOrder = new List[4];
      }
        permissionOrder[0] = new Vector();
        permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
        permissionOrder[1] = new Vector();
        permissionOrder[1].add( Integer.toString(getPermissionGroupUsers().getID()) );
        permissionOrder[2] = new Vector();
        permissionOrder[2].add( Integer.toString(user.getGroupID()) );
        permissionOrder[3] = new Vector();
        permissionOrder[3].add( Integer.toString(user.getPrimaryGroupID()) );
        // Everyone, user, primaryGroup, otherGroups
    }
    myPermission = checkForPermission(permissionOrder, obj, permissionType, iwc);
    if(myPermission != null){
      return myPermission.booleanValue();
    }
    return false;
  } // method hasPermission



  public boolean hasPermission(List groupIds,String permissionType, PresentationObject obj,IWContext iwc) throws Exception{
    Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked
/*
    // Default permission: view == true if not Page, else false
    // If no permission set, view = true for all objects other than Page objects
    if( _PERMISSIONKEY_VIEW.equals(permissionType) && obj != null && !(obj instanceof Page && ((Page)obj).getPageID() != _notBuilderPageID) ){
      // if some view permission for object, bundle, ... are set
      // then do nothing
      // else true
      // => view hashtable for obj, ...  has object
      if(!PermissionCacher.somePermissionSet( obj, iwc, permissionType)){
        return true;
      }
    }
*/
    ICPermission permission = ICPermission.getStaticInstance();
    ICPermission[] Permissions = null;
    List groups = null;
    List[] permissionOrder = null; // Everyone, users, (primaryGroup), otherGroups

    if(groupIds != null){
      if (groupIds.contains(Integer.toString(getPermissionGroupAdministrator().getID()))){
        return true;
      } else {
        if(groupIds.size() == 1){
          if(groupIds.get(0).equals(Integer.toString(_GROUP_ID_EVERYONE))){
            permissionOrder = new List[1];
            permissionOrder[0] = new Vector();
            permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
          }else {
            if(groupIds.get(0).equals(Integer.toString(_GROUP_ID_USERS))){
              permissionOrder = new List[2];
            } else{
              permissionOrder = new List[3];
              permissionOrder[2] = groupIds;
            }
              permissionOrder[0] = new Vector();
              permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
              permissionOrder[1] = new Vector();
              permissionOrder[1].add( Integer.toString(getPermissionGroupUsers().getID()) );
          }
        } else if (groupIds.size() > 1){
            permissionOrder = new List[3];
            permissionOrder[0] = new Vector();
            permissionOrder[0].add( Integer.toString(getPermissionGroupEveryOne().getID()) );
            permissionOrder[1] = new Vector();
            permissionOrder[1].add( Integer.toString(getPermissionGroupUsers().getID()) );
            permissionOrder[2] = groupIds;
            // Everyone, users, (primaryGroup), otherGroups
        } else {
          return false;
        }
      }
    } else{
        return false;
    }
    myPermission = checkForPermission(permissionOrder, obj, permissionType, iwc);
    if(myPermission != null){
      return myPermission.booleanValue();
    }
    return false;
  } // method hasPermission




  private static Boolean checkForPermission(List[] permissionGroupLists, PresentationObject obj, String permissionType, IWContext iwc ) throws Exception {
    Boolean myPermission = null;
    if(permissionGroupLists != null){
      int arrayLength = permissionGroupLists.length;
      if (obj == null){ // JSP page
        for (int i = 0; i < arrayLength; i++) {
          myPermission = PermissionCacher.hasPermissionForJSPPage(obj,iwc,permissionType,permissionGroupLists[i]);
          if(myPermission != null){
            return myPermission;
          }
        }

        return myPermission;
      } else { // if (obj != null)

        if(obj instanceof Page && ((Page)obj).getPageID() != _notBuilderPageID ){
          for (int i = 0; i < arrayLength; i++) {
            myPermission = PermissionCacher.hasPermissionForPage(obj,iwc,permissionType,permissionGroupLists[i]);
            if(myPermission != null){
              return myPermission;
            }
          }

          // Global - (Page)
          /**
           * @todo setja stopp ef einhver réttindi hafa verið sett á síðu instance
           */
          //if(!permissionType.equals(_PERMISSIONKEY_VIEW) || (!PermissionCacher.anyInstancePerissionsDefined(obj,iwc,permissionType) && permissionType.equals(_PERMISSIONKEY_VIEW)) ){
            ICObject page = getStaticPageICObject();
            if(page != null){
              for (int i = 0; i < arrayLength; i++) {
                myPermission = PermissionCacher.hasPermission(page,iwc,permissionType,permissionGroupLists[i]);
                if(myPermission != null){
                  return myPermission;
                }
              }
            }
          //}
          // Global - (Page)

          return myPermission;
        }else{
          //instance
          for (int i = 0; i < arrayLength; i++) {
            myPermission = PermissionCacher.hasPermissionForObjectInstance(obj,iwc,permissionType,permissionGroupLists[i]);
            if(myPermission != null){
              return myPermission;
            }
          }

          //instance

          // Global - (object)
          //if(!permissionType.equals(_PERMISSIONKEY_VIEW) || (!PermissionCacher.anyInstancePerissionsDefined(obj,iwc,permissionType) && permissionType.equals(_PERMISSIONKEY_VIEW)) ){
          if(!PermissionCacher.anyInstancePerissionsDefined(obj,iwc,permissionType)){
            for (int i = 0; i < arrayLength; i++) {
              myPermission = PermissionCacher.hasPermissionForObject(obj,iwc,permissionType,permissionGroupLists[i]);
              if(myPermission != null){
                return myPermission;
              }
            }
          }
          // Global - (object)


  /*
          // Bundle
          for (int i = 0; i < arrayLength; i++) {
            myPermission = PermissionCacher.hasPermissionForBundle(obj,iwc,permissionType,permissionGroupLists[i]);
            if(myPermission != null){
              return myPermission;
            }
          }// Bundle
  */
          return myPermission;
        }
      }
    }
    return myPermission;
  }


  //temp
  private static ICObject getStaticPageICObject(){
    if(staticPageICObject == null){
      try {
        staticPageICObject = (ICObject)EntityFinder.findAllByColumn((ICObject)ICObject.getStaticInstance(ICObject.class),ICObject.getClassNameColumnName(),Page.class.getName()).get(0);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return staticPageICObject;
  }


//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getObjectInstanceIdString(){
//    return "ic_object_instance_id";
//  }
//
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getObjectIdString(){
//    return "ic_object_id";
//  }
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getBundleIdentifierString(){
//    return "iw_bundle_identifier";
//  }
//
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getPageIdString(){
//    return "page_id";
//  }
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getPageString(){
//    return "page";
//  }
//
//  /**
//   * use this method when writing to database to avoid errors in database.
//   * If the name-string changes this will be the only method to change.
//   */
//  public static String getJSPPageString(){
//    return "jsp_page";
//  }
//


  public boolean hasEditPermission(PresentationObject obj,IWContext iwc)throws Exception{
    return hasPermission( _PERMISSIONKEY_EDIT , obj, iwc);
  }


  public boolean hasViewPermission(PresentationObject obj,IWContext iwc){
    try {
      /*boolean permission = hasPermission( _PERMISSIONKEY_VIEW, obj, iwc);
      System.err.println(obj.getClass().getName()+" has permission: " + permission);
      return permission;
      */
      return hasPermission( _PERMISSIONKEY_VIEW, obj, iwc);
    }
    catch (Exception ex) {
      return false;
    }
  }

  public boolean hasViewPermission(List groupIds, PresentationObject obj,IWContext iwc){
    try {
      /*boolean permission = hasPermission( _PERMISSIONKEY_VIEW, obj, iwc);
      System.err.println(obj.getClass().getName()+" has permission: " + permission);
      return permission;
      */
      return hasPermission(groupIds, _PERMISSIONKEY_VIEW, obj, iwc);
    }
    catch (Exception ex) {
      return false;
    }
  }


  public boolean hasAdminPermission(PresentationObject obj,IWContext iwc)throws Exception{
    return hasPermission( _PERMISSIONKEY_ADMIN, obj, iwc);
  }

  public boolean hasOwnerPermission(PresentationObject obj,IWContext iwc)throws Exception{
    return hasPermission( _PERMISSIONKEY_OWNER, obj, iwc);
  }

/*  public static ICObjectPermission[] getPermissionTypes(PresentationObject obj)throws Exception{
    int arobjID = obj.getICObject().getID();
    List permissions =  EntityFinder.findAllByColumn(ICObjectPermission.getStaticInstance(), ICObjectPermission.getPermissionTypeColumnName(), arobjID);
    if (permissions != null){
      return (ICObjectPermission[])permissions.toArray((Object[])new ICObjectPermission[0]);
    }else{
      return null;
    }
  }
*/


  public void setJSPPagePermission(IWContext iwc, PermissionGroup group, String PageContextValue, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_JSP_PAGE + "' AND " + permission.getContextValueColumnName() + " = '" + PageContextValue + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl._CATEYGORYSTRING_JSP_PAGE);
      // use 'ICJspHandler.getJspPageInstanceID(iwc)' on the current page and send in as PageContextValue
      permission.setContextValue(PageContextValue);
      permission.setGroupID(new Integer(group.getID()));
      permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateJSPPagePermissions(PageContextValue,permissionType,iwc);
  }

  public void setObjectPermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_ID + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getICObjectID(iwc) + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl._CATEYGORYSTRING_OBJECT_ID);
      permission.setContextValue(Integer.toString(obj.getICObjectID(iwc)));
      permission.setGroupID(new Integer(group.getID()));
      permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateObjectPermissions(Integer.toString(obj.getICObjectID(iwc)),permissionType,iwc);
  }


  public void setBundlePermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_BUNDLE_IDENTIFIER + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getBundleIdentifier() + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl._CATEYGORYSTRING_BUNDLE_IDENTIFIER);
      permission.setContextValue(obj.getBundleIdentifier());
      permission.setGroupID(new Integer(group.getID()));
      permission.setPermissionString(permissionType);
  //        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateBundlePermissions(obj.getBundleIdentifier(),permissionType,iwc);
  }



  public void setObjectInstacePermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws Exception{
    setObjectInstacePermission(iwc,Integer.toString(group.getID()),Integer.toString(obj.getICObjectInstance(iwc).getID()),permissionType,permissionValue);
  }

  public static boolean removeICObjectInstancePermissionRecords(IWContext iwc, String ObjectInstanceId, String permissionKey, String[] groupsToRemove){
    String sGroupList = "";
    if (groupsToRemove != null && groupsToRemove.length > 0){
      for(int g = 0; g < groupsToRemove.length; g++){
        if(g>0){ sGroupList += ", "; }
        sGroupList += groupsToRemove[g];
      }
    }
    if(!sGroupList.equals("")){
      ICPermission permission = ICPermission.getStaticInstance();
      try {
        boolean done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_INSTATNCE_ID + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
        if(done){
          PermissionCacher.updateObjectInstancePermissions(ObjectInstanceId,permissionKey,iwc);
        }
        return done;
      }
      catch (Exception ex) {
        return false;
      }
    } else {
      return true;
    }

  }


  public static boolean removePermissionRecords(int permissionCategory, IWContext iwc, String identifier, String permissionKey, String[] groupsToRemove){
    String sGroupList = "";
    if (groupsToRemove != null && groupsToRemove.length > 0){
      for(int g = 0; g < groupsToRemove.length; g++){
        if(g>0){ sGroupList += ", "; }
        sGroupList += groupsToRemove[g];
      }
    }
    if(!sGroupList.equals("")){
      ICPermission permission = ICPermission.getStaticInstance();
      try {
        boolean done = false;

        switch (permissionCategory) {
          case AccessControl._CATEGORY_OBJECT_INSTANCE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_INSTATNCE_ID + "' AND " + permission.getContextValueColumnName() + " = " + identifier + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_OBJECT :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_ID + "' AND " + permission.getContextValueColumnName() + " = " + identifier + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_BUNDLE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_BUNDLE_IDENTIFIER + "' AND " + permission.getContextValueColumnName() + " = " + identifier + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_PAGE_INSTANCE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_PAGE_ID + "' AND " + permission.getContextValueColumnName() + " = " + identifier + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_PAGE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_PAGE + "' AND " + permission.getContextValueColumnName() + " = " + identifier + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_JSP_PAGE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_JSP_PAGE + "' AND " + permission.getContextValueColumnName() + " = " + identifier + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
        }

        PermissionCacher.updatePermissions(permissionCategory,identifier,permissionKey,iwc);

        return true;
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    } else {
      return true;
    }

  }



  public void setPermission(int permissionCategory, IWContext iwc, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue)throws Exception{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      switch (permissionCategory) {
        case AccessControl._CATEGORY_OBJECT_INSTANCE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_INSTATNCE_ID + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_OBJECT :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_ID + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_BUNDLE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_BUNDLE_IDENTIFIER + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_PAGE_INSTANCE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_PAGE_ID + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_PAGE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_PAGE + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_JSP_PAGE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_JSP_PAGE + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
      }

    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){

      switch (permissionCategory) {
        case AccessControl._CATEGORY_OBJECT_INSTANCE :
          permission.setContextType(AccessControl._CATEYGORYSTRING_OBJECT_INSTATNCE_ID);
          break;
        case AccessControl._CATEGORY_OBJECT :
          permission.setContextType(AccessControl._CATEYGORYSTRING_OBJECT_ID);
          break;
        case AccessControl._CATEGORY_BUNDLE :
          permission.setContextType(AccessControl._CATEYGORYSTRING_BUNDLE_IDENTIFIER);
          break;
        case AccessControl._CATEGORY_PAGE_INSTANCE :
          permission.setContextType(AccessControl._CATEYGORYSTRING_PAGE_ID);
          break;
        case AccessControl._CATEGORY_PAGE :
          permission.setContextType(AccessControl._CATEYGORYSTRING_PAGE);
          break;
        case AccessControl._CATEGORY_JSP_PAGE :
          permission.setContextType(AccessControl._CATEYGORYSTRING_JSP_PAGE);
          break;
      }

      permission.setContextValue(identifier);
      permission.setGroupID(new Integer(permissionGroupId));
      permission.setPermissionString(permissionKey);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updatePermissions(permissionCategory, identifier, permissionKey, iwc);
  }


  public void setObjectInstacePermission(IWContext iwc, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue)throws Exception{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_INSTATNCE_ID + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl._CATEYGORYSTRING_OBJECT_INSTATNCE_ID);
      permission.setContextValue(ObjectInstanceId);
      permission.setGroupID(new Integer(permissionGroupId));
      permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
      permission.setPermissionValue(permissionValue);
      permission.insert();
    } else{
      permission.setPermissionValue(permissionValue);
      permission.update();
    }
    PermissionCacher.updateObjectInstancePermissions(ObjectInstanceId,permissionType,iwc);
  }





  public int createPermissionGroup(String GroupName, String Description, String ExtraInfo, int[] userIDs, int[] groupIDs)throws Exception{
    PermissionGroup newGroup = new PermissionGroup();

    if(GroupName != null)
      newGroup.setName(GroupName);

    if(Description != null)
      newGroup.setDescription(Description);

    if(ExtraInfo != null)
      newGroup.setExtraInfo(ExtraInfo);

    newGroup.insert();

    int newGroupID = newGroup.getID();

    if(userIDs != null){
      for (int i = 0; i < userIDs.length; i++) {
        addUserToPermissionGroup(newGroup, userIDs[i]);
      }
    }
    if (groupIDs != null){
      for (int j = 0; j < groupIDs.length; j++) {
        addGroupToPermissionGroup(newGroup, groupIDs[j]);
      }
    }

    return newGroupID;

  }

  public static void addUserToPermissionGroup(PermissionGroup group, int userIDtoAdd) throws Exception{
    User userToAdd = new User(userIDtoAdd);
    group.addUser(userToAdd);
  }


  public static void addGroupToPermissionGroup(PermissionGroup group, int groupIDtoAdd)throws Exception{
    GenericGroup groupToAdd = new GenericGroup(groupIDtoAdd);
    group.addGroup(groupToAdd);
  }


  private static String[] getPermissionGroupFilter(){
    //filter begin
    String[] groupsToReturn = new String[1];
    groupsToReturn[0] = PermissionGroup.getStaticPermissionGroupInstance().getGroupTypeValue();
    //filter end
    return groupsToReturn;
  }

  public static List getPermissionGroups(User user) throws Exception{
    //temp - new GenericGroup()
    int groupId = user.getGroupID();
    if(groupId != -1){
      return getPermissionGroups(new GenericGroup(groupId));
    }else{
      return null;
    }
  }

  public static List getPermissionGroups(GenericGroup group) throws Exception{
    List permissionGroups = UserGroupBusiness.getGroupsContaining(group,getPermissionGroupFilter(),true);

    if(permissionGroups != null){
      return permissionGroups;
    }else {
      return null;
    }
  }

  public List getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws Exception {
    List toReturn = new Vector(0);
    ICPermission permission = ICPermission.getStaticInstance();
    List permissions = null;

    switch (permissionCategory) {
      case AccessControl._CATEGORY_OBJECT_INSTANCE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_INSTATNCE_ID + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_OBJECT :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_OBJECT_ID + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_BUNDLE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_BUNDLE_IDENTIFIER + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_PAGE_INSTANCE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_PAGE_ID + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_PAGE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_PAGE + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_JSP_PAGE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + _CATEYGORYSTRING_JSP_PAGE + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
    }

    if (permissions != null){
      Iterator iter = permissions.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        try {
          toReturn.add(new PermissionGroup(((ICPermission)item).getGroupID()));
        }
        catch (Exception ex) {
          System.err.println("Accesscontrol.getAllowedGroups(): Group not created for id "+((ICPermission)item).getGroupID());
        }

      }
    }
    toReturn.remove(AdministratorPermissionGroup);
    return toReturn;
  }


  public List getAllPermissionGroups()throws Exception {

    List permissionGroups = GenericGroup.getAllGroups(getPermissionGroupFilter(),true);
    if(permissionGroups != null){
      permissionGroups.remove(getPermissionGroupAdministrator());
    }

    return permissionGroups;
  }


  public List getStandardGroups() throws Exception {
    if(standardGroups == null){
      initStandardGroups();
    }
    return standardGroups;
  }

  private void initStandardGroups() throws Exception {
    standardGroups = new Vector();
    //standardGroups.add(AccessControl.getPermissionGroupAdministrator());
    standardGroups.add(this.getPermissionGroupEveryOne());
    standardGroups.add(this.getPermissionGroupUsers());
  }


  public User getAdministratorUser() throws Exception{
    Object ob = getApplication().getAttribute(_APPADDRESS_ADMINISTRATOR_USER);
    if(ob == null){
      try {
        initAdministratorUser();
        return (User)getApplication().getAttribute(_APPADDRESS_ADMINISTRATOR_USER);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return null;
      }


    }else{
      return (User)ob;
    }
  }

  private User createAdministratorUser()throws Exception{
    User adminUser = new User();
    adminUser.setColumn(User.getColumnNameFirstName(),_ADMINISTRATOR_NAME);
    adminUser.insert();

    UserGroupRepresentative ugr = new UserGroupRepresentative();
    ugr.setName("admin");
    ugr.insert();

    adminUser.setGroupID(ugr.getID());
    adminUser.setPrimaryGroupID(this.getPermissionGroupAdministrator().getID());
    adminUser.update();

    LoginDBHandler.createLogin(adminUser.getID(),"Administrator","idega",Boolean.TRUE,idegaTimestamp.RightNow(),-1,Boolean.FALSE,Boolean.TRUE,Boolean.FALSE,EncryptionType.MD5);
    return adminUser;
  }

  private void initAdministratorUser() throws Exception{
    List list = EntityFinder.findAllByColumn(User.getStaticInstance(),User.getColumnNameFirstName(),_ADMINISTRATOR_NAME);
    User adminUser = null;
    if(list == null || list.size() < 1){
      adminUser = createAdministratorUser();
    } else {
      adminUser = (User)list.get(0);
    }
    getApplication().setAttribute(_APPADDRESS_ADMINISTRATOR_USER,adminUser);
  }

  public void executeService(){

    try {
      PermissionGroup permission = PermissionGroup.getStaticPermissionGroupInstance();
      List groups = EntityFinder.findAllByColumn(permission,permission.getGroupTypeColumnName(),permission.getGroupTypeValue());
      if(groups != null){
        Iterator iter = groups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          if(getAdministratorGroupName().equals (((GenericGroup)item).getName())){
            AdministratorPermissionGroup = (PermissionGroup)item;
          }
        }
      }
      if(AdministratorPermissionGroup == null){
        initAdministratorPermissionGroup();
      }
    }
    catch (Exception ex) {
      System.err.println("AccessControl: PermissionGroup administrator not initialized");
      ex.printStackTrace();
    }

    try {
      PermissionGroupEveryOne = new PermissionGroup(_GROUP_ID_EVERYONE);
    }
    catch (Exception e) {
      try {
        initPermissionGroupEveryone();
      }
      catch (Exception ex) {
        System.err.println("AccessControl: PermissionGroup Everyone not initialized");
      }
    }

    try {
      PermissionGroupUsers = new PermissionGroup(_GROUP_ID_USERS);
    }
    catch (Exception e) {
      try {
        initPermissionGroupUsers();
      }
      catch (Exception ex) {
        System.err.println("AccessControl: PermissionGroup Users not initialized");
      }
    }

    try {
      initAdministratorUser();
    }
    catch (Exception ex) {
      System.err.println("AccessControl: User Administrator not initialized");
      ex.printStackTrace();
    }

  }

  public String getServiceName(){
    return "AccessControl";
  }

  public static boolean isValidUsersFirstName(String name){
    return !_ADMINISTRATOR_NAME.equals(name);
  }



  public String[] getICObjectPermissionKeys(Class ICObject){
    String[] keys = new String[2];

    keys[0] = _PERMISSIONKEY_VIEW;
    keys[1] = _PERMISSIONKEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }


  public String[] getBundlePermissionKeys(Class ICObject){
    String[] keys = new String[2];

    keys[0] = _PERMISSIONKEY_VIEW;
    keys[1] = _PERMISSIONKEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }

  public String[] getBundlePermissionKeys(String BundleIdentifier){
    String[] keys = new String[2];

    keys[0] = _PERMISSIONKEY_VIEW;
    keys[1] = _PERMISSIONKEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }

  public String[] getPagePermissionKeys(){
    String[] keys = new String[1];

    keys[0] = _PERMISSIONKEY_VIEW;
    //keys[1] = _PERMISSIONKEY_EDIT;
    //keys[2] = _PERMISSIONKEY_DELETE;

    return keys;

    // return new String[0]; // not null
  }












} // Class AccessControl
