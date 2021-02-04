package com.idega.core.accesscontrol.data;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOQuery;
import com.idega.data.IDOUtil;
import com.idega.data.query.AND;
import com.idega.data.query.Criteria;
import com.idega.data.query.InCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;

/**
 * Title:        ICPermissionBMPBean
 * Description:
 * Copyright:   Copyright (c) 2001-2003 idega.is All Rights Reserved
 * Company: Idega Software
 * @author <a href="mailto:idega@idega.is">idega team</a>,<a
 * href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.1
 */

public class ICPermissionBMPBean extends com.idega.data.GenericEntity implements com.idega.core.accesscontrol.data.ICPermission {

	private static final String ENTITY_NAME = "IC_PERMISSION";
	private static final String CONTEXT_TYPE_COLUMN = "PERMISSION_CONTEXT_TYPE"; //group permissions, ibobject etc.
	private static final String CONTEXT_VALUE_COLUMN = "PERMISSION_CONTEXT_VALUE";//group id, object id etc. the permission key referes to.
	private static final String PERMISSION_STRING_COLUMN = "PERMISSION_STRING";//view,edit,delete etc. permission keys.
	private static final String PERMISSION_VALUE_COLUMN = "PERMISSION_VALUE";//True or false value for the permission
	private static final String GROUP_ID_COLUMN = "GROUP_ID";//the group that owns/has this permission

	private static final String INITIATION_DATE_COLUMN="INITIATION_DATE";//creation date
	private static final String TERMINATION_DATE_COLUMN="TERMINATION_DATE";//end date
	private static final String SET_PASSIVE_BY_COLUMN="SET_PASSIVE_BY";//who ended it
	private static final String STATUS_COLUMN="STATUS";//is the permission active or inactive (kept for historical purposes)

	private final static String INHERIT_TO_CHILDREN_COLUMN="INHERIT";//when creating new groups under the "context_value" (a groups primary key) should this permission be inherited?

	private final static String STATUS_ACTIVE="ST_ACTIVE";
	private final static String STATUS_PASSIVE="ST_PASSIVE";


