package com.idega.core.data;


public interface Commune extends com.idega.data.IDOEntity
{
 public int getProvinceID() throws java.rmi.RemoteException;
 public java.lang.String getCommuneName() throws java.rmi.RemoteException;
 public java.lang.String getColumnNameCommuneName() throws java.rmi.RemoteException;
 public void setProvince(com.idega.core.data.Province p0) throws java.rmi.RemoteException;
 public void setProvinceID(int p0) throws java.rmi.RemoteException;
 public com.idega.core.data.Province getProvince() throws java.rmi.RemoteException;
 public void setCommuneName(java.lang.String p0) throws java.rmi.RemoteException;
}
