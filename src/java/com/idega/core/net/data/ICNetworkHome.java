package com.idega.core.net.data;


public interface ICNetworkHome extends com.idega.data.IDOHome
{
 public ICNetwork create() throws javax.ejb.CreateException;
 public ICNetwork createLegacy();
 public ICNetwork findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICNetwork findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICNetwork findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}