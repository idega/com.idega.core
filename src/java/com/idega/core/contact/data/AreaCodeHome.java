package com.idega.core.contact.data;


public interface AreaCodeHome extends com.idega.data.IDOHome
{
 public AreaCode create() throws javax.ejb.CreateException;
 public AreaCode createLegacy();
 public AreaCode findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public AreaCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public AreaCode findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}