/*
 * Created on 11.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.data;

import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;

/**
 * Title:		ICInformationCategoryTranslationBMPBean
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICInformationCategoryTranslationBMPBean extends GenericEntity implements ICInformationCategoryTranslation {
	
	public final static String TABLENAME = "IC_INFO_CAT_TRANSL";
	public final static String COLUMNNAME_NAME = "NAME";
	public final static String COLUMNNAME_DESCRIPTION = "DESCRIPTION";
	public final static String COLUMNNAME_SUPER_CAT = "SUPER_CAT_ID";
	public final static String COLUMNNAME_LOCALE = "LOCALE_ID";
	
	private ICInformationCategory _superCatEntity = null;

	/**
	 * 
	 */
	public ICInformationCategoryTranslationBMPBean() {
		super();
	}

	/**
	 * @param dataSource
	 */
	public ICInformationCategoryTranslationBMPBean(String dataSource) {
		super(dataSource);
	}

	/**
	 * @param id
	 * @throws SQLException
	 */
	public ICInformationCategoryTranslationBMPBean(int id) throws SQLException {
		super(id);
	}

	/**
	 * @param id
	 * @param dataSource
	 * @throws SQLException
	 */
	public ICInformationCategoryTranslationBMPBean(int id, String dataSource) throws SQLException {
		super(id, dataSource);
	}




	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMNNAME_NAME, "Name", true, true, String.class);
		addAttribute(COLUMNNAME_DESCRIPTION, "Description", true, true, String.class);
		addManyToOneRelationship(COLUMNNAME_SUPER_CAT, "Super Category", ICInformationCategory.class);
		addManyToOneRelationship(COLUMNNAME_LOCALE, "Locale id", ICLocale.class);		

	}
	
	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return TABLENAME;
	}
	
	
	public String getName(){
		return getStringColumnValue(COLUMNNAME_NAME);
	}
	
	public String getDescription(){
		return getStringColumnValue(COLUMNNAME_DESCRIPTION);
	}
	
	public ICInformationCategory getSuperInformationCategory(){
		return (ICInformationCategory)getColumnValue(COLUMNNAME_SUPER_CAT);
	}
	
	public ICLocale getLocale(){
		return (ICLocale)getColumnValue(COLUMNNAME_LOCALE);
	}
	
	
	public void setName(String name){
		setColumn(COLUMNNAME_NAME,name);
	}

	public void setDescription(String description){
		setColumn(COLUMNNAME_DESCRIPTION,description);
	}

	public void setSuperInformationCategory(ICInformationCategory cat){
		setColumn(COLUMNNAME_SUPER_CAT,cat);
	}
	
	public void setSuperInformationCategory(int catID){
		setColumn(COLUMNNAME_SUPER_CAT,catID);
	}

	public void setLocale(ICLocale locale){
		setColumn(COLUMNNAME_LOCALE,locale);
	}
	
	public void setLocale(int localeID){
		setColumn(COLUMNNAME_LOCALE,localeID);
	}
	


	public Object ejbFindByCategoryAndLocale(int categoryID,int localeID) throws FinderException{
		StringBuffer sql = new StringBuffer("select * from ").append(TABLENAME);
		sql.append(" where ").append(COLUMNNAME_SUPER_CAT).append(" = ").append(categoryID);
		sql.append(" and ").append(COLUMNNAME_LOCALE).append(" = ").append(localeID);
		return idoFindOnePKBySQL(sql.toString());
	}
  	
	public Collection ejbFindAllByCategory(int categoryID) throws FinderException{
		StringBuffer sql = new StringBuffer("select * from ").append(TABLENAME);
		sql.append(" where ").append(COLUMNNAME_SUPER_CAT).append(" = ").append(categoryID);
		return idoFindPKsBySQL(sql.toString());
	}


}
