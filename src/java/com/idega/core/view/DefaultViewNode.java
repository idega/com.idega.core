/*
 * $Id: DefaultViewNode.java,v 1.20 2008/02/20 18:18:15 eiki Exp $
 * Created on 14.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.util.StringHandler;


/**
 * The default implementation of the ViewNode interface.<br>
 * 
 *  Last modified: $Date: 2008/02/20 18:18:15 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.20 $
 */
public class DefaultViewNode implements ViewNode {

	private String viewId;
	private Map children;
	private ViewHandler viewHandler;
	private String resourceUri;
	private ViewNode parent;
	private Collection roles;
	private IWMainApplication iwma;
	protected static String SLASH="/";
	static String NODE_SEPARATOR=SLASH;
	private boolean isRendered = true;
	private String name;
	private KeyboardShortcut keyboardShortcut;
	private boolean redirectsToResourceUri=false;
	
	private ViewNodeBase viewNodeBase = ViewNodeBase.UNSPECIFIED;
	
	/**
	 * @param viewId the ViewId of this node (must be unique under its parent)
	 */
	public DefaultViewNode(String viewId) {
		setViewId(viewId);
	}	
	
	/**
	 * @param viewId the ViewId of this node (must be unique under its parent)
	 * @param parent the Parent of this node. This node will be added as a child under its parent implicitly by this constructor
	 */
	public DefaultViewNode(String viewId,ViewNode parent) {
		this(viewId);
		setParent(parent);	
	}	
	
	/**
	 * @param iwma the IWMainApplication instance to register to this ViewNode
	 */
	public DefaultViewNode(IWMainApplication iwma) {
		this.setIWMainApplication(iwma);
	}
	
	public String getViewId() {
		return this.viewId;
	}
	
	public void setViewId(String viewId){
		this.viewId=viewId;
	}

	protected Map getChildrenMap(){
		if(this.children==null){
			this.children=new LinkedHashMap();
		}
		return this.children;
	}
	
