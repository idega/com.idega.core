package com.idega.core.data;


public interface CountryHome extends com.idega.data.IDOHome
{
 public Country create() throws javax.ejb.CreateException;
 public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException,java.rmi.RemoteException;
 public Country findByCountryName(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 public Country findByIsoAbbreviation(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException;

}