/*
 * $Id: ICApplicationBindingBusiness.java,v 1.4 2005/12/15 17:07:03 thomas Exp $
 * Created on Dec 15, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import java.io.IOException;
import java.util.Set;
import com.idega.business.IBOService;


/**
 * 
 *  Last modified: $Date: 2005/12/15 17:07:03 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
 */
public interface ICApplicationBindingBusiness extends IBOService {

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#initializeBean
	 */
	public void initializeBean() ;

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#get
	 */
	public String get(String key) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#put
	 */
	public String put(String key, String value) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#gremove
	 */
	public String remove(String key) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#keySet
	 */
	public Set keySet() throws IOException, java.rmi.RemoteException;
}
