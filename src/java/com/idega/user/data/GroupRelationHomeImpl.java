package com.idega.user.data;


public class GroupRelationHomeImpl extends com.idega.data.IDOFactory implements GroupRelationHome
{
 protected Class getEntityInterfaceClass(){
  return GroupRelation.class;
 }


 public GroupRelation create() throws javax.ejb.CreateException{
  return (GroupRelation) super.createIDO();
 }


public java.util.Collection findAllGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsByRelatedGroup(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllGroupsRelationshipsTerminatedWithinSpecifiedTimePeriod(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.sql.Timestamp p3,java.lang.String[] p4)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsTerminatedWithinSpecifiedTimePeriod(p0,p1,p2,p3,p4);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllGroupsRelationshipsValidBeforeAndPastSpecifiedTime(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.lang.String[] p3)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsValidBeforeAndPastSpecifiedTime(p0,p1,p2,p3);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllGroupsRelationshipsValidWithinSpecifiedTimePeriod(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.sql.Timestamp p3,java.lang.String[] p4)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsValidWithinSpecifiedTimePeriod(p0,p1,p2,p3,p4);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllGroupsWithoutRelatedGroupType()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsWithoutRelatedGroupType();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPendingGroupRelationships()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllPendingGroupRelationships();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsByRelatedGroup(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsByRelatedGroup(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(int p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContaining(int p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContainingBiDirectional(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingBiDirectional(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContainingBiDirectional(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingBiDirectional(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContainingGroupsAndStatus(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingGroupsAndStatus(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingUniDirectional(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingUniDirectional(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsUnder(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findGroupsRelationshipsUnder(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public GroupRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupRelation) super.findByPrimaryKeyIDO(pk);
 }


public java.lang.String getFindGroupsRelationshipsContainingSQL(int p0,java.lang.String p1){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupRelationBMPBean)entity).ejbHomeGetFindGroupsRelationshipsContainingSQL(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(int p0,java.lang.String p1){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupRelationBMPBean)entity).ejbHomeGetFindRelatedGroupIdsInGroupRelationshipsContainingSQL(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}