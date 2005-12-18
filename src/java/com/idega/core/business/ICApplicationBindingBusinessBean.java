/*
 * $Id: ICApplicationBindingBusinessBean.java,v 1.6 2005/12/18 08:23:02 laddi Exp $
 * Created on Oct 7, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import com.idega.business.IBOServiceBean;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingBMPBean;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;


public class ICApplicationBindingBusinessBean extends IBOServiceBean  implements ICApplicationBindingBusiness{

	private static final int MAX_KEY_LENGTH = ICApplicationBindingBMPBean.MAX_KEY_LENGTH; 
	
	private static final String LEGACY_PROPERTY_FILE_NAME = "idegaweb.pxml";
	
	private ICApplicationBindingHome applicationBindingHome = null;
	
	public void initializeBean() {
		super.initializeBean();
		fetchLegacyProperties();
	}
	
	/**
	 * Returns the corresponding value to the specified key elso null if the key is not found.
	 * 
	 * Checks first if there is an entry in the application binding table. 
	 * If not the application settings are looked up: 
	 * If a value is found a corresponding application binding is created.
	 */
	public String get(String key) throws IOException {
		try {
			return getKey(key);
		}
		catch (IDOLookupException ex) {
			throw new IOException(ex.getMessage());
		}	
	}

	/**
	 * Puts an entry into the application binding table.
	 * If the value is null an existing entry is removed.
	 * 
	 * @return the old value or null if there was no entry
	 * 
	 */
	public String put(String key, String value) throws IOException {
		try {
			return putKeyValue(key, value);
		}
		catch (IDOLookupException ex) {
			throw new IOException(ex.getMessage());
		}
		catch (CreateException ex) {
			throw new IOException(ex.getMessage());
		}
		catch (RemoveException ex) {
			throw new IOException(ex.getMessage());
		}
	}
	
	
	public String remove(String key) throws IOException {
		return put(key, null);
	}
	
	
	/**
	 * Returns a set of the keys, elements are string objects
	 */
	public Set keySet() throws IOException {
		try {
			Collection coll = getICApplicationBindingHome().findAll();
			if (coll == null) {
				return new TreeSet();
			}
			// we are keeping things simple, the list is not very large
			Set keyList = new TreeSet();
			Iterator iterator = coll.iterator();
			while (iterator.hasNext()) {
				ICApplicationBinding binding = (ICApplicationBinding) iterator.next();
				keyList.add(binding.getKey());
			}
			return keyList;
		}
		catch (IDOLookupException ex) {
			throw new IOException(ex.getMessage());
		}
		catch (FinderException ex) {
			throw new IOException(ex.getMessage());
		}
	}
	
	
	private String getKey(String key) throws IDOLookupException {
		String shortKey = StringHandler.shortenToLength(key, MAX_KEY_LENGTH);
		try {
			// fetch the value
			return getICApplicationBinding(shortKey).getValue();
		}
		catch (FinderException ex) {
			// failed?
			return null;
		}
	}
	
	private String putKeyValue(String key, String value) throws CreateException, IDOLookupException, RemoveException {
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

	private void fetchLegacyProperties() {
		String propertiesRealPath = getIWApplicationContext().getIWMainApplication().getPropertiesRealPath();
		List toDelete = new ArrayList();
		if (FileUtil.exists(propertiesRealPath, LEGACY_PROPERTY_FILE_NAME)) {
			IWPropertyList legacyPropertyList = new IWPropertyList(propertiesRealPath, LEGACY_PROPERTY_FILE_NAME, false);
			Iterator iterator = legacyPropertyList.getIWPropertyListIterator();
			while (iterator.hasNext()) {
				IWProperty property = (IWProperty) iterator.next();
				String key = property.getKey();
				try {
					String newValue = get(key);
					if (newValue == null) {
						String oldValue = property.getValue();
						// do not delete and store properties that are lists
						if (oldValue != null && oldValue.length() != 0) {
							put(key, oldValue);
							toDelete.add(key);
						}
					}
				}
				catch (IOException ex) {
					ex.printStackTrace(System.err);
				}
			}
			Iterator deleteIterator = toDelete.iterator();
			while (deleteIterator.hasNext()) {
				deleteIterator.next();
				///legacyPropertyList.removeProperty(key);
			}
			legacyPropertyList.store();
		}
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
