package com.idega.core.data;

import java.sql.*;
import com.idega.data.CacheableEntity;


public class ICCategory extends CacheableEntity{

  public ICCategory(){
    super();
  }
  public ICCategory(int id)throws SQLException{
    super(id);
  }
  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getColumnBusinessId(),"Business id", true, true, Integer.class,"many-to-one",com.idega.core.data.ICBusiness.class);
    addAttribute(getColumnName(),"Name", true, true, String.class);
    addAttribute(getColumnDescription(),"Description", true, true, String.class);
    addAttribute(getColumnType(),"Type", true, true, String.class);
    addAttribute(getColumnCreated(),"Created", true, true, java.sql.Timestamp.class);
    addAttribute(getColumnValid(),"Valid", true, true, Boolean.class);
    addManyToManyRelationShip(com.idega.core.data.ICObjectInstance.class);
  }

  /*
  public void insertStartData()throws Exception{
    NewsCategory cat = new NewsCategory();
    cat.setName("Default");
    cat.setValid(true);
    cat.setDescription("Default Category for idegaWeb");
    cat.insert();

  }
*/
  public static String getEntityTableName(){return "IC_CATEGORY";}
  public static String getColumnBusinessId(){return "IC_BUSINESS_ID";}
  public static String getColumnName(){return "NAME";}
  public static String getColumnDescription(){return "DESCRIPTION";}
  public static String getColumnType(){return "CAT_TYPE";}
  public static String getColumnCreated(){return "CREATED";}
  public static String getColumnValid(){return "VALID";}

  public String getEntityName(){
    return getEntityTableName();
  }

  public int getBusinessId(){
    return getIntColumnValue(getColumnBusinessId());
  }

  public void getBusinessId(int id){
    setColumn(getColumnBusinessId(),id);
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

  public String getCategoryType(){
    return "no_type";
  }
}
