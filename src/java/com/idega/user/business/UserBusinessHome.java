package com.idega.user.business;


public interface UserBusinessHome extends com.idega.business.IBOHome
{
 public UserBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}