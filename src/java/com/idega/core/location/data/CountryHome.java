package com.idega.core.location.data;

import java.util.Collection;

public interface CountryHome extends com.idega.data.IDOHome
{
 public Country create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public Collection findAll() throws javax.ejb.FinderException;
 public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Country findByIsoAbbreviation(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException;
public Country findByCountryName(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException;

}