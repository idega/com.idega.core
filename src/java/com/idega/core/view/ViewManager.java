/*
 * $Id: ViewManager.java,v 1.7 2005/02/01 17:48:08 thomas Exp $
 * Created on 2.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.FacesUtil;


/**
 * This class is responsible for managing the "ViewNode" hierarchy.<br>
 * <br>
 * 
 *  Last modified: $Date: 2005/02/01 17:48:08 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.7 $
 */
public class ViewManager implements Singleton {
	
	private static Instantiator instantiator = new Instantiator() 
		{ 
			public Object getInstance(Object parameter) {
				IWMainApplication iwma = null;
				if (parameter instanceof FacesContext) {
					iwma = IWMainApplication.getIWMainApplication((FacesContext) parameter);
				}
				else {
					iwma = (IWMainApplication) parameter;
				}
				return new ViewManager(iwma);
			}
		};
	
	private ViewNode rootNode;
	private ViewNode workspaceNode;
	private IWMainApplication iwma;
	
	public static ViewManager getInstance(IWMainApplication iwma){
		return (ViewManager) SingletonRepository.getRepository().getInstance(ViewManager.class, instantiator, iwma);
	}
	
	public static ViewManager getInstance(FacesContext context){
		return (ViewManager) SingletonRepository.getRepository().getInstance(ViewManager.class, instantiator, context);
	}
	
	private ViewManager(IWMainApplication iwma){
		this.iwma=iwma;
	}
	
	public void initializeStandardViews(ViewHandler handler){
		
		setApplicationRoot(iwma,handler);
		
		
		try {
			Class applicationClass = Class.forName("com.idega.builder.app.IBApplication");
			FramedWindowClassViewNode builderNode = new FramedWindowClassViewNode("builder",getWorkspaceRoot());
			builderNode.setWindowClass(applicationClass);
			builderNode.setJspUri(getWorkspaceRoot().getResourceURI());
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Class applicationClass = Class.forName("com.idega.user.app.UserApplication");
			FramedWindowClassViewNode userNode = new FramedWindowClassViewNode("user",getWorkspaceRoot());
			userNode.setWindowClass(applicationClass);
			userNode.setJspUri(getWorkspaceRoot().getResourceURI());
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Class applicationClass = Class.forName("com.idega.development.presentation.IWDeveloper");
			FramedWindowClassViewNode developerNode = new FramedWindowClassViewNode("developer",getWorkspaceRoot());
			developerNode.setWindowClass(applicationClass);
			developerNode.setJspUri(getWorkspaceRoot().getResourceURI());
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		DefaultViewNode contentNode = new ApplicationViewNode("content",getWorkspaceRoot());
		contentNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/cmspage.jsp");
		
		//TODO: move these specific views
		DefaultViewNode articleNode = new DefaultViewNode("article",contentNode);
		articleNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/articles.jsp");
		DefaultViewNode createNewArticleNode = new DefaultViewNode("create",articleNode);
		createNewArticleNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/createarticle.jsp");
		
		DefaultViewNode previewArticlesNode = new DefaultViewNode("preview",articleNode);
		previewArticlesNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/previewarticle.jsp");
		//DefaultViewNode listArticlesNode = new ApplicationViewNode("listarticles",articleNode);
		
		DefaultViewNode documentNode = new DefaultViewNode("document",contentNode);
		
		DefaultViewNode searchNode = new DefaultViewNode("search",contentNode);
		searchNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/searcharticle.jsp");
		*/
		
		DefaultViewNode myPageNode = new ApplicationViewNode("mypage",getWorkspaceRoot());
		//TODO: Change this
		myPageNode.setJspUri("/idegaweb/bundles/com.idega.block.article.bundle/jsp/cmspage.jsp");
		
		//DefaultViewNode loginViewNode = new WindowViewNode("login",getApplicationRoot());
		
		//DefaultViewNode pagesViewNode = new BuilderPageViewNode("pages",getApplicationRoot());
		
		//DefaultViewNode windowViewNode = new WindowViewNode("window",getApplicationRoot());
		
	}
	
	public ViewNode getWorkspaceRoot(){
		//ViewNode workspaceNode = getApplicationRoot().getChild("workspace");
		if(workspaceNode==null){
			DefaultViewNode node = new DefaultViewNode(iwma);
			node.setViewId("workspace");
			//getApplicationRoot().addChildViewNode(node);
			node.setParent(getApplicationRoot());
			//String jspUri = iwma.getBundle("com.idega.webface").getJSPURI("workspace.jsp");
			String jspUri = "/idegaweb/bundles/com.idega.workspace.bundle/jsp/workspace.jsp";
			node.setJspUri(jspUri);
			workspaceNode = node;
		}
		return workspaceNode;
	}

	public ViewNode getApplicationRoot(){
		//if(rootNode==null){
		//	DefaultViewNode node = new DefaultViewNode();
		//	node.setViewId("/");
		//	rootNode = node;
		//}
		return rootNode;
	}
	
	protected void setApplicationRoot(IWMainApplication iwma,ViewHandler rootViewhandler){
		DefaultViewNode node = new DefaultViewNode(iwma);
		node.setViewId("/");
		node.setViewHandler(rootViewhandler);
		rootNode = node;
	}
	
	
	public ViewNode getViewNodeForUrl(String url){
		
		ViewNode root = this.getApplicationRoot();
		
		ViewNode node = root.getChild(url);
		if(node!=null){
			return node;
		}
		else{
			//Return the rootNode if nothing found:
			return root;
		}
	}
	
	
	/**
	 * @param ctx
	 * @return
	 */
	public String getRequestUriWithoutContext(FacesContext ctx) {
		/*HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
		//String contextPath = request.getContextPath();
		//String fullRequestUri = request.getRequestURI();
		String contextPath = "/";
		String fullRequestUri = ctx.getExternalContext().getRequestServletPath()+ctx.getExternalContext().getRequestPathInfo();
		if(contextPath.equals("/")){
			return fullRequestUri;
		}
		else{
			String subPath = fullRequestUri.substring(contextPath.length());
			return subPath;
		}*/
		return FacesUtil.getRequestUri(ctx,false);
	}
	
	
	public ViewNode getViewNodeForContext(FacesContext context){
		String url = getRequestUriWithoutContext(context);
		return this.getViewNodeForUrl(url);
	}
	
	
}
