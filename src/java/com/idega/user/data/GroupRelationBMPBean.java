package com.idega.user.data;

import com.idega.data.*;

import java.util.Date;
import java.util.Collection;
import java.rmi.RemoteException;
import javax.ejb.*;

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
    this.addAttribute(STATUS_COLUMN,"Status",String.class);
    this.addAttribute(INITIATION_DATE_COLUMN,"Relationship Initiation Date",Date.class);
    this.addAttribute(TERMINATION_DATE_COLUMN,"Relationship Termination Date",Date.class);

  }
  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setGroup(Group group){
    this.setColumn(GROUP_ID_COLUMN,group);
  }

  public void setGroup(int groupID){
    this.setColumn(GROUP_ID_COLUMN,groupID);
  }

  public Group getGroup(){
    return (Group)getColumnValue(GROUP_ID_COLUMN);
  }

  public void setRelatedGroup(Group group)throws RemoteException{
    this.setColumn(RELATED_GROUP_ID_COLUMN,group);
  }

  public void setRelatedGroup(int groupID)throws RemoteException{
    this.setColumn(RELATED_GROUP_ID_COLUMN,groupID);
  }

  public void setRelatedUser(User user)throws RemoteException{
    setRelatedGroup(user.getUserGroup());
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

  /**Finders begin**/

  public Collection ejbFindGroupsRelationshipsUnder(Group group)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group,Group relatedGroup)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+group.getPrimaryKey().toString()+" and "+this.GROUP_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsUnder(int groupID)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.GROUP_ID_COLUMN,groupID);
  }

  public Collection ejbFindGroupsRelationshipsContaining(int groupID)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,groupID);
  }

  public Collection ejbFindGroupsRelationshipsContaining(int groupID,int relatedGroupID)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+groupID+" and "+this.GROUP_ID_COLUMN+"="+relatedGroupID);
  }

  /**Finders end**/

}