package com.idega.core.category.data;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.core.business.ICTreeNodeLeafComparator;
import com.idega.core.component.data.*;
import com.idega.core.user.data.User;
import com.idega.data.EntityControl;
import com.idega.data.EntityFinder;
import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.TreeableEntityBMPBean;
import com.idega.util.IWTimestamp;

/**
 *  Description of the Class
 *
 *@author     gummi
 *@created    15. mars 2002
 */

public class ICInformationCategoryBMPBean extends TreeableEntityBMPBean implements com.idega.core.category.data.ICInformationCategory, com.idega.core.category.data.InformationCategory {

	private final static String DELETED_COLUMN = "DELETED";
	private final static String DELETED_BY_COLUMN = "DELETED_BY";
	private final static String DELETED_WHEN_COLUMN = "DELETED_WHEN";
	public final static String COLUMNNAME_LOCALE = "LOCALE_ID";

	/**
	 *  Description of the Field
	 */
	public final static String DELETED = "Y";

	/**
	 *  Description of the Field
	 */
	public final static String NOT_DELETED = "N";

	/**
	 *  Constructor for the ICInformationCategory object
	 */
	public ICInformationCategoryBMPBean() {
		super();
	}

	/**
	 *  Constructor for the ICInformationCategory object
	 *
	 *@param  id                Description of the Parameter
	 *@exception  SQLException  Description of the Exception
	 */
	public ICInformationCategoryBMPBean(int id) throws SQLException {
		super(id);
	}

	/**
	 *  Description of the Method
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getColumnName(), "Name", true, true, String.class);
		addAttribute(getColumnDescription(), "Description", true, true, String.class);
		addAttribute(getColumnType(), "Type", true, true, String.class);
		addAttribute(getColumnCreated(), "Created", true, true, java.sql.Timestamp.class);
		addAttribute(getColumnValid(), "Valid", true, true, Boolean.class);
		addAttribute(getColumnDeleted(), "Deleted", true, true, String.class, 1);
		addManyToOneRelationship(getColumnDeletedBy(), "Deleted by", User.class);
		addAttribute(getColumnDeletedWhen(), "Deleted when", true, true, Timestamp.class);
		addManyToOneRelationship(getColumnFolderSpecific(), "Owner folder", ICInformationFolder.class);
		addManyToOneRelationship(getColumnObjectId(), "Object id", ICObject.class);
		addManyToManyRelationShip(com.idega.core.component.data.ICObjectInstance.class, "IC_OBJ_INST_INFO_CAT");
	}

	/**
	 *  Gets the entityTableName attribute of the ICInformationCategory class
	 *
	 *@return    The entityTableName value
	 */
	public static String getEntityTableName() {
		return "IC_INFO_CATEGORY";
	}

	/**
	 *  Gets the columnName attribute of the ICInformationCategory class
	 *
	 *@return    The columnName value
	 */
	public static String getColumnName() {
		return "NAME";
	}

	/**
	 *  Gets the columnDescription attribute of the ICInformationCategory class
	 *
	 *@return    The columnDescription value
	 */
	public static String getColumnDescription() {
		return "DESCRIPTION";
	}

	/**
	 *  Gets the columnType attribute of the ICInformationCategory class
	 *
	 *@return    The columnType value
	 */
	public static String getColumnType() {
		return "CAT_TYPE";
	}

	/**
	 *  Gets the columnCreated attribute of the ICInformationCategory class
	 *
	 *@return    The columnCreated value
	 */
	public static String getColumnCreated() {
		return "CREATED";
	}

	/**
	 *  Gets the columnValid attribute of the ICInformationCategory class
	 *
	 *@return    The columnValid value
	 */
	public static String getColumnValid() {
		return "VALID";
	}

	/**
	 *  Gets the columnFolderSpecific attribute of the ICInformationCategory
	 *  class
	 *
	 *@return    The columnFolderSpecific value
	 */
	public static String getColumnFolderSpecific() {
		return "FOLDER_SPECIFIC";
	}

	/**
	 *  Gets the columnObjectId attribute of the ICInformationCategory class
	 *
	 *@return    The columnObjectId value
	 */
	public static String getColumnObjectId() {
		return "IC_OBJECT_ID";
	}

	/**
	 *  Gets the columnDeleted attribute of the ICInformationCategory class
	 *
	 *@return    The columnDeleted value
	 */
	public static String getColumnDeleted() {
		return (DELETED_COLUMN);
	}

	/**
	 *  Gets the columnDeletedBy attribute of the ICInformationCategory class
	 *
	 *@return    The columnDeletedBy value
	 */
	public static String getColumnDeletedBy() {
		return (DELETED_BY_COLUMN);
	}

	/**
	 *  Gets the columnDeletedWhen attribute of the ICInformationCategory class
	 *
	 *@return    The columnDeletedWhen value
	 */
	public static String getColumnDeletedWhen() {
		return (DELETED_WHEN_COLUMN);
	}

	/**
	 *  Gets the entityName attribute of the ICInformationCategory object
	 *
	 *@return    The entityName value
	 */
	public String getEntityName() {
		return getEntityTableName();
	}

