package com.idega.core.data;

import java.sql.*;
import com.idega.data.GenericEntity;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class Country extends GenericEntity{

  public Country(){
          super();
  }

  public Country(int id)throws SQLException{
          super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameName(),"Nafn",true,true,String.class,255);
    this.addAttribute(getColumnNameDescription(),"Lýsing",true,true,String.class,500);
  }

  public String getEntityName() {
    return "ic_country";
  }

  public static String getColumnNameName(){return "country_name";}
  public static String getColumnNameDescription(){return "country_description";}


  public String getName(){
    return this.getStringColumnValue(getColumnNameName());
  }

  public String getDescription(){
    return this.getStringColumnValue(getColumnNameDescription());
  }



  public void setName(String typeName){
    this.setColumn(getColumnNameName(),typeName);
  }

  public void setDescription(String typeDescription){
    this.setColumn(getColumnNameDescription(),typeDescription);
  }


  public static Country getStaticInstance(){
    return(Country)getStaticInstance(Country.class);
  }


}
