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
import com.idega.data.SimpleQuerier;



/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author       <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AccessControl{

  private static PermissionGroup AdministratorPermissionGroup = null;
  private static PermissionGroup PermissionGroupEveryOne = null;
  private static PermissionGroup PermissionGroupUsers = null;
  private static List statndardGroups = null;

  public static final String _PARAMETERSTRING_IDENTIFIER = "ic_permissionobj_identifier";
  public static final String _PARAMETERSTRING_PERMISSION_CATEGORY = "ic_permission_category";

  public static final int _CATEGORY_OBJECT_INSTANCE = 0;
  public static final int _CATEGORY_OBJECT = 1;
  public static final int _CATEGORY_BUNDLE = 2;
  public static final int _CATEGORY_PAGE_INSTANCE = 3;
  public static final int _CATEGORY_PAGE = 4;
  public static final int _CATEGORY_JSP_PAGE = 5;

  private static final int _GROUP_ID_EVERYONE = -7913;
  private static final int _GROUP_ID_USERS = -1906;


  static{
    try {
      initAdministratorPermissionGroup();
      if(AdministratorPermissionGroup == null){
        new com.idega.core.accesscontrol.data.LoginTable().insertStartData();
        initAdministratorPermissionGroup();
      }
    }
    catch (Exception ex) {
      System.err.println("AccessControl: PermissionGroup administrator not created");
    }

    try {
      PermissionGroupEveryOne = new PermissionGroup(_GROUP_ID_EVERYONE);
    }
    catch (SQLException e) {
      try {
        initPermissionGroupEveryone();
      }
      catch (Exception ex) {
        System.err.println("AccessControl: PermissionGroup Everyone not created");
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
        System.err.println("AccessControl: PermissionGroup Users not created");
      }
    }
  }

  private static void initAdministratorPermissionGroup() throws SQLException {
    PermissionGroup permission = PermissionGroup.getStaticPermissionGroupInstance();
    List groups = EntityFinder.findAllByColumn(permission,permission.getGroupTypeColumnName(),permission.getGroupTypeValue());
    Iterator iter = groups.iterator();
    while (iter.hasNext()) {
      Object item = iter.next();
      if(getAdministratorGroupName().equals (((GenericGroup)item).getName())){
        AdministratorPermissionGroup = (PermissionGroup)item;
      }
    }
  }

  private static void initPermissionGroupEveryone() throws SQLException {
    PermissionGroup permission = new PermissionGroup();
    permission.setID(_GROUP_ID_EVERYONE);
    permission.setName("Everyone");
    permission.setDescription("Permission if not logged on");
    permission.insert();
    PermissionGroupEveryOne = permission;
  }

  private static void initPermissionGroupUsers() throws SQLException {
    PermissionGroup permission = new PermissionGroup();
    permission.setID(_GROUP_ID_USERS);
    permission.setName("Users");
    permission.setDescription("Permission if logged on");
    permission.insert();
    PermissionGroupUsers = permission;
  }

  public static PermissionGroup getPermissionGroupEveryOne() throws SQLException {
    if(PermissionGroupEveryOne == null){
      initPermissionGroupEveryone();
    }
    return PermissionGroupEveryOne;
  }

  public static PermissionGroup getPermissionGroupUsers() throws SQLException {
    if(PermissionGroupUsers == null){
      initPermissionGroupUsers();
    }
    return PermissionGroupUsers;
  }

  public static PermissionGroup getPermissionGroupAdministrator() throws SQLException {
    if(AdministratorPermissionGroup == null){
      initAdministratorPermissionGroup();
    }
    return AdministratorPermissionGroup;
  }

  public static boolean isAdmin(IWContext iwc)throws SQLException{
    try {
      Object ob = LoginBusiness.getLoginAttribute(getAdministratorGroupName(), iwc);
      if(ob != null){
        return ((Boolean)ob).booleanValue();
      }else{
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
  public static boolean isOwner(PresentationObject obj , IWContext iwc) throws SQLException {
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



  public static boolean hasPermission(String permissionType, PresentationObject obj,IWContext iwc) throws SQLException{
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



  public static boolean hasPermission(List groupIds,String permissionType, PresentationObject obj,IWContext iwc) throws SQLException{
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

  public static boolean hasEditPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getEditPermissionString() , obj, iwc);
  }


  public static String getDeletePermissionString(){
    return "delete";
  }

  public static boolean hasDeletePermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getDeletePermissionString(), obj, iwc);
  }


  public static String getInsertPermissionString(){
    return "insert";
  }

  public static boolean hasInsertPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getInsertPermissionString(), obj, iwc);
  }


  public static String getViewPermissionString(){
    return "view";
  }

  public static boolean hasViewPermission(PresentationObject obj,IWContext iwc){
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

  public static boolean hasViewPermission(List groupIds, PresentationObject obj,IWContext iwc){
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

  public static boolean hasAdminPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getAdminPermissionString(), obj, iwc);
  }

  public static String getIdegaAdminPermissionString(){
    return "idega_admin";
  }

  public static boolean hasIdegaAdminPermission(PresentationObject obj,IWContext iwc)throws SQLException{
    return hasPermission( getIdegaAdminPermissionString(), obj, iwc);
  }


  public static String getOwnerPemissionString(){
    return "owner";
  }

  public static boolean hasOwnerPermission(PresentationObject obj,IWContext iwc)throws SQLException{
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



  public static void setPermission(int permissionCategory, IWContext iwc, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue)throws SQLException{
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


  public static void setObjectInstacePermission(IWContext iwc, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue)throws SQLException{
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
    return getPermissionGroups(new GenericGroup(user.getGroupID()));
  }

  public static List getPermissionGroups(GenericGroup group) throws SQLException{
    List permissionGroups = UserGroupBusiness.getGroupsContaining(group,getPermissionGroupFilter(),true);

    if(permissionGroups != null){
      return permissionGroups;
    }else {
      return null;
    }
  }

  public static List getAllowedGroups(int permissionCategory, String identifier, String permissionKey) throws SQLException {
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


  public static List getAllPermissionGroups()throws SQLException {

    List permissionGroups = GenericGroup.getAllGroups(getPermissionGroupFilter(),true);
    if(permissionGroups != null){
      permissionGroups.remove(getPermissionGroupAdministrator());
    }

    return permissionGroups;
  }


  public static List getStandardGroups() throws SQLException {
    if(statndardGroups == null){
      initStandardGroups();
    }
    return statndardGroups;
  }

  private static void initStandardGroups() throws SQLException {
    statndardGroups = new Vector();
    //statndardGroups.add(AccessControl.getPermissionGroupAdministrator());
    statndardGroups.add(AccessControl.getPermissionGroupEveryOne());
    statndardGroups.add(AccessControl.getPermissionGroupUsers());
  }

  public static String[] getICObjectPermissionKeys(Class ICObject){
    String[] keys = new String[2];

    keys[0] = getViewPermissionString();
    keys[1] = getEditPermissionString();
    //keys[2] = getDeletePermissionString();

    return keys;

    // return new String[0]; // not null
  }


  public static String[] getBundlePermissionKeys(Class ICObject){
    String[] keys = new String[2];

    keys[0] = getViewPermissionString();
    keys[1] = getEditPermissionString();
    //keys[2] = getDeletePermissionString();

    return keys;

    // return new String[0]; // not null
  }

  public static String[] getPagePermissionKeys(){
    String[] keys = new String[1];

    keys[0] = getViewPermissionString();
    //keys[1] = getEditPermissionString();
    //keys[2] = getDeletePermissionString();

    return keys;

    // return new String[0]; // not null
  }


} // Class AccessControl