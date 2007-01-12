/*
 * Created on 19.2.2004
 */
package com.idega.data;

import java.io.InputStream;
import java.util.HashMap;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.core.localisation.data.ICLocale;

/**
 * Title:		IDOTranslatedEntity
 * Description:
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public abstract class IDOTranslatedEntityBMPBean extends GenericEntity {
	private HashMap storeMap = new HashMap();
	private IDOEntityDefinition trEntityDef = null;
	public static final String COLUMNNAME_CREATION_LOCALE = "CREATION_LOCALE";	
	

	protected void beforeInitializeAttributes(){
		super.beforeInitializeAttributes();
		addManyToOneRelationship(COLUMNNAME_CREATION_LOCALE, "Locale id", ICLocale.class);
		
	}
	
	public abstract Class getTranslationEntityClass();
	
	public IDOEntityDefinition getTranslationEntityDefinition() throws IDOLookupException{
		if(this.trEntityDef ==null){
			this.trEntityDef = IDOLookup.getEntityDefinitionForClass(getTranslationEntityClass());
		}
		return this.trEntityDef;
	}
	
	protected String getStringColumnValue(IDOEntityField field, ICLocale locale) throws IDOLookupException, FinderException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this, locale);
			} catch (FinderException e) {
				tr = translationHome.findTranslation(this, this.getCreationLocale());
			}
		}
		return tr.getStringColumnValue(field);
	}
	
	protected String getStringColumnValue(IDOEntityField field) throws IDOLookupException, FinderException{
		return getStringColumnValue(field,this.getCreationLocale());
	}
	
	
	protected void setColumn(IDOEntityField field, Object value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, Boolean value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, boolean value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, Double value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, double value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, char value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, Float value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, float value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, InputStream value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, Integer value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	
	protected void setColumn(IDOEntityField field, int value, ICLocale locale) throws IDOLookupException, CreateException{
		IDOTranslationEntity tr = null;
		tr = (IDOTranslationEntity)this.storeMap.get(locale);
		if(tr == null){
			IDOTranslationEntityHome translationHome = (IDOTranslationEntityHome)IDOLookup.getHome(getTranslationEntityClass());
			
			try {
				tr = translationHome.findTranslation(this,locale);
			} catch (FinderException e) {
				tr = translationHome.create();
				tr.setLocale(locale);
				if(this.getCreationLocale()==null){
					this.setCreationLocale(locale);
				}
			}
			this.storeMap.put(locale,tr);
		}
		tr.setColumn(field, value);
	}
	

	
	
	public void setCreationLocale(ICLocale locale){
		setColumn(COLUMNNAME_CREATION_LOCALE,locale);
	}
	
	public ICLocale getCreationLocale(){
		return (ICLocale)getColumnValue(COLUMNNAME_CREATION_LOCALE);
	}
	
	
}
