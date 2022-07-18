/*
 * $Id: PermissionCacher.java,v 1.35 2009/01/14 15:12:23 tryggvil Exp $ Created
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

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.dao.PermissionDAO;
import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.component.data.bean.ICObject;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.bean.Group;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * Class used for caching of permissions from the ICPermission table - used by
 * AccessControl.
 * </p>
 *
 * Last modified: $Date: 2009/01/14 15:12:23 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson </a>,
 *         Eirikur Hrafnsson, Tryggvi Larusson
 *
 * @version $Revision: 1.35 $
 */
class PermissionCacher {

	private static final String PERMISSION_MAP_PREFIX = "ic_permission_map_";
	private static final String PERMISSION_MAP_OBJECT = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_OBJECT;
	private static final String PERMISSION_MAP_OBJECT_INSTANCE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_OBJECT_INSTANCE;
	private static final String PERMISSION_MAP_BUNDLE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_BUNDLE;
	private static final String PERMISSION_MAP_PAGE_INSTANCE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_PAGE_INSTANCE;
	private static final String PERMISSION_MAP_JSP_PAGE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_JSP_PAGE;
	private static final String PERMISSION_MAP_FILE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_FILE_ID;
	private static final String PERMISSION_MAP_GROUP = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_GROUP_ID;
	private static final String PERMISSION_MAP_ROLE = PERMISSION_MAP_PREFIX + AccessController.CATEGORY_ROLE,

								MINUS_ONE = "-1";

	//for performance Gummi
	private final String _SOME_VIEW_PERMISSION_SET = "ic_viewpermission_set";

	@Autowired
	private PermissionDAO permissionDAO;

	private PermissionDAO getPermissionDAO() {
		if (permissionDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return permissionDAO;
	}

	public PermissionCacher() {
	}

	// anyPermissionsDefined
	public boolean anyInstancePermissionsDefinedForObject(PresentationObject obj, IWApplicationContext iwac,
			String permissionKey) throws SQLException {
		String[] maps = { PERMISSION_MAP_OBJECT_INSTANCE };
		return anyPermissionsDefined(obj, iwac, permissionKey, maps);
	}

	public boolean anyPermissionsDefinedForObject(PresentationObject obj, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		String[] maps = { PERMISSION_MAP_OBJECT };
		return anyPermissionsDefined(obj, iwac, permissionKey, maps);
	}

	public boolean anyPermissionsDefinedForPage(PresentationObject obj, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		return anyPermissionsDefinedForObject(obj, iwac, permissionKey);
	}

	public boolean anyInstancePermissionsDefinedForPage(Object pageObj, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		String[] maps = { PERMISSION_MAP_PAGE_INSTANCE };
		return anyPermissionsDefined(pageObj, iwac, permissionKey, maps);
	}

	public boolean anyInstancePermissionsDefinedForObject(String identifier, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwac, permissionKey, PERMISSION_MAP_OBJECT_INSTANCE);
	}

