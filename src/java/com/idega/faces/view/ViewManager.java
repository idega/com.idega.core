/*
 * $Id: ViewManager.java,v 1.1 2004/10/19 10:37:10 tryggvil Exp $
 * Created on 2.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces.view;

import javax.faces.application.ViewHandler;
import com.idega.faces.view.node.ApplicationViewNode;
import com.idega.faces.view.node.BuilderPageViewNode;
import com.idega.faces.view.node.WindowViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2004/10/19 10:37:10 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class ViewManager {
	
	private static ViewManager instance;
	
	private ViewNode rootNode;
	private ViewNode workspaceNode;
	private IWMainApplication iwma;
	
	public static ViewManager getInstance(IWMainApplication iwma){
		if(instance==null){
			instance = new ViewManager(iwma);
		}
		return instance;
	}
	
	private ViewManager(IWMainApplication iwma){
		this.iwma=iwma;
	}
	
	public void initializeStandardViews(ViewHandler handler){
		
		setApplicationRoot(iwma,handler);
		
		DefaultViewNode builderNode = new ApplicationViewNode("builder",getWorkspaceRoot());
		
		DefaultViewNode contentNode = new ApplicationViewNode("content",getWorkspaceRoot());
		contentNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/cmspage.jsf");
		
		//TODO: move these specific views
		DefaultViewNode articleNode = new ApplicationViewNode("article",contentNode);
		articleNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/articles.jsf");
		DefaultViewNode createNewArticleNode = new ApplicationViewNode("create",articleNode);
		createNewArticleNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/createarticle.jsf");
		
		DefaultViewNode previewArticlesNode = new ApplicationViewNode("preview",articleNode);
		previewArticlesNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/previewarticle.jsf");
		//DefaultViewNode listArticlesNode = new ApplicationViewNode("listarticles",articleNode);
		
		DefaultViewNode documentNode = new ApplicationViewNode("document",contentNode);
		
		DefaultViewNode searchNode = new ApplicationViewNode("search",contentNode);
		searchNode.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/searcharticle.jsf");
		
		
		DefaultViewNode webviewNode = new ApplicationViewNode("webview",getWorkspaceRoot());
		
		DefaultViewNode loginViewNode = new WindowViewNode("login",getApplicationRoot());
		
		DefaultViewNode pagesViewNode = new BuilderPageViewNode("pages",getApplicationRoot());
		
		DefaultViewNode windowViewNode = new WindowViewNode("window",getApplicationRoot());
		
	}
	
	public ViewNode getWorkspaceRoot(){
		//ViewNode workspaceNode = getApplicationRoot().getChild("workspace");
		if(workspaceNode==null){
			DefaultViewNode node = new DefaultViewNode(iwma);
			node.setViewId("workspace");
			//getApplicationRoot().addChildViewNode(node);
			node.setParent(getApplicationRoot());
			node.setJspUri("/idegaweb/bundles/com.idega.webface.bundle/jsp/workspace.jsf");
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
}
