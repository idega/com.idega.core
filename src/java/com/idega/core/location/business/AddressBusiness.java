package com.idega.core.location.business;

import com.idega.core.location.data.Address;


public interface AddressBusiness extends com.idega.business.IBOService
{
 public com.idega.core.location.data.PostalCodeHome getPostalCodeHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.CountryHome getCountryHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getStreetNameFromAddressString(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.core.contact.data.EmailHome getEmailHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.PostalCode getPostalCodeAndCreateIfDoesNotExist(java.lang.String p0,java.lang.String p1,com.idega.core.location.data.Country p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.AddressHome getAddressHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getStreetNumberFromAddressString(java.lang.String p0) throws java.rmi.RemoteException;
 /**
  * Creates the fully qualifying address string with postal and country info from the address bean<br>
  * seperated by a ","
  * @return The full address string with postal info and country
  */
 public String getFullAddressString(Address address) throws java.rmi.RemoteException;
}
