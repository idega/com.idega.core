/*
 * $Id: FramedApplicationViewNode.java,v 1.1.2.1 2007/01/12 19:32:12 idegaweb Exp $
 * Created on 20.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:12 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class FramedApplicationViewNode extends ApplicationViewNode {

	
	private String frameUrl;
	
	/**
	 * @param viewId
	 * @param parent
	 */
	public FramedApplicationViewNode(String viewId, ViewNode parent) {
		super(viewId, parent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param iwma
	 */
	public FramedApplicationViewNode(IWMainApplication iwma) {
		super(iwma);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return Returns the frameUrl.
	 */
	public String getFrameUrl() {
		return this.frameUrl;
	}
	/**
	 * @param frameUrl The frameUrl to set.
	 */
	public void setFrameUrl(String frameUrl) {
		this.frameUrl = frameUrl;
	}
}
