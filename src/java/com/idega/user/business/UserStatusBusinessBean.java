/*
 * $Id$
 *
 * Copyright (C) 2000-2003 Idega Software. All Rights Reserved.
 *
 * This software is the proprietary information of Idega Software.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOServiceBean;

/**
 * @author palli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UserStatusBusinessBean extends IBOServiceBean implements UserStatusBusiness {
	public boolean setUserGroupStatus(int user_id, int group_id, int status_id) {
		
		return true;
	}
}
