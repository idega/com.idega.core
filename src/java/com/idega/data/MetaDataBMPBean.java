package com.idega.data;

import java.sql.SQLException;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.0
 */

public class MetaDataBMPBean extends com.idega.data.GenericEntity implements com.idega.data.MetaData {

	public static final String COLUMN_META_KEY = "metadata_name";
	public static final String COLUMN_META_VALUE = "metadata_value";
	public static final String COLUMN_META_TYPE = "meta_data_type";
	
  protected MetaDataBMPBean() {
    super();
  }

  protected MetaDataBMPBean(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(COLUMN_META_KEY,"The key name",true,true, String.class,255);
    addAttribute(COLUMN_META_VALUE,"The key's value",true,true, String.class,2000);
		addAttribute(COLUMN_META_TYPE,"The value's type",true,true, String.class,255);

	addIndex("IDX_IC_METADATA_1", new String[]{ COLUMN_META_KEY, COLUMN_META_VALUE });
  }

  public String getEntityName() {
    return("ic_metadata");
  }

  public String getName(){
    return (String) getColumnValue(COLUMN_META_KEY);
  }

  public String getMetaDataName(){
    return getName();
  }

  public String getMetaDataValue(){
    return getValue();
  }

  public String getValue(){
	return (String) getColumnValue(COLUMN_META_VALUE);
  }

  public String getMetaDataType(){
	return getType();
  }

  public String getType(){
	return (String) getColumnValue(COLUMN_META_TYPE);
  }

  public void setValue(String value){
   setColumn(COLUMN_META_VALUE,value);
  }

  public void setMetaDataValue(String value){
    setValue(value);
  }

  public void setMetaDataName(String name){
   setName(name);
  }

  public void setMetaDataNameAndValue(String name, String value){
   setName(name);
   setValue(value);
  }

  public void setName(String name){
   setColumn(COLUMN_META_KEY,name);
  }

  public void setMetaDataType(String type){
		setType(type);
  }

  public void setType(String type){
   setColumn(COLUMN_META_TYPE,type);
  }

}
