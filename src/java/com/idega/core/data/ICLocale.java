package com.idega.core.data;

import com.idega.data.*;


/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class ICLocale extends GenericEntity {

  public ICLocale() {
  }

  public ICLocale(int id)throws java.sql.SQLException {
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameLocale(),"Locale",true,true,String.class,20);
    this.addAttribute(getColumnNameLanguageId(),"Language",true,true,Integer.class,"many-to-one",ICLanguage.class);
    this.addAttribute(getColumnNameCountryId(),"Country",true,true,Integer.class,"many-to-one",Country.class);
  }

  public void insertStartData() throws Exception{
    ICLocale is= new ICLocale();
    is.setLocale("is_IS");
    is.insert();

    ICLocale en= new ICLocale();
    en.setLocale("en");
    en.insert();
  }


  public String getEntityName() {
    return getEntityTableName();
  }
  public static String getEntityTableName(){return "IC_LOCALE";}
  public static String getColumnNameLocale(){return "LOCALE";}
  public static String getColumnNameLanguageId(){return "IC_LANGUAGE_ID";}
  public static String getColumnNameCountryId(){return "IC_COUNTRY_ID";}

  public String getName(){
    return getLocale();
  }

  public void setLocale(String sLocaleName){
    setColumn(getColumnNameLocale(),sLocaleName);
  }
  public String getLocale(){
    return getStringColumnValue(getColumnNameLocale());
  }
  public void setLanguageId(int iLanguageId){
    setColumn(getColumnNameLanguageId(),iLanguageId);
  }
  public void setLanguageId(Integer iLanguageId){
    setColumn(getColumnNameLanguageId(),iLanguageId);
  }
  public int getLanguageId(){
    return getIntColumnValue(getColumnNameLanguageId());
  }
  public void setCountryId(int iCountryId){
    setColumn(getColumnNameCountryId(),iCountryId);
  }
  public void setCountryId(Integer iCountryId){
    setColumn(getColumnNameCountryId(),iCountryId);
  }
  public int getCountryId(){
    return getIntColumnValue(getColumnNameCountryId());
  }

}