/*
 * $Id: LoginRecordHome.java,v 1.5 2004/10/22 13:38:28 laddi Exp $
 * Created on 22.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOException;
import com.idega.data.IDOHome;


/**
 * Last modified: 22.10.2004 15:07:55 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
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

}
