/*
 * $Id: PageTag.java,v 1.1 2005/01/18 20:58:29 tryggvil Exp $
 * Created on 17.01.2005 by Tryggvi Larusson
 * 
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import javax.faces.webapp.UIComponentTag;

/**
 * <p>
 * This is a JSP tag for the Page component.
 * </p>
 * Last modified: $Date: 2005/01/18 20:58:29 $ by $Author: tryggvil $
 *
 * @author tryggvil
 * @version $Revision: 1.1 $
 */
public class PageTag extends UIComponentTag {
	
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
}
