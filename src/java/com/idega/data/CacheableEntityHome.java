package com.idega.data;


public interface CacheableEntityHome extends com.idega.data.IDOHome
{
 public CacheableEntity create() throws javax.ejb.CreateException;
 public CacheableEntity createLegacy();
 public CacheableEntity findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public CacheableEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public CacheableEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}