package com.idega.core.file.data;


public interface ICFileCategoryHome extends com.idega.data.IDOHome
{
 public ICFileCategory create() throws javax.ejb.CreateException;
 public ICFileCategory createLegacy();
 public ICFileCategory findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICFileCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICFileCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}