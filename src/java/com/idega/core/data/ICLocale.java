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


  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameLocale(),"Locale",true,true,String.class,20);
    this.addAttribute(getColumnNameLanguageId(),"Tungumál",true,true,Integer.class,"many-to-one",ICLanguage.class);
    this.addAttribute(getColumnNameCountryId(),"Land",true,true,Integer.class,"many-to-one",Country.class);
  }


  public String getEntityName() {
    return "ic_locale";
  }


  public static String getColumnNameLocale(){return "locale";}
  public static String getColumnNameLanguageId(){return "language_id";}
  public static String getColumnNameCountryId(){return "country_id";}

}