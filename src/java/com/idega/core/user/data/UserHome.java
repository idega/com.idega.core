package com.idega.core.user.data;


public interface UserHome extends com.idega.data.IDOHome
{
 public User create() throws javax.ejb.CreateException;
 public User createLegacy();
 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public User findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public User findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public User findUserFromEmail(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public User findByPersonalID(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException;

}