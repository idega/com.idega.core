package com.idega.core.file.data;


public interface ICFileTypeHome extends com.idega.data.IDOHome
{
 public ICFileType create() throws javax.ejb.CreateException;
 public ICFileType createLegacy();
 public ICFileType findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICFileType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICFileType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}