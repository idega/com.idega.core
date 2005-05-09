/*
 * $Id: PermissionCacher.java,v 1.28 2005/05/09 12:56:31 palli Exp $ Created
 * in 2001
 * 
 * Copyright (C) 2001-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
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
 * <p>
 * Class used for caching of permissions from the ICPermission table - used by
 * AccessControl.
 * </p>
 * 
 * Last modified: $Date: 2005/05/09 12:56:31 $ by $Author: palli $
 * 
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson </a>,
 *         Eirikur Hrafnsson, Tryggvi Larusson
 * 
 * @version $Revision: 1.28 $
 */
class PermissionCacher {

	private final String PERMISSION_MAP_PREFIX = "ic_permission_map_";
	private final String PERMISSION_MAP_OBJECT = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_OBJECT;
	private final String PERMISSION_MAP_OBJECT_INSTANCE = PERMISSION_MAP_PREFIX
			+ AccessController.CATEGORY_OBJECT_INSTANCE;
	private final String PERMISSION_MAP_BUNDLE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_BUNDLE;
	private final String PERMISSION_MAP_PAGE_INSTANCE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_PAGE_INSTANCE;
	private final String PERMISSION_MAP_JSP_PAGE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_JSP_PAGE;
	private final String PERMISSION_MAP_FILE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_FILE_ID;
	private final String PERMISSION_MAP_GROUP = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_GROUP_ID;
	private final String PERMISSION_MAP_ROLE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_ROLE;
	//for performance Gummi
	private final String _SOME_VIEW_PERMISSION_SET = "ic_viewpermission_set";

	public PermissionCacher() {
	}

	// anyPermissionsDefined
	public boolean anyInstancePermissionsDefinedForObject(PresentationObject obj, IWUserContext iwc,
			String permissionKey) throws SQLException {
		String[] maps = { PERMISSION_MAP_OBJECT_INSTANCE };
		return anyPermissionsDefined(obj, iwc, permissionKey, maps);
	}

	public boolean anyPermissionsDefinedForObject(PresentationObject obj, IWUserContext iwc, String permissionKey)
			throws SQLException {
		String[] maps = { PERMISSION_MAP_OBJECT };
		return anyPermissionsDefined(obj, iwc, permissionKey, maps);
	}

	public boolean anyPermissionsDefinedForPage(PresentationObject obj, IWUserContext iwc, String permissionKey)
			throws SQLException {
		return anyPermissionsDefinedForObject(obj, iwc, permissionKey);
	}

	public boolean anyInstancePermissionsDefinedForPage(Object pageObj, IWUserContext iwc, String permissionKey)
			throws SQLException {
		String[] maps = { PERMISSION_MAP_PAGE_INSTANCE };
		return anyPermissionsDefined(pageObj, iwc, permissionKey, maps);
	}

