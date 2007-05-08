package com.idega.user.data;

import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.core.builder.data.ICDomain;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.presentation.IWContext;
import com.idega.util.IWTimestamp;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class GroupDomainRelationBMPBean extends GenericEntity implements GroupDomainRelation{

	protected static final String  TABLE_NAME="IC_GROUP_DOMAIN_RELATION";
	private static final String  DOMAIN_ID_COLUMN="IB_DOMAIN_ID";
	protected static final String  RELATED_GROUP_ID_COLUMN="RELATED_IC_GROUP_ID";
	protected static final String  RELATIONSHIP_TYPE_COLUMN="RELATIONSHIP_TYPE";
	protected static final String  STATUS_COLUMN="GROUP_RELATION_STATUS";
	private static final String  INITIATION_DATE_COLUMN="INITIATION_DATE";
	private static final String  TERMINATION_DATE_COLUMN="TERMINATION_DATE";
  private static final String  SET_PASSIVE_BY="SET_PASSIVE_BY";
  
	private static final String  STATUS_PASSIVE="ST_PASSIVE";


  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());

    this.addManyToOneRelationship(DOMAIN_ID_COLUMN,"Domain",ICDomain.class);
    this.addManyToOneRelationship(RELATED_GROUP_ID_COLUMN,"Related Group",Group.class);
    this.addAttribute(RELATIONSHIP_TYPE_COLUMN,"Type",true,true,String.class, 30, "many-to-one", GroupDomainRelationType.class);
    this.addAttribute(STATUS_COLUMN,"Status",String.class);
    this.addAttribute(INITIATION_DATE_COLUMN,"Relationship Initiation Date",Timestamp.class);
    this.addAttribute(TERMINATION_DATE_COLUMN,"Relationship Termination Date",Timestamp.class);
    this.addAttribute(SET_PASSIVE_BY, "set passive by", true, true, Integer.class, MANY_TO_ONE, User.class);

    addIndex("IDX_IC_GROUP_DOM_REL_8", RELATIONSHIP_TYPE_COLUMN);
