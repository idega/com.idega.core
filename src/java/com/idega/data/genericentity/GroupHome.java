package com.idega.data.genericentity;


public interface GroupHome extends com.idega.data.IDOHome
{
 public Group create() throws javax.ejb.CreateException;
 public Group createLegacy();
 public Group findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Group findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}