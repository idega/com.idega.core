package com.idega.user.data;


public interface GroupRelationHome extends com.idega.data.IDOHome
{
 public GroupRelation create() throws javax.ejb.CreateException;
 public GroupRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllDuplicatedAliases()throws javax.ejb.FinderException;
 public java.util.Collection findAllDuplicatedGroupRelations()throws javax.ejb.FinderException;
 public java.util.Collection findAllGroupsRelationshipsByRelatedGroupOrderedByInitiationDate(int p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findAllGroupsRelationshipsTerminatedWithinSpecifiedTimePeriod(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.sql.Timestamp p3,java.lang.String[] p4)throws javax.ejb.FinderException;
 public java.util.Collection findAllGroupsRelationshipsValidBeforeAndPastSpecifiedTime(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.lang.String[] p3)throws javax.ejb.FinderException;
 public java.util.Collection findAllGroupsRelationshipsValidWithinSpecifiedTimePeriod(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.sql.Timestamp p2,java.sql.Timestamp p3,java.lang.String[] p4)throws javax.ejb.FinderException;
 public java.util.Collection findAllGroupsWithoutRelatedGroupType()throws javax.ejb.FinderException;
 public java.util.Collection findAllPendingGroupRelationships()throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContainingBiDirectional(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContainingBiDirectional(int p0,int p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContainingGroupsAndStatus(com.idega.user.data.Group p0,com.idega.user.data.Group p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsUnder(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsRelationshipsUnder(int p0)throws javax.ejb.FinderException;
 public java.lang.String getFindGroupsRelationshipsContainingSQL(int p0,java.lang.String p1);
 public java.lang.String getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(int p0,java.lang.String p1);

}