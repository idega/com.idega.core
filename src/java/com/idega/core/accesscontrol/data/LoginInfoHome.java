package com.idega.core.accesscontrol.data;


public interface LoginInfoHome extends com.idega.data.IDOHome
{
 public LoginInfo create() throws javax.ejb.CreateException;
 public LoginInfo createLegacy();
 public LoginInfo findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public LoginInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public LoginInfo findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}