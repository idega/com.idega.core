package com.idega.user.data;


public class GroupDomainRelationHomeImpl extends com.idega.data.IDOFactory implements GroupDomainRelationHome
{
 protected Class getEntityInterfaceClass(){
  return GroupDomainRelation.class;
 }


 public GroupDomainRelation create() throws javax.ejb.CreateException{
  return (GroupDomainRelation) super.createIDO();
 }


public java.util.Collection findGroupsRelationshipsUnder(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupDomainRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findDomainsRelationshipsContaining(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupDomainRelationBMPBean)entity).ejbFindDomainsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findDomainsRelationshipsContaining(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupDomainRelationBMPBean)entity).ejbFindDomainsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupDomainRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findDomainsRelationshipsContaining(com.idega.builder.data.IBDomain p0,com.idega.user.data.Group p1)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupDomainRelationBMPBean)entity).ejbFindDomainsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsUnder(com.idega.builder.data.IBDomain p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupDomainRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public GroupDomainRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupDomainRelation) super.findByPrimaryKeyIDO(pk);
 }



}