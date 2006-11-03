/*
 * $Id: BuilderService.java,v 1.14 2006/11/03 14:34:41 justinas Exp $
 * Created on 8.7.2003
 *
 * Copyright (C) 2003-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.business;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.faces.component.UIComponent;

import com.idega.business.IBOService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.data.ICTreeNode;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

/**
 * BuilderService: This is the interface to the iW Builder functions in idegaWeb
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public interface BuilderService extends IBOService
{
	/**
	 * Gets Domain for the current running application
	 * @return IBDomain representing the current application
	 * @throws RemoteException
	 */
	public ICDomain getCurrentDomain()throws RemoteException;
	/**
	 * Gets the URI to a page on this webserver
	 * @param pageID an id for a page to get the URI to
	 * @return The string that is the URI to the requested page on this webserver
	 * @throws RemoteException
	 */	
	public String getPageURI(int pageID)throws RemoteException;
	/**
	 * Gets the URI to a page on this webserver
	 * @param pageID an id for a page to get the URI to
	 * @return The string that is the URI to the requested page on this webserver
	 * @throws RemoteException
	 */	
	public String getPageURI(String pageID)throws RemoteException;
	/**
	 * Gets the URI to a page on this webserver
	 * @param page a page to get the URI to
	 * @return The string that is the URI to the requested page on this webserver
	 * @throws RemoteException
	 */	
	public String getPageURI(ICPage page)throws RemoteException;
	/**
	 * Gets a Page on this webserver
	 * @param pageID an id for the Page to get
	 * @return The Page on this webserver
	 * @throws RemoteException
	 */	
	public Page getPage(String pageKey)throws RemoteException;
	/**
	 * Gets the id of the Root page for the current application
	 * @return the id of an IBPage
	 * @throws RemoteException
	 */	
	public int getRootPageId()throws RemoteException;
	/**
	 * Gets the key for the requested page
	 * @param iwc the IWContext of the request
	 * @return the id of the currently requested page
	 * @throws RemoteException
	 */	
	public String getRootPageKey()throws RemoteException;
	/**
	 * Gets the Root page for the current application
	 * @return the IBPage for the root page
	 * @throws RemoteException
	 */		
	public ICPage getRootPage()throws RemoteException;
	/**
	 * Gets the id for the requested page
	 * @param iwc the IWContext of the request
	 * @return the id of the currently requested page
	 * @throws RemoteException
	 */	
	public int getCurrentPageId(IWContext iwc)throws RemoteException;
	/**
	 * Gets the Key for the requested page
	 * @param iwc the IWContext of the request
	 * @return the id of the currently requested page
	 * @throws RemoteException
	 */	
	public String getCurrentPageKey(IWContext iwc)throws RemoteException;
	/**
	 * Gets the requested page
	 * @param iwc the IWContext of the request
	 * @return the IBPage for the currently requested page
	 * @throws RemoteException
	 */	
	public ICPage getCurrentPage(IWContext iwc)throws RemoteException;
	/**
	 * Gets URI for the current (last requested) page
	 * @param iwc the IWContext of the request
	 * @return the string for the URI
	 * @throws RemoteException
	 */	
	public String getCurrentPageURI(IWContext iwc)throws RemoteException;

	/**
	 * Gets the Builder page tree starting from page with id startNodeId
	 * @param startNodeId Id of the IBPage to start traversing down the tree
	 * @param userId the user who requests it, -1 if no user is available
	 * @return ICTreeNode for the page requested
	 * @throws RemoteException
	 */
	public ICTreeNode getPageTree(int startNodeId,int userId)throws RemoteException;
	/**
	 * Gets the Builder page tree starting from page with id startNodeId as an anonymous user
	 * @param startNodeId Id of the IBPage to start traversing down the tree
	 * @param userId the user who requests it, -1 if no user is available
	 * @return ICTreeNode for the page requested
	 * @throws RemoteException
	 */
	public ICTreeNode getPageTree(int startNodeId)throws RemoteException;
	

	/**
	 * Gets pageKey for the for the page with the given requestUri 
	 * @param requestUri for the page request (e.g. '/pages/1234')
	 * @param serverName the serverName (DNS) name of the request for the server
	 * @return the string representation of the key
	 * @throws RemoteException
	 */	
	public String getPageKeyByRequestURIAndServerName(String requestUri,String serverName);
	
	/**
	 * Gets a copy of a UIComponent by its instanceId (component.getId()) if it is found in the current pages ibxml
	 * @param component
	 * @return A reset copy of the component from ibxml
	 */
	public UIComponent getCopyOfUIComponentFromIBXML(UIComponent component);
	
	/**
	 * Unloads all the resources associated with the Builder
	 */
	public void unload();
	
	/**
	 * DRAFT OF METHODS TO BE IN THIS CLASS:
	 * 
	 * public IBPage getPublicRootPage();
	 * public IBPage getUserHomePage(ICUser user);
	 * public IBPage getGroupHomePage(ICGroup group);
	 * 
	 * public IBPage createPageUnderPublicRoot(ICUser creator,String name);
	 * public IBPage createPageUnderUserHome(ICUser creator,String name);
	 * public IBPage createPageUnderGroupHome(ICUser creator,ICGroup group,String name);
	 * 
	 * public IBPage createPageUnderPage(ICUser creator,IBPage parentPage,String name);
	 * 
	 * public IBPage createPageUnderPublicRoot(ICUser creator,IBPage template,String name);
	 * public IBPage createPageUnderUserHome(ICUser creator,IBPage template,String name);
	 * public IBPage createPageUnderGroupHome(ICUser creator,ICGroup group, IBPage template,String name);
	 * 
	 * public IBPage createPageUnderPage(ICUser creator,IBPage parentPage,IBPage template,String name);
	 * 
	 * public void deletePage(IBPage page,ICUser committer);
	 * public void movePageUnder(IBPage thePageToMove,IBPage oldParent,IBPage newParent,ICUser committer);
	 */
	
	/**
	 * Move a page to another parent e.g. when saving the page structure after moving (drag & drop) tree nodes
	 * @param newParentId The id of the new parent page
	 * @param pageId The id of the page to move
	 */
	public boolean movePage(int newParentId, int pageId);
	
	/**
	 * Changes the default locale name for the page to the new value
	 * @param ID of the page in the database
	 * @param newName the default local name
	 * @return true if successful
	 */
	public boolean changePageName(int ID, String newName);
	
	public Collection getTopLevelPages(IWContext iwc);
	
	public Collection getTopLevelTemplates(IWContext iwc);
}
