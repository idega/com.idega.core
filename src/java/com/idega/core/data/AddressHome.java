package com.idega.core.data;


public interface AddressHome extends com.idega.data.IDOHome
{
 public Address create() throws javax.ejb.CreateException;
 public Address createLegacy();
 public Address findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Address findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}