/*
 * $Id: ICApplicationBindingBusinessBean.java,v 1.10 2009/05/21 12:47:47 laddi Exp $
 * Created on Oct 7, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.business;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOServiceBean;
import com.idega.core.dao.ApplicationBindingDAO;
import com.idega.data.IDOLookupException;
import com.idega.util.expression.ELUtil;

/**
 *
 * Do not use this class directly, use IWMainApplicationSettings.
 *
 */
public class ICApplicationBindingBusinessBean extends IBOServiceBean  implements ICApplicationBindingBusiness{

	private static final long serialVersionUID = -4313564783724976758L;

	@Autowired
	private ApplicationBindingDAO appDao;

	private ApplicationBindingDAO getApplicationBindingDAO() {
		if (appDao == null) {
			ELUtil.getInstance().autowire(this);
		}
		return appDao;
	}

	/**
	 *
	 * Do not use this method, use IWMainApplicationSettings.
	 *
	 * Returns the corresponding value to the specified key elso null if the key is not found.
	 *
	 * Checks first if there is an entry in the application binding table.
	 * If not the application settings are looked up:
	 * If a value is found a corresponding application binding is created.
	 */
	@Override
	public String get(String key) throws IOException {
		try {
			return getKey(key);
		}
		catch (IDOLookupException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	/**
	 *
	 * Do not use this method, use IWMainApplicationSettings.
	 *
	 * Puts an entry into the application binding table.
	 * If the value is null an existing entry is removed.
	 *
	 * @return the old value or null if there was no entry
	 *
	 */
	@Override
	public String put(String key, String value) throws IOException {
		return put(key, value, null);
	}

	@Override
	public String put(String key, String value, String type) throws IOException {
		return getApplicationBindingDAO().put(key, value, type);
	}

	/**
	 *
	 * Do not use this method, use IWMainApplicationSettings.
	 *
	 */
	@Override
	public String remove(String key) throws IOException {
		return put(key, null);
	}

	/**
	 *
	 * Do not use this method, use IWMainApplicationSettings.
	 *
	 * Returns a set of the keys, elements are string objects
	 */
	@Override
	public Set<String> keySet() throws IOException {
		return getApplicationBindingDAO().keySet();
	}

	private String getKey(String key) throws IDOLookupException {
		return getApplicationBindingDAO().get(key);
	}

}