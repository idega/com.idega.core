package com.idega.core.data;


public interface PostalCode extends com.idega.data.IDOEntity
{
 public void setPostalCode(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getName() throws java.rmi.RemoteException;
 public java.lang.String getPostalAddress() throws java.rmi.RemoteException;
 public void setCountryID(int p0) throws java.rmi.RemoteException;
 public void initializeAttributes() throws java.rmi.RemoteException;
 public java.lang.String getPostalCode() throws java.rmi.RemoteException;
 public void setName(java.lang.String p0) throws java.rmi.RemoteException;
 public int getCountryID() throws java.rmi.RemoteException;
 public void setCountry(com.idega.core.data.Country p0) throws java.rmi.RemoteException;
 public com.idega.core.data.Country getCountry() throws java.rmi.RemoteException;
}
