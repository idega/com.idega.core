/*
 * $Id: ICApplicationBindingBusiness.java,v 1.1 2005/10/07 17:57:44 thomas Exp $
 * Created on Oct 7, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import javax.ejb.CreateException;
import com.idega.business.IBOService;
import com.idega.data.IDOLookupException;


/**
 * 
 *  Last modified: $Date: 2005/10/07 17:57:44 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public interface ICApplicationBindingBusiness extends IBOService {

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#contains
	 */
	public boolean contains(String key) throws IDOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#get
	 */
	public String get(String key) throws IDOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#put
	 */
	public String put(String key, String value) throws CreateException, IDOLookupException, java.rmi.RemoteException;
}
