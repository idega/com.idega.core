/*
 * $Id: UIComponentInfo.java,v 1.1 2005/09/20 15:36:49 tryggvil Exp $
 * Created on 20.9.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.business;


/**
 * <p>
 * ComponentInfo implementation for JSF UIComponents.
 * </p>
 *  Last modified: $Date: 2005/09/20 15:36:49 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class UIComponentInfo extends DefaultComponentInfo {

	/**
	 * @param componentClass
	 * @param componentName
	 * @param componentType
	 */
	public UIComponentInfo(Class componentClass, String componentName, String componentType) {
		super(componentClass, componentName, ComponentRegistry.COMPONENT_TYPE_JSF_UICOMPONENT);
	}
}
