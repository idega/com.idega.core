package com.idega.user.data;


public interface GroupEventHome extends com.idega.data.IDOHome
{
 public GroupEvent create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupEvent findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;

}