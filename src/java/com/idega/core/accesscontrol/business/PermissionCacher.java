package com.idega.core.accesscontrol.business;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.core.accesscontrol.data.*;
import com.idega.core.data.ICObject;
import com.idega.core.business.ICJspHandler;
import com.idega.presentation.Page;
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
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PermissionCacher {


/**
 * @todo depricate APPLICATION_ADDRESS_-constants, use categoryconstants from Accesscorntroller with prefix
 */
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT = "ic_permissionmap_object";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE = "ic_permissionmap_object_instance";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE = "ic_permissionmap_bundle";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE = "ic_permissionmap_page_instance";
  private static final String APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE = "ic_permissionmap_jsp_page";
  public static final String APPLICATION_ADDRESS_PERMISSIONMAP_FILE_ID = "ic_permissionmap_file_id";

  private static final String _SOME_VIEW_PERMISSION_SET = "ic_viewpermission_set";

  public PermissionCacher() {
  }


  // anyPermissionsDefined

  public static boolean anyInstancePerissionsDefinedForObject( PresentationObject obj, IWContext iwc, String permissionKey) throws SQLException{
    String[] maps = {APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE};
    return anyPermissionsDefined(obj,iwc,permissionKey,maps);
  }

  public static boolean anyPerissionsDefinedForObject( PresentationObject obj, IWContext iwc, String permissionKey) throws SQLException{
    String[] maps = {APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT};
    return anyPermissionsDefined(obj,iwc,permissionKey,maps);
  }

  public static boolean anyPerissionsDefinedForPage( PresentationObject obj, IWContext iwc, String permissionKey) throws SQLException{
    return anyPerissionsDefinedForObject(obj,iwc,permissionKey);
  }

  public static boolean anyInstancePerissionsDefinedForPage( PresentationObject obj, IWContext iwc, String permissionKey) throws SQLException{
    String[] maps = {APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE};
    return anyPermissionsDefined(obj,iwc,permissionKey,maps);
  }


  public static boolean anyInstancePerissionsDefinedForObject( String identifier, IWContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE);
  }

  public static boolean anyPerissionsDefinedForObject( String identifier, IWContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT);
  }

  public static boolean anyPerissionsDefinedForPage( String identifier, IWContext iwc, String permissionKey) throws SQLException{
    return anyPerissionsDefinedForObject(identifier,iwc,permissionKey);
  }

  public static boolean anyInstancePerissionsDefinedForPage( String identifier, IWContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE);
  }

  public static boolean anyInstancePerissionsDefinedForFile( String identifier, IWContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,APPLICATION_ADDRESS_PERMISSIONMAP_FILE_ID);
  }




  private static boolean anyPermissionsDefined( PresentationObject obj, IWContext iwc, String permissionKey, String[] maps) throws SQLException{
    String identifier = null;
    Boolean set = null;


    for (int i = 0; i < maps.length; i++) {
      String permissionMapKey = maps[i];
      if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE)){
        identifier = Integer.toString(obj.getICObjectInstanceID(iwc));
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT)){
          identifier = Integer.toString(obj.getICObjectID(iwc));
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE)){
          identifier = Integer.toString(((Page)obj).getPageID());
      }

      if(identifier != null){
        PermissionMap permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
        if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier,permissionKey,iwc);
          permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
        }

        Map permissions = permissionMap.get(identifier,permissionKey);


        if(permissions == null){
          updatePermissions(permissionMapKey,identifier,permissionKey,iwc);
          permissions = permissionMap.get(identifier,permissionKey);
          set = ((Boolean)permissions.get(_SOME_VIEW_PERMISSION_SET));
        } else {
          set = ((Boolean)permissions.get(_SOME_VIEW_PERMISSION_SET));
        }
      } else {
        throw new RuntimeException("PermissionCacher: Cannot find identifier for "+permissionMapKey+" - "+ obj);
      }
      if(set != null && set.equals(Boolean.TRUE)){
        return true;
      }
    }
    if(set != null && set.equals(Boolean.FALSE)){
      return false;
    } else{
      return true;
    }
  }



  private static boolean anyPermissionsDefined( String identifier, IWContext iwc, String permissionKey, String map) throws SQLException{
    Boolean set = null;

    String permissionMapKey = map;

    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
        updatePermissions(permissionMapKey,identifier,permissionKey,iwc);
        permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      }

      Map permissions = permissionMap.get(identifier,permissionKey);


      if(permissions == null){
        updatePermissions(permissionMapKey,identifier,permissionKey,iwc);
        permissions = permissionMap.get(identifier,permissionKey);
        set = ((Boolean)permissions.get(_SOME_VIEW_PERMISSION_SET));
      } else {
        set = ((Boolean)permissions.get(_SOME_VIEW_PERMISSION_SET));
      }
    } else {
      throw new RuntimeException("PermissionCacher: Cannot find identifier for "+permissionMapKey+" - "+ identifier);
    }
    if(set != null && set.equals(Boolean.TRUE)){
      return true;
    }
    if(set != null && set.equals(Boolean.FALSE)){
      return false;
    } else{
      return true;
    }
  }

  /**
   * Does not handle pages or jsp pages
   */
  public static boolean somePermissionSet( PresentationObject obj, IWContext iwc, String permissionKey) throws SQLException {
    String[] maps = {APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE, APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT}; //, APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE};
    return anyPermissionsDefined(obj,iwc,permissionKey,maps);
  }


  // hasPermission


  public static Boolean hasPermissionForJSPPage(PresentationObject obj, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE,obj,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObjectInstance(PresentationObject obj, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE,obj,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObject(PresentationObject obj, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT,obj,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForBundle(PresentationObject obj, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE,obj,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForPage(PresentationObject obj, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE,obj,iwc,permissionKey,groups);
  }



  public static Boolean hasPermissionForJSPPage(String identifier, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObjectInstance(String identifier, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObject(String identifier, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForBundle(String identifier, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForPage(String identifier, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForFile(String identifier, IWContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(APPLICATION_ADDRESS_PERMISSIONMAP_FILE_ID,identifier,iwc,permissionKey,groups);
  }


  //public static Boolean hasPermission()


  private static Boolean hasPermission(String permissionMapKey, PresentationObject obj, IWContext iwc, String permissionKey, List groups) throws SQLException {
    //
    String identifier = null;
    if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE)){
      identifier = Integer.toString(obj.getICObjectInstanceID(iwc));
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT)){
        identifier = Integer.toString(obj.getICObjectID(iwc));
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE)){
        identifier = obj.getBundleIdentifier();
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE)){
        identifier = Integer.toString(((Page)obj).getPageID());
        //temp
        //identifier = com.idega.builder.business.BuilderLogic.getInstance().getCurrentIBPage(iwc);
    } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE)){
        identifier = Integer.toString(ICJspHandler.getJspPageInstanceID(iwc));
    }
    //

    return hasPermission(permissionMapKey,identifier,iwc,permissionKey,groups);
/*
    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
          permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      }

      List permissions = permissionMap.get(identifier,permissionKey,groups);


      if(permissions == null){
        updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
        permissions = permissionMap.get(identifier,permissionKey,groups);
      }

      Boolean falseOrNull = null;
      if (permissions != null){
        Iterator iter = permissions.iterator();
        while (iter.hasNext()) {
          Boolean item = (Boolean)iter.next();
          if (item != null){
            if (item.equals(Boolean.FALSE)){
              falseOrNull = Boolean.FALSE;
            }else{
              return Boolean.TRUE;
            }
          }
        }
      }
      return falseOrNull;
    } else {
      return null;
    }
    */
  }



  private static Boolean hasPermission(String permissionMapKey, String identifier, IWContext iwc, String permissionKey, List groups) throws SQLException {

    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
          permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      }

      List permissions = permissionMap.get(identifier,permissionKey,groups);


      if(permissions == null){
        updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
        permissions = permissionMap.get(identifier,permissionKey,groups);
      }

      Boolean falseOrNull = null;
      if (permissions != null){
        Iterator iter = permissions.iterator();
        while (iter.hasNext()) {
          Boolean item = (Boolean)iter.next();
          if (item != null){
            if (item.equals(Boolean.FALSE)){
              falseOrNull = Boolean.FALSE;
            }else{
              return Boolean.TRUE;
            }
          }
        }
      }
      return falseOrNull;
    } else {
      return null;
    }
  }



  public static Boolean hasPermission(ICObject obj, IWContext iwc, String permissionKey, List groups) throws SQLException {
    String permissionMapKey = APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT;
    //
    String identifier = Integer.toString(obj.getID());

    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
          permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
      }

      List permissions = permissionMap.get(identifier,permissionKey,groups);


      if(permissions == null){
        updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
        permissions = permissionMap.get(identifier,permissionKey,groups);
      }

      Boolean falseOrNull = null;
      if (permissions != null){
        Iterator iter = permissions.iterator();
        while (iter.hasNext()) {
          Boolean item = (Boolean)iter.next();
          if (item != null){
            if (item.equals(Boolean.FALSE)){
              falseOrNull = Boolean.FALSE;
            }else{
              return Boolean.TRUE;
            }
          }
        }
      }
      return falseOrNull;
    } else {
      return null;
    }
  }








  //Update

  public static void updateObjectInstancePermissions(String instanceId, String permissionKey, IWContext iwc) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE,instanceId,permissionKey,iwc);
  }

  public static void updateObjectPermissions(String objectId, String permissionKey, IWContext iwc) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT,objectId,permissionKey,iwc);
  }

  public static void updateBundlePermissions(String bundleIdentifier, String permissionKey, IWContext iwc) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE,bundleIdentifier,permissionKey,iwc);
  }

  public static void updatePagePermissions(String pageId, String permissionKey, IWContext iwc) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE,pageId,permissionKey,iwc);
  }

  public static void updateJSPPagePermissions(String jspPageId, String permissionKey, IWContext iwc) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE,jspPageId,permissionKey,iwc);
  }

  public static void updateFilePermissions(String fileId, String permissionKey, IWContext iwc) throws SQLException{
    updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_FILE_ID,fileId,permissionKey,iwc);
  }


  public static void updatePermissions(int permissionCategory, String identifier, String permissionKey, IWContext iwc) throws SQLException{
    switch (permissionCategory) {
      case AccessControl._CATEGORY_OBJECT_INSTANCE :
        updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE,identifier,permissionKey,iwc);
        break;
      case AccessControl._CATEGORY_PAGE :
      case AccessControl._CATEGORY_OBJECT :
        updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT,identifier,permissionKey,iwc);
        break;
      case AccessControl._CATEGORY_BUNDLE :
        updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE,identifier,permissionKey,iwc);
        break;
      case AccessControl._CATEGORY_PAGE_INSTANCE :
        updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE,identifier,permissionKey,iwc);
        break;
      case AccessControl._CATEGORY_JSP_PAGE :
        updatePermissions(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE,identifier,permissionKey,iwc);
        break;
    }
  }

  private static void updatePermissions(String permissionMapKey, String identifier, String permissionKey, IWContext iwc) throws SQLException{
    PermissionMap permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
    if(permissionMap == null){
      permissionMap = new PermissionMap();
      iwc.setApplicationAttribute(permissionMapKey,permissionMap);
    }

    //
    List permissions = null;
    if(identifier != null){
      if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT_INSTANCE)){
        permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessController._CATEYGORYSTRING_OBJECT_INSTATNCE_ID,ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_OBJECT)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessController._CATEYGORYSTRING_OBJECT_ID,ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessController._CATEYGORYSTRING_BUNDLE_IDENTIFIER,ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_PAGE_INSTANCE)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessController._CATEYGORYSTRING_PAGE_ID,ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_JSP_PAGE)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessController._CATEYGORYSTRING_JSP_PAGE,ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(APPLICATION_ADDRESS_PERMISSIONMAP_FILE_ID)){
          permissions = EntityFinder.findAllByColumn(ICPermission.getStaticInstance(),ICPermission.getContextTypeColumnName(),AccessController._CATEYGORYSTRING_FILE_ID,ICPermission.getContextValueColumnName(),identifier,ICPermission.getPermissionStringColumnName(),permissionKey);
      }
    }
    //

    if(permissions != null){
      Iterator iter = permissions.iterator();
      boolean first = true;
      Map mapToPutTo = new Hashtable();
      while (iter.hasNext()) {
        ICPermission item = (ICPermission)iter.next();
        mapToPutTo.put(Integer.toString(item.getGroupID()),(item.getPermissionValue())? Boolean.TRUE : Boolean.FALSE);
      }
      mapToPutTo.put(_SOME_VIEW_PERMISSION_SET, Boolean.TRUE);
      permissionMap.put(identifier, permissionKey,mapToPutTo);
    } else {
      Map mapToPutTo = new Hashtable();
      mapToPutTo.put(_SOME_VIEW_PERMISSION_SET, Boolean.FALSE);
      permissionMap.put(identifier, permissionKey,mapToPutTo);
    }
  }





}