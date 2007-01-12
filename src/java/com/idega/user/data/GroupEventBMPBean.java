package com.idega.user.data;

import com.idega.data.*;

import java.sql.Date;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class GroupEventBMPBean extends GenericEntity implements GroupEvent{

  private static final String TABLE_NAME="IC_GROUP_EVENT";
	private static final String  GROUP_ID_COLUMN="IC_GROUP_ID";
	private static final String  EVENT_TYPE_COLUMN="EVENT_TYPE";
	private static final String  DATE_OCCURED= "DATE_OCCURED";
	private static final String  DATE_REGISTERED = "DATE_REGISTERED";
	private static final String  EVENT_DESCRIPTION = "EVENT_DESCRIPTION";
	private static final String  REGISTERED_BY_GROUP = "REGISTERED_BY_GROUP_ID";

  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());
    this.addManyToOneRelationship(GROUP_ID_COLUMN,"Group",Group.class);
    this.addManyToOneRelationship(EVENT_TYPE_COLUMN,"Type",GroupEventType.class);
    this.addAttribute(EVENT_DESCRIPTION,"Description",String.class,1000);
    this.addAttribute(DATE_OCCURED,"Date Occured",Date.class);
    this.addAttribute(DATE_REGISTERED,"Date Registered",Date.class);
    this.addManyToOneRelationship(REGISTERED_BY_GROUP,"Registered by",Group.class);
  }
  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setGroup(Group group){
    this.setColumn(GROUP_ID_COLUMN,group);
  }

  public Group getGroup(){
    return (Group)getColumnValue(GROUP_ID_COLUMN);
  }

  public GroupEventType getEventType(){
    return (GroupEventType)getColumnValue(EVENT_TYPE_COLUMN);
  }

  public void setEventType(GroupEventType type){
    this.setColumn(EVENT_TYPE_COLUMN,type);
  }

  public Date getDateOccured(){
    return (Date)getColumnValue(DATE_OCCURED);
  }

  public void setDateOccured(Date date){
    this.setColumn(DATE_OCCURED,date);
  }

  public Date getDateRegistered(){
    return (Date)getColumnValue(DATE_REGISTERED);
  }

  public void setDateRegistered(Date date){
    this.setColumn(DATE_REGISTERED,date);
  }

  public String getDescription(){
    return (String)getColumnValue(GroupEventBMPBean.EVENT_DESCRIPTION);
  }

  public void setDescription(String description){
    this.setColumn(EVENT_DESCRIPTION,description);
  }

  public void setRegistrant(Group group){
    this.setColumn(REGISTERED_BY_GROUP,group);
  }

  public Group getRegistrant(){
    return (Group)getColumnValue(GroupEventBMPBean.REGISTERED_BY_GROUP);
  }

}