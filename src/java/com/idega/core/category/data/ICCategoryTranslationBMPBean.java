package com.idega.core.category.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.CategoryEntityBMPBean;

public class ICCategoryTranslationBMPBean extends CategoryEntityBMPBean implements ICCategoryTranslation{
	
	public final static String TABLENAME = "IC_CAT_TRANSL";
	public final static String NAME = "NAME";
	public final static String INFO = "INFO";
	public final static String LOCALE = "LOCALE_ID";
	
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(LOCALE, "Locale", true, true, java.lang.Integer.class,"many_to_one",com.idega.core.localisation.data.ICLocale.class);
		addAttribute(NAME, "Name", true, true, String.class);
		addAttribute(INFO, "Description", true, true, String.class);
	}
	
	public String getEntityName() {
		return TABLENAME;
	}
	
	public String getName() {
		return getStringColumnValue(NAME);
	}
	public void setName(String name) {
		setColumn(NAME, name);
	}
	public String getDescription() {
		return getStringColumnValue(INFO);
	}
	public void setDescription(String description) {
		setColumn(INFO, description);
	}
	public void setLocaleID(int localeID){
		setColumn(LOCALE,localeID);	
	}
	public int getLocaleId(){
    	return getIntColumnValue(LOCALE);
  	}
  	
  	public Object ejbFindByCategoryAndLocale(int categoryID,int localeID) throws FinderException{
  		StringBuffer sql = new StringBuffer("select * from ").append(TABLENAME);
  		sql.append(" where ").append(getColumnCategoryId()).append(" = ").append(categoryID);
  		sql.append(" and ").append(LOCALE).append(" = ").append(localeID);
  		return idoFindOnePKBySQL(sql.toString());
  	}
  	
  	public Collection ejbFindAllByCategory(int categoryID) throws FinderException{
  		StringBuffer sql = new StringBuffer("select * from ").append(TABLENAME);
  		sql.append(" where ").append(getColumnCategoryId()).append(" = ").append(categoryID);
  		return idoFindPKsBySQL(sql.toString());
  	}
	
}
