package com.idega.core.location.business;


public interface CommuneBusiness extends com.idega.business.IBOService
{
 public com.idega.core.location.data.Commune getCommune(int p0) throws java.rmi.RemoteException;
 public com.idega.core.location.data.Commune getCommune(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.core.location.data.CommuneHome getCommuneHome()throws com.idega.data.IDOLookupException, java.rmi.RemoteException;
 public java.util.Collection getCommunes() throws java.rmi.RemoteException;
 public com.idega.core.location.data.Commune getDefaultCommune() throws java.rmi.RemoteException;
}
