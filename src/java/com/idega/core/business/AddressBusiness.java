package com.idega.core.business;

import javax.ejb.*;

public interface AddressBusiness extends com.idega.business.IBOService
{
 public com.idega.core.data.PostalCodeHome getPostalCodeHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.CountryHome getCountryHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getStreetNameFromAddressString(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.core.data.EmailHome getEmailHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.PostalCode getPostalCodeAndCreateIfDoesNotExist(java.lang.String p0,java.lang.String p1,com.idega.core.data.Country p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.AddressHome getAddressHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.lang.String getStreetNumberFromAddressString(java.lang.String p0) throws java.rmi.RemoteException;
}
