package com.idega.core.user.data;


public interface GenderHome extends com.idega.data.IDOHome
{
 public Gender create() throws javax.ejb.CreateException;
 public Gender createLegacy();
 public Gender findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Gender findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Gender findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}