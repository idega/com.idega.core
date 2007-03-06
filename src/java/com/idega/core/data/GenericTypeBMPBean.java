package com.idega.core.data;

import java.sql.SQLException;


/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public abstract class GenericTypeBMPBean extends com.idega.data.GenericEntity implements com.idega.core.data.GenericType {

  public GenericTypeBMPBean() {
    super();
  }

  public GenericTypeBMPBean(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameDisplayName(),"Name",true,true,String.class,255);
    this.addAttribute(getColumnNameDescription(),"Description",true,true,String.class,500);
    this.addAttribute(getColumnNameUniqueName(),"unique name",true,true,String.class,255);
    this.getEntityDefinition().setBeanCachingActiveByDefault(true);
  }

  public static String getColumnNameDisplayName(){return "type_display_name";}
  public static String getColumnNameUniqueName() {return "unique_name";}
  public static String getColumnNameDescription(){return "type_description";}


  public String getName(){
    return this.getStringColumnValue(getColumnNameDisplayName());
  }

  public String getDisplayName(){
    return this.getStringColumnValue(getColumnNameDisplayName());
  }

  public String getDescription(){
    return this.getStringColumnValue(getColumnNameDescription());
  }

  public void setDisplayName(String typeName){
    this.setColumn(getColumnNameDisplayName(),typeName);
  }

  public void setName(String typeName){
    this.setColumn(getColumnNameDisplayName(),typeName);
  }

  public void setDescription(String typeDescription){
    this.setColumn(getColumnNameDescription(),typeDescription);
  }

  public String getUniqueName() {
    return this.getStringColumnValue(getColumnNameUniqueName());
  }

  public void setUniqueName(String uniqueName) {
    this.setColumn(getColumnNameUniqueName(),uniqueName);
  }



} // Class GenericType
