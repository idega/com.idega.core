package com.idega.core.accesscontrol.data;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.TreeableEntity;
import com.idega.data.TreeableEntityBMPBean;

/**
 * Title: ICRoleBMPBean
 * Description: A treeable list of available roles
 * Copyright:   Copyright (c) 2001-2003 idega.is All Rights Reserved
 * Company: Idega Software
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class ICRoleBMPBean extends TreeableEntityBMPBean implements ICRole,TreeableEntity {

	private static final String ENTITY_NAME = "IC_PERM_ROLE";
	
	private static final String COLUMN_ROLE_KEY = "ROLE_KEY";//defines the type of permission e.g. ic_object_id,group_id,role
	private static final String COLUMN_ROLE_DESCRIPTION_LOCALIZABLE_KEY ="DESC_LOC_KEY";
	private static final String COLUMN_ROLE_NAME_LOCALIZABLE_KEY ="NAME_LOC_KEY";
	
	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityBean#getPrimaryKeyClass()
	 */
	@Override
	public Class getPrimaryKeyClass() {
		return String.class;
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName(),"The roles system name or key", java.lang.String.class,50);
		addAttribute(getDescriptionLocalizableKeyColumnName(), "a localizable key for a description", true, true, java.lang.String.class);
		addAttribute(getNameLocalizableKeyColumnName(), "a localizable key for a name", true, true, java.lang.String.class);
		
		setAsPrimaryKey(getIDColumnName(),true);//needed?
	}
	
	
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
	@Override
	public String getIDColumnName(){
		return getRoleKeyColumnName();
	}
		
	public static String getRoleKeyColumnName() {
		return COLUMN_ROLE_KEY;
	}
	
	public static String getDescriptionLocalizableKeyColumnName() {
		return COLUMN_ROLE_DESCRIPTION_LOCALIZABLE_KEY;
	}
	
	public static String getNameLocalizableKeyColumnName() {
		return COLUMN_ROLE_NAME_LOCALIZABLE_KEY;
	}
		
	public Collection ejbFindAllRoles() throws FinderException{
		return super.idoFindAllIDsBySQL();
	}
	
	public void setRoleKey(String roleKey){
		setColumn(getRoleKeyColumnName(),roleKey);
	}
	
	public void setRoleDescriptionLocalizableKey(String descriptionLocalizableKey){
		setColumn(getDescriptionLocalizableKeyColumnName(),descriptionLocalizableKey);
	}
	
	public void setRoleNameLocalizableKey(String roleNameLocalizableKey){
		setColumn(getNameLocalizableKeyColumnName(),roleNameLocalizableKey);
	}
	
	public String getRoleKey(){
		return getStringColumnValue(getRoleKeyColumnName());
	}
	
	public String getRoleDescriptionLocalizableKey(){
		return getStringColumnValue(getDescriptionLocalizableKeyColumnName());
	}
	
	public String getRoleNameLocalizableKey(){
		return getStringColumnValue(getNameLocalizableKeyColumnName());
	}
} 
