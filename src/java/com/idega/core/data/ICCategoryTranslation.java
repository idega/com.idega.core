package com.idega.core.data;

import com.idega.data.CategoryEntity;

public interface ICCategoryTranslation extends CategoryEntity
{
 public java.lang.String getDescription() throws java.rmi.RemoteException;
 public int getLocaleId() throws java.rmi.RemoteException;
 public java.lang.String getName();
 public void initializeAttributes() ;
 public void setDescription(java.lang.String p0) throws java.rmi.RemoteException;
 public void setLocaleID(int p0) throws java.rmi.RemoteException;
 public void setName(java.lang.String p0);
}
