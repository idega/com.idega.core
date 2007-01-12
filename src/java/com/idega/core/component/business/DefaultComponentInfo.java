/*
 * $Id: DefaultComponentInfo.java,v 1.2.2.1 2007/01/12 19:32:50 idegaweb Exp $
 * Created on 8.9.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * <p>
 * TODO tryggvil Describe Type UIComponentInfo
 * </p>
 *  Last modified: $Date: 2007/01/12 19:32:50 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2.2.1 $
 */
public class DefaultComponentInfo implements ComponentInfo {

	private Class componentClass;
	private String componentName;
	private String componentType;
	private String bundleIdentifier;
	private List properties;
	private List componentPermissions;
	
	/**
	 * 
	 */
	public DefaultComponentInfo(Class componentClass, String componentName,String componentType) {
		setComponentClass(componentClass);
		setComponentName(componentName);
		setComponentType(componentType);
	}

	
	/**
	 * @return Returns the bundleIdentifier.
	 */
	public String getBundleIdentifier() {
		return this.bundleIdentifier;
	}


	
	/**
	 * @param bundleIdentifier The bundleIdentifier to set.
	 */
	public void setBundleIdentifier(String bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
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


	
	/**
	 * @return Returns the componentName.
	 */
	public String getComponentName() {
		return this.componentName;
	}


	
	/**
	 * @param componentName The componentName to set.
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}


	
	/**
	 * @return Returns the componentType.
	 */
	public String getComponentType() {
		return this.componentType;
	}


	
	/**
	 * @param componentType The componentType to set.
	 */
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}


	
	/**
	 * @return Returns the properties.
	 */
	public List getProperties() {
		return this.properties;
	}


	
	/**
	 * @param properties The properties to set.
	 */
	public void setProperties(List properties) {
		this.properties = properties;
	}



	/* (non-Javadoc)
	 * @see com.idega.core.component.business.ComponentInfo#getComponentName(java.util.Locale)
	 */
	public String getComponentName(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public List getComponentPermissions(){
		if(this.componentPermissions==null){
			this.componentPermissions=new ArrayList();
		}
		return this.componentPermissions;
	}
}
