/*
 * $Id: GenderHome.java,v 1.4 2005/01/23 14:52:26 laddi Exp $
 * Created on 16.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.*;


/**
 * Last modified: $Date: 2005/01/23 14:52:26 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
 */
public interface GenderHome extends IDOHome {

	public Gender create() throws javax.ejb.CreateException;

	public Gender findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.user.data.GenderBMPBean#ejbHomeGetMaleGender
	 */
	public Gender getMaleGender() throws FinderException;

	/**
	 * @see com.idega.user.data.GenderBMPBean#ejbHomeGetFemaleGender
	 */
	public Gender getFemaleGender() throws FinderException;

	/**
	 * @see com.idega.user.data.GenderBMPBean#ejbFindByGenderName
	 */
	public Gender findByGenderName(String name) throws FinderException;

	/**
	 * @see com.idega.user.data.GenderBMPBean#ejbFindAllGenders
	 */
	public Collection findAllGenders() throws FinderException;

}
