/*
 * $Id: FacesComponentTypeHandler.java,v 1.1 2006/06/15 17:53:23 tryggvil Exp $ Created on
 * 18.5.2005 in project com.idega.core
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.idegaweb;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * <p>
 * This is a class to make the "createComponent" method in the standard JSF
 * Application class extendable and pluggable.<br>
 * The IWMainApplication has the method registerFacesComponentTypeHandler() to
 * register an instance of this class.<br>
 * The instances are then asked to create a compoment if an instance of this
 * class matches the componentType in the createComponent() method in the
 * extended IWMainApplication class.
 * </p>
 * Last modified: $Date: 2006/06/15 17:53:23 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface FacesComponentTypeHandler {

	/**
	 * <p>
	 * Returns true if this instance of the handler can create a component with
	 * the given type.
	 * </p>
	 * 
	 * @param componentType
	 * @return
	 */
	public boolean handlesType(String componentType);

	public UIComponent createComponent(String componentType);
	
	public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType);
}