	public ICPermissionBMPBean() {
		super();
	}
	public ICPermissionBMPBean(int id) throws SQLException {
		super(id);
	}
	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getContextTypeColumnName(), "Context type", true, true, "java.lang.String");// ic_object_id , group_id, role_permission
		addAttribute(getContextValueColumnName(), "Context value", true, true, "java.lang.String", 30);
		addAttribute(getPermissionStringColumnName(), "Permission string", true, true, "java.lang.String");
		addAttribute(getPermissionValueColumnName(), "Permission value", true, true, "java.lang.Boolean");
		addAttribute(getGroupIDColumnName(), "GroupID", true, true, Integer.class, "many-to-one", PermissionGroup.class);

		addAttribute(getInheritToChildrenColumnName(), "Inherit to children", true, true, "java.lang.Boolean");
		addAttribute(STATUS_COLUMN,"Status",String.class,10);
		addAttribute(INITIATION_DATE_COLUMN,"Initiation Date",Timestamp.class);
	 	addAttribute(TERMINATION_DATE_COLUMN,"Termination Date",Timestamp.class);
	 	addAttribute(SET_PASSIVE_BY_COLUMN, "Passivated by", true, true, Integer.class, MANY_TO_ONE, User.class);

	 	addIndex("IDX_IC_PERM_1", new String[]{GROUP_ID_COLUMN, PERMISSION_STRING_COLUMN, CONTEXT_TYPE_COLUMN, STATUS_COLUMN});
	 	addIndex("IDX_IC_PERM_2", CONTEXT_VALUE_COLUMN);
	 	addIndex("IDX_IC_PERM_3", new String[]{CONTEXT_TYPE_COLUMN, STATUS_COLUMN, CONTEXT_VALUE_COLUMN});
	 	addIndex("IDX_IC_PERM_4", new String[]{PERMISSION_STRING_COLUMN, CONTEXT_VALUE_COLUMN});
	 	getEntityDefinition().setBeanCachingActiveByDefault(true, 1000);
	}

    private String getInheritToChildrenColumnName() {
        return INHERIT_TO_CHILDREN_COLUMN;
    }
    @Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public static String getContextTypeColumnName() {
		return CONTEXT_TYPE_COLUMN;
	}
	@Override
	public String getContextType() {
		return getStringColumnValue(getContextTypeColumnName());
	}
	@Override
	public void setContextType(String ContextType) {
		setColumn(getContextTypeColumnName(), ContextType);
	}
	public static String getContextValueColumnName() {
		return CONTEXT_VALUE_COLUMN;
	}
	@Override
	public String getContextValue() {
		return getStringColumnValue(getContextValueColumnName());
	}
	@Override
	public void setContextValue(String ContextValue) {
		setColumn(getContextValueColumnName(), ContextValue);
	}
	public static String getPermissionStringColumnName() {
		return PERMISSION_STRING_COLUMN;
	}
	@Override
	public String getPermissionString() {
		return getStringColumnValue(getPermissionStringColumnName());
	}
	@Override
	public void setPermissionString(String PermissionString) {
		setColumn(getPermissionStringColumnName(), PermissionString);
	}

	public static String getPermissionValueColumnName() {
		return PERMISSION_VALUE_COLUMN;
	}
	@Override
	public boolean getPermissionValue() {
		return getBooleanColumnValue(getPermissionValueColumnName());
	}
	@Override
	public void setPermissionValue(Boolean PermissionStringValue) {
		setColumn(getPermissionValueColumnName(), PermissionStringValue);
	}
	@Override
	public void setPermissionValue(boolean PermissionStringValue) {
		setColumn(getPermissionValueColumnName(), PermissionStringValue);
	}
	public static String getGroupIDColumnName() {
		return GROUP_ID_COLUMN;
	}
	@Override
	public int getGroupID() {
		return getIntColumnValue(getGroupIDColumnName());
	}
	@Override
	public void setGroupID(Integer GroupID) {
		setColumn(getGroupIDColumnName(), GroupID);
	}
	@Override
	public void setGroupID(int GroupID) {
		setColumn(getGroupIDColumnName(), GroupID);
	}
	public static ICPermission getStaticInstance() {
		return (ICPermission) getStaticInstance(ICPermission.class);
	}

	@Override
	public void setActive(){
		this.setStatus(STATUS_ACTIVE);
	}

	@Override
	public void setPassive(){
		this.setStatus(STATUS_PASSIVE);
	}

	@Override
	public void setToInheritToChildren(){
		this.setColumn(INHERIT_TO_CHILDREN_COLUMN,true);
	}

	@Override
	public void setToNOTInheritToChildren(){
	    this.setColumn(INHERIT_TO_CHILDREN_COLUMN,false);
	}

	@Override
	public void setInitiationDate(Timestamp stamp){
		this.setColumn(ICPermissionBMPBean.INITIATION_DATE_COLUMN,stamp);
	}

	@Override
	public boolean doesInheritToChildren() {
	    return getBooleanColumnValue(INHERIT_TO_CHILDREN_COLUMN,false);
	}

	@Override
	public Timestamp getInitiationDate(){
		return (Timestamp)getColumnValue(ICPermissionBMPBean.INITIATION_DATE_COLUMN);
	}

	public void setTerminationDate(Timestamp stamp){
		this.setColumn(ICPermissionBMPBean.TERMINATION_DATE_COLUMN,stamp);
	}

	@Override
	public Timestamp getTerminationDate(){
		return (Timestamp)getColumnValue(ICPermissionBMPBean.TERMINATION_DATE_COLUMN);
	}

	@Override
	public void setPassiveBy(int userId)  {
		setColumn(SET_PASSIVE_BY_COLUMN, userId);
	}

	@Override
	public int getPassiveBy() {
		return getIntColumnValue(SET_PASSIVE_BY_COLUMN);
	}

	public void setStatus(String status){
		setColumn(ICPermissionBMPBean.STATUS_COLUMN,status);
	}

	@Override
	public String getStatus(){
		return getStringColumnValue(ICPermissionBMPBean.STATUS_COLUMN);
	}

	public Collection ejbFindAllPermissionsByContextTypeAndContextValue(String contextType, String contextValue) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");

		return super.idoFindPKsByQuery(sql);
	}


	public Collection ejbFindAllPermissionsByContextTypeAndContextValueAndPermissionString(String contextType, String contextValue, String permissionString) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAnd().appendEqualsQuoted(getPermissionStringColumnName(),permissionString)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");

		return super.idoFindPKsByQuery(sql);
	}

	public Collection ejbFindAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup(String contextType, String contextValue, Collection permissionStrings, Group group) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this)
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAnd().append(getPermissionStringColumnName())
		.appendInForStringCollectionWithSingleQuotes(permissionStrings)
		.appendAnd().appendEqualsQuoted(getPermissionValueColumnName(),CoreConstants.Y);
		if (group != null) {
			sql.appendAnd().appendEquals(getGroupIDColumnName(),group.getPrimaryKey().toString());
		}
		sql.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");

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
	 * Gets all the permission records for this collection of groups for a certain type
	 * (information on which objects it has permissions to).
	 * @param groups a collection of group that the permissions belong to.
	 * @param contextType the context type of the permissions.
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindAllPermissionsByContextTypeAndPermissionGroupCollectionOrderedByContextValue(String contextType, Collection groups) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelect().append(getIDColumnName()).appendFrom(this.getEntityName())
		.appendWhereEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().append(getGroupIDColumnName()).appendInCollection(groups)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )")
		.appendOrderBy(getContextValueColumnName());

		return super.idoFindPKsByQuery(sql);
	}


	/**
	 * Finds all permissions of a certain type for the contexttype and group
	 * specifide.
	 * @param group The group that ownes the records
	 * @param permissionStrings Collection of certain type of permission such as "owner"
	 * @param contextType What type of object the permission is for such a
	 * ic_group_id
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(Group group,Collection permissionStrings, String contextType) throws FinderException{
		if(permissionStrings==null || permissionStrings.isEmpty()){
			return ListUtil.getEmptyList();
		}

		SelectQuery query = idoSelectQuery();
		Criteria theCriteria = new MatchCriteria(idoQueryTable(),getGroupIDColumnName(),MatchCriteria.EQUALS,group);

		Criteria permCr = new InCriteria(idoQueryTable(),getPermissionStringColumnName(),permissionStrings);
		Criteria contextCr = new MatchCriteria(idoQueryTable(),getContextTypeColumnName(),MatchCriteria.EQUALS,contextType,true);
		Criteria statusCr = new MatchCriteria(idoQueryTable(),STATUS_COLUMN,MatchCriteria.EQUALS,STATUS_ACTIVE,true);
		statusCr = new OR(statusCr,new MatchCriteria(idoQueryTable(),STATUS_COLUMN,MatchCriteria.IS,MatchCriteria.NULL));

		theCriteria = new AND(theCriteria,permCr);
		theCriteria = new AND(theCriteria,contextCr);
		theCriteria = new AND(theCriteria,statusCr);

		query.addCriteria(theCriteria);
		query.addOrder(idoQueryTable(),getContextValueColumnName(),true);

		//return super.idoFindPKsByQuery(sql);
		return super.idoFindPKsByQueryUsingLoadBalance(query, 10000);
	}


	/**
	 * Finds all permissions of a certain type for the contexttype and the collection of groups
	 * specifide.
	 * @param group The group that ownes the records
	 * @param permissionString Collection of certain type of permission such as "owner"
	 * @param contextType What type of object the permission is for such a
	 * ic_group_id
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection groups,Collection permissionStrings, String contextType) throws FinderException{
		if(groups==null || groups.isEmpty() || permissionStrings==null || permissionStrings.isEmpty()){
			return ListUtil.getEmptyList();
		}


		SelectQuery query = idoSelectQuery();
		Criteria theCriteria = new InCriteria(idoQueryTable(),getGroupIDColumnName(),groups);

		Criteria permCr = new InCriteria(idoQueryTable(),getPermissionStringColumnName(),permissionStrings);
		Criteria contextCr = new MatchCriteria(idoQueryTable(),getContextTypeColumnName(),MatchCriteria.EQUALS,contextType,true);
		Criteria statusCr = new MatchCriteria(idoQueryTable(),STATUS_COLUMN,MatchCriteria.EQUALS,STATUS_ACTIVE,true);
		statusCr = new OR(statusCr,new MatchCriteria(idoQueryTable(),STATUS_COLUMN,MatchCriteria.IS,MatchCriteria.NULL));

		theCriteria = new AND(theCriteria,permCr);
		theCriteria = new AND(theCriteria,contextCr);
		theCriteria = new AND(theCriteria,statusCr);

		query.addCriteria(theCriteria);
		query.addOrder(idoQueryTable(),getContextValueColumnName(),true);

		//return super.idoFindPKsByQuery(sql);
		return super.idoFindPKsByQueryUsingLoadBalance(query, 10000);
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
		ArrayList l = new ArrayList();
		l.add(permissionString);
		return ejbFindAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(group, l, contextType);
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
		ArrayList l = new ArrayList();
		l.add(permissionString);
		return ejbFindAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(groups, l, contextType);
	}



	/**
	 * Finds all permission marked for inheritance. The collection is filled with groups that someone HAS permission too.
	 * This method is mainly used to get those permission to find out which groups own those permissions.
	 * @param groups A collection of groups that will be used as ContextValues
	 * @return Collection of ICPermissions
	 * @throws FinderException
	 */
	public Collection ejbFindAllGroupPermissionsToInheritByGroupCollection(Collection groups) throws FinderException{
		IDOQuery sql = idoQuery();
		IDOUtil util = IDOUtil.getInstance();
		sql.appendSelectAllFrom(this).appendWhere()
		.append(getContextValueColumnName())
		.appendIn(util.convertListToCommaseparatedString(groups,true))
		.appendAnd().appendEqualsQuoted(getPermissionValueColumnName(),CoreConstants.Y)
		.appendAnd().appendEqualsQuoted(getContextTypeColumnName(),"ic_group_id")
		.appendAnd().appendEqualsQuoted(getInheritToChildrenColumnName(),CoreConstants.Y)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");

		return super.idoFindPKsByQuery(sql);
	}


	/**
	 * Finds a single permissions of a certain type (group or something else),key and group
	 *
	 * @param group The group that ownes the records
	 * @param permissionString A certain type of permission such as "owner"
	 * @param contextType What type of object the permission is for, such as ic_group_id
	 * @param contextValue for groups this would be the group id of the group the permission refers to
	 *
	 * @return primary key of the ICPermission found
	 * @throws FinderException
	 */
	public Integer ejbFindPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue(Group group,String permissionString, String contextType, String contextValue) throws FinderException{
		IDOQuery sql = idoQuery();
		sql.appendSelectAllFrom(this).appendWhereEquals(getGroupIDColumnName(),group.getPrimaryKey().toString())
		.appendAnd().appendEqualsQuoted(getPermissionStringColumnName(),permissionString)
		.appendAnd().appendEqualsQuoted(getContextTypeColumnName(),contextType)
		.appendAnd().appendEqualsQuoted(getContextValueColumnName(),contextValue)
		.appendAnd().append(" ( "+STATUS_COLUMN+" = '"+STATUS_ACTIVE+"' OR "+STATUS_COLUMN+" is null )");

		return (Integer)super.idoFindOnePKByQuery(sql);
	}

	@Override
	public void removeBy(User currentUser){
		int userId = ((Integer) currentUser.getPrimaryKey()).intValue();
		this.setPassive();
		this.setTerminationDate(IWTimestamp.getTimestampRightNow());
		setPassiveBy(userId);
		store();
	}

	@Override
	public void setDefaultValues(){
		this.setInitiationDate(IWTimestamp.getTimestampRightNow());
		this.setStatus(STATUS_ACTIVE);
	}

	@Override
	public boolean isActive(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_ACTIVE)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isPassive(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_PASSIVE)){
			return true;
		}
		return false;
	}

	public Collection ejbFindByTypeAndValueAndKey(String type,String value,String key)throws FinderException{
	    Table table = new Table(this);
	    SelectQuery query = new SelectQuery(table);
	    query.addColumn(new WildCardColumn());
	    if(type!=null) {
				query.addCriteria(new MatchCriteria(table,getContextTypeColumnName(),MatchCriteria.EQUALS,type,true));
			}
	    if(value!=null) {
				query.addCriteria(new MatchCriteria(table,getContextValueColumnName(),MatchCriteria.EQUALS,type,true));
			}
	    if(key!=null) {
				query.addCriteria(new MatchCriteria(table,getPermissionStringColumnName(),MatchCriteria.EQUALS,type,true));
			}
	    return idoFindPKsByQuery(query);

	}

} // Class ends
