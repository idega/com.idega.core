package com.idega.core.location.business;


public interface AddressBusiness extends com.idega.business.IBOService
{
 public com.idega.core.location.data.PostalCodeHome getPostalCodeHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.CountryHome getCountryHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getStreetNameFromAddressString(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.core.contact.data.EmailHome getEmailHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.PostalCode getPostalCodeAndCreateIfDoesNotExist(java.lang.String p0,java.lang.String p1,com.idega.core.location.data.Country p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.AddressHome getAddressHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getStreetNumberFromAddressString(java.lang.String p0) throws java.rmi.RemoteException;
}
