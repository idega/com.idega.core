package com.idega.core.accesscontrol.business;

import java.sql.*;
import java.util.List;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import com.idega.jmodule.object.*;
import com.idega.block.login.business.*;
import com.idega.core.data.*;
import com.idega.core.user.data.User;
import com.idega.data.EntityFinder;
import com.idega.core.accesscontrol.data.*;
import com.idega.builder.business.*;

/**
 * @todo move com.idega.builder.business.IBJspHandler to com.idega.core.business.ICJspHandler
 */


/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author idega 2001 - <a href="mailto:idega@idega.is">idega team</a>
 * @version 1.0
 */

public class AccessControl{


    public static boolean isAdmin(ModuleInfo modinfo)throws SQLException{
      User user = LoginBusiness.getUser(modinfo);

      if (user != null){
        List access = user.getPermissionGroups();
        if (access != null){
          for(int i = 0; i < access.size(); i++){
            if (getAdministratorGroupName().equals(((PermissionGroup)access.get(i)).getName()))
                  return true;
          }
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



    public static boolean hasPermission(String permissionType, ModuleObject obj,ModuleInfo modinfo) throws SQLException{
      Boolean myPermission = null;  // Returned if one has permission for obj instance, true or false. If no instancepermission glopalpermission is checked

      if (isAdmin(modinfo)){
        return true;
      } else {

        User user = LoginBusiness.getUser(modinfo);
        if (user == null){
          return false;
        }

        IBPermission permission = IBPermission.getStaticInstance();
        IBPermission[] Permissions = null;
        PermissionGroup[] groups = getPermissionGroups(user);
        String sGroupList = "";

        if (groups != null){
          for(int g = 0; g < groups.length; g++){
            if(g>0){ sGroupList += ", "; }
            sGroupList += Integer.toString(groups[g].getID());
          }
        }

        if (obj == null){ // page
          Permissions = (IBPermission[])permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + IBJspHandler.getJspPageInstanceID(modinfo) + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " in (" + sGroupList + ")");
          if (Permissions != null){
            for (int i = 0; i < Permissions.length; i++){
              if (Permissions[i].getPermissionValue() == true )
                return true;
            }
          }
        } else { // if (obj != null)

          // instance
          Permissions = (IBPermission[])permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getIBObjectInstanceID(modinfo) + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " in (" + sGroupList + ")" );
          if (Permissions != null){
            for(int j = 0; j < Permissions.length; j++){
                if (myPermission.equals(Boolean.TRUE) ){
                  return true;
                }else{
                  myPermission = Boolean.FALSE;
                }
            } // for
          } // instance

          // Global
          if (myPermission == null){
            Permissions = (IBPermission[])permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getIBObject().getID() + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " in (" + sGroupList + ")" );
            for (int k = 0; k < Permissions.length; k++) {
              if (Permissions != null){
                  if (Permissions[k].getPermissionValue() == true )
                    return true;
              }
            }
          }else{
            return myPermission.booleanValue();
          } // Global


        }

      }

      return false;

    } // method hasPermission

    /**
     * use this method when writing to database to avoid errors in database.
     * If the name-string changes this will be the only method to change.
     */
    public static String getObjectInstanceIdString(){
      return "ib_object_instance_id";
    }


    /**
     * use this method when writing to database to avoid errors in database.
     * If the name-string changes this will be the only method to change.
     */
    public static String getObjectIdString(){
      return "ib_object_id";
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

    public static boolean hasViewPermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getViewPermissionString(), obj, modinfo);
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



    public static IBObjectPermission[] getPermissionTypes(ModuleObject obj)throws SQLException{
      int arobjID = obj.getIBObject().getID();
      List permissions =  EntityFinder.findAllByColumn(IBObjectPermission.getStaticInstance(), IBObjectPermission.getPermissionTypeColumnName(), arobjID);
      if (permissions != null){
        return (IBObjectPermission[])permissions.toArray((Object[])new IBObjectPermission[0]);
      }else{
        return null;
      }
    }



