package com.idega.util;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;

public class CoreUtil {
	
	public static IWBundle getCoreBundle() {
		return IWMainApplication.getDefaultIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER);
	}

}
