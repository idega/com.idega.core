package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class GroupRelationTypeBMPBean extends GenericEntity implements GroupRelationType{

	private static final long serialVersionUID = 106422640019120658L;

public static String	TABLE_NAME="IC_GROUP_RELATION_TYPE",
		  				TYPE_COLUMN="GROUP_RELATION_TYPE",
		  				DESCRIPTION_COLUMN="GROUP_RELATION_TYPE_DESCR";


  @Override
public void initializeAttributes() {
    //this.addAttribute(getIDColumnName());
    this.addAttribute(TYPE_COLUMN,"Type",String.class,15);
    this.setAsPrimaryKey(TYPE_COLUMN,true);
    this.addAttribute(DESCRIPTION_COLUMN,"Description",String.class,1000);
  }

  @Override
public String getEntityName() {
    return TABLE_NAME;
  }

  @Override
public void setType(String type){
    setColumn(TYPE_COLUMN,type);
  }

  @Override
public String getType(){
    return getStringColumnValue(TYPE_COLUMN);
  }

  @Override
public void setDescription(String desc){
    setColumn(DESCRIPTION_COLUMN,desc);
  }

  @Override
public String getDescription(){
    return getStringColumnValue(DESCRIPTION_COLUMN);
  }

  @Override
public String getIDColumnName(){
    return TYPE_COLUMN;
  }

  @Override
public Class getPrimaryKeyClass(){
    return String.class;
  }

  public Collection ejbFindAll() throws FinderException{
  	return super.idoFindAllIDsBySQL();
  }


}