/*
 * $Id: MoonPhase.java,v 1.2 2004/11/02 08:47:02 laddi Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
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


/**
 * Shows an image representing the current moonphase.  Has ten different images that are stored
 * in the com.idega.idegaweb.widget bundle.
 * 
 * Last modified: 02.11.2004 09:39:38 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class MoonPhase extends Widget {
	
	private int width = -1;
	private int height = -1;

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		IWCalendar calendar = new IWCalendar(getLocale());
		double moonPhase = calendar.getMoonPhase();
		
		BigDecimal bd = new BigDecimal(moonPhase);
    bd = bd.setScale(1, BigDecimal.ROUND_HALF_DOWN);
    moonPhase = bd.doubleValue();
    int moon = (int) (moonPhase * 10);
		
		Image image = getBundle().getImage("/moonphase/" + moon + ".gif");
		
		if (image != null) {
			if (width > 0) {
				image.setWidth(width);
			}
			if (height > 0) {
				image.setHeight(height);
			}
			return image;
		}

		return null;
	}

	/**
	 * @param height The height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @param width The width to set.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
}