package com.idega.user.business;


import com.idega.user.data.UserInfoColumns;
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
	public boolean setUserInfo(int userId, int groupId, String userInfo1, String userInfo2, String userInfo3) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserInfoColumnsBusinessBean#getUserInfo
	 */
	public UserInfoColumns getUserInfo(int userId, int groupId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserInfoColumnsBusinessBean#getUserInfo1
	 */
	public String getUserInfo1(int userId, int groupId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserInfoColumnsBusinessBean#getUserInfo2
	 */
	public String getUserInfo2(int userId, int groupId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserInfoColumnsBusinessBean#getUserInfo3
	 */
	public String getUserInfo3(int userId, int groupId) throws RemoteException;
}