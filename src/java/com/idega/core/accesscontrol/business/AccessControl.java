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
import com.idega.core.business.*;
import com.idega.util.datastructures.HashtableDoubleKeyed;

/**
 * @todo move com.idega.builder.business.IBJspHandler to com.idega.core.business.ICJspHandler
 */


/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author       <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AccessControl{


    public static boolean isAdmin(ModuleInfo modinfo)throws SQLException{
      Object ob = LoginBusiness.getLoginAttribute(getAdministratorGroupName(), modinfo);
      if(ob != null){
        return ((Boolean)ob).booleanValue();
      }else{
        PermissionGroup[] groups = LoginBusiness.getPermissionGroups(modinfo);
        if (groups != null){
          for(int i = 0; i < groups.length ; i++){
            if (getAdministratorGroupName().equals(groups[i].getName()))
              LoginBusiness.setLoginAttribute(getAdministratorGroupName(),Boolean.TRUE,modinfo);
              return true;
          }
        }
      }
      LoginBusiness.setLoginAttribute(getAdministratorGroupName(),Boolean.FALSE,modinfo);
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

        ICPermission permission = ICPermission.getStaticInstance();
        ICPermission[] Permissions = null;
        PermissionGroup[] groups = getPermissionGroups(user);
        String sGroupList = "";

        if (groups != null && groups.length > 0){
          for(int g = 0; g < groups.length; g++){
            if(g>0){ sGroupList += ", "; }
            sGroupList += Integer.toString(groups[g].getID());
          }
        } else {
          return false;
        }

        if (obj == null){ // JSP page
          Permissions = (ICPermission[])permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + ICJspHandler.getJspPageInstanceID(modinfo) + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " in (" + sGroupList + ")");
          if (Permissions != null){
            for (int i = 0; i < Permissions.length; i++){
              if (Permissions[i].getPermissionValue() == true )
                return true;
            }
          }
        } else { // if (obj != null)



          // instance
          Permissions = (ICPermission[])permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getICObjectInstanceID(modinfo) + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " in (" + sGroupList + ")" );
          if (Permissions != null){
            for(int j = 0; j < Permissions.length; j++){
                if (myPermission.equals(Boolean.TRUE) ){
                  return true;
                }else{
                  myPermission = Boolean.FALSE;
                }
            } // for
          } // instance


          // Bundle
          if (myPermission == null){
            Permissions = (ICPermission[])permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getBundleIdentifierString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getICObject().getBundleIdentifier() + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " in (" + sGroupList + ")" );
            for (int k = 0; k < Permissions.length; k++) {
              if (Permissions != null){
                  if (Permissions[k].getPermissionValue() == true )
                    return true;
              }
            }
          }else{
            return myPermission.booleanValue();
          } // Bundle


          // Global
          if (myPermission == null){
            Permissions = (ICPermission[])permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectIdString() + "' AND " + permission.getContextValueColumnName() + " = '" + obj.getICObject().getID() + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " in (" + sGroupList + ")" );
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


    public static String getOwnerPemissionString(){
      return "owner";
    }

    public static boolean hasOwnerPermission(ModuleObject obj,ModuleInfo modinfo)throws SQLException{
      return hasPermission( getOwnerPemissionString(), obj, modinfo);
    }


    public static ICObjectPermission[] getPermissionTypes(ModuleObject obj)throws SQLException{
      int arobjID = obj.getICObject().getID();
      List permissions =  EntityFinder.findAllByColumn(ICObjectPermission.getStaticInstance(), ICObjectPermission.getPermissionTypeColumnName(), arobjID);
      if (permissions != null){
        return (ICObjectPermission[])permissions.toArray((Object[])new ICObjectPermission[0]);
      }else{
        return null;
      }
    }



    public void setPagePermission(ModuleInfo modinfo, PermissionGroup group, String PageContextValue, String permissionType, Boolean permissionValue)throws SQLException{
      ICPermission permission = ICPermission.getStaticInstance();
      boolean update = true;
      try {
        permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getPageString() + "' AND " + permission.getContextValueColumnName() + " = '" + PageContextValue + "' AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
      }
      catch (Exception ex) {
        permission = new ICPermission();
        update = false;
      }

      if(!update){
        permission.setContextType(AccessControl.getPageString());
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
    }


    public void setObjectInstacePermission(ModuleInfo modinfo, PermissionGroup group, ModuleObject obj, String permissionType, Boolean permissionValue)throws SQLException{
      ICPermission permission = ICPermission.getStaticInstance();
      boolean update = true;
      try {
        permission = (ICPermission)(permission.findAll("SELECT * FROM " + permission.getEntityName() + " WHERE " + permission.getContextTypeColumnName() + " = '" + getObjectInstanceIdString() + "' AND " + permission.getContextValueColumnName() + " = " + obj.getICInstance(modinfo).getID() + " AND " + permission.getPermissionStringColumnName() + " = '" + permissionType + "' AND " + permission.getGroupIDColumnName() + " = " + group.getID()))[0];
      }
      catch (Exception ex) {
        permission = new ICPermission();
        update = false;
      }

      if(!update){
        permission.setContextType(AccessControl.getObjectInstanceIdString());
        permission.setContextValue(Integer.toString(obj.getICObjectInstanceID(modinfo)));
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



} // Class AccessControl