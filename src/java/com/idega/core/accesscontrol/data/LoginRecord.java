/*
 * $Id: LoginRecord.java,v 1.7 2004/10/22 13:38:28 laddi Exp $
 * Created on 22.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import java.sql.Timestamp;


import com.idega.data.IDOEntity;
import com.idega.user.data.User;


/**
 * Last modified: 22.10.2004 15:07:54 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.7 $
 */
public interface LoginRecord extends IDOEntity {

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setLoginId
	 */
	public void setLoginId(int Id);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getLoginId
	 */
	public int getLoginId();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getLogInStamp
	 */
	public Timestamp getLogInStamp();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setLogInStamp
	 */
	public void setLogInStamp(Timestamp stamp);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getLogOutStamp
	 */
	public Timestamp getLogOutStamp();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setLogOutStamp
	 */
	public void setLogOutStamp(Timestamp stamp);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getIPAdress
	 */
	public String getIPAdress();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setIPAdress
	 */
	public void setIPAdress(String ip);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getLoginAsUserID
	 */
	public int getLoginAsUserID();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setLoginAsUserID
	 */
	public void setLoginAsUserID(int userId);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getLoginAsUser
	 */
	public User getLoginAsUser();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setLoginAsUser
	 */
	public void setLoginAsUser(User user);

}
