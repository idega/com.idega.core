/*
 * Created on 21.6.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces.smile;

import javax.faces.application.ViewHandler;
import net.sourceforge.smile.application.CbpViewHandlerImpl;

/**
 * @author tryggvil
 *
 */
public class CbpViewHandler extends CbpViewHandlerImpl{

	private ViewHandler parentViewHandler;
	
	/**
	 * @return Returns the defaultViewHandler.
	 */
	public ViewHandler getParentViewHandler() {
		
		return parentViewHandler;
	}
	/**
	 * @param defaultViewHandler The defaultViewHandler to set.
	 */
	public void setParentViewHandler(ViewHandler parentViewHandler) {
		this.parentViewHandler = parentViewHandler;
	}
}
