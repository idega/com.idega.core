package com.idega.user.data;

import javax.ejb.*;

public interface GroupType extends com.idega.data.IDOEntity,com.idega.data.TreeableEntity
{
 public java.lang.String getIDColumnName();
 public boolean getVisibility() throws java.rmi.RemoteException;
 public java.lang.Class getPrimaryKeyClass() throws java.rmi.RemoteException;
 public void setGroupTypeAsPermissionGroup() throws java.rmi.RemoteException;
 public void initializeAttributes();
 public void setGroupTypeAsGeneralGroup() throws java.rmi.RemoteException;
 public void setGroupTypeAsAliasGroup() throws java.rmi.RemoteException;
 public void setDescription(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getDescription() throws java.rmi.RemoteException;
 public void setType(java.lang.String p0) throws java.rmi.RemoteException;
 public void setVisibility(boolean p0) throws java.rmi.RemoteException;
 public java.lang.String getGeneralGroupTypeString() throws java.rmi.RemoteException;
 public java.lang.String getType();
 public java.lang.String getPermissionGroupTypeString() throws java.rmi.RemoteException;
 public java.lang.String getAliasGroupTypeString() throws java.rmi.RemoteException;
 public void setDefaultGroupName(String name);
 public String getDefaultGroupName();
}
