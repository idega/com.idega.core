package com.idega.core.accesscontrol.data;


public interface LoginRecordHome extends com.idega.data.IDOHome
{
 public LoginRecord create() throws javax.ejb.CreateException;
 public LoginRecord createLegacy();
 public LoginRecord findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public LoginRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public LoginRecord findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}