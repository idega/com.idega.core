package com.idega.user.data;


public class GroupDomainRelationTypeHomeImpl extends com.idega.data.IDOFactory implements GroupDomainRelationTypeHome
{
 protected Class getEntityInterfaceClass(){
  return GroupDomainRelationType.class;
 }


 public GroupDomainRelationType create() throws javax.ejb.CreateException{
  return (GroupDomainRelationType) super.createIDO();
 }


 public GroupDomainRelationType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupDomainRelationType) super.findByPrimaryKeyIDO(pk);
 }


public com.idega.user.data.GroupDomainRelationType getTopNodeRelationType()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	com.idega.user.data.GroupDomainRelationType theReturn = ((GroupDomainRelationTypeBMPBean)entity).ejbHomeGetTopNodeRelationType();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getTopNodeRelationTypeString(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupDomainRelationTypeBMPBean)entity).ejbHomeGetTopNodeRelationTypeString();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}