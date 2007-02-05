/*
 * $Id: UserSessionBean.java,v 1.2.2.1 2007/02/05 22:25:31 laddi Exp $
 * Created on 24.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.idega.business.IBOSessionBean;
import com.idega.user.data.User;


/**
 * Last modified: $Date: 2007/02/05 22:25:31 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2.2.1 $
 */
public class UserSessionBean extends IBOSessionBean implements UserSession, HttpSessionBindingListener {

	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		iUser = null;
	}

	private User iUser;
	
	public User getUser() {
		return this.iUser;
	}
	
	public void setUser(User user) {
		this.iUser = user;
	}
}