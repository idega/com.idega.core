package com.idega.user.data;


public interface UserGroupPlugIn extends com.idega.data.IDOEntity
{
 public com.idega.core.component.data.ICObject getPresentationICObject() throws java.rmi.RemoteException;
 public java.lang.String getDefaultDisplayName() throws java.rmi.RemoteException;
 public java.lang.String getLocalizedDisplayName(java.util.Locale p0) throws java.rmi.RemoteException;
 public void initializeAttributes() throws java.rmi.RemoteException;
 public void setDescription(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getDescription() throws java.rmi.RemoteException;
 public com.idega.core.component.data.ICObject getBusinessICObject() throws java.rmi.RemoteException;
}
