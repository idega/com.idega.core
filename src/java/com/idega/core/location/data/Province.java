package com.idega.core.location.data;


public interface Province extends com.idega.data.IDOEntity
{
 public java.lang.String getProvinceName() throws java.rmi.RemoteException;
 public void setCountryID(int p0) throws java.rmi.RemoteException;
 public int getCountryID() throws java.rmi.RemoteException;
 public void setProvinceName(java.lang.String p0) throws java.rmi.RemoteException;
 public void setCountry(com.idega.core.location.data.Country p0) throws java.rmi.RemoteException;
 public com.idega.core.location.data.Country getCountry() throws java.rmi.RemoteException;
 public java.lang.String getColumnNameProvinceName() throws java.rmi.RemoteException;
}
