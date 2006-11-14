package com.idega.user.business;


import com.idega.user.data.UserInfoColumnsHome;
import com.idega.business.IBOService;
import java.rmi.RemoteException;

public interface UserInfoColumnsBusiness extends IBOService {

	/**
	 * @see com.idega.user.business.UserInfoColumnsBusinessBean#getUserInfoColumnsHome
	 */
	public UserInfoColumnsHome getUserInfoColumnsHome() throws RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserInfoColumnsBusinessBean#setUserInfo
	 */
	public boolean setUserInfo(int userId, int groupId, String userInfo) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserInfoColumnsBusinessBean#getUserInfo
	 */
	public String getUserInfo(int userId, int groupId) throws RemoteException;
}