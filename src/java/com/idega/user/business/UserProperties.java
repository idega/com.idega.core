package com.idega.user.business;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWPropertyList;
import com.idega.util.FileUtil;

/**
 * @author Laddi
 */
public class UserProperties extends IWPropertyList implements HttpSessionBindingListener {

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
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		super.store();
	}

}