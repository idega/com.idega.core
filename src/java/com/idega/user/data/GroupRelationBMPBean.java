package com.idega.user.data;

import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.presentation.IWContext;
import com.idega.util.IWTimestamp;

/**
 * Description: This bean is used to connect groups together and to keep track of their relations
 * Copyright: Idega Software   Copyright (c) 2001
 * Company: Idega Software
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.1
 */

public class GroupRelationBMPBean extends GenericEntity implements GroupRelation {


	protected static final String  TABLE_NAME="IC_GROUP_RELATION";
	protected static final String  GROUP_ID_COLUMN="IC_GROUP_ID";
	protected static final String  RELATED_GROUP_ID_COLUMN="RELATED_IC_GROUP_ID";
	protected static final String  RELATIONSHIP_TYPE_COLUMN="RELATIONSHIP_TYPE";
	protected static final String  STATUS_COLUMN="GROUP_RELATION_STATUS";
	protected static final String  INITIATION_DATE_COLUMN="INITIATION_DATE";
	protected static final String  TERMINATION_DATE_COLUMN="TERMINATION_DATE";
  protected static final String  SET_PASSIVE_BY="SET_PASSIVE_BY";
  protected static final String RELATED_GROUP_TYPE_COLUMN = "RELATED_GROUP_TYPE";
  
  protected static final String INITIATION_MODIFICATION_DATE_COLUMN="INIT_MODIFICATION_DATE";
  protected static final String TERMINATION_MODIFICATION_DATE_COLUMN="TERM_MODIFICATION_DATE";

