package com.idega.data;


public interface TreeableEntityHome extends com.idega.data.IDOHome
{
 public TreeableEntity create() throws javax.ejb.CreateException;
 public TreeableEntity createLegacy();
 public TreeableEntity findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public TreeableEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public TreeableEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}