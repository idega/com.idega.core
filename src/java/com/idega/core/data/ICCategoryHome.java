package com.idega.core.data;


public interface ICCategoryHome extends com.idega.data.IDOHome
{
 public ICCategory create() throws javax.ejb.CreateException;
 public ICCategory createLegacy();
 public ICCategory findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}