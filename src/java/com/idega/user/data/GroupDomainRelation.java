package com.idega.user.data;

import javax.ejb.*;

public interface GroupDomainRelation extends com.idega.data.IDOEntity
{
 public void setRelatedGroup(int p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setDomain(com.idega.builder.data.IBDomain p0) throws java.rmi.RemoteException;
 public com.idega.user.data.GroupDomainRelationType getRelationship() throws java.rmi.RemoteException;
 public void setRelatedGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setDomain(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getRelatedGroup() throws java.rmi.RemoteException;
 public java.lang.Integer getRelatedGroupPK() throws java.rmi.RemoteException;
 public void setRelatedUser(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setRelationship(com.idega.user.data.GroupDomainRelationType p0) throws java.rmi.RemoteException;
 public com.idega.builder.data.IBDomain getDomain() throws java.rmi.RemoteException;
}
