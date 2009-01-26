package com.idega.business;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;

public class PageSessionPoller {
	
	public final static String PROPERTY_SESSION_POLLING = "iw.core.session.polling";
	
	public boolean isPollingSettingEnabled() {
		IWMainApplicationSettings applicationSettings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		
		String value = applicationSettings.getProperty(PROPERTY_SESSION_POLLING, Boolean.FALSE.toString());
		if (Boolean.TRUE.toString().equals(value)) {
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	public boolean pollSession(String ping) {
		return Boolean.TRUE;
	}
}
