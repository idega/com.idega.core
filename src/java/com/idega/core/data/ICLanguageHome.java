package com.idega.core.data;


public interface ICLanguageHome extends com.idega.data.IDOHome
{
 public ICLanguage create() throws javax.ejb.CreateException;
 public ICLanguage createLegacy();
 public ICLanguage findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICLanguage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICLanguage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}