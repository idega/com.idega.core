/*
 * $Id: WindowViewManager.java,v 1.2 2007/02/06 00:44:23 laddi Exp $
 * Created on 2.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.faces;

import javax.faces.context.FacesContext;

import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.faces.viewnode.WindowViewNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;


/**
 *  This is the class modules should use to attatch themselves on to the '/window' viewnode structure
 * 
 *  Last modified: $Date: 2007/02/06 00:44:23 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class WindowViewManager implements Singleton  {

	private static final String IW_WINDOW_VIEW_MANAGER_KEY = "iw_windowviewmanager";
	//private static final String WINDOW_ID="window";
	private static final String FACES_BUNDLE_IDENTIFIER="com.idega.faces";
	private ViewNode windowRootNode;
	private IWMainApplication iwma;
	private ViewNode windowWorkspaceViewNode;
	private ViewNode loginViewNode;
	
	private WindowViewManager(IWMainApplication iwma){
		this.iwma=iwma;
	}

	  public static synchronized WindowViewManager getInstance(IWMainApplication iwma){
	    WindowViewManager contentViewManager = (WindowViewManager) iwma.getAttribute(IW_WINDOW_VIEW_MANAGER_KEY);
	    if(contentViewManager==null){
	      contentViewManager = new WindowViewManager(iwma);
	      iwma.setAttribute(IW_WINDOW_VIEW_MANAGER_KEY,contentViewManager);
	    }
	    return contentViewManager;
	  }	
	
	public static WindowViewManager getInstance(FacesContext context){
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		return getInstance(iwma);
	}
	
	public ViewManager getViewManager(){
		return ViewManager.getInstance(this.iwma);
	}
	
	public ViewNode getWindowNode(){
		IWBundle iwb = this.iwma.getBundle(FACES_BUNDLE_IDENTIFIER);
		//ViewNode content = root.getChild(CONTENT_ID);
		if(this.windowRootNode==null){
			this.windowRootNode = initalizeWindowNode(iwb);
		}
		return this.windowRootNode;
	}
	
	public ViewNode initalizeWindowNode(IWBundle contentBundle){
		ViewNode root = getViewManager().getApplicationRoot();
		ViewNode windowRootNode = new WindowViewNode("window",root);
		this.windowRootNode = windowRootNode;
		return this.windowRootNode;
	}
	
	public ViewNode getWindowWorkspaceNode(){
		IWBundle iwb = this.iwma.getBundle(FACES_BUNDLE_IDENTIFIER);
		//ViewNode content = root.getChild(CONTENT_ID);
		if(this.windowWorkspaceViewNode==null){
			this.windowWorkspaceViewNode = initalizeWindowWorkspaceNode(iwb);
		}
		return this.windowWorkspaceViewNode;
	}
	
	public ViewNode initalizeWindowWorkspaceNode(IWBundle contentBundle){
		ViewNode root = getViewManager().getWorkspaceRoot();
		DefaultViewNode windowViewNode2 = new WindowViewNode("window",root);
		windowViewNode2.setVisibleInMenus(false);
		this.windowWorkspaceViewNode = windowViewNode2;
		return this.windowWorkspaceViewNode;
	}
	
	public ViewNode getLoginNode(){
		IWBundle iwb = this.iwma.getBundle(FACES_BUNDLE_IDENTIFIER);
		//ViewNode content = root.getChild(CONTENT_ID);
		if(this.loginViewNode==null){
			this.loginViewNode = initalizeLoginNode(iwb);
		}
		return this.loginViewNode;
	}
	
	public ViewNode initalizeLoginNode(IWBundle contentBundle){
		ViewNode root = getViewManager().getApplicationRoot();
		ViewNode windowRootNode = new WindowViewNode("login",root);
		this.loginViewNode = windowRootNode;
		return this.loginViewNode;
	}
	
}
