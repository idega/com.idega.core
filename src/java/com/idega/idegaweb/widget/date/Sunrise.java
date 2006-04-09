/*
 * $Id: Sunrise.java,v 1.4 2006/04/09 12:13:19 laddi Exp $
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
import com.idega.util.IWTimestamp;


/**
 * Shows the time of sunset of the current Locale and date as a Text object.
 * Has a set method to change the style (details) of the time shown.
 * Available styles:<br>
 * 		IWCalendar.SHORT (default): completely numeric, such as 3:30pm<br>
 * 		IWCalendar.MEDIUM: is longer<br>
 * 		IWCalendar.LONG: is even longer, such as 3:30:32pm<br>
 * 		IWCalendar.FULL: is pretty completely specified, such as 3:30:42pm PST
 * 
 * Last modified: 14.10.2004 13:37:28 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
 */
public class Sunrise extends Widget {

	private int style = IWCalendar.SHORT;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Decoration#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		IWTimestamp stamp = new IWTimestamp(calendar.getSunRiseSet(true));
		
		Text text = new Text(stamp.getLocaleTime(getLocale(), this.style));
		return text;
	}

	/**
	 * Sets the style of the time.
	 * @param style	The style to set
	 */
	public void setTimeStyle(int style) {
		this.style = style;
	}
}