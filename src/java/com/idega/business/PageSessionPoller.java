package com.idega.business;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;

public class PageSessionPoller {
	
	public final static String PROPERTY_SESSION_POLLING = "iw.core.session.polling";
	
	public boolean isPollingSettingEnabled() {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(IWContext.getInstance());
		IWMainApplicationSettings applicationSettings  = iwma.getSettings();
		
		String value = applicationSettings.getProperty(PROPERTY_SESSION_POLLING, "false");
		if("false".equals(value)) {
			return false;
		} else if("true".equals(value)) {
			return true;
		}
		return false;
	}
	
	public boolean pollSession(String ping) {
		return true;
	}

}
