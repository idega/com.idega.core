package com.idega.data;


public interface MetaDataHome extends com.idega.data.IDOHome
{
 public MetaData create() throws javax.ejb.CreateException;
 public MetaData createLegacy();
 public MetaData findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public MetaData findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public MetaData findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}