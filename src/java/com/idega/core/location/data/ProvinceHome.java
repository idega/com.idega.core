package com.idega.core.location.data;


public interface ProvinceHome extends com.idega.data.IDOHome
{
 public Province create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public Province findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public Province findByProvinceNameAndCountryId(java.lang.String p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;

}