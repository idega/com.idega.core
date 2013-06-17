/*
 * $Id: ICObjectDAO.java 1.1 Sep 21, 2009 laddi Exp $
 * Created on Sep 21, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.dao;

import com.idega.business.SpringBeanName;
import com.idega.core.component.data.bean.ICObject;
import com.idega.core.persistence.GenericDao;

@SpringBeanName("icObjectDAO")
public interface ICObjectDAO extends GenericDao {

	public ICObject findObject(Integer objectID);

	public ICObject findByClass(Class<?> objectClass);

}