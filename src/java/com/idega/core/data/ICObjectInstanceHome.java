package com.idega.core.data;


public interface ICObjectInstanceHome extends com.idega.data.IDOHome
{
 public ICObjectInstance create() throws javax.ejb.CreateException;
 public ICObjectInstance createLegacy();
 public ICObjectInstance findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICObjectInstance findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICObjectInstance findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}