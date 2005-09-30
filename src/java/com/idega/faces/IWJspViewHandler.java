/*
 * $Id: IWJspViewHandler.java,v 1.6 2005/09/30 09:26:40 tryggvil Exp $
 * Created on 21.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces;

import java.io.IOException;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FacesUtil;


/**
 * 
 *  Last modified: $Date: 2005/09/30 09:26:40 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class IWJspViewHandler extends ViewHandlerWrapper {
	
	private static Logger log = Logger.getLogger(IWJspViewHandler.class.toString());
	
	
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
		if(node.isResourceBased() && nodeCorrespondsToViewId(node, viewId, context)){
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

	private static String JSP_EXT = ".jsp";
	
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot restoreView(FacesContext context, String viewId) {
		ViewNode node = getNode(context);
		String newViewId=viewId;
		if(node.isResourceBased()){
			if(!viewId.endsWith(JSP_EXT)){
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
		if(node.isResourceBased() && nodeCorrespondsToViewId(node, viewId, context)){
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
		String requestContextPath = context.getExternalContext().getRequestContextPath();
		// count the length
		// e.g. "/cms" + "/workspace"  
		int length = requestContextPath.length() + requestServletPath.length();
		String uri = node.getURI();
		String uriWithoutContextAndServlet = uri.substring(length);
		int viewIdLength = viewId.length();
		// remove the slash at the end if necessary
		if (viewId.endsWith("/")) {
			viewId = viewId.substring(0, viewIdLength - 1);
		}
		if (uriWithoutContextAndServlet.endsWith("/")) {
			uriWithoutContextAndServlet = uriWithoutContextAndServlet.substring(0, uriWithoutContextAndServlet.length() - 1);
		}
		
		return uriWithoutContextAndServlet.equals(viewId);
	}

}

