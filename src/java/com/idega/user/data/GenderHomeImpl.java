/*
 * $Id: GenderHomeImpl.java,v 1.3 2005/01/13 14:46:37 laddi Exp $
 * Created on 10.1.2005
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
 * Last modified: $Date: 2005/01/13 14:46:37 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class GenderHomeImpl extends IDOFactory implements GenderHome {

	protected Class getEntityInterfaceClass() {
		return Gender.class;
	}

	public Gender create() throws javax.ejb.CreateException {
		return (Gender) super.createIDO();
	}

	public Gender findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Gender) super.findByPrimaryKeyIDO(pk);
	}

	public Gender getMaleGender() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Gender theReturn = ((GenderBMPBean) entity).ejbHomeGetMaleGender();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Gender getFemaleGender() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Gender theReturn = ((GenderBMPBean) entity).ejbHomeGetFemaleGender();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Gender findByGenderName(String name) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GenderBMPBean) entity).ejbFindByGenderName(name);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllGenders() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GenderBMPBean) entity).ejbFindAllGenders();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

}
