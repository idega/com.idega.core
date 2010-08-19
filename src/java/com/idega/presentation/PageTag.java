/*
 * $Id: PageTag.java,v 1.7 2009/01/14 15:12:24 tryggvil Exp $
 * Created on 17.01.2005 by Tryggvi Larusson
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.util.Iterator;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;

import com.idega.util.CoreConstants;

/**
 * <p>
 * This is a JSP tag for the Page component.
 * </p>
 * Last modified: $Date: 2009/01/14 15:12:24 $ by $Author: tryggvil $
 *
 * @author tryggvil
 * @version $Revision: 1.7 $
 */
public class PageTag extends ComponentTag {

	private Object id;
	private Object javascripturls;
	private Object stylesheeturls;

	private String onload;
	private String styleClass;
	private String type;

	private boolean logIds;

	@Override
	public String getRendererType() {
		return null;
	}

	@Override
	public String getComponentType() {
		return "Page";
	}

	@Override
	public void release() {
		this.id = null;
		this.javascripturls = null;
		this.stylesheeturls = null;

		this.onload = null;
		this.styleClass = null;
		this.type = null;
	}

	@Override
	protected void setProperties(UIComponent component) {
		if (component instanceof Page) {
			Page page = (Page) component;

			super.setProperties(component);

			String id = getId();
			if (id != null) {
				page.setId(id);
			}
			page.setJavascriptURLs(getValue(javascripturls));
			page.setStyleSheetURL(getValue(stylesheeturls));

			page.setOnLoad(getOnload());
			page.setStyleClass(getStyleClass());
		}
	}

	@Override
	public int doEndTag() throws JspException {
		FacesContext context = getFacesContext();
		UIComponent instance = getComponentInstance();
		int theReturn = super.doEndTag();
		logIds(instance,context,CoreConstants.EMPTY);
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
				System.out.println(prefix+"ComponentClass="+componentInstance.getClass()+":id="+componentInstance.getId()+":clientId="+
						componentInstance.getClientId(context));
				for (Iterator<UIComponent> facetsAndChildren = componentInstance.getFacetsAndChildren(); facetsAndChildren.hasNext();) {
					UIComponent component = facetsAndChildren.next();
					logIds(component,context," - "+prefix);
				}
			}
		}
	}

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	/**
	 * @return Returns the logIds.
	 */
	public boolean getLogIds() {
		return this.logIds;
	}

	/**
	 * @param logIds The logIds to set.
	 */
	public void setLogIds(boolean logIds) {
		this.logIds = logIds;
	}

	public void setJavascripturls(ValueExpression javascripturls) {
		this.javascripturls = javascripturls;
	}
	public void setJavascripturls(String javascripturls) {
		this.javascripturls = javascripturls;
	}
	public void setJavascripturls(Object javascripturls) {
		this.javascripturls = javascripturls;
	}

	public void setStylesheeturls(ValueExpression stylesheeturls) {
		this.stylesheeturls = stylesheeturls;
	}
	public void setStylesheeturls(String stylesheeturls) {
		this.stylesheeturls = stylesheeturls;
	}
	public void setStylesheeturls(Object stylesheeturls) {
		this.stylesheeturls = stylesheeturls;
	}

	public String getOnload() {
		return onload;
	}

	public void setOnload(String onload) {
		this.onload = onload;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setId(ValueExpression id) {
		this.id = id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return getValue(id);
	}
}