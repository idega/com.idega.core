/*
 * $Id: BuilderPageViewNode.java,v 1.1 2004/10/19 10:37:11 tryggvil Exp $
 * Created on 16.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.view.node;

import javax.faces.application.ViewHandler;
import com.idega.faces.smile.BuilderPageViewHandler;
import com.idega.faces.view.DefaultViewNode;
import com.idega.faces.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2004/10/19 10:37:11 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class BuilderPageViewNode extends DefaultViewNode {

	private ViewHandler builderPageViewHandler;
	
	/**
	 * @param viewId
	 * @param parent
	 */
	public BuilderPageViewNode(String viewId, ViewNode parent) {
		super(viewId, parent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param iwma
	 */
	public BuilderPageViewNode(IWMainApplication iwma) {
		super(iwma);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getViewHandler()
	 */
	public ViewHandler getViewHandler() {
		if(this.builderPageViewHandler==null){
			ViewNode parentNode = getParent();
			ViewHandler parentViewHandler = parentNode.getViewHandler();
			setViewHandler(new BuilderPageViewHandler(parentViewHandler));
		}
		return this.builderPageViewHandler;
	}
	/* (non-Javadoc)
	 * @see com.idega.faces.view.DefaultViewNode#setViewHandler(javax.faces.application.ViewHandler)
	 */
	public void setViewHandler(ViewHandler viewHandler) {
		this.builderPageViewHandler=viewHandler;
	}
	
}
