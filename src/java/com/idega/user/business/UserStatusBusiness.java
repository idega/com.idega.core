package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Date;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.user.data.Status;
import com.idega.user.data.StatusHome;
import com.idega.user.data.UserStatus;
import com.idega.user.data.UserStatusHome;


public interface UserStatusBusiness extends com.idega.business.IBOService
{
 public int getUserGroupStatus(int p0,int p1) throws java.rmi.RemoteException;
 public boolean removeUserFromGroup(int p0,int p1) throws java.rmi.RemoteException;
 public boolean setUserGroupStatus(int p0,int p1,int p2) throws java.rmi.RemoteException;
 public UserStatusHome getUserStatusHome() throws RemoteException;
 public String getDeceasedStatusKey();
 public StatusHome getStatusHome() throws RemoteException;
 public Status getDeceasedStatus() throws RemoteException,FinderException;
 public Status createDeceasedStatus() throws RemoteException,CreateException;
 public Status getDeceasedStatusCreateIfNone() throws RemoteException,FinderException,CreateException;
 public UserStatus getDeceasedUserStatus(Integer userID) throws RemoteException;
 public void setUserAsDeceased(Integer userID,Date deceasedDate) throws RemoteException;
 
 
}
