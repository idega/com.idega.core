package com.idega.core.data;


public interface ICObjectFieldHome extends com.idega.data.IDOHome
{
 public ICObjectField create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public ICObjectField findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;

}