package com.idega.user.data;


public interface StatusHome extends com.idega.data.IDOHome
{
 public Status create() throws javax.ejb.CreateException;
 public Status findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;
 public Status findByStatusKey(String statusKey)throws javax.ejb.FinderException;

}