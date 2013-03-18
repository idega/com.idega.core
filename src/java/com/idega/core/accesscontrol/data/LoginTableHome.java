/*
 * $Id: LoginTableHome.java,v 1.6 2006/03/29 13:10:16 laddi Exp $
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
 * TODO laddi Describe Type LoginTableHome
 * </p>
 *  Last modified: $Date: 2006/03/29 13:10:16 $ by $Author: laddi $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.6 $
 */
public interface LoginTableHome extends IDOHome {

	public LoginTable create() throws javax.ejb.CreateException;

	public LoginTable findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindLoginForUser
	 */
	public LoginTable findLoginForUser(User user) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindLoginsForUser
	 */
	public Collection<LoginTable> findLoginsForUser(User user) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindLoginForUser
	 */
	public LoginTable findLoginForUser(int userID) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindLoginsForUser
	 */
	public Collection<LoginTable> findLoginsForUser(int userID) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbHomeGetNumberOfLogins
	 */
	public int getNumberOfLogins(String userName) throws IDOException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindByLogin
	 */
	public LoginTable findByLogin(String login) throws FinderException;

	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindByUserAndLogin
	 */
	public LoginTable findByUserAndLogin(User user, String login) throws FinderException;
	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindByUserAndLogin
	 */
	public LoginTable findByUserAndType(User user, String loginType) throws FinderException;
	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindDefaultLoginForUser
	 */
	public LoginTable findDefaultLoginForUser(int userID) throws FinderException;
	/**
	 * @see com.idega.core.accesscontrol.data.LoginTableBMPBean#ejbFindDefaultLoginForUser
	 */
	public LoginTable findDefaultLoginForUser(User user) throws FinderException;

}
