package com.idega.core.data;


public interface ICObjectHome extends com.idega.data.IDOHome
{
 public ICObject create() throws javax.ejb.CreateException;
 public ICObject createLegacy();
 public ICObject findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}