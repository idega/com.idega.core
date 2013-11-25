/*
 * $Id: EmailHomeImpl.java,v 1.3 2006/06/01 15:20:02 thomas Exp $
 * Created on May 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.data;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.data.IDOStoreException;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;


/**
 * 
 *  Last modified: $Date: 2006/06/01 15:20:02 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public class EmailHomeImpl extends IDOFactory implements EmailHome {

	private static final long serialVersionUID = -3828302165942058939L;

	@Override
	protected Class<Email> getEntityInterfaceClass() {
		return Email.class;
	}

	public Email create() throws javax.ejb.CreateException {
		return (Email) super.createIDO();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.contact.data.EmailHome#findByPrimaryKey(java.lang.Object)
	 */
	@Override
	public Email findByPrimaryKey(Object pk) {
		try {
			return (Email) super.findByPrimaryKeyIDO(pk);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to find email by primary key: " + pk);
		}

		return null;
	}

	public Collection<Email> findEmailsForUser(com.idega.user.data.User user) throws FinderException, RemoteException {
		EmailBMPBean entity = (EmailBMPBean) idoCheckOutPooledEntity();
		java.util.Collection<Object> ids = entity.ejbFindEmailsForUser(user);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection<Email> findEmailsForUser(int iUserId) throws FinderException {
		EmailBMPBean entity = (EmailBMPBean) idoCheckOutPooledEntity();
		java.util.Collection<Object> ids = entity.ejbFindEmailsForUser(iUserId);
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

	public Collection<Email> findMainEmailsForUsers(Collection<User> users) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Integer> ids = ((EmailBMPBean) entity).ejbFindMainEmailsForUsers(users);
		return this.findByPrimaryKeyCollection(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.contact.data.EmailHome#findByEmailAddress(java.util.Collection)
	 */
	@Override
	public Collection<Email> findByEmailAddress(Collection<String> emailAddress) {
		EmailBMPBean entity = (EmailBMPBean) idoCheckOutPooledEntity();
		Collection<Object> ids = entity.ejbFindByEmailAddress(emailAddress);
		try {
			return getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to get " + this.getClass().getName() + 
					"'s by id's: " + ids);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.contact.data.EmailHome#findByEmailAddressPart(java.lang.String)
	 */
	@Override
	public Collection<Email> findByEmailAddressPart(String emailAddress) {
		if (StringUtil.isEmpty(emailAddress)) {
			return Collections.emptyList();
		}

		EmailBMPBean entity = (EmailBMPBean) idoCheckOutPooledEntity();
		Collection<Object> ids = entity.ejbFindByEmailAddressPart(emailAddress);
		try {
			return getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to get " + this.getClass().getName() + 
					"'s by id's: " + ids);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.contact.data.EmailHome#update(java.lang.String, java.lang.String)
	 */
	@Override
	public Email update(String primaryKey, String emailAddress) {
		Email email = null;
		if (!StringUtil.isEmpty(primaryKey)) {
			email = findByPrimaryKey(primaryKey);
		}

		if (!StringUtil.isEmpty(emailAddress)) {
			if (email == null) {
				Collection<Email> emails = findByEmailAddress(Arrays.asList(emailAddress));
				if (!ListUtil.isEmpty(emails)) {
					email = emails.iterator().next();
				} else {
					try {
						email = createEntity();
					} catch (CreateException e) {
						java.util.logging.Logger.getLogger(getClass().getName()).log(
								Level.WARNING, 
								"Failed to create " + getEntityBeanClass().getName() + 
								" cause of: ", e);
						return null;
					}
				}
			}

			email.setEmailAddress(emailAddress);
		}

		try {
			email.store();
			return email;
		} catch (IDOStoreException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to store " + getEntityBeanClass().getName() + 
					" cause of: ", e);
		}

		return null;
	}
}
