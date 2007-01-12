package com.idega.user.data;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOQuery;
import com.idega.data.TreeableEntity;
import com.idega.data.TreeableEntityBMPBean;

/**
 * <p>
 * Title: idegaWeb
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: idega Software
 * </p>
 * 
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>,Eirikur
 *         Hrafnsson
 * @version 1.1
 */
public class GroupTypeBMPBean extends TreeableEntityBMPBean implements GroupType, TreeableEntity {

	private static final String TABLE_NAME = "IC_GROUP_TYPE";
	private static final String TYPE_COLUMN = "GROUP_TYPE";
	private static final String DESCRIPTION_COLUMN = "DESCRIPTION";
	private static final String DEFAULT_GROUP_NAME_COLUMN = "DEFAULT_GROUP_NAME";
	private static final String COLUMN_HANDLER_CLASS = "HANDLER_CLASS_ID";
	private static final String COLUMN_IS_VISIBLE = "IS_VISIBLE";
	private static final String COLUMN_MAX_INSTANCES = "MAX_INSTANCES";
	private static final String COLUMN_MAX_INSTANCES_PER_PARENT = "MAX_INSTANCES_PER_PARENT";
	private static final String COLUMN_AUTO_CREATE = "AUTO_CREATE";
	private static final String COLUMN_NUMBER_OF_INSTANCES_TO_AUTO_CREATE = "INSTANCES_AUTO_CREATED";
	private static final String COLUMN_SUPPORTS_SAME_CHILD_TYPE = "SAME_CHILD_TYPE";
	private static final String COLUMN_SAME_CHILD_TYPE_ONLY = "SAME_CHILD_TYPE_ONLY";
	private static final String TYPE_GENERAL_GROUP = "general";
	
	public static final String TYPE_USER_REPRESENTATIVE = "ic_user_representative";
	public static final String TYPE_PERMISSION_GROUP = "permission";
	public static final String TYPE_ALIAS = "alias";

	public void initializeAttributes() {
		this.addAttribute(getIDColumnName(), "Type", String.class, 30);

		this.setAsPrimaryKey(getIDColumnName(), true);
		this.addAttribute(DESCRIPTION_COLUMN, "Description", String.class, 1000);
		this.addAttribute(DEFAULT_GROUP_NAME_COLUMN, "Default generated group name", String.class);
		this.addAttribute(COLUMN_IS_VISIBLE, "is Visible", Boolean.class);
		this.addAttribute(COLUMN_AUTO_CREATE, "Auto create", Boolean.class);
		this.addAttribute(COLUMN_NUMBER_OF_INSTANCES_TO_AUTO_CREATE, "Number of instances to autocreate", Integer.class);
		this.addAttribute(COLUMN_MAX_INSTANCES, "Maximum number of instances globaly", Integer.class);
		this.addAttribute(COLUMN_MAX_INSTANCES_PER_PARENT, "Maximum number of instances per parent", Integer.class);
		this.addAttribute(COLUMN_SUPPORTS_SAME_CHILD_TYPE, "Does it support its own type as a child group",Boolean.class);
		this.addAttribute(COLUMN_SAME_CHILD_TYPE_ONLY, "Only allows the same group type (for groups) for its children",Boolean.class);
		
		this.addIndex("IDX_IC_GROUP_TYPE1", COLUMN_IS_VISIBLE);
		this.addIndex("IDX_IC_GROUP_TYPE2", TYPE_COLUMN);
		this.addIndex("IDX_IC_GROUP_TYPE3", new String[] { COLUMN_IS_VISIBLE, TYPE_COLUMN });
		
		//cache this table
		getEntityDefinition().setBeanCachingActiveByDefault(true);
	}

	public void setDefaultValues() {
		setVisibility(true);
		setAutoCreate(Boolean.TRUE);
	}

