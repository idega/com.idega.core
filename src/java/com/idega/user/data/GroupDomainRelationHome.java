package com.idega.user.data;


public interface GroupDomainRelationHome extends com.idega.data.IDOHome
{
 public GroupDomainRelation create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupDomainRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsUnder(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findDomainsRelationshipsContaining(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findDomainsRelationshipsContaining(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsContaining(int p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findDomainsRelationshipsContaining(com.idega.builder.data.IBDomain p0,com.idega.user.data.Group p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroupsRelationshipsUnder(com.idega.builder.data.IBDomain p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;

}