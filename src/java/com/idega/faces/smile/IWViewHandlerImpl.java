/*
 * Created on 12.3.2004 by  tryggvil in project smile
 */
package com.idega.faces.smile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;


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
	
	public IWViewHandlerImpl(){
		log.info("Loading IWViewHandlerImpl");
	}

	public IWViewHandlerImpl(ViewHandler parentViewHandler){
		log.info("Loading IWViewHandlerImpl with constructor IWViewHandlerImpl(ViewHandler parentViewHandler)");
		this.setParentViewHandler(parentViewHandler);
		
		ViewHandler builderPageViewHandler = new BuilderPageViewHandler(this);
		ViewHandler windowViewHandler = new WindowViewHandler(this);
		
		addChildViewHandler("/pages",builderPageViewHandler);
		addChildViewHandler("/idegaweb/pages",builderPageViewHandler);
		addChildViewHandler("/window",windowViewHandler);
		addChildViewHandler("/idegaweb/window",windowViewHandler);
	}	
	
	/**
	 * @param string
	 * @param handler
	 */
	protected void addChildViewHandler(String urlPrefix, ViewHandler handler) {
		Map m = getChildHandlerMap();
		m.put(urlPrefix,handler);
	}

	/**
	 * @return
	 */
	protected Map getChildHandlerMap() {
		if(childHandlerMap==null){
			childHandlerMap=new HashMap();
		}
		return childHandlerMap;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#calculateLocale(javax.faces.context.FacesContext)
	 */
	public Locale calculateLocale(FacesContext ctx) {
		if(getParentViewHandler()!=null){
			return getParentViewHandler().calculateLocale(ctx);
		}
		else{
			throw new RuntimeException ("No parent ViewHandler");
			//return super.calculateLocale(ctx);
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#calculateRenderKitId(javax.faces.context.FacesContext)
	 */
	public String calculateRenderKitId(FacesContext ctx) {
		if(getParentViewHandler()!=null){
			return getParentViewHandler().calculateRenderKitId(ctx);
		}
		else{
			//return super.calculateRenderKitId(ctx);
			throw new RuntimeException ("No parent ViewHandler");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#createView(javax.faces.context.FacesContext, java.lang.String)
	 */
	public UIViewRoot createView(FacesContext ctx, String viewId) {
		String url = getRequestUrl(ctx);
		ViewHandler childHandler = getChildHandler(url);
		if(childHandler!=null){
			return childHandler.createView(ctx,viewId);
		}
		else{
			if(getParentViewHandler()!=null){
				return getParentViewHandler().createView(ctx,viewId);
			}
			else{
				//return createView(ctx,vewId);
				throw new RuntimeException ("No parent ViewHandler");
			}
		}
	}
	/**
	 * @param ctx
	 * @return
	 */
	private String getRequestUrl(FacesContext ctx) {
		return ctx.getExternalContext().getRequestServletPath();
	}

	/**
	 * @param url
	 * @return
	 */
	private ViewHandler getChildHandler(String url) {
		Iterator iterator = this.getChildHandlerMap().keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(key.startsWith(url)){
				return (ViewHandler)getChildHandlerMap().get(key);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getActionURL(FacesContext ctx, String viewId) {
		if(getParentViewHandler()!=null){
			return getParentViewHandler().getActionURL(ctx,viewId);
		}
		else{
			//return getActionURL(ctx,vewId);
			throw new RuntimeException ("No parent ViewHandler");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	public String getResourceURL(FacesContext ctx, String path) {
		if(getParentViewHandler()!=null){
			return getParentViewHandler().getResourceURL(ctx,path);
		}
		else{
			//return getResourceURL(ctx,path);
			throw new RuntimeException ("No parent ViewHandler");
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
		String url = getRequestUrl(ctx);
		ViewHandler childHandler = getChildHandler(url);
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
		}	
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
		if(getParentViewHandler()!=null){
			return getParentViewHandler().restoreView(ctx,viewId);
		}
		else{
			//return super.restoreView(ctx,viewId);
			throw new RuntimeException ("No parent ViewHandler");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#writeState(javax.faces.context.FacesContext)
	 */
	public void writeState(FacesContext ctx) throws IOException {
		if(getParentViewHandler()!=null){
			getParentViewHandler().writeState(ctx);
		}
		else{
			//return super.writeState(ctx);
			throw new RuntimeException ("No parent ViewHandler");
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
	 * Breaks down the URL string separated by the '/' charachter to an array.<br>
	 * For instance it breaks down the URL "/component/78909" to {"component","78909"}
	 * @return
	 */
	protected String[] breakDownURL(String urlString) {
		//Performance optimization to handle the first 4 parameters without having to construct the Vector
		String s1=null;
		String s2=null;
		String s3=null;
		String s4=null;
		List list = null;
		StringTokenizer st = new StringTokenizer(urlString,"/");
		int index=0;
		while(st.hasMoreTokens()){
			index++;
			if(index==1){
				s1=st.nextToken();
			}
			else if(index==2){
				s2=st.nextToken();
			}
			else if(index==3){
				s3=st.nextToken();
			}
			else if(index==4){
				s4=st.nextToken();
			}
			else if(index==5){
				list=new ArrayList();
				list.add(s1);
				list.add(s2);
				list.add(s3);
				list.add(s4);
				list.add(st.nextToken());
			}
			else if(index>5){
				st.nextToken();
			}	
		}
		if(index==1){
			String[] theReturn={s1};
			return theReturn;
		}
		else if(index==2){
			String[] theReturn={s1,s2};
			return theReturn;
		}
		else if(index==3){
			String[] theReturn={s1,s2,s3};
			return theReturn;
		}
		else if(index==4){
			String[] theReturn={s1,s2,s3,s4};
			return theReturn;
		}
		else if(index>4){
			String[] theReturn = (String[])list.toArray(new String[0]);
			return theReturn;
		}
		return null;
	}
}
