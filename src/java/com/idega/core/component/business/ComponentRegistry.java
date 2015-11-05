/*
 * $Id: ComponentRegistry.java,v 1.17 2008/11/05 16:39:41 laddi Exp $ Created on 8.9.2005
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.RemoveException;
import javax.servlet.ServletContext;

import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectBMPBean;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWModuleLoader;

/**
 * <p>
 * This class holds a registry of all components available to idegaWeb.<br/>
 * This means user interface components (such as Elements,Blocks, JSF UIComponents and JSP tags) but also
 * non UI components such as business beans, JSF Managed beans etc.
 * </p>
 * Last modified: $Date: 2008/11/05 16:39:41 $ by $Author: laddi $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.17 $
 */
public class ComponentRegistry {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public final static String BEAN_KEY = "ComponentRegistry";
	private List<ComponentInfo> allComponents;
	private boolean loadedOldIWComponents=false;
	private boolean loadedFacesConfig=false;
	private boolean initializedModuleLoader=false;
	private IWMainApplication iwma;
	@SuppressWarnings("unused")
	private ServletContext context;
	public static final String COMPONENT_TYPE_ELEMENT = ICObjectBMPBean.COMPONENT_TYPE_ELEMENT;
	public static final String COMPONENT_TYPE_BLOCK = ICObjectBMPBean.COMPONENT_TYPE_BLOCK;
	public static final String COMPONENT_TYPE_JSF_UICOMPONENT = ICObjectBMPBean.COMPONENT_TYPE_JSFUICOMPONENT;

	/**
	 * @param iwma
	 *
	 */
	public ComponentRegistry(IWMainApplication iwma,ServletContext context) {
		this.iwma=iwma;
		this.context=context;
		initialize();
	}

	public static ComponentRegistry loadRegistry(IWMainApplication iwma, ServletContext context) {
		//IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		ComponentRegistry registry = null;
		//if (registry != null) {
			registry = new ComponentRegistry(iwma,context);
			//registry.getAllComponents();
			iwma.setAttribute(BEAN_KEY, registry);
		//}
		return registry;
	}

	public static ComponentRegistry getInstance(IWMainApplication iwma) {
		//IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		ComponentRegistry registry = (ComponentRegistry) iwma.getAttribute(BEAN_KEY);
		if (registry == null) {
			throw new RuntimeException("ComponentRegistry not initialized");
			//registry = new ComponentRegistry(iwma);
		}
		return registry;
	}

	public void initialize(){
		this.initializeModuleLoader();
	}

	public List<ComponentInfo> getAllComponents(){
		//this method sees to it to load first all components:
		loadOldIWComponents();
		loadFacesConfig();

		return internalGetComponentList();

	}

	/**
	 * <p>
	 * TODO tryggvil describe method loadFacesConfig
	 * </p>
	 */
	private void loadFacesConfig() {
		if (!this.loadedFacesConfig) {
			this.loadedFacesConfig=true;
			IWModuleLoader loader = initializeModuleLoader();
			loader.loadBundlesFromJars();

		}
	}

	private IWModuleLoader initializeModuleLoader() {

		if (!this.initializedModuleLoader) {
			//IWModuleLoader loader = new IWModuleLoader(this.iwma,this.context);
			IWModuleLoader loader = this.iwma.getModuleLoader();
			loader.getJarLoaders().add(new FacesConfigDeployer(this));
			initializedModuleLoader=true;
			return loader;
		}
		return this.iwma.getModuleLoader();
	}

	private List<ComponentInfo> internalGetComponentList() {
		if (this.allComponents == null) {
			this.allComponents = new ArrayList<ComponentInfo>();
		}
		return this.allComponents;
	}

	public void registerComponent(ComponentInfo info) {
		internalGetComponentList().add(info);
	}

	/**
	 * <p>
	 * Gets a list of all components with the objectType of type
	 * </p>
	 * @param type
	 * @return
	 */
	public List<ComponentInfo> getComponentsByType(String type) {
		List<ComponentInfo> componentList = getAllComponents();
		List<ComponentInfo> list = new ArrayList<ComponentInfo>();
		for (Iterator<ComponentInfo> iter = componentList.iterator(); iter.hasNext();) {
			ComponentInfo component = iter.next();
			if (component.getObjectType().equals(type)) {
				list.add(component);
			}
		}
		return list;
	}

	private void loadOldIWComponents() {
		if (!this.loadedOldIWComponents) {
			// this is so that the components list is loaded lazily
			try {
				ICObjectHome icoHome = getICObjectHome();
				Collection<ICObject> objects = icoHome.findAll();
				for (Iterator<ICObject> iter = objects.iterator(); iter.hasNext();) {
					ICObject component = iter.next();
					registerComponent(component);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			this.loadedOldIWComponents = true;
		}
	}

	protected ICObjectHome getICObjectHome(){
		ICObjectHome icoHome;
		try {
			icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
		}
		catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
		return icoHome;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method registerComponent
	 * </p>
	 * @param ico
	 */
	protected ComponentInfo registerComponent(ICObject ico) {
		try {
			ICObjectComponentInfo info = new ICObjectComponentInfo(ico);
			registerComponent(info);
			return info;
		}
		catch (ClassNotFoundException e) {
			logger.warning("[ComponentRegistry] Class not found : "+ico.getClassName());
			if (!hasReferenceToICObjectInstance(ico)) {
				try {
					ico.remove();
				}
				catch (RemoveException e1) {
					e1.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			//e.printStackTrace();
			logger.log(Level.SEVERE, "ComponentRegistry Error: "+e.getClass().getName(), e);
		}
		return null;
	}

	private boolean hasReferenceToICObjectInstance(ICObject ico) {
		ICObjectInstanceHome home = (ICObjectInstanceHome) IDOLookup.getHomeLegacy(ICObjectInstance.class);
		try {
			return home.getCountByICObject(ico) > 0;
		}
		catch (IDOException ie) {
			ie.printStackTrace();
			return false;
		}
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getComponentByClassName
	 * </p>
	 * @param componentClass
	 * @return
	 */
	public ComponentInfo getComponentByClassName(String componentClassName) {
		List<ComponentInfo> componentList = getAllComponents();
		for (Iterator<ComponentInfo> iter = componentList.iterator(); iter.hasNext();) {
			ComponentInfo info = iter.next();
			if(info.getComponentClass().getName().equals(componentClassName)){
				return info;
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Registers a new component into the registry and so it is persistent in the database.
	 * </p>
	 * @param componentClass
	 * @param componentType
	 * @param objectType
	 * @param bundleIdentifier
	 * @return
	 */
	public ComponentInfo registerComponentPersistent(String name,String componentClass, String componentType, String objectType, String bundleIdentifier) {
		try{
			ICObjectHome icoHome = getICObjectHome();
			ICObject ico = icoHome.create();
			ico.setObjectType(objectType);
			ico.setBundleIdentifier(bundleIdentifier);
			ico.setClassName(componentClass);
			ico.setName(name);
			ico.store();

			return registerComponent(ico);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}