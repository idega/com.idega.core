/*
 * $Id: Greeting.java,v 1.2 2004/11/02 14:09:22 laddi Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.general;

import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;


/**
 * Shows a localized greeting based on the time of day as a Text object.
 * 
 * Last modified: 14.10.2004 13:49:04 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class Greeting extends Widget {

	private static final String GREETING_KEY_MORNING = "greeting.morning";
	private static final String GREETING_KEY_AFTERNOON = "greeting.afternoon";
	private static final String GREETING_KEY_EVENING = "greeting.evening";
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Decoration#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWTimestamp stamp = new IWTimestamp();
		String greeting = "";
	  if (stamp.getHour() < 12) {
	  		greeting = getResourceBundle().getLocalizedString(GREETING_KEY_MORNING,"Good morning");
	  }
		else if (stamp.getHour() < 18) {
			greeting = getResourceBundle().getLocalizedString(GREETING_KEY_AFTERNOON,"Good afternoon");
		}
		else {
			greeting = getResourceBundle().getLocalizedString(GREETING_KEY_EVENING,"Good evening"); 
		}
	  
	  Text text = new Text(greeting);
	  return text;
	}
}