/*
 * $Id: DefaultComponentProperty.java,v 1.2 2006/05/10 08:27:16 laddi Exp $
 * Created on 25.4.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.business;

import java.util.Locale;
import java.util.Map;


/**
 * <p>
 * Default implementation of the ComponentProperty
 * </p>
 *  Last modified: $Date: 2006/05/10 08:27:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class DefaultComponentProperty implements ComponentProperty {

	private ComponentInfo componentInfo;
	
	private String name;
	private String className;
	private String description;
	private String displayName;
	private String icon;
	private String suggestedValue;
	private Map extensions;
	

	/**
	 * @param info
	 */
	public DefaultComponentProperty(ComponentInfo info) {
		setComponentInfo(info);
	}


	/* (non-Javadoc)
	 * @see com.idega.core.component.business.ComponentProperty#getName(java.util.Locale)
	 */
	public String getName(Locale locale) {
		// TODO Auto-generated method stub
		return getName();
	}


	/* (non-Javadoc)
	 * @see com.idega.core.component.business.ComponentProperty#getDescription(java.util.Locale)
	 */
	public String getDescription(Locale locale) {
		// TODO Auto-generated method stub
		String desc = getDescription();
		return desc;
	}


	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return this.className;
	}


	
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}


	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}


	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	
	/**
	 * @return the extensions
	 */
	public Map getExtensions() {
		return this.extensions;
	}


	
	/**
	 * @param extensions the extensions to set
	 */
	public void setExtensions(Map extensions) {
		this.extensions = extensions;
	}


	
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return this.icon;
	}


	
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}


	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}


	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	
	/**
	 * @return the suggestedValue
	 */
	public String getSuggestedValue() {
		return this.suggestedValue;
	}


	
	/**
	 * @param suggestedValue the suggestedValue to set
	 */
	public void setSuggestedValue(String suggestedValue) {
		this.suggestedValue = suggestedValue;
	}


	
	/**
	 * @return the componentInfo
	 */
	public ComponentInfo getComponentInfo() {
		return this.componentInfo;
	}


	
	/**
	 * @param componentInfo the componentInfo to set
	 */
	public void setComponentInfo(ComponentInfo componentInfo) {
		this.componentInfo = componentInfo;
	}


	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}


	
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	/* (non-Javadoc)
	 * @see com.idega.core.component.business.ComponentProperty#getDisplayName(java.util.Locale)
	 */
	public String getDisplayName(Locale locale) {
		String display = getDisplayName();
		if(display==null){
			return getName();
		}
		return display;
	}
}
