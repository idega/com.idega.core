package com.idega.core.localisation.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.data.IDOQuery;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson</a>
 * @version 1.0
 */

public class ICLanguageBMPBean extends com.idega.data.GenericEntity implements com.idega.core.localisation.data.ICLanguage {

  public static final String _COLUMN_LanguageName = "name";
  public static final String _COLUMN_LanguageDescription = "description";
  public static final String _COLUMN_ISOabbreviation = "iso_abbreviation";

  public ICLanguageBMPBean() {
    super();
  }

  public ICLanguageBMPBean(int id) throws SQLException{
    super(id);
  }


  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(_COLUMN_LanguageName,"Name",true,true, "java.lang.String");
    addAttribute(_COLUMN_LanguageDescription,"Description",true,true, "java.lang.String", 510);
    addAttribute(_COLUMN_ISOabbreviation,"ISO abbreviation",true,true,String.class,10);
    this.getEntityDefinition().setBeanCachingActiveByDefault(true);
  }
  public String getEntityName() {
    return "ic_language";
  }

  public void insertStartData()throws Exception{
    String[] JavaLocales = Locale.getISOLanguages();
    ICLanguage lang;
    Locale l = null;
    for (int i = 0; i < JavaLocales.length; i++) {
      lang = ((com.idega.core.localisation.data.ICLanguageHome)com.idega.data.IDOLookup.getHome(ICLanguage.class)).create();
      l = new Locale(JavaLocales[i],"");
      lang.setName(l.getDisplayLanguage(l));
      lang.setIsoAbbreviation(JavaLocales[i]);
      lang.store();
    }
  }



  public String getName(){
    return (String) getColumnValue(_COLUMN_LanguageName);
  }

  public String getDescription(){
    return (String) getColumnValue(_COLUMN_LanguageDescription);
  }

  public String getIsoAbbreviation(){
    return this.getStringColumnValue(_COLUMN_ISOabbreviation);
  }



  public void setName(String Name){
    setColumn(_COLUMN_LanguageName, Name);
  }

  public void setDescription(String description){
    setColumn(_COLUMN_LanguageDescription, description);
  }

  public void setIsoAbbreviation(String IsoAbbreviation){
    this.setColumn(_COLUMN_ISOabbreviation,IsoAbbreviation);
  }
  
  public Collection ejbFindAll() throws FinderException {
  	IDOQuery query = idoQuery();
  	query.appendSelectAllFrom(this);
  	return idoFindPKsByQuery(query);
  }
  
  public Integer ejbFindByDescription(String description) throws FinderException {
  	IDOQuery query = idoQuery();
  	query.appendSelectAllFrom(this).appendWhereEqualsWithSingleQuotes(_COLUMN_LanguageDescription, description);
  	return (Integer) idoFindOnePKByQuery(query);
  }

	public Integer ejbFindByISOAbbreviation(String ISOAbbreviation) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this).appendWhereEqualsWithSingleQuotes(_COLUMN_ISOabbreviation, ISOAbbreviation);
		return (Integer) idoFindOnePKByQuery(query);
	}
}