	public boolean anyPermissionsDefinedForObject(String identifier, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwac, permissionKey, PERMISSION_MAP_OBJECT);
	}

	public boolean anyPermissionsDefinedForPage(String identifier, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		return anyPermissionsDefinedForObject(identifier, iwac, permissionKey);
	}

	public boolean anyInstancePermissionsDefinedForPage(String identifier, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwac, permissionKey, PERMISSION_MAP_PAGE_INSTANCE);
	}

	public boolean anyInstancePermissionsDefinedForFile(String identifier, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		return anyPermissionsDefined(identifier, iwac, permissionKey, PERMISSION_MAP_FILE);
	}

	private boolean anyPermissionsDefined(Object obj, IWApplicationContext iwac, String permissionKey, String[] maps)
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
				else if (obj instanceof PagePermissionObject) {
					PagePermissionObject pageObj = (PagePermissionObject) obj;
					identifier = pageObj.getPageKey();
				}
			}
			if (identifier != null) {
				PermissionMap permissionMap = (PermissionMap) iwac.getApplicationAttribute(
						permissionMapKey);
				if (permissionMap == null) {
					updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
					permissionMap = (PermissionMap) iwac.getApplicationAttribute(
							permissionMapKey);
				}
				Map<?, ?> permissions = permissionMap.get(identifier, permissionKey);
				if (permissions == null) {
					updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
					permissions = permissionMap.get(identifier, permissionKey);
					set = ((Boolean) permissions.get(_SOME_VIEW_PERMISSION_SET));
				}
				else {
					set = ((Boolean) permissions.get(this._SOME_VIEW_PERMISSION_SET));
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

	private boolean anyPermissionsDefined(String identifier, IWApplicationContext iwac, String permissionKey, String map)
			throws SQLException {
		Boolean set = null;
		String permissionMapKey = map;
		if (identifier != null) {
			PermissionMap permissionMap = (PermissionMap) iwac.getApplicationAttribute(
					permissionMapKey);
			if (permissionMap == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
				permissionMap = (PermissionMap) iwac.getApplicationAttribute(permissionMapKey);
			}
			Map<?, ?> permissions = permissionMap.get(identifier, permissionKey);
			if (permissions == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
				permissions = permissionMap.get(identifier, permissionKey);
				set = ((Boolean) permissions.get(this._SOME_VIEW_PERMISSION_SET));
			}
			else {
				set = ((Boolean) permissions.get(this._SOME_VIEW_PERMISSION_SET));
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
	public boolean somePermissionSet(PresentationObject obj, IWApplicationContext iwac, String permissionKey)
			throws SQLException {
		String[] maps = { PERMISSION_MAP_OBJECT_INSTANCE, PERMISSION_MAP_OBJECT }; // ,
																					// APPLICATION_ADDRESS_PERMISSIONMAP_BUNDLE};
		return anyPermissionsDefined(obj, iwac, permissionKey, maps);
	}

	public Boolean hasPermissionForJSPPage(IWApplicationContext iwac, String permissionKey, List<?> groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_JSP_PAGE, null, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForObjectInstance(PresentationObject obj, IWApplicationContext iwac, String permissionKey,
			List<?> groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT_INSTANCE, obj, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForObject(PresentationObject obj, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT, obj, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForBundle(PresentationObject obj, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_BUNDLE, obj, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForPage(Object obj, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_PAGE_INSTANCE, obj, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForGroup(Group group, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_GROUP, group, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForRole(String theRoleIdentifier, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_ROLE, theRoleIdentifier, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForJSPPage(String identifier, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_JSP_PAGE, identifier, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForObjectInstance(String identifier, IWApplicationContext iwac, String permissionKey,
			List<?> groups) throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT_INSTANCE, identifier, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForObject(String identifier, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_OBJECT, identifier, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForBundle(String identifier, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_BUNDLE, identifier, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForPage(String identifier, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_PAGE_INSTANCE, identifier, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForFile(String identifier, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_FILE, identifier, iwac, permissionKey, groups);
	}

	public Boolean hasPermissionForGroup(String identifier, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		return hasPermission(PERMISSION_MAP_GROUP, identifier, iwac, permissionKey, groups);
	}

	private Boolean hasPermission(String permissionMapKey, Object obj, IWApplicationContext iwac, String permissionKey,
			List<?> groups) throws SQLException {
		String identifier = getIdentifier(permissionMapKey, obj);
		return hasPermission(permissionMapKey, identifier, iwac, permissionKey, groups);
	}

	protected String getIdentifier(String permissionMapKey, Object obj) {
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
			else if (obj instanceof PagePermissionObject) {
				PagePermissionObject page = (PagePermissionObject) obj;
				return page.getPageKey();
			}
			else if (obj instanceof String) {
				return (String) obj;
			}
			// temp
			// identifier =
			// com.idega.builder.business.BuilderLogic.getInstance().getCurrentIBPage(iwac);
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_JSP_PAGE)) {
			// identifier =
			// Integer.toString(com.idega.builder.business.IBJspHandler.getJspPageInstanceID(iwac));
			throw new UnsupportedOperationException("PermissionCacher : PermissinonType: " + PERMISSION_MAP_JSP_PAGE
					+ " is not supported");
		}
		else if (permissionMapKey.equals(PERMISSION_MAP_GROUP)) {
			identifier = ((Group) obj).getID().toString();
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
	 * @param iwac
	 * @param permissionKey
	 * @param groups
	 * @return Boolean
	 * @throws SQLException
	 */
	private Boolean hasPermission(String permissionMapKey, String identifier, IWApplicationContext iwac, String permissionKey,
			List<?> groups) throws SQLException {
		if (identifier != null) {
			PermissionMap permissionMap = (PermissionMap) iwac.getApplicationAttribute(
					permissionMapKey);
			if (permissionMap == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
				permissionMap = (PermissionMap) iwac.getApplicationAttribute(permissionMapKey);
			}
			List<?> permissions = permissionMap.get(identifier, permissionKey, groups);
			if (permissions == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
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

	public Boolean hasPermission(ICObject obj, IWApplicationContext iwac, String permissionKey, List<?> groups)
			throws SQLException {
		String permissionMapKey = PERMISSION_MAP_OBJECT;
		String identifier=MINUS_ONE;
		Object primaryKey = obj.getId();
		if(primaryKey!=null){
			identifier=primaryKey.toString();
		}
		if (identifier != null) {
			PermissionMap permissionMap = (PermissionMap) iwac.getApplicationAttribute(
					permissionMapKey);
			if (permissionMap == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
				permissionMap = (PermissionMap) iwac.getApplicationAttribute(permissionMapKey);
			}
			List<?> permissions = permissionMap.get(identifier, permissionKey, groups);
			if (permissions == null) {
				updatePermissions(permissionMapKey, identifier, permissionKey, iwac);
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
	public void updateObjectInstancePermissions(String instanceId, String permissionKey, IWApplicationContext iwac)
			throws SQLException {
		updatePermissions(PERMISSION_MAP_OBJECT_INSTANCE, instanceId, permissionKey, iwac);
	}

	public void updateObjectPermissions(String objectId, String permissionKey, IWApplicationContext iwac) throws SQLException {
		updatePermissions(PERMISSION_MAP_OBJECT, objectId, permissionKey, iwac);
	}

	public void updateBundlePermissions(String bundleIdentifier, String permissionKey, IWApplicationContext iwac)
			throws SQLException {
		updatePermissions(PERMISSION_MAP_BUNDLE, bundleIdentifier, permissionKey, iwac);
	}

	public void updatePagePermissions(String pageId, String permissionKey, IWApplicationContext iwac) throws SQLException {
		updatePermissions(PERMISSION_MAP_PAGE_INSTANCE, pageId, permissionKey, iwac);
	}

	public void updateJSPPagePermissions(String jspPageId, String permissionKey, IWApplicationContext iwac) throws SQLException {
		updatePermissions(PERMISSION_MAP_JSP_PAGE, jspPageId, permissionKey, iwac);
	}

	public void updateFilePermissions(String fileId, String permissionKey, IWApplicationContext iwac) throws SQLException {
		updatePermissions(PERMISSION_MAP_FILE, fileId, permissionKey, iwac);
	}

	public void updatePermissions(int permissionCategory, String identifier, String permissionKey, IWApplicationContext iwac)
			throws SQLException {
		switch (permissionCategory) {
			case AccessController.CATEGORY_OBJECT_INSTANCE:
				updatePermissions(PERMISSION_MAP_OBJECT_INSTANCE, identifier, permissionKey, iwac);
				break;
			case AccessController.CATEGORY_PAGE:
			case AccessController.CATEGORY_OBJECT:
				updatePermissions(PERMISSION_MAP_OBJECT, identifier, permissionKey, iwac);
				break;
			case AccessController.CATEGORY_BUNDLE:
				updatePermissions(PERMISSION_MAP_BUNDLE, identifier, permissionKey, iwac);
				break;
			case AccessController.CATEGORY_PAGE_INSTANCE:
				updatePermissions(PERMISSION_MAP_PAGE_INSTANCE, identifier, permissionKey, iwac);
				break;
			case AccessController.CATEGORY_GROUP_ID:
				updatePermissions(PERMISSION_MAP_GROUP, identifier, permissionKey, iwac);
				break;
			case AccessController.CATEGORY_ROLE:
				updatePermissions(PERMISSION_MAP_ROLE, identifier, permissionKey, iwac);
				break;
		}
	}

	private void updatePermissions(String permissionMapKey, String identifier, String permissionKey, IWApplicationContext iwac) throws SQLException {
		PermissionMap permissionMap = (PermissionMap) iwac.getApplicationAttribute(permissionMapKey);
		if (permissionMap == null) {
			permissionMap = new PermissionMap();
			iwac.setApplicationAttribute(permissionMapKey, permissionMap);
		}
		List<ICPermission> permissions = null;
		if (identifier != null) {
			String contextType = null;
			switch (permissionMapKey) {
			case PERMISSION_MAP_OBJECT_INSTANCE:
				contextType = AccessController.CATEGORY_STRING_OBJECT_INSTANCE_ID;
				break;

			case PERMISSION_MAP_OBJECT:
				contextType = AccessController.CATEGORY_STRING_IC_OBJECT_ID;
				break;

			case PERMISSION_MAP_BUNDLE:
				contextType = AccessController.CATEGORY_STRING_BUNDLE_IDENTIFIER;
				break;

			case PERMISSION_MAP_PAGE_INSTANCE:
				contextType = AccessController.CATEGORY_STRING_PAGE_ID;
				break;

			case PERMISSION_MAP_JSP_PAGE:
				contextType = AccessController.CATEGORY_STRING_JSP_PAGE;
				break;

			case PERMISSION_MAP_FILE:
				contextType = AccessController.CATEGORY_STRING_FILE_ID;
				break;

			case PERMISSION_MAP_GROUP:
				contextType = AccessController.CATEGORY_STRING_GROUP_ID;
				break;

			case PERMISSION_MAP_ROLE:
				contextType = identifier;
				break;

			default:
				break;
			}

			permissions = getPermissionDAO().findByContextTypeAndContextValueAndPermissionString(contextType, identifier, permissionKey);
		}

		if (!ListUtil.isEmpty(permissions)) {
			Iterator<ICPermission> iter = permissions.iterator();
			Map<String, Boolean> mapToPutTo = new Hashtable<>();
			while (iter.hasNext()) {
				ICPermission item = iter.next();
				Group group = item.getPermissionGroup();
				String id = group == null ? MINUS_ONE : group.getID().toString();

				mapToPutTo.put(
						id,
						item.getPermissionValue() ? Boolean.TRUE : Boolean.FALSE);
			}
			// THIS IS DONE SO YOU ALWAYS HAVE VIEW PERMISSION IF NO PERMISSION
			// IS DEFINED
			mapToPutTo.put(this._SOME_VIEW_PERMISSION_SET, Boolean.TRUE);
			permissionMap.put(identifier, permissionKey, mapToPutTo);
		}
		else {
			Map<String, Boolean> mapToPutTo = new Hashtable<>();
			mapToPutTo.put(this._SOME_VIEW_PERMISSION_SET, Boolean.FALSE);
			permissionMap.put(identifier, permissionKey, mapToPutTo);
		}
	}

}