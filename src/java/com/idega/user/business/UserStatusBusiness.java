package com.idega.user.business;


public interface UserStatusBusiness extends com.idega.business.IBOService
{
 public boolean removeUserFromGroup(int p0,int p1) throws java.rmi.RemoteException;
 public boolean setUserGroupStatus(int p0,int p1,int p2) throws java.rmi.RemoteException;
}
