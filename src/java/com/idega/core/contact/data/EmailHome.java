/*
 * $Id: EmailHome.java,v 1.3 2006/06/01 15:20:02 thomas Exp $
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
import java.util.Collections;

import javax.ejb.FinderException;

import com.idega.data.IDOHome;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2006/06/01 15:20:02 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public interface EmailHome extends IDOHome {

	 public Email create() throws javax.ejb.CreateException;
	 public Email createLegacy();
	 public Email findByPrimaryKey(Object pk);
	 public Email findByPrimaryKey(int id) throws javax.ejb.FinderException;
	 public Email findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
	 public Collection<Email> findEmailsForUser(com.idega.user.data.User p0)throws java.rmi.RemoteException,javax.ejb.FinderException;
	 public Collection<Email> findEmailsForUser(int p0)throws javax.ejb.FinderException;

	/**
	 * 
	 * Just a shortcut for the main email type
	 * 
	 * @see com.idega.core.contact.data.EmailBMPBean#ejbFindMainEmailForUser
	 */
	public Email findMainEmailForUser(com.idega.user.data.User user) throws FinderException, RemoteException;
	
	/**
	 * @see com.idega.core.contact.data.EmailBMPBean#ejbFindMainEmailForUser
	 */
	public Email findEmailForUser(com.idega.user.data.User user, EmailType emailType) throws FinderException, RemoteException;

	/**
	 * 
	 * Just a shortcut for the main email type
	 * 
	 * @see com.idega.core.contact.data.EmailBMPBean#ejbFindMainEmailForUser
	 */
	public Email findMainEmailForGroup(Group group) throws FinderException, RemoteException;
	
	/**
	 * @see com.idega.core.contact.data.EmailBMPBean#ejbFindMainEmailForUser
	 */
	public Email findEmailForGroup(Group group, EmailType emailType) throws FinderException, RemoteException;
	
	public Collection<Email> findMainEmailsForUsers(Collection<User> users) throws FinderException;

	/**
	 * 
	 * <p>Creates/updates {@link Email} in data source.</p>
	 * @param primaryKey is {@link Email#getPrimaryKey()}, 
	 * if not <code>null</code>, will be updated;
	 * @param emailAddress is {@link Email#getEmailAddress()}, not <code>null</code>;
	 * @return updated/created {@link Email} or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Email update(String primaryKey, String emailAddress);

	/**
	 * 
	 * @param emailAddress is {@link Collection} of {@link Email#getEmailAddress()}, 
	 * not <code>null</code>;
	 * @return {@link Email}s, by criteria or {@link Collections#emptyList()}; 
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<Email> findByEmailAddress(Collection<String> emailAddress);
	
	/**
	 * 
	 * @param emailAddress is {@link Email#getEmailAddress()}, not <code>null</code>;
	 * @return {@link Collection} of {@link Email}s similar to given one, or 
	 * {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<Email> findByEmailAddressPart(String emailAddress);
}
