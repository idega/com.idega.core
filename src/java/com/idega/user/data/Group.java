package com.idega.user.data;

import javax.ejb.*;

public interface Group extends com.idega.data.IDOEntity,com.idega.core.ICTreeNode
{
 public void removeGroup(int p0,boolean p1)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public void removeUser(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setGroupType(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getGroupTypeValue() throws java.rmi.RemoteException;
 public void setExtraInfo(java.lang.String p0) throws java.rmi.RemoteException;
 public boolean equals(com.idega.data.IDOLegacyEntity p0) throws java.rmi.RemoteException;
 public void removeGroup()throws javax.ejb.EJBException, java.rmi.RemoteException;
 public void setDescription(java.lang.String p0) throws java.rmi.RemoteException;
 public boolean equals(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public void addGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.List getGroupsContained(java.lang.String[] p0,boolean p1)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.List getListOfAllGroupsContaining(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public void addGroup(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.List getListOfAllGroupsContained()throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getAllGroupsContainingUser(com.idega.user.data.User p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public void setDefaultValues() throws java.rmi.RemoteException;
 public java.lang.String getDescription() throws java.rmi.RemoteException;
 public void removeGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.lang.String getGroupType() throws java.rmi.RemoteException;
 public java.lang.String getName() throws java.rmi.RemoteException;
 public void setName(java.lang.String p0) throws java.rmi.RemoteException;
 public java.util.List getListOfAllGroupsContainingThis()throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.lang.String getExtraInfo() throws java.rmi.RemoteException;
 public void addUser(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
}
