/*
 * $Id: BuilderService.java,v 1.71 2009/01/30 07:33:14 valdas Exp $
 * Created on 8.7.2003
 *
 * Copyright (C) 2003-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.business;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.jdom.Document;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.IBOService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.bean.RenderedComponent;
import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.user.data.User;

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
	 * Gets the URI to a page on this webserver, checks if page is not deleted
	 * @param iwc - {@link IWContext}
	 * @param pageKey an id for a page to get the URI to
	 * @param checkIfDeleted - check if page is deleted or not
	 * @return The string that is the URI to the requested page on this webserver
	 * @throws RemoteException
	 */	
	public String getPageURI(IWContext iwc, String pageKey, boolean checkIfDeleted) throws RemoteException;
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
	public boolean movePage(int newParentId, int pageId, ICDomain domain);
	
	/**
	 * Changes the default locale name for the page to the new value
	 * @param ID of the page in the database
	 * @param newName the default local name
	 * @return true if successful
	 */
	public boolean changePageName(int ID, String newName, IWContext iwc);
	
	public Collection getTopLevelPages(IWContext iwc);
	
	public Collection getTopLevelTemplates(IWContext iwc);
	
	public Map getTree(IWContext iwc);
	
	public String getTemplateKey();
	
	public String getPageKey();
	
	public String getHTMLTemplateKey();
	
	public String getTopLevelTemplateId(Collection templates);
	
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup);
	
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup, String treeOrder);
	
	public int createPageOrTemplateToplevelOrWithParent(String name, String parentId, String type, String templateId, Map tree, IWContext creatorContext);

	public boolean setProperty(String pageKey, String instanceId, String propertyName, String[] propertyValues, IWMainApplication iwma);
	
	public boolean deletePage(String pageId, boolean deleteChildren, Map tree, int userId, ICDomain domain);
	
	public boolean checkDeletePage(String pageId, ICDomain domain);
	
	public ICPage getICPage(String key);
	
	public void clearAllCachedPages();
	
	public void clearAllCaches();
	
	public void setTemplateId(String pageKey, String newTemplateId);
	
	public String getIBXMLFormat();
	
	public String[] getPropertyValues(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] selectedValues, boolean returnSelectedValueIfNothingFound);
	
	public boolean removeProperty(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] values);
	
	public boolean changePageUriByTitle(String parentId, ICPage page, String pageTitle, int domainId);
	
	public boolean movePageToTopLevel(int pageID, IWContext iwc);
	
	public void createTopLevelPageFromExistingPage(int pageID, ICDomain domain, IWUserContext creatorContext);
	
	public boolean isPageTopLevelPage(int pageID, ICDomain domain);
	
	public boolean unlockRegion(String pageKey, String parentObjectInstanceID, String label);
	
	public void setCurrentPageId(IWContext iwc, String pageKey);
	
	public boolean addPropertyToModule(String pageKey, String moduleId, String propName, String propValue);
	
	public boolean addPropertyToModules(String pageKey, List<String> moduleIds, String propName, String propValue);
	
	public List<String> getModuleId(String pageKey, String moduleClass);
	
	public boolean isPropertySet(String pageKey, String instanceId, String propertyName, IWMainApplication iwma);
	
	public boolean isPropertyValueSet(String pageKey, String moduleId, String propertyName, String propertyValue);
	
	public boolean removeValueFromModuleProperty(String pageKey, String moduleId, String propertyName, String valueToRemove);
	
	public boolean removeValueFromModulesProperties(String pageKey, List<String> moduleIds, String propertyName, String valueToRemove);
	
	public void setTreeOrder(int id, int order);
	
	public int getTreeOrder(int id);
	
	public void changeTreeOrder(int pageId, int change);
	
	public int setAsLastInLevel(boolean isTopLevel, String parentId);
	
	public String getProperty(String pageKey, String instanceId, String propertyName);
	
	public String getPageKeyByURI(String requestURI);
	
	public String getExistingPageKeyByURI(String requestURI);
	
	public Document getRenderedModule(String pageKey, String componentId, boolean cleanHtml);
	
	public Document getRenderedComponent(IWContext iwc, UIComponent component, boolean cleanHtml);
	
	public Document getRenderedComponent(IWContext iwc, UIComponent component, boolean cleanHtml, boolean omitDocTypeEnvelope, boolean omitHtmlEnvelope);
	
	public String getRenderedComponent(UIComponent component, IWContext iwc, boolean cleanHtml);
	
	public String getRenderedComponent(UIComponent component, IWContext iwc, boolean cleanHtml, boolean omitDocTypeEnvelope, boolean omitHtmlEnvelope);
	
	public boolean removeBlockObjectFromCacheByCacheKey(String cacheKey);
	
	public boolean removeBlockObjectFromCache(IWContext iwc, String cacheKey);
	
	public void startBuilderSession(IWContext iwc);
	
	/**
	 * renames region element id and label attributes
	 * 
	 * @param pageKey - page the region to be found 
	 * @param region_id - current
	 * @param region_label - current. if not provided, region_id will be used as a region_label
	 * @param new_region_id - new id
	 * @param new_region_label - new label. if not provided, new_region_id will be used as a new_region_label
	 */
	public abstract void renameRegion(String pageKey, String region_id, String region_label, String new_region_id, String new_region_label);
	
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] properties);
	
	public boolean setLocalizedText(String moduleId, String text);
	
	public boolean removeAllBlockObjectsFromCache(IWContext iwc);
	
	/**
	 * Creates path (uri) based on current time
	 * @return
	 */
	public String getYearMonthPath();
	
	public String generateResourcePath(String base, String scope, String fileName);
	
	public boolean setProperty(IWContext iwc, String pageKey, String instanceId, String propertyName, List<AdvancedProperty> properties);

	public Map getTree(IWApplicationContext iwac);
	
	public boolean setPageUri(ICPage page, String pageUri, int domainId);
	
	public String getClassNameForSourceView();
	
	public String getInstanceId(UIComponent component);
	
	public UIComponent findComponentInPage(IWContext iwc, String pageKey, String instanceId);
	
	public String addNewModule(String pageKey, String parentObjectInstanceID, String regionId, int newICObjectID, String label);
	
	public int getICObjectId(String objectClass);
	
	public boolean existsRegion(String pageKey, String label, String regionId);
	
	public boolean copyAllModulesFromRegionIntoRegion(String pageKey, String sourceRegionLabel, String destinationRegionId, String destinationRegionLabel);
	
	public String getUriToObject(Class<?> objectClass, List<AdvancedProperty> parameters);
	
	public boolean isFirstBuilderRun();
	
	public boolean reloadGroupsInCachedDomain(IWApplicationContext iwac, String serverName);
	
	public String getCleanedHtmlContent(String htmlContent, boolean omitDocTypeDeclaration, boolean omitHtmlEnvelope, boolean omitComments);
	
	public String getCleanedHtmlContent(InputStream htmlStream, boolean omitDocTypeDeclaration, boolean omitHtmlEnvelope, boolean omitComments);
	
	public String getFullPageUrlByPageType(IWContext iwc, String pageType, boolean checkFirstlyNearestPages);
	
	public String getFullPageUrlByPageType(User currentUser, String pageType, boolean checkFirstlyNearestPages);
	
	public abstract String getFullPageUrlByPageType(User user, IWContext iwc, String pageType, boolean checkFirstlyNearestPages);
	
	public ICPage getNearestPageForCurrentPageByPageType(IWContext iwc, String pageType);
	
	public String getLocalizedPageName(String pageKey, Locale locale);

	public RenderedComponent getRenderedComponentById(String uuid, String uri, List<AdvancedProperty> properties);
	
	public RenderedComponent getRenderedComponentByClassName(String className, List<AdvancedProperty> properties);
	
	public String getUriToPagePermissionsWindow(List<AdvancedProperty> parameters);
	
	public String getUriToPagePropertiesWindow(List<AdvancedProperty> parameters);
	
	public abstract ICPage getNearestPageForUserHomePageOrCurrentPageByPageType(User currentUser, IWContext iwc, String pageType);
	
	public List<com.idega.core.component.business.ComponentProperty> getComponentProperties(IWContext iwc, String instanceId);
}