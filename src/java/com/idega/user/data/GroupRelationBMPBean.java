package com.idega.user.data;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;
import com.idega.data.IDOStoreException;
import com.idega.data.IDOUtil;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.presentation.IWContext;
import com.idega.user.events.GroupRelationChangedEvent;
import com.idega.user.events.GroupRelationChangedEvent.EventType;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.expression.ELUtil;

/**
 * Description: This bean is used to connect groups together and to keep track of their relations
 * Copyright: Idega Software   Copyright (c) 2001
 * Company: Idega Software
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.1
 */

public class GroupRelationBMPBean extends GenericEntity implements GroupRelation {

	private static final long serialVersionUID = 4570104106589251472L;

	public static final String  TABLE_NAME="IC_GROUP_RELATION",
								GROUP_ID_COLUMN="IC_GROUP_ID",
								RELATED_GROUP_ID_COLUMN="RELATED_IC_GROUP_ID",
								RELATIONSHIP_TYPE_COLUMN="RELATIONSHIP_TYPE",
								STATUS_COLUMN="GROUP_RELATION_STATUS",
								INITIATION_DATE_COLUMN="INITIATION_DATE",
								TERMINATION_DATE_COLUMN="TERMINATION_DATE",
								SET_PASSIVE_BY="SET_PASSIVE_BY",
								CREATED_BY = "CREATED_BY",
								RELATED_GROUP_TYPE_COLUMN = "RELATED_GROUP_TYPE";

  protected static final String INITIATION_MODIFICATION_DATE_COLUMN="INIT_MODIFICATION_DATE";
  protected static final String TERMINATION_MODIFICATION_DATE_COLUMN="TERM_MODIFICATION_DATE";

  public final static String	STATUS_ACTIVE="ST_ACTIVE",
		  						STATUS_PASSIVE="ST_PASSIVE",
		  						STATUS_PASSIVE_PENDING="PASS_PEND",
		  						STATUS_ACTIVE_PENDING="ACT_PEND";

