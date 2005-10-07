/*
 * $Id: ICApplicationBindingBusinessBean.java,v 1.1 2005/10/07 17:57:44 thomas Exp $
 * Created on Oct 7, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.business.IBOServiceBean;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingBMPBean;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.util.StringHandler;


public class ICApplicationBindingBusinessBean extends IBOServiceBean  implements ICApplicationBindingBusiness{

	private static final int MAX_KEY_LENGTH = ICApplicationBindingBMPBean.MAX_KEY_LENGTH; 
	
	private ICApplicationBindingHome applicationBindingHome = null;
	
	public boolean contains(String key) throws IDOLookupException {
		key = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		try {
			getICApplicationBinding(key);
		}
		catch (FinderException ex) {
			return false;
		}
		return true;
	}
	
	public String get(String key) throws IDOLookupException {
		key = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		try {
			return getICApplicationBinding(key).getValue();
		}
		catch (FinderException ex) {
			return null;
		}
	}
	
	public String put(String key, String value) throws CreateException, IDOLookupException { 
		key = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		ICApplicationBinding applicationBinding = null;
		String oldValue = null;
		try {
			applicationBinding = getICApplicationBinding(key);
			oldValue = applicationBinding.getValue();
		} 
		catch (FinderException finderException) {
			// create an entry
			StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
			applicationBinding = getICApplicationBindingHome().create();
			applicationBinding.setKey(key);
		}
		// set the value
		applicationBinding.setValue(value);
		applicationBinding.store();
		return oldValue;
		
	}
	
	private ICApplicationBindingHome getICApplicationBindingHome() throws IDOLookupException {
		if (applicationBindingHome == null) {
			applicationBindingHome = (ICApplicationBindingHome) IDOLookup.getHome(ICApplicationBinding.class);
		}
		return applicationBindingHome;
	}
	
	private ICApplicationBinding getICApplicationBinding(String key) throws IDOLookupException, FinderException {
		return getICApplicationBindingHome().findByPrimaryKey(key);
	}
		

}
