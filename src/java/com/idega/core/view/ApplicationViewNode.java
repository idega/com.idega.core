/*
 * $Id: ApplicationViewNode.java,v 1.1 2004/11/14 23:24:47 tryggvil Exp $
 * Created on 16.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.Locale;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2004/11/14 23:24:47 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
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

}
