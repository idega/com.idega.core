/*
 * $Id: FramedApplicationViewNode.java,v 1.1 2004/10/25 14:48:52 tryggvil Exp $
 * Created on 20.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.view.node;

import com.idega.faces.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2004/10/25 14:48:52 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
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
		return frameUrl;
	}
	/**
	 * @param frameUrl The frameUrl to set.
	 */
	public void setFrameUrl(String frameUrl) {
		this.frameUrl = frameUrl;
	}
}
