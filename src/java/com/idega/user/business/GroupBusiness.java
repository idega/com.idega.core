package com.idega.user.business;

import javax.ejb.*;

public interface GroupBusiness extends com.idega.business.IBOService
{
 public com.idega.user.data.GroupHome getGroupHome() throws java.rmi.RemoteException;
 public java.lang.String getGroupType(java.lang.Class p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.Group createGroup(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
}
