package com.idega.core.file.data;


public interface ICMimeTypeHome extends com.idega.data.IDOHome
{
 public ICMimeType create() throws javax.ejb.CreateException;
 public ICMimeType createLegacy();
 public ICMimeType findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICMimeType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICMimeType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}