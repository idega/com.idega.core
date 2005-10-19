/*
 * $Id: ICApplicationBindingBusiness.java,v 1.2 2005/10/19 18:40:15 thomas Exp $
 * Created on Oct 12, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import com.idega.business.IBOService;
import com.idega.data.IDOLookupException;


/**
 * 
 *  Last modified: $Date: 2005/10/19 18:40:15 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public interface ICApplicationBindingBusiness extends IBOService {

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#get
	 */
	public String get(String key) throws IDOLookupException, CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#put
	 */
	public void put(String key, String value) throws CreateException, IDOLookupException, RemoveException,
			java.rmi.RemoteException;
}
