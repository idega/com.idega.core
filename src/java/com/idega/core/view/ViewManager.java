/*
 * $Id: ViewManager.java,v 1.20.2.1 2007/05/30 16:48:05 eiki Exp $
 * Created on 2.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.Collection;
import java.util.Iterator;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.FacesUtil;
import com.idega.util.RequestUtil;


/**
 * This class is responsible for managing the "ViewNode" hierarchy.<br>
 * <br>
 * 
 *  Last modified: $Date: 2007/05/30 16:48:05 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.20.2.1 $
 */
public class ViewManager implements Singleton {
	
	private static final String VIEWNODE_CACHE_KEY = "iw_viewnode_cached";

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
	private boolean showMyPage=false;
	
	public static ViewManager getInstance(IWMainApplication iwma){
		return (ViewManager) SingletonRepository.getRepository().getInstance(ViewManager.class, instantiator, iwma);
	}
	
	public static ViewManager getInstance(FacesContext context){
		return (ViewManager) SingletonRepository.getRepository().getInstance(ViewManager.class, instantiator, context);
	}
	
	protected ViewManager(IWMainApplication iwma){
		this.iwma=iwma;
	}
	
	public void initializeStandardViews(ViewHandler handler){
		
		setApplicationRoot(this.iwma,handler);
		
		/*
		try {

			Class applicationClass = RefactorClassRegistry.forName("com.idega.builder.app.IBApplication");
			FramedWindowClassViewNode builderNode = new FramedWindowClassViewNode("builder",getWorkspaceRoot());
			Collection roles = new ArrayList();
			roles.add(StandardRoles.ROLE_KEY_BUILDER);
			builderNode.setAuthorizedRoles(roles);
			builderNode.setWindowClass(applicationClass);
			builderNode.setJspUri(getWorkspaceRoot().getResourceURI());
			builderNode.setKeyboardShortcut(new KeyboardShortcut("2"));
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*try {
			Class applicationClass = RefactorClassRegistry.forName("com.idega.user.app.UserApplication");
			FramedWindowClassViewNode userNode = new FramedWindowClassViewNode("user",getWorkspaceRoot());
			userNode.setKeyboardShortcut(new KeyboardShortcut("1"));
			
			Collection roles = new ArrayList();
			roles.add(StandardRoles.ROLE_KEY_USERADMIN);
			userNode.setAuthorizedRoles(roles);
			userNode.setWindowClass(applicationClass);
			
			String jspPath = this.iwma.getBundle("com.idega.workspace").getJSPURI("workspace.jsp");
			userNode.setJspUri(jspPath);
			//userNode.setJspUri(getWorkspaceRoot().getResourceURI());
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*try {
			Class applicationClass = RefactorClassRegistry.forName("com.idega.development.presentation.IWDeveloper");
			FramedWindowClassViewNode developerNode = new FramedWindowClassViewNode("developer",getWorkspaceRoot());
			Collection roles = new ArrayList();
			roles.add(StandardRoles.ROLE_KEY_DEVELOPER);
			developerNode.setAuthorizedRoles(roles);
			developerNode.setWindowClass(applicationClass);
			developerNode.setJspUri(getWorkspaceRoot().getResourceURI());
			developerNode.setKeyboardShortcut(new KeyboardShortcut("3"));
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		
		if(this.showMyPage){
			DefaultViewNode myPageNode = new ApplicationViewNode("mypage",getWorkspaceRoot());
			myPageNode.setName("My Page");
			//TODO: Change this
			myPageNode.setJspUri("/idegaweb/bundles/com.idega.block.article.bundle/jsp/cmspage.jsp");
			myPageNode.setKeyboardShortcut(new KeyboardShortcut("5"));
		}

	}
	
	public ViewNode getWorkspaceRoot(){
		//ViewNode workspaceNode = getApplicationRoot().getChild("workspace");
		if(this.workspaceNode==null){
			//DefaultViewNode node = new DefaultViewNode(iwma);
			DefaultViewNode node=null;
			try {
				node = (DefaultViewNode) Class.forName("com.idega.workspace.view.WorkspaceClassViewNode").newInstance();
			}
			catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			node.setViewId("workspace");
			//getApplicationRoot().addChildViewNode(node);
			node.setParent(getApplicationRoot());
			//String jspUri = iwma.getBundle("com.idega.webface").getJSPURI("workspace.jsp");
			//String jspUri = "/idegaweb/bundles/com.idega.workspace.bundle/jsp/workspace.jsp";
			//node.setJspUri(jspUri);
			this.workspaceNode = node;
		}
		return this.workspaceNode;
	}

	public ViewNode getApplicationRoot(){
		//if(rootNode==null){
		//	DefaultViewNode node = new DefaultViewNode();
		//	node.setViewId("/");
		//	rootNode = node;
		//}
		return this.rootNode;
	}
	
	protected void setApplicationRoot(IWMainApplication iwma,ViewHandler rootViewhandler){
		DefaultViewNode node = new DefaultViewNode(iwma);
		node.setViewId("/");
		node.setViewHandler(rootViewhandler);
		this.rootNode = node;
	}
	
	
	public ViewNode calculateViewNodeForUrl(String url){
		
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
	 * <p>
	 * Checks if the given node is in the hierarchy of the URI uri.<br>
	 * e.g. if the given node has the uri /workspace/content then this method returns true for the uri /workspace/content/documents.
	 * </p>
	 * @param node
	 * @param uri
	 * @return
	 */
	public boolean isNodeInHierarchy(ViewNode node,String uri){
		
		String nodeUri = node.getURIWithContextPath();
		if(uri!=null){
			if(uri.startsWith(nodeUri)){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * @param ctx
	 * @return
	 */
	public String getRequestUriWithoutContext(FacesContext ctx) {
		return FacesUtil.getRequestUri(ctx,false);
	}
	
	/**
	 * @param ctx
	 * @return
	 */
	public String getRequestUriWithoutContext(HttpServletRequest request) {
		return RequestUtil.getURIMinusContextPath(request);
	}
	
	
	public ViewNode getViewNodeForContext(FacesContext context){
		ViewNode cachedNode = getCachedViewNode(context);
		if(cachedNode==null){
			String url = getRequestUriWithoutContext(context);
			cachedNode = this.calculateViewNodeForUrl(url);
			setCachedViewNode(context,cachedNode);
		}
		return cachedNode;
	}
	/**
	 * <p>
	 * TODO tryggvil describe method setCachedViewNode
	 * </p>
	 * @param context
	 * @param cachedNode
	 */
	private void setCachedViewNode(FacesContext context, ViewNode cachedNode) {
		FacesUtil.putAttribute(context,VIEWNODE_CACHE_KEY,cachedNode);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCachedViewNode
	 * </p>
	 * @param context
	 * @return
	 */
	private ViewNode getCachedViewNode(FacesContext context) {
		return (ViewNode) FacesUtil.getAttribute(context,VIEWNODE_CACHE_KEY);
	}
	
	public ViewNode getViewNodeForRequest(HttpServletRequest request){
		ViewNode cachedNode = getCachedViewNode(request);
		if(cachedNode==null){
			String url = getRequestUriWithoutContext(request);
			cachedNode = this.calculateViewNodeForUrl(url);
			setCachedViewNode(request,cachedNode);
		}
		return cachedNode;
	}
	
	
	/**
	 * <p>
	 * TODO tryggvil describe method setCachedViewNode
	 * </p>
	 * @param request
	 * @param cachedNode
	 */
	private void setCachedViewNode(HttpServletRequest request, ViewNode cachedNode) {
		//This implementation doesn't work for Navigation-Rules:
		//request.setAttribute(VIEWNODE_CACHE_KEY,cachedNode);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCachedViewNode
	 * </p>
	 * @param request
	 * @return
	 */
	private ViewNode getCachedViewNode(HttpServletRequest request) {
		//return (ViewNode) request.getAttribute(VIEWNODE_CACHE_KEY);
		return null;
	}

	protected IWMainApplication getIWMainApplication(){
		return this.iwma;
	}
	
	/**
	 * Checks if the user has access to a node. This uses the role system.
	 * @param node
	 * @param user
	 * @return
	 */
	public boolean hasUserAccess(ViewNode node,IWUserContext userContext){
		
		//First check hasUserAccess for custom logic
		try {
			boolean hasAccess = node.hasUserAccess(userContext);
			return hasAccess;
		} catch (NotImplementedException e) {
			//ignore just not implemented
		}
		
		//then check roles
		Collection roles = node.getAuthorizedRoles();
		if(roles!=null){
			if(roles.size()>0){
				for (Iterator iter = roles.iterator(); iter.hasNext();) {
					String roleKey = (String) iter.next();
					if(getIWMainApplication().getAccessController().hasRole(roleKey,userContext)){
						return true;
					}
				}
				return false;
			}
		}
		
		return true;
	}
	
}
