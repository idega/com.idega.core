package com.idega.core.data;



import com.idega.core.business.Category;

import java.sql.*;

import com.idega.data.TreeableEntity;





public class ICCategoryBMPBean extends com.idega.data.TreeableEntityBMPBean implements com.idega.core.data.ICCategory,com.idega.core.business.Category {



  public ICCategoryBMPBean(){

    super();

  }

  public ICCategoryBMPBean(int id)throws SQLException{

    super(id);

  }

  public void initializeAttributes(){

    addAttribute(getIDColumnName());

    addAttribute(getColumnBusinessId(),"Business id", true, true, Integer.class,MANY_TO_ONE,com.idega.core.data.ICBusiness.class);

    addAttribute(getColumnParentId(),"Parent id",true,true,Integer.class,MANY_TO_ONE,ICCategory.class);

    addAttribute(getColumnLocaleId(),"Locale id",true,true,Integer.class,MANY_TO_ONE,ICLocale.class);

    addAttribute(getColumnOwnerGroup(),"Owner group", true, true, Integer.class);

    addAttribute(getColumnName(),"Name", true, true, String.class);

    addAttribute(getColumnDescription(),"Description", true, true, String.class);

    addAttribute(getColumnType(),"Type", true, true, String.class);

    addAttribute(getColumnCreated(),"Created", true, true, java.sql.Timestamp.class);

    addAttribute(getColumnValid(),"Valid", true, true, Boolean.class);

    addManyToManyRelationShip(com.idega.core.data.ICObjectInstance.class);

  }



  public static String getEntityTableName(){return "IC_CATEGORY";}

  public static String getColumnBusinessId(){return "IC_BUSINESS_ID";}

  public static String getColumnLocaleId(){return "IC_LOCALE_ID";}

  public static String getColumnParentId(){return "IC_PARENT_ID";}

  public static String getColumnName(){return "NAME";}

  public static String getColumnDescription(){return "DESCRIPTION";}

  public static String getColumnType(){return "CAT_TYPE";}

  public static String getColumnCreated(){return "CREATED";}

  public static String getColumnValid(){return "VALID";}

  public static String getColumnOwnerGroup(){return "OWNER_GROUP";}



  public String getEntityName(){

    return getEntityTableName();

  }



  public int getBusinessId(){

    return getIntColumnValue(getColumnBusinessId());

  }

  public void setBusinessId(int id){

    setColumn(getColumnBusinessId(),id);

  }

  public int getParentId(){

    return getIntColumnValue(getColumnParentId());

  }

  public void setParentId(int id){

    setColumn(getColumnParentId(),id);

  }

  public int getLocaleId(){

    return getIntColumnValue(getColumnLocaleId());

  }

  public void setLocaleId(int id){

    setColumn(getColumnLocaleId(),id);

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



  public void setDefaultValues(){

    setType(getCategoryType());

  }

}

