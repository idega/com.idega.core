package com.idega.core.accesscontrol.data;
import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOQuery;
import com.idega.user.data.Group;

/**
 * Title:        AccessControl
 * Description:
 * Copyright:   Copyright (c) 2001-2003 idega.is All Rights Reserved
 * Company: Idega Software
 * @author <a href="mailto:idega@idega.is">idega team</a>,<a
 * href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.1
 */

public class ICPermissionBMPBean extends com.idega.data.GenericEntity implements com.idega.core.accesscontrol.data.ICPermission {
	private static String sClassName = ICPermission.class.getName();
	public ICPermissionBMPBean() {
		super();
	}
	public ICPermissionBMPBean(int id) throws SQLException {
		super(id);
	}
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getContextTypeColumnName(), "Context type", true, true, "java.lang.String");
		addAttribute(getContextValueColumnName(), "Context value", true, true, "java.lang.String");
		addAttribute(getPermissionStringColumnName(), "Permission string", true, true, "java.lang.String");
		addAttribute(getPermissionStringValueColumnName(), "Permission string value", true, true, "java.lang.String");
		addAttribute(getPermissionValueColumnName(), "Permission value", true, true, "java.lang.Boolean");
		addAttribute(getGroupIDColumnName(), "GroupID", true, true, Integer.class, "many-to-one", PermissionGroup.class);
	}
	public String getEntityName() {
		return "ic_permission";
	}
	/*
	
	  public void setDefaultValues(){
	
	    this.setPermissionStringValue("");
	
	  }
	
	*/
	public static String getContextTypeColumnName() {
		return "permission_context_type";
	}
	public String getContextType() {
		return getStringColumnValue(getContextTypeColumnName());
	}
	public void setContextType(String ContextType) {
		setColumn(getContextTypeColumnName(), ContextType);
	}
	public static String getContextValueColumnName() {
		return "permission_context_value";
	}
	public String getContextValue() {
		return getStringColumnValue(getContextValueColumnName());
	}
	public void setContextValue(String ContextValue) {
		setColumn(getContextValueColumnName(), ContextValue);
	}
	public static String getPermissionStringColumnName() {
		return "permission_string";
	}
	public String getPermissionString() {
		return getStringColumnValue(getPermissionStringColumnName());
	}
	public void setPermissionString(String PermissionString) {
		setColumn(getPermissionStringColumnName(), PermissionString);
	}
	public static String getPermissionStringValueColumnName() {
		return "permission_string_value";
	}
	public String getPermissionStringValue() {
		return getStringColumnValue(getPermissionStringValueColumnName());
	}
	public void setPermissionStringValue(String PermissionStringValue) {
		setColumn(getPermissionStringValueColumnName(), PermissionStringValue);
	}
	public static String getPermissionValueColumnName() {
		return "permission_value";
	}
	public boolean getPermissionValue() {
		return getBooleanColumnValue(getPermissionValueColumnName());
	}
	public void setPermissionValue(Boolean PermissionStringValue) {
		setColumn(getPermissionValueColumnName(), PermissionStringValue);
	}
	public void setPermissionValue(boolean PermissionStringValue) {
		setColumn(getPermissionValueColumnName(), PermissionStringValue);
	}
	public static String getGroupIDColumnName() {
		return "group_id";
	}
	public int getGroupID() {
		return getIntColumnValue(getGroupIDColumnName());
	}
	public void setGroupID(Integer GroupID) {
		setColumn(getGroupIDColumnName(), GroupID);
	}
	public void setGroupID(int GroupID) {
		setColumn(getGroupIDColumnName(), GroupID);
	}
	public static ICPermission getStaticInstance() {
		return (ICPermission) getStaticInstance(ICPermission.class);
	}
	

	
	public Collection ejbFindAllPermissionsByContextTypeAndContextValue(String contextType, String contextValue) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextType);
		
		return super.idoFindPKsByQuery(sql);
	}
	
	
	public Collection ejbFindAllPermissionsByContextTypeAndContextValueAndPermissionGroupOrdered(String contextType, String contextValue, Group permissionGroup) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAndEquals(getGroupIDColumnName(),permissionGroup.getPrimaryKey().toString());
		
		return super.idoFindPKsByQuery(sql);
	}
	


	/**
	 * Gets all the permission records for this group for a certain type
	 * (information on which objects it has permissions to).
	 * @param permissionGroup the group that the permissions belong to.
	 * @param contextType the context type of the permissions.
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(String contextType, Group permissionGroup) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAndEquals(getGroupIDColumnName(),permissionGroup.getPrimaryKey().toString())
		.appendOrderBy(getContextValueColumnName());
		
		return super.idoFindPKsByQuery(sql);
	}
	
	/**
	 * Finds all permissions of a certain type for the contexttype and group
	 * specifide.
	 * @param group The group that ownes the records
	 * @param permissionString A certain type of permission such as "owner"
	 * @param contextType What type of object the permission is for such a
	 * ic_group_id
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(Group group,String permissionString, String contextType) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this).appendWhereEquals(getGroupIDColumnName(),group.getPrimaryKey().toString())
		.appendAnd().appendEqualsQuoted(getPermissionStringColumnName(),permissionString)
		.appendAnd().appendEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendOrderBy(getContextValueColumnName());
		
		return super.idoFindPKsByQuery(sql);
	}

	
	
} // Class ends
