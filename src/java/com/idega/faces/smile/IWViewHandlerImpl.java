/*
 * Created on 12.3.2004 by  tryggvil in project smile
 */
package com.idega.faces.smile;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.faces.view.ViewManager;
import com.idega.faces.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 * IWViewHandlerImpl //TODO: tryggvil Describe class
 * Copyright (C) idega software 2004
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
//Removed refeence to Smile
//public class IWViewHandlerImpl extends CbpViewHandlerImpl {
public class IWViewHandlerImpl extends ViewHandler{
	
	//private static Logger log = Logger.getLogger(IWViewHandlerImpl.class);
	private static Logger log = Logger.getLogger(IWViewHandlerImpl.class.getName());
	private ViewHandler parentViewHandler;
	private Map childHandlerMap;
	private ViewManager viewManager;
	
	public IWViewHandlerImpl(){
		log.info("Loading IWViewHandlerImpl");
	}

	public IWViewHandlerImpl(ViewHandler parentViewHandler,IWMainApplication iwma){
		log.info("Loading IWViewHandlerImpl with constructor IWViewHandlerImpl(ViewHandler parentViewHandler)");
		this.setParentViewHandler(parentViewHandler);
		
		/*ViewHandler builderPageViewHandler = new BuilderPageViewHandler(this);
		ViewHandler windowViewHandler = new WindowViewHandler(this);
		ViewHandler workspaceViewHandler = new WorkspaceViewHandler(this);
		ViewHandler loginViewHandler = new LoginViewHandler(this);
		
		addChildViewHandler("/pages",builderPageViewHandler);
		addChildViewHandler("/idegaweb/pages",builderPageViewHandler);
		addChildViewHandler("/window",windowViewHandler);
		addChildViewHandler("/idegaweb/window",windowViewHandler);

		addChildViewHandler("/login",loginViewHandler);
		addChildViewHandler("/idegaweb/login",loginViewHandler);
	
		addChildViewHandler("/workspace",workspaceViewHandler);
		addChildViewHandler("/idegaweb/workspace",workspaceViewHandler);
		*/
		viewManager = ViewManager.getInstance(iwma);
		viewManager.initializeStandardViews(new RootViewHandler(parentViewHandler));
		
	}	
	
