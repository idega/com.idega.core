/*
 * $Id: DefaultViewNode.java,v 1.11 2005/10/25 00:24:30 tryggvil Exp $
 * Created on 14.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.StringHandler;


/**
 * The default implementation of the ViewNode interface.<br>
 * 
 *  Last modified: $Date: 2005/10/25 00:24:30 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.11 $
 */
public class DefaultViewNode implements ViewNode {

	private String viewId;
	private Map children;
	private ViewHandler viewHandler;
	private boolean isResourceBased;
	private boolean isComponentBased;
	private String resourceUri;
	private ViewNode parent;
	private Collection roles;
	private IWMainApplication iwma;
	private static String SLASH="/";
	private static String NODE_SEPARATOR=SLASH;
	private boolean isRendered = true;
	private String name;
	private KeyboardShortcut keyboardShortcut;
	
	/**
	 * @param viewId the ViewId of this node (must be unique under its parent)
	 * @param parent the Parent of this node. This node will be added as a child under its parent implicitly by this constructor
	 */
	public DefaultViewNode(String viewId,ViewNode parent) {
		this.setViewId(viewId);
		setParent(parent);	
	}	
	
	/**
	 * @param iwma the IWMainApplication instance to register to this ViewNode
	 */
	public DefaultViewNode(IWMainApplication iwma) {
		this.setIWMainApplication(iwma);
	}
	
	public String getViewId() {
		return viewId;
	}
	
	public void setViewId(String viewId){
		this.viewId=viewId;
	}

	protected Map getChildrenMap(){
		if(children==null){
			children=new HashMap();
		}
		return children;
	}
	
	public String getURI(){
		StringBuffer path = new StringBuffer(NODE_SEPARATOR);
		String contextURI = getIWMainApplication().getApplicationContextURI();
		
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
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getChildren()
	 */
	public Collection getChildren() {
		return getChildrenMap().values();
	}

	/**
	 * This method retorns a child that is a direct child of this instance (not a child of a child).
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
							if(firstSplit[2].equals("")){
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
		if(viewHandler==null){
			ViewNode parent = getParent();
			if(parent!=null){
				return parent.getViewHandler();
			}
		}
		return viewHandler;
	}

	public void setViewHandler(ViewHandler viewHandler) {
		this.viewHandler=viewHandler;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#isJSP()
	 */
	public boolean isResourceBased() {
		return isResourceBased;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#isCBP()
	 */
	public boolean isComponentBased() {
		return isComponentBased;
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
		//return componentClass;
		return null;
	}


	/**
	 * If no special roles are set then this method returns 
	 * the roles from the parent ViewNode
	 */
	public Collection getAuthorizedRoles() {
		if(roles==null){
			ViewNode parent = getParent();
			if(parent!=null){
				return parent.getAuthorizedRoles();
			}
		}
		return roles;
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
			return name;
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
		return keyboardShortcut;
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
	 */
	public void setComponentBased(boolean isCBP) {
		this.isComponentBased = isCBP;
	}
	/**
	 * @param isJSP The isJSP to set.
	 */
	public void setResourceBased(boolean isJSP) {
		this.isResourceBased = isJSP;
	}
	/**
	 * @param jspUri The jspUri to set.
	 */
	public void setJspUri(String jspUri) {
		this.setResourceBased(true);
		this.resourceUri = jspUri;
	}
	/**
	 * @return Returns the parent.
	 */
	public ViewNode getParent() {
		return parent;
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
			if(iwma==null){
				DefaultViewNode parent = (DefaultViewNode)getParent();
				if(parent!=null){
					return parent.getIWMainApplication();
				}
			}
			return iwma;
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
		return isRendered;
	}
	/**
	 * @param isRendered The isRendered to set.
	 */
	public void setVisibleInMenus(boolean isRendered) {
		this.isRendered = isRendered;
	}
}
