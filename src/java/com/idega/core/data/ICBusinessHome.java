package com.idega.core.data;


public interface ICBusinessHome extends com.idega.data.IDOHome
{
 public ICBusiness create() throws javax.ejb.CreateException;
 public ICBusiness createLegacy();
 public ICBusiness findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICBusiness findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICBusiness findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}