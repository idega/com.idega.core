/*
 * $Id: LoginTableHomeImpl.java,v 1.6 2006/03/29 13:10:16 laddi Exp $
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
import com.idega.data.IDOFactory;
import com.idega.user.data.User;


/**
 * <p>
 * TODO laddi Describe Type LoginTableHomeImpl
 * </p>
 *  Last modified: $Date: 2006/03/29 13:10:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.6 $
 */
public class LoginTableHomeImpl extends IDOFactory implements LoginTableHome {

	protected Class getEntityInterfaceClass() {
		return LoginTable.class;
	}

	public LoginTable create() throws javax.ejb.CreateException {
		return (LoginTable) super.createIDO();
	}

	public LoginTable findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (LoginTable) super.findByPrimaryKeyIDO(pk);
	}

	public LoginTable findLoginForUser(User user) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindLoginForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection<LoginTable> findLoginsForUser(User user) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((LoginTableBMPBean) entity).ejbFindLoginsForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public LoginTable findLoginForUser(int userID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindLoginForUser(userID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findLoginsForUser(int userID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((LoginTableBMPBean) entity).ejbFindLoginsForUser(userID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public int getNumberOfLogins(String userName) throws IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((LoginTableBMPBean) entity).ejbHomeGetNumberOfLogins(userName);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public LoginTable findByLogin(String login) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindByLogin(login);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public LoginTable findByUserAndLogin(User user, String login) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindByUserAndLogin(user, login);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	public LoginTable findByUserAndType(User user, String loginType) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindByUserAndType(user, loginType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	public LoginTable findDefaultLoginForUser(int userID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindDefaultLoginForUser(userID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	public LoginTable findDefaultLoginForUser(User user) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindDefaultLoginForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}
