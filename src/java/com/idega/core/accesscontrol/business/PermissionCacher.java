package com.idega.core.accesscontrol.business;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.component.data.ICObject;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.Group;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>,
 * Eirikur Hrafnsson
 * @version 1.1
 */

public class PermissionCacher {

	private static final String PERMISSION_MAP_PREFIX = "ic_permission_map_";
  private static final String PERMISSION_MAP_OBJECT = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_OBJECT;
  private static final String PERMISSION_MAP_OBJECT_INSTANCE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_OBJECT_INSTANCE;
  private static final String PERMISSION_MAP_BUNDLE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_BUNDLE;
  private static final String PERMISSION_MAP_PAGE_INSTANCE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_PAGE_INSTANCE;
  private static final String PERMISSION_MAP_JSP_PAGE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_JSP_PAGE;
  private static final String PERMISSION_MAP_FILE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_FILE_ID;
	private static final String PERMISSION_MAP_GROUP = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_GROUP_ID;
	private static final String PERMISSION_MAP_ROLE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_ROLE;
  private static final String _SOME_VIEW_PERMISSION_SET = "ic_viewpermission_set";//for performance (ask Gummi)


  public PermissionCacher() {
  }


  // anyPermissionsDefined

  public static boolean anyInstancePermissionsDefinedForObject( PresentationObject obj, IWUserContext iwc, String permissionKey) throws SQLException{
    String[] maps = {PERMISSION_MAP_OBJECT_INSTANCE};
    return anyPermissionsDefined(obj,iwc,permissionKey,maps);
  }

  public static boolean anyPermissionsDefinedForObject( PresentationObject obj, IWUserContext iwc, String permissionKey) throws SQLException{
    String[] maps = {PERMISSION_MAP_OBJECT};
    return anyPermissionsDefined(obj,iwc,permissionKey,maps);
  }

  public static boolean anyPermissionsDefinedForPage( PresentationObject obj, IWUserContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefinedForObject(obj,iwc,permissionKey);
  }

  public static boolean anyInstancePermissionsDefinedForPage( Object pageObj, IWUserContext iwc, String permissionKey) throws SQLException{
    String[] maps = {PERMISSION_MAP_PAGE_INSTANCE};
    return anyPermissionsDefined(pageObj,iwc,permissionKey,maps);
  }