//	this.addMetaDataRelationship(); 
}

  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setDomain(ICDomain domain){
    this.setColumn(DOMAIN_ID_COLUMN,domain);
  }

  public void setDomain(int domainID){
    this.setColumn(DOMAIN_ID_COLUMN,domainID);
  }

  public ICDomain getDomain(){
    return (ICDomain)getColumnValue(DOMAIN_ID_COLUMN);
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
    return getIntegerColumnValue(RELATED_GROUP_ID_COLUMN);
  }

  public void setRelationship(GroupDomainRelationType type){
    this.setColumn(RELATIONSHIP_TYPE_COLUMN,type);
  }

  public GroupDomainRelationType getRelationship(){
    Object obj = getColumnValue(RELATIONSHIP_TYPE_COLUMN);
    if(obj instanceof String ){
      try {
        return ((GroupDomainRelationTypeHome)IDOLookup.getHome(GroupDomainRelationType.class)).findByPrimaryKey(obj);
      }
      catch (FinderException ex) {
        ex.printStackTrace();
        return null;
      }
      catch (IDOLookupException ex) {
        throw new EJBException(ex);
      }

    } else {
      return (GroupDomainRelationType)obj;
    }
  }
  
  public void setPassiveBy(int userId)  {
    setColumn(SET_PASSIVE_BY, userId);
  }

  public int getPassiveBy() { 
    return getIntColumnValue(SET_PASSIVE_BY);
  }

  /**Finders begin**/

  public Collection ejbFindGroupsRelationshipsUnder(ICDomain domain)throws FinderException{
    //return this.idoFindAllIDsByColumnOrderedBySQL(this.DOMAIN_ID_COLUMN,domain.getPrimaryKey().toString());
    return idoFindPKsBySQL("select * from "+this.getTableName()+" where "+DOMAIN_ID_COLUMN+"="+ (domain.getPrimaryKey().toString()) +" and "+  " GROUP_RELATION_STATUS IS NULL");
  }

  public Collection ejbFindGroupsRelationshipsUnder(ICDomain domain, GroupDomainRelationType type)throws FinderException{
    IDOQuery query = idoQuery();
    query.appendSelectAllFrom(getEntityName());
    query.appendWhere(RELATIONSHIP_TYPE_COLUMN);
    query.appendLike();
    query.appendWithinSingleQuotes(type.getPrimaryKey());
    query.appendAnd();
    query.append(" GROUP_RELATION_STATUS IS NULL"); 
    return this.idoFindPKsBySQL(query.toString());
//    return this.idoFindAllIDsByColumnOrderedBySQL(this.DOMAIN_ID_COLUMN,domain.getPrimaryKey().toString());
  }

  public Collection ejbFindDomainsRelationshipsContaining(Group group)throws FinderException{
    //return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,group.getPrimaryKey().toString());
    return idoFindPKsBySQL("select * from "+this.getTableName()+" where "+RELATED_GROUP_ID_COLUMN+"="+ (group.getPrimaryKey().toString()) +" and "+  " GROUP_RELATION_STATUS IS NULL");
  }
  

  public Collection ejbFindDomainsRelationshipsContaining(ICDomain domain,Group relatedGroup)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupDomainRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString()+" and "+
      GroupDomainRelationBMPBean.DOMAIN_ID_COLUMN+"="+ domain.getPrimaryKey().toString() +" and "+  " GROUP_RELATION_STATUS IS NULL");
  }

  public Collection ejbFindGroupsRelationshipsUnder(int domainID)throws FinderException{
    //return this.idoFindAllIDsByColumnOrderedBySQL(this.DOMAIN_ID_COLUMN,domainID);
    return idoFindPKsBySQL("select * from "+this.getTableName()+" where "+DOMAIN_ID_COLUMN+"="+ Integer.toString(domainID) +" and "+  " GROUP_RELATION_STATUS IS NULL");
  }
  
	public Collection ejbFindGroupsRelationshipsUnderDomainByRelationshipType(int domainID, String relationType )throws FinderException{
		return idoFindPKsBySQL("select * from "+this.getTableName()+" where "+DOMAIN_ID_COLUMN+"="+ Integer.toString(domainID)+" and "+RELATIONSHIP_TYPE_COLUMN+"='"+relationType+"' and "+ STATUS_COLUMN+" IS NULL");
	}

  public Collection ejbFindDomainsRelationshipsContaining(int groupID)throws FinderException{
    //return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,groupID);
    return idoFindPKsBySQL("select * from "+this.getTableName()+" where "+RELATED_GROUP_ID_COLUMN+"="+ Integer.toString(groupID) +" and "+  " GROUP_RELATION_STATUS IS NULL");
  }

  public Collection ejbFindGroupsRelationshipsContaining(int domainID,int relatedGroupID)throws FinderException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+GroupDomainRelationBMPBean.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+" and "
      +GroupDomainRelationBMPBean.DOMAIN_ID_COLUMN+"="+domainID +" and "+  " GROUP_RELATION_STATUS IS NULL");
  }

  /**Finders end**/

  /**
   * @deprecated Replaced with removeBy(User)
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
    int userId = ((Integer) currentUser.getPrimaryKey()).intValue(); 
    this.setColumn(STATUS_COLUMN,STATUS_PASSIVE);
    this.setColumn(TERMINATION_DATE_COLUMN, IWTimestamp.getTimestampRightNow());
    setPassiveBy(userId);
    store();
  }
  
	public String ejbHomeGetFindGroupsDomainRelationshipsContainingSQL(int domainId, String relationType){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ")
		.append(this.getEntityName())
		.append(" where ")
		.append(DOMAIN_ID_COLUMN).append("=").append(domainId)
		.append(" and ")
		.append(RELATIONSHIP_TYPE_COLUMN).append("='").append(relationType).append("'")
		.append(" and GROUP_RELATION_STATUS IS NULL");
		
		return sql.toString();
	}
  
	public String ejbHomeGetFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(int domainId, String relationType){
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(RELATED_GROUP_ID_COLUMN).append(" from ")
		.append(this.getEntityName())
		.append(" where ")
		.append(DOMAIN_ID_COLUMN).append("=").append(domainId)
		.append(" and ")
		.append(RELATIONSHIP_TYPE_COLUMN).append("='").append(relationType).append("'")
		.append(" and GROUP_RELATION_STATUS IS NULL");
  	
		return sql.toString();
	}
}