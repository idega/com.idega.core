/*
 * $Id: PageTag.java,v 1.4 2005/10/10 11:40:04 tryggvil Exp $
 * Created on 17.01.2005 by Tryggvi Larusson
 * 
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import java.util.Iterator;

/**
 * <p>
 * This is a JSP tag for the Page component.
 * </p>
 * Last modified: $Date: 2005/10/10 11:40:04 $ by $Author: tryggvil $
 *
 * @author tryggvil
 * @version $Revision: 1.4 $
 */
public class PageTag extends UIComponentTag {
	
	String urls;
	String onload;
	String styleClass;
	boolean logIds=false;
	
	/**
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		return null;
	}
		
	/**
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		return "Page";
	}
	
	public void setJavascripturls(String urls) {
		this.urls = urls;
	}
	
	public void setOnload(String onload) {
		this.onload = onload;
	}
	
	public void release() {
		urls = null;
		onload = null;
	}

	public void setStyleClass(String styleClass){
		this.styleClass=styleClass;
	}
	
	protected void setProperties(UIComponent component) {      
		super.setProperties(component);
		if (component != null) {
			Page page = (Page) component;
			page.setJavascriptURLs(urls);
			page.setOnLoad(onload);
			if(styleClass!=null){
				page.setStyleClass(styleClass);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		FacesContext context = getFacesContext();
		UIComponent instance = getComponentInstance();
		//logClientIds(instance,context);
		int theReturn = super.doEndTag();
		logIds(instance,context,"");
		return theReturn;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method logClientIds
	 * </p>
	 * @param componentInstance
	 */
	private void logIds(UIComponent componentInstance,FacesContext context,String prefix) {
		if(getLogIds()){
			if(componentInstance!=null){
				System.out.println(prefix+"ComponentClass="+componentInstance.getClass()+":id="+componentInstance.getId()+":clientId="+componentInstance.getClientId(context));
				Iterator facetsAndChildren = componentInstance.getFacetsAndChildren();
				while(facetsAndChildren.hasNext()){
					UIComponent component = (UIComponent) facetsAndChildren.next();
					logIds(component,context," - "+prefix);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		// TODO Auto-generated method stub
		return super.doStartTag();
	}

	
	/**
	 * @return Returns the logIds.
	 */
	public boolean getLogIds() {
		return logIds;
	}

	
	/**
	 * @param logIds The logIds to set.
	 */
	public void setLogIds(boolean logIds) {
		this.logIds = logIds;
	}
	
}
