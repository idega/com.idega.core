package com.idega.core.data;

import com.idega.data.*;
import java.sql.SQLException;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class CountryCode extends GenericEntity {

  public CountryCode(){
    super();
  }

  public CountryCode(int id)throws SQLException{
    super(id);
  }
  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameCountryCode(),"Landsnúmer",true,true,String.class,10);
    this.addAttribute(getColumnNameCountryId(),"Land",true,true,Integer.class,"many-to-one",Country.class);
  }
  public String getEntityName() {
    return "ic_country_code";
  }

  public static String getColumnNameCountryCode(){return "country_code";}
  public static String getColumnNameCountryId(){return "ic_country_id";}


}