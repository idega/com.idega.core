package com.idega.core.accesscontrol.business;

import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.ModuleObject;
import com.idega.core.accesscontrol.data.*;
import com.idega.core.business.ICJspHandler;
import com.idega.data.EntityFinder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.sql.SQLException;


/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PermissionCacher {

  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT = "ic_permissionmap_object";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE = "ic_permissionmap_object_instance";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE = "ic_permissionmap_bundle";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_PAGE = "ic_permissionmap_page";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE = "ic_permissionmap_jsp_page";


  public PermissionCacher() {
  }


  private static Boolean hasPermission(String permissionMapKey, ModuleObject obj, ModuleInfo modinfo, String permissionKey, List groups) throws SQLException {
    //
    String identifier = null;
    if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE)){
      identifier = Integer.toString(obj.getICObjectInstanceID(modinfo));
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT)){
        identifier = Integer.toString(obj.getICObject().getID());
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE)){
        identifier = obj.getICObject().getBundleIdentifier();
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE)){
        identifier = null;
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE)){
        identifier = Integer.toString(ICJspHandler.getJspPageInstanceID(modinfo));
    }
    //

    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)modinfo.getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier, permissionKey, modinfo);
          permissionMap = (PermissionMap)modinfo.getApplicationAttribute(permissionMapKey);
      }

      List permissions = permissionMap.get(identifier,permissionKey,groups);


      if(permissions == null){
        updatePermissions(permissionMapKey,identifier, permissionKey, modinfo);
        permissions = permissionMap.get(Integer.toString(obj.getICObjectInstanceID(modinfo)),permissionKey,groups);
      }

      Boolean trueOrNull = null;
      if (permissions != null){
        Iterator iter = permissions.iterator();
        while (iter.hasNext()) {
          Boolean item = (Boolean)iter.next();
          if (item != null){
            if (item.equals(Boolean.TRUE)){
              trueOrNull = Boolean.TRUE;
            }else{
              return Boolean.FALSE;
            }
          }
        }
      } // instance
      return trueOrNull;
    } else {
      return null;
    }
  }

  private static void updatePermissions(String permissionMapKey, String identifier, String permissionKey, ModuleInfo modinfo) throws SQLException{
    PermissionMap permissionMap = (PermissionMap)modinfo.getApplicationAttribute(permissionMapKey);
    if(permissionMap == null){
      permissionMap = new PermissionMap();
      modinfo.setApplicationAttribute(permissionMapKey,permissionMap);
    }

    //
    List permissions = null;
    if(identifier != null){
      if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE)){
        permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessControl.getObjectInstanceIdString(),ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessControl.getObjectIdString(),ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessControl.getBundleIdentifierString(),ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE)){
          permissions = null; //EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessControl.getPageIdString(),ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessControl.getPageIdString(),ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      }
    }
    //

    if(permissions != null){
      Iterator iter = permissions.iterator();
      String oldPermissionKey = "";
      boolean first = true;
      Map mapToPutTo = new Hashtable();
      while (iter.hasNext()) {
        ICPermission item = (ICPermission)iter.next();
        mapToPutTo.put(new Integer(item.getGroupID()),(item.getPermissionValue())? Boolean.TRUE : Boolean.FALSE);
      }
      permissionMap.put(identifier, permissionKey,mapToPutTo);
    } else {
      permissionMap.put(identifier, permissionKey,new Hashtable());
    }
  }


/*
  public static Boolean hasPermissionForObjectInstance(ModuleObject obj, ModuleInfo modinfo, String permissionKey, List groups) throws SQLException {

    PermissionMap permissionMap = (PermissionMap)modinfo.getApplicationAttribute(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE);
    if(permissionMap == null){
        updateObjectInstancePermissions(new Integer(obj.getICObjectInstanceID(modinfo)), permissionKey, modinfo);
        permissionMap = (PermissionMap)modinfo.getApplicationAttribute(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE);
    }

    List permissions = permissionMap.get(new Integer(obj.getICObjectInstanceID(modinfo)),permissionKey,groups);

    if(permissions == null){
      Integer InstanceId = new Integer(obj.getICObjectInstanceID(modinfo));
      updateObjectInstancePermissions(InstanceId, permissionKey, modinfo);
      permissions = permissionMap.get(InstanceId,permissionKey,groups);
    }

    Boolean trueOrNull = null;
    if (permissions != null){
      Iterator iter = permissions.iterator();
      while (iter.hasNext()) {
        Boolean item = (Boolean)iter.next();
        if (item != null){
          if (item.equals(Boolean.TRUE)){
            trueOrNull = Boolean.TRUE;
          }else{
            return Boolean.FALSE;
          }
        }
      }
    } // instance
    return trueOrNull;
  }

  public static void updateObjectInstancePermissions(Integer instanceId, String permissionKey, ModuleInfo modinfo) throws SQLException{
    PermissionMap permissionMap = (PermissionMap)modinfo.getApplicationAttribute(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE);
    if(permissionMap == null){
      permissionMap = new PermissionMap();
      modinfo.setApplicationAttribute(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE,permissionMap);
    }

    List permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessControl.getObjectInstanceIdString(),ICPermission.getContextValueColumnName(),instanceId.toString(),ICPermission.getPermissionStringColumnName(),permissionKey);

    if(permissions != null){
      Iterator iter = permissions.iterator();
      String oldPermissionKey = "";
      boolean first = true;
      Map mapToPutTo = new Hashtable();
      while (iter.hasNext()) {
        ICPermission item = (ICPermission)iter.next();
        mapToPutTo.put(new Integer(item.getGroupID()),(item.getPermissionValue())? Boolean.TRUE : Boolean.FALSE);
      }
      permissionMap.put(instanceId, permissionKey,mapToPutTo);
    } else {
      permissionMap.put(instanceId, permissionKey,new Hashtable());
    }
  }
  */

  public static Boolean hasPermissionForObjectInstance(ModuleObject obj, ModuleInfo modinfo, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE,obj,modinfo,permissionKey,groups);
  }

  public static void updateObjectInstancePermissions(String instanceId, String permissionKey, ModuleInfo modinfo) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE,instanceId,permissionKey,modinfo);
  }

  public static Boolean hasPermissionForObject(ModuleObject obj, ModuleInfo modinfo, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT,obj,modinfo,permissionKey,groups);
  }

  public static void updateObjectPermissions(String objectId, String permissionKey, ModuleInfo modinfo) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT,objectId,permissionKey,modinfo);
  }

  public static Boolean hasPermissionForBundle(ModuleObject obj, ModuleInfo modinfo, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE,obj,modinfo,permissionKey,groups);
  }

  public static void updateBundlePermissions(String bundleIdentifier, String permissionKey, ModuleInfo modinfo) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE,bundleIdentifier,permissionKey,modinfo);
  }

  public static Boolean hasPermissionForPage(ModuleObject obj, ModuleInfo modinfo, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE,obj,modinfo,permissionKey,groups);
  }

  public static void updatePagePermissions(String pageId, String permissionKey, ModuleInfo modinfo) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE,pageId,permissionKey,modinfo);
  }

  public static Boolean hasPermissionForJSPPage(ModuleObject obj, ModuleInfo modinfo, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE,obj,modinfo,permissionKey,groups);
  }

  public static void updateJSPPagePermissions(String jspPageId, String permissionKey, ModuleInfo modinfo) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE,jspPageId,permissionKey,modinfo);
  }

}