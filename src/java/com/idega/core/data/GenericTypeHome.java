package com.idega.core.data;


public interface GenericTypeHome extends com.idega.data.IDOHome
{
 public GenericType create() throws javax.ejb.CreateException;
 public GenericType createLegacy();
 public GenericType findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public GenericType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public GenericType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}