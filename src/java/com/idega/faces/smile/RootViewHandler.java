/*
 * Created on 18.5.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.faces.smile;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.StringHandler;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RootViewHandler extends CbpViewHandler {
	
	private static Logger log = Logger.getLogger(RootViewHandler.class.getName());
	/**
	 * 
	 */
	public RootViewHandler() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param getParentViewHandler()
	 */
	public RootViewHandler(ViewHandler parentViewHandler) {
		this.setParentViewHandler(parentViewHandler);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#calculateLocale(javax.faces.context.FacesContext)
	 */
	public Locale calculateLocale(FacesContext arg0) {
		return getParentViewHandler().calculateLocale(arg0);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#calculateRenderKitId(javax.faces.context.FacesContext)
	 */
	public String calculateRenderKitId(FacesContext arg0) {
		return getParentViewHandler().calculateRenderKitId(arg0);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext arg0, String arg1) {
		return getParentViewHandler().createView(arg0, arg1);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return getParentViewHandler().equals(arg0);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#getActionURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getActionURL(FacesContext arg0, String arg1) {
		return getParentViewHandler().getActionURL(arg0, arg1);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getResourceURL(FacesContext arg0, String arg1) {
		return getParentViewHandler().getResourceURL(arg0, arg1);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getParentViewHandler().hashCode();
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	public void renderView(FacesContext arg0, UIViewRoot arg1) throws IOException, FacesException {
		getParentViewHandler().renderView(arg0, arg1);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#restoreView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot restoreView(FacesContext arg0, String arg1) {
		return getParentViewHandler().restoreView(arg0, arg1);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getParentViewHandler().toString();
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.smile.application.CbpViewHandlerImpl#writeState(javax.faces.context.FacesContext)
	 */
	public void writeState(FacesContext arg0) throws IOException {
		getParentViewHandler().writeState(arg0);
	}
}
