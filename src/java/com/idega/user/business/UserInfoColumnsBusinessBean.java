/*
 * $Id: UserInfoColumnsBusinessBean.java,v 1.1.2.1 2006/11/14 15:06:24 idegaweb Exp $
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
	
	public boolean setUserInfo(int userId, int groupId, String userInfo) {
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
			userInfoColumns.setUserInfo(userInfo);
			userInfoColumns.store();
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return false;
		}
		return true;
	}
	
	public String getUserInfo(int userId, int groupId) {
		UserInfoColumns userInfo = null;
		try {
			Collection obj = getUserInfoColumnsHome().findAllByUserIdAndGroupId(userId,groupId);
			Iterator infoIter = obj.iterator();
			if (infoIter.hasNext()) {
				userInfo = (UserInfoColumns)infoIter.next();
			}
		}
		catch(Exception e) {
			e.printStackTrace();	
		}
		return userInfo.getUserInfo();
	}
}