package com.idega.user.data;


public interface GenderHome extends com.idega.data.IDOHome
{
 public Gender create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public Gender findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public Gender findByGenderName(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllGenders()throws javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.user.data.Gender getFemaleGender()throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.user.data.Gender getMaleGender()throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;

}