package com.idega.data;


public interface CategoryEntityHome extends com.idega.data.IDOHome
{
 public CategoryEntity create() throws javax.ejb.CreateException;
 public CategoryEntity createLegacy();
 public CategoryEntity findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public CategoryEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public CategoryEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}