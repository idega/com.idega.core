package com.idega.core.accesscontrol.data;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOQuery;
import com.idega.data.IDOUtil;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

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

	private static final String ENTITY_NAME = "IC_PERMISSION";
	
	private static final String PERMISSION_TYPE_COLUMN = "PERMISSION_TYPE";//defines the type of permission e.g. ic_object_id,group_id,role
	private static final String CONTEXT_TYPE_COLUMN = "PERMISSION_CONTEXT_TYPE"; //just backward compatability, use permission type
	private static final String CONTEXT_VALUE_COLUMN = "PERMISSION_CONTEXT_VALUE";
	private static final String PERMISSION_STRING_COLUMN = "PERMISSION_STRING";
	private static final String PERMISSION_VALUE_COLUMN = "PERMISSION_VALUE";
	private static final String GROUP_ID_COLUMN = "GROUP_ID";
	
//private static final String PERMISSION_STRING_VALUE = "permission_string_value"; removed because never used! eiki
	
	
	private static final String INITIATION_DATE_COLUMN="INITIATION_DATE";
	private static final String TERMINATION_DATE_COLUMN="TERMINATION_DATE";
	private static final String SET_PASSIVE_BY_COLUMN="SET_PASSIVE_BY";
	private static final String STATUS_COLUMN="STATUS";
	
	private final static String STATUS_ACTIVE="ST_ACTIVE";
	private final static String STATUS_PASSIVE="ST_PASSIVE";
	
	
	
	public ICPermissionBMPBean() {
		super();
	}
	public ICPermissionBMPBean(int id) throws SQLException {
		super(id);
	}
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getContextTypeColumnName(), "Context type", true, true, "java.lang.String");// ic_object_id , group_id, role etc. this just for backward compatability, use permission_type
		addManyToOneRelationship(getPermissionTypeColumnName(),ICPermissionType.class); 
		
		addAttribute(getContextValueColumnName(), "Context value", true, true, "java.lang.String");
		
		
		addAttribute(getPermissionStringColumnName(), "Permission string", true, true, "java.lang.String");
		
		//addAttribute(getPermissionStringValueColumnName(), "Permission string value", true, true, "java.lang.String");
		
		addAttribute(getPermissionValueColumnName(), "Permission value", true, true, "java.lang.Boolean");
		addAttribute(getGroupIDColumnName(), "GroupID", true, true, Integer.class, "many-to-one", PermissionGroup.class);
		
		addAttribute(STATUS_COLUMN,"Status",String.class,10);
		addAttribute(INITIATION_DATE_COLUMN,"Initiation Date",Timestamp.class);
	 	addAttribute(TERMINATION_DATE_COLUMN,"Termination Date",Timestamp.class);
	 	addAttribute(SET_PASSIVE_BY_COLUMN, "Passivated by", true, true, Integer.class, MANY_TO_ONE, User.class);
 	
	}
	public String getEntityName() {
		return ENTITY_NAME;
	}
	/*
	
	  public void setDefaultValues(){
	
	    this.setPermissionStringValue("");
	
	  }
	
	*/
	
	public static String getPermissionTypeColumnName() {
		return PERMISSION_TYPE_COLUMN;
	}
	
	public static String getContextTypeColumnName() {
		return CONTEXT_TYPE_COLUMN;
	}
	public String getContextType() {
		//TODO depricate
		String type = getStringColumnValue(getContextTypeColumnName());
		if(type==null){
			type = getStringColumnValue(getPermissionTypeColumnName());//backward compatability
		}
		
		return type;
	}
	public void setContextType(String ContextType) {
		setColumn(getContextTypeColumnName(), ContextType);
	}
	public static String getContextValueColumnName() {
		return CONTEXT_VALUE_COLUMN;
	}
	public String getContextValue() {
		return getStringColumnValue(getContextValueColumnName());
	}
	public void setContextValue(String ContextValue) {
		setColumn(getContextValueColumnName(), ContextValue);
	}
	public static String getPermissionStringColumnName() {
		return PERMISSION_STRING_COLUMN;
	}
	public String getPermissionString() {
		return getStringColumnValue(getPermissionStringColumnName());
	}
	public void setPermissionString(String PermissionString) {
		setColumn(getPermissionStringColumnName(), PermissionString);
	}
	
	public String getPermissionType() {
		return getStringColumnValue(getPermissionTypeColumnName());
	}
	public void setPermissionType(String type) {
		setColumn(getPermissionTypeColumnName(), type);
		setColumn(getContextTypeColumnName(),type);
	}
	
	/*
	public static String getPermissionStringValueColumnName() {
		return PERMISSION_STRING_VALUE;
	}
	
	public String getPermissionStringValue() {
		return getStringColumnValue(getPermissionStringValueColumnName());
	}
	public void setPermissionStringValue(String PermissionStringValue) {
		setColumn(getPermissionStringValueColumnName(), PermissionStringValue);
	}
	*/
	
	
	public static String getPermissionValueColumnName() {
		return PERMISSION_VALUE_COLUMN;
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
		return GROUP_ID_COLUMN;
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
	
	public void setActive(){
		this.setStatus(STATUS_ACTIVE);
	}

	public void setPassive(){
		this.setStatus(STATUS_PASSIVE);
	}

	public void setInitiationDate(Timestamp stamp){
		this.setColumn(this.INITIATION_DATE_COLUMN,stamp);
	}

	public Timestamp getInitiationDate(){
		return (Timestamp)getColumnValue(this.INITIATION_DATE_COLUMN);
	}

	public void setTerminationDate(Timestamp stamp){
		this.setColumn(this.TERMINATION_DATE_COLUMN,stamp);
	}

	public Timestamp getTerminationDate(){
		return (Timestamp)getColumnValue(this.TERMINATION_DATE_COLUMN);
	}

	public void setPassiveBy(int userId)  {
		setColumn(SET_PASSIVE_BY_COLUMN, userId);
	}

	public int getPassiveBy() { 
		return getIntColumnValue(SET_PASSIVE_BY_COLUMN);
	}
	
	public void setStatus(String status){
		setColumn(this.STATUS_COLUMN,status);
	}

	public String getStatus(){
		return getStringColumnValue(this.STATUS_COLUMN);
	}
	

	//TODO Eiki add finders for permissiontype
	public Collection ejbFindAllPermissionsByTypeAndContextValue(String contextType, String contextValue) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");
		
		return super.idoFindPKsByQuery(sql);
	}
	
	
	public Collection ejbFindAllPermissionsByTypeAndContextValueAndPermissionString(String contextType, String contextValue, String permissionString) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAnd().appendEqualsQuoted(getPermissionStringColumnName(),permissionString)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");
		
		return super.idoFindPKsByQuery(sql);
	}
	
	public Collection ejbFindAllPermissionsByContextTypeAndContextValueAndPermissionGroupOrdered(String contextType, String contextValue, Group permissionGroup) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAndEquals(getGroupIDColumnName(),permissionGroup.getPrimaryKey().toString())
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");
		
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
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )")
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
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )")
		.appendOrderBy(getContextValueColumnName());
		
		return super.idoFindPKsByQuery(sql);
	}
	
	
	/**
	 * Finds all permissions of a certain type for the contexttype and the collection of groups
	 * specifide.
	 * @param group The group that ownes the records
	 * @param permissionString A certain type of permission such as "owner"
	 * @param contextType What type of object the permission is for such a
	 * ic_group_id
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection groups,String permissionString, String contextType) throws FinderException{
		IDOQuery sql = idoQuery();
		IDOUtil util = IDOUtil.getInstance();
		sql.appendSelectAllFrom(this).appendWhere()
		.append(getGroupIDColumnName())
		.appendIn(util.convertListToCommaseparatedString(groups))
		.appendAnd().appendEqualsQuoted(getPermissionStringColumnName(),permissionString)
		.appendAnd().appendEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )")
		.appendOrderBy(getContextValueColumnName());
		
		return super.idoFindPKsByQuery(sql);
	}

	public void removeBy(User currentUser){
		int userId = ((Integer) currentUser.getPrimaryKey()).intValue(); 
		this.setPassive();
		this.setTerminationDate(IWTimestamp.getTimestampRightNow());
		setPassiveBy(userId);
		store();
	}
	
	public void setDefaultValues(){
		this.setInitiationDate(IWTimestamp.getTimestampRightNow());
		this.setStatus(STATUS_ACTIVE);
	}
	
	public boolean isActive(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_ACTIVE)){
			return true;
		}
		return false;
	}

	public boolean isPassive(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_PASSIVE)){
			return true;
		}
		return false;
	}
	
} // Class ends
