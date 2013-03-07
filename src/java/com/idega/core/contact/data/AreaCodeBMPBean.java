package com.idega.core.contact.data;

import java.sql.SQLException;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AreaCodeBMPBean extends com.idega.data.GenericEntity implements com.idega.core.contact.data.AreaCode {

  public AreaCodeBMPBean(){
    super();
  }

  public AreaCodeBMPBean(int id)throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameAreaCode(),"Svæðisnúmer",true,true,String.class,10);
    this.addAttribute(getColumnNameAreaName(),"Heiti",true,true,String.class,255);
    this.addAttribute(getColumnNameCountryCodeId(),"Land",true,true,Integer.class,"many-to-one",CountryCode.class);
  }
  public String getEntityName() {
    return "ic_area_code";
  }

  public static String getColumnNameCountryCodeId(){return "ic_country_code_id";}
  public static String getColumnNameAreaCode(){return "area_code";}
  public static String getColumnNameAreaName(){return "area_name";}



}
