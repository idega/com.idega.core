package com.idega.core.accesscontrol.business;

import java.sql.*;
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

public class AccessControl extends IWServiceImpl implements AccessControler {
/**
 * @todo change next 4 variables to applicationAddesses
 */
  private PermissionGroup AdministratorPermissionGroup = null;
  private PermissionGroup PermissionGroupEveryOne = null;
  private PermissionGroup PermissionGroupUsers = null;
  private List standardGroups = null;

  private static final String _APPADDRESS_ADMINISTRATOR_USER = "ic_super_admin";
  private static final String _ADMINISTRATOR_NAME = "Administrator";

  public static final String _PARAMETERSTRING_IDENTIFIER = "ic_permissionobj_identifier";
  public static final String _PARAMETERSTRING_PERMISSION_CATEGORY = "ic_permission_category";


  private static final int _GROUP_ID_EVERYONE = -7913;
  private static final int _GROUP_ID_USERS = -1906;


  private void initAdministratorPermissionGroup() throws SQLException {
    PermissionGroup permission = new PermissionGroup();
    permission.setName(AccessControl.getAdministratorGroupName());
    permission.setDescription("Administrator permission");
    permission.insert();
    AdministratorPermissionGroup = permission;
  }

  private void initPermissionGroupEveryone() throws SQLException {
    PermissionGroup permission = new PermissionGroup();
    permission.setID(_GROUP_ID_EVERYONE);
    permission.setName("Everyone");
    permission.setDescription("Permission if not logged on");
    permission.insert();
    PermissionGroupEveryOne = permission;
  }

  private void initPermissionGroupUsers() throws SQLException {
    PermissionGroup permission = new PermissionGroup();
    permission.setID(_GROUP_ID_USERS);
    permission.setName("Users");
    permission.setDescription("Permission if logged on");
    permission.insert();
    PermissionGroupUsers = permission;
  }

  public PermissionGroup getPermissionGroupEveryOne() throws SQLException {
    if(PermissionGroupEveryOne == null){
      initPermissionGroupEveryone();
    }
    return PermissionGroupEveryOne;
  }

  public PermissionGroup getPermissionGroupUsers() throws SQLException {
    if(PermissionGroupUsers == null){
      initPermissionGroupUsers();
    }
    return PermissionGroupUsers;
  }

  public PermissionGroup getPermissionGroupAdministrator() throws SQLException {
    if(AdministratorPermissionGroup == null){
      initAdministratorPermissionGroup();
    }
    return AdministratorPermissionGroup;
  }

