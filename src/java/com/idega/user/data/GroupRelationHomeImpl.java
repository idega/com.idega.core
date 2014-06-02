package com.idega.user.data;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.util.ListUtil;


public class GroupRelationHomeImpl extends com.idega.data.IDOFactory implements GroupRelationHome
{
 @Override
protected Class getEntityInterfaceClass(){
  return GroupRelation.class;
 }


 @Override
public GroupRelation create() throws javax.ejb.CreateException{
  return (GroupRelation) super.createIDO();
 }


@Override
public java.util.Collection findAllGroupsRelationshipsByRelatedGroupOrderedByInitiationDate(int p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsByRelatedGroupOrderedByInitiationDate(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findAllGroupsRelationshipsTerminatedWithinSpecifiedTimePeriod(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.sql.Timestamp p3,java.lang.String[] p4)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsTerminatedWithinSpecifiedTimePeriod(p0,p1,p2,p3,p4);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findAllGroupsRelationshipsValidBeforeAndPastSpecifiedTime(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.lang.String[] p3)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsValidBeforeAndPastSpecifiedTime(p0,p1,p2,p3);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findAllGroupsRelationshipsValidWithinSpecifiedTimePeriod(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.sql.Timestamp p3,java.lang.String[] p4)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsRelationshipsValidWithinSpecifiedTimePeriod(p0,p1,p2,p3,p4);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findAllGroupsWithoutRelatedGroupType()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllGroupsWithoutRelatedGroupType();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findAllPendingGroupRelationships()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllPendingGroupRelationships();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsByRelatedGroup(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsByRelatedGroup(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContaining(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContaining(int p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}
@Override
public Collection<GroupRelation> findGroupsRelationshipsContaining(int groupId,Collection<String> relationTypes){
	IDOEntity entity = this.idoCheckOutPooledEntity();
	Collection<Integer> ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(groupId,relationTypes);
	if(ListUtil.isEmpty(ids)){
		return Collections.emptyList();
	}
	this.idoCheckInPooledEntity(entity);
	try {
		return this.getEntityCollectionForPrimaryKeys(ids);
	} catch (FinderException e) {
		return Collections.emptyList();
	}
}

@Override
public java.util.Collection findGroupsRelationshipsContaining(int p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContaining(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContainingBiDirectional(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingBiDirectional(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContainingBiDirectional(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingBiDirectional(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContainingGroupsAndStatus(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingGroupsAndStatus(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingUniDirectional(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsContainingUniDirectional(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsUnder(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findGroupsRelationshipsUnder(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindGroupsRelationshipsUnder(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 @Override
public GroupRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupRelation) super.findByPrimaryKeyIDO(pk);
 }


@Override
public java.lang.String getFindGroupsRelationshipsContainingSQL(int p0,java.lang.String p1){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupRelationBMPBean)entity).ejbHomeGetFindGroupsRelationshipsContainingSQL(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

@Override
public java.lang.String getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(int p0,java.lang.String p1){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupRelationBMPBean)entity).ejbHomeGetFindRelatedGroupIdsInGroupRelationshipsContainingSQL(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

@Override
public java.util.Collection findAllDuplicatedGroupRelations() throws javax.ejb.FinderException {
   	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
   	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllDuplicatedGroupRelations();
   	this.idoCheckInPooledEntity(entity);
   	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public java.util.Collection findAllDuplicatedAliases() throws javax.ejb.FinderException {
   	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
   	java.util.Collection ids = ((GroupRelationBMPBean)entity).ejbFindAllDuplicatedAliases();
   	this.idoCheckInPooledEntity(entity);
   	return this.getEntityCollectionForPrimaryKeys(ids);
}
}