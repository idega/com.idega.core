/*
 * Created on 13.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;


import java.io.InputStream;
import java.sql.Timestamp;

import javax.ejb.FinderException;

import com.idega.core.localisation.data.ICLocale;


/**
 * Title:		IDOTranslationEntity
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public abstract class IDOTranslationEntityBMPBean extends GenericEntity implements IDOTranslationEntity {
	
	public final static String COLUMNNAME_TRANSLATED_ENTITY_ID = IDOTranslationEntity.UFN_TRANSLATED_ENTITY_ID;
	public final static String COLUMNNAME_LOCALE = IDOTranslationEntity.UFN_LOCALE;
	
	
	
	/**
	 * 
	 */
	public IDOTranslationEntityBMPBean() {
		super();
	}
	

	protected void beforeInitializeAttributes(){
		//TMP - COLUMNNAME_TRANSLATED_ENTITY_ID and COLUMNNAME_LOCALE should be the primarykey
		addAttribute(getIDColumnName());
		
		addManyToOneRelationship(COLUMNNAME_TRANSLATED_ENTITY_ID, "Translated entity id", getTranslatedEntityClass());
		setNullable(COLUMNNAME_TRANSLATED_ENTITY_ID,false);
		addManyToOneRelationship(COLUMNNAME_LOCALE, "Locale id", ICLocale.class);
	}	
	
	protected abstract Class getTranslatedEntityClass();
	
	public String getEntityName(){
		try {
			return IDOLookup.getEntityDefinitionForClass(getTranslatedEntityClass()).getSQLTableName()+"_TR";
		} catch (IDOLookupException e) {
			System.err.println(this.getClass().getName()+"[Error in IDOTranslationEntityBMPBean#getEntityName()]: could not find the entity name because of an IDOLookupException!!!!!");
			return null;
		}
	}
	
	protected Object idoFindTranslation(Object entityToTranslate, ICLocale locale) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(COLUMNNAME_TRANSLATED_ENTITY_ID,entityToTranslate);
		query.appendAndEquals(COLUMNNAME_LOCALE,locale);
		
		return idoFindOnePKByQuery(query);
	}
	
	public void setPrimaryKey(IDOEntity translatedEntity, ICLocale locale){
		setColumn(COLUMNNAME_TRANSLATED_ENTITY_ID,translatedEntity);
		setColumn(COLUMNNAME_LOCALE,locale);
	}
	
	public void setTransletedEntity(IDOEntity translatedEntity){
		setColumn(COLUMNNAME_TRANSLATED_ENTITY_ID,translatedEntity);
	}
	
	public void setLocale(ICLocale locale){
		setColumn(COLUMNNAME_LOCALE,locale);
	}
	
	public Object ejbFindTranslation(IDOEntity translatedEntity, ICLocale locale) throws FinderException{
		return idoFindTranslation(translatedEntity,locale);
	}
	
	
	
	//Setter
	public void setColumn(IDOEntityField field, Object value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, Boolean value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, boolean value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, char value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, Double value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, double value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, Float value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, float value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, InputStream value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, Integer value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	public void setColumn(IDOEntityField field, int value){
		this.setColumn(field.getSQLFieldName(), value);
	}
	
	
	
	//Getters begin
	public Object getColumnValue(IDOEntityField field){
		return getColumnValue(field.getSQLFieldName());
	}
	
	public String getStringColumnValue(IDOEntityField field){
		return getStringColumnValue(field.getSQLFieldName());
	}

	public int getIntColumnValue(IDOEntityField field){
		return getIntColumnValue(field.getSQLFieldName());
	}
	
	public boolean getBooleanColumnValue(IDOEntityField field){
		return getBooleanColumnValue(field.getSQLFieldName());
	}
	
	public Timestamp getTimestampColumnValue(IDOEntityField field){
		return getTimestampColumnValue(field.getSQLFieldName());
	}
	//Getters end
}
