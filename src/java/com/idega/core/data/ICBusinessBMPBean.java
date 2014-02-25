package com.idega.core.data;

import java.sql.SQLException;

import com.idega.data.TreeableEntityBMPBean;

public class ICBusinessBMPBean extends TreeableEntityBMPBean<ICBusiness> implements ICBusiness {

	private static final long serialVersionUID = 4007143170674400270L;

public ICBusinessBMPBean(){
    super();
  }
  public ICBusinessBMPBean(int id)throws SQLException{
    super(id);
  }
  @Override
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

  @Override
public String getEntityName(){
    return getEntityTableName();
  }

  @Override
public String getName(){
    return getStringColumnValue(getColumnName());
  }
  @Override
public void setName(String name){
    setColumn(getColumnName(),name);
  }
  @Override
public String getDescription(){
    return getStringColumnValue(getColumnDescription());
  }
  @Override
public void setDescription(String description){
    setColumn(getColumnDescription(), description);
  }
  @Override
public boolean getValid(){
    return getBooleanColumnValue(getColumnValid());
  }
  @Override
public void setValid(boolean valid){
    setColumn(getColumnValid(), valid);
  }
  @Override
public java.sql.Timestamp getCreated(){
    return (java.sql.Timestamp) getColumnValue(getColumnCreated());
  }
  @Override
public void setCreated(java.sql.Timestamp created){
    setColumn(getColumnCreated(), created);
  }
  @Override
public String getType(){
    return getStringColumnValue(getColumnType());
  }
  @Override
public void setType(String type){
    setColumn(getColumnType(),type);
  }
}
