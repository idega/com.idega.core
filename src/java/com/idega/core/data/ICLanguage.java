package com.idega.core.data;

import com.idega.data.*;
import java.sql.SQLException;
import java.util.Locale;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson</a>
 * @version 1.0
 */

public class ICLanguage extends GenericEntity {

  public static final String _COLUMN_LanguageName = "name";
  public static final String _COLUMN_LanguageDescription = "description";
  public static final String _COLUMN_ISOabbreviation = "iso_abbreviation";

  public ICLanguage() {
    super();
  }

  public ICLanguage(int id) throws SQLException{
    super(id);
  }


  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(_COLUMN_LanguageName,"nafn",true,true, "java.lang.String");
    addAttribute(_COLUMN_LanguageDescription,"lýsing",true,true, "java.lang.String", 510);
    addAttribute(_COLUMN_ISOabbreviation,"ISO skammstöfun",true,true,String.class,10);
    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
  }
  public String getEntityName() {
    return "ic_language";
    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
  }

  public void insertStartData()throws Exception{
    String[] JavaLocales = Locale.getISOLanguages();
    ICLanguage lang;
    Locale l = null;
    for (int i = 0; i < JavaLocales.length; i++) {
      lang = new ICLanguage();
      l = new Locale(JavaLocales[i],"");
      lang.setName(l.getDisplayLanguage(l));
      lang.setIsoAbbreviation(JavaLocales[i]);
      lang.insert();
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

}