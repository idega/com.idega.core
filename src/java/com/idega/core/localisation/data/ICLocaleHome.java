package com.idega.core.localisation.data;


public interface ICLocaleHome extends com.idega.data.IDOHome
{
 public ICLocale create() throws javax.ejb.CreateException;
 public ICLocale createLegacy();
 public ICLocale findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICLocale findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICLocale findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}