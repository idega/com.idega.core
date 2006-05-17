/*
 * $Id: EmailHomeImpl.java,v 1.2 2006/05/17 16:40:53 thomas Exp $
 * Created on May 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.data;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2006/05/17 16:40:53 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class EmailHomeImpl extends IDOFactory implements EmailHome {

	protected Class getEntityInterfaceClass() {
		return Email.class;
	}

	public Email create() throws javax.ejb.CreateException {
		return (Email) super.createIDO();
	}

	public Email findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Email) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findEmailsForUser(com.idega.user.data.User user) throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((EmailBMPBean) entity).ejbFindEmailsForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findEmailsForUser(int iUserId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((EmailBMPBean) entity).ejbFindEmailsForUser(iUserId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Email findEmailForUser(com.idega.user.data.User user, EmailType emailType) throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((EmailBMPBean) entity).ejbFindEmailForUser(user, emailType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}	
	
	/**
	 * Just a shortcut for the main email type
	 */
	public Email findMainEmailForUser(com.idega.user.data.User user) throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((EmailBMPBean) entity).ejbFindMainEmailForUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Email findEmailForGroup(Group group, EmailType emailType) throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((EmailBMPBean) entity).ejbFindEmailForGroup(group, emailType);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}	
	
	/**
	 * Just a shortcut for the main email type
	 */
	public Email findMainEmailForGroup(Group group) throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((EmailBMPBean) entity).ejbFindMainEmailForGroup(group);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	
	
	public Email findEmailByAddress(String address) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((EmailBMPBean) entity).ejbFindEmailByAddress(address);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	 public Email findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
			try{
				return findByPrimaryKey(id);
			}
			catch(javax.ejb.FinderException fe){
				throw new java.sql.SQLException(fe.getMessage());
			}

		 }
	 
	 public Email findByPrimaryKey(int id) throws javax.ejb.FinderException{
		  return (Email) super.findByPrimaryKeyIDO(id);
		 }
	 
	 
	 public Email createLegacy(){
			try{
				return create();
			}
			catch(javax.ejb.CreateException ce){
				throw new RuntimeException("CreateException:"+ce.getMessage());
			}
	 }

}