    public void setPagePermission(ModuleInfo modinfo, PermissionGroup group, String PageContextValue, String permissionType, Boolean permissionValue)throws SQLException{
      IBPermission permission = IBPermission.getStaticInstance();
      boolean update = true;
      try {
        permission = (IBPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = " + getPageString() + " AND " + permission.getContextValueColumnName() + " = " + PageContextValue + " AND " + permission.getPermissionStringColumnName() + " = " + permissionType + " AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
      }
      catch (Exception ex) {
        permission = new IBPermission();
        update = false;
      }

      if(!update){
        permission.setContextType(AccessControl.getPageString());
        // use 'IBJspHandler.getJspPageInstanceID(modinfo)' on the current page and send in as PageContextValue
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

    }

    public void setObjectPermission(ModuleInfo modinfo, PermissionGroup group, ModuleObject obj, String permissionType, Boolean permissionValue)throws SQLException{
      IBPermission permission = IBPermission.getStaticInstance();
      boolean update = true;
      try {
        permission = (IBPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = " + getPageString() + " AND " + permission.getContextValueColumnName() + " = " + IBJspHandler.getJspPageInstanceID(modinfo) + " AND " + permission.getPermissionStringColumnName() + " = " + permissionType + " AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
      }
      catch (Exception ex) {
        permission = new IBPermission();
        update = false;
      }

      if(!update){
        permission.setContextType(AccessControl.getObjectIdString());
        permission.setContextValue(Integer.toString(obj.getIBObject().getID()));
        permission.setGroupID(new Integer(group.getID()));
        permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
        permission.setPermissionValue(permissionValue);
        permission.insert();
      } else{
        permission.setPermissionValue(permissionValue);
        permission.update();
      }
    }

    public void setObjectInstacePermission(ModuleInfo modinfo, PermissionGroup group, ModuleObject obj, String permissionType, Boolean permissionValue)throws SQLException{
      IBPermission permission = IBPermission.getStaticInstance();
      boolean update = true;
      try {
        permission = (IBPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = " + getObjectInstanceIdString() + " AND " + permission.getContextValueColumnName() + " = " + obj.getIBObjectInstanceID(modinfo) + " AND " + permission.getPermissionStringColumnName() + " = " + permissionType + " AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
      }
      catch (Exception ex) {
        permission = new IBPermission();
        update = false;
      }

      if(!update){
        permission.setContextType(AccessControl.getObjectInstanceIdString());
        permission.setContextValue(Integer.toString(obj.getIBObjectInstanceID(modinfo)));
        permission.setGroupID(new Integer(group.getID()));
        permission.setPermissionString(permissionType);
//        permission.setPermissionStringValue();
        permission.setPermissionValue(permissionValue);
        permission.insert();
      } else{
        permission.setPermissionValue(permissionValue);
        permission.update();
      }
    }







    public void createPermissionGroup(String GroupName, String Description, String ExtraInfo, int[] userIDs, int[] groupIDs)throws SQLException{
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
    }



    public void addUserToPermissionGroup(PermissionGroup group, int userIDtoAdd) throws SQLException{
      User userToAdd = new User(userIDtoAdd);
      group.addUser(userToAdd);
    }


    public void addGroupToPermissionGroup(PermissionGroup group, int groupIDtoAdd)throws SQLException{
      Group groupToAdd = new Group(groupIDtoAdd);
      group.addGroup(groupToAdd);
    }







    public static PermissionGroup[] getPermissionGroups(User user) throws SQLException{
      Group[] groups = PermissionGroup.getStaticPermissionGroupInstance().getAllGroupsContainingUser(user);

      if (groups != null){
        Hashtable GroupsContained = new Hashtable();

        String key = "";
        for (int i = 0; i < groups.length; i++) {
          key = Integer.toString(groups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,groups[i]);
            putGroupsContaining( (Group)groups[i], GroupsContained );
          }
        }


        Vector  groupVector = new Vector();
        Enumeration e;
        int i = 0;
        for ( e = (Enumeration)GroupsContained.elements(); e.hasMoreElements();){
          Group tempObj = (Group)e.nextElement();
          if (tempObj.getGroupType().equals(PermissionGroup.getStaticPermissionGroupInstance().getGroupTypeValue()))
            groupVector.add(i++, tempObj);
        }

        return (PermissionGroup[])groupVector.toArray((Object[])new PermissionGroup[0]);

      }else{
        return null;
      }
    }


    private static void putGroupsContaining(Group group, Hashtable GroupsContained ) throws SQLException{
      Group[] pGroups = group.getAllGroupsContainingThis();
      if (pGroups != null){
        String key = "";
        for (int i = 0; i < pGroups.length; i++) {
          key = Integer.toString(pGroups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,pGroups[i]);
            putGroupsContaining((Group)pGroups[i], GroupsContained);
          }
        }
      }
    }



} // Class AccessControl