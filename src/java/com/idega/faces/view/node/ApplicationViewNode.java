/*
 * $Id: ApplicationViewNode.java,v 1.2 2004/10/25 14:48:52 tryggvil Exp $
 * Created on 16.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.view.node;

import java.util.Locale;
import com.idega.faces.view.DefaultViewNode;
import com.idega.faces.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2004/10/25 14:48:52 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class ApplicationViewNode extends DefaultViewNode {

	private boolean showSideFunctionNavigation=false;
	
	/**
	 * @param viewId
	 * @param parent
	 */
	public ApplicationViewNode(String viewId, ViewNode parent) {
		super(viewId, parent);
	}

	/**
	 * @param iwma
	 */
	public ApplicationViewNode(IWMainApplication iwma) {
		super(iwma);
	}
	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getLocalizedName(java.util.Locale)
	 */
	public String getLocalizedName(Locale locale) {
		return this.getViewId();
	}
	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getName()
	 */
	public String getName() {
		return this.getViewId();
	}
}
