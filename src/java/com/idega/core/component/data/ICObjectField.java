package com.idega.core.component.data;


public interface ICObjectField extends com.idega.data.IDOEntity
{
 public java.lang.String getSQLFieldName() throws java.rmi.RemoteException;
 public void setFieldName(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getFieldName() throws java.rmi.RemoteException;
}
