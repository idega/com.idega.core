package com.idega.core.data;


public interface ICCategoryTranslationHome extends com.idega.data.IDOHome
{
 public ICCategoryTranslation create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public ICCategoryTranslation findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findAllByCategory(int p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public ICCategoryTranslation findByCategoryAndLocale(int p0,int p1)throws javax.ejb.FinderException, java.rmi.RemoteException;

}