package com.idega.user.data;


public interface GroupEvent extends com.idega.data.IDOEntity
{
 public void setRegistrant(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public void setEventType(com.idega.user.data.GroupEventType p0) throws java.rmi.RemoteException;
 public java.sql.Date getDateOccured() throws java.rmi.RemoteException;
 public void setGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getGroup() throws java.rmi.RemoteException;
 public void setDateRegistered(java.sql.Date p0) throws java.rmi.RemoteException;
 public void setDescription(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getRegistrant() throws java.rmi.RemoteException;
 public com.idega.user.data.GroupEventType getEventType() throws java.rmi.RemoteException;
 public java.lang.String getDescription() throws java.rmi.RemoteException;
 public void setDateOccured(java.sql.Date p0) throws java.rmi.RemoteException;
 public java.sql.Date getDateRegistered() throws java.rmi.RemoteException;
}
