/*
 * $Id: LoggedOnLink.java,v 1.2 2006/04/09 12:13:16 laddi Exp $
 * Created on 1.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.user;

import java.util.Iterator;
import java.util.List;

import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.text.LinkContainer;


/**
 * An object that is a link to a page specified by the User.  Is a container so it can contain any PresentationObject.
 * Displays the added objects without a link around it when no user is logged on.
 * 
 * Last modified: 1.11.2004 17:07:07 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class LoggedOnLink extends PresentationObjectContainer {
	
	private String iLoggedOnStyleClass;
	private String iLoggedOffStyleClass;
	
	private ICPage iPage;
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if (iwc.isLoggedOn()) {
			LinkContainer link = new LinkContainer();
			if (this.iLoggedOnStyleClass != null) {
				link.setStyleClass(this.iLoggedOnStyleClass);
			}
			if (this.iPage != null) {
				link.setPage(this.iPage);
			}
			
			List list = this.getChildren();
			if (list != null) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					PresentationObject object = (PresentationObject) iter.next();
					link.add(object);
				}
			}
			
			this.empty();
			add(link);
		}
		
		else {
			List list = this.getChildren();
			if (list != null) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					PresentationObject object = (PresentationObject) iter.next();
					if (this.iLoggedOffStyleClass != null) {
						object.setStyleClass(this.iLoggedOffStyleClass);
					}
				}
			}

		}
		super.print(iwc);
	}
	
	/**
	 * Sets the style class to use when logged on.  Is used on the surrounding Link.
	 * @param loggedOnStyleClass The loggedOnStyleClass to set.
	 */
	public void setLoggedOnStyleClass(String loggedOnStyleClass) {
		this.iLoggedOnStyleClass = loggedOnStyleClass;
	}
	
	/**
	 * Sets the style class to use when logged off.  Is set to all added objects.
	 * @param loggedOffStyleClass The loggedOffStyleClass to set.
	 */
	public void setLoggedOffStyleClass(String loggedOffStyleClass) {
		this.iLoggedOffStyleClass = loggedOffStyleClass;
	}
	
	public boolean isContainer() {
		return true;
	}
	
	/**
	 * Sets the page to link to when logged on.
	 * @param page The page to set.
	 */
	public void setPage(ICPage page) {
		this.iPage = page;
	}
}