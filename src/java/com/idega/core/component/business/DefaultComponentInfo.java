/*
 * $Id: DefaultComponentInfo.java,v 1.6 2006/05/29 18:15:50 tryggvil Exp $
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
 * Default Implementation of the ComponentInfo interface.
 * </p>
 *  Last modified: $Date: 2006/05/29 18:15:50 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class DefaultComponentInfo implements ComponentInfo {

	private Class componentClass;
	private String componentName;
	private String objectType;
	private String bundleIdentifier;
	private List properties;
	private List componentPermissions;
	private boolean deprecated=false;
	private boolean expert=false;
	private String group;
	
	/**
	 * 
	 */
	public DefaultComponentInfo(Class componentClass, String componentName,String objectType) {
		setComponentClass(componentClass);
		setComponentName(componentName);
		setObjectType(objectType);
	}

	
	/**
	 * @return Returns the bundleIdentifier.
	 */
	public String getModuleIdentifier() {
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
	public String getObjectType() {
		return this.objectType;
	}


	
	/**
	 * @param componentType The componentType to set.
	 */
	public void setObjectType(String componentType) {
		this.objectType = componentType;
	}


	
	/**
	 * @return Returns the properties.
	 */
	public List getProperties() {
		if(this.properties==null){
			this.properties=new ArrayList();
		}
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


	
	/**
	 * @return the deprecated
	 */
	public boolean isDeprecated() {
		return deprecated;
	}


	
	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}


	
	/**
	 * @return the expert
	 */
	public boolean isExpert() {
		return expert;
	}


	
	/**
	 * @param expert the expert to set
	 */
	public void setExpert(boolean expert) {
		this.expert = expert;
	}


	
	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}


	
	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
}