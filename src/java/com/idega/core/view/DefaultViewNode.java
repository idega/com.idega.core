/*
 * $Id: DefaultViewNode.java,v 1.2 2004/12/17 14:24:07 tryggvil Exp $
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
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.StringHandler;


/**
 * The default implementation of the ViewNode interface.<br>
 * 
 *  Last modified: $Date: 2004/12/17 14:24:07 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class DefaultViewNode implements ViewNode {

	private String viewId;
	private Map children;
	private ViewHandler viewHandler;
	private boolean isJSP;
	private boolean isCBP;
	private Class componentClass;
	private String jspUri;
	private ViewNode parent;
	private Collection roles;
	private IWMainApplication iwma;
	private static String SLASH="/";
	private static String NODE_SEPARATOR=SLASH;
	
	
	/**
	 * @param iwma
	 */
	public DefaultViewNode(String viewId,ViewNode parent) {
		this.setViewId(viewId);
		setParent(parent);	
	}	
	
	/**
	 * @param iwma
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

	private Map getChildrenMap(){
		if(children==null){
			children=new HashMap();
		}
		return children;
	}
	
	public String getURI(){
		StringBuffer path = new StringBuffer();
		String contextURI = getIWMainApplication().getApplicationContextURI();
		
		ViewNode view = this;
		while(view!=null){
			String viewId = view.getViewId();
			if(!viewId.equals(NODE_SEPARATOR)){
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

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getChild(java.lang.String)
	 */
	public ViewNode getChild(String childViewId) {
		if(childViewId!=null){
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
	public boolean isJSP() {
		return isJSP;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#isCBP()
	 */
	public boolean isCBP() {
		return isCBP;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getJSPURI()
	 */
	public String getJSPURI() {
		return jspUri;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getComponentClass()
	 */
	public Class getComponentClass() {
		return componentClass;
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
		return this.getViewId();
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getKeyboardShortcut()
	 */
	public KeyboardShortcut getKeyboardShortcut() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.faces.view.ViewNode#getToolTip(java.util.Locale)
	 */
	public ToolTip getToolTip(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @param componentClass The componentClass to set.
	 */
	public void setComponentClass(Class componentClass) {
		this.setCBP(true);
		this.componentClass = componentClass;
	}
	/**
	 * @param isCBP The isCBP to set.
	 */
	public void setCBP(boolean isCBP) {
		this.isCBP = isCBP;
	}
	/**
	 * @param isJSP The isJSP to set.
	 */
	public void setJSP(boolean isJSP) {
		this.isJSP = isJSP;
	}
	/**
	 * @param jspUri The jspUri to set.
	 */
	public void setJspUri(String jspUri) {
		this.setJSP(true);
		this.jspUri = jspUri;
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
}
