/*
 * $Id: UserSessionBean.java,v 1.2 2006/04/09 12:13:14 laddi Exp $
 * Created on 24.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import com.idega.business.IBOSessionBean;
import com.idega.user.data.User;


/**
 * Last modified: $Date: 2006/04/09 12:13:14 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class UserSessionBean extends IBOSessionBean  implements UserSession{

	private User iUser;
	
	public User getUser() {
		return this.iUser;
	}
	
	public void setUser(User user) {
		this.iUser = user;
	}
}