/*
 * $Id: Sunrise.java,v 1.2 2004/10/26 09:05:20 laddi Exp $
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
 * Last modified: 14.10.2004 13:37:28 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class Sunrise extends Widget {

	private int style = IWCalendar.SHORT;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Decoration#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		calendar.setDate(2004, 6, 21);
		IWTimestamp stamp = new IWTimestamp(calendar.getSunRiseSet(true));
		
		Text text = new Text(stamp.getLocaleTime(getLocale(), style));
		return text;
	}

	public void setTimeStyle(int style) {
		this.style = style;
	}
}