  private final static String STATUS_ACTIVE="ST_ACTIVE";
  private final static String STATUS_PASSIVE="ST_PASSIVE";
	private final static String STATUS_PASSIVE_PENDING="PASS_PEND";
	private final static String STATUS_ACTIVE_PENDING="ACT_PEND";
	
  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());

    this.addManyToOneRelationship(GROUP_ID_COLUMN,"Type",Group.class);
    this.addManyToOneRelationship(RELATED_GROUP_ID_COLUMN,"Related Group",Group.class);
    this.addAttribute(RELATIONSHIP_TYPE_COLUMN,"Type",true,true,String.class,15,MANY_TO_ONE,GroupRelationType.class);
    this.addAttribute(STATUS_COLUMN,"Status",String.class,30);
    this.addAttribute(INITIATION_DATE_COLUMN,"Relationship Initiation Date",Timestamp.class);
    this.addAttribute(TERMINATION_DATE_COLUMN,"Relationship Termination Date",Timestamp.class);
    this.addAttribute(SET_PASSIVE_BY, "set passive by", true, true, Integer.class, MANY_TO_ONE, User.class);
    this.addAttribute(INITIATION_MODIFICATION_DATE_COLUMN, "Initiation modification date", Timestamp.class);
    this.addAttribute(RELATED_GROUP_TYPE_COLUMN, "Related group type", String.class);
		this.addAttribute(TERMINATION_MODIFICATION_DATE_COLUMN, "Termination modification date", Timestamp.class);
 		this.addMetaDataRelationship();
 		
 		addIndex("IDX_IC_GROUP_REL_1", RELATED_GROUP_ID_COLUMN);
 		addIndex("IDX_IC_GROUP_REL_2", GROUP_ID_COLUMN);
 		addIndex("IDX_IC_GROUP_REL_3", new String[]{GROUP_ID_COLUMN, RELATIONSHIP_TYPE_COLUMN, STATUS_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_4", new String[]{GROUP_ID_COLUMN, RELATIONSHIP_TYPE_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_5", RELATIONSHIP_TYPE_COLUMN);
 		addIndex("IDX_IC_GROUP_REL_6", new String[]{RELATED_GROUP_ID_COLUMN, RELATIONSHIP_TYPE_COLUMN, STATUS_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_7", new String[]{RELATED_GROUP_ID_COLUMN, RELATIONSHIP_TYPE_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_8", new String[]{RELATIONSHIP_TYPE_COLUMN, STATUS_COLUMN});
  }
  
  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setDefaultValues(){
    this.setInitiationDate(IWTimestamp.getTimestampRightNow());
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
  
  public int getGroupID() {
  	return getIntColumnValue(GROUP_ID_COLUMN);
  }

  public void setRelatedGroup(Group group){
    this.setColumn(RELATED_GROUP_ID_COLUMN,group);
  }

  public void setRelatedGroup(int groupID){
    this.setColumn(RELATED_GROUP_ID_COLUMN,groupID);
  }

  public void setRelatedUser(User user){
    setRelatedGroup(user);
  }

  public Group getRelatedGroup(){
    return (Group)getColumnValue(RELATED_GROUP_ID_COLUMN);
  }

  public Integer getRelatedGroupPK(){
    return (Integer)getIntegerColumnValue(RELATED_GROUP_ID_COLUMN);
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
  
	public boolean isActivePending(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_ACTIVE_PENDING) ){
			return true;
		}
		return false;
	}

	public boolean isPassivePending(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_PASSIVE_PENDING) ){
			return true;
		}
		return false;
	}
	
	public boolean isPending(){
		String status = this.getStatus();
		if(status != null && ( status.equals(STATUS_ACTIVE_PENDING) || status.equals(STATUS_PASSIVE_PENDING) ) ){
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
  
	public void setPassivePending(){
		this.setStatus(STATUS_PASSIVE_PENDING);
	}

	public void setActivePending(){
		this.setStatus(STATUS_ACTIVE_PENDING);
	}
	
  public void setInitiationDate(Timestamp stamp){
    this.setColumn(this.INITIATION_DATE_COLUMN,stamp);
    this.setColumn(INITIATION_MODIFICATION_DATE_COLUMN, IWTimestamp.RightNow().getTimestamp());
  }

  public Timestamp getInitiationDate(){
    return (Timestamp)getColumnValue(this.INITIATION_DATE_COLUMN);
  }

  public Timestamp getInitiationModificationDate(){
		return (Timestamp)getColumnValue(this.INITIATION_MODIFICATION_DATE_COLUMN);
  }

  public void setTerminationDate(Timestamp stamp){
    this.setColumn(this.TERMINATION_DATE_COLUMN,stamp);
    this.setColumn(this.TERMINATION_MODIFICATION_DATE_COLUMN, IWTimestamp.RightNow().getTimestamp());
  }

  public Timestamp getTerminationDate(){
    return (Timestamp)getColumnValue(this.TERMINATION_DATE_COLUMN);
  }
  
  public Timestamp getTerminationModificationDate(){
		return (Timestamp)getColumnValue(this.TERMINATION_MODIFICATION_DATE_COLUMN);
  }
  public void setPassiveBy(int userId)  {
    setColumn(SET_PASSIVE_BY, userId);
  }

  public int getPassiveBy() { 
    return getIntColumnValue(SET_PASSIVE_BY);
  }

  public void setRelatedGroupType(String groupType)  {
    setColumn(RELATED_GROUP_TYPE_COLUMN, groupType);
  }

  public String getRelatedGroupType() { 
  	return getStringColumnValue(RELATED_GROUP_TYPE_COLUMN);
  }

  /**Finders begin**/

  public Collection ejbFindGroupsRelationshipsUnder(Group group)throws FinderException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group)throws FinderException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group,Group relatedGroup)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString()+" and "+this.GROUP_ID_COLUMN+"="+group.getPrimaryKey().toString());
  }
  
  public Collection ejbFindGroupsRelationshipsContainingGroupsAndStatus(Group group,Group relatedGroup, String status)throws FinderException{
	return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString()+" and "+this.GROUP_ID_COLUMN+"="+group.getPrimaryKey().toString()+" and "+this.STATUS_COLUMN+" like '"+status+"'");
  }
  
  
	/**
	 * Finds all relationships specified only in one direction with groupID and relationType as specified
	 */
	public Collection ejbFindAllGroupsRelationshipsByRelatedGroup(int groupID,String relationType)throws FinderException{
		return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+groupID
		+" and "+this.RELATIONSHIP_TYPE_COLUMN+"='"+relationType+"' order by " + this.INITIATION_DATE_COLUMN);
	}

  public Collection ejbFindGroupsRelationshipsUnder(int groupID)throws FinderException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.GROUP_ID_COLUMN,groupID);
  }

  /**
   * Finds all active relationships specified only in one direction with groupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID
    +" and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relationType as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,String relationType)throws FinderException{
    return this.idoFindPKsBySQL(ejbHomeGetFindGroupsRelationshipsContainingSQL(groupID,relationType));
  }
  
  public String ejbHomeGetFindGroupsRelationshipsContainingSQL(int groupId, String relationType){
  	StringBuffer sql = new StringBuffer();
  	sql.append("select * from ")
  	.append(this.getEntityName())
  	.append(" where ")
  	.append(GROUP_ID_COLUMN).append("=").append(groupId)
  	.append(" and ")
  	.append(RELATIONSHIP_TYPE_COLUMN).append("='").append(relationType).append("'")
  	.append(" and ( ")
  	.append(STATUS_COLUMN).append("='").append(STATUS_ACTIVE).append("'")
  	.append(" or ")
  	.append(STATUS_COLUMN).append("='").append(STATUS_PASSIVE_PENDING).append("' ) ");
		//might have to or null check
  	return sql.toString();
  }
  
	public String ejbHomeGetFindRelatedGroupIdsInGroupRelationshipsContainingSQL(int groupId, String relationType){
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(RELATED_GROUP_ID_COLUMN).append(" from ")
		.append(this.getEntityName())
		.append(" where ")
		.append(GROUP_ID_COLUMN).append("=").append(groupId)
		.append(" and ")
		.append(RELATIONSHIP_TYPE_COLUMN).append("='").append(relationType).append("'")
		.append(" and ( ")
		.append(STATUS_COLUMN).append("='").append(STATUS_ACTIVE).append("'")
		.append(" or ")
		.append(STATUS_COLUMN).append("='").append(STATUS_PASSIVE_PENDING).append("' ) ");
  	//might have to or null check
		return sql.toString();
	}
  
  /**
   * Finds all active relationships specified only in one direction with groupID and relationType ether with value relationType or orRelationType, relationType and orRelationType may be null
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,String relationType,String orRelationType)throws FinderException{
    String firstRelationTypeClause = getRelationTypeWhereClause(relationType);
    String secondRelationTypeClause = getRelationTypeWhereClause(orRelationType);
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID
    +" and ("+firstRelationTypeClause+" OR "+secondRelationTypeClause+") and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }



  /**
   * Finds all active relationships specified only in one direction with groupID and relationType as specified
   */
  public Collection ejbFindGroupsRelationshipsByRelatedGroup(int groupID,String relationType)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+groupID
    +" and "+this.RELATIONSHIP_TYPE_COLUMN+"='"+relationType+"' and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }
  
  /**
   * Finds all active relationships specified only in one direction with groupID and relationType ether with value relationType or orRelationType, relationType and orRelationType may be null
   */
  public Collection ejbFindGroupsRelationshipsByRelatedGroup(int groupID,String relationType,String orRelationType)throws FinderException{
    String firstRelationTypeClause = getRelationTypeWhereClause(relationType);
    String secondRelationTypeClause = getRelationTypeWhereClause(orRelationType);
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+groupID
    +" and ("+firstRelationTypeClause+" OR "+secondRelationTypeClause+") and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }
  
  protected static String getRelationTypeWhereClause(String value){
  	if(value==null){
  		return RELATIONSHIP_TYPE_COLUMN+" is null";
  	}
  	else{
  		return RELATIONSHIP_TYPE_COLUMN+"='"+value+"'";
  	}	
  }


  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,int relatedGroupID,String relationshipType)throws FinderException{
    return ejbFindGroupsRelationshipsContainingUniDirectional(groupID,relatedGroupID,relationshipType);
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,int relatedGroupID)throws FinderException{
    return ejbFindGroupsRelationshipsContainingUniDirectional(groupID,relatedGroupID);
  }

  /**
   * Finds all active relationships specified bidirectionally (in both directions) with groupID and relatedGroupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingBiDirectional(int groupID,int relatedGroupID)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where ( ("+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID
    +") or ("+this.RELATED_GROUP_ID_COLUMN+"="+groupID+" and "+this.GROUP_ID_COLUMN+"="+relatedGroupID+") ) and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified bidirectionally (in both directions) with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingBiDirectional(int groupID,int relatedGroupID,String relationshipType)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where ( ("+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+") or ("+this.RELATED_GROUP_ID_COLUMN+"="+groupID
    +" and "+this.GROUP_ID_COLUMN+"="+relatedGroupID+") ) and "+this.RELATIONSHIP_TYPE_COLUMN+"='"+relationshipType+"' and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingUniDirectional(int groupID,int relatedGroupID)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID
    +" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+" and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingUniDirectional(int groupID,int relatedGroupID,String relationshipType)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.GROUP_ID_COLUMN+"="+groupID+" and "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID
    +" and "+this.RELATIONSHIP_TYPE_COLUMN+"='"+relationshipType+"' and ( "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }
  
	/**
		* Finds all active relationships specified only in one direction with groupID and relatedGroupID and relationshipType as specified
		*/
	 public Collection ejbFindAllPendingGroupRelationships()throws FinderException{
		 return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.STATUS_COLUMN+"='"+STATUS_ACTIVE_PENDING+"' OR "+this.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"'");
	 }
	 
	 /**
		* Finds all relationships with null values in related_group_type column 
		* That is a new column in ic_group_relation that is a duplicate of the value in group_type column in ic_group for the related group
		* Created 9.7.2004 by Sigtryggur for optimising purposes
		*/
	 public Collection ejbFindAllGroupsWithoutRelatedGroupType()throws FinderException{
//		 return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_TYPE_COLUMN+" is null");
		 SelectQuery query = idoSelectQuery();
		 query.addCriteria(new MatchCriteria(idoQueryTable(),RELATED_GROUP_TYPE_COLUMN,MatchCriteria.IS,MatchCriteria.NULL));
		 if(this.isDebugActive()){
		 	debug("["+this.getClass().getName()+"]: "+query.toString());
		 }
		 return idoFindPKsByQueryUsingLoadBalance(query,2000);
	 }

  /**Finders end**/
  
  /**
   * @deprecated Replaced with remove(User)
   */
  public void remove()  throws RemoveException  {    
    User currentUser;
    try {
      currentUser = IWContext.getInstance().getCurrentUser();
    }
    catch (Exception ex)  {
    currentUser = null;
    }
    removeBy(currentUser);
  }

  /**
   * 
   */
  public void removeBy(User currentUser) throws RemoveException{
    this.removeBy(currentUser,IWTimestamp.getTimestampRightNow());

  }
  
	/**
	 * 
	 */
	public void removeBy(User currentUser, Timestamp time) throws RemoveException{
		int userId = ((Integer) currentUser.getPrimaryKey()).intValue(); 
		this.setPassive();
		this.setTerminationDate(time);
		setPassiveBy(userId);
		store();
	}
  
  
  public Collection ejbFindAllGroupsRelationshipsTerminatedWithinSpecifiedTimePeriod(Group group, Group relatedGroup, Timestamp firstDateInPeriod, Timestamp lastDateInPeriod, String[] relationStatus) throws FinderException{
  	
	//constructing query
	IDOQuery query = idoQuery();
	//select
	query.appendSelectAllFrom(this);
	//where
	query.appendWhere();
	query.appendEquals(GROUP_ID_COLUMN,String.valueOf(group.getPrimaryKey()));
	//and
	query.appendAnd();
	query.appendEquals(RELATED_GROUP_ID_COLUMN,String.valueOf(relatedGroup.getPrimaryKey()));
	//and
	query.appendAnd();
	query.append(TERMINATION_DATE_COLUMN);
	query.appendGreaterThanOrEqualsSign();
	query.append(firstDateInPeriod);
	//and
	query.appendAnd();
	query.append(TERMINATION_DATE_COLUMN);
	query.appendLessThanOrEqualsSign();
	query.append(lastDateInPeriod);
	
	
	//and if relationstatus
	if(relationStatus!= null){
		//and
		query.appendAnd();
		query.append(STATUS_COLUMN);
		query.appendInArrayWithSingleQuotes(relationStatus);		
	}
  	
//	System.out.println("SQL -> "+this.getClass()+":"+query);
	return idoFindPKsByQuery(query); 
	

  }
  
  public Collection ejbFindAllGroupsRelationshipsValidWithinSpecifiedTimePeriod(Group group, Group relatedGroup, Timestamp firstDateInPeriod, Timestamp lastDateInPeriod, String[] relationStatus) throws FinderException{
  	
		//constructing query
		IDOQuery query = idoQuery();
		//select
		query.appendSelectAllFrom(this);
		//where
		query.appendWhere();
		query.appendEquals(GROUP_ID_COLUMN,String.valueOf(group.getPrimaryKey()));
		//and
		query.appendAnd();
		query.appendEquals(RELATED_GROUP_ID_COLUMN,String.valueOf(relatedGroup.getPrimaryKey()));
		//and
		query.appendAnd();
		query.append(INITIATION_DATE_COLUMN);
		query.appendLessThanOrEqualsSign();
		query.append(firstDateInPeriod);
		//and
		query.appendAnd();
		query.append(TERMINATION_DATE_COLUMN);
		query.appendGreaterThanSign();
		query.append(lastDateInPeriod);
	
	
		//and if relationstatus
		if(relationStatus!= null){
			//and
			query.appendAnd();
			query.append(STATUS_COLUMN);
			query.appendInArrayWithSingleQuotes(relationStatus);		
		}
  	
//		System.out.println("SQL -> "+this.getClass()+":"+query);
		return idoFindPKsByQuery(query); 
	

  }
  
  
	public Collection ejbFindAllGroupsRelationshipsValidBeforeAndPastSpecifiedTime(Group group, Group relatedGroup, Timestamp time, String[] relationStatus) throws FinderException{
  	
		//constructing query
		IDOQuery query = idoQuery();
		//select
		query.appendSelectAllFrom(this);
		//where
		query.appendWhere();
		query.appendEquals(GROUP_ID_COLUMN,String.valueOf(group.getPrimaryKey()));
		//and
		query.appendAnd();
		query.appendEquals(RELATED_GROUP_ID_COLUMN,String.valueOf(relatedGroup.getPrimaryKey()));
		//and
		query.appendAnd();
		query.append(INITIATION_DATE_COLUMN);
		query.appendLessThanSign();
		query.append(time);
		//and
		query.appendAnd();
		query.append(TERMINATION_DATE_COLUMN);
		query.appendGreaterThanSign();
		query.append(time);
	
	
		//and if relationstatus
		if(relationStatus!= null){
			//and
			query.appendAnd();
			query.append(STATUS_COLUMN);
			query.appendInArrayWithSingleQuotes(relationStatus);		
		}
  	
//		System.out.println("SQL -> "+this.getClass()+":"+query);
		return idoFindPKsByQuery(query); 
	

	}
  
}