package com.idega.user.data;

import javax.ejb.*;

public interface GroupDomainRelation extends com.idega.data.IDOEntity
{
 public com.idega.builder.data.IBDomain getDomain();
 public int getPassiveBy();
 public com.idega.user.data.Group getRelatedGroup();
 public java.lang.Integer getRelatedGroupPK();
 public com.idega.user.data.GroupDomainRelationType getRelationship();
 public void initializeAttributes();
 public void setDomain(com.idega.builder.data.IBDomain p0);
 public void setDomain(int p0);
 public void setPassiveBy(int p0);
 public void setRelatedGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException;
 public void setRelatedGroup(int p0)throws java.rmi.RemoteException;
 public void setRelatedUser(com.idega.user.data.User p0)throws java.rmi.RemoteException;
 public void setRelationship(com.idega.user.data.GroupDomainRelationType p0);
 public void removeBy(com.idega.user.data.User p0) throws RemoveException;
}
