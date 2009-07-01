/*
 * $Id: SunOrMoon.java,v 1.2 2006/04/09 12:13:19 laddi Exp $
 * Created on Oct 27, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.date;

import java.math.BigDecimal;
import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.util.IWCalendar;
import com.idega.util.IWTimestamp;


/**
 * Last modified: $Date: 2006/04/09 12:13:19 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class SunOrMoon extends Widget {

	private int iWidth = -1;
	private int iHeight = -1;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		double moonPhase = calendar.getMoonPhase();
		IWTimestamp sunrise = new IWTimestamp(calendar.getSunRiseSet(true));
		IWTimestamp sunset = new IWTimestamp(calendar.getSunRiseSet(false));
		IWTimestamp now = new IWTimestamp();
		
		Image image = null;

		if (now.isBetween(sunrise, sunset)) {
			image = getBundle().getImage("/widgets/moonphase/sun.png");
		}
		else {
			BigDecimal bd = new BigDecimal(moonPhase);
			bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
			moonPhase = bd.doubleValue();
			int moon = (int) (moonPhase * 10);
			
			image = getBundle().getImage("/widgets/moonphase/" + moon + ".png");
		}
		
		if (image != null) {
			if (this.iWidth > 0) {
				image.setWidth(this.iWidth);
			}
			if (this.iHeight > 0) {
				image.setHeight(this.iHeight);
			}
			return image;
		}

		return null;
	}

	/**
	 * Sets the height of the moon phase image
	 * @param height The height to set.
	 */
	public void setHeight(int height) {
		this.iHeight = height;
	}
	
	/**
	 * Sets the width of the moon phase image
	 * @param width The width to set.
	 */
	public void setWidth(int width) {
		this.iWidth = width;
	}
}