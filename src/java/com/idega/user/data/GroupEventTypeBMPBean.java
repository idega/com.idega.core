package com.idega.user.data;

import com.idega.data.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class GroupEventTypeBMPBean extends GenericEntity implements GroupEventType{

  private static String TABLE_NAME="IC_GROUP_EVENT_TYPE";
  private static String TYPE_COLUMN="EVENT_TYPE";

  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());
    this.addAttribute(TYPE_COLUMN,"Type",true,true,String.class);
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
}