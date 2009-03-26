package com.idega.business;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;

public class PageSessionPoller {
	
	public boolean isPollingSettingEnabled() {
		IWMainApplicationSettings applicationSettings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		
		return applicationSettings.getIfUseSessionPolling();
	}
	
	public boolean pollSession(String ping) {
		return Boolean.TRUE;
	}
}
