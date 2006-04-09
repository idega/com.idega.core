/*
 * $Id: ComponentRegistry.java,v 1.2 2006/04/09 12:13:16 laddi Exp $ Created on 8.9.2005
 * in project com.idega.core
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.component.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;

/**
 * <p>
 * This class holds a registry of all components available to idegaWeb.<br/>
 * This means user interface components (such as Elements,Blocks, JSF UIComponents and JSP tags) but also
 * non UI components such as business beans, JSF Managed beans etc.
 * </p>
 * Last modified: $Date: 2006/04/09 12:13:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class ComponentRegistry {

	public final static String BEAN_KEY = "ComponentRegistry";
	private ArrayList allComponents;
	private boolean loadedOldIWComponents;
	public static final String COMPONENT_TYPE_ELEMENT = "iw.element";
	public static final String COMPONENT_TYPE_BLOCK = "iw.block";
	public static final String COMPONENT_TYPE_JSF_UICOMPONENT = "jsf.uicomponent";

	/**
	 * 
	 */
	public ComponentRegistry() {
		super();
	}

	public static ComponentRegistry getInstance() {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		ComponentRegistry registry = (ComponentRegistry) iwma.getAttribute(BEAN_KEY);
		if (registry != null) {
			registry = new ComponentRegistry();
		}
		return registry;
	}
	
	public List getAllComponents(){
		//this method sees to it to load first all components:
		loadOldIWComponents();
		return internalGetComponentList();
	}

	private List internalGetComponentList() {
		if (this.allComponents == null) {
			this.allComponents = new ArrayList();
		}
		return this.allComponents;
	}

	public void registerComponent(ComponentInfo info) {
		internalGetComponentList().add(info);
	}

	public List getComponentsByType(String type) {
		List componentList = getAllComponents();
		ArrayList list = new ArrayList();
		for (Iterator iter = componentList.iterator(); iter.hasNext();) {
			ComponentInfo component = (ComponentInfo) iter.next();
			if (component.getComponentType().equals(type)) {
				list.add(component);
			}
		}
		return list;
	}

	private void loadOldIWComponents() {
		if (!this.loadedOldIWComponents) {
			// this is so that the components list is loaded lazily
			try {
				ICObjectHome icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
				Collection objects = icoHome.findAll();
				for (Iterator iter = objects.iterator(); iter.hasNext();) {
					ICObject component = (ICObject) iter.next();
					registerComponent(component);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			this.loadedOldIWComponents = true;
		}
	}

	private void registerComponent(ICObject ico) {
		try {
			Class clazz = ico.getObjectClass();
			String name = ico.getName();
			String type = ico.getObjectType();
			int icObjectId = ((Integer)ico.getPrimaryKey()).intValue();
			ICObjectComponentInfo info = new ICObjectComponentInfo(clazz, name, type,icObjectId);
			registerComponent(info);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
