package com.idega.user.data;


public interface GroupRelationHome extends com.idega.data.IDOHome
{
 public GroupRelation create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1,java.lang.String p2)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,java.lang.String p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsUnder(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1,java.lang.String p2)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsUnder(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContainingUniDirectional(int p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContaining(com.idega.user.data.Group p0,com.idega.user.data.Group p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;

}