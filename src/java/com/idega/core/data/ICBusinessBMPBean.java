package com.idega.core.data;

import java.sql.SQLException;


public class ICBusinessBMPBean extends com.idega.data.TreeableEntityBMPBean implements com.idega.core.data.ICBusiness {

  public ICBusinessBMPBean(){
    super();
  }
  public ICBusinessBMPBean(int id)throws SQLException{
    super(id);
  }
  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getColumnName(),"Name", true, true, String.class);
    addAttribute(getColumnDescription(),"Description", true, true, String.class);
    addAttribute(getColumnType(),"Type", true, true, String.class);
    addAttribute(getColumnCreated(),"Created", true, true, java.sql.Timestamp.class);
    addAttribute(getColumnValid(),"Valid", true, true, Boolean.class);
  }

  public static String getEntityTableName(){return "IC_BUSINESS";}
  public static String getColumnName(){return "NAME";}
  public static String getColumnDescription(){return "DESCRIPTION";}
  public static String getColumnType(){return "CAT_TYPE";}
  public static String getColumnCreated(){return "CREATED";}
  public static String getColumnValid(){return "VALID";}

  public String getEntityName(){
    return getEntityTableName();
  }

  public String getName(){
    return getStringColumnValue(getColumnName());
  }
  public void setName(String name){
    setColumn(getColumnName(),name);
  }
  public String getDescription(){
    return getStringColumnValue(getColumnDescription());
  }
  public void setDescription(String description){
    setColumn(getColumnDescription(), description);
  }
  public boolean getValid(){
    return getBooleanColumnValue(getColumnValid());
  }
  public void setValid(boolean valid){
    setColumn(getColumnValid(), valid);
  }
  public java.sql.Timestamp getCreated(){
    return (java.sql.Timestamp) getColumnValue(getColumnCreated());
  }
  public void setCreated(java.sql.Timestamp created){
    setColumn(getColumnCreated(), created);
  }
  public String getType(){
    return getStringColumnValue(getColumnType());
  }
  public void setType(String type){
    setColumn(getColumnType(),type);
  }
}
