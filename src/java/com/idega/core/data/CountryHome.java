package com.idega.core.data;


public interface CountryHome extends com.idega.data.IDOHome
{
 public Country create() throws javax.ejb.CreateException, java.rmi.RemoteException;

 public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public Country findByIsoAbbreviation(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
public Country findByCountryName(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;

}