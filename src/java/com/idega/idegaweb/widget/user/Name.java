/*
 * $Id: Name.java,v 1.2 2004/11/02 14:09:22 laddi Exp $
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
 * @version $Revision: 1.2 $
 */
public class Name extends Widget {

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		try {
			User user = iwc.getCurrentUser();
			
			Text text = new Text(user.getName());
			return text;
		}
		catch (NotLoggedOnException nloe) {
			return null;
		}
	}
}