/*
 * $Id: DayOfWeekAndDate.java,v 1.3 2006/04/09 12:13:19 laddi Exp $
 * Created on Oct 27, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
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
import com.idega.util.LocaleUtil;
import com.idega.util.text.TextSoap;


/**
 * Last modified: $Date: 2006/04/09 12:13:19 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class DayOfWeekAndDate extends Widget {

	private int style = IWCalendar.FULL;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		
		String dayName = TextSoap.capitalize(calendar.getDayName(calendar.getDayOfWeek()));
		Text text = new Text(dayName + (getLocale().equals(LocaleUtil.getIcelandicLocale()) ? " " : ", ") + calendar.getLocaleDate(getLocale(), this.style));
		
		return text;
	}

	/**
	 * Sets the style of the date.
	 * @param style	The style to set
	 */
	public void setDateStyle(int style) {
		this.style = style;
	}
}