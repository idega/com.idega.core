/*
 * $Id: Sunset.java,v 1.1 2004/10/14 12:11:56 laddi Exp $
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
 * @version $Revision: 1.1 $
 */
public class Sunset extends Widget {

	private int style = IWCalendar.SHORT;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Decoration#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		IWTimestamp stamp = new IWTimestamp(calendar.getSunRiseSet(false));
		
		Text text = new Text(stamp.getLocaleTime(getLocale(), style));
		return text;
	}

	public void setTimeStyle(int style) {
		this.style = style;
	}
}