	public void insertStartData() {
		try {
			GroupTypeHome home = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
			GroupType type = home.create();
			type.setType(TYPE_GENERAL_GROUP);
			type.setDescription("");
			type.setVisibility(true);
			type.store();
		}
		catch (RemoteException ex) {
			throw new EJBException(ex);
		}
		catch (CreateException ex) {
			ex.printStackTrace();
		}
		try {
			GroupTypeHome home = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
			GroupType type = home.create();
			type.setType(TYPE_PERMISSION_GROUP);
			type.setDescription("");
			type.setVisibility(true);
			type.store();
		}
		catch (RemoteException ex) {
			throw new EJBException(ex);
		}
		catch (CreateException ex) {
			ex.printStackTrace();
		}
		try {
			GroupTypeHome home = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
			GroupType type = home.create();
			type.setType(TYPE_USER_REPRESENTATIVE);
			type.setDescription("");
			type.setVisibility(false);
			type.store();
		}
		catch (RemoteException ex) {
			throw new EJBException(ex);
		}
		catch (CreateException ex) {
			ex.printStackTrace();
		}
		try {
			GroupTypeHome home = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
			GroupType type = home.create();
			type.setType(TYPE_ALIAS);
			type.setDescription("Alias group, points to another group");
			type.setVisibility(true);
			type.store();
		}
		catch (RemoteException ex) {
			throw new EJBException(ex);
		}
		catch (CreateException ex) {
			ex.printStackTrace();
		}
	}

	public String getEntityName() {
		return TABLE_NAME;
	}

	public void setVisibility(boolean visible) {
		setColumn(COLUMN_IS_VISIBLE, visible);
	}

	public void setType(String type) {
		setColumn(TYPE_COLUMN, type);
	}

	public String getType() {
		return getStringColumnValue(TYPE_COLUMN);
	}

	public void setDescription(String desc) {
		setColumn(DESCRIPTION_COLUMN, desc);
	}

	public String getDescription() {
		return getStringColumnValue(DESCRIPTION_COLUMN);
	}

	public void setDefaultGroupName(String name) {
		setColumn(DEFAULT_GROUP_NAME_COLUMN, name);
	}

	public String getDefaultGroupName() {
		return getStringColumnValue(DEFAULT_GROUP_NAME_COLUMN);
	}

	public Integer getNumberOfInstancesToAutoCreate() {
		return getIntegerColumnValue(COLUMN_NUMBER_OF_INSTANCES_TO_AUTO_CREATE);
	}

	public void setNumberOfInstancesToAutoCreate(Integer number) {
		setColumn(COLUMN_NUMBER_OF_INSTANCES_TO_AUTO_CREATE, number);
	}

	public Integer getMaximumNumberOfInstances() {
		return getIntegerColumnValue(COLUMN_MAX_INSTANCES);
	}

	public void setMaximumNumberOfInstances(Integer max) {
		setColumn(COLUMN_MAX_INSTANCES, max);
	}

	public Integer getMaximumNumberOfInstancesPerParent() {
		return getIntegerColumnValue(COLUMN_MAX_INSTANCES_PER_PARENT);
	}

	public void setMaximumNumberOfInstancesPerParent(Integer max) {
		setColumn(COLUMN_MAX_INSTANCES_PER_PARENT, max);
	}

	public String getIDColumnName() {
		return TYPE_COLUMN;
	}

	public Class getPrimaryKeyClass() {
		return String.class;
	}

	/*
	 * public Class getHandlerClass() throws ClassNotFoundException { ICObject
	 * obj = (ICObject)this.getColumn(COLUMN_HANDLER_CLASS); if(obj != null){
	 * return obj.getObjectClass(); } else { return null; } }
	 * 
	 * 
	 * 
	 * public void setHandlerClass(ICObject obj){
	 * setColumn(COLUMN_HANDLER_CLASS,obj); }
	 */
	protected String getGeneralGroupTypeString() {
		return TYPE_GENERAL_GROUP;
	}

	protected String getPermissionGroupTypeString() {
		return TYPE_PERMISSION_GROUP;
	}

	protected String getAliasGroupTypeString() {
		return TYPE_ALIAS;
	}

	public void setGroupTypeAsGeneralGroup() {
		setType(GroupTypeBMPBean.TYPE_GENERAL_GROUP);
	}

	public void setGroupTypeAsPermissionGroup() {
		setType(GroupTypeBMPBean.TYPE_PERMISSION_GROUP);
	}

	public void setGroupTypeAsAliasGroup() {
		setType(TYPE_ALIAS);
	}

