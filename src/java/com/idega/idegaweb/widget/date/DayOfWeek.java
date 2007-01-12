/*
 * $Id: DayOfWeek.java,v 1.5.2.1 2007/01/12 19:32:39 idegaweb Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.date;

import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.util.IWCalendar;
import com.idega.util.text.TextSoap;


/**
 * Shows the day of week as a Text object.  Uses localized names for the weekdays.
 * 
 * Last modified: 14.10.2004 14:04:38 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5.2.1 $
 */
public class DayOfWeek extends Widget {

	private boolean capitalized = false;
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		
		String dayName = calendar.getDayName(calendar.getDayOfWeek());
		if (this.capitalized) {
			dayName = TextSoap.capitalize(dayName);
		}
		
		Text text = new Text(dayName);
		return text;
	}
	
	public void setCapitalized(boolean capitalized) {
		this.capitalized = capitalized;
	}
}