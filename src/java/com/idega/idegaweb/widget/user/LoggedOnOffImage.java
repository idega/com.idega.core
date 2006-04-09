/*
 * $Id: LoggedOnOffImage.java,v 1.3 2006/04/09 12:13:16 laddi Exp $
 * Created on 1.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.user;

import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;


/**
 * Shows an image when user is logged on/off.
 * 
 * Last modified: 1.11.2004 18:16:35 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class LoggedOnOffImage extends Widget {

	private Image iLoggedOnImage;
	private Image iLoggedOffImage;
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		if (iwc.isLoggedOn()) {
			if (this.iLoggedOnImage != null) {
				return this.iLoggedOnImage;
			}
		}
		else {
			if (this.iLoggedOffImage != null) {
				return this.iLoggedOffImage;
			}
		}
		return null;
	}

	/**
	 * Sets the image to show when user is logged off.
	 * @param loggedOffImage The loggedOffImage to set.
	 */
	public void setLoggedOffImage(Image loggedOffImage) {
		this.iLoggedOffImage = loggedOffImage;
	}
	
	/**
	 * Sets the image to show when user is logged on.
	 * @param loggedOnImage The loggedOnImage to set.
	 */
	public void setLoggedOnImage(Image loggedOnImage) {
		this.iLoggedOnImage = loggedOnImage;
	}
}
