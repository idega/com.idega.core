package com.idega.user.data;


public interface GroupEventTypeHome extends com.idega.data.IDOHome
{
 public GroupEventType create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupEventType findByPrimaryKey(int id) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public GroupEventType findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;

}