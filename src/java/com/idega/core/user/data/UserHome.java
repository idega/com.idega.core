package com.idega.core.user.data;


public interface UserHome extends com.idega.data.IDOHome
{
 public User create() throws javax.ejb.CreateException;
 public User createLegacy();
 public User findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public User findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}