package com.idega.core.category.data;


public interface ICCategoryICObjectInstanceHome extends com.idega.data.IDOHome
{
 public ICCategoryICObjectInstance create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public ICCategoryICObjectInstance findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public int getOrderNumber(com.idega.core.category.data.Category p0,com.idega.core.component.data.ICObjectInstance p1)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.List getListOfCategoryForObjectInstance(com.idega.core.component.data.ICObjectInstance p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.lang.String getRelatedSQL(int p0) throws java.rmi.RemoteException;
 public java.lang.String getRelatedSQL(int p0, String p1) throws java.rmi.RemoteException;
 public boolean setOrderNumber(com.idega.core.category.data.Category p0,com.idega.core.component.data.ICObjectInstance p1,int p2)throws com.idega.data.IDOException, java.rmi.RemoteException;

}