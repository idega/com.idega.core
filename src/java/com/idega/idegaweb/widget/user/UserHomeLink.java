/*
 * $Id: UserHomeLink.java,v 1.1 2004/11/01 18:46:40 laddi Exp $
 * Created on 1.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget.user;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.text.LinkContainer;
import com.idega.user.business.UserBusiness;


/**
 * An object that is a link to the logged in user's home page.  Is a container so it contain any PresentationObject.
 * Displays the added objects without a link around it when no user is logged on.
 * 
 * Last modified: 1.11.2004 17:07:07 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class UserHomeLink extends PresentationObjectContainer {
	
	private String iLoggedOnStyleClass;
	private String iLoggedOffStyleClass;
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.widget.Widget#getWidget(com.idega.presentation.IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if (iwc.isLoggedOn()) {
			try {
				ICPage homePage = getUserBusiness(iwc).getHomePageForUser(iwc.getCurrentUser());
				LinkContainer link = new LinkContainer();
				if (iLoggedOnStyleClass != null) {
					link.setStyleClass(iLoggedOnStyleClass);
				}
				link.setPage(homePage);
				
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
				super.print(iwc);
			}
			catch (RemoteException re) {
				log(re);
			}
			catch (FinderException fe) {
				log(fe);
			}
		}
		
		else {
			List list = this.getChildren();
			if (list != null) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					PresentationObject object = (PresentationObject) iter.next();
					if (iLoggedOffStyleClass != null) {
						object.setStyleClass(iLoggedOffStyleClass);
					}
				}
			}

			super.print(iwc);
		}
	}
	
	protected UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
	
	public void setLoggedOnStyleClass(String loggedOnStyleClass) {
		iLoggedOnStyleClass = loggedOnStyleClass;
	}
	
	public void setLoggedOffStyleClass(String loggedOffStyleClass) {
		iLoggedOffStyleClass = loggedOffStyleClass;
	}
	
	public boolean isContainer() {
		return true;
	}
}