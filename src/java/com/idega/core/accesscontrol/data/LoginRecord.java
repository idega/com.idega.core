/*
 * $Id: LoginRecord.java,v 1.9 2006/01/15 21:15:49 laddi Exp $
 * Created on Jan 15, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import java.sql.Timestamp;
import com.idega.data.IDOEntity;
import com.idega.user.data.User;


/**
 * <p>
 * TODO laddi Describe Type LoginRecord
 * </p>
 *  Last modified: $Date: 2006/01/15 21:15:49 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.9 $
 */
public interface LoginRecord extends IDOEntity {

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setLoginId
	 */
	public void setLoginId(int Id);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#setLogin
	 */
	public void setLogin(LoginTable login);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getLoginId
	 */
	public int getLoginId();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#getLogin
	 */
	public LoginTable getLogin();

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
