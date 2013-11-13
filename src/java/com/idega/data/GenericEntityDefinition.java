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
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private Boolean _isBeanCachingActive = null;
	private int maxBeanCachedEntities=-1;
	private boolean allRecordsCached=false;
	private boolean useFinderCollectionPrefetch=false;
	private int finderCollectionPrefetchSize=1000;

	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 *
	 */
	public GenericEntityDefinition() {
		super();
		this._pkDefinition = new PrimaryKeyDefinition(this);
	}

	public void addManyToManyRelatedEntity(IDOEntityDefinition def) {
		if (!containsEquivalentRelation(def)) {
			int length = this._manyToManyRelatedEntities.length;
			IDOEntityDefinition[] tempArray = new IDOEntityDefinition[length + 1];
			System.arraycopy(this._manyToManyRelatedEntities, 0, tempArray, 0, length);
			tempArray[length] = def;
			this._manyToManyRelatedEntities = tempArray;
		} else {
			//System.err.println(this.getUniqueEntityName() + ": already contains relation to " + def.getUniqueEntityName());
		}
	}

	protected boolean containsEquivalentRelation(IDOEntityDefinition def) {
		for (int i = 0; i < this._manyToManyRelatedEntities.length; i++) {
			if (this._manyToManyRelatedEntities[i].getSQLTableName().equals(def.getSQLTableName())) {
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
				int length = this._fields.length;
				EntityAttribute[] tempArray = new EntityAttribute[length + 1];
				System.arraycopy(this._fields, 0, tempArray, 0, length);
				tempArray[length] = field;
				if(field.getDeclaredEntity()== null){
					field.setDeclaredEntity(this);
				}
				this._fields = tempArray;
			} else {
				logger.warning(this.getUniqueEntityName() + ": already contains equivalent field \"" + field.getSQLFieldName() + "\"");
			}
		} catch (ClassCastException e) {
			logger.log(Level.SEVERE, "ClassCastException: "+this.getClass()+" only supports "+ EntityAttribute.class +" as "+IDOEntityField.class);
			throw e;
		}
	}

	protected boolean containsEquivalentField(IDOEntityField field) {
		for (int i = 0; i < this._fields.length; i++) {
			if (this._fields[i].getSQLFieldName().equals(field.getSQLFieldName())) {
				return true;
			}
		}
		return false;
	}

	public void setFieldAsPartOfPrimaryKey(IDOEntityField field){
		this._pkDefinition.addFieldEntity(field);
	}

	public void setUniqueEntityName(String name) {
		this._uniqueEntityName = name;
	}

	public void setSQLTableName(String name) {
		this._sqlTableName = name;
	}


	@Override
	public HashMap getIndexes() throws NoIndexException{
		if (this._indexes == null) {
			throw new NoIndexException("No Indexes");
		}
		return this._indexes;
	}

	public void addIndex(String field) {
		addIndex(field+"_index", field);
	}

	public void addIndex(String name, String field) {
		addIndex(name, new String[] {field});
	}

	public void addIndex(String name, String[] fields) {
		if (name != null && !name.equals("") && fields != null && fields.length > 0) {
			if (this._indexes == null) {
				this._indexes = new HashMap();
			}
			this._indexes.put(name, fields);
		} else {
			throw new IllegalArgumentException("Name must and fields must bet be set");
		}
	}


	public EntityAttribute getEntityAttribute(String attributeName){
		for (int i = 0; i < this._fields.length; i++) {
			if (this._fields[i].getSQLFieldName().equals(attributeName)) {
				return this._fields[i];
			}
		}
		return null;
	}

	public Collection getEntityFieldsCollection(){
		Vector coll = new Vector(this._fields.length);
		for (int i = 0; i < this._fields.length; i++) {
			coll.add(this._fields[i]);
		}
		return coll;
	}


	public void setInterfaceClass(Class interfaceClass){
		this._interfaceClass = interfaceClass;
	}


	// IDOEntityDefinition begins //

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getUniqueEntityName()
	 */
	@Override
	public String getUniqueEntityName() {
		return this._uniqueEntityName;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getSQLTableName()
	 */
	@Override
	public String getSQLTableName() {
		return this._sqlTableName;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getManyToManyRelatedEntities()
	 */
	@Override
	public IDOEntityDefinition[] getManyToManyRelatedEntities() {
		return this._manyToManyRelatedEntities;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getFields()
	 */
	@Override
	public IDOEntityField[] getFields() {
		return this._fields;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getPrimaryKeyClass()
	 */

	@Override
	public IDOPrimaryKeyDefinition getPrimaryKeyDefinition() {
		return this._pkDefinition;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#getInterfaceClass()
	 */
	@Override
	public Class getInterfaceClass() {
		return this._interfaceClass;
	}

	public Class getBeanClass() {
		if(this._beanClass == null){
			this._beanClass = IDOLookup.getBeanClassFor(this._interfaceClass);
		}
		return this._beanClass;
	}

	@Override
	public IDOEntityField[] findFieldByRelation(Class interfaceClass){
		ArrayList<IDOEntityField> list = new ArrayList<IDOEntityField>();
		for (int i = 0; i < this._fields.length; i++) {
			if(this._fields[i].getRelationShipClass().equals(interfaceClass)){
				list.add(this._fields[i]);
			}
		}
		if(list.size() > 0) {
			return list.toArray(new IDOEntityField[list.size()]);
		}
		else {
			return new IDOEntityField[0];
		}
	}

	// IDOEntityDefinition ends //

	@Override
	public IDOEntityField findFieldByUniqueName(String name){
		for (int i = 0; i < this._fields.length; i++) {
			if(this._fields[i].getUniqueFieldName().equalsIgnoreCase(name)){
				return 	this._fields[i];
			}
		}
		return null;
	}

	public boolean hasField(String uniqueName){
		IDOEntityField field = findFieldByUniqueName(uniqueName);
		if(field!=null){
			return true;
		}
		else {
			return false;
		}
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IDOEntityDefinition) {
			IDOEntityDefinition compareObject = (IDOEntityDefinition) obj;
			return compareObject.getSQLTableName().equals(this._sqlTableName) && this._interfaceClass.equals(compareObject.getInterfaceClass());
		}
		return false;
	}

	/*
	 * @see com.idega.data.IDOEntityDefinition#getMiddleTableNameForRelation(java.lang.String)
	 */
	@Override
	public String getMiddleTableNameForRelation(String relatedTable) {
		EntityRelationship rel = EntityControl.getManyToManyRelationShip(this._sqlTableName, relatedTable);
		if (rel != null) {
			return rel.getTableName();
		} else {
			return null;
		}
	}
	/**
	 * @return Returns the _hasAutoIncrementColumn.
	 */
	@Override
	public boolean hasAutoIncrementColumn() {
		return this._hasAutoIncrementColumn;
	}
	/**
	 * @param autoIncrementColumn The _hasAutoIncrementColumn to set.
	 */
	@Override
	public void setHasAutoIncrementColumn(boolean autoIncrementColumn) {
		this._hasAutoIncrementColumn = autoIncrementColumn;
	}

	/**
	 * @return Boolean.TRUE if active by default, Boolean.FALSE if inactive by default, null if system-default
	 */
	@Override
	public Boolean isBeanCachingActive() {
		return this._isBeanCachingActive;
	}

	@Override
	public void setBeanCachingActiveByDefault(boolean value){
		setBeanCachingActiveByDefault(value,200);
	}

	@Override
	public void setBeanCachingActiveByDefault(boolean value, int maxBeanCachedEntities){
		this._isBeanCachingActive = ((value)?Boolean.TRUE:Boolean.FALSE);
		this.maxBeanCachedEntities=maxBeanCachedEntities;
	}

	@Override
	public int getMaxCachedBeans(){
		return this.maxBeanCachedEntities;
	}


	/**
	 * @return the allRecordsCached
	 */
	@Override
	public boolean isAllRecordsCached() {
		return this.allRecordsCached;
	}


	/**
	 * @param allRecordsCached the allRecordsCached to set
	 */
	@Override
	public void setAllRecordsCached(boolean allRecordsCached) {
		this.allRecordsCached = allRecordsCached;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntityDefinition#setBeanCachingActiveByDefault(boolean, boolean)
	 */
	@Override
	public void setBeanCachingActiveByDefault(boolean beanCachingEnabled, boolean allRecordsCached) {
		this.setBeanCachingActiveByDefault(beanCachingEnabled);
		if(beanCachingEnabled&&allRecordsCached){
			setAllRecordsCached(allRecordsCached);
			int maxBeanCachedEntities = 2000000000;
			setMaxBeanCachedEntities(maxBeanCachedEntities);
		}
	}


	/**
	 * @return the maxBeanCachedEntities
	 */
	protected int getMaxBeanCachedEntities() {
		return this.maxBeanCachedEntities;
	}


	/**
	 * @param maxBeanCachedEntities the maxBeanCachedEntities to set
	 */
	protected void setMaxBeanCachedEntities(int maxBeanCachedEntities) {
		this.maxBeanCachedEntities = maxBeanCachedEntities;
	}


	/**
	 * @return the finderCollectionPrefetchSize
	 */
	@Override
	public int getFinderCollectionPrefetchSize() {
		return this.finderCollectionPrefetchSize;
	}


	/**
	 * @param finderCollectionPrefetchSize the finderCollectionPrefetchSize to set
	 */
	@Override
	public void setFinderCollectionPrefetchSize(int finderCollectionPrefetchSize) {
		this.finderCollectionPrefetchSize = finderCollectionPrefetchSize;
	}


	/**
	 * @return the useFinderCollectionPrefetch
	 */
	@Override
	public boolean isUseFinderCollectionPrefetch() {
		return this.useFinderCollectionPrefetch;
	}


	/**
	 * @param useFinderCollectionPrefetch the useFinderCollectionPrefetch to set
	 */
	@Override
	public void setUseFinderCollectionPrefetch(boolean useFinderCollectionPrefetch) {
		this.useFinderCollectionPrefetch = useFinderCollectionPrefetch;
	}
}
