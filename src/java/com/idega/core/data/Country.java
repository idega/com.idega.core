package com.idega.core.data;


public interface Country extends com.idega.data.IDOEntity
{
 public java.lang.String getName() throws java.rmi.RemoteException;
 public void setIsoAbbreviation(java.lang.String p0) throws java.rmi.RemoteException;
 public void setName(java.lang.String p0) throws java.rmi.RemoteException;
 public void setDescription(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getIsoAbbreviation() throws java.rmi.RemoteException;
 public java.lang.String getDescription() throws java.rmi.RemoteException;
}
