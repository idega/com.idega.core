/*
 * $Id: ViewHandlerWrapper.java,v 1.2 2006/04/09 11:56:22 laddi Exp $
 * Created on 21.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces;

import javax.faces.application.ViewHandler;


/**
 * This is a view handler that by default delegates all method calls to its wrapped viewHandler instance.<br>
 * This is convenient to override parts of the viewhandler methods.
 * 
 * 
 *  Last modified: $Date: 2006/04/09 11:56:22 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class ViewHandlerWrapper extends javax.faces.application.ViewHandlerWrapper {

	private ViewHandler parentViewHandler;
	
	
	public ViewHandlerWrapper(ViewHandler wrappedHandler){
		parentViewHandler = wrappedHandler;
	}

	@Override
	public ViewHandler getWrapped() {
		return parentViewHandler;
	}
}
