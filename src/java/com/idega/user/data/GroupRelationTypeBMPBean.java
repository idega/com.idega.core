package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class GroupRelationTypeBMPBean extends GenericEntity implements GroupRelationType{

  private static String TABLE_NAME="IC_GROUP_RELATION_TYPE";
  private static String TYPE_COLUMN="GROUP_RELATION_TYPE";
  private static String DESCRIPTION_COLUMN="GROUP_RELATION_TYPE_DESCR";


  public void initializeAttributes() {
    //this.addAttribute(getIDColumnName());
    this.addAttribute(TYPE_COLUMN,"Type",String.class,15);
    this.setAsPrimaryKey(TYPE_COLUMN,true);
    this.addAttribute(DESCRIPTION_COLUMN,"Description",String.class,1000);
  }

  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setType(String type){
    setColumn(TYPE_COLUMN,type);
  }

  public String getType(){
    return getStringColumnValue(TYPE_COLUMN);
  }

  public void setDescription(String desc){
    setColumn(DESCRIPTION_COLUMN,desc);
  }

  public String getDescription(){
    return getStringColumnValue(DESCRIPTION_COLUMN);
  }

  public String getIDColumnName(){
    return TYPE_COLUMN;
  }

  public Class getPrimaryKeyClass(){
    return String.class;
  }
  
  public Collection ejbFindAll() throws FinderException{
  	return super.idoFindAllIDsBySQL();
  }


}