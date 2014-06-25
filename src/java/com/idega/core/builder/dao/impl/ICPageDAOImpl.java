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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.builder.dao.ICPageDAO;
import com.idega.core.builder.data.bean.ICPage;
import com.idega.core.persistence.impl.GenericDaoImpl;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("icPageDAO")
@Transactional(readOnly = true)
public class ICPageDAOImpl extends GenericDaoImpl implements ICPageDAO {

	private Map<Integer, Boolean> cache = new ConcurrentHashMap<Integer, Boolean>();

	@Override
	public ICPage findPage(Integer pageID) {
		return find(ICPage.class, pageID);
	}

	@Override
	public boolean isPagePublished(Integer pageId) {
		if (pageId == null) {
			return false;
		}

		if (cache.containsKey(pageId)) {
			return cache.get(pageId);
		}

		ICPage page = findPage(pageId);
		Boolean published = page != null && page.isPublished() ? Boolean.TRUE : Boolean.FALSE;
		cache.put(pageId, published);
		return published;
	}

}