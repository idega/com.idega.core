package com.idega.core.data;


public interface ICFileHome extends com.idega.data.IDOHome
{
 public ICFile create() throws javax.ejb.CreateException;
 public ICFile createLegacy();
 public ICFile findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICFile findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public ICFile findByFileName(java.lang.String p0)throws javax.ejb.FinderException;
}