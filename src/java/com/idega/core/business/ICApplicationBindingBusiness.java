/*
 * $Id: ICApplicationBindingBusiness.java,v 1.6 2009/05/21 12:47:47 laddi Exp $
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
 * Do not use this class directly, use IWMainApplicationSettings.
 * 
 *  Last modified: $Date: 2009/05/21 12:47:47 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.6 $
 */
public interface ICApplicationBindingBusiness extends IBOService {

	/**
	 * 
	 * Do not use this method, use IWMainApplicationSettings.
	 * 
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#get
	 */
	public String get(String key) throws IOException, java.rmi.RemoteException;

	/**
	 * 
	 * Do not use this method, use IWMainApplicationSettings.
	 * 
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#put
	 */
	public String put(String key, String value) throws IOException, java.rmi.RemoteException;

	/**
	 * 
	 * Do not use this method, use IWMainApplicationSettings.
	 * 
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#put
	 */
	public String put(String key, String value, String type) throws IOException, java.rmi.RemoteException;

	/**
	 * 
	 * Do not use this method, use IWMainApplicationSettings.
	 * 
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#gremove
	 */
	public String remove(String key) throws IOException, java.rmi.RemoteException;

	/**
	 * 
	 * Do not use this method, use IWMainApplicationSettings.
	 * 
	 * @see com.idega.core.business.ICApplicationBindingBusinessBean#keySet
	 */
	public Set keySet() throws IOException, java.rmi.RemoteException;
}