  public boolean isAdmin(IWContext iwc)throws SQLException{
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
  public boolean isOwner(PresentationObject obj , IWContext iwc) throws SQLException {
    User user = LoginBusiness.getUser(iwc);
    if(user == null){
      return false;
    }
    Boolean myPermission = null;
    String permissionType = AccessControl.getOwnerPemissionString();
    List permissionGroup = new Vector();
    permissionGroup.add(Integer.toString(user.getGroupID()));
    if (obj == null){ // JSP page
      myPermission = PermissionCacher.hasPermissionForJSPPage(obj,iwc,permissionType,permissionGroup);
    } else { // if (obj != null)
      if(obj instanceof Page){
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



  public boolean hasPermission(String permissionType, PresentationObject obj,IWContext iwc) throws SQLException{
    Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked

    // Default permission: view == true if not Page, else false
    // If no permission set, view = true for all objects other than Page objects
    if( getViewPermissionString().equals(permissionType) && obj != null && !(obj instanceof Page && !(obj instanceof BlockWindow)) ){
      // if some view permission for object, bundle, ... are set
      // then do nothing
      // else true
      // => view hashtable for obj, ...  has object
      if(!PermissionCacher.somePermissionSet( obj, iwc, permissionType)){
        return true;
      }
    }

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
    for (int i = 0; i < permissionOrder.length; i++) {
      myPermission = checkForPermission(permissionOrder[i], obj, permissionType, iwc);
      if(myPermission != null){
        return myPermission.booleanValue();
      }
    }
    return false;
  } // method hasPermission



  public boolean hasPermission(List groupIds,String permissionType, PresentationObject obj,IWContext iwc) throws SQLException{
    Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked

    // Default permission: view == true if not Page, else false
    // If no permission set, view = true for all objects other than Page objects
    if( getViewPermissionString().equals(permissionType) && obj != null && !(obj instanceof Page && !(obj instanceof BlockWindow)) ){
      // if some view permission for object, bundle, ... are set
      // then do nothing
      // else true
      // => view hashtable for obj, ...  has object
      if(!PermissionCacher.somePermissionSet( obj, iwc, permissionType)){
        return true;
      }
    }

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
    for (int i = 0; i < permissionOrder.length; i++) {
      myPermission = checkForPermission(permissionOrder[i], obj, permissionType, iwc);
      if(myPermission != null){
        return myPermission.booleanValue();
      }
    }
    return false;
  } // method hasPermission




  private static Boolean checkForPermission(List permissionGroups, PresentationObject obj, String permissionType, IWContext iwc ) throws SQLException {
    Boolean myPermission = null;

    if (obj == null){ // JSP page
      myPermission = PermissionCacher.hasPermissionForJSPPage(obj,iwc,permissionType,permissionGroups);

      return myPermission;
    } else { // if (obj != null)

      if(obj instanceof Page && !(obj instanceof BlockWindow)){
        myPermission = PermissionCacher.hasPermissionForPage(obj,iwc,permissionType,permissionGroups);

        return myPermission;
      }else{
        //instance
        myPermission = PermissionCacher.hasPermissionForObjectInstance(obj,iwc,permissionType,permissionGroups);
        //instance

        // Global - (object)
        if (myPermission == null){
          myPermission = PermissionCacher.hasPermissionForObject(obj,iwc,permissionType,permissionGroups);
        }else{
          return myPermission;
        } // Global - (object)

        // Bundle
        if (myPermission == null){
          myPermission = PermissionCacher.hasPermissionForBundle(obj,iwc,permissionType,permissionGroups);
        }else{
          return myPermission;
        }// Bundle

      }

        return myPermission;
    }
  }


  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getObjectInstanceIdString(){
    return "ic_object_instance_id";
  }


  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getObjectIdString(){
    return "ic_object_id";
  }

  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getBundleIdentifierString(){
    return "iw_bundle_identifier";
  }


  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getPageIdString(){
    return "page_id";
  }

  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getPageString(){
    return "page";
  }

  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getJSPPageString(){
    return "jsp_page";
  }



  /**
   * use this method when writing to database to avoid errors in database.
   * If the name-string changes this will be the only method to change.
   */
  public static String getEditPermissionString(){
    return "edit";
  }

  public boolean hasEditPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getEditPermissionString() , obj, iwc);
  }


  public static String getDeletePermissionString(){
    return "delete";
  }

  public boolean hasDeletePermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getDeletePermissionString(), obj, iwc);
  }


  public static String getInsertPermissionString(){
    return "insert";
  }

  public boolean hasInsertPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getInsertPermissionString(), obj, iwc);
  }


  public static String getViewPermissionString(){
    return "view";
  }

  public boolean hasViewPermission(PresentationObject obj,IWContext iwc){
    try {
      /*boolean permission = hasPermission( getViewPermissionString(), obj, iwc);
      System.err.println(obj.getClass().getName()+" has permission: " + permission);
      return permission;
      */
      return hasPermission( getViewPermissionString(), obj, iwc);
    }
    catch (SQLException ex) {
      return false;
    }
  }

  public boolean hasViewPermission(List groupIds, PresentationObject obj,IWContext iwc){
    try {
      /*boolean permission = hasPermission( getViewPermissionString(), obj, iwc);
      System.err.println(obj.getClass().getName()+" has permission: " + permission);
      return permission;
      */
      return hasPermission(groupIds, getViewPermissionString(), obj, iwc);
    }
    catch (SQLException ex) {
      return false;
    }
  }


  public static String getAdminPermissionString(){
    return "admin";
  }

  public boolean hasAdminPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getAdminPermissionString(), obj, iwc);
  }

  public static String getOwnerPemissionString(){
    return "owner";
  }

  public boolean hasOwnerPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getOwnerPemissionString(), obj, iwc);
  }

