package com.idega.data;

import com.idega.data.GenericEntity;
import java.lang.String;
import java.sql.SQLException;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.0
 */

public class MetaData extends GenericEntity {

  protected MetaData() {
    super();
  }

  protected MetaData(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("metadata_name","The key name",true,true, String.class,255);
    addAttribute("metadata_value","The key's value",true,true, String.class,255);
  }

  public String getEntityName() {
    return("ic_metadata");
  }

  public String getName(){
    return (String) getColumnValue("metadata_name");
  }

  public String getMetaDataName(){
    return getName();
  }

  public String getMetaDataValue(){
    return getValue();
  }

  public String getValue(){
    return (String) getColumnValue("metadata_value");
  }

  public void setValue(String value){
   setColumn("metadata_value",value);
  }

  public void setMetaDataValue(String value){
    setValue(value);
  }

  public void setMetaDataName(String name){
   setName(name);
  }

  public void setName(String name){
   setColumn("metadata_name",name);
  }

}