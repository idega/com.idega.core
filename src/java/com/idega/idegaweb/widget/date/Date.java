/*
 * $Id: Date.java,v 1.1 2004/10/14 12:11:56 laddi Exp $
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
 * Last modified: 14.10.2004 11:18:49 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class Date extends Widget {
	
	private int style = IWCalendar.SHORT;

	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar();
		
		Text text = new Text(calendar.getLocaleDate(getLocale(), style));
		return text;
	}
	
	public void setDateStyle(int style) {
		this.style = style;
	}
}