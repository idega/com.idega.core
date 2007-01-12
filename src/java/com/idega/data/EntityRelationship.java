package com.idega.data;
import java.util.Map;
import java.util.Hashtable;
/**
 * Title:        A class to handle information of  many-to-manrelationships
 * between IDO data entities. Copyright: Copyright (c) 2001-2002 Company: idega
 * software
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
class EntityRelationship {
	private boolean isImplementedWithTable = true;
	private Map columnsMap;
	private String setTableName;
	private String checkedTableName;
	EntityRelationship() {
	}
	
	/**
	 * Sets the table name to be used for the relationship.
	 * This will be checked for length and getTableName() will return a
	 * length checked version of the originalTableName for the underlying
	 * datastore.
	 */
	void setTableName(String originalTableName) {
		//this.tableName=tableName;
		this.setTableName = originalTableName;
		this.checkedTableName = EntityControl.getCheckedRelatedTableName(originalTableName);
	}
	
	/**
	 * Gets the tablename set with setTableName but checked with length i.e. if
	 * the length of the tablename is too long for the underlying datastore it
	 * truncates the name.
	 * @return String with the original tableName
	 */
	String getTableName() {
		//return tableName;
		//return checkedTableName;
		return EntityControl.getCheckedRelatedTableName(this.setTableName);
	}
	/**
	 * Gets the tablename originally set with setTableName
	 * @return String with the original tableName
	 */
	public String getSetTableName() {
		return this.setTableName;
	}
	/**
	 * Gets if the Entity is implemented with a "many-to-many" relationship
	 * table.
	 * @return boolean if the relationship is implemented with a table
	 */
	boolean isImplementedWithTable() {
		return this.isImplementedWithTable;
	}
	Map getColumnsAndReferencingClasses() {
		return this.columnsMap;
	}
	void addColumn(String columnName, Class referencingClass) {
		if (this.columnsMap == null) {
			this.columnsMap = new Hashtable();
		}
		this.columnsMap.put(columnName, referencingClass);
	}
}
