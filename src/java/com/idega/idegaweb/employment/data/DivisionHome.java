package com.idega.idegaweb.employment.data;


public interface DivisionHome extends com.idega.data.IDOHome
{
 public Division create() throws javax.ejb.CreateException;
 public Division createLegacy();
 public Division findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Division findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Division findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}