package com.idega.user.data;

import java.sql.Timestamp;
import com.idega.builder.data.IBDomain;
import com.idega.data.*;

import java.util.Date;
import java.util.Collection;
import java.rmi.RemoteException;
import javax.ejb.*;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GroupDomainRelationBMPBean extends GenericEntity implements GroupDomainRelation{

  private static String TABLE_NAME="IC_GROUP_DOMAIN_RELATION";
  private static String DOMAIN_ID_COLUMN="IB_DOMAIN_ID";
  private static String RELATED_GROUP_ID_COLUMN="RELATED_IC_GROUP_ID";
  private static String RELATIONSHIP_TYPE_COLUMN="RELATIONSHIP_TYPE";
  private static String STATUS_COLUMN="GROUP_RELATION_STATUS";
  private static String INITIATION_DATE_COLUMN="INITIATION_DATE";
  private static String TERMINATION_DATE_COLUMN="TERMINATION_DATE";


  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());

    this.addManyToOneRelationship(DOMAIN_ID_COLUMN,"Domain",IBDomain.class);
    this.addManyToOneRelationship(RELATED_GROUP_ID_COLUMN,"Related Group",Group.class);
    this.addAttribute(RELATIONSHIP_TYPE_COLUMN,"Type",true,true,String.class, 30, "many-to-one", GroupDomainRelationType.class);
    this.addAttribute(STATUS_COLUMN,"Status",String.class);
    this.addAttribute(INITIATION_DATE_COLUMN,"Relationship Initiation Date",Timestamp.class);
    this.addAttribute(TERMINATION_DATE_COLUMN,"Relationship Termination Date",Timestamp.class);
  }

  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setDomain(IBDomain domain){
    this.setColumn(DOMAIN_ID_COLUMN,domain);
  }

  public void setDomain(int domainID){
    this.setColumn(DOMAIN_ID_COLUMN,domainID);
  }

  public IBDomain getDomain(){
    return (IBDomain)getColumnValue(DOMAIN_ID_COLUMN);
  }

  public void setRelatedGroup(Group group)throws RemoteException{
    this.setColumn(RELATED_GROUP_ID_COLUMN,group);
  }

  public void setRelatedGroup(int groupID)throws RemoteException{
    this.setColumn(RELATED_GROUP_ID_COLUMN,groupID);
  }

  public void setRelatedUser(User user)throws RemoteException{
    setRelatedGroup(user);
  }

  public Group getRelatedGroup(){
    return (Group)getColumnValue(RELATED_GROUP_ID_COLUMN);
  }

  public Integer getRelatedGroupPK(){
    return (Integer)getIntegerColumnValue(RELATED_GROUP_ID_COLUMN);
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
      catch (RemoteException ex) {
        throw new EJBException(ex);
      }

    } else {
      return (GroupDomainRelationType)obj;
    }
  }

  /**Finders begin**/

  public Collection ejbFindGroupsRelationshipsUnder(IBDomain domain)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.DOMAIN_ID_COLUMN,domain.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsUnder(IBDomain domain, GroupDomainRelationType type)throws FinderException,RemoteException{
    IDOQuery query = idoQuery();
    query.appendSelectAllFrom(getEntityName());
    query.appendWhere(RELATIONSHIP_TYPE_COLUMN);
    query.appendLike();
    query.appendWithinSingleQuotes(type.getPrimaryKey());
    return this.idoFindPKsBySQL(query.toString());
//    return this.idoFindAllIDsByColumnOrderedBySQL(this.DOMAIN_ID_COLUMN,domain.getPrimaryKey().toString());
  }

  public Collection ejbFindDomainsRelationshipsContaining(Group group)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,group.getPrimaryKey().toString());
  }

  public Collection ejbFindDomainsRelationshipsContaining(IBDomain domain,Group relatedGroup)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+domain.getPrimaryKey().toString()+" and "+this.DOMAIN_ID_COLUMN+"="+relatedGroup.getPrimaryKey().toString());
  }

  public Collection ejbFindGroupsRelationshipsUnder(int domainID)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.DOMAIN_ID_COLUMN,domainID);
  }

  public Collection ejbFindDomainsRelationshipsContaining(int groupID)throws FinderException,RemoteException{
    return this.idoFindAllIDsByColumnOrderedBySQL(this.RELATED_GROUP_ID_COLUMN,groupID);
  }

  public Collection ejbFindGroupsRelationshipsContaining(int domainID,int relatedGroupID)throws FinderException,RemoteException{
    return this.idoFindPKsBySQL("select * from "+this.getTableName()+" where "+this.RELATED_GROUP_ID_COLUMN+"="+relatedGroupID+" and "+this.DOMAIN_ID_COLUMN+"="+domainID);
  }

  /**Finders end**/

}