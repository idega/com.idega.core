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

public class GroupEventBMPBean extends GenericEntity implements GroupEvent{

  private static String TABLE_NAME="IC_GROUP_EVENT";
  private static String GROUP_ID_COLUMN="IC_GROUP_ID";
  private static String EVENT_TYPE_COLUMN="EVENT_TYPE_ID";

  private static String DATE_REGISTERED;
  private static String EVENT_DESCRIPTION;
  private static String REGISTERED_BY_GROUP;

  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());
    this.addManyToOneRelationship(GROUP_ID_COLUMN,"Group",Group.class);
    //this.addManyToOneRelationship(RELATIONSHIP_TYPE_COLUMN,"Type",GroupRelationType.class);

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
}