package com.idega.user.data;


public interface UserHome extends com.idega.data.IDOHome
{
 public User create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllUsers()throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findUsersInPrimaryGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;

 public java.util.Collection findAllUsersOrderedByFirstName()throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public User findUserFromEmail(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public User findByPersonalID(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;


}