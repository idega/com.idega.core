package com.idega.core.data;


public interface AddressTypeHome extends com.idega.data.IDOHome
{
 public AddressType create() throws javax.ejb.CreateException;
 public AddressType createLegacy();
 public AddressType findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public AddressType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public AddressType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}