package com.idega.notifier.presentation;

import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;

public class InstantMessageNotification extends BasicNotification {

	public InstantMessageNotification() {
		super();
	}
	
	public InstantMessageNotification(String title, String message) {
		this();
		
		setTitle(title);
		setText(message);
	}
	
	@Override
	public void present(IWContext iwc) {
		if (StringUtil.isEmpty(getTitle())) {
			setTitle(getResourceBundle(iwc).getLocalizedString("instant_message_notification.title", "Instant message"));
		}
	}
	
	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}

}
