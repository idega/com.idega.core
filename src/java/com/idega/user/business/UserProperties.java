package com.idega.user.business;

import java.util.Map;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWPropertyList;
import com.idega.util.FileUtil;

/**
 * @author Laddi
 */
public class UserProperties extends IWPropertyList {

	public UserProperties(IWMainApplication application,int userID) {
		super(application.getPropertiesRealPath() + FileUtil.getFileSeparator() + "users", "user_"+String.valueOf(userID)+"_properties.pxml", true);
	}

	public IWPropertyList getProperties(String propertyListName) {
		IWPropertyList list = getPropertyList(propertyListName);
		if ( list == null ) {
			list = this.getNewPropertyList(propertyListName);
		}
		return list;
	}
	
	/**
	 * @see com.idega.idegaweb.IWPropertyList#setArrayProperty(String, Object)
	 */
	public void setArrayProperty(String key, Object value) {
		super.setArrayProperty(key, value);
		store();
	}

	/**
	 * @see com.idega.idegaweb.IWPropertyList#setProperties(Map)
	 */
	public void setProperties(Map properties) {
		super.setProperties(properties);
		store();
	}

	/**
	 * @see com.idega.idegaweb.IWPropertyList#setProperty(String, boolean)
	 */
	public void setProperty(String key, boolean value) {
		super.setProperty(key, value);
		store();
	}

	/**
	 * @see com.idega.idegaweb.IWPropertyList#setProperty(String, int)
	 */
	public void setProperty(String key, int value) {
		super.setProperty(key, value);
		store();
	}

	/**
	 * @see com.idega.idegaweb.IWPropertyList#setProperty(String, Object)
	 */
	public void setProperty(String key, Object value) {
		super.setProperty(key, value);
		store();
	}

	/**
	 * @see com.idega.idegaweb.IWPropertyList#setProperty(String, Object[])
	 */
	public void setProperty(String key, Object[] value) {
		super.setProperty(key, value);
		store();
	}

	/**
	 * @see com.idega.idegaweb.IWPropertyList#setProperty(String, String)
	 */
	public void setProperty(String key, String value) {
		super.setProperty(key, value);
		store();
	}
}