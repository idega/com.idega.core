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
import javax.ejb.FinderException;
import com.idega.data.IDOHome;
import com.idega.user.data.Group;


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
	 public Email findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
	 public Email findByPrimaryKey(int id) throws javax.ejb.FinderException;
	 public Email findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
	 public java.util.Collection findEmailsForUser(com.idega.user.data.User p0)throws java.rmi.RemoteException,javax.ejb.FinderException;
	 public java.util.Collection findEmailsForUser(int p0)throws javax.ejb.FinderException;

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


}