	public boolean getVisibility() {
		return getBooleanColumnValue(COLUMN_IS_VISIBLE, true);
	}

	public boolean getOnlySupportsSameTypeChildGroups() {
		return getBooleanColumnValue(COLUMN_SAME_CHILD_TYPE_ONLY, false);
	}

	public void setOnlySupportsSameTypeChildGroups(Boolean onlySupportsSameTypeChildGroups) {
		setColumn(COLUMN_SAME_CHILD_TYPE_ONLY, onlySupportsSameTypeChildGroups);
	}
	
	public boolean getSupportsSameTypeChildGroups() {
		return getBooleanColumnValue(COLUMN_SUPPORTS_SAME_CHILD_TYPE, true);
	}

	public void setSupportsSameTypeChildGroups(Boolean supportsSameTypeChildGroups) {
		setColumn(COLUMN_SUPPORTS_SAME_CHILD_TYPE, supportsSameTypeChildGroups);
	}

	public boolean getAutoCreate() {
		return getBooleanColumnValue(COLUMN_AUTO_CREATE, true);
	}

	public void setAutoCreate(Boolean autoCreate) {
		setColumn(COLUMN_AUTO_CREATE, autoCreate);
	}

	public Collection ejbFindAllGroupTypes() throws FinderException {
		return super.idoFindIDsBySQL("select * from " + getEntityName());
	}

	public String ejbFindGroupTypeByGroupTypeString(String groupType) throws FinderException {
		return (String) idoFindOnePKByColumnBySQL(TYPE_COLUMN, groupType);
	}

	public Collection ejbFindVisibleGroupTypes() throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom();
		query.append(getEntityName());
		query.appendWhere(COLUMN_IS_VISIBLE);
		query.appendNOTEqual();
		query.appendWithinSingleQuotes(super.COLUMN_VALUE_FALSE);
		query.appendOrderBy(this.getIDColumnName());
		// System.out.println("[GroupTypeBMPBean](ejbFindVisibleGroupTypes):
		// "+query.toString());
		return this.idoFindPKsBySQL(query.toString());
		// return super.idoFindIDsBySQL("select * from "+getEntityName()+" where
		// "+ COLUMN_IS_VISIBLE + "!='"+super.COLUMN_VALUE_FALSE+"'");
	}

	public int ejbHomeGetNumberOfGroupTypes() throws FinderException, IDOException {
		return idoGetNumberOfRecords();
	}

	public int ejbHomeGetNumberOfVisibleGroupTypes() throws FinderException, IDOException {
		IDOQuery query = idoQuery();
		query.appendSelectCountFrom();
		query.append(getEntityName());
		query.appendWhere(COLUMN_IS_VISIBLE);
		query.appendNOTEqual();
		query.appendWithinSingleQuotes(super.COLUMN_VALUE_FALSE);
		// System.out.println("[GroupTypeBMPBean](ejbHomeGetNumberOfVisibleGroupTypes):
		// "+query.toString());
		return this.idoGetNumberOfRecords(query.toString());
		// return super.idoGetNumberOfRecords("select count(*) from
		// "+getEntityName()+" where "+ COLUMN_IS_VISIBLE +
		// "!='"+super.COLUMN_VALUE_FALSE+"'");
	}

	public String ejbHomeGetVisibleGroupTypesSQLString() {
		IDOQuery query = idoQuery();
		query.append("SELECT ").append(getIDColumnName()).appendFrom();
		query.append(getEntityName());
		query.appendWhere(COLUMN_IS_VISIBLE);
		query.appendNOTEqual();
		query.appendWithinSingleQuotes(super.COLUMN_VALUE_FALSE);
		return query.toString();
	}

	public static GroupTypeBMPBean getStaticInstance() {
		return (GroupTypeBMPBean) GenericEntity.getStaticInstance(GroupType.class.getName());
	}

	public String ejbHomeGetGeneralGroupTypeString() {
		return getGeneralGroupTypeString();
	}

	public String ejbHomeGetPermissionGroupTypeString() {
		return getPermissionGroupTypeString();
	}

	public String ejbHomeGetAliasGroupTypeString() {
		return getAliasGroupTypeString();
	}
}