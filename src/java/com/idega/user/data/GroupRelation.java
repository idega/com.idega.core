package com.idega.user.data;

import javax.ejb.*;

public interface GroupRelation extends com.idega.data.IDOEntity
{
 public void setRelationship(com.idega.user.data.GroupRelationType p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getGroup() throws java.rmi.RemoteException;
 public com.idega.user.data.Group getRelatedGroup() throws java.rmi.RemoteException;
 public void setRelatedUser(com.idega.core.user.data.User p0) throws java.rmi.RemoteException;
 public com.idega.user.data.GroupRelationType getRelationship() throws java.rmi.RemoteException;
 public void setGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
}
