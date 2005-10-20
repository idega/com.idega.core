/*
 * $Id: ICApplicationBindingBusinessBean.java,v 1.3 2005/10/20 14:47:01 thomas Exp $
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
import javax.ejb.RemoveException;
import com.idega.business.IBOServiceBean;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingBMPBean;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.StringHandler;


public class ICApplicationBindingBusinessBean extends IBOServiceBean   implements ICApplicationBindingBusiness{

	private static final int MAX_KEY_LENGTH = ICApplicationBindingBMPBean.MAX_KEY_LENGTH; 
	
	private ICApplicationBindingHome applicationBindingHome = null;
	
	/**
	 * Returns the corresponding value to the specified key elso null if the key is not found.
	 * 
	 * Checks first if there is an entry in the application binding table. 
	 * If not the application settings are looked up: 
	 * If a value is found a corresponding application binding is created.
	 */
	public String get(String key) throws IDOLookupException, CreateException {
		String shortKey = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		try {
			// fetch the value
			return getICApplicationBinding(shortKey).getValue();
		}
		catch (FinderException ex) {
			// failed?
			// fetch the value from the applications settings, create a corresponding application binding
			return getLegacyProperty(key, shortKey);
		}
	}
	
	private String getLegacyProperty(String key, String shortKey) throws IDOLookupException, CreateException {
		String oldValue = getLegacyPropertyFromApplicationSettings(key);
		// create new entry (does not create a new entry if the value is null)
		createApplicationBindingCheckValue(shortKey, oldValue);
		return oldValue;
	}
	
	/**
	 * Puts an entry into the application binding table.
	 * If the value is null an existing entry is removed.
	 * 
	 * @return the old value or null if there was no entry
	 * 
	 */
	public String put(String key, String value) throws CreateException, IDOLookupException, RemoveException {
		key = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		ICApplicationBinding applicationBinding = null;
		String oldValue = null;
		// find an existing entry
		try {
			applicationBinding = getICApplicationBinding(key);
			oldValue = applicationBinding.getValue();
		} 
		catch (FinderException finderException) {
			// not found?
			// create a new entry (does not create an entry if the value is null)
			createApplicationBindingCheckValue(key, value);
			return null;
		}
		// set the value of the existing entry
		if (value == null) {
			// remove the entry if the value is set to null
			applicationBinding.remove();
			return oldValue;
		}
		// set the value
		applicationBinding.setValue(value);
		applicationBinding.store();
		return oldValue;
	}
	
	private ICApplicationBinding createApplicationBindingCheckValue(String key, String value) throws IDOLookupException, CreateException {
		// do not create an application binding without a value
		if (value == null) {
			return null;
		}
		ICApplicationBinding applicationBinding = getICApplicationBindingHome().create();
		applicationBinding.setKey(key);
		applicationBinding.setValue(value);
		applicationBinding.store();
		return applicationBinding;
	}

	private String getLegacyPropertyFromApplicationSettings(String key) {
		IWMainApplicationSettings settings = getIWApplicationContext().getApplicationSettings();
		return settings.getProperty(key);
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
