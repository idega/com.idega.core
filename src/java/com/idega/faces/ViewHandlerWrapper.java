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

import java.io.IOException;
import java.util.Locale;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;


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
public class ViewHandlerWrapper extends ViewHandler {

	private ViewHandler parentViewHandler;
	
	
	public ViewHandlerWrapper(ViewHandler wrappedHandler){
		this.setParentViewHandler(wrappedHandler);
	}
	
	/**
	 * @return Returns the defaultViewHandler.
	 */
	public ViewHandler getParentViewHandler() {
		return this.parentViewHandler;
	}
	/**
	 * @param defaultViewHandler The defaultViewHandler to set.
	 */
	public void setParentViewHandler(ViewHandler parentViewHandler) {
		this.parentViewHandler = parentViewHandler;
	}
	/**
	 * @param context
	 * @return
	 */
	public Locale calculateLocale(FacesContext context) {
		return getParentViewHandler().calculateLocale(context);
	}
	/**
	 * @param context
	 * @return
	 */
	public String calculateRenderKitId(FacesContext context) {
		return getParentViewHandler().calculateRenderKitId(context);
	}
	/**
	 * @param context
	 * @param viewId
	 * @return
	 */
	public UIViewRoot createView(FacesContext context, String viewId) {
		return getParentViewHandler().createView(context, viewId);
	}
	/**
	 * @param context
	 * @param viewId
	 * @return
	 */
	public String getActionURL(FacesContext context, String viewId) {
		return getParentViewHandler().getActionURL(context, viewId);
	}
	/**
	 * @param context
	 * @param path
	 * @return
	 */
	public String getResourceURL(FacesContext context, String path) {
		return getParentViewHandler().getResourceURL(context, path);
	}
	/**
	 * @param context
	 * @param viewToRender
	 * @throws java.io.IOException
	 * @throws javax.faces.FacesException
	 */
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		getParentViewHandler().renderView(context, viewToRender);
	}
	/**
	 * @param context
	 * @param viewId
	 * @return
	 */
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		return getParentViewHandler().restoreView(context, viewId);
	}
	/**
	 * @param context
	 * @throws java.io.IOException
	 */
	public void writeState(FacesContext context) throws IOException {
		getParentViewHandler().writeState(context);
	}
}
