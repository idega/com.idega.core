package com.idega.core.data;


public interface CommuneHome extends com.idega.data.IDOHome
{
 public Commune create() throws javax.ejb.CreateException;
 public Commune findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Commune findByCommuneNameAndProvinceId(java.lang.String p0,int p1)throws javax.ejb.FinderException,java.rmi.RemoteException;

}