/*
 * $Id: PageTag.java,v 1.2 2005/03/08 10:22:32 gimmi Exp $
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
import javax.faces.webapp.UIComponentTag;

/**
 * <p>
 * This is a JSP tag for the Page component.
 * </p>
 * Last modified: $Date: 2005/03/08 10:22:32 $ by $Author: gimmi $
 *
 * @author tryggvil
 * @version $Revision: 1.2 $
 */
public class PageTag extends UIComponentTag {
	
	String urls;
	String onload;
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

	protected void setProperties(UIComponent component) {      
		super.setProperties(component);
		if (component != null) {
			Page page = (Page) component;
			page.setJavascriptURLs(urls);
			page.setOnLoad(onload);
		}
	}	
	
}
