package com.idega.core.accesscontrol.business;

import java.sql.*;
import java.util.List;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;
import com.idega.jmodule.object.*;
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

    public static final String _PARAMETERSTRING_IDENTIFIER = "ic_permissionobj_identifier";
    public static final String _PARAMETERSTRING_PERMISSION_CATEGORY = "ic_permission_category";

    public static final int _CATEGORY_OBJECT_INSTANCE = 0;
    public static final int _CATEGORY_OBJECT = 1;
    public static final int _CATEGORY_BUNDLE = 2;
    public static final int _CATEGORY_PAGE_INSTANCE = 3;
    public static final int _CATEGORY_PAGE = 4;
    public static final int _CATEGORY_JSP_PAGE = 5;

    private static final int _GROUP_ID_EVERYONE = -7913;


    static{
      try {
        initAdministratorPermissionGroup();
        if(AdministratorPermissionGroup == null){
          LoginTable.getStaticInstance().insertStartData();
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

    public static PermissionGroup getPermissionGroupEveryOne() throws SQLException {
      if(PermissionGroupEveryOne == null){
        initPermissionGroupEveryone();
      }
      return PermissionGroupEveryOne;
    }

    public static boolean isAdmin(ModuleInfo modinfo)throws SQLException{
      try {
        Object ob = LoginBusiness.getLoginAttribute(getAdministratorGroupName(), modinfo);
        if(ob != null){
          return ((Boolean)ob).booleanValue();
        }else{
          PermissionGroup[] groups = LoginBusiness.getPermissionGroups(modinfo);
          if (groups != null){
            for(int i = 0; i < groups.length ; i++){
              if (getAdministratorGroupName().equals(groups[i].getName())){
                LoginBusiness.setLoginAttribute(getAdministratorGroupName(),Boolean.TRUE,modinfo);
                return true;
              }
            }
          }
        }
        LoginBusiness.setLoginAttribute(getAdministratorGroupName(),Boolean.FALSE,modinfo);
        return false;
      }
      catch (NotLoggedOnException ex) {
        return false;
      }
    }


    /**
     * use this method when writing to database to avoid errors in database.
     * If the name-string changes this will be the only method to change.
     */
    public static String getAdministratorGroupName(){
      return "administrator";
    }



    public static boolean hasPermission(String permissionType, ModuleObject obj,ModuleInfo modinfo) throws SQLException{
      Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked
      int place = 1;

      if (isAdmin(modinfo)){
        return true;
      }

      User user = LoginBusiness.getUser(modinfo);
      ICPermission permission = ICPermission.getStaticInstance();
      ICPermission[] Permissions = null;
      PermissionGroup[] groups = null;
      String sGroupList = "";
      List tempGroupList = new Vector();
      List[] permissionOreder = null; // Everyone, user, primaryGroup, otherGroups

      if (user == null){
        permissionOreder = new List[1];
        permissionOreder[0] = new Vector();
        ((List)permissionOreder[0]).add( Integer.toString(getPermissionGroupEveryOne().getID()));
      } else {

        groups = LoginBusiness.getPermissionGroups(modinfo);

        if (groups != null && groups.length > 0){
          for(int g = 0; g < groups.length; g++){
            if(g>0){ sGroupList += ", "; }
            tempGroupList.add( Integer.toString(groups[g].getID()));
            sGroupList += Integer.toString(groups[g].getID());
          }
          permissionOreder = new List[2];
          permissionOreder[0] = new Vector();
          ((List)permissionOreder[0]).add( Integer.toString(getPermissionGroupEveryOne().getID()));
          permissionOreder[1] = tempGroupList;
        } else {
          permissionOreder = new List[1];
          permissionOreder[0] = new Vector();
          ((List)permissionOreder[0]).add( Integer.toString(getPermissionGroupEveryOne().getID()));
        }
      }





      for (int i = 0; i < permissionOreder.length; i++) { // Everyone, user, primaryGroup, otherGroups
        if (obj == null){ // JSP page
          myPermission = PermissionCacher.hasPermissionForJSPPage(obj,modinfo,permissionType,permissionOreder[i]);
        } else { // if (obj != null)

          if(obj instanceof Page){
            myPermission = PermissionCacher.hasPermissionForPage(obj,modinfo,permissionType,permissionOreder[i]);

          }else{
            //instance
            myPermission = PermissionCacher.hasPermissionForObjectInstance(obj,modinfo,permissionType,permissionOreder[i]);
            //instance

            // Bundle
            if (myPermission == null){
              myPermission = PermissionCacher.hasPermissionForBundle(obj,modinfo,permissionType,permissionOreder[i]);
            }else{
              return myPermission.booleanValue();
            }// Bundle


            // Global - (object)
            if (myPermission == null){
              myPermission = PermissionCacher.hasPermissionForObject(obj,modinfo,permissionType,permissionOreder[i]);
            }else{
              return myPermission.booleanValue();
            } // Global - (object)
          }


          if(myPermission != null){
            return myPermission.booleanValue();
          }

        }
      }


      // Default permission: view == true if not Page, else false
      // If no permission set, view = true for all objects other than Page objects
      if( getViewPermissionString().equals(permissionType) && obj != null && !(obj instanceof Page) ){
        // if some view permission for object, bundle, ... are set
        // then false
        // else true
        // => view hashtable for obj, ...  has object
        return !PermissionCacher.permissionSet( obj, modinfo, permissionType);
        /*
        if(permissionSet( obj, modinfo, permissionType)){
          return false;
        }
        else{
          return true;
        }
        */
      }

      return false;

    } // method hasPermission




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

    public static boolean hasEditPermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getEditPermissionString() , obj, modinfo);
    }


    public static String getDeletePermissionString(){
      return "delete";
    }

    public static boolean hasDeletePermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getDeletePermissionString(), obj, modinfo);
    }


    public static String getInsertPermissionString(){
      return "insert";
    }

    public static boolean hasInsertPermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getInsertPermissionString(), obj, modinfo);
    }


    public static String getViewPermissionString(){
      return "view";
    }

    public static boolean hasViewPermission(ModuleObject obj,ModuleInfo modinfo){
      try {
        boolean permission = hasPermission( getViewPermissionString(), obj, modinfo);
        System.err.println(obj.getClass().getName()+" has permission: " + permission);
        return permission;
        //return hasPermission( getViewPermissionString(), obj, modinfo);
      }
      catch (SQLException ex) {
        return false;
      }
    }



    public static String getAdminPermissionString(){
      return "admin";
    }

    public static boolean hasAdminPermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getAdminPermissionString(), obj, modinfo);
    }

    public static String getIdegaAdminPermissionString(){
      return "idega_admin";
    }

    public static boolean hasIdegaAdminPermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getIdegaAdminPermissionString(), obj, modinfo);
    }


    public static String getOwnerPemissionString(){
      return "owner";
    }

    public static boolean hasOwnerPermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getOwnerPemissionString(), obj, modinfo);
    }

