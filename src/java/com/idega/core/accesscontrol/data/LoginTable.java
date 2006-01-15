/*
 * $Id: LoginTable.java,v 1.24 2006/01/15 17:29:35 laddi Exp $
 * Created on Jan 15, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import java.sql.Timestamp;
import com.idega.core.user.data.User;
import com.idega.data.IDOEntity;
import com.idega.util.EncryptionType;


/**
 * <p>
 * TODO laddi Describe Type LoginTable
 * </p>
 *  Last modified: $Date: 2006/01/15 17:29:35 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.24 $
 */
public interface LoginTable extends IDOEntity, EncryptionType {

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setUserPasswordInClearText
	 */
	public void setUserPasswordInClearText(String password);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getUserPasswordInClearText
	 */
	public String getUserPasswordInClearText();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getUserPassword
	 */
	public String getUserPassword();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setUserPassword
	 */
	public void setUserPassword(String userPassword);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setUserLogin
	 */
	public void setUserLogin(String userLogin);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getUserLogin
	 */
	public String getUserLogin();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getUserId
	 */
	public int getUserId();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setUserId
	 */
	public void setUserId(Integer userId);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setUserId
	 */
	public void setUserId(int userId);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setLastChanged
	 */
	public void setLastChanged(Timestamp when);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getLastChanged
	 */
	public Timestamp getLastChanged();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setUserPassword
	 */
	public void setUserPassword(String encryptedPassword, String unEncryptedPassword);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getUnencryptedUserPassword
	 */
	public String getUnencryptedUserPassword() throws PasswordNotKnown;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#setLoginType
	 */
	public void setLoginType(String loginType);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#getLoginType
	 */
	public String getLoginType();
}
