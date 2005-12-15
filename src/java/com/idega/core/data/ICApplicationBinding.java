/*
 * $Id: ICApplicationBinding.java,v 1.2 2005/12/15 16:57:12 thomas Exp $
 * Created on Dec 14, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.data;

import com.idega.data.IDOEntity;


/**
 * 
 *  Last modified: $Date: 2005/12/15 16:57:12 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public interface ICApplicationBinding extends IDOEntity {

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#getPrimaryKeyClass
	 */
	public Class getPrimaryKeyClass();

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#getKey
	 */
	public String getKey();

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#getValue
	 */
	public String getValue();

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#getBindingType
	 */
	public String getBindingType();

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#setKey
	 */
	public void setKey(String key);

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#setValue
	 */
	public void setValue(String value);

	/**
	 * @see com.idega.core.data.ICApplicationBindingBMPBean#setBindingType
	 */
	public void setBindingType(String type);
}
