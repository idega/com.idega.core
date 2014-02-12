/*
 * $Id: LoginRecordHome.java,v 1.8 2006/02/16 12:48:50 laddi Exp $
 * Created on Feb 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.data.IDOException;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookupException;
import com.idega.user.data.User;


/**
 * <p>
 * TODO laddi Describe Type LoginRecordHome
 * </p>
 *  Last modified: $Date: 2006/02/16 12:48:50 $ by $Author: laddi $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.8 $
 */
public interface LoginRecordHome extends IDOHome {

	public LoginRecord create() throws javax.ejb.CreateException;

	public LoginRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbFindAllLoginRecords
	 */
	public Collection<LoginRecord> findAllLoginRecords(int loginID) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbHomeGetNumberOfLoginsByLoginID
	 */
	public int getNumberOfLoginsByLoginID(int loginID) throws IDOException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbFindByLoginID
	 */
	public LoginRecord findByLoginID(int loginID) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbHomeGetLastLoginByLoginID
	 */
	public java.sql.Date getLastLoginByLoginID(Integer loginID) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbHomeGetLastLoginByUserID
	 */
	public java.sql.Date getLastLoginByUserID(Integer userID) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbFindLastLoginRecord
	 */
	public LoginRecord findLastLoginRecord(User user) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbFindPreviousLoginRecord
	 */
	public LoginRecord findPreviousLoginRecord(LoginRecord record) throws FinderException;

	/**
	 * User
	 * Date: last login date
	 *
	 * @return
	 * @throws FinderException
	 */
	public Map<User, Date> getLastLoginRecordsForAllUsers() throws FinderException, IDOLookupException;
}
