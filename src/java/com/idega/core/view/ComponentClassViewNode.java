/*
 * $Id: ComponentClassViewNode.java,v 1.2 2006/04/09 12:13:17 laddi Exp $
 * Created on 28.11.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * ViewNode that is 'ComponentBased' and implements the createComponent() method
 * so that it simply creates a new instance of the set UICompoment Class.
 * </p>
 *  Last modified: $Date: 2006/04/09 12:13:17 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class ComponentClassViewNode extends DefaultViewNode {

	private Class componentClass;
	
	/**
	 * @param viewId
	 */
	public ComponentClassViewNode(String viewId) {
		super(viewId);
		setComponentBased(true);
	}

	/**
	 * @param viewId
	 * @param parent
	 */
	public ComponentClassViewNode(String viewId, ViewNode parent) {
		super(viewId, parent);
		setComponentBased(true);
	}

	/**
	 * @param iwma
	 */
	public ComponentClassViewNode(IWMainApplication iwma) {
		super(iwma);
		setComponentBased(true);
	}

	
	/**
	 * @return Returns the componentClass.
	 */
	public Class getComponentClass() {
		return this.componentClass;
	}

	
	/**
	 * @param componentClass The componentClass to set.
	 */
	public void setComponentClass(Class componentClass) {
		this.componentClass = componentClass;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#createComponent(javax.faces.context.FacesContext)
	 */
	public UIComponent createComponent(FacesContext context) {
		Class clazz = getComponentClass();
		if(clazz!=null){
			try {
				return (UIComponent)clazz.newInstance();
			}
			catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("ComponentClass is not set");
	}
}
