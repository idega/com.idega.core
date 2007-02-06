/*
 * $Id: IWBundleStarter.java,v 1.8 2007/02/06 00:44:23 laddi Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;


/**
 * 
 *  Last modified: $Date: 2007/02/06 00:44:23 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.8 $
 */
public class IWBundleStarter implements IWBundleStartable {

	/**
	 * 
	 */
	public IWBundleStarter() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {

		//Install the idegaweb views:
		
		
		WindowViewManager viewmanager = WindowViewManager.getInstance(starterBundle.getApplication());
		viewmanager.initalizeWindowNode(starterBundle);
		viewmanager.initalizeLoginNode(starterBundle);
		viewmanager.initalizeWindowWorkspaceNode(starterBundle);
		
		/*ViewManager viewManager = ViewManager.getInstance(starterBundle.getApplication());
		
		new WindowViewNode("login",viewManager.getApplicationRoot());
		new WindowViewNode("window",viewManager.getApplicationRoot());
	
		//add the window node under workspace:
		DefaultViewNode windowViewNode2 = new WindowViewNode("window",viewManager.getWorkspaceRoot());
		windowViewNode2.setVisibleInMenus(false);*/
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
	}
}