package com.idega.user.data;

import com.idega.data.*;
import com.idega.core.user.data.User;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class GroupRelationBMPBean extends GenericEntity implements GroupRelation{

  private static String TABLE_NAME="IC_GROUP_RELATION";
  private static String GROUP_ID_COLUMN="IC_GROUP_ID";
  private static String RELATED_GROUP_ID_COLUMN="RELATED_IC_GROUP_ID";
  private static String RELATIONSHIP_TYPE_COLUMN="RELATIONSHIP_TYPE";
  private static String STATUS_COLUMN="GROUP_RELATION_STATUS";
  private static String INITIATION_DATE_COLUMN="INITIATION_DATE";
  private static String TERMINATION_DATE_COLUMN="TERMINATION_DATE";


  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());

    this.addManyToOneRelationship(GROUP_ID_COLUMN,"Type",Group.class);
    this.addManyToOneRelationship(RELATED_GROUP_ID_COLUMN,"Related Group",Group.class);
    this.addManyToOneRelationship(RELATIONSHIP_TYPE_COLUMN,"Type",GroupRelationType.class);

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

  public void setRelatedUser(User user){
    this.setColumn(RELATED_GROUP_ID_COLUMN,user);
  }

  public Group getRelatedGroup(){
    return (Group)getColumnValue(RELATED_GROUP_ID_COLUMN);
  }

  public void setRelationship(GroupRelationType type){
    this.setColumn(RELATIONSHIP_TYPE_COLUMN,type);
  }

  public GroupRelationType getRelationship(){
    return (GroupRelationType)getColumnValue(RELATIONSHIP_TYPE_COLUMN);
  }

}