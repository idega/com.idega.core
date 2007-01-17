package com.idega.user.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface UserInfoColumnsBusinessHome extends IBOHome {

	public UserInfoColumnsBusiness create() throws CreateException, RemoteException;
}