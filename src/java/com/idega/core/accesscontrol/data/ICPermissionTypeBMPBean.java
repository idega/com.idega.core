package com.idega.core.accesscontrol.data;
import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.TreeableEntity;
import com.idega.data.TreeableEntityBMPBean;

/**
 * Title:        ICPermissionTypeBMPBean
 * Description: A treeable list of roles or permission types
 * Copyright:   Copyright (c) 2001-2003 idega.is All Rights Reserved
 * Company: Idega Software
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class ICPermissionTypeBMPBean extends TreeableEntityBMPBean implements TreeableEntity {

	private static final String ENTITY_NAME = "IC_PERMISSION_TYPE";
	
	private static final String PERMISSION_TYPE_COLUMN = "PERMISSION_TYPE";//defines the type of permission e.g. ic_object_id,group_id,role
	
	private static final String DESCRIPTION_LOCAL_KEY_COLUMN ="DESC_LOC_KEY";
	private static final String NAME_LOCAL_KEY_COLUMN ="NAME_LOC_KEY";
	
	
	public ICPermissionTypeBMPBean() {
		super();
	}
	public ICPermissionTypeBMPBean(int id) throws SQLException {
		super(id);
	}
	
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getDescriptionLocalizableKeyColumnName(), "a localizable key for a description", true, true, "java.lang.String");
		addAttribute(getNameLocalizableKeyColumnName(), "a localizable key for a name", true, true, "java.lang.String");
		
		
		setAsPrimaryKey(getIDColumnName(),true);//needed?
 	
	}
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
	public String getIDColumnName(){
		return getPermissionTypeColumnName();
	}
		
	public static String getPermissionTypeColumnName() {
		return PERMISSION_TYPE_COLUMN;
	}
	
	public static String getDescriptionLocalizableKeyColumnName() {
		return DESCRIPTION_LOCAL_KEY_COLUMN;
	}
	
	public static String getNameLocalizableKeyColumnName() {
		return NAME_LOCAL_KEY_COLUMN;
	}
		
	public Collection ejbFindAllPermissionsTypes() throws FinderException{
		return super.idoFindAllIDsBySQL();
	}
	
	
} // Class ends
