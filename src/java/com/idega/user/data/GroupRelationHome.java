package com.idega.user.data;


public interface GroupRelationHome extends com.idega.data.IDOHome
{
 public GroupRelation create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupRelation findByPrimaryKey(int id) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public GroupRelation findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;

}