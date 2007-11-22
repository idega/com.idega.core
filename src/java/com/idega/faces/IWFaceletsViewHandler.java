package com.idega.faces;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.core.view.ViewNodeBase;
import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.FacesUtil;
import com.sun.facelets.FaceletViewHandler;

/**
 * <p>
 * Mostly copied from IWJspViewHandler
 * </p>
 *  Last modified: $Date: 2007/11/22 13:42:31 $ by $Author: civilis $
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 */
public class IWFaceletsViewHandler extends FaceletViewHandler {
	
	public IWFaceletsViewHandler(ViewHandler wrapped_vh){
		super(wrapped_vh);
	}
	
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		
		//Changing the ViewId so it is read from the ViewNode structure:
		ViewNode node = getNode(context);
		String viewId = viewToRender.getViewId();
		String newViewId=viewId;
		if(node.getViewNodeBase() == ViewNodeBase.FACELET && nodeCorrespondsToViewId(node, viewId, context)){
			newViewId=node.getResourceURI();
		}
		viewToRender.setViewId(newViewId);
		super.renderView(context,viewToRender);
	}
	
	private ViewNode getNode(FacesContext context) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		return ViewManager.getInstance(iwma).getViewNodeForContext(context);
	}
	
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		ViewNode node = getNode(context);
		String newViewId=viewId;
		if(node.getViewNodeBase() == ViewNodeBase.FACELET){
			if(!viewId.endsWith(ViewNodeBase.FACELET.extension())){
				newViewId=node.getResourceURI();
			}
		}
		return super.restoreView(context, newViewId);
	}
	
	public UIViewRoot createView(FacesContext context, String viewId) {
		ViewNode node = getNode(context);
		String newViewId = viewId;
		if(node.getViewNodeBase() == ViewNodeBase.FACELET && nodeCorrespondsToViewId(node, viewId, context)) {
			checkCopyOfFaceletToWebapp(context, node);
			newViewId = node.getResourceURI();
		}
		return super.createView(context, newViewId);
	}
	
	public String getActionURL(FacesContext context, String viewId) {
		return FacesUtil.getRequestUri(context);
	}
	
	protected ViewManager getViewManager(){
		return ViewManager.getInstance(IWMainApplication.getDefaultIWMainApplication());
	}

	private boolean nodeCorrespondsToViewId(ViewNode node, String viewId, FacesContext context) {
		// does the viewId correspond to the node?
		String requestServletPath = context.getExternalContext().getRequestServletPath();
		
		// count the length
		// e.g. "/cms" + "/workspace"  
		String uri = node.getURI();
		String uriStripped = null;
		int stripLength=0;
		if(viewId.startsWith(requestServletPath)){
			//this is a special case that happens on Oracle Application Server (oc4j)
			//stripLength = requestContextPath.length();
			stripLength=0;
		}
		else{
			//this is the case on Tomcat (5), i.e. the viewId doesn't contain the servletPath
			//stripLength = requestContextPath.length() + requestServletPath.length();
			stripLength=requestServletPath.length();
		}
		uriStripped= uri.substring(stripLength);
		
		int viewIdLength = viewId.length();
		// remove the slash at the end if necessary
		if (viewId.endsWith("/")) {
			viewId = viewId.substring(0, viewIdLength - 1);
		}
		if (uriStripped.endsWith("/")) {
			uriStripped = uriStripped.substring(0, uriStripped.length() - 1);
		}
		
		return uriStripped.equals(viewId);
	}

	
	/**
	 * <p>
	 * This method checks if the System property idegaweb.bundles.resource.dir is set.<br/>
	 * If it is set it checks the timestamps of the jsp files in both the webapp folder and the 
	 * [idegaweb.bundles.resource.dir] or workspace folder and copies the latter into the webapp
	 * folder if the lastmodified timestamp is more recent.
	 * </p>
	 */
	private void checkCopyOfFaceletToWebapp(FacesContext context,ViewNode node) {
		
		if(node.getViewNodeBase() == ViewNodeBase.FACELET)
			IWBundleResourceFilter.checkCopyOfResourceToWebapp(context, node.getResourceURI());
	}
}