package com.idega.core.location.data;

import java.util.Collection;

import javax.ejb.FinderException;


public interface AddressHome extends com.idega.data.IDOHome
{
 public Address create() throws javax.ejb.CreateException;
 public Address createLegacy();
 public Address findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Address findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public Address findPrimaryUserAddress(int userID) throws javax.ejb.FinderException;
 public Address findUserAddressByAddressType(int userID,AddressType type) throws javax.ejb.FinderException;
 public java.util.Collection findPrimaryUserAddresses(String[] userIDs) throws javax.ejb.FinderException;
 public java.util.Collection findPrimaryUserAddresses(com.idega.data.IDOQuery query) throws javax.ejb.FinderException;
 public java.util.Collection findUserAddressesByAddressType(int userID,AddressType type) throws javax.ejb.FinderException ;
 public com.idega.core.location.data.AddressType getAddressType2()throws java.rmi.RemoteException;
 public com.idega.core.location.data.AddressType getAddressType1()throws java.rmi.RemoteException;
 public Collection findByPostalCode(Integer postalCodeID)throws FinderException;
}