/*
 * $Id: IWBundleStarter.java,v 1.1 2004/11/14 23:37:11 tryggvil Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces;

import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewManager;
import com.idega.faces.viewnode.BuilderPageViewNode;
import com.idega.faces.viewnode.WindowViewNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;


/**
 * 
 *  Last modified: $Date: 2004/11/14 23:37:11 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
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
		DefaultViewNode pagesViewNode = new BuilderPageViewNode("pages",viewManager.getApplicationRoot());
		DefaultViewNode windowViewNode = new WindowViewNode("window",viewManager.getApplicationRoot());
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWBundleStartable#stop(com.idega.idegaweb.IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		// TODO Auto-generated method stub
	}
}
