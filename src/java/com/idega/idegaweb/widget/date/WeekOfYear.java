/*
 * $Id: WeekOfYear.java,v 1.2 2004/11/02 14:09:22 laddi Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.date;

import java.text.MessageFormat;

import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.util.IWCalendar;


/**
 * Shows the week of year as a Text object.  Uses localization that can be altered in the Localizer.
 * 
 * Last modified: 14.10.2004 12:04:48 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class WeekOfYear extends Widget {

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Decoration#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar();
		
		Object[] arguments = { String.valueOf(calendar.getWeekOfYear()) };
		
		Text text = new Text(MessageFormat.format(getResourceBundle().getLocalizedString("week_of_year.week", "week {0}"), arguments));
		return text;
	}

}
