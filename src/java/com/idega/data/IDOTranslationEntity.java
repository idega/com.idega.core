package com.idega.data;

import com.idega.core.localisation.data.ICLocale;


public interface IDOTranslationEntity extends com.idega.data.IDOEntity
{
	public final static String COLUMNNAME_TRANSLATED_ENTITY_ID = "TRANSLATED_ENTITY_ID";
	public final static String COLUMNNAME_LOCALE = "LOCALE_ID";
	
	public void setPrimaryKey(IDOEntity mainEntity, ICLocale locale);
	public void setTransletedEntity(IDOEntity translatedEntity);
	public void setLocale(ICLocale locale);
}
