/*
 * $Id: IWBundleStarter.java,v 1.3 2005/03/03 04:16:21 tryggvil Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces;

import com.idega.builder.view.BuilderRootViewNode;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewManager;
import com.idega.faces.viewnode.WindowViewNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;


/**
 * 
 *  Last modified: $Date: 2005/03/03 04:16:21 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class IWBundleStarter implements IWBundleStartable {

	/**
	 * 
	 */
	public IWBundleStarter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#start(com.idega.idegaweb.IWBundle)
	 */
	public void start(IWBundle starterBundle) {

		//Install the idegaweb views:
		
		ViewManager viewManager = ViewManager.getInstance(starterBundle.getApplication());
		
		DefaultViewNode loginViewNode = new WindowViewNode("login",viewManager.getApplicationRoot());
		//DefaultViewNode pagesViewNode = new BuilderRootViewNode("pages",viewManager.getApplicationRoot());
		DefaultViewNode windowViewNode = new WindowViewNode("window",viewManager.getApplicationRoot());
	
		//add the window node under workspace:
		DefaultViewNode windowViewNode2 = new WindowViewNode("window",viewManager.getWorkspaceRoot());
		windowViewNode2.setRendered(false);
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		// TODO Auto-generated method stub
	}
}
