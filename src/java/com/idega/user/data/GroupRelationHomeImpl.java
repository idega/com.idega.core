package com.idega.user.data;


public class GroupRelationHomeImpl extends com.idega.data.IDOFactory implements GroupRelationHome
{
 protected Class getEntityInterfaceClass(){
  return GroupRelation.class;
 }


 public GroupRelation create() throws javax.ejb.CreateException{
  return (GroupRelation) super.createIDO();
 }


public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsUnder(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0,com.idega.user.data.Group p1)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsUnder(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public GroupRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupRelation) super.findByPrimaryKeyIDO(pk);
 }



}