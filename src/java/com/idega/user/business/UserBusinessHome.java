package com.idega.user.business;


import javax.ejb.CreateException;
import java.rmi.RemoteException;
import com.idega.business.IBOHome;

public interface UserBusinessHome extends IBOHome {
	public UserBusiness create() throws CreateException, RemoteException;
}