  public static boolean anyInstancePermissionsDefinedForObject( String identifier, IWUserContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,PERMISSION_MAP_OBJECT_INSTANCE);
  }

  public static boolean anyPermissionsDefinedForObject( String identifier, IWUserContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,PERMISSION_MAP_OBJECT);
  }

  public static boolean anyPermissionsDefinedForPage( String identifier, IWUserContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefinedForObject(identifier,iwc,permissionKey);
  }

  public static boolean anyInstancePermissionsDefinedForPage( String identifier, IWUserContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,PERMISSION_MAP_PAGE_INSTANCE);
  }

  public static boolean anyInstancePermissionsDefinedForFile( String identifier, IWUserContext iwc, String permissionKey) throws SQLException{
    return anyPermissionsDefined(identifier,iwc,permissionKey,PERMISSION_MAP_FILE);
  }




  private static boolean anyPermissionsDefined( Object obj, IWUserContext iwc, String permissionKey, String[] maps) throws SQLException{
    String identifier = null;
    Boolean set = null;


    for (int i = 0; i < maps.length; i++) {
      String permissionMapKey = maps[i];
      if(permissionMapKey.equals(PERMISSION_MAP_OBJECT_INSTANCE)){
      	PresentationObject pObject = (PresentationObject)obj;
        identifier = Integer.toString(pObject.getICObjectInstanceID());
      } else if(permissionMapKey.equals(PERMISSION_MAP_OBJECT)){
      	PresentationObject pObject = (PresentationObject)obj;
      	identifier = Integer.toString(pObject.getICObjectID());
      } else if(permissionMapKey.equals(PERMISSION_MAP_PAGE_INSTANCE)){
      		if(obj instanceof Page){
      			Page page = (Page)obj;
                identifier = Integer.toString(page.getPageID());
      		}
      		else if (obj instanceof AccessControl.PagePermissionObject){
      			AccessControl.PagePermissionObject pageObj = (AccessControl.PagePermissionObject)obj;
      			identifier = pageObj.getPageKey();
      		}
      }

      if(identifier != null){
        PermissionMap permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
        if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier,permissionKey,iwc);
          permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
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



  private static boolean anyPermissionsDefined( String identifier, IWUserContext iwc, String permissionKey, String map) throws SQLException{
    Boolean set = null;

    String permissionMapKey = map;

    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
        updatePermissions(permissionMapKey,identifier,permissionKey,iwc);
        permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
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
  public static boolean somePermissionSet( PresentationObject obj, IWUserContext iwc, String permissionKey) throws SQLException {
    String[] maps = {PERMISSION_MAP_OBJECT_INSTANCE, PERMISSION_MAP_OBJECT}; //, APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE};
    return anyPermissionsDefined(obj,iwc,permissionKey,maps);
  }


  public static Boolean hasPermissionForJSPPage(IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_JSP_PAGE,null,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObjectInstance(PresentationObject obj, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_OBJECT_INSTANCE,obj,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObject(PresentationObject obj, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_OBJECT,obj,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForBundle(PresentationObject obj, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_BUNDLE,obj,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForPage(Object obj, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_PAGE_INSTANCE,obj,iwc,permissionKey,groups);
  }
  
	public static Boolean hasPermissionForGroup(Group group, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_GROUP,group,iwc,permissionKey,groups);
	}
	
  
	public static Boolean hasPermissionForRole(String theRoleIdentifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_ROLE,theRoleIdentifier,iwc,permissionKey,groups);
	}

  public static Boolean hasPermissionForJSPPage(String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_JSP_PAGE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObjectInstance(String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_OBJECT_INSTANCE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForObject(String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_OBJECT,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForBundle(String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_BUNDLE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForPage(String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_PAGE_INSTANCE,identifier,iwc,permissionKey,groups);
  }

  public static Boolean hasPermissionForFile(String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    return hasPermission(PERMISSION_MAP_FILE,identifier,iwc,permissionKey,groups);
  }

	public static Boolean hasPermissionForGroup(String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_GROUP,identifier,iwc,permissionKey,groups);
	}

  private static Boolean hasPermission(String permissionMapKey, Object obj, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    String identifier = getIdentifier(permissionMapKey, obj, iwc);

    return hasPermission(permissionMapKey,identifier,iwc,permissionKey,groups);
  }

	protected static String getIdentifier(String permissionMapKey, Object obj, IWUserContext iwc) {
		String identifier = null;
		if(permissionMapKey.equals(PERMISSION_MAP_OBJECT_INSTANCE)){
		  identifier = Integer.toString( ((PresentationObject) obj).getICObjectInstanceID());
		} 
		else if(permissionMapKey.equals(PERMISSION_MAP_OBJECT)){
		    identifier = Integer.toString( ((PresentationObject) obj).getICObjectID());//todo change to icobject?
		} 
		else if(permissionMapKey.equals(PERMISSION_MAP_BUNDLE)){
		    identifier = ((PresentationObject) obj).getBundleIdentifier();//todo change to bundle?
		} 
		else if(permissionMapKey.equals(PERMISSION_MAP_PAGE_INSTANCE)){
			if(obj instanceof Page){
			    identifier = Integer.toString(((Page)obj).getPageID());
			}
			else if(obj instanceof AccessControl.PagePermissionObject){
				AccessControl.PagePermissionObject page = (AccessControl.PagePermissionObject)obj;
				return page.getPageKey();
			}
			else if(obj instanceof String){
				return (String)obj;
			}
		    //temp
		    //identifier = com.idega.builder.business.BuilderLogic.getInstance().getCurrentIBPage(iwc);
		} 
		else if(permissionMapKey.equals(PERMISSION_MAP_JSP_PAGE)){
		    //identifier = Integer.toString(com.idega.builder.business.IBJspHandler.getJspPageInstanceID(iwc));
			throw new UnsupportedOperationException("PermissionCacher : PermissinonType: "+PERMISSION_MAP_JSP_PAGE+" is not supported");
		}
		else if(permissionMapKey.equals(PERMISSION_MAP_GROUP)){
			identifier = ((Group) obj).getPrimaryKey().toString();
		}
		else if(permissionMapKey.equals(PERMISSION_MAP_ROLE)){
			identifier = obj.toString();
		}
		else{
			System.err.println("ACCESSCONTROL: type not supported");
		
		}
		
		return identifier;
	}


/**
 * The permissionchecking ends in this method.
 * 
 * @param permissionMapKey
 * @param identifier
 * @param iwc
 * @param permissionKey
 * @param groups
 * @return Boolean
 * @throws SQLException
 */

  private static Boolean hasPermission(String permissionMapKey, String identifier, IWUserContext iwc, String permissionKey, List groups) throws SQLException {

    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
          permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
      }

      List permissions = permissionMap.get(identifier,permissionKey,groups);


      if(permissions == null){
        updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
        permissions = permissionMap.get(identifier,permissionKey,groups);
      }

      if (permissions != null){
      	if( permissions.contains(Boolean.TRUE)){
      		return Boolean.TRUE;
      	}
      /*  Iterator iter = permissions.iterator();
        while (iter.hasNext()) {
          Boolean item = (Boolean)iter.next();
          if (item != null){
            if (item.equals(Boolean.FALSE)){
              falseOrNull = Boolean.FALSE;
            }else{
              return Boolean.TRUE;
            }
          }
        }*/
        
      }
      
      
      return Boolean.FALSE;
    } 
    else {
      return Boolean.FALSE;
    }
    
    
  }



  public static Boolean hasPermission(ICObject obj, IWUserContext iwc, String permissionKey, List groups) throws SQLException {
    String permissionMapKey = PERMISSION_MAP_OBJECT;
    //
    String identifier = Integer.toString(obj.getID());

    if(identifier != null){
      PermissionMap permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
      if(permissionMap == null){
          updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
          permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
      }

      List permissions = permissionMap.get(identifier,permissionKey,groups);


      if(permissions == null){
        updatePermissions(permissionMapKey,identifier, permissionKey, iwc);
        permissions = permissionMap.get(identifier,permissionKey,groups);
      }
			
			if (permissions != null){
				if( permissions.contains(Boolean.TRUE)){
					return Boolean.TRUE;
				}
			/*  Iterator iter = permissions.iterator();
				while (iter.hasNext()) {
					Boolean item = (Boolean)iter.next();
					if (item != null){
						if (item.equals(Boolean.FALSE)){
							falseOrNull = Boolean.FALSE;
						}else{
							return Boolean.TRUE;
						}
					}
				}*/
  
			}


			return Boolean.FALSE;
    } else {
      return null;
    }
  }








  //Update

  public static void updateObjectInstancePermissions(String instanceId, String permissionKey, IWUserContext iwc) throws SQLException{
    updatePermissions(PERMISSION_MAP_OBJECT_INSTANCE,instanceId,permissionKey,iwc);
  }

  public static void updateObjectPermissions(String objectId, String permissionKey, IWUserContext iwc) throws SQLException{
    updatePermissions(PERMISSION_MAP_OBJECT,objectId,permissionKey,iwc);
  }

  public static void updateBundlePermissions(String bundleIdentifier, String permissionKey, IWUserContext iwc) throws SQLException{
    updatePermissions(PERMISSION_MAP_BUNDLE,bundleIdentifier,permissionKey,iwc);
  }

  public static void updatePagePermissions(String pageId, String permissionKey, IWUserContext iwc) throws SQLException{
    updatePermissions(PERMISSION_MAP_PAGE_INSTANCE,pageId,permissionKey,iwc);
  }

  public static void updateJSPPagePermissions(String jspPageId, String permissionKey, IWUserContext iwc) throws SQLException{
    updatePermissions(PERMISSION_MAP_JSP_PAGE,jspPageId,permissionKey,iwc);
  }

  public static void updateFilePermissions(String fileId, String permissionKey, IWUserContext iwc) throws SQLException{
    updatePermissions(PERMISSION_MAP_FILE,fileId,permissionKey,iwc);
  }


  public static void updatePermissions(int permissionCategory, String identifier, String permissionKey, IWUserContext iwc) throws SQLException{
    switch (permissionCategory) {
      case AccessControl.CATEGORY_OBJECT_INSTANCE :
        updatePermissions(PERMISSION_MAP_OBJECT_INSTANCE,identifier,permissionKey,iwc);
        break;
      case AccessControl.CATEGORY_PAGE :
      case AccessControl.CATEGORY_OBJECT :
        updatePermissions(PERMISSION_MAP_OBJECT,identifier,permissionKey,iwc);
        break;
      case AccessControl.CATEGORY_BUNDLE :
        updatePermissions(PERMISSION_MAP_BUNDLE,identifier,permissionKey,iwc);
        break;
      case AccessControl.CATEGORY_PAGE_INSTANCE :
        updatePermissions(PERMISSION_MAP_PAGE_INSTANCE,identifier,permissionKey,iwc);
        break;
      case AccessControl.CATEGORY_GROUP_ID :
        updatePermissions(PERMISSION_MAP_GROUP,identifier,permissionKey,iwc);
        break;
      case AccessControl.CATEGORY_ROLE :
		updatePermissions(PERMISSION_MAP_ROLE,identifier,permissionKey,iwc);
		break;
        
        
    }
  }

  private synchronized static void updatePermissions(String permissionMapKey, String identifier, String permissionKey, IWUserContext iwc) throws SQLException{
    //PermissionMap permissionMap = (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
    PermissionMap permissionMap = (PermissionMap)iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);

    if(permissionMap == null){
      permissionMap = new PermissionMap();
      iwc.getApplicationContext().setApplicationAttribute(permissionMapKey,permissionMap);
    }

    //
    List permissions = null;
    if(identifier != null){
      if(permissionMapKey.equals(PERMISSION_MAP_OBJECT_INSTANCE)){
        permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(PERMISSION_MAP_OBJECT)){
          //permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_IC_OBJECT_ID,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
      		//temporary fix because of hsql select problems:
      	permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_IC_OBJECT_ID,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(PERMISSION_MAP_BUNDLE)){
          permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_BUNDLE_IDENTIFIER,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(PERMISSION_MAP_PAGE_INSTANCE)){
          //permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_PAGE_ID,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
      	//temporary fix because of hsql select problems:
      	permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_PAGE_ID,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);

      } else if(permissionMapKey.equals(PERMISSION_MAP_JSP_PAGE)){
          permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_JSP_PAGE,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
      } else if(permissionMapKey.equals(PERMISSION_MAP_FILE)){
          permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_FILE_ID,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
      }
		else if(permissionMapKey.equals(PERMISSION_MAP_GROUP)){
			permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),AccessController.CATEGORY_STRING_GROUP_ID,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
		}
		else if(permissionMapKey.equals(PERMISSION_MAP_ROLE)){
			permissions = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(),identifier,com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),permissionKey);
		}
    }
    //TODO ask gummi how this works

    if(permissions != null){
      Iterator iter = permissions.iterator();
      Map mapToPutTo = new Hashtable();
      while (iter.hasNext()) {
        ICPermission item = (ICPermission)iter.next();
        //make sure that contextvalue is equal to identifier because of the hsql hack above:
        String itemContextValue = item.getContextValue();
        if(itemContextValue!=null){
	        if(itemContextValue.equals(identifier)){
	        		mapToPutTo.put(Integer.toString(item.getGroupID()),(item.getPermissionValue())? Boolean.TRUE : Boolean.FALSE);
	        }
        }
      }
      
      //THIS IS DONE SO YOU ALWAYS HAVE VIEW PERMISSION IF NO PERMISSION IS DEFINED
      mapToPutTo.put(_SOME_VIEW_PERMISSION_SET, Boolean.TRUE);
      permissionMap.put(identifier, permissionKey,mapToPutTo);
    } else {
      Map mapToPutTo = new Hashtable();
      mapToPutTo.put(_SOME_VIEW_PERMISSION_SET, Boolean.FALSE);
      permissionMap.put(identifier, permissionKey,mapToPutTo);
    }
  }





}
