package com.idega.core.accesscontrol.data;


public interface LoginTableHome extends com.idega.data.IDOHome
{
 public LoginTable create() throws javax.ejb.CreateException;
 public LoginTable createLegacy();
 public LoginTable findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public LoginTable findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public LoginTable findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findLoginsForUser(com.idega.core.user.data.User p0)throws javax.ejb.FinderException;
 public java.util.Collection findByLogin(String login)throws javax.ejb.FinderException;
}