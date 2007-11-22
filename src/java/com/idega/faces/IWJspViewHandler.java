/*
 * $Id: IWJspViewHandler.java,v 1.16 2007/11/22 13:42:31 civilis Exp $
 * Created on 21.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
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


/**
 * <p>
 * This class overrides the default JSF ViewHandler and adds idegaWeb specific way of handling JSP pages
 * that are registered in the ViewNode hierarchy.<br/>
 * </p>
 *  Last modified: $Date: 2007/11/22 13:42:31 $ by $Author: civilis $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.16 $
 */
public class IWJspViewHandler extends ViewHandlerWrapper {
	
	public IWJspViewHandler(ViewHandler wrappedHandler){
		super(wrappedHandler);
	}
	
	
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
       /*
		ViewNode node = getNode(facesContext);
		
		if (viewToRender == null)
        {
            log.warning("viewToRender must not be null");
            throw new NullPointerException("viewToRender must not be null");
        }

        ExternalContext externalContext = facesContext.getExternalContext();

        String viewId = facesContext.getViewRoot().getViewId();*/
        
        /*
        ServletMapping servletMapping = getServletMapping(externalContext);
        if (servletMapping.isExtensionMapping())
        {
            String defaultSuffix = externalContext.getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
            String suffix = defaultSuffix != null ? defaultSuffix : ViewHandler.DEFAULT_SUFFIX;
            DebugUtils.assertError(suffix.charAt(0) == '.',
                                   log, "Default suffix must start with a dot!");
            if (!viewId.endsWith(suffix))
            {
                int dot = viewId.lastIndexOf('.');
                if (dot == -1)
                {
                    if (log.isTraceEnabled()) log.trace("Current viewId has no extension, appending default suffix " + suffix);
                    viewId = viewId + suffix;
                }
                else
                {
                    if (log.isTraceEnabled()) log.trace("Replacing extension of current viewId by suffix " + suffix);
                    viewId = viewId.substring(0, dot) + suffix;
                }
                facesContext.getViewRoot().setViewId(viewId);
            }
        }

        if (log.isTraceEnabled()) log.trace("Dispatching to " + viewId);*/
        
		//externalContext.dispatch(viewId);
		
		
		//Changing the ViewId so it is read from the ViewNode structure:
		ViewNode node = getNode(context);
		String viewId = viewToRender.getViewId();
		String newViewId=viewId;
		if(node.getViewNodeBase() == ViewNodeBase.JSP && nodeCorrespondsToViewId(node, viewId, context)){
			newViewId=node.getResourceURI();
		}
		viewToRender.setViewId(newViewId);
		
		
		super.renderView(context,viewToRender);
	}
	/**
	 * @param facesContext
	 * @return
	 */
	private ViewNode getNode(FacesContext context) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		return ViewManager.getInstance(iwma).getViewNodeForContext(context);
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		ViewNode node = getNode(context);
		String newViewId=viewId;
		if(node.getViewNodeBase() == ViewNodeBase.JSP){
			if(!viewId.endsWith(ViewNodeBase.JSP.extension())){
				newViewId=node.getResourceURI();
			}
		}
		return super.restoreView(context, newViewId);
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext context, String viewId) {
		ViewNode node = getNode(context);
		String newViewId=viewId;
		if(node.getViewNodeBase() == ViewNodeBase.JSP && nodeCorrespondsToViewId(node, viewId, context)){
			checkCopyOfJspToWebapp(context,node);
			newViewId=node.getResourceURI();
		}
		//if(node.isResourceBased()){
		//	if(!viewId.endsWith(JSP_EXT)){
		//		newViewId=node.getResourceURI();
		//	}
		//}
		return super.createView(context, newViewId);
	}
	
	
	public String getActionURL(FacesContext context, String viewId) {
		//The default faces implementation is a little bit strange here. it returns something like /contentapp/workspace/idegaweb/bundles/com.idega.webface.bundle/jsp/workspace.jsp
		//Here we return ust the requestUri. This might have to change.
		
		//HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
		//return request.getRequestURI();
		String requestUri = FacesUtil.getRequestUri(context);
		return requestUri;
	}
	
	protected ViewManager getViewManager(){
		return ViewManager.getInstance(IWMainApplication.getDefaultIWMainApplication());
	}

	private boolean nodeCorrespondsToViewId(ViewNode node, String viewId, FacesContext context) {
		// does the viewId correspond to the node?
		String requestServletPath = context.getExternalContext().getRequestServletPath();
		//String requestContextPath = context.getExternalContext().getRequestContextPath();
		//String requestContextPath = IWMainApplication.getIWMainApplication(context).getApplicationContextURI();
		
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
	private void checkCopyOfJspToWebapp(FacesContext context,ViewNode node) {
		
		if(node.getViewNodeBase() == ViewNodeBase.JSP)
			IWBundleResourceFilter.checkCopyOfResourceToWebapp(context, node.getResourceURI());
	}
}