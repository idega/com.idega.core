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

public abstract class GenericType extends GenericEntity {

  public GenericType() {
    super();
  }

  public GenericType(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameName(),"Nafn",true,true,String.class,255);
    this.addAttribute(getColumnNameDescription(),"Lýsing",true,true,String.class,500);
  }

  public static String getColumnNameName(){return "type_name";}
  public static String getColumnNameDescription(){return "type_description";}


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





} // Class GenericType