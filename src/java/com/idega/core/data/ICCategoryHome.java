package com.idega.core.data;


public interface ICCategoryHome extends com.idega.data.IDOHome
{
 public ICCategory create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public ICCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllByObjectInstance(int p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllByObjectInstance(com.idega.core.data.ICObjectInstance p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findRootsByType(java.lang.String p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.List getListOfCategoryForObjectInstance(com.idega.core.data.ICObjectInstance p0,boolean p1)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public int getOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public boolean setOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1,int p2)throws com.idega.data.IDOException, java.rmi.RemoteException;

}