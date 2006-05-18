/*
 * $Id: EmailTypeHome.java,v 1.3 2006/05/18 11:43:43 laddi Exp $
 * Created on May 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.data;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOException;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookupException;


/**
 * 
 *  Last modified: $Date: 2006/05/18 11:43:43 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public interface EmailTypeHome extends IDOHome {

	public EmailType create() throws javax.ejb.CreateException;

	public EmailType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
	
	public EmailType findMainEmailType() throws FinderException, RemoteException;
	
	public EmailType findEmailTypeByUniqueName(String uniqueName) throws FinderException, RemoteException;

	/**
	 * @see com.idega.core.contact.data.EmailTypeBMPBean#ejbHomeUpdateStartData
	 */
	public boolean updateStartData() throws IDOException, IDOLookupException, CreateException;
}
