/*
 * $Id$
 *
 * Copyright (C) 2000-2003 Idega Software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega Software.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.util.Collection;
import java.util.Iterator;

import com.idega.business.IBOServiceBean;
import com.idega.user.data.UserStatus;
import com.idega.user.data.UserStatusHome;
import com.idega.util.IWTimestamp;

/**
 * @author palli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserStatusBusinessBean extends IBOServiceBean implements UserStatusBusiness {
	public boolean removeUserFromGroup(int user_id, int group_id) {
		return setUserGroupStatus(user_id,group_id,-1);
	}
	
	public boolean setUserGroupStatus(int user_id, int group_id, int status_id) {
		try {
			Collection obj = ((UserStatusHome) com.idega.data.IDOLookup.getHome(UserStatus.class)).findAllByUserIdAndGroupId(user_id,group_id);
			
			IWTimestamp now = new IWTimestamp();
			if (obj != null) {
				Iterator it = obj.iterator();
				while (it.hasNext()) {
					UserStatus uStatus = (UserStatus)it.next();
					if (uStatus.getDateTo() == null) {
						uStatus.setDateTo(now.getTimestamp());
						uStatus.store();
					}
				}
			}
			
			if (status_id > 0) {
				UserStatus uStatus = ((UserStatusHome) com.idega.data.IDOLookup.getHome(UserStatus.class)).create();
				uStatus.setUserId(user_id);
				uStatus.setGroupId(group_id);
				uStatus.setDateFrom(now.getTimestamp());
				uStatus.setStatusId(Integer.toString(status_id));
				uStatus.store();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return false;
		}
		
		
		return true;
	}
	
	public int getUserGroupStatus(int user_id, int group_id) {
		try {
			Collection obj = ((UserStatusHome) com.idega.data.IDOLookup.getHome(UserStatus.class)).findAllByUserIdAndGroupId(user_id,group_id);
			int ret = -1;

			if (obj != null && obj.size() > 0) {
				UserStatus uStatus = (UserStatus)obj.toArray()[obj.size()-1];
				ret = uStatus.getStatusId();			
			}
			
			return ret;
		}
		catch(Exception e) {
			e.printStackTrace();
			
			return -1;
		}
	}	
}