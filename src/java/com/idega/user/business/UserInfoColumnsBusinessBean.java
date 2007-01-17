/*
 * $Id: UserInfoColumnsBusinessBean.java,v 1.2.2.2 2007/01/17 11:33:57 idegaweb Exp $
 * Created on Nov 14, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import com.idega.business.IBOServiceBean;
import com.idega.user.data.UserInfoColumns;
import com.idega.user.data.UserInfoColumnsHome;


public class UserInfoColumnsBusinessBean extends IBOServiceBean implements UserInfoColumnsBusiness {
	
	public UserInfoColumnsHome getUserInfoColumnsHome() throws RemoteException{
		return (UserInfoColumnsHome) getIDOHome(UserInfoColumns.class);
	}
	
	public boolean setUserInfo(int userId, int groupId, String userInfo1, String userInfo2, String userInfo3) {
		try {
			UserInfoColumns userInfoColumns = null;
			Collection obj = getUserInfoColumnsHome().findAllByUserIdAndGroupId(userId,groupId);
			Iterator infoIter = obj.iterator();
			if (infoIter.hasNext()) {
				userInfoColumns = (UserInfoColumns)infoIter.next();
			}
			if (userInfoColumns == null) {
				userInfoColumns = getUserInfoColumnsHome().create();
			}
			userInfoColumns.setUserId(userId);
			userInfoColumns.setGroupId(groupId);
			userInfoColumns.setUserInfo1(userInfo1);
			userInfoColumns.setUserInfo2(userInfo2);
			userInfoColumns.setUserInfo3(userInfo3);
			userInfoColumns.store();
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return false;
		}
		return true;
	}
	
	public UserInfoColumns getUserInfo(int userId, int groupId) {
		UserInfoColumns userInfoColumns = null;
		try {
			Collection obj = getUserInfoColumnsHome().findAllByUserIdAndGroupId(userId,groupId);
			Iterator infoIter = obj.iterator();
			if (infoIter.hasNext()) {
				userInfoColumns = (UserInfoColumns)infoIter.next();
			}
		}
		catch(Exception e) {
			e.printStackTrace();	
		}
		return userInfoColumns;
	}

	public String getUserInfo1(int userId, int groupId) {
		String infoString = null;
		UserInfoColumns columns = getUserInfo(userId, groupId);
		if (columns != null) {
			infoString = columns.getUserInfo1();
		}
		return infoString;
	}

	public String getUserInfo2(int userId, int groupId) {
		String infoString = null;
		UserInfoColumns columns = getUserInfo(userId, groupId);
		if (columns != null) {
			infoString = columns.getUserInfo2();
		}
		return infoString;
	}

	public String getUserInfo3(int userId, int groupId) {
		String infoString = null;
		UserInfoColumns columns = getUserInfo(userId, groupId);
		if (columns != null) {
			infoString = columns.getUserInfo3();
		}
		return infoString;
	}
}