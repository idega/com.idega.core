/*
 * Created on 6.8.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.smile;

import javax.faces.application.ViewHandler;


/**
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version 1.0
 */
public class LoginViewHandler extends WindowViewHandler {
	
	/**
	 * @param parentViewHandler
	 */
	public LoginViewHandler(ViewHandler parentViewHandler) {
		this.setParentViewHandler(parentViewHandler);
	}	
	
	public Class getDefaultPageClass() throws ClassNotFoundException{
		return Class.forName("com.idega.webface.workspace.WorkspaceLoginPage");
	}
	
}