	/**
	 *  Gets the name attribute of the ICInformationCategory object
	 *
	 *@return    The name value
	 */

	public String getName() {
		return getStringColumnValue(getColumnName());
	}

	/**
	 *  Sets the name attribute of the ICInformationCategory object
	 *
	 *@param  name  The new name value
	 */
	public void setName(String name) {
		setColumn(getColumnName(), name);
	}

	/**
	 *  Gets the description attribute of the ICInformationCategory object
	 *
	 *@return    The description value
	 */
	public String getDescription() {
		return getStringColumnValue(getColumnDescription());
	}

	/**
	 *  Sets the description attribute of the ICInformationCategory object
	 *
	 *@param  description  The new description value
	 */

	public void setDescription(String description) {
		setColumn(getColumnDescription(), description);
	}

	/**
	 *  Gets the valid attribute of the ICInformationCategory object
	 *
	 *@return    The valid value
	 */
	public boolean getValid() {
		return getBooleanColumnValue(getColumnValid());
	}

	/**
	 *  Sets the valid attribute of the ICInformationCategory object
	 *
	 *@param  valid  The new valid value
	 */

	public void setValid(boolean valid) {
		setColumn(getColumnValid(), valid);
	}

	/**
	 *  Gets the created attribute of the ICInformationCategory object
	 *
	 *@return    The created value
	 */

	public java.sql.Timestamp getCreated() {
		return (java.sql.Timestamp)getColumnValue(getColumnCreated());
	}

	/**
	 *  Sets the created attribute of the ICInformationCategory object
	 *
	 *@param  created  The new created value
	 */
	public void setCreated(java.sql.Timestamp created) {
		setColumn(getColumnCreated(), created);
	}

	/**
	 *  Gets the type attribute of the ICInformationCategory object
	 *
	 *@return    The type value
	 */
	public String getType() {
		return getStringColumnValue(getColumnType());
	}

	/**
	 *  Sets the type attribute of the ICInformationCategory object
	 *
	 *@param  type  The new type value
	 */
	public void setType(String type) {
		setColumn(getColumnType(), type);
	}

	/**
	 *  Gets the iCObjectId attribute of the ICInformationCategory object
	 *
	 *@return    The iCObjectId value
	 */
	public int getICObjectId() {
		return getIntColumnValue(getColumnObjectId());
	}

	/**
	 *  Sets the iCObjectId attribute of the ICInformationCategory object
	 *
	 *@param  id  The new iCObjectId value
	 */
	public void setICObjectId(int id) {
		setColumn(getColumnObjectId(), id);
	}

	/**
	 *  Gets the ownerFolderId attribute of the ICInformationCategory object
	 *
	 *@return    The ownerFolderId value
	 */
	public int getOwnerFolderId() {
		return getIntColumnValue(getColumnFolderSpecific());
	}

	/**
	 *  Sets the folderSpecific attribute of the ICInformationCategory object
	 *
	 *@param  folderId  The new folderSpecific value
	 */
	public void setFolderSpecific(int folderId) {
		setColumn(getColumnFolderSpecific(), folderId);
	}

	/**
	 *  Sets the global attribute of the ICInformationCategory object
	 *
	 *@exception  SQLException  Description of the Exception
	 */
	public void setGlobal() throws SQLException {
		this.removeFromColumn(getColumnFolderSpecific());
	}

	/**
	 *@return    The deleted value
	 */
	public boolean getDeleted() {
		String deleted = getStringColumnValue(getColumnDeleted());
		if ((deleted == null) || (deleted.equals(NOT_DELETED))) {
			return (false);
		} else if (deleted.equals(DELETED)) {
			return (true);
		} else {
			return (false);
		}
	}

	/**
	 *@param  deleted  The new deleted value
	 */
	public void setDeleted(int userID, boolean deleted) {
		if (deleted) {
			setColumn(getColumnDeleted(), DELETED);
			setDeletedWhen(IWTimestamp.getTimestampRightNow());
			if (userID != -1) {
				setDeletedBy(userID);
			}
		} else {
			setColumn(getColumnDeleted(), NOT_DELETED);
			setDeletedBy(-1);
			setDeletedWhen(null);
		}
	}

	/**
	 *@return    The deletedBy value
	 */
	public int getDeletedBy() {
		return (getIntColumnValue(getColumnDeletedBy()));
	}

	/**
	 *@param  userID  The new deletedBy value
	 */
	private void setDeletedBy(int userID) {
		setColumn(getColumnDeletedBy(), userID);
	}

	/**
	 *@return    The deletedWhen value
	 */
	public Timestamp getDeletedWhen() {
		return ((Timestamp)getColumnValue(getColumnDeletedWhen()));
	}

	/**
	 *@param  when  The new deletedWhen value
	 */
	private void setDeletedWhen(Timestamp when) {
		setColumn(getColumnDeletedWhen(), when);
	}

	/**
		 * Returns the children of the reciever as an Iterator. Returns null if no children found
		 */
	public Iterator getChildren() {
		return getChildren(null);
	}