	/*
	protected void addChildViewHandler(String urlPrefix, ViewHandler handler) {
		Map m = getChildHandlerMap();
		m.put(urlPrefix,handler);
	}
	
	protected Map getChildHandlerMap() {
		if(childHandlerMap==null){
			childHandlerMap=new HashMap();
		}
		return childHandlerMap;
	}
	*/

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#calculateLocale(javax.faces.context.FacesContext)
	 */
	public Locale calculateLocale(FacesContext ctx) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			return realHandler.calculateLocale(ctx);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found to calculate Locale");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#calculateRenderKitId(javax.faces.context.FacesContext)
	 */
	public String calculateRenderKitId(FacesContext ctx) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			return realHandler.calculateRenderKitId(ctx);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found to calculate RenderKitId");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext ctx, String viewId) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			return realHandler.createView(ctx,viewId);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found to create View");
		}
	}
	/**
	 * @param ctx
	 * @return
	 */
	private String getRequestUrl(FacesContext ctx) {
		HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
		String contextPath = request.getContextPath();
		String fullRequestUri = request.getRequestURI();
		if(contextPath.equals("/")){
			return fullRequestUri;
		}
		else{
			String subPath = fullRequestUri.substring(contextPath.length());
			return subPath;
		}
	}

	
	private ViewHandler getViewHandlerForContext(FacesContext ctx) {
		String url = getRequestUrl(ctx);
		ViewHandler viewHandler = getViewHandlerForUrl(url,ctx);
			if(viewHandler!=null){
				return viewHandler;
			}
			else{
				if(getParentViewHandler()!=null){
					return getParentViewHandler();
				}
				else{
					//return createView(ctx,vewId);
					throw new RuntimeException ("No parent ViewHandler");
				}
			}
		//return viewHandler;
	}
	
	/**
	 * @param url
	 * @return
	 */
	private ViewHandler getViewHandlerForUrl(String url,FacesContext ctx) {
		/*Iterator iterator = this.getChildHandlerMap().keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(key.startsWith(url)){
				return (ViewHandler)getChildHandlerMap().get(key);
			}
		}
		return null;*/
		
		ViewNode node = getViewManager().getViewNodeForUrl(url);
		if(node!=null){
			if(node.isJSP()){
				try {
					ctx.getExternalContext().dispatch(node.getJSPURI());
					ctx.responseComplete();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return node.getViewHandler();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getActionURL(FacesContext ctx, String viewId) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			return realHandler.getActionURL(ctx,viewId);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found for getActionURL");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getResourceURL(FacesContext ctx, String path) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			return realHandler.getResourceURL(ctx,path);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found for getResourceURL");
		}
	}
	/*
	public StateManager getStateManager() {
		if(defaultViewHandler!=null){
			return defaultViewHandler.getStateManager();
		}
		else{
			return super.getStateManager();
		}
	}

	public String getViewIdPath(FacesContext ctx, String viewId) {
		if(defaultViewHandler!=null){
			return defaultViewHandler.getViewIdPath(ctx,viewId);
		}
		else{
			return super.getViewIdPath(ctx,viewId);
		}
	}
	*/
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	public void renderView(FacesContext ctx, UIViewRoot viewId)
			throws IOException, FacesException {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			realHandler.renderView(ctx,viewId);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found for getResourceURL");
		}
		/*String url = getRequestUrl(ctx);
		ViewHandler childHandler = getViewHandlerForUrl(url);
		if(childHandler!=null){
			childHandler.renderView(ctx,viewId);
		}
		else{
			if(getParentViewHandler()!=null){
				getParentViewHandler().renderView(ctx,viewId);
			}
			else{
				//return createView(ctx,vewId);
				throw new RuntimeException ("No parent ViewHandler");
			}
		}*/
		/*
		if(getParentViewHandler()!=null){
			getParentViewHandler().renderView(ctx,viewRoot);
		}
		else{
			//return super.renderView(ctx,viewRoot);
			throw new RuntimeException ("No parent ViewHandler");
		}*/
	}
	
	
	/**
	 * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	protected void cbpRenderView(FacesContext ctx, UIViewRoot viewRoot) throws IOException, FacesException {
		// Apparently not all versions of tomcat have the same
		// default content-type.
		// So we'll set it explicitly.
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		response.setContentType("text/html");
		
		// make sure to set the responsewriter
		//initializeResponseWriter(ctx);		
		
		if(viewRoot == null) {
			throw new RuntimeException("No component tree is available !");
		}
		String renderkitId = viewRoot.getRenderKitId();
		if (renderkitId == null) {
			renderkitId = calculateRenderKitId(ctx);
		}
		viewRoot.setRenderKitId(renderkitId);

		ResponseWriter out = ctx.getResponseWriter();
		try {

			out.startDocument();
			renderComponent(ctx.getViewRoot(),ctx);
			out.endDocument();
			ctx.getResponseWriter().flush();

		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}	
	
	/**
	 * Recursive operation to render a specific component in the view tree.
	 * 
	 * @param component
	 * @param context
	 */
	private void renderComponent(UIComponent component, FacesContext ctx) {
		try {
			component.encodeBegin(ctx);
			if(component.getRendersChildren()) {
				component.encodeChildren(ctx);
			} else {
				Iterator it;
				UIComponent currentChild;
				it = component.getChildren().iterator();
				while(it.hasNext()) {
					currentChild = (UIComponent) it.next();
					renderComponent(currentChild,ctx);
				}
			}		
			//if (component instanceof Screen) {
			//	writeState(ctx); 
			//}
			component.encodeEnd(ctx);
		} catch(IOException e) {
			log.severe("Component <" + component.getId() + "> could not render ! Continuing rendering of view <" + ctx.getViewRoot().getViewId() + ">...");
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot restoreView(FacesContext ctx, String viewId) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			return realHandler.restoreView(ctx,viewId);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found for restoreView");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#writeState(javax.faces.context.FacesContext)
	 */
	public void writeState(FacesContext ctx) throws IOException {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if(realHandler!=null){
			realHandler.writeState(ctx);
		}
		else{
			throw new RuntimeException ("No ViewHandler Found for writeState");
		}
	}
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
	
	/**
	 * @return Returns the viewManager.
	 */
	protected ViewManager getViewManager() {
		return viewManager;
	}
}
