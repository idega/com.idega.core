/*
 * $Id: Page.java,v 1.5 2004/11/02 14:09:22 laddi Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.navigation;

import java.rmi.RemoteException;

import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.widget.Widget;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;


/**
 * Shows the current page's localized name as a Text object.
 * 
 * Last modified: 14.10.2004 13:56:18 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public class Page extends Widget {

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#decorate(com.idega.presentation.IWContext)
	 */
	protected PresentationObject getWidget(IWContext iwc) {
		try {
			ICPage currentPage = getBuilderService(iwc).getCurrentPage(iwc);
			
			Text text = new Text(currentPage.getName(iwc.getCurrentLocale()));
			return text;
		}
		catch (RemoteException re) {
			log(re);
			return null;
		}
	}
}