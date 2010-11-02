/*
 * $Id: UserImage.java 1.1 Sep 29, 2009 laddi Exp $
 * Created on Sep 29, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.user;

import java.sql.SQLException;

import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.User;

public class UserImage extends Widget {

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	@Override
	protected PresentationObject getWidget(IWContext iwc) {
		try {
			User user = iwc.getCurrentUser();
			
			int imageID = user.getSystemImageID();
			if (imageID > 0) {
				try {
					Image image = new Image(imageID);
					image.setAlt(user.getName());
					return image;
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		catch (NotLoggedOnException nloe) {
			return null;
		}
	}
}