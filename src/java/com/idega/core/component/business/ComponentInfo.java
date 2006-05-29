/*
 * $Id: ComponentInfo.java,v 1.4 2006/05/29 18:15:50 tryggvil Exp $
 * Created on 8.9.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.business;

import java.util.List;
import java.util.Locale;


/**
 * <p>
 * This class describes a component (JSF UIComponent, IW Element, IW Block etc. ) and holds information about it. 
 * </p>
 *  Last modified: $Date: 2006/05/29 18:15:50 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public interface ComponentInfo {
	
	public Class getComponentClass();
	
	public String getComponentName();
	
	public String getComponentName(Locale locale);
	
	public String getObjectType();
	
	public String getModuleIdentifier();
	
	/**
	 * <p>
	 * Returns List of ComponentProperty instances
	 * </p>
	 * @return
	 */
	public List getProperties();
	
	/**
	 * <p>
	 * Get list of component permissions. (that describe who has access to the component)
	 * </p>
	 * @return
	 */
	public List getComponentPermissions();

	/**
	 * @return the deprecated
	 */
	public boolean isDeprecated();

	/**
	 * @return the expert
	 */
	public boolean isExpert();

	/**
	 * @return the group
	 */
	public String getGroup();
	
}
