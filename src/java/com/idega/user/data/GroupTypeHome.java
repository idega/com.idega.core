package com.idega.user.data;


public interface GroupTypeHome extends com.idega.data.IDOHome
{
 public GroupType create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupType findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllGroupTypes()throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findVisibleGroupTypes()throws javax.ejb.FinderException, java.rmi.RemoteException;
 public int getNumberOfVisibleGroupTypes()throws com.idega.data.IDOException,javax.ejb.FinderException, java.rmi.RemoteException;

}