	public Iterator getChildren(String orderBy) {
		try {
			String thisTable = this.getTableName();
			String treeTable = EntityControl.getTreeRelationShipTableName(this);
			String idColumnName = this.getIDColumnName();
			String childIDColumnName = EntityControl.getTreeRelationShipChildColumnName(this);
			StringBuffer buffer = new StringBuffer();
			buffer.append("select " + thisTable + ".* from " + thisTable + "," + treeTable + " where " + thisTable + "." + idColumnName + "=" + treeTable + "." + childIDColumnName + " and " + treeTable + "." + idColumnName + "='" + this.getPrimaryKey().toString() + "'");

			// is not deleted
			buffer.append(" and (").append(thisTable + "." + getColumnDeleted()).append("='").append(COLUMN_VALUE_FALSE).append("' or ").append(thisTable + "." + getColumnDeleted()).append(" is null ) ");

			if (orderBy != null && !orderBy.equals("")) {
				buffer.append(" order by " + thisTable + "." + orderBy);
			}

			List list = EntityFinder.findAll(this, buffer.toString());
			if (list != null) {
				if (_sortLeafs) {
					ICTreeNodeLeafComparator c = new ICTreeNodeLeafComparator(_leafsFirst);
					Collections.sort(list, c);
				}
				return list.iterator();
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.TreeableEntityBMPBean.getChildren() " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 *    Returns the number of children TreeNodes the receiver contains.
	 */
	public int getChildCount() {
		
		String thisTable = this.getTableName();
		String treeTable = EntityControl.getTreeRelationShipTableName(this);
		String idColumnName = this.getIDColumnName();
		String childIDColumnName = EntityControl.getTreeRelationShipChildColumnName(this);

		StringBuffer buffer = new StringBuffer();
		//buffer.append("select count(*) from ").append(treeTableName).append(" where ").append(this.getIDColumnName()).append("='").append(this.getPrimaryKey().toString()).append("'");
		buffer.append("select count(*) from " + thisTable + "," + treeTable + " where " + thisTable + "." + idColumnName + "=" + treeTable + "." + childIDColumnName + " and " + treeTable + "." + idColumnName + "='" + this.getPrimaryKey().toString() + "'");

		//	is not deleted
		buffer.append(" and (").append(thisTable + "." + getColumnDeleted()).append("='").append(COLUMN_VALUE_FALSE).append("' or ").append(thisTable + "." + getColumnDeleted()).append(" is null ) ");
		
		//System.out.println("Count: "+buffer.toString());
		
		return EntityControl.returnSingleSQLQuery(this, buffer.toString());
	}

	public void addCategoryToInstance(int instanceId) throws SQLException {
		this.addTo(ICObjectInstance.class, instanceId);
	}

	public void removeCategoryFromInstance(int instanceId) throws SQLException {
		this.removeFrom(ICObjectInstance.class, instanceId);
	}

	public Collection ejbFindAvailableCategories(int icObjectId, int workingFolderId) throws FinderException {

		IDOQuery query = selectAvailableCategories(icObjectId, workingFolderId);

		//System.out.println("SQL:" + query.toString());

		return idoFindIDsBySQL(query.toString());
	}

	public Collection ejbFindAvailableTopNodeCategories(int icObjectId, int workingFolderId) throws FinderException {

		IDOQuery query = selectAvailableCategories(icObjectId, workingFolderId);

		IDOQuery treeQuery = idoQuery();
		treeQuery.appendSelect();
		treeQuery.append(this.getTreeRelationshipChildColumnName(this));
		treeQuery.appendFrom();
		treeQuery.append(this.getTreeRelationshipTableName(this));

		query.appendAnd();
		query.append(this.getIDColumnName());
		query.appendNotIn(treeQuery);

		//System.out.println("SQL:" + query.toString());

		return idoFindIDsBySQL(query.toString());
	}

	protected IDOQuery selectAvailableCategories(int icObjectId, int workingFolderId) {

		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this);

		query.appendWhereEquals(getColumnObjectId(), icObjectId);
		query.appendAnd();

		IDOQuery subQuery = idoQuery();
		subQuery.appendEquals(getColumnFolderSpecific(), workingFolderId);
		subQuery.appendOrIsNull(getColumnFolderSpecific());
		subQuery.appendOrEquals(getColumnFolderSpecific(), -1);

		query.appendWithinParentheses(subQuery);
		query.appendAnd();
		appendIsNotDeleted(query);

		return query;
	}

	/**
	 * add condition if column deleted equals 'N' or is null
	 * 
	 * @param query
	 */
	private void appendIsNotDeleted(IDOQuery query) {
		query.appendLeftParenthesis().appendEqualsQuoted(getColumnDeleted(), GenericEntity.COLUMN_VALUE_FALSE).appendOrIsNull(getColumnDeleted()).appendRightParenthesis();
	}
	
	public void ejbHomeRemoveObjectInstanceRelation(ICObjectInstance instance) throws IDORemoveRelationshipException{
		idoRemoveFrom(instance);
	}

}