/*
 * $Id: ICPageDAO.java 1.1 Sep 22, 2009 laddi Exp $
 * Created on Sep 22, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.dao;

import com.idega.business.SpringBeanName;
import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.persistence.GenericDao;

@SpringBeanName("icPageDAO")
public interface ICPageDAO extends GenericDao {
	
	public ICPage findPage(Integer pageID);
	
}