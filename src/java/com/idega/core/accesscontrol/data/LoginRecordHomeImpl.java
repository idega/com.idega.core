/*
 * $Id: LoginRecordHomeImpl.java,v 1.5 2004/10/22 13:38:28 laddi Exp $
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
import com.idega.data.IDOFactory;


/**
 * Last modified: 22.10.2004 15:08:00 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public class LoginRecordHomeImpl extends IDOFactory implements LoginRecordHome {

	protected Class getEntityInterfaceClass() {
		return LoginRecord.class;
	}

	public LoginRecord create() throws javax.ejb.CreateException {
		return (LoginRecord) super.createIDO();
	}

	public LoginRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (LoginRecord) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllLoginRecords(int loginID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((LoginRecordBMPBean) entity).ejbFindAllLoginRecords(loginID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public int getNumberOfLoginsByLoginID(int loginID) throws IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((LoginRecordBMPBean) entity).ejbHomeGetNumberOfLoginsByLoginID(loginID);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public LoginRecord findByLoginID(int loginID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LoginRecordBMPBean) entity).ejbFindByLoginID(loginID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public java.sql.Date getLastLoginByLoginID(Integer loginID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.sql.Date theReturn = ((LoginRecordBMPBean) entity).ejbHomeGetLastLoginByLoginID(loginID);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public java.sql.Date getLastLoginByUserID(Integer userID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.sql.Date theReturn = ((LoginRecordBMPBean) entity).ejbHomeGetLastLoginByUserID(userID);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

}
