/*
 * $Id: Gender.java,v 1.5 2005/01/23 14:52:26 laddi Exp $
 * Created on 16.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;



import com.idega.data.*;


/**
 * Last modified: $Date: 2005/01/23 14:52:26 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public interface Gender extends IDOEntity {

	/**
	 * @see com.idega.user.data.GenderBMPBean#isFemaleGender
	 */
	public boolean isFemaleGender();

	/**
	 * @see com.idega.user.data.GenderBMPBean#isMaleGender
	 */
	public boolean isMaleGender();

	/**
	 * @see com.idega.user.data.GenderBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.user.data.GenderBMPBean#setDescription
	 */
	public void setDescription(String description);

	/**
	 * @see com.idega.user.data.GenderBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.user.data.GenderBMPBean#getDescription
	 */
	public String getDescription();

}
