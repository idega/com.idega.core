package com.idega.user.data;

import com.idega.data.*;
import com.idega.util.idegaTimestamp;

//import java.util.Date;
import java.sql.Timestamp;
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

  private final static String STATUS_ACTIVE="ST_ACTIVE";
  private final static String STATUS_PASSIVE="ST_PASSIVE";

  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());

    this.addManyToOneRelationship(GROUP_ID_COLUMN,"Type",Group.class);
    this.addManyToOneRelationship(RELATED_GROUP_ID_COLUMN,"Related Group",Group.class);
    this.addManyToOneRelationship(RELATIONSHIP_TYPE_COLUMN,"Type",GroupRelationType.class);
    this.addAttribute(STATUS_COLUMN,"Status",String.class,10);
    this.addAttribute(INITIATION_DATE_COLUMN,"Relationship Initiation Date",Timestamp.class);
    this.addAttribute(TERMINATION_DATE_COLUMN,"Relationship Termination Date",Timestamp.class);

  }
  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setDefaultValues(){
    this.setInitiationDate(idegaTimestamp.getTimestampRightNow());
    this.setStatus(STATUS_ACTIVE);
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

  public void setRelationshipType(String groupRelationType){
    this.setColumn(RELATIONSHIP_TYPE_COLUMN,groupRelationType);
  }

  public GroupRelationType getRelationship(){
    return (GroupRelationType)getColumnValue(RELATIONSHIP_TYPE_COLUMN);
  }

  public String getRelationshipType(){
    return getStringColumnValue(RELATIONSHIP_TYPE_COLUMN);
  }


  public void setStatus(String status){
    setColumn(this.STATUS_COLUMN,status);
  }

  public String getStatus(){
    return getStringColumnValue(this.STATUS_COLUMN);
  }

  public boolean isActive(){
    String status = this.getStatus();
    if(status != null && status.equals(STATUS_ACTIVE)){
      return true;
    }
    return false;
  }

  public boolean isPassive(){
    String status = this.getStatus();
    if(status != null && status.equals(STATUS_PASSIVE)){
      return true;
    }
    return false;
  }

  public void setActive(){
    this.setStatus(STATUS_ACTIVE);
  }

  public void setPassive(){
    this.setStatus(STATUS_PASSIVE);
  }

  public void setInitiationDate(Timestamp stamp){
    this.setColumn(this.INITIATION_DATE_COLUMN,stamp);
  }

  public Timestamp getInitiationDate(){
    return (Timestamp)getColumnValue(this.INITIATION_DATE_COLUMN);
  }

  public void setTerminationDate(Timestamp stamp){
    this.setColumn(this.TERMINATION_DATE_COLUMN,stamp);
  }

  public Timestamp getTerminationDate(){
    return (Timestamp)getColumnValue(this.TERMINATION_DATE_COLUMN);
  }

  /**Finders begin**/

  public Collection ejbFindGroupsRelationshipsUnder(Group group)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group,Group relatedGroup)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString()+" and "+this.GROUP_ID_COLUMN+"="+group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsUnder(int groupID)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.GROUP_ID_COLUMN,groupID);
  }

  /**
   * Finds all active relationships specified only in one direction with groupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"'");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relationType as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,String relationType)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATIONSHIP_TYPE_COLUMN+"='"+relationType+"' and "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"'");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,int relatedGroupID,String relationshipType)throws FinderException,RemoteException{
    return ejbFindGroupsRelationshipsContainingUniDirectional(groupID,relatedGroupID,relationshipType);
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,int relatedGroupID)throws FinderException,RemoteException{
    return ejbFindGroupsRelationshipsContainingUniDirectional(groupID,relatedGroupID);
  }

  /**
   * Finds all active relationships specified bidirectionally (in both directions) with groupID and relatedGroupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingBiDirectional(int groupID,int relatedGroupID)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where ("+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+") or ("+this.RELATED_GROUP_ID_COLUMN+"="+groupID+" and "+this.GROUP_ID_COLUMN+"="+relatedGroupID+") and "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"'");
  }

  /**
   * Finds all active relationships specified bidirectionally (in both directions) with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingBiDirectional(int groupID,int relatedGroupID,String relationshipType)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where ("+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+") or ("+this.RELATED_GROUP_ID_COLUMN+"="+groupID+" and "+this.GROUP_ID_COLUMN+"="+relatedGroupID+") and "+this.RELATIONSHIP_TYPE_COLUMN+"='"+relationshipType+"' and "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"'");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingUniDirectional(int groupID,int relatedGroupID)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+" and "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"'");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingUniDirectional(int groupID,int relatedGroupID,String relationshipType)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+" and "+this.RELATIONSHIP_TYPE_COLUMN+"='"+relationshipType+"' and "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"'");
  }

  /**Finders end**/

  /**
   * Overriding the remove function
   */
  public void remove()throws RemoveException{
    this.setPassive();
    this.setTerminationDate(idegaTimestamp.getTimestampRightNow());
    store();
  }
}