package com.idega.user.data;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;


public interface UserHome extends com.idega.data.IDOHome
{
 public User create() throws javax.ejb.CreateException;
 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public User findByPersonalID(java.lang.String p0)throws javax.ejb.FinderException;
 public User findUserForUserGroup(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllUsers()throws javax.ejb.FinderException;
 public java.util.Collection findByNames(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException;
 public User findUserForUserRepresentativeGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public java.util.Collection findUsersInPrimaryGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public java.util.Collection findUsersBySearchCondition(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public Collection findUsersBySearchCondition(String condition, Collection validUserPks) throws FinderException, RemoteException;
 public User findUserForUserGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public User findUserFromEmail(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public java.util.Collection findUsers(java.lang.String[] userIDs)throws javax.ejb.FinderException;
 public java.util.Collection findAllUsersOrderedByFirstName()throws javax.ejb.FinderException;
 public int getUserCount()throws com.idega.data.IDOException;
 public java.lang.String getGroupType();

}