/*
 * $Id: Holiday.java,v 1.1 2004/10/14 12:11:56 laddi Exp $
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


/**
 * Last modified: 14.10.2004 11:24:56 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class Holiday extends Widget {

	/* (non-Javadoc)
	 * @see com.idega.block.decorations.presentation.Decoration#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		
		if (calendar.isHoliday()) {
			Text text = new Text(calendar.getHoliday().getDisplayName(getLocale()));
			return text;
		}
		
		return null;
	}

}
