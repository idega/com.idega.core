package com.idega.user.data;


public interface GroupRelationTypeHome extends com.idega.data.IDOHome
{
 public GroupRelationType create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupRelationType findByPrimaryKey(int id) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public GroupRelationType findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;

}