/*
 * $Id: LoginInfo.java,v 1.14 2006/01/15 17:29:35 laddi Exp $
 * Created on Jan 15, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import com.idega.data.IDOEntity;
import com.idega.data.MetaDataCapable;
import com.idega.util.IWTimestamp;


/**
 * <p>
 * TODO laddi Describe Type LoginInfo
 * </p>
 *  Last modified: $Date: 2006/01/15 17:29:35 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.14 $
 */
public interface LoginInfo extends IDOEntity, MetaDataCapable {

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getLoginTableId
	 */
	public int getLoginTableId();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getLoginTable
	 */
	public LoginTable getLoginTable();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getAccountEnabled
	 */
	public boolean getAccountEnabled();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getModified
	 */
	public IWTimestamp getModified();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getDaysOfVality
	 */
	public int getDaysOfVality();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getPasswordExpires
	 */
	public boolean getPasswordExpires();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getPasswNeverExpires
	 */
	public boolean getPasswNeverExpires();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getAllowedToChange
	 */
	public boolean getAllowedToChange();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getChangeNextTime
	 */
	public boolean getChangeNextTime();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getEncryprionType
	 */
	public String getEncryprionType();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getAccessClosed
	 */
	public boolean getAccessClosed();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#getFailedAttemptCount
	 */
	public int getFailedAttemptCount();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setLoginTableId
	 */
	public void setLoginTableId(int id);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setLoginTable
	 */
	public void setLoginTable(LoginTable login);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setAccountEnabled
	 */
	public void setAccountEnabled(boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setAccountEnabled
	 */
	public void setAccountEnabled(Boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setModified
	 */
	public void setModified(IWTimestamp date);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setDaysOfVality
	 */
	public void setDaysOfVality(int days);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setAllowedToChange
	 */
	public void setAllowedToChange(boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setAllowedToChange
	 */
	public void setAllowedToChange(Boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setChangeNextTime
	 */
	public void setChangeNextTime(boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setChangeNextTime
	 */
	public void setChangeNextTime(Boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setPasswordExpires
	 */
	public void setPasswordExpires(boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setPasswordExpires
	 */
	public void setPasswordExpires(Boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setPasswNeverExpires
	 */
	public void setPasswNeverExpires(boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setPasswNeverExpires
	 */
	public void setPasswNeverExpires(Boolean value);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setEncriptionType
	 */
	public void setEncriptionType(String type);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#isLoginExpired
	 */
	public boolean isLoginExpired();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#isLoginValid
	 */
	public boolean isLoginValid();

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setAccessClosed
	 */
	public void setAccessClosed(boolean closed);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setFailedAttemptCount
	 */
	public void setFailedAttemptCount(int attempts);

	/**
	 * @see com.idega.core.accesscontrol.data.LoginInfoBMPBean#setID
	 */
	public void setID(int id);
}
