/*
 * $Id: ICObjectComponentInfo.java,v 1.1.2.1 2007/01/12 19:32:50 idegaweb Exp $
 * Created on 8.9.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.business;


/**
 * <p>
 * Implementation of ComponentInfo for ICObject registered objects
 * </p>
 *  Last modified: $Date: 2007/01/12 19:32:50 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class ICObjectComponentInfo extends DefaultComponentInfo {

	int icObjectId;

	/**
	 * @param componentClass
	 * @param componentName
	 * @param componentType
	 */
	public ICObjectComponentInfo(Class componentClass, String componentName, String componentType,int icObjectId) {
		super(componentClass, componentName, componentType);
		// TODO Auto-generated constructor stub
		setIcObjectId(icObjectId);
	}
	
	/**
	 * @return Returns the icObjectId.
	 */
	public int getIcObjectId() {
		return this.icObjectId;
	}
	
	/**
	 * @param icObjectId The icObjectId to set.
	 */
	public void setIcObjectId(int icObjectId) {
		this.icObjectId = icObjectId;
	}
	
}
