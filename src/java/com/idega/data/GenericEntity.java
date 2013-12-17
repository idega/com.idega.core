/*
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 *
 */
package com.idega.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.persistence.Entity;
import javax.persistence.EntityManager;

import com.idega.core.idgenerator.business.IdGenerator;
import com.idega.core.idgenerator.business.IdGeneratorFactory;
import com.idega.core.persistence.EntityManagerService;
import com.idega.data.query.Column;
import com.idega.data.query.Criteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.logging.LoggingHelper;

/**
 * A class to serve as a base implementation for objects mapped to persistent
 * data in the IDO Framework.
 *
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.4
 * @modified <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public abstract class GenericEntity implements Serializable, IDOEntity, IDOEntityBean, EntityRepresentation {

	private static final long serialVersionUID = -6654406719924582653L;

	public static final String MANY_TO_ONE = "many-to-one";
	public static final String ONE_TO_MANY = "one-to-many";
	public static final String MANY_TO_MANY = "many-to-many";
	public static final String ONE_TO_ONE = "one-to-one";
	protected static final String UNIQUE_ID_COLUMN_NAME = "UNIQUE_ID";

	static String DEFAULT_DATASOURCE = "default";
	private int _state = IDOLegacyEntity.STATE_NEW;

	private Map<String, Object> _columns = new Hashtable<String, Object>();
	private Map<String, Boolean> _updatedColumns;
	private String _dataSource;
	String[] _cachedColumnNameList;

	private Map<String, EJBLocalHome> _ejbHomes = new HashMap<String, EJBLocalHome>();

	private Object _primaryKey;
	private Map<String, String> _theMetaDataAttributes;
	private List<String> _insertMetaData;
	private List<String> _updateMetaData;
	private List<String> _deleteMetaData;
	private Hashtable<String, Integer> _theMetaDataIds;
	private Map<String, String> _theMetaDataTypes;
	private boolean _hasMetaDataRelationship = false;
	private boolean _metaDataHasChanged = false;
	public String _lobColumnName;
	private boolean insertStartData = true;
	protected static String COLUMN_VALUE_TRUE = "Y";
	protected static String COLUMN_VALUE_FALSE = "N";
	private boolean canRegisterColumnsForUpdate = false;

	private Table idoQueryTable = null;

	/*
	 * protected static int STATE_NEW = 0; protected static int
	 * STATE_IN_SYNCH_WITH_DATASTORE = 1; protected static int
	 * STATE_NOT_IN_SYNCH_WITH_DATASTORE = 2; protected static int
	 * STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE = 3; protected static int
	 * STATE_DELETED = 4;
	 */
	protected GenericEntity() {
		this(DEFAULT_DATASOURCE);
	}

	protected GenericEntity(String dataSource) {
		setDatasource(dataSource);
		try {
			firstLoadInMemoryCheck();
		}
		catch (Error e) {
			System.err.println("Error in " + this.getClass().getName() + ".firstLoadInMemoryCheck()");
			e.printStackTrace();
		}
		setDefaultValues();
		// this boolean is needed because developers are using setColumn in
		// setDefaultValues() and not initializeColumnValue
		// it stops the defaultvalues from being considered changed
		this.canRegisterColumnsForUpdate = true;
	}

	protected GenericEntity(int id) throws SQLException {
		this(id, DEFAULT_DATASOURCE);
	}

	protected GenericEntity(int id, String dataSource) throws SQLException {
		setDatasource(dataSource);
		firstLoadInMemoryCheck();
		this.findByPrimaryKey(id);
	}

	private void firstLoadInMemoryCheck() {
		GenericEntityDefinition entityDefinition = getGenericEntityDefinition();
		if (entityDefinition == null) {
			// IDOEntityDefinition
			entityDefinition = new GenericEntityDefinition();
			entityDefinition.setSQLTableName(this.getEntityName());
			entityDefinition.setUniqueEntityName(this.getEntityName());
			((PrimaryKeyDefinition) entityDefinition.getPrimaryKeyDefinition()).setPrimaryKeyClass(this.getPrimaryKeyClass());
			this.setGenericEntityDefinition(entityDefinition);
			// IDOEntityDefinition ends

			// First store a static instance of this class
			try {
				getIDOContainer().getEntityStaticInstances().put(this.getClass(), GenericEntity.instanciateEntity(this.getClass(), getDatasource()));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			this.getGenericEntityDefinition().setInterfaceClass(getInterfaceClass());
			// call the ializeAttributes that stores information about columns and
			// relationships
			beforeInitializeAttributes();
			initializeAttributes();
			afterInitializeAttributes();
			setLobColumnName();
			if (EntityControl.getIfEntityAutoCreate()) {
				try {
					DatastoreInterface.getInstance(this).createEntityRecord(this);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	protected static IDOContainer getIDOContainer() {
		return IDOContainer.getInstance();
	}

	protected static Collection getLoadedEntityBeans() {
		return getIDOContainer().getEntityStaticInstances().values();
	}

	/**
	 * Meant to be overrided in subclasses to add default attributes
	 */
	protected void beforeInitializeAttributes() {
	}

	/**
	 * Meant to be overrided in subclasses to add default attributes
	 */
	protected void afterInitializeAttributes() {
	}

	/**
	 * Override this function to set staring Data into the record of the entity at
	 * creation time
	 */
	public void insertStartData() throws Exception {
	}

	public String getTableName() {
		return getEntityName();
	}

	/**
	 * Subclasses have to implement this method - this should return the name of
	 * the underlying table
	 */
	public abstract String getEntityName();

	public abstract void initializeAttributes();

	@Override
	public Collection<EntityAttribute> getAttributes() {
		// ties the attribute vector to the subclass of IDOLegacyEntity because
		// the theAttributes variable is static.

		return getGenericEntityDefinition().getEntityFieldsCollection();
	}

	protected GenericEntityDefinition getGenericEntityDefinition() {
		return (GenericEntityDefinition) getIDOContainer().getEntityDefinitions().get(this.getClass());
	}

	protected GenericEntityDefinition setGenericEntityDefinition(GenericEntityDefinition definition) {
		return (GenericEntityDefinition) getIDOContainer().getEntityDefinitions().put(this.getClass(), definition);
	}

	public void setID(int id) {
		setColumn(getIDColumnName(), new Integer(id));
	}

	public void setID(Integer id) {
		setColumn(getIDColumnName(), id);
	}

	public int getID() {
		return getIntColumnValue(getIDColumnName());
	}

	public Object getPrimaryKeyValue() {
		if (this._primaryKey != null) {
			return this._primaryKey;
		}
		else {
			return this.getValue(getIDColumnName());
		}
	}

	public Integer getIDInteger() {
		return (Integer) getPrimaryKeyValue();
	}

	/**
	 * default unimplemented function, gets the name of the record from the
	 * datastore
	 */
	public String getName() {
		Object primaryKey = this.getPrimaryKey();
		if (primaryKey != null) {
			return primaryKey.toString();
		}
		return null;
	}

	public BlobWrapper getEmptyBlob(String columnName) {
		return new BlobWrapper(this, columnName);
	}

	/**
	 * default unimplemented function, sets the name of the record in the
	 * datastore
	 */
	public void setName(String name) {
		// does nothing
	}

	/**
	 * @see java.lang.Object#toString()
	 * @see com.idega.data.GenericEntity#getName()
	 * @see com.idega.data.GenericEntity#decode(String pkString)
	 */
	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * Decodes a String into a primaryKey Object. Recognises strings of the same
	 * format as com.idega.data.GenericEntity#toString() returns.
	 *
	 * @see com.idega.data.GenericEntity#toString()
	 */
	@Override
	public Integer decode(String pkString) {
		return Integer.decode(pkString);
	}

	/**
	 * Decodes a String into a primaryKey Object. Recognizes strings of the same
	 * format as com.idega.data.GenericEntity#toString() returns.
	 *
	 * @see com.idega.data.GenericEntity#toString()
	 */
	@Override
	public Collection<Integer> decode(String[] primaryKeys) {
		Collection<Integer> c = new ArrayList<Integer>();
		for (int i = 0; i < primaryKeys.length; i++) {
			c.add(decode(primaryKeys[i]));
		}
		return c;
	}

	/**
	 * @deprecated Replaced with addAttribute()
	 */
	@Deprecated
	protected void addColumnName(String columnName) {
		addAttribute(columnName);
	}

	protected void addAttribute(String attributeName) {
		EntityAttribute attribute;
		attribute = new EntityAttribute(attributeName);
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setAsPrimaryKey(true);
		attribute.setNullable(false);
		addAttribute(attribute);
	}

	/**
	 * @deprecated Replaced with addAttribute()
	 */
	@Deprecated
	protected void addColumnName(String columnName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName) {
		addAttribute(columnName, longName, ifVisible, ifEditable, storageClassName);
	}

	public void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, RefactorClassRegistry.forName(storageClassName));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName.toLowerCase());
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setStorageClass(storageClass);
		addAttribute(attribute);
	}

	/**
	 * Added by Eirikur Hrafnsson
	 *
	 */
	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, int maxLength) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, RefactorClassRegistry.forName(storageClassName), maxLength);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	protected void addAttribute(String attributeName, String longName, Class storageClass) {
		addAttribute(attributeName, longName, true, true, storageClass);
	}

	protected void addAttribute(String attributeName, String longName, Class storageClass, int maxLength) {
		addAttribute(attributeName, longName, true, true, storageClass, maxLength);
	}

	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass, int maxLength) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setStorageClass(storageClass);
		attribute.setMaxLength(maxLength);
		addAttribute(attribute);
	}

	/**
	 * @deprecated Replaced with addAttribute()
	 */
	@Deprecated
	protected void addColumnName(String columnName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, String relationShipType, String relationShipClassName) {
		addAttribute(columnName, longName, ifVisible, ifEditable, storageClassName, relationShipType, relationShipClassName);
	}

	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, String relationShipType, String relationShipClassName) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, RefactorClassRegistry.forName(storageClassName), relationShipType, RefactorClassRegistry.forName(relationShipClassName));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass, String relationShipType, Class relationShipClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		addAttribute(attribute);
	}

	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, String storageClassName, int maxLength, String relationShipType, String relationShipClassName) {
		try {
			addAttribute(attributeName, longName, ifVisible, ifEditable, RefactorClassRegistry.forName(storageClassName), maxLength, relationShipType, RefactorClassRegistry.forName(relationShipClassName));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	protected void addAttribute(String attributeName, String longName, boolean ifVisible, boolean ifEditable, Class storageClass, int maxLength, String relationShipType, Class relationShipClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setAsPrimaryKey(this.getIDColumnName().equalsIgnoreCase(attributeName));
		attribute.setDeclaredEntity(getGenericEntityDefinition());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		attribute.setMaxLength(maxLength);

		addAttribute(attribute);
	}

	protected void addAttribute(EntityAttribute attribute) {
		// getAttributesMap().put(attribute.getName().toUpperCase(),attribute);
		getGenericEntityDefinition().addFieldEntity(attribute);
		// getAttributes().addElement(attribute);

	}

	protected void addLanguageAttribute() {
		this.addAttribute(getLanguageIDColumnName(), "Language", true, true, "java.lang.Integer", "one_to_one", "com.idega.core.localisation.data.Language");
	}

	/**
	 * @deprecated Replaced with getAttribute()
	 */
	@Deprecated
	public EntityAttribute getColumn(String columnName) {
		return getAttribute(columnName);
	}

	public EntityAttribute getAttribute(String attributeName) {
		// return (EntityAttribute) columns.get(columnName.toLowerCase());
		// EntityAttribute theReturn =
		// (EntityAttribute)getAttributesMap().get(attributeName.toUpperCase());
		EntityAttribute theReturn = getGenericEntityDefinition().getEntityAttribute(attributeName.toUpperCase());
		/**
		 * EntityAttribute theReturn = null; EntityAttribute tempColumn = null; for
		 * (Enumeration enumeration = getAttributes().elements();
		 * enumeration.hasMoreElements();) { tempColumn = (EntityAttribute)
		 * enumeration.nextElement(); if
		 * (tempColumn.getColumnName().equalsIgnoreCase(attributeName)) { theReturn =
		 * tempColumn; } }
		 */
		/*
		 * if(theReturn==null){ System.err.println("Error in
		 * "+this.getClass().getName()+".getAttribute():
		 * ColumnName='"+attributeName+"' exists in table but not in Entity Class"); }
		 */
		return theReturn;
	}

	protected void addOneToOneRelationship(String relationshipColumnName, Class relatingEntityClass) {
		addOneToOneRelationship(relationshipColumnName, relatingEntityClass.getName(), relatingEntityClass);
	}

	protected void addOneToOneRelationship(String relationshipColumnName, String description, Class relatingEntityClass) {
		try {
			Class primaryKeyInRelatedClass = IDOLookup.getEntityDefinitionForClass(relatingEntityClass).getPrimaryKeyDefinition().getPrimaryKeyClass();
			addAttribute(relationshipColumnName, description, true, true, primaryKeyInRelatedClass, com.idega.data.GenericEntity.ONE_TO_ONE, relatingEntityClass);
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
	}

	protected void addManyToOneRelationship(String relationshipColumnName, Class relatingEntityClass) {
		addManyToOneRelationship(relationshipColumnName, relatingEntityClass.getName(), relatingEntityClass);
	}

	protected void addManyToOneRelationship(String relationshipColumnName, String description, Class relatingEntityClass) {
		try {
			Class primaryKeyInRelatedClass = IDOLookup.getEntityDefinitionForClass(relatingEntityClass).getPrimaryKeyDefinition().getPrimaryKeyClass();
			addAttribute(relationshipColumnName, description, true, true, primaryKeyInRelatedClass, com.idega.data.GenericEntity.MANY_TO_ONE, relatingEntityClass);
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
	}

	protected void addRelationship(String relationshipName, String relationshipType, String relationshipClassName) {
		try {
			EntityAttribute attribute = new EntityAttribute(getGenericEntityDefinition());
			attribute.setName(relationshipName);
			attribute.setAttributeType("relationship");
			attribute.setRelationShipClass(RefactorClassRegistry.forName(relationshipClassName));
			addAttribute(attribute);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	// /**
	// *Constructs an array of GenericEntities through an int Array (containing
	// rows of id's from the datastore) - uses the findByPrimaryKey method to
	// instanciate a new object fetched from the database
	// *@deprecated Only IDOLegacyEntity method, does not work with pure IDOEntity
	// **/
	// public IDOLegacyEntity[] constructArray(int[] id_array) {
	// IDOLegacyEntity[] returnArr = null;
	// try {
	// returnArr =
	// (IDOLegacyEntity[])java.lang.reflect.Array.newInstance(this.getClass(),
	// id_array.length);
	// for (int i = 0; i < id_array.length; i++) {
	// returnArr[i] = (IDOLegacyEntity)instanciateEntity(this.getClass());
	// returnArr[i].findByPrimaryKey(id_array[i]);
	// }
	// } catch (Exception ex) {
	// System.err.println("There was an error in
	// com.idega.data.GenericEntity.constructArray(int[] id_array): " +
	// ex.getMessage());
	// }
	// return returnArr;
	// }
	/**
	 * Constructs an array of GenericEntities through a String Array (containing
	 * only int's for the rows of id's from the datastore) - uses the
	 * findByPrimaryKey method to instanciate a new object fetched from the
	 * database
	 *
	 * @deprecated Only IDOLegacyEntity method, does not work with pure IDOEntity
	 */
	@Deprecated
	public IDOLegacyEntity[] constructArray(String[] id_array) {
		IDOLegacyEntity[] returnArr = null;
		try {
			returnArr = (IDOLegacyEntity[]) java.lang.reflect.Array.newInstance(this.getClass(), id_array.length);
			for (int i = 0; i < id_array.length; i++) {
				returnArr[i] = (IDOLegacyEntity) instanciateEntity(this.getClass(), getDatasource());
				returnArr[i].findByPrimaryKey(Integer.parseInt(id_array[i]));
			}
		}
		catch (Exception ex) {
			System.err.println("There was an error in com.idega.data.GenericEntity.constructArray(String[] id_array): " + ex.getMessage());
		}
		return returnArr;
	}

	/**
	 * Sets the colums value or removes the existing value if columnValue == null
	 *
	 * @param columnName
	 * @param columnValue
	 * @param needsToUpdate
	 *          true if this is a column that is changing, false if it is just
	 *          being loaded from the database
	 */
	protected void setValue(String columnName, Object columnValue, boolean needsToUpdate) {
		// only update if it is a new value
		// and remove if it a null value
		String upperCaseColumnName = columnName.toUpperCase();
		Object oldValue = this._columns.get(upperCaseColumnName);

		if (columnValue != null) {
			if (!(oldValue != null && columnValue.equals(oldValue))) {
				this._columns.put(upperCaseColumnName, columnValue);
			}
			else {
				// its the same, don't add it
				needsToUpdate = false;
			}
		}
		else {
			removeFromColumn(upperCaseColumnName);
		}

		if (needsToUpdate) {
			flagColumnUpdate(upperCaseColumnName);

			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			}
			else {
				setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}
		}
	}

	protected void setValue(String columnName, Object columnValue) {
		setValue(columnName, columnValue, true);
	}

	protected Object getValue(String columnName) {
		return this._columns.get(columnName.toUpperCase());
	}

	/**
	 * Sets the column to null
	 */
	public void removeFromColumn(String columnName) {
		this._columns.remove(columnName.toUpperCase());
		this.flagColumnUpdate(columnName);
	}

	/**
	 * This method is called when changing a columns value or initilizing it
	 *
	 * @param columnName
	 * @param columnValue
	 * @param needsToUpdate
	 *          true if we are changing the column value, false if just loading
	 *          from the database
	 */
	public void setColumn(String columnName, Object columnValue, boolean needsToUpdate) {
		if (this.getRelationShipClass(columnName) == null) {
			// this is the most common case, the column is just a primative type and
			// not a relational column (foreign key)
			setValue(columnName, columnValue, needsToUpdate);
		}
		else if (columnValue instanceof IDOEntity) {
			// this is the second most common case, an IDOEntity is passed into the
			// setColumn method but we save its primarykey as the column value
			setValue(columnName, ((IDOEntity) columnValue).getPrimaryKey(), needsToUpdate);
		}
		else {
			// lastly this is used for other cases. setting ids directly (foreign
			// keys)
			setValue(columnName, columnValue, needsToUpdate);
		}
	}

	/**
	 * Sets the columns value and flags the column as changed
	 *
	 * @param columnName
	 * @param columnValue
	 */
	public void setColumn(String columnName, Object columnValue) {
		setColumn(columnName, columnValue, true);
	}

	/**
	 * Sets the column value for the first time, the column is NOT flagged as
	 * changed
	 *
	 * @param columnName
	 * @param columnValue
	 */
	protected void initializeColumnValue(String columnName, Object columnValue) {
		setColumn(columnName, columnValue, false);
	}

	public void setColumn(String columnName, int columnValue) {
		setColumn(columnName, new Integer(columnValue), true);
	}

	public void setColumn(String columnName, Integer columnValue) {
		setColumn(columnName, columnValue, true);
	}

	public void setColumn(String columnName, float columnValue) {
		setColumn(columnName, new Float(columnValue), true);
	}

	public void setColumn(String columnName, Float columnValue) {
		setColumn(columnName, columnValue, true);
	}

	public void setColumn(String columnName, boolean columnValue) {
		setColumn(columnName, new Boolean(columnValue), true);
	}

	public void setColumn(String columnName, Boolean columnValue) {
		setColumn(columnName, columnValue, true);
	}

	public void setColumn(String columnName, char columnValue) {
		setColumn(columnName, String.valueOf(columnValue), true);
	}

	public void setColumn(String columnName, double columnValue) {
		setColumn(columnName, new Double(columnValue), true);
	}

	public void setColumn(String columnName, Double columnValue) {
		setColumn(columnName, columnValue, true);
	}

	/**
	 * @deprecated replaced with removeFromColumn(columnName) Sets a column value
	 *             to null
	 */
	@Deprecated
	public void setColumnAsNull(String columnName) throws SQLException {
		Connection Conn = null;
		try {
			/**
			 * @todo stringbuffer
			 */
			Conn = getConnection(getDatasource());
			String sql = "update " + this.getEntityName() + " set " + columnName + " = null where " + this.getIDColumnName() + " = " + this.getPrimaryKeyValueSQLString();

			Conn.createStatement().executeUpdate(sql);
			Conn.commit();
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		}
		finally {
			if (Conn != null) {
				freeConnection(getDatasource(), Conn);
			}
		}
	}

	/**
	 * The Outputstream must be completele written to when insert() or update() is
	 * executed on the Entity Class
	 */
	public OutputStream getColumnOutputStream(String columnName) {
		BlobWrapper wrapper = getBlobColumnValue(columnName);
		if (wrapper == null) {
			wrapper = new BlobWrapper(this, columnName);
			setColumn(columnName, wrapper);
		}
		else {
			this.flagColumnUpdate(columnName);
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			}
			else {
				this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}
		}
		return wrapper.getOutputStreamForBlobWrite();
	}

	public void setColumn(String columnName, InputStream streamForBlobWrite) {
		BlobWrapper wrapper = getBlobColumnValue(columnName);
		if (wrapper != null) {
			wrapper.setInputStreamForBlobWrite(streamForBlobWrite);
			this.flagColumnUpdate(columnName);
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			}
			else {
				this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}
		}
		else {
			wrapper = new BlobWrapper(this, columnName);
			wrapper.setInputStreamForBlobWrite(streamForBlobWrite);
			setColumn(columnName, wrapper, true);
		}
	}

	public BlobWrapper getBlobColumnValue(String columnName) {
		BlobWrapper wrapper = (BlobWrapper) getColumnValue(columnName);
		if (wrapper == null) {
			wrapper = new BlobWrapper(this, columnName);
			this.setColumn(columnName, wrapper, false);
		}
		return wrapper;
	}

	public InputStream getInputStreamColumnValue(String columnName) {
		BlobWrapper wrapper = getBlobColumnValue(columnName);
		/*
		 * if(wrapper==null){ wrapper = new BlobWrapper(this,columnName);
		 * this.setColumn(columnName,wrapper); }
		 */
		return wrapper.getBlobInputStream();
	}

	protected <T>T getRealValue(String columnName) {
		return getRealValue(columnName, (T) null);	//	Type parameter added because of Maven 2 compiler - it fails without casting to T
	}

	@SuppressWarnings("unchecked")
	protected <T>T getRealValue(String columnName, T defaultValue) {
		Object value = getColumnValue(columnName);
		return value == null ? defaultValue : (T) value;
	}

	@Override
	public Object getColumnValue(String columnName) {
		Object returnObj = null;
		Object value = getValue(columnName);
		Class<? extends IDOEntity> relationClass = this.getRelationShipClass(columnName);
		if (value instanceof com.idega.data.IDOEntity) {
			returnObj = value;
		}
		// else if (value instanceof java.lang.Integer){
		else if (relationClass != null) {
			// if (getRelationShipClass(columnName).getName().indexOf("idega") != -1){
			if (relationClass.isAnnotationPresent(Entity.class)) {
				if (value != null) {
					EntityManagerService service = new EntityManagerService();
					EntityManager manager = service.getEntityManagerFactory().createEntityManager();

					returnObj = manager.find(relationClass, value);
				}
			}
			else {
				try {
					// returnObj =
					// this.findByPrimaryInOtherClass(getRelationShipClass(columnName),((Integer)value).intValue());
					if (value != null) {
						IDOHome home = IDOLookup.getHome(relationClass);
						if (this.getDatasource() != null) {
							home = IDOLookup.getHome(relationClass, getDatasource());
						}
						returnObj = home.findByPrimaryKeyIDO(value);
					}
				}
				catch (Exception ex) {
					System.err.println("Exception in com.idega.data.GenericEntity.getColumnValue(String columnName): of type+ " + ex.getClass().getName() + " , Message = " + ex.getMessage());
					ex.printStackTrace(System.err);
				}
				finally {
				}
			}
			// }
			// else{
			// }
		}
		// else{
		// returnObj = value;
		// }
		// }
		else {
			returnObj = value;
		}
		if (returnObj == null) {
		}
		else {
		}
		return returnObj;
	}

	public String getStringColumnValue(String columnName) {
		if (getValue(columnName) != null) {
			if (this.getStorageClass(columnName).equals(java.lang.Boolean.class)) {
				if (((Boolean) getColumnValue(columnName)).booleanValue() == true) {
					return "Y";
				}
				else {
					return "N";
				}
			}
			else {
				return getValue(columnName).toString();
			}
		}
		else {
			return null;
		}
	}

	public char getCharColumnValue(String columnName) {
		return getCharColumnValue(columnName, ' ');
	}

	public char getCharColumnValue(String columnName, char returnValueIfNull) {
		String tempString = (String) getColumnValue(columnName);
		return (tempString == null || tempString.length() == 0) ? returnValueIfNull : tempString.charAt(0);
	}

	public float getFloatColumnValue(String columnName) {
		return getFloatColumnValue(columnName, -1F);
	}

	public float getFloatColumnValue(String columnName, float returnValueIfNull) {
		Float tempFloat = (Float) getColumnValue(columnName);
		return (tempFloat == null) ? returnValueIfNull : tempFloat.floatValue();
	}

	public double getDoubleColumnValue(String columnName) {
		return getDoubleColumnValue(columnName, -1D);
	}

	public double getDoubleColumnValue(String columnName, double returnValueIfNull) {
		Double tempDouble = (Double) getColumnValue(columnName);
		return (tempDouble == null) ? returnValueIfNull : tempDouble.doubleValue();
	}

	public Integer getIntegerColumnValue(String columnName) {
		return (Integer) getValue(columnName);
	}

	public int getIntColumnValue(String columnName) {
		return getIntColumnValue(columnName, -1);
	}

	public int getIntColumnValue(String columnName, int returnValueIfNull) {
		Object tempInt = getValue(columnName);
		return (tempInt == null) ? returnValueIfNull : new Integer(tempInt.toString()).intValue();
	}

	public boolean getBooleanColumnValue(String columnName) {
		return getBooleanColumnValue(columnName, false);
	}

	public boolean getBooleanColumnValue(String columnName, boolean returnValueIfNull) {
		Object value = getValue(columnName);
		if (value != null) {
			// Boolean tempBool = (Boolean) getValue(columnName);
			// if (tempBool != null){
			// return tempBool.booleanValue();
			if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue();
			}
			else if (value instanceof String) {
				String sValue = (String) value;
				if (sValue.equals(COLUMN_VALUE_TRUE)) {
					return true;
				}
				else if (sValue.equals(COLUMN_VALUE_FALSE)) {
					return false;
				}
			}
		}
		return returnValueIfNull;
	}

	public Date getDateColumnValue(String columnName) {
		return (Date) getValue(columnName);
	}

	public Timestamp getTimestampColumnValue(String columnName) {
		return (Timestamp) getValue(columnName);
	}

	public Time getTimeColumnValue(String columnName) {
		return (Time) getValue(columnName);
	}

	public void setLongName(String columnName, String longName) {
		getColumn(columnName).setLongName(longName);
	}

	public String getLongName(String columnName) {
		return getColumn(columnName).getLongName();
	}

	public void setRelationShipType(String columnName, String type) {
		getColumn(columnName).setRelationShipType(type);
	}

	public String getRelationShipType(String columnName) {
		return getColumn(columnName).getRelationShipType();
	}

	/**
	 * @deprecated replaced with getStorageClass
	 */
	@Deprecated
	public int getStorageClassType(String columnName) {
		EntityAttribute attribute = getColumn(columnName);
		if (attribute != null) {
			return attribute.getStorageClassType();
		}
		else {
			return 0;
		}
	}

	/**
	 * @deprecated replaced with getStorageClass
	 */
	@Deprecated
	public String getStorageClassName(String columnName) {
		String theReturn = "";
		if (getColumn(columnName) != null) {
			theReturn = getColumn(columnName).getStorageClassName();
		}
		return theReturn;
	}

	public Class getStorageClass(String columnName) {
		Class theReturn = null;
		if (getColumn(columnName) != null) {
			theReturn = getColumn(columnName).getStorageClass();
		}
		return theReturn;
	}

	/**
	 * @deprecated replaced with setStorageClass
	 */
	@Deprecated
	public void setStorageClassName(String columnName, String className) {
		try {
			getColumn(columnName).setStorageClass(RefactorClassRegistry.forName(className));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	public void setStorageClass(String columnName, Class javaClass) {
		getColumn(columnName).setStorageClass(javaClass);
	}

	/**
	 * @deprecated replaced with setStorageClass
	 */
	@Deprecated
	public void setStorageClassType(String columnName, int classType) {
		getColumn(columnName).setStorageClassType(classType);
	}

	public void setEditable(String columnName, boolean ifEditable) {
		getColumn(columnName).setEditable(ifEditable);
	}

	public boolean getIfEditable(String columnName) {
		return getColumn(columnName).getIfEditable();
	}

	public void setVisible(String columnName, boolean ifVisible) {
		getColumn(columnName).setVisible(ifVisible);
	}

	public boolean getIfVisible(String columnName) {
		return getColumn(columnName).getIfVisible();
	}

	/*
	 * public String getRelationShipClassName(String columnName){ String theReturn =
	 * ""; if (getColumn(columnName) != null){ theReturn =
	 * getColumn(columnName).getRelationShipClassName(); } return theReturn; }
	 */
	/**
	 * Returns null if the specified column does have a relationship Class
	 */
	public Class<? extends IDOEntity> getRelationShipClass(String columnName) {
		EntityAttribute column = getAttribute(columnName);
		if (column != null) {
			return column.getRelationShipClass();
		}
		return null;
	}

	public void setRelationShipClassName(String columnName, String className) {
		try {
			getColumn(columnName).setRelationShipClass(RefactorClassRegistry.forName(className));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	public void setMaxLength(String columnName, int maxLength) {
		getColumn(columnName).setMaxLength(maxLength);
	}

	public int getMaxLength(String columnName) {
		return getColumn(columnName).getMaxLength();
	}

	public void setNullable(String columnName, boolean ifNullable) {
		getColumn(columnName).setNullable(ifNullable);
	}

	public boolean getIfNullable(String columnName) {
		return getColumn(columnName).getIfNullable();
	}

	public boolean getIfUnique(String columnName) {
		return getColumn(columnName).getIfUnique();
	}

	public void setUnique(String columnName, boolean ifUnique) {
		getColumn(columnName).setUnique(ifUnique);
	}

	public void setAsPrimaryKey(String columnName, boolean ifPrimaryKey) {
		getColumn(columnName).setAsPrimaryKey(ifPrimaryKey);
	}

	public boolean isPrimaryKey(String columnName) {
		return getColumn(columnName).isPrimaryKey();
	}

	/**
	 * Gets a databaseconnection identified by the datasourceName
	 */
	public Connection getConnection(String datasourceName) throws SQLException {
		return ConnectionBroker.getConnection(datasourceName);
	}

	/**
	 * Gets the default database connection
	 */
	public Connection getConnection() throws SQLException {
		return ConnectionBroker.getConnection(getDatasource());
	}

	/**
	 * Frees the connection used, must be done after using a databaseconnection
	 */
	public void freeConnection(String datasourceName, Connection connection) {
		ConnectionBroker.freeConnection(datasourceName, connection);
	}

	/**
	 * Frees the default connection used, must be done after using a
	 * databaseconnection
	 */
	public void freeConnection(Connection connection) {
		ConnectionBroker.freeConnection(getDatasource(), connection);
	}

	/**
	 * Sets the datasource to another datastore.<br>
	 * Can be used to switch the datasource when the entity has been fetched from
	 * another datasource (.e.g. when this instance is inserted into a new
	 * datastore with another datasource).
	 */
	@Override
	public void setDatasource(String dataSource) {
		setDatasource(dataSource, false);
	}

	/**
	 * Sets the datasource to another datastore.<br>
	 * Can be used to switch the datasource when the entity has been fetched from
	 * another datasource (.e.g. when this instance is inserted into a new
	 * datastore with another datasource).
	 *
	 * @param reloadEntity
	 *          if TRUE then the entity datatables in the new datasource will be
	 *          checked for inconsistencies
	 */
	public void setDatasource(String dataSource, boolean reloadEntity) {
		if (!dataSource.equals(this._dataSource)) {
			try {
				// Connect the blob fields if the datasource is changed
				// System.out.println("[setDataSource()] "+this.getClass().getName()+"
				// EntityState="+this.getEntityState());
				if (getEntityState() == IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE && this.hasLobColumn()) {
					BlobWrapper wrapper = this.getBlobColumnValue(this.getLobColumnName());
					BlobInputStream inStream = wrapper.getBlobInputStream();
					// inStream.setDataSource(this._dataSource);
					wrapper.setInputStreamForBlobWrite(inStream);
					setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
					// System.out.println(this.getClass().getName()+".setDatasource("+dataSource+"),
					// connecting blob fields");
				}
			}
			catch (Exception e) {
				System.err.println("Exception in connecting blob fields for " + this.getClass().getName() + ", primary value=" + this.getPrimaryKeyValueSQLString());
				e.printStackTrace();
			}
		}
		this._dataSource = dataSource;
		if (reloadEntity) {
			getIDOContainer().getEntityDefinitions().put(this.getClass(), null);
			firstLoadInMemoryCheck();
		}
	}

	@Override
	public String getDatasource() {
		return this._dataSource;
	}

	/**
	 * @todo add: public String getPKColumnName(){ String entityName =
	 *       getEntityName(); if (entityName.endsWith("_")){ return
	 *       entityName+"id"; } else{ return entityName+"_id"; } }
	 */
	public boolean isPrimaryKeyColumn(String columnName) {
		IDOEntityField[] fields = getEntityDefinition().getPrimaryKeyDefinition().getFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getSQLFieldName().equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	public String getIDColumnName() {
		IDOPrimaryKeyDefinition pkd = this.getEntityDefinition().getPrimaryKeyDefinition();
		if (pkd == null || pkd.getFields().length == 0) {
			String entityName = getEntityName();
			if (entityName.endsWith("_")) {
				return entityName + "ID";
			}
			else {
				return entityName + "_ID";
			}
		}
		else {
			try {
				return pkd.getField().getSQLFieldName();
			}
			catch (IDOCompositePrimaryKeyException e) {
				return pkd.getFields()[0].getSQLFieldName();
			}
		}
	}

	public static String getLanguageIDColumnName() {
		return "language_id";
	}

	/**
	 * Gets a String array with all the columns defined in this entity bean.
	 *
	 * @see com.idega.data.IDOLegacyEntity#getColumnNames()
	 */
	public String[] getColumnNames() {
		String[] theReturn = getCachedColumnNames();

		if (theReturn == null) {
			Collection<String> results = new ArrayList<String>();
			// int i = 0;
			// for (Enumeration e = columns.keys(); e.hasMoreElements();i++){
			// for (Enumeration e = getAttributes().elements(); e.hasMoreElements();
			// i++)
			/*for (Iterator iter = getAttributes().iterator(); iter.hasNext(); i++) {
				EntityAttribute temp = (EntityAttribute) iter.next();
				// EntityAttribute temp = (EntityAttribute) e.nextElement();
				if (temp.getAttributeType().equals("column")) {
					// vector.addElement(temp.getColumnName().toLowerCase());
					vector.addElement(temp.getColumnName());
				}
			}*/
			for (EntityAttribute object : getAttributes()) {
				if (object.getAttributeType().equals("column")) {
					results.add(object.getColumnName());
				}
			}

			theReturn = results.toArray(new String[0]);
			setCachedColumnNames(theReturn);
		}
		return theReturn;
	}

	private String[] getCachedColumnNames() {
		return getIDOEntityStaticInstance()._cachedColumnNameList;
	}

	private void setCachedColumnNames(String[] columnNames) {
		getIDOEntityStaticInstance()._cachedColumnNameList = columnNames;
	}

	/** @todo this should not be done every time cache!!* */
	public String[] getVisibleColumnNames() {
		List<String> theColumns = new ArrayList<String>();
		for (Iterator<EntityAttribute> iter = getAttributes().iterator(); iter.hasNext();) {
			String tempName = iter.next().getColumnName();
			if (getIfVisible(tempName)) {
				theColumns.add(tempName);
			}
		}
		return theColumns.toArray(new String[0]);
	}

	public String[] getEditableColumnNames() {
		Collection<String> theColumns = new ArrayList<String>();
		for (Iterator<EntityAttribute> iter = getAttributes().iterator(); iter.hasNext();) {
			String tempName = iter.next().getColumnName();
			if (getIfEditable(tempName)) {
				theColumns.add(tempName);
			}
		}
		return theColumns.toArray(new String[0]);
	}

	public boolean isNull(String columnName) {
		Object o = this._columns.get(columnName.toUpperCase());
		if (o == null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasBeenSetNull(String columnName) {
		if (this.hasColumnBeenUpdated(columnName)) {
			return this.isNull(columnName);
		}
		else {
			return false;
		}
	}

	/*
	 * Returns the type of the underlying datastore - returns: "mysql",
	 * "interbase", "oracle", "unimplemented"
	 */
	/*
	 * public String getDataStoreType(Connection connection){ if (dataStoreType ==
	 * null){ if (connection != null){ if
	 * (connection.getClass().getName().indexOf("oracle") != -1 ){ dataStoreType =
	 * "oracle"; } else if (connection.getClass().getName().indexOf("interbase") !=
	 * -1 ){ dataStoreType = "interbase"; } else if
	 * (connection.getClass().getName().indexOf("mysql") != -1 ){ dataStoreType =
	 * "mysql"; }
	 *
	 * else{ dataStoreType = "unimplemented"; } } else{ dataStoreType = ""; } }
	 * return dataStoreType; }
	 */
	/**
	 * *Override this function to set default values to columns if they have no
	 * set values
	 */
	public void setDefaultValues() {
		// default implementation does nothing
	}

	/**
	 * Inserts this entity as a record into the datastore
	 */
	public void insert() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).insert(this);
			if (isBeanCachingActive()) {
				IDOContainer.getInstance().getBeanCache(getDatasource(),this.getInterfaceClass()).putCachedEntity(getPrimaryKey(), this);
			}
			flushQueryCache();
		}
		catch (Exception ex) {
			if (isDebugActive()) {
				ex.printStackTrace();
			}

			try {
				this.closeBlobConnections();
			}
			catch (Exception e) {
			}
			if (ex instanceof SQLException) {
				// ex.printStackTrace();
				// throw (SQLException)ex.fillInStackTrace();
				throw (SQLException) ex;
			}
			else {
				ex.printStackTrace();
				throw new SQLException("Exception rethrown: " + ex.getClass().getName() + " - " + ex.getMessage());
			}
		}
	}

	/**
	 * Inserts/update/removes this entity's metadata into the datastore
	 */
	public void updateMetaData() throws SQLException {
		try {
			if (isIdColumnValueNotEmpty()) {
				DatastoreInterface.getInstance(this).crunchMetaData(this);
			}
			else {
				logWarning("[IDOLegacyEntity] updateMetaData(): getID == -1 (" + this.getTableName() + ")");
			}
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException) ex.fillInStackTrace();
			}
			else {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Inserts/update/removes this entity's metadata into the datastore
	 */
	public void insertMetaData() throws SQLException {
		updateMetaData();
	}

	/**
	 * deletes all of this entity's metadata
	 */
	public void deleteMetaData() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).deleteMetaData(this);
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException) ex.fillInStackTrace();
			}
		}
	}

	/**
	 * Inserts this entity as a record into the datastore
	 */
	public void insert(Connection c) throws SQLException {
		try {
			DatastoreInterface.getInstance(c).insert(this, c);
			flushQueryCache();
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException) ex.fillInStackTrace();
			}
		}
	}

	/*
	 * public void insert()throws SQLException{ EntityControl.insert(this); }
	 */
	/**
	 * Updates the entity in the datastore
	 */
	public synchronized void update() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).update(this);
			updateInCaches();
		}
		catch (Exception ex) {
			if (isDebugActive()) {
				ex.printStackTrace();
			}

			if (ex instanceof SQLException) {
				// ex.printStackTrace();
				throw (SQLException) ex.fillInStackTrace();
			}
		}
	}

	/**
	 * Updates the entity in the datastore
	 */
	public void update(Connection c) throws SQLException {
		try {
			DatastoreInterface.getInstance(c).update(this, c);
			updateInCaches();
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				// ex.printStackTrace();
				throw (SQLException) ex.fillInStackTrace();
			}
		}
	}

	private void updateInCaches() {
		flushQueryCache();
		if (IDOContainer.getInstance().beanCachingActive(this.getInterfaceClass())) {
			IDOContainer.getInstance().getBeanCache(getDatasource(),this.getInterfaceClass()).putCachedEntity(this.getPrimaryKey(), this);
		}
	}


	public void delete() throws SQLException {
		try {
			DatastoreInterface.getInstance(this).delete(this);
			deleteInCaches();
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException) ex.fillInStackTrace();
			}
		}
	}

	public void delete(Connection c) throws SQLException {
		try {
			DatastoreInterface.getInstance(c).delete(this, c);
			deleteInCaches();
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				ex.printStackTrace();
				throw (SQLException) ex.fillInStackTrace();
			}
		}
	}

	private void deleteInCaches() {
		flushQueryCache();
		if (IDOContainer.getInstance().beanCachingActive(this.getInterfaceClass())) {
			IDOContainer.getInstance().getBeanCache(getDatasource(),this.getInterfaceClass()).removeCachedEntity(this.getPrimaryKey());
		}
		this.empty();
	}



	public void deleteMultiple(String columnName, String stringColumnValue) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = this.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + this.getEntityName() + " where " + columnName + "='" + stringColumnValue + "'");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				this.freeConnection(conn);
			}
		}
	}

	public void deleteMultiple(String columnName1, String stringColumnValue1, String columnName2, String stringColumnValue2) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = this.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + this.getEntityName() + " where " + columnName1 + "='" + stringColumnValue1 + "' and " + columnName2 + "='" + stringColumnValue2 + "'");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				this.freeConnection(conn);
			}
		}
	}

	/**
	 * Deletes everything from the table of this entity
	 */
	public void clear() throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = this.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + this.getEntityName());
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				this.freeConnection(conn);
			}
		}
		this.setEntityState(IDOLegacyEntity.STATE_DELETED);
	}

	/**
	 * *Function to populate a column through a string representation
	 */
	public void setStringColumn(String columnName, String columnValue) {
		int classType = getStorageClassType(columnName);
		if (classType == EntityAttribute.TYPE_JAVA_LANG_INTEGER) {
			if (columnValue != null) {
				setColumn(columnName, new Integer(columnValue), true);
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_LANG_STRING) {
			if (columnValue != null) {
				setColumn(columnName, columnValue, true);
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_LANG_BOOLEAN) {
			if (columnValue != null) {
				if (columnValue.equals("Y")) {
					setColumn(columnName, new Boolean(true), true);
				}
				else if (columnValue.equals("N")) {
					setColumn(columnName, new Boolean(false), true);
				}
				else {
					setColumn(columnName, new Boolean(false), true);
				}
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_LANG_FLOAT) {
			if (columnValue != null) {
				setColumn(columnName, new Float(columnValue), true);
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_LANG_DOUBLE) {
			if (columnValue != null) {
				setColumn(columnName, new Double(columnValue), true);
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Timestamp.valueOf(columnValue), true);
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_SQL_DATE) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Date.valueOf(columnValue), true);
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_UTIL_DATE) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Date.valueOf(columnValue), true);
			}
		}
		else if (classType == EntityAttribute.TYPE_JAVA_SQL_TIME) {
			if (columnValue != null) {
				setColumn(columnName, java.sql.Time.valueOf(columnValue), true);
			}
		}
		else if (classType == EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER) {
			if (columnValue != null) {
				setColumn(columnName, columnValue.toString(), true);
			}
		}
	}

	public void fillColumn(String columnName, ResultSet RS) throws SQLException {
		DatastoreInterface.getInstance(this).fillColumn(this, columnName, RS);
		/*
		 * int classType = getStorageClassType(columnName);
		 *
		 * if (classType==EntityAttribute.TYPE_JAVA_LANG_INTEGER){ //if
		 * (RS.getInt(columnName) != -1){ int theInt = RS.getInt(columnName);
		 * boolean wasNull = RS.wasNull(); if(!wasNull){ setColumn(columnName,new
		 * Integer(theInt)); //setColumn(columnName.toLowerCase(),new
		 * Integer(theInt)); }
		 *
		 * //} } else if (classType==EntityAttribute.TYPE_JAVA_LANG_STRING){ if
		 * (RS.getString(columnName) != null){
		 * setColumn(columnName,RS.getString(columnName)); }
		 *  } else if (classType==EntityAttribute.TYPE_JAVA_LANG_BOOLEAN){ String
		 * theString = RS.getString(columnName); if (theString != null){ if
		 * (theString.equals("Y")){ setColumn(columnName,new Boolean(true)); } else
		 * if (theString.equals("N")){ setColumn(columnName,new Boolean(false)); } } }
		 * else if (classType==EntityAttribute.TYPE_JAVA_LANG_FLOAT){ float theFloat =
		 * RS.getFloat(columnName); boolean wasNull = RS.wasNull(); if(!wasNull){
		 * setColumn(columnName,new Float(theFloat));
		 * //setColumn(columnName.toLowerCase(),new Float(theFloat)); }
		 *  } else if (classType==EntityAttribute.TYPE_JAVA_LANG_DOUBLE){ double
		 * theDouble = RS.getFloat(columnName); boolean wasNull = RS.wasNull();
		 * if(!wasNull){ setColumn(columnName,new Double(theDouble));
		 * //setColumn(columnName.toLowerCase(),new Double(theDouble)); }
		 *
		 * double doble = RS.getDouble(columnName); } else if
		 * (classType==EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP){ if
		 * (RS.getTimestamp(columnName) != null){
		 * setColumn(columnName,RS.getTimestamp(columnName)); } } else if
		 * (classType==EntityAttribute.TYPE_JAVA_SQL_DATE){ if
		 * (RS.getDate(columnName) != null){
		 * setColumn(columnName,RS.getDate(columnName)); } } else if
		 * (classType==EntityAttribute.TYPE_JAVA_SQL_TIME){ java.sql.Date date =
		 * RS.getDate(columnName); if (date != null){ setColumn(columnName,date);
		 * //setColumn(columnName.toLowerCase(),date); } } else if
		 * (classType==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){ //if
		 * (RS.getDate(columnName) != null){ //
		 * setColumn(columnName.toLowerCase(),RS.getTime(columnName)); //}
		 * setColumn(columnName,getEmptyBlob(columnName));
		 * //setColumn(columnName.toLowerCase(),getEmptyBlob(columnName));
		 *  } else if (classType==EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER){
		 * String gender = RS.getString(columnName); if (gender != null){
		 * setColumn(columnName,new Gender(gender));
		 * //setColumn(columnName.toLowerCase(),new Gender(gender));
		 *  } }
		 */
	}

	@Override
	public synchronized void ejbLoad() throws EJBException {
		try {
			if (this.getEntityState() != IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE) {
				Object pk = this.getPrimaryKey();
				/*
				 * if(pk instanceof Integer){
				 * findByPrimaryKey(((Integer)pk).intValue()); }
				 */
				if (pk == null) {
					throw new EJBException("Cannot load entity " + this.getClass().getName() + " for primary key null");
				}
				ejbLoad(pk);
				setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
			}
		}
		catch (SQLException e) {
			// e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
		catch (FinderException e) {
			// e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
	}

	/**
	 * To speed up loading when the ResultSet is already constructed
	 */
	public synchronized void preEjbLoad(ResultSet rs) throws EJBException {
		try {
			// Object pk = this.getPrimaryKey();
			if (rs != null) {
				this.loadFromResultSet(rs);
				setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
			}
		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}

	private void ejbLoad(Object pk) throws SQLException, FinderException {
		Connection conn = null;
		PreparedStatement Stmt = null;
		ResultSet RS = null;
		try {
			conn = getConnection(getDatasource());
			// Stmt = conn.createStatement();
			StringBuffer buffer = new StringBuffer();
			// buffer.append("select * from ");
			buffer.append("select ");
			// System.out.println("COLUMN NAMES :
			// "+DatastoreInterface.getCommaDelimitedColumnNamesForSelect(this));/**@is
			// this where it is supposed to be?**/
			DatastoreInterface dsi = DatastoreInterface.getInstance(this);
			buffer.append(dsi.getCommaDelimitedColumnNamesForSelect(this));
			buffer.append(" from "); // skips lob colums
			buffer.append(getEntityName());
			/*
			 * buffer.append(" where "); buffer.append(getIDColumnName());
			 * buffer.append("='"); buffer.append(pk.toString()); buffer.append("'");
			 */
			// dsi.appendPrimaryKeyWhereClause(this,buffer);
			IDOEntityField[] fields = getEntityDefinition().getPrimaryKeyDefinition().getFields();
			dsi.appendPrimaryKeyWhereClauseWithQuestionMarks(fields, buffer);
			String sql = buffer.toString();
			logSQL(sql);
			Stmt = conn.prepareStatement(sql);
			dsi.setForPreparedStatementPrimaryKeyQuestionValues(this, fields, Stmt, 1);
			RS = Stmt.executeQuery();
			// ResultSet RS = Stmt.executeQuery("select * from "+getTableName()+"
			// where "+getIDColumnName()+"="+id);
			// eiki added null check
			if ((RS == null) || !RS.next()) {
				throw new FinderException("Record with Primary Key = '" + pk + "' not found");
			}
			loadFromResultSet(RS);
		}
		finally {
			if (RS != null) {
				RS.close();
			}
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}

	/**
	 * @ deprecated Replaced with ejbLoad(Object value);
	 * @param id
	 * @throws SQLException
	 */
	public void findByPrimaryKey(int id) throws SQLException {
		setPrimaryKey(id);
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			StringBuffer buffer = new StringBuffer();
			// buffer.append("select * from ");
			buffer.append("select ");
			// System.out.println("COLUMN NAMES :
			// "+DatastoreInterface.getCommaDelimitedColumnNamesForSelect(this));/**@is
			// this where it is supposed to be?**/
			buffer.append(DatastoreInterface.getInstance(this).getCommaDelimitedColumnNamesForSelect(this));
			buffer.append(" from "); // skips lob colums
			buffer.append(getEntityName());
			buffer.append(" where ");
			buffer.append(getIDColumnName());
			buffer.append("=");
			buffer.append(id);
			ResultSet RS = Stmt.executeQuery(buffer.toString());
			// ResultSet RS = Stmt.executeQuery("select * from "+getTableName()+"
			// where "+getIDColumnName()+"="+id);
			// eiki added null check
			if ((RS == null) || !RS.next()) {
				throw new SQLException("Record with id=" + id + " not found");
			}
			loadFromResultSet(RS);
			if (RS != null) {
				RS.close();
			}
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}

	<PK> PK getPrimaryKeyFromResultSet(ResultSet rs) throws SQLException {
		IDOEntityField[] fields = getGenericEntityDefinition().getPrimaryKeyDefinition().getFields();
		Class<PK> primaryKeyClass = getPrimaryKeyClass();
		return getPrimaryKeyFromResultSet(primaryKeyClass, fields, rs);
	}

	@SuppressWarnings("unchecked")
	<PK> PK getPrimaryKeyFromResultSet(Class<PK> primaryKeyClass, IDOEntityField[] primaryKeyFields, ResultSet rs) throws SQLException {
		IDOEntityField[] fields = primaryKeyFields;
		Class<PK> pkClass = primaryKeyClass;
		PK theReturn = null;

		if (pkClass == Integer.class) {
			theReturn = (PK) new Integer(rs.getInt(this.getIDColumnName()));
		}
		else {
			try {
				theReturn = (PK) getPrimaryKeyClass().newInstance();
			}
			catch (InstantiationException e1) {
				e1.printStackTrace();
			}
			catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}

			if (theReturn instanceof String) {
				theReturn = (PK) rs.getString(getIDColumnName());
			}
			else {
				if (theReturn instanceof IDOPrimaryKey) {
					IDOPrimaryKey primaryKey = (IDOPrimaryKey) theReturn;
					for (int i = 0; i < fields.length; i++) {
						Object value = rs.getObject(fields[i].getSQLFieldName());
						primaryKey.setPrimaryKeyValue(fields[i].getSQLFieldName(), value);
					}
					return (PK) primaryKey;
				}
			}
		}
		if (rs.wasNull()) {
			return null;
		}
		return theReturn;
	}

	private void loadFromResultSet(ResultSet RS) {
		String[] columnNames = getColumnNames();
		for (int i = 0; i < columnNames.length; i++) {
			try {
				// if (RS.getString(columnNames[i]) != null){
				fillColumn(columnNames[i], RS);
				// }
			}
			catch (Exception ex) {
				/*
				 * //NOCATH try{ //if (RS.getString(columnNames[i].toUpperCase()) !=
				 * null){ fillColumn(columnNames[i],RS); //} } catch(SQLException exe){
				 * try{ //if (RS.getString(columnNames[i].toLowerCase()) != null){
				 * fillColumn(columnNames[i],RS); //} } catch(SQLException exep){
				 *
				 * System.err.println("Exception in "+this.getClass().getName()+"
				 * findByPrimaryKey, RS.getString( "+columnNames[i]+" ) not found:
				 * "+exep.getMessage()); //exep.printStackTrace(System.err); } }
				 */
				System.err.println("Exception in " + this.getClass().getName() + " findByPrimaryKey, RS.getString( " + columnNames[i] + " ) not found: " + ex.getMessage());
				if (!(ex instanceof NullPointerException)) {
					ex.printStackTrace(System.err);
				}
			}
		}
	}

	public String getNameOfMiddleTable(IDOEntity entity1, IDOEntity entity2) {
		return EntityControl.getNameOfMiddleTable(entity1, entity2);
	}

	/**
	 * @deprecated replaced with idoGetRelated
	 */
	@Deprecated
	public IDOLegacyEntity[] findRelated(IDOLegacyEntity entity) throws SQLException {
		return findRelated(entity, "", "");
	}

	/**
	 * @deprecated replaced with idoGetRelated
	 */
	@Deprecated
	public int[] findRelatedIDs(IDOLegacyEntity entity) throws SQLException {
		return findRelatedIDs(entity, "", "");
	}

	private String getFindRelatedSQLQuery(IDOEntity entity, String entityColumnName, String entityColumnValue) {
		return getFindRelatedSQLQuery(entity, entityColumnName, entityColumnValue, null);
	}

	private String getFindRelatedSQLQuery(IDOEntity entity, String entityColumnName, String entityColumnValue, String orderByColumnName) {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		String primaryValue = getPrimaryKeyValueSQLString(); // eiki added for
																													// string primary key
																													// support
		String entityIDColumnName = null;

		try {
			entityIDColumnName = entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
		}

		StringBuffer buffer = new StringBuffer();
		// Optimization by Sigtryggur 23.06.05
		buffer.append("select e." + entityIDColumnName + " from ");
		buffer.append(tableToSelectFrom + " middle, " + entity.getEntityDefinition().getSQLTableName() + " e");
		buffer.append(" where ");

		if (isColumnValueNotEmpty(primaryValue)) {
			buffer.append("middle." + this.getIDColumnName());
			buffer.append("=");
			buffer.append(primaryValue);
			buffer.append(" and ");
		}

		buffer.append(" middle." + entityIDColumnName);
		buffer.append("=");
		buffer.append("e." + entityIDColumnName);

		primaryValue = GenericEntity.getKeyValueSQLString(entity.getPrimaryKey());

		if (isColumnValueNotEmpty(primaryValue)) {
			buffer.append(" and ");
			buffer.append("middle." + entityIDColumnName);
			buffer.append("=");
			buffer.append(primaryValue);
		}

		if (entityColumnName != null) {
			if (!entityColumnName.equals("")) {
				buffer.append(" and ");
				buffer.append("e." + entityColumnName);
				if (entityColumnValue != null) {
					buffer.append(" = ");
					buffer.append("'" + entityColumnValue + "'");
				}
				else {
					buffer.append(" is null");
				}
			}
		}
		if (orderByColumnName != null) {
			if (!orderByColumnName.equals("")) {
				buffer.append(" order by e.");
				buffer.append(orderByColumnName);
			}
		}
		String SQLString = buffer.toString();
		return SQLString;
	}

	protected String getFindReverseRelatedSQLQuery(IDOEntity entity, String entityColumnName, String entityColumnValue) {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		String primaryValue = getPrimaryKeyValueSQLString();

		StringBuffer buffer = new StringBuffer();
		buffer.append("select e.* from ");
		buffer.append(tableToSelectFrom + " middle, " + this.getEntityName() + " e");
		buffer.append(" where ");

		if (isColumnValueNotEmpty(primaryValue)) {
			buffer.append("middle." + this.getIDColumnName());
			buffer.append("=");
			buffer.append(primaryValue);
			buffer.append(" and ");
		}

		buffer.append(" middle." + this.getIDColumnName());
		buffer.append("=");
		buffer.append("e." + this.getIDColumnName());

		primaryValue = GenericEntity.getKeyValueSQLString(entity.getPrimaryKey());

		try {
			if (isColumnValueNotEmpty(primaryValue)) {
				buffer.append(" and ");
				buffer.append("middle." + entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName());
				buffer.append("=");
				buffer.append(primaryValue);
			}
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			return null;
		}

		if (entityColumnName != null) {
			if (!entityColumnName.equals("")) {
				buffer.append(" and ");
				buffer.append("e." + entityColumnName);
				if (entityColumnValue != null) {
					buffer.append(" = ");
					buffer.append("'" + entityColumnValue + "'");
				}
				else {
					buffer.append(" is null");
				}
			}
		}
		String SQLString = buffer.toString();
		return SQLString;
	}

	/**
	 * @deprecated replaced with idoGetRelated()
	 */
	@Deprecated
	public IDOLegacyEntity[] findRelated(IDOLegacyEntity entity, String entityColumnName, String entityColumnValue) throws SQLException {

		String SQLString = this.getFindRelatedSQLQuery(entity, entityColumnName, entityColumnValue);
		return findRelated(entity, SQLString);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findReverseRelated(IDOLegacyEntity entity) throws SQLException {
		return findRelated(entity);
	}

	/**
	 * @deprecated replaced with idoGetRelated
	 */
	@Deprecated
	protected IDOLegacyEntity[] findRelated(IDOLegacyEntity entity, String SQLString) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		Collection<IDOLegacyEntity> results = new ArrayList<IDOLegacyEntity>();
		/*
		 * String tableToSelectFrom = ""; if (entity.getEntityName().endsWith("_")) {
		 * tableToSelectFrom = entity.getEntityName() + this.getEntityName(); } else {
		 * tableToSelectFrom = entity.getEntityName() + "_" + this.getEntityName(); }
		 */

		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			while (RS.next()) {
				IDOLegacyEntity tempobj = null;
				try {
					Class<?> relatedClass = entity.getClass();
					tempobj = this.findByPrimaryInOtherClass(relatedClass, RS.getInt(entity.getIDColumnName()));
				}
				catch (Exception ex) {
					System.err.println("There was an error in com.idega.data.GenericEntity.findRelated(IDOLegacyEntity entity,String SQLString): " + ex.getMessage());
				}
				results.add(tempobj);
			}
			RS.close();
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}

		return (IDOLegacyEntity[]) results.toArray((Object[]) Array.newInstance(entity.getClass(), 0));
	}

	/**
	 * @deprecated replaced with idoGetRelatedPKs
	 */
	@Deprecated
	public int[] findRelatedIDs(IDOLegacyEntity entity, String entityColumnName, String entityColumnValue) throws SQLException {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		StringBuffer buffer = new StringBuffer();
		buffer.append("select e.* from ");
		buffer.append(tableToSelectFrom + " middle, " + entity.getEntityName() + " e");
		buffer.append(" where ");
		buffer.append("middle." + this.getIDColumnName());
		buffer.append("=");
		// buffer.append(this.getID());
		buffer.append(getPrimaryKeyValueSQLString());
		buffer.append(" and ");
		buffer.append("middle." + entity.getIDColumnName());
		buffer.append("=");
		buffer.append("e." + entity.getIDColumnName());

		// /if (entity.getID() != -1)
		if (isColumnValueNotEmpty(getKeyValueSQLString(entity.getPrimaryKeyValue()))) {
			buffer.append(" and ");
			buffer.append("middle." + entity.getIDColumnName());
			buffer.append("=");
			// buffer.append(entity.getID());
			buffer.append(getKeyValueSQLString(entity.getPrimaryKeyValue()));
		}

		if (entityColumnName != null) {
			if (!entityColumnName.equals("")) {
				buffer.append(" and ");
				buffer.append("e." + entityColumnName);
				if (entityColumnValue != null) {
					buffer.append(" = ");
					buffer.append("'" + entityColumnValue + "'");
				}
				else {
					buffer.append(" is null");
				}
			}
		}
		String SQLString = buffer.toString();
		return findRelatedIDs(entity, SQLString);
	}

	/**
	 * @deprecated replaced with idoGetRelatedPKs
	 */
	@Deprecated
	protected int[] findRelatedIDs(IDOLegacyEntity entity, String SQLString) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		int[] toReturn = null;
		int length;
		Collection<Integer> results = new ArrayList<Integer>();
		/*
		 * String tableToSelectFrom = ""; if (entity.getEntityName().endsWith("_")) {
		 * tableToSelectFrom = entity.getEntityName() + this.getEntityName(); } else {
		 * tableToSelectFrom = entity.getEntityName() + "_" + this.getEntityName(); }
		 */
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet rs = Stmt.executeQuery(SQLString);
			length = 0;
			while (rs.next()) {
				try {
					results.add(((Number) rs.getObject(entity.getIDColumnName())).intValue());
					length++;
				} catch (Exception ex) {
					System.err.println("There was an error in com.idega.data.GenericEntity.findRelatedIDs(IDOLegacyEntity entity,String SQLString): " + ex.getMessage());
				}
			}
			rs.close();
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		if (length > 0) {
			toReturn = new int[length];
			int index = 0;
			for (Iterator<Integer> iter = results.iterator(); iter.hasNext();) {
				Integer item = iter.next();
				toReturn[index++] = item.intValue();
			}
		}
		else {
			toReturn = new int[0];
		}
		return toReturn;
	}

	/**
	 * Finds all instances of the current object in the otherEntity
	 */
	@SuppressWarnings("deprecation")
	public IDOLegacyEntity[] findAssociated(IDOLegacyEntity otherEntity) throws SQLException {
		return otherEntity.findAll("select * from " + otherEntity.getEntityName() + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString());
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAssociatedOrdered(IDOLegacyEntity otherEntity, String column_name) throws SQLException {
		return otherEntity.findAll("select * from " + otherEntity.getEntityName() + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString() + " order by " + column_name);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAll() throws SQLException {
		return findAll("select * from " + getEntityName());
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllOrdered(String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName, String toFind, String orderByColumnName, String condition) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " " + condition + " '" + toFind + "' order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName, String toFind, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " like '" + toFind + "' order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnEqualsOrdered(String columnName, String toFind, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " = '" + toFind + "' order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName, String condition1, String condition2) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " " + condition1 + " '" + toFind1 + "' and " + columnName2 + " " + condition2 + " '" + toFind2 + "' order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "' order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnEqualsOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " = '" + toFind1 + "' and " + columnName2 + " = '" + toFind2 + "' order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnEqualsOrdered(String columnName1, int toFind1, String columnName2, int toFind2, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " = " + toFind1 + " and " + columnName2 + " = " + toFind2 + " order by " + orderByColumnName);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnDescendingOrdered(String columnName, String toFind, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " like '" + toFind + "' order by " + orderByColumnName + " desc");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnDescendingOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "' order by " + orderByColumnName + " desc");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllDescendingOrdered(String orderByColumnName) throws SQLException {
		return findAll("select * from " + getEntityName() + " order by " + orderByColumnName + " desc");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName, String toFind, String condition) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " " + condition + " '" + toFind + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName1, String toFind1, char condition1, String columnName2, String toFind2, char condition2) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " " + String.valueOf(condition1) + " '" + toFind1 + "' and " + columnName2 + " " + String.valueOf(condition2) + " '" + toFind2 + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName, String toFind) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " like '" + toFind + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnEquals(String columnName, String toFind) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " = '" + toFind + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName, int toFind) throws SQLException {
		return findAllByColumn(columnName, Integer.toString(toFind));
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnEquals(String columnName, int toFind) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName + " = " + toFind + "");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName1, String toFind1, String columnName2, String toFind2, String columnName3, String toFind3) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "' and " + columnName3 + " like '" + toFind3 + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnEquals(String columnName1, String toFind1, String columnName2, String toFind2, String columnName3, String toFind3) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " = '" + toFind1 + "' and " + columnName2 + " = '" + toFind2 + "' and " + columnName3 + " = '" + toFind3 + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName1, String toFind1, String columnName2, String toFind2) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " like '" + toFind1 + "' and " + columnName2 + " like '" + toFind2 + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnEquals(String columnName1, String toFind1, String columnName2, String toFind2) throws SQLException {
		return findAll("select * from " + getEntityName() + " where " + columnName1 + " = '" + toFind1 + "' and " + columnName2 + " = '" + toFind2 + "'");
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public int getNumberOfRecords(String columnName, String columnValue) throws SQLException {
		return getNumberOfRecords("select count(*) from " + getEntityName() + " where " + columnName + " like '" + columnValue + "'");
	}

	public int getNumberOfRecords(String columnName, int columnValue) throws SQLException {
		// return getNumberOfRecords("select count(*) from " + getEntityName() + "
		// where " + columnName + " = " + columnValue);
		return getNumberOfRecords(new MatchCriteria(this.idoQueryTable, columnName, MatchCriteria.EQUALS, columnValue));
	}

	public int getNumberOfRecordsRelated(IDOLegacyEntity entity) throws SQLException {
		String tableToSelectFrom = getNameOfMiddleTable(entity, this);
		String SQLString = "select count(*) from " + tableToSelectFrom + " where " + this.getIDColumnName() + "=" + getPrimaryKeyValueSQLString();
		// System.out.println(SQLString);
		return getNumberOfRecords(SQLString);
	}

	public int getNumberOfRecordsReverseRelated(IDOLegacyEntity entity) throws SQLException {
		String tableToSelectFrom = getNameOfMiddleTable(this, entity);
		String SQLString = "select count(*) from " + tableToSelectFrom + " where " + this.getIDColumnName() + "=" + getPrimaryKeyValueSQLString();
		// System.out.println(SQLString);
		return getNumberOfRecords(SQLString);
	}

	public int getNumberOfRecords() throws SQLException {
		// return getNumberOfRecords("select count(*) from " + getEntityName());
		return getNumberOfRecords((Criteria) null);
	}

	public int getNumberOfRecords(String CountSQLString) throws SQLException {
		return getIntTableValue(CountSQLString);
	}

	public int getNumberOfRecords(SelectQuery query) throws SQLException {
		return getIntTableValue(query);
	}

	public double getAverage(String averageSQLString) throws SQLException {
		return getDoubleTableValue(averageSQLString);
	}

	/**
	 * Checks if the value of the specified columnName in the database is null.
	 * Use this method for columns where the storage type is a blob, because even
	 * if the value in the database is null, the value in the _columns map is a
	 * BlobWrapper, that is the method isNull(columnName) returns false. Why don'
	 * t we solve the problem by changing the method isNull(columnName)? Because
	 * you can't check if the value is null without starting to read the
	 * inputstream, that is after checking you have to reset the internal stream
	 * of the BlobWrapper. In order to avoid this we check the value directly in
	 * the database via a sql statement. Keep in mind that the BlobWrapper has to
	 * execute a sql statement to get a stream of the value, that is the statement
	 * of the method isStoredValueNull is even faster.
	 *
	 * @author thomas
	 */
	protected boolean isStoredValueNull(String columnName) {
		IDOQuery query = idoQuery();
		query.appendSelectCountFrom(this).appendWhereEquals(getIDColumnName(), getPrimaryKeyValueSQLString());
		query.appendAndIsNotNull(columnName);
		// number of records is either zero or one
		try {
			int numberOfRecords = getNumberOfRecords(query.toString());
			return (numberOfRecords == 0);
		}
		catch (SQLException sqlEx) {
			logError("[GenericEntity] Could not check the following column " + columnName);
			log(sqlEx);
			return true;
		}
	}

	protected double idoGetValueFromSingleValueResultSet(IDOQuery query) throws IDOException {
		return idoGetValueFromSingleValueResultSet(query.toString());
	}

	protected double idoGetValueFromSingleValueResultSet(String sqlString) throws IDOException {
		try {
			if (isDebugActive()) {
				logSQL(sqlString);
			}
			return this.getDoubleTableValue(sqlString);
		}
		catch (SQLException e) {
			throw new IDOException(e, this);
		}
	}

	public int getNumberOfRecordsForStringColumn(String columnName, String operator, String columnValue) throws SQLException {
		// StringBuffer buffer = new StringBuffer("\'");
		// buffer.append(columnValue);
		// buffer.append('\'');
		return getNumberOfRecords(columnName, operator, columnValue);
	}

	public int getNumberOfRecords(String columnName, String operator, String columnValue) throws SQLException {
		return getNumberOfRecords(new MatchCriteria(idoQueryTable(), columnName, operator, columnValue));

		// StringBuffer buffer = new StringBuffer("select count(*) from ");
		// buffer.append(getEntityName()).append(" where
		// ").append(columnName).append(" ").append(operator).append("
		// ").append(columnValue);
		// return getNumberOfRecords(buffer.toString());
	}

	private int getNumberOfRecords(Criteria criteria) throws SQLException {
		SelectQuery query = new SelectQuery(idoQueryTable());
		query.addColumn(new Column(idoQueryTable(), getIDColumnName()));
		query.setAsCountQuery(true);
		if (criteria != null) {
			query.addCriteria(criteria);
		// query.addCriteria(new
		// MatchCriteria(idoQueryTable(),columnName,operator,columnValue,));
		}

		// StringBuffer buffer = new StringBuffer("select count(*) from ");
		// buffer.append(getEntityName()).append(" where
		// ").append(columnName).append(" ").append(operator).append("
		// ").append(columnValue);
		// return getNumberOfRecords(buffer.toString());
		return getNumberOfRecords(query);
	}

	public int getMaxColumnValue(String columnName) throws SQLException {
		return getIntTableValue("select max(" + columnName + ") from " + getEntityName());
	}

	public int getMaxColumnValue(String columnToGetMaxFrom, String columnCondition, String columnConditionValue) throws SQLException {
		return getIntTableValue("select max(" + columnToGetMaxFrom + ") from " + getEntityName() + " where " + columnCondition + " = '" + columnConditionValue + "'");
	}

	public int getIntTableValue(String CountSQLString) throws SQLException {
		return getIntTableValue(CountSQLString, null);
	}

	public int getIntTableValue(SelectQuery query) throws SQLException {
		return getIntTableValue(null, query);
	}

	private int getIntTableValue(String CountSQLString, SelectQuery query) throws SQLException {
		Connection conn = null;
		// Statement stmt = null;
		// ResultSet rs = null;
		ResultHelper rsh = null;
		int recordCount = -1;
		try {
			conn = getConnection(this.getDatasource());
			rsh = prepareResultSet(conn, CountSQLString, query);
			if (rsh.rs.next()) {
				recordCount = rsh.rs.getInt(1);
			// rs.close();
			// System.out.println(SQLString+"\n");
			}
		}
		catch (SQLException e) {
			throw new SQLException("There was an error in com.idega.data.GenericEntity.getNumberOfRecords \n" + e.getMessage());
		}
		catch (Exception e) {
			System.err.println("There was an error in com.idega.data.GenericEntity.getNumberOfRecords " + e.getMessage());
		}
		finally {
			if (rsh != null) {
				rsh.close();
			}
			/*
			 * if (stmt != null) { stmt.close(); }
			 */
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return recordCount;
	}

	public Date getDateTableValue(String dateSQLString) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Date date = null;
		try {
			conn = getConnection(this.getDatasource());
			stmt = conn.createStatement();
			rs = stmt.executeQuery(dateSQLString);
			if (rs.next()) {
				date = new Date(rs.getTimestamp(1).getTime());
			}
			rs.close();
			// System.out.println(SQLString+"\n");
		}
		catch (SQLException e) {
			throw new SQLException("There was an error in com.idega.data.GenericEntity.getDateTableValue \n" + e.getMessage());
		}
		catch (Exception e) {
			System.err.println("There was an error in com.idega.data.GenericEntity.getDateTableValue " + e.getMessage());
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return date;
	}

	public double getDoubleTableValue(String sqlString) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		double value = 0;
		try {
			conn = getConnection(this.getDatasource());
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString);
			if (rs.next()) {
				value = rs.getDouble(1);
			}
			rs.close();
			// System.out.println(SQLString+"\n");
		}
		catch (SQLException e) {
			throw new SQLException("There was an error in com.idega.data.GenericEntity.getDoubleTableValue \n" + e.getMessage());
		}
		catch (Exception e) {
			System.err.println("There was an error in com.idega.data.GenericEntity.getDoubleTableValue " + e.getMessage());
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return value;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAll(String SQLString) throws SQLException {
		// System.out.println(SQLString);
		return findAll(SQLString, -1);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public IDOLegacyEntity[] findAll(String SQLString, int returningNumberOfRecords) throws SQLException {
		// System.err.println("com.idega.data.GenericEntity.findAll(\""+SQLString+"\");");
		/*
		 * Connection conn= null; Statement Stmt= null; ResultSetMetaData metaData;
		 * Vector vector = new Vector(); boolean check=true; //Vector theIDs = new
		 * Vector(); try{ conn = getConnection(getDatasource()); Stmt =
		 * conn.createStatement(); ResultSet RS = Stmt.executeQuery(SQLString);
		 * metaData = RS.getMetaData(); int count = 1; while (RS.next() && check){
		 * count++; if(returningNumberOfRecords!=-1){
		 * if(count>returningNumberOfRecords){ check=false; } }
		 *
		 * IDOLegacyEntity tempobj=null; try{ tempobj =
		 * (IDOLegacyEntity)Class.forName(this.getClass().getName()).newInstance(); }
		 * catch(Exception ex){ System.err.println("There was an error in
		 * com.idega.data.GenericEntity.findAll "+ex.getMessage());
		 * ex.printStackTrace(System.err); } if(tempobj != null){ for (int i = 1; i <=
		 * metaData.getColumnCount(); i++){
		 *
		 *
		 * if ( RS.getObject(metaData.getColumnName(i)) != null){
		 *
		 * //System.out.println("ColumName "+i+": "+metaData.getColumnName(i));
		 * tempobj.fillColumn(metaData.getColumnName(i),RS); } }
		 *  } vector.addElement(tempobj);
		 *  } RS.close();
		 *  } finally{ if(Stmt != null){ Stmt.close(); } if (conn != null){
		 * freeConnection(getDatasource(),conn); } } /* for (Enumeration enum =
		 * theIDs.elements();enum.hasMoreElements();){ Integer tempInt = (Integer)
		 * enum.nextElement(); vector.addElement(new
		 * IDOLegacyEntity(tempInt.intValue())); }
		 */
		this.logSQL(SQLString);
		List list = EntityFinder.findAll((IDOLegacyEntity) this, SQLString, returningNumberOfRecords);
		if (list != null) {
			return (IDOLegacyEntity[]) list.toArray((Object[]) Array.newInstance(this.getClass(), 0));
			// return vector.toArray(new IDOLegacyEntity[0]);
		}
		else {
			// Provided for backwards compatability where there was almost never
			// returned null if
			// there was nothing found
			return (IDOLegacyEntity[]) Array.newInstance(this.getClass(), 0);
		}
	}

	/**
	 * @deprecated Replaced with idoAddTo
	 */
	@Deprecated
	public void addTo(IDOLegacyEntity entityToAddTo) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			// String sql = "insert into
			// "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+")
			// values("+getID()+","+entityToAddTo.getID()+")";
			String sql = null;
			// try
			// {
			sql = "insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")";
			/*
			 * } catch (RemoteException rme) { throw new SQLException("RemoteException
			 * in addTo, message: " + rme.getMessage()); }
			 */

			// debug("statement: "+sql);
			Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * Default move behavior with a tree relationship
	 */
	public void moveChildrenToCurrent(IDOLegacyEntity entityFrom, String entityFromColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "update " + getNameOfMiddleTable(entityFrom, this) + " set " + getIDColumnName() + " = " + getPrimaryKeyValueSQLString() + " where " + getIDColumnName() + " = " + getKeyValueSQLString(entityFrom.getPrimaryKeyValue());
			Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * Default relationship adding behavior with a many-to-many relationship
	 */
	public void addTo(IDOLegacyEntity entityToAddTo, String entityToAddToColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddToColumName + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")";
			Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	public void addToTree(IDOLegacyEntity entityToAddTo, String entityToAddToColumName, String middleTableName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "insert into " + middleTableName + "(" + getIDColumnName() + "," + entityToAddToColumName + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")";
			logSQL(sql);
			Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * Default delete behavior with a tree relationship
	 */
	public void removeFrom(IDOLegacyEntity entityToDelete, String entityToDeleteColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "delete from " + getNameOfMiddleTable(entityToDelete, this) + " where " + entityToDeleteColumName + " = " + getKeyValueSQLString(entityToDelete.getPrimaryKeyValue());
			logSQL(sql);
			Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * *Default insert behavior with a many-to-many relationship and
	 * EntityBulkUpdater
	 */
	public void addTo(IDOLegacyEntity entityToAddTo, Connection conn) throws SQLException {
		Statement Stmt = null;
		try {
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName() + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ")");
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
		}
	}

	/**
	 * Attention: Beta implementation
	 */
	public void addTo(Class entityToAddTo, int id) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(com.idega.data.GenericEntity.getStaticInstanceIDO(entityToAddTo), this) + "(" + getIDColumnName() + "," + (com.idega.data.GenericEntity.getStaticInstanceIDO(entityToAddTo)).getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName() + ") values(" + getPrimaryKeyValueSQLString() + "," + id + ")");
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * Attention: Beta implementation
	 */
	public void addTo(Class entityToAddTo, int[] ids) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String middleTable = getNameOfMiddleTable(getStaticInstanceIDO(entityToAddTo), this);
			String columnName = (getStaticInstanceIDO(entityToAddTo)).getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
			if (ids != null) {
				for (int i = 0; i < ids.length; i++) {
					try {
						Stmt.executeUpdate("insert into " + middleTable + "(" + getIDColumnName() + "," + columnName + ") values(" + getPrimaryKeyValueSQLString() + "," + ids[i] + ")");
					}
					finally {
					}
				}
			}
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	public void addTo(IDOLegacyEntity entityToAddTo, String extraColumnName, String extraColumnValue) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + "," + extraColumnName + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ",'" + extraColumnValue + "')");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	public void addTo(IDOLegacyEntity entityToAddTo, String extraColumnName, String extraColumnValue, String extraColumnName1, String extraColumnValue1) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + "," + extraColumnName + "," + extraColumnName1 + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ",'" + extraColumnValue + "','" + extraColumnValue1 + "')");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	public void addTo(IDOLegacyEntity entityToAddTo, String extraColumnName, String extraColumnValue, String extraColumnName1, String extraColumnValue1, String extraColumnName2, String extraColumnValue2) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			Stmt.executeUpdate("insert into " + getNameOfMiddleTable(entityToAddTo, this) + "(" + getIDColumnName() + "," + entityToAddTo.getIDColumnName() + "," + extraColumnName + "," + extraColumnName1 + "," + extraColumnName2 + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(entityToAddTo.getPrimaryKeyValue()) + ",'" + extraColumnValue + "','" + extraColumnValue1 + "','" + extraColumnValue2 + "')");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * @deprecated Replaced with idoRemoveFrom
	 */
	@Deprecated
	public void removeFrom(IDOLegacyEntity entityToRemoveFrom) throws SQLException {
		removeFrom((IDOEntity) entityToRemoveFrom);
	}

	/**
	 *
	 * <p>Removes all records from related entity, where records are related
	 * to current instance of entity.</p>
	 * @param relatedTableName is table name to remove from,
	 * not <code>null</code>;
	 * @return <code>true</code> when successfully removed, <code>false</code>
	 * otherwise;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean removeFrom(String relatedTableName) {
		if (StringUtil.isEmpty(relatedTableName)) {
			return Boolean.FALSE;
		}

		/* Creating query */
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM ").append(relatedTableName).append(CoreConstants.SPACE)
		.append("WHERE ").append(getIDColumnName()).append(CoreConstants.EQ).append(getPrimaryKeyValueSQLString());

		return executeUpdate(query.toString());
	}

	private void removeFrom(IDOEntity entityToRemoveFrom, String middleTableName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		String qry = "";
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			// try
			// {
			if (!isColumnValueNotEmpty(getKeyValueSQLString(entityToRemoveFrom.getPrimaryKey()))) {
				// all
																																														// in
																																														// middle
																																														// table
				qry = "delete from " + middleTableName + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString();
			}
			else {
				// just removing this particular one
				qry = "delete from " + middleTableName + " where " + this.getIDColumnName() + "=" + getPrimaryKeyValueSQLString() + " AND " + entityToRemoveFrom.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName() + "= " + getKeyValueSQLString(entityToRemoveFrom.getPrimaryKey());
			}
			// }
			/*
			 * catch (RemoteException rme) { throw new SQLException("RemoteException
			 * in removeFrom, message: " + rme.getMessage()); }
			 */
			// System.out.println("GENERIC ENTITY: "+ qry);
			logSQL(qry);
			Stmt.executeUpdate(qry);
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
		}
		catch (EJBException e) {
			e.printStackTrace();
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	private void removeFrom(IDOEntity entityToRemoveFrom) throws SQLException {
		removeFrom(entityToRemoveFrom, getNameOfMiddleTable(entityToRemoveFrom, this));
	}

	/**
	 * Attention: Beta implementation
	 */
	public void removeFrom(Class entityToRemoveFrom, int id) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		String qry = "";
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			qry = "delete from " + getNameOfMiddleTable(com.idega.data.GenericEntity.getStaticInstance(entityToRemoveFrom), this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString() + " AND " + com.idega.data.GenericEntity.getStaticInstance(entityToRemoveFrom).getIDColumnName() + "='" + id + "'";
			// System.out.println("GENERIC ENTITY: "+ qry);
			Stmt.executeUpdate(qry);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * @deprecated Replaced with idoRemoveFrom
	 */
	@Deprecated
	public void removeFrom(Class entityToRemoveFrom) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		String qry = "";
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			qry = "delete from " + getNameOfMiddleTable(getStaticInstanceIDO(entityToRemoveFrom), this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString();

			// System.out.println("GENERIC ENTITY: "+ qry);
			Stmt.executeUpdate(qry);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	/**
	 * *Default remove behavior with a many-to-many relationship * deletes only
	 * one line in middle table if the genericentity wa consructed with a value *
	 * Takes in a connection but does not close it.
	 */
	public void removeFrom(IDOLegacyEntity entityToRemoveFrom, Connection conn) throws SQLException {
		Statement Stmt = null;
		String qry = "";
		try {
			Stmt = conn.createStatement();
			if (isColumnValueNotEmpty(getKeyValueSQLString(entityToRemoveFrom.getPrimaryKeyValue()))) {
				// all
																																																// in
																																																// middle
																																																// table
				qry = "delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString();
			}
			else {
				// just removing this particular one
				qry = "delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + this.getIDColumnName() + "= " + getPrimaryKeyValueSQLString() + " AND " + entityToRemoveFrom.getIDColumnName() + "= " + getKeyValueSQLString(entityToRemoveFrom.getPrimaryKeyValue());
			}

			// System.out.println("GENERIC ENTITY: "+ qry);
			logSQL(qry);
			Stmt.executeUpdate(qry);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
		}
	}

	public void removeFrom(IDOLegacyEntity[] entityToRemoveFrom) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String idColumnName = this.getIDColumnName();
			String id = getPrimaryKeyValueSQLString();
			//int count = 0;
			for (int i = 0; i < entityToRemoveFrom.length; i++) {
				String sql1 = "delete from " + getNameOfMiddleTable(entityToRemoveFrom[i], this) + " where " + idColumnName + "= " + id;
				logSQL(sql1);
				/*count += */Stmt.executeUpdate(sql1);
				if (!isColumnValueNotEmpty(getKeyValueSQLString(entityToRemoveFrom[i].getPrimaryKeyValue()))) {
					// removing all in middle table
					String sql2 = "delete from " + getNameOfMiddleTable(entityToRemoveFrom[i], this) + " where " + idColumnName + "= " + id;
					logSQL(sql2);
					/*count += */Stmt.executeUpdate(sql2);
				}
				else {
					// just removing this particular one
					String sql2 = "delete from " + getNameOfMiddleTable(entityToRemoveFrom[i], this) + " where " + idColumnName + "= " + id + " AND " + entityToRemoveFrom[i].getIDColumnName() + "= " + getKeyValueSQLString(entityToRemoveFrom[i].getPrimaryKeyValue());
					logSQL(sql2);
					/*count += */Stmt.executeUpdate(sql2);
				}
			}
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	public void reverseRemoveFrom(IDOLegacyEntity entityToRemoveFrom) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			String sql = "delete from " + getNameOfMiddleTable(entityToRemoveFrom, this) + " where " + entityToRemoveFrom.getIDColumnName() + "= " + getKeyValueSQLString(entityToRemoveFrom.getPrimaryKeyValue());
			logSQL(sql);
			Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
	}

	@Override
	public int compareTo(IDOEntity entity) {
		Collator coll = Collator.getInstance();
		return coll.compare(this.getPrimaryKey().toString(), entity.getPrimaryKey().toString());
	}

	/**
	 * This method just calls equals(IDOEntity) by casting obj to IDOEntity
	 */
	@Override
	public boolean equals(Object obj) {

		boolean isEqual = false;

		try {
			isEqual = equals((IDOEntity) obj);
		}
		catch (ClassCastException e) {
			// the user is comparing apples to oranges of course they are not equal
			// and this is an error too!
			e.printStackTrace();
			return false;
		}
		return isEqual;
	}

	/**
	 * The method returns true if the entity primary keys match
	 *
	 * @param entity
	 * @return
	 */
	public boolean equals(IDOEntity entity) {
		if (entity != null) {
			if (entity.getEntityDefinition().getSQLTableName().equalsIgnoreCase(this.getEntityDefinition().getSQLTableName())) {
				String entityPK = String.valueOf(entity.getPrimaryKey());
				if (entityPK != null && entityPK.equals(String.valueOf(this.getPrimaryKey()))) {
					return true;
				}
				return false;
			}
			return false;
		}
		return false;
	}

	public void empty() {
		this._columns.clear();
	}

	public boolean hasLobColumn() throws Exception {
		String lobColumnName = this.getLobColumnName();
		// String lobColumnName = this.getStaticInstance()._lobColumnName;
		if (lobColumnName == null) {
			return false;
		}
		return true;
	}

	private void setLobColumnName(String lobColumnName) {
		this.getIDOEntityStaticInstance()._lobColumnName = lobColumnName;
	}

	private void setLobColumnName() {
		if (getLobColumnName() == null) {
			String[] columnNames = this.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				if (EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER == this.getStorageClassType(columnNames[i])) {
					setLobColumnName(columnNames[i]);
				}
			}
		}
	}

	public String getLobColumnName() {
		return this.getIDOEntityStaticInstance()._lobColumnName;
	}

	public static GenericEntity getStaticInstance(String entityClassName) {
		try {
			return (GenericEntity) getStaticInstanceIDO(RefactorClassRegistry.forName(entityClassName));
		}
		catch (Exception e) {
			throw new RuntimeException(e.getClass().getName() + ": " + e.getMessage());
		}
		/*
		 * if (_allStaticClasses==null){ _allStaticClasses=new Hashtable(); }
		 * IDOLegacyEntity theReturn =
		 * (IDOLegacyEntity)_allStaticClasses.get(entityClassName);
		 * if(theReturn==null){ try{ theReturn =
		 * (IDOLegacyEntity)Class.forName(entityClassName).newInstance();
		 * _allStaticClasses.put(entityClassName,theReturn); } catch(Exception ex){
		 * ex.printStackTrace(); } } return theReturn;
		 */
	}

	private GenericEntity getIDOEntityStaticInstance() {
		// return getStaticInstance(entityClass.getName());
		return (GenericEntity) getStaticInstanceIDO(this.getClass());
	}

	/**
	 * @deprecated Only for IDOLegacyEntity, does not work with pure IDOEntity,
	 *             use getStaticInstanceIDO() instead
	 */
	@Deprecated
	public static IDOLegacyEntity getStaticInstance(Class entityClass) {
		// //return getStaticInstance(entityClass.getName());
		// if (entityClass.isInterface()) {
		// return getStaticInstance(IDOLookup.getBeanClassFor(entityClass));
		// }
		// if (_allStaticClasses == null) {
		// _allStaticClasses = new Hashtable();
		// }
		//
		// IDOLegacyEntity theReturn =
		// (IDOLegacyEntity)_allStaticClasses.get(entityClass.getName());
		//
		// if (theReturn == null) {
		// try {
		// //theReturn = (IDOLegacyEntity)entityClass.newInstance();
		// theReturn = (IDOLegacyEntity)instanciateEntity(entityClass);
		// _allStaticClasses.put(entityClass, theReturn);
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// }
		// return theReturn;
		return (IDOLegacyEntity) getStaticInstanceIDO(entityClass, GenericEntity.DEFAULT_DATASOURCE);
	}

	public static IDOEntity getStaticInstanceIDO(Class entityClass) {
		return GenericEntity.getStaticInstanceIDO(entityClass, GenericEntity.DEFAULT_DATASOURCE);
	}

	public static IDOEntity getStaticInstanceIDO(Class entityClass, String datasource) {
		// return getStaticInstance(entityClass.getName());
		if (entityClass.isInterface()) {
			return getStaticInstanceIDO(IDOLookup.getBeanClassFor(entityClass));
		}

		IDOEntity theReturn = getIDOContainer().getEntityStaticInstances().get(entityClass + datasource);

		if (theReturn == null) {
			try {

				theReturn = instanciateEntity(entityClass, datasource);
				// it might be that the method instanciateEntity(Class) has just put an
				// initialized instance of the specified entityClass into
				// the _allStaticInstances map.
				// !!!!!!This instance should not be replaced!!!!
				// Therefore get the "right" instance.
				IDOEntity correctInstance = getIDOContainer().getEntityStaticInstances().get(entityClass);
				if (correctInstance != null) {
					theReturn = correctInstance;
				}
				else {
					getIDOContainer().getEntityStaticInstances().put(entityClass, theReturn);
				}

			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return theReturn;
	}

	public void addManyToManyRelationShip(IDOEntity relatingEntity, String relationShipTableName) {
		// addManyToManyRelationShip(relatingEntity.getClass().getName(),
		// relationShipTableName);
		addManyToManyRelationShip(relatingEntity.getEntityDefinition().getInterfaceClass().getName(), relationShipTableName);
	}

	public void addManyToManyRelationShip(Class relatingEntityClass, String relationShipTableName) {

		EntityControl.addManyToManyRelationShip(this.getClass().getName(), relatingEntityClass.getName(), relationShipTableName);
	}

	public void addManyToManyRelationShip(String relatingEntityClassName, String relationShipTableName) {
		try {
			addManyToManyRelationShip(RefactorClassRegistry.forName(relatingEntityClassName), relationShipTableName);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Exception in " + this.getClass().getName() + e.getMessage());
		}
	}

	public void addManyToManyRelationShip(String relatingEntityClassName) {
		try {
			// relationShipTableName =
			// EntityControl.getMiddleTableString(this,instanciateEntity(relatingEntityClassName)
			// );
			// addManyToManyRelationShip(this.getClass().getName(),relatingEntityClassName);
			EntityControl.addManyToManyRelationShip(this.getClass().getName(), relatingEntityClassName);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addManyToManyRelationShip(
			Class<? extends IDOEntity> relatingEntityClass) {
		addManyToManyRelationShip(relatingEntityClass.getName());
	}

	public void addTreeRelationShip() {
		EntityControl.addTreeRelationShip(this);
	}

	public int getEntityState() {
		return this._state;
	}

	public void setEntityState(int state) {
		this._state = state;
	}

	public boolean isInSynchWithDatastore() {
		return (getEntityState() == IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}

	/**
	 *
	 * @deprecated replaced with IDOLookup.findByPrimaryKeyLegacy();
	 */
	@Deprecated
	public static IDOLegacyEntity getEntityInstance(Class entityClass, int id) {
		IDOLegacyEntity entity = null;
		try {
			return IDOLookup.findByPrimaryKeyLegacy(entityClass, id);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println("IDOLegacyEntity: error initializing entity");
		}
		return entity;
	}

	/**
	 *
	 * @deprecated Replaced with IDOLookup.instanciateEntity(entityClass);
	 */
	@Deprecated
	public static GenericEntity getEntityInstance(Class entityClass) {
		return (GenericEntity) IDOLookup.instanciateEntity(entityClass);
	}

	public void addMetaDataRelationship() {
		addManyToManyRelationShip(MetaData.class);
		// this.getStaticInstance(this.getClass())._hasMetaDataRelationship=true;
		this.getIDOEntityStaticInstance()._hasMetaDataRelationship = true;
		// bug in getIDOEntityStaticInstance
		this._hasMetaDataRelationship = true;
	}

	public boolean hasMetaDataRelationship() {
		return this.getIDOEntityStaticInstance()._hasMetaDataRelationship;
	}

	// fetches the metadata for this id and puts it in a HashTable
	private void getMetaData() {
		this._theMetaDataAttributes = new Hashtable<String, String>();
		this._theMetaDataIds = new Hashtable<String, Integer>();
		this._theMetaDataTypes = new Hashtable<String, String>();
		// _theMetaDataOrdering = new Hashtable();

		if (getEntityState() == IDOLegacyEntity.STATE_NEW || getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE) {
			// if it is new then it has no primary key and has no relation to the
			// metdata table
			return;
		}
		else {
			// look for the metadata
			Connection conn = null;
			Statement Stmt = null;
			ResultSet RS = null;
			try {
				conn = getConnection(getDatasource());
				Stmt = conn.createStatement();
				MetaData metadata = (MetaData) getStaticInstance(MetaData.class);
				String metadataIdColumn = metadata.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
				String metadataTableName = metadata.getEntityDefinition().getSQLTableName();
				String tableToSelectFrom = getNameOfMiddleTable(metadata, this);
				StringBuffer buffer = new StringBuffer();
				buffer.append("select ic_metadata.ic_metadata_id,ic_metadata.metadata_name,ic_metadata.metadata_value,ic_metadata.meta_data_type from ");
				buffer.append(tableToSelectFrom);
				buffer.append(",ic_metadata where ");
				buffer.append(tableToSelectFrom);
				buffer.append(".");
				buffer.append(getIDColumnName());
				buffer.append("= ");
				buffer.append(getPrimaryKeyValueSQLString());
				buffer.append(" and ");
				buffer.append(tableToSelectFrom);
				buffer.append(".");
				buffer.append(metadataIdColumn);
				buffer.append("=");
				buffer.append(metadataTableName);
				buffer.append(".");
				buffer.append(metadataIdColumn);
				// buffer.append(" order by ");
				// buffer.append(metadataTableName);
				// buffer.append(".order_number");
				String query = buffer.toString();
				this.logSQL("[MetadataQuery]: " + query);
				RS = Stmt.executeQuery(query);
				while (RS.next()) {
					if (RS.getString("metadata_value") != null) {
						this._theMetaDataAttributes.put(RS.getString("metadata_name"), RS.getString("metadata_value"));
						this._theMetaDataIds.put(RS.getString("metadata_name"), new Integer(RS.getInt("ic_metadata_id")));
					}
					if (RS.getString("meta_data_type") != null) {
						this._theMetaDataTypes.put(RS.getString("metadata_name"), RS.getString("meta_data_type"));
					}
					// if (RS.getInt("ordering_number") != -1) {
					// _theMetaDataOrdering.put(RS.getString("metadata_name"), new
					// Integer(RS.getInt("ordering_number")));
					// }
				}
			}
			catch (SQLException ex) {
				System.err.println("Exception in " + this.getClass().getName() + " gettingMetaData " + ex.getMessage());
				ex.printStackTrace(System.err);
			}
			catch (IDOCompositePrimaryKeyException e) {
				System.err.println("Exception in " + this.getClass().getName() + " gettingMetaData " + e.getMessage());
				e.printStackTrace(System.err);
			}
			finally {
				try {

					if (RS != null) {
						RS.close();
					}
					if (Stmt != null) {
						Stmt.close();
					}

				}
				catch (SQLException ex) {
					System.err.println("Exception in " + this.getClass().getName() + " gettingMetaData " + ex.getMessage());
					ex.printStackTrace(System.err);
				}
				if (conn != null) {
					freeConnection(getDatasource(), conn);
				}
			}
		}
	}

	public String getMetaData(String metaDataKey) {
		if (this._theMetaDataAttributes == null) {
			getMetaData(); // get all meta data first if null
		}
		return this._theMetaDataAttributes.get(metaDataKey);
	}

	public void setMetaDataAttributes(Map<String, String> metaDataAttribs) {
		String metaDataKey;
		for (Iterator<String> iterator = metaDataAttribs.keySet().iterator(); iterator.hasNext();) {
			metaDataKey = iterator.next();
			addMetaData(metaDataKey, metaDataAttribs.get(metaDataKey));
		}
	}

	public void setMetaData(String metaDataKey, String metaDataValue) {
		addMetaData(metaDataKey, metaDataValue);
	}

	public void setMetaData(String metaDataKey, String metaDataValue, String metaDataType) {
		addMetaData(metaDataKey, metaDataValue, metaDataType, -1);
	}

	public void addMetaData(String metaDataKey, String metaDataValue) {
		addMetaData(metaDataKey, metaDataValue, null, -1);
	}

	public void renameMetaData(String oldKeyName, String newKeyName) {
		renameMetaData(oldKeyName, newKeyName, null);
	}

	public void renameMetaData(String oldKeyName, String newKeyName, String value) {
		if (this._theMetaDataAttributes == null) {
			getMetaData();
		}
		if (oldKeyName != null && newKeyName != null && !oldKeyName.equals("") && !newKeyName.equals("") && !oldKeyName.equals(newKeyName)) {
			Integer pk = this._theMetaDataIds.get(oldKeyName);
			if (pk != null) {
				try {
					MetaData md = ((MetaDataHome) IDOLookup.getHome(MetaData.class)).findByPrimaryKey(pk);
					md.setName(newKeyName);
					if (value != null) {
						md.setValue(value);
					}
					md.store();
					getMetaData(); // Reloading metadata cache
				}
				catch (IDOLookupException e) {
					e.printStackTrace();
				}
				catch (FinderException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addMetaData(String metaDataKey, String metaDataValue, String metaDataType) {
		addMetaData(metaDataKey, metaDataValue, metaDataType, -1);
	}

	// Ordering not implemented yet
	private void addMetaData(String metaDataKey, String metaDataValue, String metaDataType, int orderingNumber) {
		boolean dataHasChanged = false;
		if (this._theMetaDataAttributes == null) {
			getMetaData(); // get all meta data first if null
		}

		// this null string is a strange value coming from the user tabs in the user
		// system, it means the value is empty or should be removed
		if (metaDataValue != null && !"null".equals(metaDataValue)) {
			if (metaDataType != null) {
				Object oldType = this._theMetaDataTypes.get(metaDataKey);
				if (!(oldType != null && metaDataType.equals(oldType))) {
					// the value changed
					this._theMetaDataTypes.put(metaDataKey, metaDataType);
					dataHasChanged = true;
				}
			}

			Object oldValue = this._theMetaDataAttributes.get(metaDataKey);
			Object obj = null;
			if (!(oldValue != null && metaDataValue.equals(oldValue))) {
				// the value changed
				obj = this._theMetaDataAttributes.put(metaDataKey, metaDataValue);
				dataHasChanged = true;
			}

			// Integer oldOrder = (Integer) _theMetaDataOrdering.get(metaDataKey);
			// if ( ! (oldOrder != null && oldOrder.intValue() == orderingNumber)) {
			// if (orderingNumber == -1) {
			// _theMetaDataOrdering.remove(metaDataKey);
			// } else {
			// _theMetaDataOrdering.put(metaDataKey, new Integer(orderingNumber));
			// }
			// dataHasChanged = true;
			// }

			if (dataHasChanged) {
				if (obj == null) { // is new
					if (this._insertMetaData == null) {
						this._insertMetaData = new ArrayList<String>();
					}
					this._insertMetaData.add(metaDataKey);
				}
				else { // is old
					if (this._updateMetaData == null) {
						this._updateMetaData = new ArrayList<String>();
					}
					if (this._insertMetaData != null) {
						if (this._insertMetaData.indexOf(metaDataKey) == -1) { // is old
							this._updateMetaData.add(metaDataKey);
						}
					}
					else {
						this._updateMetaData.add(metaDataKey);
					}
				}

				// change state of the entity bean
				if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
					setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
				}
				else {
					this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
				}

				metaDataHasChanged(true);
			}

		}
		else if (metaDataValue == null || "null".equals(metaDataValue)) {
			removeMetaData(metaDataKey);
		}
	}

	public void removeAllMetaData() {
		if (this._theMetaDataAttributes == null) {
			getMetaData(); // get all meta data first if null
		}
		if (this._deleteMetaData == null) {
			this._deleteMetaData = new ArrayList<String>();
		}
		if (this._theMetaDataAttributes != null) {
			Set keySet = this._theMetaDataAttributes.keySet();
			if (keySet != null) {
				Iterator iter = keySet.iterator();
				while (iter.hasNext()) {
					String metaDataKey = (String) iter.next();
					this._deleteMetaData.add(metaDataKey);
					if (this._insertMetaData != null) {
						this._insertMetaData.remove(metaDataKey);
					}
					if (this._updateMetaData != null) {
						this._updateMetaData.remove(metaDataKey);
					}
				}
				metaDataHasChanged(true);
			}
		}
	}

	/**
	 * return true if the metadata to delete already exists
	 */
	public boolean removeMetaData(String metaDataKey) {
		if (this._theMetaDataAttributes == null) {
			getMetaData(); // get all meta data first if null
		}

		if (this._theMetaDataAttributes.get(metaDataKey) != null) {
			if (this._deleteMetaData == null) {
				this._deleteMetaData = new ArrayList<String>();
			}
			this._deleteMetaData.add(metaDataKey);

			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				setEntityState(IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
			}
			else {
				this.setEntityState(IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
			}

			if (this._insertMetaData != null) {
				this._insertMetaData.remove(metaDataKey);
			}

			if (this._updateMetaData != null) {
				this._updateMetaData.remove(metaDataKey);
			}

			metaDataHasChanged(true);
			return true;
		}
		else {
			return false;
		}
	}

	public void clearMetaDataVectors() {
		this._insertMetaData = null;
		this._updateMetaData = null;
		this._deleteMetaData = null;
		this._theMetaDataAttributes = null;
		this._theMetaDataTypes = null;
	}

	public Map<String, String> getMetaDataAttributes() {
		if (this._theMetaDataAttributes == null) {
			getMetaData();
		}
		return this._theMetaDataAttributes;
	}

	public Hashtable<String, Integer> getMetaDataIds() {
		return this._theMetaDataIds;
	}

	public Map<String, String> getMetaDataTypes() {
		if (this._theMetaDataTypes == null) {
			getMetaData();
		}
		return this._theMetaDataTypes;
	}

	public List<String> getMetaDataUpdate() {
		return this._updateMetaData;
	}

	public List<String> getMetaDataInsert() {
		return this._insertMetaData;
	}

	public List<String> getMetaDataDelete() {
		return this._deleteMetaData;
	}

	public boolean metaDataHasChanged() {
		return this._metaDataHasChanged;
	}

	public void metaDataHasChanged(boolean metaDataHasChanged) {
		this._metaDataHasChanged = metaDataHasChanged;
	}

	@Override
	public void setEJBLocalHome(javax.ejb.EJBLocalHome ejbHome) {
		this._ejbHomes.put(this.getClass().getName() + getDatasource(), ejbHome);
	}

	@Override
	public javax.ejb.EJBLocalHome getEJBLocalHome() {
		return getEJBLocalHome(getDatasource());
	}

	public javax.ejb.EJBLocalHome getEJBLocalHome(String datasource) {
		String key = this.getClass().toString() + datasource;
		EJBLocalHome ejbHome = this._ejbHomes.get(key);
		if (ejbHome == null) {
			try {
				ejbHome = IDOLookup.getHome(this.getClass(), datasource);
				this._ejbHomes.put(key, ejbHome);
			}
			catch (Exception e) {
				throw new EJBException("Lookup for home for: " + this.getClass().getName() + " failed. Errormessage was: " + e.getMessage());
			}
		}
		return ejbHome;
	}

	/**
	 * Not implemented
	 *
	 * @todo: implement
	 */
	public javax.ejb.Handle getHandle() {
		return null;
	}

	@Override
	public Object getPrimaryKey() {
		return getPrimaryKeyValue();
	}

	public boolean isIdentical(javax.ejb.EJBObject ejbo) {
		if (ejbo != null) {
			try {
				return ejbo.getPrimaryKey().equals(this.getPrimaryKey());
			}
			catch (java.rmi.RemoteException rme) {
				rme.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void remove() throws RemoveException {
		try {
			delete();
		}
		catch (Exception e) {
			throw new IDORemoveException(e);
		}
	}

	@Override
	public void store() throws IDOStoreException {
		try {
			if ((getEntityState() == IDOLegacyEntity.STATE_NEW) || (getEntityState() == IDOLegacyEntity.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)) {
				insert();
			}
			else if (this.getEntityState() == IDOLegacyEntity.STATE_NOT_IN_SYNCH_WITH_DATASTORE) {
				update();
			}
			if (this.hasMetaDataRelationship()) {
				this.updateMetaData();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IDOStoreException(e.getMessage(), e);
		}
	}

	@Override
	public void ejbActivate() {
	}

	@Override
	public void ejbPassivate() {
		if (this._columns != null) {
			this._columns.clear();
		}
		this._dataSource = DEFAULT_DATASOURCE;
		this._state = IDOLegacyEntity.STATE_NEW;
		this._updatedColumns = null;
		this._primaryKey = null;
		this._theMetaDataAttributes = null;
		this._insertMetaData = null;
		this._updateMetaData = null;
		this._deleteMetaData = null;
		this._theMetaDataIds = null;
		// _hasMetaDataRelationship = false;
		this._metaDataHasChanged = false;

	}

	@Override
	public void ejbRemove() throws javax.ejb.RemoveException {
		remove();
	}

	@Override
	public void ejbStore() {
		store();
	}

	@Override
	public void setEntityContext(javax.ejb.EntityContext ctx) {
	}

	@Override
	public void unsetEntityContext() {
	}

	@Override
	public Object ejbCreate() throws CreateException {

		// if this entity has used addUniqueIdColumn() this method will generated
		// the unique id and set the column in the entity.
		// see addUniqueIDColumn() and generateAndSetUniqueIDForIDO()
		if (hasUniqueIDColumn()) {
			generateAndSetUniqueIDForIDO();
		}
		if (this.doInsertInCreate()) {
			this.insertForCreate();
		}

		return getPrimaryKey();
	}

	/**
	 * Generates unique id string 36 characters long (128bit) and sets the unique
	 * id column. <br>
	 * The default implementation generates the string with a combination of a
	 * <br>
	 * dummy ip address and a time based random number generator.<br>
	 * For more info see the JUG project,
	 * http://www.doomdark.org/doomdark/proj/jug/ An example uid:
	 * ac483688-b6ed-4f45-ac64-c105e599d482 <br>
	 * You must call addUniqueIDColumn() in your IDO's initializeAttributes method
	 * to enable this behavior.
	 */
	protected void generateAndSetUniqueIDForIDO() {
		if (getUniqueId() == null) {
			IdGenerator uidGenerator = IdGeneratorFactory.getUUIDGenerator();
			String uniqueId = uidGenerator.generateId();

			setUniqueId(uniqueId);
		}
	}

	/**
	 * Gets the name for the UniqueId Column. Defaults to UNIQUE_ID
	 *
	 * @return
	 */
	protected String getUniqueIdColumnName() {
		return UNIQUE_ID_COLUMN_NAME;
	}

	/**
	 * Sets the Unique ID column. This method should generally never be called
	 * manually
	 *
	 * @param uniqueId
	 */
	public void setUniqueId(String uniqueId) {
		setColumn(getUniqueIdColumnName(), uniqueId);
	}

	/**
	 * @return true if this entity has called addUniqueIdColumn() to add a unique
	 *         id column to its table
	 */
	protected boolean hasUniqueIDColumn() {
		return getGenericEntityDefinition().hasField(getUniqueIdColumnName());
	}

	/**
	 * Default create method for IDO
	 */
	public Object ejbCreateIDO() throws CreateException {
		return ejbCreate();
	}

	/**
	 * Default create method for IDO
	 */
	public IDOEntity ejbHomeCreateIDO() throws CreateException {
		throw new UnsupportedOperationException("Not implemented");
		// return ejbCreate();
	}

	/**
	 * Default postcreate method for IDO
	 */
	public void ejbPostCreateIDO() {
		// does nothing
	}

	public void ejbPostCreate() {
	}

	@Override
	public Object ejbFindByPrimaryKey(Object pk) throws FinderException {
		this.setPrimaryKey(pk);
		return getPrimaryKey();
	}

	/**
	 * Default findByPrimaryKey method for IDO
	 */
	public Object ejbFindByPrimaryKeyIDO(Object pk) throws FinderException {
		return ejbFindByPrimaryKeyIDO(pk);
	}

	void flagColumnUpdate(String columnName) {
		if (this.canRegisterColumnsForUpdate) {
			if (this._updatedColumns == null) {
				this._updatedColumns = new HashMap<String, Boolean>();
			}
			this._updatedColumns.put(columnName.toUpperCase(), Boolean.TRUE);
		}
	}

	boolean hasColumnBeenUpdated(String columnName) {
		if (this._updatedColumns == null) {
			return false;
		}
		else {
			return (this._updatedColumns.get(columnName.toUpperCase()) != null);
		}
	}

	public boolean columnsHaveChanged() {
		return (this._updatedColumns != null);
	}

	public void setToInsertStartData(boolean ifTrue) {
		this.insertStartData = ifTrue;
	}

	public boolean getIfInsertStartData() {
		return this.insertStartData;
	}

	protected void setPrimaryKey(int pk) {
		Integer id = new Integer(pk);
		this.setPrimaryKey(id);
	}

	protected void setPrimaryKey(Object pk) {
		if (pk instanceof IDOPrimaryKey) {
			IDOEntityField[] fields = this.getGenericEntityDefinition().getPrimaryKeyDefinition().getFields();
			IDOPrimaryKey primaryKey = (IDOPrimaryKey) pk;
			for (int i = 0; i < fields.length; i++) {
				initializeColumnValue(fields[i].getSQLFieldName(), primaryKey.getPrimaryKeyValue(fields[i].getSQLFieldName().toUpperCase()));
			}
		}
		else {
			initializeColumnValue(getIDColumnName(), pk);
		}
		this._primaryKey = pk;
	}

	private static GenericEntity instanciateEntity(Class entityInterfaceOrBeanClass, String datasource) {
		try {
			return (GenericEntity) IDOLookup.instanciateEntity(entityInterfaceOrBeanClass, datasource);
		}
		catch (Exception e1) {
			// Only for legacy beans;
			e1.printStackTrace();
			try {
				GenericEntity ent = (GenericEntity) entityInterfaceOrBeanClass.newInstance();
				ent.setDatasource(datasource);
				return ent;
			}
			catch (Exception e2) {
				e2.printStackTrace();
				throw new RuntimeException(e1.getMessage());
			}
		}
	}

	private IDOLegacyEntity findByPrimaryInOtherClass(Class entityInterfaceOrBeanClass, int id) throws java.sql.SQLException {
		IDOLegacyEntity returnEntity = IDOLookup.findByPrimaryKeyLegacy(entityInterfaceOrBeanClass, id, this.getDatasource());
		// returnEntity.setDatasource(this.getDatasource());
		return returnEntity;
	}

	/**
	 * @deprecated replacced with idoFindPKsBySQL
	 */
	@Deprecated
	protected <PK> Collection<PK> idoFindIDsBySQL(String sqlQuery) throws FinderException {
		return idoFindPKsBySQL(sqlQuery);
	}

	protected <PK> Collection<PK> idoFindPKsBySQL(String sqlQuery) throws FinderException {
		return idoFindPKsBySQL(sqlQuery, -1, -1);
	}

	/**
	 * Fetches the primarykey resultset and then loads the beans with data(the
	 * prefect size determines how many get loaded) The query must be a select all
	 * query!
	 *
	 * @param sqlQuery
	 * @param countQuery
	 * @param prefetchSize
	 * @return
	 * @throws FinderException
	 */
	protected Collection idoFindPKsByQueryUsingLoadBalance(SelectQuery sqlQuery, int prefetchSize) throws FinderException {
		Collection pkColl = null;
		Class interfaceClass = this.getInterfaceClass();
		boolean queryCachingActive = IDOContainer.getInstance().queryCachingActive(interfaceClass);
		if (queryCachingActive) {
			pkColl = IDOContainer.getInstance().getBeanCache(getDatasource(),interfaceClass).getCachedFindQuery(sqlQuery.toString());
		}
		if (pkColl == null) {
			pkColl = this.idoFindPKsByQueryIgnoringCacheAndUsingLoadBalance(sqlQuery, prefetchSize);
			if (queryCachingActive) {
				IDOContainer.getInstance().getBeanCache(getDatasource(),interfaceClass).putCachedFindQuery(sqlQuery.toString(), pkColl);
			}
		}
		else {
			if (this.isDebugActive()) {
				logSQL("Cache hit for SQL query: " + sqlQuery);
			}
		}
		return pkColl;
	}

	protected Collection idoFindByPrimaryKeyCollection(Collection primaryKeys, int prefetchSize) throws FinderException {
		return new IDOPrimaryKeyList(primaryKeys, this, prefetchSize);
	}

	public Collection ejbFindByPrimaryKeyCollection(Collection primaryKeys) throws FinderException {
		return idoFindByPrimaryKeyCollection(primaryKeys, 1000);
	}

	/**
	 *
	 * @param sqlQuery
	 * @return IDOPrimaryKeyList
	 * @throws FinderException
	 */
	protected Collection idoFindPKsByQueryIgnoringCacheAndUsingLoadBalance(SelectQuery sqlQuery, int prefetchSize) throws FinderException {
		return new IDOPrimaryKeyList(sqlQuery, this, prefetchSize);
	}

	/**
	 *
	 * @param sqlQuery
	 * @param returnProxy
	 *          If this entity and the returnProxy have one-to-one related primary
	 *          keys, that is they have the same primaryKeys, then this method is
	 *          able to return IDOPriamryKeyList that will return IDEntities of
	 *          the same type as returnProxy when wrapped into IDOEntityList
	 *          although the SelectQuery only uses this entity
	 * @return IDOPrimaryKeyList
	 * @throws FinderException
	 */
	protected Collection idoFindPKsByQueryIgnoringCacheAndUsingLoadBalance(SelectQuery sqlQuery, GenericEntity returnProxy, SelectQuery proxyQueryConstraints, int prefetchSize) throws FinderException {
		return new IDOPrimaryKeyList(sqlQuery, this, returnProxy, proxyQueryConstraints, prefetchSize);
	}

	protected Collection idoFindPKsBySQLIgnoringCache(String sqlQuery, int returningNumber, int startingEntry) throws FinderException {
		return idoFindPKsBySQLIgnoringCache(sqlQuery, returningNumber, startingEntry, null);
	}

	protected <PK> Collection<PK> idoFindPKsBySQLIgnoringCache(String sqlQuery, int returningNumber, int startingEntry, SelectQuery query) throws FinderException {
		logSQL(sqlQuery);

		if (startingEntry < 0) {
			startingEntry = 0;
		}
		if (returningNumber < 0) {
			returningNumber = 0;
		}

		Connection conn = null;
		ResultHelper rsh = null;

		Collection<PK> results = new ArrayList<PK>();
		try {
			conn = getConnection(getDatasource());
			rsh = prepareResultSet(conn, sqlQuery, query);
			int counter = 0;
			boolean addEntity = true;
			while (rsh.rs.next() && addEntity) {
				if (startingEntry <= counter) {
					if (returningNumber > 0) {
						if (counter < (returningNumber + startingEntry)) {
							addEntity = true;
						} else {
							addEntity = false;
						}
					} else {
						addEntity = true;
					}

					if (addEntity) {
						PK pk = this.getPrimaryKeyFromResultSet(rsh.rs);
						if (pk != null) {
							results.add(pk);
						}
					}
				}
				counter++;
			}
		} catch (SQLException sqle) {
			throw new IDOFinderException(sqle);
		} finally {
			if (rsh != null) {
				rsh.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return results;
	}

	protected class ResultHelper {
		ResultSet rs = null;
		Statement stmt = null;

		void close() {
			if (this.rs != null) {
				try {
					this.rs.close();
				}
				catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (this.stmt != null) {
				try {
					this.stmt.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private ResultHelper prepareResultSet(Connection conn, String sqlString, SelectQuery query) throws SQLException {
		ResultHelper rsh = new ResultHelper();
		if (conn != null) {
			if (query != null) {
				DatastoreInterface dsi = DatastoreInterface.getDatastoreInterfaceByDatasource(getDatasource());
				List values = query.getValues();
				if (values != null && dsi.isUsingPreparedStatements()) {
					rsh.stmt = conn.prepareStatement(query.toString(true));
					dsi.insertIntoPreparedStatement(values, (PreparedStatement) rsh.stmt, 1);
					rsh.rs = ((PreparedStatement) rsh.stmt).executeQuery();
				} else {
					rsh.stmt = conn.createStatement();
					rsh.rs = rsh.stmt.executeQuery(query.toString());
				}
			} else if (sqlString != null) {
				rsh.stmt = conn.createStatement();
				rsh.rs = rsh.stmt.executeQuery(sqlString);
			}
		}
		return rsh;
	}

	/**
	 * Finds all relationships this entity bean instane has with ALL
	 * returningEntityInterfaceClass beans Returns a collection of returningEntity
	 * instances
	 *
	 * @throws IDORelationshipException
	 *           if the returningEntity has no relationship defined with this bean
	 *           or an error with the query
	 */
	protected <T extends IDOEntity> Collection<T> idoGetRelatedEntities(Class<T> returningEntityInterfaceClass) throws IDORelationshipException {
		T returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass, getDatasource());
		return idoGetRelatedEntitiesBySQL(returningEntity, getFindRelatedSQLQuery(returningEntity, "", ""));
	}

	/**
	 * Returns a collection of returningEntity instances
	 */
	protected <T extends IDOEntity> Collection<T> idoGetRelatedEntities(T returningEntity, String columnName, String entityColumnValue) throws IDOException {
		String SQLString = this.getFindRelatedSQLQuery(returningEntity, columnName, entityColumnValue);
		return this.idoGetRelatedEntitiesBySQL(returningEntity, SQLString);
	}

	/**
	 * Returns a collection of returningEntity instances
	 */
	protected <T extends IDOEntity> Collection<T> idoGetRelatedEntities(Class<T> returningEntityInterfaceClass, String columnName, String entityColumnValue) throws IDOException {
		T returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass);
		String SQLString = this.getFindRelatedSQLQuery(returningEntity, columnName, entityColumnValue);
		return this.idoGetRelatedEntitiesBySQL(returningEntity, SQLString);
	}

	/**
	 * Returns a collection of returningEntity instances
	 */
	protected <T extends IDOEntity> Collection<T> idoGetRelatedEntitiesOrderedByColumn(Class<T> returningEntityInterfaceClass, String columnName) throws IDOException {
		T returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass);
		String SQLString = this.getFindRelatedSQLQuery(returningEntity, null, null, columnName);
		return this.idoGetRelatedEntitiesBySQL(returningEntityInterfaceClass, SQLString);
	}

	/**
	 * Returns a collection of returningEntity instances
	 *
	 * @throws IDORelationshipException
	 *           if the returningEntity has no relationship defined with this bean
	 *           or an error with the query
	 */
	protected <T extends IDOEntity> Collection<T> idoGetRelatedEntities(T returningEntity) throws IDORelationshipException {
		String sqlQuery = this.getFindRelatedSQLQuery(returningEntity, "", "");

		logSQL(sqlQuery);

		return idoGetRelatedEntitiesBySQL(returningEntity, sqlQuery);
	}

	/**
	 * Returns a collection of entity(this) instances
	 *
	 * @throws IDORelationshipException
	 *           if the relatedEntity has no relationship defined with this bean
	 *           or an error with the query
	 */
	protected Collection idoGetReverseRelatedEntities(IDOEntity relatedEntity) throws IDORelationshipException {
		String sqlQuery = this.getFindReverseRelatedSQLQuery(relatedEntity, "", "");

		logSQL(sqlQuery);

		return idoGetRelatedEntitiesBySQL(this, sqlQuery);

	}

	protected <T extends IDOEntity> Collection<T> idoGetRelatedEntitiesBySQL(Class<T> returningEntityInterfaceClass, String sqlQuery) throws IDORelationshipException {
		T returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass);
		return idoGetRelatedEntitiesBySQL(returningEntity, sqlQuery);
	}

	/**
	 * Returns a collection of returningEntity instances
	 *
	 * @throws IDORelationshipException
	 *           if the returningEntity has no relationship defined with this bean
	 *           or an error with the query
	 */
	private <PK, T extends IDOEntity> Collection<T> idoGetRelatedEntitiesBySQL(T returningEntity, String sqlQuery) throws IDORelationshipException {
		Collection<T> results = new ArrayList<T>();
		Collection<PK> ids = idoGetRelatedEntityPKs(returningEntity, sqlQuery);
		try {
			IDOHome home = IDOLookup.getHome(returningEntity.getClass(), getDatasource());
			for (Iterator<PK> iter = ids.iterator(); iter.hasNext();) {
				try {
					PK pk = iter.next();
					T entityToAdd = home.findByPrimaryKeyIDO(pk);
					results.add(entityToAdd);
				} catch (Exception e) {
					throw new EJBException(e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new IDORelationshipException("Error in idoGetRelatedEntities()" + e.getMessage());
		}
		return results;
	}

	protected <PK, T extends IDOEntity> Collection<PK> idoGetRelatedEntityPKs(Class<T> returningEntityInterfaceClass) throws IDORelationshipException {
		T returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass);
		String sqlQuery = this.getFindRelatedSQLQuery(returningEntity, "", "");
		return idoGetRelatedEntityPKs(returningEntity, sqlQuery);
	}

	protected <PK, T extends IDOEntity> Collection<PK> idoGetRelatedEntityPKs(Class<T> returningEntityInterfaceClass, String sqlQuery) throws IDORelationshipException {
		IDOEntity returningEntity = IDOLookup.instanciateEntity(returningEntityInterfaceClass);
		return idoGetRelatedEntityPKs(returningEntity, sqlQuery);
	}

	/**
	 * Returns a collection of returningEntity primary keys
	 *
	 * @throws IDORelationshipException
	 *           if the returningEntity has no relationship defined with this bean
	 *           or an error with the query
	 */
	protected <PK> Collection<PK> idoGetRelatedEntityPKs(IDOEntity returningEntity) throws IDORelationshipException {
		String sqlQuery = this.getFindRelatedSQLQuery(returningEntity, "", "");
		return idoGetRelatedEntityPKs(returningEntity, sqlQuery);
	}

	/**
	 * Returns a collection of returningEntity primary keys
	 */
	private <PK> Collection<PK> idoGetRelatedEntityPKs(IDOEntity returningEntity, String sqlQuery) throws IDORelationshipException {
		Connection conn = null;
		Statement Stmt = null;
		Collection results = new ArrayList();
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			logSQL(sqlQuery);
			ResultSet rs = Stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				PK pk = ((GenericEntity) returningEntity).getPrimaryKeyFromResultSet(rs);
				results.add(pk);
			}
			rs.close();
		} catch (Exception sqle) {
			throw new IDORelationshipException(sqle, this);
		} finally {
			if (Stmt != null) {
				try {
					Stmt.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				freeConnection(getDatasource(), conn);
			}
		}
		return results;
	}

	protected <PK> Collection<PK> idoFindAllIDsOrderedBySQL(String oderByColumnName) throws FinderException {
		return this.idoFindIDsBySQL("select * from " + getTableName() + " order by " + oderByColumnName);
	}

	protected <PK> Collection<PK> idoFindAllIDsBySQL() throws FinderException {
		return this.idoFindIDsBySQL("select * from " + getTableName());
	}

	/**
	 * Finds one primary key by an SQL query
	 */
	protected Object idoFindOnePKBySQL(String sqlQuery) throws FinderException {
		return idoFindOnePKBySQL(sqlQuery, null);
	}

	/**
	 * Finds one primary key by an SQL query
	 */
	private <PK> PK idoFindOnePKBySQL(String sqlQuery, SelectQuery selectQuery) throws FinderException {
		Collection<PK> coll = idoFindPKsBySQL(sqlQuery, 1, -1, selectQuery);
		try {
			if (!ListUtil.isEmpty(coll)) {
				return coll.iterator().next();
			}
		}
		catch (Exception e) {
			throw new IDOFinderException(e);
		}
		throw new IDOFinderException("Nothing found");
	}

	/**
	 * Finds returningNumberOfRecords Primary keys from the specified sqlQuery
	 */
	protected <PK> Collection<PK> idoFindPKsBySQL(String sqlQuery, int returningNumberOfRecords) throws FinderException {
		return idoFindPKsBySQL(sqlQuery, returningNumberOfRecords, -1);
	}

	protected <PK> Collection<PK> idoFindPKsBySQL(String sqlQuery, int returningNumberOfRecords, int startingEntry) throws FinderException {
		return idoFindPKsBySQL(sqlQuery, returningNumberOfRecords, startingEntry, null);
	}

	/**
	 * Finds returningNumberOfRecords Primary keys from the specified sqlQuery
	 */
	protected <PK> Collection<PK> idoFindPKsBySQL(String sqlQuery, int returningNumberOfRecords, int startingEntry, SelectQuery selectQuery) throws FinderException {
		boolean measureSQL = CoreUtil.isSQLMeasurementOn();
		long start = measureSQL ? System.currentTimeMillis() : 0;
		try {
			Collection<PK> pkColl = null;
			Class interfaceClass = this.getInterfaceClass();
			boolean queryCachingActive = IDOContainer.getInstance().queryCachingActive(interfaceClass);
			if (queryCachingActive) {
				IDOBeanCache cache = IDOContainer.getInstance().getBeanCache(getDatasource(), interfaceClass);
				pkColl = cache.getCachedFindQuery(sqlQuery);
			}
			if (pkColl == null) {
				pkColl = this.idoFindPKsBySQLIgnoringCache(sqlQuery, returningNumberOfRecords, startingEntry, selectQuery);
				if (queryCachingActive) {
					IDOContainer.getInstance().getBeanCache(getDatasource(),interfaceClass).putCachedFindQuery(sqlQuery, pkColl);
				}
			} else {
				if (this.isDebugActive()) {
					logSQL("Cache hit for SQL query: " + sqlQuery);
				}
			}
			return pkColl;
		} finally {
			if (measureSQL) {
				log(Level.INFO, "Query '" + sqlQuery + "' was executed in " + (System.currentTimeMillis() - start) + " ms");
			}
		}
	}

	/**
	 * @deprecated replaced with idoFindPKsBySQL
	 */
	@Deprecated
	protected Collection idoFindIDsBySQL(String SQLString, int returningNumberOfRecords) throws FinderException {
		return idoFindPKsBySQL(SQLString, returningNumberOfRecords);
	}

	/**
	 * @todo use selectquery in all ido find methods
	 */
	protected Collection idoFindAllIDsByColumnBySQL(String columnName, String toFind) throws FinderException {
		return idoFindIDsBySQL("select " + getIDColumnName() + " from " + getTableName() + " where " + columnName + "='" + toFind + "'");
	}

	/**
	 * Finds PK of one entity that has this exact column value
	 */
	protected Object idoFindOnePKByColumnBySQL(String columnName, String toFind) throws FinderException {
		return idoFindOnePKBySQL("select " + getIDColumnName() + " from " + getTableName() + " where " + columnName + "='" + toFind + "'");
	}

	/**
	 * Finds PKs of all entities by a metadata key or metadata key and value
	 *
	 * @param key,
	 *          the metadata name cannot be null
	 * @param value,
	 *          the metadata value can be null
	 * @return all collection of primary keys of the current genericentity
	 * @throws FinderException
	 */
	protected Collection idoFindPKsByMetaData(String key, String value) throws FinderException {
		MetaData metadata = (MetaData) getStaticInstance(MetaData.class);
		final String middleTableName = getNameOfMiddleTable(metadata, this);
		final String tableToSelectFrom = getEntityName();
		final String metadataIdColumnName;
		final String metadataTableName = metadata.getEntityDefinition().getSQLTableName();
		final String primaryColumnName = getIDColumnName();
		final String keyColumn = MetaDataBMPBean.COLUMN_META_KEY;
		final String valueColumn = MetaDataBMPBean.COLUMN_META_VALUE;

		try {
			metadataIdColumnName = metadata.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
			StringBuffer sql = new StringBuffer();
			sql.append("select entity.* from ").append(tableToSelectFrom).append(" entity ,").append(middleTableName).append(" middle ,").append(metadataTableName).append(" meta ").append(" where ").append("entity.").append(primaryColumnName).append("=").append("middle").append(".").append(primaryColumnName).append(" and ").append("middle.").append(metadataIdColumnName).append("=").append("meta").append(".").append(metadataIdColumnName).append(" and ").append("meta.").append(keyColumn).append("=").append("'").append(key).append("'");
			if (value != null) {
				sql.append(" and ").append("meta.").append(valueColumn).append("=").append("'").append(value).append("'");
			}
			// TODO use selectquery
			return idoFindPKsBySQL(sql.toString());
		}
		catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			throw new FinderException(e.getMessage());
		}

	}

	/**
	 * Finds an entity by its unique id column. <br>
	 * See also addUniqueIDColumn() on how to add a unique generated id column to
	 * your entity.
	 *
	 * @param uniqueID,
	 *          A 128 bit unique id string (36 characters long)
	 * @return Object which is the primary key of the object found from the query.
	 * @throws FinderException
	 *           if nothing found or there is an error with the query.
	 */
	protected Object idoFindOnePKByUniqueId(String uniqueID) throws FinderException {
		return idoFindOnePKByColumnBySQL(getUniqueIdColumnName(), uniqueID);
	}

	/**
	 * Finds by two columns
	 */
	protected Collection idoFindAllIDsByColumnsBySQL(String columnName, String toFind, String columnName2, String toFind2) throws FinderException {
		IDOQuery query = new IDOQuery();
		query.appendSelectAllFrom(getTableName());
		query.appendWhere(columnName);
		query.appendEqualSign();
		query.appendWithinSingleQuotes(toFind);
		query.appendAnd();
		query.append(columnName2);
		query.appendEqualSign();
		query.appendWithinSingleQuotes(toFind2);
		return idoFindIDsBySQL(query.toString());
	}

	/**
	 * Finds by two columns
	 */
	protected Collection idoFindAllIDsByColumnsBySQL(String columnName, int toFind, String columnName2, int toFind2) throws FinderException {
		IDOQuery query = new IDOQuery();
		query.appendSelectAllFrom(getTableName());
		query.appendWhere(columnName);
		query.appendEqualSign();
		query.append(toFind);
		query.appendAnd();
		query.append(columnName2);
		query.appendEqualSign();
		query.append(toFind2);
		return idoFindIDsBySQL(query.toString());
	}

	/**
	 * Finds by two columns
	 */
	protected Collection idoFindAllIDsByColumnsBySQL(String columnName, int toFind, String columnName2, String toFind2) throws FinderException {
		IDOQuery query = new IDOQuery();
		query.appendSelectAllFrom(getTableName());
		query.appendWhere(columnName);
		query.appendEqualSign();
		query.append(toFind);
		query.appendAnd();
		query.append(columnName2);
		query.appendEqualSign();
		query.appendWithinSingleQuotes(toFind2);
		return idoFindIDsBySQL(query.toString());
	}

	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, String toFind, String orderByColumnName) throws FinderException {
		return idoFindIDsBySQL("select " + getIDColumnName() + " from " + getTableName() + " where " + columnName + " = '" + toFind + "' order by " + orderByColumnName);
	}

	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, int toFind, String orderByColumnName) throws FinderException {
		return idoFindIDsBySQL("select " + getIDColumnName() + " from " + getTableName() + " where " + columnName + " = " + toFind + " order by " + orderByColumnName);
	}

	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, String toFind) throws FinderException {
		return idoFindAllIDsByColumnOrderedBySQL(columnName, toFind, columnName);
	}

	protected Collection idoFindAllIDsByColumnOrderedBySQL(String columnName, int toFind) throws FinderException {
		return idoFindAllIDsByColumnOrderedBySQL(columnName, toFind, columnName);
	}

	protected Class getInterfaceClass() {
		return IDOLookup.getInterfaceClassFor(this.getClass());
	}

	private void flushQueryCache() {
		Class interfaceClass = this.getInterfaceClass();
		boolean queryCachingActive = IDOContainer.getInstance().queryCachingActive(interfaceClass);
		if (queryCachingActive) {
			IDOContainer.getInstance().getBeanCache(getDatasource(),interfaceClass).flushAllQueryCache();
		}
	}

	private void flushBeanCache() {
		Class interfaceClass = this.getInterfaceClass();
		boolean beanCachingActive = IDOContainer.getInstance().beanCachingActive(interfaceClass);
		if (beanCachingActive) {
			IDOContainer.getInstance().getBeanCache(getDatasource(),interfaceClass).flushAllBeanCache();
		}
	}

	boolean closeBlobConnections() throws Exception {
		if (this.hasLobColumn()) {
			BlobWrapper wrapper = this.getBlobColumnValue(this.getLobColumnName());
			if (wrapper != null) {
				wrapper.close();
				return true;
			}
		}
		return false;
	}

	IDOEntity prefetchBeanFromResultSet(Object pk, ResultSet rs, String dataSourceName) throws FinderException {
		return IDOContainer.getInstance().findByPrimaryKey(this.getInterfaceClass(), pk, rs, (IDOHome) this.getEJBLocalHome(), dataSourceName);

	}

	/**
	 * Meant to be overrided in subclasses, returns default Integer.class
	 */
	@Override
	public Class getPrimaryKeyClass() {
		return Integer.class;
	}

	/**
	 * The default implementation. Returns the number of all records for this
	 * entity.
	 *
	 * @return int the count of all records
	 * @throws IDOException
	 *           if there was an exceptoin accessing the datastore
	 */
	protected int idoGetNumberOfRecords() throws IDOException {
		try {
			return this.getNumberOfRecords();
		}
		catch (SQLException e) {
			throw new IDOException(e, this);
		}
	}

	/**
	 * Returns the number of recors for the query sql.
	 *
	 * @param sql
	 *          A count SQL query.
	 * @return int the count of the records
	 * @throws IDOException
	 *           if there was an error with the query or erroraccessing the
	 *           datastore
	 */
	protected int idoGetNumberOfRecords(String sql) throws IDOException {
		try {
			if (isDebugActive()) {
				logSQL(sql);
			}
			return this.getNumberOfRecords(sql);
		}
		catch (SQLException e) {
			throw new IDOException(e, this);
		}
	}

	protected double idoGetAverage(String sql) throws IDOException {
		try {
			if (isDebugActive()) {
				logSQL(sql);
			}
			return this.getAverage(sql);
		}
		catch (SQLException e) {
			throw new IDOException(e, this);
		}
	}

	/**
	 * Returns the number of recors for the query sql.
	 *
	 * @param query
	 *          A count query.
	 * @return int the count of the records
	 * @throws IDOException
	 *           if there was an error with the query or erroraccessing the
	 *           datastore
	 */
	protected int idoGetNumberOfRecords(IDOQuery query) throws IDOException {
		return idoGetNumberOfRecords(query.toString());
	}

	/**
	 * Returns the number of recors for the query sql.
	 *
	 * @param query
	 *          A count query.
	 * @return int the count of the records
	 * @throws IDOException
	 *           if there was an error with the query or erroraccessing the
	 *           datastore
	 */
	protected int idoGetNumberOfRecords(SelectQuery query) throws IDOException {
		try {
			if (isDebugActive()) {
				logSQL(query.toString());
			}
			return this.getNumberOfRecords(query);
		}
		catch (SQLException e) {
			throw new IDOException(e, this);
		}
	}

	/**
	 * *Default remove behavior with a many-to-many relationship * Deletes <b>ALL</b>
	 * records of relation with all instances of entityInterfaceClass with this
	 * entity bean instance
	 *
	 * @throws IDORemoveRelationshipException
	 *           if there is no relationship defined with the given entity class
	 *           or there is an error accessing it
	 */
	protected void idoRemoveFrom(Class entityInterfaceClass) throws IDORemoveRelationshipException {
		/**
		 * @todo Change implementation
		 */
		try {
			// removeFrom(this.getStaticInstance(entityInterfaceClass));
			removeFrom(entityInterfaceClass);
		}
		catch (SQLException ex) {
			// ex.printStackTrace();
			throw new IDORemoveRelationshipException(ex, this);
		}
	}

	/**
	 * *Default remove behavior with a many-to-many relationship * deletes only
	 * one line in middle table if the genericentity wa consructed with a value
	 *
	 * @throws IDORemoveRelationshipException
	 *           if there is no relationship defined with the given entity class
	 *           or there is an error accessing it
	 */
	protected void idoRemoveFrom(IDOEntity entity) throws IDORemoveRelationshipException {
		/**
		 * @todo Change implementation
		 */
		try {
			removeFrom(entity);
		}
		catch (SQLException ex) {
			// ex.printStackTrace();
			throw new IDORemoveRelationshipException(ex, this);
		}
	}

	/**
	 *
	 * <p>Removes all records from SQL table, which are related to this
	 * instance of entity object.</p>
	 * @param relatedTableName is table name of related entity, not <code>null</code>;
	 * @return <code>true</code> if successfully removed, <code>false</code>
	 * otherwise;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean idoRemoveFrom(String relatedTableName) {
		return removeFrom(relatedTableName);
	}

	protected void idoRemoveFrom(IDOEntity entity, String middleTableName) throws IDORemoveRelationshipException {
		try {
			removeFrom(entity, middleTableName);
		}
		catch (SQLException ex) {
			throw new IDORemoveRelationshipException(ex, this);
		}
	}

	/**
	 * *Default insert behavior with a many-to-many relationship
	 *  *
	 * @throws IDOAddRelationshipException
	 *           if there is no relationship with the given entity or there is an
	 *           error accessing it
	 */
	protected void idoAddTo(Class entityToAddTo, Object primaryKey) throws IDOAddRelationshipException {
		try {
			IDOEntity entity = IDOLookup.instanciateEntity(entityToAddTo);
			idoAddTo(getNameOfMiddleTable(entity, this), entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName(), primaryKey);
		}
		catch (Exception e) {
			throw new IDOAddRelationshipException(e, this);
		}
	}

	/**
	 * *Default insert behavior with a many-to-many relationship
	 *  *
	 * @throws IDOAddRelationshipException
	 *           if there is no relationship with the given entity or there is an
	 *           error accessing it
	 */
	protected void idoAddTo(IDOEntity entity) throws IDOAddRelationshipException {

		try {
			idoAddTo(getNameOfMiddleTable(entity, this), entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName(), entity.getPrimaryKey());
		}
		catch (Exception e) {
			throw new IDOAddRelationshipException(e, this);
		}
	}

	/**
	 * 
	 * <p>Adds realtions between current entity and given ones.</p>
	 * @param entities to add, not <code>null</code>;
	 * @return <code>true</code> if all entities added, 
	 * <code>false</code> otherwise;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean idoAddTo(Collection<? extends IDOEntity> entities) {
		if (ListUtil.isEmpty(entities)) {
			return false;
		}
		
		for (IDOEntity entity: entities) {
			try {
				idoAddTo(entity);
				getLogger().info("Relation between " + 
						getClass().getName() + " by id: '" + getPrimaryKey() + "' and " + 
						entity.getClass().getName() + " by id: '" + entity.getPrimaryKey() + "' added!");
			} catch (IDOAddRelationshipException e) {
				getLogger().log(Level.WARNING, 
						"Failed to add relation between " + getClass().getName() +
						" by id: '" + getPrimaryKey() + "' and " + 
						entity.getClass().getName() + " by id: '" + entity.getPrimaryKey() + 
						"' cause of: ", e);
				return false;
			}
		}

		return true;
	}

	protected void idoAddTo(IDOEntity entity, String middleTableName) throws IDOAddRelationshipException {
		try {
			idoAddTo(middleTableName, entity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName(), entity.getPrimaryKey());
		}
		catch (Exception e) {
			throw new IDOAddRelationshipException(e, this);
		}
	}

	/**
	 *
	 * <p>Gracefully executes {@link Statement#executeUpdate(String)} with
	 * default {@link Connection}</p>
	 * @param sqlQuery to execute. UPDATE, DELETE, INSERT statements only,
	 * not <code>null</code>;
	 * @return <code>true</code> if executed, <code>false</code> otherwise;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean executeUpdate(String sqlQuery) {
		if (StringUtil.isEmpty(sqlQuery)) {
			return Boolean.FALSE;
		}

		Connection connection = null;
		Statement statement = null;

		/* Opening connection */
		try {
			connection = getConnection();
			statement = connection.createStatement();
		} catch (SQLException e) {
			getLogger().log(Level.WARNING,
					"Failed to create connection cause of:", e);
			return Boolean.FALSE;
		}

		logSQL(sqlQuery);

		/* Executing query */
		boolean result = Boolean.FALSE;
		try {
			statement.executeUpdate(sqlQuery);
			result = Boolean.TRUE;
		} catch (SQLException e) {
			getLogger().log(Level.WARNING, "Failed to perform update cause of:", e);
		}

		/* Closing */
		try {
			statement.close();
		} catch (SQLException e) {
			getLogger().log(Level.WARNING, "Failed to close connection, cause of: ", e);
		}

		freeConnection(connection);

		return result;
	}

	/**
	 *
	 * <p>Inserts given primary keys of entities into required relation table.</p>
	 * @param entities to add, not <code>null</code>;
	 * @param relatedTableName is name of relation table with this entity,
	 * not <code>null</code>;
	 * @return <code>true</code> if successfully updated, <code>false</code>
	 * otherwise;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean idoAddTo(Collection<? extends IDOEntity> entities,
			String relatedTableName) {
		if (StringUtil.isEmpty(relatedTableName) || ListUtil.isEmpty(entities)) {
			return Boolean.FALSE;
		}

		/* Creating query */
		StringBuilder query = new StringBuilder();
		try {
			query.append("INSERT INTO ").append(relatedTableName).append(CoreConstants.SPACE)
			.append(CoreConstants.BRACKET_LEFT)
			.append(getIDColumnName()).append(CoreConstants.COMMA).append(entities.iterator().next().getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName())
			.append(CoreConstants.BRACKET_RIGHT).append(CoreConstants.SPACE)
			.append("VALUES ");
		} catch (IDOCompositePrimaryKeyException e1) {
			getLogger().log(Level.WARNING,
					"Failed to get primary key of insertable entity, cause of: ", e1);
			return Boolean.FALSE;
		}

		for (Iterator<? extends IDOEntity> iterator = entities.iterator(); iterator.hasNext();) {
			query.append(CoreConstants.BRACKET_LEFT)
			.append(getPrimaryKeyValueSQLString()).append(CoreConstants.COMMA)
			.append(iterator.next().getPrimaryKey().toString())
			.append(CoreConstants.BRACKET_RIGHT);

			if (iterator.hasNext()) {
				query.append(CoreConstants.COMMA).append(CoreConstants.SPACE);
			}
		}

		return executeUpdate(query.toString());
	}

	/**
	 * *Default insert behavior with a many-to-many relationship
	 *  *
	 * @throws IDOAddRelationshipException
	 *           if there is no relationship with the given entity or there is an
	 *           error accessing it
	 */
	private void idoAddTo(String middleTableName, String sqlFieldName, Object primaryKey) throws IDOAddRelationshipException {
		/**
		 * @todo Change implementation
		 */
		try {
			Connection conn = null;
			Statement Stmt = null;
			try {
				conn = getConnection(getDatasource());
				Stmt = conn.createStatement();
				// String sql = "insert into
				// "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+")
				// values("+getID()+","+entityToAddTo.getID()+")";
				String sql = null;
				// try
				// {
				sql = "insert into " + middleTableName + "(" + getIDColumnName() + "," + sqlFieldName + ") values(" + getPrimaryKeyValueSQLString() + "," + getKeyValueSQLString(primaryKey) + ")";
				/*
				 * } catch (RemoteException rme) { throw new
				 * SQLException("RemoteException in addTo, message: " +
				 * rme.getMessage()); }
				 */

				// debug("statement: "+sql);
				Stmt.executeUpdate(sql);
			}
			finally {
				if (Stmt != null) {
					Stmt.close();
				}
				if (conn != null) {
					freeConnection(getDatasource(), conn);
				}
			}

		}
		catch (Exception ex) {
			// ex.printStackTrace();
			throw new IDOAddRelationshipException(ex, this);
		}
	}

	/**
	 * Method to execute an explicit update on the table of this entity bean <br>
	 * <br>
	 * This method then throws away all cache associated with all instances of
	 * <b>THIS</b> entity bean class.
	 *
	 * @throws IDOException
	 *           if there is an error with the query or accessing the datastore
	 *
	 */
	protected boolean idoExecuteTableUpdate(String sqlUpdateQuery) throws IDOException {
		try {
			if (SimpleQuerier.executeUpdate(sqlUpdateQuery, this.getDatasource(), false)) {
				synchronized (IDOContainer.getInstance().getBeanCache(getDatasource(),this.getInterfaceClass())) {
					flushQueryCache();
					flushBeanCache();
				}
				return true;
			}
			return false;
		}
		catch (SQLException sqle) {
			throw new IDOException(sqle, this);
		}
	}

	/**
	 * Method to execute an explicit update on many (undetermined) tables in an
	 * SQL datastore. <br>
	 * <br>
	 * This method then flushes all cache associated with all instances of <b>ALL</b>
	 * entity bean classes.
	 *
	 * @throws IDOException
	 *           if there is an error with the query or accessing the datastore
	 *
	 */
	protected boolean idoExecuteGlobalUpdate(String sqlUpdateQuery) throws IDOException {
		try {
			return SimpleQuerier.executeUpdate(sqlUpdateQuery, getDatasource(), true);
		}
		catch (SQLException sqle) {
			throw new IDOException(sqle, this);
		}
	}

	/**
	 * This method will be changed to return true for "non-legacy" ido beans.<br>
	 * Can be overrided to do an insert (insertForCreate()) when ejbCreate(xx) is
	 * called
	 */
	protected boolean doInsertInCreate() {
		return false;
	}

	protected Object insertForCreate() throws CreateException {
		try {
			insert();
			return this.getPrimaryKey();
		}
		catch (SQLException sqle) {
			logError("Error in insertForCreate() for PrimaryKey=" + this.getPrimaryKey());
			sqle.printStackTrace();
			throw new IDOCreateException(sqle);
		}
	}

	@Override
	public IDOEntityDefinition getEntityDefinition() {
		return getGenericEntityDefinition();
	}

	private boolean isBeanCachingActive() {
		try {
			if (IDOContainer.getInstance().beanCachingActive(getInterfaceClass())) {
				return true;
			}
			return false;
		}
		catch (Exception ex) {
			return false;
		}
	}

	protected IDOUtil getIDOUtil() {
		return IDOUtil.getInstance();
	}

	@Override
	public boolean isIdentical(EJBLocalObject obj) {
		return this.equals(obj);
	}

	/**
	 * Method getPrimaryKeyValueSQLString. Gets the primarykey for this record and
	 * returns it value to be added to an sql query.<br>
	 * e.g. if the primary key of of the type String this method returns the value
	 * as = 'value' but if it is an integer as = value .
	 *
	 * @return String
	 */
	public String getPrimaryKeyValueSQLString() {
		return getKeyValueSQLString(getPrimaryKeyValue());
	}

	/**
	 * Method getPrimaryKeyValueSQLString.
	 *
	 * @return String
	 */
	/**
	 * Method getKeyValueSQLString. Returns the value to be added to an sql query.<br>
	 * e.g. if the value is of the type String this method returns the value as =
	 * 'value' but if it is an integer as = value .
	 *
	 * @param keyValue
	 * @return String
	 */
	public static String getKeyValueSQLString(Object value) {
		if (value != null) {
			if (value instanceof String) {
				return "'" + value.toString() + "'";
			}
			else {
				return value.toString();
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Method isIdColumnValueNotEmpty gets the primarykey value and uses
	 * isColumnValueNotEmpty to check if it is empty or not
	 *
	 * @return boolean
	 */
	public boolean isIdColumnValueNotEmpty() {
		String value = getPrimaryKeyValueSQLString();
		return isColumnValueNotEmpty(value);
	}

	/**
	 * Method isColumnNotEmpty. This methods checks if the value is null,-1,'-1'
	 * or "" and return false or true.
	 *
	 * @param value
	 * @return boolean return true if the value is non of the above values
	 */
	public static boolean isColumnValueNotEmpty(String value) {
		// not sure if the =0 check is needed
		if ((value != null) && (!value.equals("-1")) && (!value.equals("'-1'")) && (!value.equals("")) && (!value.equals("0"))) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets a new empty query
	 *
	 * @return IDOQuery which is new and emtpy
	 */
	protected IDOQuery idoQuery() {
		IDOQuery query = new IDOQuery();
		query.setDataStore(DatastoreInterface.getInstance(this));
		return query;
	}

	protected SelectQuery idoSelectQuery() {
		SelectQuery query = new SelectQuery(idoQueryTable());
		query.addColumn(new WildCardColumn(idoQueryTable()));
		return query;
	}

	protected SelectQuery idoSelectPKQuery() {
		SelectQuery query = new SelectQuery(idoQueryTable());
		query.addColumn(new Column(idoQueryTable(), getIDColumnName()));
		return query;
	}

	protected Table idoQueryTable() {
		if (this.idoQueryTable == null) {
			this.idoQueryTable = new Table(this);
		}
		return this.idoQueryTable;
	}

	/**
	 *
	 * @param sqlQuery
	 * @return IDOQuery with sqlQuery as the query
	 */
	protected IDOQuery idoQuery(String sqlQuery) {
		IDOQuery query = new IDOQuery(sqlQuery);
		query.setDataStore(DatastoreInterface.getInstance(this));
		return query;
	}

	/**
	 * @return IDOQuery With a prefixed select statement from this entity record.
	 */
	protected IDOQuery idoQueryGetSelect() {
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this.getEntityName());
		return query;
	}

	/**
	 * @return IDOQuery With a prefixed select count statement from this entity
	 *         record.
	 */
	protected IDOQuery idoQueryGetSelectCount() {
		IDOQuery query = idoQuery();
		query.appendSelectCountFrom(this.getEntityName());
		return query;
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 *
	 * @param query
	 *          an IDOQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException
	 *           if there is an error with the query.
	 */
	protected <PK> Collection<PK> idoFindPKsByQuery(IDOQuery query) throws FinderException {
		return idoFindPKsByQuery(query, -1, -1);
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 *
	 * @param query
	 *          an IDOQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException
	 *           if there is an error with the query.
	 */
	protected <PK> Collection<PK> idoFindPKsByQuery(SelectQuery query) throws FinderException {
		if (getEntityDefinition().isUseFinderCollectionPrefetch()) {
			int prefetchSize = getEntityDefinition().getFinderCollectionPrefetchSize();
			return idoFindPKsByQueryUsingLoadBalance(query, prefetchSize);
		} else {
			return idoFindPKsByQuery(query, -1, -1);
		}
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 *
	 * @param query
	 *          an IDOQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException
	 *           if there is an error with the query.
	 */
	protected <PK> Collection<PK> idoFindPKsByQuery(IDOQuery query, int returningNumberOfEntities) throws FinderException {
		return idoFindPKsByQuery(query, returningNumberOfEntities, -1);
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 *
	 * @param query
	 *          an SelectQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException
	 *           if there is an error with the query.
	 */
	protected <PK> Collection<PK> idoFindPKsByQuery(SelectQuery query, int returningNumberOfEntities) throws FinderException {
		return idoFindPKsByQuery(query, returningNumberOfEntities, -1);
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 *
	 * @param query
	 *          an IDOQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException
	 *           if there is an error with the query.
	 */
	protected <PK> Collection<PK> idoFindPKsByQuery(IDOQuery query, int returningNumberOfEntities, int startingEntry) throws FinderException {
		return idoFindPKsBySQL(query.toString(), returningNumberOfEntities, startingEntry, null);
	}

	/**
	 * Method idoFindPKsByQuery. Gets the result of the query.
	 *
	 * @param query
	 *          an SelectQuery for this entity.
	 * @return Collection of Primary keys which is a result from the query.
	 * @throws FinderException
	 *           if there is an error with the query.
	 */
	protected <PK> Collection<PK> idoFindPKsByQuery(SelectQuery query, int returningNumberOfEntities, int startingEntry) throws FinderException {
		return idoFindPKsBySQL(query.toString(), returningNumberOfEntities, startingEntry, query);
	}

	/**
	 * Method idoFindOnePKByQuery. Gets the one primary key of the query or the
	 * first result if there are many results.*
	 *
	 * @param query
	 *          an IDOQuery for this entity.
	 * @return Object which is the primary key of the object found from the query.
	 * @throws FinderException
	 *           if nothing found or there is an error with the query.
	 */
	protected <PK> PK idoFindOnePKByQuery(IDOQuery query) throws FinderException {
		return idoFindOnePKBySQL(query.toString(), null);
	}

	/**
	 * Method idoFindOnePKBySelectQuery. Gets the one primary key of the query or
	 * the first result if there are many results.*
	 *
	 * @param query
	 *          an IDOQuery for this entity.
	 * @return Object which is the primary key of the object found from the query.
	 * @throws FinderException
	 *           if nothing found or there is an error with the query.
	 */
	protected Object idoFindOnePKByQuery(SelectQuery query) throws FinderException {
		return idoFindOnePKBySQL(query.toString(), query);
	}

	/**
	 * Default implimentation for IDOReportableEntity
	 *
	 * @param field
	 * @return
	 */
	public Object getFieldValue(IDOReportableField field) {
		return this.getColumnValue(field.getName());
	}

	/**
	 * Convenience method to get a reference to a home for an entity
	 *
	 * @param entityClass
	 *          The entity interface class to get a reference to its home.
	 * @return the home instance
	 */
	protected IDOHome getIDOHome(Class entityClass) throws IDOLookupException {
		return IDOLookup.getHome(entityClass);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getEntityName());
		buffer.append(getPrimaryKey().hashCode());
		return buffer.toString().hashCode();
	}

	// STANDARD LOGGING METHODS:

	/**
	 * Logs out to the default log level (which is by default INFO)
	 *
	 * @param msg
	 *          The message to log out
	 */
	protected void log(String msg) {
		// System.out.println(string);
		getLogger().log(getDefaultLogLevel(), msg);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the
	 * default Logger
	 *
	 * @param e
	 *          The Exception to log out
	 */
	protected void log(Exception e) {
		LoggingHelper.logException(e, this, getLogger(), getErrorLogLevel());
	}

	/**
	 * Logs out to the specified log level to the default Logger
	 *
	 * @param level
	 *          The log level
	 * @param msg
	 *          The message to log out
	 */
	protected void log(Level level, String msg) {
		// System.out.println(msg);
		getLogger().log(level, msg);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the
	 * default Logger
	 *
	 * @param msg
	 *          The message to log out
	 */
	protected void logError(String msg) {
		// System.err.println(msg);
		getLogger().log(getErrorLogLevel(), msg);
	}

	/**
	 * Logs out to the debug log level (which is by default FINER) to the default
	 * Logger
	 *
	 * @param msg
	 *          The message to log out
	 */
	protected void logDebug(String msg) {
		System.err.println(msg);
		getLogger().log(getDebugLogLevel(), msg);
	}

	/**
	 * Logs out to the SEVERE log level to the default Logger
	 *
	 * @param msg
	 *          The message to log out
	 */
	protected void logSevere(String msg) {
		// System.err.println(msg);
		getLogger().log(Level.SEVERE, msg);
	}

	/**
	 * Logs out to the WARNING log level to the default Logger
	 *
	 * @param msg
	 *          The message to log out
	 */
	protected void logWarning(String msg) {
		// System.err.println(msg);
		getLogger().log(Level.WARNING, msg);
	}

	/**
	 * Logs out to the CONFIG log level to the default Logger
	 *
	 * @param msg
	 *          The message to log out
	 */
	protected void logConfig(String msg) {
		// System.err.println(msg);
		getLogger().log(Level.CONFIG, msg);
	}

	/**
	 * Logs out to the debug log level to the default Logger
	 *
	 * @param msg
	 *          The message to log out
	 */
	protected void debug(String msg) {
		logDebug(msg);
	}

	/**
	 * Gets the default Logger. By default it uses the package and the class name
	 * to get the logger.<br>
	 * This behaviour can be overridden in subclasses.
	 *
	 * @return the default Logger
	 */
	protected Logger getLogger() {
		return Logger.getLogger(this.getClass().getName());
	}

	/**
	 * Gets the log level which messages are sent to when no log level is given.
	 *
	 * @return the Level
	 */
	protected Level getDefaultLogLevel() {
		return Level.INFO;
	}

	/**
	 * Gets the log level which debug messages are sent to.
	 *
	 * @return the Level
	 */
	protected Level getDebugLogLevel() {
		return Level.FINER;
	}

	/**
	 * Gets the log level which error messages are sent to.
	 *
	 * @return the Level
	 */
	protected Level getErrorLogLevel() {
		return Level.WARNING;
	}

	public void addIndex(String field) {
		getGenericEntityDefinition().addIndex(field);
	}

	public void addIndex(String name, String field) {
		getGenericEntityDefinition().addIndex(name, field);
	}

	public void addIndex(String name, String[] fields) {
		getGenericEntityDefinition().addIndex(name, fields);
	}

	public void hasAutoIncrement(boolean autoIncrement) {
		getEntityDefinition().setHasAutoIncrementColumn(autoIncrement);
	}

	public boolean getIfAutoIncrement() {
		return getEntityDefinition().hasAutoIncrementColumn();
	}

	// ENTITY SPECIFIC LOG MEHTODS:

	// /**
	// * This method outputs the outputString to System.out if the Application
	// property
	// * "debug" is set to "TRUE"
	// */
	// public void debug(String outputString) {
	// if (isDebugActive()) {
	// //System.out.println("[DEBUG] \"" + outputString + "\" : " +
	// this.getEntityName());
	// }
	// }
	/**
	 * This method logs the sqlCommand if the Log Level is low enough
	 */
	public void logSQL(String sqlCommand) {
		log(Level.FINEST, sqlCommand);
		// if (isDebugActive()) {
		// System.out.println("[DEBUG] \"" + outputString + "\" : " +
		// this.getEntityName());
		// }
	}

	protected boolean isDebugActive() {
		return getIWMainApplication().getSettings().isDebugActive();
	}

	public IWMainApplication getIWMainApplication() {
		return IWMainApplication.getDefaultIWMainApplication();
	}

	// END STANDARD LOGGING METHODS

	/**
	 * Adds a unique id string column to this entity that is filled with a
	 * generated id when the entity is first stored.<br>
	 * The UID is a 36 character long string (128bit). The default implementation
	 * generates the string with a combination of a <br>
	 * dummy ip address and a time based random number generator.<br>
	 * For more info see the JUG project,
	 * http://www.doomdark.org/doomdark/proj/jug/ An example uid:
	 * ac483688-b6ed-4f45-ac64-c105e599d482
	 */
	protected void addUniqueIDColumn() {
		addAttribute(getUniqueIdColumnName(), "A generated unique id do not change manually!", String.class, 36);
		setUnique(getUniqueIdColumnName(), true);
	}

	/**
	 * A convenience method that you can use if your entity has the UniqueID
	 * column.<br>
	 * If you make your IDO interface extend the UniqueIDCapable interface you get
	 * this method added to your bean.
	 *
	 * @return The unique id string of the entity if it has it, otherwise null
	 */
	public String getUniqueId() {
		if (hasUniqueIDColumn()) {
			return getStringColumnValue(getUniqueIdColumnName());
		}
		return null;
	}

	/**
	 * <p>
	 * Gets the BeanCache instance for this entity and datasource.
	 * </p>
	 * @return
	 */
	protected IDOBeanCache getBeanCache(){
		return IDOContainer.getInstance().getBeanCache(getDatasource(), getInterfaceClass());
	}
	/**
	 * <p>
	 * Gets the collection of cached entities for this entity and datasource
	 * </p>
	 * @return
	 */
	protected Collection getCachedEntities(){
		return getBeanCache().getCachedEntities();
	}
}