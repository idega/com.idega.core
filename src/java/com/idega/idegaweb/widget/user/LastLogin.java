/*
 * $Id$
 * Created on Jan 15, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.user;

import javax.ejb.FinderException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.accesscontrol.data.LoginRecord;
import com.idega.core.accesscontrol.data.LoginRecordHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.user.data.User;
import com.idega.util.IWCalendar;
import com.idega.util.IWTimestamp;


public class LastLogin extends Widget {

	private int dateStyle = IWCalendar.SHORT;
	private int timeStyle = IWCalendar.SHORT;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		try {
			User user = iwc.getCurrentUser();
			
			try {
				LoginRecordHome home = (LoginRecordHome) IDOLookup.getHome(LoginRecord.class);
				LoginRecord record = home.findLastLoginRecord(user);

				IWTimestamp stamp = new IWTimestamp(record.getLogInStamp());
				
				Text text = new Text(getResourceBundle(iwc).getLocalizedString("last_logged_in_at", "You were last logged in at") + Text.NON_BREAKING_SPACE + stamp.getLocaleDateAndTime(iwc.getCurrentLocale(), dateStyle, timeStyle));
				return text;
			}
			catch (IDOLookupException ile) {
				throw new IBORuntimeException(ile);
			}
			catch (FinderException fe) {
				//Nothing found...
			}
		}
		catch (NotLoggedOnException nloe) {
		}
		return null;
	}

	/**
	 * Sets the style of the date.
	 * @param style	The style to set
	 */
	public void setDateStyle(int style) {
		this.dateStyle = style;
	}

	/**
	 * Sets the style of the time.
	 * @param style	The style to set
	 */
	public void setTimeStyle(int style) {
		this.timeStyle = style;
	}
}