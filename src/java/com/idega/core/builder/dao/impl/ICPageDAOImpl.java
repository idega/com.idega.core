/*
 * $Id: ICPageDAOImpl.java 1.1 Sep 22, 2009 laddi Exp $
 * Created on Sep 22, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.builder.dao.ICPageDAO;
import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.persistence.impl.GenericDaoImpl;

@Scope("singleton")
@Repository("icPageDAO")
@Transactional(readOnly = true)
public class ICPageDAOImpl extends GenericDaoImpl implements ICPageDAO {
	
	public ICPage findPage(Integer pageID) {
		return find(ICPage.class, pageID);
	}
	
}