	/**
	 * Returns the primary URI up the tree hierarchy and does NOT include the webapplications context path if any.
	 */
	public String getURI(){
		StringBuffer path = new StringBuffer(NODE_SEPARATOR);
		
		ViewNode view = this;
		while(view!=null){
			String viewId = view.getViewId();
			if(!viewId.equals(NODE_SEPARATOR)){
				//path.insert(0,NODE_SEPARATOR);
				path.insert(0,view.getViewId());
				path.insert(0,NODE_SEPARATOR);
			}
			view = view.getParent();
		}
		return path.toString();
	}
	/**
	 * Returns the primary URI up the tree hierarchy and includes the webapplications context path if any.
	 */
	public String getURIWithContextPath(){
		StringBuffer path = new StringBuffer(getURI());
		String contextURI = getIWMainApplication().getApplicationContextURI();
		if(contextURI != null ){
			if(!contextURI.equals(SLASH)){
				path.insert(0,contextURI);
			}
		}
		
		return path.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#addChildViewNode(com.idega.faces.view.ViewNode)
	 */
	public void addChildViewNode(ViewNode node) {
		getChildrenMap().put(node.getViewId(),node);
		//node.setParent(this);
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getChildren()
	 */
	public Collection getChildren() {
		return getChildrenMap().values();
	}

	/**
	 * This method returns a child that is a direct child of this instance (not a child of a child).
	 * This method assumes that childId does not contain the / (SLASH) character.
	 * @param childId
	 * @return
	 */
	protected ViewNode getDirectChild(String realChildId){
		ViewNode theReturn = (ViewNode)getChildrenMap().get(realChildId);
		if(theReturn==null){
			theReturn = loadChild(realChildId);
			if(theReturn!=null){
				getChildrenMap().put(realChildId,theReturn);	
				return theReturn;
			}
		}
		else{
			return theReturn;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getChild(java.lang.String)
	 */
	public ViewNode getChild(String childViewId) {
		
		if(childViewId!=null){
			String realChildId=childViewId;
			String childrenOfChildId = null;
			if(childViewId.indexOf(SLASH)!=-1){
				String[] childArray = parseChildren(childViewId);
				realChildId=childArray[0];
				if(childArray.length==2){
					//In this case the '/' character must have been somewhere in the middle of the string
					//If however this is NOT case the '/' must have been in the beginning of the string, and thus only one element in the array.
					childrenOfChildId = childArray[1];
				}
			}
			ViewNode directChild = getDirectChild(realChildId);
			if(directChild!=null){
				if(childrenOfChildId!=null){
					return directChild.getChild(childrenOfChildId);
				}
				else{
					return directChild;
				}
			}
			
		}
		return this;
		
		/*
		if(newChildViewId.equals(StringHandler.SLASH)){
			return this;
		}
		 elseif(childViewId!=null){
			int slashIndex = childViewId.indexOf(StringHandler.SLASH);
			if(childViewId.equals(StringHandler.SLASH)){
				return this;
			}
			else if (slashIndex!=-1){
					//String[] split = StringHandler.breakDownURL(childViewId);
					String[] split;
					if(slashIndex==0){
						//This case is when the '/' is in the beginning of the string.
						//This results that the array returned out of s.split(s,i) returns an empty string as the first result:
						String[] firstSplit = split= childViewId.split(StringHandler.SLASH,3);
						//So we shift the results that we want
						if(firstSplit.length==3){
							
							if(firstSplit[2].equals("")){
//								In this case there are empty strings in indexes 0 and 2
								split = new String[1];
								split[0]=firstSplit[1];
							}
							else{
								split = new String[2];
								split[0]=firstSplit[1];
								split[1]=firstSplit[2];
							}
						}
						else if(firstSplit.length==2){
							split = new String[1];
							split[0]=firstSplit[1];
						}
						else{
							split = new String[2];
							split[0]=firstSplit[1];
							split[1]=firstSplit[2];
						}
					}
					else{
						split= childViewId.split(StringHandler.SLASH,2);
					}
					
					int length = split.length;
					
					if(length==2){
						//In this case the '/' character must have been somewhere in the middle of the string
						String prefix = split[0];
						String rest = split[1];
						ViewNode child = (ViewNode)getChildrenMap().get(prefix);
						if(child==null){
							//Instead of returning null we return this if no chid is found
							return this;
						}
						else{
							return child.getChild(rest);
						}
					}
					else if(length==1){
						String prefix = split[0];
						//In this case the '/' must have been in the beginning of the string
						ViewNode theReturn = (ViewNode)getChildrenMap().get(prefix);
						if(theReturn==null){
							//Instead of returning null we return this if no chid is found
							theReturn = this;
							
						}
						return theReturn;
					}
					
			}
			else{
				ViewNode child = (ViewNode)getChildrenMap().get(childViewId);
				if(child==null){
					//Instead of returning null we return this.
					child=this;
				}
				return child;
			}
		}
		*/
	}
	
	
	/**
	 * This method is called if a node instance is not found in the map.
	 * This can be overrided in suclasses and can be a relatively expensive operation, 
	 * therefore is only called the first time (lazy-loading) when getting the child and after that accessed from the map instance variable.
	 */
	protected ViewNode loadChild(String childId){
		return null;
	}
	
	/**
	 * This method parses the childViewId with the first separating slash found.<br>
	 * This method should return either an array of size 1 or 2 depending on if the '/' is in the middle of the string or in the beginning.
	 * @param childViewId
	 * @return
	 */
	protected String[] parseChildren(String childViewId){
		
		
		if(childViewId!=null){
			int slashIndex = childViewId.indexOf(StringHandler.SLASH);
			//if(childViewId.equals(StringHandler.SLASH)){
			//	return this;
			//}
			if (slashIndex!=-1){
					//String[] split = StringHandler.breakDownURL(childViewId);
					String[] split;
					if(slashIndex==0){
						//This case is when the '/' is in the beginning of the string.
						//This results that the array returned out of s.split(s,i) returns an empty string as the first result:
						String[] firstSplit = split= childViewId.split(StringHandler.SLASH,3);
						//So we shift the results that we want
						if(firstSplit.length==3){
							if(firstSplit[2].equals(StringHandler.EMPTY_STRING)){
								//In this case there are empty strings in indexes 0 and 2
								split = new String[1];
								split[0]=firstSplit[1];
							}
							else{
								split = new String[2];
								split[0]=firstSplit[1];
								split[1]=firstSplit[2];
							}
						}
						else if(firstSplit.length==2){
							split = new String[1];
							split[0]=firstSplit[1];
						}
						else{
							split = new String[2];
							split[0]=firstSplit[1];
							split[1]=firstSplit[2];
						}
					}
					else{
						split= childViewId.split(StringHandler.SLASH,2);
					}
					
					//int length = split.length;
					
					
					return split;
					
					/*
					if(length==2){
						//In this case the '/' character must have been somewhere in the middle of the string
						String prefix = split[0];
						String rest = split[1];
						//ViewNode child = (ViewNode)getChildrenMap().get(prefix);
						//if(child==null){
						//	//Instead of returning null we return this if no chid is found
						//	return this;
						//}
						//else{
						//	return child.getChild(rest);
						//}
						
						return prefix;
					}
					else if(length==1){
						String prefix = split[0];
						//In this case the '/' must have been in the beginning of the string
						//ViewNode theReturn = (ViewNode)getChildrenMap().get(prefix);
						//if(theReturn==null){
						//	//Instead of returning null we return this if no chid is found
						//	theReturn = this;
						//	
						//}
						//return theReturn;
						return prefix;
					}*/
			}
		}
		return null;
		
	}

	/**
	 * If no special Viewhandler is set then this method returns 
	 * the ViewHandler from the parent ViewNode
	 */
	public ViewHandler getViewHandler() {
		
		if(this.viewHandler==null){
			ViewNode parent = getParent();
			if(parent!=null){
				return parent.getViewHandler();
			}
		}
		return this.viewHandler;
	}

	public void setViewHandler(ViewHandler viewHandler) {
		this.viewHandler=viewHandler;
	}
	
	public ViewNodeBase getViewNodeBase() {
		return viewNodeBase;
	}
	
	public void setViewNodeBase(ViewNodeBase view_node_base) {
		this.viewNodeBase = view_node_base;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#isCBP()
	 */
	public boolean isComponentBased() {
		return getViewNodeBase() == ViewNodeBase.COMPONENT;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getJSPURI()
	 */
	public String getResourceURI() {
		return resourceUri;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getComponentClass()
	 */
	public UIComponent createComponent(FacesContext context) {
		throw new UnsupportedOperationException("DefaultViewNode.createComponent() not implemented");
	}


	/**
	 * If no special roles are set then this method returns 
	 * the roles from the parent ViewNode
	 */
	public Collection getAuthorizedRoles() {
		if(this.roles==null){
			ViewNode parent = getParent();
			if(parent!=null){
				return parent.getAuthorizedRoles();
			}
		}
		return this.roles;
	}
	
	public void setAuthorizedRoles(Collection coll){
		this.roles=coll;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getIcon()
	 */
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getLocalizedName(java.util.Locale)
	 */
	public String getLocalizedName(Locale locale) {
		return this.getViewId();
	}
	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getName()
	 */
	public String getName() {
		if(this.name==null){
			return StringHandler.firstCharacterToUpperCase(getViewId());
		}
		else{
			return this.name;
		}
	}
	/**
	 * <p>
	 * This method supports setting the name as a JSF ValueBinding (that can be localized)
	 * </p>
	 * @param name
	 */
	public void setName(String name){
		this.name=name;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getKeyboardShortcut()
	 */
	public KeyboardShortcut getKeyboardShortcut() {
		return this.keyboardShortcut;
	}
	
	
	/**
	 * @param keyboardShortcut The keyboardShortcut to set.
	 */
	public void setKeyboardShortcut(KeyboardShortcut keyboardShortcut) {
		this.keyboardShortcut = keyboardShortcut;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getToolTip(java.util.Locale)
	 */
	public ToolTip getToolTip(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param isCBP The isCBP to set.
	 * @deprecated use setViewNodeBase instead
	 */
	@Deprecated
	public void setComponentBased(boolean isCBP) {
		
		if(isCBP)
			viewNodeBase = ViewNodeBase.COMPONENT;
	}
	/**
	 * @param isJSP The isJSP to set.
	 * @deprecated use setViewNodeBase instead
	 */
	@Deprecated
	public void setResourceBased(boolean isJSP) {
		
		if(isJSP)
			viewNodeBase = ViewNodeBase.JSP;
	}
	/**
	 * @param jspUri The jspUri to set.
	 */
	public void setJspUri(String jspUri) {
		setViewNodeBase(ViewNodeBase.JSP);
		resourceUri = jspUri;
	}
	
	public void setFaceletUri(String faceletUri) {
		setViewNodeBase(ViewNodeBase.FACELET);
		resourceUri = faceletUri;
	}
	/**
	 * @return Returns the parent.
	 */
	public ViewNode getParent() {
		return this.parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(ViewNode parent) {
		parent.addChildViewNode(this);
		this.parent = parent;
	}
	/**
	 * If no special IWMainApplication instance is set then this method returns 
	 * the IWMainApplication from the parent ViewNode
	 */
	public IWMainApplication getIWMainApplication() {
			if(this.iwma==null){
				DefaultViewNode parent = (DefaultViewNode)getParent();
				if(parent!=null){
					return parent.getIWMainApplication();
				}
			}
			return this.iwma;
	}
	/**
	 * @param iwma The iwma to set.
	 */
	public void setIWMainApplication(IWMainApplication iwma) {
		this.iwma = iwma;
	}
	/**
	 * @return Returns the isRendered.
	 */
	public boolean isVisibleInMenus() {
		return this.isRendered;
	}
	/**
	 * @param isRendered The isRendered to set.
	 */
	public void setVisibleInMenus(boolean isRendered) {
		this.isRendered = isRendered;
	}

	
	/**
	 * @return Returns the redirectsToResourceUri.
	 */
	public boolean getRedirectsToResourceUri() {
		return this.redirectsToResourceUri;
	}
	
	/**
	 * @param redirectsToResourceUri The redirectsToResourceUri to set.
	 */
	public void setRedirectsToResourceUri(boolean redirectsToResourceUri) {
		this.redirectsToResourceUri = redirectsToResourceUri;
	}

	/**
	 * Override this method to do your own access control for viewnodes, throws NotImplementedException by default
	 */
	public boolean hasUserAccess(IWUserContext iwuc) {
		throw new UnsupportedOperationException();
	}
}
