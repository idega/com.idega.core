package com.idega.user.data;


public interface GroupHome extends com.idega.data.IDOHome
{
 public Group create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public Group findSystemUsersGroup()throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findGroups(java.lang.String[] p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllGroups(java.lang.String[] p0,boolean p1)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.lang.String getGroupType() throws java.rmi.RemoteException;

}