	public boolean anyInstancePermissionsDefinedForObject(String identifier, IWUserContext iwc, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwc, permissionKey, PERMISSION_MAP_OBJECT_INSTANCE);
	}

	public boolean anyPermissionsDefinedForObject(String identifier, IWUserContext iwc, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwc, permissionKey, PERMISSION_MAP_OBJECT);
	}

	public boolean anyPermissionsDefinedForPage(String identifier, IWUserContext iwc, String permissionKey)
			throws SQLException {
		return anyPermissionsDefinedForObject(identifier, iwc, permissionKey);
	}

	public boolean anyInstancePermissionsDefinedForPage(String identifier, IWUserContext iwc, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwc, permissionKey, PERMISSION_MAP_PAGE_INSTANCE);
	}

	public boolean anyInstancePermissionsDefinedForFile(String identifier, IWUserContext iwc, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwc, permissionKey, PERMISSION_MAP_FILE);
	}

	private boolean anyPermissionsDefined(Object obj, IWUserContext iwc, String permissionKey, String[] maps)
			throws SQLException {
		String identifier = null;
		Boolean set = null;
		for (int i = 0; i < maps.length; i++) {
			String permissionMapKey = maps[i];
			if (permissionMapKey.equals(PERMISSION_MAP_OBJECT_INSTANCE)) {
				PresentationObject pObject = (PresentationObject) obj;
				identifier = Integer.toString(pObject.getICObjectInstanceID());
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_OBJECT)) {
				PresentationObject pObject = (PresentationObject) obj;
				identifier = Integer.toString(pObject.getICObjectID());
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_PAGE_INSTANCE)) {
				if (obj instanceof Page) {
					Page page = (Page) obj;
					identifier = Integer.toString(page.getPageID());
				}
				else if (obj instanceof AccessControl.PagePermissionObject) {
					AccessControl.PagePermissionObject pageObj = (AccessControl.PagePermissionObject) obj;
					identifier = pageObj.getPageKey();
				}
			}
			if (identifier != null) {
				PermissionMap permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(
						permissionMapKey);
				if (permissionMap == null) {
					updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
					permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(
							permissionMapKey);
				}
				Map permissions = permissionMap.get(identifier, permissionKey);
				if (permissions == null) {
					updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
					permissions = permissionMap.get(identifier, permissionKey);
					set = ((Boolean) permissions.get(_SOME_VIEW_PERMISSION_SET));
				}
				else {
					set = ((Boolean) permissions.get(_SOME_VIEW_PERMISSION_SET));
				}
			}
			else {
				throw new RuntimeException("PermissionCacher: Cannot find identifier for " + permissionMapKey + " - "
						+ obj);
			}
			if (set != null && set.equals(Boolean.TRUE)) {
				return true;
			}
		}
		if (set != null && set.equals(Boolean.FALSE)) {
			return false;
		}
		else {
			return true;
		}
	}

	private boolean anyPermissionsDefined(String identifier, IWUserContext iwc, String permissionKey, String map)
			throws SQLException {
		Boolean set = null;
		String permissionMapKey = map;
		if (identifier != null) {
			PermissionMap permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(
					permissionMapKey);
			if (permissionMap == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
				permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
			}
			Map permissions = permissionMap.get(identifier, permissionKey);
			if (permissions == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
				permissions = permissionMap.get(identifier, permissionKey);
				set = ((Boolean) permissions.get(_SOME_VIEW_PERMISSION_SET));
			}
			else {
				set = ((Boolean) permissions.get(_SOME_VIEW_PERMISSION_SET));
			}
		}
		else {
			throw new RuntimeException("PermissionCacher: Cannot find identifier for " + permissionMapKey + " - "
					+ identifier);
		}
		if (set != null && set.equals(Boolean.TRUE)) {
			return true;
		}
		if (set != null && set.equals(Boolean.FALSE)) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Does not handle pages or jsp pages
	 */
	public boolean somePermissionSet(PresentationObject obj, IWUserContext iwc, String permissionKey)
			throws SQLException {
		String[] maps = { PERMISSION_MAP_OBJECT_INSTANCE, PERMISSION_MAP_OBJECT }; // ,
																					// APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE};
		return anyPermissionsDefined(obj, iwc, permissionKey, maps);
	}

	public Boolean hasPermissionForJSPPage(IWUserContext iwc, String permissionKey, List groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_JSP_PAGE, null, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForObjectInstance(PresentationObject obj, IWUserContext iwc, String permissionKey,
			List groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT_INSTANCE, obj, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForObject(PresentationObject obj, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT, obj, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForBundle(PresentationObject obj, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_BUNDLE, obj, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForPage(Object obj, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_PAGE_INSTANCE, obj, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForGroup(Group group, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_GROUP, group, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForRole(String theRoleIdentifier, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_ROLE, theRoleIdentifier, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForJSPPage(String identifier, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_JSP_PAGE, identifier, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForObjectInstance(String identifier, IWUserContext iwc, String permissionKey,
			List groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT_INSTANCE, identifier, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForObject(String identifier, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT, identifier, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForBundle(String identifier, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_BUNDLE, identifier, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForPage(String identifier, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_PAGE_INSTANCE, identifier, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForFile(String identifier, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_FILE, identifier, iwc, permissionKey, groups);
	}

	public Boolean hasPermissionForGroup(String identifier, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_GROUP, identifier, iwc, permissionKey, groups);
	}

	private Boolean hasPermission(String permissionMapKey, Object obj, IWUserContext iwc, String permissionKey,
			List groups) throws SQLException {
		String identifier = getIdentifier(permissionMapKey, obj, iwc);
		return hasPermission(permissionMapKey, identifier, iwc, permissionKey, groups);
	}

	protected String getIdentifier(String permissionMapKey, Object obj, IWUserContext iwc) {
		String identifier = null;
		if (permissionMapKey.equals(PERMISSION_MAP_OBJECT_INSTANCE)) {
			identifier = Integer.toString(((PresentationObject) obj).getICObjectInstanceID());
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_OBJECT)) {
			identifier = Integer.toString(((PresentationObject) obj).getICObjectID());// todo
																						// change
																						// to
																						// icobject?
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_BUNDLE)) {
			identifier = ((PresentationObject) obj).getBundleIdentifier();// todo
																			// change
																			// to
																			// bundle?
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_PAGE_INSTANCE)) {
			if (obj instanceof Page) {
				identifier = Integer.toString(((Page) obj).getPageID());
			}
			else if (obj instanceof AccessControl.PagePermissionObject) {
				AccessControl.PagePermissionObject page = (AccessControl.PagePermissionObject) obj;
				return page.getPageKey();
			}
			else if (obj instanceof String) {
				return (String) obj;
			}
			// temp
			// identifier =
			// com.idega.builder.business.BuilderLogic.getInstance().getCurrentIBPage(iwc);
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_JSP_PAGE)) {
			// identifier =
			// Integer.toString(com.idega.builder.business.IBJspHandler.getJspPageInstanceID(iwc));
			throw new UnsupportedOperationException("PermissionCacher : PermissinonType: " + PERMISSION_MAP_JSP_PAGE
					+ " is not supported");
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_GROUP)) {
			identifier = ((Group) obj).getPrimaryKey().toString();
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_ROLE)) {
			identifier = obj.toString();
		}
		else {
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
	private Boolean hasPermission(String permissionMapKey, String identifier, IWUserContext iwc, String permissionKey,
			List groups) throws SQLException {
		if (identifier != null) {
			PermissionMap permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(
					permissionMapKey);
			if (permissionMap == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
				permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
			}
			List permissions = permissionMap.get(identifier, permissionKey, groups);
			if (permissions == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
				permissions = permissionMap.get(identifier, permissionKey, groups);
			}
			if (permissions != null) {
				if (permissions.contains(Boolean.TRUE)) {
					return Boolean.TRUE;
				}
				/*
				 * Iterator iter = permissions.iterator(); while
				 * (iter.hasNext()) { Boolean item = (Boolean)iter.next(); if
				 * (item != null){ if (item.equals(Boolean.FALSE)){ falseOrNull =
				 * Boolean.FALSE; }else{ return Boolean.TRUE; } } }
				 */
			}
			return Boolean.FALSE;
		}
		else {
			return Boolean.FALSE;
		}
	}

	public Boolean hasPermission(ICObject obj, IWUserContext iwc, String permissionKey, List groups)
			throws SQLException {
		String permissionMapKey = PERMISSION_MAP_OBJECT;
		String identifier="-1";
		Object primaryKey = obj.getPrimaryKey();
		if(primaryKey!=null){
			identifier=primaryKey.toString();
		}
		if (identifier != null) {
			PermissionMap permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(
					permissionMapKey);
			if (permissionMap == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
				permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(permissionMapKey);
			}
			List permissions = permissionMap.get(identifier, permissionKey, groups);
			if (permissions == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwc);
				permissions = permissionMap.get(identifier, permissionKey, groups);
			}
			if (permissions != null) {
				if (permissions.contains(Boolean.TRUE)) {
					return Boolean.TRUE;
				}
				/*
				 * Iterator iter = permissions.iterator(); while
				 * (iter.hasNext()) { Boolean item = (Boolean)iter.next(); if
				 * (item != null){ if (item.equals(Boolean.FALSE)){ falseOrNull =
				 * Boolean.FALSE; }else{ return Boolean.TRUE; } } }
				 */
			}
			return Boolean.FALSE;
		}
		else {
			return null;
		}
	}

	// Update
	public void updateObjectInstancePermissions(String instanceId, String permissionKey, IWUserContext iwc)
			throws SQLException {
		updatePermissions(PERMISSION_MAP_OBJECT_INSTANCE, instanceId, permissionKey, iwc);
	}

	public void updateObjectPermissions(String objectId, String permissionKey, IWUserContext iwc) throws SQLException {
		updatePermissions(PERMISSION_MAP_OBJECT, objectId, permissionKey, iwc);
	}

	public void updateBundlePermissions(String bundleIdentifier, String permissionKey, IWUserContext iwc)
			throws SQLException {
		updatePermissions(PERMISSION_MAP_BUNDLE, bundleIdentifier, permissionKey, iwc);
	}

	public void updatePagePermissions(String pageId, String permissionKey, IWUserContext iwc) throws SQLException {
		updatePermissions(PERMISSION_MAP_PAGE_INSTANCE, pageId, permissionKey, iwc);
	}

	public void updateJSPPagePermissions(String jspPageId, String permissionKey, IWUserContext iwc) throws SQLException {
		updatePermissions(PERMISSION_MAP_JSP_PAGE, jspPageId, permissionKey, iwc);
	}

	public void updateFilePermissions(String fileId, String permissionKey, IWUserContext iwc) throws SQLException {
		updatePermissions(PERMISSION_MAP_FILE, fileId, permissionKey, iwc);
	}

	public void updatePermissions(int permissionCategory, String identifier, String permissionKey, IWUserContext iwc)
			throws SQLException {
		switch (permissionCategory) {
			case AccessControl.CATEGORY_OBJECT_INSTANCE:
				updatePermissions(PERMISSION_MAP_OBJECT_INSTANCE, identifier, permissionKey, iwc);
				break;
			case AccessControl.CATEGORY_PAGE:
			case AccessControl.CATEGORY_OBJECT:
				updatePermissions(PERMISSION_MAP_OBJECT, identifier, permissionKey, iwc);
				break;
			case AccessControl.CATEGORY_BUNDLE:
				updatePermissions(PERMISSION_MAP_BUNDLE, identifier, permissionKey, iwc);
				break;
			case AccessControl.CATEGORY_PAGE_INSTANCE:
				updatePermissions(PERMISSION_MAP_PAGE_INSTANCE, identifier, permissionKey, iwc);
				break;
			case AccessControl.CATEGORY_GROUP_ID:
				updatePermissions(PERMISSION_MAP_GROUP, identifier, permissionKey, iwc);
				break;
			case AccessControl.CATEGORY_ROLE:
				updatePermissions(PERMISSION_MAP_ROLE, identifier, permissionKey, iwc);
				break;
		}
	}

	private synchronized void updatePermissions(String permissionMapKey, String identifier, String permissionKey,
			IWUserContext iwc) throws SQLException {
		// PermissionMap permissionMap =
		// (PermissionMap)iwc.getApplicationAttribute(permissionMapKey);
		PermissionMap permissionMap = (PermissionMap) iwc.getApplicationContext().getApplicationAttribute(
				permissionMapKey);
		if (permissionMap == null) {
			permissionMap = new PermissionMap();
			iwc.getApplicationContext().setApplicationAttribute(permissionMapKey, permissionMap);
		}
		//
		List permissions = null;
		if (identifier != null) {
			if (permissionMapKey.equals(PERMISSION_MAP_OBJECT_INSTANCE)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),
						AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_OBJECT)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),
						AccessController.CATEGORY_STRING_IC_OBJECT_ID,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_BUNDLE)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),
						AccessController.CATEGORY_STRING_BUNDLE_IDENTIFIER,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_PAGE_INSTANCE)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),
						AccessController.CATEGORY_STRING_PAGE_ID,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_JSP_PAGE)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),
						AccessController.CATEGORY_STRING_JSP_PAGE,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_FILE)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),
						AccessController.CATEGORY_STRING_FILE_ID,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_GROUP)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(),
						AccessController.CATEGORY_STRING_GROUP_ID,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
			else if (permissionMapKey.equals(PERMISSION_MAP_ROLE)) {
				permissions = EntityFinder.findAllByColumn(
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getStaticInstance(),
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextTypeColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getContextValueColumnName(), identifier,
						com.idega.core.accesscontrol.data.ICPermissionBMPBean.getPermissionStringColumnName(),
						permissionKey);
			}
		}
		// TODO ask gummi how this works
		if (permissions != null) {
			Iterator iter = permissions.iterator();
			Map mapToPutTo = new Hashtable();
			while (iter.hasNext()) {
				ICPermission item = (ICPermission) iter.next();
				mapToPutTo.put(Integer.toString(item.getGroupID()), (item.getPermissionValue()) ? Boolean.TRUE
						: Boolean.FALSE);
			}
			// THIS IS DONE SO YOU ALWAYS HAVE VIEW PERMISSION IF NO PERMISSION
			// IS DEFINED
			mapToPutTo.put(_SOME_VIEW_PERMISSION_SET, Boolean.TRUE);
			permissionMap.put(identifier, permissionKey, mapToPutTo);
		}
		else {
			Map mapToPutTo = new Hashtable();
			mapToPutTo.put(_SOME_VIEW_PERMISSION_SET, Boolean.FALSE);
			permissionMap.put(identifier, permissionKey, mapToPutTo);
		}
	}
}