/*  public static ICObjectPermission[] getPermissionTypes(PresentationObject obj)throws SQLException{
    int arobjID = obj.getICObject().getID();
    List permissions =  EntityFinder.findAllByColumn(ICObjectPermission.getStaticInstance(), ICObjectPermission.getPermissionTypeColumnName(), arobjID);
    if (permissions != null){
      return (ICObjectPermission[])permissions.toArray((Object[])new ICObjectPermission[0]);
    }else{
      return null;
    }
  }
*/


  public void setJSPPagePermission(IWContext iwc, PermissionGroup group, String PageContextValue, String permissionType, Boolean permissionValue)throws SQLException{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getJSPPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + PageContextValue + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.getJSPPageString());
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

  public void setObjectPermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws SQLException{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getICObjectID(iwc) + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.getObjectIdString());
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


  public void setBundlePermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws SQLException{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getBundleIdentifierString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getBundleIdentifier() + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.getBundleIdentifierString());
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



  public void setObjectInstacePermission(IWContext iwc, PermissionGroup group, PresentationObject obj, String permissionType, Boolean permissionValue)throws SQLException{
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
        boolean done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
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


  public static boolean removePermissionRecords(int permissionCategory, IWContext iwc, String ObjectInstanceId, String permissionKey, String[] groupsToRemove){
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
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_OBJECT :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectIdString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_BUNDLE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getBundleIdentifierString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_PAGE_INSTANCE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageIdString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_PAGE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
          case AccessControl._CATEGORY_JSP_PAGE :
            done = SimpleQuerier.execute("DELETE FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getJSPPageString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey + "' AND " + permission.getGroupIDColumnName() + " IN (" + sGroupList + ")" );
            break;
        }

        PermissionCacher.updatePermissions(permissionCategory,ObjectInstanceId,permissionKey,iwc);

        return true;
      }
      catch (Exception ex) {
        return false;
      }
    } else {
      return true;
    }

  }



  public void setPermission(int permissionCategory, IWContext iwc, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue)throws SQLException{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      switch (permissionCategory) {
        case AccessControl._CATEGORY_OBJECT_INSTANCE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_OBJECT :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_BUNDLE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getBundleIdentifierString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_PAGE_INSTANCE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_PAGE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
          break;
        case AccessControl._CATEGORY_JSP_PAGE :
          permission = (ICPermission)EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getJSPPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId).get(0);
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
          permission.setContextType(AccessControl.getObjectInstanceIdString());
          break;
        case AccessControl._CATEGORY_OBJECT :
          permission.setContextType(AccessControl.getObjectIdString());
          break;
        case AccessControl._CATEGORY_BUNDLE :
          permission.setContextType(AccessControl.getBundleIdentifierString());
          break;
        case AccessControl._CATEGORY_PAGE_INSTANCE :
          permission.setContextType(AccessControl.getPageIdString());
          break;
        case AccessControl._CATEGORY_PAGE :
          permission.setContextType(AccessControl.getPageString());
          break;
        case AccessControl._CATEGORY_JSP_PAGE :
          permission.setContextType(AccessControl.getJSPPageString());
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


  public void setObjectInstacePermission(IWContext iwc, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue)throws SQLException{
    ICPermission permission = ICPermission.getStaticInstance();
    boolean update = true;
    try {
      permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = " + ObjectInstanceId + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + permissionGroupId))[0];
    }
    catch (Exception ex) {
      permission = new ICPermission();
      update = false;
    }

    if(!update){
      permission.setContextType(AccessControl.getObjectInstanceIdString());
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





  public int createPermissionGroup(String GroupName, String Description, String ExtraInfo, int[] userIDs, int[] groupIDs)throws SQLException{
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

  public static void addUserToPermissionGroup(PermissionGroup group, int userIDtoAdd) throws SQLException{
    User userToAdd = new User(userIDtoAdd);
    group.addUser(userToAdd);
  }


  public static void addGroupToPermissionGroup(PermissionGroup group, int groupIDtoAdd)throws SQLException{
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

  public static List getPermissionGroups(User user) throws SQLException{
    //temp - new GenericGroup()
    int groupId = user.getGroupID();
    if(groupId != -1){
      return getPermissionGroups(new GenericGroup(groupId));
    }else{
      return null;
    }
  }

  public static List getPermissionGroups(GenericGroup group) throws SQLException{
    List permissionGroups = UserGroupBusiness.getGroupsContaining(group,getPermissionGroupFilter(),true);

    if(permissionGroups != null){
      return permissionGroups;
    }else {
      return null;
    }
  }

  public List getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws SQLException {
    List toReturn = new Vector(0);
    ICPermission permission = ICPermission.getStaticInstance();
    List permissions = null;

    switch (permissionCategory) {
      case AccessControl._CATEGORY_OBJECT_INSTANCE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_OBJECT :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_BUNDLE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getBundleIdentifierString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_PAGE_INSTANCE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_PAGE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
      case AccessControl._CATEGORY_JSP_PAGE :
        permissions = EntityFinder.findAll(permission,"SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getJSPPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + identifier + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionKey +"' AND "+permission.getPermissionValueColumnName() +" = 'Y'");
        break;
    }

    if (permissions != null){
      Iterator iter = permissions.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        try {
          toReturn.add(new PermissionGroup(((ICPermission)item).getGroupID()));
        }
        catch (SQLException ex) {
          System.err.println("Accesscontrol.getAllowedGroups(): Group not created for id "+((ICPermission)item).getGroupID());
        }

      }
    }
    toReturn.remove(AdministratorPermissionGroup);
    return toReturn;
  }


  public List getAllPermissionGroups()throws SQLException {

    List permissionGroups = GenericGroup.getAllGroups(getPermissionGroupFilter(),true);
    if(permissionGroups != null){
      permissionGroups.remove(getPermissionGroupAdministrator());
    }

    return permissionGroups;
  }


  public List getStandardGroups() throws SQLException {
    if(standardGroups == null){
      initStandardGroups();
    }
    return standardGroups;
  }

  private void initStandardGroups() throws SQLException {
    standardGroups = new Vector();
    //standardGroups.add(AccessControl.getPermissionGroupAdministrator());
    standardGroups.add(this.getPermissionGroupEveryOne());
    standardGroups.add(this.getPermissionGroupUsers());
  }


  public User getAdministratorUser(){
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
    catch (SQLException e) {
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
    catch (SQLException e) {
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

    keys[0] = getViewPermissionString();
    keys[1] = getEditPermissionString();
    //keys[2] = getDeletePermissionString();

    return keys;

    // return new String[0]; // not null
  }


  public String[] getBundlePermissionKeys(Class ICObject){
    String[] keys = new String[2];

    keys[0] = getViewPermissionString();
    keys[1] = getEditPermissionString();
    //keys[2] = getDeletePermissionString();

    return keys;

    // return new String[0]; // not null
  }

  public String[] getPagePermissionKeys(){
    String[] keys = new String[1];

    keys[0] = getViewPermissionString();
    //keys[1] = getEditPermissionString();
    //keys[2] = getDeletePermissionString();

    return keys;

    // return new String[0]; // not null
  }












} // Class AccessControl