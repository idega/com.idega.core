/*
 * $Id: ICObjectComponentInfo.java,v 1.3 2006/05/09 14:47:18 tryggvil Exp $
 * Created on 8.9.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.business;

import com.idega.core.component.data.ICObject;


/**
 * <p>
 * Implementation of ComponentInfo for ICObject registered objects
 * </p>
 *  Last modified: $Date: 2006/05/09 14:47:18 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class ICObjectComponentInfo extends DefaultComponentInfo {

	ICObject icObject;

	/**
	 * @param componentClass
	 * @param componentName
	 * @param componentType
	 * @throws ClassNotFoundException 
	 */
	public ICObjectComponentInfo(ICObject icObject) throws ClassNotFoundException {
		super(icObject.getObjectClass(), icObject.getName(), icObject.getObjectType());
		setIcObject(icObject);
	}

	
	/**
	 * @return the icObject
	 */
	ICObject getIcObject() {
		return icObject;
	}

	
	/**
	 * @param icObject the icObject to set
	 */
	void setIcObject(ICObject icObject) {
		this.icObject = icObject;
	}
	
	
}
