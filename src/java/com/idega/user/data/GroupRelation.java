package com.idega.user.data;

import javax.ejb.*;

public interface GroupRelation extends com.idega.data.IDOEntity
{
 public boolean isPassive() throws java.rmi.RemoteException;
 public void setRelatedUser(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.Group getRelatedGroup() throws java.rmi.RemoteException;
 public void setRelatedGroup(int p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getStatus() throws java.rmi.RemoteException;
 public java.sql.Timestamp getTerminationDate() throws java.rmi.RemoteException;
 public java.sql.Timestamp getInitiationDate() throws java.rmi.RemoteException;
 public com.idega.user.data.GroupRelationType getRelationship() throws java.rmi.RemoteException;
 public void setGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public void setPassive() throws java.rmi.RemoteException;
 public void setStatus(java.lang.String p0) throws java.rmi.RemoteException;
 public void setRelationshipType(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getGroup() throws java.rmi.RemoteException;
 public java.lang.String getRelationshipType() throws java.rmi.RemoteException;
 public void setTerminationDate(java.sql.Timestamp p0) throws java.rmi.RemoteException;
 public void setRelatedGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void remove()throws javax.ejb.RemoveException, java.rmi.RemoteException;
 public void setActive() throws java.rmi.RemoteException;
 public void setGroup(int p0) throws java.rmi.RemoteException;
 public void setRelationship(com.idega.user.data.GroupRelationType p0) throws java.rmi.RemoteException;
 public void setInitiationDate(java.sql.Timestamp p0) throws java.rmi.RemoteException;
 public void setDefaultValues() throws java.rmi.RemoteException;
 public boolean isActive() throws java.rmi.RemoteException;
}
