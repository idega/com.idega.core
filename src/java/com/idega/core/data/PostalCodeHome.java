package com.idega.core.data;


public interface PostalCodeHome extends com.idega.data.IDOHome
{
 public PostalCode create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllOrdererByCode()throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection findAll()throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection findByPostalCodeAndCountryId(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public PostalCode findByPostalCodeAndCountryId(java.lang.String p0,int p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;

}