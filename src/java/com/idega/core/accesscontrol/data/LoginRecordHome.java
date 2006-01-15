/*
 * $Id: LoginRecordHome.java,v 1.7 2006/01/15 21:15:49 laddi Exp $
 * Created on Jan 15, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOException;
import com.idega.data.IDOHome;
import com.idega.user.data.User;


/**
 * <p>
 * TODO laddi Describe Type LoginRecordHome
 * </p>
 *  Last modified: $Date: 2006/01/15 21:15:49 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.7 $
 */
public interface LoginRecordHome extends IDOHome {

	public LoginRecord create() throws javax.ejb.CreateException;

	public LoginRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginRecordBMPBean#ejbFindAllLoginRecords
	 */
	public Collection findAllLoginRecords(int loginID) throws FinderException;

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
}
