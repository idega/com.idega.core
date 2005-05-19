/*
 * $Id: GenderBusiness.java,v 1.1 2005/05/19 11:15:54 laddi Exp $
 * Created on May 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.business.IBOService;
import com.idega.user.data.Gender;


/**
 * Last modified: $Date: 2005/05/19 11:15:54 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface GenderBusiness extends IBOService {

	/**
	 * @see com.idega.user.business.GenderBusinessBean#getGender
	 */
	public Gender getGender(Integer primaryKey) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GenderBusinessBean#getAllGenders
	 */
	public Collection getAllGenders() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GenderBusinessBean#getMaleGender
	 */
	public Gender getMaleGender() throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GenderBusinessBean#getFemaleGender
	 */
	public Gender getFemaleGender() throws FinderException, java.rmi.RemoteException;
}
