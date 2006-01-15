/*
 * $Id: Name.java,v 1.3 2006/01/15 21:15:49 laddi Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.user;

import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.user.data.User;


/**
 * Displays the current logged in user's full name as a Text object.
 * 
 * Last modified: 14.10.2004 13:53:25 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class Name extends Widget {

	private boolean showLastName = true;
	private boolean showMiddleName = true;
	private boolean showFirstName = true;
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		try {
			User user = iwc.getCurrentUser();
			
			com.idega.util.text.Name name = new com.idega.util.text.Name();
			if (showFirstName) {
				name.setFirstName(user.getFirstName());
			}
			if (showMiddleName) {
				name.setMiddleName(user.getMiddleName());
			}
			if (showLastName) {
				name.setLastName(user.getLastName());
			}
			
			Text text = new Text(name.getName(iwc.getCurrentLocale()));
			return text;
		}
		catch (NotLoggedOnException nloe) {
			return null;
		}
	}
	
	public void setShowFirstName(boolean showFirstName) {
		this.showFirstName = showFirstName;
	}
	
	public void setShowLastName(boolean showLastName) {
		this.showLastName = showLastName;
	}
	
	public void setShowMiddleName(boolean showMiddleName) {
		this.showMiddleName = showMiddleName;
	}
}