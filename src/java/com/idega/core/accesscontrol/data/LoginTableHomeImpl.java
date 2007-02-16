package com.idega.core.accesscontrol.data;


import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOFactory;
import com.idega.user.data.User;

public class LoginTableHomeImpl extends IDOFactory implements LoginTableHome {
	public Class getEntityInterfaceClass() {
		return LoginTable.class;
	}

	public LoginTable create() throws CreateException {
		return (LoginTable) super.createIDO();
	}

	public LoginTable findByPrimaryKey(Object pk) throws FinderException {
		return (LoginTable) super.findByPrimaryKeyIDO(pk);
	}

	public LoginTable findLoginForUser(User user) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindLoginForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findLoginsForUser(User user) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((LoginTableBMPBean) entity).ejbFindLoginsForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public LoginTable findLoginForUser(int userID) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindLoginForUser(userID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findLoginsForUser(int userID) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((LoginTableBMPBean) entity).ejbFindLoginsForUser(userID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public int getNumberOfLogins(String userName) throws IDOException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((LoginTableBMPBean) entity).ejbHomeGetNumberOfLogins(userName);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public LoginTable findByLogin(String login) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindByLogin(login);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public LoginTable findByUserAndLogin(User user, String login) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindByUserAndLogin(user, login);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public LoginTable findByUserAndType(User user, String loginType) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindByUserAndType(user, loginType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public LoginTable findDefaultLoginForUser(int userID) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindDefaultLoginForUser(userID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public LoginTable findDefaultLoginForUser(User user) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginTableBMPBean) entity).ejbFindDefaultLoginForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}