package com.idega.data;

import java.io.InputStream;
import java.sql.Timestamp;

import com.idega.core.localisation.data.ICLocale;


public interface IDOTranslationEntity extends com.idega.data.IDOEntity
{
	public final static String UFN_TRANSLATED_ENTITY_ID = "TRANSLATED_ENTITY_ID";
	public final static String UFN_LOCALE = "LOCALE_ID";
	
	public void setPrimaryKey(IDOEntity mainEntity, ICLocale locale);
	public void setTransletedEntity(IDOEntity translatedEntity);
	public void setLocale(ICLocale locale);
	
	
	//Setter
	void setColumn(IDOEntityField field, Object value);
	public void setColumn(IDOEntityField field, Boolean value);
	public void setColumn(IDOEntityField field, boolean value);
	public void setColumn(IDOEntityField field, char value);
	public void setColumn(IDOEntityField field, Double value);
	public void setColumn(IDOEntityField field, double value);
	public void setColumn(IDOEntityField field, Float value);
	public void setColumn(IDOEntityField field, float value);
	public void setColumn(IDOEntityField field, InputStream value);
	public void setColumn(IDOEntityField field, Integer value);
	public void setColumn(IDOEntityField field, int value);
	
	//Getters begin
	Object getColumnValue(IDOEntityField field);
	
	String getStringColumnValue(IDOEntityField field);

	int getIntColumnValue(IDOEntityField field);
	
	boolean getBooleanColumnValue(IDOEntityField field);
	
	Timestamp getTimestampColumnValue(IDOEntityField field);
	//Getters end
	
}
