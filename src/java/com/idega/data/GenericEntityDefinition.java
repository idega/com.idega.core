/*
 * Created on 25.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

/**
 * Title:		GenericEntityDefinition
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class GenericEntityDefinition implements IDOEntityDefinition {

	private String _uniqueEntityName = null;
	private String _sqlTableName = null;
	private IDOEntityDefinition[] _manyToManyRelatedEntities = new IDOEntityDefinition[0];
	private EntityAttribute[] _fields = new EntityAttribute[0];
	private PrimaryKeyDefinition _pkDefinition = null;
	private HashMap _indexes = null;
	private Class _interfaceClass=null;
	private Class _beanClass = null;
	private boolean _hasAutoIncrementColumn = true;
	/**
	 * 
	 */
	public GenericEntityDefinition() {
		super();
		_pkDefinition = new PrimaryKeyDefinition(this);
	}

	public void addManyToManyRelatedEntity(IDOEntityDefinition def) {
		if (!containsEquivalentRelation(def)) {
			int length = _manyToManyRelatedEntities.length;
			IDOEntityDefinition[] tempArray = new IDOEntityDefinition[length + 1];
			System.arraycopy(_manyToManyRelatedEntities, 0, tempArray, 0, length);
			tempArray[length] = def;
			_manyToManyRelatedEntities = tempArray;
		} else {
			//System.err.println(this.getUniqueEntityName() + ": already contains relation to " + def.getUniqueEntityName());
		}
	}

	protected boolean containsEquivalentRelation(IDOEntityDefinition def) {
		for (int i = 0; i < _manyToManyRelatedEntities.length; i++) {
			if (_manyToManyRelatedEntities[i].getSQLTableName().equals(def.getSQLTableName())) {
				return true;
			}
		}
		return false;
	}

	public void addFieldEntity(IDOEntityField eField) {
		
		try {
			EntityAttribute field = (EntityAttribute)eField;
			if(field.isPartOfPrimaryKey()){
				setFieldAsPartOfPrimaryKey(field);
			}
			if (!containsEquivalentField(field)) {
				int length = _fields.length;
				EntityAttribute[] tempArray = new EntityAttribute[length + 1];
				System.arraycopy(_fields, 0, tempArray, 0, length);
				tempArray[length] = (EntityAttribute)field;
				if(field.getDeclaredEntity()== null){
					((EntityAttribute)field).setDeclaredEntity(this);
				}
				_fields = tempArray;
			} else {
				System.err.println(this.getUniqueEntityName() + ": already contains equivalent field \"" + field.getSQLFieldName() + "\"");
			}
		} catch (ClassCastException e) {
			System.err.println("ClassCastException: "+this.getClass()+" only supports "+ EntityAttribute.class +" as "+IDOEntityField.class);
			throw e;
		}
	}

	protected boolean containsEquivalentField(IDOEntityField field) {
		for (int i = 0; i < _fields.length; i++) {
			if (_fields[i].getSQLFieldName().equals(field.getSQLFieldName())) {
				return true;
			}
		}
		return false;
	}
	
	public void setFieldAsPartOfPrimaryKey(IDOEntityField field){
		_pkDefinition.addFieldEntity(field);
	}

	public void setUniqueEntityName(String name) {
		_uniqueEntityName = name;
	}

	public void setSQLTableName(String name) {
		_sqlTableName = name;
	}
	
	
	public HashMap getIndexes() throws NoIndexException{
		if (_indexes == null) {
			throw new NoIndexException("No Indexes");
		}
		return _indexes;
	}

	public void addIndex(String field) {
		addIndex(field+"_index", field);
	}
	
	public void addIndex(String name, String field) {
		addIndex(name, new String[] {field});
	}
	
	public void addIndex(String name, String[] fields) {
		if (name != null && !name.equals("") && fields != null && fields.length > 0) {
			if (_indexes == null) {
				_indexes = new HashMap();
			}
			_indexes.put(name, fields);
		} else {
			throw new IllegalArgumentException("Name must and fields must bet be set");
		}
	}
	
	
	public EntityAttribute getEntityAttribute(String attributeName){
		for (int i = 0; i < _fields.length; i++) {
			if (_fields[i].getSQLFieldName().equals(attributeName)) {
				return _fields[i];
			}
		}
		return null;
	}
	
	public Collection getEntityFieldsCollection(){
		Vector coll = new Vector(_fields.length);
		for (int i = 0; i < _fields.length; i++) {
			coll.add(_fields[i]);
		}
		return coll;
	}
	
	
	public void setInterfaceClass(Class interfaceClass){
		_interfaceClass = interfaceClass;
	}
	

	// IDOEntityDefinition begins //
	
	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getUniqueEntityName()
	 */
	public String getUniqueEntityName() {
		return _uniqueEntityName;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getSQLTableName()
	 */
	public String getSQLTableName() {
		return _sqlTableName;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getManyToManyRelatedEntities()
	 */
	public IDOEntityDefinition[] getManyToManyRelatedEntities() {
		return _manyToManyRelatedEntities;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getFields()
	 */
	public IDOEntityField[] getFields() {
		return _fields;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getPrimaryKeyClass()
	 */

	public IDOPrimaryKeyDefinition getPrimaryKeyDefinition() {
		return _pkDefinition;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getInterfaceClass()
	 */
	public Class getInterfaceClass() {
		return _interfaceClass;
	}
	
	public Class getBeanClass() {
		if(_beanClass == null){
			_beanClass = IDOLookup.getBeanClassFor(_interfaceClass);
		}
		return _beanClass;
	}
	
	public IDOEntityField[] findFieldByRelation(Class interfaceClass){
		ArrayList list = new ArrayList();
		for (int i = 0; i < _fields.length; i++) {
			if(_fields[i].getRelationShipClass().equals(interfaceClass)){
				list.add(_fields[i]);
			}
		}
		if(list.size() > 0)
			return (IDOEntityField[])list.toArray(new IDOEntityField[list.size()]);
		else
			return new IDOEntityField[0];
	}

	// IDOEntityDefinition ends //

	public IDOEntityField findFieldByUniqueName(String name){
		for (int i = 0; i < _fields.length; i++) {
			if(_fields[i].getUniqueFieldName().equalsIgnoreCase(name)){
				return 	_fields[i];
			}
		}
		return null;
	}
	
	public boolean hasField(String uniqueName){
		IDOEntityField field = findFieldByUniqueName(uniqueName);
		if(field!=null){
			return true;
		}
		else return false;
	}
	
	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof IDOEntityDefinition) {
			IDOEntityDefinition compareObject = (IDOEntityDefinition) obj;
			return compareObject.getSQLTableName().equals(_sqlTableName) && _interfaceClass.equals(compareObject.getInterfaceClass());
		}
		return false;
	}
	
	/* 
	 * @see com.idega.data.IDOEntityDefinition#getMiddleTableNameForRelation(java.lang.String)
	 */
	public String getMiddleTableNameForRelation(String relatedTable) {
		EntityRelationship rel = EntityControl.getManyToManyRelationShip(_sqlTableName, relatedTable);
		if (rel != null) {
			return rel.getTableName();
		} else {
			return null;
		}
	}
	/**
	 * @return Returns the _hasAutoIncrementColumn.
	 */
	public boolean hasAutoIncrementColumn() {
		return _hasAutoIncrementColumn;
	}
	/**
	 * @param autoIncrementColumn The _hasAutoIncrementColumn to set.
	 */
	public void setHasAutoIncrementColumn(boolean autoIncrementColumn) {
		_hasAutoIncrementColumn = autoIncrementColumn;
	}
}