/*  public static ICObjectPermission[] getPermissionTypes(ModuleObject obj)throws SQLException{
      int arobjID = obj.getICObject().getID();
      List permissions =  EntityFinder.findAllByColumn(ICObjectPermission.getStaticInstance(), ICObjectPermission.getPermissionTypeColumnName(), arobjID);
      if (permissions != null){
        return (ICObjectPermission[])permissions.toArray((Object[])new ICObjectPermission[0]);
      }else{
        return null;
      }
    }
*/


    public void setJSPPagePermission(ModuleInfo modinfo, PermissionGroup group, String PageContextValue, String permissionType, Boolean permissionValue)throws SQLException{
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
        // use 'ICJspHandler.getJspPageInstanceID(modinfo)' on the current page and send in as PageContextValue
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
      PermissionCacher.updateJSPPagePermissions(PageContextValue,permissionType,modinfo);
    }

    public void setObjectPermission(ModuleInfo modinfo, PermissionGroup group, ModuleObject obj, String permissionType, Boolean permissionValue)throws SQLException{
      ICPermission permission = ICPermission.getStaticInstance();
      boolean update = true;
      try {
        permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getICObject().getID() + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
      }
      catch (Exception ex) {
        permission = new ICPermission();
        update = false;
      }

      if(!update){
        permission.setContextType(AccessControl.getObjectIdString());
        permission.setContextValue(Integer.toString(obj.getICObject().getID()));
        permission.setGroupID(new Integer(group.getID()));
        permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
        permission.setPermissionValue(permissionValue);
        permission.insert();
      } else{
        permission.setPermissionValue(permissionValue);
        permission.update();
      }
      PermissionCacher.updateObjectPermissions(Integer.toString(obj.getICObject().getID()),permissionType,modinfo);
    }


    public void setBundlePermission(ModuleInfo modinfo, PermissionGroup group, ModuleObject obj, String permissionType, Boolean permissionValue)throws SQLException{
      ICPermission permission = ICPermission.getStaticInstance();
      boolean update = true;
      try {
        permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getBundleIdentifierString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getICObject().getBundleIdentifier() + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
      }
      catch (Exception ex) {
        permission = new ICPermission();
        update = false;
      }

      if(!update){
        permission.setContextType(AccessControl.getBundleIdentifierString());
        permission.setContextValue(obj.getICObject().getBundleIdentifier());
        permission.setGroupID(new Integer(group.getID()));
        permission.setPermissionString(permissionType);
    //        permission.setPermissionStringValue();
        permission.setPermissionValue(permissionValue);
        permission.insert();
      } else{
        permission.setPermissionValue(permissionValue);
        permission.update();
      }
      PermissionCacher.updateBundlePermissions(obj.getICObject().getBundleIdentifier(),permissionType,modinfo);
    }



    public void setObjectInstacePermission(ModuleInfo modinfo, PermissionGroup group, ModuleObject obj, String permissionType, Boolean permissionValue)throws SQLException{
      setObjectInstacePermission(modinfo,Integer.toString(group.getID()),Integer.toString(obj.getICObjectInstance(modinfo).getID()),permissionType,permissionValue);
    }

    public static boolean removeICObjectInstancePermissionRecords(ModuleInfo modinfo, String ObjectInstanceId, String permissionKey, String[] groupsToRemove){
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
            PermissionCacher.updateObjectInstancePermissions(ObjectInstanceId,permissionKey,modinfo);
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


    public static boolean removePermissionRecords(int permissionCategory, ModuleInfo modinfo, String ObjectInstanceId, String permissionKey, String[] groupsToRemove){
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

          PermissionCacher.updatePermissions(permissionCategory,ObjectInstanceId,permissionKey,modinfo);

          return true;
        }
        catch (Exception ex) {
          return false;
        }
      } else {
        return true;
      }

    }



    public static void setPermission(int permissionCategory, ModuleInfo modinfo, String permissionGroupId, String identifier, String permissionKey, Boolean permissionValue)throws SQLException{
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
      PermissionCacher.updatePermissions(permissionCategory, identifier, permissionKey, modinfo);
    }


    public static void setObjectInstacePermission(ModuleInfo modinfo, String permissionGroupId, String ObjectInstanceId, String permissionType, Boolean permissionValue)throws SQLException{
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
      PermissionCacher.updateObjectInstancePermissions(ObjectInstanceId,permissionType,modinfo);
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



    public void addUserToPermissionGroup(PermissionGroup group, int userIDtoAdd) throws SQLException{
      User userToAdd = new User(userIDtoAdd);
      group.addUser(userToAdd);
    }


    public void addGroupToPermissionGroup(PermissionGroup group, int groupIDtoAdd)throws SQLException{
      GenericGroup groupToAdd = new GenericGroup(groupIDtoAdd);
      group.addGroup(groupToAdd);
    }







    public static PermissionGroup[] getPermissionGroups(User user) throws SQLException{
      GenericGroup[] groups = PermissionGroup.getStaticPermissionGroupInstance().getAllGroupsContainingUser(user);

      if (groups != null && groups.length > 0){
        Hashtable GroupsContained = new Hashtable();

        String key = "";
        for (int i = 0; i < groups.length; i++) {
          key = Integer.toString(groups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,groups[i]);
            putGroupsContaining( (GenericGroup)groups[i], GroupsContained );
          }
        }


        Vector  groupVector = new Vector();
        Enumeration e;
        int i = 0;
        for ( e = (Enumeration)GroupsContained.elements(); e.hasMoreElements();){
          GenericGroup tempObj = (GenericGroup)e.nextElement();
          if (tempObj.getGroupType().equals(PermissionGroup.getStaticPermissionGroupInstance().getGroupTypeValue()))
            groupVector.add(i++, tempObj);
        }

        return (PermissionGroup[])groupVector.toArray((Object[])new PermissionGroup[0]);

      }else{
        return null;
      }
    }


    private static void putGroupsContaining(GenericGroup group, Hashtable GroupsContained ) throws SQLException{
      GenericGroup[] pGroups = group.getAllGroupsContainingThis();
      if (pGroups != null){
        String key = "";
        for (int i = 0; i < pGroups.length; i++) {
          key = Integer.toString(pGroups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,pGroups[i]);
            putGroupsContaining((GenericGroup)pGroups[i], GroupsContained);
          }
        }
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
      PermissionGroup permission = PermissionGroup.getStaticPermissionGroupInstance();
      List pGroups = EntityFinder.findAllByColumn(permission,permission.getGroupTypeColumnName(),permission.getGroupTypeValue());
      pGroups.remove(AdministratorPermissionGroup);
      return pGroups;
    }



    public static String[] getICObjectPermissionKeys(Class ICObject){
      String[] keys = new String[3];

      keys[0] = getViewPermissionString();
      keys[1] = getEditPermissionString();
      keys[2] = getDeletePermissionString();

      return keys;

      // return new String[0]; // not null
    }


    public static String[] getBundlePermissionKeys(Class ICObject){
      String[] keys = new String[3];

      keys[0] = getViewPermissionString();
      keys[1] = getEditPermissionString();
      keys[2] = getDeletePermissionString();

      return keys;

      // return new String[0]; // not null
    }

    public static PermissionGroup getAdministratorGroup() {
      return AdministratorPermissionGroup;
    }

} // Class AccessControl