  @Override
public void initializeAttributes() {
    this.addAttribute(getIDColumnName());

    this.addManyToOneRelationship(GROUP_ID_COLUMN,"Type",Group.class);
    this.addManyToOneRelationship(RELATED_GROUP_ID_COLUMN,"Related Group",Group.class);
    this.addAttribute(RELATIONSHIP_TYPE_COLUMN,"Type",true,true,String.class,15,MANY_TO_ONE,GroupRelationType.class);
    this.addAttribute(STATUS_COLUMN,"Status",String.class,30);
    this.addAttribute(INITIATION_DATE_COLUMN,"Relationship Initiation Date",Timestamp.class);
    this.addAttribute(TERMINATION_DATE_COLUMN,"Relationship Termination Date",Timestamp.class);
    this.addAttribute(SET_PASSIVE_BY, "set passive by", true, true, Integer.class, MANY_TO_ONE, User.class);
    this.addAttribute(CREATED_BY, "Created by", true, true, Integer.class, MANY_TO_ONE, User.class);
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
 		addIndex("IDX_IC_GROUP_REL_9", STATUS_COLUMN);
 		addIndex("IDX_IC_GROUP_REL_10", new String[]{GROUP_ID_COLUMN, RELATED_GROUP_ID_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_11", new String[]{RELATED_GROUP_ID_COLUMN, INITIATION_DATE_COLUMN, STATUS_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_12", new String[]{GROUP_ID_COLUMN, RELATED_GROUP_ID_COLUMN, RELATIONSHIP_TYPE_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_13", new String[]{GROUP_ID_COLUMN, RELATED_GROUP_ID_COLUMN, RELATIONSHIP_TYPE_COLUMN, STATUS_COLUMN});
 		addIndex("IDX_IC_GROUP_REL_14", new String[]{RELATED_GROUP_ID_COLUMN, STATUS_COLUMN});
  }

  @Override
public String getEntityName() {
    return TABLE_NAME;
  }

  @Override
public void setDefaultValues(){
    this.setInitiationDate(IWTimestamp.getTimestampRightNow());
    this.setStatus(STATUS_ACTIVE);
  }

  @Override
public void setGroup(Group group){
    this.setColumn(GROUP_ID_COLUMN,group);
  }

  @Override
public void setGroup(int groupID){
    this.setColumn(GROUP_ID_COLUMN,groupID);
  }

  @Override
public Group getGroup(){
    return (Group)getColumnValue(GROUP_ID_COLUMN);
  }

  @Override
public int getGroupID() {
  	return getIntColumnValue(GROUP_ID_COLUMN);
  }

  @Override
public void setRelatedGroup(Group group){
    this.setColumn(RELATED_GROUP_ID_COLUMN,group);
  }

  @Override
public void setRelatedGroup(int groupID){
    this.setColumn(RELATED_GROUP_ID_COLUMN,groupID);
  }

  @Override
public void setRelatedUser(User user){
    setRelatedGroup(user);
  }

  @Override
public Group getRelatedGroup(){
    return (Group)getColumnValue(RELATED_GROUP_ID_COLUMN);
  }

  @Override
public Integer getRelatedGroupPK(){
    return getIntegerColumnValue(RELATED_GROUP_ID_COLUMN);
  }

  @Override
public void setRelationship(GroupRelationType type){
    this.setColumn(RELATIONSHIP_TYPE_COLUMN,type);
  }

  @Override
public void setRelationshipType(String groupRelationType){
    this.setColumn(RELATIONSHIP_TYPE_COLUMN,groupRelationType);
  }

  @Override
public GroupRelationType getRelationship(){
    return (GroupRelationType)getColumnValue(RELATIONSHIP_TYPE_COLUMN);
  }

  @Override
public String getRelationshipType(){
    return getStringColumnValue(RELATIONSHIP_TYPE_COLUMN);
  }


  @Override
public void setStatus(String status){
    setColumn(GroupRelationBMPBean.STATUS_COLUMN,status);
  }

  @Override
public String getStatus(){
    return getStringColumnValue(GroupRelationBMPBean.STATUS_COLUMN);
  }

  @Override
public boolean isActive(){
    String status = this.getStatus();
    if(status != null && status.equals(STATUS_ACTIVE)){
      return true;
    }
    return false;
  }

  @Override
public boolean isPassive(){
    String status = this.getStatus();
    if(status != null && status.equals(STATUS_PASSIVE)){
      return true;
    }
    return false;
  }

	@Override
	public boolean isActivePending(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_ACTIVE_PENDING) ){
			return true;
		}
		return false;
	}

	@Override
	public boolean isPassivePending(){
		String status = this.getStatus();
		if(status != null && status.equals(STATUS_PASSIVE_PENDING) ){
			return true;
		}
		return false;
	}

	@Override
	public boolean isPending(){
		String status = this.getStatus();
		if(status != null && ( status.equals(STATUS_ACTIVE_PENDING) || status.equals(STATUS_PASSIVE_PENDING) ) ){
			return true;
		}
		return false;
	}
  @Override
public void setActive(){
    this.setStatus(STATUS_ACTIVE);
  }

  @Override
public void setPassive(){
    this.setStatus(STATUS_PASSIVE);
  }

	@Override
	public void setPassivePending(){
		this.setStatus(STATUS_PASSIVE_PENDING);
	}

	@Override
	public void setActivePending(){
		this.setStatus(STATUS_ACTIVE_PENDING);
	}

  @Override
public void setInitiationDate(Timestamp stamp){
    this.setColumn(GroupRelationBMPBean.INITIATION_DATE_COLUMN,stamp);
    this.setColumn(INITIATION_MODIFICATION_DATE_COLUMN, IWTimestamp.RightNow().getTimestamp());
  }

  @Override
public Timestamp getInitiationDate(){
    return (Timestamp)getColumnValue(GroupRelationBMPBean.INITIATION_DATE_COLUMN);
  }

  @Override
public Timestamp getInitiationModificationDate(){
		return (Timestamp)getColumnValue(GroupRelationBMPBean.INITIATION_MODIFICATION_DATE_COLUMN);
  }

  @Override
public void setTerminationDate(Timestamp stamp){
    this.setColumn(GroupRelationBMPBean.TERMINATION_DATE_COLUMN,stamp);
    this.setColumn(GroupRelationBMPBean.TERMINATION_MODIFICATION_DATE_COLUMN, IWTimestamp.RightNow().getTimestamp());
  }

  @Override
public Timestamp getTerminationDate(){
    return (Timestamp)getColumnValue(GroupRelationBMPBean.TERMINATION_DATE_COLUMN);
  }

  @Override
public Timestamp getTerminationModificationDate(){
		return (Timestamp)getColumnValue(GroupRelationBMPBean.TERMINATION_MODIFICATION_DATE_COLUMN);
  }
  @Override
public void setPassiveBy(int userId)  {
    setColumn(SET_PASSIVE_BY, userId);
  }

  @Override
public int getPassiveById() {
    return getIntColumnValue(SET_PASSIVE_BY);
  }

  @Override
public User getPassiveBy(){
      return (User)getColumnValue(SET_PASSIVE_BY);
  }

  @Override
public void setCreatedBy(int userId){
      setColumn(CREATED_BY,userId);
  }

  @Override
public void setCreatedBy(Integer userId){
      setColumn(CREATED_BY,userId);
  }

  @Override
public void setCreatedBy(User user){
      setColumn(CREATED_BY,user);
  }

  @Override
public int getCreatedById(){
      return getIntColumnValue(CREATED_BY);
  }

  @Override
public User getCreatedBy(){
      return (User)getColumnValue(CREATED_BY);
  }

  @Override
public void setRelatedGroupType(String groupType)  {
    setColumn(RELATED_GROUP_TYPE_COLUMN, groupType);
  }

  public String getRelatedGroupType() {
  	return getStringColumnValue(RELATED_GROUP_TYPE_COLUMN);
  }

  @Override
public boolean equals(Object obj) {
  	if (obj instanceof GroupRelationBMPBean) {
	  	GroupRelation compareRelation = (GroupRelationBMPBean)obj;
	  	if (this.getRelatedGroupPK().equals(compareRelation.getRelatedGroupPK()) &&
	  		new Integer(this.getGroupID()).equals(new Integer(compareRelation.getGroupID())) &&
	  		this.getStatus().equals(compareRelation.getStatus()) &&
			this.getRelationshipType().equals(compareRelation.getRelationshipType())) {
	  			return true;
	  	}
  	}
  	return false;
  }

  /**Finders begin**/

  public Collection ejbFindGroupsRelationshipsUnder(Group group)throws FinderException{
  	//FIXME why does this method not check for active status?
    return this.idoFindAllIDsByColumnOrderedBySQL(GroupRelationBMPBean.GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group)throws FinderException{
  	//FIXME why does this method not check for active status?
    return this.idoFindAllIDsByColumnOrderedBySQL(GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContaining(Group group,Group relatedGroup)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString()+" and "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+group.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsContainingGroupsAndStatus(Group group,Group relatedGroup, String status)throws FinderException{
	return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString()+" and "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+group.getPrimaryKey().toString()+" and "+GroupRelationBMPBean.STATUS_COLUMN+" like '"+status+"'");
  }


	/**
	 * Finds all relationships specified only in one direction with groupID and relationType as specified ordered by initiation date
	 */
	public Collection ejbFindAllGroupsRelationshipsByRelatedGroupOrderedByInitiationDate(int groupID,String relationType)throws FinderException{
		return idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+groupID
		+" and "+GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN+"='"+relationType+"' order by " + GroupRelationBMPBean.INITIATION_DATE_COLUMN);
	}

  public Collection ejbFindGroupsRelationshipsUnder(int groupID)throws FinderException{
  	//FIXME why does this method not check for active status and use prefetch method?
    return this.idoFindAllIDsByColumnOrderedBySQL(GroupRelationBMPBean.GROUP_ID_COLUMN,groupID);
  }

  /**
   * Finds all active relationships specified only in one direction with groupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+groupID
    +" and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relationType as specified
   */
  public Collection ejbFindGroupsRelationshipsContaining(int groupID,String relationType)throws FinderException{
    return this.idoFindPKsBySQL(ejbHomeGetFindGroupsRelationshipsContainingSQL(groupID,relationType));
  }

  public Collection ejbFindGroupsRelationshipsContaining(int groupId,Collection<String> relationTypes){
	  StringBuilder relations = new StringBuilder();
	  for(Iterator<String> iter = relationTypes.iterator();iter.hasNext();){
		  relations.append(CoreConstants.QOUTE_SINGLE_MARK).append(iter.next()).append(CoreConstants.QOUTE_SINGLE_MARK);
		  if(iter.hasNext()){
			  relations.append(", ");
		  }
	  }
	  StringBuilder sql = new StringBuilder();
	  	sql.append("select " + getIDColumnName() + " from ")
	  	.append(this.getEntityName())
	  	.append(" where ")
	  	.append(GROUP_ID_COLUMN).append("=").append(groupId)
	  	.append(" and ")
	  	.append(RELATIONSHIP_TYPE_COLUMN).append(" IN (").append(relations).append(")")
	  	.append(" and ( ")
	  	.append(STATUS_COLUMN).append("='").append(STATUS_ACTIVE).append("'")
	  	.append(" or ")
	  	.append(STATUS_COLUMN).append("='").append(STATUS_PASSIVE_PENDING).append("' ) ");
	  	try {
	  		return this.idoFindPKsBySQL(sql.toString());
		} catch (Exception e) {
			return Collections.emptyList();
		}
  }

  public String ejbHomeGetFindGroupsRelationshipsContainingSQL(int groupId, String relationType){
  	StringBuffer sql = new StringBuffer();
  	sql.append("select " + getIDColumnName() + " from ")
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
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+groupID
    +" and ("+firstRelationTypeClause+" OR "+secondRelationTypeClause+") and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }



  /**
   * Finds all active relationships specified only in one direction with groupID and relationType as specified
   */
  public Collection ejbFindGroupsRelationshipsByRelatedGroup(int groupID,String relationType)throws FinderException{
    return this.idoFindPKsBySQL("select "+ getIDColumnName() +" from "+this.getTableName()+" where "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+groupID
    +" and "+GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN+"='"+relationType+"' and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relationType ether with value relationType or orRelationType, relationType and orRelationType may be null
   */
  public Collection ejbFindGroupsRelationshipsByRelatedGroup(int groupID,String relationType,String orRelationType)throws FinderException{
    String firstRelationTypeClause = getRelationTypeWhereClause(relationType);
    String secondRelationTypeClause = getRelationTypeWhereClause(orRelationType);
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+groupID
    +" and ("+firstRelationTypeClause+" OR "+secondRelationTypeClause+") and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
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
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where ( ("+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+groupID+" and "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID
    +") or ("+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+groupID+" and "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+relatedGroupID+") ) and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified bidirectionally (in both directions) with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingBiDirectional(int groupID,int relatedGroupID,String relationshipType)throws FinderException{
    return this.idoFindPKsBySQL("select "+ getIDColumnName() +" from "+this.getTableName()+" where ( ("+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+groupID+" and "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+") or ("+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+groupID
    +" and "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+relatedGroupID+") ) and "+GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN+"='"+relationshipType+"' and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingUniDirectional(int groupID,int relatedGroupID)throws FinderException{
    return this.idoFindPKsBySQL("select "+ getIDColumnName() +" from "+this.getTableName()+" where "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+groupID
    +" and "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+" and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

	/**
	 * <p>Bidirectional, all data included</p>
	 * @param groupID is {@link Group#getPrimaryKey()} to search by,
	 * not <code>null</code>;
	 * @param relatedGroupID is {@link Group#getPrimaryKey()} to search by,
	 * not <code>null</code>;
	 * @return primary keys or {@link Collections#emptyList()} on failure;
	 */
	public Collection<Object> ejbFindAllOrderedByDate(int groupID, int relatedGroupID) {
		StringBuilder query = new StringBuilder();
		query.append(" SELECT ").append(getIDColumnName());
		query.append(" FROM ").append(getTableName());
		query.append(" WHERE (");
			query.append(GroupRelationBMPBean.GROUP_ID_COLUMN).append(" = ").append(groupID);
			query.append(" AND ").append(GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN).append(" = ").append(relatedGroupID);
		query.append(") OR (");
			query.append(GroupRelationBMPBean.GROUP_ID_COLUMN).append(" = ").append(relatedGroupID);
			query.append(" AND ").append(GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN).append(" = ").append(groupID);
		query.append(") ORDER BY INITIATION_DATE ");

		try {
			return this.idoFindPKsBySQL(query.toString());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, "Failed to get primary keys by query: '" + query.toString() + "'");
		}

		return Collections.emptyList();
	}

  /**
   * Finds all active relationships specified only in one direction with groupID and relatedGroupID and relationshipType as specified
   */
  public Collection ejbFindGroupsRelationshipsContainingUniDirectional(int groupID,int relatedGroupID,String relationshipType)throws FinderException{
    return this.idoFindPKsBySQL("select "+ getIDColumnName() +" from "+this.getTableName()+" where "+GroupRelationBMPBean.GROUP_ID_COLUMN+"="+groupID+" and "+GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID
    +" and "+GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN+"='"+relationshipType+"' and ( "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_ACTIVE+"' OR "+GroupRelationBMPBean.STATUS_COLUMN+"='"+STATUS_PASSIVE_PENDING+"' ) ");
  }

	/**
		* Finds all active relationships specified only in one direction with groupID and relatedGroupID and relationshipType as specified
		*/
	 public Collection ejbFindAllPendingGroupRelationships()throws FinderException{
		 return this.idoFindPKsBySQL("select "+ getIDColumnName() +" from "+this.getTableName()+" where "+GroupRelationBMPBean.STATUS_COLUMN+" in ('"+STATUS_ACTIVE_PENDING+"','"+STATUS_PASSIVE_PENDING+"')");
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
  @Deprecated
  @Override
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
  @Override
public void removeBy(User currentUser) throws RemoveException{
    this.removeBy(currentUser,IWTimestamp.getTimestampRightNow());

  }

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupRelation#removeBy(com.idega.user.data.User, java.sql.Timestamp)
	 */
	@Override
	public void removeBy(User currentUser, Timestamp time) throws RemoveException{
		if (currentUser != null && currentUser.getPrimaryKey() != null) {
			Integer userId = null;
			String primaryKey = currentUser.getPrimaryKey().toString();
			try {
				userId = Integer.valueOf(primaryKey);
			} catch (NumberFormatException e) {
				getLogger().warning(
						"Failed to convert " + primaryKey +
						" to " + Integer.class.getSimpleName());
			}

			setPassive();
			setTerminationDate(time);
			setPassiveBy(userId);
			store();
		}
	}

	@Override
	public void store() throws IDOStoreException{
		super.store();
		ELUtil.getInstance().publishEvent(new GroupRelationChangedEvent(EventType.GROUP_CHANGE, (Integer) getPrimaryKey()));
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

	/**
  	* Finds all duplicated groupRelations
	*/
	public Collection ejbFindAllDuplicatedGroupRelations()throws FinderException{
    	String subTable = "select ic_group_id, related_ic_group_id from ic_group_relation where relationship_type='GROUP_PARENT' and group_relation_status='ST_ACTIVE' group by ic_group_id, related_ic_group_id having count(*)>1";
		return this.idoFindPKsBySQL("select r.ic_group_relation_id from ic_group_relation r, ("+subTable+") t where r.ic_group_id=t.ic_group_id and r.related_ic_group_id=t.related_ic_group_id and relationship_type='GROUP_PARENT' and group_relation_status='ST_ACTIVE'");
	}

	/**
  	* Finds all duplicated aliases
	*/
	public Collection ejbFindAllDuplicatedAliases()throws FinderException{
		String subTable = "select r.ic_group_id, g.alias_id from ic_group_relation r, ic_group g where r.related_ic_group_id=g.ic_group_id and r.relationship_type='GROUP_PARENT' and r.group_relation_status='ST_ACTIVE' and g.group_type='alias' and alias_id is not null group by r.ic_group_id, g.alias_id having count(*)>1";
		return this.idoFindPKsBySQL("select r.ic_group_relation_id from ic_group_relation r, ic_group g, ("+subTable+") t where r.related_ic_group_id=g.ic_group_id and r.ic_group_id=t.ic_group_id and g.alias_id=t.alias_id and relationship_type='GROUP_PARENT' and group_relation_status='ST_ACTIVE'");
	}

	public Collection ejbFindGroupRelationsByRelatedGroupTypeAndRelatedGroupIdsAndDate(String relatedGroupType, List<String> relatedGroupIds, java.sql.Date dateFrom, java.sql.Date dateTo) throws FinderException{
		IDOUtil util = IDOUtil.getInstance();

		//constructing query
		IDOQuery query = idoQuery();
		//select
		query.appendSelectAllFrom(this);
		//where
		query.appendWhere();
		query.appendEquals(RELATED_GROUP_TYPE_COLUMN, "'" + relatedGroupType + "'");
		//and
		query.appendAnd();
		query.append(GROUP_ID_COLUMN);
		query.appendInForStringCollectionWithSingleQuotes(relatedGroupIds);
		//and
		query.appendAnd();
		query.append("(");
		//dates
		query.append("(");
		query.append(TERMINATION_DATE_COLUMN);
		query.appendIsNotNull();
		query.appendAnd();
		query.appendBetweenDates(TERMINATION_DATE_COLUMN, dateFrom, dateTo);
		query.append(")");
		//or
		query.appendOr();
		query.append("(");
		query.append(TERMINATION_DATE_COLUMN);
		query.appendIsNull();
		query.appendAnd();
		query.append(INITIATION_MODIFICATION_DATE_COLUMN);
		query.appendIsNotNull();
		query.appendAnd();
		query.appendBetweenDates(INITIATION_MODIFICATION_DATE_COLUMN, dateFrom, dateTo);
		query.append(")");
		//or
		query.appendOr();
		query.append("(");
		query.append(TERMINATION_DATE_COLUMN);
		query.appendIsNull();
		query.appendAnd();
		query.append(INITIATION_MODIFICATION_DATE_COLUMN);
		query.appendIsNull();
		query.appendAnd();
		query.append(INITIATION_DATE_COLUMN);
		query.appendIsNotNull();
		query.appendAnd();
		query.appendBetweenDates(INITIATION_DATE_COLUMN, dateFrom, dateTo);
		query.append(")");
		//end
		query.append(")");
		//System.out.println("SQL -> "+this.getClass()+":"+query);
		return idoFindPKsByQuery(query);


	}

}