/*
 * $Id: WindowViewNode.java,v 1.3 2006/02/22 20:50:21 laddi Exp $
 * Created on 4.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.viewnode;

import javax.faces.application.ViewHandler;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.faces.WindowViewHandler;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2006/02/22 20:50:21 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class WindowViewNode extends DefaultViewNode {

	private ViewHandler windowViewHandler;
	/**
	 * @param viewId
	 * @param parent
	 */
	public WindowViewNode(String viewId, ViewNode parent) {
		super(viewId, parent);
		initialize();
	}

	/**
	 * @param iwma
	 */
	public WindowViewNode(IWMainApplication iwma) {
		super(iwma);	
		initialize();
	}
	
	private void initialize(){
		this.setComponentBased(true);
		/*try {
			this.setComponentClass(Class.forName("com.idega.webface.workspace.WorkspaceLoginPage"));
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	
	public ViewHandler getViewHandler() {
		if(this.windowViewHandler==null){
			setViewHandler(new WindowViewHandler(this));
		}
		return this.windowViewHandler;
	}
	/* (non-Javadoc)
	 * @see com.idega.faces.view.DefaultViewNode#setViewHandler(javax.faces.application.ViewHandler)
	 */
	public void setViewHandler(ViewHandler viewHandler) {
		this.windowViewHandler=viewHandler;
	}
	
}
